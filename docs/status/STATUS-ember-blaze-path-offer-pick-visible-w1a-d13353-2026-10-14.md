# STATUS · TIP-DV W1a 避焰路径选打菜单化 · D13353

> 2026-10-14 16:00 Asia/Shanghai · 余烬-插件岗 · W1a 已交 · tip **勿勾 CLOSED** · feat `7696e36f6` · jar `1.65.846-d13353.local`

## 交付
- feat: `7696e36f6`（commit 后钉死）
- `EmberBlazePath.offerPick` →开 `/ember_p1_blaze_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `blazepath` 为主 UI
- 无参 `/corerpg p1 blazepath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_blaze_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `blazepath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 blazepath auto|ask|mute|clear」主教 →「点避焰路径页」
- 局内 `maybeAfterBlaze`/`maybeProbe`「避焰」战斗钮语义 **零改**（仍 `/corerpg p1 blaze`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_blaze_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberBlazePathPickTest`（新 8）· `EmberBlazePathTest` · `EmberSpinPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.846-d13353.local` · sha256 `74af0b7bcc0f…` · ECONOMY_BV **123** 未动
- play：live `1.65.846-d13353.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D576 焰圈避焰语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Spin·Snap·Veil·Free·Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- tip **勿勾 CLOSED** · 可派测 / 物品 W1b

## 落点
- `EmberBlazePath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点避焰路径页」
- `EmberRunPapi`：blaze_path / blaze_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.846-d13353.local
- 单测 `EmberBlazePathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 焰圈避焰语义(D576)
- spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_blaze_path`）

## 下步
- 可派测 · 物品 W1b 并行 · tip **勿勾 CLOSED** · 下一窗旁注 PacePath（本仓无）后置 / 其他残留 *Path.offerPick 逐支另窗
