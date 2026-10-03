# 设计稿 · arena/settings/set 文件头管理口吻（B2.50）

> **STATUS：已批 A（总控 · 2026-09-29 05:10 Asia/Shanghai）。**  
> 本稿只定 TrMenu `ember_arena.yml` / `ember_settings.yml` / `ember_set.yml` **YAML 文件头 L2 注释**各 1 行：去掉「逻辑待* / 待 CoreRpg」管理尾句（含仓库路径），保留功能摘要。  
> **对齐 B2.49：纯文件头注释（非玩家 UI / 非 Open tell）；本窗收同族 soft×3。**  
> **本窗 commit 只写 docs；玩法 / TrMenu / NI YAML 零改**（批后由专岗改 TrMenu）。  
> **禁**改 Icons / Open / 其它菜单；禁长测/挑刺；**勿宣称 B0.1 已清**；精英壳不捆。  
> **不捆本窗：** 其它菜单 / NI / CoreRpg 玩法旋钮。  
> 前序：B2.49 PASS · 勾销 `cc15352`（设计 `d9c5f02` · 批准 `c79e480` · 插件 `7af95b6` · 测 `4f52d81`）；friends/mail 文件头已清 · **旁证 · 勿回改**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | arena/settings/set · **文件头注释管理口吻人话化**（UX · B2.50） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 05:09 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_arena.yml` · `ember_settings.yml` · `ember_set.yml` · B2.49 PASS 旁证 |
| 状态 | **已批 A** · 交插件 TrMenu |
| tip 路径 | `docs/design/design-ember-shell-menu-header-copy.md` |
| 上游 | B2.49 close `cc15352` · friends/mail 已清；升本窗收 soft×3 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 TrMenu 施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------------|--------------------------------|
| `ember_arena.yml` **L2** 文件头注释 1 行 | Icons / Open Events / 其它 actions；Title / Layout |
| `ember_settings.yml` **L2** 文件头注释 1 行 | 其它 TrMenu 页（friends/mail/guild/disassemble）；NI / loot / DP / MM / CoreRpg |
| `ember_set.yml` **L2** 文件头注释 1 行 | git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML；教手打命令；写 STATUS / 仓库路径 |

---

## 1. 问题一句话

**arena / settings / set 菜单 YAML 文件头 L2 仍挂「逻辑待插件岗 / 逻辑待接线 / 待 CoreRpg（含路径）」——管理口吻残留在注释（非玩家 UI）；功能摘要本身可用，管理半句与路径应删。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 目标三行（实际行号）

| 文件 | 行 | 现句（全文） | 可见性 |
|------|----|--------------|--------|
| `ember_arena.yml` | **L2** | `# 1v1 / 2v2 排队 tell；积分赛季 · 奖币与外观；逻辑待插件岗` | 否 · YAML 注释 |
| `ember_settings.yml` | **L2** | `# 音效 / 提示 / 隐私；低优先级，逻辑待接线` | 否 · YAML 注释 |
| `ember_set.yml` | **L2** | `# 背包持有即计件；击杀回能待 CoreRpg（plugins/CoreRpg/set.yml 旋钮）` | 否 · YAML 注释 |

**文件头 1–5 行确认：**

```
# ember_arena.yml
L1 # 竞技子菜单壳 — docs/design/DESIGN-ember-arena-auction.md §1
L2 # 1v1 / 2v2 排队 tell；积分赛季 · 奖币与外观；逻辑待插件岗
L3 （空）
L4 Title: '§c余烬 · 竞技'

# ember_settings.yml
L1 # 设置子菜单壳 — docs/design/DESIGN-ember-mail-friends.md §3
L2 # 音效 / 提示 / 隐私；低优先级，逻辑待接线
L3 （空）
L4 Title: '§7余烬 · 设置'

# ember_set.yml
L1 # 套装 · 余烬同袍 2 件（戒+刃）— docs/ember-raid-channel-spec.md
L2 # 背包持有即计件；击杀回能待 CoreRpg（plugins/CoreRpg/set.yml 旋钮）
L3 （空）
L4 Title: '§9余烬 · 套装'
```

**扫网（精确族）：**

```bash
rg -n "逻辑待|待 CoreRpg" plugins/TrMenu/menus/
# 现网命中 3：arena L2 + settings L2 + set L2（本窗目标 soft×3）
# friends/mail 已由 B2.49 清 · 0 命中
```

### 2.2 B2.49 旁证（施工已落地 · 本窗勿回改）

| 文件 | 现况 | 备注 |
|------|------|------|
| `ember_friends.yml` L2 | `# 列表 / 申请 / 组队邀请 / 师徒（可选）` | B2.49 PASS · 旁证 |
| `ember_mail.yml` L2 | `# 系统邮件 / 战令 / 维护补偿` | B2.49 PASS · 旁证 |

### 2.3 旁附 soft（本窗即收 · 计数 **3**）

同族文件头管理口吻；本窗 **捆收** 下列 3 条（B2.49 已旁记 · 现升窗）：

