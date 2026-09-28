# 设计稿 · 精英周本波次差异（消 kill-any / 厅一链式）

> 债源 / 派工：总控【精英周本波次差异/消 kill-any 薄设计】priority=true  
> 背景：普通周本中核链式 `75e06b0` 测报 PASS（`STATUS-ember-weekly-wave-variance-test.md`）；现网 `EmberEliteWeekly` 厅一/厅二仍用 `$kill-any`（×5 / ×3），与日常+$kill 及周本中核纪律不一致。  
> 对照：`design-ember-weekly-wave-variance.md`（中核链式 PASS 体例）；日常分房 `$kill`（禁 kill-any）。  
> **已批准（总控 2026-09-28）：** 方案 A · 厅一链式 + 两处消 kill-any；厅二不链式（双 `$kill` AND）；**仅** `EmberEliteWeekly/monster.yml`；零碰 option（`wave1`+`wave1b`）。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 / 功能名 | 精英周本 · 波次差异（消两处 kill-any + 厅一 start 链式） |
| 负责人 / 日期 | 余烬-策划 / 2026-09-28（Asia/Shanghai） |
| 关联世界 / 地图 | `ember_elite`（现网三厅短廊，不新挖房） |
| 目标验收里程碑 | 体验迭代（不挡可玩；挡「精英仍 kill-any + 厅一糊一团」） |
| 当前状态 | **已批准 · 本额度施工** |
| 关联实现 / STATUS | P3：`STATUS-ember-weekly-elite-maps-p3.md`；4.4：`STATUS-ember-elite-weekly-4.4.md`；周本链式 PASS：`STATUS-ember-weekly-wave-variance-test.md`（commit `75e06b0`） |

### 硬约束（本条）

| 可动 | 不可动 |
|------|--------|
| **仅** `EmberEliteWeekly/monster.yml` 厅一/厅二组表时序与 `$kill` 文案 | ≥现网 **三厅** 结构；波末 teleport 落点 `(-4,72,270)` / `(22,68,270)` |
| 组内 delay、start 链式 `$monstergroup`；按组独立 `$kill` | Boss / 小怪 **HP**；体力 / 精英票；通关奖励脚本 / 掉落表 |
| Display 文案（重叠体感；去色名对齐 `$kill`） | Display **去色名**字符串本身（须与 MM 一字不差）；`option.yml` / `obstacle.yml` / `task/` |
| | **新建 `$kill-any`**（本轮两处一并消掉，禁止残留） |
| | `EmberWeekly` / 日常七线；`ember_elite/` 小写空壳（若有） |
| | 新 MM id；盲目加血；零增怪外的加怪 |

**总量：** 小怪/精英仍 **炽尸 3 + 骨刺 2 + 蛮纹 1 + 混纹 2 = 8** + 烬纹执行官×1；**零增怪**。超时 720s 不动。

---

## 1. 问题一句话

周本中核已消 kill-any 并链式 PASS；精英试炼厅一/厅二仍是 **同刷 + `$kill-any`**，与日常/周本 `$kill` 纪律不一致，且厅一近战+远程糊成一团、软尾巴感重。

---

## 2. 现网三厅一句话 vs 改后体感

| | 内容 |
|--|------|
| **现网** | 厅一炽尸×3+骨刺×2 同刷同清（**kill-any×5**）→ delay3 传厅二 → 蛮纹×1+混纹×2 同刷同清（**kill-any×3**）→ delay3 传厅三 → 执行官×1 |
| **改后体感** | 厅一先打炽尸，**约 2s 内骨刺远程重叠压上** → 两组都清完才传厅二 → 厅二蛮纹+混纹仍**同刷同清门槛**（仅消 kill-any，不叠压）→ 执行官原样；玩家能感到「厅一不是一团糊完」且厅二仍是蛮压记忆 |

玩家可感知的时间轴差异：**厅一由「同时糊一团」变为「近战先压 + 远程交错重叠」**；厅二时序体感零 diff（只修 `$kill` 纪律）；厅三 Boss 不动。

---

## 3. 杠杆比选（推荐结论前置）

