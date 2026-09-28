# 设计稿 · CoreRpg 重铸缺料 chat 人话对齐（B2.21）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 STATUS-ember-reforge-need-copy-approve.md）。本稿只定 **玩家可见** CoreRpg `cmdReforge` 缺料 `sendMessage` **1** 句；**禁**改重铸消耗数 / `stone_ni_id` / 词缀池 / 掉落 / 体力 / TrMenu / NI 物品文件（除非另批 B）。  
> 债源：B2.20 轻测旁证（`docs/STATUS-ember-disassemble-reforge-copy-test.md`）；backlog 软观察「CoreRpg 重铸缺料 tell 裸 id 可升 B2.21」；B2.20 PASS · 勾销 `5bbaab0`。  
> 对齐：B2.20 已清拆解页 lore 裸 id；UX「文案短清楚 · 玩家点击即见 · NI ID 管理侧可留」。  
> 排除本轮：刚结 B2.6–B2.20 / B-flex-1/2 / B-anvil-1；精英预览厚壳（hub 一点进本、无 P / 无独立 rewards → 证据不足勿硬开）；NI 物品 lore 内 `&7mat_ember_reforge_stone`（物品岗 · 方案 B soft · 勿与 A 双上）；墙钟/DPS；霜锈前压；断塔无证据升 B；B0.1 除非新证据；四件甲/锻炉重做/誓约大改。**勿宣称 B0.1 已清。** 与 B-flex / B-anvil **不捆**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | CoreRpg 重铸 · **缺料 chat 裸 NI id →「余烬重铸石」**（UX · B2.21） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 01:55 Asia/Shanghai |
| 关联 | live `CoreRpg/.../ScrapService.java` · `scrap.yml` `stone_ni_id` · B2.20 测旁证 |
| 状态 | **已批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.21** · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ScrapService.cmdReforge` 缺料 **1** 句玩家可见 chat 字符串（见 §3 · 荐改中文） | 重铸消耗 ×1 / `stone_ni_id` / `countInInventory`·`consumeExact` 逻辑 / 词缀池 / 掉落 / 体力 |
| 批后插件重编译 / 热更 jar（服约定） | NI 物品 YAML；TrMenu（B2.20 已清）；其它 CoreRpg message；loot / DP / MM |
| | git push；开精英预览子菜单壳；宣称 B0.1 已清；未批改玩法 YAML / Java |

---

## 1. 问题一句话

**拆解页 lore 已人话，但玩家无石轻点重铸时 CoreRpg chat 仍回 `[重铸] 需要 mat_ember_reforge_stone ×1`——点击即见管理噪音；属 B2.20 测旁证点名、零改消耗数/逻辑的极薄文案债（1 句 chat）。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 入口 | `/ember` → 拆解 → 点「重铸词缀」→ TrMenu 走 `corerpg reforge` → `ScrapService.cmdReforge` |
| **脏点计数** | **1** 处玩家可见缺料 chat 裸 id |
| 源码 | `CoreRpg/src/main/java/.../ScrapService.java` **L385**：`"[重铸] 需要 " + stoneNiId + " ×1"` |
| 配置键 | `scrap.yml` `reforge.stone_ni_id: mat_ember_reforge_stone`（**管理侧 · 本窗不动**；仅作 count/consume） |
| 无独立 messages.yml | 缺料句硬编码于 Java；**无** lang 表可改 |
| 测岗旁证 | B2.20 轻测：`[重铸] 需要 mat_ember_reforge_stone ×1`——「属 scrap/reforge 运行时文案…非 TrMenu lore，不计入本窗 FAIL」（`STATUS-ember-disassemble-reforge-copy-test.md`） |
| 对照 · 已人话 | TrMenu lore「§8消耗 §6余烬重铸石」（B2.20 PASS `5bbaab0`）；NI `name: '&6余烬重铸石'` |
| 候选对比 · NI 物品 lore | `plugins/NeigeItems/Items/ember-disassemble.yml` L9：`&7mat_ember_reforge_stone` —— 物品岗；**方案 B soft，勿与 A 双上** |
| 候选对比 · 精英预览 | hub 精英一点进本、**无 P / 无独立 rewards** → 厚壳；**勿硬开** |
| 扫网结论 | 缺料句为插件 chat 专岗、玩家点击即见；NI lore 同 id 为悬停物品岗、次痛；**无**比本句更实、更高优的更薄可勾销债；默认推本窗 A |

### 替换口径（展示层 · 不改重铸逻辑）

#### A · CoreRpg 缺料 chat 1 句（推荐 · 升自 B2.20 测旁证）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | `ScrapService.java` L385 `sendMessage` | `[重铸] 需要 ` + `stoneNiId` + ` ×1` → 玩家见 `…mat_ember_reforge_stone ×1` | **字面** `[重铸] 需要 余烬重铸石 ×1`（chat 展示用人话；**仍**用 `stoneNiId` 做 `countInInventory` / `consumeExact`） |

**口径备忘：** 只动上表 1 句玩家可见字符串；其它 `[重铸]` 句（未启用/手持/白名单/词缀池空/扣除失败/成功摘要）**一字不动**；`stone_ni_id` / 消耗 ×1 / 词缀刷新逻辑 **零改**；管理侧 scrap/life/auction/warehouse 键仍用 id。

#### B · NI 重铸石物品 lore 灰字（旁附 soft · 勿双上）

| # | 位置 | 旧 | 新（若另批） |
|---|------|----|--------------|
| B1 | NI `ember-disassemble.yml` lore L9 | `&7mat_ember_reforge_stone` | 删行或改中文（物品岗） |

**本轮不与 A 同上施工**；可作下一项 soft。精英预览壳仍 soft、证据不足勿硬开。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 缺料 chat 人话（推荐）** | L385 展示改「余烬重铸石」；逻辑仍用 `stoneNiId` | 测旁证主债；玩家点击即见；零改消耗×1；与 NI 显示名一致 | 需插件编译；未顺手清 NI lore | **推荐** |
| **B. NI 物品 lore 一行** | 上表 B1 | 同材 id | 物品岗；与 A 捆则双表面 | **旁附 soft · 勿双上** |

**不施工：** 开精英奖励预览子菜单壳；改 NI 物品 lore 裸 id（除非另批 B）；改 `stone_ni_id` / 消耗数 / 词缀池 / 掉落 / 体力；宣称 B0.1 已清；未批改 YAML/Java；重开 B2.6–B2.20 / B-flex / B-anvil；墙钟/DPS；叫挑刺。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / Java / jar / NI / TrMenu **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** 无石轻点重铸 chat 为 `[重铸] 需要 余烬重铸石 ×1`（**无**字面 `mat_ember_reforge_stone`）；消耗仍 ×1；`stone_ni_id` 未改。  
3. **若批 A：** `scrap.yml` / life / 体力 / TrMenu / NI 物品文件 **相对批前零 diff**（除另批 B）。  
4. **若批 A · 禁项：** 未改 NI 物品 lore（除非另批 B）；未开精英预览壳；**不**宣称 B0.1 / NI lore 裸 id 已清。  
5. **目视轻测：** hub→拆解→无石点重铸，chat 人话且数量仍 ×1。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后 · 源码展示层）：

```bash
rg -n '需要 .*stoneNiId|需要 mat_ember_reforge_stone' CoreRpg/src/main/java/town/sunshine/corerpg/ScrapService.java
# 期望：缺料 sendMessage 行不再拼接 stoneNiId / 不再字面 mat_…（count/consume 仍可用 stoneNiId）
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 重铸缺料回报 | —（CoreRpg chat） | `[重铸]` | hub → 拆解 → 点重铸 | `ember_disassemble` → `corerpg reforge` | — | **UI/文案可用** · 无新 NPC |

