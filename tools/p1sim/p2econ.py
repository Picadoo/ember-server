"""P2 economy check (docs/design-ember-v1.1-P2-draft.md §4): what the weekly challenge rotation (P2-1) adds after Q07.

Phase 1 = p1sim as-is (balance_version 2, stepdown route) until the player's Q07 first clear.
Phase 2 = W weeks of challenge runs only (3/day, the §18.1 T3 overrides from ember-v1-runs.yml `challenge:`), comparing
  base  : no rotation (player farms whichever challenge map it clears best),
  rot   : P2-1 — the week's featured map pays +1 T3 forge mark on each of the first 3 clears per week (no coin, no item).
Reports, per week checkpoint: share of players with a T3 two-piece of the target family, median enhance, coin balance,
T3 marks earned and the mark share of total T3 items. Standard library only; reads the real config through p1config.
"""
import argparse, copy, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(__file__))
import p1config, p1sim, miniyaml

ROOT = os.path.join(os.path.dirname(__file__), '..', '..')
# P2-1 parameter source (D66): ember-v1-runs.yml `rotation:`
_ROT = miniyaml.load(os.path.join(ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml')).get('rotation', {'bonus_marks': 0, 'weekly_cap': 0})
ROT = {k: int(v) for k, v in _ROT.items() if k in ('bonus_marks', 'weekly_cap')}
# P2-8 (D80) weekly rules on the featured map's challenge runs (potion cap / role remap / room 1↔3 swap)
MODS = list(_ROT.get('modifiers') or [])


def mod_cfg(ccfg, key, mod):
    """P2-8: challenge config with the week's rule applied to map `key`; returns (cfg, potion cap or None)."""
    if not mod:
        return ccfg, None
    c = copy.deepcopy(ccfg)
    m = c['maps'][key]
    remap = {k: v for k, v in (mod.get('remap') or {}).items() if v in m['mobs']}  # server: only roles the map defines
    if remap:
        for room in m['rooms'].values():
            for var in ('a', 'b'):
                comp = {}
                for role, n in room[var].items():
                    r = remap.get(role, role)
                    comp[r] = comp.get(r, 0) + n
                room[var] = comp
    if mod.get('swap_rooms'):
        r1, r3 = m['rooms']['r1'], m['rooms']['r3']
        r1['a'], r1['b'], r3['a'], r3['b'] = r3['a'], r3['b'], r1['a'], r1['b']
    cap = int(mod['potion_cap']) if mod.get('potion_cap') else None
    return c, cap


def challenge_cfg(cfg):
    ch = miniyaml.load(os.path.join(ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml'))['challenge']
    c = copy.deepcopy(cfg)
    c['quality_w'] = ch['quality']
    for key, m in c['maps'].items():
        m['tier'] = ch['tier']
        for role, mob in m['mobs'].items():
            if role in ch['mobs']:
                mob['hp'], mob['atk'] = ch['mobs'][role]['hp'], ch['mobs'][role]['atk']
        b = m['boss']
        b['hp'], b['atk'] = ch['boss']['hp'], ch['boss']['atk']
        for s in b.get('skills', []):
            s['dmg'] = ch['boss']['light'] if s.get('light') else ch['boss']['heavy']
            if s.get('follow'):
                s['follow']['dmg'] = ch['boss']['light'] if s['follow'].get('light') else ch['boss']['heavy']
        if b.get('adds'):
            b['adds']['role'] = 'melee'
    return c


ABYSS = miniyaml.load(os.path.join(ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml')).get('abyss', {}).get('tiers', [])


def abyss_cfg(ccfg, t):
    """P2-2: challenge values × tier factors, tier quality table (tiers are 1-based)."""
    row = ABYSS[t - 1]
    c = copy.deepcopy(ccfg)
    c['quality_w'] = row['quality']
    for m in c['maps'].values():
        for mob in m['mobs'].values():
            mob['hp'] *= row['hp']; mob['atk'] *= row['dmg']
        b = m['boss']
        b['hp'] *= row['hp']; b['atk'] *= row['dmg']
        for sk in b.get('skills', []):
            sk['dmg'] *= row['dmg']
            if sk.get('follow'):
                sk['follow']['dmg'] *= row['dmg']
    return c


def abyss_rate(acfg, st, kn, rng, n=3):
    order = acfg['order']
    return statistics.mean(p1sim.clear_rate(acfg, k, st, kn, n, seed=rng.randrange(1 << 30)) for k in order)


def phase2_abyss(cfg, ccfg, kn, p, rng, weeks, per_day, reserve=1000):
    """P2-2 policy: like phase2 until challenge is viable; then every run is an abyss segment at the highest open tier
    (best cleared + 1) whose estimated clear rate is >= 50 % and whose fee leaves `reserve` coins; tier 1 otherwise.
    The fee is paid per started segment (a failed segment keeps it, like stamina)."""
    order = ccfg['order']
    out, marks_earned, ch_runs, best, fees, tiers_played = [], 0, 0, 0, 0, []
    acfgs = {t: abyss_cfg(ccfg, t) for t in range(1, len(ABYSS) + 1)}
    for w in range(weeks):
        rates = {k: p1sim.clear_rate(ccfg, k, p.st(), kn, 10, seed=rng.randrange(1 << 30)) for k in order}
        can_ch = max(rates.values()) >= 0.5
        tier = 1
        for d in range(7):
            if can_ch:  # re-pick the tier every day (players push up as soon as they clear)
                tier = 1
                for t in range(min(best + 1, len(ABYSS)), 1, -1):
                    if ABYSS[t - 1]['fee'] + reserve <= p.coin and abyss_rate(acfgs[t], p.st(), kn, rng) >= 0.5:
                        tier = t
                        break
            for _ in range(per_day):
                p.day = 1000 + w * 7 + d
                if can_ch:
                    if ABYSS[tier - 1]['fee'] + reserve > p.coin and tier > 1:
                        tier -= 1
                    use, key = acfgs[tier], order[rng.randrange(len(order))]
                    p.coin -= ABYSS[tier - 1]['fee']; fees += ABYSS[tier - 1]['fee']
                else:
                    use, key = cfg, order[-1]
                p.buy_potions()
                ok, used, extra, *_ = p1sim.run_map(use, key, p.st(), kn, rng, p.potions)
                p.potions -= used
                ch_runs += use is not cfg
                if ok:
                    p.settle(key, extra) if use is cfg else settle_with(p, use, key, extra)
                    marks_earned += 1
                    if use is not cfg:
                        best = max(best, tier)
                    p.invest()
        tiers_played.append(tier if can_ch else 0)
        tgt = kn.target
        done = p.blade['tier'] == 3 and p.charm['tier'] == 3 and p.blade['fam'] == tgt and p.charm['fam'] == tgt
        out.append({'week': w + 1, 'set': done, 'enh': (p.blade['enh'] + p.charm['enh']) / 2, 'coin': p.coin,
                    'q': max(p.blade['q'], p.charm['q']), 'qmin': min(p.blade['q'], p.charm['q']), 'marks': marks_earned,
                    'B': p.st()['B'], 'ch': ch_runs, 'best': best, 'fees': fees, 'tier': tiers_played[-1]})
    return out


def to_q07(cfg, kn, seed):
    """p1sim.simulate_player, but returns the Player and the run count at the Q07 first clear (None if never)."""
    rng = random.Random(seed)
    p = p1sim.Player(cfg, kn, rng)
    order, per_day = cfg['order'], cfg['stamina_day'] // cfg['run_cost']
    cur, runs, refund_day = 0, 0, -1
    while runs < 60 * per_day:
        front = next((i for i, k in enumerate(order) if k not in p.cleared), None)
        if front is None:
            return p, runs, rng
        cur = min(cur, front)
        key = order[cur]
        p.buy_potions()
        p.day = runs // per_day + 1
        ok, used, extra, *_ = p1sim.run_map(cfg, key, p.st(), kn, rng, p.potions)
        p.potions -= used
        runs += 1
        if ok:
            p.settle(key, extra)
            p.invest()
            cur = front + 1 if cur == front else front
        else:
            day = runs // per_day + 1
            if cfg['death_refund'] > 0 and refund_day != day:
                refund_day = day
                p.potions += min(used, cfg['death_refund'])
            cur = max(0, cur - 1)
    return None, runs, rng


TRADE = {'price': 800, 'fee': 0.10, 'listings': 10}  # P2-3 model knobs (not live parameters; the market is closed)


def buy_listing(p, ccfg, rng):
    """P2-3: once a week buy the best of `listings` unused T3 target-family drops for the weaker slot (sellers list what
    they do not wear; quality per the challenge table, craft per the normal table)."""
    cost = int(TRADE['price'] * (1 + TRADE['fee']))
    if p.coin < cost + 1000:
        return False
    slot = p.weaker_slot(3)
    best = max((p1sim.item(p.kn.target, slot, 3, p1sim.pick(ccfg['quality_w'], rng.random()), p1sim.pick(ccfg['craft_w'], rng.random()))
                for _ in range(TRADE['listings'])), key=lambda it: (it['q'], it['f']))
    p.coin -= cost
    p.consider(best)
    return True


RUNS = miniyaml.load(os.path.join(ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml'))
RAIDS = RUNS.get('raids', {})
# P2-5/P2-6: party clear rate by week after Q07 (tools/p1party.py, 4-player median of r01 / r02 at boss HP 13000:
# week 2 ≈ 0.40, week 4 ≈ 0.76); weeks in between interpolated, capped at week 4's value.
RAID_RATE = {1: 0.30, 2: 0.40, 3: 0.58, 4: 0.76}
RAID_TRIES = 5  # a party gives up for the week after 5 attempts


def raid_cap():
    """Weekly settled raid clears per character: raids sharing a cap_group share one counter (P2-6)."""
    groups = {}
    for k, r in RAIDS.items():
        g = r.get('cap_group') or k
        groups[g] = max(groups.get(g, 0), int(r.get('weekly_cap', 0)))
    return sum(groups.values())


def phase2(cfg, ccfg, kn, p, rng, weeks, rotation, per_day, start_run, trade=False, raid=False, mods=False, stats=None):
    """Each week: if the player clears some challenge map >= 50 % of the time it farms challenge (featured first when
    rotating), otherwise it farms Q07 normal (T3). As on the server, the bonus only pays on challenge clears."""
    order = ccfg['order']
    out, marks_earned, ch_runs = [], 0, 0
    for w in range(weeks):
        featured = order[(w + 3) % len(order)]  # server: ISO week mod 7 (offset arbitrary here)
        rates = {k: p1sim.clear_rate(ccfg, k, p.st(), kn, 10, seed=rng.randrange(1 << 30)) for k in order}
        best = max(rates, key=rates.get)
        can_ch = rates[best] >= 0.5
        bonus_left = ROT['weekly_cap']
        mod = MODS[w % len(MODS)] if mods and MODS else None
        fcfg, fcap = mod_cfg(ccfg, featured, mod)
        if mod:  # featured rate under this week's rule (own seed: the main rng stream stays paired with 'rot')
            kn2 = copy.copy(kn)
            if fcap is not None:
                kn2.potion_keep = min(kn.potion_keep, fcap)
            sd = 7919 * (w + 1) + int(100 * p.st()['B'])
            rates[featured] = p1sim.clear_rate(fcfg, featured, p.st(), kn2, 10, seed=sd)
            if stats is not None and can_ch:  # paired check: same seed, 40 entries, with vs without the rule
                st_ = stats.setdefault(mod['id'], [0, 0.0, 0.0])
                st_[0] += 1
                st_[1] += p1sim.clear_rate(ccfg, featured, p.st(), kn, 40, seed=sd + 1)
                st_[2] += p1sim.clear_rate(fcfg, featured, p.st(), kn2, 40, seed=sd + 1)
        if trade:
            buy_listing(p, ccfg, rng)
        runs_left = 7 * per_day
        p.day = 1000 + w * 7  # raids happen on the week's first day
        if raid and can_ch:  # raids only once the player can farm challenge (same gear bar as the p1party pool)
            stamina, clears, cost = 7 * per_day * cfg['run_cost'], 0, int(next(iter(RAIDS.values()))['cost'])
            for _ in range(RAID_TRIES):
                if clears >= raid_cap() or stamina < cost:
                    break
                stamina -= cost
                if rng.random() < RAID_RATE[min(w + 1, 4)]:
                    clears += 1
                    if OLD_RAID_ITEM:
                        settle_with(p, ccfg, order[-1], 'chest')  # pre-P2-9: challenge settlement + one more plain T3 roll
                    else:  # P2-9 (D82): raid_item = the player's target family, quality floor 精良
                        settle_with(p, ccfg, order[-1], 'none')
                        p.consider(p1sim.item(kn.target, 'blade' if rng.random() < 0.5 else 'charm', 3,
                                              max(RAID_FLOOR, p1sim.pick(ccfg['quality_w'], rng.random())),
                                              p1sim.pick(ccfg['craft_w'], rng.random())))
                    p.marks[3] += 1; marks_earned += 2       # raid_mark + the base mark
                    p.invest()
            runs_left = stamina // cfg['run_cost']
        for i in range(runs_left):
            p.day = 1000 + w * 7 + i * 7 // max(1, runs_left)
            if can_ch:
                use, key = ccfg, (featured if rotation and bonus_left > 0 and rates[featured] >= 0.3 else farm_map(p, kn, ccfg, rates, best))
            else:
                use, key = cfg, order[-1]
            p.buy_potions()
            if mod and use is ccfg and key == featured:
                ok, used, extra, *_ = p1sim.run_map(fcfg, key, p.st(), kn, rng, min(p.potions, fcap) if fcap is not None else p.potions)
            else:
                ok, used, extra, *_ = p1sim.run_map(use, key, p.st(), kn, rng, p.potions)
            p.potions -= used
            ch_runs += use is ccfg
            if ok:
                if rotation and use is ccfg and key == featured and bonus_left > 0:
                    bonus_left -= 1
                    p.marks[3] += ROT['bonus_marks']
                    marks_earned += ROT['bonus_marks']
                p.settle(key, extra) if use is cfg else settle_with(p, ccfg, key, extra)
                marks_earned += 1
                p.invest()
        tgt = kn.target
        done = p.blade['tier'] == 3 and p.charm['tier'] == 3 and p.blade['fam'] == tgt and p.charm['fam'] == tgt
        out.append({'week': w + 1, 'set': done, 'enh': (p.blade['enh'] + p.charm['enh']) / 2, 'coin': p.coin,
                    'q': max(p.blade['q'], p.charm['q']), 'qmin': min(p.blade['q'], p.charm['q']), 'marks': marks_earned,
                    'B': p.st()['B'], 'ch': ch_runs, 'best': 0, 'fees': 0, 'tier': 0})
    return out


OLD_RAID_ITEM = False
RAID_FLOOR = 1  # P2-9 (D82) raid_item quality floor (1 = 精良), from ember-v1-runs.yml raid_item.quality_floor


def farm_map(p, kn, ccfg, rates, best):
    """P2-9: with per-map loot identity the player farms the map of its target family and weaker slot when that map
    clears within 10 points of its best one (otherwise the best map, as before)."""
    if not ccfg.get('loot_bias'):
        return best
    bl, ch = p.blade, p.charm
    want = 'blade' if (bl['fam'] != kn.target, -bl['q'], bl['enh']) >= (ch['fam'] != kn.target, -ch['q'], ch['enh']) else 'charm'
    def score(k):
        lo = ccfg['maps'][k].get('loot') or {}
        return (lo.get('family') == kn.target) * 2 + (lo.get('slot') == want), rates[k]
    ok = [k for k in rates if rates[k] >= max(0.5, rates[best] - 0.10)]
    return max(ok, key=score) if ok else best


def settle_with(p, ccfg, key, extra):
    """Challenge settlement: same base, T3 items with the challenge quality table, no first clear (already cleared)."""
    saved = p.cfg
    p.cfg = ccfg
    try:
        p.settle(key, extra)
    finally:
        p.cfg = saved


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--players', type=int, default=60)
    ap.add_argument('--weeks', type=int, default=8)
    ap.add_argument('--dodge', type=float, default=0.5)
    ap.add_argument('--no-swap', action='store_true', help='phase 2 without the §6.3 enhance-track swap (old behaviour)')
    ap.add_argument('--trade', action='store_true', help='add the P2-3 market model (weekly best-of-10 purchase)')
    ap.add_argument('--abyss', action='store_true', help='add the P2-2 abyss policy as a third column')
    ap.add_argument('--raid', action='store_true', help='add the P2-5/6 raids (rotation + weekly raid clears, shared cap)')
    ap.add_argument('--loot-own', type=float, help='override loot_bias.own_family (tuning)')
    ap.add_argument('--loot-slot', type=float, help='override loot_bias.slot (tuning)')
    ap.add_argument('--no-loot', action='store_true', help='without the P2-9 per-map loot identity (D81)')
    ap.add_argument('--old-raid-item', action='store_true', help='pre-P2-9 raid_item (plain roll, no family target, no floor)')
    ap.add_argument('--mods', action='store_true', help='add the P2-8 weekly rules on the featured map (rotation + rule)')
    ap.add_argument('--no-bounty', action='store_true', help='without the P2-7 daily bounty (D79) for comparison')
    a = ap.parse_args()
    cfg = p1config.load()
    if a.no_bounty:
        cfg['bounty'] = []
    if a.no_loot:
        cfg['loot_bias'] = {}
    if a.loot_own is not None:
        cfg['loot_bias']['own_family'] = a.loot_own
    if a.loot_slot is not None:
        cfg['loot_bias']['slot'] = a.loot_slot
    global OLD_RAID_ITEM, RAID_FLOOR
    OLD_RAID_ITEM = a.old_raid_item
    RAID_FLOOR = int((RUNS.get('raid_item') or {}).get('quality_floor', 1))
    ccfg = challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    modes = ('base', 'rot') + (('abyss',) if a.abyss else ()) + (('trade',) if a.trade else ()) + (('raid',) if a.raid else ()) + (('mods',) if a.mods else ())
    res = {m: [] for m in modes}
    stats = {}
    for i in range(a.players):
        for mode in modes:
            kn = p1sim.Knobs(a.dodge)
            p, runs, rng = to_q07(cfg, kn, 5000 + i)   # same seed → same phase-1 player for both modes
            if p is None:
                continue
            kn.swap = not a.no_swap  # §6.3 free enhance-track swap: late-game players re-equip better-quality drops
            if mode == 'abyss':
                res[mode].append(phase2_abyss(cfg, ccfg, kn, p, random.Random(9000 + i), a.weeks, per_day))
            else:
                res[mode].append(phase2(cfg, ccfg, kn, p, random.Random(9000 + i), a.weeks, mode in ('rot', 'raid', 'mods'), per_day, runs,
                                        trade=mode == 'trade', raid=mode == 'raid', mods=mode == 'mods',
                                        stats=stats if mode == 'mods' else None))
    print('# p2econ: %d players reached Q07 (dodge %.2f), %d challenge weeks after it, 3 runs/day' % (len(res['base']), a.dodge, a.weeks))
    print('| 周 | 方案 | T3 目标族两件 | 强化均值（中位） | 最好成色≥卓越 | 两件都≥卓越 | 有极品 | 两件极品 | 余烬币（中位） | 累计 T3 印记（中位） | B（中位） | 挑战/深渊局占比 | 深渊最高层（中位） | 累计深渊费（中位） |')
    print('|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
    for w in (1, 2, 4, 6, 8, 10, 12):
        if w > a.weeks:
            continue
        for mode in modes:
            rows = [r[w - 1] for r in res[mode]]
            if not rows:
                continue
            n = len(rows)
            print('| %d | %s | %d%% | %.1f | %d%% | %d%% | %d%% | %d%% | %d | %d | %.1f | %d%% | %s | %s |' % (
                w, {'base': '无轮换', 'rot': 'P2-1 轮换', 'abyss': 'P2-2 深渊', 'trade': 'P2-3 交易', 'raid': '轮换 + 团本', 'mods': '轮换 + 周规则'}[mode], round(100 * sum(r['set'] for r in rows) / n),
                statistics.median(r['enh'] for r in rows), round(100 * sum(r['q'] >= 2 for r in rows) / n),
                round(100 * sum(r['qmin'] >= 2 for r in rows) / n), round(100 * sum(r['q'] >= 3 for r in rows) / n),
                round(100 * sum(r['qmin'] >= 3 for r in rows) / n),
                statistics.median(r['coin'] for r in rows), statistics.median(r['marks'] for r in rows),
                statistics.median(r['B'] for r in rows), round(100 * statistics.mean(r['ch'] for r in rows) / (w * 7 * per_day)),
                ('%d' % statistics.median(r['best'] for r in rows)) if mode == 'abyss' else '—',
                ('%d' % statistics.median(r['fees'] for r in rows)) if mode == 'abyss' else '—'))

    if a.mods:
        print()
        print('P2-8 精选图挑战通关率（能刷挑战的玩家-周；同一种子 40 局配对：无规则 vs 本周规则）：')
        print('| 规则 | 玩家-周 | 无规则 | 有规则 | 差 |')
        print('|---|---:|---:|---:|---:|')
        for m in MODS:
            n, b, v = stats.get(m['id'], [0, 0.0, 0.0])
            print('| %s %s | %d | %d%% | %d%% | %+d 点 |' % (m['id'], m.get('name', ''), n, round(100 * b / max(1, n)),
                                                       round(100 * v / max(1, n)), round(100 * (v - b) / max(1, n))))

if __name__ == '__main__':
    main()