| 候选 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 消两处 kill-any + 厅一 start 链式** | 厅一拆 `wave1a` 炽尸 / `wave1b` 骨刺；start→delay2；传厅二挂 wave1b；厅二同组双 `$kill` AND（或等价拆组同刷） | 对齐周本中核 PASS；清纪律债；零增怪；厅二不抬难 | 厅二不链式 → 宏观仍有一段「同刷等清」 | **推荐 · 主杠杆** |
| B. 厅一+厅二都链式 | 厅二再蛮纹 start→delay2 混纹 | 两厅都有重叠压 | 蛮纹 10500 HP 叠混纹抬难；冲「蛮压词缀」单锚记忆；周本 PASS 亦未对深室做 start 重叠 | **不推荐**（另议否决，见 §3.2） |
| C. 只消 kill-any、厅一也不链式 | 两厅均同组双 `$kill` AND | 改动极小 | **不打断厅一时间轴**；与派工「可选厅一链式对齐周本」不符 | 否（纪律可清，体感债留） |
| D. 只改文案 | 不动条件/时序 | 改动极小 | 不消 kill-any、不改体感 | 否 |

**推荐：A。**  
理由：派工主杠杆=消两处 kill-any + 可选厅一链式；周本中核同杠杆已 PASS；厅二只拆纪律不链式，避免精英票本抬难与记忆冲撞。

### 3.2 厅二是否链式？

**推荐：否 · 只拆组/消 kill-any，不 start 链式。**

| 理由 | 说明 |
|------|------|
| 记忆锚 | 厅二文案「蛮压词缀」；蛮纹是单锚肉盾，混纹为伴生；再叠压易变成「双压糊」 |
| 难度 | 蛮纹 Health **10500** + 混纹×2（2700）；start 重叠显著抬瞬时压力，超出「波次差异」薄改 |
| 对照 | 周本 PASS 只动中核尸↔骷；深室甲→乙仍为 **delay 串行**，未做 start 重叠 |
| 纪律 | 厅二用双 `$kill` AND（或同刷双组）即可消 kill-any，**无需链式也能过验收** |

若总控强制厅二也链式：最小草案见 §6.5（**默认不施工**）。

---

## 4. 站岗表（功能锚）

本条 **无新入口 / 无新 NPC**；玩家路径仍走现网精英菜单 → `EmberEliteWeekly`。

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 精英选本（既有） | 既有枢纽锚 | — | `ember_hub` | 既有 TrMenu / `corerpg elite start` | 无 | 本条不改菜单 |
| 试炼短廊节奏 | — | — | `ember_elite` 内（见 §5/§6） | 无（进本后自动波次） | 波末 teleport 语义不变 | 专岗只改 `EmberEliteWeekly/monster.yml` |

**站岗表自检：** 无新锚；不伪报场景完成；批准后施工 + 抽检。

---

## 5. 调研摘要（现网事实）

调研时点：2026-09-28 Asia/Shanghai；**本稿未改 YAML**。

### 5.1 目录与命名

