# STATUS · TIP-DC W1a 图身份首通结算「下一环」菜单化 · D13334

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · W1a 已交 · 勿勾 tip CLOSED · 批 A·M `0a92cb3c8`

## 交付
- `EmberSettleService` 图身份首通（firstClear + 有效 family / D673）：主钮 → `/ember_p1_identity_settle`（`EmberIdentitySettle.SETTLE_NEXT_LOOP_*`）；工坊 → `/ember_p1_forge`；装备 → `/ember_p1_gear`；remember settle 图/族/槽
- 清 ConfirmTokens 三钮教 `/corerpg p1 forge` 主 UI（裸 forge 指令不再作主交付）
- `EmberIdentitySettle`：常量 + remember/lastMap/lastFamily/lastSlot/displayName/familyDisplay/slotDisplay/line
- PAPI（对齐物品薄页）：`%corerpg_p1_identity_settle%/_map/_name/_family/_slot/_line%`（经 `EmberRunPapi` GATE）
- 单测：`EmberIdentitySettleTest` · `EmberIdentitySettleNextLoopTest` · `EmberRunPapiTest` 绿
- jar：`1.65.827-d13334.local` · sha256 `94d211372105…` · ECONOMY_BV **123** 未动
- play：live `1.65.827-d13334.local` · storage=mysql · bv123 · MySQL connected（见交班验证）
- **零改**掉落 / 体力价 / 首通包 / 图身份语义 / 短征·团本·挑战·深渊 settle / variety / soft-reward / Cast/Aid / D794 / TrMenu yml（物品 W1b 并行薄页已存在）
- **未做** W1b（物品岗）· **未勾** tip CLOSED · **未派测**

## 落点
- `EmberIdentitySettle`（新）
- `EmberSettleService`：D673 下一环薄页 + remember + 工坊菜单
- `EmberRunPapi`：identity_settle_*
- `pom.xml` / `plugin.yml`：1.65.827-d13334.local

## 零改边界
- 掉落 / 体力价 / 首通包 / 图身份语义 / S表 / 伤表
- 短征·团本·挑战·深渊 settle 薄页 / variety / soft-reward / Cast/Aid / D794
- balance_version 123 · sx20 · legacy evacuate · TrMenu yml（本窗未改）

## 下步
- 物品 W1b 薄页已并行存在（`ember_p1_identity_settle.yml`）· 待测岗抽测 · 总控收口 CLOSED
