#!/usr/bin/env python3
"""D174 main-story signature legendaries (签名传奇) · offline budget checks.
docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md §5 / §8. Standard library only; offline.

Every signature is a set of the EXISTING growth modifier keys (Java EmberGrowth.Mods / growth.py) — the Stage-1 ones
(L01–L06) use only keys the live Java already implements; L07+ use p1sim proposal keys (skill_*, sustain_low*) that need a
Java hook in their stage. A signature works only while its piece is worn; set-tied ones (family != 'any') only while that
set is active (their keys are set events anyway). Max 2 active (1 blade + 1 charm); same tag = only the blade's counts.

  python3 mainline.py list
  python3 mainline.py tol    [N] [OUT.pkl]          # each signature alone: 42 cells (7 maps × first-clear r / challenge c × dodge 0.3/0.5/0.7)
  python3 mainline.py pairs  [N] [OUT.pkl]          # every legal blade+charm pair per family (different tags)
  python3 mainline.py talent [N] [OUT.pkl]          # strongest pair per family ON TOP of a full talent build vs that build
  python3 mainline.py try    '{"name": ["fam|any", {mods}]}' [N] [OUT.pkl]   # candidate sweep
  python3 mainline.py report OUT.pkl [OUT2.pkl …]   # markdown tables
  python3 mainline.py econ '<sig ids json list>' [p2econ args…]   # two-极品 W30 upper bound: signatures worn from day 1
Pass mark (D174 §8): per signature and per pair, every cell within ±2 pp of the paired baseline is the target; the hard line
is "within range" = mean |Δ| ≤ 1 pp and no cell beyond ±3 pp (noise floor: max +1.7 pp between disjoint seed sets, B-report).
"""
import json
import os
import pickle
import statistics
import sys
from multiprocessing import Pool

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1sim  # noqa: E402

NPROC = int(os.environ.get('NPROC', '6'))
SEEDS = tuple(int(x) for x in os.environ.get('ML_SEEDS', '7,11').split(','))
STAGE = int(os.environ.get('ML_STAGE', '9'))  # only signatures of stage <= ML_STAGE
# same tag as a talent node = does not stack: while that talent is picked the signature is off (Java EmberSignature.active)
EXCL = {'L01': 't3a', 'L02': 't1c', 'L03': 't3b', 'L10': 't3b'}
FAMS = ('burst', 'scorch', 'sustain')
MAPS = ['q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07']

# id: (map, slot, family or 'any', tag, stage, name, mods)
SIGS = {
    # ---- Stage 1 · act 1 (T1 maps) — live Java keys only
    'L01': ('q01', 'blade', 'scorch', 'burn', 1, '残门焚斧', {'burn_spread': 2, 'spread_icd': 10, 'burn_mult': 0.95}),
    'L02': ('q01', 'charm', 'any', 'dodgeheal', 1, '门楼余烬', {'dodge_heal': 0.01, 'dodge_icd': 15, 'taken_tele': 1.04}),
    'L03': ('q02', 'charm', 'burst', 'hitburst', 1, '炉心护符', {'hit_burst': 1, 'taken_tele': 1.03}),
    'L04': ('q02', 'blade', 'burst', 'dodgeburst', 1, '守炉重锤', {'dodge_burst': 1, 'dmg_boss': 0.985}),  # burst rework (CoreRpg 1.65.17): was taken_tele 1.03 + dmg_boss 0.98 (pairs −3～−4 at low dodge)
    'L05': ('q03', 'blade', 'sustain', 'mend', 1, '残誓长戟', {'sustain_every': -1}),
    'L06': ('q03', 'charm', 'any', 'supply', 1, '守誓残灯', {'potion': 1.05, 'taken_boss': 1.04}),
    # ---- Stage 2 · act 2 (T2 maps) — need Java hooks (烬斩形状 / 烬斩点燃 / 烬斩护盾 / 低血回涌)
    'L07': ('q04', 'blade', 'any', 'shape', 2, '潮闸长杆', {'skill_var': 1, 'skill_cap': 3, 'skill_line': 5}),  # skill_line: Java-only reach (sim: cap only)
    'L08': ('q04', 'charm', 'scorch', 'burn', 2, '潮蚀护符', {'skill_var': 1, 'skill_ignite': 1, 'skill_ignite_n': 1, 'skill_burn': 1.0, 'burn_mult': 0.92}),  # stage 2a: n 3 → 1 (+20 at q03r)
    'L09': ('q05', 'blade', 'any', 'shape', 2, '断塔双斧', {'skill_var': 1, 'skill_cap': 3, 'skill_ring': 3.0}),  # stage 2a: ring ≤ 3 (skill_plus swept: +4 / −30 swings); skill_ring Java-only radius
    'L10': ('q05', 'charm', 'burst', 'dodgeburst', 2, '回廊护符', {'hit_burst': 1, 'dodge_burst': 1, 'burst_mult': 0.9}),
    'L11': ('q06', 'blade', 'any', 'guard', 2, '霜封长刀', {'skill_var': 1, 'skill_shield': 0.005, 'skill_shield_max': 0.01, 'skill_shield_secs': 5, 'skill_mult': 0.9, 'taken_boss': 1.05}),
    'L12': ('q06', 'charm', 'sustain', 'heal', 2, '统领护符', {'sustain_low': 0.4, 'sustain_low_every': -2, 'sustain_mult': 0.99}),
    # ---- Stage 3 · Q07 (T3) — candidates, re-checked in stage 3
    'L13': ('q07', 'blade', 'any', 'shape', 3, '炉锁巨锤', {'skill_var': 1, 'skill_charge': 0.5, 'skill_plus': 1}),
    'L14': ('q07', 'charm', 'any', 'guard', 3, '炉芯护符', {'skill_var': 1, 'skill_shield': 0.02, 'skill_shield_max': 0.06, 'skill_shield_secs': 6, 'taken_mob': 1.03}),
    'L15': ('q07', 'charm', 'scorch', 'burn', 3, '锈轨余火', {'burn_ticks': 1, 'burn_mult': 0.8}),
}


