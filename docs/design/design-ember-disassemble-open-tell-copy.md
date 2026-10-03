# 设计稿 · 拆解菜单 Open tell 管理口吻（B2.47）

> **STATUS：已批 A（总控 · 2026-09-29 04:52 Asia/Shanghai）。**  
> 本稿只定 TrMenu `ember_disassemble.yml` **Open Events tell** **1** 句：去掉「逻辑待 CoreRpg 接线」管理口吻，保留手持刃/护符人话提示。  
> **本窗 commit 只写 docs；玩法 / TrMenu / NI YAML 零改**（批后由专岗改 TrMenu）。  
> **禁**改 scrap/reforge 数值与逻辑、体力门、四件甲、锻炉；禁长测/挑刺；**勿宣称 B0.1 已清**；精英壳不捆。  
> **不捆本窗：** `ember_guild` / `ember_friends` / `ember_mail` 同句「逻辑待 CoreRpg 接线」（旁记 soft）。  
> 前序：B2.46 PASS · 勾销 `f04c01b`（插件 `530c2f8` · 测 `89b1117`）；L89 已是人话「点左侧即可分解或重铸」——**本窗旁证 · 勿回改**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 拆解菜单 · **Open tell 管理口吻人话化**（UX · B2.47） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 04:51 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_disassemble.yml` · B2.46 PASS 旁证 L89 |
| 状态 | **已批 A** · 交插件改 TrMenu |
| tip 路径 | `docs/design/design-ember-disassemble-open-tell-copy.md` |
| 上游 | B2.46 close `f04c01b` · 规则速览管理注释已清 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 TrMenu 施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------------|--------------------------------|
| `ember_disassemble.yml` Open Events **tell 1 句**（L22） | scrap / reforge command、掉落、体力 cost、数值逻辑 |
| 热更 / TrMenu reload（服约定） | NI 物品 YAML；其它 TrMenu 页（guild/friends/mail）；loot / DP / MM / CoreRpg |
| | git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML；四件甲/锻炉；教手打命令 |

---

## 1. 问题一句话

**开拆解菜单即 chat 提示含「逻辑待 CoreRpg 接线」——管理口吻漏进玩家可见 Open tell；持装提示本身可用，末尾半句应删。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 入口

枢纽 `/ember` → hub → 拆解/重铸 → `ember_disassemble`（TrMenu 打开即触发 Open Events）

### 2.2 Open Events（实际行号 · 本窗唯一目标）

| 行 | 现句 | 可见性 |
|----|------|--------|
| L20 | `Open:` | — |
| L21 | `sound: BLOCK_NOTE_PLING-1-0.8` | 音效 |
| **L22** | **`tell: §7[分解] §7请手持 §c余烬之刃 §7或 §e余烬护符 §7。逻辑待 CoreRpg 接线。`** | **玩家 chat · 脏点** |

**全句（现网 L22）：**

```
tell: §7[分解] §7请手持 §c余烬之刃 §7或 §e余烬护符 §7。逻辑待 CoreRpg 接线。
```

**扫网：**

```bash
rg -n "逻辑待 CoreRpg|Open" plugins/TrMenu/menus/ember_disassemble.yml
# 现网：L20 Open: · L22 tell 含「逻辑待 CoreRpg 接线」（本窗目标）
```

### 2.3 B2.46 旁证（施工已落地 · 本窗勿回改）

| 行 | 现句 | 备注 |
|----|------|------|
| **L89** | `§8点左侧即可分解或重铸` | B2.46 人话 · **已清 docs/status/STATUS.md** · 旁证 |
| L94 | `§8点左侧按钮即可分解或重铸，无需手打命令` | 点击 tell · 勿改 |

### 2.4 左侧 CTA 对照（对齐参考 · 勿改）

| 图标 | 行 | 要点 |
|------|----|------|
| S 分解 | L49 / L51 | `手持余烬装备后点击分解` · `§7➥ §f点击分解` |
| R 重铸 | L68 / L70 | `手持装备且背包有重铸石后点击` · `§6➥ §f点击重铸` |

### 2.5 旁附 soft 列表（只列证 · 本窗不推 · 计数 **3**）

| # | 文件 | 行 | 现况 | 玩家可见？ |
|---|------|----|------|------------|
| 1 | `ember_guild.yml` | L22 | Open tell：`…逻辑待 CoreRpg 接线。` | 是 · chat |
| 2 | `ember_friends.yml` | L2 | YAML 文件头注释含同句 | 否 · 注释 |
| 3 | `ember_mail.yml` | L2 | YAML 文件头注释含同句 | 否 · 注释 |

```bash
rg -n "逻辑待 CoreRpg" plugins/TrMenu/menus/
# 命中 4：disassemble L22（本窗）+ guild L22 + friends L2 + mail L2
# 本窗只动 disassemble；其余 3 条旁记 soft · 不捆
```

---

## 3. 荐案（明确推荐其一）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 去管理口吻 + 微对齐 CTA（推荐）** | L22 → 保留持装提示，去掉「逻辑待 CoreRpg 接线」，末尾微加「点左侧即可」与 L89 同向 | 短；去管理词；与左侧 CTA / L89 一致；**勿教命令** | 多 6 字 | **推荐** |
| A′. 仅删半句 | L22 → 保留「请手持刃/护符。」整句止于句号 | 改动最小 | 开菜单无点击指引（悬停 L89 才有） | 备选同档 |

**推荐句（批 A 后专岗改 TrMenu）：**

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | Open tell **L22** | `tell: §7[分解] §7请手持 §c余烬之刃 §7或 §e余烬护符 §7。逻辑待 CoreRpg 接线。` | `tell: §7[分解] §7请手持 §c余烬之刃 §7或 §e余烬护符 §7，点左侧即可。` |

**荐替换全文（一行）：**

```
    - 'tell: §7[分解] §7请手持 §c余烬之刃 §7或 §e余烬护符 §7，点左侧即可。'
