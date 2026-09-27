# STATUS · S4 日常线 F/G 地图粗胚（霜晶裂隙 · 锈轨矿道）

**日期：** 2026-09-28 05:15（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design-ember-daily-s4.md`（b4a1b21 已批准）；怪就绪 `docs/STATUS-ember-daily-s4-mobs.md` / 506106e（Display 去色：霜晶尸/霜矢骷/霜核蛮兵 · 锈轨尸/矿矢骷/锈轨矿监）；仿 S3 Tide/Spire builder  
**CoreRpg 版本：** **1.15.18**（bump：`DailyFrostService` / `DailyRailService` + enter）  
**Verdict：** **✅ S4 粗胚落地**（两 MV 模板世界 + DP map 导出；各 ≥2 真铁栅门；坐标进表；anvil/BFS 走位与门挡 PASS；裂隙底有落点、巷宽 7+1 支洞；**未改体力**；未 commit/push）

---

## 一句话

按设计新建 `ember_daily_frost`（冰蓝裂隙冻台横移+霜厅）与 `ember_daily_rail`（废弃矿轨+恰好 1 短支洞→机房）。施工 `/corerpg frostbuild` · `/corerpg railbuild`；导出 DP map + Multiverse 常驻世界。门为铁栅关闭态。粗胚可验走位（anvil/BFS）。

---

## 1. 备份

`/workspace/minecraft/server-runtime/config-backups/ember-daily-s4-20260928-050543/`

| 内容 | 说明 |
|------|------|
| `map/ember_daily_tide/` · ash | 施工前参考 map |
| `menus/ember_daily.yml` | 改前菜单 |
| `cash/` · `mv/` · DP config | 改前快照 |

---

## 2. 世界与施工

| 世界 / map | 施工命令 | 服务 | 体量 | 导出 |
|------------|----------|------|------|------|
| **ember_daily_frost** | `/corerpg frostbuild` | `DailyFrostService` | blocks≈**13818** | `plugins/DungeonPlus/map/ember_daily_frost` + MV `ember_daily_frost` |
| **ember_daily_rail** | `/corerpg railbuild` | `DailyRailService` | blocks≈**14785** | `plugins/DungeonPlus/map/ember_daily_rail` + MV `ember_daily_rail` |

- Multiverse：`/mv import … normal` Complete；`/mv list` 含两世界；frost spawn y=70；rail spawn y=64  
- DP：map 已导出；完整本见 DP STATUS（非空壳）  
- 告示语义：回枢纽 / 清房开门 / Boss；**不写** `/dp` `/hub` `/mvtp`

---

## 3. 坐标表（脚坐标 · 对接 DP/怪）

### 线 F · 霜晶裂隙 `ember_daily_frost`

| 角色 | 坐标 | 说明 |
|------|------|------|
| **spawn** | **(0, 70, 0)** | 裂隙口干台 · 朝 +Z |
| 房1 刷点 | **(-3,70,8)** · **(3,70,8)** · **(0,70,12)** · **(-3,70,14)** · **(3,70,6)** | 冻台壁龛 · `EmberDailyFrostZombie` |
| **门1** | **z=16** · x=-1..1 · y=70..72 | **IRON_FENCE×9** · 清房1才开 |
| 裂隙底 | x=±4..±6 · bottom y=64 packed_ice + snow | **禁虚空** |
| 房2 刷点 | **(-4,70,24)** · **(4,70,28)** · **(0,70,30)** · **(-4,70,26)** · **(4,70,22)** | 折台混编 |
| **门2** | **z=34** · x=-1..1 · y=70..72 | **IRON_FENCE×9** |
| **Boss 霜厅** | **(0, 70, 48)** | 半宽≈6 · 可绕 · 角裂隙有雪底 · `EmberDailyFrostBrute` |

主材：`packed_ice` / `ice` / `snow_block` / `quartz_block` / glowstone；**无大片流水**（≠潮蚀）。

### 线 G · 锈轨矿道 `ember_daily_rail`

| 角色 | 坐标 | 说明 |
|------|------|------|
| **spawn** | **(0, 64, 0)** | 矿洞口木框 · 朝 +Z |
| 房1 刷点 | **(-4,64,6)** · **(4,64,6)** · **(0,64,10)** · **(-4,64,12)** · **(4,64,12)** | 柱间 · `EmberDailyRailZombie` |
| **门1** | **z=15** · x=-1..1 · y=64..66 | **IRON_FENCE×9** |
| **短支洞** | x=4..12 · z=18..22 · 尽头 x=12 封墙 | **恰好 1** · 非迷宫 |
| 房2 刷点 | **(-5,64,20)** · **(5,64,24)** · **(-5,64,28)** · **(5,64,30)** · **(0,64,22)** | 混编+支洞口 |
| **门2** | **z=33** · x=-1..1 · y=64..66 | **IRON_FENCE×9** |
| **Boss 机房** | **(0, 64, 46)** | 半宽≈6 · 柱掩体 · `EmberDailyRailWarden` |

主材：`cobble` / `oak_log`·支柱 / `rail` 装饰 / `oak_fence` / torch / 少量矿石；巷宽 **7**（≠焦骨 5 窄筒）；同层横推（≠断塔/地窖垂直翻版）。

---

## 4. 自检

| 检查 | 结果 |
|------|------|
| 两世界非空板 · 七线可辨 | ✅ anvil：frost=浮冰/石英/雪+裂隙底；rail=粗石/木梁/铁轨+支洞（≠庭院/焦骨/地窖/潮蚀/断塔） |
| 各 ≥2 真门（关闭铁栅） | ✅ frost z=16/34；rail z=15/33 · iron_bars×9 |
| 门挡走位（清房前不可过） | ✅ BFS（铁栅按实心）：spawn→门前可达，门后/Boss 不可达 · `/tmp/s4-bfs-gates.json` |
| 裂隙落点安全 | ✅ packed_ice@y64 + snow；无虚空；**无水面** |
| 锈轨巷宽+1 支洞 | ✅ gallery w=7；spur x=4..12 封死 |
| Live frost 进本落点 | ✅ spawn quartz · 门1 铁栅×9 |
| 体力 | ✅ **未改**（仍共享池 30） |
| `ops.json` | ✅ `[]` |
| commit/push | ✅ **未做** |

复跑：`/corerpg frostbuild` · `/corerpg railbuild`（需 `corerpg.admin`）。

证据：`/tmp/s4-anvil-verify.json` · `/tmp/s4-bfs-gates.json` · `/tmp/s4-frost-rail-build-result.json`

---

## 5. 改动文件（地图棒）

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../DailyFrostService.java` | **新建** 霜晶裂隙建造+导出 |
| `CoreRpg/.../DailyRailService.java` | **新建** 锈轨矿道建造+导出 |
| `CoreRpg/.../CoreRpgPlugin.java` | 路由 `frostbuild` / `railbuild` |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.17 → 1.15.18** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `plugins/DungeonPlus/map/ember_daily_frost\|rail/` | 地形 region |
| `server-runtime/ember_daily_frost/` · `ember_daily_rail/` | MV 世界 |
| `plugins/Multiverse-Core/worlds.yml` | 登记两世界 |
| `docs/STATUS-ember-daily-s4.md` | 本文件 |

---

## 6. 续 · DP/菜单

见 **`docs/STATUS-ember-daily-s4-dp.md`**（同版本 **1.15.18** · EmberDailyFrost/Rail · TrMenu F/G）。
