# STATUS · TIP-DG W1a 团本路径选打菜单化 · D13338

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **CLOSED live** · PASS `7ec45e0a1` · tip `fd80fd696` · 总控收口 2026-10-14 11:29 Asia/Shanghai

## 交付
- `EmberRaidPath.offerPick` →开 `/ember_p1_raid_path`（`openPathPick` / `PATH_PICK_*`）；**禁**借 `ember_p1_raid_settle` ConfirmTokens 当选打主 UI
- 无参 `/corerpg p1 raidpath`（及冒险 Open 同调）→ glance + offerPick →同薄页
- `applyAndReply` 下一步→同薄页 / 进本（路径本） / 工坊 `/ember_p1_forge`；禁借 settle ConfirmTokens 为主 UI
- glance / enterCue / nudge / 用法：清「手打 /corerpg p1 raidpath」主教 →「点团本路径页」
- PAPI（复用+扩）：`%corerpg_p1_raid_path%`/`_label`/`_tip`/`_line%`（经 `EmberRaidService.papi` · RAID）；`raid_settle_*` 零改
- 单测：`EmberRaidPathPickTest` · `EmberRaidPathTest` · `EmberRaidSettleNextLoopTest` · `EmberRunPapiTest` · `EmberChallengePathPickTest` · `EmberFeaturedPathPickTest` 绿
- jar：`1.65.831-d13338.local` · sha256 `2e4eab9ab641…` · feat `c81c9e7e2` · ECONOMY_BV **123** 未动
- play：live `1.65.831-d13338.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**掉落 / 体力价 / 周帽 / 团本语义 / Cast·Aid / variety / 已齐 settle 薄页（含 `ember_p1_raid_settle` / afterClearHit）/ FeaturedPath / ChallengePath / 其他 *Path / 庭内 / TrMenu yml（物品 W1b 并行已交 `b9b8f688f`）
- **勾毕 CLOSED live** · PASS `7ec45e0a1` · 总控收口 2026-10-14 11:29 Asia/Shanghai

## 落点
- `EmberRaidPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/enterCue/menuLine
- `EmberCommand`：nudge/用法改「点团本路径页」
- `EmberRaidService`：path_tip / path_line（既有 path/label 复用）
- `pom.xml` / `plugin.yml`：1.65.831-d13338.local
- 单测 `EmberRaidPathPickTest`（新）· RaidPathTest / SettleNextLoopTest glance 对齐 TIP-DG

## 零改边界
- 掉落 / 体力价 / 周帽 / 团本 resolve 语义 / S表 / 伤表
- raid_settle / afterClearHit ConfirmTokens → settle 薄页（TIP-CZ 保持）
- featured_path / challenge_path / 图身份·深渊·短征·挑战·精选 settle / variety / soft-reward / Cast/Aid / 庭内
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick · FeaturedPath/ChallengePath 同窗
- TrMenu yml（本窗插件未改；W1b 物品并行已交 `ember_p1_raid_path.yml`）

## 下步
- 物品 W1b / 测岗均已齐 · 总控收口 CLOSED

## 测岗抽测（TIP-DG spot）
- **PASS** · 2026-10-14 11:29 Asia/Shanghai · 证据 `/workspace/tmp/tip-dg-raid-path/` · 详 `STATUS-ember-raid-path-offer-pick-tip-dg-spot-d13338-2026-10-14.md`
- live jar `1.65.831-d13338.local` · feat `c81c9e7e2` · tip `fd80fd696` · 批 `fd80fd696` · 团本路径选打菜单化全绿

## 续 · 测岗抽测 PASS（2026-10-14 Asia/Shanghai · 余烬-测试）
- PASS live · feat `c81c9e7e2` · 证据 `/workspace/tmp/tip-dg-raid-path/` · STATUS `7ec45e0a1`
- **CLOSED live**（总控收口）· tip `fd80fd696` · jar `1.65.831-d13338.local`
