# STATUS · B2.18 奖励预览 NI 灰字 A · 轻测

**日期：** 2026-09-29 01:38 → 01:42 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `5c2f7f2` · 批准 `cef1017` · 插件 tip `dc8ebb8`  
**范围：** 五入口奖励预览 `ember_*_rewards` · `§8NI` 灰字清零 · 中文主名/材料保留  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/长本/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

`rg '§8NI' ember_*_rewards.yml` → **0**；hub → 日/周/团/深渊/灾厄 → 奖励预览各轻点一眼：中文主名/材料仍在，live lore **无** `NI id:` / `gear_ember_` / `mat_ember_` 灰字；未进本、未发物；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | `rg '§8NI' plugins/TrMenu/menus/ember_*_rewards.yml` → **0**（五文件） | **PASS** |
| 2 | 五入口奖励预览：中文主名/材料仍在 | **PASS** |
| 3 | 五入口 live lore：**无**裸 NI id 灰字 | **PASS** |
| 4 | 禁项：未改配置；未宣称 B0.1；ops=`[]` | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 插件 tip | `dc8ebb8` fix(trmenu): align ember reward preview NI id（34 行删） |
| 设计 / 批准 | `5c2f7f2` / `cef1017` |
| TrMenu | tip 后自动热更五页（01:36:49 CST）· 38 菜单 |
| 账号 | 验收 `NiPj7k9e`（非 OP · Lv.37）· 辅助 `NiOpj7k9e`（临时 OP，已 deop） |
| 探针 | `/tmp/b218-reward-ni-align-test.js` · JSON `/tmp/b218-reward-ni-align-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 静态 `§8NI` = 0

```
rg '§8NI' plugins/TrMenu/menus/ember_*_rewards.yml  →  (空 · exit 1)
rg 'NI id:|§8NI:|gear_ember_|mat_ember_|acc_ember_' … →  (空)
```

五文件中文主名抽查仍在（例：日常 `余烬之刃` / 周本 `精炼护符` / 团本 `余烬团本之戒` / 深渊档位与 COMPLETE / 灾厄日箱·尾刀·凝核）。

### 2 · 五入口菜单路径（UX）

| 入口 | 动线 | live 标题 | 中文主名抽查 | NI 灰字 |
|------|------|-----------|--------------|---------|
| 日常 | hub→日常→P | `日常 · 奖励预览` | 余烬之刃 / 余烬碎片×5 / 核心碎片 / 附魔晶 | **无** |
| 周本 | hub→周常→P | `周本 · 奖励预览` | 精炼护符 / 核心碎片×3 / 骨尘×4 | **无** |
| 团本 | hub→团本→P | `团本 · 奖励预览` | 团本之戒 / 锋利石约50% / 核心碎片×4 | **无** |
| 深渊 | hub→深渊→P | `深渊 · 奖励预览` | 档1～4/5～9/10+ · COMPLETE · 稳定符 | **无** |
| 灾厄 | hub→灾厄→P | `灾厄 · 奖励预览` | 日箱 / 凝核 / 尾刀必给·概率 | **无** |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务；**菜单标题与槽位已切换**，以窗口 dump 为准。）

### 3 · 禁项

| 项 | 证据 | 判定 |
|----|------|------|
| 未改配置 | 本岗仅写测报；五 `*_rewards.yml` 相对 tip 无再改 | **PASS** |
| 未进本 / 未发物 | 坐标始终 hub；背包刃/护符/面包×16 前后同 | **PASS** |
| 未宣称 B0.1 | 本报不涉及 B0.1 | **PASS** |
| ops=`[]` | play=`[]` · login=`[]`；临时 OP 已 deop | **PASS** |
| 未带 dirty runtime | commit 仅 docs 测报 | **PASS** |

---

## 阻塞点

无。

---

## 交付

- 报告：`docs/status/STATUS-ember-reward-ni-id-align-test.md`
- tip SHA：`dc8ebb8`
- ops：`[]`
- push：见本 commit 是否已 push（失败则交总控代推）
