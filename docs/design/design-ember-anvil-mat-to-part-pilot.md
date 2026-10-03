# 设计稿 · 烬砧材料→部件短链试点（B-anvil-1）

> **STATUS：已批 A · 待物品+插件施工。** 本稿只定现网烬砧（升阶+强化/镶嵌/分解）之上，**材料→可装备部件** 一条可玩短链。  
> **禁**四件甲重做、多部位锻炉大改、誓约技、体力门、B0.1 声称、挑刺、wall-clock / DPS 盲调。  
> 对齐：用户「材料堆着用不出去」· backlog soft「烬砧材料→部件」升本窗 · UX「TrMenu `/ember` 点击 · NI ID · 文案短清楚」。  
> **并行不捆：** 文案薄窗暂缓；**勿捆 B2.x**；B-flex-1 / B-flex-2 已结，本窗不回改。  
> **勿宣称 B0.1 已清。** 验收口径写死：**静态 rg + 菜单轻测**；禁 wall-clock / DPS；**不叫挑刺**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 烬砧 · **材料→部件** 短链试点（B-anvil-1） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 01:23 Asia/Shanghai |
| 关联 | TrMenu `ember_forge` / `ember_hub` · NI 材料/板材 · CoreRpg `forge.yml` · B-flex-1 `stats.offhand` · CoreRpg **1.15.25** |
| 状态 | **已批 A · 待物品+插件** |
| 关联 STATUS / 稿 | backlog soft「烬砧材料→部件」升正式条 **B-anvil-1** · 前序 B-flex-2 close `ba47f3e` |
| 上游 tip | B-flex-2 close `ba47f3e` · B-flex-1 close `d333b01` |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| 新 NI **1** 件可装备部件 + 1 条材料配方（菜单点击合成） | 现网 `forge.yml` T1↔T2↔T3 升阶成本 / 目标 id |
| TrMenu `ember_forge` 薄入口（或 1 页 `ember_part`）；点击炼制 | 四件甲、多部位锻炉大改、誓约三主动、体力日周门 |
| 部件挂入既有 **副手白名单** `stats.offhand`（复用 B-flex-1 钩子） | 改 T0–T3 刃/护符数值、`enhance.yml` 成功率、B-flex-1/2 已交 id/数值 |
| 热更 / reload（服约定） | 文案薄窗 / B2.x 捆；git push；宣称 B0.1 已清；wall-clock/DPS |

---

## 1. 问题一句话

**现网烬砧只有「同槽升阶 + 强化/镶嵌/分解」；地窟/冶炼材料（碎片、铁锭、铁板等）缺一条「点菜单 → 炼成可装备部件」的短链，材料易堆、无部件可玩感。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| **烬砧入口** | NPC `ember_smith` → TrMenu `ember_forge`（`plugins/CoreRpg/hub_npcs.yml` · `docs/design/design-ember-hub-workshop-npcs.md`）。hub「装备成长」行：强化 / 镶嵌 / 附魔 / 套装 / 分解。 |
| **锻炉页能力** | `plugins/TrMenu/menus/ember_forge.yml`：仅 **T1→T2 / T2→T3 预览** + **确认锻造**（`corerpg forge` / `forge confirm`）+ 跳转强化/镶嵌。**无**「材料炼部件」图标。 |
| **升阶配方** | `plugins/CoreRpg/forge.yml`：4 条刃/护符同槽升阶（耗 `mat_ember_shard` / `mat_ember_core_fragment` / `core_ember_compact` / `mat_calamity_ember` + 币）。**不是**多部位锻炉。 |
| **强化/镶嵌/分解** | `ember_enhance` / `ember_socket` / `ember_disassemble`；材料沉入强化表（`enhance.yml` 吃 shard/bone/core）与分解产出（`scrap.yml`）。 |
| **冶炼→板材断点** | CoreSmelt：`ore_ember_*` → `ingot_ember_iron` / `ingot_ember_gold` / `crystal_ember_carbon`（`plugins/NeigeItems/Items/ember-furnace.yml`）。CoreCraft：`ingot_ember_iron` → `plate_ember_iron` / `rod_ember_iron`（demo 板/棍，lore 仅标注「由余烬铁锭合成」）。**板/棍无进入可装备养成链**；升阶/强化表 **不吃** ingot/plate。 |
| **地窟材料** | `mat_ember_shard` / `mat_ember_bone_dust` / `mat_ember_core_fragment`（`ember-dungeon.yml`）→ 强化 / 附魔晶合成 / 升阶 / 铁砧修理。**无**「炼成部件」菜单路径。 |
| **副手已通** | B-flex-1 PASS：`acc_ember_offhand_ward` / `_vita`（`ember-gear-offhand.yml`）+ `stats.offhand` Stat 钩子。获取偏发测/薄接；**非**烬砧材料炼成。 |
| **材料仓** | `warehouse.yml` 可堆材料 → 进一步放大「堆着用不出去」观感（仓本身不是沉底出口）。 |
| **CoreRpg** | live jar **1.15.25**。 |

