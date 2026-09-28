# 设计稿 · 公会菜单 Open tell 管理口吻（B2.48）

> **STATUS：待批 A（策划交稿 · 2026-09-29 04:58 Asia/Shanghai）。**  
> 本稿只定 TrMenu `ember_guild.yml` **Open Events tell** **1** 句：去掉「逻辑待 CoreRpg 接线」管理口吻，保留「创建公会 · 日捐盟课 · 周盟 Boss」人话提示。  
> **本窗 commit 只写 docs；玩法 / TrMenu / NI YAML 零改**（批后由专岗改 TrMenu）。  
> **禁**改 icons / 其它 actions、公会数值与逻辑；禁长测/挑刺；**勿宣称 B0.1 已清**；精英壳不捆。  
> **不捆本窗：** `ember_friends` / `ember_mail` 文件头同句「逻辑待 CoreRpg 接线」（旁记 soft）。  
> 前序：B2.47 PASS · 勾销 `1561dac`（设计 `e9370a8` · 批准 `78c815e` · 插件 `8c09391` · 测 `6952749`）；拆解 Open tell 已清 · **旁证 · 勿回改**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 公会菜单 · **Open tell 管理口吻人话化**（UX · B2.48） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 04:58 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_guild.yml` · B2.47 PASS 旁证（拆解 Open tell 已清） |
| 状态 | **待批 A** · 交总控 |
| tip 路径 | `docs/design-ember-guild-open-tell-copy.md` |
| 上游 | B2.47 close `1561dac` · 拆解 Open tell 已清；升本窗 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 TrMenu 施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------------|--------------------------------|
| `ember_guild.yml` Open Events **tell 1 句**（L22） | icons / 其它 actions；公会 create/donate/boss 数值逻辑 |
| 热更 / TrMenu reload（服约定） | NI 物品 YAML；其它 TrMenu 页（friends/mail/disassemble）；loot / DP / MM / CoreRpg |
| | git push；开精英预览壳；宣称 B0.1 已清；未批改玩法 YAML；教手打命令 |

---

## 1. 问题一句话

**开盟约菜单即 chat 提示含「逻辑待 CoreRpg 接线」——管理口吻漏进玩家可见 Open tell；功能概览本身可用，末尾半句应删。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 入口

枢纽 `/ember` → hub → 盟约 → `ember_guild`（TrMenu 打开即触发 Open Events）

### 2.2 Open Events（实际行号 · 本窗唯一目标）

| 行 | 现句 | 可见性 |
|----|------|--------|
| L20 | `Open:` | — |
| L21 | `sound: BLOCK_NOTE_PLING-1-0.8` | 音效 |
| **L22** | **`tell: §2[盟约] §7创建公会 · 日捐盟课 · 周盟 Boss。逻辑待 CoreRpg 接线。`** | **玩家 chat · 脏点** |

**全句（现网 L22）：**

```
tell: §2[盟约] §7创建公会 · 日捐盟课 · 周盟 Boss。逻辑待 CoreRpg 接线。
```

**扫网：**

```bash
rg -n "逻辑待 CoreRpg|Open" plugins/TrMenu/menus/ember_guild.yml
# 现网：L20 Open: · L22 tell 含「逻辑待 CoreRpg 接线」（本窗目标）
```

### 2.3 Icons 摘要（对齐 CTA · 勿改）

Layout 5 行：空顶 → 中排 `C D B` → 下排 `I` → 底 `X`。

| 图标 | 行（display name） | 人话名 | CTA lore |
|------|-------------------|--------|----------|
| C | L57 | §a创建盟约 | `§a➥ §f查看说明` |
| D | L75 | §6捐献 · 盟课 | `§6➥ §f查看说明` |
| B | L94 | §c周盟 Boss | `§c➥ §f查看说明` |
| I | L42 | §e我的盟约 | `§e➥ §f点击查询` |
| X | L33 | §7返回主菜单 | — |

**CTA 方位：** 可操作钮在菜单**中下**（第 3–4 行）；同构 B2.47「点左侧」→ 本窗用「**点下方即可**」（勿臆造具体按钮名）。

### 2.4 B2.47 旁证（施工已落地 · 本窗勿回改）

| 文件 | 现况 | 备注 |
|------|------|------|
| `ember_disassemble.yml` Open tell | 无「逻辑待 CoreRpg」 | B2.47 PASS · 旁证 |

### 2.5 旁附 soft 列表（只列证 · 本窗不推 · 计数 **2**）

| # | 文件 | 行 | 现况 | 玩家可见？ |
|---|------|----|------|------------|
| 1 | `ember_friends.yml` | L2 | YAML 文件头注释含同句 | 否 · 注释 |
| 2 | `ember_mail.yml` | L2 | YAML 文件头注释含同句 | 否 · 注释 |

```bash
rg -n "逻辑待 CoreRpg" plugins/TrMenu/menus/
# 命中 3：guild L22（本窗）+ friends L2 + mail L2
# 本窗只动 guild；其余 2 条旁记 soft · 不捆
# （disassemble 已由 B2.47 清 · 0 命中）
```

---

## 3. 荐案（明确推荐其一）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 去管理口吻 + 微对齐 CTA（推荐）** | L22 → 保留「创建公会 · 日捐盟课 · 周盟 Boss」，去掉「逻辑待 CoreRpg 接线」，末尾微加「点下方即可」与 icons 中下方位同向 | 短；去管理词；与中下 CTA 一致；**勿教命令** | 多 6 字 | **推荐** |
| A′. 仅删半句 | L22 → 保留「创建公会 · 日捐盟课 · 周盟 Boss。」整句止于句号 | 改动最小 | 开菜单无点击指引（悬停 lore 才有） | 备选同档 |

**推荐句（批 A 后专岗改 TrMenu）：**

| # | 位置 | 旧 | 新（荐） |
|---|------|----|----------|
| A1 | Open tell **L22** | `tell: §2[盟约] §7创建公会 · 日捐盟课 · 周盟 Boss。逻辑待 CoreRpg 接线。` | `tell: §2[盟约] §7创建公会 · 日捐盟课 · 周盟 Boss，点下方即可。` |

**荐替换全文（一行）：**

```
    - 'tell: §2[盟约] §7创建公会 · 日捐盟课 · 周盟 Boss，点下方即可。'
