# 设计稿 · friends/mail 文件头管理口吻（B2.49）

> **STATUS：已批 A（总控 · 2026-09-29 05:06 Asia/Shanghai）。**  
> 本稿只定 TrMenu `ember_friends.yml` / `ember_mail.yml` **YAML 文件头 L2 注释**各 1 行：去掉尾「；逻辑待 CoreRpg 接线」，保留功能摘要。  
> **比 B2.48 更薄：纯文件头注释（非玩家 UI / 非 Open tell）。**  
> **本窗 commit 只写 docs；玩法 / TrMenu / NI YAML 零改**（批后由专岗改 TrMenu）。  
> **禁**改 Icons / Open / 其它菜单；禁长测/挑刺；**勿宣称 B0.1 已清**；精英壳不捆。  
> **不捆本窗：** arena / settings / set 文件头同族旁记 soft。  
> 前序：B2.48 PASS · 勾销 `800aa05`（设计 `6b0e743` · 批准 `811dc78` · 插件 `cd4f6c0` · 测 `8d3f3cf`）；公会 Open tell 已清 · **旁证 · 勿回改**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | friends/mail · **文件头注释管理口吻人话化**（UX · B2.49） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 05:05 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_friends.yml` · `ember_mail.yml` · B2.48 PASS 旁证 |
| 状态 | **已批 A** · 交插件 TrMenu |
| tip 路径 | `docs/design/design-ember-friends-mail-header-copy.md` |
| 上游 | B2.48 close `800aa05` · 公会 Open tell 已清；升本窗 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 TrMenu 施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------------|--------------------------------|
| `ember_friends.yml` **L2** 文件头注释 1 行 | Icons / Open Events / 其它 actions；Title / Layout |
| `ember_mail.yml` **L2** 文件头注释 1 行 | 其它 TrMenu 页（arena/settings/set/guild/disassemble）；NI / loot / DP / MM / CoreRpg |
| 热更 / TrMenu reload（服约定） | git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML；教手打命令；写 STATUS 路径 |

---

## 1. 问题一句话

**friends / mail 菜单 YAML 文件头 L2 仍挂「逻辑待 CoreRpg 接线」——管理口吻残留在注释（非玩家 UI）；功能摘要本身可用，尾半句应删。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 目标两行（实际行号）

| 文件 | 行 | 现句（全文） | 可见性 |
|------|----|--------------|--------|
| `ember_friends.yml` | **L2** | `# 列表 / 申请 / 组队邀请 / 师徒（可选）；逻辑待 CoreRpg 接线` | 否 · YAML 注释 |
| `ember_mail.yml` | **L2** | `# 系统邮件 / 战令 / 维护补偿；逻辑待 CoreRpg 接线` | 否 · YAML 注释 |

**文件头 1–5 行确认：**

```
# ember_friends.yml
L1 # 好友子菜单壳 — docs/design/DESIGN-ember-mail-friends.md
L2 # 列表 / 申请 / 组队邀请 / 师徒（可选）；逻辑待 CoreRpg 接线
L3 （空）
L4 Title: '§a余烬 · 好友'

# ember_mail.yml
L1 # 邮寄子菜单壳 — docs/design/DESIGN-ember-mail-friends.md
L2 # 系统邮件 / 战令 / 维护补偿；逻辑待 CoreRpg 接线
L3 （空）
L4 Title: '§f余烬 · 邮寄'
```

**扫网（精确字面）：**

```bash
rg -n "逻辑待 CoreRpg" plugins/TrMenu/menus/
# 现网命中 2：friends L2 + mail L2（本窗目标）
# （guild Open tell 已由 B2.48 清 · 0 命中）
```

### 2.2 B2.48 旁证（施工已落地 · 本窗勿回改）

| 文件 | 现况 | 备注 |
|------|------|------|
| `ember_guild.yml` Open tell | 无「逻辑待 CoreRpg」 | B2.48 PASS · 旁证 |

### 2.3 旁附 soft 列表（只列证 · 本窗不捆 · 计数 **3**）

同族文件头「待接线 / 待插件 / 待 CoreRpg」旁记；**字面「逻辑待 CoreRpg」仅 friends+mail**，下列为同族 soft：

| # | 文件 | 行 | 现况 | 玩家可见？ |
|---|------|----|------|------------|
| 1 | `ember_arena.yml` | L2 | `# …；逻辑待插件岗` | 否 · 注释 |
| 2 | `ember_settings.yml` | L2 | `# …；低优先级，逻辑待接线` | 否 · 注释 |
| 3 | `ember_set.yml` | L2 | `# …；击杀回能待 CoreRpg（…）` | 否 · 注释 |

