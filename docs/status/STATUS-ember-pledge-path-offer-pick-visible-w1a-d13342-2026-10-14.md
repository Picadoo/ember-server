# STATUS · TIP-DK W1a 誓约路径选打菜单化 · D13342

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **CLOSED live** · PASS `14a003c56` · tip `6ee232766` · 总控收口 2026-10-14 12:41 Asia/Shanghai

## 交付
- feat: `42fc81f49`
- `EmberPledgePath.offerPick` →开 `/ember_p1_pledge_path`（`openPathPick` / `PATH_PICK_*`）；**禁** ConfirmTokens 教 `pledgepath` 为主 UI
- 无参 `/corerpg p1 pledgepath` + 周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ06` →同薄页（经 offerPick）
- `applyAndReply` 下一步→同薄页 `/ember_p1_pledge_path` / 誓约页 `/ember_p1_pledge` / 冒险 `/ember_p1_adventure`；**禁**以裸 `pledgepath`/`pledge`/`modes` ConfirmTokens 为主 UI
- glance / nudge / 用法：清「手打 /corerpg p1 pledgepath …」主教 →「点誓约路径页」
- PAPI：`%corerpg_p1_pledge_path%`/`_label`/`_tip`/`_line`（`menuLine`）；`pledge_ok` 既有未改
- 单测：`EmberPledgePathPickTest`（新）· `EmberPledgePathTest` · `EmberTrialPathPickTest` · `EmberRunPapiTest` 绿
- jar：`1.65.835-d13342.local` · sha256 `3ab2d2190517…` · ECONOMY_BV **123** 未动
- play：live `1.65.835-d13342.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**伤害 / CD 表 / 掉落 / 体力价 / 誓约规则语义(D481) / Cast·Aid / variety / 已齐 settle 薄页 / Trial·Abyss·Short·Raid·Featured·Challenge Path / Counter·Stride / 庭内 / TrMenu yml（物品 W1b 并行已交 `fb95767dd`）
- **勾毕 CLOSED live** · PASS `14a003c56` · 总控收口 2026-10-14 12:41 Asia/Shanghai

## 落点
- `EmberPledgePath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/menuLine
- `EmberCommand`：nudge/用法改「点誓约路径页」
- `EmberRunPapi`：pledge_path / pledge_path_label / tip / line → Section.RUSH（先于一般 pledge_*）
- `pom.xml` / `plugin.yml`：1.65.835-d13342.local
- 单测 `EmberPledgePathPickTest`（新）

## 零改边界
- 伤害 / CD 表 / 掉落 / 体力价 / 誓约 lean/reverse pool 与徽记逻辑(D481)
- trial_path / short_path / raid_path / featured_path / challenge_path / abyss_path / 已齐 settle
- variety / soft-reward / Cast/Aid / 庭内 / Counter·Stride *Path
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品并行 `ember_p1_pledge_path` · `fb95767dd`）

## 下步
- 物品 W1b / 测岗均已齐 · 总控收口 CLOSED

## 测岗抽测（TIP-DK spot）
- **PASS** · 2026-10-14 12:41 Asia/Shanghai · 证据 `/workspace/tmp/tip-dk-pledge-path/` · 详 `STATUS-ember-pledge-path-offer-pick-tip-dk-spot-d13342-2026-10-14.md`
- live jar `1.65.835-d13342.local` · feat `42fc81f49` · tip `6ee232766` · 批 `6ee232766` · 誓约路径选打菜单化全绿

## 续 · 测岗抽测 PASS（2026-10-14 Asia/Shanghai · 余烬-测试）
- PASS live · feat `42fc81f49` · 证据 `/workspace/tmp/tip-dk-pledge-path/` · STATUS `14a003c56`
- **CLOSED live**（总控收口）· tip `6ee232766` · jar `1.65.835-d13342.local`
