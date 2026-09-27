# STATUS · S3 日常线 D/E 地图粗胚（潮蚀水道 · 断塔回廊）

**日期：** 2026-09-28 04:54（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design-ember-daily-s3.md`；怪就绪 `docs/STATUS-ember-daily-s3-mobs.md`（Display 去色：潮蚀尸/浪矢骷/潮闸蛮兵 · 断塔卫尸/裂隙箭骷/断塔守望）；仿 S2 Ash/Crypt builder  
**CoreRpg 版本：** **1.15.17**（bump：`DailyTideService` / `DailySpireService` + enter）  
**Verdict：** **✅ S3 粗胚落地**（两 MV 模板世界 + DP map 导出；各 ≥2 真铁栅门；坐标进表；anvil/BFS 走位与门挡 PASS；落水有底、顶台有栏；**未改体力**；未 commit/push）

---

## 一句话

按设计新建 `ember_daily_tide`（淹水运河横推+桥闸+闸厅）与 `ember_daily_spire`（向上攀塔+中层环廊+顶台有栏）。施工 `/corerpg tidebuild` · `/corerpg spirebuild`；导出 DP map + Multiverse 常驻世界。门为铁栅关闭态。粗胚可验走位（anvil/BFS）。

---

## 1. 备份

`server-runtime/config-backups/ember-daily-s3-20260928-044229/`

| 内容 | 说明 |
|------|------|
| `map/ember_daily_ash/` | 施工前参考 map |
| `menus/ember_daily.yml` | 改前菜单 |
| `cash/` · `mv/` · DP config | 改前快照 |

---

## 2. 世界与施工

| 世界 / map | 施工命令 | 服务 | 体量 | 导出 |
|------------|----------|------|------|------|
| **ember_daily_tide** | `/corerpg tidebuild` | `DailyTideService` | blocks≈**18020** | `plugins/DungeonPlus/map/ember_daily_tide` + MV `ember_daily_tide` |
| **ember_daily_spire** | `/corerpg spirebuild` | `DailySpireService` | blocks≈**14322** | `plugins/DungeonPlus/map/ember_daily_spire` + MV `ember_daily_spire` |

- Multiverse：`/mv import … NORMAL` Complete；`/mv list` 含两世界；spawn y=64；hub spawn 保持 (-18.5,58,110.5)  
- DP：map 已导出；完整本见 DP STATUS（非空壳）  
- 告示语义：回枢纽 / 清房开门 / Boss；**不写** `/dp` `/hub` `/mvtp`

---

## 3. 坐标表（脚坐标 · 对接 DP/怪）

### 线 D · 潮蚀水道 `ember_daily_tide`

| 角色 | 坐标 | 说明 |
|------|------|------|
| **spawn** | **(0, 64, 0)** | 干岸闸内 · 朝 +Z |
| 房1 刷点 | **(-2,64,8)** · **(2,64,8)** · **(0,64,14)** | 沿岸平台 · `EmberDailyTideZombie` |
| **门1** | **z=18** · x=-1..1 · y=64..66 | **IRON_FENCE×9** · 清房1才开 |
| 死桥短支 | x=3..8 · z=10..13 · 尽头棱柱封 | 非迷宫 |
| 房2 刷点 | **(-2,64,26)** · **(2,64,28)** · **(-1,64,32)** · **(1,64,34)** | 折桥厅混编 |
| **门2** | **z=38** · x=-1..1 · y=64..66 | **IRON_FENCE×9** |
| **Boss 闸厅** | **(0, 64, 46)** | 半宽≈6 · 可绕 · 边角水有石底 · `EmberDailyTideBrute` |

主材：`prismarine` / `smooth_brick` / `sea_lantern` / `lapis_block` / 铁栏桥；中央/侧缘常驻水层，底 y=61 石 → **禁虚空秒杀**。

### 线 E · 断塔回廊 `ember_daily_spire`

| 角色 | 坐标 | 说明 |
|------|------|------|
| **spawn** | **(0, 64, 0)** | 塔底大厅 · 朝上阶 |
| 底层刷点 | **(-5,64,-3)** · **(5,64,-3)** · **(-5,64,2)** · **(5,64,2)** | y=64 · `EmberDailySpireZombie` |
| **门1** | **z=4 @ y64** · x=-1..1 · y=64..66 | **IRON_FENCE×9** · 清底 → 上阶 |
| 上阶1 | z≈5..10 · y 64→70 | 步进抬升 |
| 中层环廊刷点 | **(-6,70,0)** · **(6,70,0)** · **(0,70,-6)** · **(0,70,6)** | y=70 · 混编/裂隙箭骷 |
| **门2** | **z=4 @ y70** · x=-1..1 · y=70..72 | **IRON_FENCE×9** · 清中 → 顶 |
| 上阶2 | z≈5..10 · y 70→76 | |
| **Boss 顶台** | **(0, 76, 0)** | r≈5 + 外围铁栏 · `EmberDailySpireWarden` |

主材：风化 `stone_bricks` / `cobble` / `oak_log` 梁 / 铁栏；顶台露天有栏；内环落差下有 catch 台 + 实底 y=49 → **禁虚空死循环**。

---

## 4. 自检

| 检查 | 结果 |
|------|------|
| 两世界非空板 · 五线可辨 | ✅ anvil：tide=棱柱/海晶灯/水渠+铁闸+闸厅；spire=石砖/木梁+环廊+顶台栏（≠庭院/焦骨/地窖） |
| 各 ≥2 真门（关闭铁栅） | ✅ tide z=18/38；spire z=4@y64 / z=4@y70 · iron_bars×9 |
| 门挡走位（清房前不可过） | ✅ BFS（铁栅按实心）：spawn→门前可达，门后/Boss 不可达 |
| 水道落水安全 | ✅ 水下 y=61 石底；无虚空 |
| 断塔向上剖面 · 顶台有栏 | ✅ mid y70 / top y76；rail iron_bars；catch solid |
| Live tide 进本落点 | ✅ spawn 干岸 stonebrick · 门1 铁栅×9 |
| 体力 | ✅ **未改**（仍共享池 30） |
| `ops.json` | ✅ `[]` |
| commit/push | ✅ **未做** |

复跑：`/corerpg tidebuild` · `/corerpg spirebuild`（需 `corerpg.admin`）。

---

## 5. 改动文件（地图棒）

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../DailyTideService.java` | **新建** 潮蚀水道建造+导出 |
| `CoreRpg/.../DailySpireService.java` | **新建** 断塔回廊建造+导出 |
| `CoreRpg/.../CoreRpgPlugin.java` | 路由 `tidebuild` / `spirebuild` |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.16 → 1.15.17** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `plugins/DungeonPlus/map/ember_daily_tide\|spire/` | 地形 region |
| `server-runtime/ember_daily_tide/` · `ember_daily_spire/` | MV 世界 |
| `plugins/Multiverse-Core/worlds.yml` | 登记两世界 |
| `docs/STATUS-ember-daily-s3.md` | 本文件 |

---

## 6. 续 · DP/菜单

见 **`docs/STATUS-ember-daily-s3-dp.md`**（同版本 **1.15.17** · EmberDailyTide/Spire · TrMenu D/E）。
