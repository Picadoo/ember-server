# 设计稿 · NI 天赋重置券 lore 去斜杠（B2.41）

> **STATUS：待批 A**。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-covenant-talent.yml` 内 `mat_ember_talent_reset` **1** 行 lore 斜杠句的人话替换；**禁**改 NI 数值 / 配方 / 给物逻辑 / 掉落 / 体力 / TrMenu / CoreRpg Java。
> 债源：B2.40 **PASS · 勾销** close `611f999`（测 `9affd15` · 施工 `c7d55d1`；设计 tip `d49dd7b` · 批准 `1a0f820`）。B2.41 承接同文件 talent_reset 剩余 `/corerpg` 斜杠软债。
> 对齐：B2.40 誓约重置券已批口径「用于枢纽 · 誓约」；本窗同构「用于枢纽 · 天赋」，并**必须保留**括号内「日免费用尽后」。**未批准前不改 NI Items YAML。**
> 排除本轮：`mat_ember_covenant_reset`（B2.40 已清）；pet summon/feed；其它 NI `/corerpg` 斜杠；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*` 批扫；精英预览壳；数值 / 配方 / 给物 / 掉落 / 体力；B0.1；git push。**勿宣称其它斜杠或 B0.1 已清。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI `mat_ember_talent_reset` · **玩家可见 lore `/corerpg` → 枢纽人话**（UX · B2.41） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 03:53 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-covenant-talent.yml` · B2.40 close 后升窗 |
| 状态 | **待批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.41** · 批后另开施工 / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `mat_ember_talent_reset` lore **L20** 这 1 行玩家可见字符串（见 §3 · 荐改） | `mat_ember_talent_reset` 键名 / `material` / `name` / L19「额外洗点 · 免晶钻」/ 其它 lore / enchantments / hideflags |
| 批 A 后由物品岗施工、再按专岗轻测 | 同文件 `mat_ember_covenant_reset`（B2.40 已施工）；pet summon/feed；其它 `/corerpg` 斜杠；TrMenu；CoreRpg Java；玩法/NI Items YAML 未批不得改；git push |
| | 数值 / 配方 / 给物 / 掉落 / 体力 / 精英壳；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*`；B0.1；与其它斜杠双上 |

---

## 1. 问题一句话

**`mat_ember_talent_reset` 显示名已经是「天赋重置券」，L20 仍把管理侧 `/corerpg talent reset` 露给玩家；背包悬停应指向现网枢纽点击路径，并保留「日免费用尽后」语义。属 B2.40 后升起的物品岗、单件、零改数值/配方/给物的极薄文案债（1 行）。**

---

## 2. 现网事实（只读调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 目标文件 / 键 | `plugins/NeigeItems/Items/ember-covenant-talent.yml` · `mat_ember_talent_reset:` |
| **实际目标行** | **L20** `- '&7用于 /corerpg talent reset（日免费用尽后）'`（本窗唯一施工候选） |
| L17 | `name: '&b天赋重置券'`（保留） |
| L19 | `- '&8额外洗点 · 免晶钻'`（保留） |
| 同文件已清 | `mat_ember_covenant_reset` L9 现为 `- '&7用于枢纽 · 誓约'`（无 `/corerpg`；本窗禁动） |
| 目标现状 | 该件 lore 当前仅 L20 命中字面 `/corerpg`；本设计只推该件 1 行，不宣称其它斜杠已清 |

### TrMenu 路径证据（现网文件）

1. `plugins/TrMenu/menus/README-ember.md` L7：`ember_hub.yml` 的入口为 `/ember` / `/menu`；README 亦将 `ember_talent.yml` 标为「主菜单『天赋』」。
2. `plugins/TrMenu/menus/ember_hub.yml` L83–97：枢纽按钮「天赋」（键 C）点击进入 `menu: ember_talent`；lore 明示「日免洗 1 次」。
3. `plugins/TrMenu/menus/ember_talent.yml` L73–89：天赋页「洗点」按钮 lore 写明「日免费 1 次（上海日历日）」「额外：晶钻 ×25 或天赋重置券」；actions 执行 `corerpg talent reset`（菜单底层，非玩家可见斜杠教学）。
4. `plugins/TrMenu/menus/ember_character.yml`：本窗 `rg` 未见直达「天赋」按钮；进入天赋的最短现网路径是枢纽「天赋」一跳，而非角色页中转。

