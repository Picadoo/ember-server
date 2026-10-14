# STATUS · TIP-DA W1a 挑战本结算「下一环」菜单化 · D13332

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **CLOSED live** · PASS `020a8f2a7` · tip `d0f10a344` · 总控收口 2026-10-14 09:28 Asia/Shanghai

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
- 物品 W1b / 测岗均已齐 · 总控收口 CLOSED

## 测岗抽测（TIP-DA spot）
- **PASS** · 2026-10-14 09:28 Asia/Shanghai · 证据 `/workspace/tmp/tip-da-challenge-settle/` · 详 `STATUS-ember-challenge-settle-next-loop-tip-da-spot-d13332-2026-10-14.md`
- live jar `1.65.825-d13332.local` · feat `3d218ee83` · tip `d0f10a344` · 批 `d0f10a344` · 挑战结算下一环菜单化全绿

## 续 · 测岗抽测 PASS（2026-10-14 Asia/Shanghai · 余烬-测试）
- PASS live · feat `3d218ee83` · 证据 `/workspace/tmp/tip-da-challenge-settle/` · STATUS `020a8f2a7`
- **CLOSED live**（总控收口）· tip `d0f10a344` · jar `1.65.825-d13332.local`
