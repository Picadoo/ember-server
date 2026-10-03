# 设计稿 · NI 余烬核心碎片 lore 裸 id 人话对齐（B2.26）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 docs/status/STATUS-ember-mat-core-fragment-ni-lore-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `ember-dungeon.yml` 中 `mat_ember_core_fragment` **1** 行 lore 裸 NI id；**禁**改 NI 数值 / 配方 / 给物逻辑 / 掉落 / 体力 / TrMenu / CoreRpg Java。  
> 债源：B2.25 tip / backlog 软观察「其余 NI `&7mat_*`（可升 B2.26 · 下一件最薄荐核心碎片）」；B2.25 **PASS · 勾销** close **`35abc98`**（设计 `355603e` · 批准 `1b52ef9` · 施工 `e6df747` · 测 `220cb2a`）。  
> 对齐：B2.22/B2.24/B2.25 已对重铸石/碎片/骨尘删 lore 裸 id；UX「显示名已中文则灰字裸 id 可删 · 悬停即见 · 键/管理侧可留」。  
> 排除本轮：刚结 B2.6–B2.25 / B-flex-1/2 / B-anvil-1；其它 NI 材 lore `&7mat_*` **整批/双上**（旁附 soft · 约 6 件）；其它 NI `/corerpg` 斜杠（誓约/天赋/使魔 · soft）；精英预览厚壳（hub 一点进本、无 P / 无独立 rewards → 证据不足勿硬开）；墙钟/DPS；霜锈前压；断塔无证据升 B；B0.1 除非新证据；四件甲/锻炉重做/誓约大改。**勿宣称 B0.1 已清。** 与 B-flex / B-anvil **不捆**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI 余烬核心碎片 · **玩家可见 lore 裸 id `mat_ember_core_fragment` 对齐**（UX · B2.26） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 02:34 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-dungeon.yml` · B2.25 旁附「下一件 `&7mat_*`」荐核心碎片 |
| 状态 | **已批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.26** · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember-dungeon.yml` 内 `mat_ember_core_fragment` lore **1** 行玩家可见字符串（见 §3 · 荐删） | NI 键名 / `material` / `name` / 其它 lore / 其它物品 / enchantments / hideflags |
| 热更 / NI reload（服约定） | 配方 / 给物 / 掉落表 / 体力 / 强化·升阶消耗数 |
| | TrMenu；CoreRpg Java；其它 NI 文件整批 `&7mat_*`；git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML |

---

## 1. 问题一句话

**余烬核心碎片显示名已是「余烬核心碎片」，lore 首行仍印 `&7mat_ember_core_fragment`——玩家背包/聊天悬停即见管理噪音；属 B2.25 已点名「下一件最薄荐核心碎片」、物品岗、零改数值/配方/给物的极薄文案债（1 行）。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 入口 | 背包持有 / 聊天展示 / 仓库等悬停 NI 物品 `mat_ember_core_fragment` |
| **脏点计数** | **1** 处本窗目标（该物品 lore 首行裸 id）；同键仅此一条 |
| L17 | 键 `mat_ember_core_fragment:`（**管理侧 · 不动**） |
| L18 | `material: MAGMA_CREAM`（不动） |
| L19 | `name: '&6余烬核心碎片'`（**已中文 · 保留**） |
| **L21** | `- '&7mat_ember_core_fragment'`（**本窗唯一目标**） |
| L22 | `- '&8余烬地窟掉落 · 稀有材料'`（**已人话说明 · 保留**） |
| L23–L26 | enchantments / hideflags（不动） |
| B2.25 勾销 | close **`35abc98`** · 施工 `e6df747` 已清骨尘 lore 裸 id；测 `220cb2a` PASS |
| 对照 · shard 已清 | `mat_ember_shard`：仅键名 L5 命中；lore **无**字面 `mat_ember_shard`（B2.24 后） |
| 对照 · bone_dust 已清 | `mat_ember_bone_dust`：仅键名 L11 命中；lore **无**字面 `mat_ember_bone_dust`（B2.25 后） |
| 对照 · 已人话同模式 | B2.22 重铸石 / B2.24 碎片 / B2.25 骨尘：显示名已中文 → **删** lore 裸 id 行 |
| 管理侧键 | forge / AFK cap / MM 掉落 / warehouse / anvil 等仍用 id —— **不动** |
| 扫网 · lore 字面 `&7mat_`（live Items · 不含 bak） | **7** 处（见下表）；本窗只推 **core_fragment 1 件** |
| 候选对比 · 其它 `/corerpg` 斜杠 | 誓约/天赋 2 + 使魔 3 = **5** 处玩家可见（非本窗点名 · soft） |
| 候选对比 · 精英预览 | hub 精英一点进本、**无 P / 无独立 rewards** → 厚壳；**勿硬开** |
| 扫网结论 | 总控荐核心碎片；显示名已中文；同文件下仍有说明行；与 B2.24/25 同构 → **删行最薄**；**无**比本行更实、更高优的更薄可勾销债（其它 6 件同模式但非本窗点名、不宜抢升/厚批）；默认推本窗 A |

