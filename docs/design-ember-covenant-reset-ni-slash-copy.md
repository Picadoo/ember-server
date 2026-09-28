# 设计稿 · NI 誓约重置券 lore 去斜杠（B2.40）

> **STATUS：待批 A**。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-covenant-talent.yml` 内 `mat_ember_covenant_reset` **1** 行 lore 斜杠句的人话替换；**禁**改 NI 数值 / 配方 / 给物逻辑 / 掉落 / 体力 / TrMenu / CoreRpg Java。
> 债源：B2.39 **PASS · 勾销** `f0f6255`（测 `a188a8b` · 施工 `6e08d32`；设计 tip `88c53bd` · 批准 `8e2ef70`）。B2.40 承接同一物品先前 B2.27 裸 id 已清后的剩余 `/corerpg` 斜杠软债。
> 对齐：B2.23 重铸石已批口径「用于枢纽 · 拆解 → 重铸」；本窗继续采用「用于枢纽 · …」的人话提示。**未批准前不改 NI Items YAML。**
> 排除本轮：`mat_ember_talent_reset`；pet summon/feed；其它 NI `/corerpg` 斜杠；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*` 批扫；精英预览壳；数值 / 配方 / 给物 / 掉落 / 体力；B0.1；git push。**勿宣称其它斜杠或 B0.1 已清。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI `mat_ember_covenant_reset` · **玩家可见 lore `/corerpg` → 枢纽人话**（UX · B2.40） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 03:47 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-covenant-talent.yml` · B2.39 close 后升窗 |
| 状态 | **待批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.40** · 批后另开施工 / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `mat_ember_covenant_reset` lore **L9** 这 1 行玩家可见字符串（见 §3 · 荐改） | `mat_ember_covenant_reset` 键名 / `material` / `name` / L8「洗约 · 免晶钻」/ 其它 lore / enchantments / hideflags |
| 批 A 后由物品岗施工、再按专岗轻测 | 同文件 `mat_ember_talent_reset`；pet summon/feed；其它 `/corerpg` 斜杠；TrMenu；CoreRpg Java；玩法/NI Items YAML 未批不得改；git push |
| | 数值 / 配方 / 给物 / 掉落 / 体力 / 精英壳；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*`；B0.1；与其它斜杠双上 |

---

## 1. 问题一句话

**`mat_ember_covenant_reset` 显示名已经是「誓约重置券」，L9 仍把管理侧 `/corerpg covenant set|reset` 露给玩家；背包悬停应指向现网枢纽点击路径。属 B2.39 后升起的物品岗、单件、零改数值/配方/给物的极薄文案债（1 行）。**

---

## 2. 现网事实（只读调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 目标文件 / 键 | `plugins/NeigeItems/Items/ember-covenant-talent.yml` · `mat_ember_covenant_reset:` |
| **实际目标行** | **L9** `- '&7用于 /corerpg covenant set|reset'`（本窗唯一施工候选） |
| L6 | `name: '&6誓约重置券'`（保留） |
| L8 | `- '&8洗约 · 免晶钻'`（保留） |
| 同文件禁动 | `mat_ember_talent_reset` 实际 L15 起；其 lore L20 斜杠本窗不动 |
| 目标现状 | 该件 lore 当前仅 L9 命中字面 `/corerpg`；本设计只推该件 1 行，不宣称其它斜杠已清 |

### TrMenu 路径证据（现网文件）

1. `plugins/TrMenu/menus/README-ember.md` L7：`ember_hub.yml` 的入口为 `/ember` / `/menu`。
2. `plugins/TrMenu/menus/ember_hub.yml` L52–65：枢纽按钮「角色」进入 `menu: ember_character`；L67–81：枢纽另有独立按钮「誓约」，点击进入 `menu: ember_covenant`。
3. `plugins/TrMenu/menus/ember_character.yml` L59–71：角色页「当前誓约」点击执行 `corerpg covenant`，并提示「打开誓约页可重选」；因此「角色 → 誓约」是现网的上下文路径，但不是进入 `ember_covenant` 的唯一/最短路径。
4. `plugins/TrMenu/menus/ember_covenant.yml` L54–67：誓约页明确展示「洗约说明」，写明「晶钻 ×80 或 誓约重置券」，且入口本身由枢纽「誓约」按钮打开。

