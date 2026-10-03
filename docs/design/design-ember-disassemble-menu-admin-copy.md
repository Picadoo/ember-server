# 设计稿 · 拆解菜单管理注释人话化（B2.46）

> **STATUS：已批 A（总控 · 2026-09-29 04:47 Asia/Shanghai）。**  
> 本稿只定 TrMenu `ember_disassemble.yml` **规则速览** 玩家 lore **1** 行：去掉「docs/status/STATUS-ember-disassemble.md」管理路径，改玩家人话。  
> **本窗 commit 只写 docs；玩法 / TrMenu / NI YAML 零改**（批后由专岗改 TrMenu）。  
> **禁**改拆解/重铸消耗与逻辑、体力门、四件甲、锻炉；禁长测/挑刺；**勿宣称 B0.1 已清**；精英壳不捆。  
> 前序：B-flex-4 PASS · 勾销 `6b0ce3d`（插件 `005a03b` · 测 `a1d5f7c` · CoreRpg **1.15.28**）；灵活三窗挑刺软债本轨清。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 拆解菜单 · **规则速览管理注释人话化**（UX · B2.46） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 04:46 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_disassemble.yml` · B2.20 PASS 旁附 soft |
| 状态 | **已批 A** · 交插件改 TrMenu |
| tip 路径 | `docs/design/design-ember-disassemble-menu-admin-copy.md` |
| 上游 | B-flex-4 close `6b0ce3d` · 灵活三窗挑刺本轨清 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 TrMenu 施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------------|--------------------------------|
| `ember_disassemble.yml` 规则速览 I 图标 **玩家 lore 1 行**（L89） | 拆解/重铸数值 / scrap·reforge command / 掉落 / 体力 cost |
| 热更 / TrMenu reload（服约定） | NI 物品 YAML 文件头；其它 TrMenu 页；loot / DP / MM / CoreRpg |
| | git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML；四件甲/锻炉 |

---

## 1. 问题一句话

**规则速览悬停末行印 `§8详见 docs/status/STATUS-ember-disassemble.md`——管理文档路径漏进玩家 UI；左侧分解/重铸按钮已人话，本行与之脱节。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 入口

枢纽 `/ember` → hub → 拆解/重铸 → `ember_disassemble`（TrMenu 点击）

### 2.2 规则速览 I（实际行号 · 本窗唯一目标）

| 行 | 现句 | 可见性 |
|----|------|--------|
| L81 | `name: '§e规则速览'` | 玩家 |
| L84 | `§7分解：多余刃 / 护符 → 碎片 / 骨尘` | 玩家 |
| L85 | `§7低概率孔石（锋利 / 稳固 / 汲取 / 疾风）` | 玩家 |
| L86 | `§7重铸：刷次要词缀，主词缀不乱飙` | 玩家 |
| L87 | `§7消耗重铸石 · 不耗核心` | 玩家 |
| L88 | `''`（空行） | — |
| **L89** | **`§8详见 docs/status/STATUS-ember-disassemble.md`** | **玩家 · 脏点** |
| L93–L94 | tell：规则摘要 +「点左侧按钮即可分解或重铸，无需手打命令」 | 点击才发 |

**扫网：**

```bash
rg -n "STATUS-ember-disassemble|详见" plugins/TrMenu/menus/ember_disassemble.yml
# 现网：L1（YAML 文件头注释 · 非玩家 UI）· L89（玩家 lore · 本窗目标）
```

### 2.3 左侧按钮语义（对照 · 勿改）

| 图标 | 行 | 要点 |
|------|----|------|
| S 分解 | L51 | `§7➥ §f点击分解` |
| R 重铸 | L70 | `§6➥ §f点击重铸` |
| I 点击 tell | L94 | 已写「点左侧按钮即可…」——与荐句同向 |

### 2.4 旁附只列证（本窗不做）

| 位置 | 现况 | 本窗 |
|------|------|------|
| YAML L1 `# … docs/status/STATUS-ember-disassemble.md` | 文件头管理注释 · **非**玩家 lore | 旁记 soft · **勿当主目标** |
| Open tell L22 `逻辑待 CoreRpg 接线` | 开菜单 chat 含管理口吻 | 旁证 · **本窗不推** |
| actions `command: corerpg scrap/reforge` | 点击执行 · 非悬停 lore | **不动**（硬禁逻辑） |
| NI `ember-disassemble.yml` L1 文件头含 STATUS / DESIGN / `/corerpg` | 管理注释 · 另一轨 | **旁记 soft · 勿当主目标**（总控点 TrMenu） |

