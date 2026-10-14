# STATUS · TIP-DO W1a 身法路径选打菜单化 · D13346

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · W1a 已交 · **勿勾 tip CLOSED** · tip APPROVED `057b6b380`

## 交付
- feat: `bb9accd40f1fb359c495f3d31f4d2d592d612cfb`
- `EmberStepPath.offerPick` →开 `/ember_p1_step_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `steppath` 为主 UI
- 无参 `/corerpg p1 steppath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ05` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_step_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `steppath`/`enter` ConfirmTokens 为主 UI
- glance / 用法：清「手打 steppath forward|back|clear」主教 →「点身法路径页」
- PAPI：`%corerpg_p1_step_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberStepPathPickTest`（新 7）· `EmberStepPathTest` · `EmberFlexPathPickTest` · `EmberStridePathPickTest` · `EmberCounterPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.839-d13346.local` · sha256 `d35731caef43faac…` · ECONOMY_BV **123** 未动
- play：live  · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / 身法规则语义(D491 forward/back 真改方向) / Cast·Aid / variety / 已齐 settle 薄页 / Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Sidestep 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- **勿勾 tip CLOSED**（测岗/总控勾）

## 落点
- `EmberStepPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine
- `EmberCommand`：用法改「点身法路径页」
- `EmberRunPapi`：step_path / step_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.839-d13346.local
- 单测 `EmberStepPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / forward/back 方向语义(D491)
- flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / SidestepPath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_step_path`）

## 下步
- 物品 W1b 并行薄页；测岗抽测；总控收口 CLOSED