**路径结论：** 证据充分，现网枢纽有独立「天赋」直达按钮，且天赋页洗点说明已绑定「日免 / 晶钻 / 天赋重置券」。本稿 **荐最短且同构 B2.40 的** `&7用于枢纽 · 天赋（日免费用尽后）`；较长链无现网必需证据，不作主荐。

### 旁附 `/corerpg` 扫描（本窗只推 talent_reset）

```text
plugins/NeigeItems/Items/ember-covenant-talent.yml:20:    - '&7用于 /corerpg talent reset（日免费用尽后）'
plugins/NeigeItems/Items/ember-pets.yml:10:    - '&7用于 /corerpg pet summon'
plugins/NeigeItems/Items/ember-pets.yml:22:    - '&7用于 /corerpg pet summon'
plugins/NeigeItems/Items/ember-pets.yml:34:    - '&a喂使魔：/corerpg pet feed'
plugins/NeigeItems/Items/ember-disassemble.yml:3:# 命令（插件岗待接）：/corerpg scrap · /corerpg reforge
```

当前 `rg -n "/corerpg" plugins/NeigeItems/Items/` 共 **5** 处：玩家可见 lore **4** 处，另有 `ember-disassemble.yml` 文件头管理注释 **1** 处。`mat_ember_covenant_reset` 已无 `/corerpg`（B2.40）。旁附均只列证，不在本窗推施工；不宣称其它斜杠/B0.1 已清。

---

## 3. 替换口径（展示层 · 不改 NI 数值/配方/给物）

### A · 单件 `mat_ember_talent_reset` 去斜杠（荐）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ember-covenant-talent.yml` lore **L20** | `- '&7用于 /corerpg talent reset（日免费用尽后）'` | `- '&7用于枢纽 · 天赋（日免费用尽后）'` |

**备选（同档 · 非荐）：** `- '&7用于枢纽 · 天赋 → 洗点（日免费用尽后）'`。天赋页确有「洗点」按钮，但 B2.40 主荐止于枢纽直达页名「誓约」，不叠功能按钮名；本窗同构止于「天赋」，日免语义已在括号保留。

**L19 保留：** `- '&8额外洗点 · 免晶钻'` 一字不动。

**日免语义：** 荐文案括号内「日免费用尽后」**必须保留**；与 TrMenu「日免费 1 次」及券作额外消耗的现网规则一致。

**口径备忘：** 批 A 后只替换上表 1 句玩家可见 lore；键名 / `name` / L19 / enchantments / hideflags 不动；`mat_ember_covenant_reset`、pet summon/feed、其它斜杠、TrMenu、CoreRpg、数值/配方/给物/掉落/体力均不动。

---

## 4. 方案与决策

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 枢纽直达短句 + 日免（推荐）** | L20 → `&7用于枢纽 · 天赋（日免费用尽后）` | 与 `ember_hub` C→`ember_talent` 一跳直连；同构 B2.40；保留日免语义；短、准确、去斜杠 | 不复述「洗点」按钮名 | **推荐** |
| **A′. 叠洗点按钮名** | L20 → `&7用于枢纽 · 天赋 → 洗点（日免费用尽后）` | 更贴近洗点动作 | 比 B2.40 同构长一层；玩家未必需要 | 备选 |
| B. 其它 `/corerpg` 斜杠批 | 顺手改 pet / disassemble 等 | 扩大覆盖 | 违反单件薄窗、硬禁、与其它斜杠双上 | **不做** |

**不施工：** 未批改 `plugins/NeigeItems/Items/*.yml`；不动 L19；不动 `mat_ember_covenant_reset`；不动 pet summon/feed；不改其它斜杠；不改数值/配方/给物；不开精英壳；不宣称 B0.1 已清；不 git push；不叫挑刺。

---

## 5. 验收（待批 A 后另开施工）

