# 设计稿 · NI 重铸石物品 lore 裸 id 人话对齐（B2.22）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 STATUS-ember-reforge-ni-lore-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `ember-disassemble.yml` 中重铸石物品 **1** 行 lore 裸 NI id；**禁**改 NI 数值 / 配方 / 给物逻辑 / scrap·reforge 消耗 / 词缀池 / 掉落 / 体力 / TrMenu / CoreRpg Java。  
> 债源：B2.21 tip §B soft（`docs/design-ember-reforge-need-copy.md`）；backlog 软观察「NI 物品 lore 同 id（可升 B2.22）」；B2.21 **PASS · 勾销** `79f5d7e`（施工 `16b5334` · 测 `5b255e9` · CoreRpg **1.15.27**）。  
> 对齐：B2.20 已清 TrMenu 拆解页；B2.21 已清缺料 chat；UX「文案短清楚 · 悬停物品即见 · NI 键/管理侧可留」。  
> 排除本轮：刚结 B2.6–B2.21 / B-flex-1/2 / B-anvil-1；精英预览厚壳（hub 一点进本、无 P / 无独立 rewards → 证据不足勿硬开）；其它 NI 材 lore `&7mat_*` 整批（旁附 soft · 勿与 A 双上厚扫）；墙钟/DPS；霜锈前压；断塔无证据升 B；B0.1 除非新证据；四件甲/锻炉重做/誓约大改。**勿宣称 B0.1 已清。** 与 B-flex / B-anvil **不捆**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI 重铸石 · **玩家可见 lore 裸 id `mat_ember_reforge_stone` 对齐**（UX · B2.22） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 02:14 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-disassemble.yml` · B2.21 旁附 B1 |
| 状态 | **已批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.22** · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember-disassemble.yml` 内 `mat_ember_reforge_stone` lore **1** 行玩家可见字符串（见 §3 · 荐删） | NI 键名 / `material` / `name` / 其它 lore / enchantments / hideflags |
| 热更 / NI reload（服约定） | 配方 / 给物 / scrap·reforge 消耗×1 / `stone_ni_id` / 词缀池 / 掉落 / 体力 |
| | TrMenu；CoreRpg Java；其它 NI 文件整批裸 id；git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML |

---

## 1. 问题一句话

**重铸石物品显示名已是「余烬重铸石」，lore 首行仍印 `&7mat_ember_reforge_stone`——玩家背包/聊天悬停即见管理噪音；属 B2.21 已点名、物品岗、零改数值/配方/给物的极薄文案债（1 行）。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 入口 | 背包持有 / 聊天展示 / 仓库等悬停 NI 物品 `mat_ember_reforge_stone` |
| **脏点计数** | **1** 处本窗目标（该物品 lore 首行裸 id）；同文件仅此一条 |
| L5 | 键 `mat_ember_reforge_stone:`（**管理侧 · 不动**） |
| L6 | `material: FIREBALL`（不动） |
| L7 | `name: '&6余烬重铸石'`（**已中文 · 保留**） |
| **L9** | `- '&7mat_ember_reforge_stone'`（**本窗唯一目标**） |
| L10–L12 | `&8重铸石 · 刷新次要词缀` / `&8主词缀不随重铸乱飙` / `&7用于 /corerpg reforge`（**已人话说明 · 保留**） |
| 对照 · 已人话 | TrMenu 拆解页（B2.20 PASS `5bbaab0`）；CoreRpg 缺料 chat「需要 余烬重铸石 ×1」（B2.21 PASS · close `79f5d7e` · jar 1.15.27 · `ScrapService` L385） |
| 管理侧键 | `scrap.yml` `stone_ni_id` / life give / auction / warehouse 仍用 id —— **不动** |
| 候选对比 · 其它 NI lore `&7mat_*` | 约 9 处同模式（`ember-dungeon`/`ember-enhance-gems`/`ember-pets`/…）——物品岗批量 soft；**勿与 A 双上厚扫** |
| 候选对比 · 精英预览 | hub 精英一点进本、**无 P / 无独立 rewards** → 厚壳；**勿硬开** |
| 扫网结论 | 本窗升自 B2.21 命名 soft、单文件 1 行、显示名已中文 → **删行最薄**；其它 `&7mat_*` 同模式但非本窗点名债、不宜抢升；**无**比本行更实、更高优的更薄可勾销债；默认推本窗 A |

### 替换口径（展示层 · 不改 NI 数值/配方/给物）

#### A · NI 重铸石 lore 1 行（推荐 · 升自 B2.21 旁附）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ember-disassemble.yml` lore **L9** | `- '&7mat_ember_reforge_stone'` | **删该行**（显示名 L7 已有「余烬重铸石」；L10 已有「重铸石 · …」· 更薄、无重复） |

**备选（同档 · 非荐）：** 改 `- '&7余烬重铸石'`——与 L7 显示名叠床，不如删行。

**口径备忘：** 只动上表 1 句玩家可见 lore；键名 / `name` / 其余 lore / enchantments / hideflags **一字不动**；配方·给物·消耗×1·`stone_ni_id`·词缀 **零改**；管理侧 scrap/life/auction/warehouse 键仍用 id。

#### B · 其它 NI 材 lore 裸 id 批（旁附 soft · 勿双上）

| # | 位置 | 旧 | 新（若另批） |
|---|------|----|--------------|
| B1 | 其它 NI Items `lore:` 内 `&7mat_*`（约 9 处） | 裸 id 灰字 | 逐项删行或改中文（物品岗 · 另开薄窗） |

