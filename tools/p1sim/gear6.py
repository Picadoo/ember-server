#!/usr/bin/env python3
"""Gear STAGE 0 · 6-slot model checks (docs/design/DESIGN-ember-gear-6slot-stage0-2026-10-04.md).

Uses p1sim's opt-in 6-slot model (P1SIM_SIX / p1sim.SIX; None = today's 2-slot model, unchanged). All runs offline.

  python3 gear6.py tol  '<json {name: {"w": [...], "armor": "eq|enh1|q1|f1|..."} | null}>' N OUT.pkl SEEDS   # static 42 cells
  python3 gear6.py tolrep OUT.pkl
  python3 gear6.py prog '<json {name: SIX | null}>' PLAYERS OUT.pkl                # dynamic Q01→Q07 first clears (paired seeds)
  python3 gear6.py progrep OUT.pkl                                                 # first-clear day · H vs 2-slot · armor lag
  python3 gear6.py entrep OUT.pkl                                                  # frontier clear rate before the first clear
  python3 gear6.py affix                                                           # charm-pool affix x1 vs x5 (4 armor stacks)
SIX keys: w = (charm, head, chest, legs, boots) shares of the charm's h / D; drop = armor drops per completed run (fraction =
probability); start = true (Q01 head+chest, Q02 legs+boots) | "q01" (all four at Q01); up_all = upgrade every affordable
armor piece in one invest pass; gap = gap-weighted armor slot; cost = {"charm": x, "armor": y} enhance / upgrade cost multipliers;
cost_up = same shape, upgrade-only override; arm_cap = true (armor upgraded only up to the charm tier) | "all" (drops capped too);
blank = white-blank yield of a dismantled armor piece (x tier; default 1.0).
D246 (2026-10-06): base_armor = share of the settlement base roll that becomes armor (today's "base covers all slots";
violates the staged-plan §4.4 no-dilution limit, diagnostic only); base_mapslot / chest_armor = variants; arm_repeat = the
extra armor drop only on repeat runs (none on the player's own first-clear run of a map); arm_front = armor dropped on a
first-clear run is one tier lower.
"""
import os
import pickle
import statistics
import sys
from multiprocessing import Pool

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import json  # noqa: E402
import p1config  # noqa: E402
import p1sim  # noqa: E402

MAPS = ['q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07']
NPROC = int(os.environ.get('NPROC', '6'))


def _tol_job(j):
    import builddiv as bd
    r = bd.measure(j)
    return {'clear': r['clear'], 'H': r['H']}


def tol(V, n, out, seeds):
    jobs, keys = [], []
    for name, six in V.items():
        for fam in ('burst', 'scorch', 'sustain'):
            sp = {'set': fam}
            if six:
                sp['six'] = {'w': six['w']}
                sp['armor'] = six['armor']
            for k in p1sim.REF_GEAR:
                for kind in 'rc':
                    for d in (0.3, 0.5, 0.7):
                        for sd in seeds:
                            jobs.append((sp, k + kind, d, 'base', n, sd))
                            keys.append((name, fam, k + kind, d, sd))
    with Pool(NPROC) as p:
        res = p.map(_tol_job, jobs, chunksize=4)
    R = pickle.load(open(out, 'rb')) if os.path.exists(out) else {}
    R.update(dict(zip(keys, res)))
    pickle.dump(R, open(out, 'wb'))
    print('wrote', out, len(R))


def tolrep(path):
    R = pickle.load(open(path, 'rb'))
    names = sorted({k[0] for k in R} - {'base'})
    cells = sorted({(k[2], k[3]) for k in R})
    seeds = sorted({k[4] for k in R})

    def cl(nm, fam, c, d):
        return statistics.mean(R[(nm, fam, c, d, s)]['clear'] for s in seeds)
    print('| 方案 | 套 | ±2 内 | 最高 | 最低 | 平均 | H 变化（Q04r / Q07r / Q07c） |')
    print('|---|---|---|---|---|---|---|')
    for nm in names:
        for fam in ('burst', 'scorch', 'sustain'):
            if (nm, fam, 'q01r', 0.3, seeds[0]) not in R:
                continue
            dd = {(c, d): 100 * (cl(nm, fam, c, d) - cl('base', fam, c, d)) for c, d in cells}
            up = max(dd.items(), key=lambda x: x[1])
            dn = min(dd.items(), key=lambda x: x[1])

            def h(c):
                return 100 * (R[(nm, fam, c, 0.5, seeds[0])]['H'] / R[('base', fam, c, 0.5, seeds[0])]['H'] - 1)
            print('| %s | %s | %d/42 | %+.1f (%s d%.1f) | %+.1f (%s d%.1f) | %+.1f | %+.1f%% / %+.1f%% / %+.1f%% |' % (
                nm, fam, sum(abs(v) <= 2 for v in dd.values()), up[1], up[0][0], up[0][1], dn[1], dn[0][0], dn[0][1],
                statistics.mean(dd.values()), h('q04r'), h('q07r'), h('q07c')))


