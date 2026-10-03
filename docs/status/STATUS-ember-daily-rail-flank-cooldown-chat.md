# STATUS · 收口可改两件打磨（锈轨真侧袭 + enter 冷却 chat）

**日期：** 2026-09-28 07:45–07:50 CST（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** 总控【派工 · 收口可改两件打磨】  
**制品：** CoreRpg **1.15.21** · `plugins/CoreRpg.jar`  
**Verdict：** **✅ 两件可结案**（锈轨房2 真侧袭链式已对齐霜晶；enter 失败/无效果人话冷却提示已接；`dp reload` + 短重启 jar 生效；ops=`[]`；**未** commit/push）

---

## 一句话

锈轨房2：`wave2a` **start** 即 `delay=1` 刷 `wave2b` 支洞（勿等主巷 `$kill`）；**door2** 仍在支洞 `wave2b` `$kill` 完成。CoreRpg **1.15.21**：`TicketEntryService.tryEnter` 在 `dp start-console` 失败或 2s 内未进本时 tell「出本后再进约等 5 秒（缓存冷却），不是进本坏了」（与菜单 lore 一致），并退还已扣体力。禁项未动。

---

## 1. 锈轨真侧袭（对齐霜晶链式）

**路径（live 为准）：** `plugins/DungeonPlus/dungeon/EmberDailyRail/monster.yml`  
（仓库为 `dungeon/` 单数；无 Java/resources 副本，无需同步）

### 改前

| 组 | 链 |
|----|-----|
| wave2a | start 文案 → end：主巷清完后 `$monstergroup wave2b delay=2` |
| wave2b | `$kill` 矿矢骷×3 → door2 z=33 AIR×9 → boss |

侧袭要等主巷清完才出。

### 改后（对齐霜晶 wave1→wave1b）

| 组 | 刷点 / `$kill` | 链 |
|----|----------------|-----|
| **wave2a** | 主巷尸×3 @ (-5,64,20)(0,64,22) / 锈轨尸×3 | **start** → `$monstergroup wave2b delay=1` + 侧袭文案；**end 不开门**（仅「主巷已清」+ heal） |
| **wave2b** | 支洞矿矢骷×3 @ (8/10,64,20) / 矿矢骷×3 | end：**door2 z=33 AIR×9** + 机房门文案 → boss delay3 |

**未改：** 刷点坐标、`$kill` 数量/名、door 坐标、Boss、wave1、体力、其它日常线。

### 热更

| 步 | 证据 |
|----|------|
| YAML `safe_load` | ✅ |
| FIFO `dp reload`（停服前） | `[07:48:56] [DungeonPlus] 插件重载完毕`（含 EmberDailyRail） |
| 短重启后再 `dp reload` | `[07:50:10] [DungeonPlus] 插件重载完毕` · EmberDailyRail 初始化完毕 |

---

## 2. enter 冷却 chat（CoreRpg 1.15.21）

**文件：** `CoreRpg/.../TicketEntryService.java`

| 点 | 说明 |
|----|------|
| DP 回传 | `dispatchCommand` 对冷却拒进常仍 `true`，**无可转发回执** |
| 本地策略 | 记 `lastTryEnterMs` / `lastOkEnterMs`；dispatch 失败立即退还+人话；成功则若未立刻在 `dungeon_*` / 实例世界，**40 tick（≈2s）** 校验，无效果则退还+人话（仿 `GuildService` 验进本） |
| 文案 | `§e出本后再进约等 5 秒（缓存冷却），不是进本坏了`（与 `ember_daily.yml` T/R lore 一致）；非短窗再附一行灰字「若仍进不去…」 |
| 禁改 | **未改** 体力数值 / `consumeForEnter` cost / 门票 NI / Citizens |

### 版本与部署

| 项 | 结果 |
|----|------|
| bump | **1.15.20 → 1.15.21**（`pom.xml` + `plugin.yml`） |
| 构建 | `mvn -o -q -DskipTests package`（JDK8）✅ |
| 部署 | `CoreRpg/target/CoreRpg.jar` → `plugins/CoreRpg.jar` |
| 短重启 | FIFO `console.in` + keeper · `stop` → 再起 · stdin=`console.in` |
| live | `[07:49:26] CoreRpg 1.15.21 enabled` · `Done (6.419s)!` @ 07:49:27 CST · `version CoreRpg` → `1.15.21` |

---

## 3. 禁项核对

| 禁项 | 状态 |
|------|------|
| 体力数值 / 进本 cost | ✅ 未改 |
| Boss / 门坐标 | ✅ 未改 |
| 其它日常线 monster | ✅ 未改 |
| Citizens | ✅ 未引入 |
| 玩家文案教 `/dp` `/corerpg` | ✅ 无 |
| ops.json play+login | ✅ `[]` |
| commit / push | ✅ **未做** |

---

## 4. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDailyRail/monster.yml` | 房2 真侧袭链式 |
| `CoreRpg/.../TicketEntryService.java` | 冷却人话 + 无效果退还 |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.21** |
| `plugins/CoreRpg.jar` | 换入（gitignore） |
| `docs/status/STATUS-ember-daily-rail-flank-cooldown-chat.md` | 本文件 |

---

## 5. 验收命令（测岗）

1. **锈轨侧袭：** 进 `EmberDailyRail` → 开 door1 进房2 → 主巷尸刷出后 **≈1s** 支洞矿矢骷应出现（主巷未必要清完）；door2 仍须 **支洞 `$kill`×3** 后才 AIR。  
2. **冷却 chat：** 出本后 **5s 内** 再点菜单进本 → 应收到「出本后再进约等 5 秒（缓存冷却），不是进本坏了」；体力应退还（若曾扣）。  
3. `version CoreRpg` → **1.15.21**；`ops.json` 保持 `[]`。

---

## 6. Blocker

无。测岗需真人/ bot 复测侧袭时序与冷却 tell（本岗未做进本 play）。
