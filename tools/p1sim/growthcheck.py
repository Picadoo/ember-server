"""D141–D143 clear-rate check: Δ clear rate per map at the book reference loadout (normal) and T3+6 (challenge),
dodge 0.3 / 0.5 / 0.7, growth off vs on (seeds averaged). Usage (repo root):
  python3 tools/p1sim/growthcheck.py q04 '{"talents":["t1a"]}' ['{"talents":["t1b"],"set":"scorch"}' ...] [--n 4000 --seeds 5]
Each JSON = one build: talents (node ids), honors ("all" or ids), affixes ({"blade": id, "charm": id}), affix_tier,
set (family used for the reference pieces; default burst = the book's reference), label.
Prints one line per build: max |Δ| and every cell old→new.
"""
import sys, json, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ, growth

args = sys.argv[1:]
N, S = 4000, 5
if '--n' in args: i = args.index('--n'); N = int(args[i + 1]); del args[i:i + 2]
if '--seeds' in args: i = args.index('--seeds'); S = int(args[i + 1]); del args[i:i + 2]
seeds = [7, 11, 13, 17, 19, 23, 29][:S]
k = args[0]
builds = [json.loads(a) for a in args[1:]]
cfg = p1config.load('current'); ccfg = p2econ.challenge_cfg(cfg)
g = growth.load()
print('# ' + __import__('rules').stamp(), flush=True)


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
        growth.disable()
        base[fam] = rates(fam)
    growth.enable(b.get('talents', []), b.get('honors', ()), b.get('affixes'), b.get('affix_tier', 4), g)
    r = rates(fam)
    growth.disable()
    o = base[fam]
    dev = max(abs(r[x] - o[x]) for x in r)
    up = max(r[x] - o[x] for x in r)
    print('%+.1f/%.1f %s %s %s' % (100 * up, 100 * dev, k, b.get('label', json.dumps(b, ensure_ascii=False)),
          ' '.join('%s:%.0f→%.0f(%+.1f)' % (x, 100 * o[x], 100 * r[x], 100 * (r[x] - o[x])) for x in sorted(r))), flush=True)
