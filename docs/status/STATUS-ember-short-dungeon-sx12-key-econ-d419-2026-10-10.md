# 状态 · D419：短征 sx12 键 + S51 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN tip [`a72f4d05`](../design/DESIGN-ember-short-dungeon-sx12-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第十二本 + sx_fc_* 扩扫 + DP idle 壳 + 装 play · **地图 WE / TrMenu 挂键 / MM 完整另号** · **未动 afk / gate_daily / 观察三开关 / K3 / ×0.97**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx12` | name 烬枢转厅 · EmberSx12 · cost 30 · Q01 · `p1_sx12_day`×3（与 sx01–sx11 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx12* MM 另号；坐标参考 sx01 白盒；`map_version …@d419-pending`） | 完整烬枢转厅地形 / Cast 打磨 / MM |
| S51 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx12）」；有奖 **80/4/3**；首通 **70/6/1** | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx12_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第十二本** | TrMenu 挂盘另号 |
| 首通合计 | `sx12_fc`；`sx_fc_left` / `sx_fc_pending_line` 扩扫 sx12（文案「十二本」） | — |
| 代码 | EmberShortRules `economyId` sx12→S51；`SHORT_KEYS` 含 sx12；`p1 enter sx12` | — |
| DP | `EmberSx12` idle 骨架本号落壳 | 地图 WE / MM |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx12` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S51 | 有奖/首通/帽后发放；source 标签 S51；前十一本日帽不串 | EmberShortRulesTest |
| V-papi | sx12 day/line/left 路由 SHORT；sum/fc 含第十二本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **76** | yml |
| V-live | resources ↔ live runs/economy 外科对齐 | 装服核对 |
| V-play | jar 装 play · Enabling | 见 tip/jar |

## tip / jar

| 项 | 值 |
|----|-----|
| tip | `86744492` |
| jar | `CoreRpg-1.65.123-d419.local.jar` |
| sha256 | `87826547d375714ea0e5ee63aa0280e4c9dd40c77adb278986f3e21efb554749` |
| Enabling | `CoreRpg v1.65.123-d419.local` |
| bv | 76 |
