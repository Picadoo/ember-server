# 设计稿 · 副手/饰品位试点（B-flex-1）

> **未批准前不施工。** 本稿只定「刃+护符之外 +1 副手/饰品位」的薄试点：NI 白名单 + 菜单展示 + 极简属性。  
> **禁**全套四件甲、技能大改、本窗重做锻炉产线；**禁**动体力日周门与现有 T0–T3 刃/护符数值。  
> 对齐：用户「稍微灵活」· 总控自决拍板 · UX「TrMenu · 少打指令 · NI 自定义物 · 文案短清楚」。  
> **并行不捆：** B2.17（天赋 `cost`→消耗）另线；技能可装配、烬砧材料→部件产线仅 **soft backlog**。  
> **勿宣称 B0.1 已清。** 验收口径写死：**静态 rg + 菜单点开目视**；禁 wall-clock 长测、禁多轮 DPS 盲调。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 装备灵活试点 · **+1 副手/饰品位**（B-flex-1） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 00:48 Asia/Shanghai |
| 关联 | `StatService` · NI `ember-gear-*.yml` / `ember-dungeon.yml` · TrMenu `ember_hub`/`ember_set`/`ember_forge` · `hub_npcs.yml` 烬砧 |
| 状态 | **未批 · 待总控批 A（或极薄 B）** |
| 关联 STATUS / 稿 | backlog `B-flex-1` · 批后 STATUS-approve / 测报（仅 rg+目视） |

### 硬约束（本稿）

| 可动（**仅若批 A/B 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| 新 NI 副手/饰品 1～2 件（新 id；微量属性） | 现有 T0–T3 `gear_ember_*_blade` / `charm`/`*_talisman` **数值/lore 基数** |
| `stats` 新白名单键（建议 `offhand:`）+ `StatService` 副手槽读取钩子 | 体力日/周门、进本 cost、`afk_caps`/`over_chance*` |
| TrMenu 薄展示（hub 装备行 / `ember_set` 增一行说明） | 四件甲（盔甲槽成套）、技能树/技能装配大改、`skills.yml` 数值 |
| 热更 / reload（服约定） | 重做 `forge.yml` 产线 / 多部位锻炉；改分解/强化/镶嵌数值曲线 |
| | B2.17 天赋文案；git push；宣称 B0.1 已清 |

---

## 1. 问题一句话

**现网成长几乎只有「主手刃 + 背包最佳一件护符」一条轴，玩家嫌单调；要「稍微灵活」——在不动刃护符数值与锻炉产线的前提下，加 1 个可感知的副手/饰品位试点。**

---

## 2. 现网事实（只读扫网 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| **刃** | NI：`gear_ember_blade` / `_t1/_t2/_t3_blade`（`plugins/NeigeItems/Items/ember-dungeon.yml`、`ember-gear-t1.yml`…）。`StatService`：**仅主手**计入；读 lore `物理伤害` → `phys_damage`。 |
| **护符** | NI：T0 `gear_ember_charm`；T1+ `gear_ember_t*_talisman`。`StatService`：**背包任意格 + 副手**里白名单饰品取 **best 一件**（`max_health + phys_defense×3` 打分）；读 `生命力`/`物理防御`。默认白名单仅 charm/talisman 四档（config 未显式列出时走 Java 默认）。 |
| **团戒** | `acc_ember_raid_ring`（`ember-gear-t1.yml`）· `set.yml` 与任意刃组成「余烬同袍」；**背包持有即计件**。lore 有微量属性，但 **不在** `accessoryIds` 默认白名单 → Stat 层不当护符叠。菜单：`ember_set`（明文「日后 4 件（甲/饰品）再扩」）。 |
| **属性管线** | AttributePlus **已停放**（`plugins/_parked`）；战力靠 CoreRpg `StatService` + lore（见 `docs/ember-gear-stats.md`）。 |
| **工坊 ≠ 多部位锻炉** | 枢纽 NPC `ember_smith`（烬砧）→ TrMenu `ember_forge`（`hub_npcs.yml` / `design-ember-hub-workshop-npcs.md`）。现网是：**强化** `ember_enhance` / **镶嵌** `ember_socket` / **分解** `ember_disassemble` + **同槽升阶锻造**（`forge.yml`：T1↔T2↔T3 刃/护符配方）。**不是**头盔/胸甲等多部位锻炉产线。 |
| **枢纽入口** | `ember_hub` 第 4 行「装备成长」：强化/镶嵌/附魔/套装/分解；工坊牌旁烬砧右键进锻炉（少打指令已满足）。 |
| **技能** | 誓约 1 主动技（`DESIGN-ember-simple-skills.md`）；**无**多技能栏装配。本窗不改。 |

### 路径速查（施工锚 · 未改）

