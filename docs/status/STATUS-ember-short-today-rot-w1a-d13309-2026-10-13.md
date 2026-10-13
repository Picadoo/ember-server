# STATUS · TIP-CD W1a 短征今日推轮换 PAPI · D13309

> 2026-10-13 Asia/Shanghai · 余烬-插件岗

## 交付
- `%corerpg_p1_sx_today%` / `_name` / `_day_line`（Asia/Shanghai · floorMod epochDay,19 · idx0=sx01）
- `/corerpg p1 enter short-today`（及 `sx_today`）→ 当日 key → tryEnter
- 单测：`EmberShortTodayRotTest` mod 0/18/19 + 同日稳定；`EmberRunPapiTest` 路由三键
- jar：`1.65.805-d13309.local` · ECONOMY_BV **123** 未动 · 零改 claim/日帽/S表/体力/afk.tiers/Stage2

## 落点
- `EmberShortRules.todayKey(long|())`
- `EmberRunPapi.isShortDayKey` + `shortDay`
- `EmberRunService` case enter 别名

## 下步
- 物品 W1b/c（TrMenu）等键