### 只读扫描 · lore `&7mat_*` 全表（live · 2026-09-29）

| # | 文件 | 键 | name（已有中文） | lore 灰字行 | 本窗 |
|---|------|-----|------------------|-------------|------|
| **1** | `ember-dungeon.yml` | `mat_ember_core_fragment` | `&6余烬核心碎片`（L19） | **L21** `&7mat_ember_core_fragment` | **A · 荐删** |
| 2 | `ember-covenant-talent.yml` | `mat_ember_covenant_reset` | `&6誓约重置券` | L8 `&7mat_ember_covenant_reset` | B soft |
| 3 | `ember-covenant-talent.yml` | `mat_ember_talent_reset` | `&b天赋重置券` | L20 `&7mat_ember_talent_reset` | B soft |
| 4 | `ember-pets.yml` | `mat_ember_soul_dust` | `&b余烬魂尘` | L35 `&7mat_ember_soul_dust` | B soft |
| 5 | `ember-abyss-calamity.yml` | `mat_calamity_ember` | `&4灾厄余烬` | L8 `&7mat_calamity_ember` | B soft |
| 6 | `ember-enhance-gems.yml` | `mat_ember_protect_scroll` | `&d余烬保护券` | L9 `&7mat_ember_protect_scroll` | B soft |
| 7 | `ember-enhance-gems.yml` | `mat_ember_stable_charm` | `&b余烬稳固符` | L21 `&7mat_ember_stable_charm` | B soft |

> 注：`mat_ember_shard` / `mat_ember_bone_dust` / `mat_ember_reforge_stone` 的 `&7mat_*` 已由 **B2.24 / B2.25 / B2.22** 删行；均不在上表。bak 非 live · 不计。

### 替换口径（展示层 · 不改 NI 数值/配方/给物）

#### A · NI 余烬核心碎片 lore 1 行（推荐 · 升自 B2.25 旁附）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ember-dungeon.yml` lore **L21** | `- '&7mat_ember_core_fragment'` | **删该行**（显示名 L19 已有「余烬核心碎片」；L22 已有「余烬地窟掉落 · 稀有材料」· 更薄、无重复） |

**备选（同档 · 非荐）：** 改 `- '&7余烬核心碎片'`——与 L19 显示名叠床，不如删行。

**口径备忘：** 只动上表 1 句玩家可见 lore；键名 / `name` / L22 / material / enchantments / hideflags **一字不动**；配方·给物·掉落·消耗 **零改**；管理侧 forge/AFK/MM/warehouse 键仍用 id。

#### B · 其它 NI 材 lore 裸 id（旁附 soft · 勿双上）

| # | 位置 | 旧 | 新（若另批） |
|---|------|----|--------------|
| B1～B6 | 上表 #2～#7（**6** 件） | 裸 id 灰字 | 逐项删行或改中文（物品岗 · **下一件 mat** 另开薄窗） |

**本轮不与 A 同上施工**；可作下一项 soft（荐下一件仍按「显示名已有 → 删行」最薄挑 1）。其它 `/corerpg` 斜杠与精英预览壳仍 soft、证据不足勿硬开。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 删核心碎片 lore 灰字 1 行（推荐）** | 删 L21；零改数值/配方/给物 | B2.25 旁附主债；单文件极薄；显示名已中文；rg 可勾销；悬停可点验 | 未顺手清其它 6 件 | **推荐** |
| **A′. 改中文（备选）** | L21 → `&7余烬核心碎片` | 保留灰字位 | 与 L19 叠床 | **不荐** |
| **B. 其它 6 件 mat lore 批** | 上表 B1～B6 | 同模式债 | 厚扫双上；禁 | **旁附 soft · 勿双上** |

**不施工：** 开精英奖励预览子菜单壳；改其它 NI 文件整批裸 id（除非另批 B）；改数值/配方/给物/掉落/消耗；宣称 B0.1 已清；未批改 YAML；重开 B2.6–B2.25 / B-flex / B-anvil；墙钟/DPS；叫挑刺。

