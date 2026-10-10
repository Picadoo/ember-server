# 状态 · D418 派测：工坊速览挂 remain/eta（薄抽 · 菜单）

**日期：** 2026-10-10（上海时间）  
**号：** D418Spot · 证据 `/workspace/tmp/d418-forge-remain-spot/`  
**上游：** 菜单 tip [`9cfe956c`](https://github.com/Picadoo/ember-server/commit/9cfe956c) · 批旁注 [`28346bd4`](https://github.com/Picadoo/ember-server/commit/28346bd4) · DESIGN [`DESIGN-ember-forge-recipes-remain-eta-2026-10-10.md`](../design/DESIGN-ember-forge-recipes-remain-eta-2026-10-10.md) · 菜单 STATUS [`STATUS-ember-forge-recipes-remain-eta-menu-d418-2026-10-10.md`](STATUS-ember-forge-recipes-remain-eta-menu-d418-2026-10-10.md) · D405 remain_line live  
**范围：** `ember_p1_forge_recipes` A 格 `%corerpg_p1_afk_remain_line%` · V/Open「速览可看今日还差/约满」· C/G/静态约保留 · 回挂机庭 · **零 jar** · **≠关观察** · **未改** daily_kills / afk.tiers / Stage2 三开关 / gate_daily · **未切分支** · **未碰** login/proxy/MariaDB  
**总结果：** **PASS**

## 人话

打开配方速览：A「挂机打满对照」末行真显「今日挂机：还差 2400 只 · 开打后估时」（与挂机战况同量级）；Open /「回挂机庭」半行「速览可看今日还差/约满」。挂机推进后 PAPI 还差 2400→2398 且出现「约 N 分钟满」。仓差 G / 仓内 C / 静态约表仍在。满额活体灌顶未做（纪律）；静态无「还能挂出今日日量」假喊。产量/Stage2/gate_daily 一行没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| S1 | 静态：A 挂 remain_line（非假写固定分钟）；V/Open 半行；C vault + G gap + 静态四层约；文案无保证升阶/sx12/教裸/还能挂出日量 | **PASS** | `10-menu-static.json` |
| V1 | 未满打开速览 → A 真解析 remain_line（非 `%corerpg_`）· 与 `/papi parse me` / 挂机战况同量级 2400 | **PASS** | `30-recipes-window-base.json` · `20-papi-base.json` · `31-afk-window-compare.json` |
| V2 | 挂机击杀推进后 remain/line 随进度变（2400→2398 · 开打后估时→约 1199 分钟满） | **PASS** | `40-papi-mid-afk.json` · `41-recipes-window-mid.json` |
| V3 | 日量已满诚实态；无「还能挂出今日日量」 | **PASS（静态代理）** | `50-v3-full.json` · 活体灌满 SKIP（禁 setkills/禁改日顶）；D405 满额文案同路径 |
| V4 | 仓差 G / 仓内 C / 静态约 A 仍在；不保证坐满必够升阶 | **PASS** | `30-recipes-window-base.json` |
| V5 | 点「回挂机庭」→ ember_p1_afk；战况 remain/仓差仍在 | **PASS** | `55-v5-back-afk.json` |
| V6 | Open/V 半行；无保证升阶/旧日常窟假开/sx12/教裸指令 | **PASS** | `10-menu-static.json` · Open hits |
| V7 | `%corerpg_gate_daily%` = `no` | **PASS** | `60-gate-daily.json` |
| V8 | six_slot 三开关 true · daily_kills=2400 · afk.tiers md5 未变 · 本号零改 jar | **PASS** | `00-switches-pre.json` · `90-endstate.json` |

## 关键证据摘要

- 施工 tip `9cfe956c` · 批旁注 `28346bd4` · recipes md5 `553301792563f0c83de7b8c9dc727f2e`  
- live jar 抽测窗：`1.65.123-d419.local`（**零 jar 本号**；并行 D419 换装；remain_line 键 D405 加性保留）sha256 `87826547…` · bv **76**  
- 测号 `D418ForgeEta2` · 起止约 **2026-10-10 09:30～09:32 CST**  
- V1 同屏：A「今日挂机：还差 2400 只 · 开打后估时」= 战况 I 同行  
- V2：灰坡 afk 击杀 0→2 · PAPI remain 2400→2398 · line「约 1199 分钟满」  
- 汇总 `99-summary.json`：17/17 PASS

## 旁注

- **A 格无 `update:`**：同会话重开速览 lore 可能滞后（V2 重开 A 仍见 2400，PAPI 已 2398）· 与 D404 C/G 旁注同构 · **不挡绿** · 另号可薄加 update。  
- V3 活体灌满 SKIP（无 setkills / 禁 MariaDB 灌顶）；静态无假喊 + D405 满额文案路径。  
- 并行 D419 换装 `1.65.123-d419.local` bv76；本号未改 jar / 未拧日表 / 未动 Stage2。  
- ≠关观察 · ≠交 sx12 本号验收 · ≠开 gate_daily。

## 不动确认

afk.tiers / daily_kills · Stage2 三开关 / ×0.97 · gate_daily · K3 · 关观察 · 切分支 · stash/reset · login/proxy/MariaDB

## 总控旁注

**≠关观察**。D418 工坊速览挂 remain/eta 薄抽 **PASS**（A 真键同屏 + Open/V 半行 + 进度 PAPI 可变 + 零改产量）。

---

*D418Spot · PASS · tip `9cfe956c`(+`28346bd4`) · 证据 `/workspace/tmp/d418-forge-remain-spot/` · ≠关观察 · checked 2026-10-10 09:34 CST。*
