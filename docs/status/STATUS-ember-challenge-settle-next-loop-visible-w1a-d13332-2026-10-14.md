# STATUS · TIP-DA W1a 挑战本结算「下一环」菜单化 · D13332

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **交仓** · 勿勾 tip CLOSED · 物品可接薄页 V

## 交付
- `EmberChallengePath.afterSettle`：非首通（有路径/无路径）主钮 → `/ember_p1_challenge_settle`（`EmberChallengeSettle.SETTLE_NEXT_LOOP_*`）；remember settle map（q01–q07 period）
- 辅钮：冒险 `/ember_p1_adventure` · 工坊 `/ember_p1_forge`；禁教手打 `enter … challenge` / `forge` / `mapfarm` / `challengepath` 主 UI
- `EmberChallengePath`：glance「点下一环选路径」；offerPick / applyAndReply / enterCue 菜单优先（开薄页）
- D671 挑战首通：主钮开薄页 + 工坊 → `/ember_p1_forge`
- `EmberChallengeSettle`：常量 + remember/lastMap/displayName/line
- PAPI（对齐物品薄页）：`%corerpg_p1_challenge_settle%/_map/_name/_line/_path/_path_label%` · `%corerpg_p1_challenge_path%` / `_path_label%`（经 `EmberRunPapi` GATE）
- 单测：`EmberChallengeSettleTest` · `EmberChallengeSettleNextLoopTest` · `EmberChallengePathTest` · `EmberRunPapiTest` 绿
- jar：`1.65.825-d13332.local` · sha256 `7a3cb57896c2…` · ECONOMY_BV **123** 未动
- play：live `1.65.825-d13332.local` · storage=mysql · bv123 · MySQL connected
- **零改**挑战掉落 / 体力价 / 成色 / 日帽 / 短征·团本薄页 / variety / soft-reward / ShortPath / 深渊 / TrMenu yml
- **未做** W1b（物品岗薄页已并行存在；本窗未改 yml）· **未勾** tip CLOSED · **未派测**

## 落点
- `EmberChallengeSettle`（新）
- `EmberChallengePath`：afterSettle / glance / offerPick / applyAndReply / enterCue
- `EmberSettleService`：D671 工坊→菜单 + 主钮薄页
- `EmberRunPapi`：challenge_settle_* / challenge_path*
- `pom.xml` / `plugin.yml`：1.65.825-d13332.local

## 零改边界
- 挑战掉落 / 体力价 / 成色表 / 日帽 / S表 / 伤表
- 短征·团本薄页 / variety / soft-reward / ShortPath · TrMenu yml
- balance_version 123 · sx20 · 深渊 settle

## 下步
- 物品 W1b 薄页（若未齐）/ 测岗抽测 · 总控收口 CLOSED（本岗勿勾）
