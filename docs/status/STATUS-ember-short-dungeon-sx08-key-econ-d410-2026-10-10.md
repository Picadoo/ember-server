# 状态 · D410：短征 sx08 键 + S47 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN tip [`2f4ff19d`](../design/DESIGN-ember-short-dungeon-sx08-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第八本 + sx_fc_* 扩扫 + DP idle 核对 + 装 play · **地图 WE / TrMenu 挂键另号**（MM/DP idle 已由并行号就绪）· **未动 afk / gate_daily / 观察三开关 / K3**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx08` | name 错层烬庭 · EmberSx08 · cost 30 · Q01 · `p1_sx08_day`×3（与 sx01–sx07 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx08* MM 已就绪；坐标参考 sx01 白盒；`map_version …@d410-pending`） | 完整错层烬庭地形 / Cast 打磨 |
| S47 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx08）」；有奖 **80/4/3**；首通 **110/6/1**（DESIGN 略薄） | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx08_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第八本** | TrMenu 八本选页挂键 |
| 首通合计 | `sx08_fc`（MAP+shortMaps）；`sx_fc_left` / `sx_fc_pending_line` 扩扫 sx08（文案「八本」） | — |
| 代码 | EmberShortRules `economyId` sx08→S47；`SHORT_KEYS` 含 sx08；`p1 enter sx08` | — |
| DP | `EmberSx08` idle 骨架已就绪（本号核对；Director 刷怪；boss `$end` → CoreRpg settle） | 地图 WE |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx08` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S47 | 有奖/首通/帽后发放；source 标签 S47；前七本日帽不串 | EmberShortRulesTest |
| V-papi | sx08 day/line/left 路由 SHORT；sum/fc 含第八本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **72** | yml |
| V-live | resources ↔ live runs/economy 对齐；six_slot 仍 true | 装服核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip | `24ffa712` |
| jar | `CoreRpg-1.65.118-d410.local.jar` |
| sha256 | `922a0c8124f2f3cca1d4fbe33898e1d0ec5096b32bba85b46aff4f5a91959c16` |
| Enabling | `CoreRpg v1.65.118-d410.local` · play PID 1258611 |
