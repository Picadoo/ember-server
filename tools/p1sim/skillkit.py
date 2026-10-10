#!/usr/bin/env python3
"""D210 skill kit S0 (技能架构 第 0 阶段) · offline budget checks for the 4-input kit of
docs/design/RESEARCH-ember-skill-architecture-2026-10-06.md §4 → docs/design/DESIGN-ember-skill-kit-2026-10-06.md (incl. S0b D212 守招).
Standard library only; offline. Models live in p1sim.py (PROPOSAL D210 kit_* keys; absent = today, bit-identical).

  python3 skillkit.py list
  python3 skillkit.py run    [N] [OUT.pkl] [ids,…]     # each candidate vs baseline on 42 cells × 3 sets
  python3 skillkit.py try    '{"name": [unlock_map, "fam|any", {mods}]}' [N] [OUT.pkl]
  python3 skillkit.py report OUT.pkl
Cells = mainline.py: 7 maps × first-clear r / challenge c × dodge 0.3 / 0.5 / 0.7, seeds ML_SEEDS (7,11).
Reachable cells: a slot unlocked by the first clear of map Mk is worn from the r cell of M(k+1) on; c cells always.
Pass mark (same as D174 §8): every reachable cell within ±2 pp clear rate is the target; hard line "within range" =
mean |Δ| ≤ 1 pp and no cell beyond ±3 pp. Also reported: clear-time Δ% (trash / boss), damage-taken Δ%, and the
active share (烬斩 + 烬突 damage / all damage) — budget ≈ today's 烬斩 share + 3 pp.
"""
import json
import os
import pickle
import statistics
import sys
from multiprocessing import Pool

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import mainline  # noqa: E402

NPROC = int(os.environ.get('NPROC', '6'))
SEEDS = mainline.SEEDS
FAMS = mainline.FAMS
MAPS = mainline.MAPS

GUARD = {'kit_guard_cd': 18, 'kit_guard_secs': 2, 'kit_guard_red': 0.4}
INT = {'kit_int_cd': 20, 'kit_int_p': 0.6}
GATHER = {'kit_gather_cd': 12, 'kit_gather_plus': 2, 'kit_gather_secs': 4}
DASH = {'kit_dash_cd': 10, 'kit_dash_mult': 0.5, 'kit_dash_n': 3, 'kit_dash_boss': 0.5}
MARK = {'kit_mark_cd': 10, 'kit_mark_delay': 0.5, 'kit_mark_boss': 0.5}
STEP = {'kit_step_cd': 14, 'kit_step_n': 2, 'kit_step_cost': 0.5}
LINE = dict(mainline.SIGS['L07'][6])
RING = dict(mainline.SIGS['L09'][6])


def M(*parts):
    out = {}
    for p in parts:
        out.update(p)
    return out


