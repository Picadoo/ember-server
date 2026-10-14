# STATUS · TIP-DL W1a 破绽路径选打菜单化 · D13343

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · 勿勾 tip CLOSED · 待测岗/物品 W1b

## 交付
- feat: 
- `EmberCounterPath.offerPick` →开 `/ember_p1_counter_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `counterpath` 为主 UI
- 无参 `/corerpg p1 counterpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ02` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_counter_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `counterpath`/`enter` ConfirmTokens 为主 UI
- glance / 用法：清「手打 /corerpg p1 counterpath …」主教 →「点破绽路径页」
- PAPI：`%corerpg_p1_counter_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberCounterPathPickTest`（新）· `EmberCounterPathTest` · `EmberPledgePathPickTest` · `EmberTrialPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.836-d13343.local` · sha256 `8961daa00067…` · ECONOMY_BV **123** 未动
- play：live `1.65.836-d13343.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / 破绽眩晕加秒表(D480 +0.12s) / 破绽规则语义(wall/whiff/break) / Cast·Aid / variety / 已齐 settle 薄页 / Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Stride / 庭内 / TrMenu yml（物品 W1b 并行）
- **勿勾 tip CLOSED**

## 落点
- `EmberCounterPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine
- `EmberCommand`：用法改「点破绽路径页」
- `EmberRunPapi`：counter_path / counter_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.836-d13343.local
- 单测 `EmberCounterPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / preferredStunBonusSec +0.12s / wall·whiff·break 语义(D480)
- pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / Stride *Path
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_counter_path`）

## 下步
- 物品 W1b 薄页 `ember_p1_counter_path` · 测岗抽测 · 总控收口 CLOSED
