"""Raid composition check after the M03 fix (multi-焚烬 was undercounted): how much does the number of 焚烬 members
change the clear rate, across several independent player pools (clustered CI over pools)?
  python3 raidcomp.py OUT.json [--trials T] [--procs P] [--dodge 0.5] [--pools 7000,17000,...]
then  python3 raidcomp.py --report OUT.json [more.json ...] > out-build-diversity-raidcomp.md
Same protocol as simfix_check.py PART=comp (k members forced to 焚烬, the rest re-dressed 烬爆 if they were 焚烬; group
draw and per-run seeds identical for every k → paired within a pool), repeated per pool."""
import sys, os, json, random, time, statistics, multiprocessing as mp
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ, p1party, growth, rules

args = sys.argv[1:]
def opt(name, d, cast):
    if name in args:
        i = args.index(name); return cast(args[i + 1])
    return d
TRIALS = opt('--trials', 1500, int); PROCS = opt('--procs', 6, int); POOL = 36
DODGE = opt('--dodge', 0.5, float)
POOLS = opt('--pools', [7000, 17000, 27000, 37000, 47000, 57000, 67000], lambda s: [int(x) for x in s.split(',')])
COMPS = ((3, (0, 1, 2, 3)), (4, (0, 2, 4)), (5, (0, 1, 3, 5)))
RAIDS = ('r01', 'r02', 'r03')
cfg = None


def players(dodge, seed0):
    growth.disable()
    ccfg = p2econ.challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    out, i = [], 0
    while len(out) < POOL and i < POOL * 3:
        kn = p1sim.Knobs(dodge)
        p, runs, rng = p2econ.to_q07(cfg, kn, seed0 + i)
        i += 1
        if p is None:
            continue
        kn.swap = True
        p2econ.phase2(cfg, ccfg, kn, p, random.Random(seed0 + 99 + i), 2, False, per_day, runs)
        out.append((p, kn))
    return out


def job(j):
    global cfg
    raid, seed0 = j
    cfg = p1config.load()
    rr = rules.runs().get('raid_revive') or {}
    p1party.LAST_REVIVE_HP = float(rr.get('last_phase_hp', 0)); p1party.LAST_REVIVE_DELAY = float(rr.get('delay', 10))
    ps = players(DODGE, seed0)
    pool = [(p.st(), kn) for p, kn in ps]
    m = p1party.raid_map(cfg, raid)
    out = {}
    for n, ks in COMPS:
        for k in ks:
            rng = random.Random(4242)
            wins = []
            for i in range(TRIALS):
                grp = rng.sample(range(len(pool)), n)
                sts, kns = [], []
                for jj, idx in enumerate(grp):
                    st = dict(pool[idx][0])
                    st['set'] = 'scorch' if jj < k else ('burst' if st['set'] == 'scorch' else st['set'])
                    sts.append(st); kns.append(pool[idx][1])
                wins.append(int(p1party.run_party(cfg, m, sts, kns, random.Random(900001 + i))[0]))
            out['%d/%d' % (n, k)] = wins
    natural = sum(1 for s, _ in pool if s['set'] == 'scorch') / len(pool)
    return [raid, seed0], {'wins': out, 'scorch_share': natural}


T975 = {1: 12.71, 2: 4.30, 3: 3.18, 4: 2.78, 5: 2.57, 6: 2.45, 7: 2.36, 8: 2.31, 9: 2.26, 10: 2.23}


def ci(xs):
    m = statistics.mean(xs)
    if len(xs) < 2:
        return m, float('nan')
    return m, T975.get(len(xs) - 1, 2.0) * statistics.stdev(xs) / len(xs) ** 0.5


def report(files):
    R = {}
    meta = []
    for f in files:
        d = json.load(open(f))
        meta.append('%s: %d 个团本 × %d 池 × 每格 %d 局，躲避 %.1f' % (os.path.basename(f), len({r[0][0] for r in d['results']}), len({r[0][1] for r in d['results']}), d['trials'], d['dodge']))
        for (raid, s0), r in d['results']:
            R.setdefault((d['dodge'], raid), {})[s0] = r
    print('# 团本焚烬人数对照（多池聚类区间，M03 修正后），2026-10-04 CST\n')
    print('%s。每个池 36 人（Q07 后 2 周，`p2econ.to_q07` + `phase2`），k 名强制穿焚烬、其余原本穿焚烬的改烬爆；同一池内抽队与每局种子对所有 k 相同（配对）。'
          '**区间 = 池间聚类 95%% t 区间**（把每个池的 "k − 0" 差当一个观测），所以包含了池效应。%s\n' % ('；'.join(meta), rules.stamp()))
    for (dodge, raid), pools in sorted(R.items()):
        print('## %s · 躲避 %.1f（%d 池）\n' % (raid.upper(), dodge, len(pools)))
        print('| 人数 | 焚烬人数 k | 通关率（池均值） | 相对 k=0（pp，95% 区间） | 相对"自然构成"期望（pp） |')
        print('|---:|---:|---:|---|---|')
        share = statistics.mean(r['scorch_share'] for r in pools.values())
        for n, ks in COMPS:
            rates = {k: [100 * statistics.mean(r['wins']['%d/%d' % (n, k)]) for r in pools.values()] for k in ks}
            for k in ks:
                m, h = ci(rates[k])
                dm, dh = ci([a - b for a, b in zip(rates[k], rates[0])])
                print('| %d | %d | %.1f | %s | |' % (n, k, m, '—' if k == 0 else '%+.1f [%+.1f, %+.1f]' % (dm, dm - dh, dm + dh)))
        print('\n池里自然穿焚烬的比例 %.0f%%（随机组队时 3 人全焚烬的概率约 %.1f%%）。\n' % (100 * share, 100 * share ** 3))


if __name__ == '__main__':
    if args and args[0] == '--report':
        report(args[1:]); sys.exit(0)
    print(rules.stamp(), flush=True)
    t0 = time.time()
    js = [(r, s) for r in RAIDS for s in POOLS]
    with mp.Pool(PROCS) as pool:
        res = pool.map(job, js, chunksize=1)
    json.dump({'trials': TRIALS, 'dodge': DODGE, 'elapsed': time.time() - t0, 'results': [[list(j), r] for j, r in res]},
              open(args[0], 'w'))
    print('done', round(time.time() - t0), 's')
