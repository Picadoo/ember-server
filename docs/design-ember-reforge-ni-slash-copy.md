# 设计稿 · NI 重铸石 lore 斜杠去指令化（B2.23）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 STATUS-ember-reforge-ni-slash-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `ember-disassemble.yml` 中重铸石物品 **1** 行 lore 斜杠句；**禁**改 NI 数值 / 配方 / 给物逻辑 / scrap·reforge 消耗 / 词缀池 / 掉落 / 体力 / TrMenu / CoreRpg Java。  
> 债源：B2.22 tip / backlog 软观察「同物 lore「用于 /corerpg reforge」斜杠（可升 B2.23）」；B2.22 **PASS · 勾销** `48647e0`（设计 `115ebab` · 批准 `7724338` · 施工 `b24510c` · 测 `eff6037`）。  
> 对齐：B2.20 拆解菜单已人话；B2.21 缺料 chat 已人话；B2.22 已删 lore 裸 id；UX「玩家入口优先 TrMenu 点击 · 少依赖聊天打指令 · hint 不写请玩家执行 /dp start…」。  
> 排除本轮：刚结 B2.6–B2.22 / B-flex-1/2 / B-anvil-1；其它 NI 材 lore `&7mat_*` 整批（旁附 soft · 勿与 A 双上厚扫）；其它 NI `/corerpg` 斜杠（誓约/天赋/使魔 · soft）；精英预览厚壳（hub 一点进本、无 P / 无独立 rewards → 证据不足勿硬开）；墙钟/DPS；霜锈前压；断塔无证据升 B；B0.1 除非新证据；四件甲/锻炉重做/誓约大改。**勿宣称 B0.1 已清。** 与 B-flex / B-anvil **不捆**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI 重铸石 · **玩家可见 lore 斜杠 `/corerpg reforge` → 菜单口径**（UX · B2.23） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 02:20 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-disassemble.yml` · B2.22 旁附斜杠 soft |
| 状态 | **已批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.23** · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember-disassemble.yml` 内 `mat_ember_reforge_stone` lore **1** 行玩家可见字符串（见 §3 · 荐改菜单口径） | NI 键名 / `material` / `name` / 其它 lore / enchantments / hideflags |
| 热更 / NI reload（服约定） | 配方 / 给物 / scrap·reforge 消耗×1 / `stone_ni_id` / 词缀池 / 掉落 / 体力 |
| | TrMenu；CoreRpg Java；其它 NI 文件整批斜杠或 `&7mat_*`；git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML |

---

## 1. 问题一句话

**B2.22 已删重铸石 lore 裸 id 后，同物末行仍印 `&7用于 /corerpg reforge`——玩家背包悬停即见插件斜杠，与「入口优先 TrMenu 点击」冲突；属 B2.22 已点名、物品岗、零改数值/配方/给物的极薄文案债（1 行）。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 入口 | 背包持有 / 聊天展示 / 仓库等悬停 NI 物品 `mat_ember_reforge_stone` |
| **脏点计数** | **1** 处本窗目标（该物品 lore 末行斜杠）；同文件玩家可见仅此一条 |
| L5 | 键 `mat_ember_reforge_stone:`（**管理侧 · 不动**） |
| L6 | `material: FIREBALL`（不动） |
| L7 | `name: '&6余烬重铸石'`（**已中文 · 保留**） |
| L9 | `- '&8重铸石 · 刷新次要词缀'`（B2.22 删裸 id 后上行；**保留**） |
| L10 | `- '&8主词缀不随重铸乱飙'`（**保留**） |
| **L11** | `- '&7用于 /corerpg reforge'`（**本窗唯一目标**） |
| L3 | 文件头注释 `# 命令（插件岗待接）：/corerpg scrap · /corerpg reforge`（**非玩家可见 · 不动**） |
| B2.22 勾销 | close **`48647e0`** · 施工 `b24510c` 已删原 L9 `&7mat_ember_reforge_stone`；测 `eff6037` PASS |
| 对照 · 已人话入口 | TrMenu `ember_hub` →「打开分解 / 重铸」→ `menu: ember_disassemble`；拆解页「点左侧按钮即可…无需手打命令」（B2.20 PASS） |
| 候选对比 · 其它 NI lore `&7mat_*` | **9** 处同模式（`ember-dungeon`×3 / `ember-covenant-talent`×2 / `ember-pets`×1 / `ember-abyss-calamity`×1 / `ember-enhance-gems`×2）——物品岗批量 soft；**勿与 A 双上厚扫** |
| 候选对比 · 其它 NI `/corerpg` 斜杠 | 誓约/天赋 2 + 使魔 3 = **5** 处玩家可见（非本窗点名债 · soft） |
| 候选对比 · 精英预览 | hub 精英一点进本、**无 P / 无独立 rewards** → 厚壳；**勿硬开** |
| 扫网结论 | 本窗升自 B2.22 命名 soft、同物续债、单文件 1 行、菜单路径已通 → **改菜单口径最薄**；其它 `&7mat_*` / 斜杠同模式但非本窗点名、不宜抢升；**无**比本行更实、更高优的更薄可勾销债；默认推本窗 A |