**更高优更薄债？** 扫网后：**无**。其余 6 件同构但曝光/点名均次于核心碎片（总控明示荐）；其它 `/corerpg` 斜杠非本窗点名且属 soft；精英壳证据不足勿硬开。故默认推 A。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / NI Items / TrMenu / CoreRpg / loot **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `ember-dungeon.yml` 内 `mat_ember_core_fragment` 的 **lore 列表无字面** `mat_ember_core_fragment`；L19「余烬核心碎片」保留；L22 / material / enchantments / hideflags **语义不变**；**键名**仍为 `mat_ember_core_fragment:`。  
3. **若批 A：** 配方 / 给物 / 掉落 / 体力 / TrMenu / 其它 NI 文件 **相对批前零 diff**（除另批 B）。  
4. **若批 A · 禁项：** 未改数值/配方/给物逻辑；未开精英预览壳；**不**宣称 B0.1 / 其它 NI lore 裸 id 已清。  
5. **目视轻测：** 给 1 枚核心碎片 → 悬停 lore 无裸 `mat_ember_core_fragment`；说明仍短清楚。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n "mat_ember_core_fragment" plugins/NeigeItems/Items/ember-dungeon.yml
# 期望：仅键名行（L17）命中；lore 列表无字面 mat_ember_core_fragment
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 余烬核心碎片物品悬停 | —（NI 物品） | 余烬核心碎片 | 背包 / 聊天展示 | — | — | **UI/文案可用** · 无新 NPC · **物品岗** |

**站岗自检：** 无新 NPC；仅物品 lore 文案 → 最高标「UI 可用」，不宣称掉落/配方/数值改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 获得核心碎片 | 物品名「余烬核心碎片」 | NI 生成物品 | — |
| 2 | 悬停 | lore **无**裸 id；保留「余烬地窟掉落 · 稀有材料」 | 仅展示 | — |
| 3 | 强化/锻炉/仓库等 | 既有菜单与消耗（本窗不动） | 管理侧仍用 id | — |

---

## 7. UX 否决条

| 硬条 | 预期 |
|------|------|
| 施工后悬停 lore 仍出现字面 `mat_ember_core_fragment` | **FAIL** |
| 借机改 `name`/配方/给物/掉落/消耗 / 开精英厚壳 / 顺手改另 6 件 | **FAIL** |
| 验收靠 wall-clock 或 DPS / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 / 其它 NI `&7mat_*` 已清 | **FAIL** |
| 未批准即改 NI YAML | **FAIL** |

---

## 8. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A（或不附 B） | **本窗**（文档 only） |
| **物品** | `ember-dungeon.yml` 删 lore L21（批 A 后） | **批 A 后** |
| **测试** | 静态 `rg` + 悬停 lore 轻测；**不叫挑刺**；禁 wall-clock/DPS | 施工后 |

**未批准前不施工**（含不改 NI YAML）。

---

## 9. 明确未动 / 不做

- 精英奖励预览子菜单壳（证据不足勿硬开）  
- 其它 6 件 NI lore 内 `&7mat_*`（B soft，勿与 A 双上厚批）  
- 其它 NI `/corerpg` 斜杠（誓约/天赋/使魔 · soft）  
- 配方 / 给物 / 掉落 / 体力 / TrMenu / CoreRpg  
- NI 键名 / `name` / L22 / material / enchantments / hideflags  
- B0.1 票扣显示名；四件甲；锻炉重做；誓约大改  
- B-flex-1/2 / B-anvil-1 已结物回改  
- 墙钟 / DPS / 挑刺口径  

---

## 10. Backlog 挂账

- 新号：**B2.26**（B2 文案可维护轨 · 与 B-flex / B-anvil **不捆**）  
- 软观察「其它 NI `&7mat_*`」本窗主清 **仅 core_fragment 1 行**；其余 6 件留 B soft；「其它 `/corerpg` 斜杠」「精英预览壳」仍 soft、勿硬开  
- B2.25 PASS 勾销保持（close `35abc98`）；**勿宣称 B0.1 已清**

---

## 11. 回总控摘要

- **荐 A：** `ember-dungeon.yml` 删 `mat_ember_core_fragment` lore **L21**（显示名 L19 已有「余烬核心碎片」）；零改数值/配方/给物。  
- **路径：** `docs/design/design-ember-mat-core-fragment-ni-lore-copy.md` · **B2.26** · STATUS **待批 A**。  
- **证据：** L19 已中文 / L21 裸 id / L22 已有说明；升自 B2.25 旁附；B2.25 close `35abc98`；shard/bone_dust 已无 lore 裸 id；live `&7mat_*` 共 7 · 本窗只推 1。  
- **B 旁附 soft：** 其余 6 件 mat lore；**勿双上**。其它 `/corerpg` 斜杠 soft；精英壳勿硬开。  
- **未动：** 精英厚壳 / 其它 NI 批 / 配方·给物 / B0.1 / 四件甲·锻炉·誓约。  
- **验收专岗：** 物品岗删 1 行 lore → 测试 `rg`+悬停；禁 wall-clock/DPS；不叫挑刺。  
- **勿宣称 B0.1 已清。** 与 B-flex / B-anvil 不捆。