| 用途 | 路径 |
|------|------|
| 属性结算 | `CoreRpg/.../StatService.java`；live `plugins/CoreRpg/config.yml` → `stats:` |
| NI 刃/护符/团戒 | `plugins/NeigeItems/Items/ember-dungeon.yml` · `ember-gear-t{1,2,3}.yml` |
| 套装 | `plugins/CoreRpg/set.yml` · `plugins/TrMenu/menus/ember_set.yml` |
| 工坊 NPC | `plugins/CoreRpg/hub_npcs.yml`（`ember_smith` → `ember_forge`） |
| 菜单 | `plugins/TrMenu/menus/ember_{hub,forge,enhance,socket,disassemble,set}.yml` |
| 锻炉配方 | `plugins/CoreRpg/forge.yml` / src `forge.yml`（仅刃/护符升阶） |

---

## 3. 目标体验

- 玩家感知：**主手刃 + 护符（既有）+ 副手再挂 1 件薄饰品**，装搭多一格选择，而非「只刷同一把刃」。  
- 操作：物品放 **副手槽（Off Hand）** 即生效；说明走 TrMenu（hub/套装页），**不教手打命令**。  
- 数值：**微量**物防/生命（或极小特效），**不**触发刃护符 T0–T3 重平衡，**不做** DPS 盲调。

---

## 4. 方案

### 方案 A（荐）· +1 副手位 + 白名单 + 菜单 + 极简属性

| 块 | 做法 |
|----|------|
| **槽位语义** | 新增独立档 **副手/饰品**：只认 **Off Hand** 上、且 id ∈ 新白名单 `stats.offhand`（名称可落地为 `offhand` / `offhand_accessories`）的 NI 物。与护符「背包 best 一件」**并行叠加**，互不抢槽。 |
| **白名单** | 试点 id **不进**既有 `accessories`（避免被护符 best 逻辑吃掉或双计）。团戒继续只做套装，**本窗不改**其 Stat 待遇。 |
| **钩子** | `StatService.compute`：读 `getItemInOffHand()` → 若 id∈offhand 白名单 → `itemStats` 累加（复用现有 lore 解析：`生命力`/`物理防御`；本窗试点 **不**加物伤，避免 DPS 轴变脏）。 |
| **菜单** | `ember_hub` 装备行 **或** `ember_set` 增薄图标/一行 lore：`§7副手饰品 · 放副手槽生效`；可 `tell` 短句。不新开厚菜单壳亦可验收。 |
| **试点物（建议 2 件 · 择一或双上）** | 见下表。 |

#### 试点物建议

| NI id（建议） | 显示名 | material 建议 | 微量属性（lore） | 备注 |
|---------------|--------|---------------|------------------|------|
| `acc_ember_offhand_ward` | §a余烬守腕 | `IRON_NUGGET` 或 `SHIELD`（若用盾需确认不挡刃操作；**荐 nugget/paper 类非盾**） | `物理防御: +2` | 守向薄样；**远低于** T0 护符防 3，免冲曲线 |
| `acc_ember_offhand_vita` | §a余烬生坠 | `GOLD_NUGGET` / `EMERALD` 染色区分 | `生命力: +8` | 生向薄样；**远低于** T0 护符生命 20 |

掉落/获取（批后施工薄接，本稿不定死经济）：日箱低权 / 工坊灰粮兑换 stub / 或 OP 发测 — **禁止**本窗重做锻炉配方表。分解白名单可后续软挂，不挡试点验收。

**利：** 对准「稍微灵活」；槽位清晰（副手）；与护符正交；测口只 rg+菜单。  
**弊：** 需极小 Java 钩子（若仅 NI+文案而无 Stat 钩子则属性不生效——施工岗必须带钩子或明确「仅展示假窗」；**荐真生效**）。

### 方案 B（极薄旁附 · 勿捆厚）

在 A 已落地前提下 **择一**：

| B 选项 | 内容 | 禁扩 |
|--------|------|------|
| B1 | 再 +1 件样例（如 `acc_ember_offhand_spark` · 显示名「余烬余火饰」· lore `物理防御: +1`） | 不扩甲/技能/锻炉 |
| B2 | 极小特效文案-only：副手物加一行 `§8副手 · 守势`（**无**新被动代码）；或既有 passives 复用且 **零新数值轴** | 禁止新 DoT/爆伤乘区 |

**本轮默认：批 A 即可；B 仅总控点名时附带，勿与技能装配/锻炉产线捆。**

### 明确不施工

- 四件甲 / 盔甲槽养成  
- 技能大改、技能栏多装配（→ soft backlog）  
- 烬砧「材料→部件」新产线（→ soft backlog）  
- 改 T0–T3 刃护符数字、体力门、`forge.yml` 升阶成本  
- 把团戒强行并进副手白名单并重做套装（另窗）  
- B2.17 文案；宣称 B0.1 已清  

---

## 5. 硬约束表（可动 / 禁动）总览

| 域 | 可动 | 禁动 |
|----|------|------|
| 装备位 | +1 副手白名单位 | 甲四件、主手刃位语义 |
| NI | 新 `acc_ember_offhand_*` | 改既有 `gear_ember_*` 基数 |
| Stat | offhand 列表 + OffHand 读取 | 护符 best 算法大改、物伤乘区试点 |
| 菜单 | hub/set 薄展示 | 新指令教学、厚新菜单树 |
| 工坊 | 无（本窗） | 锻炉配方/强化曲线/镶嵌孔数 |
| 体力/日周 | 无 | 一切 cost/门 |
| 技能 | 无 | skills / 天赋主动数值 |
| 测试 | rg + 菜单目视 | wall-clock、多轮 DPS 盲调 |

