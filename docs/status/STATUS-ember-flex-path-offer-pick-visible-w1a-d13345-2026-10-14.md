# STATUS · TIP-DN W1a 轻技路径选打菜单化 · D13345

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **CLOSED live** · PASS `89c68de74` · tip `68230fe44` · 总控收口 2026-10-14 13:38 Asia/Shanghai

## 交付
- feat: `53be7080e08572f2f981287e74b814cea7d45be3`
- `EmberFlexPath.offerPick` →开 `/ember_p1_flex_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `flexpath` 为主 UI
- 无参 `/corerpg p1 flexpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_flex_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `flexpath`/`steppath` ConfirmTokens 为主 UI（身法方向后置 Step 窗）
- glance / 用法：清「手打 /corerpg p1 flexpath …」主教 →「点轻技路径页」
- PAPI：`%corerpg_p1_flex_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberFlexPathPickTest`（新）· `EmberFlexPathTest` · `EmberStridePathPickTest` · `EmberCounterPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.838-d13345.local` · sha256 `ff6548cc0148092a…` · ECONOMY_BV **123** 未动
- play：live `1.65.838-d13345.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / 轻技规则语义(D494 on/off 装配潜行+Q) / Cast·Aid / variety / 已齐 settle 薄页 / Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Step/Sidestep 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- **勾毕 CLOSED live** · PASS `89c68de74` · 总控收口 2026-10-14 13:38 Asia/Shanghai

## 落点
- `EmberFlexPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine
- `EmberCommand`：用法改「点轻技路径页」
- `EmberRunPapi`：flex_path / flex_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.838-d13345.local
- 单测 `EmberFlexPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / on/off 装配潜行+Q 语义(D494)
- stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / StepPath / SidestepPath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_flex_path`）

## 下步
- 物品 W1b / 测岗均已齐 · 总控收口 CLOSED

## 测岗抽测（TIP-DN spot）
- **PASS** · 2026-10-14 13:38 Asia/Shanghai · 证据 `/workspace/tmp/tip-dn-flex-path/` · 详 `STATUS-ember-flex-path-offer-pick-tip-dn-spot-d13345-2026-10-14.md`
- live jar `1.65.838-d13345.local` · feat `53be7080e` · tip `68230fe44` · 批 `68230fe44` · 轻技路径选打菜单化全绿

## 续 · 测岗抽测 PASS（2026-10-14 Asia/Shanghai · 余烬-测试）
- PASS live · feat `53be7080e` · 证据 `/workspace/tmp/tip-dn-flex-path/` · STATUS `89c68de74`
- **CLOSED live**（总控收口）· tip `68230fe44` · jar `1.65.838-d13345.local`
