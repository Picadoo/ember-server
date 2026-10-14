# STATUS · TIP-DD W1a 精选结算「下一环」菜单化 · D13335

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · W1a 交 · **勿勾 tip CLOSED** · 待物品 W1b / 抽测

## 交付
- `EmberFeaturedPath.afterSettle`：精选图结算下一环 → `/ember_p1_featured_settle`（`EmberFeaturedSettle.SETTLE_NEXT_LOOP_*`）；工坊 → `/ember_p1_forge`；冒险 → `/ember_p1_adventure`；remember settle 图/路径/周帽态
- `EmberSettleService` D794 周帽满「下一步」→同薄页；weekCapFull 时 afterSettle 不重复发钮
- 清 ConfirmTokens 教 `/corerpg p1 enter …` / `featurepath` / `challengepath` / `forge` / `mapfarm` 为主 UI（settle 入口）
- `EmberFeaturedSettle`：常量 + remember/lastMap/lastPath/lastCapFull/displayName/line
- PAPI（对齐物品薄页）：`%corerpg_p1_featured_settle%/_map/_name/_path/_path_label/_cap/_cap_full/_cap_label/_line%`（经 `EmberRunPapi` ROTATION）
- 单测：`EmberFeaturedSettleTest` · `EmberFeaturedSettleNextLoopTest` · `EmberRunPapiTest` 绿
- jar：`1.65.828-d13335.local` · sha256 `642f02476144…` · ECONOMY_BV **123** 未动
- play：live `1.65.828-d13335.local` · storage=mysql · bv123 · MySQL connected
- **零改**掉落 / 体力价 / 周帽数值 / 精选语义 / Cast·Aid / variety / 已齐 settle 薄页 / 庭内 / TrMenu yml（物品 W1b 并行）
- **未做** W1b（物品岗）· **未勾** tip CLOSED · **未派测**

## 落点
- `EmberFeaturedSettle`（新）
- `EmberFeaturedPath`：afterSettle 下一环薄页 + remember；无路径文案改「点下一环选路径」
- `EmberSettleService`：D794 下一环薄页 + weekCapFull 传 afterSettle
- `EmberRunPapi`：featured_settle_*
- `pom.xml` / `plugin.yml`：1.65.828-d13335.local

## 零改边界
- 掉落 / 体力价 / 周帽数值 / 精选语义 / S表 / 伤表
- 图身份·深渊·短征·团本·挑战 settle 薄页 / variety / soft-reward / Cast/Aid
- balance_version 123 · sx20 · legacy evacuate · TrMenu yml（本窗未改）
- offerPick / glance / enterCue / applyAndReply 全链教指令大扫（后置；本窗只钉 settle 下一环）

## 下步
- 物品 W1b 薄页 `ember_p1_featured_settle` · 测岗抽测 · 总控收口 CLOSED
