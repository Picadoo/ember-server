"""D192 落空破绽 gate: clear rate per map (Q01–Q05) at the book reference loadout (normal) and T3+6 (challenge), dodge
0.3 / 0.5 / 0.7, 5 seeds × 4000 runs; baseline = p1sim.WHIFF_STUN False + REVERT (live 1.65.29 numbers, mechanic stripped) vs as shipped.
Default WHIFF_ARM 1.0 = upper-ish bound (solo, always in melee: every dodged whiff_stun telegraph staggers the boss; Java
arms only if someone was inside the shape at warn start). Usage (repo root):
  python3 tools/p1sim/whiffstun.py q01 q02 q03 q04 q05 [--arm 0.6]
"""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ
args = sys.argv[1:]
if '--arm' in args:
    i = args.index('--arm'); p1sim.WHIFF_ARM = float(args[i + 1]); del args[i:i + 2]
STUN = None; DMG = 1.0  # sweep only: override every whiff_stun / scale the whiff move's dmg in the NEW config
if '--stun' in args:
    i = args.index('--stun'); STUN = float(args[i + 1]); del args[i:i + 2]
EVERY = 0.0
if '--every' in args:
    i = args.index('--every'); EVERY = float(args[i + 1]); del args[i:i + 2]
if '--dmg' in args:
    i = args.index('--dmg'); DMG = float(args[i + 1]); del args[i:i + 2]
N = 4000; SEEDS = [7, 11, 13, 17, 19]
def rates(cfg, k, seed):
    ccfg = p2econ.challenge_cfg(cfg)
    bt, be, ct, ce, lv = p1sim.REF_GEAR[k]
    st = p1sim.stats(cfg, dict(p1sim.item('burst' if bt else 'none', 'blade', bt), enh=be), dict(p1sim.item('burst' if ct else 'none', 'charm', ct), enh=ce), lv)
    st3 = p1sim.stats(cfg, dict(p1sim.item('burst', 'blade', 3), enh=6), dict(p1sim.item('burst', 'charm', 3), enh=6), 30)
    r = {}
    for d in (0.3, 0.5, 0.7):
        r['n %.1f' % d] = p1sim.clear_rate(cfg, k, st, p1sim.Knobs(d), N, seed=seed)
        r['c %.1f' % d] = p1sim.clear_rate(ccfg, k, st3, p1sim.Knobs(d), N, seed=seed)
    return r
def avg(rs): return {x: sum(r[x] for r in rs) / len(rs) for x in rs[0]}
import copy
# live (1.65.29) values the D192 yml changed besides whiff_stun — restored in the baseline copy
REVERT = {'q04': {'冲击圈': {'dmg': 22}}}
cfg = p1config.load('current')
mx = 0.0
for k in args:
    assert any(sk.get('whiff_stun') for sk in cfg['maps'][k]['boss']['skills']), k + ' has no whiff_stun'
    new = copy.deepcopy(cfg)
    for sk in new['maps'][k]['boss']['skills']:
        if sk.get('whiff_stun'):
            if STUN is not None: sk['whiff_stun'] = STUN
            sk['dmg'] = sk['dmg'] * DMG
            sk['every'] = sk['every'] + EVERY
    old = copy.deepcopy(cfg)
    for sk in old['maps'][k]['boss']['skills']:
        sk.update(REVERT.get(k, {}).get(sk.get('name'), {}))
    p1sim.WHIFF_STUN = False; base = avg([rates(old, k, s) for s in SEEDS])
    p1sim.WHIFF_STUN = True; r = avg([rates(new, k, s) for s in SEEDS])
    dev = max(abs(r[x] - base[x]) for x in r); mx = max(mx, dev)
    print('- %.1f %s ' % (100 * dev, k) + ' '.join('%s:%.0f→%.0f(%+.1f)' % (x, 100 * base[x], 100 * r[x], 100 * (r[x] - base[x])) for x in sorted(r)), flush=True)
print('\nMAX_ABS_DPP = %.1f (cap 3.0, prefer ≤2.0; WHIFF_ARM %.2f; sweep stun %s dmg ×%.2f every %+.1f)' % (100 * mx, p1sim.WHIFF_ARM, STUN, DMG, EVERY))