```

**口径备忘：** 只动上表 1 句 Open tell；sound / Icons / create·donate·boss actions **一字不动**；**勿**写入 `/corerpg`、**勿**写入 `STATUS.md`；公会数值逻辑 **零改**；friends/mail **本窗不改**。

**非目标 / 不施工：** 改 icons / 其它 actions；捆 friends/mail；开精英壳；宣称 B0.1 已清；未批改 YAML；长测/挑刺；教手打命令；回改 B2.47 拆解 Open tell。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿 + backlog；玩法 YAML / loot / NI / DP / MM / `ember_guild.yml` **零 diff**。  
2. **若批 A：** Open tell **无**字面「逻辑待 CoreRpg」；保留创建/日捐/周盟 Boss 提示；**无** `/corerpg`、**无** STATUS.md。  
3. **若批 A：** Icons / 其它 actions **相对批前零 diff**。  
4. **若批 A · 禁项：** 未捆 friends/mail；未开精英壳；**不**宣称 B0.1 已清。  
5. **目视轻测：** `/ember` → 盟约 → 开菜单 chat 无「逻辑待 CoreRpg」；功能概览可读。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n '逻辑待 CoreRpg' plugins/TrMenu/menus/ember_guild.yml
# 期望：0（本文件 Open tell 已清）

rg -n '逻辑待 CoreRpg' plugins/TrMenu/menus/
# 期望：仅剩 friends/mail（旁记 soft · 本窗不推）
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 盟约 | —（TrMenu） | 余烬 · 盟约 | hub → 盟约 | `ember_guild` | 返回 hub | **UI/文案可用** · 无新 NPC |

**站岗自检：** 无新 NPC；仅 Open tell 文案 → 最高标「UI 可用」，不宣称公会数值/接线改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | `/ember` → 盟约 | 「余烬 · 盟约」 | 打开 `ember_guild` | 返回 hub |
| 2 | 开菜单瞬间 | chat：`[盟约] 创建公会 · 日捐盟课 · 周盟 Boss，点下方即可。`；**无**「逻辑待 CoreRpg」 | Open tell | — |
| 3 | 点中下创建/捐献/周盟 Boss/我的盟约 | 既有 CTA / tell | 走既有 actions | — |

---

## 7. 回总控摘要

- **STATUS：待批 A** · tip `docs/design-ember-guild-open-tell-copy.md`
- **荐案：L22** → `tell: §2[盟约] §7创建公会 · 日捐盟课 · 周盟 Boss，点下方即可。`
- **实际行号：** 脏点 **L22**；icons C/D/B/I/X 摘要见 §2.3 · **勿改**
- **旁附 soft 计数：2**（friends L2 · mail L2）· **不捆**
- **本窗零改：** TrMenu / NI / CoreRpg / 玩法 YAML **未动**
- **勿 push**；批后专岗改 TrMenu · 禁长测/挑刺 · **勿宣称 B0.1 已清**
- **前序确认：** B2.47 PASS · 勾销 `1561dac`（设计 `e9370a8`）已入仓

---

## 8. 总控批示

- [ ] **批 A** · L22 替换为荐句（去「逻辑待 CoreRpg 接线」；保留创建/日捐/周盟 Boss；可点下方）
- [ ] **驳回** · 说明

**批示摘要（待填）：** —
