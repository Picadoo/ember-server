# STATUS · B2.2 Ember / DungeonPlus 本地坐标总表

**日期：** 2026-09-28（Asia/Shanghai）
**任务：** `docs/design-ember-content-backlog.md` B2.2
**tip：** `10cdc36`（Boss前压扩线短抽已 PASS）
**范围：** `plugins/DungeonPlus` 当前注册并有实际玩法配置的 Ember 地牢（`server-runtime/plugins/DungeonPlus` → symlink 同 inode）。
**坐标约定：** 下表均为 DP map 的本地 `x,y,z`；`y` 按配置中的玩家/怪物脚位置记录，不把不同 map 的坐标互相混用。
**相对旧表（2026-09-27）改了哪些本：** 日常由单本庭院扩为七线分表，并补齐房2 `wave2a/2b` 链式与五线 `boss_prep`（霜晶/锈轨仍无）；周本中核改为 `wave2a/2b` 链式；精英厅一改为 `wave1→wave1b` 链式；Abyss F2/F7 按消债后双 `$kill` 刷点复核（坐标未变）。

## 0. 口径与来源

- map 注册真相：`plugins/DungeonPlus/config.yml` 的 `dungeon-pre-folder` / 地图绑定。
- 出生真相：各大写地牢目录的 `option.yml` 中 `$setmap`、`$setspawn`，以及开场 `$teleport`。
- 刷点/门/房间跳转真相：各大写地牢目录的 `monster.yml` 中 `$mob{...location=...}`、`$operation-block`、`$teleport`。
- 地图实体文件：`plugins/DungeonPlus/map/<world>/`；本文不从二进制反推未经配置确认的坐标。
- `plugins/DungeonPlus/dungeon/ember_*` 小写目录是 OP 地图编辑壳，不是下表的 live 玩法配置。
- **新增行标注：** 相对旧表新增的 `boss_prep` / 房2 链式行在备注中标「**新增**」。

---

## 1. 日常 · 庭院 `EmberDaily`（Yard）