| 路径 | 角色 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/` | **现网精英试炼**（monster / option / obstacle / task/timeout） |
| `plugins/DungeonPlus/dungeon/ember_elite/` | 若存在则为 OP/空壳侧；**勿当施工目标** |
| `plugins/DungeonPlus/dungeon/EmberWeekly/` | 普通周本（本轮**不动**；中核链式已 PASS） |
| `plugins/MythicMobs/Mobs/EmberEliteWeekly.yml` | 精英 MM 口径 |

### 5.2 三厅结构（无铁栅真门 · 以 teleport 分厅）

现网 **无** `$operation-block` 铁栅门；分厅靠波末 `$teleport`。硬约束解读为：**保留三厅 + 两道波末传送落点**，不删厅、不改落点坐标。

| 厅 | 脚 Y | 关键坐标 | 触发 |
|----|------|----------|------|
| spawn / 入口 | 70 | `(-35,70,270)` | `option` setspawn / dungeon-start |
| **厅一** wave1 | 70 | 炽尸 `(-30,70,270)` · 骨刺 `(-28,70,272)` | 清完 → tp **厅二** `(-4,72,270)` |
| **厅二** wave2 | 72 | 蛮纹 `(-4,72,270)` · 混纹 `(-6,72,268)` | 清完 → tp **厅三** `(22,68,270)` |
| **厅三** wave3 | 68 | 执行官 `(24,68,270)` | COMPLETE |

来源：`EmberEliteWeekly/monster.yml` + `STATUS-ember-weekly-elite-maps-p3.md` §3。中轴 CZ=270、沿 **+X**（与周本 +Z 可辨）。

### 5.3 现网组表 / MM 真名 / 数量 / kill-any 债

| 组 | 时机 | MM id | Display 去色名 | 数量 | 刷点 | `$kill` | 下一段 |
|----|------|-------|----------------|------|------|---------|--------|
| **wave1** | 进本 delay2 | Zombie×3 + Skeleton×2 | 余烬试炼·炽尸 / ·骨刺 | **3+2=5** | `(-30,70,270)` / `(-28,70,272)` | **`$kill-any` ×5**（债） | tp 厅二 + wave2 delay=**3** |
| **wave2** | 厅二 | Brute×1 + Mix×2 | 余烬试炼·蛮纹 / ·混纹 | **1+2=3** | `(-4,72,270)` / `(-6,72,268)` | **`$kill-any` ×3**（债） | tp 厅三 + wave3 delay=**3** |
| **wave3** | 厅三 | `EmberEliteBoss` | 余烬试炼·烬纹执行官 | **1** | `(24,68,270)` | `$kill` ×1 | COMPLETE |

小怪+精英合计 **8** + Boss。超时 `task/timeout.yml`：**720s**（本轮不动）。人数 1～2；等级门 Lv.40。

**现网 kill-any 原文：**

```
wave1: $kill-any{mobname=余烬试炼·炽尸,余烬试炼·骨刺;amount=5}
wave2: $kill-any{mobname=余烬试炼·蛮纹,余烬试炼·混纹;amount=3}
```

### 5.4 MM 口径（禁止本轮改 Health / 掉落技能）

| MM id | Type | Display（去色） | Health（现网） | 备注 |
|-------|------|-----------------|----------------|------|
| `EmberEliteZombie` | ZOMBIE | 余烬试炼·炽尸 | **5250** | 近战；Rush/Ignite |
| `EmberEliteSkeleton` | SKELETON | 余烬试炼·骨刺 | **3300** | **BOW** 远程 |
| `EmberEliteMix` | ZOMBIE | 余烬试炼·混纹 | **2700** | 厅二伴生 |
| `EmberEliteBrute` | HUSK | 余烬试炼·蛮纹 | **10500** | 厅二精英锚 |
| `EmberEliteBoss` | WITHER_SKELETON | 余烬试炼·烬纹执行官 | **5200** | 终局；禁动 |

（设计稿初值表与现网已调血不一致；**以现网 MM YAML 为准，本轮禁止改 Health。**）

### 5.5 option / 票体力（不动）

- map `ember_elite` · spawn `(-35,70,270)` · 人数 1～2 · 等级门 Lv.40  
- 票：CoreRpg `ticket_ember_elite`（option 注释；**不在本文件扣**）  
- 奖励脚本 NI / `corerpg loot elite_gem` / weekly-first / progress / quest / `mvtp ember_hub` —— **一律不动**

### 5.6 对照：普通周本 PASS 写法（`75e06b0`）

| 项 | 周本中核（已 PASS） | 本条精英对齐点 |
|----|---------------------|----------------|
| 拆组 | wave2 → wave2a 尸 / wave2b 骷 | 厅一 → wave1a 炽尸 / wave1b 骨刺 |
| 链 | `wave2a.start` → delay**2** `wave2b` | `wave1a.start` → delay**2** `wave1b` |
| 传送 | 挂 **末组** wave2b.end | 挂 **末组** wave1b.end → 厅二 |
| kill | 分组 `$kill`；无 kill-any | 同；厅二另消 kill-any |
| 深室/厅三 | 甲→乙 delay 串行未叠压 | 厅二**不链式**；厅三 Boss 零 diff |

---

## 6. 可执行方案（推荐 · A）

约定：

- `delay` 单位秒；`$kill` **按 Display 独立**；禁止 `kill-any`、禁止把多 Display 合并进 `$kill-any`。  
- 链式先例：周本中核 / 断塔 / 焦骨 / 霜晶（`start` 拉下一组；**末组**才推进传送/Boss）。  
- **wave3 / teleport 坐标 / MM / option / 日常 / EmberWeekly 一律不动。**

### 6.1 组表（相对现网）

| 组 | 时机 | MM | 数量 | 刷点 | `$kill`（按组） | 门 / 链 / 传送 |
|----|------|-----|------|------|-----------------|----------------|
| **wave1a**（新拆） | 进本 delay2（原 wave1） | `EmberEliteZombie` | **3** | `(-30,70,270)` | 余烬试炼·炽尸 ×**3** | **start** → `$monstergroup{wave1b;delay=2}`；**end 不传厅二、不调 wave2**（仅提示+heal） |
| **wave1b**（新拆） | wave1a start 后 delay **2s** | `EmberEliteSkeleton` | **2** | `(-28,70,272)` | 余烬试炼·骨刺 ×**2** | **end**：heal + tp `(-4,72,270)` + `$monstergroup{wave2;delay=3}` |
| **wave2**（改条件） | 厅二 | Brute×1 + Mix×2 | **1+2** | 现网坐标 | **两条 `$kill` AND**：蛮纹×1 + 混纹×2（**禁 kill-any**） | end：heal + tp `(22,68,270)` + wave3 delay=3（时序零 diff） |
| wave3 | **不动** | 执行官 | 1 | `(24,68,270)` | ×1 | COMPLETE |

小怪+精英合计仍 **3+2+1+2=8** + Boss（零增怪）。原 wave1 组名废弃（由 1a/1b 取代）。

`option.yml` dungeon-start 现调 `wave1` → 施工时改为调 **`wave1a`**（仅组名；delay=2 保留）。**若硬约束「仅 monster.yml」不可动 option：** 则保留组名 `wave1` 作为炽尸组、另增 `wave1b`，由 `wave1.start` 拉 `wave1b`（等价；推荐此写法以 **零碰 option**）。

**零碰 option 的推荐组名落地：**

| 组名 | 角色 |
|------|------|
| `wave1` | = 上表 wave1a（炽尸×3；start 拉 wave1b） |
| `wave1b` | 骨刺×2；end 传厅二 + 调 wave2 |
| `wave2` | 蛮纹+混纹；双 `$kill` AND |
| `wave3` | 不动 |

### 6.2 链式写法（施工备忘 · 对齐周本中核）

```
# option dungeon-start 仍调 wave1（零碰 option）
wave1.start:   message（厅一·炽尸贴上）+ $monstergroup{group=wave1b;delay=2}
wave1.end:     仅「厅一炽尸侧已清」类提示 + heal；禁止 teleport / wave2 / wave3
wave1b.start:  message（骨刺重叠压上 · 清完传厅二）
wave1b.end:    heal + $teleport{location=-4,72,270} + $monstergroup{wave2;delay=3}
wave2.condition:
  - $kill{mobname=余烬试炼·蛮纹;amount=1}
  - $kill{mobname=余烬试炼·混纹;amount=2}    # AND；禁 kill-any
