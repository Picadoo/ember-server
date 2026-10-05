"""D196 Affix Pack 5 gate (旋光 arcane / 火链 firechain).

Part A — per-affix room cost: every repeat normal run gets the affixed elite (affix_rate 1.0, pool forced to one id, no
room event) on Q01–Q07 at the book reference loadout (p1sim.REF_GEAR) and at T3 +6, dodge 0.3 / 0.5 / 0.7, SEEDS × N
runs. Baseline = mortar 投弹 (D181): p1sim never modelled its circles (Stage C deferred), so in the sim it is a plain
promoted elite — the no-pressure baseline. The D189 venom 毒十字 column is the modelled live reference. A new affix passes
when its clear rate stays within MAX_ABS_DPP_CAP pp of that baseline in every cell (it pays the same affix_shard, so it
must not be a much harder room; it cannot be easier than a plain elite).

Part B (--econ): p2econ with the shipped 12-affix pool vs the pre-D196 10-affix pool (pass p2econ args after --econ);
compare the two tables with --diff <new.md> <old.md> (max |Δ| over the percentage columns, cap 3.0 pp).

Usage (repo root): python3 tools/p1sim/affixpack5.py [--n 600]
                   python3 tools/p1sim/affixpack5.py --econ [--old] --players 120 --weeks 12 --dodge 0.5
                   python3 tools/p1sim/affixpack5.py --diff out-new.md out-old.md
"""
import os, sys, copy, random, re
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim

NEW = ('arcane', 'firechain')
REF = ('mortar', 'venom')
MAX_ABS_DPP_CAP = 3.0

if '--diff' in sys.argv:
    i = sys.argv.index('--diff')
    a, b = sys.argv[i + 1], sys.argv[i + 2]

    def rows(path):
        out = {}
        for ln in open(path, encoding='utf-8'):
            c = [x.strip() for x in ln.strip().strip('|').split('|')]
            if len(c) > 3 and c[0].isdigit():
                out[(c[0], c[1])] = c[2:]
        return out
    ra, rb = rows(a), rows(b)
    worst, where = 0.0, None
    for k in ra:
        for j, (x, y) in enumerate(zip(ra[k], rb.get(k, []))):
            if x.endswith('%') and y.endswith('%'):
                d = abs(float(x[:-1]) - float(y[:-1]))
                if d > worst:
                    worst, where = d, (k, j)
    print('MAX_ABS_DPP (p2econ %% columns, new vs old pool) = %.1f at %s (cap %.1f) → %s'
          % (worst, where, MAX_ABS_DPP_CAP, 'WITHIN RANGE' if worst <= MAX_ABS_DPP_CAP else 'OUT OF RANGE'))
    sys.exit(0)

if '--econ' in sys.argv:
    sys.argv.remove('--econ')
    import p2econ
    if '--old' in sys.argv:
        sys.argv.remove('--old')
        _orig = p1config.load

        def _load(*a, **k):
            c = _orig(*a, **k)
            v = c.get('variety') or {}
            v['affixes'] = [x for x in v.get('affixes', []) if x not in NEW]
            return c
        p1config.load = _load
    p2econ.main()
    sys.exit(0)

N = 600
if '--n' in sys.argv:
    N = int(sys.argv[sys.argv.index('--n') + 1])
SEEDS = [7, 11, 13]
MAPS = ['q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07']


def rate(cfg, k, st, d, seed, stress=False):
    p1sim.AFFIX_STRESS = stress
    kn = p1sim.Knobs(d)
    rng = random.Random(seed)
    ok = 0
    for _ in range(N):
        c = p1sim.run_map(cfg, k, st, kn, rng, kn.potion_keep, repeat=True)
        ok += 1 if c[0] else 0
    p1sim.AFFIX_STRESS = False
    return ok / N


base = p1config.load('current')
v0 = base['variety']
assert all(x in v0['affixes'] for x in NEW), 'yml affixes lack the D196 ids'
cfgs = {}
for kind in REF + NEW:
    c = copy.deepcopy(base)
    c['variety']['affix_rate'] = 1.0
    c['variety']['affixes'] = [kind]
    c['variety']['event_rate'] = 0.0
    cfgs[kind] = c
