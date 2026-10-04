"""Pre-/post-fix model check (M01-M03 + M06). Same script for both code trees:
  python3 simfix_check.py CODE_DIR OUT.json PART [--trials T] [--procs P]
PART: raid | comp | graid | solo | pools | gpools   (CODE_DIR = pre-fix tree from `git archive bfcff7e` or this dir; then simfix_report.py → out-simfix-m01-m06.md body)
Only uses APIs present before and after the fix (p1party.player_pool/evaluate/run_party, growth.build/enable, p1sim.clear_rate)."""
import sys, os, json, random, time, multiprocessing as mp
CODE, OUT, PART = sys.argv[1], sys.argv[2], sys.argv[3]
args = sys.argv[4:]
def opt(name, d, cast):
    if name in args:
        i = args.index(name); return cast(args[i + 1])
    return d
TRIALS = opt('--trials', 2000, int); PROCS = opt('--procs', 6, int); POOL = opt('--pool', 36, int)
sys.path.insert(0, CODE)
import p1config, p1sim, p2econ, p1party, growth, miniyaml

cfg = p1config.load()
G = growth.load()
ROW3 = {'scorch': 't3a', 'burst': 't3b', 'sustain': 't3c'}
BUILDS = {'off': None}
for r1 in ('t1a', 't1b', 't1c'):
    BUILDS[r1 + '+t2a+row3'] = {'talents': [r1, 't2a'], 'honors': 'all', 'affixes': {'blade': 'b_set', 'charm': 'c_tele'}, 'affix_tier': 4}
RAIDS = ('r01', 'r02', 'r03')
DODGES = (0.3, 0.5, 0.7)


