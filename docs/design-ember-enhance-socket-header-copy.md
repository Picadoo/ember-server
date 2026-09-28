# 设计稿 · enhance/socket 文件头斜杠教学口吻（B2.51）

> **STATUS：已批 A（总控 · 2026-09-29 05:16 Asia/Shanghai）。**
> 本稿只定 TrMenu `ember_enhance.yml` / `ember_socket.yml` **YAML 文件头 L2 注释**各 1 行：去掉「跑命令」教学，保留功能摘要。
> **本窗只处理维护者可见注释（非玩家 UI / 非 Open tell）；新句不写斜杠、不写 STATUS 路径。**
> **本窗 commit 只写 docs；玩法 / TrMenu / NI YAML 零改**（批 A 后由专岗只改两行 TrMenu L2）。
> **禁**改 Icons / Open / 其它菜单；禁长测/挑刺；**勿宣称 B0.1 已清**；精英壳不捆。
> **旁记 soft 不捆：** `ember_shop.yml` L3 软通货提示；hub L188 AFK 注释；NI `ember-disassemble.yml` / `ember-pets.yml` 文件头。
> 前序：B2.50 **PASS · 勾销**（close `30ca5c8`；设计 `da20e44`；批准 `d808398`；插件 `4e84455`；测 `b84e948`）；menus「逻辑待」本轨已归零；交稿等批 A。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | enhance/socket · **文件头命令教学去除，保留功能摘要**（UX · B2.51） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 05:15 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_enhance.yml` · `ember_socket.yml` · B2.50 PASS 旁证 |
| 状态 | **已批 A** · 交插件 TrMenu |
| tip 路径 | `docs/design-ember-enhance-socket-header-copy.md` |
| 上游 | B2.50 close `30ca5c8` · menus「逻辑待」本轨已归零 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 TrMenu 施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------------|--------------------------------|
| `ember_enhance.yml` **L2** 文件头注释 1 行 | Icons / Open Events / 其它 actions；Title / Layout |
| `ember_socket.yml` **L2** 文件头注释 1 行 | 其它 TrMenu 页；NI / loot / DP / MM / CoreRpg |
| | git push；热更 / reload；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML；教玩家手打命令；写 STATUS 路径 |

---

## 1. 问题一句话

**enhance / socket 菜单 YAML 文件头 L2 仍写「跑命令」——这是维护注释中的斜杠教学，不是玩家 UI；保留菜单用途摘要即可。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 目标两行（实际行号）

| 文件 | 行 | 现句（全文） | 可见性 |
|------|----|--------------|--------|
| `ember_enhance.yml` | **L2** | `# 壳：提示手持余烬装备 + 跑 /corerpg enhance` | 否 · YAML 注释 |
| `ember_socket.yml` | **L2** | `# 壳：提示手持装备 + 跑 /corerpg socket …` | 否 · YAML 注释 |

**文件头与 Open tell 确认：**

```
# ember_enhance.yml
L1 # 强化子菜单 — DESIGN-ember-enhance-socket.md §7
L2 # 壳：提示手持余烬装备 + 跑 /corerpg enhance
L3 （空）
L4 Title: '§c余烬 · 强化'
L19 Events:
L20   Open:
L21     - 'sound: BLOCK_ANVIL_LAND-1-0.6'
L22     - 'tell: §7请手持 §c余烬之刃 §7或 §e余烬护符 §7后再操作。'

# ember_socket.yml
L1 # 镶嵌子菜单 — DESIGN-ember-enhance-socket.md §7
L2 # 壳：提示手持装备 + 跑 /corerpg socket …
L3 （空）
L4 Title: '§5余烬 · 镶嵌'
L19 Events:
L20   Open:
L21     - 'sound: BLOCK_NOTE_PLING-1-0.8'
L22     - 'tell: §7请手持 §c余烬之刃 §7或 §e余烬护符 §7；宝石放在背包。'
```

**Open tell 现况：** 两页均已是玩家人话（强化：手持余烬之刃/余烬护符；镶嵌：手持装备并准备背包宝石），本窗确认零改。

### 2.2 B2.50 旁证（施工已落地 · 本窗勿回改）

| 项目 | 现况 | 备注 |
|------|------|------|
| `ember_arena.yml` / `ember_settings.yml` / `ember_set.yml` L2 | 管理尾已清 | B2.50 PASS · 勾销 · 本窗只作旁证 |
| menus「逻辑待」本轨 | 已归零 | 不回改、不扩范围 |

### 2.3 旁记 soft（只列证 · 本窗不捆）