# id: (unlock map = first clear of, family or 'any', label, mods)
CANDS = {
    # 守招 (Q, Q03 first clear)
    'G18': ('q03', 'any', '守招·守墓壁垒 cd18 2s −40%', GUARD),
    'G24': ('q03', 'any', '守招·守墓壁垒 cd24 −40%', M(GUARD, {'kit_guard_cd': 24})),
    'G24r30': ('q03', 'any', '守招·守墓壁垒 cd24 −30%', M(GUARD, {'kit_guard_cd': 24, 'kit_guard_red': 0.3})),
    'G30': ('q03', 'any', '守招·守墓壁垒 cd30 −40%', M(GUARD, {'kit_guard_cd': 30})),
    'G30r30': ('q03', 'any', '守招·守墓壁垒 cd30 −30%', M(GUARD, {'kit_guard_cd': 30, 'kit_guard_red': 0.3})),
    # 守招 second rune (Q07 first clear)
    'I30': ('q07', 'any', '守招·破势 cd30 p0.6', M(INT, {'kit_int_cd': 30})),
    'I40': ('q07', 'any', '守招·破势 cd40 p0.6', M(INT, {'kit_int_cd': 40})),
    # 副招 (sneak+F, Q02 first clear)
    'K10': ('q02', 'any', '副招·灰印 cd10 延后 0.5s', MARK),
    'K10d1': ('q02', 'any', '副招·灰印 cd10 延后 1.0s', M(MARK, {'kit_mark_delay': 1.0})),
    'S12': ('q02', 'any', '副招·聚火 cd12 +2 4s', GATHER),
    'S16p1': ('q02', 'any', '副招·聚火 cd16 +1 4s', M(GATHER, {'kit_gather_cd': 16, 'kit_gather_plus': 1})),
    'D12': ('q02', 'any', '副招·烬突 cd12 0.5B×3 首领×0.5', M(DASH, {'kit_dash_cd': 12})),
    'D15m35': ('q02', 'any', '副招·烬突 cd15 0.35B×3', M(DASH, {'kit_dash_cd': 15, 'kit_dash_mult': 0.35})),
    # 身法 rune (Q05 first clear)
    'F14': ('q05', 'scorch', '身法·火痕步 cd14 点 2', STEP),
    'F14n1': ('q05', 'scorch', '身法·火痕步 cd14 点 1', M(STEP, {'kit_step_n': 1})),
    # 烬斩 runes (Q04 first clear)
    'RL': ('q04', 'any', '烬斩符文·直线（=L07）', LINE),
    'RR': ('q04', 'any', '烬斩符文·环斩（=L09）', RING),
}
# S0 round 2 (recommended structure): 副招 spends the 烬斩 charge, 守招 spends the 身法 cooldown (WoW 1.12 shocks style)
SEC = {'kit_sec_shared': 1}
QSH = {'kit_q_shared': 1}
CANDS2 = {
    'GS': ('q03', 'any', '守招·守墓壁垒 共享身法 cd14 2s −40%', M(GUARD, QSH, {'kit_guard_cd': 14})),
    'GSr50': ('q03', 'any', '守招·守墓壁垒 共享身法 −50%', M(GUARD, QSH, {'kit_guard_cd': 14, 'kit_guard_red': 0.5})),
    'IS': ('q07', 'any', '守招·破势 共享身法 cd14 p0.6', M(INT, QSH, {'kit_int_cd': 14})),
    'KS': ('q02', 'any', '副招·灰印 共享烬斩 延后 1.0s', M(MARK, SEC, {'kit_mark_delay': 1.0, 'kit_mark_boss': 1.0})),
    'KSd15': ('q02', 'any', '副招·灰印 共享烬斩 延后 1.5s', M(MARK, SEC, {'kit_mark_delay': 1.5, 'kit_mark_boss': 1.0})),
    'SS': ('q02', 'any', '副招·聚火 共享烬斩 +2 9s', M(GATHER, SEC, {'kit_gather_secs': 9})),
    'DS10': ('q02', 'any', '副招·烬突 共享烬斩 1.0B×3', M(DASH, SEC, {'kit_dash_mult': 1.0})),
    'DS15': ('q02', 'any', '副招·烬突 共享烬斩 1.5B×3（=烬斩）', M(DASH, SEC, {'kit_dash_mult': 1.5})),
    'F14c0': ('q05', 'scorch', '身法·火痕步 替换踏步 点 2 无额外耗时', M(STEP, {'kit_step_cost': 0})),
    'KITS-mark': ('q07', 'any', '全套·灰印+壁垒（共享）', M(MARK, SEC, {'kit_mark_delay': 1.0, 'kit_mark_boss': 1.0}, GUARD, QSH, {'kit_guard_cd': 14})),
    'KITS-gather': ('q07', 'any', '全套·聚火+壁垒+环斩（共享）', M(GATHER, SEC, {'kit_gather_secs': 9}, GUARD, QSH, {'kit_guard_cd': 14}, RING)),
    'KITS-dash': ('q07', 'any', '全套·烬突+破势+直线（共享）', M(DASH, SEC, {'kit_dash_mult': 1.0}, INT, QSH, {'kit_int_cd': 14}, LINE)),
    'KITS-fire': ('q07', 'scorch', '全套·烬突+壁垒+火痕步（共享）', M(DASH, SEC, {'kit_dash_mult': 1.0}, GUARD, QSH, {'kit_guard_cd': 14}, STEP, {'kit_step_cost': 0})),
}
# S0 round 3: tune the shared versions toward "even trade" (sometimes better, sometimes worse than what they replace)
CANDS3 = {
    'GSk50': ('q03', 'any', '守招·壁垒 共享身法 留 50% 预警加成 −40%', M(GUARD, QSH, {'kit_guard_cd': 14, 'kit_q_keep': 0.5})),
    'GSk50r60': ('q03', 'any', '守招·壁垒 共享身法 留 50% −60%', M(GUARD, QSH, {'kit_guard_cd': 14, 'kit_q_keep': 0.5, 'kit_guard_red': 0.6})),
    'GSk75': ('q03', 'any', '守招·壁垒 共享身法 留 75% −40%', M(GUARD, QSH, {'kit_guard_cd': 14, 'kit_q_keep': 0.75})),
    'ISk50': ('q07', 'any', '守招·破势 共享身法 留 50% p0.6', M(INT, QSH, {'kit_int_cd': 14, 'kit_q_keep': 0.5})),
    'ISk75': ('q07', 'any', '守招·破势 共享身法 留 75% p0.6', M(INT, QSH, {'kit_int_cd': 14, 'kit_q_keep': 0.75})),
    'KSm10': ('q02', 'any', '副招·灰印 共享烬斩 单体 1.0B + 延后 1.0s', M(MARK, SEC, {'kit_mark_delay': 1.0, 'kit_mark_boss': 1.0, 'kit_mark_mult': 1.0})),
    'KSm15': ('q02', 'any', '副招·灰印 共享烬斩 单体 1.5B + 延后 0.5s', M(MARK, SEC, {'kit_mark_delay': 0.5, 'kit_mark_boss': 1.0, 'kit_mark_mult': 1.5})),
    'SSm10': ('q02', 'any', '副招·聚火 共享烬斩 1.0B 脉冲 + 9s 内 +2', M(GATHER, SEC, {'kit_gather_secs': 9, 'kit_gather_mult': 1.0})),
    'SSm10p1': ('q02', 'any', '副招·聚火 共享烬斩 1.0B 脉冲 + 9s 内 +1', M(GATHER, SEC, {'kit_gather_secs': 9, 'kit_gather_mult': 1.0, 'kit_gather_plus': 1})),
    'F14c0n1': ('q05', 'scorch', '身法·火痕步 替换踏步 点 1', M(STEP, {'kit_step_cost': 0, 'kit_step_n': 1})),
}
# S0 round 4: 守招 on the 烬斩 charge (one 8 s 余烬 charge feeds F / sneak+F / Q; 身法 keeps its own 14 s) + 聚火 fix
CHG = {'kit_guard_charge': 1, 'kit_guard_cd': 0.01}
CANDS4 = {
    'GC': ('q03', 'any', '守招·壁垒 共享烬斩充能 2s −40%', M(GUARD, CHG)),
    'GCr60': ('q03', 'any', '守招·壁垒 共享烬斩充能 2s −60%', M(GUARD, CHG, {'kit_guard_red': 0.6})),
    'GCs1': ('q03', 'any', '守招·壁垒 共享烬斩充能 1s −60%', M(GUARD, CHG, {'kit_guard_red': 0.6, 'kit_guard_secs': 1})),
    'IC': ('q07', 'any', '守招·破势 共享烬斩充能 p0.6', M(INT, {'kit_int_cd': 8, 'kit_int_charge': 1})),
    'SSf': ('q02', 'any', '副招·聚火 共享烬斩 0.5B 脉冲 + 9s 内 +2（不连放）', M(GATHER, SEC, {'kit_gather_secs': 9, 'kit_gather_mult': 0.5})),
    'SSf0': ('q02', 'any', '副招·聚火 共享烬斩 无伤 + 9s 内 +2（不连放）', M(GATHER, SEC, {'kit_gather_secs': 9})),
}
# S0 round 5: as round 4, but in boss fights the 守招 player HOLDS the charge for the next telegraph (no 烬斩 on the
# boss; trash rooms unchanged) — the honest use of a charge-sharing guard (round 4 never found the charge free)
CANDS5 = {
    'GCh': ('q03', 'any', '守招·壁垒 共享烬斩充能 首领战留给预警 2s −40%', M(GUARD, CHG)),
    'GChr60': ('q03', 'any', '守招·壁垒 共享烬斩充能 首领战留给预警 2s −60%', M(GUARD, CHG, {'kit_guard_red': 0.6})),
    'GChs1': ('q03', 'any', '守招·壁垒 共享烬斩充能 首领战留给预警 1s −60%', M(GUARD, CHG, {'kit_guard_red': 0.6, 'kit_guard_secs': 1})),
    'ICh': ('q07', 'any', '守招·破势 共享烬斩充能 首领战留给预警 p0.6', M(INT, {'kit_int_cd': 8, 'kit_int_charge': 1})),
}
# S0b (D212): 守招 without permanent survival — 醉拳 defer (total taken unchanged) and/or heavy-only (non-light telegraphs)
# so 守招 and 踏步 do not overlap. Prefer share with 身法 OR long CD.
DEFER = {'kit_guard_defer': 1, 'kit_guard_pay': 4.0, 'kit_guard_red': 0.0}  # red ignored when defer on
HEAVY = {'kit_guard_heavy': 1}
CANDS_S0B = {
    # round s0b-r1: pure defer (own CD) — does free spike-window alone blow clear rate?
    'GDf18': ('q03', 'any', '守招·醉拳 cd18 吸2s 摊4s', M(GUARD, DEFER, {'kit_guard_cd': 18})),
    'GDf30': ('q03', 'any', '守招·醉拳 cd30 吸2s 摊4s', M(GUARD, DEFER, {'kit_guard_cd': 30})),
    # defer + share 身法 (opportunity cost)
    'GDfS': ('q03', 'any', '守招·醉拳 共享身法 吸2s 摊4s', M(GUARD, DEFER, QSH, {'kit_guard_cd': 14})),
    'GDfSk50': ('q03', 'any', '守招·醉拳 共享身法 留50%预警 吸2s 摊4s', M(GUARD, DEFER, QSH, {'kit_guard_cd': 14, 'kit_q_keep': 0.5})),
    # heavy-only reduce (no defer) — only non-light telegraphs
    'GHv18': ('q03', 'any', '守招·重压壁垒 cd18 仅非轻招 −40%', M(GUARD, HEAVY, {'kit_guard_cd': 18})),
    'GHv30': ('q03', 'any', '守招·重压壁垒 cd30 仅非轻招 −40%', M(GUARD, HEAVY, {'kit_guard_cd': 30})),
    'GHvS': ('q03', 'any', '守招·重压壁垒 共享身法 仅非轻招 −40%', M(GUARD, HEAVY, QSH, {'kit_guard_cd': 14})),
    'GHvSk50': ('q03', 'any', '守招·重压壁垒 共享身法 留50% 仅非轻招 −40%', M(GUARD, HEAVY, QSH, {'kit_guard_cd': 14, 'kit_q_keep': 0.5})),
    # heavy-only + defer
    'GHvDf18': ('q03', 'any', '守招·重压醉拳 cd18 仅非轻招 吸2s 摊4s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18})),
    'GHvDfS': ('q03', 'any', '守招·重压醉拳 共享身法 仅非轻招 吸2s 摊4s', M(GUARD, HEAVY, DEFER, QSH, {'kit_guard_cd': 14})),
}
# S0b round 2: shorten absorb / one-hit / nobusy — probe r1: GHvDf18 closest (择优 ✅) but always-press −8～−10 from busy
CANDS_S0B2 = {
    'GHvDf1s': ('q03', 'any', '守招·重压醉拳 cd18 仅非轻招 吸1s 摊3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_secs': 1, 'kit_guard_pay': 3.0})),
    'GHvDf0': ('q03', 'any', '守招·重压醉拳 cd18 仅非轻招 一击摊4s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3})),
    'GHvDf0b0': ('q03', 'any', '守招·重压醉拳 cd18 仅非轻招 一击摊4s 无僵直', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_nobusy': 1})),
    'GHvDf24': ('q03', 'any', '守招·重压醉拳 cd24 仅非轻招 吸2s 摊4s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 24})),
    'GHvDf30': ('q03', 'any', '守招·重压醉拳 cd30 仅非轻招 吸2s 摊4s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 30})),
    'GHvDf18nb': ('q03', 'any', '守招·重压醉拳 cd18 仅非轻招 吸2s 摊4s 无僵直', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_nobusy': 1})),
    'GDf0': ('q03', 'any', '守招·醉拳 cd24 一击摊4s 僵直0.3s', M(GUARD, DEFER, {'kit_guard_cd': 24, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3})),
    'GDf0b0': ('q03', 'any', '守招·醉拳 cd24 一击摊4s 无僵直', M(GUARD, DEFER, {'kit_guard_cd': 24, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_nobusy': 1})),
    'GHvDf0cd30': ('q03', 'any', '守招·重压醉拳 cd30 仅非轻招 一击摊4s 僵直0.3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 30, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3})),
    'GHvDfSk75': ('q03', 'any', '守招·重压醉拳 共享身法 留75% 仅非轻招 一击摊4s', M(GUARD, HEAVY, DEFER, QSH, {'kit_guard_cd': 14, 'kit_q_keep': 0.75, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3})),
}
# S0b round 3: HP-gated press / payback tax / short busy — r2 d0.3 upward OK on GHvDf24/1s/30 but busy hurts always-press
CANDS_S0B3 = {
    'GHvDf24hp50': ('q03', 'any', '守招·重压醉拳 cd24 仅非轻招 吸2s 摊4s HP<50%才按', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 24, 'kit_guard_hp': 0.5})),
    'GHvDf24hp60': ('q03', 'any', '守招·重压醉拳 cd24 仅非轻招 吸2s 摊4s HP<60%才按', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 24, 'kit_guard_hp': 0.6})),
    'GHvDf18hp50': ('q03', 'any', '守招·重压醉拳 cd18 仅非轻招 吸2s 摊4s HP<50%才按', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_hp': 0.5})),
    'GHvDf0t15': ('q03', 'any', '守招·重压醉拳 cd18 一击摊4s 回敬1.15× 僵直0.3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3, 'kit_guard_pay_mult': 1.15})),
    'GHvDf0t25': ('q03', 'any', '守招·重压醉拳 cd18 一击摊4s 回敬1.25× 僵直0.3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3, 'kit_guard_pay_mult': 1.25})),
    'GHvDf0cd36': ('q03', 'any', '守招·重压醉拳 cd36 一击摊4s 僵直0.3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 36, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3})),
    'GHvDf24b05': ('q03', 'any', '守招·重压醉拳 cd24 吸2s 摊4s 僵直0.5s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 24, 'kit_guard_busy_secs': 0.5})),
    'GHvDf24b10': ('q03', 'any', '守招·重压醉拳 cd24 吸2s 摊4s 僵直1.0s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 24, 'kit_guard_busy_secs': 1.0})),
    'GDf24hp50': ('q03', 'any', '守招·醉拳 cd24 吸2s 摊4s HP<50%才按', M(GUARD, DEFER, {'kit_guard_cd': 24, 'kit_guard_hp': 0.5})),
    'GHvDf1shp50': ('q03', 'any', '守招·重压醉拳 cd18 吸1s 摊3s HP<50%才按', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_secs': 1, 'kit_guard_pay': 3.0, 'kit_guard_hp': 0.5})),
}
# S0b round 4: lethal-gated + confirm closest r2/r3 (GHvDf0cd36 / GHvDf24 / GHvDf1s)
CANDS_S0B4 = {
    # confirm aliases of r2 closest (unique ids; same mods)
    'GHvDf0cd36c': ('q03', 'any', '守招·重压醉拳 cd36 一击摊4s 僵直0.3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 36, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3})),
    'GHvDf24c': ('q03', 'any', '守招·重压醉拳 cd24 仅非轻招 吸2s 摊4s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 24})),
    'GHvDf1sc': ('q03', 'any', '守招·重压醉拳 cd18 仅非轻招 吸1s 摊3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_secs': 1, 'kit_guard_pay': 3.0})),
    'GHvDf0L': ('q03', 'any', '守招·重压醉拳 cd18 一击摊4s 濒死才按 僵直0.3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 18, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3, 'kit_guard_lethal': 0.35})),
    'GHvDf0Lcd30': ('q03', 'any', '守招·重压醉拳 cd30 一击摊4s 濒死才按 僵直0.3s', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 30, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3, 'kit_guard_lethal': 0.35})),
    'GHvDf24L': ('q03', 'any', '守招·重压醉拳 cd24 吸2s 摊4s 濒死才按', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 24, 'kit_guard_lethal': 0.35})),
    'GHvDf0Lcd36': ('q03', 'any', '守招·重压醉拳 cd36 一击摊4s 濒死才按', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 36, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3, 'kit_guard_lethal': 0.35})),
    'GHvDf0L25': ('q03', 'any', '守招·重压醉拳 cd24 一击摊4s 濒死<25%才按', M(GUARD, HEAVY, DEFER, {'kit_guard_cd': 24, 'kit_guard_secs': 0.05, 'kit_guard_pay': 4.0, 'kit_guard_busy_secs': 0.3, 'kit_guard_lethal': 0.25})),
}

# T0 guard-skill pivot (DESIGN-ember-guard-skill-pivot · 已批 A · 方案 M): 预警短窗招架
# Independent CD + single-hit nullify (A) / flat counter (B). No kit_guard_red / kit_q_shared / charge.
# land_p=1 = 最优时机（按在落地窗）；spam>0 = always-press 乱按耗 CD；从不按 = baseline.
PARRY_A = {'kit_guard_parry_cd': 24, 'kit_guard_parry': 1, 'kit_guard_parry_land_p': 1.0}
CANDS_PARRY = {
    'G_parry_A': ('q03', 'any', '守招·余烬招架 A cd24 窗≈0.45 一次无效·最优', dict(PARRY_A)),
    'G_parry_A2': ('q03', 'any', '守招·余烬招架 A2 cd28 稍紧·一次无效·最优',
                   {'kit_guard_parry_cd': 28, 'kit_guard_parry': 1, 'kit_guard_parry_land_p': 1.0}),
    'G_parry_A_ap': ('q03', 'any', '守招·余烬招架 A cd24 always-press 乱按',
                    {'kit_guard_parry_cd': 24, 'kit_guard_parry': 1, 'kit_guard_parry_land_p': 0.45,
                     'kit_guard_parry_spam': 0.12}),
    'G_parry_B': ('q03', 'any', '守招·余烬招架 B cd24 一次 flat×1.5B 反打·最优',
                 {'kit_guard_parry_cd': 24, 'kit_guard_parry_flat': 1.5, 'kit_guard_parry_land_p': 1.0}),
}
# T0b narrow (压 B / 弱化 A): flatter 0.6~0.8×B + 更长 CD; B2 更弱; A_weak=半伤非全抹
CANDS_PARRY_T0B = {
    'G2_parry_B': ('q03', 'any', 'T0b·招架 B cd27 flat×0.7B·最优',
                  {'kit_guard_parry_cd': 27, 'kit_guard_parry_flat': 0.7, 'kit_guard_parry_land_p': 1.0}),
    'G2_parry_B_ap': ('q03', 'any', 'T0b·招架 B cd27 flat×0.7B·乱按',
                     {'kit_guard_parry_cd': 27, 'kit_guard_parry_flat': 0.7, 'kit_guard_parry_land_p': 0.45,
                      'kit_guard_parry_spam': 0.12}),
    'G2_parry_B2': ('q03', 'any', 'T0b·招架 B2 cd28 flat×0.45B·最优',
                   {'kit_guard_parry_cd': 28, 'kit_guard_parry_flat': 0.45, 'kit_guard_parry_land_p': 1.0}),
    'G2_parry_B2_ap': ('q03', 'any', 'T0b·招架 B2 cd28 flat×0.45B·乱按',
                      {'kit_guard_parry_cd': 28, 'kit_guard_parry_flat': 0.45, 'kit_guard_parry_land_p': 0.45,
                       'kit_guard_parry_spam': 0.12}),
    'G2_parry_A_weak': ('q03', 'any', 'T0b·招架 A_weak cd27 预警半伤·最优',
                       {'kit_guard_parry_cd': 27, 'kit_guard_parry': 1, 'kit_guard_parry_keep': 0.5,
                        'kit_guard_parry_land_p': 1.0}),
    'G2_parry_A_weak_ap': ('q03', 'any', 'T0b·招架 A_weak cd27 预警半伤·乱按',
                          {'kit_guard_parry_cd': 27, 'kit_guard_parry': 1, 'kit_guard_parry_keep': 0.5,
                           'kit_guard_parry_land_p': 0.45, 'kit_guard_parry_spam': 0.12}),
}

# T0 ash-imprint pivot (DESIGN-ember-ash-imprint-pivot · 已批 A · 方案 R): independent CD + mark/defer
# New key family kit_ash_* — NOT old kit_mark_* cand decimals; NO kit_sec_shared / NO 烬斩 spend / NO dmg tax.
# A = Slow I mark window; B = one-shot next-hit defer; opt_win>0 = optimal (press when foe hits soon); ap = always-press.
ASH_A = {'kit_ash_cd': 15, 'kit_ash_mark_secs': 2.5, 'kit_ash_slow': 0.2, 'kit_ash_opt_win': 1.2}
CANDS_ASH_T0 = {
    'A_imprint_A': ('q02', 'any', '灰印·A cd15 标2.5s 缓+0.2·最优', dict(ASH_A)),
    'A_imprint_A_ap': ('q02', 'any', '灰印·A cd15 标2.5s 缓+0.2·乱按',
                      {'kit_ash_cd': 15, 'kit_ash_mark_secs': 2.5, 'kit_ash_slow': 0.2}),
    'A_imprint_A18': ('q02', 'any', '灰印·A18 cd18 标2.5s 缓+0.2·最优',
                     {'kit_ash_cd': 18, 'kit_ash_mark_secs': 2.5, 'kit_ash_slow': 0.2, 'kit_ash_opt_win': 1.2}),
    'A_imprint_B': ('q02', 'any', '灰印·B cd15 下击推迟0.4s·最优',
                   {'kit_ash_cd': 15, 'kit_ash_defer': 0.4, 'kit_ash_opt_win': 1.2}),
    'A_imprint_B_ap': ('q02', 'any', '灰印·B cd15 下击推迟0.4s·乱按',
                      {'kit_ash_cd': 15, 'kit_ash_defer': 0.4}),
    'A_imprint_B18': ('q02', 'any', '灰印·B18 cd18 下击推迟0.35s·最优',
                     {'kit_ash_cd': 18, 'kit_ash_defer': 0.35, 'kit_ash_opt_win': 1.2}),
}

# T0b narrow (设计 §4：换 CD/窗长/效用 A↔B 一轮；仍禁旧 mark / 永久伤税 / 放宽 ±2)
CANDS_ASH_T0B = {
    'A2_imprint_A': ('q02', 'any', 'T0b·灰印 A cd16 标2.0s 缓+0.15·最优',
                    {'kit_ash_cd': 16, 'kit_ash_mark_secs': 2.0, 'kit_ash_slow': 0.15, 'kit_ash_opt_win': 1.2}),
    'A2_imprint_A_ap': ('q02', 'any', 'T0b·灰印 A cd16 标2.0s 缓+0.15·乱按',
                       {'kit_ash_cd': 16, 'kit_ash_mark_secs': 2.0, 'kit_ash_slow': 0.15}),
    'A2_imprint_A18': ('q02', 'any', 'T0b·灰印 A18 cd18 标2.0s 缓+0.15·最优',
                      {'kit_ash_cd': 18, 'kit_ash_mark_secs': 2.0, 'kit_ash_slow': 0.15, 'kit_ash_opt_win': 1.2}),
    'A2_imprint_A18_ap': ('q02', 'any', 'T0b·灰印 A18 cd18 标2.0s 缓+0.15·乱按',
                         {'kit_ash_cd': 18, 'kit_ash_mark_secs': 2.0, 'kit_ash_slow': 0.15}),
    'A2_imprint_Aw': ('q02', 'any', 'T0b·灰印 Aw cd15 标2.0s 缓+0.12·最优',
                     {'kit_ash_cd': 15, 'kit_ash_mark_secs': 2.0, 'kit_ash_slow': 0.12, 'kit_ash_opt_win': 1.2}),
    'A2_imprint_B': ('q02', 'any', 'T0b·灰印 B cd18 下击推迟0.25s·最优',
                    {'kit_ash_cd': 18, 'kit_ash_defer': 0.25, 'kit_ash_opt_win': 1.2}),
    'A2_imprint_B_ap': ('q02', 'any', 'T0b·灰印 B cd18 下击推迟0.25s·乱按',
                       {'kit_ash_cd': 18, 'kit_ash_defer': 0.25}),
}

# D434 套装身法对称 S0（live 钉死 · 不调参）: 火痕 F14c0n1 对照 · 爆闪 SlowI 1.5s · 承护 ResistI 20%×2s
STEP_FIRE = M(STEP, {'kit_step_cost': 0, 'kit_step_n': 1, 'kit_step_set': 1})
STEP_BAO = {'kit_step_cd': 14, 'kit_step_cost': 0, 'kit_step_n': 1, 'kit_step_set': 2,
            'kit_step_mark_secs': 1.5, 'kit_step_slow': 0.2}
STEP_CHENG = {'kit_step_cd': 14, 'kit_step_cost': 0, 'kit_step_n': 1, 'kit_step_set': 3,
              'kit_step_resist': 0.20, 'kit_step_resist_secs': 2.0}
CANDS_D434 = {
    'F14c0n1_d434': ('q05', 'scorch', '身法·火痕步 对照（点1 无额外耗时）', STEP_FIRE),
    'B14c0': ('q05', 'burst', '身法·爆闪步 缓速I 1.5s', STEP_BAO),
    'S14c0': ('q05', 'sustain', '身法·承护步 抗性I 20%×2s', STEP_CHENG),
}
ALL = dict(CANDS, **CANDS2, **CANDS3, **CANDS4, **CANDS5, **CANDS_S0B, **CANDS_S0B2, **CANDS_S0B3, **CANDS_S0B4,
           **CANDS_PARRY, **CANDS_PARRY_T0B, **CANDS_ASH_T0, **CANDS_ASH_T0B, **CANDS_D434, **CANDS_D435)


def fams(c):
    return FAMS if c[1] == 'any' else (c[1],)


def cells():
    return mainline.cells()


def reachable(unlock, cell):
    m, k = cell[0][:3], cell[0][3]
    return k == 'c' or MAPS.index(m) > MAPS.index(unlock)


def _job(j):
    import builddiv as bd
    spec, c, d, n, sd = j
    r = bd.measure((spec, c, d, 'base', n, sd))
    sh = r['share']
    return {'clear': r['clear'], 'secs': r['secs'], 't_trash': r['t_trash'], 't_boss': r['t_boss'], 'taken': r['taken'],
            'active': sh.get('skill', 0.0) + sh.get('dash', 0.0), 'skill': sh.get('skill', 0.0), 'dash': sh.get('dash', 0.0),
            'burn': sh.get('burn', 0.0), 'potions': r['potions']}


def run(C, n, out):
    R = pickle.load(open(out, 'rb')) if os.path.exists(out) else {}
    jobs, keys = [], []
    for name, c in list(C.items()) + [('base', ('q01', 'any', 'baseline', None))]:
        for f in fams(c):
            for cl, d in cells():
                for sd in SEEDS:
                    k = (name, f, cl, d, sd)
                    if k in R:
                        continue
                    spec = {'set': f}
                    if c[3]:
                        spec['extra'] = c[3]
                    jobs.append((spec, cl, d, n, sd))
                    keys.append(k)
    if jobs:
        with Pool(NPROC) as p:
            res = p.map(_job, jobs, chunksize=4)
        R.update(dict(zip(keys, res)))
        R[('_meta',)] = dict(R.get(('_meta',), {}), **{k: v for k, v in C.items()})
        pickle.dump(R, open(out, 'wb'))
    print('wrote', out, len(R), 'new', len(jobs), file=sys.stderr)
    return R


def avg(R, name, f, cl, d, key):
    v = [R[(name, f, cl, d, s)][key] for s in SEEDS]
    v = [x for x in v if x is not None]
    return statistics.mean(v) if v else None


def rel(a, b):
    return None if a is None or b in (None, 0) else 100.0 * (a / b - 1.0)


def mean_ok(xs):
    xs = [x for x in xs if x is not None]
    return statistics.mean(xs) if xs else None


def report(R):
    meta = R.get(('_meta',), {})
    print('| 候选 | 套装 | 可达格 | ±2 内 | 最高 Δpp | 最低 Δpp | 平均 |Δ| | 判定 | 用时 Δ% | 杂兵房 Δ% | 首领 Δ% | 受伤 Δ% | 主动份额（基线） |')
    print('|---|---|---|---|---|---|---|---|---|---|---|---|---|')
    for name, c in meta.items():
        for f in fams(c):
            if (name, f, 'q01r', 0.3, SEEDS[0]) not in R:
                continue
            cs = [(cl, d) for cl, d in cells() if reachable(c[0], (cl, d))]
            dd = {x: 100 * (avg(R, name, f, x[0], x[1], 'clear') - avg(R, 'base', f, x[0], x[1], 'clear')) for x in cs}
            up = max(dd.items(), key=lambda x: x[1]); dn = min(dd.items(), key=lambda x: x[1])
            inside = sum(1 for v in dd.values() if abs(v) <= 2.0)
            mabs = statistics.mean(abs(v) for v in dd.values())
            ok = '✅' if inside == len(dd) else ('🟡' if mabs <= 1.0 and max(abs(up[1]), abs(dn[1])) <= 3.0 else '❌')
            tm = {k: mean_ok([rel(avg(R, name, f, x[0], x[1], k), avg(R, 'base', f, x[0], x[1], k)) for x in cs])
                  for k in ('secs', 't_trash', 't_boss', 'taken')}
            act = mean_ok([avg(R, name, f, x[0], x[1], 'active') for x in cs])
            act0 = mean_ok([avg(R, 'base', f, x[0], x[1], 'active') for x in cs])
            bo = {x: max(0.0, v) for x, v in dd.items()}  # optional tool: the player uses it only where it helps
            bmax = max(bo.items(), key=lambda x: x[1]); bmabs = statistics.mean(bo.values())
            bok = '✅' if all(v <= 2.0 for v in bo.values()) else ('🟡' if bmabs <= 1.0 and bmax[1] <= 3.0 else '❌')
            fm = lambda kv: '%+.1f（%s d%.1f）' % (kv[1], kv[0][0], kv[0][1])
            pf = lambda v: '—' if v is None else '%+.1f' % v
            print('| %s %s | %s | %d | %d/%d | %s | %s | %.2f | %s | %s | %s | %s | %s | %.1f%%（%.1f%%） |' % (
                name, c[2], f, len(cs), inside, len(dd), fm(up), fm(dn), mabs, ok, pf(tm['secs']), pf(tm['t_trash']),
                pf(tm['t_boss']), pf(tm['taken']), 100 * act, 100 * act0))
            if os.environ.get('SK_BEST'):
                print('| ↳ 择优（只在有利时用） | %s | %d | — | %s | — | %.2f | %s | | | | | |' % (f, len(cs), fm(bmax), bmabs, bok))


def main():
    cmd = sys.argv[1] if len(sys.argv) > 1 else 'list'
    if cmd == 'list':
        for k, c in CANDS.items():
            print(k, c[0], c[1], c[2], c[3])
        return
    if cmd == 'run':
        n = int(sys.argv[2]) if len(sys.argv) > 2 else 1500
        out = sys.argv[3] if len(sys.argv) > 3 else '/tmp/sk/run.pkl'
        ids = sys.argv[4].split(',') if len(sys.argv) > 4 else list({'2': CANDS2, '3': CANDS3, '4': CANDS4, '5': CANDS5, 's0b': CANDS_S0B, 's0b1': CANDS_S0B, 's0b2': CANDS_S0B2, 's0b3': CANDS_S0B3, 's0b4': CANDS_S0B4, 'parry': CANDS_PARRY, 'parry_t0b': CANDS_PARRY_T0B, 'ash': CANDS_ASH_T0, 'ash_t0': CANDS_ASH_T0, 'ash_t0b': CANDS_ASH_T0B, 'd434': CANDS_D434, 'd435': CANDS_D435}.get(os.environ.get('SK_ROUND'), CANDS))
        os.makedirs(os.path.dirname(out), exist_ok=True)
        run({i: ALL[i] for i in ids}, n, out)
        return
    if cmd == 'try':
        C = {k: tuple(v[:2]) + (k,) + (v[2],) for k, v in json.loads(sys.argv[2]).items()}
        n = int(sys.argv[3]) if len(sys.argv) > 3 else 1500
        out = sys.argv[4] if len(sys.argv) > 4 else '/tmp/sk/try.pkl'
        os.makedirs(os.path.dirname(out), exist_ok=True)
        run(C, n, out)
        return
    if cmd == 'report':
        R = {}
        for p in sys.argv[2:]:
            R.update(pickle.load(open(p, 'rb')))
        report(R)
        return
    print(__doc__)


if __name__ == '__main__':
    main()