```bash
rg -n "逻辑待 CoreRpg" plugins/TrMenu/menus/
# 精确字面：仅 friends L2 + mail L2（本窗）；arena/settings/set 用不同措辞 · soft 不捆
```

---

## 3. 荐案（明确推荐其一）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 各删尾半句（推荐）** | 两行各删「；逻辑待 CoreRpg 接线」，保留功能摘要 | 最短；去管理词；注释仍可读 | 无 | **推荐** |
| A′. 等价更短摘要 | 再压缩功能词 | 更短 | 可能丢信息 | 备选 |

**推荐句（批 A 后专岗改 TrMenu · 新旧全文）：**

| # | 位置 | 旧（全文） | 新（荐 · 全文） |
|---|------|------------|-----------------|
| A1 | `ember_friends.yml` **L2** | `# 列表 / 申请 / 组队邀请 / 师徒（可选）；逻辑待 CoreRpg 接线` | `# 列表 / 申请 / 组队邀请 / 师徒（可选）` |
| A2 | `ember_mail.yml` **L2** | `# 系统邮件 / 战令 / 维护补偿；逻辑待 CoreRpg 接线` | `# 系统邮件 / 战令 / 维护补偿` |

**口径备忘：** 只动上表 2 行 L2 注释；Icons / Open / Title / Layout / 其它菜单 **一字不动**；**勿**写入 `/corerpg`、**勿**写入 STATUS 路径、**勿**教命令；arena/settings/set **本窗不改**。

**非目标 / 不施工：** 改 Icons / Open；捆 arena/settings/set；开精英壳；宣称 B0.1 已清；未批改 YAML；长测/挑刺；教手打命令；回改 B2.48 公会 Open tell。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿 + backlog；玩法 YAML / loot / NI / DP / MM / 两目标 yml **零 diff**。  
2. **若批 A：** 两文件静态 `rg` **无**字面「逻辑待 CoreRpg」；功能摘要保留；**无** STATUS 路径、**无**教命令。  
3. **若批 A：** Icons / Open / 其它菜单 **相对批前零 diff**。  
4. **若批 A · 禁项：** 未捆 arena/settings/set；未开精英壳；**不**宣称 B0.1 已清。  
5. **禁长测/挑刺**（注释非玩家 UI · 静态 rg 即可）。

静态 rg（施工后）：

```bash
rg -n '逻辑待 CoreRpg' plugins/TrMenu/menus/ember_friends.yml plugins/TrMenu/menus/ember_mail.yml
# 期望：0

rg -n '逻辑待 CoreRpg' plugins/TrMenu/menus/
# 期望：0（精确字面；arena/settings/set 同族 soft 措辞不同 · 本窗不推）
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 好友 | —（TrMenu） | 余烬 · 好友 | hub → 好友 | `ember_friends` | 返回 hub | **注释文案** · 无新 NPC |
| 邮寄 | —（TrMenu） | 余烬 · 邮寄 | hub → 邮寄 | `ember_mail` | 返回 hub | **注释文案** · 无新 NPC |

**站岗自检：** 无新 NPC；仅文件头注释 → 最高标「维护可读」，不宣称好友/邮件逻辑接线改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | （维护者读 YAML） | 文件头 L2 无「逻辑待 CoreRpg」 | 注释摘要仍可读 | — |
| 2 | 玩家开菜单 | **不变**（本窗不改玩家 UI） | Icons / Open 原样 | — |

---

## 7. 回总控摘要

- **STATUS：待批 A** · tip `docs/design/design-ember-friends-mail-header-copy.md`
- **荐案：**
  - friends **L2** → `# 列表 / 申请 / 组队邀请 / 师徒（可选）`
  - mail **L2** → `# 系统邮件 / 战令 / 维护补偿`
- **实际行号：** 脏点各 **L2**；Icons / Open **勿改**
- **旁附 soft 计数：3**（arena L2 · settings L2 · set L2）· **不捆**
- **本窗零改：** TrMenu / NI / CoreRpg / 玩法 YAML **未动**
- **勿 push**；批后专岗改 TrMenu · 禁长测/挑刺 · **勿宣称 B0.1 已清**
- **前序确认：** B2.48 PASS · 勾销 `800aa05`（设计 `6b0e743`）已入仓

---

## 8. 总控批示

- [x] **批 A** · 两文件 L2 各删「；逻辑待 CoreRpg 接线」尾，保留功能摘要
- [ ] **驳回** · 说明

**批示摘要（待填）：** —
