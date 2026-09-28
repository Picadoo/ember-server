# 设计稿 · 拆解菜单重铸石灰字 `mat_ember_reforge_stone` 人话对齐（B2.20）

> **STATUS：已批 A**（总控 2026-09-29 · tip 见 STATUS-ember-disassemble-reforge-copy-approve.md）。 本稿只定 **玩家可见** TrMenu `ember_disassemble.yml` 中 **1** 行灰字裸 NI id；**禁**改拆解/重铸数值 / 掉落 / 体力 / NI 物品文件 / loot / DP / MM / scrap·reforge 逻辑。  
> 债源：B2.19 PASS 旁附 B soft（`b3a00b8` / tip `design-ember-set-gear-id-copy.md` §B）；backlog 软观察「拆解 `mat_ember_reforge_stone` 可升 B2.20」。  
> 对齐：B2.19 已清套装页 4 句；UX「文案短清楚 · TrMenu 点击 · NI ID 管理侧可留」。  
> 排除本轮：刚结 B2.6–B2.19 / B-flex-1/2 / B-anvil-1；精英预览厚壳（hub 一点进本、无 P / 无独立 rewards → 证据不足勿硬开）；NI 物品 lore 内 `&7mat_ember_reforge_stone` 等（物品岗另软挂 · 勿与 A 双上）；墙钟/DPS；霜锈前压；断塔无证据升 B；B0.1 除非新证据；四件甲/锻炉重做/誓约大改。**勿宣称 B0.1 已清。** 与 B-flex / B-anvil **不捆**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 拆解菜单 · **玩家可见 `mat_ember_reforge_stone` 灰字对齐**（UX · B2.20） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 01:48 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_disassemble.yml` · B2.19 旁附 B1 |
| 状态 | **已批 A** |
| 关联 STATUS / 稿 | backlog 挂 **B2.20** · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember_disassemble.yml` 重铸 lore **1** 行玩家可见字符串（见 §3 · 荐删） | 拆解/重铸数值 / scrap·reforge command / 掉落 / 体力 cost |
| 热更 / TrMenu reload（服约定） | NI 物品 YAML（含 lore 内 `&7mat_ember_reforge_stone`）；其它 TrMenu 页；loot / DP / MM / CoreRpg scrap·life |
| | git push；开精英预览子菜单壳；宣称 B0.1 已清；未批改玩法 YAML |

---

## 1. 问题一句话

**拆解页重铸图标上行已写「§8消耗 §6余烬重铸石」，下一行仍印裸 id `§8mat_ember_reforge_stone`——玩家悬停即见管理噪音；属 B2.19 已点名、可菜单轻测、零改拆解数值的极薄文案债（1 行）。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 入口 | `/ember` → hub → 拆解/重铸 → `ember_disassemble`（TrMenu 点击） |
| **脏点计数** | **1** 处玩家可见裸 id（重铸 lore） |
| L66 | `§8消耗 §6余烬重铸石`（**已中文** · 保留） |
| L67 | `§8mat_ember_reforge_stone`（**本窗唯一目标**） |
| 对照 · 已人话 | `name: §6重铸词缀`；tell（L75）已写「背包有重铸石」无裸 id；规则速览「消耗重铸石」 |
| NI 显示名 | `mat_ember_reforge_stone`→余烬重铸石（**本窗不改 NI 文件**） |
| 管理侧键 | `scrap.yml` `stone_ni_id` / life give / auction / warehouse 仍用 id —— **不动** |
| 候选对比 · NI 物品 lore | `ember-disassemble.yml` L9：`&7mat_ember_reforge_stone` —— 物品岗另软挂；**方案 B soft，勿与 A 双上** |
| 候选对比 · 精英预览 | hub 精英一点进本、**无 P / 无独立 rewards** → 厚壳；**勿硬开** |
| 扫网结论 | TrMenu `ember*.yml` 字面 `§8mat_/gear_/acc_` **仅余本 1 行**；**无**比本行更实、更高优的更薄可勾销债；默认仍推本窗 |

### 替换口径（展示层 · 不改拆解/重铸逻辑）

#### A · 拆解重铸 lore 1 行（推荐 · 升自 B2.19 旁附）

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | 重铸 lore L67 | `§8mat_ember_reforge_stone` | **删该行**（上行 L66 已有「§8消耗 §6余烬重铸石」· 更薄、无重复） |

**备选（同档 · 非荐）：** 改 `§8余烬重铸石`——与 L66 语义叠床，不如删行。

**口径备忘：** 只动上表 1 句玩家可见字符串；`name:` / 其它 lore / tell / 分解图标 / `actions` 里 sound·command·close **一字不动**；拆解掉落与重铸消耗逻辑 **零改**；管理侧文档与 NI/CoreRpg 键仍用 id。

#### B · NI 重铸石物品 lore 灰字（旁附 soft · 勿双上）

| # | 位置 | 旧 | 新（若另批） |
|---|------|----|--------------|
| B1 | NI `ember-disassemble.yml` lore | `&7mat_ember_reforge_stone` | 删行或改中文（物品岗） |

