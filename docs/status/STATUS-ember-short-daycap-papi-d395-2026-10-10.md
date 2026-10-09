# 状态 · D395：短征日帽 PAPI（批 A·M · §2.2）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 @`7b43f741` · DESIGN [`DESIGN-ember-short-menu-polish-2026-10-10.md`](../design/DESIGN-ember-short-menu-polish-2026-10-10.md) **§2.2**  
**裁决：** **本号交** 只读 Placeholder + 单测 + 装 play · **TrMenu 挂键另号（菜单岗 §2.3）** · **未动** daily_cap / 结算金额 / 体力 / afk / gate_daily / 观察三开关

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `%corerpg_p1_sx01_day%` 等 | EmberRunPapi **SHORT** 段；真源 EmberCounters `p1_sx0N_day` + `EmberShortService.rewardedToday` | — |
| `%corerpg_p1_sx0N_day_line%` | 复用 `EmberShortRules.dayLine`（`今日有奖 n/3` / 已满行） | — |
| 可选 | `sx0N_day_left` · `sx_day_left_sum` | — |
| 路由 | `isShortDayKey` 在 MAP 尾之前；`sx01_state` 等 map 字段仍走 MAP | — |

## 键清单

| Placeholder | 含义 | 空/异常 |
|-------------|------|---------|
| `%corerpg_p1_sx01_day%` | sx01 今日已有奖次数 | `0` |
| `%corerpg_p1_sx02_day%` | sx02 今日已有奖次数 | `0` |
| `%corerpg_p1_sx03_day%` | sx03 今日已有奖次数 | `0` |
| `%corerpg_p1_sx01_day_line%` | `今日有奖 n/3` 或已满行 | 恒有 |
| `%corerpg_p1_sx02_day_line%` | 同上 | 恒有 |
| `%corerpg_p1_sx03_day_line%` | 同上 | 恒有 |
| `%corerpg_p1_sx01_day_left%` | 单本剩余 `max(0,3-n)`（可选） | `3`/`0` |
| `%corerpg_p1_sx02_day_left%` | 同上 | |
| `%corerpg_p1_sx03_day_left%` | 同上 | |
| `%corerpg_p1_sx_day_left_sum%` | 三本剩余合计（可选） | `0+` |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · S40–S42 金额 · `daily_cap` · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-route | SHORT 路由；sx01_state 仍 MAP | EmberRunPapiTest |
| V-line | dayLine / dayLeft / claimKey | EmberShortRulesTest |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip |  |
| jar | `CoreRpg-1.65.108-d395.local.jar` |
| sha256 |  |
| Enabling | `CoreRpg v1.65.108-d395.local` |