| # | 文件 | 行 | 现况 | 玩家可见？ |
|---|------|----|------|------------|
| 1 | `ember_arena.yml` | L2 | `# …；逻辑待插件岗` | 否 · 注释 |
| 2 | `ember_settings.yml` | L2 | `# …；低优先级，逻辑待接线` | 否 · 注释 |
| 3 | `ember_set.yml` | L2 | `# …；击杀回能待 CoreRpg（…）` | 否 · 注释 |

---

## 3. 荐案（明确推荐其一）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 各删管理尾（推荐）** | 三行各去管理半句/路径；settings 一并删「低优先级」；set 保留「击杀回能另计」功能意、**勿**写路径/STATUS | 最短；去管理词；注释仍可读 | 无 | **推荐** |
| A′. set 只留「背包持有即计件」 | 更短 | 丢击杀回能摘要 | 备选 |
| A″. settings 保留「低优先级」 | 留优先级备忘 | 仍偏管理口吻 | 不荐 |

**推荐句（批 A 后专岗改 TrMenu · 新旧全文）：**

| # | 位置 | 旧（全文） | 新（荐 · 全文） |
|---|------|------------|-----------------|
| A1 | `ember_arena.yml` **L2** | `# 1v1 / 2v2 排队 tell；积分赛季 · 奖币与外观；逻辑待插件岗` | `# 1v1 / 2v2 排队 tell；积分赛季 · 奖币与外观` |
| A2 | `ember_settings.yml` **L2** | `# 音效 / 提示 / 隐私；低优先级，逻辑待接线` | `# 音效 / 提示 / 隐私` |
| A3 | `ember_set.yml` **L2** | `# 背包持有即计件；击杀回能待 CoreRpg（plugins/CoreRpg/set.yml 旋钮）` | `# 背包持有即计件；击杀回能另计` |

**口径备忘：** 只动上表 3 行 L2 注释；Icons / Open / Title / Layout / 其它菜单 **一字不动**；**勿**写入 `/corerpg`、**勿**写入 STATUS / 仓库路径、**勿**教命令；friends/mail **本窗不回改**。

**非目标 / 不施工：** 改 Icons / Open；开精英壳；宣称 B0.1 已清；未批改 YAML；长测/挑刺；教手打命令；回改 B2.49 friends/mail；改 `plugins/CoreRpg/set.yml` 玩法旋钮。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿 + backlog；玩法 YAML / loot / NI / DP / MM / 三目标 yml **零 diff**。  
2. **若批 A：** 三文件静态 `rg` **无**「逻辑待」「待 CoreRpg」；功能摘要保留；set **无**仓库路径 / STATUS；**无**教命令。  
3. **若批 A：** Icons / Open / 其它菜单 **相对批前零 diff**。  
4. **若批 A · 禁项：** 未开精英壳；**不**宣称 B0.1 已清；未回改 friends/mail。  
5. **禁长测/挑刺**（注释非玩家 UI · 静态 rg 即可）。

静态 rg（施工后）：

```bash
rg -n '逻辑待|待 CoreRpg' plugins/TrMenu/menus/
# 期望：0（friends/mail 已清；本窗三文件亦清）
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 竞技 | —（TrMenu） | 余烬 · 竞技 | hub → 竞技 | `ember_arena` | 返回 hub | **注释文案** · 无新 NPC |
| 设置 | —（TrMenu） | 余烬 · 设置 | hub → 设置 | `ember_settings` | 返回 hub | **注释文案** · 无新 NPC |
| 套装 | —（TrMenu） | 余烬 · 套装 | hub → 套装 | `ember_set` | 返回 hub | **注释文案** · 无新 NPC |

**站岗自检：** 无新 NPC；仅文件头注释 → 最高标「维护可读」，不宣称竞技/设置/套装逻辑接线改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | （维护者读 YAML） | 文件头 L2 无「逻辑待* / 待 CoreRpg」 | 注释摘要仍可读 | — |
| 2 | 玩家开菜单 | **不变**（本窗不改玩家 UI） | Icons / Open 原样 | — |

---

## 7. 回总控摘要

- **STATUS：待批 A** · tip `docs/design/design-ember-shell-menu-header-copy.md`
- **荐案：**
  - arena **L2** → `# 1v1 / 2v2 排队 tell；积分赛季 · 奖币与外观`
  - settings **L2** → `# 音效 / 提示 / 隐私`
  - set **L2** → `# 背包持有即计件；击杀回能另计`
- **实际行号：** 脏点各 **L2**；Icons / Open **勿改**
- **本窗收 soft×3**（arena · settings · set）；friends/mail 已清旁证
- **本窗零改：** TrMenu / NI / CoreRpg / 玩法 YAML **未动**
- **勿 push**；批后专岗改 TrMenu · 禁长测/挑刺 · **勿宣称 B0.1 已清**
- **前序确认：** B2.49 PASS · 勾销 `cc15352`（设计 `d9c5f02`）已入仓

---

## 8. 总控批示

- [x] **批 A** · 三文件 L2 按上表荐案去管理尾（settings 去「低优先级」；set 用「击杀回能另计」· 勿写路径）
- [ ] **驳回** · 说明

**批示摘要（待填）：** —
