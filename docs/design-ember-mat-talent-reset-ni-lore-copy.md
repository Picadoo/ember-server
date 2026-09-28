# 设计稿 · NI 天赋重置券 lore 裸 id 人话对齐（B2.28）

> **STATUS：待批 A**（总控派下一薄窗 · 2026-09-29）。本稿只定 **玩家可见** NeigeItems `ember-covenant-talent.yml` 中 `mat_ember_talent_reset` **1** 行 lore 裸 NI id；**禁**改 NI 数值 / 配方 / 给物逻辑 / 掉落 / 体力 / TrMenu / CoreRpg Java。  
> 债源：B2.27 tip / backlog 软观察「其余 NI `&7mat_*`（可升 B2.28 · 下一件最薄荐天赋重置券）」；B2.27 **PASS · 勾销** close **`2aaffe3`**（设计 `8bb16c9` · 批准 `1d659d0` · 施工 `d0830a7` · 测 `1d71c6c`）。  
> 对齐：B2.22/B2.24–27 已对重铸石/碎片/骨尘/核心碎片/誓约重置券删 lore 裸 id；UX「显示名已中文则灰字裸 id 可删 · 悬停即见 · 键/管理侧可留」。  
> 排除本轮：刚结 B2.6–B2.27 / B-flex-1/2 / B-anvil-1；其它 NI 材 lore `&7mat_*` **整批/双上**（旁附 soft · 约 4 件）；同物 `/corerpg` 斜杠（L21 · **勿与本窗双上** · soft）；其它 NI `/corerpg` 斜杠（誓约/使魔 · soft）；精英预览厚壳（hub 一点进本、无 P / 无独立 rewards → 证据不足勿硬开）；墙钟/DPS；霜锈前压；断塔无证据升 B；B0.1 除非新证据；四件甲/锻炉重做/誓约大改。**勿宣称 B0.1 已清。** 与 B-flex / B-anvil **不捆**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI 天赋重置券 · **玩家可见 lore 裸 id `mat_ember_talent_reset` 对齐**（UX · B2.28） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 02:44 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-covenant-talent.yml` · B2.27 旁附「下一件 `&7mat_*`」荐天赋重置券 |
| 状态 | **待批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.28** · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember-covenant-talent.yml` 内 `mat_ember_talent_reset` lore **1** 行玩家可见字符串（见 §3 · 荐删） | NI 键名 / `material` / `name` / 其它 lore（含 L20 说明、**L21 `/corerpg` 斜杠**）/ 同文件 `mat_ember_covenant_reset` / enchantments / hideflags |
| 热更 / NI reload（服约定） | 配方 / 给物 / 掉落表 / 体力 / 强化·升阶消耗数 |
| | TrMenu；CoreRpg Java；其它 NI 文件整批 `&7mat_*`；git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML；与 `/corerpg` 斜杠双上 |

---

## 1. 问题一句话

