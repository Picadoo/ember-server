# STATUS · TIP-DT W1a 断链路径选打菜单化 · D13351

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip APPROVED `f5cea1e1d` · 勿勾 tip CLOSED

## 交付
- feat: `4417ea595`
- `EmberSnapPath.offerPick` →开 `/ember_p1_snap_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `snappath` 为主 UI
- 无参 `/corerpg p1 snappath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_snap_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `snappath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 snappath auto|ask|mute|clear」主教 →「点断链路径页」
- 局内 `maybeAfterLink`/`maybeProbe`「断链」战斗钮语义 **零改**（仍 `/corerpg p1 snap`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_snap_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberSnapPathPickTest`（新 8）· `EmberSnapPathTest` · `EmberVeilPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.844-d13351.local` · sha256 `PENDING` · ECONOMY_BV **123** 未动
- play：live `1.65.844-d13351.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D574 火链断链语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Veil·Free·Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / Spin/Blaze 同窗 / 庭内 / TrMenu yml（物品 W1b 并行 `4070a04f7`）
- tip **勿勾 CLOSED**（总控勾）

## 落点
- `EmberSnapPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点断链路径页」
- `EmberRunPapi`：snap_path / snap_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.844-d13351.local
- 单测 `EmberSnapPathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 火链断链语义(D574)
- veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath / Spin/Blaze
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_snap_path` · `4070a04f7`）

## 下步
- 可派测 / 物品 W1b 已并行 · tip **勿勾 CLOSED** · 总控收口后 CLOSED