**本轮不与 A 同上施工**；可作下一项 soft。精英预览壳仍 soft、证据不足勿硬开。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 删重铸石 lore 灰字 1 行（推荐）** | 删 L9；零改数值/配方/给物 | B2.21 旁附主债；单文件极薄；显示名已中文；rg 可勾销；悬停可点验 | 未顺手清其它 `&7mat_*` | **推荐** |
| **A′. 改中文（备选）** | L9 → `&7余烬重铸石` | 保留灰字位 | 与 L7 叠床 | **不荐** |
| **B. 其它 NI 材 lore 批** | 上表 B1 | 同模式债 | 厚扫；与 A 捆则双表面 | **旁附 soft · 勿双上** |

**不施工：** 开精英奖励预览子菜单壳；改其它 NI 文件整批裸 id（除非另批 B）；改数值/配方/给物/`stone_ni_id`/消耗×1/词缀；宣称 B0.1 已清；未批改 YAML；重开 B2.6–B2.21 / B-flex / B-anvil；墙钟/DPS；叫挑刺。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / NI Items / TrMenu / CoreRpg / loot **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `ember-disassemble.yml` 内 `mat_ember_reforge_stone` 的 **lore 列表无字面** `mat_ember_reforge_stone`；L7「余烬重铸石」保留；L10–L12 / material / enchantments **语义不变**；**键名**仍为 `mat_ember_reforge_stone:`。  
3. **若批 A：** scrap / reforge / life / 体力 / TrMenu / 其它 NI 文件 **相对批前零 diff**（除另批 B）。  
4. **若批 A · 禁项：** 未改数值/配方/给物逻辑；未开精英预览壳；**不**宣称 B0.1 / 其它 NI lore 裸 id 已清。  
5. **目视轻测：** 给 1 枚重铸石 → 悬停 lore 无裸 `mat_ember_reforge_stone`；说明仍短清楚。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n "mat_ember_reforge_stone" plugins/NeigeItems/Items/ember-disassemble.yml
# 期望：仅键名行（L5）命中；lore 列表无字面 mat_ember_reforge_stone
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
| 2 | 悬停 | lore **无**裸 id；保留「重铸石 · 刷新次要词缀」等 | 仅展示 | — |
| 3 | `/ember`→拆解→重铸 | 既有菜单/缺料 chat（B2.20/21 已人话） | 消耗仍 ×1（本窗不动） | — |

---

## 7. UX 否决条

| 硬条 | 预期 |
|------|------|
| 施工后悬停 lore 仍出现字面 `mat_ember_reforge_stone` | **FAIL** |
| 借机改 `name`/配方/给物/`stone_ni_id`/消耗×1/词缀 / 开精英厚壳 | **FAIL** |
| 验收靠 wall-clock 或 DPS / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 / 其它 NI `&7mat_*` 已清 | **FAIL** |
| 未批准即改 NI YAML | **FAIL** |

---

## 8. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A（或不附 B） | **本窗**（文档 only） |
| **物品** | `ember-disassemble.yml` 删 lore L9（批 A 后） | **批 A 后** |
| **测试** | 静态 `rg` + 悬停 lore 轻测；**不叫挑刺**；禁 wall-clock/DPS | 施工后 |

**未批准前不施工**（含不改 NI YAML）。

---

## 9. 明确未动 / 不做

- 精英奖励预览子菜单壳（证据不足勿硬开）  
- 其它 NI 文件 lore 内 `&7mat_*` 整批（B soft，勿与 A 双上）  
- `stone_ni_id` / 消耗 ×1 / 词缀池 / 掉落 / 体力 / TrMenu（B2.20 已结）/ CoreRpg 缺料 chat（B2.21 已结）  
- NI 键名 / `name` / L10–L12 / material / enchantments  
- B0.1 票扣显示名；四件甲；锻炉重做；誓约大改  
- B-flex-1/2 / B-anvil-1 已结物回改  
- 墙钟 / DPS / 挑刺口径  

---

## 10. Backlog 挂账

- 新号：**B2.22**（B2 文案可维护轨 · 与 B-flex / B-anvil **不捆**）  
- 软观察「NI 物品 lore 同 id」本窗主清（仅重铸石 1 行）；「其它 NI `&7mat_*`」留 B soft；「精英预览壳」仍 soft、勿硬开  
- B2.21 PASS 勾销保持（close `79f5d7e` · CoreRpg **1.15.27**）；**勿宣称 B0.1 已清**

---

## 11. 回总控摘要

- **荐 A：** `ember-disassemble.yml` 删重铸石 lore **L9**（显示名 L7 已有「余烬重铸石」）；零改数值/配方/给物。  
- **路径：** `docs/design-ember-reforge-ni-lore-copy.md` · **B2.22** · STATUS **待批 A**。  
- **证据：** L7 已中文 / L9 裸 id / L10 已有「重铸石 · …」；升自 B2.21 旁附 B1；B2.21 close `79f5d7e`。  
- **B 旁附 soft：** 其它 NI lore `&7mat_*`（约 9 处）；**勿双上**。精英壳勿硬开。  
- **未动：** 精英厚壳 / 其它 NI 批 / 消耗·逻辑 / B0.1 / 四件甲·锻炉·誓约。  
- **验收专岗：** 物品岗删 1 行 lore → 测试 `rg`+悬停；禁 wall-clock/DPS；不叫挑刺。  
- **勿宣称 B0.1 已清。** 与 B-flex / B-anvil 不捆。
