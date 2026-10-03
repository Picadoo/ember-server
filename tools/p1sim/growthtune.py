"""D141 tuning helper: like growthcheck.py but each build carries raw mods (no yml): '{"mods": {...}, "set": "burst", "label": ".."}'
"mods" may be a list of dicts (one per node): they are combined with growth.combine (a plain merged dict would let a
later duplicate key override the earlier one instead of multiplying)."""
import sys, json, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ
args = sys.argv[1:]
N, S = 3000, 3
if '--n' in args: i = args.index('--n'); N = int(args[i + 1]); del args[i:i + 2]
if '--seeds' in args: i = args.index('--seeds'); S = int(args[i + 1]); del args[i:i + 2]
seeds = [7, 11, 13, 17, 19, 23, 29][:S]
k = args[0]
builds = [json.loads(a) for a in args[1:]]
cfg = p1config.load('current'); ccfg = p2econ.challenge_cfg(cfg)
def rates(fam):
    bt, be, ct, ce, lv = p1sim.REF_GEAR[k]
    st = p1sim.stats(cfg, dict(p1sim.item(fam if bt else 'none', 'blade', bt), enh=be), dict(p1sim.item(fam if ct else 'none', 'charm', ct), enh=ce), lv)
    st3 = p1sim.stats(cfg, dict(p1sim.item(fam, 'blade', 3), enh=6), dict(p1sim.item(fam, 'charm', 3), enh=6), 30)
    r = {}
    for d in (0.3, 0.5, 0.7):
        r['n%.1f' % d] = sum(p1sim.clear_rate(cfg, k, st, p1sim.Knobs(d), N, seed=s) for s in seeds) / len(seeds)
        r['c%.1f' % d] = sum(p1sim.clear_rate(ccfg, k, st3, p1sim.Knobs(d), N, seed=s) for s in seeds) / len(seeds)
    return r
base = {}
for b in builds:
    fam = b.get('set', 'burst')
    if fam not in base:
        p1sim.GROWTH = None
        base[fam] = rates(fam)
    mods = b['mods']; gate = b.get('gate')
    if isinstance(mods, list):  # several nodes: combine like the game (same keys multiply, not override)
        import growth
        mods = growth.combine(mods)
    p1sim.GROWTH = lambda info, ctx: (mods if (not gate or info.get('set') == gate) else None)
    r = rates(fam)
    p1sim.GROWTH = None
    o = base[fam]
    print('%+.1f/%.1f %s %s %s' % (100 * max(r[x] - o[x] for x in r), 100 * max(abs(r[x] - o[x]) for x in r), k, b.get('label'),
          ' '.join('%s:%+.1f' % (x, 100 * (r[x] - o[x])) for x in sorted(r))), flush=True)
