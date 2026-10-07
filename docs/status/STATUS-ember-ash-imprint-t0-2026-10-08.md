# T0 · 灰印副招换机制（独立 CD + 标记/短推迟）报告

**日期：** 2026-10-08（Asia/Shanghai）  
**规格：** [`DESIGN-ember-ash-imprint-pivot-2026-10-08.md`](../design/DESIGN-ember-ash-imprint-pivot-2026-10-08.md)（已批 A · 方案 R · `dbf77b64`）  
**结论：❌** — 新键族 `kit_ash_*` × 三套 × 全量 **42** **未**全部 ∈ ±2pp。效用 A（Slow 标记窗）烬爆峰 **+2.4～+2.7**（无格 |Δ|>3）；效用 B（下击推迟）峰 **+3.5～+4.9**（多格 |Δ|>3）。**不开 T1。** 线上 jar/yml **未改**。

> 临时登记灰印 cand（**新** `kit_ash_cd` / `mark_secs` / `slow` / `defer` / `opt_win`；**未**照搬旧 `kit_mark_*` cand 小数；**无** `kit_sec_shared` / 花烬斩 / 永久伤税 / 易伤% / *d）；**不**放宽 ±2；**不**夹带天赋/守招/Pack6。按设计 §4 允许 **一轮** T0b → 见 [`STATUS-ember-ash-imprint-t0b-2026-10-08.md`](STATUS-ember-ash-imprint-t0b-2026-10-08.md)。

## 0. 键与跑法

| id | 行为 | mods |
|---|---|---|
| A_imprint_A | **最优** · 效用 A | `kit_ash_cd 15` / `mark_secs 2.5` / `slow 0.2` / `opt_win 1.2` |
| A_imprint_A_ap | **乱按** · A | 同 A · 无 opt（CD 好就按） |
| A_imprint_A18 | **最优** · A 稍长 CD | `cd 18` / 同窗 |
| A_imprint_B | **最优** · 效用 B | `cd 15` / `kit_ash_defer 0.4` / `opt_win 1.2` |
| A_imprint_B_ap | **乱按** · B | `cd 15` / `defer 0.4` |
| A_imprint_B18 | **最优** · B 紧 | `cd 18` / `defer 0.35` / `opt_win 1.2` |
| （从不按） | 基线 | 无灰印 mods |

- Slow 窗：标记期间每次敌攻间隔 +`slow`（缓慢 I 近似）；B：下一次攻击一次性 +`defer`。  
- rules：`rules sha256 d80a38151fdd6a23 (balance_version 60, repo, unmodelled keys: reroll_coin)`  
- 导出：`tools/p1sim/out-ash-imprint-t0-rules-export.json`  
- `skillkit.py run 1500` · seeds **7,11** · `SK_ROUND=ash_t0` · 可达 36（q02）+ 全量 42 · `SK_BEST` 择优行  
- 表：`tools/p1sim/out-ash-imprint-t0.md`

## 1. 总表（全量 42 · ±2pp）

| 候选 | 套 | 42 ±2 | 最高/最低 Δpp | \|Δ\|>3? | 择优最高 | 判定 |
|---|---|---|---|---|---|---|
| A_imprint_A（最优·Slow） | 烬爆 | 39/42 | +2.4 / −0.1 | 否 | +2.4 | ❌ |
| A_imprint_A | 焚烬 | 42/42 | +1.7 / −0.5 | 否 | +1.7 | ✅ |
| A_imprint_A | 炽愈 | 42/42 | +1.7 / −0.4 | 否 | +1.7 | ✅ |
| A_imprint_A_ap（乱按） | 烬爆 | 41/42 | +2.7 / −0.0 | 否 | +2.7 | 🟡 |
| A_imprint_A_ap | 焚烬/炽愈 | 42/42 | ≤+1.6 | 否 | ≤+1.6 | ✅ |
| A_imprint_A18 | 烬爆 | 39/42 | +2.5 / −0.1 | 否 | +2.5 | ❌ |
| A_imprint_A18 | 焚烬/炽愈 | 41/42 | ≤+2.2 | 否 | ≤+2.2 | 🟡 |
| A_imprint_B（最优·defer） | 烬爆 | 33/42 | +4.0 / −0.1 | **是** | +4.0 | ❌ |
| A_imprint_B | 焚烬 | 35/42 | +4.9 / −0.1 | **是** | +4.9 | ❌ |
| A_imprint_B | 炽愈 | 36/42 | +3.9 / −0.0 | **是** | +3.9 | ❌ |
| A_imprint_B_ap | 三套 | 33～36/42 | +3.4～+3.9 | **是** | ≤+3.9 | ❌ |
| A_imprint_B18 | 烬爆 | 34/42 | +3.5 / −0.0 | **是** | +3.5 | ❌ |
| A_imprint_B18 | 焚烬/炽愈 | 40～41/42 | ≤+2.7 | 否 | ≤+2.7 | 🟡 |

**从不按** = 基线（Δ=0）。择优 vs 从不按：A/B 峰均 >0 → **择优非 0**；但 A 族乱按 ≈ 最优（择优时机差不显著），B 族低操作格白送。

## 2. 读数

- **A（Slow 标记窗）：** 最近轴；烬爆粘格（q05r/q06r d0.3、q07c d0.5）出廊 +2.4～+2.7，**无** |Δ|>3。cd15→18 几乎不救粘格。受伤约 −0.3～−0.4%。  
- **B（下击推迟）：** 同构旧 `kit_mark` 失败形——低操作格白送（峰 **+4.9**），多格 |Δ|>3。  
- **总形：** 独立 CD 新键族可建模；A 形窄幅近线、B 形结构性过强。  
- **禁项自检：** 未用旧 `kit_mark_*` cand；未挂 `kit_sec_shared`/花烬斩；未永久伤税/*d；未放宽 ±2；未改线上 jar；未夹带天赋/守招/Pack6；未声称 T1。

## 3. 结论与下一轮

- **T0 结论：❌** — **禁开 T1**；**不放宽 ±2**。  
- **下一轮（设计 §4 允许一轮）：** 换 CD/窗长/效用 A↔B → T0b（见姊妹 STATUS）。  
- 产物见 §4。

## 4. 产物

| 文件 | 用途 |
|---|---|
| 本文件 / `tools/p1sim/out-ash-imprint-t0.md` | 结论 |
| `tools/p1sim/out-ash-imprint-t0-rules-export.json` | 规则快照 |
| `p1sim.py` `kit_ash_*` + `skillkit.py` `CANDS_ASH_T0` | 离线建模（非线上） |
| `/tmp/sk/ash-t0.pkl` | 原始（未入库） |