**天赋重置券显示名已是「天赋重置券」，lore 首行仍印 `&7mat_ember_talent_reset`——玩家背包/聊天悬停即见管理噪音；属 B2.27 已点名「下一件最薄荐天赋重置券」、物品岗、零改数值/配方/给物的极薄文案债（1 行）。本窗不碰同物 L21 `/corerpg` 斜杠。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 入口 | 背包持有 / 聊天展示 / 仓库等悬停 NI 物品 `mat_ember_talent_reset` |
| **脏点计数** | **1** 处本窗目标（该物品 lore 首行裸 id）；同键仅此一条 |
| L15 | 键 `mat_ember_talent_reset:`（**管理侧 · 不动**） |
| L16 | `material: PAPER`（不动） |
| L17 | `name: '&b天赋重置券'`（**已中文 · 保留**） |
| **L19** | `- '&7mat_ember_talent_reset'`（**本窗唯一目标**） |
| L20 | `- '&8额外洗点 · 免晶钻'`（**已人话说明 · 保留**） |
| L21 | `- '&7用于 /corerpg talent reset（日免费用尽后）'`（**斜杠 soft · 本窗不动 · 勿双上**） |
| L22–L25 | enchantments / hideflags（不动） |
| 同文件旁附 | `mat_ember_covenant_reset`：B2.27 已删 lore 裸 id；L9 `/corerpg` 斜杠仍在（**本窗勿回改 · soft**） |
| B2.27 勾销 | close **`2aaffe3`** · 施工 `d0830a7` 已清誓约重置券 lore 裸 id；测 `1d71c6c` PASS |
| 对照 · dungeon/重铸/誓约已清 | `mat_ember_shard` / `mat_ember_bone_dust` / `mat_ember_core_fragment` / `mat_ember_reforge_stone` / `mat_ember_covenant_reset`：仅键名命中；lore **无**字面裸 id（B2.22 / B2.24–27 后） |
| 对照 · 已人话同模式 | B2.22 重铸石 / B2.24 碎片 / B2.25 骨尘 / B2.26 核心碎片 / B2.27 誓约重置券：显示名已中文 → **删** lore 裸 id 行 |
| 管理侧键 | 商城 / 活跃箱 / CoreRpg 消耗等仍用 id —— **不动** |
| 扫网 · lore 字面 `&7mat_`（live Items · 不含 bak） | **5** 处（见下表）；本窗只推 **talent_reset 1 件** |
| 候选对比 · 其它 `/corerpg` 斜杠 | 誓约/天赋 2 + 使魔 3 = **5** 处玩家可见（含本物 L21 · **非本窗目标 · soft · 勿双上**） |
| 候选对比 · 精英预览 | hub 精英一点进本、**无 P / 无独立 rewards** → 厚壳；**勿硬开** |
| 扫网结论 | 总控荐天赋重置券；显示名已中文；同物仍有说明行；与 B2.24–27 同构 → **删行最薄**；**无**比本行更实、更高优的更薄可勾销债（其它 4 件同模式但非本窗点名、不宜抢升/厚批；斜杠另轨勿双上）；默认推本窗 A |

### 只读扫描 · lore `&7mat_*` 全表（live · 2026-09-29）

| # | 文件 | 键 | name（已有中文） | lore 灰字行 | 本窗 |
|---|------|-----|------------------|-------------|------|
| **1** | `ember-covenant-talent.yml` | `mat_ember_talent_reset` | `&b天赋重置券`（L17） | **L19** `&7mat_ember_talent_reset` | **A · 荐删** |
| 2 | `ember-pets.yml` | `mat_ember_soul_dust` | `&b余烬魂尘` | L35 `&7mat_ember_soul_dust` | B soft |
| 3 | `ember-abyss-calamity.yml` | `mat_calamity_ember` | `&4灾厄余烬` | L8 `&7mat_calamity_ember` | B soft |
| 4 | `ember-enhance-gems.yml` | `mat_ember_protect_scroll` | `&d余烬保护券` | L9 `&7mat_ember_protect_scroll` | B soft |
| 5 | `ember-enhance-gems.yml` | `mat_ember_stable_charm` | `&b余烬稳固符` | L21 `&7mat_ember_stable_charm` | B soft |

> 注：`mat_ember_shard` / `mat_ember_bone_dust` / `mat_ember_core_fragment` / `mat_ember_reforge_stone` / `mat_ember_covenant_reset` 的 `&7mat_*` 已由 **B2.24 / B2.25 / B2.26 / B2.22 / B2.27** 删行；均不在上表。bak 非 live · 不计。

### 替换口径（展示层 · 不改 NI 数值/配方/给物）

#### A · NI 天赋重置券 lore 1 行（推荐 · 升自 B2.27 旁附）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ember-covenant-talent.yml` lore **L19** | `- '&7mat_ember_talent_reset'` | **删该行**（显示名 L17 已有「天赋重置券」；L20 已有「额外洗点 · 免晶钻」· 更薄、无重复） |

