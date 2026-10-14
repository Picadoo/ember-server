# STATUS · TIP-DJ W1a 试炼路径选打菜单化 · D13341

> 2026-10-14 12:11 Asia/Shanghai · 余烬-插件岗 · W1a 已交 · feat `845a99920` · **勿勾 tip CLOSED** · 待物品 W1b / 测岗抽测

## 交付
- `EmberTrialPath.offerPick` →开 `/ember_p1_trial_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `trialpath` 为主 UI
- 无参 `/corerpg p1 trialpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ06` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 / 冒险 `/ember_p1_adventure` / 工坊 `/ember_p1_forge`；**禁**以裸 `trialpath`/`pledgepath`/`counterpath` ConfirmTokens 为主 UI
- glance / nudge / 用法：清「手打 /corerpg p1 trialpath …」主教 →「点试炼路径页」
- PAPI：`%corerpg_p1_trial_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberTrialPathPickTest`（新）· `EmberTrialPathTest` · `EmberRunPapiTest` · `EmberAbyssPathPickTest` 绿
- jar：`1.65.834-d13341.local` · sha256 `91e07896d2fe…` · ECONOMY_BV **123** 未动
- play：live `1.65.834-d13341.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / 试炼语义 / Cast·Aid / variety / 已齐 settle 薄页 / Short·Raid·Featured·Challenge·Abyss Path / 签律·破绽·步态 / 庭内 / TrMenu yml（物品 W1b 并行）
- **勿勾 tip CLOSED** · 待物品 W1b + 测岗 PASS 后总控收口

## 落点
- `EmberTrialPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine
- `EmberCommand`：nudge/用法改「点试炼路径页」
- `EmberRunPapi`：trial_path / trial_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.834-d13341.local
- 单测 `EmberTrialPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / 试炼写入语义（誓约+破绽）
- short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / 签律·破绽·步态 *Path
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_trial_path`）

## 下步
- 物品 W1b 薄页 `ember_p1_trial_path`
- 测岗抽测 PASS 后总控收口 CLOSED
