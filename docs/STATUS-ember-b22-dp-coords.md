# STATUS · B2.2 Ember / DungeonPlus 本地坐标总表

**日期：** 2026-09-27（Asia/Shanghai）
**任务：** `docs/design-ember-content-backlog.md` B2.2
**范围：** `plugins/DungeonPlus` 当前注册并有实际玩法配置的 Ember 地牢。
**坐标约定：** 下表均为 DP map 的本地 `x,y,z`；`y` 按配置中的玩家/怪物脚位置记录，不把不同 map 的坐标互相混用。

## 0. 口径与来源

- map 注册真相：`plugins/DungeonPlus/config.yml` 的 `dungeon-pre-folder` / 地图绑定（`ember_daily`、`ember_weekly`、`ember_elite`、`ember_abyss`、`ember_calamity`、`ember_raid`）。
- 出生真相：各大写地牢目录的 `option.yml` 中 `$setmap`、`$setspawn`，以及开场 `$teleport`。
- 刷点/房间跳转真相：各大写地牢目录的 `monster.yml` 中 `$mob{...location=...}` 与 `$teleport{location=...}`。
- 地图实体文件：`plugins/DungeonPlus/map/<world>/`；它们是二进制 map 数据，本文不从二进制反推未经配置确认的坐标。
- `plugins/DungeonPlus/dungeon/ember_*` 小写目录是 OP 地图编辑壳（`option.yml` 只有 `$setmap`、无正式刷怪/出生流程），不是下表的 live 玩法配置；若将来重新启用，缺失项标为 **❓**，应回看对应小写 `option.yml` / `monster.yml`，不要沿用本表大写地牢的坐标。

## 1. `EmberDaily`

**DP map / world：** `ember_daily`
**出生点：** `(0,65,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberDaily/option.yml`；`plugins/DungeonPlus/dungeon/EmberDaily/monster.yml`；`docs/STATUS-ember-daily-maps-p4.md`。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 南拱门内 | 65 | `(0,65,0)` | `$setspawn`；开场玩家也传到此处，面向庭院。 |
| wave1 · 回廊清潮 | 65 | `(-13,65,6)` | `EmberDailyZombie ×2`。 |
| wave2 · 北侧远廊 | 65 | `(-13,65,24)` | `EmberDailyZombie ×1`；同波第二刷点 `(12,65,24)`：`EmberDailySkeleton ×2`。 |
| boss · 中央垫 | 66 | `(0,66,12)` | `EmberDailyBrute ×1`；Boss 垫比回廊高 1。 |

**关键点：** 刷点为回廊 → 远廊 → 中央 Boss 垫；没有额外房间 TP。数量、波次和条件以 `monster.yml` 为准，本文不改数值。

## 2. `EmberWeekly`

**DP map / world：** `ember_weekly`
**出生点：** `(-40,65,270)`
**来源：** `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml`；`plugins/DungeonPlus/dungeon/EmberWeekly/monster.yml`；`docs/STATUS-ember-weekly-elite-maps-p3.md`。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 前厅入口 | 65 | `(-40,65,270)` | `$setspawn`；开场传送点。 |
| wave1 · 前厅 | 65 | `(-40,65,280)` | `EmberWeeklyZombie ×3`。 |
| wave1 → wave2 · 中核入口 | 68 | `(-40,68,308)` | 波末玩家 `$teleport`；wave2 主刷点。 |
| wave2 · 中核 | 68 | `(-40,68,308)` | `EmberWeeklyZombie ×2`；次点 `(-42,68,312)`：`EmberWeeklySkeleton ×3`。 |
| wave2 → wave3 · 深室入口 | 63 | `(-40,63,340)` | 波末玩家 `$teleport`；wave3 主刷点。 |
| wave3 · 深室 | 63 | `(-40,63,340)` | `EmberWeeklyBruteA ×1`。 |
| boss · 深室终局 | 63 | `(-40,63,342)` | `EmberWeeklyBruteB ×1`；与 wave3 同一深室。 |

**关键点：** 本地轴向为前厅 → 中核 → 深室（大致 `+Z`）；两次房间跳转的确切点见 `monster.yml`。

## 3. `EmberEliteWeekly`