玩家可见 lore 内字面 `STATUS-ember-disassemble` / `详见`：**仅 L89**。

---

## 3. 荐案（明确推荐其一）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 人话替换（推荐）** | L89 → `§8点左侧即可分解或重铸` | 短、指导点击；与 S/R CTA 及 L94 tell 语义一致；去文档名；悬停即见 CTA | 多留 1 行灰字 | **推荐** |
| A′. 删该行 | 删 L89（可保留 L88 空行或一并收） | 更薄；L84–87 规则已够 | 悬停无收尾 CTA；点击才见 L94 | **不荐**（备选同档） |

**推荐句（批 A 后专岗改 TrMenu）：**

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | 规则速览 lore **L89** | `§8详见 docs/status/STATUS-ember-disassemble.md` | `§8点左侧即可分解或重铸` |

**口径备忘：** 只动上表 1 句玩家可见字符串；`name:` / L84–87 / tell / S·R lore / `actions` 里 sound·command·close **一字不动**；拆解掉落与重铸消耗逻辑 **零改**；YAML L1 / NI 文件头 **本窗不改**。

**不施工：** 改拆解/重铸数值/体力；改 NI 文件头；开精英壳；宣称 B0.1 已清；未批改 YAML；长测/挑刺；四件甲/锻炉；捆灵活窗已结项。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿 + backlog；玩法 YAML / loot / NI / DP / MM / `ember_disassemble.yml` **零 diff**。  
2. **若批 A：** 该菜单玩家 lore 无字面 `STATUS-ember-disassemble`；L89 为人话点击指导（或若改批删行则无该行）；L84–87 / S·R CTA / tell **语义不变**。  
3. **若批 A：** scrap / reforge / 体力 / NI 物品文件 **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改 NI 文件头（除非另批）；未开精英壳；**不**宣称 B0.1 已清。  
5. **目视轻测：** `/ember` → 拆解 → 悬停「规则速览」，无 docs/status/STATUS.md / 内部文档名；说明短清楚。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n 'STATUS-ember-disassemble' plugins/TrMenu/menus/ember_disassemble.yml
# 期望：玩家 lore = 0（L89 已无人话路径；L1 YAML 注释可仍在 · 非玩家 UI）
# 更严（若批 A 且只动 lore）：
rg -n "STATUS-ember-disassemble|详见" plugins/TrMenu/menus/ember_disassemble.yml
# 期望：玩家 lore 段无命中；最多剩 L1 文件头注释
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
| 2 | 悬停规则速览 | 规则摘要 +「点左侧即可分解或重铸」；**无** docs/status/STATUS.md | 只读展示 | — |
| 3 | 点左侧分解/重铸 | 既有 CTA / tell | 走既有 scrap / reforge | — |

---

## 7. 回总控摘要

- **STATUS：已批 A** · tip `docs/design/design-ember-disassemble-menu-admin-copy.md`
- **荐案：人话替换 L89** → `§8点左侧即可分解或重铸`（删行备选不荐）
- **实际行号：** 脏点 **L89**；对照 L84–87 / L94
- **本窗零改：** TrMenu / NI / CoreRpg / 玩法 YAML **未动**
- **旁记 soft：** YAML L1 文件头；NI `ember-disassemble.yml` 文件头（另一轨）；Open tell L22 管理口吻
- **勿 push**；批后专岗改 TrMenu · 禁长测/挑刺 · **勿宣称 B0.1 已清**

---

## 8. 总控批示

- [x] **批 A** · L89 人话替换为 `§8点左侧即可分解或重铸`（总控 · tip 本提交）
- [ ] **驳回** · 说明

**批示摘要：** 采纳荐句；**勿**删行优先于替换；YAML L1 / NI 文件头 / Open tell L22 本窗不推（旁记 soft）。交 **余烬-插件** 只改 `ember_disassemble.yml` L89。禁长测/挑刺；勿宣称 B0.1 已清。验收：玩家 lore `STATUS-ember-disassemble`=0 + 悬停轻测。
