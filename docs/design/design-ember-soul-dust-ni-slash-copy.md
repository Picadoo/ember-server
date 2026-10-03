# 设计稿 · NI 余烬魂尘 lore feed 去斜杠（B2.44）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 docs/status/STATUS-ember-soul-dust-ni-slash-copy-approve.md）。本稿只定 **玩家可见** NeigeItems `plugins/NeigeItems/Items/ember-pets.yml` 内 `mat_ember_soul_dust` **1** 行 lore feed 斜杠句的人话替换；**禁**改 NI 数值 / 配方 / 给物逻辑 / 掉落 / 体力 / TrMenu / CoreRpg Java。
> 债源：B2.43 **PASS · 勾销** close `7a00524`（测 `3c30405` · 施工 `94ad273`；设计 tip `e8d3704` · 批准 `2d2fcf8`）。总控声明：pet summon 玩家可见斜杠本轨已清；剩 feed。
> 对齐：B2.42/B2.43「用于枢纽 · 使魔」；本窗因 feed 动作比 summon 更需叠功能名，荐叠「· 投喂」并 **保留 `&a`**。同构 B2.40 誓约 / B2.41 天赋枢纽短句骨架。**未批准前不改 NI Items YAML。**
> 排除本轮：`pet_ember_ashling` / `pet_ember_cinder`（B2.42/B2.43 已清 · 禁回改）；`ember-disassemble.yml` 文件头管理注释（非玩家可见 lore · 本窗不推）；talent/covenant 已清项；其它 NI `/corerpg` 斜杠；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*` 批扫；精英预览壳；数值 / 配方 / 给物 / 掉落 / 体力；B0.1；git push。**勿宣称其它斜杠或 B0.1 已清。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI `mat_ember_soul_dust` · **玩家可见 lore `/corerpg pet feed` → 枢纽人话**（UX · B2.44） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 04:08 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-pets.yml` · B2.43 close 后升窗 |
| 状态 | **已批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.44** · 批后另开施工 / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `mat_ember_soul_dust` lore **L34** 这 1 行玩家可见字符串（见 §3 · 荐改） | `mat_ember_soul_dust` 键名 / `material` / `name` / L33「挂机 / 副本副产 · 可堆叠」/ L35「不消耗核心」/ 其它 lore |
| 批 A 后由物品岗施工、再按专岗轻测 | 同文件 `pet_ember_ashling` / `pet_ember_cinder`（B2.42/B2.43 已清 · L10/L22 已是枢纽人话）；`ember-disassemble.yml` 文件头管理注释；talent/covenant 已清项；其它 `/corerpg` 斜杠；TrMenu；CoreRpg Java；玩法/NI Items YAML 未批不得改；git push |
| | 数值 / 配方 / 给物 / 掉落 / 体力 / 精英壳；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*`；B0.1；与其它斜杠双上 |

---

## 1. 问题一句话

**`mat_ember_soul_dust` 显示名已经是「余烬魂尘」，L34 仍把管理侧 `/corerpg pet feed` 露给玩家；背包悬停应指向现网枢纽点击路径（使魔 → 魂尘投喂）。属 B2.43 后升起的物品岗、单件、零改数值/配方/给物的极薄文案债（1 行）。**

---

## 2. 现网事实（只读调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 目标文件 / 键 | `plugins/NeigeItems/Items/ember-pets.yml` · `mat_ember_soul_dust:` |
| **实际目标行** | **L34** `- '&a喂使魔：/corerpg pet feed'`（本窗唯一施工候选） |
| L31 | `name: '&b余烬魂尘'`（保留） |
| L33 | `- '&8挂机 / 副本副产 · 可堆叠'`（保留） |
| L35 | `- '&7不消耗核心'`（保留） |
| 同文件已清 / 禁动 | `pet_ember_ashling` L10 `- '&7用于枢纽 · 使魔'`（B2.42）；`pet_ember_cinder` L22 `- '&7用于枢纽 · 使魔'`（B2.43） |
| 目标现状 | 该件 lore 当前仅 L34 命中字面 `/corerpg`；本设计只推该件 1 行，不宣称其它斜杠已清 |
| 颜色 | 旧句前缀 **`&a`**（绿）；荐改 **保留 `&a`**（与 B2.42/B2.43 的 `&7` 不同，以总控荐与现网 feed 行色为准） |

### TrMenu 路径证据（现网文件 · 对齐 B2.42/B2.43 + 投喂按钮）

1. `plugins/TrMenu/menus/README-ember.md`：`ember_hub.yml` 入口为 `/ember` / `/menu`。
2. `plugins/TrMenu/menus/ember_hub.yml` **L102–112**：枢纽按钮「使魔」点击进入 `menu: ember_pet`；lore 明示「收集出战使魔，偏外观与微量助战」「打开使魔菜单」。
3. `plugins/TrMenu/menus/ember_pet.yml`：Title「余烬 · 使魔」。
4. **投喂按钮（本窗关键）：** `ember_pet.yml` **L76–92** 槽位 `S`：
   - **L79** `name: '§6魂尘投喂'`（玩家可见按钮名）
   - L82 lore「挂机副产魂尘升级使魔」；L85「点击投喂」
   - L91 tell「投喂魂尘升级」；L92 底层 `command: corerpg pet feed`（非玩家可见斜杠教学）

**路径结论：** 证据充分——现网枢纽有独立「使魔」直达按钮 → `ember_pet`，且使魔页有独立「魂尘投喂」按钮对应 feed。B2.42/B2.43 summon 止于「使魔」；本窗 feed **更需叠功能名**，故荐 `&a用于枢纽 · 使魔 · 投喂`（「投喂」取自按钮名「魂尘投喂」的动作词，对齐总控示例；若日后菜单按钮文案变更，按现网按钮定最终措辞，但优先本荐）。

### 旁附 `/corerpg` 扫描（本窗只推 soul_dust feed）

```text
plugins/NeigeItems/Items/ember-pets.yml:34:    - '&a喂使魔：/corerpg pet feed'
plugins/NeigeItems/Items/ember-disassemble.yml:3:# 命令（插件岗待接）：/corerpg scrap · /corerpg reforge
```

当前 `rg -n "/corerpg" plugins/NeigeItems/Items/` 共 **2** 处：玩家可见 lore **1** 处（soul_dust feed），另有 `ember-disassemble.yml` 文件头管理注释 **1** 处。ashling / cinder summon 已无 `/corerpg`（B2.42 / B2.43）；`mat_ember_covenant_reset` / `mat_ember_talent_reset` 已无 `/corerpg`（B2.40 / B2.41）。旁附均只列证，不在本窗推施工；不宣称其它斜杠/B0.1 已清。**禁**动 disassemble 管理注释（非玩家可见 lore）。

---

## 3. 替换口径（展示层 · 不改 NI 数值/配方/给物）

### A · 单件 `mat_ember_soul_dust` 去斜杠（荐）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ember-pets.yml` lore **L34** | `- '&a喂使魔：/corerpg pet feed'` | `- '&a用于枢纽 · 使魔 · 投喂'` |