_bv = next((ln.split(':')[1].strip() for ln in open(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'CoreRpg', 'src', 'main', 'resources', 'ember-v1-runs.yml'), encoding='utf-8') if ln.startswith('balance_version:')), '?')
print('# D196 affixpack5 gate — rules balance_version %s, N=%d × seeds %s, FIRECHAIN_EXPOSURE %.2f'
      % (_bv, N, SEEDS, p1sim.FIRECHAIN_EXPOSURE))
print('# arcane %s / firechain %s' % (v0.get('arcane'), v0.get('firechain')))
print('# pressure (atk/s if every cast lands): mortar %.3f · venom %.3f · arcane %.3f · firechain %.3f (≤ 1/tick × exposure)' % (
    v0['mortar']['dmg'] / (v0['mortar']['every'] + v0['mortar']['warn']),
    v0['venom']['dmg'] / (v0['venom']['every'] + v0['venom']['warn']),
    v0['arcane']['dmg'] / (v0['arcane']['every'] + v0['arcane']['warn'] + v0['arcane']['spin']),
    v0['firechain']['dmg'] / v0['firechain']['tick'] * p1sim.FIRECHAIN_EXPOSURE))
print()
print('Stress columns (压力): the first venom / arcane / firechain hit comes at room start + 1.5 s instead of after one')
print('cycle (the sim clears rooms faster than people, so in the plain columns the 11 s 旋光 cycle rarely fires before the')
print('elite dies). Stress pass: new affix − venom under the same stress ≤ cap (not harder than the live 毒十字).')
print()
print('| 图 | 装备 | 躲避 | 基线 mortar（sim 无圈 = 普通精英） | 毒十字 venom | 旋光 arcane | 火链 firechain | 旋光 / 火链 − 基线 (pp) | 压力 毒十字 | 压力 旋光 | 压力 火链 | 压力 旋光 / 火链 − 毒十字 (pp) |')
print('|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
worst = 0.0
worst_s = 0.0
for k in MAPS:
    bt, be, ct, ce, lv = p1sim.REF_GEAR[k]
    ref = p1sim.stats(base, dict(p1sim.item('burst' if bt else 'none', 'blade', bt), enh=be), dict(p1sim.item('burst' if ct else 'none', 'charm', ct), enh=ce), lv)
    t3 = p1sim.stats(base, dict(p1sim.item('burst', 'blade', 3), enh=6), dict(p1sim.item('burst', 'charm', 3), enh=6), 30)
    for gname, st in (('书参考', ref), ('T3+6', t3)):
        for d in (0.3, 0.5, 0.7):
            r = {kind: sum(rate(cfgs[kind], k, st, d, s) for s in SEEDS) / len(SEEDS) for kind in REF + NEW}
            rs = {kind: sum(rate(cfgs[kind], k, st, d, s, True) for s in SEEDS) / len(SEEDS) for kind in ('venom',) + NEW}
            dd = [100 * (r[x] - r['mortar']) for x in NEW]
            ds = [100 * (rs['venom'] - rs[x]) for x in NEW]  # positive = the new affix is harder than venom
            worst = max(worst, max(abs(x) for x in dd))
            worst_s = max(worst_s, max(ds))
            print('| %s | %s | %.1f | %.1f%% | %.1f%% | %.1f%% | %.1f%% | %+.1f / %+.1f | %.1f%% | %.1f%% | %.1f%% | %+.1f / %+.1f |' % (
                k, gname, d, 100 * r['mortar'], 100 * r['venom'], 100 * r['arcane'], 100 * r['firechain'], dd[0], dd[1],
                100 * rs['venom'], 100 * rs['arcane'], 100 * rs['firechain'], -ds[0], -ds[1]), flush=True)
print()
print('MAX_ABS_DPP (new affix clear rate vs baseline, any cell) = %.1f (cap %.1f) → %s'
      % (worst, MAX_ABS_DPP_CAP, 'WITHIN RANGE' if worst <= MAX_ABS_DPP_CAP else 'OUT OF RANGE'))
print('STRESS_MAX_HARDER_THAN_VENOM_PP = %.1f (cap %.1f) → %s'
      % (worst_s, MAX_ABS_DPP_CAP, 'WITHIN RANGE' if worst_s <= MAX_ABS_DPP_CAP else 'OUT OF RANGE'))
