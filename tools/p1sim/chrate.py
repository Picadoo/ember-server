"""D104 challenge entry check: clear rate of the challenge maps for a player at the moment of the Q07 first clear.

For each dodge level: phase 1 = p1sim (p2econ.to_q07) until the Q07 first clear, then
  at Q07   : the gear the player actually has (a T3 first-clear / drop piece at +0 and a T2 piece, enhanced)
  +swap    : the free §6.3 swap moves the enhance onto the new T3 piece (both pieces at the higher enhance)
  +T3 blade: then the blade upgraded to T3 (same enhance / quality / craft — upgrade keeps them)
  +T3 both : both pieces T3
and for each: the Q07 normal clear rate and the best of the seven challenge maps (10 runs per map).
Optional --scale-hp / --scale-atk multiply the challenge mob + boss values (tuning); --cfg-from reads the live yml.
Standard library only.
"""
import argparse, copy, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(__file__))
import p1config, p1sim, p2econ


def best_rate(ccfg, st, kn, seed, n):
    rng = random.Random(seed)
    return max(p1sim.clear_rate(ccfg, k, st, kn, n, seed=rng.randrange(1 << 30)) for k in ccfg['order'])


def scaled(ccfg, hp, atk):
    c = copy.deepcopy(ccfg)
    for m in c['maps'].values():
        for mob in m['mobs'].values():
            mob['hp'] *= hp; mob['atk'] *= atk
        b = m['boss']
        b['hp'] *= hp; b['atk'] *= atk
        for sk in b.get('skills', []):
            sk['dmg'] *= atk
            if sk.get('follow'):
                sk['follow']['dmg'] *= atk
    return c


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--players', type=int, default=40)
    ap.add_argument('--dodge', type=float, nargs='+', default=[0.3, 0.5, 0.7])
    ap.add_argument('--runs', type=int, default=10)
    ap.add_argument('--scale-hp', type=float, default=1.0)
    ap.add_argument('--scale-atk', type=float, default=1.0)
    a = ap.parse_args()
    cfg = p1config.load()
    ccfg = scaled(p2econ.challenge_cfg(cfg), a.scale_hp, a.scale_atk)
    print('# chrate: challenge clear rate at the Q07 first clear (%d players/dodge, %d runs per map, hp ×%.2f atk ×%.2f)'
          % (a.players, a.runs, a.scale_hp, a.scale_atk))
    print('| 躲技能 | 攻击 / 生命（中位） | 强化均值（中位） | Q07 普通版 | 挑战最好一张：刚首通 | ≥50% 的人 | 刚首通 + 免费互换 | 再刃升 T3 | 两件 T3 | 两件 T3 ≥50% |')
    print('|---|---|---:|---:|---:|---:|---:|---:|---:|---:|')
    for d in a.dodge:
        rows = []
        for i in range(a.players):
            kn = p1sim.Knobs(d)
            p, runs, rng = p2econ.to_q07(cfg, kn, 5000 + i)
            if p is None:
                continue
            st = p.st()
            q7 = p1sim.clear_rate(cfg, cfg['order'][-1], st, kn, a.runs, seed=77 + i)
            c0 = best_rate(ccfg, st, kn, 900 + i, a.runs)
            saved = (dict(p.blade), dict(p.charm))
            # free §6.3 swap: the new T3 first-clear / drop piece takes the enhance of the piece it replaces
            e = max(p.blade['enh'], p.charm['enh'])
            p.blade['enh'] = p.charm['enh'] = e
            cs = best_rate(ccfg, p.st(), kn, 900 + i, a.runs)
            p.blade['tier'] = 3
            if p.blade['fam'] == 'none':
                p.blade['fam'] = kn.target
            c1 = best_rate(ccfg, p.st(), kn, 900 + i, a.runs)
            p.charm['tier'] = 3
            if p.charm['fam'] == 'none':
                p.charm['fam'] = kn.target
            c2 = best_rate(ccfg, p.st(), kn, 900 + i, a.runs)
            p.blade, p.charm = saved
            rows.append((st['B'], st['H'], (p.blade['enh'] + p.charm['enh']) / 2, q7, c0, c1, c2, cs))
        n = len(rows)
        med = lambda j: statistics.median(r[j] for r in rows)
        mean = lambda j: statistics.mean(r[j] for r in rows)
        print('| %.1f | %.1f / %.0f | %.1f | %d%% | %d%% | %d%% | %d%% | %d%% | %d%% | %d%% |' % (
            d, med(0), med(1), med(2), round(100 * mean(3)), round(100 * mean(4)),
            round(100 * sum(r[4] >= 0.5 for r in rows) / n), round(100 * mean(7)), round(100 * mean(5)), round(100 * mean(6)),
            round(100 * sum(r[6] >= 0.5 for r in rows) / n)))


if __name__ == '__main__':
    main()
