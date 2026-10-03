# 阶段 4.4 · EmberEliteWeekly 精英周常（N2）

**日期：** 2026-09-27（Asia/Shanghai）  
**作者岗：** 余烬-策划  
**状态：** 落地/balance ✅ 4.4f  
**批准：** 总控全量 I+J+N；落地顺序在 4.1～4.3 之后、主线 4.5 之前  
**承接：** `docs/design/design-stage4-mainline-vol2.md` §3.3.2；对照 `EmberWeekly` 结构  
**给谁：** **插件**（DP+扣次）+ **怪物**（词缀怪/Boss）+ **物品**（票/箱）

**玩家体验：** 入口只有 `/ember` →「精英试炼」；`/dp start` 仅管理/测试（主稿 §0.1）。

---

## 0. 定位

| | 余烬周本 EmberWeekly | **精英试炼 EmberEliteWeekly** |
|--|----------------------|-------------------------------|
| 频率 | 周票 1 | **独立周 1 次**（不抢周票/团票） |
| 人数 | 1～? | **1～2**（单人可过） |
| 时长 | ~10～15 分 | **8～12 分** |
| 卖点 | T1 周首通等 | **词缀精英压力 + 周首通稳定符** |
| 等级 | 较低 | 软门 **EmberLevel ≥40** |

**禁止：** 掉落世界进入 `afk_caps`；与挂机抢材料定位。

---

## 1. ID 与入口

| 项 | 值 |
|----|-----|
| DP 地牢 ID | `EmberEliteWeekly`（底层；玩家不可见） |
| 显示名 | 余烬·精英试炼 |
| **玩家入口** | TrMenu `/ember` →「精英试炼」一点进入 |
| 管理/测试启动 | `/dp start EmberEliteWeekly`（**不对玩家文案出现**） |
| 门票 NI | `ticket_ember_elite`（新建 · NeigeItems） |
| 发放 | 每周一 0:00 Asia/Shanghai 发 **1** 张（与周本同节奏）；**不**进商城日票池 |
| 硬顶 | 持有上限 1；已通关本周拒绝进本 |

### 1.1 TrMenu 补丁（必做 · 4.4 与文档同步）

在 `ember_hub.yml` **周常旁**新增按钮「§e精英试炼」：

- lore：`§7每周 1 次 · 词缀精英 · 周首通稳定符` / `§8需余烬 Lv.40` / `§e➥ 点击进入`
- 点击：直接启动本（底层 DP `EmberEliteWeekly` 或 `corerpg elite start`——**玩家看不见指令**）
- **不要**新建第三层套娃菜单；一点进本即可
- 失败（无票/未到等级/本周已打）：tell 短句，勿弹出命令提示

进本条件（option.yml js-condition 示意）：

- `%corerpg_gate_elite%`==yes（插件：Lv≥40 且本周未通关且有票）或 op  
- 文案失败：`§c精英试炼需要余烬 Lv.40，且本周尚未通关`

---

## 2. 地图

- **占位：** 复用周本/日本地图模板换皮即可（HANDOFF 地图美化仍属后续）。  
- 建议三室短廊：前厅 → 词缀厅 → Boss 厅；刷点落在开放空间（防窒息，对齐深渊教训）。

---

## 3. 波次与文案

### 3.1 结构

| 波 | group | 怪 | 条件 | 开场文案 |
|----|-------|-----|------|----------|
| 1 | wave1 | `EmberEliteZombie`×3 + `EmberEliteSkeleton`×2 | kill-any 对应 Display 共 5 | `§e—— 试炼一：词缀苏醒 ——` |
| 2 | wave2 | `EmberEliteBrute`×1 + `EmberEliteMix`×2 | 蛮精英×1（或 kill-any 3） | `§c—— 试炼二：蛮压词缀 ——` |
| 3 | wave3 | **`EmberEliteBoss`**×1 | Boss×1 | `§4—— 试炼终：烬纹执行官 ——` |

波末：Instant Health III（与深渊/周本一致）→ 下一波。  
wave3 end：`$end … COMPLETE`。

### 3.2 播报

