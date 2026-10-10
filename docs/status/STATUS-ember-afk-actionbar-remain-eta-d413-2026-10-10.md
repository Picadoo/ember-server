# 状态 · D413：挂机 ActionBar 叠 remain + 满额短征追（批 A·M · W1a+W1b）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN tip [`17cb56d0`](../design/DESIGN-ember-afk-actionbar-remain-eta-2026-10-10.md)  
**裁决：** **本号交** 未满 ActionBar 叠 remain_line + 满额 ActionBar 短征半句 + 单测 + 装 play · **W1c Open 菜单另号** · **未动** daily_kills / afk.tiers / Stage2 三开关 / gate_daily / ×0.97 · **≠sx10**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| W1a 未满 ActionBar | `fightActionBar` 末尾 ` · ` + `remainLine`（D405 口径）；保留层名/今日/kph/阵亡 | — |
| W1b 满额 ActionBar | `capActionBar(daily, shortLeft, stamina)`；`sx_day_left_sum`>0 且 stamina≥30 →「短征还可追 · 剩余有奖 N」；stamina&lt;30 →「短征需30体力」；sum=0 不叠 | — |
| 刷新 | 仍 ticks%40（~2s） | — |
| W1c Open tell | — | TrMenu 可选另号 |

## 不动

daily_kills / afk.tiers / 离线% · Stage2 三开关 / ×0.97 · gate_daily · K3 · sx10 · 菜单 remain 主块重开

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-W1a | 有 kph / 无 kph / 含阵亡尾 | EmberAfkServiceTest.actionBarRemain_D413 |
| V-W1b | sum=0 / 可追 / 需30；「去冒险」保留；无「保证分钟满」 | 同上 |
| V-cfg | daily_kills 仍 2400 | 同上 |
| V-play | jar 装 play · Enabling | 见 tip/jar |

## tip / jar

| 项 | 值 |
|----|-----|
| tip | `cd9a19f2` |
| jar | `CoreRpg-1.65.120-d413.local.jar` |
| sha256 | `01dee759375f51635e2a51a77bfde66d0997650100cab527ea296437de4c680d` |
| Enabling | `CoreRpg v1.65.120-d413.local` |

## 下一号

D414 sx10 键+S49（同批顺序）；W1c Open 可选另号。
