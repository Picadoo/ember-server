# STATUS · TIP-DR W1a 解锢路径选打菜单化 · D13349

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip APPROVED `66bfe1203` · tip **CLOSED live** · PASS `36db6d583` · menu `b1f0db650` · 2026-10-14 14:57 Asia/Shanghai

## 交付
- feat: `446b2d907`
- `EmberFreePath.offerPick` →开 `/ember_p1_free_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `freepath` 为主 UI
- 无参 `/corerpg p1 freepath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_free_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `freepath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 freepath auto|ask|mute|clear」主教 →「点解锢路径页」
- 局内 `maybeAfterJailer`/`maybeProbe`「解锢」战斗钮语义 **零改**（仍 `/corerpg p1 free`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_free_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberFreePathPickTest`（新 8）· `EmberFreePathTest` · `EmberDodgePathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.842-d13349.local` · sha256 `5013fc78d220a941…` · ECONOMY_BV **123** 未动
- play：live `1.65.842-d13349.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D570 禁锢解锢语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- **CLOSED live by 总控** · PASS `36db6d583` · menu `b1f0db650` · 2026-10-14 14:57 Asia/Shanghai

## 落点
- `EmberFreePath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点解锢路径页」
- `EmberRunPapi`：free_path / free_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.842-d13349.local
- 单测 `EmberFreePathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 禁锢解锢语义(D570)
- dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_free_path`）

## 下步
- tip **CLOSED live** · PASS `36db6d583` · 总控收口 `2026-10-14 14:57 Asia/Shanghai` · 下一窗 VeilPath.offerPick
