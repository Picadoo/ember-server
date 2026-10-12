# RESEARCH · 套装手感 · 觉醒升档白话薄窗 · 2026-10-12

> **状态：** 调研定稿 · 对接 tip `TIP-ember-set-awaken-feel-thin-d12392-d12407-2026-10-12.md`  
> **上游：** [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · D439 触发短闪 · D457 SetFeel · D4385 AwakenBreak · TIP-J 停 glance densify  
> **硬禁：** 零 Affix densify · 零伤乘 / 抬 `BURN_COEF`/`BURST_*`/`SUSTAIN_PCT` · 零 glance twin · 零 skill_mult

---

## 0. 为何挑这条

总控 TIP-O CLOSED 后要变强途径薄窗（工坊选择 / 套装手感 / 图装身份 / 可见保底），**非加难**；glance 轴 TIP-J 已停。  
盘点：工坊稳赌/commit/forgegoal、图装 methodLine/农场闭环、宝石/强化保底表面化 **多已齐**。  
套装侧：成套形状软切（D607）+ 触发短闪（D439）+ 枢纽套装格（TIP-D）已有；**觉醒 I→II→III 突破瞬间只喊罗马数字**，玩家听不见「这档套装手感具体强在哪」——变强途径断在套装深度，不是再铺枢纽一眼。

---

## 1. 现网缺口（码）

| 项 | 现网 | 缺口 |
|----|------|------|
| 觉醒突破 | `EmberAwakenBreak.maybeAfterAwkUp`：`觉醒突破 · 觉醒II · {套名}` + AB | **无**对照 `EmberSetRules` 的白话差（燃/爆/愈系数、烬爆半径） |
| 成套追 | `EmberSetChase` awkUp → `tell` + 工坊钮 | 同：不解释档差 |
| SetFeel | `runLine` 本局套装+觉醒罗马+四件套 | 进本可读；**≠**升档瞬间的「变强了什么」 |
| 表 | `BURN_COEF` / `BURST_COEF`+`RADIUS` / `SUSTAIN_PCT` 按觉醒 1..3 | **真源已有**；只缺读表→人话 |

工坊选择感 / 可见保底 / 图装身份一眼：本窗不抢（TIP-B/H/E/C/L 已收；再动易 densify）。

---

## 2. 薄施工边界

| 做 | 不做 |
|----|------|
| 觉醒升档时一行族向白话（读现网表，**不改表**） | 改任何 coef / every / ICD / skill_mult |
| 挂在 `AwakenBreak` 突破消息后（可热关） | 枢纽新 glance 格 / 新 hope PAPI |
| 例：烬爆「圈伤更厚 · 半径略扩」；焚烬「燃跳更烫」；炽愈「回血略增」——数字可省略或只写相对词 | Affix densify / 四件套伤减改值 |

门禁：仅文案读表 → **不跑 p1sim**（零战斗表变更）。

---

## 3. 对照

| 号 | 性质 |
|----|------|
| D439 | 触发瞬间可读（proc） |
| D607 / TIP-F | 成套→烬斩形状 |
| TIP-D | 枢纽成套追一眼 |
| **本窗** | 觉醒升档→套装手感白话（变强途径深度，非 glance） |
