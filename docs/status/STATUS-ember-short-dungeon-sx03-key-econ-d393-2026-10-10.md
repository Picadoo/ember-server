# 状态 · D393：短征 sx03 键 + S42（批 A·M）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 @`088fd3b7` · DESIGN [`DESIGN-ember-short-dungeon-sx03-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx03-2026-10-10.md)  
**裁决：** **本号交** 键+经济+rooms/boss + DP idle + 装 play · **地图/MM/菜单另号**（MM/地图岗并行骨架可热更）· **未动 afk / gate_daily / 观察三开关 / K3**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| `short.sx03` | name 霜雾闸廊 · EmberSx03 · cost 30 · Q01 · `p1_sx03_day`×3（与 sx01/sx02 **分开**） | — |
| rooms×3+boss | 首航同构瘦表（EmberSx03* 占位 MM；坐标参考 sx01 白盒；`map_version …@d393-pending`） | 完整霜雾闸廊地形 / Cast 打磨 |
| S42 | REG + EmberEconomy + economy.yml + source-map「短征通关（sx03）」；有奖 **80/4/3**（同 S40/S41 量级）；首通 **160/8/1**（DESIGN 略薄；≠ sx02 180/6/1） | p1sim 薄模型 |
| 代码 | EmberShortRules `economyId` sx03→S42；`p1 enter sx03` 走 short 结算钩（多本已通） | TrMenu 三本选页 |
| DP | `EmberSx03` idle 骨架（Director 刷怪；boss `$end` → CoreRpg settle） | 地图 WE；MM 真怪由并行号 |

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-yml | `sx03` parse：3 rooms + boss Warden · claim 独立 | 单测 |
| V-S42 | 有奖/首通/帽后发放；source 标签 S42；sx01/sx02 日帽不串 | EmberShortRulesTest |
| V-bv | runs `balance_version` **66** | yml |
| V-live | resources ↔ live runs/economy 对齐；six_slot 仍 true | 装服核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

## tip / jar

| 项 | 值 |
|----|----|
| tip | _(commit 后填)_ |
| jar | _(装服后填)_ |
| sha256 | _(装服后填)_ |
| Enabling | _(装服后填)_ |
| bv | runs **66** · six_slot 三 true |
