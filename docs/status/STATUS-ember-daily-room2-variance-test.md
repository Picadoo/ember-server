# STATUS · 日常第二房 / Boss 前门压试点 · §8 硬条短抽

**日期：** 2026-09-28 08:28 Asia/Shanghai  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-daily-room2-variance.md` §8 · `docs/status/STATUS-ember-daily-room2-variance.md` · commit `3459d53`  
**测前：** play FIFO 控制台 `dp reload` → `[08:10:36] [DungeonPlus] 插件重载完毕`（**RCON 关** `enable-rcon=false`）  
**Verdict：** **✅ PASS**（三线产品硬条全过 · ops=`[]`）

---

## 一句话

庭院/潮蚀 Boss 前门压（尸贴 vs 浪矢射）与断塔房2链式重叠（Δ≈2.4s、顶门挂 wave2b）live 实锤；房2 必改4 节拍保留；霜晶/锈轨/焦骨/地窖 YAML 零 diff；真击杀、禁 killall、禁改配置、未 commit/push；测后 ops play+login=`[]`。

---

## 三线总表

| 线 | enter | 杠杆硬条 | 结果 | 要点证据 |
|----|-------|----------|------|----------|
| 庭院 | `daily` | door2 后先门槛尸×2，清完才 Boss；房2 仍对射→涌；wave1≈4 | **PASS** | prepSeen≪boss；文案「终厅·门槛…贴脸」→「门槛已清 · 蛮兵现身」；wave1×4；door1/2 AIR×9 |
| 潮蚀 | `daily_tide` | door2 后先门槛浪矢×2，清完才 Boss；射 vs 贴可区分；房2 仍桥射→冲 | **PASS** | prep=skeleton；文案「闸厅·门槛…浪矢点射」→「门槛卫已清 · 潮闸蛮兵现身」；与庭院手感可区分 |
| 断塔 | `daily_spire` | 对射后≈2s 内卫尸在场；顶门仅 wave2b 清完后开 | **PASS** | 重叠 Δ=**2408ms**（对射仍在）；重叠时顶门 iron×9；**无「对射侧已清」却已「环廊已清 · 顶门开了」**→开门挂 wave2b |

**总评：** PASS

---

## 分线证据

### 1. 庭院 · Boss 前门压（尸贴）— PASS

- **玩家：** `R2vY108` · OP 临时 `R2vOp90`
- **wave1≈4：** 进本快照僵尸×**4**（`$kill×4`）
- **房2 仍对射→涌：** 先 skeleton（firstSkel=251ms，双侧）→「对射已清 · 僵尸涌上」→「回廊·涌尸」
- **≥2 真门：** door1 z=13 **air×9**；door2 z=25 **air×9**
- **前压：** door2 后 prepSeen=**200ms**、bossSeen=null；文案「【终厅·门槛】两侧僵尸贴脸 —— 清完才出蛮兵」→「门槛已清 · 蛮兵现身」→「【终厅·中央垫】Boss 蛮兵」→通关
- **击杀：** 仅 `attack`/`swingArm`；禁 killall

### 2. 潮蚀 · Boss 前门压（浪矢射）— PASS

- **玩家：** `R2vT542`
- **wave1≈4：** 沿岸僵尸×**4**
- **房2 仍桥射→冲：** 先浪矢骷×3 →「对岸弓已清 · 潮蚀尸冲锋」
- **≥2 真门：** door1 z=18 / door2 z=38 **air×9**
- **前压：** prep=**skeleton**（浪矢）@门槛；prepSeen=**201ms**、bossSeen=null；「【闸厅·门槛】两侧浪矢点射」→「门槛卫已清 · 潮闸蛮兵现身」→通关
- **与庭院区分：** 庭院 prep=zombie 贴脸；潮蚀 prep=skeleton 点射（`feel_ranged_vs_melee=true`）

### 3. 断塔 · 房2 链式重叠 — PASS

- **玩家：** `R2vS455`（首轮完整通关）
- **重叠：** tSkel=150ms → tZombie=2558ms · Δ=**2408ms**（≈2s 窗内）；卫尸刷出时对射 sk≥1 仍在场；双侧箭骷
- **重叠时顶门：** z=4 y70 **iron_bars×9**
- **顶门仅 wave2b：** 全程聊天 **无**「对射侧已清」（wave2a.end 未触发），却有「环廊已清 · 顶门开了」+ door **air×9** + Boss/通关 → 开门条件在 wave2b `$kill`，非等对射全清
- **YAML 对照：** wave2a.start 链式 `delay=2`；wave2a.end 仅提示+heal（无 door）；wave2b.end 顶门×9 AIR
- **测法备注：** 首轮自动项 `door2_still_iron_after_skel` 曾 FAIL，实为清对射期间卫尸 `$kill` 已先满足（测法噪声，恰佐证 wave2b 开门）。定向复测 `R2sP695` 重叠 Δ=2894ms 再确；先清卫尸时怪坠落未计入 `$kill` 致门未开——**不否决**首轮产品证

---

## 共性

| 项 | 结果 | 证据 |
|----|------|------|
| ≥2 真门骨架 | ✅ | 三线 door1+door2 均 AIR×9 后见 Boss |
| `$kill` 无跨组卡死 | ✅ | 无 `$kill-any`；断塔无「对射侧已清」仍能开门（组独立） |
| 不加血 | ✅ | commit `3459d53` 未碰 MythicMobs；Health 未改 |
| 非试点零 diff | ✅ | Frost/Rail/Ash/Crypt `git diff HEAD` 空；commit 仅 3 份 monster.yml + STATUS |
| 禁 killall / RCON | ✅ | 脚本真击杀；`enable-rcon=false`；热更走 FIFO |
| 测后 ops | ✅ | play=`[]` · login=`[]`（De-opped R2vOp90 / R2sOp72） |
| 体力 | ✅ | 三线进本文案「体力 -30」 |

---

## 环境

| 项 | 值 |
|----|-----|
| commit | `3459d53` feat(dp): 日常三线第二房/Boss前压试点 |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 热更 | FIFO `console.in` → `dp reload`（08:10:36 CST） |
| 工作区 | `/workspace/minecraft` |
| JSON | `/tmp/room2-variance-test.json`（可选复测 `/tmp/room2-spire-retest.json`） |
| 禁改配置 / 未 commit/push | ✅ |

---

## §8 硬条核对

| # | 硬条 | 结果 |
|---|------|------|
| 1 | 骨架 ≥2 真门 → Boss | ✅ |
| 2 | 庭院前压 门槛尸×2 再 Boss | ✅ |
| 3 | 潮蚀前压 门槛浪矢×2 再 Boss（射 vs 贴） | ✅ |
| 4 | 断塔重叠 ≈2s 卫尸在场；顶门仅 wave2b | ✅ |
| 5 | `$kill` 纪律（无跨组合并 / 无 kill-any） | ✅ |
| 6 | 与必改4不冲（房2 对射→涌 / 桥射→冲 / 双侧箭） | ✅ |
| 7 | 数值边界（不加血；波次裁至≤12） | ✅ 静态+commit |
| 8 | 非试点零 diff | ✅ |

---

## 阻塞点

**无产品 blocker。**

测法债（非阻断）：断塔「先清卫尸、对射仍在」定向采样时卫尸易坠落导致 `$kill` 未计入——若插件后续要加坠落击杀计入或刷点防坠，可另开；本试点硬条已由首轮「无 wave2a.end 却开门」证毕。

---

## 回执摘要（给总控）

- **总评：** PASS  
- **三线：** 庭院 PASS · 潮蚀 PASS · 断塔 PASS  
- **共性：** ≥2 门 · `$kill` 未跨组卡死 · 不加血 · 非试点零 diff · ops=`[]`  
- **报告：** `docs/status/STATUS-ember-daily-room2-variance-test.md`  
- **JSON：** `/tmp/room2-variance-test.json`  
- **ops：** `[]`  
- **阻塞点：** 无  
- **未 commit/push**（交总控推）
