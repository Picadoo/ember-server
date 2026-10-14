# STATUS · TIP-DF W1a 硬本路径选打菜单化 · D13337

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交待测** · 勿勾 tip CLOSED

## 交付
- `EmberChallengePath.offerPick` →开 `/ember_p1_challenge_path`（`openPathPick` / `PATH_PICK_*`）；**禁**借 `ember_p1_challenge_settle` ConfirmTokens 当选打主 UI
- 无参 `/corerpg p1 challengepath`（及冒险 Open 同调）→ glance + offerPick →同薄页
- `applyAndReply` 下一步→同薄页 / 冒险 `/ember_p1_adventure` / 挑战选图 `/ember_p1_challenge`；禁借 settle ConfirmTokens 为主 UI
- glance / enterCue / nudge / 用法：清「手打 /corerpg p1 challengepath」主教 →「点硬本路径页」
- PAPI（复用+扩）：`%corerpg_p1_challenge_path%`/`_label`/`_tip`/`_line%`（经 `EmberRunPapi` GATE）；`q07done` 复用
- 单测：`EmberChallengePathPickTest` · `EmberChallengePathTest` · `EmberChallengeSettleNextLoopTest` · `EmberChallengeSettleTest` · `EmberRunPapiTest` · `EmberFeaturedPathPickTest` 绿
- jar：`1.65.830-d13337.local` · sha256 `b34dc2ffa59f…` · ECONOMY_BV **123** 未动
- play：待装 jar 后验证 live（本 STATUS 交收时填）
- **零改**掉落 / 体力价 / 硬本语义 / Cast·Aid / variety / 已齐 settle 薄页（含 `ember_p1_challenge_settle` / afterSettle）/ FeaturedPath / RaidPath / 庭内 / TrMenu yml（物品 W1b 并行已交 `ember_p1_challenge_path.yml`）
- **未勾** tip CLOSED · **未派测**（总控派测）

## 落点
- `EmberChallengePath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/enterCue/menuLine
- `EmberCommand`：nudge/用法改「点硬本路径页」
- `EmberRunPapi`：challenge_path_tip / challenge_path_line（既有 path/label 复用）
- `pom.xml` / `plugin.yml`：1.65.830-d13337.local
- 单测 `EmberChallengePathPickTest`（新）· SettleNextLoopTest glance 对齐 TIP-DF

## 零改边界
- 掉落 / 体力价 / 硬本 resolve 语义 / S表 / 伤表
- challenge_settle / afterSettle ConfirmTokens → settle 薄页（TIP-DA 保持）
- featured_path / 图身份·深渊·短征·团本·精选 settle / variety / soft-reward / Cast/Aid / 庭内
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick · RaidPath 同窗
- TrMenu yml（本窗插件未改；W1b 物品已交 `ember_p1_challenge_path.yml`）

## 下步
- 测岗抽测 PASS live → 总控收口 CLOSED
