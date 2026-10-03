# 设计稿 · 副手守腕/生坠薄获取口（B-flex-3）

> **STATUS：已批 A（总控 · 2026-09-29 04:17 Asia/Shanghai）。** 本稿只定路人拿到 `acc_ember_offhand_ward`（守腕）/`acc_ember_offhand_vita`（生坠）的**薄获取口**——二选一荐案已定：**灰粮 stub 兑换/购买**。  
> **本窗 commit 只写 docs；玩法/NI YAML 零改。**  
> **禁**四件甲、锻炉重做、动 T0–T3 刃护符数值与体力日周门；灰箍配方数值本窗不动；踏步热键 / 灰箍抢口文案不捆。  
> 对齐挑刺 #1 soft（只读 · 勿开挑刺流程）。旁记：B2.44 close `5d65f54`（本窗不是斜杠文案窗）。  
> **勿宣称 B0.1 已清。** 验收：**静态 rg + 菜单轻测**；**禁**长测/挑刺。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 副手 **守腕/生坠薄获取口**（B-flex-3） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 04:15 Asia/Shanghai |
| 关联 | NI `ember-gear-offhand.yml` · `stats.offhand` · TrMenu `ember_life` · CoreRpg `life.yml` · 灰粮 NPC `ember_quartermaster` · 挑刺 `docs/status/STATUS-ember-flex-trilogy-picky.md` #1 |
| 状态 | **已批 A** · 交插件施工 |
| 上游 | B-flex-1 PASS close `d333b01` · B-anvil-1 灰箍 PASS · B2.44 close `5d65f54`（旁记） |
| tip 路径 | `docs/design/design-ember-flex-offhand-thin-acquire.md` |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `life.yml` 增 1～2 条 offers（币购守腕/生坠） | 现有 T0–T3 刃/护符/饰品 **数值**；体力日周门 |
| `ember_life.yml` 增图标 + 薄 layout | 四件甲、锻炉/`forge.yml`/`part.yml` 灰箍配方数值 |
| （可选）hub/set 一行「灰粮可购」说明 | NI 两件属性 lore；MM 日线 Boss 掉落表大改；git push；宣称 B0.1 已清 |
| | 踏步热键 / 灰箍抢口文案窗；长测/挑刺 |

---

## 1. 问题 / 为何要薄获取

**副手试点已 PASS（属性真生效），但路人摸不到货：套装页只 tell「放副手槽生效」，游玩路径 = 看说明书，实物靠 `/ni give`。**

需要一条**当天可触达、改动面薄、不碰数值体系**的获取口，把守腕/生坠从「发测物」变成「菜单能点到的试点货」。

---

## 2. 对齐挑刺 #1 soft（只读摘要 · 勿扩 scope）

摘自 `docs/status/STATUS-ember-flex-trilogy-picky.md`（2026-09-29 · 无挡级）：

- 属性真生效；**刺：没有游玩获取口**（日箱/灰粮 stub 设计里挂着没接）→ 路人 = 说明书 + `/ni give`。
- 软债 #1：**守腕/生坠薄获取**（日箱低权 **或** 灰粮 stub）——否则副手窗对路人是说明书店。
- 与灰箍抢同一副手槽（选择感有、厚度不够）——**本窗只补获取口，不调灰箍配方/数值**。

---

## 3. 现网证据摘要（只读扫网 · 本稿未改）

### 3.1 两件 NI + 其它副手

| id | 显示名 | 文件 | 属性 | 掉落/商店/配方 |
|----|--------|------|------|----------------|
| `acc_ember_offhand_ward` | §a余烬守腕 | `plugins/NeigeItems/Items/ember-gear-offhand.yml` | `物理防御: +2` · `IRON_NUGGET` | **无**掉落/商店/配方；仅白名单 + hub/set 说明；发测 `/ni give` |
| `acc_ember_offhand_vita` | §a余烬生坠 | 同上 | `生命力: +8` · `EMERALD` | 同上 |
| `part_ember_ash_brace` | §a余烬灰箍 | `ember-gear-parts.yml` + `part.yml` | 副手物防 +1 | 烬砧「炼部件」已有（B-anvil-1）；**本窗不动配方数值** |

