"""D222 首领徽记 (boss insignia, counter p1_sigmark_<map>) account — REG §5 gaps 1–3 (model coverage only, no numbers changed).

OPT-IN: nothing here runs unless install() is called (p2econ --insignia, or `python3 insignia.py report`). Without it every
p1sim / p2econ / signin output stays bit-identical. The account never touches the run / drop rng streams.

Sources (CoreRpg EmberRunService / EmberSignature / EmberSignService / ember-v1-runs.yml, read 2026-10-06 at 1.65.49 / bv57):
  S07 first clear of a signature map (all Q01–Q07)       : FC_MARKS 3 of that map, once per map (p1_sigfc_)
  S08 repeat NORMAL clear of a first-cleared sig map      : CLEAR_MARKS 1 of that map per run (challenge / abyss / raid pay 0)
  S09 自选誓约 (leader's own Q06 first clear; repeat normal) : +1 per pledged pool rule (pool = normal: true → lean, reverse
                                                            → ≤ 2) — opt-in --ins-pledge N (default 0); clear rate NOT
                                                            penalised (upper bound, like p1sim P1_PLEDGE)
  S17 连战·前哨 outpost (needs Q05 first clear)            : 2 × each chain map (q01, q02, q03), first settled clear of the
                                                            week only — assumed cleared every week (upper bound)
  S18 首领残响 echo_q01..q04 (needs Q04 first clear)        : 2 of that boss's map per claim, 3 claims / week shared
                                                            (p4_echo_claim) — assumed all 3 claimed (upper bound)
  S23 sign-in 7th / 21st sign                              : 1 of the HIGHEST first-cleared signature map
                                                            (EmberSignService.sigMap); via signin.py hook (--ins-signin)
Sinks:
  C12 烬炉烙印  5 insignia of the signature's map + 300 × item tier coins; needs own Q02 + that map's first clear;
                overwrites; the signature is lost with the piece (no refund)
  C13 签名调律  10 insignia of the signature's map, once per shipped alt (L01b L02b L06b L08b L10b L12b — L11b held,
                mainline.py ALTS); needs own Q07 first clear

SPEND POLICY (assumption, not a server rule):
  * brand: each worn slot (blade, charm) wants the fitting signature (family 'any' or the player's target family) of the
    HIGHEST first-cleared map that has one for that slot. When the slot is unbranded or the worn piece was replaced
    (signature lost) it brands the best signature it can afford now (a lower map if the top map is short of insignia) and
    re-brands upward later when a higher map's 5 insignia are there; coin >= cost + BRAND_RESERVE (1000, the p2econ forge
    reserve). Checked after every grant (greedy).
  * tune: after the own Q07 first clear, unlock every shipped alt whose map balance stays >= the brand reserve of that map
    (5 if a worn slot currently wants that map, else 0) after paying 10 — i.e. surplus insignia are turned into permanent
    alts (collection), brands never starve for a tune.
  * echo: each claim goes to the q01–q04 map with the lowest balance (ties → lower map).
  * signature stamps on drops (S08 12 %) are NOT modelled → brand counts are an upper bound of brand demand.
"""
import argparse, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

MAPS = ('q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07')      # EmberSignature.maps(): every main map has signatures
FC_MARKS, CLEAR_MARKS, IMPRINT_MARKS, IMPRINT_COIN_PER_TIER = 3, 1, 5, 300   # EmberSignature
IMPRINT_UNLOCK, ALT_UNLOCK, ALT_MARKS = 'q02', 'q07', 10
PLEDGE_UNLOCK = 'q06'                                            # EmberRunService.PLEDGE_UNLOCK
ECHO = {'maps': ('q01', 'q02', 'q03', 'q04'), 'requires': 'q04', 'claims': 3, 'per': 2}   # ember-v1-runs.yml echo_q0*
OUTPOST = {'chain': ('q01', 'q02', 'q03'), 'requires': 'q05', 'per': 2}                     # ember-v1-runs.yml outpost
BRAND_RESERVE = 1000
# (map, slot, family) — EmberSignature.DEFS / mainline.py SIGS
DEFS = {'L01': ('q01', 'blade', 'scorch'), 'L02': ('q01', 'charm', 'any'), 'L03': ('q02', 'charm', 'burst'),
        'L04': ('q02', 'blade', 'burst'), 'L05': ('q03', 'blade', 'sustain'), 'L06': ('q03', 'charm', 'any'),
        'L07': ('q04', 'blade', 'any'), 'L08': ('q04', 'charm', 'scorch'), 'L09': ('q05', 'blade', 'any'),
        'L10': ('q05', 'charm', 'burst'), 'L11': ('q06', 'blade', 'any'), 'L12': ('q06', 'charm', 'sustain'),
        'L13': ('q07', 'blade', 'any'), 'L14': ('q07', 'charm', 'any'), 'L15': ('q07', 'charm', 'scorch')}
