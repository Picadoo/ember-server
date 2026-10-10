# 状态 · D420 派测：工坊回挂机挂 next_farm（薄抽 · 菜单）

**日期：** 2026-10-10（上海时间）  
**号：** D420Spot · 证据 `/workspace/tmp/d420-forge-next-farm-spot/`  
**上游：** 菜单 tip [`fae2b9e3`](https://github.com/Picadoo/ember-server/commit/fae2b9e3) · DESIGN [`DESIGN-ember-forge-recipes-next-farm-2026-10-10.md`](../design/DESIGN-ember-forge-recipes-next-farm-2026-10-10.md) · 菜单 STATUS [`STATUS-ember-forge-recipes-next-farm-menu-d420-2026-10-10.md`](STATUS-ember-forge-recipes-next-farm-menu-d420-2026-10-10.md) · D411 next_farm live · D418 速览 A remain  
**范围：** `ember_p1_forge_recipes` V「回挂机庭」挂 `%corerpg_p1_afk_next_farm%` · Open「回挂机庭可看下一层养签」· A remain / C / G / 静态约保留 · **零 jar** · **≠关观察** · **未改** daily_kills / afk.tiers / Stage2 三开关 / gate_daily · **未切分支** · **未碰** login/proxy/MariaDB  
**总结果：** **PASS**

## 人话

打开配方速览：V「回挂机庭」真显「下一层养签：本层继续养 · 更高层需通主线」（与挂机战况 I / `/papi parse me` 同文）；Open 半行「回挂机庭可看下一层养签」。A remain「今日挂机：还差 2400 只 · 开打后估时」/ 仓内 C / 还差 G / 静态四层约仍在，next_farm 未塞 A。点「回挂机庭」→ 挂机庭，战况 next_farm/remain/仓差仍在。产量/Stage2/gate_daily 一行没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| S1 | 静态：V 挂 next_farm（非假写固定层）；A remain 在且无 next_farm；C vault + G gap + 静态四层约；Open「回挂机庭可看下一层养签」；文案无保证换层必赚/sx13/教裸 | **PASS** | `10-menu-static.json` |
| V1 | 打开速览 → V 真解析 next_farm（非 `%corerpg_`）· 与 `/papi parse me` 同文 | **PASS** | `30-recipes-window-base.json` · `20-papi-base.json` |
| V2 | 与挂机战况 I 的 next_farm 同量级/同语义 | **PASS** | `31-afk-window-compare.json` |
| V3 | A remain_line / 仓差 C·G / 静态约仍在；不与 V 抢同一长块 | **PASS** | `30-recipes-window-base.json` · `10-menu-static.json` |
| V4 | 点「回挂机庭」→ ember_p1_afk；战况 next_farm/remain/仓差仍在 | **PASS** | `55-v4-back-afk.json` |
| V5 | Open 半行「回挂机庭可看下一层养签」 | **PASS** | `30-recipes-window-base.json` openChat |
| V6 | 文案无保证换层必赚/升阶/旧日常窟假开/sx13/教裸指令 | **PASS** | `10-menu-static.json` |
| V7 | `%corerpg_gate_daily%` = `no` | **PASS** | `60-gate-daily.json` |
| V8 | six_slot 三开关 true · daily_kills=2400 · afk.tiers md5 未变 · 本号零改 jar | **PASS** | `00-switches-pre.json` · `90-endstate.json` |

## 关键证据摘要

- 施工 tip `fae2b9e3` · recipes md5 `b93938150f15caf9cb1ff0b8877d7445`  
- live jar 抽测窗：`1.65.123-d419.local`（**零 jar 本号**；并行 D419 换装；next_farm 键 D411 加性保留）sha256 `87826547…` · bv **76**  
- 测号 `D420ForgeNf` · 起止约 **2026-10-10 09:40～09:41 CST**  
- V1/V2 同屏：V「下一层养签：本层继续养 · 更高层需通主线」= 战况 I 同行 = PAPI  
- V3：A「今日挂机：还差 2400 只 · 开打后估时」· 无「下一层养签」抢位  
- 汇总 `99-summary.json`：14/14 PASS

## 旁注

- **零 jar**：本号仅菜单；next_farm 键沿用 D411 live。  
- live 并行 jar `1.65.123-d419.local` bv76；本号未改 jar / 未拧日表 / 未动 Stage2。  
- ≠关观察 · ≠交 sx13 本号验收 · ≠开 gate_daily。

## 不动确认

afk.tiers / daily_kills · Stage2 三开关 / ×0.97 · gate_daily · K3 · 关观察 · 切分支 · stash/reset · login/proxy/MariaDB

## 总控旁注

**≠关观察**。D420 工坊回挂机挂 next_farm 薄抽 **PASS**（V 真键同屏 + Open 半行 + 与战况同语义 + A/C/G 不抢位 + 零改产量）。

---

*D420Spot · PASS · tip `fae2b9e3` · STATUS `TBD` · 证据 `/workspace/tmp/d420-forge-next-farm-spot/` · ≠关观察 · checked 2026-10-10 09:41 CST。*
