# 状态 · D408：挂机满额→短征可追（已批 A·M · 已落地 · ≠关观察 ≠抬日表 ≠sx08 ≠拆 D285）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-afk-full-short-chase-2026-10-10.md`](../design/DESIGN-ember-afk-full-short-chase-2026-10-10.md) @ tip `191b845f` · tip [`STATUS-ember-next-hard-debt-afk-full-short-chase-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-afk-full-short-chase-need-design-2026-10-10.md) · backlog `B-afk-full-short-chase`  
**裁决：** **已批 A · 批 M · D408 · 已落地** · TrMenu 同号 · **零 Java** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠开 gate_daily / R / K3** · **≠交 sx08** · **≠拆 D285 左键去冒险** · **≠动 Stage2 三开关** · **≠派测**

**toplevel：** `/workspace/minecraft` @ `main`

## 人话

挂机打满后，F 格写成「去冒险/短征」：左键仍进冒险（D285 钉死），右键进短征选本；lore 挂今日剩余有奖合计。体力不够右键只诚实提示需 30，不假开。Open / 战况 I 各加满额可追短征半行。不抬挂机产量。

## 落地对照（方案 M）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a 满额 F 双路径 | **PASS** | `ember_p1_afk` F · `afk_full>=1`：名「去冒险/短征」· lore 挂 `%corerpg_p1_sx_day_left_sum%` + 体力30 · **左**→`ember_p1_adventure` · **右**→`ember_p1_short` |
| W1b Open / I 半行 | **PASS** | Open「满后…也可追短征」· I「挂满可追短征 · 今日剩余有奖 %sx_day_left_sum%」（并列 remain_line，未删改） |
| W1c 体力不足诚实 | **PASS** | `afk_full>=1 and stamina<30` 优先态：右键 tell「短征需 30 体力 · 当前 N」· **不**开选页；≥30 才进选页 |
| W1d hub/adventure 复述 | **本窗不做** | D399 已齐 |
| W1e sx08 / 抬表 / 拆 D285 | **本窗不做** | 硬禁 |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_afk.yml` | F 满额双路径 + 体力&lt;30 诚实；Open/I 满额半行 |
| DESIGN / tip / backlog | 勾批 A·M · **已落地** |
| 本 STATUS | 菜单侧结案 |

## 验收（本号 · 菜单面）

| ID | 结果 | 备注 |
|----|------|------|
| V1 未满 F | **PASS（菜单）** | 未满仍自动战斗开/关；无强制短征主钮 |
| V2 满额双路径 | **PASS（菜单）** | 左冒险 / 右短征；lore 含 sx_day_left_sum |
| V3 体力&lt;30 | **PASS（菜单）** | 右键 tell 需30 · 不开选页 |
| V4 Open/I | **PASS** | 满额短征半行；remain_line 保留 |
| V5 零改表 | **PASS（纪律）** | 未 stage Java / afk.tiers / daily_kills / Stage2 / gate_daily |
| V6 D285 回归 | **PASS（菜单）** | 满额左键仍进冒险 |
| trmenu reload | **PASS** | `良好 | 73 个菜单已加载 (118 ms)`（**07:09:39 CST**）|

## 不动

- Java / afk 产量 / daily_kills / tiers / Stage2 / gate_daily / jar
- sx08；拆 D285 左键；假开旧日常
- **本号不派测**（派单钉死）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

## 热更旁注

- play：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 73 个菜单已加载 (118 ms)`（**2026-10-10 07:09:39 CST**）
