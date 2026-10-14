# STATUS · TIP-DM W1a 步态路径选打菜单化 · D13344

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · W1a 已交 · **勿勾 tip CLOSED** · 待测岗/物品 W1b

## 交付
- feat: （commit 后填）
- `EmberStridePath.offerPick` →开 `/ember_p1_stride_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `stridepath` 为主 UI
- 无参 `/corerpg p1 stridepath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ05` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_stride_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `stridepath`/`enter`/`flexpath`/`steppath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 /corerpg p1 stridepath …」主教 →「点步态路径页」
- PAPI：`%corerpg_p1_stride_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberStridePathPickTest`（新）· `EmberStridePathTest` · `EmberCounterPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.837-d13344.local` · sha256 `9c2f45172a51c8b9…` · ECONOMY_BV **123** 未动
- play：live `1.65.837-d13344.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / 步态规则语义(D499 push/bail/bare 写轻技+身法) / Cast·Aid / variety / 已齐 settle 薄页 / Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Flex/Step/Sidestep 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- **勿勾 tip CLOSED**（测岗/总控勾）

## 落点
- `EmberStridePath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine
- `EmberCommand`：用法改「点步态路径页」
- `EmberRunPapi`：stride_path / stride_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.837-d13344.local
- 单测 `EmberStridePathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / flexId·stepId 写轻技+身法映射(D499)
- counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / FlexPath / StepPath / SidestepPath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_stride_path`）

## 下步
- 物品 W1b 薄页 `ember_p1_stride_path` + 测岗抽测 · 总控收口 CLOSED
