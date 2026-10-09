# 状态 · D412：短征 sx09 键 + S48 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN tip [`a35ae9bd`](../design/DESIGN-ember-short-dungeon-sx09-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第九本 + sx_fc_* 扩扫 + DP idle 核对 + 装 play · **地图 WE / TrMenu 挂键另号**（MM/DP idle 已由并行号就绪）· **未动 afk / gate_daily / 观察三开关 / K3 / EmberAfkService next_farm（D411 并行另 jar）**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx09` | name 烬渠跳石 · EmberSx09 · cost 30 · Q01 · `p1_sx09_day`×3（与 sx01–sx08 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx09* MM 已就绪；坐标参考 sx01 白盒；`map_version …@d412-pending`） | 完整烬渠跳石地形 / Cast 打磨 |
| S48 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx09）」；有奖 **80/4/3**；首通 **100/6/1**（DESIGN 略薄） | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx09_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第九本** | TrMenu 九本选页挂键 |
| 首通合计 | `sx09_fc`（MAP+shortMaps）；`sx_fc_left` / `sx_fc_pending_line` 扩扫 sx09（文案「九本」） | — |
| 代码 | EmberShortRules `economyId` sx09→S48；`SHORT_KEYS` 含 sx09；`p1 enter sx09` | — |
| DP | `EmberSx09` idle 骨架已就绪（本号核对；Director 刷怪；boss `$end` → CoreRpg settle） | 地图 WE |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · EmberAfkService next_farm · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx09` parse：3 rooms + boss Warden · claim 独立 | 单测 PASS |
| V-S48 | 有奖/首通/帽后发放；source 标签 S48；前八本日帽不串 | EmberShortRulesTest PASS |
| V-papi | sx09 day/line/left 路由 SHORT；sum/fc 含第九本 | EmberRunPapiTest PASS |
| V-bv | runs `balance_version` **73** | yml |
| V-live | resources ↔ live runs/economy 外科对齐；six_slot 仍 true | 装服核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip | `13583cfe` |
| jar | `CoreRpg-1.65.119-d412.local.jar` |
| sha256 | `fab84c141987b62f66c0042b7984f6c730df1972d9524c69bbb99ebeb847959a` |
| Enabling | `CoreRpg v1.65.119-d412.local` · play PID 1317630 |