```

**口径备忘：** 只动上表 1 句 Open tell；sound / Icons / scrap·reforge `command:` / L89 / L94 **一字不动**；**勿**写入 `/corerpg`、**勿**写入 `docs/status/STATUS.md`；拆解掉落与重铸消耗逻辑 **零改**；guild/friends/mail **本窗不改**。

**非目标 / 不施工：** 改 scrap/reforge 数值/体力；捆 guild/friends/mail；开精英壳；宣称 B0.1 已清；未批改 YAML；长测/挑刺；四件甲/锻炉；回改 L89；教手打命令。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿 + backlog；玩法 YAML / loot / NI / DP / MM / `ember_disassemble.yml` **零 diff**。  
2. **若批 A：** Open tell **无**字面「逻辑待 CoreRpg」；保留手持刃/护符提示；**无** `/corerpg`、**无** docs/status/STATUS.md。  
3. **若批 A：** scrap / reforge / 体力 / L89 / S·R lore **相对批前零 diff**。  
4. **若批 A · 禁项：** 未捆 guild/friends/mail；未开精英壳；**不**宣称 B0.1 已清。  
5. **目视轻测：** `/ember` → 拆解 → 开菜单 chat 无「逻辑待 CoreRpg」；持装提示可读。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n '逻辑待 CoreRpg' plugins/TrMenu/menus/ember_disassemble.yml
# 期望：0（本文件 Open tell 已清）

rg -n '逻辑待 CoreRpg' plugins/TrMenu/menus/
# 期望：仅剩 guild/friends/mail（旁记 soft · 本窗不推）
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 拆解/重铸 | —（TrMenu） | 余烬 · 拆解 | hub → 拆解 | `ember_disassemble` | 返回 hub | **UI/文案可用** · 无新 NPC |

**站岗自检：** 无新 NPC；仅 Open tell 文案 → 最高标「UI 可用」，不宣称拆解数值/掉落改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | `/ember` → 拆解 | 「余烬 · 拆解」 | 打开 `ember_disassemble` | 返回 hub |
| 2 | 开菜单瞬间 | chat：`[分解] 请手持 余烬之刃 或 余烬护符，点左侧即可。`；**无**「逻辑待 CoreRpg」 | Open tell | — |
| 3 | 点左侧分解/重铸 | 既有 CTA / tell | 走既有 scrap / reforge | — |

---

## 7. 回总控摘要

- **STATUS：已批 A** · tip `docs/design/design-ember-disassemble-open-tell-copy.md`
- **荐案：L22** → `tell: §7[分解] §7请手持 §c余烬之刃 §7或 §e余烬护符 §7，点左侧即可。`
- **实际行号：** 脏点 **L22**；旁证 L89 已人话（B2.46 · 勿回改）
- **旁附 soft 计数：3**（guild L22 · friends L2 · mail L2）· **不捆**
- **本窗零改：** TrMenu / NI / CoreRpg / 玩法 YAML **未动**
- **勿 push**；批后专岗改 TrMenu · 禁长测/挑刺 · **勿宣称 B0.1 已清**

---

## 8. 总控批示

- [x] **批 A** · L22 替换为荐句（去「逻辑待 CoreRpg 接线」；保留持装；可点左侧）（总控 · tip 本提交）
- [ ] **驳回** · 说明

**批示摘要：** 采纳荐句；guild/friends/mail 旁记 soft 不捆；L89 旁证勿回改。交 **余烬-插件** 只改 `ember_disassemble.yml` L22。禁长测/挑刺；勿宣称 B0.1 已清。验收：Open tell 无「逻辑待 CoreRpg」+ 开菜单轻测。
