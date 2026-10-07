# T0 · 守招换机制（预警短窗招架）报告

**日期：** 2026-10-07（Asia/Shanghai）  
**规格：** [`DESIGN-ember-guard-skill-pivot-2026-10-07.md`](../design/DESIGN-ember-guard-skill-pivot-2026-10-07.md)（已批 A · 方案 M · `64d162d9`）  
**结论：❌** — `G_parry_A` / `G_parry_A2` / `G_parry_A_ap` / `G_parry_B` × 三套 × 可达 33 **未**全部 ∈ ±2pp（多格 |Δ|>3）。**不开 T1。** 主屏 |Δ| 峰 **+19.0**（G_parry_A · 炽愈 · q02c d0.3）。线上 jar/yml **未改**。

> 临时登记招架 cand（**未**覆写旧 `G*`/`GHv*`/`GDf*` 壁垒表）；**无** `kit_guard_red` 均匀乘 / `kit_q_shared` / `kit_guard_charge`；**不**放宽 ±2；**不**夹带天赋。

## 0. 键与跑法

| id | 行为 | mods |
|---|---|---|
| G_parry_A | **最优时机**（落地窗内按） | `kit_guard_parry_cd 24` / `kit_guard_parry 1` / `land_p 1.0` · 成功=该次预警伤无效 |
| G_parry_A2 | 最优 · 稍长 CD | `cd 28` / 同 A |
| G_parry_A_ap | **always-press 乱按** | `cd 24` / `land_p 0.45` / `spam 0.12`（窗外耗 CD） |
| G_parry_B | 最优 · 效用 B | `cd 24` / `kit_guard_parry_flat 1.5`（×B 反打；预警伤仍落地） |
| （从不按） | 基线 | 无守招 mods |

- 窗长意向 `parry_win≈0.45s`：离散 sim 以「预警落地当帧按=窗内」建模；乱按用 `land_p`+`spam` 耗独立 CD。  
- rules：`rules sha256 d80a38151fdd6a23 (balance_version 60, repo, unmodelled keys: reroll_coin)`  
- 导出：`tools/p1sim/out-guard-parry-t0-rules-export.json`  
- `skillkit.py run 1500` · seeds 7,11 · 可达 33（q03 解锁惯例）· `SK_BEST` 择优行  
- 表：`tools/p1sim/out-guard-parry-t0.md`

## 1. 总表（可达 33 · ±2pp · always-press/最优 即表内主行）

| 候选 | 套 | 33 ±2 | 最高/最低 Δpp | |Δ|>3? | 择优最高 | 判定 |
|---|---|---|---|---|---|---|
| G_parry_A（最优·A） | 烬爆 | 15/33 | +13.6 / +0.0 | 是 | +13.6 | ❌ |
| G_parry_A | 焚烬 | 15/33 | +15.7 / +0.0 | 是 | +15.7 | ❌ |
| G_parry_A | 炽愈 | 14/33 | +19.0 / +0.0 | 是 | +19.0 | ❌ |
| G_parry_A2（最优·A 紧） | 烬爆 | 16/33 | +13.4 / +0.0 | 是 | +13.4 | ❌ |
| G_parry_A2 | 焚烬 | 15/33 | +14.1 / +0.0 | 是 | +14.1 | ❌ |
| G_parry_A2 | 炽愈 | 15/33 | +17.7 / +0.0 | 是 | +17.7 | ❌ |
| G_parry_A_ap（乱按） | 烬爆 | 28/33 | +9.0 / +0.0 | 是 | +9.0 | ❌ |
| G_parry_A_ap | 焚烬 | 30/33 | +5.0 / +0.0 | 是 | +5.0 | ❌ |
| G_parry_A_ap | 炽愈 | 28/33 | +4.8 / +0.0 | 是 | +4.8 | ❌ |
| G_parry_B（最优·B flat） | 烬爆 | 31/33 | +2.8 / −0.9 | 否 | +2.8 | 🟡 |
| G_parry_B | 焚烬 | 31/33 | +3.6 / −0.6 | 是 | +3.6 | ❌ |
| G_parry_B | 炽愈 | 30/33 | +3.3 / −0.9 | 是 | +3.3 | ❌ |

**从不按** = 基线（Δ=0）。A 族下沿贴 0 → **无代价净赚**；择优 ≡ 主行（乱按也不亏通关）。

## 2. 读数

- **A（一次无效）：** 预警命中直接抹掉 = 低操作格（d0.3）白送通关（峰 **+19**）。受伤仅 −1～−1.6%，用时≈0 → 典型「生存白嫖」。`cd 24→28` 几乎不救。  
- **A_ap（乱按）：** `spam`+低 `land_p` 把峰压到 +5～+9，仍多格 |Δ|>3，且下沿仍 ≥0 → **错开窗也不亏通关**（择优不成立）。  
- **B（flat×1.5B）：** 最近轴（烬爆 🟡 31/33 · 峰 +2.8）；焚烬/炽愈仍触 |Δ|>3（+3.6/+3.3）。反打加快击杀、预警仍吃满 → 形态比 A 健康，但本档幅度仍出廊。  
- **总形：** 与旧壁垒同构失败——**效用过强 / 无负向代价** → 低操作格冲廊；乱按耗 CD **不足以**制造「按错亏通关」。  
- **禁项自检：** 未动旧 `kit_guard_red`/共享身法/花充能表；未放宽 ±2；未改线上 jar；未夹带天赋；未声称 T1。

## 3. 结论与下一轮（仍禁旧壁垒表）

- **T0 结论：❌** — **禁开 T1**；**不放宽 ±2**。  
- **下一轮允许（设计 §4.3）：** 换窗长 / CD / 效用 A↔B（例：B 降 flat→0.8～1.0B；A 加短僵直或仅非轻招；更长 CD）——**仍禁**重交 `kit_guard_red`/共享身法/花充能旧表。  
- **两轮后再议 R**（搁置守招改灰印等横向）；本轮不落 R。  
- 连续失败则 HOLD，不改线上 jar。

## 4. 产物

| 文件 | 用途 |
|---|---|
| 本文件 / `tools/p1sim/out-guard-parry-t0.md` | 结论 |
| `tools/p1sim/out-guard-parry-t0-rules-export.json` | 规则快照 |
| `p1sim.py` 招架键 + `skillkit.py` CANDS_PARRY | 离线建模（非线上） |
| `/tmp/sk/parry-t0.pkl` | 原始（未入库） |