def staged():
    return [i for i in SIGS if SIGS[i][4] <= STAGE]


def fams_of(sid):
    f = SIGS[sid][2]
    return FAMS if f == 'any' else (f,)


def combine(ids):
    import growth
    return growth.combine([SIGS[i][6] for i in ids])


def legal_pairs(fam):
    bl = [i for i in staged() if SIGS[i][1] == 'blade' and fam in fams_of(i)]
    ch = [i for i in staged() if SIGS[i][1] == 'charm' and fam in fams_of(i)]
    return [(b, c) for b in bl for c in ch if SIGS[b][3] != SIGS[c][3]]


def _job(j):
    import builddiv as bd
    spec, ctx, d, n, sd = j
    return bd.measure((spec, ctx, d, 'base', n, sd))['clear']


def cells():
    return [(m + k, d) for m in MAPS for k in 'rc' for d in (0.3, 0.5, 0.7)]


def run(variants, n, out):
    """variants: {name: (fam, extra mods or None, talents list or None)}; baseline rows are their own variants."""
    R = pickle.load(open(out, 'rb')) if os.path.exists(out) else {}
    jobs, keys = [], []
    for name, (fam, extra, tal) in variants.items():
        for c, d in cells():
            for sd in SEEDS:
                k = (name, fam, c, d, sd)
                if k in R:
                    continue
                spec = {'set': fam}
                if extra:
                    spec['extra'] = extra
                if tal:
                    spec['talents'] = tal
                    spec['tier'] = 1
                jobs.append((spec, c, d, n, sd))
                keys.append(k)
    if jobs:
        with Pool(NPROC) as p:
            res = p.map(_job, jobs, chunksize=8)
        R.update(dict(zip(keys, res)))
        pickle.dump(R, open(out, 'wb'))
    print('wrote', out, len(R), 'new', len(jobs))
    return R


def delta(R, name, base, fam):
    out = {}
    for c, d in cells():
        a = statistics.mean(R[(name, fam, c, d, s)] for s in SEEDS)
        b = statistics.mean(R[(base, fam, c, d, s)] for s in SEEDS)
        out[(c, d)] = 100 * (a - b)
    return out


def reachable(ids, cell):
    """D174 §8: a cell where these signatures can be worn. Signatures of map Mk drop on its repeat clears → first-clear
    cells (r) of later maps only; two at once need the own DUAL first clear (Q03) → r cells after Q03; challenge (c) cells
    come after Q07, so always reachable."""
    m, k = cell[0][:3], cell[0][3]
    if k == 'c':
        return True
    last = max(MAPS.index(SIGS[i][0]) for i in ids)
    if MAPS.index(m) <= last:
        return False
    return len(ids) < 2 or MAPS.index(m) > MAPS.index(DUAL)


DUAL = 'q03'


def row(label, dd):
    up = max(dd.items(), key=lambda x: x[1])
    dn = min(dd.items(), key=lambda x: x[1])
    inside = sum(1 for v in dd.values() if abs(v) <= 2.0)
    mabs = statistics.mean(abs(v) for v in dd.values())
    ok = '✅' if inside == len(dd) else ('🟡' if mabs <= 1.0 and max(abs(up[1]), abs(dn[1])) <= 3.0 else '❌')
    f = lambda kv: '%+.1f (%s d%.1f)' % (kv[1], kv[0][0], kv[0][1])
    return '| %s | %d/%d | %s | %s | %+.2f | %.2f | %s |' % (label, inside, len(dd), f(up), f(dn), statistics.mean(dd.values()), mabs, ok)


