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

# row 1 round 3 (round 2: F1–F4 lose 5–10pp at q03c d0.5 → boss swings dominate there; smaller swing penalty. W: lower off-window)
for i, (t, b_) in enumerate(((0.80, 1.05), (0.80, 1.06), (0.85, 1.04), (0.75, 1.08)), 5):
    cand('F%d' % i, 'row1', '稳桩→铁壁 受首领预警招 ×%.2f / 受首领普攻 ×%.2f' % (t, b_), {'taken_tele': t, 'taken_boss': b_})
for i, (w, sec, o) in enumerate(((1.30, 2, 0.90), (1.25, 1.5, 0.94)), 5):
    cand('W%d' % i, 'row1', '回身斩→破绽 首领预警招落下后 %.1f 秒对首领 ×%.2f / 其余时间 ×%.2f（NEW win_*）' % (sec, w, o),
         {'win_dmg': w, 'win_secs': sec, 'win_out': o})

# T0b narrowed shapes (screen-only; DESIGN-ember-talent-row1-t0b-narrow · 已批 A)
# Do NOT overwrite D1/F7/W6. Pair: D1b↔t1a, F7b↔t1b, W6b↔t1c.
cand('D1b', 'row1', 'T0b 机动 躲后3s×1.22 / 首领×0.86', {'dodge_dmg': 1.22, 'dodge_secs': 3, 'dmg_boss': 0.86})
cand('F7b', 'row1', 'T0b 站桩 预警×0.90 / 普攻×1.07', {'taken_tele': 0.90, 'taken_boss': 1.07})
cand('W6b', 'row1', 'T0b 压上 窗1.2s×1.18 / 窗外×0.97', {'win_dmg': 1.18, 'win_secs': 1.2, 'win_out': 0.97})

# T0c mid-corridor (screen-only; DESIGN-ember-talent-row1-t0c-narrow · 已批 A)
# Do NOT overwrite D1/D1b/F7/F7b/W6/W6b. Pair: D1c↔t1a, F7c↔t1b, W6c↔t1c.
cand('D1c', 'row1', 'T0c 机动 躲后3s×1.28 / 首领×0.90', {'dodge_dmg': 1.28, 'dodge_secs': 3, 'dmg_boss': 0.90})
cand('F7c', 'row1', 'T0c 站桩 预警×0.88 / 普攻×1.05', {'taken_tele': 0.88, 'taken_boss': 1.05})
cand('W6c', 'row1', 'T0c 压上 窗1.2s×1.18 / 窗外×0.98', {'win_dmg': 1.18, 'win_secs': 1.2, 'win_out': 0.98})

# T0' mech-pivot (screen-only; DESIGN-ember-talent-row1-mech-pivot · 已批 A · 方案 M)
# Do NOT overwrite D1*/F7*/W6*. Pair: M_dodge↔t1a, M_stance↔t1b, M_winhit↔t1c.
# No permanent dmg_boss / taken_boss / win_out<1 tax keys.
cand('M_dodge', 'row1', "T0' 机动 躲窗至多3次·2s×1.40（无永久首领税）",
     {'dodge_procs_cap': 3, 'dodge_secs': 2, 'dodge_dmg': 1.40})
C['M_dodge']['base'] = 't1a'
cand('M_stance', 'row1', "T0' 站桩 预警落地后2.5s受预警×0.80（窗外无普攻罚）",
     {'stance_secs': 2.5, 'stance_taken_tele': 0.80})
C['M_stance']['base'] = 't1b'
cand('M_winhit', 'row1', "T0' 压上 窗2s内至多5刀×1.30/窗外×1.00（命中封顶）",
     {'win_dmg': 1.30, 'win_secs': 2.0, 'win_out': 1.0, 'win_hits_cap': 5})
C['M_winhit']['base'] = 't1c'

# T0'' narrowed mech-pivot (screen-only; same DESIGN · §4 re-shape amp; do NOT overwrite M_*)
# Pair: M2_dodge↔t1a, M2_stance↔t1b, M2_winhit↔t1c. Still no permanent tax keys.
cand('M2_dodge', 'row1', "T0'' 机动 躲窗至多2次·1.2s×1.15（无永久首领税）",
     {'dodge_procs_cap': 2, 'dodge_secs': 1.2, 'dodge_dmg': 1.15})
