"""D193 破招 gate: clear rate per map (Q06 / Q07) at the book reference loadout (normal) and T3+6 (challenge), dodge
0.3 / 0.5 / 0.7, 5 seeds x 4000 runs; baseline = p1sim.BREAK False (live 1.65.30: the half-HP channel does not exist) vs
as shipped. Solo model (p1sim BREAK_UPTIME swings during the warn). Usage (repo root):
  python3 tools/p1sim/breakmove.py q06 q07 [--hp 0.06] [--stun 1.0] [--dmg 30] [--every 25] [--uptime 0.85]
Also prints the share of channels broken (normal dodge 0.5) so the check is a real check, not a free stagger.
"""
import sys, os, copy
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ
args = sys.argv[1:]
OV = {}
for flag, key in (('--hp', 'break_hp'), ('--stun', 'break_stun'), ('--dmg', 'dmg'), ('--every', 'every'), ('--warn', 'warn')):
    if flag in args:
        i = args.index(flag); OV[key] = float(args[i + 1]); del args[i:i + 2]
if '--light' in args:
    args.remove('--light'); OV['light'] = True
if '--uptime' in args:
    i = args.index('--uptime'); p1sim.BREAK_UPTIME = float(args[i + 1]); del args[i:i + 2]
N = int(os.environ.get('N', 4000)); SEEDS = [7, 11, 13, 17, 19]
def loadouts(cfg, k):
    bt, be, ct, ce, lv = p1sim.REF_GEAR[k]
    st = p1sim.stats(cfg, dict(p1sim.item('burst' if bt else 'none', 'blade', bt), enh=be), dict(p1sim.item('burst' if ct else 'none', 'charm', ct), enh=ce), lv)
    st3 = p1sim.stats(cfg, dict(p1sim.item('burst', 'blade', 3), enh=6), dict(p1sim.item('burst', 'charm', 3), enh=6), 30)
    return st, st3
def rates(cfg, k, seed):
    ccfg = p2econ.challenge_cfg(cfg)
    st, st3 = loadouts(cfg, k)
    r = {}
    for d in (0.3, 0.5, 0.7):
        r['n %.1f' % d] = p1sim.clear_rate(cfg, k, st, p1sim.Knobs(d), N, seed=seed)
        r['c %.1f' % d] = p1sim.clear_rate(ccfg, k, st3, p1sim.Knobs(d), N, seed=seed)
    return r
def avg(rs): return {x: sum(r[x] for r in rs) / len(rs) for x in rs[0]}
cfg = p1config.load('current')
mx = 0.0
for k in args:
    new = copy.deepcopy(cfg)
    hit = [sk for sk in new['maps'][k]['boss']['skills'] if sk.get('break_hp')]
    assert hit, k + ' has no break_hp skill'
    for sk in hit: sk.update(OV)
    p1sim.BREAK = False; base = avg([rates(new, k, s) for s in SEEDS])
    p1sim.BREAK = True; p1sim.BREAK_STATS[:] = [0, 0]; r = avg([rates(new, k, s) for s in SEEDS])
    b, f = p1sim.BREAK_STATS; share = b / max(1, b + f)
    dev = max(abs(r[x] - base[x]) for x in r); mx = max(mx, dev)
    print('- %.1f %s ' % (100 * dev, k) + ' '.join('%s:%.0f→%.0f(%+.1f)' % (x, 100 * base[x], 100 * r[x], 100 * (r[x] - base[x])) for x in sorted(r)) + ' · broken %.0f%% of %d channels (all six cells)' % (100 * share, b + f), flush=True)
print('\nMAX_ABS_DPP = %.1f (cap 3.0, prefer ≤2.0; BREAK_UPTIME %.2f; overrides %s)' % (100 * mx, p1sim.BREAK_UPTIME, OV or 'none'))