---

## 6. 专岗链

| 序 | 岗 | 产出 | 备注 |
|----|----|------|------|
| 1 | **策划** | 本稿；批后 ID/显示名/微量属性冻结 | 本 commit |
| 2 | **物品（NI）** | 新物品 YAML（建议新文件 `ember-gear-offhand.yml` 或附入 t1 文件末） | 未批不写 |
| 3 | **插件** | `stats.offhand` + `StatService` OffHand 钩子；TrMenu 薄展示 | 未批不改玩法 YAML |
| 4 | **测试** | **仅**静态 `rg` + 菜单点开目视 | **禁**长测 / DPS 盲调 |

---

## 7. 验收清单（勾销条件）

**设计窗（本 commit）**

- [ ] 本稿落盘；玩法 YAML / NI / TrMenu **零 diff**（仅 docs + backlog）
- [ ] backlog 已挂 **B-flex-1**；注明与 **B2.17 并行不捆**；软挂项单列

**若批 A 施工后（另 commit）**

- [ ] `rg 'acc_ember_offhand_' plugins/NeigeItems/Items/` ≥ 试点件数；显示名中文短清楚
- [ ] `rg 'offhand' plugins/CoreRpg/config.yml`（或约定键名）含试点 id；`StatService` 含 OffHand 白名单分支（`rg` 源码）
- [ ] 试点 lore **无**对既有 T0–T3 刃护符文件的数值 diff（`git diff` 那些文件为空）
- [ ] TrMenu：`ember_hub` 或 `ember_set` 可见「副手」说明；点开无教手打 `/corerpg`
- [ ] **目视：** 副手放入试点物 → `/corerpg stats`（或属性菜单）见微量防/生命变化；取下恢复（**单次**目视即可，不写 DPS 表）
- [ ] **禁项声明：** 未改体力门、未改刃护符基数、未开四件甲、未重做锻炉、未做技能装配；**未宣称 B0.1 已清**

---

## 8. Backlog 软挂（本窗不捆）

| 软挂项 | 说明 | 本窗 |
|--------|------|------|
| **技能可装配** | 多技能栏/切换装配；现网誓约 1 主动技 | **不捆** |
| **烬砧材料→部件产线** | 材料加工为可装配部件；现网烬砧=升阶+强化/镶嵌入口 | **不捆** |
| 副手进分解白名单 / 日箱掉率 | 经济接线 | 批 A 后可另薄窗 |
| 团戒与副手位关系整理 | 是否允许戒占副手 | 另窗 |
| 四件甲 | `ember_set` 已写「日后再扩」 | **明确不做** |

---

## 9. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 副手说明（UI） | — | 余烬 · 套装/枢纽装备行 | 枢纽既有 | `ember_set` 或 `ember_hub` 薄行 | 无 | **UI 可用**；无新 NPC |
| 工坊（对照 · 不改） | `ember_smith` | 锻炉师 · 烬砧 | `ember_hub` (-11.5,58,105.5) | `ember_forge` | 页内→enhance/socket | 本窗 **零动** |

**站岗自检：** 无新 NPC；菜单路径不要求手打命令；不宣称场景新建筑完成。

---

## 10. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 枢纽 | hub 装备行 / 套装 | 读到「副手饰品 · 放副手槽」 | 返回 hub |
| 2 | 背包 | 获得试点饰品 | 拖到 **副手槽** | 放背包则不生效（护符逻辑不变） |
| 3 | 任意 | 属性面板 | 微量防/生命已计 | 取下副手即失 |

---

## 11. UX 否决条

| 硬条 | 预期 |
|------|------|
| 玩家路径要求手打 `/corerpg` 才知副手 | **FAIL** |
| 文案宣称「全身甲/四件套已开放」 | **FAIL** |
| 未批改玩法 YAML / 刃护符数值 | **FAIL** |
| 验收靠 wall-clock 或 DPS 表 | **FAIL** |
| 宣称 B0.1 已清 | **FAIL** |

---

## 12. 回总控摘要

- **选窗：** B-flex-1 副手/饰品位试点（内容灵活薄窗；**优先于**非文案内容窗；与 B2.17 **并行不捆**）。  
- **荐 A：** OffHand 白名单 + 1～2 件微量 NI + 菜单薄展示 + Stat 钩子。  
- **极薄 B：** 多 1 样例或文案-only 小特效；勿扩甲/技能/锻炉。  
- **锻炉澄清：** 现网=强化/镶嵌/分解+同槽升阶，**不是**多部位锻炉；本窗不重做。  
- **未捆：** B2.17 / 锻炉产线 / 技能大改 / 四件甲 / B0.1。  
- **验收：** 静态 rg + 菜单目视。
