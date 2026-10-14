# STATUS · TIP-DW W1a 连斩路径选打菜单化 · D13354

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip **勿勾 CLOSED** · feat `8058a9e51` · jar `1.65.847-d13354.local`

## 交付
- feat: `8058a9e51`
- `EmberStreakPath.offerPick` →开 `/ember_p1_streak_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `streakpath` 为主 UI
- 无参 `/corerpg p1 streakpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_streak_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `streakpath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 streakpath auto|ask|mute|clear」主教 →「点连斩路径页」
- 局内 `maybeAfterChain`/`maybeProbe`「连斩」战斗钮语义 **零改**（仍 `/corerpg p1 streak`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_streak_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberStreakPathPickTest`（新 8）· `EmberStreakPathTest` · `EmberBlazePathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.847-d13354.local` · sha256 `e5ee45ed1543…` · ECONOMY_BV **123** 未动
- play：live `1.65.847-d13354.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D577 连斩语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Blaze·Spin·Snap·Veil·Free·Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / Clean·Rush·Cut 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- tip **勿勾 CLOSED** · 可派测 / 物品 W1b

## 落点
- `EmberStreakPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点连斩路径页」
- `EmberRunPapi`：streak_path / streak_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.847-d13354.local
- 单测 `EmberStreakPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 连斩语义(D577)
- blaze_path / spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath / Clean/Rush/Cut
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_streak_path`）

## 下步
- 可派测 · 物品 W1b 并行 · **勿勾 tip CLOSED** · 下一窗旁注 Clean/Rush/Cut… D578+ 后置 / PacePath（本仓无）后置
