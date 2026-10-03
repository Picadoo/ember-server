# STATUS · P3 ember_weekly 深核廊 + ember_elite 试炼短廊

**日期：** 2026-09-27 21:15–21:25（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design/design-ember-multiworld-maps.md` §2 周常/精英 · §4.2「精英≠周本房」；参考 P2 `/corerpg abyssbuild`；4.4 `docs/status/STATUS-ember-elite-weekly-4.4.md`  
**Verdict：** **✅ P3 完成**（周本三室深核廊、精英独立 map、setmap/刷点/config 绑定、进本 smoke、ops=[]）

---

## 1. 备份路径

`server-runtime/config-backups/ember_weekly-elite-p3-20260927-211549/`

| 内容 | 说明 |
|------|------|
| `map/ember_weekly/` | 施工前整份 DP map（level.dat + region） |
| `dungeon/EmberWeekly/` · `EmberEliteWeekly/` | 改前 option + monster + task |
| `config-snippets/dp-config-maps.yml` | 改前地图绑定（weekly 含 Elite） |
| `config-snippets/ember_hub-weekly-elite.yml` · `ember_weekly.yml` | 菜单片段 |

---

## 2. weekly 三室坐标与刷点（深核廊 · 暗色石砖/下界）

**中轴：** `CX=-40`，沿 **+Z**  
**施工：** `/corerpg weeklybuild`（`WeeklyCorridorService`，CoreRpg **1.15.5**）→ 临时世界 `ember_weekly_build` → 导出 `plugins/DungeonPlus/map/ember_weekly`  
**体量：** blocks≈**69543**

| 角色 | 脚 Y | 坐标 | 材质带 |
|------|------|------|--------|
| **spawn / 前厅入口** | **65** | **(-40, 65, 270)** | 石砖/下界砖混 |
| 前厅中心 / wave1 | 65 | 主 **(-40,65,280)** · 次 (-42,65,282) | 同上 + 半墙掩体 |
| 折角廊1（西折） | 65→68 | 经 x≈-52 爬升 | 下界砖廊 + 柱灯 |
| **中核** / wave2 | **68** | 主 **(-40,68,308)** · 次 (-42,68,312) | 下界砖台 |
| 折角廊2（东折） | 68→63 | 经 x≈-28 下降 | 同上 |
| **深室** / wave3 | **63** | **(-40,63,340)** | 下界/黑曜石 |
| boss | 63 | **(-40,63,342)** | 黑曜石垫 |

- 波末 `$teleport`：wave1→中核 `(-40,68,308)`；wave2→深室 `(-40,63,340)`  
- kill / 治疗 / COMPLETE 奖励脚本：**保留**；**未改 MM 血伤掉落**  
- 告示：回枢纽 / 前厅→中核→深室 / 通关语义；**不写打指令**

---

## 3. elite 新图与 setmap（试炼短廊 · 石英/金砖中轴）

**新建 map：** `plugins/DungeonPlus/map/ember_elite/`（自 weekly 壳复制后 `/corerpg elitebuild` 重做）  
**中轴：** `CZ=270`，沿 **+X**（与周本 +Z 可辨）  
**施工：** `EliteCorridorService` · blocks≈**65967**

| 角色 | 脚 Y | 坐标 | 材质带 |
|------|------|------|--------|
| **spawn** | **70** | **(-35, 70, 270)** | 金砖脚垫 + 石英 |
| 厅一 / wave1 | 70 | 主 **(-30,70,270)** · 次 (-28,70,272) | 石英厅 + 金中轴 |
| 折角廊1（南折） | 70→72 | 经 z≈260 | 石英廊 |
| **厅二** / wave2 | **72** | 主 **(-4,72,270)** · 次 (-6,72,268) | 石英/金 |
| 折角廊2（北折） | 72→68 | 经 z≈280 | 同上 |
| **厅三** / boss | **68** | wave3 区 (22,68,270) · boss **(24,68,270)** | 金砖 Boss 垫 |

**EmberEliteWeekly setmap 变更：**

| 项 | 改前 | 改后 |
|----|------|------|
| `$setmap` | `ember_weekly` | **`ember_elite`** |
| `$setspawn` / 进本 teleport | `-40,65,270` | **`-35,70,270`** |

波次逻辑 / kill / heal / COMPLETE 奖励：**保留**；未改 MM。

---

## 4. config 绑定

`plugins/DungeonPlus/config.yml`：

```yaml
  ember_weekly:
    "EmberWeekly": 1
  ember_elite:
    "EmberEliteWeekly": 1
