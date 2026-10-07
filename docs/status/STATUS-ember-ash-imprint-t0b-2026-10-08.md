# T0b · 灰印收窄一轮（独立 CD + 标记/短推迟）报告

**日期：** 2026-10-08（Asia/Shanghai）  
**规格：** [`DESIGN-ember-ash-imprint-pivot-2026-10-08.md`](../design/DESIGN-ember-ash-imprint-pivot-2026-10-08.md)（已批 A · 方案 R · §4 允许换 CD/窗长/A↔B **一轮**）  
**上游：** T0 ❌ [`STATUS-ember-ash-imprint-t0-2026-10-08.md`](STATUS-ember-ash-imprint-t0-2026-10-08.md)（A 峰+2.4～+2.7；B |Δ|>3）  
**结论：❌** — 收窄后仍 **无** 候选 × 三套 × 全量 **42** 全进 ±2pp。最近 **`A2_imprint_A18_ap`**（cd18 · 标2.0s · 缓+0.15 · 乱按）烬爆 **41/42** 峰 **+2.03**（仅 q07c d0.5 出廊）；其余 A/B 变体烬爆仍漏 ≥1 格。**无格 |Δ|>3。****不开 T1 · HOLD。** 线上 jar/yml **未改**。**不开 T0c**（§4 只允一轮）。

> 临时登记 A2_*（**未**回潮旧 `kit_mark_*`；**未**放宽 ±2；**未**永久伤税/*d / 共享充能）。

## 0. 键与跑法

| id | 行为 | mods |
|---|---|---|
| A2_imprint_A | **最优** · 压 A | `cd 16` / `mark 2.0` / `slow 0.15` / `opt 1.2` |
| A2_imprint_A_ap | **乱按** | 同 A · 无 opt |
| A2_imprint_A18 | **最优** · 更长 CD | `cd 18` / 同窗 |
| A2_imprint_A18_ap | **乱按** | `cd 18` / 同窗 · 无 opt |
| A2_imprint_Aw | **最优** · 更弱 slow | `cd 15` / `mark 2.0` / `slow 0.12` / `opt` |
| A2_imprint_B | **最优** · 压 B | `cd 18` / `defer 0.25` / `opt` |
| A2_imprint_B_ap | **乱按** | `cd 18` / `defer 0.25` |
| （从不按） | 基线 | 无灰印 mods |

- rules：`rules sha256 d80a38151fdd6a23 (balance_version 60, repo, unmodelled keys: reroll_coin)`  
- 导出：`tools/p1sim/out-ash-imprint-t0b-rules-export.json`  
- `skillkit.py run 1500` · seeds **7,11** · `SK_ROUND=ash_t0b` · 全量 42  
- 表：`tools/p1sim/out-ash-imprint-t0b.md`

## 1. 总表（全量 42 · ±2pp）

| 候选 | 套 | 42 ±2 | 峰/底 | \|Δ\|>3? | 判定 |
|---|---|---|---|---|---|
| A2_imprint_A | 烬爆 | 39/42 | +2.4 / −0.2 | 否 | ❌ |
| A2_imprint_A | 焚烬/炽愈 | 42/42 | ≤+1.9 | 否 | ✅ |
| A2_imprint_A_ap | 烬爆 | 41/42 | +2.5 / −0.1 | 否 | 🟡 |
| A2_imprint_A_ap | 焚烬/炽愈 | 42/42 | ≤+1.7 | 否 | ✅ |
| A2_imprint_A18 | 烬爆 | 39/42 | +2.5 / −0.3 | 否 | ❌ |
| A2_imprint_A18 | 焚烬/炽愈 | 41/42 | ≤+2.2 | 否 | 🟡 |
| **A2_imprint_A18_ap** | **烬爆** | **41/42** | **+2.03 / −0.1** | **否** | **🟡（最近）** |
| A2_imprint_A18_ap | 焚烬/炽愈 | 42/42 | ≤+1.2 | 否 | ✅ |
| A2_imprint_Aw | 烬爆 | 39/42 | +2.4 / −0.3 | 否 | ❌ |
| A2_imprint_Aw | 焚烬/炽愈 | 42/42 | ≤+1.7 | 否 | ✅ |
| A2_imprint_B / B_ap | 烬爆 | 37/42 | +2.5～+2.8 | 否 | ❌ |
| A2_imprint_B / B_ap | 焚烬/炽愈 | 41/42 | ≤+2.5 | 否 | 🟡 |

## 2. 读数

- **压 A（短窗/弱 slow/长 CD）：** 粘格仍在（烬爆 q05r/q06r d0.3、q07c d0.5）；幅度从 T0 的 +2.7 压到最近 **+2.03**，仍差一格。  
- **压 B（defer 0.25 · cd18）：** 已去掉 |Δ|>3，但仍多格出 ±2；非过线形。  
- **择优：** vs 从不按峰 >0（非 0）；乱按与最优通关差小 → 「时机决策」可感偏弱（控效用本身轻）。  
- **禁项自检：** 未放宽 ±2；未开 T0c；未改线上 jar；未旧 mark / 永久伤税 / 共享充能；未声称 T1。

## 3. 结论与下一窗

> **下一档硬债 tip（已开）：** [`STATUS-ember-next-hard-debt-weekly-raid-feel-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-weekly-raid-feel-need-design-2026-10-08.md) · 选题 **G** 团本/周本入口+本间段感 · **需策划** · 非灰印续跑。


- **T0b 结论：❌** — **禁开 T1**；**HOLD**（设计 §4：再 ❌ → HOLD 或改派；**不**再开 T0c / **不**放宽 ±2）。  
- **给总控：** 独立 CD + 纯控/标记在现行 p1sim 廊内夹不住「烬爆粘格」与「可感择优」同时过线；若续做须**改派新形状**（非本窗小数再拧），或接受 HOLD 直到邻域模型/门禁策略另议。  
- **仍禁：** 旧 `kit_mark_*` cand、永久伤税、*d、共享充能灰印、Pack6、六槽、守招再调、天赋。

## 4. 产物

| 文件 | 用途 |
|---|---|
| 本文件 / `tools/p1sim/out-ash-imprint-t0b.md` | 结论 |
| `tools/p1sim/out-ash-imprint-t0b-rules-export.json` | 规则快照 |
| `skillkit.py` `CANDS_ASH_T0B` | 离线建模 |
| `/tmp/sk/ash-t0b.pkl` | 原始（未入库） |
