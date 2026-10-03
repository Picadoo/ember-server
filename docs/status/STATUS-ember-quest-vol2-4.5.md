# STATUS · 主线第二卷 ch7～10 + 新 quest event（阶段 4.5 插件岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-插件
- 依据：`docs/design/design-stage4-quest-vol2.md`；对照 `docs/design/design-stage4-mainline-vol2.md` §3.1
- 未改：Paper NMS、MythicMobs、afk 平衡、深渊/精英数值、git commit/push
- `ops.json` = `[]`（测后保持空）

## 1. 变更文件

### CoreRpg（源码 + 资源 + 运行目录 + jar）

| 路径 | 说明 |
|------|------|
| `CoreRpg/.../QuestService.java` | 新 event：`abyss_floor` / `elite_weekly_clear` / `forge`（`already()` state）；`Step.floor`；`quest event … abyss_floor <N>`；`chapterLabel` 第二卷；ch6→ch7 与 vol1-done 迁移；卷终文案 |
| `CoreRpg/.../ForgeService.java` | 锻造成功：`ever_forge` + `onEvent("forge")` + `checkPassive` |
| `CoreRpg/.../AbyssSettleService.java` | settle 写最高层后 `refreshQuestAbyss`（checkPassive + onEvent abyss_floor） |
| `CoreRpg/.../ProgressService.java` | `elite_weekly` **兼发** `quest event elite_weekly_clear`（含已标记周，防漏；已标则仍跳过双倍 XP） |
| `plugins/CoreRpg/quest.yml` | 合并 ch7～10；文件头注释列新 event |
| `CoreRpg/src/main/resources/quest.yml` | 同上（与 plugins 一致） |
| `CoreRpg` → **1.15.1** · `plugins/CoreRpg.jar`（`server-runtime/plugins` 为同目录软链） |

### TrMenu

| 路径 | 说明 |
|------|------|
| `plugins/TrMenu/menus/ember_hub.yml` | 主线按钮名改为「主线 · 旧誓余烬 / 烬火未灭」（lore 仍用 `%corerpg_quest_chapter%`） |

## 2. 新 event 实现要点

| event | `already()` | 触发点 |
|-------|-------------|--------|
| `forge` | `periodCount("ever_forge","all") > 0` | `ForgeService` 成功后 mark + `onEvent("forge")` |
| `abyss_floor` | `getAbyssBest() ≥ step.floor`（多选时仍读同一步 `floor:`） | settle/`recordAbyssFloor` 后 refresh；测试：`quest event <p> abyss_floor <N>` 写入最高层再检查 |
| `elite_weekly_clear` | `lootWeekMarks` 含 `elite_weekly_clear=<本周 weekId>` | DP COMPLETE 已有 `quest event … elite_weekly_clear`；**且** `progress elite_weekly` 内兼发 |

- `abyss_clear` 语义未改（任意一次深渊结算，走既有 progress）。
- state 型与 sign/talent 同类：步骤开始已满足则 `checkPassive` 立即完成。

## 3. ch7～10 XP 合计核对

| 章 | 名称 | 逐步 XP | 章合计 |
|----|------|---------|--------|
| 7 | 烬原之下 | 80+200+220+400 | **900** |
| 8 | 更深的井 | 80+200+400+200+220 | **1100** |
| 9 | 第二层誓火 | 80+200+500+420 | **1200** |
| 10 | 未灭之火 | 100+400+400+200+300 | **1400** |
| **合计** | | | **4600** |

hint 一律「打开 /ember（或枢纽菜单）→…」；第二卷 hint **无** `/corerpg` `/dp` `/dungeon` `/hub`。  
ch1～6 未改步骤内容；ch6 完成后 `higherKey` 自动开 ch7。已 `questDone` 的旧玩家进服时若存在后续章，清 done 并 `startChapter(下一章)`。

## 4. elite_weekly 双触发

1. **DP** `EmberEliteWeekly` COMPLETE：`corerpg quest event %player_name% elite_weekly_clear`（4.4 已有，未改）
2. **ProgressService** 处理 `elite_weekly`：写周标后（或已标）兼发 `onEvent(elite_weekly_clear)` + `checkPassive`；已标仍跳过 XP 双领

## 5. 重启 / 热重载

1. **游玩服短重启**（必须）：新 class（Quest/Forge/Abyss/Progress）+ jar 1.15.1  
   `server-runtime`：`./stop.sh` → `./start.sh custom`
2. `quest.yml` 也可随 `/corerpg reload` 热更，但本版含 Java 变更，以重启为准
3. TrMenu：`ember_hub.yml` 已自动重载；无需动 MM/Paper
4. 测后确认 `ops.json` = `[]`

## 6. 验收步骤

```text
# 进第二卷
corerpg quest set <玩家> 7 0
# 应显示：第二卷 · 烬原之下

# forge（已锻过可直接 checkPassive；否则真锻或）
corerpg quest set <玩家> 7 2
corerpg quest event <玩家> forge
# 或先改数据：依赖 ever_forge；真锻一次亦可

# abyss_floor
corerpg quest set <玩家> 8 2
corerpg quest event <玩家> abyss_floor 9
# best≥9 → 完成「历史最高第 9 层」

# elite_weekly_clear（双触发任一即可）
corerpg quest set <玩家> 9 2
corerpg progress <玩家> elite_weekly
# 或：corerpg quest event <玩家> elite_weekly_clear

# 复合步 floor 仍生效
corerpg quest set <玩家> 10 2
corerpg quest event <玩家> abyss_floor 10
# 或本周 elite 标记已在则 already 立即完成

# ch6→ch7：把进度打到 ch6 末步完成，应自动进入第 7 章
```

侧边栏 / `%corerpg_quest_chapter%`：ch≥7 为「第二卷 · \<章名\>」。

## 7. 风险 / 缺口

- **需重启后**新 event 才生效；仅 reload YAML 不够。
- 复合 `abyss_floor|elite_weekly_clear`：同一步共享 `floor:`；选 elite 支路忽略 floor（`already`/`matches` 按分支）。
- vol1 已完成玩家依赖进服迁移开 ch7；若离线不动存档，需上线一次或 `quest set 7 0`。
- 主线 smoke 扩到 ch10 属测试岗；本岗未跑完整战斗通关。
- 未改深渊/精英数值、挂机平衡、Paper/MM。

