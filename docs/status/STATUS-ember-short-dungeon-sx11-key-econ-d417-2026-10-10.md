# 状态 · D417：短征 sx11 键 + S50 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN tip [`fc951892`](../design/DESIGN-ember-short-dungeon-sx11-2026-10-10.md) · MM/DP idle 已就绪（并行号）  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第十一本 + sx_fc_* 扩扫 + DP idle 核对 + 装 play · **地图 WE / TrMenu 挂键另号** · **未动 afk / gate_daily / 观察三开关 / K3 / ×0.97**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx11` | name 烬镜对廊 · EmberSx11 · cost 30 · Q01 · `p1_sx11_day`×3（与 sx01–sx10 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx11* MM 已就绪；坐标参考 sx01 白盒；`map_version …@d417-pending`） | 完整烬镜对廊地形 / Cast 打磨 |
| S50 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx11）」；有奖 **80/4/3**；首通 **80/6/1** | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx11_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第十一本** | TrMenu 挂盘另号 |
| 首通合计 | `sx11_fc`；`sx_fc_left` / `sx_fc_pending_line` 扩扫 sx11（文案「十一本」） | — |
| 代码 | EmberShortRules `economyId` sx11→S50；`SHORT_KEYS` 含 sx11；`p1 enter sx11` | — |
| DP | `EmberSx11` idle 骨架已就绪（本号核对） | 地图 WE |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx11` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S50 | 有奖/首通/帽后发放；source 标签 S50；前十本日帽不串 | EmberShortRulesTest |
| V-papi | sx11 day/line/left 路由 SHORT；sum/fc 含第十一本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **75** | yml |
| V-live | resources ↔ live runs/economy 外科对齐 | 装服核对 |
| V-play | jar 装 play · Enabling | 见 tip/jar |

## tip / jar

| 项 | 值 |
|----|-----|
| tip | `557781b3` |
| jar | `CoreRpg-1.65.122-d417.local.jar` |
| sha256 | `36b6637d6ed241745f5393c3c447970af210660be75e71bd43a064a1cadc0604` |
| Enabling | `CoreRpg v1.65.122-d417.local` |
| bv | 75 |
