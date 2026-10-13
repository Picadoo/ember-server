# STATUS · TIP-CU W1a 短征选页「日差本可冲」chase PAPI · D13326

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **勿勾 CLOSED**（等 live + 物品 W1b/c）

## 交付
- `%corerpg_p1_short_div%`（进度 `n/3`）/ `_n` / `_line`（今日有奖不同本 n/3）
- `%corerpg_p1_short_div_chase%` / `_name` / `_day_line`：已解锁 ∧ dayLeft>0 ∧ rewardedToday==0；稳定序 SHORT_KEYS；todayKey 合格优先；已有奖今日推自然跳过
- `%corerpg_p1_short_div_chase_ok%`（chase 非空→1）/ `%corerpg_p1_short_div_done%`（n≥3 或无 chase→1）
- 软目标 **SOFT_TARGET=3 仅展示零发奖**；解锁谓词 `EmberShortPath.unlocked`
- `/corerpg p1 enter short-div`（别名 `short-day-div` / `short_day_div` / `short_div`）→ 同 chase → tryEnter；无合格诚实拒
- 单测：`EmberShortDayDivChaseTest`（chase/跳过已奖/today 优先/n/ok/done/Q01 ShortPath）；`EmberRunPapiTest` 路由八键
- jar：`1.65.818-d13326.local` · sha256 `1c14b8d3d7a1…` · ECONOMY_BV **123** 未动
- **零改** 日帽 / S40–S58 / weekly_goals.targets.short / Cast / Aid / variety / 庭内 / 今日推算法 / forgeroll·convert 价 / event_core
- **未做** 菜单 W1b/c（物品岗）

## 键清单
| 键 | 口径 |
|----|------|
| `short_div` | 进度 `n/3`（软目标展示） |
| `short_div_n` | 今日有奖不同本数（distinct·已解锁） |
| `short_div_line` | `今日有奖不同本 n/3` |
| `short_div_chase` | 冲图 key；空=无可冲 |
| `short_div_chase_name` | 冲图显示名（无 chase→空） |
| `short_div_chase_day_line` | 冲图今日有奖行（复用 dayLine；无 chase→空） |
| `short_div_chase_ok` | chase 非空→1 否则 0（TrMenu 单条件灰） |
| `short_div_done` | n≥3 或无合格 chase→1 |
| enter `short-div` | 解析为 chase 后同 enter &lt;map&gt; |

## 落点
- `EmberShortDayDivChase`：distinctRewarded / chaseKey / chaseOk / done / line / progress
- `EmberRunPapi`：SHORT 节 `shortDayDiv`；route `short_div*`
- `EmberRunService`：enter 别名 short-div / short-day-div
- `pom.xml` / `plugin.yml`：1.65.818-d13326.local

## 零改边界
- 日帽 cap / S40–S58 / weekly_goals.targets / addGoal
- Cast / Aid / variety / todayRot / 庭内菜单 / forgeroll·convert 价
- balance_version 123 · sx20 · event_core / event_rate

## 下步
- 物品 W1b/c（选页 L + Open 辅半行）等键
