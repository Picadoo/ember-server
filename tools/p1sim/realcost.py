"""Real-cost growth model (review B03, 2026-10-04) — players EARN and PAY for D141–D143 growth instead of having the
finished build from day 1 (that is growthrun.py's "upper-bound effect check").

What a player gets, and when (rules from the canonical snapshot, rules.growth()):
  * talent points: first clears q03 / q05 / q07 (p.cleared), first challenge clear, first raid clear, abyss floor 5
    (growth talents.points); rows bought in order with coins (rows.coin 800 / 2000 / 4000), row 3 = the target family's
    specialist; no respec.
  * honors (free, by condition): h_abyss5 / h_abyss10 (highest abyss tier settled), h_chal3 / h_chal7 (distinct maps cleared
    on challenge; abyss segments do not count), h_raid1 / h_raid3 (raid clears; p2econ does not
    say WHICH raid → 3 clears stand in for "all three"), h_codex not modelled (its reroll discount is ignored).
  * affixes: only on the worn T3 piece of the target family (a player does not reroll a piece it will replace); a try
    costs reroll.coin[3] × honor reroll_coin + reroll.shard[3] shards (a duplicate piece instead of shards is not
    modelled: p2econ keeps no spare drops); the affix is uniform over the slot pool, the tier follows tier_weights
    truncated at tier_cap[quality], pity → cap tier (EmberAffix.roll). Once the wanted affix is on the piece the next
    tries are LOCKED (+ lock_shard[3]) and only roll the tier. The player keeps a candidate only if it is better
    (wanted affix, higher tier). A new piece arrives empty (the affix stays on the replaced item).
  * policies: 'gear' = growth only from coins above the knob reserve after the normal gear investment and never while
    saving for a tier upgrade; 'growth' = growth purchases before the gear investment (same reserve).
Each player has its own growth RNG stream (seeded by creation order), so the drop / combat streams stay paired with
the off and upper-bound runs.
"""
import random, statistics
import p1sim, growth, rules

CFG = None
G = None
REG = {}       # id(player.cleared) -> (cleared set, state)   (the set is kept alive, so ids are never reused)
STATES = []    # creation order
_N = [0]


def _state():
    _N[0] += 1
    return {'nodes': [], 'chal': set(), 'raids': 0, 'abyss': 0, 'tal_coin': 0, 'aff_coin': 0, 'aff_shard': 0, 'tries': 0,
            'rng': random.Random('realcost-%d' % _N[0]), 'mode': None, 'weekly': []}


def points(p, s):
    pts = sum(1 for k in ('q03', 'q05', 'q07') if k in p.cleared)
    return pts + (1 if s['chal'] else 0) + (1 if s['raids'] else 0) + (1 if _abyss(p, s) >= 5 else 0)


def _abyss(p, s):
    return max(getattr(p, 'abyss_best', 0), s['abyss'])


def honors(p, s):
    out = []
    best = _abyss(p, s)
    for h in (G.get('honors') or {}).get('list', []):
        k, a = h['kind'], h.get('arg')
        ok = (k == 'abyss_floor' and best >= int(a)) or (k == 'challenge_count' and len(s['chal']) >= int(a)) or \
             (k == 'raid_count' and s['raids'] >= int(a))
        if ok:
            out.append(h)
    return out


def growth_fn(info, ctx):
    e = REG.get(id(ctx))
    if e is None:
        return None
    s = e[1]
    parts = [n['mods'] for n in s['nodes'] if not n.get('set') or n['set'] == info.get('set')]
    hs = s.get('_honors') or []
    if hs:
        parts.append(growth.cap_honors(growth.combine([h['mods'] for h in hs]), (G.get('honors') or {}).get('caps') or {}))
    rr = G['reroll']
    for slot in ('blade', 'charm'):
        it = info.get(slot) or {}
        a = it.get('affix')
        if a and it.get('tier', 0) >= 1:
            d = next(x for x in rr['affixes'][slot] if x['id'] == a[0])
            parts.append({d['key']: d['values'][min(a[1], rr['tier_cap'][it.get('q', 0)]) - 1]})
    return growth.combine(parts) if parts else None