**路径结论：** 证据充分，且现网有「枢纽 → 誓约」直达与「枢纽 → 角色 → 当前誓约」上下文两条入口。为不臆造、也不让提示多写一层实际非必需路径，本稿 **荐最短且直接承载洗约的** `&7用于枢纽 · 誓约`；总控示例 `&7用于枢纽 · 角色 → 誓约` 有角色页旁证，列作备选，不作最终荐文案。

### 旁附 `/corerpg` 扫描（本窗只推 covenant_reset）

```text
plugins/NeigeItems/Items/ember-covenant-talent.yml:9:    - '&7用于 /corerpg covenant set|reset'
plugins/NeigeItems/Items/ember-covenant-talent.yml:20:    - '&7用于 /corerpg talent reset（日免费用尽后）'
plugins/NeigeItems/Items/ember-pets.yml:10:    - '&7用于 /corerpg pet summon'
plugins/NeigeItems/Items/ember-pets.yml:22:    - '&7用于 /corerpg pet summon'
plugins/NeigeItems/Items/ember-pets.yml:34:    - '&a喂使魔：/corerpg pet feed'
plugins/NeigeItems/Items/ember-disassemble.yml:3:# 命令（插件岗待接）：/corerpg scrap · /corerpg reforge
```

当前 `rg -n "/corerpg" plugins/NeigeItems/Items/` 共 **6** 处：玩家可见 lore **5** 处，另有 `ember-disassemble.yml` 文件头管理注释 **1** 处。旁附均只列证，不在本窗推施工；不宣称其它斜杠/B0.1 已清。

---

## 3. 替换口径（展示层 · 不改 NI 数值/配方/给物）

### A · 单件 `mat_ember_covenant_reset` 去斜杠（荐）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ember-covenant-talent.yml` lore **L9** | `- '&7用于 /corerpg covenant set|reset'` | `- '&7用于枢纽 · 誓约'` |

**备选（同档 · 非荐）：** `- '&7用于枢纽 · 角色 → 誓约'`。该链有 `ember_hub`「角色」→`ember_character`「当前誓约」的现网旁证，但现网枢纽已提供独立「誓约」直达按钮；故不把较长链写成主荐。

**L8 保留：** `- '&8洗约 · 免晶钻'` 一字不动。

**口径备忘：** 批 A 后只替换上表 1 句玩家可见 lore；键名 / `name` / L8 / enchantments / hideflags 不动；`mat_ember_talent_reset`、pet summon/feed、其它斜杠、TrMenu、CoreRpg、数值/配方/给物/掉落/体力均不动。

---

## 4. 方案与决策

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 枢纽直达短句（推荐）** | L9 → `&7用于枢纽 · 誓约` | 与 `ember_hub` B→`ember_covenant` 一跳直连；同构 B2.23；短、准确、去斜杠 | 不复述角色页上下文路径 | **推荐** |
| **A′. 角色上下文链** | L9 → `&7用于枢纽 · 角色 → 誓约` | 对齐总控示例；角色页有「当前誓约」旁证 | 比现网直达链多一层，可能让玩家误以为必须先进角色 | 备选 |
| B. 其它 `/corerpg` 斜杠批 | 顺手改 talent / pet 等 | 扩大覆盖 | 违反单件薄窗、硬禁、与其它斜杠双上 | **不做** |

**不施工：** 未批改 `plugins/NeigeItems/Items/*.yml`；不动 L8；不动 `mat_ember_talent_reset`；不动 pet summon/feed；不改其它斜杠；不改数值/配方/给物；不开精英壳；不宣称 B0.1 已清；不 git push；不叫挑刺。

---

## 5. 验收（待批 A 后另开施工）

