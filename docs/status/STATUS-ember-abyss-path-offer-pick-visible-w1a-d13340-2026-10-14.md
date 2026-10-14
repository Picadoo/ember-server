# STATUS · TIP-DI W1a 深渊路径选打菜单化 · D13340

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · 勿勾 tip CLOSED · 待测岗抽测

## 交付
- `EmberAbyssPath.offerPick` →开 `/ember_p1_abyss_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `abysspath` 为主 UI
- 无参 `/corerpg p1 abysspath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ07` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 / 深渊表 `/ember_p1_abyss` / 冒险 `/ember_p1_adventure`；**禁**以裸 `abysspath`/`abyss` ConfirmTokens 为主 UI
- glance / enterCue（AbyssService）/ nudge / 用法：清「手打 /corerpg p1 abysspath …」主教 →「点深渊路径页」
- PAPI：复用 `%corerpg_p1_abyss_path%`/`_label`；扩 `%corerpg_p1_abyss_path_tip%`/`_line%`（`menuLine`）
- 单测：`EmberAbyssPathPickTest`（新）· `EmberAbyssPathTest` · `EmberAbyssSettleNextLoopTest` · `EmberRunPapiTest` 绿
- jar：`1.65.833-d13340.local` · sha256 `9e9754e7de9f…` · ECONOMY_BV **123** 未动
- play：live `1.65.833-d13340.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**层费 / 强度 / 掉落 / 体力价 / 层表 / Cast·Aid / variety / 已齐 settle 薄页（含 `ember_p1_abyss_settle`）/ Short·Raid·Featured·Challenge Path / TrialPath / 庭内 / TrMenu yml（物品 W1b 并行）
- **勿勾 tip CLOSED** · 待测岗 PASS 后总控收口

## 落点
- `EmberAbyssPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine
- `EmberCommand`：nudge/用法改「点深渊路径页」
- `EmberAbyssService`：未选路径 cue 改「点深渊路径页」
- `EmberRunPapi`：abyss_path_tip / abyss_path_line
- `pom.xml` / `plugin.yml`：1.65.833-d13340.local
- 单测 `EmberAbyssPathPickTest`（新）

## 零改边界
- 层费 / 强度 / 掉落 / 体力价 / 层表 / 深渊语义
- abyss_settle / settle「下一环」SETTLE_NEXT_LOOP_CMD → 薄页（TIP-DB 保持）
- short_path / raid_path / featured_path / challenge_path / 图身份 settle / variety / soft-reward / Cast/Aid / 庭内
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick · TrialPath 同窗
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_abyss_path`）

## 下步
- 物品 W1b / 测岗抽测 → 总控收口 CLOSED
