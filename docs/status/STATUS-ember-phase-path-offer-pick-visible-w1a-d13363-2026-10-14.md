# STATUS · TIP-EF W1a 半血路径选打菜单化 · D13363

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · tip **勿勾 CLOSED** · feat `PENDING` · jar `1.65.856-d13363.local`

## 交付
- feat: `PENDING`（commit 后回填）
- `EmberPhasePath.offerPick` →开 `/ember_p1_phase_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `phasepath` 为主 UI
- 无参 `/corerpg p1 phasepath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_phase_path` / 冒险 `/ember_p1_adventure`；**禁**以裸 `phasepath` ConfirmTokens 为主 UI
- glance / 用法：清「手打 phasepath auto|ask|mute|clear」主教 →「点半血路径页」
- 局内 `maybeAfterPhase`/`maybeProbe`「加压」战斗钮语义 **零改**（仍 `/corerpg p1 phase`）；「改路径」随 offerPick 改开薄页 `PATH_PICK_CMD`
- PAPI：`%corerpg_p1_phase_path%`/`_label`/`_tip`/`_line`（`menuLine`）
- 单测：`EmberPhasePathPickTest`（新 8）· `EmberPhasePathTest` · `EmberWavePathPickTest` · `EmberRunPapiTest` 绿（22 PASS）
- jar：`1.65.856-d13363.local` · ECONOMY_BV **123** 未动
- play：装 jar + restart 验证 storage=mysql · bv123 · MySQL connected
- **零改**伤害 / CD 表 / 掉落 / 体力价 / D586 半血贴脸加压语义(auto/ask/mute) / Cast·Aid / variety / 已齐 settle 薄页 / Wave·Soak·Shell·Clone·Press·Cut·Rush·Clean·Streak·Blaze·Spin·Snap·Veil·Free·Dodge·Sidestep·Step·Flex·Stride·Counter·Pledge·Trial·Abyss·Short·Raid·Featured·Challenge Path / Pace 同窗 / 庭内 / TrMenu yml（物品 W1b）
- tip **勿勾 CLOSED** · 可派测 / 物品 W1b

## 落点
- `EmberPhasePath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine · 改路径薄页
- `EmberCommand`：用法改「点半血路径页」
- `EmberRunPapi`：phase_path / phase_path_label / tip / line → Section.RUSH
- `pom.xml` / `plugin.yml`：1.65.856-d13363.local
- 单测 `EmberPhasePathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / auto/ask/mute 半血语义(D586)
- wave_path / soak_path / shell_path / clone_path / press_path / cut_path / rush_path / clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / counter_path / pledge_path / trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / PacePath
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行）

## 下步
- 可派测 · 物品 W1b 交薄页 `ember_p1_phase_path` · **勿勾 tip CLOSED** · 下一窗旁注其它剩支 D587+（Gap…）后置 / PacePath（本仓无）后置

## 证据
- `/workspace/tmp/tip-ef-phase-path/` · jar sha256 见 `00-jar.sha256`
