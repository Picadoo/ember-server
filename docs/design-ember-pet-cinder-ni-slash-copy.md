# 设计稿 · NI 余烬烬火 lore summon 去斜杠（B2.43）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 STATUS-ember-pet-cinder-ni-slash-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-pets.yml` 内 `pet_ember_cinder` **1** 行 lore summon 斜杠句的人话替换；**禁**改 NI 数值 / 配方 / 给物逻辑 / 掉落 / 体力 / TrMenu / CoreRpg Java。
> 债源：B2.42 **PASS · 勾销** close `ef6ff87`（测 `d855ce3` · 施工 `1dbcb88`；设计 tip `696c80c` · 批准 `48dc3d1`）。B2.43 同轨 cinder summon 单件薄窗。
> 对齐：B2.42 灰灵「用于枢纽 · 使魔」；同构 B2.40 誓约 / B2.41 天赋枢纽短句。**未批准前不改 NI Items YAML。**
> 排除本轮：`pet_ember_ashling`（B2.42 已清 · 禁回改）；`mat_ember_soul_dust` feed；talent/covenant 已清项；其它 NI `/corerpg` 斜杠；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*` 批扫；精英预览壳；数值 / 配方 / 给物 / 掉落 / 体力；B0.1；git push。**勿宣称其它斜杠或 B0.1 已清。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI `pet_ember_cinder` · **玩家可见 lore `/corerpg pet summon` → 枢纽人话**（UX · B2.43） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 04:03 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-pets.yml` · B2.42 close 后升窗 |
| 状态 | **已批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.43** · 批后另开施工 / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `pet_ember_cinder` lore **L22** 这 1 行玩家可见字符串（见 §3 · 荐改） | `pet_ember_cinder` 键名 / `material` / `name` / L20「使魔蛋 · 外观 / 余火」/ L21「出战 1 只 · 不卖满级战力」/ 其它 lore / enchantments / hideflags |
| 批 A 后由物品岗施工、再按专岗轻测 | 同文件 `pet_ember_ashling`（B2.42 已清 · L10 已是枢纽人话）；`mat_ember_soul_dust` feed（L34）；talent/covenant 已清项；其它 `/corerpg` 斜杠；TrMenu；CoreRpg Java；玩法/NI Items YAML 未批不得改；git push |
| | 数值 / 配方 / 给物 / 掉落 / 体力 / 精英壳；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*`；B0.1；与其它斜杠双上 |

---

## 1. 问题一句话

**`pet_ember_cinder` 显示名已经是「余烬烬火」，L22 仍把管理侧 `/corerpg pet summon` 露给玩家；背包悬停应指向现网枢纽点击路径。属 B2.42 后升起的物品岗、单件、零改数值/配方/给物的极薄文案债（1 行）。**

---

## 2. 现网事实（只读调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 目标文件 / 键 | `plugins/NeigeItems/Items/ember-pets.yml` · `pet_ember_cinder:` |
| **实际目标行** | **L22** `- '&7用于 /corerpg pet summon'`（本窗唯一施工候选） |
| L18 | `name: '&6余烬烬火'`（保留） |
| L20 | `- '&8使魔蛋 · 外观 / 余火'`（保留） |
| L21 | `- '&8出战 1 只 · 不卖满级战力'`（保留） |
| 同文件已清 / 禁动 | `pet_ember_ashling` L10 `- '&7用于枢纽 · 使魔'`（B2.42 已清 · 禁回改）；`mat_ember_soul_dust` L34 `- '&a喂使魔：/corerpg pet feed'`（禁动） |
| 目标现状 | 该件 lore 当前仅 L22 命中字面 `/corerpg`；本设计只推该件 1 行，不宣称其它斜杠已清 |

### TrMenu 路径证据（现网文件 · 对齐 B2.42）

1. `plugins/TrMenu/menus/README-ember.md`：`ember_hub.yml` 入口为 `/ember` / `/menu`（B2.42 已引）。
2. `plugins/TrMenu/menus/ember_hub.yml` **L102–112**：枢纽按钮「使魔」点击进入 `menu: ember_pet`；lore 明示「收集出战使魔，偏外观与微量助战」「打开使魔菜单」。
3. `plugins/TrMenu/menus/ember_pet.yml`：Title「余烬 · 使魔」；「出战 / 收回」玩家可见 lore 写「同时出战 1 只」「点击出战」（底层 command 非玩家可见斜杠教学）。

**路径结论：** 证据充分，与 B2.42 同轨——现网枢纽有独立「使魔」直达按钮 → `ember_pet`。本稿 **荐最短且同构 B2.40/B2.41/B2.42 的** `&7用于枢纽 · 使魔`。

### 旁附 `/corerpg` 扫描（本窗只推 cinder summon）

```text
plugins/NeigeItems/Items/ember-pets.yml:22:    - '&7用于 /corerpg pet summon'
plugins/NeigeItems/Items/ember-pets.yml:34:    - '&a喂使魔：/corerpg pet feed'
plugins/NeigeItems/Items/ember-disassemble.yml:3:# 命令（插件岗待接）：/corerpg scrap · /corerpg reforge
```

当前 `rg -n "/corerpg" plugins/NeigeItems/Items/` 共 **3** 处：玩家可见 lore **2** 处（cinder summon + soul_dust feed），另有 `ember-disassemble.yml` 文件头管理注释 **1** 处。ashling summon 已无 `/corerpg`（B2.42）；`mat_ember_covenant_reset` / `mat_ember_talent_reset` 已无 `/corerpg`（B2.40 / B2.41）。旁附均只列证，不在本窗推施工；不宣称其它斜杠/B0.1 已清。

---

## 3. 替换口径（展示层 · 不改 NI 数值/配方/给物）

### A · 单件 `pet_ember_cinder` 去斜杠（荐）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ember-pets.yml` lore **L22** | `- '&7用于 /corerpg pet summon'` | `- '&7用于枢纽 · 使魔'` |

