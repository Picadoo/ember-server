# 状态 · D397：短征 sx04 键 + S43 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 @`0ffa02f2` · DESIGN [`DESIGN-ember-short-dungeon-sx04-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx04-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第四本 + DP idle + 装 play · **地图 WE / TrMenu 四本选页另号**（MM/DP idle 已由并行号就绪）· **未动 afk / gate_daily / 观察三开关 / K3**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx04` | name 烬井螺旋 · EmberSx04 · cost 30 · Q01 · `p1_sx04_day`×3（与 sx01–sx03 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx04* 占位 MM；坐标参考 sx01 白盒；`map_version …@d397-pending`） | 完整烬井螺旋地形 / Cast 打磨 |
| S43 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx04）」；有奖 **80/4/3**；首通 **150/6/1**（DESIGN 略薄） | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx04_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第四本** | TrMenu 四本选页挂键 |
| 代码 | EmberShortRules `economyId` sx04→S43；`p1 enter sx04` 走 short 结算钩 | — |
| DP | `EmberSx04` idle 骨架（Director 刷怪；boss `$end` → CoreRpg settle） | 地图 WE |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx04` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S43 | 有奖/首通/帽后发放；source 标签 S43；前三本日帽不串 | EmberShortRulesTest |
| V-papi | sx04 day/line/left 路由 SHORT；sum 含第四本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **67** | yml |
| V-live | resources ↔ live runs/economy 对齐；six_slot 仍 true | 装服核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip |  |
| jar | `CoreRpg-1.65.109-d397.local.jar` |
| sha256 |  |
| Enabling |  · play PID 1057991 |
| bv | runs **67** · six_slot 三 true |