**备选（同档 · 非荐）：** `- '&a用于枢纽 · 使魔 · 魂尘投喂'`。完整复述按钮名，但偏长；总控示例止于「· 投喂」，且「魂尘」已在显示名「余烬魂尘」出现，荐短句。

**备选（不荐）：** `- '&a用于枢纽 · 使魔'`（完全同构 B2.42/B2.43）。省略投喂动作，玩家不易区分魂尘用途与使魔蛋 summon 入口。

**L33 / L35 保留：** `- '&8挂机 / 副本副产 · 可堆叠'`、`- '&7不消耗核心'` 一字不动；`name: '&b余烬魂尘'` 不动。**保留 `&a`。**

**口径备忘：** 批 A 后只替换上表 1 句玩家可见 lore；键名 / `name` / L33 / L35 不动；`pet_ember_ashling`、`pet_ember_cinder`、disassemble 管理注释、talent/covenant 已清项、其它斜杠、TrMenu、CoreRpg、数值/配方/给物/掉落/体力均不动。

---

## 4. 方案与决策

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 枢纽 · 使魔 · 投喂（推荐）** | L34 → `&a用于枢纽 · 使魔 · 投喂` | 对齐总控荐；有 TrMenu「魂尘投喂」按钮证据；保留 `&a`；去斜杠 | 比 summon 同构多一层 | **推荐** |
| **A′. 叠完整按钮名** | L34 → `&a用于枢纽 · 使魔 · 魂尘投喂` | 与按钮名一字对齐 | 偏长；「魂尘」与显示名重复 | 备选 |
| **A″. 止于使魔（不荐）** | L34 → `&a用于枢纽 · 使魔` | 完全同构 B2.42/B2.43 | 不区分 feed 与 summon 用途 | 不荐 |
| B. 其它 `/corerpg` 斜杠批 | 顺手改 disassemble 注释等 | 扩大覆盖 | 违反单件薄窗、硬禁、与其它斜杠双上 | **不做** |

**不施工：** 未批改 `plugins/NeigeItems/Items/*.yml`；不动 L33/L35；不动 ashling/cinder；不动 disassemble 管理注释；不改其它斜杠；不改数值/配方/给物；不开精英壳；不宣称 B0.1 已清；不 git push；不叫挑刺。

