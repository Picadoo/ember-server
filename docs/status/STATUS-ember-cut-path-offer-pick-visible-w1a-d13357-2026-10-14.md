# STATUS · TIP-DZ W1a 打断路径选打菜单化 · D13357

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip **勿勾 CLOSED** · feat `a7f634bc5` · jar `1.65.850-d13357.local`

## 交付
- feat: `a7f634bc5`（commit 后钉死）
- `EmberCutPath.offerPick` →开 `/ember_p1_cut_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `cutpath` 为主 UI
- 无参 `/corerpg p1 cutpath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_cut_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `cutpath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 cutpath auto|ask|mute|clear」主教 →「点打断路径页」
- 局内 `maybeAfterCut`/`maybeProbe`「打断」战斗钮语义 **零改**（仍 `/corerpg p1 cut`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_cut_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberCutPathPickTest`（新 8）· `EmberCutPathTest` · `EmberRushPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.850-d13357.local` · sha256 见证据 · ECONOMY_BV **123** 未动
- play：装 jar + restart 验证 storage=mysql · bv123 · MySQL connected
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D580 打断语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Rush·Clean·Streak·Blaze·Spin·Snap·Veil·Free·Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- tip **勿勾 CLOSED** · 可派测 / 物品 W1b

## 落点
- `EmberCutPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点打断路径页」
- `EmberRunPapi`：cut_path / cut_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.850-d13357.local
- 单测 `EmberCutPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 打断语义(D580)
- rush_path / clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_cut_path`）

## 下步
- 可派测 · 物品 W1b 并行 · **勿勾 tip CLOSED** · 下一窗旁注其它剩支 D581+ 后置 / PacePath（本仓无）后置