### 路径速查（施工锚 · 未改）

| 用途 | 路径 |
|------|------|
| 锻炉菜单 | `plugins/TrMenu/menus/ember_forge.yml`（可加图标；或新薄页 `ember_part.yml`） |
| 升阶配方（**本窗零动**） | `plugins/CoreRpg/forge.yml` |
| 强化表（**本窗零动**） | `plugins/CoreRpg/enhance.yml` |
| 冶炼/板材 | `plugins/NeigeItems/Items/ember-furnace.yml` · `ember-craft-fish-combat.yml` · `plugins/CoreCraft/config.yml` |
| 地窟材料 | `plugins/NeigeItems/Items/ember-dungeon.yml` |
| 副手白名单 / Stat | `plugins/CoreRpg/config.yml` → `stats.offhand`；`StatService` OffHand 钩子（B-flex-1 已有） |
| 工坊 NPC | `plugins/CoreRpg/hub_npcs.yml`（`ember_smith` · **不改站位/主交互**） |

---

## 3. 目标体验

- 玩家感知：挂机/地窟/冶炼拿到的 **碎片+铁锭**，能在烬砧菜单 **点一下炼成 1 件副手部件**，材料有去处、部件可装备。  
- 操作：**TrMenu 点击**（`/ember` → 锻炉，或烬砧 NPC → forge 页内「炼部件」）；**少打指令**（底层可 `corerpg part craft …`，玩家 lore **不教**手打）。  
- 数值：**微量**属性（薄于 B-flex-1 守腕/生坠）；**不**开 DPS 轴；**不**绑体力。

---

## 4. 方案

### 方案 A（荐）· 材料→副手部件「余烬灰箍」一条短链

| 块 | 做法 |
|----|------|
| **为何副手部件（非新槽/非升阶）** | ① 复用 B-flex-1 已通 OffHand 钩子，**不**新开四件甲/多部位锻炉。② 对准「材料用不出去」：吃掉 **shard + ingot**（升阶/强化不吃 ingot/plate）。③ 验收薄：静态 rg id + 菜单点炼 → 背包见部件 → 副手生效 lore。 |
| **配方（薄·可测）** | 见下表；一次炼 **1** 件；可重复；材料不足人话 tell。 |
| **UX** | `ember_forge` 增图标「§a炼部件」→ 确认炼制（同页或薄子页 `ember_part` 仅 1 配方）；短 lore。不教 `/corerpg`。烬砧 NPC 主路径仍开 `ember_forge`（方案 A 工坊契约不变）。 |
| **属性挂载** | 新 id **加入** `stats.offhand` 白名单；副手槽生效；**不进**护符 `accessories` best。 |
| **消耗节奏** | 无体力、无币（试点）；材料量见下——够「有意识攒一波再炼」，不做成垃圾倾倒口。 |

#### 试点物 + 配方（荐 A · 仅此一条上线）

| 字段 | 值 |
|------|----|
| 产物 NI id | `part_ember_ash_brace` |
| 显示名 | §a余烬灰箍 |
| material 建议 | `IRON_NUGGET`（与守腕区分可用 damage/lore；或 `FLINT`） |
| lore（示意） | `§8部件 · 放副手槽生效` / `§7烬砧材料炼成 · 微量物防` / `物理防御: +1` |
| 消耗 | `mat_ember_shard` **×12** + `ingot_ember_iron` **×2** |
| 产出 | `part_ember_ash_brace` **×1** |
| 属性 | `物理防御: +1`（**薄于**守腕 +2，免冲曲线） |
| 解锁 | 试点期全员可炼（**禁**绑日周票/体力） |