**备选（同档 · 非荐）：** `- '&7用于枢纽 · 使魔 → 出战'`。使魔页确有「出战 / 收回」按钮，但 B2.40/B2.41/B2.42 主荐止于枢纽直达页名，不叠功能按钮名；本窗同构止于「使魔」。

**L20 / L21 保留：** `- '&8使魔蛋 · 外观 / 余火'`、`- '&8出战 1 只 · 不卖满级战力'` 一字不动；`name: '&6余烬烬火'` 不动。

**口径备忘：** 批 A 后只替换上表 1 句玩家可见 lore；键名 / `name` / L20 / L21 / enchantments / hideflags 不动；`pet_ember_ashling`、`mat_ember_soul_dust` feed、talent/covenant 已清项、其它斜杠、TrMenu、CoreRpg、数值/配方/给物/掉落/体力均不动。

---

## 4. 方案与决策

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 枢纽直达短句（推荐）** | L22 → `&7用于枢纽 · 使魔` | 与 `ember_hub`→`ember_pet` 一跳直连；同构 B2.40/B2.41/B2.42；短、准确、去斜杠 | 不复述「出战」按钮名 | **推荐** |
| **A′. 叠出战按钮名** | L22 → `&7用于枢纽 · 使魔 → 出战` | 更贴近出战动作 | 比同构长一层；玩家未必需要 | 备选 |
| B. 其它 `/corerpg` 斜杠批 | 顺手改 soul_dust feed / disassemble 等 | 扩大覆盖 | 违反单件薄窗、硬禁、与其它斜杠双上 | **不做** |

**不施工：** 未批改 `plugins/NeigeItems/Items/*.yml`；不动 L20/L21；不动 `pet_ember_ashling` / `mat_ember_soul_dust`；不改其它斜杠；不改数值/配方/给物；不开精英壳；不宣称 B0.1 已清；不 git push；不叫挑刺。

---

## 5. 验收（待批 A 后另开施工）

