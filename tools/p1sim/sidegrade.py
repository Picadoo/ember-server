"""Growth sidegrades (B02 follow-up, 2026-10-04): talent row 1 redesign, row 3 + point-6 gate, 余烬纹 / 定身纹, 炽愈 balance.
Reuses builddiv's measurement (same metrics / contexts / tolerance cells); adds per-job cfg patches (set constants) and
a candidate table. Modes:
  python3 sidegrade.py screen  --ids A1,B1 [--sets burst,scorch,sustain] [--n 1000] [--seeds 7,11,13]   → /tmp/sg/<tag>.pkl
  python3 sidegrade.py report  [--pkl ...]                                                            → out-growth-sidegrades.md
Every candidate is compared with the CURRENT node / value it replaces (paired: same seeds, same runs), on
  * tolerance: 7 maps × (first-clear normal at book reference gear r / challenge T3+6 c) × dodge 0.3/0.5/0.7 = 42 cells per set
  * felt: Q04 / Q07 repeat normal (T1+6 / T2+8) and Q07 challenge (T3+6), dodge 0.3 / 0.5 / 0.7.
All mods here are existing Java EmberGrowth.Mods keys unless marked NEW."""
import sys, os, json, pickle, statistics, random, copy
from multiprocessing import Pool
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import builddiv as bd, p1sim, rules

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.environ.get('SG_DIR', '/tmp/sg')
SETS = ('burst', 'scorch', 'sustain')
DODGES = (0.3, 0.5, 0.7)
FELT_CTX = ('q04n', 'q07n', 'q07c')

# ---------------------------------------------------------------- candidates
# kind 'node': mods replace the node (isolated: no other growth); 'cfg': set-constant patch (炽愈 balance);
# 'affix': affix values at tier 4 (and tier 1) as raw mods.
C = {}
def cand(cid, item, label, mods=None, node=None, cfg=None, sets=SETS, note=''):
    C[cid] = {'id': cid, 'item': item, 'label': label, 'mods': mods or {}, 'node': node, 'cfg': cfg or {}, 'sets': sets, 'note': note}

# current nodes (the baselines)
cand('t1a', 'cur', '现行 回身斩', {'dodge_dmg': 1.02, 'dodge_secs': 3, 'taken_tele': 1.02}, 't1a')
cand('t1b', 'cur', '现行 稳桩', {'taken_tele': 0.97, 'dmg_boss': 0.99}, 't1b')
cand('t1c', 'cur', '现行 踏步回气', {'dodge_heal': 0.008, 'dodge_icd': 15, 'taken_tele': 1.03}, 't1c')
cand('none', 'cur', '无成长', {})
# row 1 · aggression: faster boss, harder hits (total damage taken ≈ same when taken × ≈ dmg ×)
for i, (g, c) in enumerate(((1.12, 1.12), (1.15, 1.15), (1.12, 1.08), (1.15, 1.10)), 1):
    cand('A%d' % i, 'row1', '压上 首领伤害 ×%.2f / 受到首领伤害 ×%.2f' % (g, c), {'dmg_boss': g, 'taken_boss': c, 'taken_tele': c})
# row 1 · standing ground: slower boss, much less from telegraphs (stand in and keep swinging)
for i, (y, tt, tb) in enumerate(((0.90, 0.90, 0.90), (0.90, 0.75, 0.90), (0.88, 0.80, 0.85), (0.92, 0.80, 0.92)), 1):
    cand('B%d' % i, 'row1', '稳桩 首领伤害 ×%.2f / 受预警招 ×%.2f / 受首领普攻 ×%.2f' % (y, tt, tb), {'dmg_boss': y, 'taken_tele': tt, 'taken_boss': tb})
# row 1 · mobility: dodge a telegraph → big damage window; lower baseline damage on the boss
for i, (z, b) in enumerate(((1.40, 0.92), (1.50, 0.92), (1.40, 0.95), (1.60, 0.90)), 1):
    cand('D%d' % i, 'row1', '回身斩 躲开预警招后 3 秒伤害 ×%.2f / 首领伤害 ×%.2f' % (z, b), {'dodge_dmg': z, 'dodge_secs': 3, 'dmg_boss': b})

# row 1 round 2 (after round 1: uniform boss-damage / boss-taken trades swing the 42 cells ±10–29pp, see report)
for i, (w, sec, o) in enumerate(((1.30, 2, 0.92), (1.35, 1.5, 0.93), (1.25, 2, 0.94), (1.40, 1.5, 0.92)), 1):  # aggression = punish windows (NEW keys)
    cand('W%d' % i, 'row1', '回身斩→破绽 首领预警招落下后 %.1f 秒对首领 ×%.2f / 其余时间 ×%.2f（NEW win_*）' % (sec, w, o),
         {'win_dmg': w, 'win_secs': sec, 'win_out': o})
