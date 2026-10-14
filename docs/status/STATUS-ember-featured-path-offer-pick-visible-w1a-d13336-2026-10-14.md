# STATUS · TIP-DE W1a 精选路径选打菜单化 · D13336

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交待测** · 勿勾 tip CLOSED

## 交付
- `EmberFeaturedPath.offerPick` →开 `/ember_p1_featured_path`（`openPathPick` / `PATH_PICK_*`）；禁 ConfirmTokens 教 `featurepath challenge|normal|clear` 为主 UI
- 无参 `/corerpg p1 featurepath`（及冒险 Open 同调）→ glance + offerPick →同薄页
- `applyAndReply` 下一步→同薄页 / 冒险 `/ember_p1_adventure` / 工坊 `/ember_p1_forge`；禁裸 enter / featurepath / runs 为主 UI
- glance / enterCue：清「手打 /corerpg p1 featurepath」主教 →「点精选路径页」
- PAPI（对齐物品薄页）：`%corerpg_p1_featured_path%/_label/_tip/_line%`（经 `EmberRunPapi` ROTATION）；既有 `featured` / `featured_key` / `access_q01` / `q07done` 复用
- 单测：`EmberFeaturedPathPickTest` · `EmberFeaturedPathTest` · `EmberFeaturedSettle*Test` · `EmberRunPapiTest` 绿
- jar：`1.65.829-d13336.local` · sha256 `05551d1620bb…` · feat `1465d7622` · ECONOMY_BV **123** 未动
- play：live `1.65.829-d13336.local` · storage=mysql · bv123 · MySQL connected（装 jar + restart play 验证）
- **零改**掉落 / 体力价 / 周帽数值 / 精选语义 / Cast·Aid / variety / 已齐 settle 薄页（含 `ember_p1_featured_settle`）/ 庭内 / 其他 *Path.offerPick / TrMenu yml（物品 W1b 并行已交）
- **未勾** tip CLOSED · **未派测**（总控派测）

## 落点
- `EmberFeaturedPath`：PATH_PICK_* · offerPick→openPathPick · applyAndReply 下一步菜单 · glance/enterCue/menuLine
- `EmberCommand`：nudge 文案改「点精选路径页」
- `EmberRunPapi`：featured_path_*
- `pom.xml` / `plugin.yml`：1.65.829-d13336.local
- 单测 `EmberFeaturedPathPickTest`（新）

## 零改边界
- 掉落 / 体力价 / 周帽数值 / 精选语义 / S表 / 伤表
- featured_settle / 图身份·深渊·短征·团本·挑战 settle 薄页 / variety / soft-reward / Cast/Aid / 庭内
- balance_version 123 · sx20 · Pack6 · 开K3 · legacy evacuate · 全族 *Path.offerPick
- TrMenu yml（本窗插件未改；W1b 物品已交 `ember_p1_featured_path.yml`）

## 下步
- 测岗抽测 PASS live → 总控收口 CLOSED