1. 本设计 commit 仅包含本稿与 backlog；玩法 YAML / NI Items / TrMenu / CoreRpg / loot **零 diff**。
2. 若批 A，物品岗只改 `pet_ember_cinder` L22 为 `- '&7用于枢纽 · 使魔'`；L20/L21、键名、name、其它字段语义不变。
3. 该件 lore 施工后无字面 `/corerpg`；`pet_ember_ashling` L10、`mat_ember_soul_dust` L34 及其它文件相对批前零 diff。
4. 静态 `rg` + 目视悬停：1 枚「余烬烬火」显示「使魔蛋 · 外观 / 余火」「出战 1 只 · 不卖满级战力」「用于枢纽 · 使魔」，不出现 `/corerpg`。
5. **范围声明：** 仅该件 `/corerpg` 目标可在批后验收；**不宣称其它斜杠或 B0.1 已清**。

预期施工后静态检查（本设计窗不执行改动）：

```bash
rg -n -C 3 "pet_ember_cinder" plugins/NeigeItems/Items/ember-pets.yml
rg -n "/corerpg" plugins/NeigeItems/Items/ember-pets.yml
# 预期：同文件仍命中 soul_dust L34 feed；cinder lore 无 /corerpg；ashling L10 仍为枢纽人话
rg -n "/corerpg" plugins/NeigeItems/Items/
# 预期：剩 feed + disassemble 文件头注释；本件不再命中
```

---

## 6. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **物品** | 批 A 后仅替换 `ember-pets.yml` 目标 L22 | 批 A 后 |
| **测试** | `rg` + 悬停 lore；不测玩法数值，不叫挑刺 | 施工后 |

玩家动线：`/ember` 或 `/menu` → 枢纽「使魔」→ 使魔页「出战 / 收回」；物品 lore 只作入口提示，不改变消耗、菜单或指令实现。

**未批准前不施工**（含不改 NI YAML）。

---

## 7. 明确未动 / 不做

- `pet_ember_ashling`（B2.42 已清 · 禁回改）
- `mat_ember_soul_dust` feed（L34）
- talent/covenant 已清项（B2.40 / B2.41，禁回改）
- 其它 NI `/corerpg` 斜杠；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*`
- `pet_ember_cinder` 的 L20/L21 说明及 name、键名、数值、配方、给物、掉落、体力
- TrMenu / CoreRpg Java / 精英预览壳 / B0.1 / git push
- B2.42 已 PASS · 勾销，不回改；不把本窗扩大为其它斜杠清扫

---

## 8. Backlog 挂账

- 新号：**B2.43**（B2 文案可维护轨 · 单件物品 · 与 B-flex / B-anvil **不捆**）
- B2.42 **PASS · 勾销**保持：close `ef6ff87`（测 `d855ce3` · 施工 `1dbcb88`；设计 `696c80c` · 批准 `48dc3d1`）；ashling summon 斜杠本轨已清
- B2.43：`pet_ember_cinder` L22 `/corerpg pet summon` → **已批 A**；荐 `&7用于枢纽 · 使魔`；soul_dust feed / 其它斜杠仍 soft；**勿宣称 B0.1 已清**

---

## 9. 回总控摘要

- **STATUS：待批 A。** tip：`docs/design-ember-pet-cinder-ni-slash-copy.md`。
- **荐 A（最终替换字符串）：** `- '&7用于枢纽 · 使魔'`，目标实际 **L22**；L20/L21 与显示名保留。
- **TrMenu 依据：** `/ember` / `/menu` → `ember_hub`；枢纽「使魔」（`ember_hub.yml` L102–112）→ `menu: ember_pet`（对齐 B2.42）。
- **旁附：** 当前 `rg -n "/corerpg" plugins/NeigeItems/Items/` 为 **3** 处（玩家可见 2 + 文件头注释 1）；本窗只推 cinder summon 这一处，不宣称其它斜杠/B0.1 已清。
- **硬禁：** 不动 `pet_ember_ashling`、`mat_ember_soul_dust` feed、talent/covenant 已清项、数值/给物；未批不改玩法/NI Items YAML；不叫挑刺；不 git push。
