# STATUS · TIP-CH W1a 冒险「今日三冲」聚合 PAPI · D13313

> 2026-10-14 00:01 Asia/Shanghai · 余烬-插件岗

## 交付
- `%corerpg_p1_today_triple%` / `_name` / `_kind`（today|week|event|empty）/ `_ok`（非空→1；TrMenu 灰态单键）
- `/corerpg p1 enter today-triple`（及 `today_triple` / `triple-chase`）→ 内部转到 short-today → short-goal → event-chase；全空诚实拒
- 优先序钉死：今日推有奖（ShortPath 解锁 + day_left>0）→ 周目标未满且 chase_ok → 事件 chase_ok → 空
- 单测：`EmberTodayTripleTest` 优先今日推 / 次回周 / 再事件 / 全空 / ok·kind 边界；`EmberRunPapiTest` 路由四键
- jar：`1.65.810-d13313.local` · sha256 `ccd19244e0ad…` · ECONOMY_BV **123** 未动 · **零改** 日帽/short:5/徽/event_rate/NEED/addGoal/targets/SoftPity
- play：live `1.65.810-d13313.local` · storage=mysql · bv123 · MySQL connected

## 键清单
| 键 | 口径 |
|----|------|
| `today_triple` | 智路由解析后的 mapId（优先序见上；皆无→空） |
| `today_triple_name` | 推荐本显示名（无→空） |
| `today_triple_kind` | today\|week\|event\|empty |
| `today_triple_ok` | 非空→1 否则 0（TrMenu 单条件灰） |
| enter `today-triple` | 转别名 short-today / short-goal / event-chase 后同既有门禁 |

## 落点
- `EmberTodayTriple.pick` / `ok` / `Pick`
- `EmberRunPapi.route` + `shortDay` → `todayTriple`（SHORT 节；复用 ShortPath.unlocked / GoalShortChase / EventChase）
- `EmberRunService` case enter 别名（先解析再 fall-through 三别名）

## 零改边界
- weekly_goals.targets / addGoal / 徽表 / short:5
- 短征日帽 / SoftPity / event_rate / NEED / Pack
- balance_version 123 / afk.tiers / daily_kills / Stage2
- 菜单（物品 W1b/c 另派）· 不撤 H/E/F

## 下步
- 物品 W1b/c（冒险 P 格 + Open 半行）等键
