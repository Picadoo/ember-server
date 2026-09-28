# STATUS · B2.19 套装页 gear/戒人话 A · 轻测

**日期：** 2026-09-29 01:46 → 01:47 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `0687fe5` · 批准 `157543b` · 插件 tip `24eb1b5`  
**范围：** `ember_set.yml` 玩家 lore/tell 裸 `gear_ember_*` / `acc_ember_raid_ring` → 0 · hub→套装 轻点  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/长本/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

`rg 'gear_ember_|acc_ember_raid_ring' ember_set.yml` → **0**（整文件字面无命中 → 玩家 lore/tell 路径亦 0）；hub→套装：刃/戒中文档位与团戒说明可见、live lore/tell **无**裸 id；未改配置；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | `rg 'gear_ember_|acc_ember_raid_ring' plugins/TrMenu/menus/ember_set.yml` → **0**（玩家 lore/tell） | **PASS** |
| 2 | hub→套装：刃/戒中文档位与团戒说明可见 | **PASS** |
| 3 | live lore/tell：**无**裸 gear/戒 id | **PASS** |
| 4 | 禁项：未改配置；未宣称 B0.1；ops=`[]` | **PASS** |

**判定口径（点 1）：** 设计/验收要求「玩家 lore/tell → 0」。本文件 `rg` 整文件字面 **0** 命中（无非玩家路径残留），故玩家可见侧亦 **0**。A1～A4 已为人话：`T0 余烬之刃` / `T1/T2/T3 精炼/深核/灾厄之刃` / `套装件 B · 余烬团本之戒` / 戒 tell「余烬团本之戒 · 周首通保底…」。

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 插件 tip | `24eb1b5` fix(trmenu): B2.19 ember_set gear id→中文名 |
| 设计 / 批准 | `0687fe5` / `157543b` |
| TrMenu | tip 后自动热更 `ember_set.yml`（01:45:09 CST）· 38 菜单 |
| 账号 | 验收 `NiPjh42r`（非 OP · Lv.10）· 辅助 `NiOpjh42r`（临时 OP，已 deop） |
| 探针 | `/tmp/b219-set-gear-id-copy-test.js` · JSON `/tmp/b219-set-gear-id-copy-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 静态 `gear_ember_|acc_ember_raid_ring` = 0

```
rg -n 'gear_ember_|acc_ember_raid_ring' plugins/TrMenu/menus/ember_set.yml  →  (空 · 0 hits)
```

YAML 抽查 A1～A4 + name 均中文（刃 `§6余烬之刃` / 戒 `§6余烬团本之戒`）。

### 2 · UX 菜单路径 hub→套装

| 步骤 | 结果 |
|------|------|
| `/ember` → hub | 标题「余烬 · 冒险枢纽」 |
| 点「套装」 | 标题「余烬 · 套装」· 槽 20 余烬之刃 / 22 余烬团本之戒 / 24 二件套 · 余烬同袍 |
| 悬停刃 lore | `T0 余烬之刃` · `T1/T2/T3 精炼/深核/灾厄之刃` · **无**裸 id |
| 悬停戒 lore | `套装件 B · 余烬团本之戒` · 周首通保底 · **无**裸 id |
| 点刃 tell | `套装·刃 任意余烬之刃（T0～T3）+ 团戒 = 余烬同袍。` |
| 点戒 tell | `套装·戒 余烬团本之戒 · 周首通保底。与刃同持即 余烬同袍 ✓` |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务；**菜单标题与槽位/tell 已切换**，以窗口 dump + chat 为准。）

### 3 · 禁项

| 项 | 证据 | 判定 |
|----|------|------|
| 未改配置 | 本岗仅写测报；`ember_set.yml` 相对 tip 无再改 | **PASS** |
| 未进本 / 坐标 hub | (-18.5, 58, 110.5) 前后同 | **PASS** |
| 未宣称 B0.1 | 本报不涉及 B0.1 | **PASS** |
| ops=`[]` | play=`[]` · login=`[]`；临时 OP 已 deop | **PASS** |
| 未带 dirty runtime | commit 仅 docs 测报 | **PASS** |
| 不叫挑刺 / 禁 wall-clock/DPS | 仅菜单轻点 | **PASS** |

---

## 阻塞点

无。

---

## 交付

- 报告：`docs/STATUS-ember-set-gear-id-copy-test.md`
- tip SHA：`24eb1b5`
- ops：`[]`
- push：见本 commit 是否已 push（失败则交总控代推）
