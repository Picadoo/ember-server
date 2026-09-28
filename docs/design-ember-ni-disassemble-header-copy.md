# 设计稿 · NI disassemble 文件头去命令教学（B2.54）

> **STATUS：已批 A（总控 · 2026-09-29 05:31 Asia/Shanghai）。**
> 本稿只定 `plugins/NeigeItems/Items/ember-disassemble.yml` **YAML 文件头 L3 注释** 1 行：去掉命令教学，改成功能摘要。
> **本窗只处理维护者可见注释（非玩家 UI）；新句不写斜杠、不写 STATUS 路径。**
> **本窗 commit 只写 docs；NI 玩法 YAML、物品 lore、数值、配方及其它 NI 文件零改**（批 A 后由**物品岗**只改 NI L3，非插件 TrMenu）。
> **禁**长测/挑刺；**勿宣称 B0.1 已清**；pets 旁记 soft 不捆；勿 push。
> 前序：B2.53 hub AFK 注释 **PASS · 勾销**（close `b265f0b`）；本窗升收 NI disassemble 文件头 L3。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI disassemble 文件头 L3 去命令教学，改功能摘要（UX · B2.54） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 05:30 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-disassemble.yml` · B2.53 PASS 旁证 |
| 状态 | **已批 A** · 交**物品岗（NI）**，非插件 TrMenu |
| tip 路径 | `docs/design-ember-ni-disassemble-header-copy.md` |
| 上游 | B2.53 close `b265f0b` · hub AFK 已清；升本窗收 NI disassemble L3 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 NI 施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------------|--------------------------------|
| `ember-disassemble.yml` **L3** 文件头注释 1 行 | 物品 lore、数值、配方、产出、消耗、逻辑 |
| 仅替换为荐案功能摘要 | L1/L2、其它 NI 文件、`ember-pets.yml` |
| 批后由**物品岗（NI）**施工 | TrMenu / 插件岗施工；git push；热更 / reload |
| | 长测/挑刺；开精英预览壳；宣称 B0.1 已清；在新注释写 `/corerpg`、插件岗待接或 STATUS 路径 |

---

## 1. 问题一句话

**NI 分解物品文件头 L3 仍把底层命令写法当作维护提示；这是维护者可见注释，不是玩家 UI，应改成分解产材与重铸用途的功能摘要。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 目标行与文件头（实际行号）

`plugins/NeigeItems/Items/ember-disassemble.yml` 当前 live 文件头：

```yaml
L1: # 分解 / 重铸材料 — DESIGN-ember-rpg-systems.md §3.4 · STATUS-ember-disassemble.md
L2: # 产出：分解多余刃/护符、精英词缀残页、周本/深渊副产；不进地窟 MM 掉落表
L3: # 命令（插件岗待接）：/corerpg scrap · /corerpg reforge
L4:
```

L3 是唯一待替换行；L1/L2 与空行保持原样。其余正文中的物品 lore、数值、配方、产出与逻辑均不在本窗施工。

### 2.2 物品内容零改基线

本窗只交文档与 backlog；目标 NI 文件批前 `git diff` 为空。验收时需确认：

- `ember-disassemble.yml` lore 全部原样；
- 数值、配方、产出、消耗及物品逻辑全部原样；
- 其它 NI 文件全部零 diff；
- 本窗不触碰 `ember-pets.yml`。

---

## 3. 荐案（明确推荐）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 功能摘要（推荐）** | L3 改为“分解多余刃、护符等产出材料；材料用于重铸” | 保留分解→材料→重铸的用途闭环；短；无命令教学 | 不再记录底层命令写法 | **推荐** |

### 3.1 旧 / 新 L3 全文对照

| # | 位置 | 旧（全文） | 新（荐 · 全文） |
|---|------|------------|-----------------|
| A1 | `ember-disassemble.yml` **L3** | `# 命令（插件岗待接）：/corerpg scrap · /corerpg reforge` | `# 功能摘要：分解多余刃、护符等产出材料；材料用于重铸` |

**口径备忘：** 批 A 后只替换上表 L3 这一行注释；L1/L2、空行、物品 lore、数值、配方、产出、消耗及其它 NI 文件 **一字不动**。荐案只写功能摘要，**无斜杠、无插件岗待接、无 STATUS 路径、无命令教学**。