def _prog_job(a):
    name, six, d, seed = a
    p1sim.SIX = six
    cfg = p1config.load()
    kn = p1sim.Knobs(d)
    rec, runs = p1sim.simulate_player(cfg, kn, seed, max_runs=60 * (cfg['stamina_day'] // cfg['run_cost']))
    return (name, d, seed), {k: {x: r.get(x) for x in ('fc_day', 'fc_run', 'H', 'M', 'B', 'armor', 'charm_te', 'entries',
                                                        'front_entries', 'deaths', 'clears')} for k, r in rec.items()}


def prog(V, n, out):
    jobs = [(nm, six, d, 1000 + i) for nm, six in V.items() for d in (0.3, 0.5, 0.7) for i in range(n)]
    with Pool(NPROC) as p:
        res = p.map(_prog_job, jobs, chunksize=2)
    R = pickle.load(open(out, 'rb')) if os.path.exists(out) else {}
    R.update(dict(res))
    pickle.dump(R, open(out, 'wb'))
    print('wrote', out, len(R))


def _med(xs):
    xs = [x for x in xs if x is not None]
    return statistics.median(xs) if xs else None


def progrep(path):
    R = pickle.load(open(path, 'rb'))
    names = sorted({k[0] for k in R}, key=lambda x: (x != 'base', x))
    for d in (0.3, 0.5, 0.7):
        print('\n### 躲避 %.1f：首通在第几天（中位）· 首通时 H 相对 2 槽（同种子配对，中位）· 首通时护甲平均强化差（护甲 − 护符）\n' % d)
        print('| 方案 | ' + ' | '.join(m.upper() for m in MAPS) + ' | Q07 首通率 60 天 |')
        print('|---|' + '---|' * (len(MAPS) + 1))
        seeds = sorted({k[2] for k in R if k[1] == d})
        for nm in names:
            cells = []
            for m in MAPS:
                fd = _med([R[(nm, d, s)][m]['fc_day'] for s in seeds])
                hr = _med([R[(nm, d, s)][m]['H'] / R[('base', d, s)][m]['H'] - 1 for s in seeds
                           if R[(nm, d, s)][m]['H'] and R[('base', d, s)][m]['H']])
                lag = _med([statistics.mean(a[1] for a in R[(nm, d, s)][m]['armor']) - R[(nm, d, s)][m]['charm_te'][1]
                            for s in seeds if R[(nm, d, s)][m].get('armor')])
                cells.append('%s · %s%s' % ('—' if fd is None else '%g' % fd, '—' if hr is None else '%+.1f%%' % (100 * hr),
                                            '' if lag is None else ' · %+.1f' % lag))
            reach = sum(1 for s in seeds if R[(nm, d, s)]['q07']['fc_day'] is not None) / len(seeds)
            print('| %s | %s | %.0f%% |' % (nm, ' | '.join(cells), 100 * reach))


def entrep(path):
    R = pickle.load(open(path, 'rb'))
    names = sorted({k[0] for k in R}, key=lambda x: (x != 'base', x))
    for d in (0.3, 0.5, 0.7):
        seeds = sorted({k[2] for k in R if k[1] == d})
        print('\n### 躲避 %.1f：首通前在前线的平均进入次数；括号内 = 首通前通关率（首通数 / 前线进入数）相对 2 槽变化 pp\n' % d)
        print('| 方案 | ' + ' | '.join(m.upper() for m in MAPS) + ' | ±2 内 |')
        print('|---|' + '---|' * (len(MAPS) + 1))
        base = {}
        for nm in names:
            cells, ok = [], 0
            for m in MAPS:
                e = [R[(nm, d, s)][m]['front_entries'] for s in seeds if R[(nm, d, s)][m]['fc_day'] is not None]
                rate = len(e) / sum(e) if e else 0
                if nm == 'base':
                    base[m] = rate
                dpp = 100 * (rate - base[m])
                ok += abs(dpp) <= 2
                cells.append('%.2f (%s)' % (statistics.mean(e), '—' if nm == 'base' else '%+.1f' % dpp))
            print('| %s | %s | %s |' % (nm, ' | '.join(cells), '—' if nm == 'base' else '%d/7' % ok))


AFFIX_V = {'none': None, 'c_affix1': {'taken_affix': 0.84}, 'c_affix5': {'taken_affix': 0.84 ** 5},
           'c_share1': {'share_taken': 0.96}, 'c_share5': {'share_taken': 0.96 ** 5},
           'c_tele1': {'taken_tele': 0.98}, 'c_tele5': {'taken_tele': 0.98 ** 5}}


def _affix_job(j):
    import builddiv as bd
    return bd.measure(j)['clear']


def affix(n=2000, seed=7):
    jobs, keys = [], []
    for nm, ex in AFFIX_V.items():
        for fam in ('burst', 'scorch', 'sustain'):
            sp = {'set': fam}
            if ex:
                sp['extra'] = ex
            for k in p1sim.REF_GEAR:
                for kind in 'rc':
                    for d in (0.3, 0.5, 0.7):
                        jobs.append((sp, k + kind, d, 'base', n, seed))
                        keys.append((nm, fam, k + kind, d))
    with Pool(NPROC) as p:
        R = dict(zip(keys, p.map(_affix_job, jobs, chunksize=4)))
    cells = sorted({(k[2], k[3]) for k in R})
    print('| 词条（4 档） | 套 | 平均 pp | 最高 pp | 超 +2 的格 |')
    print('|---|---|---|---|---|')
    for a in ('c_affix', 'c_share', 'c_tele'):
        for fam in ('burst', 'scorch', 'sustain'):
            for lvl in ('1', '5'):
                dd = [100 * (R[(a + lvl, fam, c, d)] - R[('none', fam, c, d)]) for c, d in cells]
                print('| %s ×%s | %s | %+.1f | %+.1f | %d/42 |' % (a, lvl, fam, sum(dd) / 42, max(dd), sum(x > 2 for x in dd)))


if __name__ == '__main__':
    cmd = sys.argv[1]
    if cmd == 'tol':
        tol(json.loads(sys.argv[2]), int(sys.argv[3]), sys.argv[4], [int(x) for x in sys.argv[5].split(',')])
    elif cmd == 'prog':
        prog(json.loads(sys.argv[2]), int(sys.argv[3]), sys.argv[4])
    elif cmd in ('tolrep', 'progrep', 'entrep'):
        globals()[cmd](sys.argv[2])
    elif cmd == 'affix':
        affix()
    else:
        sys.exit(__doc__)