for i, (t, b_) in enumerate(((0.80, 1.10), (0.80, 1.12), (0.75, 1.12), (0.75, 1.15)), 1):  # standing ground = telegraphs hurt less, boss swings more
    cand('F%d' % i, 'row1', '稳桩→铁壁 受首领预警招 ×%.2f / 受首领普攻 ×%.2f' % (t, b_), {'taken_tele': t, 'taken_boss': b_})
for i, (z, sec, b_, w) in enumerate(((1.50, 3, 0.88, 1.0), (1.40, 3, 0.90, 1.10), (1.60, 2, 0.90, 1.0), (1.50, 3, 0.90, 1.15)), 1):  # mobility
    cand('M%d' % i, 'row1', '踏步→闪击 躲开预警招后 %d 秒伤害 ×%.2f / 首领伤害 ×%.2f / 被预警招打中 ×%.2f' % (sec, z, b_, w),
         {'dodge_dmg': z, 'dodge_secs': sec, 'dmg_boss': b_, 'taken_tele': w})

# row 3 (set-locked: only the matching set) · target metric in brackets
cand('t3a', 'cur', '现行 燎原', {'burn_spread': 1, 'spread_icd': 15, 'burn_mult': 0.94}, 't3a', sets=('scorch',))
cand('t3b', 'cur', '现行 反震', {'hit_burst': 1, 'taken_tele': 1.02}, 't3b', sets=('burst',))
cand('t3c', 'cur', '现行 扛核', {'share_w': 1.5, 'sustain_mult': 0.99}, 't3c', sets=('sustain',))
for i, (m, b_) in enumerate(((1.15, 0.92), (1.12, 0.94), (1.18, 0.90)), 1):   # 焚烬 = clear packs [杂兵房用时]
    cand('R3a%d' % i, 'row3', '燎原→清场 对普通怪 ×%.2f / 对首领 ×%.2f' % (m, b_), {'dmg_mob': m, 'dmg_boss': b_}, 't3a', sets=('scorch',))
cand('R3a4', 'row3', '燎原→连锁燃烧 4 秒无冷却 / 每跳 ×0.90 / 对普通怪 ×1.08', {'burn_spread': 4, 'spread_icd': 0, 'burn_mult': 0.90, 'dmg_mob': 1.08}, 't3a', sets=('scorch',))
for i, (b_, m) in enumerate(((1.12, 0.88), (1.10, 0.90), (1.14, 0.86)), 1):   # 烬爆 = kill bosses [首领用时]
    cand('R3b%d' % i, 'row3', '反震→破阵 对首领 ×%.2f / 对普通怪 ×%.2f（保留被预警招打中烬爆 +1）' % (b_, m), {'hit_burst': 1, 'dmg_boss': b_, 'dmg_mob': m}, 't3b', sets=('burst',))
for i, (sm, sh) in enumerate(((1.35, 0.0), (1.25, 0.5), (1.5, 0.0)), 1):       # 炽愈 = heal more, smaller buffer [自我回复 / 最低血量]
    cand('R3c%d' % i, 'row3', '扛核→回涌 炽愈回复 ×%.2f / 生命加成 ×%.1f（保留烬核 1.5 份）' % (sm, sh), {'share_w': 1.5, 'sustain_mult': sm, 'sustain_hp': sh}, 't3c', sets=('sustain',))
# row 3 round 2 (reachable cells only: row 3 needs all 6 points → challenge / raid / abyss, never a first clear)
cand('R3a5', 'row3', '燎原→连锁燃烧 4 秒无冷却 / 每跳 ×0.92 / 对普通怪 ×1.10', {'burn_spread': 4, 'spread_icd': 0, 'burn_mult': 0.92, 'dmg_mob': 1.10}, 't3a', sets=('scorch',))
cand('R3a6', 'row3', '燎原→连锁燃烧 4 秒无冷却 / 每跳 ×0.90 / 对普通怪 ×1.12', {'burn_spread': 4, 'spread_icd': 0, 'burn_mult': 0.90, 'dmg_mob': 1.12}, 't3a', sets=('scorch',))
for i, (pk, so) in enumerate(((1.30, 0.50), (1.25, 0.60), (1.40, 0.40)), 4):   # 烬爆 = pull packs (NEW burst_pack / burst_solo)
    cand('R3b%d' % i, 'row3', '反震→聚爆 烬爆命中 ≥3 个 ×%.2f / 只命中 1 个 ×%.2f（NEW）' % (pk, so), {'burst_pack': pk, 'burst_solo': so}, 't3b', sets=('burst',))