C['M2_dodge']['base'] = 't1a'
cand('M2_stance', 'row1', "T0'' 站桩 预警落地后1.5s受预警×0.90（窗外无普攻罚）",
     {'stance_secs': 1.5, 'stance_taken_tele': 0.90})
C['M2_stance']['base'] = 't1b'
cand('M2_winhit', 'row1', "T0'' 压上 窗1.5s内至多3刀×1.12/窗外×1.00（命中封顶）",
     {'win_dmg': 1.12, 'win_secs': 1.5, 'win_out': 1.0, 'win_hits_cap': 3})
C['M2_winhit']['base'] = 't1c'

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

# row 3 round 3 · 焚烬 slope check; 烬爆 聚爆 with a smaller single-target cost (round 2: solo ≤0.60 slows bosses → q02c −5)
cand('R3a7', 'row3', '燎原→连锁燃烧 4 秒无冷却 / 每跳 ×0.92 / 对普通怪 ×1.16', {'burn_spread': 4, 'spread_icd': 0, 'burn_mult': 0.92, 'dmg_mob': 1.16}, 't3a', sets=('scorch',))
for i, (pk, so) in enumerate(((1.35, 0.75), (1.30, 0.80), (1.40, 0.70)), 9):
    cand('R3b%d' % i, 'row3', '反震→聚爆 烬爆命中 ≥3 个 ×%.2f / 只命中 1 个 ×%.2f（NEW）' % (pk, so), {'burst_pack': pk, 'burst_solo': so}, 't3b', sets=('burst',))
# 炽愈 row 3 on top of the S2 set nerf (sustain_hp 1.00 → the row-3 "sustain_hp" lever is gone, need another cost)
S2CFG = {'sustain_hp': 1.00}
cand('t3cS2', 'cur', '现行 扛核（在 S2 之上）', {'share_w': 1.5, 'sustain_mult': 0.99}, 't3c', cfg=S2CFG, sets=('sustain',))
cand('R3c7', 'row3', '扛核→回涌 炽愈回复 ×1.20 / 伤害 ×0.95（在 S2 之上）', {'share_w': 1.5, 'sustain_mult': 1.20, 'dmg_boss': 0.95, 'dmg_mob': 0.95}, 't3c', cfg=S2CFG, sets=('sustain',))
cand('R3c8', 'row3', '扛核→回涌 炽愈回复 ×1.15 / 伤害 ×0.96（在 S2 之上）', {'share_w': 1.5, 'sustain_mult': 1.15, 'dmg_boss': 0.96, 'dmg_mob': 0.96}, 't3c', cfg=S2CFG, sets=('sustain',))
for _k in ('R3c7', 'R3c8'):
    C[_k]['base'] = 't3cS2'
cand('R3c9', 'row3', '扛核→救急 生命 <50% 时炽愈回复 ×1.50 / ≥50% 时 ×0.60（在 S2 之上）', {'share_w': 1.5, 'sustain_low': 0.5, 'sustain_mult': 0.60, 'sustain_low_mult': 2.5}, 't3c', cfg=S2CFG, sets=('sustain',))
C['R3c9']['base'] = 't3cS2'

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
        'n_dodge_tele', 'n_hit_tele', 'minhp_p10', 'share', 't_affix_blazing', 't_affix_split', 't_affix_shield', 'H',
        # target metrics added for the hold-out pass (not in the round-1–3 pkls)
        'boss_win_share', 'window_extra', 'n_burst', 'n_spread', 'n_window', 'n_sustain', 'taken_share', 'heal_potion',
        'shield_abs', 'shield_given', 'n_skill_ignite', 'n_burn', 'n_dodge_heal')


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


def getm(r, metric):
    """metric value; 'taken_<cls>' = damage taken from that hit class (× H per run), e.g. taken_tele = boss telegraphs"""
    if metric.startswith('taken_') and metric != 'taken_share':
        ts = r.get('taken_share')
        return None if ts is None or r.get('taken') is None else r['taken'] * ts.get(metric[6:], 0.0)
    return r.get(metric)


def felt_cmp(R, cid, base, fam, metric):
    """{(ctx, dodge): relative change of metric vs base}"""
    a, b = cells(R, cid, fam, 'felt'), cells(R, base, fam, 'felt')
    out = {}
    for key in a:
        if key in b:
            x, y = getm(a[key][0], metric), getm(b[key][0], metric)
            out[key] = None if x is None or not y else x / y - 1
    return out