白名单：`plugins/CoreRpg/config.yml` → `stats.offhand` 含 ward / vita / ash_brace。

### 3.2 日箱可挂点（低权真实落点）

| 层 | 现网 | 可否「低权」 |
|----|------|-------------|
| **通关箱** | 七线 `DungeonPlus/.../EmberDaily*/option.yml` → 定发核心碎片×1 + 附魔晶×1 + 碎片×5 + xp（**无权重**） | 不适合「低权」——改这里等于加保底件，非薄权 |
| **Boss 装掉** | 七线 `MythicMobs/Mobs/EmberDaily*.yml`：`gear_ember_blade` **0.12** / `gear_ember_charm` **0.08** / `gear_ember_t1_blade` **0.03**（`corerpg mmgive`） | **可挂**：仿一行 `mmgive … acc_ember_offhand_* 1` + 低概率（如 ≤0.05）；须改 **7 个** MM 文件 + 预览 `ember_daily_rewards.yml` |
| **预览菜单** | `ember_daily_rewards.yml` 仅展示刃/护符/精炼机会 · **无**副手 | 若走日箱须同步加图标 |

### 3.3 灰粮 stub 可挂点

| 层 | 现网 | 可否挂 1～2 副手 |
|----|------|------------------|
| NPC | `ember_quartermaster`「补给官 · 灰粮」→ `menu: ember_life`（`hub_npcs.yml`） | 动线已通 |
| 兑换壳 | `plugins/CoreRpg/life.yml` → `offers:`（`bread`/`rod`/魂尘/使魔等 · `coin` + 可选 `inputs`/`weekly`/`daily`/`life_level`） | **可挂**：新 offer key → `give: { acc_ember_offhand_ward: 1 }` 等 · **零配方** |
| 菜单 | `plugins/TrMenu/menus/ember_life.yml` · `command: corerpg life buy <key>` | **可挂**：加 1～2 图标 + layout 薄扩 |
| 商城 | `ember_shop` = 便利·特权（体力药/月卡等）· **不卖毕业伤害** | **不荐**挂试点副手（付费架叙事不符） |

---

## 4. 荐方案（二选一 · 明确推荐）

### **荐：方案 2 · 灰粮 stub 币购（守腕 + 生坠各 1 条）**

**一句话：** 在现成 `life.yml` offers + `ember_life` 菜单壳上挂两件币购（周限 1、无生活等级门），路人当天点灰粮就能摸到，改动面最薄、不碰掉落数值体系。

| 块 | 做法（批 A 后施工 · 本稿不改 YAML） |
|----|-------------------------------------|
| 后端 | `life.yml` 增 `offhand_ward` / `offhand_vita`：`coin: 80` · `give` 对应 NI ×1 · `weekly: 1` · **不设** `life_level`（路人当天可买） |
| 菜单 | `ember_life.yml` layout 薄扩两格（建议图标键 `O`/`W`）；材料对齐 NI（铁粒 / 绿宝石）；lore 写清「副手饰品 · 放副手槽生效」+ 价 + 周限；`command: corerpg life buy offhand_ward\|vita` |
| 动线 | 枢纽右键灰粮 **或** hub「补给/生活」→「补给 · 生活」→ 点守腕/生坠 → 购入（**勿教手打指令**） |
| 可选薄文案 | `ember_set` / hub 套装行加半行「工坊灰粮可购」——非硬条，批后可裁 |

### 为何不选方案 1（日箱低权）

1. **「低权」真钩子在七线 Boss MM**，不是通关箱；通关箱是定发材料，挂副手会变成保底件、偏厚。  
2. 改面：**7×** `EmberDaily*.yml` + 奖励预览，大于灰粮两文件。  
3. **RNG ≠ 路人当天能摸到**——与决策原则「当天能摸到」冲突；低权还可能几天摸不到，说明书问题只缓解一半。  
4. 与刃 12% / 护符 8% 同池抢注意力，易被读成「又一件装掉」，不像「副手试点薄入口」。

（若总控否决灰粮、强要日箱：备选硬条见 §6.2，仍须批 A 另开施工。）

---

## 5. 硬条（可施工清单 · 仍只是设计）

批 A 后施工窗建议只动下列（**本稿与本 commit 零改**）：

