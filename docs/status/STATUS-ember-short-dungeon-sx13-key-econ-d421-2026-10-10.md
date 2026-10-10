# 状态 · D421：短征 sx13 键 + S52 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN tip [`d61e0d88`](../design/DESIGN-ember-short-dungeon-sx13-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第十三本 + sx_fc_* 扩扫 + DP idle 壳 + 装 play · **地图 WE / TrMenu 挂键 / MM 完整另号** · **未动 afk / gate_daily / 观察三开关 / K3 / ×0.97**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx13` | name 烬衡悬梁 · EmberSx13 · cost 30 · Q01 · `p1_sx13_day`×3（与 sx01–sx12 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx13* MM 另号；坐标参考 sx01 白盒；`map_version …@d421-pending`） | 完整烬衡悬梁地形 / Cast 打磨 / MM |
| S52 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx13）」；有奖 **80/4/3**；首通 **60/6/1** | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx13_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第十三本** | TrMenu 挂盘另号 |
| 首通合计 | `sx13_fc`；`sx_fc_left` / `sx_fc_pending_line` 扩扫 sx13（文案「十三本」） | — |
| 代码 | EmberShortRules `economyId` sx13→S52；`SHORT_KEYS` 含 sx13；`p1 enter sx13` | — |
| DP | `EmberSx13` idle 骨架本号落壳 | 地图 WE / MM |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx13` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S52 | 有奖/首通/帽后发放；source 标签 S52；前十二本日帽不串 | EmberShortRulesTest |
| V-papi | sx13 day/line/left 路由 SHORT；sum/fc 含第十三本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **77** | yml |
| V-live | resources ↔ live runs/economy 外科对齐 | 装服核对 |
| V-play | jar 装 play · Enabling | 见 tip/jar |

## tip / jar

| 项 | 值 |
|----|-----|
| tip |  |
| jar | `CoreRpg-1.65.124-d421.local.jar` |
| sha256 |  |
| Enabling | `CoreRpg v1.65.124-d421.local` |
| bv | 77 |