1. 本设计 commit 仅包含本稿与 backlog；玩法 YAML / NI Items / TrMenu / CoreRpg / loot **零 diff**。
2. 若批 A，物品岗只改 `mat_ember_talent_reset` L20 为 `- '&7用于枢纽 · 天赋（日免费用尽后）'`；L19「额外洗点 · 免晶钻」、键名、name、其它字段语义不变；日免语义保留。
3. 该件 lore 施工后无字面 `/corerpg`；`mat_ember_covenant_reset`、pet summon/feed 及其它文件相对批前零 diff。
4. 静态 `rg` + 目视悬停：1 枚「天赋重置券」显示「额外洗点 · 免晶钻」与「用于枢纽 · 天赋（日免费用尽后）」，不出现 `/corerpg`。
5. **范围声明：** 仅该件 `/corerpg` 目标可在批后验收；**不宣称其它斜杠或 B0.1 已清**。

预期施工后静态检查（本设计窗不执行改动）：

```bash
rg -n -C 3 "mat_ember_talent_reset" plugins/NeigeItems/Items/ember-covenant-talent.yml
rg -n "/corerpg" plugins/NeigeItems/Items/ember-covenant-talent.yml
# 预期：同文件无 /corerpg（covenant_reset 已清；talent_reset L20 已换人话）
rg -n "/corerpg" plugins/NeigeItems/Items/
# 预期：剩 pet summon/feed + disassemble 文件头注释；本件不再命中
```

---

## 6. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **物品** | 批 A 后仅替换 `ember-covenant-talent.yml` 目标 L20 | 批 A 后 |
| **测试** | `rg` + 悬停 lore；不测玩法数值，不叫挑刺 | 施工后 |

玩家动线：`/ember` 或 `/menu` → 枢纽「天赋」→ 天赋页「洗点」→ 日免用尽后使用「天赋重置券」（或晶钻）；物品 lore 只作入口提示，不改变消耗、菜单或指令实现。

**未批准前不施工**（含不改 NI YAML）。

---

## 7. 明确未动 / 不做

- `mat_ember_covenant_reset`（B2.40 已 PASS · 勾销，禁回改）
- pet summon / pet feed，以及 `ember-pets.yml`
- 其它 NI `/corerpg` 斜杠；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*`
- `mat_ember_talent_reset` 的 L19「额外洗点 · 免晶钻」及 name、键名、数值、配方、给物、掉落、体力
- TrMenu / CoreRpg Java / 精英预览壳 / B0.1 / git push
- B2.40 已 PASS · 勾销，不回改；不把本窗扩大为其它斜杠清扫

---

## 8. Backlog 挂账

- 新号：**B2.41**（B2 文案可维护轨 · 单件物品 · 与 B-flex / B-anvil **不捆**）
- B2.40 **PASS · 勾销**保持：close `611f999`（测 `9affd15` · 施工 `c7d55d1`；设计 `d49dd7b` · 批准 `1a0f820`）；covenant_reset 斜杠本轨已清
- B2.41：`mat_ember_talent_reset` L20 `/corerpg` → **待批 A**；荐 `&7用于枢纽 · 天赋（日免费用尽后）`；pet summon/feed / 其它斜杠仍 soft；**勿宣称 B0.1 已清**

---

## 9. 回总控摘要

- **STATUS：待批 A。** tip：`docs/design-ember-talent-reset-ni-slash-copy.md`。
- **荐 A（最终替换字符串）：** `- '&7用于枢纽 · 天赋（日免费用尽后）'`，目标实际 **L20**；L19「额外洗点 · 免晶钻」保留；日免语义保留。
- **TrMenu 依据：** `/ember` / `/menu` → `ember_hub`；枢纽「天赋」C（`ember_hub.yml` L83–97）→ `menu: ember_talent`；天赋页 L73–89「洗点」明示日免 1 次与「晶钻 ×25 或天赋重置券」。
- **旁附：** 当前 `rg -n "/corerpg" plugins/NeigeItems/Items/` 为 **5** 处（玩家可见 4 + 文件头注释 1）；本窗只推 talent_reset 这一处，不宣称其它斜杠/B0.1 已清。
- **硬禁：** 不动 `mat_ember_covenant_reset`、pet summon/feed、数值/给物；未批不改玩法/NI Items YAML；不叫挑刺；不 git push。