**本轮不与 A 同上施工**；可作下一项 soft。精英预览壳仍 soft、证据不足勿硬开。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 删重铸 lore 灰字 1 行（推荐）** | 删 L67；零改数值/掉落/体力 | B2.19 旁附主债；单文件极薄；上行已中文；rg 可勾销；TrMenu 可点验 | 未顺手清 NI 物品 lore | **推荐** |
| **A′. 改中文（备选）** | L67 → `§8余烬重铸石` | 保留灰字位 | 与 L66 叠床 | **不荐** |
| **B. NI 物品 lore 一行** | 上表 B1 | 同材 id | 物品岗；与 A 捆则双表面 | **旁附 soft · 勿双上** |

**不施工：** 开精英奖励预览子菜单壳；改 NI 物品 lore 裸 id（除非另批 B）；改拆解/重铸数值/掉落/体力；宣称 B0.1 已清；未批改 YAML；重开 B2.6–B2.19 / B-flex / B-anvil；墙钟/DPS；叫挑刺。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / loot / NI / DP / MM / `ember_disassemble.yml` **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `ember_disassemble.yml` 无字面 `mat_ember_reforge_stone`（玩家 lore）；L66「余烬重铸石」保留；其余 lore/name/tell/actions **语义不变**。  
3. **若批 A：** scrap / reforge / life / 体力 cost / NI 物品文件 **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改 NI 物品 lore（除非另批 B）；未开精英预览壳；**不**宣称 B0.1 / NI 物品 lore 裸 id 已清。  
5. **目视轻测：** `/ember` → 拆解 → 悬停重铸图标，无裸 `mat_ember_reforge_stone`；说明仍短清楚。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n 'mat_ember_reforge_stone' plugins/TrMenu/menus/ember_disassemble.yml
# 期望：0（玩家可见行）
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 拆解/重铸 | —（TrMenu） | 余烬 · 拆解 | hub → 拆解 | `ember_disassemble` | 返回 hub | **UI/文案可用** · 无新 NPC |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称拆解数值/掉落改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | `/ember` → 拆解 | 「余烬 · 拆解」 | 打开 `ember_disassemble` | 返回 hub |
| 2 | 悬停重铸 | 「消耗 余烬重铸石」；**无**裸 NI id | 只读展示 | — |
| 3 | 点击重铸 | tell 含「重铸石」人话 | 走既有 `corerpg reforge` | — |

---

## 7. UX 否决条

| 硬条 | 预期 |
|------|------|
| 玩家须手打指令才能看拆解说明 | **FAIL** |
| 施工后重铸 lore 仍出现字面 `mat_ember_reforge_stone` | **FAIL** |
| 借机改拆解/重铸数值 / 掉落 / 体力 / 开精英厚壳 | **FAIL** |
| 验收靠 wall-clock 或 DPS / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 | **FAIL** |
| 未批准即改 `ember_disassemble.yml` | **FAIL** |

---

## 8. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A（或不附 B） | **本窗**（文档 only） |
| **插件** | `ember_disassemble.yml` 删 L67（批 A 后） | **批 A 后** |
| **测试** | 静态 `rg` + 拆解菜单悬停轻测；**不叫挑刺**；禁 wall-clock/DPS | 施工后 |

**未批准前不施工**（含不改 `ember_disassemble.yml`）。

---

## 9. 明确未动 / 不做

- 精英奖励预览子菜单壳（证据不足勿硬开）  
- NI 物品文件 lore 内裸 id（B soft，勿与 A 双上）  
- 拆解掉落 / 重铸消耗逻辑 / scrap.yml / life.yml / 体力 / 掉率  
- B0.1 票扣显示名；四件甲；锻炉重做；誓约大改  
- B-flex-1/2 / B-anvil-1 已结物回改  
- 墙钟 / DPS / 挑刺口径  

---

## 10. Backlog 挂账

- 新号：**B2.20**（B2 文案可维护轨 · 与 B-flex / B-anvil **不捆**）  
- 软观察「拆解 `mat_ember_reforge_stone`」本窗主清；「NI 物品 lore 同 id」留 B soft；「精英预览壳」仍 soft、勿硬开  
- B2.19 PASS 勾销保持（close `b3a00b8`）；**勿宣称 B0.1 已清**

---

## 11. 回总控摘要

- **荐 A：** `ember_disassemble.yml` 删重铸 lore 1 行玩家可见裸 NI（上行已有「余烬重铸石」）；零改拆解数值/掉落/体力。  
- **路径：** `docs/design-ember-disassemble-reforge-copy.md` · **B2.20** · STATUS **待批 A**。  
- **证据：** L66 已中文 / L67 裸 id；升自 B2.19 旁附 B1；TrMenu 扫网仅余该行。  
- **B 旁附 soft：** NI 物品 lore `&7mat_ember_reforge_stone`；**勿双上**。精英壳勿硬开。  
- **未动：** 精英厚壳 / NI 物品 lore / 拆解数值·掉落·体力 / B0.1 / 四件甲·锻炉·誓约。  
- **验收专岗：** 插件删 1 行 → 测试 `rg`+拆解悬停；禁 wall-clock/DPS；不叫挑刺。  
- **勿宣称 B0.1 已清。** 与 B-flex / B-anvil 不捆。
