# 状态 · D409：短征有奖→工坊可追 · 菜单侧 W1b/W1c（+W1d）（已批 A·M · 菜单已落地 · ≠关观察 ≠抬日表 ≠sx08 ≠改 Java）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-short-reward-spend-chase-2026-10-10.md`](../design/DESIGN-ember-short-reward-spend-chase-2026-10-10.md) @ tip `9a22f1a0` · tip [`STATUS-ember-next-hard-debt-short-reward-spend-chase-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-reward-spend-chase-need-design-2026-10-10.md) · backlog `B-short-reward-spend-chase`  
**裁决：** **已批 A · 批 M · D409 · 菜单侧已落地** · TrMenu 同号 · **零 Java（W1a 插件另做）** · **含 W1d** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠开 gate_daily / R / K3** · **≠交 sx08** · **≠改 S40–S46 / 日帽 / 体力** · **≠动 Stage2 三开关** · **≠派测**

**toplevel：** `/workspace/minecraft` @ `main`

## 人话

今日短征有奖合计打满后，打开选本页会直接说「今日有奖已满 · 去工坊花」；说明格与「去哪花」换成满态 CTA，点「去哪花」仍进工坊。体力还在时，「去哪花」右键可进挂机庭，不挡左键工坊主路。未满样子与现网一致。结算半句（W1a）仍等插件。

## 落地对照（方案 M · 菜单窗）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a 有奖结算半句 | **本号不做** | Java 薄改 · 插件另号 |
| W1b 日帽满 Open/I | **PASS** | Open：`sx_day_left_sum<=0` → 满态 tell（deny=未满现网句）；I icons 满态块放大同义 |
| W1c 满态「去哪花」B | **PASS** | `sx_day_left_sum<=0`：名/lore「今日有奖已满 · 去工坊花材料」· **左/all**→`ember_p1_forge`；未满保持现网静态 |
| W1d 满态可挂机半行 | **PASS（同批）** | 满态 + `stamina>0`：I 半行「体力还在可挂机」；B **右键**→`ember_p1_afk`（左仍工坊）；体力尽则诚实「先回满」· **不**盖工坊主 CTA |
| W1e sx08 / 仓差复述等 | **本窗不做** | 硬禁 |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | Open 条件满态；I/B icons 满态；W1d 右键挂机 |
| DESIGN / tip / backlog | 勾批 A·M · **菜单侧已落地**（W1a 仍施工/另号） |
| 本 STATUS | 菜单侧结案 |

## 验收（本号 · 菜单面）

| ID | 结果 | 备注 |
|----|------|------|
| V3 `sx_day_left_sum==0` | **PASS（菜单）** | Open/I 满态；B 满态 lore；左键仍进 forge |
| V4 `sx_day_left_sum>0` | **PASS（菜单）** | 未满 Open/I/B 保持现网；不误显「今日有奖已满」 |
| V5 工坊内 | **PASS（纪律）** | 未改 forge 价表/规则 |
| V6 回归 | **PASS（纪律）** | 零改 daily_kills/tiers/S40–S46/日帽/体力30；无手打指令教学 |
| W1d | **PASS（菜单）** | 满+体力>0 右键挂机；不盖左键工坊 |
| trmenu reload | **PASS** | `良好 | 73 个菜单已加载 (134 ms)`（**07:18:16 CST**）|

## 不动

- Java / W1a settle 半句（插件另做）
- afk 产量 / daily_kills / tiers / Stage2 / gate_daily / jar / ×0.97
- sx08；假开旧日常；改 S40–S46 / 日帽 / 体力
- **本号不派测**（派单钉死）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

## 热更旁注

- play：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 73 个菜单已加载 (134 ms)`（**2026-10-10 07:18:16 CST**）· 旁注：1.12 `updateCommands` NoSuchMethodError 已知无害

---

*D409 菜单 · W1b/W1c+W1d · 菜单侧已落地 · W1a 插件另做 · ≠关观察 ≠抬日表 ≠sx08。*