**备选（同档 · 非荐）：** 改 `- '&7天赋重置券'`——与 L17 显示名叠床，不如删行。

**口径备忘：** 只动上表 1 句玩家可见 lore；键名 / `name` / L20 / **L21 `/corerpg`** / material / enchantments / hideflags **一字不动**；同文件 `mat_ember_covenant_reset` **不动**；配方·给物·掉落·消耗 **零改**；管理侧键仍用 id。

#### B · 其它 NI 材 lore 裸 id（旁附 soft · 勿双上）

| # | 位置 | 旧 | 新（若另批） |
|---|------|----|--------------|
| B1～B4 | 上表 #2～#5（**4** 件） | 裸 id 灰字 | 逐项删行或改中文（物品岗 · **下一件 mat** 另开薄窗） |

**本轮不与 A 同上施工**；可作下一项 soft（荐下一件仍按「显示名已有 → 删行」最薄挑 1）。其它 `/corerpg` 斜杠（含本物 L21）与精英预览壳仍 soft、证据不足勿硬开；**勿与斜杠双上**。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 删天赋重置券 lore 灰字 1 行（推荐）** | 删 L19；零改数值/配方/给物；不动 L21 斜杠 | B2.27 旁附主债；单文件极薄；显示名已中文；rg 可勾销；悬停可点验 | 未顺手清其它 4 件；同物斜杠仍在 | **推荐** |
| **A′. 改中文（备选）** | L19 → `&7天赋重置券` | 保留灰字位 | 与 L17 叠床 | **不荐** |
| **B. 其它 4 件 mat lore 批** | 上表 B1～B4 | 同模式债 | 厚扫双上；禁 | **旁附 soft · 勿双上** |

**不施工：** 开精英奖励预览子菜单壳；改其它 NI 文件整批裸 id（除非另批 B）；改本物 L21 `/corerpg` 或其它斜杠（除非另开斜杠窗）；改数值/配方/给物/掉落/消耗；宣称 B0.1 已清；未批改 YAML；重开 B2.6–B2.27 / B-flex / B-anvil；墙钟/DPS；叫挑刺。

**更高优更薄债？** 扫网后：**无**。其余 4 件同构但曝光/点名均次于天赋重置券（总控明示荐）；其它 `/corerpg` 斜杠非本窗点名且属 soft、**勿双上**；精英壳证据不足勿硬开。故默认推 A。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / NI Items / TrMenu / CoreRpg / loot **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `ember-covenant-talent.yml` 内 `mat_ember_talent_reset` 的 **lore 列表无字面** `mat_ember_talent_reset`；L17「天赋重置券」保留；L20 / **L21 `/corerpg`** / material / enchantments / hideflags **语义不变**；**键名**仍为 `mat_ember_talent_reset:`。  
3. **若批 A：** 配方 / 给物 / 掉落 / 体力 / TrMenu / 其它 NI 文件（含同文件 `mat_ember_covenant_reset`） **相对批前零 diff**（除另批 B）。  
4. **若批 A · 禁项：** 未改数值/配方/给物逻辑；未改 L21 斜杠；未开精英预览壳；**不**宣称 B0.1 / 其它 NI lore 裸 id / `/corerpg` 斜杠已清。  
5. **目视轻测：** 给 1 枚天赋重置券 → 悬停 lore 无裸 `mat_ember_talent_reset`；说明仍短清楚。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n "mat_ember_talent_reset" plugins/NeigeItems/Items/ember-covenant-talent.yml
# 期望：仅键名行（L15）命中；lore 列表无字面 mat_ember_talent_reset
# 另：L21 /corerpg 斜杠行仍在（本窗不动；删行后行号可能上移 1，语义须在）
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 天赋重置券物品悬停 | —（NI 物品） | 天赋重置券 | 背包 / 聊天展示 | — | — | **UI/文案可用** · 无新 NPC · **物品岗** |

