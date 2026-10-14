# STATUS · TIP-EB W1a 分身路径选打菜单化 · D13359

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip **勿勾 CLOSED** · feat `fdd409b970467b476be0f360b63a21b36181533c` · jar `1.65.852-d13359.local`

## 交付
- feat: `fdd409b970467b476be0f360b63a21b36181533c`
- `EmberClonePath.offerPick` →开 `/ember_p1_clone_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `clonepath` 为主 UI
- 无参 `/corerpg p1 clonepath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_clone_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `clonepath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 clonepath auto|ask|mute|clear」主教 →「点分身路径页」
- 局内 `maybeAfterClone`/`maybeProbe`「清分身」战斗钮语义 **零改**（仍 `/corerpg p1 clone`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_clone_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberClonePathPickTest`（新 8）· `EmberClonePathTest` · `EmberPressPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.852-d13359.local` · ECONOMY_BV **123** 未动
- play：装 jar + restart 验证 storage=mysql · bv123 · MySQL connected
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D582 分身语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Press·Cut·Rush·Clean·Streak·Blaze·Spin·Snap·Veil·Free·Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / 庭内 / TrMenu yml（物品 W1b 并行已交 `938e20faa`）
- tip **勿勾 CLOSED** · 可派测 / 物品 W1b

## 落点
- `EmberClonePath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点分身路径页」
- `EmberRunPapi`：clone_path / clone_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.852-d13359.local
- 单测 `EmberClonePathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 分身语义(D582)
- press_path / cut_path / rush_path / clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_clone_path` · feat `938e20faa`）

## 下步
- 可派测 · 物品 W1b 已交薄页 · **勿勾 tip CLOSED** · 下一窗旁注其它剩支 D583+ 后置 / PacePath（本仓无）后置

## 证据
- `/workspace/tmp/tip-eb-clone-path/` · jar sha256 见 `00-jar.sha256`
