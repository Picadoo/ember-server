# 设计稿 · NI pets 文件头去管理口吻（B2.55）

> **STATUS：待批 A（总控 · 2026-09-29 05:34 Asia/Shanghai）。**
> 本稿只定 `plugins/NeigeItems/Items/ember-pets.yml` **YAML 文件头 L1 注释** 1 行：去掉模块待接的管理口吻，改成功能摘要。
> **本窗只处理维护者可见注释（非玩家 UI）；新句不写「待 CoreRpg」、不写斜杠、不写 STATUS 路径。**
> **本窗 commit 只写 docs；NI 玩法 YAML、物品 lore、数值、配方、产出、消耗及其它 NI 文件零改**（批 A 后由**物品岗（NI）**只改 L1，非插件 TrMenu）。
> **禁**长测/挑刺；**勿宣称 B0.1 已清**；勿顺手改 L2 或物品内容；勿 push。
> 前序：B2.54 NI disassemble 文件头 **PASS · 勾销**（close `ee02ed6`）；本轨旁记 soft 收口，本窗收 pets。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | NI pets 文件头 L1 去管理口吻，改功能摘要（UX · B2.55） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 05:34 Asia/Shanghai |
| 关联 | live `plugins/NeigeItems/Items/ember-pets.yml` · B2.54 close `ee02ed6` |
| 状态 | **待批 A** · 批后交**物品岗（NI）**，非插件 TrMenu |
| tip 路径 | `docs/design-ember-ni-pets-header-copy.md` |
| 上游 | B2.54 close `ee02ed6` · disassemble 已清；本窗收 pets 文件头旁记 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 NI 施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------------|--------------------------------|
| `ember-pets.yml` **L1** 文件头注释 1 行 | L2、物品 lore、数值、配方、产出、消耗、逻辑 |
| 仅替换为荐案功能摘要 | 其它 NI 文件、其它玩法 YAML、TrMenu、CoreRpg |
| 批后由**物品岗（NI）**施工 | 插件 TrMenu 施工；git push；热更 / reload |
| | 长测/挑刺；开精英预览壳；宣称 B0.1 已清 |

---

## 1. 问题一句话

**NI pets 文件头 L1 仍写模块待接信息；这是维护者可见注释，不是玩家 UI，应改成使魔蛋外观收集与枢纽使魔用途的功能摘要。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 目标行与文件头

`plugins/NeigeItems/Items/ember-pets.yml` 当前 live 文件头：

```yaml
L1: # 使魔蛋 stub — 外观收集；出战逻辑待 CoreRpg pet 模块
L2: # DESIGN-ember-pet-bestiary.md · 偏外观，属性≤战力 5%；不卖满级战力宠
```

L1 是唯一待替换行；L2 保持原样。正文中的使魔蛋物品 lore、魂尘 lore、数值、配方、产出、消耗与逻辑均不在本窗施工。

### 2.2 物品内容零改基线

本窗只交文档与 backlog；目标 NI 文件批前 `git diff` 为空。验收时需确认：

- `ember-pets.yml` L2 原样；
- 使魔蛋与魂尘 lore 全部原样；
- 数值、配方、产出、消耗及物品逻辑全部原样；
- 其它 NI 文件全部零 diff；
- 本窗不施工 `ember-pets.yml`，仅交批 A tip。

---

## 3. 荐案（明确推荐）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 功能摘要（推荐）** | L1 改为“外观收集使魔蛋；用于枢纽使魔” | 保留物品定位与用途；短；去掉模块待接管理口吻 | 不再记录底层模块状态 | **推荐** |

### 3.1 旧 / 新 L1 全文对照

| # | 位置 | 旧（全文） | 新（荐 · 全文） |
|---|------|------------|-----------------|
| A1 | `ember-pets.yml` **L1** | `# 使魔蛋 stub — 外观收集；出战逻辑待 CoreRpg pet 模块` | `# 功能摘要：外观收集使魔蛋；用于枢纽使魔` |

