# STATUS · EmberAbyss / EmberCalamity / EmberRaid 冒烟

**日期：** 2026-09-12 15:19 CST（Asia/Shanghai）  
**执行岗：** 余烬服测试专岗执行器  
**环境：** `/workspace/minecraft/server-runtime` · custom · 端口 **25565**  
**服状态：** 冒烟全程**未停服**；结束时 **仍在跑**（java pid 1208967，`paper-custom.jar`）  
**BOT：** mineflayer OP `RpgBot`（总控预冒烟）/ `Tester`（补测）

---

## 总体结论

| 域 | 结论 |
|----|------|
| **总体** | **PASS**（深渊进本+清第1层+出本；灾厄单独进本+Boss 降临；团本票+人数限制；使魔/图录菜单壳） |
| 关键异常 | 见文末「异常摘要」——均非阻断 |

---

## 0. 环境与插件 enable

| 项 | 结果 | 说明 |
|----|------|------|
| 端口 25565 / 进程 | **PASS** | 已在跑，**未**执行 stop/start |
| DP / MM / NI enable | **PASS** | latest.log：`Enabling DungeonPlus v1.4.5` / `MythicMobs v4.11.0` / `NeigeItems v1.21.151`；Done (2.041s) |
| `/dp reload` | **PASS** | EmberAbyss / EmberCalamity / EmberRaid 等导入+初始化完毕（总控预冒烟已 OK，补测前亦曾 reload） |

---

## 1. 总控预冒烟（直接采信）

| 步骤 | 结论 | 证据摘要 |
|------|------|----------|
| `/ni give … ticket_ember_abyss` | **PASS** | `成功给予 RpgBot 3 个 余烬深渊票` |
| `/dp start EmberAbyss` | **PASS** | 扣票文案 + `—— 深渊第 1 层 —— 灰烬潮（僵尸×4）`；地牢创建完毕 |
| 本中再 `/dp start EmberCalamity` | **PASS**（互斥） | `你已经在地牢中,请退出当前地牢后再进入` = 预期 |

---

## 2. 补测 · EmberAbyss 清层 / 掉落 / 出本

| 步骤 | 结论 | 说明 |
|------|------|------|
| 再开 EmberAbyss | **PASS** | Tester：扣票 + 灰烬潮文案；刷出僵尸×4 |
| **清层/推进（第1层灰烬潮）** | **PASS** | 近战清空约 16s → 聊天 `第 1 层通过` → 自动 `—— 深渊第 2 层 —— 混响（僵尸×3 + 骷髅×2）` |
| 第2～5层全通 | **SKIP** | 本期冒烟可验证范围止于「第1层可完成并推进」；未打满 5 层 |
| **掉落抽检** | **SKIP** | YAML：`EmberAbyss.yml` `~onDeath` → `ni give`（碎片/孔石，带概率）。清层后背包**未见**入包；latest.log 亦无对应 `成功给予 Tester`。配置存在，实装掉落入包未在本局抽中/归因待后续专项 |
| **出本** `/dp leave` | **PASS** | leave 后可离开本；再 start 受 **5 秒**「副本挑战过快」冷却（预期） |

---

## 3. 补测 · EmberCalamity 单独进本

| 步骤 | 结论 | 说明 |
|------|------|------|
| 出深渊后等待 ≥5s 再 `/dp start EmberCalamity` | **PASS** | `灾厄降临时刻…`；地牢创建信息 `EmberCalamity (3B6D06BF)` |
| Boss 脚本/刷怪 | **PASS** | `外壳坚固。先削甲！` / `余烬灾厄使 已降临。`；附近 mob≥1 |
| `/dp leave` | **PASS** | 出本成功 |
| 备用 `/mm m spawn EmberCalamityBoss …` | **PASS**（历史） | 总控侧更早：`Spawned`/`Killed 余烬灾厄使`；本局以 **DP start** 为主路径 |