**站岗自检：** 无新 NPC；仅物品 lore 文案 → 最高标「UI 可用」，不宣称掉落/配方/数值改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | 获得天赋重置券 | 物品名「天赋重置券」 | NI 生成物品 | — |
| 2 | 悬停 | lore **无**裸 id；保留「额外洗点 · 免晶钻」；L21 斜杠本窗仍在 | 仅展示 | — |
| 3 | 洗点 / 商城等 | 既有菜单与消耗（本窗不动） | 管理侧仍用 id | — |

---

## 7. UX 否决条

| 硬条 | 预期 |
|------|------|
| 施工后悬停 lore 仍出现字面 `mat_ember_talent_reset` | **FAIL** |
| 借机改 `name`/L21 斜杠/配方/给物/掉落/消耗 / 开精英厚壳 / 顺手改另 4 件或 covenant_reset | **FAIL** |
| 验收靠 wall-clock 或 DPS / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 / 其它 NI `&7mat_*` 已清 / `/corerpg` 斜杠已清 | **FAIL** |
| 未批准即改 NI YAML | **FAIL** |

---

## 8. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A（或不附 B） | **本窗**（文档 only） |
| **物品** | `ember-covenant-talent.yml` 删 lore L19（批 A 后） | **批 A 后** |
| **测试** | 静态 `rg` + 悬停 lore 轻测；**不叫挑刺**；禁 wall-clock/DPS | 施工后 |

**未批准前不施工**（含不改 NI YAML）。

---

## 9. 明确未动 / 不做

- 精英奖励预览子菜单壳（证据不足勿硬开）  
- 其它 4 件 NI lore 内 `&7mat_*`（B soft，勿与 A 双上厚批）  
- 本物 L21 及其它 NI `/corerpg` 斜杠（soft · **勿双上**）  
- 同文件 `mat_ember_covenant_reset`  
- 配方 / 给物 / 掉落 / 体力 / TrMenu / CoreRpg  
- NI 键名 / `name` / L20 / material / enchantments / hideflags  
- B0.1 票扣显示名；四件甲；锻炉重做；誓约大改  
- B-flex-1/2 / B-anvil-1 已结物回改  
- 墙钟 / DPS / 挑刺口径  

---

## 10. Backlog 挂账

- 新号：**B2.28**（B2 文案可维护轨 · 与 B-flex / B-anvil **不捆**）  
- 软观察「其它 NI `&7mat_*`」本窗主清 **仅 talent_reset 1 行**；其余 4 件留 B soft；「其它 `/corerpg` 斜杠」「精英预览壳」仍 soft、勿硬开；**勿与斜杠双上**  
- B2.27 PASS 勾销保持（close `2aaffe3`）；**勿宣称 B0.1 已清**

---

## 11. 回总控摘要

- **荐 A：** `ember-covenant-talent.yml` 删 `mat_ember_talent_reset` lore **L19**（显示名 L17 已有「天赋重置券」）；零改数值/配方/给物；**不动** L21 `/corerpg`。  
- **路径：** `docs/design-ember-mat-talent-reset-ni-lore-copy.md` · **B2.28** · STATUS **待批 A**。  
- **证据：** L17 已中文 / L19 裸 id / L20 已有说明；升自 B2.27 旁附；B2.27 close `2aaffe3`；已清 5 件 lore 裸 id；live `&7mat_*` 共 5 · 本窗只推 1。  
- **B 旁附 soft：** 其余 4 件 mat lore；**勿双上**。其它 `/corerpg` 斜杠 soft；精英壳勿硬开。  
- **未动：** 精英厚壳 / 其它 NI 批 / 斜杠双上 / 配方·给物 / B0.1 / 四件甲·锻炉·誓约。  
- **验收专岗：** 物品岗删 1 行 lore → 测试 `rg`+悬停；禁 wall-clock/DPS；不叫挑刺。  
- **勿宣称 B0.1 已清。** 与 B-flex / B-anvil 不捆。
