# STATUS · TIP-DB W1a 深渊结算「下一环」菜单化 · D13333

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **交卷** · 勿勾 tip CLOSED · 批 `8a3f93564` · ancestor TIP-DA `3d218ee83`

## 交付
- `EmberSettleService` 深渊 `newBest`：主钮 → `/ember_p1_abyss_settle`（`EmberAbyssSettle.SETTLE_NEXT_LOOP_*`）；工坊 → `/ember_p1_forge`；remember settle 层（每次 abyss settle 覆盖）
- 辅钮：冒险 `/ember_p1_adventure`；D712 文案改「点下一环设目标」（禁教手打 abysstarget）
- `EmberAbyssTarget`：afterBest / glance / cmd 无参·设后 ConfirmTokens → 开薄页；禁教手打 abysstarget|forge 主 UI
- `EmberAbyssPath.applyAndReply`：一层教指令改「点下一环设目标」（顺手清 abysstarget 教）
- `EmberAbyssSettle`：常量 + remember/lastFloor/displayName/line
- PAPI（对齐物品薄页）：`%corerpg_p1_abyss_settle%/_floor/_name/_line/_path/_path_label/_next/_can_next%` · `%corerpg_p1_abyss_path%` / `_path_label%`（经 `EmberRunPapi` ABYSS）
- 单测：`EmberAbyssSettleTest` · `EmberAbyssSettleNextLoopTest` · `EmberAbyssTargetTest` · `EmberRunPapiTest` 绿
- jar：`1.65.826-d13333.local` · ECONOMY_BV **123** 未动
- **零改**深渊掉落 / 体力价 / 层表 / 日帽 / 短征·团本·挑战薄页 / variety / soft-reward / Cast/Aid / TrMenu yml（物品已并行薄页）
- **未做** W1b（物品岗）· **未勾** tip CLOSED · **未派测**

## 落点
- `EmberAbyssSettle`（新）
- `EmberSettleService`：D675 下一环薄页 + remember
- `EmberAbyssTarget`：afterBest / glance / cmd 菜单优先
- `EmberAbyssPath`：一层 abysstarget 教指令清
- `EmberRunPapi`：abyss_settle_* / abyss_path*
- `pom.xml` / `plugin.yml`：1.65.826-d13333.local

## 零改边界
- 深渊掉落 / 体力价 / 层费表 / 层表 / 日帽 / S表 / 伤表
- 短征·团本·挑战薄页 / variety / soft-reward / ShortPath · TrMenu yml（本窗未改）
- balance_version 123 · sx20 · legacy evacuate

## 下步
- 物品 W1b 薄页已并行存在 · 待测岗抽测 · 总控收口 CLOSED