def felt_abs(R, cid, fam, metric):
    return {k: getm(v[0], metric) for k, v in cells(R, cid, fam, 'felt').items()}


# ---------------------------------------------------------------- report
# reachable tolerance cells (M04): row 1 needs the Q03 first-clear point → Q01–Q03 first clears (r) are unreachable;
# row 3 needs all 6 points and affixes only roll on T3 gear → challenge cells (c) only; set constants → all 42.
REACH = {'row1': lambda ck: ck.endswith('c') or ck[:3] in ('q04', 'q05', 'q06', 'q07'),
         'row3': lambda ck: ck.endswith('c'), 'affix': lambda ck: ck.endswith('c'), 'set': lambda ck: True}
SETNAME = {'burst': '烬爆', 'scorch': '焚烬', 'sustain': '炽愈'}


def tol_reach(R, cid, base, fam, item, lim=3.0):
    t = {k: v for k, v in tol_cmp(R, cid, base, fam).items() if REACH[item](k[0])}
    if not t:
        return None
    up = max(t.items(), key=lambda x: x[1][0]); dn = min(t.items(), key=lambda x: x[1][0])
    # seed-clustered spread of the worst cell: max |diff| over the seeds (each seed = an independent run set)
    return {'up': up[1][0], 'up_at': '%s d%.1f' % up[0], 'dn': dn[1][0], 'dn_at': '%s d%.1f' % dn[0],
            'in': sum(abs(v[0]) <= lim for v in t.values()), 'n': len(t), 'mean': statistics.mean(v[0] for v in t.values()),
            'seed_sd': statistics.mean(statistics.pstdev(v[1]) for v in t.values()) if len(next(iter(t.values()))[1]) > 1 else 0.0}


def _felt_row(R, cid, base, fam, metric):
    d = felt_cmp(R, cid, base, fam, metric)
    return ' '.join('%+.0f' % (100 * d[(c, x)]) if d.get((c, x)) is not None else '—' for c in FELT_CTX for x in DODGES)


SECTIONS = (
    ('行 1（回身斩 / 稳桩 / 踏步回气）', 'row1', {'A': 't1a', 'B': 't1b', 'D': 't1a', 'W': 't1a', 'F': 't1b', 'M': 't1c'},
     ('t_boss', 'taken', 'k_boss', 'clear')),
    ('行 3（燎原 / 反震 / 扛核）', 'row3', {'R3a': 't3a', 'R3b': 't3b', 'R3c': 't3c'}, ('t_trash', 't_boss', 'heal_sustain', 'clear')),
    ('词缀（余烬纹 / 定身纹，4 档）', 'affix', {'E': 'b_set4', 'T': 'c_tele4'}, ('t_boss', 'k_boss', 'potions', 'clear')),
)


def base_of(cid, prefixes):
    for p in sorted(prefixes, key=len, reverse=True):
        if cid.startswith(p) and cid[len(p):].isdigit():
            return prefixes[p]
    return None