cand('R3b7', 'row3', '反震→连爆 烬爆每 4 次普攻（原 5）/ 系数 ×0.80', {'burst_every': -1, 'burst_mult': 0.80}, 't3b', sets=('burst',))
cand('R3b8', 'row3', '反震→连爆 烬爆每 3 次普攻 / 系数 ×0.62', {'burst_every': -2, 'burst_mult': 0.62}, 't3b', sets=('burst',))
for i, (sm, sh) in enumerate(((1.20, 0.6), (1.15, 0.7), (1.20, 0.5)), 4):
    cand('R3c%d' % i, 'row3', '扛核→回涌 炽愈回复 ×%.2f / 生命加成 ×%.1f（保留烬核 1.5 份）' % (sm, sh), {'share_w': 1.5, 'sustain_mult': sm, 'sustain_hp': sh}, 't3c', sets=('sustain',))
# 炽愈 balance round 2
cand('S6', 'set', '炽愈 生命加成 1.06 + 回复 ×0.85', cfg={'sustain_hp': 1.06, 'sustain_pct': [0, 0.02125, 0.0276, 0.034]}, sets=('sustain',))
cand('S7', 'set', '炽愈 生命加成 1.00 + 回复 ×0.90', cfg={'sustain_hp': 1.00, 'sustain_pct': [0, 0.0225, 0.0293, 0.036]}, sets=('sustain',))
cand('S8', 'set', '炽愈 回复 ×0.70', cfg={'sustain_pct': [0, 0.0175, 0.0228, 0.028]}, sets=('sustain',))
cand('S9', 'set', '炽愈 生命加成 1.06 + 回复冷却 6→7 秒', cfg={'sustain_hp': 1.06, 'sustain_icd': 7.0}, sets=('sustain',))
cand('S10', 'set', '炽愈 生命加成 1.00 + 回复 ×0.80', cfg={'sustain_hp': 1.00, 'sustain_pct': [0, 0.02, 0.026, 0.032]}, sets=('sustain',))

# affixes at tier 4 (isolated, vs the current affix at tier 4)
cand('b_set4', 'cur', '现行 余烬纹 4 档', {'set_dmg': 1.01})
cand('c_tele4', 'cur', '现行 定身纹 4 档', {'taken_tele': 0.98})
cand('E1', 'affix', '余烬纹→专注纹 4 档 对首领 ×1.10 / 对普通怪 ×0.90（NEW 第二键）', {'dmg_boss': 1.10, 'dmg_mob': 0.90})
cand('E2', 'affix', '余烬纹→专注纹 4 档 对首领 ×1.08 / 对普通怪 ×0.92', {'dmg_boss': 1.08, 'dmg_mob': 0.92})
cand('T1', 'affix', '定身纹→迎击纹 4 档 受预警招 ×0.80 / 受普通怪 ×1.10（NEW 第二键）', {'taken_tele': 0.80, 'taken_mob': 1.10})
cand('T2', 'affix', '定身纹→迎击纹 4 档 受预警招 ×0.85 / 受普通怪 ×1.06', {'taken_tele': 0.85, 'taken_mob': 1.06})
# 炽愈 set balance (set constants; only the 炽愈 set changes)
cand('S1', 'set', '炽愈 生命加成 1.12→1.06（ember-v1.yml sustain_hp_mult）', cfg={'sustain_hp': 1.06}, sets=('sustain',))
cand('S2', 'set', '炽愈 生命加成 1.12→1.00', cfg={'sustain_hp': 1.00}, sets=('sustain',))
cand('S3', 'set', '炽愈 回复 2.5/3.25/4% → 2.0/2.6/3.2%（EmberSetRules SUSTAIN_PCT ×0.8）', cfg={'sustain_pct': [0, 0.02, 0.026, 0.032]}, sets=('sustain',))
cand('S4', 'set', '炽愈 回复内置冷却 6→8 秒（SUSTAIN_ICD_MS）', cfg={'sustain_icd': 8.0}, sets=('sustain',))
cand('S5', 'set', '炽愈 回复每 5→6 次普攻（SUSTAIN_EVERY）', cfg={'sustain_every': 6}, sets=('sustain',))


def spec_of(c, fam):
    sp = {'set': fam}
    if c['mods']:
        sp['extra'] = dict(c['mods'])
    if c['cfg']:
        sp['cfgpatch'] = dict(c['cfg'])
    return sp


def measure2(job):
    spec = job[0]
    patch = spec.get('cfgpatch')
    if not patch:
        return bd.measure(job)
    cf = bd.cfgs()
    keep = {(k, ck): cf[ck].get(k) for k in patch for ck in ('n', 'c')}
    try:
        for k, v in patch.items():
            for ck in ('n', 'c'):
                cf[ck][k] = copy.deepcopy(v)
        return bd.measure((dict(spec, cfgpatch=None),) + tuple(job[1:]))
    finally:
        for (k, ck), v in keep.items():
            cf[ck][k] = v