---

## 5. 验收（待批 A 后另开施工）

1. 本设计 commit 仅包含本稿与 backlog；玩法 YAML / NI Items / TrMenu / CoreRpg / loot **零 diff**。
2. 若批 A，物品岗只改 `mat_ember_soul_dust` L34 为 `- '&a用于枢纽 · 使魔 · 投喂'`；L33/L35、键名、name、其它字段语义不变；**保留 `&a`**。
3. 该件 lore 施工后无字面 `/corerpg`；`pet_ember_ashling` L10、`pet_ember_cinder` L22、`ember-disassemble.yml` 文件头及其它文件相对批前零 diff。
4. 静态 `rg` + 目视悬停：1 枚「余烬魂尘」显示「挂机 / 副本副产 · 可堆叠」「用于枢纽 · 使魔 · 投喂」「不消耗核心」，不出现 `/corerpg`。
5. **范围声明：** 仅该件 `/corerpg` 目标可在批后验收；**不宣称其它斜杠或 B0.1 已清**；不宣称 disassemble 管理注释已清。

预期施工后静态检查（本设计窗不执行改动）：

```bash
rg -n -C 3 "mat_ember_soul_dust" plugins/NeigeItems/Items/ember-pets.yml
rg -n "/corerpg" plugins/NeigeItems/Items/ember-pets.yml
# 预期：同文件无 /corerpg；ashling L10 / cinder L22 仍为枢纽人话
rg -n "/corerpg" plugins/NeigeItems/Items/
# 预期：仅剩 disassemble 文件头管理注释；本件不再命中
```

---

## 6. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **物品** | 批 A 后仅替换 `ember-pets.yml` 目标 L34 | 批 A 后 |
| **测试** | `rg` + 悬停 lore；不测玩法数值，不叫挑刺 | 施工后 |

玩家动线：`/ember` 或 `/menu` → 枢纽「使魔」→ 使魔页「魂尘投喂」；物品 lore 只作入口提示，不改变消耗、菜单或指令实现。

**未批准前不施工**（含不改 NI YAML）。

---

## 7. 明确未动 / 不做

- `pet_ember_ashling` / `pet_ember_cinder`（B2.42/B2.43 已清 · 禁回改）
- `ember-disassemble.yml` 文件头管理注释（非玩家可见 · 本窗不推）
- talent/covenant 已清项（B2.40 / B2.41，禁回改）
- 其它 NI `/corerpg` 斜杠；其它 `&7mat_*` / `&7pet_*` / `&7gem_*` / `&7cosmetic_*`
- `mat_ember_soul_dust` 的 L33/L35 说明及 name、键名、数值、配方、给物、掉落、体力
- TrMenu / CoreRpg Java / 精英预览壳 / B0.1 / git push
- B2.43 已 PASS · 勾销，不回改；不把本窗扩大为其它斜杠清扫；不宣称 pet summon 以外本轨外斜杠已清

---

## 8. Backlog 挂账

- 新号：**B2.44**（B2 文案可维护轨 · 单件物品 · 与 B-flex / B-anvil **不捆**）
- B2.43 **PASS · 勾销**保持：close `7a00524`（测 `3c30405` · 施工 `94ad273`；设计 `e8d3704` · 批准 `2d2fcf8`）；pet summon 玩家可见斜杠本轨已清
- B2.44：`mat_ember_soul_dust` L34 `/corerpg pet feed` → **已批 A**；荐 `&a用于枢纽 · 使魔 · 投喂`；disassemble 管理注释 / 其它斜杠仍 soft；**勿宣称 B0.1 已清**

---

## 9. 回总控摘要

- **STATUS：待批 A。** tip：`docs/design/design-ember-soul-dust-ni-slash-copy.md`。
- **荐 A（最终替换字符串）：** `- '&a用于枢纽 · 使魔 · 投喂'`，目标实际 **L34**；L33/L35 与显示名保留；**保留 `&a`**。
- **TrMenu 依据：** `/ember` / `/menu` → `ember_hub`；枢纽「使魔」（`ember_hub.yml` L102–112）→ `menu: ember_pet`；使魔页「魂尘投喂」（`ember_pet.yml` L79）支撑「· 投喂」。
- **旁附：** 当前 `rg -n "/corerpg" plugins/NeigeItems/Items/` 为 **2** 处（玩家可见 1 + 文件头注释 1）；本窗只推 soul_dust feed 这一处，不宣称其它斜杠/B0.1 已清。
- **硬禁：** 不动 ashling/cinder、disassemble 管理注释、talent/covenant 已清项、数值/给物；未批不改玩法/NI Items YAML；不叫挑刺；不 git push。
