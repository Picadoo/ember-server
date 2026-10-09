# 状态 · D392：短征 sx02 键 + S41（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 @`5bb2c41c` · DESIGN [`DESIGN-ember-short-dungeon-sx02-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx02-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + DP idle + 装 play · **地图/MM/菜单另号** · **未动 afk / gate_daily / 观察三开关 / K3**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx02` | name 锈灯栈道 · EmberSx02 · cost 30 · Q01 · `p1_sx02_day`×3（与 sx01 分开） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx02* 占位 MM；坐标参考 sx01 白盒；`map_version …@d392-pending`） | 完整栈道地形 / Cast 打磨 |
| S41 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx02）」；有奖 80/4/3（同 S40）；首通 **180/6/1**（略薄） | p1sim 薄模型 |
| 代码 | EmberShortRules/Service 扩多本（sx01→S40 / sx02→S41）；`p1 enter sx02` 走 short 结算钩 | TrMenu 双本选页 |
| DP | `EmberSx02` idle 骨架（Director 刷怪；boss `$end` → CoreRpg settle） | 地图 WE / MM 真怪 |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx02` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S41 | 有奖/首通/帽后发放；source 标签 S41；sx01 日帽不串 | EmberShortRulesTest |
| V-bv | runs `balance_version` **65** | yml |
| V-live | resources ↔ live runs/economy 对齐；six_slot 仍 true | 装服核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip | `878b9938` |
| jar | `CoreRpg-1.65.106-d392.local.jar` |
| sha256 | `82de57177477cef3e3ce593bc1b5606cb2bf666e5dde388b964706968415ac3d` |
| Enabling | `CoreRpg v1.65.106-d392.local` · play PID 1004829 |
| bv | runs **65** · six_slot 三 true |
