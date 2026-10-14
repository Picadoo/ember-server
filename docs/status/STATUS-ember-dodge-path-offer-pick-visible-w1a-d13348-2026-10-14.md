# STATUS · TIP-DQ W1a 闪榴路径选打菜单化 · D13348

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip APPROVED `68585e732` · **勿勾 tip CLOSED** · 待测岗/物品 W1b

## 交付
- feat: `7551786ea65488ce5b80b86e3dbff80b4e163d13`
- `EmberDodgePath.offerPick` →开 `/ember_p1_dodge_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `dodgepath` 为主 UI
- 无参 `/corerpg p1 dodgepath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_dodge_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `dodgepath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 dodgepath auto|ask|mute|clear」主教 →「点闪榴路径页」
- 局内 `maybeAfterMortar`/`maybeProbe`「闪榴」战斗钮语义 **零改**（仍 `/corerpg p1 dodge`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_dodge_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberDodgePathPickTest`（新 8）· `EmberDodgePathTest` · `EmberSidestepPathPickTest` · `EmberStepPathPickTest` · `EmberFlexPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.841-d13348.local` · sha256 `4a926329d3462849…` · ECONOMY_BV **123** 未动
- play：live `1.65.841-d13348.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D571 榴弹拉开语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / 庭内 / TrMenu yml（物品 W1b 并行）
- **勿勾 tip CLOSED** · 待测岗抽测 + 物品 W1b

## 落点
- `EmberDodgePath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点闪榴路径页」
- `EmberRunPapi`：dodge_path / dodge_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.841-d13348.local
- 单测 `EmberDodgePathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 榴弹拉开语义(D571)
- sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_dodge_path`）

## 下步
- 物品 W1b 并行薄页 · 测岗抽测 · 总控勿勾 CLOSED 至 PASS
