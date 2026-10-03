# STATUS · P4 ember_daily「灰烬庭院」

**日期：** 2026-09-27 21:38–21:43（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design/design-ember-daily-maps-p4.md`；参考 P3 `WeeklyCorridorService` / `EliteCorridorService`、`docs/status/STATUS-ember-weekly-elite-maps-p3.md`  
**Verdict：** **✅ P4 完成**（独立庭院动线、刷点开放、菜单去教指令、ops=[]、CoreRpg 1.15.7）

---

## 1. 备份路径

`server-runtime/config-backups/ember_daily-p4-20260927-213835/`

| 内容 | 说明 |
|------|------|
| `map/ember_daily/` | 施工前整份 DP map（level.dat + region） |
| `dungeon/EmberDaily/` · `ember_daily/` | 改前 option/monster（小写占位未改逻辑） |
| `menus/ember_daily.yml` | 改前日菜单 |
| `config-snippets/dp-config-ember_daily.txt` | config 中 `ember_daily → EmberDaily: 2` |

---

## 2. 出生与刷点新坐标（灰烬庭院 · 石砖/砂砾/火盆）

**施工：** `/corerpg dailybuild`（`DailyCourtyardService`，CoreRpg **1.15.7**）→ 临时世界 `ember_daily_build` → 导出 `plugins/DungeonPlus/map/ember_daily`  
**体量：** blocks≈**28940**  
**轮廓：** x=-16..15 × z=-4..27（约 32×32）；回廊宽约 3；中央 Boss 垫 ~8×8 抬高 1；南北拱门；局部开天（无整盘顶盖）

| 角色 | 脚 Y | 坐标 | 说明 |
|------|------|------|------|
| **spawn / 南拱门内** | **65** | **(0, 65, 0)** | 脚下 stonebrick；面向 +Z 庭院 |
| wave1 回廊 | 65 | **(-13, 65, 6)** | 西回廊开放格 · 僵尸×2 |
| wave2 远廊 | 65 | 僵尸 **(-13, 65, 24)** · 骷髅 **(12, 65, 24)** | 北回廊偏远 |
| **Boss 垫中心** | **66** | **(0, 66, 12)** | 蛮兵×1；垫面相对回廊 +1 |

- `$setmap{name=ember_daily}` **未改**（仍 EmberDaily 启动名）  
- 波次数量 / kill / 波末回血 / 通关箱 / `daily_clear` / 票与等级门：**未改数值**  
- 告示：灰烬庭院动线 / 回枢纽（开菜单、未通关不发箱）；**不写** `/dp` `/hub`

---

## 3. 改动文件列表

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../DailyCourtyardService.java` | **新建** 庭院建造 + 导出 |
| `CoreRpg/.../CoreRpgPlugin.java` | 路由 `dailybuild` / `dailycourtyard` / `courtyard` |
| `CoreRpg/pom.xml` · `plugin.yml` | 版本 **1.15.7** |
| `plugins/CoreRpg.jar` | 重编换入（`server-runtime/plugins` 为 symlink，未破坏） |
| `plugins/DungeonPlus/map/ember_daily/region/*.mca` | 灰烬庭院地形 |
| `plugins/DungeonPlus/dungeon/EmberDaily/option.yml` | spawn/teleport → `0,65,0`；开场文案「灰烬庭院·日」 |
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | 三波 location 对齐开放格（数量/条件不变） |
| `plugins/TrMenu/menus/ember_daily.yml` | 标题/庭院 lore；去掉「进本命令：/dp start」tell（底层 `command: dp start` 保留） |
| `plugins/TrMenu/menus/ember_hub.yml` | 日常入口一行 lore：去 `/dp start EmberDaily` 教学 → 庭院意象（进本 action 未动） |
| `docs/status/STATUS-ember-daily-maps-p4.md` | 本文件 |

未改：`dungeon/ember_daily` 小写占位、MM 血攻、票/波次、其他副本菜单扫。

---

## 4. 菜单 / 告示文案变更要点

| 位置 | 改前 | 改后 |
|------|------|------|
| 日菜单 Title | `日常 · 余烬窟` | `日常 · 灰烬庭院` |
| 日菜单 lore | （无庭院句） | `§8灰烬庭院 · 回廊清潮后上中央垫` |
| 日菜单 tell | `进本命令：/dp start EmberDaily` | **删除**；保留 `command: dp start EmberDaily` |
| 枢纽日常 lore | `进本扣余烬日票 · /dp start EmberDaily` | `灰烬庭院 · 回廊清潮后上中央垫` |
| 本内告示 | （旧石盒） | `灰烬庭院` / `回枢纽·打开枢纽菜单` / `中央垫·蛮兵` |

---

## 5. 自检结果与验收命令

| 检查 | 结果 |
|------|------|
| 备份整份 map + EmberDaily + 菜单 | ✅ `ember_daily-p4-20260927-213835` |
| `/corerpg dailybuild` 导出 | ✅ blocks≈28940 → `map/ember_daily` |
| EmberDaily 进本 smoke (`P4sm_*`) | ✅ 落地 **(0, 65, 0)** 脚下 `stonebrick`；开场「灰烬庭院·日」+ 波一文案 |
| 刷点开放（无窒息） | ✅ spawn / w1 / w2a / w2b / boss 均为 under≠air + feet/head=air |
| 主题可辨 | ✅ stonebrick + gravel + cobble + netherrack + glow/sea_lantern；露天庭院 ≠ 周本深廊 / 精英石英 |
| 告示 | ✅ 「灰烬庭院」「回枢纽」「中央垫」；无教打指令 |
| `ops.json` | ✅ **`[]`**（测中临时 OP RpgBot，已 `/deop` + 空档重启） |

痕迹：`/tmp/p4-daily-build-result.json` · `/tmp/p4-daily-smoke-result.json`  
验收：`/corerpg dailybuild`（admin）· `/dp start EmberDaily`（有日票、Lv≥10）

---

## 6. STATUS 路径与 CoreRpg 版本

- STATUS：`docs/status/STATUS-ember-daily-maps-p4.md`  
- CoreRpg：**1.15.7**

---

## 7. 风险 / 未做

1. **实例缓存** — 大改后已短重启并清 `dungeon_EmberDaily_*`；若见旧石盒再清 caches / 重启 play。  
2. **本内 `/tp` 抽样** — adventure 下玩家自 tp 未落到垫上；刷点开放靠同图 `blockAt`（Boss 告示已从出生点可视）。  
3. **工作目录** — `ember_daily_build` 已删。  
4. **未做** P5、全菜单 TrMenu 扫、票扣次审计（B0/B0.1）、MM 数值、commit/push、本内 Ady NPC。  
5. **复跑** — `/corerpg dailybuild`（需 admin）；会重建临时世界并覆盖 `map/ember_daily` region。