| # | 文件 / 位置 | 现况 | 玩家可见？ |
|---|-------------|------|------------|
| 1 | `plugins/TrMenu/menus/ember_shop.yml` L3 | 软通货提示仍带命令教学 | 否 · 注释 |
| 2 | `plugins/TrMenu/menus/ember_hub.yml` L188 | AFK tier picker 注释仍带命令 | 否 · 注释 |
| 3 | `plugins/NeigeItems/Items/ember-disassemble.yml` 文件头 | L3 仍为分解/重铸命令管理注释 | 否 · 注释 |
| 4 | `plugins/NeigeItems/Items/ember-pets.yml` 文件头 | L1 仍写使魔逻辑待模块 | 否 · 注释 |

旁记 soft **不捆施工**，不在本稿验收中顺手清理。

---

## 3. 荐案（明确推荐其一）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 两行改功能摘要（推荐）** | 删除 L2 的命令教学，分别保留强化 / 镶嵌所需准备条件 | 短；维护者一眼懂用途；不把斜杠带进非玩家注释 | 无 | **推荐** |
| A′. 只留「强化」/「镶嵌」 | 极短功能名 | 丢失手持装备 / 宝石准备摘要 | 备选 |

**推荐句（批 A 后专岗改 TrMenu · 新旧全文）：**

| # | 位置 | 旧（全文） | 新（荐 · 全文） |
|---|------|------------|-----------------|
| A1 | `ember_enhance.yml` **L2** | `# 壳：提示手持余烬装备 + 跑 /corerpg enhance` | `# 手持余烬装备后进行强化` |
| A2 | `ember_socket.yml` **L2** | `# 壳：提示手持装备 + 跑 /corerpg socket …` | `# 手持余烬装备、备好宝石后进行镶嵌` |

**口径备忘：** 批后只动上表 2 行 L2 注释；Icons / Open / Title / Layout / 其它菜单 **一字不动**；荐案无斜杠、无 STATUS 路径、无命令教学。

**非目标 / 不施工：** 改 Icons / Open；改 `ember_shop.yml`；改 hub AFK 注释；改 NI `ember-disassemble.yml` / `ember-pets.yml`；开精英壳；宣称 B0.1 已清；未批改 YAML；长测/挑刺；推送远端。

---

## 4. 验收（≤5，批 A 后另开施工）

1. **本设计 commit：** 仅本稿 + backlog；两目标 yml、其它玩法 YAML / NI / loot / DP / MM **零 diff**。
2. **若批 A：** 仅 `ember_enhance.yml` L2 与 `ember_socket.yml` L2 分别替换为荐案；新句为功能摘要，**无**斜杠、**无** STATUS 路径。
3. **若批 A：** 两页 Icons / Open tell / 其它 actions 相对批前零 diff；现有玩家句保持人话。
4. **若批 A · 禁项：** shop L3、hub L188、两份 NI 文件头仍不捆；未开精英壳；**不**宣称 B0.1 已清。
5. **禁长测/挑刺**（文件头注释非玩家 UI · 静态 diff / rg 即可）。

预期施工后静态检查（本设计窗不执行改动）：

```bash
rg -n '^#' plugins/TrMenu/menus/ember_enhance.yml plugins/TrMenu/menus/ember_socket.yml
# 仅 L2 替换为两句功能摘要；其它文件头与正文不动

git diff -- plugins/TrMenu/menus/ember_enhance.yml plugins/TrMenu/menus/ember_socket.yml
# 仅显示各自 L2 1 行
```

---

## 5. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|------|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **插件** | 批 A 后仅替换两目标 yml 各 L2 | 批 A 后 |
| **测试** | 静态 diff / rg；不测玩法，不叫挑刺 | 施工后 |

玩家动线与 Open tell 不变：强化页提示手持余烬之刃/余烬护符；镶嵌页提示手持装备、背包准备宝石。本稿不新增玩家命令教学。

---

## 6. 回总控摘要

- **STATUS：待批 A** · tip `docs/design-ember-enhance-socket-header-copy.md`
- **荐案：**
  - enhance **L2** → `# 手持余烬装备后进行强化`
  - socket **L2** → `# 手持余烬装备、备好宝石后进行镶嵌`
- **旧/新已全文对照：** 仅两文件各自 L2；Open tell 已是人话，**零改**
- **旁记 soft 不捆：** shop L3 `/corerpg coin`；hub L188 AFK 注释；NI `ember-disassemble.yml` / `ember-pets.yml` 文件头
- **本窗零改：** TrMenu / NI / CoreRpg / 玩法 YAML **未动**
- **勿 push**；批后专岗改 TrMenu · 禁长测/挑刺 · **勿宣称 B0.1 已清**
- **前序确认：** B2.50 **PASS · 勾销**（close `30ca5c8`）；menus「逻辑待」本轨已归零

---

## 7. 总控批示

- [x] **批 A** · 两文件 L2 按上表荐案去命令教学、保留功能摘要
- [ ] **驳回** · 说明

**批示摘要：** 批 A。仅 `ember_enhance.yml` / `ember_socket.yml` 各改 L2 为荐案功能摘要；Icons/Open 零改；shop/hub/NI 旁记不捆；禁长测/挑刺；勿宣称 B0.1。
