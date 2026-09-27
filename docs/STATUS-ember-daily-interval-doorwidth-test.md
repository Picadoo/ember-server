# STATUS · 冷却提示 + 庭院门宽 短验收

**日期：** 2026-09-28 07:43:00 Asia/Shanghai  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/STATUS-ember-daily-interval-doorwidth.md`（已推 `3299ae6`）  
**范围：** 菜单 T/R 冷却文案 + 庭院（`daily`）门1/门2 宽 + 清房开门 AIR×9 + Boss 短回归  
**禁项：** 禁改配置 · 禁 killall · **未** commit/push  
**Verdict：** **✅ PASS**（四条硬条全过 · ops=`[]`）

---

## 一句话

CoreRpg **1.15.20** live；`ember_daily` 顶栏 T / 体力说明 R lore 与 R tell 均含「出本后再进约等 5 秒（缓存冷却）」；庭院门1 z=13 / 门2 z=25 铁栅 **x=-1..1 ×9**，**x=-2 为石砖墙**；真击杀清房后两门 **AIR×9**；波次/$kill/Boss 短回归无异常；测后 ops play+login=`[]`。

---

## 四条硬条

| # | 硬条 | 结果 | 证据 |
|---|------|------|------|
| 1 | `ember_daily` 顶栏 **T** 与体力说明 **R** lore（及 R tell）含「出本后再进约等 5 秒（缓存冷却）」 | **✅ PASS** | 静态 YAML + live `/trmenu open ember_daily`：T lore / R lore / 点 R tell 三处均命中 |
| 2 | 庭院门1 **z=13** / 门2 **z=25**：铁栅宽 **x=-1..1（×9）**；**x=-2 非门洞铁栅**（墙砖 OK） | **✅ PASS** | monster.yml 操作块 18（无 loc=-2）；模板图 iron×9 / x=-2=SMOOTH_BRICK；live 进本采样同证 |
| 3 | 清房后开门仍 **AIR×9**；波次/$kill/Boss 无回归（庭院短验） | **✅ PASS** | 真击杀 w1「前厅已清 · 门开了」→ door1 air×9；w2 链式对射→涌尸「回廊已清 · Boss 门开了」→ door2 air×9；终厅见「Boss 蛮兵」文案 |
| 4 | 测后 **ops=[]**（play+login） | **✅ PASS** | play=`[]` · login=`[]`（FIFO `deop` + 写空 ops.json） |

---

## 证据摘要

### 环境

| 项 | 值 |
|----|----|
| CoreRpg jar | **1.15.20**（`plugin.yml`） |
| live 日志 | `Loading/Enabling CoreRpg v1.15.20` @ 06:34:46/52 CST |
| commit 依据 | `3299ae6` fix(daily): cooldown lore + courtyard door width ×9 |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 端口 | proxy 25565 / login 25566 / play 25567 |
| 玩家 | `IdwP5533`（短唯一）· 临时 OP `IdwOp85` |
| JSON | `/tmp/interval-doorwidth-test.json` |

### 硬条1 · 冷却提示

**静态** `plugins/TrMenu/menus/ember_daily.yml`：

- **T** lore：`§8出本后再进约等 5 秒（缓存冷却），不是进本坏了`
- **R** lore：同行
- **R** tell：`…出本后再进约等 5 秒（缓存冷却），不是进本坏了。`

**Live**（`/trmenu open ember_daily IdwP5533`）：

- T：`余烬体力 90/90` · lore 含冷却句
- R：`体力说明` · lore 含冷却句
- 点击 R → tell：`日常消耗 30 体力，0:00（上海）回满；不足无法进本。出本后再进约等 5 秒（缓存冷却），不是进本坏了。`

### 硬条2 · 门宽

| 层 | 门1 z=13 | 门2 z=25 |
|----|----------|----------|
| monster.yml `$operation-block` | x=-1,0,1 × y=65..67 → **9**；无 loc=-2 | 同左 **9** |
| 模板图 `map/ember_daily` | iron(101)×9；x=-2 id=98×3 | 同左 |
| live 进本前采样 | `iron_bars`×9 @ x=-1..1；x=-2=`stonebrick`×3 | 同左 |

### 硬条3 · 开门 + 短回归

| 阶段 | 结果 |
|------|------|
| 进本 | `[日常] 正在进入……（体力 -30）` · `余烬窟·庭院 开始！…门廊安全 · 清前厅开门` · spawn (0,65,0) |
| wave1 | 真击杀 hits=25 · **「前厅已清 · 门开了」** · door1 **air×9**（x=-2 仍 stonebrick） |
| wave2 | 真击杀 hits=38 · 文案「对射」→「涌尸」→ **「回廊已清 · Boss 门开了」** · door2 **air×9** |
| Boss 短 | 进终厅见 **「【终厅·中央垫】Boss 蛮兵！…」**（短回归够用；未强求通关） |
| 禁 killall | ✅ 仅 `attack`/`swingArm` |

### 硬条4 · ops

测后：

```
play:  []
login: []
```

---

## 成功标准核对

| 标准 | 状态 |
|------|------|
| 报告落盘 | ✅ 本文件 |
| 结论明确 | ✅ **PASS** |
| 四条硬条 | ✅ / ✅ / ✅ / ✅ |
| ops 清空 | ✅ |
| 禁改配置 | ✅（仅读 YAML/模板 + 临时 OP 测） |
| 未 commit/push | ✅ HEAD 仍 `3299ae6` |

---

## 阻塞点

**无。**

**最终结论：** 冷却提示 + 庭院门宽短验收 **PASS**。