def _reroll_mult(s):
    m = 1.0
    for h in s.get('_honors') or []:
        m *= float(h['mods'].get('reroll_coin', 1.0))
    return max(float(((G.get('honors') or {}).get('caps') or {}).get('reroll_coin', 0.0)), m)


def spend(p, want):
    s = REG[id(p.cleared)][1]
    s['_honors'] = honors(p, s)
    if getattr(p, 'save_for_upgrade', False):
        return
    reserve = p.kn.coin_reserve
    rows = {r['row']: r for r in G['talents']['rows']}
    nodes = {n['id']: n for n in G['talents']['nodes']}
    row3 = {'scorch': 't3a', 'burst': 't3b', 'sustain': 't3c'}.get(p.kn.target)
    wanted = list(want.get('talents', [])) + ([row3] if row3 and len(want.get('talents', [])) >= 2 else [])
    have = {n['id'] for n in s['nodes']}
    used = sum(int(rows[n['row']]['points']) for n in s['nodes'])
    pts = points(p, s)
    for nid in wanted:
        if nid in have:
            continue
        n = nodes[nid]
        r = rows[n['row']]
        if n['row'] != len(s['nodes']) + 1 or used + int(r['points']) > pts or p.coin - int(r['coin']) < reserve:
            break
        p.coin -= int(r['coin']); s['tal_coin'] += int(r['coin'])
        s['nodes'].append(n); used += int(r['points'])
    rr = G['reroll']
    for slot, aid in (want.get('affixes') or {}).items():
        it = getattr(p, slot)
        if it.get('tier') != 3 or it.get('fam') != p.kn.target:
            continue
        cap = int(rr['tier_cap'][it.get('q', 0)])
        for _ in range(3):  # at most 3 tries per settlement
            cur = it.get('affix')
            if cur and cur[0] == aid and cur[1] >= cap:
                break
            locked = bool(cur and cur[0] == aid)
            cc = int(round(int(rr['coin'][3]) * _reroll_mult(s)))
            sh = int(rr['shard'][3]) + (int(rr['lock_shard'][3]) if locked else 0)
            if p.coin - cc < reserve or p.shard < sh:
                break
            p.coin -= cc; p.shard -= sh
            s['aff_coin'] += cc; s['aff_shard'] += sh; s['tries'] += 1
            rng = s['rng']
            pool = rr['affixes'][slot]
            d = next(x for x in pool if x['id'] == aid) if locked else pool[rng.randrange(len(pool))]
            pity = it.get('apity', 0)
            if pity >= int(rr['pity']):
                tier = cap
            else:
                wts = [float(x) for x in rr['tier_weights'][:cap]]
                u, acc, tier = rng.random() * sum(wts), 0.0, cap
                for i, x in enumerate(wts):
                    acc += x
                    if u < acc:
                        tier = i + 1
                        break
            it['apity'] = 0 if tier >= cap else pity + 1
            if d['id'] == aid and (not cur or cur[0] != aid or tier > cur[1]):
                it['affix'] = (aid, tier)


