# 状态 · D405：挂机 remain / eta PAPI（批 A·M · W1a+W1b）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN [`DESIGN-ember-afk-remain-eta-visible-2026-10-10.md`](../design/DESIGN-ember-afk-remain-eta-visible-2026-10-10.md) **§2.1**  
**裁决：** **本号交** 只读派生 Placeholder + 单测 + 装 play · **TrMenu W1c/d 另号** · **未动** daily_kills / afk.tiers / UpgradeRules / gate_daily / 观察三开关 / settle

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| remain / eta_min | `EmberAfkService.papi` 扩键；纯函数 `remainKills` / `etaMinText` | — |
| remain_line（可选同批） | `remainLine` 半行拼装 | — |
| 路由 | 既有 `afk_*` → GROWTH → `EmberAfkService.papi` | — |
| 战况挂键 | — | W1c/d TrMenu 另号 |

## 键清单（STATUS 钉死）

| Placeholder | 公式 / 行为 | 已满 | 未测速 |
|-------------|-------------|------|--------|
| `%corerpg_p1_afk_remain%` | `max(0, daily_kills − kills)` | **`0`（数字）** | 同左 |
| `%corerpg_p1_afk_eta_min%` | 未满且 kph>0 → `ceil(remain/kph*60)`（remain>0 至少 1） | **`0`（数字）** | **`开打后估时`** |
| `%corerpg_p1_afk_remain_line%` | `还差 X 只 · 约 Y 分钟满` | `今日已满 · 去冒险 / 去哪花` | `还差 X 只 · 开打后估时` |

**纪律：** 只读派生；禁暗示抬高 2400；禁「保证 X 分钟必满」；离线折算不另造假 ETA。

## 不动

daily_kills / afk.tiers / 离线% · UpgradeRules · gate_daily · 观察三开关 / ×0.97 · K3 · settle 结算 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-pure | remain/eta/line 边界（满/无 kph/ceil≥1） | EmberAfkServiceTest.remainEta_D405 |
| V-route | afk_remain / eta_min / remain_line → GROWTH | EmberRunPapiTest |
| V-cfg | daily_kills 仍 2400 | EmberAfkServiceTest |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|-----|
| tip | _(commit 后填)_ |
| jar | `CoreRpg-1.65.114-d405.local.jar` |
| sha256 | _(装服后填)_ |
| Enabling | `CoreRpg v1.65.114-d405.local` |

## 下一号

菜单岗 W1c/d：`ember_p1_afk` 格 I 软目标块挂 remain/eta 或 `remain_line`。