ALTS = ('L01', 'L02', 'L06', 'L08', 'L10', 'L12')               # shipped 调律 versions (mainline.py ALTS minus L11b)

CONF = {'pledge': 0, 'pledge_q07': True, 'echo': True, 'outpost': True}
TOT = {k: 0 for k in ('fc', 'rerun', 'pledge', 'outpost', 'echo', 'signin', 'brand', 'tune', 'brand_coin')}


class Account:
    def __init__(self):
        self.bal = {k: 0 for k in MAPS}
        self.src = {k: 0 for k in ('fc', 'rerun', 'pledge', 'outpost', 'echo', 'signin')}
        self.fc = set()
        self.brands = self.tunes = self.brand_coin = 0
        self.spent = {k: 0 for k in MAPS}
        self.alts = set()
        self.worn = {'blade': None, 'charm': None}   # slot -> (item dict ref, def id)
        self.week = None
        self.p2base = None
        self.last_w1 = None
        self.hist = {}

    def snap(self):
        return {'bal': dict(self.bal), 'tot': sum(self.bal.values()), 'brands': self.brands, 'tunes': self.tunes,
                'src': dict(self.src), 'spent': dict(self.spent), 'coin': self.brand_coin,
                'branded2': all(v is not None for v in self.worn.values()),
                'top2': all(v is not None and DEFS[v[1]][0] == 'q07' for v in self.worn.values())}


def acc(p):
    a = p.__dict__.get('_ins')
    if a is None:
        a = p._ins = Account()
    return a


def grant(p, mp, n, kind):
    if n <= 0 or mp not in MAPS:
        return
    a = acc(p)
    a.bal[mp] += n
    a.src[kind] += n
    TOT[kind] += n


def wants(p, slot):
    """fitting signatures (family 'any' or the player's target family) of first-cleared maps, best first:
    higher map first, then the family-bound one over 'any'"""
    l = [sid for sid, (mp, sl, fam) in DEFS.items() if sl == slot and mp in p.cleared and fam in ('any', p.kn.target)]
    return sorted(l, key=lambda sid: (-MAPS.index(DEFS[sid][0]), DEFS[sid][2] == 'any', sid))


def want(p, slot):
    """the signature a worn slot wants in the end: highest first-cleared map with a fitting def"""
    l = wants(p, slot)
    return l[0] if l else None


def spend(p):
    a = acc(p)
    if IMPRINT_UNLOCK not in p.cleared:
        return
    for slot in ('blade', 'charm'):
        it = p.blade if slot == 'blade' else p.charm
        if it['tier'] < 1:
            continue
        cur = a.worn[slot]
        if cur is not None and cur[0] is not it:
            a.worn[slot] = cur = None            # piece replaced → signature lost with it
        cost = IMPRINT_COIN_PER_TIER * it['tier']
        if p.coin < cost + BRAND_RESERVE:
            continue
        for sid in wants(p, slot):   # best affordable; a lower map now, re-brand upward later
            mp = DEFS[sid][0]
            if cur is not None and MAPS.index(mp) <= MAPS.index(DEFS[cur[1]][0]):
                break
            if a.bal[mp] >= IMPRINT_MARKS:
                a.bal[mp] -= IMPRINT_MARKS; a.spent[mp] += IMPRINT_MARKS
                p.coin -= cost; a.brand_coin += cost; TOT['brand_coin'] += cost
                a.brands += 1; TOT['brand'] += 1
                a.worn[slot] = (it, sid)
                break
    if ALT_UNLOCK in p.cleared:
        need = {}
        for slot in ('blade', 'charm'):
            sid = want(p, slot)
            if sid:
                need[DEFS[sid][0]] = IMPRINT_MARKS
        for sid in ALTS:
            mp = DEFS[sid][0]
            if sid in a.alts or mp not in p.cleared:
                continue
            if a.bal[mp] - ALT_MARKS >= need.get(mp, 0):
                a.bal[mp] -= ALT_MARKS; a.spent[mp] += ALT_MARKS
                a.alts.add(sid); a.tunes += 1; TOT['tune'] += 1


