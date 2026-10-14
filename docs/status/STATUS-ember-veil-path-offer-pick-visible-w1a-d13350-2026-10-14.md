# STATUS · TIP-DS W1a 避毒路径选打菜单化 · D13350

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip APPROVED `3947da6bf` · tip **CLOSED live** · PASS `0364f3334` · menu `bf2645adf` · 2026-10-14 15:18 Asia/Shanghai

## 交付
- feat: `3e3a8f876`
- `EmberVeilPath.offerPick` →开 `/ember_p1_veil_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `veilpath` 为主 UI
- 无参 `/corerpg p1 veilpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_veil_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `veilpath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 veilpath auto|ask|mute|clear」主教 →「点避毒路径页」
- 局内 `maybeAfterVenom`/`maybeProbe`「避毒」战斗钮语义 **零改**（仍 `/corerpg p1 veil`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_veil_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberVeilPathPickTest`（新 8）· `EmberVeilPathTest` · `EmberFreePathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.843-d13350.local` · sha256 `3d129e65f95eb2d2…` · ECONOMY_BV **123** 未动
- play：live `1.65.843-d13350.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D573 毒十字避毒语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Free·Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / Snap/Spin/Blaze 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- tip **CLOSED live by 总控** · PASS `0364f3334` · menu `bf2645adf` · 2026-10-14 15:18 Asia/Shanghai

## 落点
- `EmberVeilPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点避毒路径页」
- `EmberRunPapi`：veil_path / veil_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.843-d13350.local
- 单测 `EmberVeilPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 毒十字避毒语义(D573)
- free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath / Snap/Spin/Blaze
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_veil_path`）

## 下步
- tip **CLOSED live** · PASS `0364f3334` · 总控收口 `2026-10-14 15:18 Asia/Shanghai` · 下一窗 SnapPath.offerPick
