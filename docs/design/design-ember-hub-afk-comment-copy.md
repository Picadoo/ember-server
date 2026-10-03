# 设计稿 · hub AFK 入口注释功能摘要去斜杠（B2.53）

> **STATUS：已批 A（总控 · 2026-09-29 05:25 Asia/Shanghai）。**
> 本稿只定 TrMenu `ember_hub.yml` **actions 内 L188 注释** 1 行：去掉命令教学，改成功能摘要。
> **本窗只处理维护者可见注释（非玩家 UI / 非 Open tell）；新句不写斜杠、不写 STATUS 路径。**
> **本窗 commit 只写 docs；玩法 / TrMenu / NI YAML 零改**（批 A 后由专岗只改 hub L188 注释）。
> **禁**改 actions 动作、`menu: ember_afk`、Icons、Open Events；禁长测/挑刺；**勿宣称 B0.1 已清**；NI 旁记 soft 不捆。
> 前序：B2.52 shop 文件头 **PASS · 勾销**（close `55045e9`）；本窗升收 hub AFK 注释 soft。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | hub AFK 入口注释去命令教学，改功能摘要（UX · B2.53） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 05:24 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_hub.yml` · B2.52 PASS 旁证 |
| 状态 | **已批 A** · 交插件 TrMenu |
| tip 路径 | `docs/design/design-ember-hub-afk-comment-copy.md` |
| 上游 | B2.52 close `55045e9` · shop 已清；升本窗收 hub L188 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 TrMenu 施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------------|--------------------------------|
| `ember_hub.yml` **L188** actions 内注释 1 行 | actions 动作、`menu: ember_afk`、Icons、Open Events；Title / Layout |
| 仅替换为荐案功能摘要 | 玩家 UI、菜单动线、等级门槛、传送逻辑；其它玩法 YAML / NI 文件 |
| | git push；热更 / reload；开精英预览壳；宣称 B0.1 已清；写 STATUS 路径；教玩家手打命令 |

---

## 1. 问题一句话

**hub 的 AFK 入口 actions 注释仍带命令教学；这是维护者可见注释，不是玩家 UI，应改成档位选择进入挂机菜单的功能摘要。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 目标行与上下文（实际行号）

`plugins/TrMenu/menus/ember_hub.yml` 当前 live 行号如下：

```yaml
L185:    actions:
L186:      all:
L187:        - 'sound: BLOCK_NOTE_PLING-1-2'
L188:        # 1.14.0: tier picker → /corerpg afk n (level gate + teleport in CoreRpg)
L189:        - 'menu: ember_afk'
```

L188 是待替换的 YAML 注释；L187 音效与 L189 菜单动作保持原样。`menu: ember_afk` 是 AFK 入口动作，**不改**。

### 2.2 Icons / Open 确认

本稿只记录目标 actions 上下文，不施工 Icons 或 Open Events；两者相对批前 **零改**。玩家可见 lore 与菜单动线也不在本窗范围。

### 2.3 旁记 soft（不捆施工）

| # | 文件 / 位置 | 现况 | 玩家可见？ |
|---|-------------|------|------------|
| 1 | `plugins/NeigeItems/Items/ember-disassemble.yml` 文件头 | 仍有管理注释 | 否 · 注释 |
| 2 | `plugins/NeigeItems/Items/ember-pets.yml` 文件头 | 仍有模块待接线注释 | 否 · 注释 |

旁记 soft **不捆施工**，不在本稿验收中顺手清理。

---

## 3. 荐案（明确推荐）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 功能摘要（推荐）** | 保留版本号与档位选择信息，改为“选择挂机档位并进入挂机菜单”，并保留等级门槛与传送摘要 | 短；维护者一眼懂入口用途；无命令教学、无斜杠 | 不再记录底层命令写法 | **推荐** |

### 3.1 旧 / 新注释全文对照

| # | 位置 | 旧（全文） | 新（荐 · 全文） |
|---|------|------------|-----------------|
| A1 | `ember_hub.yml` **L188** | `# 1.14.0: tier picker → /corerpg afk n (level gate + teleport in CoreRpg)` | `# 1.14.0：选择挂机档位并进入挂机菜单（等级门槛 + 传送）` |

**口径备忘：** 批 A 后只替换上表 L188 这一行注释；L187 音效、L189 `menu: ember_afk`、Icons、Open Events、Title、Layout 及玩法逻辑 **一字不动**。荐案为功能摘要，**无斜杠、无 STATUS 路径、无命令教学**。

**非目标 / 不施工：** 改 actions 动作；改 `menu: ember_afk`；改 Icons / Open；改玩家 lore、等级门槛、传送或菜单逻辑；改 NI `ember-disassemble.yml` / `ember-pets.yml` 文件头；开精英壳；宣称 B0.1 已清；未批改 YAML；长测/挑刺；推送远端。

---

## 4. 验收（≤5，批 A 后另开施工）

1. **本设计 commit：** 仅本稿 + backlog；`plugins/TrMenu/menus/ember_hub.yml` 与其它玩法 YAML / NI / loot / DP / MM **零 diff**。
2. **若批 A：** 仅 hub **L188** 注释替换为荐案；新句是档位选择进入挂机菜单的短摘要，**无**斜杠、**无** STATUS 路径、**无**命令教学。
3. **若批 A：** L187 音效、L189 `menu: ember_afk`、actions 其它动作、Icons、Open Events、Title / Layout 相对批前零 diff。
4. **若批 A · 禁项：** NI 两份文件头仍不捆；未开精英壳；**不**宣称 B0.1 已清。
5. **禁长测/挑刺**（维护者注释非玩家 UI · 静态 diff / rg 即可）。

预期施工后静态检查（本设计窗不执行改动）：

```bash
rg -n -C 2 '1\.14\.0|menu: ember_afk' plugins/TrMenu/menus/ember_hub.yml
# 仅 L188 注释替换；L187 音效与 L189 菜单动作不变

git diff -- plugins/TrMenu/menus/ember_hub.yml
# 仅显示 L188 1 行注释
```

---

## 5. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|------|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **插件** | 批 A 后仅替换 hub L188 注释 | 批 A 后 |
| **测试** | 静态 diff / rg；不测玩法，不叫挑刺 | 施工后 |

玩家菜单动线、音效、AFK 菜单动作、等级门槛与传送逻辑均不变。本稿只处理维护者可见 actions 注释，不新增玩家命令教学。

---

## 6. 回总控摘要

- **STATUS：待批 A** · tip `docs/design/design-ember-hub-afk-comment-copy.md`
- **荐案：** hub **L188** → `# 1.14.0：选择挂机档位并进入挂机菜单（等级门槛 + 传送）`
- **旧/新已全文对照：** 仅 L188 1 行；L187 音效、L189 `menu: ember_afk`、actions / Icons / Open **零改**
- **旁记 soft 不捆：** NI `ember-disassemble.yml` / `ember-pets.yml` 文件头
- **本窗零改：** `ember_hub.yml` / TrMenu / NI / CoreRpg / 玩法 YAML **未动**
- **勿 push**；批后专岗改 TrMenu · 禁长测/挑刺 · **勿宣称 B0.1 已清**
- **前序确认：** B2.52 shop **PASS · 勾销**（close `55045e9`）

---

## 7. 总控批示

- [x] **批 A** · 仅 `ember_hub.yml` L188 按上表荐案改功能摘要；actions / Icons / Open / 玩法零改
- [ ] **驳回** · 说明

**批示摘要：** 批 A。仅 hub L188 改功能摘要；`menu: ember_afk`/Icons/Open 零改；NI 旁记不捆；禁长测/挑刺；勿宣称 B0.1。
