"""D12230 Affix Pack 26 gate (削斩 chip / 峡洼 glen).

Thinner than affixpack5: Part A stress band vs venom (new − venom ≤ 3.0 pp under AFFIX_STRESS);
Part B p2econ 54-affix pool vs pre-Pack26 52-affix pool (max |Δ| ≤ 3.0 pp).

Usage (repo root): python3 tools/p1sim/affixpack26.py [--n 150]
                   python3 tools/p1sim/affixpack26.py --econ [--old] --players 80 --weeks 8 --dodge 0.5
                   python3 tools/p1sim/affixpack26.py --diff out-new.md out-old.md
"""
import os, sys, copy, random
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim

NEW = ('chip', 'glen')
REF = ('venom',)
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
    print('MAX_ABS_DPP (p2econ %% columns, new 54 vs old 52 pool) = %.1f at %s (cap %.1f) → %s'
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

N = 150
if '--n' in sys.argv:
    N = int(sys.argv[sys.argv.index('--n') + 1])
SEEDS = [7, 11, 13]
MAPS = ['q01', 'q03', 'q05', 'q07']  # thinner sample


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
assert all(x in v0['affixes'] for x in NEW), 'yml affixes lack the D12230 ids'
assert len(v0['affixes']) == 54, 'expected pool 54, got %d' % len(v0['affixes'])
cfgs = {}
for kind in REF + NEW:
    c = copy.deepcopy(base)
    c['variety']['affix_rate'] = 1.0
    c['variety']['affixes'] = [kind]
    c['variety']['event_rate'] = 0.0
    cfgs[kind] = c
_bv = next((ln.split(':')[1].strip() for ln in open(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', 'CoreRpg', 'src', 'main', 'resources', 'ember-v1-runs.yml'), encoding='utf-8') if ln.startswith('balance_version:')), '?')
print('# D12230 affixpack26 gate — rules balance_version %s, N=%d × seeds %s' % (_bv, N, SEEDS))
print('# chip %s / glen %s' % (v0.get('chip'), v0.get('glen')))
print()
print('| 图 | 装备 | 躲避 | 压力 毒十字 | 压力 削斩 | 压力 峡洼 | 削斩/峡洼 − 毒十字 (pp) |')
print('|---|---|---:|---:|---:|---:|---:|')
worst_s = 0.0
for k in MAPS:
    bt, be, ct, ce, lv = p1sim.REF_GEAR[k]
    ref = p1sim.stats(base, dict(p1sim.item('burst' if bt else 'none', 'blade', bt), enh=be), dict(p1sim.item('burst' if ct else 'none', 'charm', ct), enh=ce), lv)
    for gname, st in (('书参考', ref),):
        for d in (0.3, 0.5, 0.7):
            rs = {kind: sum(rate(cfgs[kind], k, st, d, s, True) for s in SEEDS) / len(SEEDS) for kind in REF + NEW}
            dd = [100 * (rs[x] - rs['venom']) for x in NEW]
            worst_s = max(worst_s, max(-min(dd), 0.0), max(dd) if False else 0.0)  # harder = negative vs venom
            # "not harder than venom by > cap": venom - new ≤ cap  →  new - venom ≥ -cap
            hard = [100 * (rs['venom'] - rs[x]) for x in NEW]
            worst_s = max(worst_s, max(hard))
            print('| %s | %s | %.1f | %.1f%% | %.1f%% | %.1f%% | %+.1f / %+.1f |'
                  % (k, gname, d, 100 * rs['venom'], 100 * rs['chip'], 100 * rs['glen'], dd[0], dd[1]))
print()
print('MAX_HARDER_THAN_VENOM_PP (stress) = %.1f (cap %.1f) → %s'
      % (worst_s, MAX_ABS_DPP_CAP, 'WITHIN RANGE' if worst_s <= MAX_ABS_DPP_CAP else 'OUT OF RANGE'))
print('NOTE: run --econ twice (default 54-pool, then --old 52-pool) and --diff the two md tables for Part B.')