**口径备忘：** 批 A 后只替换上表 L1 这一行注释；L2、空行、物品 lore、数值、配方、产出、消耗、逻辑及其它 NI 文件 **一字不动**。荐案只写功能摘要，**不写「待 CoreRpg」、不写斜杠、不写 STATUS 路径**。

**非目标 / 不施工：** 改 `ember-pets.yml` lore、数值、配方、产出、消耗或逻辑；改 L2；改其它 NI / TrMenu / CoreRpg / 玩法 YAML；把施工交插件 TrMenu；开精英壳；宣称 B0.1 已清；长测/挑刺；推送远端。

---

## 4. 验收（≤5，批 A 后另开施工）

1. **本设计 commit：** 仅本稿 + backlog；`plugins/NeigeItems/Items/ember-pets.yml` 与其它玩法 YAML / NI **零 diff**。
2. **若批 A：** 仅 `ember-pets.yml` L1 替换为荐案；新句是外观收集与枢纽使魔用途摘要，**不写「待 CoreRpg」、不写斜杠、不写 STATUS 路径**。
3. **若批 A：** L2、空行、物品 lore、数值、配方、产出、消耗、逻辑及其它 NI 文件相对批前零 diff。
4. **若批 A · 专岗：** 由**物品岗（NI）**施工，**不是插件 TrMenu**；B2.54 disassemble 已 PASS，本窗 pets soft 收口。
5. **禁长测/挑刺**（维护者注释非玩家 UI · 静态 diff / rg 即可）；**勿宣称 B0.1 已清**。

预期施工后静态检查（本设计窗不执行改动）：

```bash
awk 'NR <= 2 { print NR ":" $0 }' plugins/NeigeItems/Items/ember-pets.yml
# 仅 L1 替换为功能摘要；L2 保持原样

git diff -- plugins/NeigeItems/Items/ember-pets.yml
# 仅显示 L1 1 行注释
```

---

## 5. 旁记 soft 收口

| # | 文件 / 位置 | 现况 | 玩家可见？ | 本轮 |
|---|-------------|------|------------|------|
| 1 | `ember-disassemble.yml` 文件头 L3 | B2.54 去命令教学 | 否 · 注释 | **PASS · 勾销**（close `ee02ed6`） |
| 2 | `ember-pets.yml` 文件头 L1 | 「待 CoreRpg」模块旁记 | 否 · 注释 | **本窗收 pets · 待批 A** |

B2.54 disassemble 已清；本窗只收 pets 文件头旁记，不扩到玩家 UI、玩法逻辑或其它软债。

---

## 6. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|------|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **物品** | 批 A 后仅替换 NI `ember-pets.yml` L1 注释 | 批 A 后 |
| **插件 TrMenu** | 不施工本项 | 不适用 |
| **测试** | 批后静态 diff / rg；不测玩法，不叫挑刺 | 施工后 |

本项只处理维护者可见 NI 文件头注释，不新增玩家命令教学；不改玩家物品 lore 或任何玩法数据。

---

## 7. 回总控摘要

- **STATUS：待批 A** · tip `docs/design-ember-ni-pets-header-copy.md`
- **荐案：** `ember-pets.yml` **L1** → `# 功能摘要：外观收集使魔蛋；用于枢纽使魔`
- **旧/新已全文对照：** 仅 L1 1 行；L2、lore、数值、配方、产出、消耗、逻辑及其它 NI **零改**
- **批后施工岗：** **物品岗（NI）**，**非插件 TrMenu**
- **旁记 soft 收口：** B2.54 NI disassemble 已 PASS · 勾销（close `ee02ed6`）；本窗收 pets
- **本窗零改：** `ember-pets.yml` / 其它 NI / TrMenu / CoreRpg / 玩法 YAML **未动**
- **勿 push**；禁长测/挑刺；**勿宣称 B0.1 已清**

---

## 8. 总控批示

- [ ] **批 A** · 仅 `ember-pets.yml` L1 按上表荐案改功能摘要；L2/lore/数值/其它 NI 零改
- [ ] **驳回** · 说明