def install(want):
    """want: {"talents": [...rows 1-2...], "affixes": {"blade": id, "charm": id}, "policy": "gear" | "growth"}"""
    global CFG, G
    G = rules.growth()
    policy = want.get('policy', 'gear')
    init0, inv0 = p1sim.Player.__init__, p1sim.Player.invest

    def init(self, *a, **k):
        init0(self, *a, **k)
        st = _state()
        REG[id(self.cleared)] = (self.cleared, st)
        STATES.append(st)

    def invest(self):
        if policy == 'growth':
            spend(self, want)
            inv0(self)
        else:
            inv0(self)
            spend(self, want)
    p1sim.Player.__init__, p1sim.Player.invest = init, invest
    p1sim.GROWTH = growth_fn
    try:
        import p2econ
    except ImportError:
        return
    sw0, fs0, ph0, pa0 = p2econ.settle_with, p2econ.forge_sink, p2econ.phase2, p2econ.phase2_abyss

    def settle_with(p, ccfg, key, extra):
        e = REG.get(id(p.cleared))
        if e:
            if getattr(p, 'bounty_weight', 1) == 2:
                e[1]['raids'] += 1
            elif ccfg.get('abyss'):  # abyss segment (abyss_cfg marks the tier); not a challenge clear
                e[1]['abyss'] = max(e[1]['abyss'], int(ccfg['abyss']))
            else:
                e[1]['chal'].add(key)
        return sw0(p, ccfg, key, extra)

    def forge_sink(p, reserve=None):
        r = fs0(p, reserve)
        e = REG.get(id(p.cleared))
        if e:  # end of a week (both phase loops call it once a week)
            s = e[1]
            bl, ch = p.blade.get('affix'), p.charm.get('affix')
            s['weekly'].append({'rows': len(s['nodes']), 'tal': s['tal_coin'], 'aff': s['aff_coin'], 'sh': s['aff_shard'],
                                'tries': s['tries'], 'bl': bl, 'ch': ch, 'hon': len(s.get('_honors') or []),
                                'pts': points(p, s)})
        return r

    def mode_of(kw, abyss):
        if abyss:
            return 'abyssg' if kw.get('goals') else 'abyss'
        if kw.get('goals'):
            return 'goals'
        for k in ('raid', 'trade', 'mods'):
            if kw.get(k):
                return k
        return None

    def phase2(cfg, ccfg, kn, p, rng, weeks, rotation, *a, **kw):
        e = REG.get(id(p.cleared))
        if e:
            e[1]['mode'] = mode_of(kw, False) or ('rot' if rotation else 'base')
        return ph0(cfg, ccfg, kn, p, rng, weeks, rotation, *a, **kw)

    def phase2_abyss(cfg, ccfg, kn, p, rng, weeks, per_day, *a, **kw):
        e = REG.get(id(p.cleared))
        if e:
            e[1]['mode'] = mode_of(kw, True)
        return pa0(cfg, ccfg, kn, p, rng, weeks, per_day, *a, **kw)
    p2econ.settle_with, p2econ.forge_sink, p2econ.phase2, p2econ.phase2_abyss = settle_with, forge_sink, phase2, phase2_abyss


ZH = {'base': '无轮换', 'rot': 'P2-1 轮换', 'abyss': 'P2-2 深渊', 'trade': 'P2-3 交易', 'raid': '轮换 + 团本', 'mods': '轮换 + 周规则',
      'goals': '团本 + 周目标', 'abyssg': '深渊 + 周目标'}


def report(weeks_show=(1, 2, 4, 8, 12)):
    by = {}
    for s in STATES:
        if s['mode'] and s['weekly']:
            by.setdefault(s['mode'], []).append(s)
    print()
    print('真实成本成长（realcost.py）：每周末状态（中位 / 比例）')
    print('| 周 | 方案 | 天赋点（中位） | 买到第 1 / 2 / 3 排 | 天赋花费币（中位） | 洗练次数（中位） | 洗练花费币 / 碎片（中位） | 刃有目标词条 / 封顶档 | 护符有目标词条 / 封顶档 | 勋记数（中位） |')
    print('|---|---|---:|---|---:|---:|---|---|---|---:|')
    for w in weeks_show:
        for mode, l in by.items():
            rows = [s['weekly'][w - 1] for s in l if len(s['weekly']) >= w]
            if not rows:
                continue
            n = len(rows)
            pc = lambda f: round(100.0 * sum(1 for r in rows if f(r)) / n)
            print('| %d | %s | %d | %d%% / %d%% / %d%% | %d | %d | %d / %d | %d%% / %d%% | %d%% / %d%% | %d |' % (
                w, ZH.get(mode, mode), statistics.median(r['pts'] for r in rows), pc(lambda r: r['rows'] >= 1), pc(lambda r: r['rows'] >= 2),
                pc(lambda r: r['rows'] >= 3), statistics.median(r['tal'] for r in rows), statistics.median(r['tries'] for r in rows),
                statistics.median(r['aff'] for r in rows), statistics.median(r['sh'] for r in rows),
                pc(lambda r: r['bl'] is not None), pc(lambda r: r['bl'] is not None and r['bl'][1] >= 3),
                pc(lambda r: r['ch'] is not None), pc(lambda r: r['ch'] is not None and r['ch'][1] >= 3),
                statistics.median(r['hon'] for r in rows)))
