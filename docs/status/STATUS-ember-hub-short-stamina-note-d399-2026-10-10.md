# 状态 · D399：枢纽短征体力旁注（已批 A·M · 已落地 · ≠关观察 ≠抬日表 ≠sx05）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-hub-short-stamina-note-2026-10-10.md`](../design/DESIGN-ember-hub-short-stamina-note-2026-10-10.md) @ tip `7f61be97` · tip [`STATUS-ember-next-hard-debt-hub-short-stamina-note-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-hub-short-stamina-note-need-design-2026-10-10.md) · backlog `B-hub-short-stamina-note`  
**裁决：** **已批 A · 批 M · D399 · 已落地** · 总控采纳方案 M · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠改 stamina / route_pri** · **≠开 gate_daily / R / K3** · **≠交 sx05** · **≠动 Stage2 三开关**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

打开枢纽「今天该打哪」：体力行写明「短征也是 30」；有体力 / 挂机满时「今日可追」能看见短征与日帽合计；默认右键可进短征选本。体力不够时不假推短征进本，仍挂机/生活。不抬挂机表、不开旧日常、不交第五本。

## 落地对照（方案 M）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a 体力行旁注 | **PASS** | hub 格 H 三分支：`主线30 / 短征30 / 深渊30 / 团本50` |
| W1b 今日可追短征行 | **PASS** | 默认/挂机满挂 `%corerpg_p1_sx_day_left_sum%`；体力&lt;30 写「短征需30 · 先挂机/补给」；生活·工坊并列保留 |
| W1c 互指/轻跳 | **PASS** | 默认右键→`ember_p1_short`；挂机满 Shift+左键→短征选本、右键仍工坊；体力不足**无**进本跳（左挂机/右生活） |
| W1d 假平面禁 | **PASS** | 无旧日常窟；无 sx05；无「短征不耗体力」；Open tell 短征半行保留 |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_hub.yml` | 格 H 三分支 lore/轻跳 |
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已落地** |
| 本 STATUS | 施工结案 |

## 验收（本号）

| ID | 结果 | 备注 |
|----|------|------|
| V1 体力行含短征30 | **PASS（菜单）** | 三分支 |
| V2 今日可追含短征 | **PASS（菜单）** | 默认/挂机满 + day_left_sum |
| V3 体力不足无假开 | **PASS（菜单）** | 无短征进本跳 |
| V4 轻跳 | **PASS（菜单）** | 默认右键 / 挂机满 Shift+左键 → `ember_p1_short` |
| V5 文案 | **PASS** | 无旧日常 / sx05 / 不耗体力暗示 |
| V6 配置 | **PASS（纪律）** | 未改 stamina / route_pri / afk / Stage2 / gate_daily |
| trmenu reload | **PASS** | `scripts/console.sh play "trmenu reload"` → `良好 | 72 个菜单已加载 (94 ms)`（**06:13:55 CST**）；RegisterCommands `updateCommands` WARN 为 1.12 已知噪点，菜单已加载 |

## 不动

- stamina 数值 / route_pri 主逻辑 / afk.tiers / daily_kills / Stage2 / gate_daily / jar / sx05  
- 本号**不派测**（测试另派）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

## 热更旁注

- play：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 72 个菜单已加载 (94 ms)`（**2026-10-10 06:13:55 CST**）