**非目标 / 不施工：** 改 `ember-disassemble.yml` lore、数值、配方、产出、消耗或逻辑；改 L1/L2；改 `ember-pets.yml`；改其它 NI / TrMenu / CoreRpg / 玩法 YAML；把施工交插件 TrMenu；开精英壳；宣称 B0.1 已清；长测/挑刺；推送远端。

---

## 4. 验收（≤5，批 A 后另开施工）

1. **本设计 commit：** 仅本稿 + backlog；`plugins/NeigeItems/Items/ember-disassemble.yml` 与其它玩法 YAML / NI **零 diff**。
2. **若批 A：** 仅 `ember-disassemble.yml` L3 替换为荐案；新句是分解与重铸材料用途摘要，**无斜杠、无插件岗待接、无 STATUS 路径**。
3. **若批 A：** L1/L2、空行、物品 lore、数值、配方、产出、消耗、逻辑及其它 NI 文件相对批前零 diff。
4. **若批 A · 专岗：** 由**物品岗（NI）**施工，**不是插件 TrMenu**；`ember-pets.yml` 文件头「待 CoreRpg」旁记仍不捆。
5. **禁长测/挑刺**（维护者注释非玩家 UI · 静态 diff / rg 即可）；**勿宣称 B0.1 已清**。

预期施工后静态检查（本设计窗不执行改动）：

```bash
awk 'NR <= 3 { print NR ":" $0 }' plugins/NeigeItems/Items/ember-disassemble.yml
# 仅 L3 替换为功能摘要；L1/L2 保持原样

git diff -- plugins/NeigeItems/Items/ember-disassemble.yml
# 仅显示 L3 1 行注释
```

---

## 5. 旁记 soft（不捆）

| # | 文件 / 位置 | 现况 | 玩家可见？ | 本轮 |
|---|-------------|------|------------|------|
| 1 | `plugins/NeigeItems/Items/ember-pets.yml` 文件头 | 「待 CoreRpg」模块旁记（当前为出战逻辑待 CoreRpg pet 模块） | 否 · 注释 | **不捆** |

旁记 soft 不纳入 B2.54 施工与验收；不得顺手改 pets 文件头。

---

## 6. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|------|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **物品** | 批 A 后仅替换 NI `ember-disassemble.yml` L3 注释 | 批 A 后 |
| **插件 TrMenu** | 不施工本项 | 不适用 |
| **测试** | 批后静态 diff / rg；不测玩法，不叫挑刺 | 施工后 |

本项只处理维护者可见 NI 文件头注释，不新增玩家命令教学；不改玩家物品 lore 或任何玩法数据。

---

## 7. 回总控摘要

- **STATUS：待批 A** · tip `docs/design-ember-ni-disassemble-header-copy.md`
- **荐案：** `ember-disassemble.yml` **L3** → `# 功能摘要：分解多余刃、护符等产出材料；材料用于重铸`
- **旧/新已全文对照：** 仅 L3 1 行；L1/L2、lore、数值、配方、产出、消耗、逻辑及其它 NI **零改**
- **批后施工岗：** **物品岗（NI）**，**非插件 TrMenu**
- **旁记 soft 不捆：** `ember-pets.yml` 文件头「待 CoreRpg」
- **本窗零改：** `ember-disassemble.yml` / `ember-pets.yml` / TrMenu / CoreRpg / 玩法 YAML **未动**
- **勿 push**；禁长测/挑刺；**勿宣称 B0.1 已清**
- **前序确认：** B2.53 hub AFK 注释 **PASS · 勾销**（close `b265f0b`）

---

## 8. 总控批示

- [x] **批 A** · 仅 `ember-disassemble.yml` L3 按上表荐案改功能摘要；物品内容、其它 NI、玩法零改
- [ ] **驳回** · 说明

**批示摘要：** 批 A。仅 NI `ember-disassemble.yml` L3 改功能摘要；lore/数值/配方零改；pets 不捆；施工交物品岗；禁长测/挑刺；勿宣称 B0.1。
