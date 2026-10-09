# 状态 · D405 抽测：挂机 remain / eta / remain_line

**日期：** 2026-10-10（上海时间）  
**号：** D405Spot · 证据 `/workspace/tmp/d405-afk-remain/`  
**上游：** tip 键 [`7123d944`](https://github.com/Picadoo/ember-server/commit/7123d944) · 菜单 tip [`555bf9a5`](https://github.com/Picadoo/ember-server/commit/555bf9a5) · jar `1.65.114-d405.local`（sha256 `2098ca89…`）· DESIGN [`DESIGN-ember-afk-remain-eta-visible-2026-10-10.md`](../design/DESIGN-ember-afk-remain-eta-visible-2026-10-10.md) · 插件 STATUS [`STATUS-ember-afk-remain-eta-papi-d405-2026-10-10.md`](STATUS-ember-afk-remain-eta-papi-d405-2026-10-10.md) · 菜单 STATUS [`STATUS-ember-afk-remain-eta-visible-menu-d405-2026-10-10.md`](STATUS-ember-afk-remain-eta-visible-menu-d405-2026-10-10.md)  
**范围：** 活体 PAPI + 战况同屏 + 纯函数单测 · **≠关观察** · **未改** daily_kills / afk.tiers / Stage2 三开关 / gate_daily · **未切分支** · **未碰** login/proxy/MariaDB  
**总结果：** **PASS**

## 人话

打开挂机战况能看见「还差 X 只 · 约 Y 分钟满」。未测速写「开打后估时」；已满由单测钉 `0` / 「今日已满 · 去冒险 / 去哪花」。产量日顶仍 2400，没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| V0 | six_slot 三开 true · daily_kills=2400 前后未变 | **PASS** | `00-switches-pre.json` / `90-endstate.json` |
| S0 | 战况 I 挂 `%corerpg_p1_afk_remain_line%` · Open/hub 半行 · 无假「保证分钟满」 | **PASS** | `10-menu-static.json` · tip `555bf9a5` |
| V-pure | remain/eta/line 满=0 · 无 kph「开打后估时」· ceil≥1 | **PASS** | `15-unit-remainEta.txt` · `EmberAfkServiceTest#remainEta_D405` |
| L0 | 未满 remain=**2400−today**；无 kph →「开打后估时」；line 同义 | **PASS** | `run.log` 早期 `/papi parse me`：`2400` / `开打后估时` / `还差 2400 只 · 开打后估时` |
| L1 | 有击杀+有 kph：remain=2400−today；eta≈ceil(remain/kph×60)；line「约 N 分钟满」 | **PASS** | 战况 lore：今日 **9/2400** · **还差 2391** · **约 448 分钟满** · 速度 320 只/小时（`run.log` L1_same_screen；`50-math-cases.json`） |
| L-screen | 同屏可见 remain_line（非裸 `%corerpg_…%`） | **PASS** | `40-afk-window-base.json` · L1 lore |
| V4 | gate_daily 仍拒（`no`） | **PASS** | `60-gate-daily.json` |
| V-full | 已满=0 | **PASS（单测代理）** | 活体未灌满 2400（禁 MariaDB / 禁改日顶）；纯函数与 live 同路径 |

## 关键证据摘要

- tip 键 `7123d944` · 菜单 `555bf9a5` · 均为 `main` 祖先（HEAD 于抽测时含二者）
- jar 抽测窗：`CoreRpg v1.65.114-d405.local` · sha256 `2098ca8935e74c612a0b737c547f4f8e53a21c288bde6ad600f1a34f4f7e910e`
- 旁注：并行 D406 随后换装 `1.65.115-d406.local`（remain 键加性保留）；run2 中段遇服重启，结案以 **d405 窗活体 + 单测** 为准
- 测号 `D405AfkEta` · Q01 首通后进灰坡自动战斗
- 数学抽查：today=9 → remain=2391；kph≈320 → ceil(2391/320×60)=449（显示 448，±2 内）

## 不动确认

daily_kills / afk.tiers · Stage2 三开关 / ×0.97 · gate_daily · K3 · 关观察 · 切分支 · login/proxy/MariaDB

## 总控旁注

**≠关观察**。D405 挂机还差/约满抽测结案 PASS；已满活体灌顶未做（纪律），单测已钉。

---

*D405Spot · PASS · tip `7123d944`+`555bf9a5` · jar `1.65.114-d405.local` · 证据 `/workspace/tmp/d405-afk-remain/` · ≠关观察 · checked 2026-10-10 07:03 CST。*
