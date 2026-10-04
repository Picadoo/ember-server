"""Raid check for a set-constant change (sidegrade.py 炽愈 balance): same 7 player pools as raidcomp.py, each pool's
players are evaluated with the CURRENT constants and with the patched ones (same lineups / run seeds → paired), on
  * natural random teams (p1party.evaluate, 3 / 4 / 5 people)
  * forced compositions: k members re-dressed 炽愈 (the rest: their own set, 炽愈 → 烬爆), k = 0..n
  python3 raidset.py OUT.json '{"sustain_hp": 1.06}' [--trials T] [--procs P] [--raids r01,r03]
  python3 raidset.py --report OUT.json [...]"""
import sys, os, json, random, time, statistics, multiprocessing as mp
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ, p1party, growth, rules
import raidcomp

args = sys.argv[1:]
opt = raidcomp.opt
TRIALS = opt('--trials', 1500, int); PROCS = opt('--procs', 6, int)
RAIDS = opt('--raids', ['r01', 'r02', 'r03'], lambda s: s.split(','))
POOLS = raidcomp.POOLS
COMPS = ((3, (0, 1, 2)), (5, (0, 1, 2)))


def job(j):
    raid, seed0, patch = j
    cfg = p1config.load()
    raidcomp.cfg = cfg
    rr = rules.runs().get('raid_revive') or {}
    p1party.LAST_REVIVE_HP = float(rr.get('last_phase_hp', 0)); p1party.LAST_REVIVE_DELAY = float(rr.get('delay', 10))
    ps = raidcomp.players(0.5, seed0)
    m = p1party.raid_map(cfg, raid)
    out = {}
    for lab in ('base', 'new'):
        if lab == 'new':
            for k, v in patch.items():
                cfg[k] = v          # the players share this cfg dict → p.st() and the fight both see the patch
        pool = [(p.st(), kn) for p, kn in ps]
        ev = p1party.evaluate(cfg, m, pool, (3, 4, 5), TRIALS, random.Random(11))
        out[lab] = {'natural': {str(n): x['rate'] for n, x in ev.items()}}
        for n, ks in COMPS:
            for k in ks:
                rng = random.Random(4242)
                wins = 0
                for i in range(TRIALS):
                    grp = rng.sample(range(len(pool)), n)
                    sts, kns = [], []
                    for jj, idx in enumerate(grp):
                        p, kn = ps[idx]
                        st = dict(pool[idx][0])
                        if jj < k:   # re-dress as 炽愈 with this cfg (HP bonus follows the set)
                            st = p1sim.stats(cfg, dict(p.blade, fam='sustain'), dict(p.charm, fam='sustain'), p1sim.level_of(cfg, p.xp), p.cleared)
                        elif st['set'] == 'sustain':
                            st = p1sim.stats(cfg, dict(p.blade, fam='burst'), dict(p.charm, fam='burst'), p1sim.level_of(cfg, p.xp), p.cleared)
                        sts.append(st); kns.append(kn)
                    wins += int(p1party.run_party(cfg, m, sts, kns, random.Random(900001 + i))[0])
                out[lab]['%d/%d' % (n, k)] = wins / TRIALS
    return [raid, seed0], out


def report(files):
    for f in files:
        d = json.load(open(f))
        print('## 补丁 %s（%d 池 × 每格 %d 局，躲避 0.5）\n' % (json.dumps(d['patch'], ensure_ascii=False), len(POOLS), d['trials']))
        by = {}
        for (raid, s0), r in d['results']:
            by.setdefault(raid, []).append(r)
        print('| 团本 | 队伍 | 现行 | 补丁后 | 差（pp，池聚类 95% 区间） |\n|---|---|---:|---:|---|')
        for raid, rs in sorted(by.items()):
            keys = [('natural', n) for n in ('3', '4', '5')] + [(None, '%d/%d' % (n, k)) for n, ks in COMPS for k in ks]
            for a, b in keys:
                get = (lambda r, lab: r[lab][a][b]) if a else (lambda r, lab: r[lab][b])
                base = [100 * get(r, 'base') for r in rs]; new = [100 * get(r, 'new') for r in rs]
                dm, dh = raidcomp.ci([x - y for x, y in zip(new, base)])
                lab = ('随机组队 %s 人' % b) if a else ('%s 人，其中 %s 名炽愈' % tuple(b.split('/')))
                print('| %s | %s | %.1f | %.1f | %+.1f [%+.1f, %+.1f] |' % (raid.upper(), lab, statistics.mean(base), statistics.mean(new), dm, dm - dh, dm + dh))
        print()


if __name__ == '__main__':
    if args and args[0] == '--report':
        report(args[1:]); sys.exit(0)
    out, patch = args[0], json.loads(args[1])
    print(rules.stamp(), flush=True)
    t0 = time.time()
    js = [(r, s, patch) for r in RAIDS for s in POOLS]
    with mp.Pool(PROCS) as pool:
        res = pool.map(job, js, chunksize=1)
    json.dump({'patch': patch, 'trials': TRIALS, 'elapsed': time.time() - t0, 'results': [[list(j), r] for j, r in res]}, open(out, 'w'))
    print('done', round(time.time() - t0), 's')
