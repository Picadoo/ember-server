"""D188 撞墙破绽 gate: clear rate per map (Q02 / Q07) at the book reference loadout (normal) and T3+6 (challenge), dodge
0.3 / 0.5 / 0.7, 5 seeds × 4000 runs, the current runs yml with every wall_stun stripped (baseline) vs as shipped. Default: crash share = dodge × p1sim.WALL_STUN_K (0.5);
--upper: every charge ends in a wall (p1sim.WALL_STUN_P = 1.0, informational). p1config.load reads one canonical rules snapshot, so the baseline is a deep copy with the
key removed (an --old yml path is not honoured). Usage (repo root):
  python3 tools/p1sim/wallstun.py q02 q07 [--upper]
"""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import copy
if '--upper' in sys.argv:
    sys.argv.remove('--upper')
    import p1sim as _p; _p.WALL_STUN_P = 1.0
import p1config, p1sim, p2econ
# live (1.65.26) values the D188 yml changed besides wall_stun — restored in the baseline copy
REVERT = {'q07': {'冲撞': {'dmg': 36}}}
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
mx = 0.0
for k in sys.argv[1:]:
    new = p1config.load('current')
    old = copy.deepcopy(new)
    for sk in old['maps'][k]['boss']['skills']:
        sk.pop('wall_stun', None)
        sk.update(REVERT.get(k, {}).get(sk.get('name'), {}))
    assert any(sk.get('wall_stun') for sk in new['maps'][k]['boss']['skills']), k + ' has no wall_stun'
    base = avg([rates(old, k, s) for s in SEEDS]); r = avg([rates(new, k, s) for s in SEEDS])
    dev = max(abs(r[x] - base[x]) for x in r); mx = max(mx, dev)
    print('- %.1f %s ' % (100 * dev, k) + ' '.join('%s:%.0f→%.0f(%+.1f)' % (x, 100 * base[x], 100 * r[x], 100 * (r[x] - base[x])) for x in sorted(r)), flush=True)
print('\nMAX_ABS_DPP = %.1f (cap 3.0, prefer ≤2.0)' % (100 * mx))
