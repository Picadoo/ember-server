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
ROT = {k: int(v) for k, v in miniyaml.load(os.path.join(ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml')).get(
    'rotation', {'bonus_marks': 0, 'weekly_cap': 0}).items()}


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


def phase2(cfg, ccfg, kn, p, rng, weeks, rotation, per_day, start_run):
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
        for _ in range(7 * per_day):
            if can_ch:
                use, key = ccfg, (featured if rotation and bonus_left > 0 and rates[featured] >= 0.3 else best)
            else:
                use, key = cfg, order[-1]
            p.buy_potions()
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
                    'q': max(p.blade['q'], p.charm['q']), 'marks': marks_earned, 'B': p.st()['B'], 'ch': ch_runs})
    return out


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
    a = ap.parse_args()
    cfg = p1config.load()
    ccfg = challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    res = {'base': [], 'rot': []}
    for i in range(a.players):
        for mode in ('base', 'rot'):
            kn = p1sim.Knobs(a.dodge)
            p, runs, rng = to_q07(cfg, kn, 5000 + i)   # same seed → same phase-1 player for both modes
            if p is None:
                continue
            res[mode].append(phase2(cfg, ccfg, kn, p, random.Random(9000 + i), a.weeks, mode == 'rot', per_day, runs))
    print('# p2econ: %d players reached Q07 (dodge %.2f), %d challenge weeks after it, 3 runs/day' % (len(res['base']), a.dodge, a.weeks))
    print('| 周 | 方案 | T3 目标族两件 | 强化均值（中位） | 最好成色≥卓越 | 余烬币（中位） | 累计 T3 印记（中位） | B（中位） | 挑战局占比 |')
    print('|---|---|---:|---:|---:|---:|---:|---:|---:|')
    for w in (1, 2, 4, 8, 12):
        if w > a.weeks:
            continue
        for mode in ('base', 'rot'):
            rows = [r[w - 1] for r in res[mode]]
            if not rows:
                continue
            n = len(rows)
            print('| %d | %s | %d%% | %.1f | %d%% | %d | %d | %.1f | %d%% |' % (
                w, '无轮换' if mode == 'base' else 'P2-1 轮换', round(100 * sum(r['set'] for r in rows) / n),
                statistics.median(r['enh'] for r in rows), round(100 * sum(r['q'] >= 2 for r in rows) / n),
                statistics.median(r['coin'] for r in rows), statistics.median(r['marks'] for r in rows),
                statistics.median(r['B'] for r in rows), round(100 * statistics.mean(r['ch'] for r in rows) / (w * 7 * per_day))))


if __name__ == '__main__':
    main()