| 触发 | 文案 |
|------|------|
| 进本 | `§e精英试炼开启。§7本周只有一次——词缀会咬人。` |
| 波1 清 | `§7第一层词缀散了。` |
| 波2 清 | `§7蛮压退潮。执行官在前。` |
| Boss 倒 | `§a试炼通过。§7火还在你这边。` |
| 失败/超时 | `§c试炼失败。§7票已扣，下周再来。` |

超时建议 **12 分钟**（可 YAML）。

---

## 4. 怪物规格（1.12.2）

对照档：单人烬刃 T2+6～+8、天赋一层、Lv40+。  
目标：全本 8～12 分；Boss TTK **45～75s**；结束剩余生命 **25～55%**。

| MM ID | Display | Health | Damage | 备注 |
|-------|---------|--------|--------|------|
| `EmberEliteZombie` | 余烬试炼·炽尸 | 160 | 6 | 词缀：小幅加速或点燃（可选技能） |
| `EmberEliteSkeleton` | 余烬试炼·骨刺 | 110 | 5 | 远程；别比挂机骷髅强太多 |
| `EmberEliteMix` | 余烬试炼·混纹 | 180 | 6 | |
| `EmberEliteBrute` | 余烬试炼·蛮纹 | 700 | 5 | 波2 精英 |
| `EmberEliteBoss` | 余烬试炼·烬纹执行官 | **2800** | **4** | 初值；4.3/上线后 bot 调 |

- 材质：僵尸/骷髅/蠹虫等 **1.12.2 有的**。  
- 掉落：小怪材料可走 mmgive（碎片/骨尘小额）；**稳定符只走周首通箱**，不 mm 掉。  
- 不进 `ember_afk, world` 的 afk_caps 世界名单。

---

## 5. 票 / 箱产出

### 5.1 新 NI（物品岗）

| NI ID | 显示名 | 说明 |
|-------|--------|------|
| `ticket_ember_elite` | 余烬精英票 | 周 1；lore 写清不退 |

可放 `ember-dungeon-tickets.yml`。

### 5.2 通关箱（每人 · COMPLETE）

| NI | 数量 | 备注 |
|----|------|------|
| `mat_ember_core_fragment` | 4 | |
| `mat_ember_shard` | 12 | |
| `mat_ember_bone_dust` | 6 | |
| 随机孔石（sharp/steady/drain/gale 一枚） | 1 | 可用 `corerpg loot …` |
| `crystal_ember_enchant` | 1 | |

### 5.3 周首通（额外 · 每玩家每周 1）

| NI | 数量 |
|----|------|
| `mat_ember_stable_charm` | **1** |

命令：`corerpg elite weekly-first <player>` 或并入 progress。

### 5.4 经验

| 来源 | 余烬 XP | 战令 XP |
|------|---------|---------|
| 通关 | **80** | **15** |

`corerpg progress <player> elite_weekly`（插件新增 source；计入合理日/周，**不要**无顶）。

同时：`corerpg quest event <player> elite_weekly_clear`（供主线 ch9）。

### 5.5 reward 脚本顺序（示意）

1. 通关箱 ni give…  
2. 孔石 loot  
3. weekly-first 稳定符（若本周未领）  
4. `corerpg progress … elite_weekly`  
5. quest event elite_weekly_clear  
6. `mvtp … ember_hub`

---

## 6. 与主线 / 深渊的关系

- ch9 步骤 `elite_weekly_clear` 依赖本本可通关。  
- 稳定符月产出：深渊 12 层周首 1 + 本本周首 1 + 主线少量 —— 见总稿 §3.4，勿再加战斗掉落符。

---

## 7. 验收

1. [ ] Lv<40 拒绝；无票拒绝；本周已通关拒绝。  
2. [ ] 有票进本扣 1；通关发箱；周首通稳定符仅 1 次/周。  
3. [ ] 单人平衡落在 §4；无窒息卡波。  
4. [ ] progress + quest event 触发；主线 ch9 可完成。  
5. [ ] 掉落不进挂机日顶。

---

## 8. 专岗

| 岗 | 任务 |
|----|------|
| 物品 | `ticket_ember_elite` |
| 怪物 | 5 只 MM + 技能词缀 |
| 插件 | DP 三波、gate、扣次、progress source、weekly-first、菜单 |
| 测试 | smoke + 周重置 |
| 策划 | 本稿 |