**站岗自检：** 无新 NPC；仅 chat 文案 → 最高标「UI 可用」，不宣称重铸数值/掉落改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | `/ember` → 拆解 | 「余烬 · 拆解」；lore 已无人话裸 id（B2.20） | 打开菜单 | 返回 hub |
| 2 | 无石点重铸 | chat **`[重铸] 需要 余烬重铸石 ×1`** | `cmdReforge` 缺料 early-return；**未**扣石 | — |
| 3 | 有石点重铸 | 既有成功/失败句（本窗不动） | 扣 1 + 刷新次要词缀 | — |

---

## 7. UX 否决条

| 硬条 | 预期 |
|------|------|
| 玩家须手打指令才能触发缺料提示 | **FAIL**（应菜单点击即见） |
| 施工后缺料 chat 仍出现字面 `mat_ember_reforge_stone` | **FAIL** |
| 借机改消耗×1 / `stone_ni_id` / 词缀池 / 开精英厚壳 | **FAIL** |
| 验收靠 wall-clock 或 DPS / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 | **FAIL** |
| 未批准即改 Java / scrap.yml / NI | **FAIL** |

---

## 8. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A（或不附 B） | **本窗**（文档 only） |
| **插件** | `ScrapService.java` L385 展示改「余烬重铸石」；重编译 CoreRpg jar（批 A 后） | **批 A 后** |
| **测试** | 静态 `rg` + 无石点重铸 chat 轻测；**不叫挑刺**；禁 wall-clock/DPS | 施工后 |

**未批准前不施工**（含不改 Java / scrap.yml / NI）。

---

## 9. 明确未动 / 不做

- 精英奖励预览子菜单壳（证据不足勿硬开）  
- NI 物品文件 lore 内裸 id（B soft，勿与 A 双上）  
- `stone_ni_id` / 消耗 ×1 / 词缀池 / 掉落 / 体力 / TrMenu（B2.20 已结）  
- 其它 `[重铸]` chat（成功摘要仍可能带 gearId——**不叫挑刺**、本窗不做）  
- B0.1 票扣显示名；四件甲；锻炉重做；誓约大改  
- B-flex-1/2 / B-anvil-1 已结物回改  
- 墙钟 / DPS / 挑刺口径  

---

## 10. Backlog 挂账

- 新号：**B2.21**（B2 文案可维护轨 · 与 B-flex / B-anvil **不捆**）  
- 软观察「CoreRpg 重铸缺料 tell」本窗主清；「NI 物品 lore 同 id」留 B soft；「精英预览壳」仍 soft、勿硬开  
- B2.20 PASS 勾销保持（close `5bbaab0`）；**勿宣称 B0.1 已清**

---

## 11. 回总控摘要

- **荐 A：** `ScrapService` 缺料 chat →「余烬重铸石」；零改消耗 ×1 / `stone_ni_id` / 逻辑。  
- **路径：** `docs/design-ember-reforge-need-copy.md` · **B2.21** · STATUS **待批 A**。  
- **证据：** L385 拼接 `stoneNiId`；B2.20 测旁证句；非 TrMenu lore。  
- **B 旁附 soft：** NI 物品 lore `&7mat_ember_reforge_stone`；**勿双上**。精英壳勿硬开。  
- **未动：** 精英厚壳 / NI 物品 lore / 消耗·逻辑 / B0.1 / 四件甲·锻炉·誓约。  
- **验收专岗：** 插件改 1 句 chat → 测试 `rg`+无石点重铸；禁 wall-clock/DPS；不叫挑刺。  
- **勿宣称 B0.1 已清。** 与 B-flex / B-anvil 不捆。
