# STATUS · TIP-DY W1a 限时路径选打菜单化 · D13356

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip **勿勾 CLOSED** · feat `90f5bb643` · jar `1.65.849-d13356.local`

## 交付
- feat: `90f5bb643`（commit 后钉死）
- `EmberRushPath.offerPick` →开 `/ember_p1_rush_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `rushpath` 为主 UI
- 无参 `/corerpg p1 rushpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_rush_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `rushpath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 rushpath auto|ask|mute|clear」主教 →「点限时路径页」
- 局内 `maybeAfterRush`/`maybeProbe`「冲」战斗钮语义 **零改**（仍 `/corerpg p1 rush`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_rush_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberRushPathPickTest`（新 8）· `EmberRushPathTest` · `EmberCleanPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.849-d13356.local` · sha256 见证据 · ECONOMY_BV **123** 未动
- play：装 jar + restart 验证 storage=mysql · bv123 · MySQL connected
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D579 限时语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Clean·Streak·Blaze·Spin·Snap·Veil·Free·Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / Cut 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- tip **勿勾 CLOSED** · 可派测 / 物品 W1b

## 落点
- `EmberRushPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点限时路径页」
- `EmberRunPapi`：rush_path / rush_path_label / tip / line → Section.RUSH（dispatch 在 rush_<entry> 前）
- `pom.xml` / `plugin.yml`：1.65.849-d13356.local
- 单测 `EmberRushPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 限时语义(D579)
- clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath / Cut
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_rush_path`）

## 下步
- 可派测 · 物品 W1b 并行 · **勿勾 tip CLOSED** · 下一窗旁注 Cut… D580+ 后置 / PacePath（本仓无）后置
