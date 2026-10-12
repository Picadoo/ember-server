# RESEARCH · 工坊选择感 · 稳/旁路「选的是」薄窗 · 2026-10-12

> **状态：** 调研定稿 · 对接 tip `TIP-ember-forge-choice-stable-gamble-thin-d12440-d12455-2026-10-12.md`  
> **上游：** [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) §3.2 WS · TIP-B `EmberForgeChoice` · TIP-H 稳赌一眼 · TIP-J 停 glance · TIP-R CLOSED  
> **硬禁：** 零 Affix densify · 零伤乘 / 抬表 / 极品权 · 零 glance twin · 零 K3

---

## 0. 为何挑这条

总控 TIP-R 后要 **工坊选择感深化**，非加难；glance / 图装 / pity 本窗不抢。  
TIP-B 已给烙纹「将得到」、洗练「选的是」、转化周余；TIP-H 枢纽稳/赌一眼已齐。  
缺口在 **预览确认聊天**：成色/精工/强化走稳线时仍像「扣料办事」，随机锻预览有产物说明但 **无** `EmberForgeChoice` 半行——玩家确认前说不清「我选的是稳还是旁路」。

---

## 1. 现网缺口（码）

| 路径 | 已有 | 缺口 |
|------|------|------|
| 烙纹 / 洗练 / 转化 | `EmberForgeChoice.brandWillGet` / `rerollChoice` / `convertLeftover` | 齐 |
| 成色 / 精工预览 | 「确定成功，预览即结果」+ dial | **无**「选的是：稳线·…」 |
| 强化预览 | 成功率 / 必成 | **无**稳线选择半行（可选） |
| 随机锻预览 | 产物/下限说明 + 确认钮 | **无** `选的是：旁路·…`；与稳线目标冲突时无提醒 |

再铺枢纽稳赌格 = glance twin → **否**。

---

## 2. 薄施工边界

| 做 | 不做 |
|----|------|
| `EmberForgeChoice.stableChoice(kind)` / `gambleChoice(floor)` | 改 cost / rate / 极品权 / NEED |
| 挂成色/精工（+可选强化）预览 chat；挂 forgeroll 预览 chat | 枢纽 / forge 主页再 densify |
| 可选：forgeroll 预览时若 forgeGoal∈稳线 → 半行「本周目标是稳线·此为旁路」 | K3 自动跳页 · ConfirmTokens 种类海 |

门禁：纯展示 → **不跑 p1sim**。

---

## 3. 对照

| 号 | 性质 |
|----|------|
| TIP-B D12257–59 | 烙/洗/转选择半行 |
| TIP-H | 稳赌一眼（菜单） |
| **本窗** | 预览确认「选的是」补齐稳/旁路（深化，非 glance） |
