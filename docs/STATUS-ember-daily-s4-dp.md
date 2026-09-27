# STATUS · S4 DP 波次/开门 + 菜单 F/G

**日期：** 2026-09-28 05:15（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** 总控【派工 · S4 地图粗胚 + 随后 DP/菜单】；坐标 `STATUS-ember-daily-s4.md`；怪 `STATUS-ember-daily-s4-mobs.md` / 506106e；S3 范式 EmberDailyTide/Spire  
**CoreRpg 版本：** **1.15.18**  
**Verdict：** **✅ 两本 DP 分房 $kill + 门 AIR + TrMenu F/G 可进；frost 真击杀开门1 AIR×9 闭环；结构对称；怪 Display 去色对齐**

---

## 一句话

新建 `EmberDailyFrost` / `EmberDailyRail` 完整 option+monster（分房 `$kill`、禁 kill-any、门 AIR、Boss、`daily_clear` 回枢纽）。TrMenu 增 F/G，无「筹备中」，`corerpg enter daily_frost|daily_rail` 各扣 30（同池）。frost 真击杀跑通门1 AIR×9。未改体力上限/日切；未 commit/push。

---

## 1. 波次设计摘要

### 线 F · EmberDailyFrost（map `ember_daily_frost` · spawn 0,70,0）

| 组 | 刷怪 | `$kill`（去色 Display） | 开门 |
|----|------|-------------------------|------|
| wave1 | EmberDailyFrostZombie×5 @ (-3,70,8)(3,70,8)(0,70,12)(-3,70,14) | 霜晶尸×5 | 门1 z=16 x=-1..1 y=70..72 → AIR×9 |
| wave2a | FrostZombie×3 @ (-4,70,24)(0,70,30) | 霜晶尸×3 | — → wave2b |
| wave2b | EmberDailyFrostSkeleton×3 @ (4,70,28)(4,70,22) | 霜矢骷×3 | 门2 z=34 → AIR×9 → boss |
| boss | EmberDailyFrostBrute×1 @ (0,70,48) | 霜核蛮兵×1 | COMPLETE + daily_clear |

### 线 G · EmberDailyRail（map `ember_daily_rail` · spawn 0,64,0）

| 组 | 刷怪 | `$kill` | 开门 |
|----|------|---------|------|
| wave1 | EmberDailyRailZombie×5 主巷前段 | 锈轨尸×5 | 门1 z=15 → AIR×9 |
| wave2a | RailZombie×3 后段 | 锈轨尸×3 | — → wave2b |
| wave2b | EmberDailyRailSkeleton×3 支洞口侧 | 矿矢骷×3 | 门2 z=33 → AIR×9 → boss |
| boss | EmberDailyRailWarden×1 @ (0,64,46) | 锈轨矿监×1 | COMPLETE + daily_clear |

入口：`TicketEntryService.Kind.DAILY_FROST|DAILY_RAIL` → `dp start-console … EmberDailyFrost|EmberDailyRail`；门槛仍 `gate=daily`（Lv.10）；体力 key `daily_frost`/`daily_rail` = **30**（同池，**未改**上限/刷新）。

---

## 2. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDailyFrost/{option,monster,obstacle}.yml` | **新建** 完整本 |
| `plugins/DungeonPlus/dungeon/EmberDailyRail/{option,monster,obstacle}.yml` | **新建** 完整本 |
| `plugins/DungeonPlus/config.yml` | 预缓存 frost/rail ×1 |
| `plugins/TrMenu/menus/ember_daily.yml` | 增 F/G · enter daily_frost/rail · 无筹备中 |
| `CoreRpg/.../TicketEntryService.java` | Kind DAILY_FROST / DAILY_RAIL |
| `CoreRpg/.../StaminaService.java` | costs daily_frost/rail=30 |
| `plugins/CoreRpg/cash.yml` · `src/.../cash.yml` | costs 同步 |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.17 → 1.15.18** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `docs/STATUS-ember-daily-s4-dp.md` | 本文件 |
| 备份 | `/workspace/minecraft/server-runtime/config-backups/ember-daily-s4-20260928-050543/` |

**未改：** 体力上限/日切 / MM 怪文件（怪物岗已入库 506106e）/ Stamina 数值以外。  
**未 commit/push。**

---

## 3. 验证证据

账号：`S4FrostF77`（frost 真击杀）· anvil `/tmp/s4-anvil-verify.json` · BFS `/tmp/s4-bfs-gates.json` · live `/tmp/s4-live-fight.json`

| 检查 | 结果 | 证据 |
|------|------|------|
| YAML 无 `$kill-any` 条件 · Display `$kill` 去色对齐 | ✅ | 霜晶尸/霜矢骷/霜核蛮兵 · 锈轨尸/矿矢骷/锈轨矿监 |
| TrMenu 无「筹备中」· enter daily_frost/rail | ✅ | ember_daily.yml F/G |
| frost 进本 | ✅ | 「[霜晶] 正在进入……」· 「霜晶裂隙 开始」· 落点 y≈70 · 门1 iron×9 |
| frost 真击杀 wave1 → 门1 AIR×9 | ✅ | 「冻台已清 · 冰闸开了」· iron=0 air=9 @z=16 · hits≈23 · **禁 killall** |
| 门挡 BFS（关） | ✅ | frost/rail 门后不可达 |
| 裂隙有底 · 锈轨有轨/支洞 | ✅ | anvil |
| 体力上限/刷新 | ✅ **未改** | 仍 30/同池 |
| `ops.json` | ✅ `[]` | |
| jar | ✅ **1.15.18** | |
| commit/push | ✅ **未做** | |

---

## 4. 给总控的结案转发正文

```
【结案 · S4 地图粗胚 + DP/菜单】priority=true
岗：余烬-插件 · CoreRpg 1.15.18
世界：ember_daily_frost / ember_daily_rail（MV Complete + DP map 导出）
结构：F 冰蓝裂隙冻台横移+霜厅（≠潮蚀水面）；G 废弃矿轨+1短支洞+机房（巷宽7≠焦骨窄筒、≠垂直翻版）；各 ≥2 铁栅真门
坐标：frost spawn(0,70,0) 门z=16/34 boss(0,70,48)；rail spawn(0,64,0) 门z=15/33 boss(0,64,46) spur x=4..12@z20
DP：EmberDailyFrost/Rail 分房 $kill（去色：霜晶尸/霜矢骷/霜核蛮兵 · 锈轨尸/矿矢骷/锈轨矿监 · 无 kill-any）· 门 AIR · Boss · daily_clear
菜单：F/G 可进 · enter daily_frost|daily_rail · 各扣 30（同池）· 无筹备中/零指令
验证：anvil/BFS 门挡+安全；frost 真击杀开门1 AIR×9；ops=[]
未改体力上限/日切；未 commit/push。可派测。
```


## 补丁 · F/G 体力不足灰显（S4 结案后）
- `ember_daily.yml` F/G 已套与 A–E 同款 `icons` condition（不足灰显 + 0:00 回满 tell）。