HEAD = '| 方案 | ±2 内 | 最高 | 最低 | 平均 Δpp | 平均 |Δ| | 判定 |\n|---|---|---|---|---|---|---|'
TALENTS = {'scorch': ['t1c', 't2c', 't3a'], 'burst': ['t1a', 't2a', 't3b'], 'sustain': ['t1b', 't2b', 't3c']}


def main():
    cmd = sys.argv[1] if len(sys.argv) > 1 else 'list'
    if cmd == 'list':
        for i, s in SIGS.items():
            print(i, s[5], s[0], s[1], s[2], s[3], 'stage', s[4], s[6])
        return
    if cmd in ('tol', 'pairs', 'talent'):
        n = int(sys.argv[2]) if len(sys.argv) > 2 else 2000
        out = sys.argv[3] if len(sys.argv) > 3 else '/tmp/ml/%s.pkl' % cmd
        if cmd == 'tol':
            V = {('base', f): (f, None, None) for f in FAMS}
            for i in staged():
                for f in fams_of(i):
                    V[(i, f)] = (f, SIGS[i][6], None)
        elif cmd == 'pairs':
            V = {('base', f): (f, None, None) for f in FAMS}
            for f in FAMS:
                for b, c in legal_pairs(f):
                    V[(b + '+' + c, f)] = (f, combine([b, c]), None)
        else:
            best = json.loads(os.environ.get('ML_BEST', '{}'))
            V = {}
            for f in FAMS:
                V[('tal', f)] = (f, None, TALENTS[f])
                for pr in best.get(f, []):  # EXCL applied (ML_NOEXCL=1: diagnostic without it)
                    ids = [i for i in pr.split('+') if os.environ.get('ML_NOEXCL') or EXCL.get(i) not in TALENTS[f]]
                    V[(pr + ('+tal!' if os.environ.get('ML_NOEXCL') else '+tal'), f)] = (f, combine(ids) if ids else None, TALENTS[f])
        for f in FAMS:  # names repeat across families → one batch per family
            run({nm: v for (nm, ff), v in V.items() if ff == f}, n, out)
        return
    if cmd == 'try':  # candidate sweep: '{"name": ["fam|any", {mods}]}' N OUT.pkl
        C = json.loads(sys.argv[2])
        n = int(sys.argv[3]) if len(sys.argv) > 3 else 1500
        out = sys.argv[4] if len(sys.argv) > 4 else '/tmp/ml/try.pkl'
        for f in FAMS:
            sub = {'base': (f, None, None)}
            for nm, (fam, mods) in C.items():
                if fam in ('any', f):
                    sub[nm] = (f, mods, None)
            run(sub, n, out)
        return
    if cmd == 'report':
        R = {}
        for p in sys.argv[2:]:
            R.update(pickle.load(open(p, 'rb')))
        names = sorted({(k[0], k[1]) for k in R})
        print(HEAD)
        for nm, f in names:
            if nm in ('base', 'tal'):
                continue
            base = 'tal' if nm.endswith(('+tal', '+tal!')) else 'base'
            if (base, f, 'q01r', 0.3, SEEDS[0]) not in R:
                continue
            lab = nm
            if nm in SIGS:
                lab = '%s %s（%s·%s·%s）' % (nm, SIGS[nm][5], SIGS[nm][0].upper(), SIGS[nm][1], SIGS[nm][3])
            dd = delta(R, nm, base, f)
            print(row('%s · %s' % (lab, f), dd))
            ids = [i for i in nm.replace('+tal!', '').replace('+tal', '').split('+') if i in SIGS]
            if ids and os.environ.get('ML_REACH', '1') == '1':
                rd = {c: v for c, v in dd.items() if reachable(ids, c)}
                if len(rd) < len(dd):
                    print(row('%s · %s · 可达 %d 格' % (lab, f, len(rd)), rd))
        return
    if cmd == 'econ':
        ids = json.loads(sys.argv[2])
        rest = sys.argv[3:]
        if ids:
            if isinstance(ids, dict):  # {"scorch": ["L01", "L02"], ...}: the pair each family wears
                parts = {f: [SIGS[i][6] for i in ids.get(f, [])] for f in FAMS}
            else:
                parts = {f: [SIGS[i][6] for i in ids if f in fams_of(i)] for f in FAMS}
            import growth

            def fn(info, ctx):
                return growth.combine(parts.get(info.get('set'), [])) or None
            p1sim.GROWTH = fn
        import p2econ
        sys.argv = ['p2econ'] + rest
        p2econ.main()
        return
    print(__doc__)


if __name__ == '__main__':
    main()