def setup_revive():
    try:
        import rules  # post-fix tree: the canonical snapshot
        rr = rules.runs().get('raid_revive') or {}
    except ImportError:  # pre-fix tree (bfcff7e) has no rules.py
        rr = (miniyaml.load(os.path.join(p1config.ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml')).get('raid_revive') or {})
    p1party.LAST_REVIVE_HP = float(rr.get('last_phase_hp', 0)); p1party.LAST_REVIVE_DELAY = float(rr.get('delay', 10))


def fn_for(b):
    def fn(info, ctx):
        t = list(b.get('talents', []))
        if len(t) >= 2 and ROW3.get(info.get('set')):
            t.append(ROW3[info.get('set')])
        return growth.build(G, t, b.get('honors', ()), b.get('affixes'), b.get('affix_tier', 4))(info, ctx)
    return fn


def players(dodge, seed0=7000, weeks=2):
    """growthraid.players (same seeds)"""
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
        p2econ.phase2(cfg, ccfg, kn, p, random.Random(seed0 + 99 + i), weeks, False, per_day, runs)
        out.append((p, kn))
    return out


def sts_for(ps, build):
    p1sim.GROWTH = fn_for(build) if build else None
    pool = [(p.st(), kn) for p, kn in ps]
    growth.disable()
    return pool


def summ(res):
    ok = [r for r in res if r[0]]
    return {'n': len(res), 'ok': len(ok), 'rate': len(ok) / len(res),
            'secs': sorted(r[1] for r in ok)[len(ok) // 2] if ok else None,
            'deaths': sum(r[2] for r in res) / len(res), 'pot': sum(r[3] for r in res) / len(res),
            'wins': [int(r[0]) for r in res]}


def job(j):
    setup_revive()
    kind = j[0]
    if kind == 'pools':
        _, raid, d, seed0 = j
        ps = players(d, seed0)
        pool = sts_for(ps, None)
        m = p1party.raid_map(cfg, raid)
        r = p1party.evaluate(cfg, m, pool, (3, 4, 5), TRIALS, random.Random(11))
        return j, {'eval': {str(n): {k: v for k, v in x.items() if k != 'wins'} for n, x in r.items()},
                   'pool_sets': [s['set'] for s, _ in pool], 'pool_B': [s['B'] for s, _ in pool]}
    if kind == 'gpools':
        _, raid, d, seed0 = j
        ps = players(d, seed0)
        m = p1party.raid_map(cfg, raid)
        out = {}
        for bl in BUILDS:
            pool = sts_for(ps, BUILDS[bl])
            r = p1party.evaluate(cfg, m, pool, (3, 4, 5), TRIALS, random.Random(11))
            out[bl] = {str(n): {'rate': x['rate'], 'wins': x['wins'], 'lineup': x['lineup']} for n, x in r.items()}
        return j, out
    if kind in ('raid', 'graid'):
        _, raid, d, bl = j
        ps = players(d)
        pool = sts_for(ps, BUILDS[bl])
        m = p1party.raid_map(cfg, raid)
        t0 = time.time()
        r = p1party.evaluate(cfg, m, pool, (3, 4, 5), TRIALS, random.Random(11))
        out = {str(n): {k: v for k, v in x.items()} for n, x in r.items()}
        return j, {'eval': out, 'pool_sets': [s['set'] for s, _ in pool], 'pool_B': [s['B'] for s, _ in pool], 'secs': time.time() - t0}
    if kind == 'comp':
        _, raid, d, n, k = j
        ps = players(d)
        pool = sts_for(ps, None)
        m = p1party.raid_map(cfg, raid)
        rng = random.Random(4242)
        res = []
        for i in range(TRIALS):
            grp = rng.sample(range(len(pool)), n)
            sts, kns = [], []
            for jj, idx in enumerate(grp):
                st = dict(pool[idx][0])
                st['set'] = 'scorch' if jj < k else ('burst' if st['set'] == 'scorch' else st['set'])
                sts.append(st); kns.append(pool[idx][1])
            res.append(p1party.run_party(cfg, m, sts, kns, random.Random(900001 + i)))
        return j, summ(res)
    if kind == 'solo':
        _, key, fam, bl = j
        ccfg = p2econ.challenge_cfg(cfg)
        b = BUILDS[bl]
        if b:
            growth.enable(b['talents'] + [ROW3[fam]], b['honors'], b['affixes'], b['affix_tier'], G)
        else:
            growth.disable()
        bt, be, ct, ce, lv = p1sim.REF_GEAR[key]
        st = p1sim.stats(cfg, dict(p1sim.item(fam if bt else 'none', 'blade', bt), enh=be), dict(p1sim.item(fam if ct else 'none', 'charm', ct), enh=ce), lv)
        st3 = p1sim.stats(cfg, dict(p1sim.item(fam, 'blade', 3), enh=6), dict(p1sim.item(fam, 'charm', 3), enh=6), 30)
        r = {}
        for d in DODGES:
            for s in (7, 11, 13):
                r['n%.1f/%d' % (d, s)] = p1sim.clear_rate(cfg, key, st, p1sim.Knobs(d), TRIALS, seed=s)
                r['c%.1f/%d' % (d, s)] = p1sim.clear_rate(ccfg, key, st3, p1sim.Knobs(d), TRIALS, seed=s)
        growth.disable()
        return j, r


def jobs():
    if PART == 'raid':
        return [('raid', r, d, 'off') for r in RAIDS for d in DODGES]
    if PART == 'graid':
        return [('graid', r, d, b) for r in RAIDS for d in DODGES for b in BUILDS if b != 'off']
    if PART == 'comp':
        return [('comp', r, 0.5, n, k) for r in RAIDS for n, ks in ((3, (0, 1, 2, 3)), (5, (0, 1, 3, 5))) for k in ks]
    if PART == 'gpools':
        return [('gpools', r, 0.5, s0) for r in RAIDS for s0 in (7000, 17000, 27000, 37000, 47000, 57000, 67000)]
    if PART == 'pools':
        return [('pools', r, 0.5, s0) for r in RAIDS for s0 in (17000, 27000, 37000, 47000, 57000, 67000)]
    if PART == 'solo':
        return [('solo', 'q%02d' % q, f, b) for q in range(1, 8) for f in ('burst', 'scorch', 'sustain') for b in BUILDS]


if __name__ == '__main__':
    t0 = time.time()
    js = jobs()
    with mp.Pool(PROCS) as pool:
        res = pool.map(job, js, chunksize=1)
    json.dump({'code': CODE, 'part': PART, 'trials': TRIALS, 'pool': POOL, 'elapsed': time.time() - t0,
               'results': [[list(j), r] for j, r in res]}, open(OUT, 'w'))
    print(PART, 'done', round(time.time() - t0), 's')