```

（改前 `ember_weekly` 同时绑 `EmberEliteWeekly: 1`，已拆出。）

---

## 5. 自检与 ops

| 检查 | 结果 |
|------|------|
| 备份整份 weekly map + 两本 dungeon + config 片段 | ✅ `ember_weekly-elite-p3-20260927-211549` |
| `/corerpg weeklybuild` 导出 | ✅ blocks≈69543 → `map/ember_weekly` |
| `/corerpg elitebuild` 导出 | ✅ blocks≈65967 → `map/ember_elite` |
| EmberWeekly 进本 smoke (`P3sm_*`) | ✅ 落地 **(-40,65,270)** 脚下 `stonebrick`；告示「回枢纽」「深核·周」 |
| EmberEliteWeekly 进本 smoke (`P3el_*`) | ✅ 落地 **(-35,70,270)** 脚下 `gold_block`；邻域 quartz×47 gold×7；告示「精英试炼」「回枢纽」；波一文案出 |
| 主题可辨 | ✅ 周本暗色石砖/下界 vs 精英石英/金砖中轴；轴向 +Z vs +X |
| TrMenu 周常/精英进本 | ✅ 语义保留（hub→周常菜单 / `corerpg elite start`）；通关仍 `mvtp ember_hub` |
| 菜单 lore | ✅ 去掉 hub/周常「教打 `/dp start`」句（进本 action 未改） |
| `ops.json` | ✅ **`[]`**（测中临时 OP RpgBot，已 `/deop` + 空档重启） |

痕迹：`/tmp/p3-weekly-build-result.json` · `/tmp/p3-elite-build-result.json` · `/tmp/p3-smoke-result.json` · `/tmp/p3-elite-smoke-result.json`

---

## 6. STATUS 路径

`docs/status/STATUS-ember-weekly-elite-maps-p3.md`

---

## 7. 风险 / 未做

1. **实例缓存** — DP 启动预缓存 map；大改后本轮已短重启；若见旧地形再清 `dungeon-caches` / 重启 play。  
2. **折角廊可走性** — 建造保证 3×3 开敞；未做全程 bot 走廊验收，若卡角可再收紧墙厚。  
3. **波间 teleport** — 跳过廊道直达下一室（可选增强；玩法不依赖走廊）。  
4. **告示朝向** — `SIGN_POST` 默认朝向，远处可读性一般。  
5. **工作目录** — `ember_weekly_build` / `ember_elite_build` 已删；`ember_abyss_build`（P2）可能仍在，不影响模板。  
6. **未做** 日/灾厄/团本美化、MM 数值、票价/NI、afk_caps、commit/push。  
7. **复跑** — `/corerpg weeklybuild` · `/corerpg elitebuild`（需 admin）；会重建临时世界并覆盖对应 map region。

---

## 8. 变更文件清单

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../WeeklyCorridorService.java` | **新建** 深核廊建造 + 导出 |
| `CoreRpg/.../EliteCorridorService.java` | **新建** 试炼短廊建造 + 导出 |
| `CoreRpg/.../CoreRpgPlugin.java` | 路由 `weeklybuild` / `elitebuild` |
| `CoreRpg/pom.xml` · `plugin.yml` | 版本 **1.15.5** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `plugins/DungeonPlus/map/ember_weekly/region/*.mca` | 深核廊地形 |
| `plugins/DungeonPlus/map/ember_elite/` | **新建** 试炼短廊 map |
| `plugins/DungeonPlus/dungeon/EmberWeekly/monster.yml` | 三室刷点 + 波间 teleport |
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml` | `$setmap{ember_elite}` + 新 spawn |
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/monster.yml` | 新图刷点 + 波间 teleport |
| `plugins/DungeonPlus/config.yml` | `ember_elite` 绑定 Elite；weekly 去掉 Elite |
| `plugins/TrMenu/menus/ember_hub.yml` · `ember_weekly.yml` | lore 去教指令（进本保留） |
| `docs/status/STATUS-ember-weekly-elite-maps-p3.md` | 本文件 |
