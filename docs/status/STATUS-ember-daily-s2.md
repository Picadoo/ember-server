# STATUS · S2 日常线 B/C 地图粗胚（焦骨甬道 · 残誓地窖）

**日期：** 2026-09-28 03:50（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design/design-ember-daily-s2.md`；怪物就绪 `docs/status/STATUS-ember-daily-s2-mobs.md`；参考 `DailyCourtyardService`  
**CoreRpg 版本：** **1.15.14**（bump：新建造服务）  
**Verdict：** **✅ S2 粗胚落地**（两 MV 模板世界 + DP map 导出；≥2 真铁栅门；坐标进表；可验走位；**未写满 DP/菜单**；**未改体力**；未 commit/push）

---

## 一句话

按设计新建 `ember_daily_ash`（狭长焦土+假岔+鼓室）与 `ember_daily_crypt`（三层下阶+中厅+底层圆厅）。施工命令 `/corerpg ashbuild` · `/corerpg cryptbuild`；导出 DP map + Multiverse 常驻世界。门为铁栅关闭态（开门脚本下一棒）。粗胚可验走位。

---

## 1. 备份

`server-runtime/config-backups/ember-daily-s2-20260928-034039/`

| 内容 | 说明 |
|------|------|
| `map/ember_daily/` | 施工前庭院 map 壳备份 |
| `mv/worlds.yml` | 改前 Multiverse |
| `menus/ember_daily.yml` | 参考（本单未改菜单） |

---

## 2. 世界与施工

| 世界 / map | 施工命令 | 服务 | 体量 | 导出 |
|------------|----------|------|------|------|
| **ember_daily_ash** | `/corerpg ashbuild` | `DailyAshCorridorService` | blocks≈**12162** | `plugins/DungeonPlus/map/ember_daily_ash` + MV `ember_daily_ash` |
| **ember_daily_crypt** | `/corerpg cryptbuild` | `DailyCryptService` | blocks≈**24003** | `plugins/DungeonPlus/map/ember_daily_crypt` + MV `ember_daily_crypt` |

- Multiverse：`/mv import … normal` 已 Complete；`/mv list` 含两世界  
- DP：map 文件夹已有；DungeonPlus 自动生成了空壳 `dungeon/ember_daily_ash|crypt`（仅 `$setmap`，**未写波次/开门/结算** → 下一棒）  
- 告示语义：回枢纽 / 清房开门 / Boss 厅；**不写** `/dp` `/hub` `/mvtp`

---

## 3. 坐标表（脚坐标 · 对接怪物岗刷点）

### 线 B · 焦骨甬道 `ember_daily_ash`

| 角色 | 坐标 | 说明 |
|------|------|------|
| **spawn** | **(0, 65, 0)** | 焦黑拱门内 · 朝 +Z |
| 房1 刷点 | **(-3,65,8)** · **(3,65,8)** · **(0,65,13)** | 壁龛开放格 · 对接 `EmberAshZombie` |
| **门1** | **z=16** · x=-1..1 · y=65..67 | **IRON_FENCE×9** · 清房1才开 |
| 假岔 | x=3..9 · z=9..12 · **封墙 x=9 BONE_BLOCK** | 死路分心 · 非迷宫 |
| 房2 刷点 | **(-2,65,24)** · **(1,65,28)** · **(-1,65,32)** · **(0,65,22)** | 收窄弯 · 对接混编 |
| **门2** | **z=36** · x=-1..1 · y=65..67 | **IRON_FENCE×9** · 清房2才开 |
| **Boss 鼓室** | **(0, 65, 44)** | 约 10×10 · 可绕 · 对接 `EmberAshBrute` |

主材：`netherrack` / `red_sandstone` / `soul_sand` / 骨块封岔；非庭院回廊。

### 线 C · 残誓地窖 `ember_daily_crypt`

| 角色 | 坐标 | 说明 |
|------|------|------|
| **spawn** | **(0, 72, 0)** | 井口平台 · 朝下阶 |
| 上层厅刷点 | **(-6,72,6)** · **(5,72,6)** · **(-6,72,14)** · **(5,72,14)** | y=72 · 对接 `EmberDailyCryptZombie` |
| **门1** | **z=18** · x=-1..1 · y=72..74 | **IRON_FENCE×9** · 清上层 → 下阶 |
| 下阶1 | z≈19..25 · y 72→66 | 步进落差 |
| 中层厅刷点 | **(-5,66,30)** · **(4,66,30)** · **(-5,66,36)** · **(4,66,36)** | y=66 · 对接混编/誓印骷 |
| **门2** | **z=40** · x=-1..1 · y=66..68 | **IRON_FENCE×9** · 清中层 → 底层 |
| 下阶2 | z≈41..47 · y 66→60 | |
| **Boss 圆厅** | **(0, 60, 48)** | r≈6 + 外廊 ring≈8 · 对接 `EmberDailyCryptWarden` |

主材：`stonebrick` / `mossy_cobble` / `iron_bars` / 稀疏火把；垂直三层语义。

---

## 4. 自检

| 检查 | 结果 |
|------|------|
| 两世界非空板 · 结构可辨 | ✅ anvil：ash=红砂/地狱岩/灵魂沙+铁栅+骨封岔+鼓室；crypt=石砖/苔石+铁栅+三层+圆厅廊 |
| 各 ≥2 真门（关闭铁栅） | ✅ ash z=16/36；crypt z=18@y72 / z=40@y66 · id=101 |
| 门挡走位（清房前不可过） | ✅ BFS：spawn→门前可达，门后不可达；旁路后 Boss 垫可达 |
| 假岔非迷宫 | ✅ 短支路尽头骨墙 |
| 地窖楼梯不卡（结构） | ✅ 两段步进落差 72→66→60 |
| Boss 风筝空间 | ✅ ash 鼓室半宽5；crypt 内厅+外廊 |
| Live ash `/mvtp` | ✅ 脚下 red_sandstone · door iron_bars · bone_block |
| Live crypt mineflayer | ⚠ 客户端 chunk 加载失败（反复 air）；**以 anvil/BFS 为准** |
| 体力 | ✅ **未改**（仍共享池 30；本单未动 Stamina/菜单扣次） |
| `ops.json` | ✅ `[]` |
| commit/push | ✅ **未做** |

复跑：`/corerpg ashbuild` · `/corerpg cryptbuild`（需 `corerpg.admin`）；会覆盖对应 DP map region 并刷新 MV 世界目录。

---

## 5. 改动文件

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../DailyAshCorridorService.java` | **新建** 焦骨甬道建造+导出 |
| `CoreRpg/.../DailyCryptService.java` | **新建** 残誓地窖建造+导出 |
| `CoreRpg/.../CoreRpgPlugin.java` | 路由 `ashbuild` / `cryptbuild` |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.13 → 1.15.14** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `plugins/DungeonPlus/map/ember_daily_ash/` | 地形 region |
| `plugins/DungeonPlus/map/ember_daily_crypt/` | 地形 region |
| `server-runtime/ember_daily_ash/` · `ember_daily_crypt/` | MV 世界 |
| `plugins/Multiverse-Core/worlds.yml` | 登记两世界；hub spawn 保持 (-18.5,58,110.5) |
| `plugins/DungeonPlus/dungeon/ember_daily_ash\|crypt/` | DP **空壳**（自动生成，下一棒填） |
| `docs/status/STATUS-ember-daily-s2.md` | 本文件 |

