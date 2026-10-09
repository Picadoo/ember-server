# 状态 · D403：短征 sx06 键 + S45 + 日帽扩（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN [`DESIGN-ember-short-dungeon-sx06-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx06-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + 日帽 PAPI 扩第六本 + DP idle 核对 + 装 play · **地图 WE / TrMenu 挂键另号**（MM/DP idle 已由并行号就绪）· **未动 afk / gate_daily / 观察三开关 / K3**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx06` | name 烬环廊 · EmberSx06 · cost 30 · Q01 · `p1_sx06_day`×3（与 sx01–sx05 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx06* MM 已就绪；坐标参考 sx01 白盒；`map_version …@d403-pending`） | 完整烬环廊地形 / Cast 打磨 |
| S45 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx06）」；有奖 **80/4/3**；首通 **130/6/1**（DESIGN 略薄） | p1sim 薄模型 |
| 日帽 PAPI | `p1_sx06_day` / `_day_line` / `_day_left`；`sx_day_left_sum` **纳入第六本** | TrMenu 六本选页挂键（菜单壳已有） |
| 代码 | EmberShortRules `economyId` sx06→S45；`p1 enter sx06` 走 short 结算钩 | — |
| DP | `EmberSx06` idle 骨架已就绪（本号核对；Director 刷怪；boss `$end` → CoreRpg settle） | 地图 WE |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx06` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S45 | 有奖/首通/帽后发放；source 标签 S45；前五本日帽不串 | EmberShortRulesTest |
| V-papi | sx06 day/line/left 路由 SHORT；sum 含第六本 | EmberRunPapiTest |
| V-bv | runs `balance_version` **70** | yml |
| V-live | resources ↔ live runs/economy 对齐；six_slot 仍 true | 装服核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip | _(commit 后回填)_ |
| jar | `CoreRpg-1.65.112-d403.local.jar` |
| sha256 | _(装服后回填)_ |
| Enabling | _(装服后回填)_ |
