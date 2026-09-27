# STATUS · B0.2 天赋二层 TTK + B1.3 精英 Boss TTK（同测窗）

**日期：** 2026-09-27 22:59～23:16（Asia/Shanghai / CST）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design-ember-content-backlog.md` §B0.2 / §B1.3；总控口径 §7.2.4（二层 vs 一层满全层 ΔTTK &lt;15%；一次跑算清；无法估记债）；精英对照 4.4f（Boss HP 5200、TTK~70s、清本~510s）  
**脚本：** `/tmp/ttk-b0-b1-run.js` · 日志 `/tmp/ttk-b0-b1-run.log` · 原始 JSON `/tmp/ttk-b0-b1-result.json`  
**账号：** 主测 **`TtkR9420`**（非 op）；临时管理 **`TtkOpBot`**（ops + LP `admin`，测后已清）  
**CoreRpg：** **1.15.9**（`latest.log`：`CoreRpg 1.15.9 enabled`）  
**未改：** 怪物 / 掉落 / 票价 / YAML 数值 / jar / MM mtime（Weekly/Elite/Abyss yml mtime 测前测后一致）

---

## 总评

# **条件 FAIL**（B0.2 周本硬条 **PASS** · B1.3 精英 **FAIL** · 团/深渊 **SKIP/债**）

| 项 | 结果 | 摘要 |
|----|------|------|
| **B0.2 周本 Boss ΔTTK** | **PASS** | L1=**20s** · L2=**20s** · **Δ=0%**（&lt;15%） |
| B0.2 团本使徒 | **SKIP live** | 人数门 3～5，单人 enter 拒绝 |
| B0.2 深渊 10·12 | **SKIP / 记债** | live F2 卡死，未采到 L1/L2 对照 |
| **B1.3 精英** | **FAIL** | 进本✓ · 全本至 Boss 后 **超时失败** · Boss 未击杀（TTK=null）· 对照 4.4f 明显变差 |
| 战斗 buff | **PASS** | 全程 `/effect clear`；无 strength/resistance |
| ops | **PASS** | play+login **`[]`** |
| Deep maxHealth WARN | **无** | `spigot.yml` maxHealth.max=**20000**；本窗无 WARN |

**本岗不改数值。** 精英 FAIL 只报数，建议总控派平衡/复测；周本 Δ 已过硬条。

---

## 参照档（同装同誓约）

| 项 | 一层满（基线） | 二层满 |
|----|----------------|--------|
| 等级 | Lv.**60** | Lv.**60**（同号顺序加点） |
| 已花 / 已获 | **20 / 30** | **30 / 30** |
| 节点 | `blaze_root`…`blaze_cap`（不点二层） | + `blaze_ember2` / `blaze_heat2` / `blaze_second` |
| 武器 | T2 刃 **+7** · Sharpness **III** | 同 |
| 护符 | T2 **+4** | 同 |
| 誓约 | blaze | blaze |
| 属性（stats） | 攻 **+53** · 生命 103 · 暴击 5.0% | 攻 **+55** · 生命 103 · 暴击 **5.5%** |
| 补给 | 面包；NI 给票 | 同 |
| 战斗 buff | **无** | **无** |

---

## B0.2 对照表（一层 / 二层 × 本）

| 本 | 一层满 TTK | 二层满 TTK | ΔTTK%（相对一层；+为变快） | 一层结束剩血 | 二层结束剩血 | 判定 |
|----|-----------:|-----------:|---------------------------:|-------------:|-------------:|------|
| **周本 Boss（蛮兵·乙）** | **20s** | **20s** | **0%** | ≈100% | ≈98% | **PASS**（硬条 &lt;15%） |
| 团本·终厅使徒 | — | — | — | — | — | **SKIP live**（人数 3～5） |
| 深渊 10 看守·深 | — | — | — | — | — | **SKIP/债**（F2 卡死） |
| 深渊 12 看守·深 | — | — | — | — | — | **SKIP/债**（F2 卡死） |

### 周本细节

| | L1 | L2 |
|--|---:|---:|
| 进本 | `/corerpg enter weekly` **PASS** | 同 |
| 全本 | 51s | 55s |
| Boss 出现→通关 | **20s** | **20s** |
| 死亡 | 0 | 0 |
| hits / casts | 46 / 6 | 42 / 7 |

Boss 时钟以聊天 `【深核·终局】蛮兵·乙` 起算，至 `深核·周 通关！`。

### 团本

- `/corerpg enter raid` → **「团本人数 3～5，当前 (1)」**  
- `dp start-console` 亦未形成可测使徒战  
- **SKIP live**（设计人数门；无稳定 admin 单刷路径本窗）

### 深渊（债）

- 同窗曾开 live：L1 进本成功，**卡在第 2 层**（hits 停在 11，数分钟无推进）→ 中止以免拖死测窗  
- **记债：** 10/12 看守 L1 vs L2 ΔTTK 本窗无法估  
- 参考既有二层满复测（`STATUS-ember-abyss-ttk-4.3c.md`，非本窗）：TTK10≈**66.1s** / TTK12≈**87.9s**（Health 6000）——仅作背景，**不**计入本窗 Δ 验收

---

## B1.3 精英 vs 4.4f

| 指标 | 窗口 / 4.4f | 本窗（L2 满 · Lv60） | 判定 |
|------|-------------|---------------------|------|
| hub/菜单进本 | 要 | **PASS**（精英试炼开启 · 试炼一） | PASS |
| `ticket_ember_elite`×1 | 要 | 进本成功；计数器采样 ticket_before/after 均为 0（给票与扣票同窗过密，**以进本成功为准**） | 记注 |
| 全本 480～720s | 4.4f **510s** | 至失败 **713s**（落在 480～720 数字窗，但 **未通关**） | 通关 **FAIL** |
| Boss TTK 45～75s | 4.4f **70s** | Boss@**493s** 后战至超时；**未击杀** → TTK=**null**；战至失败约 **220s** | **FAIL** |
| 通关前剩血 25～55% | 4.4f **34%** | 无通关快照；失败时采样 ≈90%（不可作通关验收） | **FAIL** |
| 死亡 / 复活 | 4.4f 1/1 | **1 / 1**（Boss 战中） | 记录 |
| minHp | — | ≈1.46/40 | 记录 |
| 战斗 buff | 无 | **无** | PASS |

### 波次

| | 4.4f | 本窗 |
|--|-----:|-----:|
| 试炼一 | 0s | 0s |
| 试炼二 | 247s | **327s** |
| Boss | 439s | **493s** |
| 通关/结束 | 509s 通关 | **713s 超时失败** |

### 建议（本岗不改）

1. 精英 Boss 在满二层参照档下本窗 **未能在时限内击杀**（相对 4.4f 70s）——优先排查是否 bot 走位/目标选择方差，再考虑是否微削 Boss 或词缀压力。  
2. **不要**借本 FAIL 改票价/周首通稳定符。  
3. 若总控要复测：同脚本 + 同装，可再开一局仅 `ONLY=elite`。

---

## SKIP / 债一览

| 项 | 类型 | 说明 |
|----|------|------|
| 团本使徒 TTK | SKIP live | 人数 3～5 |
| 深渊 10·12 L1/L2 Δ | 债 | F2 卡死，无法估 |
| 精英通关/Boss TTK | FAIL（非 SKIP） | 超时未击杀 |

---

## 环境与清理

| 项 | 值 |
|----|-----|
| CoreRpg | **1.15.9** |
| maxHealth.max | **20000.0**（未改） |
| Deep WARN | **无** |
| `server-runtime/ops.json` | **`[]`** |
| `login-runtime/ops.json` | **`[]`** |
| 临时 OP | `TtkOpBot`（曾 `lp parent set admin` 以跑 `progress`/`enhance set`；测后 deop + ops 清空） |
| MM mtime | Weekly/Elite/Abyss **未变** |

---

## 痕迹

- 脚本：`/tmp/ttk-b0-b1-run.js`  
- JSON：`/tmp/ttk-b0-b1-result.json`  
- 日志：`/tmp/ttk-b0-b1-run.log`  
- 对照：`STATUS-ember-elite-weekly-test-4.4f.md` · `docs/STATUS-ember-b0.md`（B0.2 原挂测窗）

**请总控：** B0.2 周本硬条已过；B1.3 按数 FAIL 派平衡或复测；深渊 Δ 另开测窗。