### 替换口径（展示层 · 不改 NI 数值/配方/给物）

#### A · NI 重铸石 lore 1 行去斜杠（推荐 · 升自 B2.22 旁附）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ember-disassemble.yml` lore **L11** | `- '&7用于 /corerpg reforge'` | `- '&7用于枢纽 · 拆解 → 重铸'` |

**备选（同档 · 非荐）：** `- '&7用于分解 / 重铸菜单'`——也能去斜杠，但不如枢纽→拆解→重铸与 TrMenu 路径对齐清晰。

**口径备忘：** 只动上表 1 句玩家可见 lore；键名 / `name` / L9–L10 / enchantments / hideflags **一字不动**；配方·给物·消耗×1·`stone_ni_id`·词缀 **零改**；文件头注释 L3 **不动**（管理侧）；管理侧 scrap/life/auction/warehouse 键仍用 id。

#### B · 其它 NI 材 lore 裸 id 批（旁附 soft · 勿双上）

| # | 位置 | 旧 | 新（若另批） |
|---|------|----|--------------|
| B1 | 其它 NI Items `lore:` 内 `&7mat_*`（**9** 处） | 裸 id 灰字 | 逐项删行或改中文（物品岗 · 另开薄窗 · **下一件最薄**） |

**本轮不与 A 同上施工**；可作下一项 soft。其它 `/corerpg` 斜杠与精英预览壳仍 soft、证据不足勿硬开。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 改菜单口径 1 行（推荐）** | L11 → `&7用于枢纽 · 拆解 → 重铸`；零改数值/配方/给物 | B2.22 旁附主债；同物续清；对齐 TrMenu；rg 可勾销；悬停可点验 | 未顺手清其它斜杠 / `&7mat_*` | **推荐** |
| **A′. 泛菜单句（备选）** | L11 → `&7用于分解 / 重铸菜单` | 去斜杠 | 路径感弱于枢纽→拆解 | **不荐** |
| **B. 其它 NI 材 lore 批** | 上表 B1（下一件最薄） | 同模式债 | 厚扫；与 A 捆则双表面 | **旁附 soft · 勿双上** |

**不施工：** 开精英奖励预览子菜单壳；改其它 NI 文件整批斜杠或裸 id（除非另批 B）；改数值/配方/给物/`stone_ni_id`/消耗×1/词缀；宣称 B0.1 已清；未批改 YAML；重开 B2.6–B2.22 / B-flex / B-anvil；墙钟/DPS；叫挑刺。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / NI Items / TrMenu / CoreRpg / loot **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `ember-disassemble.yml` 内 `mat_ember_reforge_stone` 的 lore 列表 **无字面** `/corerpg`；L11 为菜单口径（荐「用于枢纽 · 拆解 → 重铸」）；L7/L9/L10 / material / enchantments **语义不变**；**键名**仍为 `mat_ember_reforge_stone:`。  
3. **若批 A：** scrap / reforge / life / 体力 / TrMenu / 其它 NI 文件 **相对批前零 diff**（除另批 B）。  
4. **若批 A · 禁项：** 未改数值/配方/给物逻辑；未开精英预览壳；**不**宣称 B0.1 / 其它 NI lore 斜杠或 `&7mat_*` 已清。  
5. **目视轻测：** 给 1 枚重铸石 → 悬停 lore 无 `/corerpg`；说明仍短清楚、指向枢纽拆解。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n "/corerpg" plugins/NeigeItems/Items/ember-disassemble.yml
# 期望：仅文件头注释 L3 命中；lore 列表无 /corerpg
rg -n "用于枢纽 · 拆解 → 重铸" plugins/NeigeItems/Items/ember-disassemble.yml
# 期望：lore L11 命中
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 重铸石物品悬停 | —（NI 物品） | 余烬重铸石 | 背包 / 聊天展示 | — | — | **UI/文案可用** · 无新 NPC · 物品岗 |

