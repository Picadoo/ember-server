"""D247 gate — model the five previously-unmodelled affixes (regen / charge / frost / mortar / molten).

Every repeat normal run forces one affix (affix_rate 1.0, no room event) on Q01–Q07 at book REF_GEAR and T3+6,
dodge 0.3 / 0.5 / 0.7, SEEDS × N. Baseline = `_plain` (promoted elite, no combat pressure).

Pass: each new affix's clear rate stays within MAX_ABS_DPP_CAP pp of `_plain` in every cell (same shard pay → must not
be a much harder room). Soft observe: cyan cells where the new affix is >1 pp *easier* than plain (should be rare).

Usage (repo root): python3 tools/p1sim/affix-d247.py [--n 200]
"""
import os, sys, copy, random
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, affixtable

NEW = ('regen', 'charge', 'frost', 'mortar', 'molten')
BASE = affixtable.PLAIN
MAX_ABS_DPP_CAP = 3.0
N = 200
if '--n' in sys.argv:
    N = int(sys.argv[sys.argv.index('--n') + 1])
SEEDS = [7, 11, 13]
MAPS = ['q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07']


def rate(cfg, k, st, d, seed):
    kn = p1sim.Knobs(d)
    rng = random.Random(seed)
    ok = 0
    for _ in range(N):
        c = p1sim.run_map(cfg, k, st, kn, rng, kn.potion_keep, repeat=True)
        ok += 1 if c[0] else 0
    return ok / N


base = p1config.load('current')
v0 = base['variety']
assert all(x in v0['affixes'] for x in NEW), 'yml affixes lack the D247 ids'
cfgs = {}
for kind in (BASE,) + NEW:
    c = copy.deepcopy(base)
    c['variety']['affix_rate'] = 1.0
    c['variety']['affixes'] = [kind]
    c['variety']['event_rate'] = 0.0
    cfgs[kind] = c

print('# D247 affix model gate — N=%d × seeds %s, baseline=%s, cap %.1f pp' % (N, SEEDS, BASE, MAX_ABS_DPP_CAP))
print('# models: charge/mortar=PERIODIC telegraph; frost=AURA dodge×1/(1+0.15·amp); regen=CHANNEL heal/interrupt; molten=DEATH_BLAST')
print()
print('| 图 | 装备 | 躲避 | _plain | regen | charge | frost | mortar | molten | max |Δ| vs plain (pp) |')
print('|---|---|---:|---:|---:|---:|---:|---:|---:|---:|')
worst, where = 0.0, None
for k in MAPS:
    bt, be, ct, ce, lv = p1sim.REF_GEAR[k]
    ref = p1sim.stats(base, dict(p1sim.item('burst' if bt else 'none', 'blade', bt), enh=be),
                      dict(p1sim.item('burst' if ct else 'none', 'charm', ct), enh=ce), lv)
    t3 = p1sim.stats(base, dict(p1sim.item('burst', 'blade', 3), enh=6), dict(p1sim.item('burst', 'charm', 3), enh=6), 30)
    for gname, st in (('书参考', ref), ('T3+6', t3)):
        for d in (0.3, 0.5, 0.7):
            r = {kind: sum(rate(cfgs[kind], k, st, d, s) for s in SEEDS) / len(SEEDS) for kind in (BASE,) + NEW}
            dd = {x: 100 * (r[x] - r[BASE]) for x in NEW}
            cell = max(abs(v) for v in dd.values())
            if cell > worst:
                worst, where = cell, (k, gname, d, max(dd, key=lambda x: abs(dd[x])))
            print('| %s | %s | %.1f | %.1f%% | %.1f%% | %.1f%% | %.1f%% | %.1f%% | %.1f%% | %.1f (%s) |' % (
                k, gname, d, 100 * r[BASE], 100 * r['regen'], 100 * r['charge'], 100 * r['frost'],
                100 * r['mortar'], 100 * r['molten'], cell, max(dd, key=lambda x: abs(dd[x]))), flush=True)
print()
print('MAX_ABS_DPP (new affix clear rate vs _plain, any cell) = %.1f at %s (cap %.1f) → %s'
      % (worst, where, MAX_ABS_DPP_CAP, 'WITHIN RANGE' if worst <= MAX_ABS_DPP_CAP else 'OUT OF RANGE'))
sys.exit(0 if worst <= MAX_ABS_DPP_CAP else 1)
