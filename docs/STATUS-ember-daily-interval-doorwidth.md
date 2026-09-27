# STATUS · 日常体验两件软债（start-interval 提示 + 庭院门宽对齐）

**日期：** 2026-09-28 06:35（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** 总控【派工 · 日常体验两件软债】  
**Verdict：** **✅ 两件可结案**（菜单冷却提示已落；庭院门宽 yml+Java+dailybuild 对齐 x=-1..1；CoreRpg **1.15.20**；ops=`[]`；**未** commit/push）

---

## 一句话

菜单 T/R 标明「出本后再进约等 5 秒（缓存冷却）」；庭院门1/门2 开门块与模板图铁栅统一为 **x=-1..1 ×9**（删 x=-2）；`DOOR_X0=-1` · dailybuild 成功 · 地图 x=-2 已为石砖墙。**未**改 Java enter 接冷却（`TicketEntryService` 仍仅 `dp start-console`）。禁项与体力/波次/$kill/Boss 未动。

---

## 1. start-interval 提示（菜单为主）

| 项 | 结果 |
|----|------|
| 背景 | DP `dungeon-pre-folder.start-interval: true` ≈ 出本后再进约 5 秒缓存冷却 |
| CoreRpg enter | 已核实 `TicketEntryService.tryEnter` **仅** `dp start-console`，**不捕获** DP 冷却拒进文案 → 按派工「有则改，无则只做菜单」**未改 Java / 非为此单独 bump**（本次 bump 因门宽） |
| `ember_daily.yml` **T**（体力） | lore 增：`§8出本后再进约等 5 秒（缓存冷却），不是进本坏了` |
| **R**（体力说明） | lore 同行；actions `tell` 顺带一句同语义 |
| 热更 | FIFO `trmenu reload` → `[06:35:06] 良好 \| 31 个菜单已加载` |

**未改：** 体力数值 / 各线进本 cost / 进本 command。

---

## 2. 庭院门宽对齐 x=-1..1（×9）

### 2.1 改前

- `EmberDaily/monster.yml` 门1 z=13 / 门2 z=25：`$operation-block` 含 **x=-2..1**（AIR×12）
- `DailyCourtyardService`：`DOOR_X0=-2`、`DOOR_X1=1`（铁栅 4 列）
- 其余日常线已是 x=-1..1（×9）

### 2.2 改后

| 层 | 变更 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | 门1/门2 **仅** x=-1,0,1 × y=65..67 → **AIR×9×2=18**；删全部 `loc=-2,...`；注释同步「门宽对齐」 |
| `CoreRpg/.../DailyCourtyardService.java` | `DOOR_X0` **-2 → -1**；类头注释 `x=-1..1 … IRON_FENCE ×9` |
| 版本 | **1.15.19 → 1.15.20**（`pom.xml` + `plugin.yml`） |
| 构建/部署 | `mvn -o -q -DskipTests package` → `plugins/CoreRpg.jar` |
| 热更 | play 短重启（FIFO `console.in` + keeper）· `Loading/Enabling/enabled CoreRpg v1.15.20` · `Done (5.613s)!` @ 06:34:53 CST |
| `corerpg dailybuild` | ✅ `[06:34:57] courtyard done · blocks≈6039 · … door1z=13 door2z=25 · exported → plugins/DungeonPlus/map/ember_daily`；表打印 `door1 z=13 x=-1..1` |
| `dp reload` | ✅ `[06:35:03] [DungeonPlus] 插件重载完毕`（含 EmberDaily） |

### 2.3 自检

| 检查 | 结果 |
|------|------|
| monster 门操作块数 | ✅ **18**（两门×9） |
| 无 `loc=-2` | ✅ |
| YAML `safe_load` | ✅ |
| dailybuild 日志成功 | ✅ blocks≈6039 · x=-1..1 |
| 模板图门平面 | ✅ anvil 抽样：z=13/25 · **x=-2 → id=98 SMOOTH_BRICK**；**x=-1..1 → id=101 IRON_FENCE×9**（非半开铁栅缝） |
| 波次 / `$kill` / Boss | ✅ **未动** |
| Citizens | ✅ **未引入** |
| `ops.json` play+login | ✅ **`[]`** |
| commit/push | ✅ **未做** |

---

## 3. 版本与部署

| 项 | 值 |
|----|-----|
| CoreRpg | **1.15.20** |
| jar | `plugins/CoreRpg.jar`（与 `CoreRpg/target/CoreRpg.jar` 同源） |
| 菜单 | `plugins/TrMenu/menus/ember_daily.yml` |
| DP | `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` + `map/ember_daily`（dailybuild 覆盖） |
| 控制台 | FIFO `server-runtime/console.in`（stdin 已接；RCON 仍关） |

---

## 4. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_daily.yml` | T/R lore + R tell 冷却提示 |
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | 门宽 x=-1..1（删 loc=-2） |
| `CoreRpg/.../DailyCourtyardService.java` | `DOOR_X0=-1` + 注释 |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.20** |
| `plugins/CoreRpg.jar` | 换入 |
| `plugins/DungeonPlus/map/ember_daily/` | dailybuild 重建 |
| `docs/STATUS-ember-daily-interval-doorwidth.md` | 本文件 |

**未改：** `TicketEntryService` / 体力配置 / 波次与 `$kill` / Boss / Citizens / 其它六线门宽。

---

## 5. 给总控结案正文（可直接转发 · priority=true）

```
【结案 · 日常体验两件软债】priority=true
岗：余烬-插件 · CoreRpg 1.15.20
1) start-interval 提示：ember_daily.yml 顶栏 T + 体力说明 R lore 各加「出本后再进约等 5 秒（缓存冷却），不是进本坏了」；R tell 顺带一句。
   enter 未接冷却：TicketEntryService.tryEnter 仅 dp start-console、不捕获 DP 拒进文案 → 按派工只做菜单、未改 Java 专接冷却。
2) 庭院门宽对齐 x=-1..1（×9）：
   - EmberDaily/monster.yml 门1 z=13 / 门2 z=25 删 loc=-2，仅留 x=-1,0,1 × y=65..67 → 操作块 18；波次/$kill/Boss 未动
   - DailyCourtyardService DOOR_X0 -2→-1；bump 1.15.20；mvn package 部署；FIFO 短重启
   - corerpg dailybuild ✅ blocks≈6039 · door1 x=-1..1；dp reload ✅；模板图 x=-2=SMOOTH_BRICK(98)、x=-1..1=IRON_FENCE(101)×9
禁项：未改体力数值/波次/$kill/Boss · 未 Citizens · ops=[] · 未 commit/push
STATUS：docs/STATUS-ember-daily-interval-doorwidth.md
```

---

## 6. blocker / 备注

| 项 | 说明 |
|----|------|
| blocker | **无** |
| enter 冷却文案 | 仍不转发 DP 冷却拒进；玩家靠菜单 lore 预期。若日后要进本失败 chat 直出，需另派改 `TicketEntryService`（独立 bump） |
| play 控制台 | 本轮用 FIFO+keeper 接 stdin（`start.sh` 默认仍 `/dev/null`）；后续短重启若需控制台请同样接线或走临时 OP bot |
| DP 实例缓存 | 已 `dp reload`；若测服仍见旧门洞，再清对应 `dungeon-caches/dungeon_EmberDaily_*` 后重进 |
