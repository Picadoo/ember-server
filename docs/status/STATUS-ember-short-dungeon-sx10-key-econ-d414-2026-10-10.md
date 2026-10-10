# 状态 · D414：短征 sx10 键 + S49 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN tip [`e052caa9`](../design/DESIGN-ember-short-dungeon-sx10-2026-10-10.md) · MM/骨架 tip `efb26b5d` / `57edf3fc`  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第十本 + sx_fc_* 扩扫 + `isShortDayKey` 支持 sx10 + DP idle 核对 + 装 play · **地图 WE / TrMenu 挂键另号**（MM/DP idle 已由并行号就绪）· **未动 afk / gate_daily / 观察三开关 / K3** · 同批上游 D413 ActionBar tip `cd9a19f2`

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx10` | name 烬闸递室 · EmberSx10 · cost 30 · Q01 · `p1_sx10_day`×3（与 sx01–sx09 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx10* MM 已就绪；坐标参考 sx01 白盒；`map_version …@d414-pending`） | 完整烬闸递室地形 / Cast 打磨 |
| S49 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx10）」；有奖 **80/4/3**；首通 **90/6/1** | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx10_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第十本**；`isShortDayKey` 扩 sxNN（修 sx10 路由） | TrMenu 十本选页挂键另号（骨架已落） |
| 首通合计 | `sx10_fc`；`sx_fc_left` / `sx_fc_pending_line` 扩扫 sx10（文案「十本」） | — |
| 代码 | EmberShortRules `economyId` sx10→S49；`SHORT_KEYS` 含 sx10；`p1 enter sx10` | — |
| DP | `EmberSx10` idle 骨架已就绪（本号核对） | 地图 WE |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx10` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S49 | 有奖/首通/帽后发放；source 标签 S49；前九本日帽不串 | EmberShortRulesTest |
| V-papi | sx10 day/line/left 路由 SHORT；sum/fc 含第十本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **74** | yml |
| V-live | resources ↔ live runs/economy 外科对齐 | 装服核对 |
| V-play | jar 装 play · Enabling | 见 tip/jar |

## tip / jar

| 项 | 值 |
|----|-----|
| tip | _(commit 后填)_ |
| jar | `CoreRpg-1.65.121-d414.local.jar` |
| sha256 | _(装服后填)_ |
| Enabling | `CoreRpg v1.65.121-d414.local` |
| 最终 play | **以本 jar 为准**（含 D413 ActionBar feat） |

## 同批上游

| 号 | tip | jar |
|----|-----|-----|
| D413 ActionBar | `cd9a19f2` | `1.65.120-d413.local` sha `01dee759…`（已被本号叠装） |