> 注意：在 Abyss **内**再 start Calamity = 互斥拒进（预冒烟 PASS）。单独进本必须先 leave。

---

## 4. 增补 · EmberRaid

| 步骤 | 结论 | 说明 |
|------|------|------|
| 门票配置 | 存在 | `NeigeItems/Items/ember-dungeon-tickets.yml` → `ticket_ember_raid`（余烬团本票） |
| `/ni give Tester ticket_ember_raid` | **PASS** | **需 `/ni reload` 后**才识别；reload 前会 `找不到ID`（见异常） |
| `/dp start EmberRaid`（单人 OP） | **PASS**（人数限制行为） | 回显 `团本人数 3～5，当前 (1)` —— 设计 min=3 max=5，单人拒开 = **预期** |
| 3～5 人实开本 | **SKIP** | 冒烟未组队 |

DP：`EmberRaid/option.yml` `$team-condition{min=3;max=5}` + 扣「余烬团本票」。

---

## 5. 增补 · 使魔 / 图鉴（菜单壳）

| 步骤 | 结论 | 说明 |
|------|------|------|
| `/trmenu reload` | **PASS** | `18 menus were loaded` |
| `/trmenu open ember_pet` | **PASS** | tell：`[使魔] 收集出战 · 偏外观与微量助战…逻辑待接线`；无 Exception |
| `/trmenu open ember_bestiary` | **PASS** | tell：`[图录] 怪物 · 装备 · 使魔图鉴…`；无 Exception |
| `/ember` 主菜单 | **PASS** | 欢迎语正常；壳层逻辑待接线不影响壳 PASS |

> reload **前** `Unkown trplugins.menu ember_pet` —— 菜单文件已在磁盘，热更后需 trmenu reload（见异常）。

---

## 6. 分项速查（回报用）

### EmberAbyss
| 门票 | start / 进本 | 清层推进 | 掉落 | 出本 |
|------|--------------|----------|------|------|
| **PASS** | **PASS** | **PASS**（第1→第2） | **SKIP** | **PASS** |

### EmberCalamity
| 路径 | 结论 |
|------|------|
| 本中互斥再 start | **PASS**（拒进） |
| **单独** `/dp start EmberCalamity` | **PASS** |
| Boss 降临 | **PASS** |

### EmberRaid
| 门票 | 单人 start |
|------|------------|
| **PASS**（reload 后） | **PASS**（3～5 人数限制拒开） |

### 使魔 / 图录
| ember_pet | ember_bestiary |
|-----------|----------------|
| **PASS** | **PASS** |

---

## 7. 异常摘要

1. **MythicMobs 示例配置 WARN（启动/reload）**  
   - `ExampleItems.yml` KingsCrown：`GOLDEN_HELMET` not found（1.12）  
   - `ExampleDropTables.yml`：`heroesexp` Drop type not found  
   → 与 Ember* 无关，不阻断。
2. **TrMenu reload** 偶发 `NoSuchMethodError: Player.updateCommands()`（1.12 无此 API）→ 菜单仍加载成功。
3. **副本再进冷却 5 秒**：leave 后立即 start → `副本挑战过快`；等待后正常。
4. **ticket_ember_raid / 新菜单**：文件已落盘但进程未 reload 时 NI「找不到ID」、TrMenu「Unkown menu」→ **`/ni reload` + `/trmenu reload` 后 PASS**。
5. **深渊战斗掉落**：本局清第1层未见 NI 入包日志；配置有概率 onDeath，记 **SKIP**，建议后续专项抽检。
6. **并行**：预冒烟 RpgBot 断线时可能残留实例；补测用 Tester + leave，未停服。

---

## 8. 服状态（结案）

- **未执行** `./stop.sh` / kill  
- **仍在跑：** 是（25565 LISTEN，`paper-custom.jar`）  
- 报告路径：`/workspace/minecraft/STATUS-ember-abyss-raid-smoke.md`