---

## 6. 不做 / 下一棒

1. **未写满 DP**：分房 `$kill` / 开门清栅 / 结算 `daily_clear` / 扣体力挂接  
2. **未改 TrMenu**：B/C「筹备中」去灰属菜单棒  
3. **未改体力数值 / StaminaService**  
4. **未改怪物 MM**（已由怪物岗交付）  
5. 未 commit/push  

---

## 7. 给总控的结案转发正文

```
【结案 · S2 地图粗胚】priority=true
岗：余烬-插件 · CoreRpg 1.15.14
世界：ember_daily_ash / ember_daily_crypt（MV 已 import + DP map 已导出）
结构：B 狭长焦土+假岔+鼓室；C 三层下阶+中厅+圆厅；各 ≥2 铁栅真门（关闭）
坐标：见 docs/status/STATUS-ember-daily-s2.md
粗胚可验走位（anvil/BFS；ash live 已过）。DP 波次/开门/菜单去灰/体力挂接 → 下一棒。
未改体力；未 commit/push。
```

---

## 续 · DP/菜单（2026-09-28 03:57）

已由本岗落地：见 **`docs/status/STATUS-ember-daily-s2-dp.md`**（CoreRpg **1.15.15** · EmberDailyAsh/Crypt · TrMenu 去灰）。
