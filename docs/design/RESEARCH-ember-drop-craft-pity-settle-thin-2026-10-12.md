# RESEARCH · 可见保底 · 精工软保底结算进度薄窗 · 2026-10-12

> **状态：** 调研定稿 · 对接 tip `TIP-ember-drop-craft-pity-settle-thin-d12408-d12423-2026-10-12.md`  
> **上游：** [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) §3.4 Visible pity · TIP-J 停 glance densify · TIP-P CLOSED  
> **硬禁：** 零 Affix densify · 零伤乘 / 抬表 · 零 glance twin · 零改 `NEED`/`FLOOR`/`CRAFT_WEIGHTS`

---

## 0. 为何挑这条

总控 TIP-P 后要变强途径薄窗：**工坊选择感 / 图装身份 / 可见保底深化**，非加难；glance 轴仍停。  
盘点：工坊稳赌/commit/forgegoal、图装 methodLine/农场闭环/共鸣 **多已齐**。  
可见保底：强化/宝石/签名/成色掉落软保 — 成色在结算 **每局** 报进度或触发；**精工软保底**（`EmberDropCraftPity`）仅在 **触发瞬间** 喊一句，进度连空时 **沉默** —— 同轴不对称，玩家摸不到「还差几次」。

---

## 1. 现网缺口（码）

| 项 | 成色 `DropQualityPity` | 精工 `DropCraftPity` |
|----|------------------------|----------------------|
| apply | 结算 `applyToGrants` | 同 |
| 触发喊 | 「成色软保底触发 · ≥精良」+ AB | 「精工软保底触发 · ≥精工1」+ AB |
| **进度 remain** | 未触发时每局 chat+AB `remainLine` | **无**（仅 forced 分支） |
| Hope 枢纽 | HopeMeter 有 dropQuality | **无** craft hope（本窗 **不** 开 glance） |

工坊选择感 / 图装身份一眼：本窗不抢（再动易 densify / hub 挤）。

---

## 2. 薄施工边界

| 做 | 不做 |
|----|------|
| 结算未触发时半行 `DropCraftPity.remainLine`（对齐成色 else 分支） | 改 NEED=5 / FLOOR=1 / 权重表 |
| 可选：触发后仍只一句（已有） | 枢纽新 hope 格 / 新 PAPI |
| 热关可挂既有 settle soft 旗 | Affix · skill_mult · soft gift densify |

门禁：仅结算文案 → **不跑 p1sim**。

---

## 3. 对照

| 号 | 性质 |
|----|------|
| D611 / D1323 | 成色软保 + 结算 remain |
| D6817 | 精工软保（正交轴）· 缺进度表面化 |
| TIP-J SUR densify 停 | **禁** 平行 remainLine 族 / 枢纽再铺；本窗 = **既有 settle 口对齐**，非新族 |