def week_of(p, a):
    """calendar week of p.day: phase 1 days 1.. → (day-1)//7; p2econ phase 2 days 1000 + 7w + d continue from the week of
    the Q07 first clear (p2econ D108: phase-2 week 0 IS the Q07 week, the shared caps carry)."""
    d = p.day
    if d is None:
        return None
    if d < 1000:
        a.last_w1 = (d - 1) // 7
        return a.last_w1
    if a.p2base is None:
        a.p2base = a.last_w1 if a.last_w1 is not None else 0
    return a.p2base + (d - 1000) // 7


def weekly(p):
    a = acc(p)
    w = week_of(p, a)
    if w is None or w == a.week:
        return
    a.week = w
    if CONF['outpost'] and OUTPOST['requires'] in p.cleared:
        for mp in OUTPOST['chain']:
            grant(p, mp, OUTPOST['per'], 'outpost')
    if CONF['echo'] and ECHO['requires'] in p.cleared:
        for _ in range(ECHO['claims']):
            mp = min(ECHO['maps'], key=lambda k: (a.bal[k], MAPS.index(k)))
            grant(p, mp, ECHO['per'], 'echo')


def signin_hook(p, n):
    """S23: EmberSignService.sigMap = the highest first-cleared signature map"""
    best = None
    for k in MAPS:
        if k in p.cleared:
            best = k
    if best is not None:
        grant(p, best, n, 'signin')
        spend(p)


def _p2mods():
    """the p2econ module(s) to patch: `p2econ.py` run as a script lives in __main__"""
    m = sys.modules.get('__main__')
    if m is not None and hasattr(m, 'phase2_abyss') and hasattr(m, 'to_q07'):
        return [m] + ([sys.modules['p2econ']] if 'p2econ' in sys.modules else [])
    import p2econ
    return [p2econ]


def install(pledge=0, signin=False, echo=True, outpost=True, pledge_q07=True):
    import p1sim
    CONF.update(pledge=pledge, echo=echo, outpost=outpost, pledge_q07=pledge_q07)
    if signin:
        import signin  # noqa: F401
    for name in ('signin', '__main__'):   # signin.py may be the running script (`signin.py p2econ -- --insignia`)
        sg = sys.modules.get(name)
        if sg is not None and hasattr(sg, 'INS_HOOK') and hasattr(sg, 'pay_day'):
            sg.INS_HOOK = signin_hook
    if getattr(p1sim.Player, '_ins_installed', False):
        return
    oinit, osettle = p1sim.Player.__init__, p1sim.Player.settle

    def __init__(self, cfg, kn, rng):
        oinit(self, cfg, kn, rng)
        self._ins_cfg0 = cfg            # the NORMAL config; p2econ.settle_with swaps in challenge / abyss copies

    def settle(self, key, extra, var=None):
        normal = self.cfg is self.__dict__.get('_ins_cfg0')
        fc = key not in self.cleared
        osettle(self, key, extra, var)
        a = acc(self)
        weekly(self)
        if normal and key in MAPS and not self.cfg['maps'][key].get('raid'):
            if fc and key not in a.fc:
                a.fc.add(key)
                grant(self, key, FC_MARKS, 'fc')
            elif not fc:
                grant(self, key, CLEAR_MARKS, 'rerun')
                if CONF['pledge'] and PLEDGE_UNLOCK in self.cleared and (CONF['pledge_q07'] or key != 'q07'):
                    grant(self, key, CONF['pledge'], 'pledge')
        spend(self)
    p1sim.Player.__init__, p1sim.Player.settle = __init__, settle

    for mod in _p2mods():
        _patch_p2(mod)
    p1sim.Player._ins_installed = True


