# 状态 · D411 抽测：挂机 next_farm 养签

**日期：** 2026-10-10（上海时间）  
**号：** D411Spot · 证据 `/workspace/tmp/d411-next-farm/`  
**上游：** 键 tip [`3c59bab5`](https://github.com/Picadoo/ember-server/commit/3c59bab5) · 菜单 tip [`e87433db`](https://github.com/Picadoo/ember-server/commit/e87433db) · jar live `1.65.119-d412.local`（叠装；next_farm 键仍在 · 上游 d411 `3c59bab5`）· DESIGN [`DESIGN-ember-afk-next-farm-visible-2026-10-10.md`](../design/DESIGN-ember-afk-next-farm-visible-2026-10-10.md) · 插件 STATUS [`STATUS-ember-afk-next-farm-papi-d411-2026-10-10.md`](STATUS-ember-afk-next-farm-papi-d411-2026-10-10.md) · 菜单 STATUS [`STATUS-ember-afk-next-farm-menu-d411-2026-10-10.md`](STATUS-ember-afk-next-farm-menu-d411-2026-10-10.md)  
**范围：** 活体 Open/I 同屏 + 态机抽样 + 纯函数单测 · **≠关观察** · **未改** daily_kills / afk.tiers / Stage2 三开关 / gate_daily · **未切分支** · **未无故停服**（与 D409/D412 并行）  
**总结果：** **PASS**

## 人话

打开挂机战况就能看见「下一层养签：…」真 PAPI：未解锁继续养、打稳前继续打稳、满额让位去冒险/短征/工坊、已在最高打满去花。Open tell 有「战况有下一层养签」。D383 静态层差和 remain_line 都还在。没抬产量。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| V0 | six_slot 三开 true · daily_kills=2400 前后未变 · t1 coin=60 | **PASS** | `00-switches-pre.json` · `90-endstate-v2.json` |
| S0 | I 挂 `next_farm`+「下一层养签」· Open「战况有下一层养签」· 无假写固定层名个人态 | **PASS** | `10-menu-static.json` · tip `e87433db` |
| V-pure | 满/最高/未解锁/可试/未打稳/hint 关 | **PASS** | `15-unit-nextFarm.txt` · `EmberAfkServiceTest#nextFarm_D411_stateMachine` |
| V1 Open | Open tell「战况有下一层养签」 | **PASS** | `40b-window-locked.json` · `42c-window-full.json` |
| V1 I | 同屏 `下一层养签：…` 已解析（非裸 `%corerpg_…%`） | **PASS** | `40b` / `41b` / `42c` / `55b` |
| L-locked | 未解锁下一层 →「本层继续养 · 更高层需通主线」 | **PASS** | `20b-papi-locked.json` · I lore |
| L-未打稳 | Q03 已解锁 · 短挂 →「本层继续打稳 · 满速后再试荒原」 | **PASS** | `30b-papi-unlocked.json` · `41b-window-unlocked.json` |
| L-满额 | afk_full=1 ·「今日已满 · 去冒险/短征/工坊」同屏 | **PASS** | `50c-papi-full.json` · `42c-window-full.json` · 测号 `D405AfkEta` |
| L-最高 | T4 烬原深处 →「已是最高 · 打满去花或短征」 | **PASS** | `55b-tmax.json` · 测号 `D411NfMax` |
| L-可试 | 打稳可试+层差物 | **PASS（单测代理）** | 活体未养满 sess≥40/kph≥800 带；纯函数与 live 同路径 |
| V3 | remain_line 仍在 · D383 层差一览三行仍在 | **PASS** | 各 window lore · `10-menu-static` |
| V4 | gate_daily=`no` | **PASS** | `60b-gate.json` |
| V5 | 零改 daily_kills/tiers/Stage2 · 无假喊抬产 | **PASS** | endstate · `L_no_false_raise` |

## 关键证据摘要

- tip 键 `3c59bab5` · 菜单 `e87433db` · 均为 `main` 祖先  
- jar 抽测窗：`CoreRpg v1.65.119-d412.local`（sha256 `fab84c14…`）· 类内含 next_farm 文案串  
- 测号：`D411NfA`（锁/未打稳）· `D405AfkEta`（满额既有键 2400）· `D411NfMax`（T4）  
- 活体态样例：锁=`本层继续养…` · 未打稳=`本层继续打稳 · 满速后再试荒原` · 满=`今日已满 · 去冒险/短征/工坊` · 最高=`已是最高 · 打满去花或短征`  
- 旁注：代理下 `papi parse <名>` 常 Failed to find player → 改 `/papi parse me`；新号离线 INSERT `p1_afk_kill` 会被 onJoin 冲掉（同 D408 旁注）→ 满额用既有键号 REPLACE/沿用

## 不动确认

daily_kills / afk.tiers · Stage2 三开关 / ×0.97 · gate_daily · K3 · 关观察 · 切分支 · 无故停服

## 总控旁注

**≠关观察**。D411 挂机 next_farm 养签抽测结案 PASS；可试态活体未养满（纪律/时长），单测已钉。

---

*D411Spot · PASS · tip `3c59bab5`+`e87433db` · jar `1.65.119-d412.local` · 证据 `/workspace/tmp/d411-next-farm/` · ≠关观察 · checked 2026-10-10 08:04 CST。*
