"""D141–D143 raid check: the same post-Q07 players (p2econ to Q07 + `weeks` challenge weeks) with growth off vs on,
same party draws (same rng seed). Each player takes row-1 / row-2 talents from the build and the row-3 node of the
set they wear (t3a scorch / t3b burst / t3c sustain); honors and affixes as given.
Usage (repo root): python3 tools/p1sim/growthraid.py r03 '{"talents":["t1b","t2a"],"honors":"all","affixes":{"blade":"b_set","charm":"c_tele"},"affix_tier":1}' [--pool 36 --trials 300 --dodge 0.5]
"""
import sys, json, os, random
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ, p1party, growth, miniyaml

args = sys.argv[1:]
def opt(name, d, cast):
    if name in args:
        i = args.index(name); v = cast(args[i + 1]); del args[i:i + 2]; return v
    return d
POOL = opt('--pool', 36, int); TRIALS = opt('--trials', 300, int); WEEKS = opt('--weeks', 2, int)
DODGES = [float(x) for x in opt('--dodge', '0.3,0.5,0.7', str).split(',')]
key = args[0]
builds = [json.loads(a) for a in args[1:]]
cfg = p1config.load()
g = growth.load()
import rules
rr = (rules.runs().get('raid_revive') or {})
print('#', rules.stamp(), flush=True)
p1party.LAST_REVIVE_HP = float(rr.get('last_phase_hp', 0)); p1party.LAST_REVIVE_DELAY = float(rr.get('delay', 10))
ROW3 = {'scorch': 't3a', 'burst': 't3b', 'sustain': 't3c'}


def players(dodge, seed0=7000):
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
        if WEEKS:
            p2econ.phase2(cfg, ccfg, kn, p, random.Random(seed0 + 99 + i), WEEKS, False, per_day, runs)
        out.append((p, kn))
    return out


def fn_for(b):
    base = b.get('talents', [])
    def fn(info, ctx):
        t = list(base)
        if len(t) >= 2 and ROW3.get(info.get('set')):
            t.append(ROW3[info.get('set')])
        return growth.build(g, t, b.get('honors', ()), b.get('affixes'), b.get('affix_tier', 4))(info, ctx)
    return fn


m = p1party.raid_map(cfg, key)
for d in DODGES:
    ps = players(d)
    growth.disable()
    pool0 = [(p.st(), kn) for p, kn in ps]
    r0 = p1party.evaluate(cfg, m, pool0, (3, 4, 5), TRIALS, random.Random(11))
    for b in builds:
        p1sim.GROWTH = fn_for(b)
        pool1 = [(p.st(), kn) for p, kn in ps]
        growth.disable()
        r1 = p1party.evaluate(cfg, m, pool1, (3, 4, 5), TRIALS, random.Random(11))
        cells = ' '.join('%d人:%.0f→%.0f(%+.1f)' % (n, 100 * r0[n]['rate'], 100 * r1[n]['rate'], 100 * (r1[n]['rate'] - r0[n]['rate'])) for n in (3, 4, 5))
        up = max(r1[n]['rate'] - r0[n]['rate'] for n in (3, 4, 5))
        print('%+.1f %s d%.1f %s %s' % (100 * up, key, d, b.get('label', ''), cells), flush=True)