1. 本设计 commit 仅包含本稿与 backlog；玩法 YAML / NI Items / TrMenu / CoreRpg / loot **零 diff**。
2. 若批 A，物品岗只改 `mat_ember_covenant_reset` L9 为 `- '&7用于枢纽 · 誓约'`；L8「洗约 · 免晶钻」、键名、name、其它字段语义不变。
3. 该件 lore 施工后无字面 `/corerpg`；`mat_ember_talent_reset`、pet summon/feed 及其它文件相对批前零 diff。
4. 静态 `rg` + 目视悬停：1 枚「誓约重置券」显示「洗约 · 免晶钻」与「用于枢纽 · 誓约」，不出现 `/corerpg`。
5. **范围声明：** 仅该件 `/corerpg` 目标可在批后验收；**不宣称其它斜杠或 B0.1 已清**。

预期施工后静态检查（本设计窗不执行改动）：

```bash
rg -n -C 3 "mat_ember_covenant_reset" plugins/NeigeItems/Items/ember-covenant-talent.yml
rg -n "/corerpg" plugins/NeigeItems/Items/ember-covenant-talent.yml
# 预期：第二条仅命中同文件 talent_reset L20；本件 lore 无 /corerpg
```

---

## 6. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **物品** | 批 A 后仅替换 `ember-covenant-talent.yml` 目标 L9 | 批 A 后 |
| **测试** | `rg` + 悬停 lore；不测玩法数值，不叫挑刺 | 施工后 |

玩家动线：`/ember` 或 `/menu` → 枢纽「誓约」→ 誓约页「洗约说明」→ 使用「誓约重置券」；物品 lore 只作入口提示，不改变消耗、菜单或指令实现。

**未批准前不施工**（含不改 NI YAML）。

---

## 7. 明确未动 / 不做

- `mat_ember_talent_reset`（含其 `/corerpg talent reset`）
- pet summon / pet feed，以及 `ember-pets.yml`
- 其它 NI `/corerpg` 斜杠；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*`
- `mat_ember_covenant_reset` 的 L8「洗约 · 免晶钻」及 name、键名、数值、配方、给物、掉落、体力
- TrMenu / CoreRpg Java / 精英预览壳 / B0.1 / git push
- B2.39 已 PASS · 勾销，不回改；不把本窗扩大为其它斜杠清扫

---

## 8. Backlog 挂账

- 新号：**B2.40**（B2 文案可维护轨 · 单件物品 · 与 B-flex / B-anvil **不捆**）
- B2.39 **PASS · 勾销**保持：close `f0f6255`（测 `a188a8b` · 施工 `6e08d32`；设计 `88c53bd` · 批准 `8e2ef70`）；live `&7pet_*` 本轨已归零
- B2.40：`mat_ember_covenant_reset` L9 `/corerpg` → **待批 A**；荐 `&7用于枢纽 · 誓约`；`talent_reset` 与 pet summon/feed / 其它斜杠仍 soft；**勿宣称 B0.1 已清**

---

## 9. 回总控摘要

- **STATUS：待批 A。** tip：`docs/design-ember-covenant-reset-ni-slash-copy.md`。
- **荐 A（最终替换字符串）：** `- '&7用于枢纽 · 誓约'`，目标实际 **L9**；L8「洗约 · 免晶钻」保留。
- **TrMenu 依据：** `/ember` / `/menu` → `ember_hub`；枢纽「誓约」B（`ember_hub.yml` L67–81）→ `menu: ember_covenant`；誓约页 L54–67 明示洗约与重置券。角色链亦存在，但不是最短入口。
- **旁附：** 当前 `rg -n "/corerpg" plugins/NeigeItems/Items/` 为 **6** 处（玩家可见 5 + 文件头注释 1）；本窗只推 covenant_reset 这一处，不宣称其它斜杠/B0.1 已清。
- **硬禁：** 不动 `mat_ember_talent_reset`、pet summon/feed、数值/给物；未批不改玩法/NI Items YAML；不叫挑刺；不 git push。