def report(pkls, holdout, path):
    R = {}
    for p in pkls:
        R.update(pickle.load(open(p, 'rb')))
    H = {}
    for p in holdout:
        H.update(pickle.load(open(p, 'rb')))
    L = ['# 成长 sidegrade 模拟（sidegrade.py report）', '', '`' + rules.stamp() + '`', '',
         '源 pkl：' + ', '.join(os.path.basename(p) for p in pkls) + ('；留出（hold-out）：' + ', '.join(os.path.basename(p) for p in holdout) if holdout else ''), '',
         '容差 = 各通关率格子（7 图 × 首通 r / 挑战 c × 闪避 0.3/0.5/0.7，种子 7/11/13 各 n=2000）与**被替换的现行节点**之差（pp，配对）。',
         '“可达格” = M04 规则：行 1 去掉 Q01–Q03 首通（拿不到第 1 点）；行 3 / 词缀只看挑战格（要 6 点 / T3 装）；套装常数看全部 42 格。',
         '体感 = 相对现行节点的变化（%），列顺序 Q04普通 d0.3/0.5/0.7 · Q07普通 · Q07挑战（n=3000，种子 4243）。', '']
    for title, item, prefixes, metrics in SECTIONS:
        L += ['## ' + title, '']
        ids = [cid for cid in C if C[cid]['item'] == item and base_of(cid, prefixes)]
        for fam in SETS:
            rows = []
            for cid in ids:
                c = C[cid]; base = c.get('base') or base_of(cid, prefixes)
                if fam not in c['sets'] or not cells(R, cid, fam) or not cells(R, base, fam):
                    continue
                tr = tol_reach(R, cid, base, fam, item); tf = tol_reach(R, cid, base, fam, 'set')
                hr = tol_reach(H, cid, base, fam, item) if H and cells(H, cid, fam) and cells(H, base, fam) else None
                rows.append('| %s | %s | %+.1f (%s) / %+.1f (%s) · %d/%d | %+.1f / %+.1f · %d/%d | %s | %s |' % (
                    cid, c['label'], tr['up'], tr['up_at'], tr['dn'], tr['dn_at'], tr['in'], tr['n'], tf['up'], tf['dn'], tf['in'], tf['n'],
                    ('%+.1f / %+.1f · %d/%d' % (hr['up'], hr['dn'], hr['in'], hr['n'])) if hr else '—',
                    ' · '.join('%s %s' % (m, _felt_row(R, cid, base, fam, m)) for m in metrics)))
            if rows:
                L += ['### %s（%s 套）' % (SETNAME[fam], fam), '',
                      '| id | 方案 | 可达格容差 最高/最低 · ±3 内 | 全 42 格 | 留出种子 可达格 | 体感（vs 现行）|', '|---|---|---|---|---|---|'] + rows + ['']
    L += ['## 炽愈套装常数（全 42 格，平均通关率 %）', '',
          '| id | 方案 | 首通 r 平均 | 挑战 c 平均 | 体感通关率 Q04n · Q07n · Q07c (d0.3/0.5/0.7) |', '|---|---|---|---|---|']
    def means(RR, cid, fam):
        tc = cells(RR, cid, fam)
        m = lambda e: 100 * statistics.mean(statistics.mean(x['clear'] for x in v) for k, v in tc.items() if k[0].endswith(e))
        fa = felt_abs(RR, cid, fam, 'clear')
        return m('r'), m('c'), ' '.join('%.0f' % (100 * fa[(c, d)]) for c in FELT_CTX for d in DODGES if fa.get((c, d)) is not None)
    for fam, cid in (('burst', 'none'), ('scorch', 'none')):
        if cells(R, cid, fam):
            r_, c_, f_ = means(R, cid, fam); L.append('| %s | %s（参照）| %.1f | %.1f | %s |' % (SETNAME[fam], '无成长', r_, c_, f_))
    for cid in ['none'] + [k for k in C if C[k]['item'] == 'set']:
        if cells(R, cid, 'sustain'):
            r_, c_, f_ = means(R, cid, 'sustain')
            L.append('| %s | %s | %.1f | %.1f | %s |' % (cid, '炽愈 现行' if cid == 'none' else C[cid]['label'], r_, c_, f_))
    hs = [k for k in C if C[k]['item'] == 'set' and cells(H, k, 'sustain')]
    if hs and cells(H, 'none', 'sustain'):
        L += ['', '留出种子：']
        for cid in ['none'] + hs:
            r_, c_, f_ = means(H, cid, 'sustain'); L.append('- %s：r %.1f / c %.1f · 体感 %s' % (cid, r_, c_, f_))
    open(path, 'w').write('\n'.join(L) + '\n')
    print('wrote', path)

if __name__ == '__main__':
    mode = sys.argv[1]
    print('# ' + rules.stamp(), flush=True)
    if mode == 'screen':
        screen(arg('--ids', '', lambda s: s.split(',')), arg('--sets', SETS, lambda s: tuple(s.split(','))), arg('--n', 1000, int),
               arg('--seeds', (7, 11, 13), lambda s: tuple(int(x) for x in s.split(','))), arg('--nfelt', 2000, int),
               arg('--fseed', 4243, int), arg('--tag', 'screen'), int(os.environ.get('NPROC', '6')))
    elif mode == 'report':
        report(arg('--pkl', [], lambda s: s.split(',')), arg('--holdout', [], lambda s: s.split(',')),
               arg('--out', os.path.join(HERE, 'out-growth-sidegrades.md')))