def _patch_p2(p2econ):
    if getattr(p2econ, '_ins_patched', False):
        return
    p2econ._ins_patched = True
    ofs, oph2, oab = p2econ.forge_sink, p2econ.phase2, p2econ.phase2_abyss

    def forge_sink(p, reserve=None):
        r = ofs(p, reserve)
        if p.day is not None and p.day >= 1000:
            acc(p).hist[(p.day - 1000) // 7] = acc(p).snap()   # last call of the week = end-of-week state
        return r

    def attach(p, rows):
        a = acc(p)
        last = a.snap()
        for i in range(len(rows) - 1, -1, -1):
            if i in a.hist:
                last = a.hist[i]
            rows[i]['ins'] = a.hist.get(i, last)
        return rows

    def phase2(cfg, ccfg, kn, p, *args, **kw):
        acc(p).q07 = acc(p).snap()
        return attach(p, oph2(cfg, ccfg, kn, p, *args, **kw))

    def phase2_abyss(cfg, ccfg, kn, p, *args, **kw):
        acc(p).q07 = acc(p).snap()
        return attach(p, oab(cfg, ccfg, kn, p, *args, **kw))
    p2econ.forge_sink, p2econ.phase2, p2econ.phase2_abyss = forge_sink, phase2, phase2_abyss


def table(res, modes, weeks, label, every=False):
    """markdown rows from p2econ result lists (rows carry 'ins')"""
    out = ['| 周 | 方案 | 徽记结余（中位 合计） | 徽记结余 P10 / P90 | Q01–Q04 结余（中位） | Q05–Q06 结余（中位） | Q07 结余（中位） | 烙印次数（中位 / 均值） | 调律次数（中位 / 均值） | 两槽都烙上 | 两槽都是 Q07 签名 | 累计烙印币（中位） |',
           '|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|']
    for w in (range(1, weeks + 1) if every else sorted({1, 4, 8, 12, 20, weeks})):
        if w > weeks:
            continue
        for m in modes:
            rows = [r[w - 1]['ins'] for r in res[m] if len(r) >= w]
            if not rows:
                continue
            tot = sorted(x['tot'] for x in rows)
            q = lambda f: statistics.median(f(x) for x in rows)
            pct = lambda s, f: s[min(len(s) - 1, int(f * len(s)))]
            out.append('| %d | %s | %d | %d / %d | %d | %d | %d | %s / %.1f | %s / %.1f | %d%% | %d%% | %d |' % (
                w, label.get(m, m), statistics.median(tot), pct(tot, 0.1), pct(tot, 0.9),
                q(lambda x: sum(x['bal'][k] for k in MAPS[:4])), q(lambda x: x['bal']['q05'] + x['bal']['q06']),
                q(lambda x: x['bal']['q07']), ('%g' % q(lambda x: x['brands'])), statistics.mean(x['brands'] for x in rows),
                ('%g' % q(lambda x: x['tunes'])), statistics.mean(x['tunes'] for x in rows),
                round(100 * sum(x['branded2'] for x in rows) / len(rows)), round(100 * sum(x['top2'] for x in rows) / len(rows)),
                q(lambda x: x['coin'])))
    return out


def src_line(res, modes, weeks):
    out = []
    for m in modes:
        rows = [r[weeks - 1]['ins'] for r in res[m] if len(r) >= weeks]
        if not rows:
            continue
        n = len(rows)
        s = {k: sum(x['src'][k] for x in rows) / n for k in rows[0]['src']}
        sp = {k: sum(x['spent'][k] for x in rows) / n for k in MAPS}
        bal = {k: sum(x['bal'][k] for x in rows) / n for k in MAPS}
        out.append('- %s · W%d 人均来源：%s；人均花掉（按图）：%s；人均结余（按图）：%s' % (
            m, weeks, ' / '.join('%s %.1f' % (k, v) for k, v in s.items()),
            ' '.join('%s %.0f' % (k.upper(), v) for k, v in sp.items()),
            ' '.join('%s %.0f' % (k.upper(), v) for k, v in bal.items())))
    return out


def _job(args):
    dodge, players, weeks, modes, pledge, signin, seed0 = args
    import p1sim, p1config, p2econ
    if signin:
        import signin as sg
        sg.install(120, 1.0)
    install(pledge=pledge, signin=signin)
    p2econ.RAID_FLOOR = int((p2econ.RUNS.get('raid_item') or {}).get('quality_floor', 1))
    cfg = p1config.load()
    ccfg = p2econ.challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    res = {m: [] for m in modes}
    q07 = {m: [] for m in modes}
    for i in range(players):
        for mode in modes:
            kn = p1sim.Knobs(dodge)
            p, runs, rng = p2econ.to_q07(cfg, kn, seed0 + i)
            if p is None:
                continue
            kn.swap = True
            kn.quality_chase = p2econ.FORGE_SINK
            if mode in ('abyss', 'abyssg'):
                rows = p2econ.phase2_abyss(cfg, ccfg, kn, p, random.Random(9000 + i), weeks, per_day, goals=mode == 'abyssg')
            else:
                rows = p2econ.phase2(cfg, ccfg, kn, p, random.Random(9000 + i), weeks, mode in ('rot', 'raid', 'mods', 'goals'),
                                     per_day, runs, raid=mode in ('raid', 'goals'), goals=mode == 'goals')
            res[mode].append(rows)
            q07[mode].append((runs // per_day, acc(p).q07))
    return dodge, res, q07, dict(TOT)


LABEL = {'base': '无轮换', 'rot': 'P2-1 轮换', 'abyss': 'P2-2 深渊', 'raid': '轮换 + 团本', 'goals': '团本 + 周目标', 'abyssg': '深渊 + 周目标'}


def report(a):
    from multiprocessing import Pool
    import rules
    modes = a.modes.split(',')
    jobs = [(d, a.players, a.weeks, modes, a.pledge, a.signin, a.seed0) for d in a.dodge]
    with Pool(min(len(jobs), a.nproc)) as pool:
        outs = pool.map(_job, jobs)
    print('# ' + rules.stamp())
    print('# insignia report: players=%d/档 weeks=%d (after the Q07 first clear) modes=%s pledge=%d signin=%s seed0=%d' % (
        a.players, a.weeks, ','.join(modes), a.pledge, a.signin, a.seed0))
    for dodge, res, q07, tot in outs:
        print('\n## 躲技能 %.1f（%s）\n' % (dodge, {0.3: '笨拙', 0.5: '一般', 0.7: '熟练'}.get(dodge, '')))
        for m in modes:
            xs = q07[m]
            if xs:
                print('- %s · 首通 Q07 时（中位第 %d 天）：徽记结余中位 %d（Q01–Q04 %d · Q05–Q06 %d · Q07 %d），已烙印中位 %g 次' % (
                    LABEL.get(m, m), statistics.median(d for d, _ in xs), statistics.median(s['tot'] for _, s in xs),
                    statistics.median(sum(s['bal'][k] for k in MAPS[:4]) for _, s in xs),
                    statistics.median(s['bal']['q05'] + s['bal']['q06'] for _, s in xs),
                    statistics.median(s['bal']['q07'] for _, s in xs), statistics.median(s['brands'] for _, s in xs)))
        print()
        for line in table(res, modes, a.weeks, LABEL, a.every_week):
            print(line)
        print()
        for line in src_line(res, modes, a.weeks):
            print(line)


if __name__ == '__main__':
    ap = argparse.ArgumentParser()
    ap.add_argument('tool', choices=['report'])
    ap.add_argument('--players', type=int, default=40)
    ap.add_argument('--weeks', type=int, default=30)
    ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
    ap.add_argument('--modes', default='rot,goals')
    ap.add_argument('--pledge', type=int, default=0, help='S09: insignia per repeat normal run after own Q06 (0 = no pledge; 2 = lean+reverse)')
    ap.add_argument('--signin', action='store_true', help='S23: install signin.py (online 120, every day signed) + its insignia hook')
    ap.add_argument('--seed0', type=int, default=5000)
    ap.add_argument('--nproc', type=int, default=6)
    ap.add_argument('--every-week', action='store_true')
    a = ap.parse_args()
    report(a)
