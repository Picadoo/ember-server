# STATUS · TIP-CE FAIL 修 · event_chase_ok · D13310b

> 2026-10-13 Asia/Shanghai · 余烬-插件岗 · 上游测岗 `87941360f`

## 问题
无 chase 时 TrMenu 七段 `q*done and…` 复合 and 未命中灰（同 D408）；默认仍火球。命令拒诚实。

## 交付
- `%corerpg_p1_event_chase_ok%` = chase 非空→`1` 否则 `0`（只读；同 soft_ready 风格）
- 单测：空→0 / 有→1（`EmberEventChaseTest.chaseOkEmptyAndPresent`）+ 路由
- jar：`1.65.807-d13310b.local` · sha256 `81a0d8d7cf0b…` · ECONOMY_BV **123** · **零改** SoftPity/onSettle/event_rate/NEED/Pack/weights/event_core
- play：live `1.65.807-d13310b.local` · storage=mysql · bv123

## 落点
- `EmberEventChase.chaseOk(String)`
- `EmberRunPapi` ROTATION：`event_chase_ok`

## 下步
- 物品：E 灰态改单条件 `event_chase_ok < 1`（等键）