**站岗自检：** 无新 NPC；仅物品 lore 文案 → 最高标「UI 可用」，不宣称重铸数值/掉落改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 获得重铸石 | 物品名「余烬重铸石」 | NI 生成物品 | — |
| 2 | 悬停 | lore **无** `/corerpg`；见「用于枢纽 · 拆解 → 重铸」 | 仅展示 | — |
| 3 | 枢纽→拆解→重铸 | 既有菜单点击（B2.20 已人话） | 消耗仍 ×1（本窗不动） | — |

---

## 7. UX 否决条

| 硬条 | 预期 |
|------|------|
| 施工后悬停 lore 仍出现字面 `/corerpg` | **FAIL** |
| 借机改 `name`/配方/给物/`stone_ni_id`/消耗×1/词缀 / 开精英厚壳 | **FAIL** |
| 验收靠 wall-clock 或 DPS / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 / 其它 NI 斜杠或 `&7mat_*` 已清 | **FAIL** |
| 未批准即改 NI YAML | **FAIL** |
| 设计稿 hint 写「请玩家执行 /dp start …」同类 | **FAIL** |

---

## 8. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A（或不附 B） | **本窗**（文档 only） |
| **物品** | `ember-disassemble.yml` 改 lore L11（批 A 后） | **批 A 后** |
| **测试** | 静态 `rg` + 悬停 lore 轻测；**不叫挑刺**；禁 wall-clock/DPS | 施工后 |

**未批准前不施工**（含不改 NI YAML）。

---

## 9. 明确未动 / 不做

- 精英奖励预览子菜单壳（证据不足勿硬开）  
- 其它 NI 文件 lore 内 `&7mat_*` 整批（B soft，勿与 A 双上；下一件最薄另开）  
- 其它 NI `/corerpg` 斜杠（誓约/天赋/使魔 · soft）  
- `stone_ni_id` / 消耗 ×1 / 词缀池 / 掉落 / 体力 / TrMenu（B2.20 已结）/ CoreRpg 缺料 chat（B2.21 已结）/ 裸 id（B2.22 已结）  
- NI 键名 / `name` / L9–L10 / material / enchantments / 文件头注释 L3  
- B0.1 票扣显示名；四件甲；锻炉重做；誓约大改  
- B-flex-1/2 / B-anvil-1 已结物回改  
- 墙钟 / DPS / 挑刺口径  

---

## 10. Backlog 挂账

- 新号：**B2.23**（B2 文案可维护轨 · 与 B-flex / B-anvil **不捆**）  
- 软观察「同物 lore 斜杠」本窗主清（仅重铸石 1 行）；「其它 NI `&7mat_*`」留 B soft（下一件最薄）；「其它 `/corerpg` 斜杠 / 精英预览壳」仍 soft、勿硬开  
- B2.22 PASS 勾销保持（close **`48647e0`**）；**勿宣称 B0.1 已清**

---

## 11. 回总控摘要

- **荐 A：** `ember-disassemble.yml` 改重铸石 lore **L11** → `&7用于枢纽 · 拆解 → 重铸`；零改数值/配方/给物。  
- **路径：** `docs/design-ember-reforge-ni-slash-copy.md` · **B2.23** · STATUS **待批 A**。  
- **证据：** B2.22 close `48647e0` 后 L11 仍 `/corerpg reforge`；TrMenu 枢纽→拆解已通；升自 B2.22 旁附 soft。  
- **B 旁附 soft：** 其它 NI lore `&7mat_*`（9 处 · 下一件最薄）；**勿双上**。精英壳勿硬开。  
- **未动：** 精英厚壳 / 其它 NI 批 / 消耗·逻辑 / B0.1 / 四件甲·锻炉·誓约。  
- **验收专岗：** 物品岗改 1 行 lore → 测试 `rg`+悬停；禁 wall-clock/DPS；不叫挑刺。  
- **勿宣称 B0.1 已清。** 与 B-flex / B-anvil 不捆。