**DP map / world：** `ember_elite`
**出生点：** `(-35,70,270)`
**来源：** `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml`；`plugins/DungeonPlus/dungeon/EmberEliteWeekly/monster.yml`；`docs/STATUS-ember-weekly-elite-maps-p3.md`；`docs/STATUS-ember-b13-elite-map-check.md`。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 试炼入口 | 70 | `(-35,70,270)` | `$setspawn`；开场传送点。 |
| wave1 · 厅一 | 70 | `(-30,70,270)` | `EmberEliteZombie ×3`；次点 `(-28,70,272)`：`EmberEliteSkeleton ×2`。 |
| wave1 → wave2 · 厅二入口 | 72 | `(-4,72,270)` | 波末玩家 `$teleport`；wave2 主刷点。 |
| wave2 · 厅二 | 72 | `(-4,72,270)` | `EmberEliteBrute ×1`；次点 `(-6,72,268)`：`EmberEliteMix ×2`。 |
| wave2 → wave3 · 厅三入口 | 68 | `(22,68,270)` | 波末玩家 `$teleport`；进入 Boss 厅，非怪物刷点。 |
| wave3 / boss · 厅三 | 68 | `(24,68,270)` | `EmberEliteBoss ×1`；Boss 刷在开放中央垫。 |

**关键点：** 精英使用独立 `ember_elite`，不要回填为 `ember_weekly`；厅一 → 厅二 → 厅三的跳转点和 Boss 点必须保持同一套局部坐标。

## 4. `EmberAbyss`

**DP map / world：** `ember_abyss`
**井顶出生点：** `(-40,90,274)`
**来源：** `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml`；`plugins/DungeonPlus/dungeon/EmberAbyss/monster.yml`；`docs/STATUS-ember-abyss-maps-p2.md`；`docs/design-stage4-abyss-9-12.md`。

每层清场后由 `monster.yml` 的 `$teleport` 传到该层主点；表中“次点”是同一层的其他 `$mob location`，不是新的出生点。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 井顶出生 | 90 | `(-40,90,274)` | `$setspawn`；开场传送点；不属于 floor1 刷怪点。 |
| floor1 · 潮尸 | 85 | `(-38,85,276)` | `EmberAbyssZombie ×3`。 |
| floor2 · 混潮/骨潮 | 80 | `(-42,80,264)` | 主点 `EmberAbyssMix ×2`；次点 `(-38,80,264)`：`EmberAbyssSkeleton ×2`。 |
| floor3 · 骨潮 | 75 | `(-38,75,276)` | `EmberAbyssSkeleton ×3`。 |
| floor4 · 蛮层/潮尸 | 70 | `(-42,70,264)` | 主点 `EmberAbyssBrute ×1`；次点 `(-38,70,264)`：`EmberAbyssZombie ×1`。 |
| floor5 · 看守 | 65 | `(-38,65,276)` | `EmberAbyssWatcher ×1`。 |
| floor6 · 潮尸 | 60 | `(-42,60,264)` | `EmberAbyssZombie ×3`。 |
| floor7 · 混潮/骨潮 | 55 | `(-38,55,276)` | 主点 `EmberAbyssMix ×2`；次点 `(-42,55,276)`：`EmberAbyssSkeleton ×2`。 |
| floor8 · 看守/混潮 | 50 | `(-42,50,264)` | 主点 `EmberAbyssWatcher ×1`；次点 `(-38,50,264)`：`EmberAbyssMix ×1`。 |
| floor9 · 蛮层/潮尸 | 45 | `(-38,45,276)` | 主点 `EmberAbyssBrute ×1`；次点 `(-42,45,276)`：`EmberAbyssZombie ×2`。 |
| floor10 · 深看守/混潮 | 38 | `(-42,38,264)` | 主点 `EmberAbyssWatcherDeep ×1`；次点 `(-38,38,264)`：`EmberAbyssMix ×1`。 |
| floor11 · 潮尸 | 33 | `(-38,33,276)` | `EmberAbyssZombie ×4`。 |
| floor12 · 最终看守/骨潮 | 28 | `(-42,28,264)` | 主点 `EmberAbyssWatcherDeep ×1`；次点 `(-38,28,264)`：`EmberAbyssSkeleton ×2`；floor12 是当前顶层 COMPLETE，配置没有另一个独立 Boss 坐标。 |

**关键点：** 当前脚本是 floor1–12；层间主点的 Y 由配置明确给出，不能用“约 5 格”替代实际值（floor9–12 尤其以 `monster.yml` 为准）。

## 5. `EmberCalamity`