def tol_jobs(spec, n, seeds):
    jobs, keys = [], []
    for k in p1sim.REF_GEAR:
        for kind in ('r', 'c'):
            for d in DODGES:
                for sd in seeds:
                    jobs.append((spec, k + kind, d, 'base', n, sd)); keys.append(('tol', k + kind, d, sd))
    return jobs, keys


def felt_jobs(spec, n, seed):
    jobs, keys = [], []
    for ctx in FELT_CTX:
        for d in DODGES:
            jobs.append((spec, ctx, d, 'base', n, seed)); keys.append(('felt', ctx, d, seed))
    return jobs, keys


KEEP = ('clear', 'secs', 't_trash', 't_boss', 'k_boss', 'p_boss', 'k_trash', 'potions', 'taken', 'heal_sustain', 'heal_dodge',
        'n_dodge_tele', 'n_hit_tele', 'minhp_p10', 'share', 't_affix_blazing', 't_affix_split', 't_affix_shield', 'H')


def slim(r):
    return {k: r.get(k) for k in KEEP}


def screen(ids, sets, n, seeds, nfelt, fseed, tag, nproc):
    jobs, keys = [], []
    for cid in ids:
        c = C[cid]
        for fam in sets:
            if fam not in c['sets']:
                continue
            sp = spec_of(c, fam)
            j1, k1 = tol_jobs(sp, n, seeds)
            j2, k2 = felt_jobs(sp, nfelt, fseed)
            jobs += j1 + j2; keys += [(cid, fam) + k for k in k1 + k2]
    print('jobs', len(jobs), flush=True)
    with Pool(nproc) as pool:
        res = pool.map(measure2, jobs, chunksize=2)
    path = os.path.join(OUT, tag + '.pkl')
    R = pickle.load(open(path, 'rb')) if os.path.exists(path) else {}
    for k, r in zip(keys, res):
        R[k] = slim(r)
    pickle.dump(R, open(path, 'wb'))
    print('wrote', path, len(R), flush=True)


def arg(name, d, cast=str):
    return cast(sys.argv[sys.argv.index(name) + 1]) if name in sys.argv else d


if __name__ == '__main__':
    mode = sys.argv[1]
    print('# ' + rules.stamp(), flush=True)
    if mode == 'screen':
        screen(arg('--ids', '', lambda s: s.split(',')), arg('--sets', SETS, lambda s: tuple(s.split(','))), arg('--n', 1000, int),
               arg('--seeds', (7, 11, 13), lambda s: tuple(int(x) for x in s.split(','))), arg('--nfelt', 2000, int),
               arg('--fseed', 4243, int), arg('--tag', 'screen'), int(os.environ.get('NPROC', '6')))


# ---------------------------------------------------------------- analysis
def cells(R, cid, fam, kind='tol'):
    out = {}
    for k, r in R.items():
        if k[0] == cid and k[1] == fam and k[2] == kind:
            out.setdefault((k[3], k[4]), []).append(r)
    return out


def tol_cmp(R, cid, base, fam):
    """per tolerance cell: mean clear-rate difference (pp) over the seeds (paired: same seeds) + per-seed diffs"""
    a, b = cells(R, cid, fam), cells(R, base, fam)
    out = {}
    for key in a:
        if key in b:
            ds = [100 * (x['clear'] - y['clear']) for x, y in zip(a[key], b[key])]
            out[key] = (statistics.mean(ds), ds)
    return out


def tol_summary(R, cid, base, fam, lim=3.0):
    t = tol_cmp(R, cid, base, fam)
    if not t:
        return None
    up = max(t.items(), key=lambda x: x[1][0]); dn = min(t.items(), key=lambda x: x[1][0])
    inn = sum(1 for v in t.values() if abs(v[0]) <= lim)
    return {'up': up[1][0], 'up_at': '%s d%.1f' % up[0], 'dn': dn[1][0], 'dn_at': '%s d%.1f' % dn[0], 'in': inn, 'n': len(t),
            'mean': statistics.mean(v[0] for v in t.values())}


def felt_cmp(R, cid, base, fam, metric):
    """{(ctx, dodge): relative change of metric vs base}"""
    a, b = cells(R, cid, fam, 'felt'), cells(R, base, fam, 'felt')
    out = {}
    for key in a:
        if key in b:
            x, y = a[key][0].get(metric), b[key][0].get(metric)
            out[key] = None if x is None or not y else x / y - 1
    return out


def felt_abs(R, cid, fam, metric):
    return {k: v[0].get(metric) for k, v in cells(R, cid, fam, 'felt').items()}