**实现落点（批后施工择一，策划偏好）：**  
- **荐：** CoreRpg 薄表 `part.yml`（或 `forge.yml` 旁独立段 `parts:`）+ 命令 `corerpg part craft ash_brace`，TrMenu 点按调用；**勿**改现有升阶 4 条。  
- 备选：CoreCraft 新配方 + 菜单仅引导「工作台合成」——UX 较弱（多一步方块），**不荐**作主路径。

**利：** 短链完整、复用副手、沉掉 ingot 断点、与升阶/强化正交、测口只 rg+菜单。  
**弊：** 需 NI 1 件 + 白名单 1 行 + 菜单 1 钮 + 薄扣物逻辑（物品±插件）。

### 方案 B（极薄旁附 · **勿与 A 同窗双上**）

| B 选项 | 内容 | 何时 |
|--------|------|------|
| B1 · **换部件类型** | 同短链改产物为生向：`part_ember_bone_charm`「余烬骨饰」· `mat_ember_bone_dust`×10 + `mat_ember_shard`×5 → lore `生命力: +4`（薄于生坠 +8）· 仍副手 | 总控否决物防向或想先沉骨尘时 **整窗换皮** |
| B2 · **同产物换皮配方** | 仍 `part_ember_ash_brace`：改耗 `plate_ember_iron`×1 + `mat_ember_shard`×8（沉板材 demo），或数量 ±2 | A 已通、仅经济手感微调 |

**本轮默认：只批 A（灰箍）；B 仅总控点名替换，禁止「灰箍+骨饰」两件同时开炼。**

### 明确不施工 / 未动

- 四件甲 / 盔甲槽 / 多部位锻炉大改  
- 改 `forge.yml` 升阶成本与 T 档目标  
- 改 `enhance.yml` / 镶嵌孔石数值 / 誓约三主动 / 体力日周门  
- 改 B-flex-1 守腕/生坠数值、B-flex-2 踏步  
- 材料仓经济重做、新货币、新副本产线  
- 文案薄窗 / B2.x 捆；宣称 B0.1 已清  
- wall-clock / DPS 验收；叫挑刺玩家  

---

## 5. 硬约束表（可动 / 禁动）总览

| 域 | 可动 | 禁动 |
|----|------|------|
| NI | +1 部件 id（A 或 B1 择一） | 改既有刃/护符/孔石/票券 id 数值 |
| 菜单 | `ember_forge` 加炼部件钮；可选 `ember_part` | hub 大改；教手打斜杠 |
| 插件 | 薄 craft 扣物+给物；offhand 白名单 +1 | 升阶/强化/誓约/体力 YAML 数值 |
| 验收 | 静态 rg + 菜单轻测 | wall-clock / DPS / 挑刺 |
| 文档 | 本 tip + backlog | 宣称 B0.1 已清 |

---

## 6. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A/B | **本窗**（文档 only） |
| **物品** | NI `part_ember_ash_brace`（或 B1 骨饰）定义 + lore | **批 A 后** |
| **插件** | 扣物配方表 + TrMenu 接线 + `stats.offhand` 加 id；（若需）`part` 子命令 | **批 A 后**（可与物品同窗或物品先） |
| **测试** | 静态 rg + 菜单轻测（炼成→副手）；**不叫挑刺** | 施工后 |

**未批准前不施工**（含不改玩法 YAML / 不改 NI 生产物）。

---

## 7. 验收口径

**本稿（设计窗）**

- [x] 只读扫网：烬砧=升阶+强化/镶嵌/分解；板/锭无装备沉底；证据入 §2  
- [x] 荐 A 唯一主推 + B 极薄旁附（勿双上）  
- [x] 明确未动 / 不做清单  
- [x] backlog：soft 升 **B-anvil-1**；B-flex-1/2 已勾销注明；**勿捆 B2.x**；勿宣称 B0.1 已清  
- [x] STATUS：**待批 · 未批准前不施工**

**若批 A 施工后（另 commit）**