**DP map / world：** `ember_calamity`
**DP 测试出生点：** `(-40,65,270)`
**来源：** `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml`；`plugins/DungeonPlus/dungeon/EmberCalamity/monster.yml`；`docs/STATUS-ember-calamity-raid-maps-p5.md`。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 测试入口 | 65 | `(-40,65,270)` | `$setspawn`；此 DP 实例是 OP 调试入口。 |
| boss · 测试祭坛房 | 65 | `(-41,65,272)` | `EmberCalamityBoss ×1`；当前 DP `monster.yml` 唯一刷点。 |

**不要混用正式公共窗坐标：** 正式灾厄不是这个 DP map。其 `plugins/CoreRpg/calamity.yml` 使用 world `ember_event`，Boss `(-96.5,64,266.5)`；P5 STATUS 记录玩家 MV 入口 `(-96.5,65,282.5)`。这两组点分别属于 `ember_event` 公共窗和 `ember_calamity` DP 测本。

## 6. `EmberRaid`

**DP map / world：** `ember_raid`
**出生点：** `(0,65,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberRaid/option.yml`；`plugins/DungeonPlus/dungeon/EmberRaid/monster.yml`；`docs/STATUS-ember-calamity-raid-maps-p5.md`。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 集结大厅 | 65 | `(0,65,0)` | `$setspawn`；开场传送点。 |
| wave1 · 左道 | 65 | `(18,65,-7)` | `EmberRaidFootman ×6`。 |
| wave2 · 右道 | 65 | `(22,65,7)` | 主点 `EmberRaidArcher ×4`；同波左侧次点 `(18,65,-7)`：`EmberRaidFootman ×3`。 |
| wave3 · 汇合门前 | 65 | `(30,65,0)` | `EmberRaidElite ×1` + `EmberRaidFootman ×3`。 |
| boss · 终厅 | 66 | `(40,66,0)` | `EmberRaidBoss ×1`；终厅中央 Boss 垫。 |

**关键点：** 玩法动线为大厅 → `-Z` 左道 / `+Z` 右道 → 汇合 → 终厅；`wave2` 的两个刷点不要合并成一个点。

## 7. `EmberGuildBoss`

**DP map / world：** `ember_raid`（与 `EmberRaid` 共享 map，非独立 world）
**出生点：** `(0,65,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberGuildBoss/option.yml`；`plugins/DungeonPlus/dungeon/EmberGuildBoss/monster.yml`；`docs/STATUS-ember-calamity-raid-maps-p5.md`。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 集结大厅 | 65 | `(0,65,0)` | `$setspawn`；开场传送点。 |
| wave1 · 潮与骨 | 65 | `(4,65,-6)` | `EmberAbyssZombie ×5`；次点 `(4,65,6)`：`EmberAbyssSkeleton ×3`。 |
| wave2 · 精英 | 65 | `(20,65,0)` | `EmberAbyssBrute ×1` + `EmberAbyssZombie ×3`。 |
| boss · 终厅 | 66 | `(40,66,0)` | `EmberGuildCalamity ×1`；复用团本终厅 Boss 垫。 |

**关键点：** 公会 Boss 的 `$setmap`、出生和终厅均沿用 `ember_raid`；不要把其 `EmberAbyss*` 怪物名误当成 `EmberAbyss` 竖井坐标。

## 8. 明确未纳入的编辑壳 / 未知项

- `plugins/DungeonPlus/dungeon/ember_daily/`、`ember_weekly/`、`ember_abyss/`、`ember_calamity/`、`ember_raid/`、`ember_arena/` 是小写编辑/回退壳；它们不在当前 live 玩法表中。小写壳的正式出生、房间和刷点：**❓**；查对应 `option.yml` / `monster.yml`，不要猜坐标。
- `EmberCalamity` 的正式玩家入口属于 `ember_event` 公共窗，不属于 DP 本地 map；公共窗继续查 `plugins/CoreRpg/calamity.yml` 与 `plugins/Multiverse-Core/worlds.yml`。本 STATUS 已列出配置中实际可确认的 Boss/MV spawn 点。
- 本文只统一 DP 配置显式写出的锚点；地图二进制中未被 option/monster/status 明确命名的装饰、告示牌、刷怪器位置仍为 **❓**，查 `plugins/DungeonPlus/map/<world>/` 的地图施工/验收记录，禁止凭图猜数。

## 9. 变更与验收

- 本次只新增本 STATUS 文档；不改 YAML、jar、secret、ops、region 或 world binary。
- 验收重点：每个 live 大写地牢均有 map/world、`$setspawn`、房间/楼层主点、Boss 点（若配置存在）及来源；不明坐标使用 **❓**，没有用估算数补齐。
