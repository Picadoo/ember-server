# STATUS · S3 DP 波次/开门 + 菜单 D/E

**日期：** 2026-09-28 04:54（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** 总控【派工 · S3 地图粗胚 + 随后 DP/菜单】；坐标 `docs/status/STATUS-ember-daily-s3.md`；怪 `docs/status/STATUS-ember-daily-s3-mobs.md`；S2 范式 EmberDailyAsh/Crypt  
**CoreRpg 版本：** **1.15.17**  
**Verdict：** **✅ 两本 DP 分房 $kill + 门 AIR + TrMenu D/E 可进；tide 真击杀开门1 AIR×9 闭环；结构对称；怪 Display 去色对齐**

---

## 一句话

新建 `EmberDailyTide` / `EmberDailySpire` 完整 option+monster（分房 `$kill`、禁 kill-any、门 AIR、Boss、`daily_clear` 回枢纽）。TrMenu 增 D/E，无「筹备中」，`corerpg enter daily_tide|daily_spire` 各扣 30（同池）。tide 真击杀跑通门1 AIR×9。未改体力上限/日切；未 commit/push。

---

## 1. 波次设计摘要

### 线 D · EmberDailyTide（map `ember_daily_tide` · spawn 0,64,0）

| 组 | 刷怪 | `$kill`（去色 Display） | 开门 |
|----|------|-------------------------|------|
| wave1 | EmberDailyTideZombie×5 @ (-2,64,8)(2,64,8)(0,64,14) | 潮蚀尸×5 | 门1 z=18 x=-1..1 y=64..66 → AIR×9 |
| wave2a | TideZombie×3 @ (-2,64,26)(1,64,34) | 潮蚀尸×3 | — → wave2b |
| wave2b | EmberDailyTideSkeleton×3 @ (2,64,28)(-1,64,32) | 浪矢骷×3 | 门2 z=38 → AIR×9 → boss |
| boss | EmberDailyTideBrute×1 @ (0,64,46) | 潮闸蛮兵×1 | COMPLETE + daily_clear |

### 线 E · EmberDailySpire（map `ember_daily_spire` · spawn 0,64,0）

| 组 | 刷怪 | `$kill` | 开门 |
|----|------|---------|------|
| wave1 | EmberDailySpireZombie×5 底层 | 断塔卫尸×5 | 门1 z=4@y64 → AIR×9 |
| wave2a | SpireZombie×3 中层 | 断塔卫尸×3 | — → wave2b |
| wave2b | EmberDailySpireSkeleton×3 中层 | 裂隙箭骷×3 | 门2 z=4@y70 → AIR×9 → boss |
| boss | EmberDailySpireWarden×1 @ (0,76,0) | 断塔守望×1 | COMPLETE + daily_clear |

入口：`TicketEntryService.Kind.DAILY_TIDE|DAILY_SPIRE` → `dp start-console … EmberDailyTide|EmberDailySpire`；门槛仍 `gate=daily`（Lv.10）；体力 key `daily_tide`/`daily_spire` = **30**（同池，**未改**上限/刷新）。

---

## 2. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDailyTide/{option,monster,obstacle}.yml` | **新建** 完整本 |
| `plugins/DungeonPlus/dungeon/EmberDailySpire/{option,monster,obstacle}.yml` | **新建** 完整本 |
| `plugins/DungeonPlus/config.yml` | 预缓存 tide/spire ×1 |
| `plugins/TrMenu/menus/ember_daily.yml` | 增 D/E · enter daily_tide/spire · 无筹备中 |
| `CoreRpg/.../TicketEntryService.java` | Kind DAILY_TIDE / DAILY_SPIRE |
| `CoreRpg/.../StaminaService.java` | costs daily_tide/spire=30 |
| `plugins/CoreRpg/cash.yml` · `src/.../cash.yml` | costs 同步 |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.16 → 1.15.17** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `docs/status/STATUS-ember-daily-s3-dp.md` | 本文件 |
| 备份 | `server-runtime/config-backups/ember-daily-s3-20260928-044229/` |

**未改：** 体力上限/日切 / MM 怪文件（怪物岗已入库）/ Stamina 数值以外。  
**未 commit/push。**

---

## 3. 验证证据

账号：`S3TideF92`（tide 真击杀）· anvil `/tmp/s3-anvil-verify.json` · BFS `/tmp/s3-bfs-gates.json`

| 检查 | 结果 | 证据 |
|------|------|------|
| YAML 无 `$kill-any` · Display `$kill` 去色对齐 | ✅ | 潮蚀尸/浪矢骷/潮闸蛮兵 · 断塔卫尸/裂隙箭骷/断塔守望 |
| TrMenu 无「筹备中」· enter daily_tide/spire | ✅ | ember_daily.yml D/E |
| tide 进本 | ✅ | 「[潮蚀] 正在进入……」· 「潮蚀水道 开始」· 落点 y≈64 · 门1 iron×9 |
| tide 真击杀 wave1 → 门1 AIR×9 | ✅ | 「沿岸已清 · 桥闸开了」· iron=0 air=9 @z=18 · hits≈22 · **禁 killall** |
| 门挡 BFS（关） | ✅ | tide/spire 门后不可达 |
| 水道有底 · 顶台有栏 | ✅ | anvil |
| 体力上限/刷新 | ✅ **未改** | 仍 30/同池 |
| `ops.json` | ✅ `[]` | |
| jar | ✅ **1.15.17** | |
| commit/push | ✅ **未做** | |

---

## 4. 给总控的结案转发正文

```
【结案 · S3 地图粗胚 + DP/菜单】priority=true
岗：余烬-插件 · CoreRpg 1.15.17
世界：ember_daily_tide / ember_daily_spire（MV Complete + DP map 导出）
结构：D 淹水运河+桥闸+闸厅；E 向上攀塔+环廊+顶台有栏；各 ≥2 铁栅真门
坐标：tide spawn(0,64,0) 门z=18/38 boss(0,64,46)；spire spawn(0,64,0) 门z=4@y64/y70 boss(0,76,0)
DP：EmberDailyTide/Spire 分房 $kill（去色 Display·无 kill-any）· 门 AIR · Boss · daily_clear
菜单：D/E 可进 · enter daily_tide|daily_spire · 各扣 30（同池）· 无筹备中/零指令
验证：anvil/BFS 门挡+安全；tide 真击杀开门1 AIR×9；ops=[]
未改体力上限/日切；未 commit/push。可派测。
```
