# 状态 · D407：短征 sx07 键 + S46 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN [`DESIGN-ember-short-dungeon-sx07-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx07-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第七本 + sx_fc_* 扩扫 + DP idle 核对 + 装 play · **地图 WE / TrMenu 挂键另号**（MM/DP idle 已由并行号就绪）· **未动 afk / gate_daily / 观察三开关 / K3**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx07` | name 烬塔回升 · EmberSx07 · cost 30 · Q01 · `p1_sx07_day`×3（与 sx01–sx06 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx07* MM 已就绪；坐标参考 sx01 白盒；`map_version …@d407-pending`） | 完整烬塔回升地形 / Cast 打磨 |
| S46 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx07）」；有奖 **80/4/3**；首通 **120/6/1**（DESIGN 略薄） | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx07_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第七本** | TrMenu 七本选页挂键 |
| 首通合计 | `sx07_fc`（MAP+shortMaps）；`sx_fc_left` / `sx_fc_pending_line` 扩扫 sx07（文案「七本」） | — |
| 代码 | EmberShortRules `economyId` sx07→S46；`SHORT_KEYS` 含 sx07；`p1 enter sx07` | — |
| DP | `EmberSx07` idle 骨架已就绪（本号核对；Director 刷怪；boss `$end` → CoreRpg settle） | 地图 WE |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx07` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S46 | 有奖/首通/帽后发放；source 标签 S46；前六本日帽不串 | EmberShortRulesTest |
| V-papi | sx07 day/line/left 路由 SHORT；sum/fc 含第七本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **71** | yml |
| V-live | resources ↔ live runs/economy 对齐；six_slot 仍 true | 装服核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip | `52adffc2` |
| jar | `CoreRpg-1.65.116-d407.local.jar` |
| sha256 | `07bb79e51c01b63094728c1d026995c6c059ceec64d4aa650d8c2bf92757b915` |
| Enabling | `CoreRpg v1.65.116-d407.local` · play PID 1218119 |