1. **`plugins/CoreRpg/life.yml`**  
   - `offers.offhand_ward`：`name: '&a余烬守腕'` · `coin: 80` · `give: { acc_ember_offhand_ward: 1 }` · `weekly: 1`  
   - `offers.offhand_vita`：`name: '&a余烬生坠'` · `coin: 80` · `give: { acc_ember_offhand_vita: 1 }` · `weekly: 1`  
2. **`plugins/TrMenu/menus/ember_life.yml`**  
   - Layout 增加两键（例第 3 行或底栏旁扩 `O`/`W`）  
   - 图标：守腕 `iron nugget`「§a余烬守腕」；生坠 `emerald`「§a余烬生坠」  
   - lore：`§780 余烬币` · `§8每周 1 次` · `§8副手饰品 · 放副手槽生效` · `§e➥ §f购买`  
   - `command: corerpg life buy offhand_ward` / `offhand_vita`  
3. **不动：** NI `ember-gear-offhand.yml` 属性；`part.yml`/`forge.yml` 灰箍；MM 日线；体力门；T0–T3 刃护符。

价锚：面包 40 / 钓竿 60 → 试点副手 **80 币 · 周 1**（永久薄样、远低于护符曲线，避免「白送毕业」观感）。总控可批时微调价，不另开窗。

---

## 6. 备选（不荐 · 仅留档）

### 6.2 方案 1 · 日箱 Boss 低权（否决灰粮时用）

- 七线 `EmberDaily*.yml` Boss 段各加：  
  `mmgive … acc_ember_offhand_ward 1` @ **0.04** · `…_vita 1` @ **0.04**（或二选一随机一条 0.05——须施工窗再钉）  
- 同步 `ember_daily_rewards.yml` 预览两图标  
- **勿**改通关箱定发三件套

---

## 7. 非目标 / 硬禁

- 勿四件甲；勿锻炉重做；勿动 T0–T3 刃护符数值与体力日周门  
- 灰箍配方数值本窗不动（抢口文案另开 soft）  
- 踏步热键不捆  
- 禁长测/挑刺；不叫挑刺玩家；勿宣称 B0.1 已清  
- **玩法/NI YAML：交稿本窗零改**；批 A 前不施工  
- 不进 `ember_shop` 付费架；不改 NI 两件物防/生命数字

---

## 8. 玩家动线（TrMenu · 勿教指令）

1. 到枢纽广场 → **右键「补给官 · 灰粮」**（或 hub 点进「补给 · 生活」）。  
2. 打开 **「补给 · 生活」** → 看到 **余烬守腕 / 余烬生坠**。  
3. 点击购买（够币且未超周限）→ 入包 → 放 **副手槽** 生效（套装页既有说明可沿用）。

后台 `corerpg life buy …` 仅菜单 action，**不对玩家教学**。

---

## 9. 验收口径（批 A · 施工后）

| # | 检查 | 期望 |
|---|------|------|
| 1 | 静态 `rg` `offhand_ward`/`offhand_vita` 于 `life.yml` + `ember_life.yml` | 键/价/give id 与硬条一致 |
| 2 | 静态 `rg` `acc_ember_offhand_ward|vita` 仍仅 NI + offhand 白名单 + 新 acquire 引用 | 无误改属性 lore |
| 3 | 菜单轻测：灰粮 → 生活 → 点购守腕/生坠 | 入包；周限提示正常 |
| 4 | 回归：面包/钓竿/魂尘等旧 offers 仍可点 | 无回归坏 |

**禁：** wall-clock 长测、多轮掉率统计、挑刺流程、宣称 B0.1 已清。

---

## 10. 总控批示位

- [x] **批 A** · 按荐案（灰粮 stub）开施工（总控 · tip 本提交）  
- [ ] **批 A′** · 改走日箱低权（用 §6.2）——**未选**  
- [ ] **驳回** · 说明  

**批示摘要：** 采纳灰粮 stub 币购（`offhand_ward`/`offhand_vita` · coin 80 · weekly 1 · 无 life_level）；日箱低权不选；NI 属性/灰箍/体力门/T0–T3 刃护符不动；禁长测/挑刺；勿宣称 B0.1 已清。交 **余烬-插件**（`life.yml` + `ember_life.yml`）；可选 hub/set 半行文案可裁。