- [ ] `rg 'part_ember_ash_brace' plugins/NeigeItems/` 命中；`forge.yml` 升阶 4 条 **无 diff**  
- [ ] `rg 'part_ember_ash_brace' plugins/CoreRpg/config.yml`（或 offhand 白名单路径）命中  
- [ ] TrMenu：`ember_forge`（或 `ember_part`）可点「炼部件」；玩家 lore **无**教手打 `/corerpg`  
- [ ] **菜单轻测：** 材料足够 → 炼成 1 件入包；不足 → 人话提示；副手装备后属性行可见（单次轻测）  
- [ ] **禁项声明：** 未改升阶/强化/誓约/体力/四件甲/B-flex 已结物；**未宣称 B0.1 已清**；验收 **未**用 wall-clock/DPS；**不叫挑刺**

---

## 8. Backlog 软挂（本窗不捆）

| 软挂项 | 说明 | 本窗 |
|--------|------|------|
| B1 骨饰 / B2 换皮配方 | 与 A 互斥换窗 | **不与 A 同上** |
| 分解白名单收部件 / 日箱掉部件 | 经济接线 | 批 A 后可另薄窗 |
| plate/rod 全沉底表 | 多配方扩展 | **明确不做**（本窗只 1 链） |
| 四件甲 | `ember_set` 已写「日后再扩」 | **明确不做** |
| 文案薄窗 / B2.x | 另线 | **不捆** |

---

## 9. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 材料炼部件（UI） | `ember_smith`（既有） | §6锻炉师 · 烬砧 | `ember_hub` 工棚（既有站位） | `ember_forge`（+炼部件钮）或 `ember_part` | 升阶/强化/镶嵌仍方案 A | **UI 扩既有入口**；无新 NPC |
| 枢纽菜单入口 | — | 余烬 · 主菜单 | `/ember` | `ember_hub` → 锻炉 | — | 玩家可不打 forge 指令 |

**站岗自检：** 无新 NPC；菜单路径不要求手打命令；不宣称场景新建筑完成（标 **UI 可用**）。

---

## 10. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 枢纽 | 烬砧 NPC 或 hub「锻炉」 | 打开 `ember_forge` | 返回 hub |
| 2 | 锻炉页 | 「炼部件 · 余烬灰箍」 | 显示消耗 shard×12 + 铁锭×2 | 点说明可返回 |
| 3 | 确认 | 点击炼制 | 扣材料 → 给 `part_ember_ash_brace`×1 | 材料不足人话；不扣 |
| 4 | 背包/副手 | 灰箍 | 放入副手槽 → 微量物防生效 | 未放副手则无属性 |

---

## 11. UX 否决条

| 硬条 | 预期 |
|------|------|
| 玩家路径必须手打 `/corerpg` 才能炼部件 | **FAIL** |
| 文案宣称「多部位锻炉/四件甲已开放」或「灰箍+骨饰均已上」 | **FAIL** |
| 未批改玩法 YAML / 升阶成本 / 强化表 / 体力门 | **FAIL** |
| 验收靠 wall-clock 或 DPS 表 / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 | **FAIL** |

---

## 12. 回总控摘要

- **选窗：** B-anvil-1 烬砧材料→部件短链（soft 升正式；**与文案薄窗无关；勿捆 B2.x**）。  
- **荐 A：** `mat_ember_shard`×12 + `ingot_ember_iron`×2 → `part_ember_ash_brace`「余烬灰箍」· 副手 · 物防 +1 · TrMenu 点击。  
- **极薄 B：** 换生向骨饰，或同产物换皮配方；**勿两件一起上**。  
- **未捆：** 四件甲 / 锻炉升阶大改 / 誓约 / 体力 / B-flex 回改 / B2.x / B0.1。  
- **验收：** 静态 rg + 菜单轻测；禁 wall-clock/DPS；不叫挑刺。  
- **STATUS：待批 · 未批准前不施工。**

## 13. 批准记录

| 字段 | 记录 |
|------|------|
| 决策 | **APPROVED A**（不附 B）· 总控 |
| 设计 tip | `ab33c21` |
| 批准 tip | 见 `docs/status/STATUS-ember-anvil-mat-to-part-pilot-approve.md` |
| 状态 | **已批 A · 待物品+插件** |
| 下一棒 | 物品 NI + 插件配方/菜单 → 测试 |
