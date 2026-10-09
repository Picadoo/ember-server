# 状态 · D400：短征 sx05 键 + S44 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN [`DESIGN-ember-short-dungeon-sx05-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx05-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第五本 + DP idle 核对 + 装 play · **地图 WE / TrMenu 挂键另号**（MM/DP idle 已由并行号就绪）· **未动 afk / gate_daily / 观察三开关 / K3**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx05` | name 裂谷风廊 · EmberSx05 · cost 30 · Q01 · `p1_sx05_day`×3（与 sx01–sx04 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx05* MM 已就绪；坐标参考 sx01 白盒；`map_version …@d400-pending`） | 完整裂谷风廊地形 / Cast 打磨 |
| S44 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx05）」；有奖 **80/4/3**；首通 **140/6/1**（DESIGN 略薄） | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx05_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第五本** | TrMenu 五本选页挂键（菜单壳已有） |
| 代码 | EmberShortRules `economyId` sx05→S44；`p1 enter sx05` 走 short 结算钩 | — |
| DP | `EmberSx05` idle 骨架已就绪（本号核对；Director 刷怪；boss `$end` → CoreRpg settle） | 地图 WE |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx05` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S44 | 有奖/首通/帽后发放；source 标签 S44；前四本日帽不串 | EmberShortRulesTest |
| V-papi | sx05 day/line/left 路由 SHORT；sum 含第五本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **68** | yml |
| V-live | resources ↔ live runs/economy 对齐；six_slot 仍 true | 装服核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip | `c89c0f41` |
| jar | `CoreRpg-1.65.110-d400.local.jar` |
| sha256 | `0aae1701fcbb08bf772bb7408aa76646002d156cda4cc98f8f3f42bf764ca2f9` |
| Enabling | （装服后填） |