**DP map / world：** `ember_daily`
**出生点：** `(0,65,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberDaily/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 南拱门内 | 65 | `(0,65,0)` | `$setspawn`；开场 `$teleport`。 |
| wave1 · 回廊双侧 | 65 | `(-5,65,7)` | `EmberDailyZombie ×2`；次点 `(5,65,7)`：`×2`。 |
| door1 | 65 | `x=-1..1,y=65..67,z=13` | wave1.end → AIR×9。 |
| wave2a · 房2对射骷 | 65 | `(-6,65,21)` | **新增** 链式；`EmberDailySkeleton ×1`；次点 `(6,65,21)`：`×1`；start→delay2 wave2b。 |
| wave2b · 房2涌尸 | 65 | `(-6,65,17)` | **新增**；`EmberDailyZombie ×2`；次点 `(6,65,17)`：`×1`；`(0,65,19)`：`×1`。 |
| door2 | 65 | `x=-1..1,y=65..67,z=25` | **新增口径**；wave2b.end → AIR×9 → boss_prep。 |
| boss_prep · 门槛尸 | 65 | `(-3,65,27)` | **新增**；`EmberDailyZombie ×1`；次点 `(3,65,27)`：`×1`。 |
| boss · 中央垫 | 66 | `(0,66,31)` | `EmberDailyBrute ×1`。 |

**关键点：** 房2 链式 + door2→boss_prep→boss；禁 kill-any。

---

## 2. 日常 · 潮蚀 `EmberDailyTide`（Tide）

**DP map / world：** `ember_daily_tide`
**出生点：** `(0,64,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberDailyTide/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 | 64 | `(0,64,0)` | `$setspawn`。 |
| wave1 · 桥头尸 | 64 | `(-2,64,8)` | `EmberDailyTideZombie ×2`；次点 `(2,64,8)`：`×2`。 |
| door1 | 64 | `x=-1..1,y=64..66,z=18` | wave1.end → AIR×9。 |
| wave2a · 房2浪矢 | 64 | `(2,64,28)` | **新增** 链式；`EmberDailyTideSkeleton ×2`；次点 `(-1,64,32)`：`×1`。 |
| wave2b · 房2潮蚀尸 | 64 | `(-2,64,26)` | **新增**；`EmberDailyTideZombie ×2`；次点 `(1,64,34)`：`×1`。 |
| door2 | 64 | `x=-1..1,y=64..66,z=38` | wave2b.end → AIR×9 → boss_prep。 |
| boss_prep · 门槛浪矢 | 64 | `(-3,64,41)` | **新增**；`EmberDailyTideSkeleton ×1`；次点 `(3,64,41)`：`×1`。 |
| boss · 闸厅 | 64 | `(0,64,46)` | `EmberDailyTideBrute ×1`。 |

**关键点：** 与庭院同构：房2 start 链式 + door2→boss_prep→boss。

---

## 3. 日常 · 断塔 `EmberDailySpire`（Spire）

**DP map / world：** `ember_daily_spire`
**出生点：** `(0,64,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberDailySpire/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 | 64 | `(0,64,0)` | `$setspawn`。 |
| wave1 · 底层卫尸 | 64 | `(-5,64,-3)` | `EmberDailySpireZombie ×2`；另 `(5,64,-3)`×1、`(-5,64,2)`×1、`(5,64,2)`×1。 |
| door1 | 64 | `x=-1..1,y=64..66,z=4` | wave1.end → AIR×9。 |
| wave2a · 中层裂隙箭 | 70 | `(-6,70,0)` | **新增** 链式；`EmberDailySpireSkeleton ×2`；次点 `(6,70,0)`：`×2`。 |
| wave2b · 中层卫尸 | 70 | `(0,70,-4)` | **新增**；`EmberDailySpireZombie ×1`；次点 `(-1,69,1)`：`×1`；**环廊防坠内收**（施工 `29fb133`；原 `(0,70,-6)`/`(0,70,2)`）。 |
| door2 | 70 | `x=-1..1,y=70..72,z=4` | wave2b.end → AIR×9 → boss_prep。 |
| boss_prep · 顶台门槛 | 76 | `(-3,76,2)` | **新增**（Boss前压扩线）；`EmberDailySpireZombie ×1`；次点 `(3,76,2)`：`×1`。 |
| boss · 顶台守望 | 76 | `(0,76,0)` | `EmberDailySpireWarden ×1`。 |

**关键点：** 竖向断塔；door2 与 door1 同 `z=4` 不同 Y；boss_prep 在顶台。

---

## 4. 日常 · 焦骨 `EmberDailyAsh`（Ash）

**DP map / world：** `ember_daily_ash`
**出生点：** `(0,65,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberDailyAsh/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 | 65 | `(0,65,0)` | `$setspawn`。 |
| wave1 · 主甬+假岔 | 65 | `(-3,65,8)` | `EmberAshZombie ×2`；`(3,65,8)`×2；假岔 `(8,65,10)`×1、`(7,65,11)`×1。 |
| door1 | 65 | `x=-1..1,y=65..67,z=16` | wave1.end → AIR×9。 |
| wave2a · 房2尸 | 65 | `(-2,65,24)` | **新增** 链式；`EmberAshZombie ×2`；次点 `(0,65,22)`：`×1`。 |
| wave2b · 房2燃矢 | 65 | `(1,65,28)` | **新增**；`EmberAshSkeleton ×2`；次点 `(-1,65,32)`：`×1`。 |
| door2 | 65 | `x=-1..1,y=65..67,z=36` | wave2b.end → AIR×9 → boss_prep。 |
| boss_prep · 鼓室门槛 | 65 | `(-2,65,38)` | **新增**（Boss前压扩线）；`EmberAshSkeleton ×1`；次点 `(2,65,38)`：`×1`。 |
| boss · 鼓室 | 65 | `(0,65,44)` | `EmberAshBrute ×1`。 |

**关键点：** 房1 假岔刷点计入 door1 `$kill`；boss_prep 为燃矢近战贴脸。

---

## 5. 日常 · 地窖 `EmberDailyCrypt`（Crypt）

**DP map / world：** `ember_daily_crypt`
**出生点：** `(0,72,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberDailyCrypt/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 | 72 | `(0,72,0)` | `$setspawn`。 |
| wave1 · 上层窖卫 | 72 | `(-6,72,6)` | `EmberDailyCryptZombie ×2`；另 `(5,72,6)`×1、`(-6,72,14)`×1、`(5,72,14)`×1。 |
| door1 | 72 | `x=-1..1,y=72..74,z=18` | wave1.end → AIR×9。 |
| wave2a · 中层誓印 | 66 | `(-5,66,36)` | **新增** 链式；`EmberDailyCryptSkeleton ×2`；次点 `(4,66,36)`：`×1`。 |
| wave2b · 中层窖卫 | 66 | `(-5,66,30)` | **新增**；`EmberDailyCryptZombie ×2`；次点 `(4,66,30)`：`×1`。 |
| door2 | 66 | `x=-1..1,y=66..68,z=40` | wave2b.end → AIR×9 → boss_prep。 |
| boss_prep · 圆厅门槛 | 60 | `(-3,60,42)` | **新增**（Boss前压扩线）；`EmberDailyCryptSkeleton ×1`；次点 `(3,60,42)`：`×1`。 |
| boss · 圆厅残誓 | 60 | `(0,60,48)` | `EmberDailyCryptWarden ×1`。 |

**关键点：** 多层下降；boss_prep 在 y=60（door2 y=66 下至 Boss）。

---

## 6. 日常 · 霜晶 `EmberDailyFrost`（Frost）

**DP map / world：** `ember_daily_frost`
**出生点：** `(0,70,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberDailyFrost/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 | 70 | `(0,70,0)` | `$setspawn`。 |
| wave1 · 左廊 | 70 | `(-3,70,8)` | `EmberDailyFrostZombie ×2`；次点 `(-3,70,14)`：`×1`；start→wave1b。 |
| wave1b · 右廊 | 70 | `(3,70,8)` | `EmberDailyFrostZombie ×1`；次点 `(3,70,6)`：`×1`；door1 挂 wave1b.end。 |
| door1 | 70 | `x=-1..1,y=70..72,z=16` | wave1b.end → AIR×9。 |
| wave2a · 房2尸 | 70 | `(-4,70,24)` | **新增** 链式；`EmberDailyFrostZombie ×2`；次点 `(0,70,30)`：`×1`。 |
| wave2b · 房2霜矢 | 70 | `(4,70,28)` | **新增**；`EmberDailyFrostSkeleton ×2`；次点 `(4,70,22)`：`×1`。 |
| door2 | 70 | `x=-1..1,y=70..72,z=34` | wave2b.end → AIR×9 → **直接 boss**（无 boss_prep）。 |
| boss_prep | — | **无** | 配置无 `boss_prep` 组；勿造。 |
| boss · 霜厅 | 70 | `(0,70,48)` | `EmberDailyFrostBrute ×1`。 |

**关键点：** 霜晶**仍无** `boss_prep`；房2 链式有，door2 后 delay3 进 boss。

---

## 7. 日常 · 锈轨 `EmberDailyRail`（Rail）

**DP map / world：** `ember_daily_rail`
**出生点：** `(0,64,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberDailyRail/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 | 64 | `(0,64,0)` | `$setspawn`。 |
| wave1 · 轨廊 | 64 | `(-4,64,6)` | `EmberDailyRailZombie ×2`；另 `(4,64,6)`×1、`(0,64,10)`×1、`(-4,64,12)`×1。 |
| door1 | 64 | `x=-1..1,y=64..66,z=15` | wave1.end → AIR×9。 |
| wave2a · 房2主巷 | 64 | `(-5,64,20)` | **新增** 链式；`EmberDailyRailZombie ×2`；次点 `(0,64,22)`：`×1`；start→delay1 wave2b（真侧袭）。 |
| wave2b · 房2支洞矿矢 | 64 | `(8,64,20)` | **新增**；`EmberDailyRailSkeleton ×2`；次点 `(10,64,20)`：`×1`。 |
| door2 | 64 | `x=-1..1,y=64..66,z=33` | wave2b.end → AIR×9 → **直接 boss**（无 boss_prep）。 |
| boss_prep | — | **无** | 配置无 `boss_prep` 组；勿造。 |
| boss · 矿监 | 64 | `(0,64,46)` | `EmberDailyRailWarden ×1`。 |

**关键点：** 锈轨**仍无** `boss_prep`；房2 delay=1 侧袭链式。

---

## 8. `EmberWeekly`（周本 · 深核廊）

**DP map / world：** `ember_weekly`
**出生点：** `(-40,65,270)`
**来源：** `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 前厅入口 | 65 | `(-40,65,270)` | `$setspawn`。 |
| wave1 · 前厅 | 65 | `(-40,65,280)` | `EmberWeeklyZombie ×3`。 |
| wave1 → 中核入口 | 68 | `(-40,68,308)` | 波末玩家 `$teleport`；接 wave2a。 |
| wave2a · 中核尸 | 68 | `(-40,68,308)` | **新增** 链式（原 wave2 拆分）；`EmberWeeklyZombie ×2`；start→delay2 wave2b。 |
| wave2b · 中核骷 | 68 | `(-42,68,312)` | **新增**；`EmberWeeklySkeleton ×3`；与 wave2a **重叠压**（同中核室）。 |
| wave2b → 深室入口 | 63 | `(-40,63,340)` | 波末玩家 `$teleport`；接 wave3。 |
| wave3 · 深室 | 63 | `(-40,63,340)` | `EmberWeeklyBruteA ×1`。 |
| boss · 深室终局 | 63 | `(-40,63,342)` | `EmberWeeklyBruteB ×1`；与 wave3 同一深室。 |

**关键点：** 中核 `wave2a/2b` 链式重叠；传深室挂 `wave2b.end`；禁 kill-any。无 `boss_prep`。

---

## 9. `EmberEliteWeekly`（精英周常）

**DP map / world：** `ember_elite`
**出生点：** `(-35,70,270)`
**来源：** `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml`；`monster.yml`；厅三东门封死见 map MCA / `STATUS-ember-b13-elite-map-check.md`（**非** monster 刷点）。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 试炼入口 | 70 | `(-35,70,270)` | `$setspawn`。 |
| wave1 · 厅一炽尸 | 70 | `(-30,70,270)` | `EmberEliteZombie ×3`；start→delay2 wave1b（**新增** 链式）。 |
| wave1b · 厅一骨刺 | 70 | `(-28,70,272)` | **新增** 链式拆分；`EmberEliteSkeleton ×2`；传厅二挂 wave1b.end。 |
| wave1b → 厅二入口 | 72 | `(-4,72,270)` | 波末玩家 `$teleport`。 |
| wave2 · 厅二 | 72 | `(-4,72,270)` | `EmberEliteBrute ×1`；次点 `(-6,72,268)`：`EmberEliteMix ×2`；双 `$kill` AND。 |
| wave2 → 厅三入口 | 68 | `(22,68,270)` | 波末玩家 `$teleport`。 |
| wave3 / boss · 厅三 | 68 | `(24,68,270)` | `EmberEliteBoss ×1`。 |
| 厅三东门封死 | 68 | `(29,68..71,269..271)` | map 实墙（quartz/gold）；**monster.yml 无相关刷点**；勿当 `$mob` 坐标。 |

**关键点：** 厅一链式消 kill-any；东门封死属 map/MCA，不是 YAML 刷点。

---

## 10. `EmberAbyss`（深渊竖井）

**DP map / world：** `ember_abyss`
**井顶出生点：** `(-40,90,274)`
**来源：** `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml`；`monster.yml`

每层清场后由 `monster.yml` 的 `$teleport` 传到该层主点；表中“次点”是同一层的其他 `$mob location`。F2/F7 已消 `$kill-any` 债，改为分组双 `$kill`（坐标相对旧表未变）。

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 井顶出生 | 90 | `(-40,90,274)` | `$setspawn`；开场传送点。 |
| floor1 · 潮尸 | 85 | `(-38,85,276)` | `EmberAbyssZombie ×3`。 |
| floor2 · 混潮/骨潮 | 80 | `(-42,80,264)` | **消债后**：主点 `EmberAbyssMix ×2`；次点 `(-38,80,264)`：`EmberAbyssSkeleton ×2`；双 `$kill`（非 kill-any）。 |
| floor3 · 骨潮 | 75 | `(-38,75,276)` | `EmberAbyssSkeleton ×3`。 |
| floor4 · 蛮层/潮尸 | 70 | `(-42,70,264)` | 主点 `EmberAbyssBrute ×1`；次点 `(-38,70,264)`：`EmberAbyssZombie ×1`。 |
| floor5 · 看守 | 65 | `(-38,65,276)` | `EmberAbyssWatcher ×1`。 |
| floor6 · 潮尸 | 60 | `(-42,60,264)` | `EmberAbyssZombie ×3`。 |
| floor7 · 混潮/骨潮 | 55 | `(-38,55,276)` | **消债后**：主点 `EmberAbyssMix ×2`；次点 `(-42,55,276)`：`EmberAbyssSkeleton ×2`；双 `$kill`。 |
| floor8 · 看守/混潮 | 50 | `(-42,50,264)` | 主点 `EmberAbyssWatcher ×1`；次点 `(-38,50,264)`：`EmberAbyssMix ×1`。 |
| floor9 · 蛮层/潮尸 | 45 | `(-38,45,276)` | 主点 `EmberAbyssBrute ×1`；次点 `(-42,45,276)`：`EmberAbyssZombie ×2`。 |
| floor10 · 深看守/混潮 | 38 | `(-42,38,264)` | 主点 `EmberAbyssWatcherDeep ×1`；次点 `(-38,38,264)`：`EmberAbyssMix ×1`。 |
| floor11 · 潮尸 | 33 | `(-38,33,276)` | `EmberAbyssZombie ×4`。 |
| floor12 · 最终看守/骨潮 | 28 | `(-42,28,264)` | 主点 `EmberAbyssWatcherDeep ×1`；次点 `(-38,28,264)`：`EmberAbyssSkeleton ×2`；顶层 COMPLETE，无独立 Boss 坐标。 |

**关键点：** floor1–12；Y 以配置为准；F2/F7 刷点坐标与旧表一致，口径改为双 `$kill`。

---

## 11. `EmberCalamity`

**DP map / world：** `ember_calamity`
**DP 测试出生点：** `(-40,65,270)`
**来源：** `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 测试入口 | 65 | `(-40,65,270)` | `$setspawn`；OP 调试入口。 |
| boss · 测试祭坛房 | 65 | `(-41,65,272)` | `EmberCalamityBoss ×1`；当前唯一刷点。 |

**不要混用正式公共窗坐标：** 正式灾厄在 `plugins/CoreRpg/calamity.yml` / world `ember_event`。

---

## 12. `EmberRaid`

**DP map / world：** `ember_raid`
**出生点：** `(0,65,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberRaid/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 集结大厅 | 65 | `(0,65,0)` | `$setspawn`。 |
| wave1 · 左道 | 65 | `(18,65,-7)` | `EmberRaidFootman ×6`。 |
| wave2 · 右道 | 65 | `(22,65,7)` | 主点 `EmberRaidArcher ×4`；同波左侧次点 `(18,65,-7)`：`EmberRaidFootman ×3`。 |
| wave3 · 汇合门前 | 65 | `(30,65,0)` | `EmberRaidElite ×1` + `EmberRaidFootman ×3`。 |
| boss · 终厅 | 66 | `(40,66,0)` | `EmberRaidBoss ×1`。 |

---

## 13. `EmberGuildBoss`

**DP map / world：** `ember_raid`（与 `EmberRaid` 共享 map）
**出生点：** `(0,65,0)`
**来源：** `plugins/DungeonPlus/dungeon/EmberGuildBoss/option.yml`；`monster.yml`

| 名称 | Y | 主刷点 / 锚点 | 备注 |
|---|---:|---|---|
| 出生 / 集结大厅 | 65 | `(0,65,0)` | `$setspawn`。 |
| wave1 · 潮与骨 | 65 | `(4,65,-6)` | `EmberAbyssZombie ×5`；次点 `(4,65,6)`：`EmberAbyssSkeleton ×3`。 |
| wave2 · 精英 | 65 | `(20,65,0)` | `EmberAbyssBrute ×1` + `EmberAbyssZombie ×3`。 |
| boss · 终厅 | 66 | `(40,66,0)` | `EmberGuildCalamity ×1`。 |

**关键点：** 勿把 `EmberAbyss*` 怪物名误当成 `EmberAbyss` 竖井坐标。

---

## 14. 明确未纳入的编辑壳 / 未知项

- 小写 `ember_*` 编辑/回退壳不在 live 玩法表中；正式出生/刷点：**❓**。
- 霜晶 / 锈轨：**确认无 `boss_prep`**（配置无该组）。
- 精英厅三东门封死是 map 实墙，不是 monster 刷点。
- 本文只统一 DP 配置显式写出的锚点；地图二进制中未命名装饰/告示仍为 **❓**。

## 15. 变更与验收

- 本次**只改**本 STATUS 文档；不改玩法 YAML、jar、secret、ops、region 或 world binary。
- 验收：日常七线均有出生 / wave2a·2b（若有）/ door2 / boss_prep（有则写、无则标「无」）/ Boss；周本中核链式；精英厅一链式；Abyss F2/F7 消债刷点；坐标均来自 live `option.yml` + `monster.yml`。
