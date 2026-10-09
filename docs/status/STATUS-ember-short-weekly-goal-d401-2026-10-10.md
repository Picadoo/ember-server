# 状态 · D401：短征可选周目标钩（批 A·M · W1a–W1c）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN [`DESIGN-ember-short-weekly-goal-2026-10-10.md`](../design/DESIGN-ember-short-weekly-goal-2026-10-10.md)  
**裁决：** **本号交** 源表 short:5 + OPTIONAL + 有奖结算 addGoal + PAPI goal_short / goals 纪律 · **TrMenu W1d 另号** · **未动 afk / gate_daily / 观察三开关 / stamina / S40–S44 量**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `weekly_goals.targets` | 增 `short: 5`；reward/bonus 与 featured/abyss/raid/bounty/core 目标数未改 | — |
| `EmberSeason.OPTIONAL` | 增 `"short"`（**不**入 GOALS） | — |
| `goalName` / `goalOpen` | 「周目标 · 短征（可选）」→ `/ember_p1_short` | 赛季页短征格（W1d） |
| 结算钩 | `EmberShortService.settle` 有奖成功 → `season.addGoal(…,"short",1)`；第4+/失败不计；Q07 闸沿用 addGoal | — |
| PAPI | `%corerpg_p1_goal_short%`（goalLine）；`%corerpg_p1_goals%` 仍只计必做四键（对齐 core） | TrMenu 挂键 |
| 完成奖 | 复用 S19 +15 余烬徽；不算全部完成 / 不单独触发 bonus | — |
| bv | **69** | — |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · stamina · S40–S44 发放量 · 把 short 塞进必做 GOALS · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V1 | targets 含 short:5；其余目标未抬 | 单测 + yml |
| V2–V5 | 有奖计 / 帽后不计 / Q07 闸 / 完成奖 S19 | 钩+addGoal 内建；单测 countsTowardShortGoal |
| V-papi | goal_short → SEASON；goals 分母仍 4 | EmberRunPapiTest |
| V-bv | runs `balance_version` **69** | yml |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|------|
| tip | `746a5259` |
| jar | `CoreRpg-1.65.111-d401.local.jar` |
| sha256 | `12d954dcc7b8745fec8350fb7c7c6d48abc8fc8e8e9725d6df91563e067fa6b4` |
| Enabling | `CoreRpg v1.65.111-d401.local` · play PID 1113657 |
