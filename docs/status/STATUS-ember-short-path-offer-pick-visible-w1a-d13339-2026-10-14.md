# STATUS · TIP-DH W1a 短征路径选打菜单化 · D13339

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · 勿勾 tip CLOSED · 待物品 W1b + 测岗抽测

## 交付
- `EmberShortPath.offerPick` →开 `/ember_p1_short_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `shortpath` 为主 UI
- 无参 `/corerpg p1 shortpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 / 短征选页 `/ember_p1_short` / 工坊 `/ember_p1_forge`；**禁**以裸 `enter` ConfirmTokens 为主 UI（进本走薄页「进下一本」）
- glance / nudge / 用法：清「手打 /corerpg p1 shortpath …」主教 →「点路径追页」
- PAPI（复用既有 TIP-CV）：`%corerpg_p1_short_path%`/`_label`/`_tip`/`_resolve`/`_line%` 等；新增 `menuLine` 供薄页 lore
- 单测：`EmberShortPathPickTest`（新）· `EmberShortPathTest` · `EmberShortSettleNextLoopTest` · `EmberShortSettleTest` · `EmberShortPathChaseTest` · `EmberRaidPathPickTest` · `EmberChallengePathPickTest` · `EmberFeaturedPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.832-d13339.local` · sha256 `2d1d760d705d…` · ECONOMY_BV **123** 未动
- play：live `1.65.832-d13339.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**日表 / 掉落 / 体力价 / ShortPath resolve / Cast·Aid / variety / 已齐 settle 薄页（含 `ember_p1_short_settle`）/ Raid·Featured·Challenge Path / Abyss/Trial / 庭内 / TrMenu yml（物品 W1b 并行）
- **勿勾 tip CLOSED** · 待 W1b + 抽测 PASS 后总控收口

## 落点
- `EmberShortPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine
- `EmberCommand`：nudge/用法改「点路径追页」
- `pom.xml` / `plugin.yml`：1.65.832-d13339.local
- 单测 `EmberShortPathPickTest`（新）· SettleNextLoopTest 对齐 PATH_PICK

## 零改边界
- 日表 / 掉落 / 体力价 / ShortPath resolve 算法 / S表 / 伤表
- short_settle / settle「换一条」SETTLE_PATH_CMD → 薄页（TIP-CW 保持；与 PATH_PICK_CMD 同值）
- raid_path / featured_path / challenge_path / 图身份·深渊 settle / variety / soft-reward / Cast/Aid / 庭内
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick · AbyssPath/TrialPath 同窗
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_short_path` 接线/补洞）

## 下步
- 物品 W1b：既有薄页接线/补洞 + 清选页 Open 旁路
- 测岗抽测 → 总控收口 CLOSED