wave2.end:     （传送落点 / wave3 delay 与现网一致）
wave3:         零 diff
```

**禁止：** `$kill-any`；wave1.end 传厅二；厅二 start 拉混纹链式（本轮）；改 teleport 坐标。

### 6.3 建议文案（可施工微调，须保留可测关键词）

| 节点 | 文案意图 |
|------|----------|
| wave1.start | `【试炼·厅一】炽尸贴上 —— 骨刺将交错压上` |
| wave1.end | `厅一炽尸侧已清`（**不要**写传送） |
| wave1b.start | `【试炼·厅一】骨刺重叠压上 —— 清完进入蛮压` |
| wave1b.end | `第一层词缀散了。`（可保留现网句）+ 传厅二 |
| wave2.start | 可保留 `—— 试炼二：蛮压词缀 ——` |
| wave2.end | 可保留 `蛮压退潮。执行官在前。` |
| wave3 | **零 diff** |

### 6.4 与三厅如何叠加不冲掉

| 段 | 记忆点 | 本轮 |
|----|--------|------|
| 厅一 | （新）炽尸与骨刺 **时序重叠**；分 `$kill` | 拆组+链；数量与坐标保留 |
| 厅二 | 蛮纹单锚 + 混纹伴生同刷 | **只消 kill-any**；时序零 diff |
| 厅三 | 烬纹执行官 | **零 diff** |

时间轴目标：**厅一重叠压 → 厅二蛮压等清（纪律已修）→ Boss**，而非三段 kill-any 糊完。

### 6.5 备选（不推荐本轮）：厅二也链式

若总控驳回「厅二不链式」、改批厅二重叠：

- `wave2` 拆 `wave2a` 蛮纹×1 + `wave2b` 混纹×2；`wave2a.start`→delay2 `wave2b`；传厅三挂 wave2b.end  
- **风险：** 蛮纹存活期内混纹远程/近战叠压，TTK/死亡率上升；须测岗加「厅二死亡次数/耗时」条  
- **默认不施工**；批准 A 即可。

### 6.6 厅二「拆组」与「同组双 kill」说明

派工「按组独立 `$kill`」在厅二的薄落地：

| 写法 | 含义 | 本轮 |
|------|------|------|
| **同组双 `$kill` AND** | 仍一个 `wave2`；两条 condition；同刷；两侧都清完才 end | **推荐**（零误传、零时序 diff、改动最小） |
| 双组同刷 wave2a/2b | 须解决「谁挂传送」；易误传 | 不优先 |
| 双组链式 | 见 §6.5 | 不推荐 |

厅一必须拆组（否则无法 start 链式）；厅二用同组双 kill 即满足「消 kill-any / 按 Display 独立 `$kill`」。

---

## 7. 动线（差异段）

入口 / 回枢纽与现网精英相同。下表只写节奏差异段。

| 步骤 | 世界 / 区域 | 玩家看到什么 | 发生什么 | 失败 / 撤离 |
|---|---|---|---|---|
| **厅一重叠（本条）** | `ember_elite` Y70 | 炽尸在场约 2s 内骨刺已压上 | 链式；末组才 tp 厅二 | 超时/撤离沿用现网（720s） |
| 厅二（纪律修） | Y72 | 蛮纹+混纹同刷 | 双 `$kill` AND；时序同现网 | 同上 |
| 厅三（不动） | Y68 | 烬纹执行官 | Boss `$kill` | 同上 |

### 地图与观感

- **独立 map：** `ember_elite` 既有；**不新挖**。  
- **刷点：** 默认现网坐标。  
- **玩法可测 vs 视觉：** 本条只验收节拍与 `$kill` 纪律；地图视觉沿用 P3。

---

## 8. 专岗分工（批准后；策划不施工）

| 专岗 | 交付 |
|------|------|
| **余烬-插件（DP）** | 改 **仅** `plugins/DungeonPlus/dungeon/EmberEliteWeekly/monster.yml`：厅一拆链式；厅二消 kill-any→双 `$kill`；**禁止**改 teleport 坐标 / wave3 / option |
| **余烬-怪物（MM）** | 核口径：沿用 `EmberElite*`；Display 去色名不变；**禁止**涨 Health / 改死亡掉落 |
| **地图** | 不要求（坐标沿用）；仅当刷点无站位时补 1～2 格 |
| **余烬-测试** | 精英打一遍：§10 硬条；厅一重叠窗可测；wave1.end 无 tp；厅二无 kill-any；厅三 Boss 链不变 |
| **余烬-策划** | 本稿；批准前 YAML 零改；勿 commit |

**TrMenu / EmberWeekly / 日常七线：** 本轮禁止触碰。

---

## 9. 不动清单

| 项 | 说明 |
|----|------|
| 体力 / 精英票 | 消耗与发放逻辑不动 |
| 通关奖励 / 掉落 | `option` reward 脚本；MM `~onDeath` 掉落不动 |
| Boss / 小怪 HP | 炽尸 5250 / 骨刺 3300 / 混纹 2700 / 蛮纹 10500 / 执行官 5200 **禁止改** |
| 三厅结构 / teleport 落点 | 仍厅一→厅二→厅三；`(-4,72,270)` / `(22,68,270)` |
| wave3 / Boss 组内容 | 数量、刷点、`$kill`、COMPLETE **零 diff** |
| `EmberWeekly` | **本轮不改**（已 PASS） |
| 日常七线 YAML | 零 diff |
| 新 MM id / 新 NPC / 新房间 | 不要求 |
| `option.yml` / `obstacle.yml` / `task/timeout.yml` | 不动（start 仍调 `wave1`） |
| `$kill-any` | 禁止保留或新建（两处均改为独立 `$kill`） |
| 增怪 | 零增怪；合计仍 8+Boss |

---

## 10. 验收硬条（草案）

| # | 硬条 | PASS 标准 |
|---|------|-----------|
| 1 | 骨架 | 仍：选本 → 厅一 → tp 厅二 → tp 厅三 → Boss → COMPLETE；传送落点坐标不变；玩家零指令 |
| 2 | 厅一重叠 | 炽尸刷出后 **约 2s 内** 骨刺已在场（无需等炽尸全清）；**仅** wave1b `$kill` 完成后才 tp 厅二 |
| 3 | `$kill` 纪律 | 全本无 kill-any；wave1 / wave1b 独立 `$kill`；wave2 为蛮纹×1 **与** 混纹×2 双 `$kill`（AND）；wave1.end **无** teleport / wave2 |
| 4 | 厅二不链式 | wave2 **无** start→delay 拉另一组；蛮纹与混纹仍同刷；体感不为「混纹滞后压上」 |
| 5 | 数值边界 | 无加血；票/奖励/掉落未改；怪仍 **8** + Boss；熟手时长仍合理 |
| 6 | 范围 | 仅 `EmberEliteWeekly/monster.yml` 预期 diff；周本 / 日常 / option / MM Health **零 diff** |
| 7 | 口述可辨 | 熟手口述 ≠「厅一还是一坨清完就传」；能感到炽尸↔骨刺重叠压，且仍记得厅二蛮压与执行官 |

---

## 11. UX 否决条

| 硬条 | PASS / FAIL | 证据或债务说明 |
|---|---|---|
| **零手打指令** | PASS | 沿用精英菜单进本；波次自动 |
| **自定义物均为 NI ID** | PASS | 本条不改掉落；奖励脚本既有 NI |
| **功能锚可点** | PASS（既有） | 无新锚；不宣称新场景完成 |
| **实景地图** | PASS（既有） | 不新挖；链式用现厅一几何 |
| **换皮不算视觉 PASS** | PASS | 本条验收节拍非换皮地图 |
| **占位有名字、有债务、有里程碑** | PASS | 无新占位；已批准施工 |

---

## 12. 交付结论

- [x] 杠杆比选写清（推荐消 kill-any + 厅一链式；厅二不链式；厅二链式为备选否决）。  
- [x] 可执行组表 / delay / `$kill` / 传送挂点已写。  
- [x] 验收硬条 + 不动清单 + 专岗已写。  
- [x] **总控已批准（2026-09-28）** — 方案 A；本额度仅改 `EmberEliteWeekly/monster.yml`；厅二不链式。

**最终结论：** **已批准 · 施工中。** 厅一链式 + 消两处 kill-any；厅二双 `$kill` AND；wave3/option/MM/周本/日常零 diff。

---

## 13. 交卷推荐摘要（给总控）

- **问题：** 精英厅一/厅二仍 `$kill-any`（×5/×3）；厅一近战+远程同刷糊一团；与日常+$kill 及周本中核 PASS 纪律不一致。  
- **推荐杠杆：** **消两处 kill-any** + **厅一 start 链式**（炽尸 start→delay2 骨刺；传厅二挂末组；对齐周本中核 `75e06b0`）；**厅二不链式**（同组双 `$kill` AND，时序零 diff）。  
- **厅二是否链式：** **否**（蛮纹锚+高血叠压抬难；周本 PASS 亦未叠深室）。  
- **改动文件：** **仅** `plugins/DungeonPlus/dungeon/EmberEliteWeekly/monster.yml`。  
- **是否建议施工：** **建议批准后本额度施工**（周本同杠杆已 PASS；改动面单文件；零增怪）；未批准前 YAML 零改、勿 commit。  
- **落盘：** `docs/design-ember-elite-wave-variance.md`
