"""D191 Room Events Pack 4 gate (裂隙 breach / 连斩 chain / 无伤 unscathed).

Part A — per-kind success: every repeat normal run gets its event (event_rate 1.0, forced kind) on Q01–Q07 at the book
reference loadout (p1sim.REF_GEAR) and at T3 +6 (the usual farming gear), dodge 0.3 / 0.5 / 0.7, SEEDS × N runs.
The D138–D179 kinds share one model (room cleared within event_secs, as p1sim always did) — that is the reference band.
New kinds are modelled on what they ask (p1sim.event_ok). Pass: no new kind beats the reference by more than +10 pp on
average (they pay the same event_core, so an easier kind would only raise income; harder is fine — optional event).
Events never change combat in the sim (clear rate identical by construction: kind is drawn from its own stream).

Part B (--econ): p2econ with the shipped 9-kind pool vs the pre-D191 6-kind pool (pass p2econ args after --econ).

Usage (repo root): python3 tools/p1sim/eventpack4.py [--n 1500]
                   python3 tools/p1sim/eventpack4.py --econ [--old] --players 120 --weeks 12 --dodge 0.5
"""
import os, sys, copy, random
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim

NEW = ('breach', 'chain', 'unscathed')
OLD = ('timed', 'crystal', 'escort', 'hold', 'beacon', 'relay')

if '--econ' in sys.argv:
    sys.argv.remove('--econ')
    import p2econ
    if '--old' in sys.argv:
        sys.argv.remove('--old')
        _orig = p1config.load

        def _load(*a, **k):
            c = _orig(*a, **k)
            v = c.get('variety') or {}
            v['events'] = [x for x in v.get('events', []) if x not in NEW]
            return c
        p1config.load = _load
    p2econ.main()
    sys.exit(0)

N = 1500
if '--n' in sys.argv:
    N = int(sys.argv[sys.argv.index('--n') + 1])
SEEDS = [7, 11, 13]
MAPS = ['q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07']


def rate(cfg, k, st, d, kind, seed):
    p1sim.EVENT_FORCE = kind
    kn = p1sim.Knobs(d)
    rng = random.Random(seed)
    ok = 0
    for _ in range(N):
        p1sim.run_map(cfg, k, st, kn, rng, kn.potion_keep, repeat=True)
        ok += 1 if p1sim.LAST_VAR['event'] else 0
    return ok / N


cfg = p1config.load('current')
v = cfg['variety']
assert all(x in v['events'] for x in NEW), 'yml events lack the D191 kinds'
cfg = copy.deepcopy(cfg)
cfg['variety']['event_rate'] = 1.0
print('# D191 eventpack4 gate — rules balance_version %s, N=%d × seeds %s' % (cfg.get('balance_version', '?'), N, SEEDS))
print('# breach %s / chain %s / unscathed %s' % (v.get('breach'), v.get('chain'), v.get('unscathed')))
print()
print('| 图 | 装备 | 躲避 | 参考(旧 6 种同模) | 裂隙 breach | 连斩 chain | 无伤 unscathed |')
print('|---|---|---:|---:|---:|---:|---:|')
acc = {x: [] for x in ('ref',) + NEW}
for k in MAPS:
    bt, be, ct, ce, lv = p1sim.REF_GEAR[k]
    ref = p1sim.stats(cfg, dict(p1sim.item('burst' if bt else 'none', 'blade', bt), enh=be), dict(p1sim.item('burst' if ct else 'none', 'charm', ct), enh=ce), lv)
    t3 = p1sim.stats(cfg, dict(p1sim.item('burst', 'blade', 3), enh=6), dict(p1sim.item('burst', 'charm', 3), enh=6), 30)
    for gname, st in (('书参考', ref), ('T3+6', t3)):
        for d in (0.3, 0.5, 0.7):
            r = {kind: sum(rate(cfg, k, st, d, kind, s) for s in SEEDS) / len(SEEDS) for kind in ('timed',) + NEW}
            acc['ref'].append(r['timed'])
            for x in NEW:
                acc[x].append(r[x])
            print('| %s | %s | %.1f | %.0f%% | %.0f%% | %.0f%% | %.0f%% |' % (k, gname, d, 100 * r['timed'], 100 * r['breach'], 100 * r['chain'], 100 * r['unscathed']), flush=True)
p1sim.EVENT_FORCE = None
mean = {x: sum(acc[x]) / len(acc[x]) for x in acc}
print()
print('均值：参考 %.0f%% · 裂隙 %.0f%% · 连斩 %.0f%% · 无伤 %.0f%%' % tuple(100 * mean[x] for x in ('ref',) + NEW))
worst = max(mean[x] - mean['ref'] for x in NEW)
# expected event_core per event-rolled run: old pool all at ref, new pool 6/9 ref + 3/9 new kinds
old_exp = mean['ref']
new_exp = (6 * mean['ref'] + sum(mean[x] for x in NEW)) / 9
print('每次出事件的期望核心：旧池 %.3f → 新池 %.3f（×event_core %s，event_rate %s）' % (old_exp, new_exp, v.get('event_core'), v.get('event_rate')))
print('MAX_NEW_MINUS_REF_PP = %+.1f (cap +10.0)  →  %s' % (100 * worst, 'WITHIN RANGE' if worst <= 0.10 and new_exp <= old_exp + 0.02 else 'OUT OF RANGE'))
