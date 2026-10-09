# 状态 · D391 FAIL 修：short.sx01 补 rooms/boss 同构

**日期：** 2026-10-10（上海时间）  
**上游：** 测岗 FAIL @`dbf001cc` · 总控修单 · 键+S40 tip [`STATUS-ember-short-dungeon-sx01-key-econ-d391-2026-10-10.md`](STATUS-ember-short-dungeon-sx01-key-econ-d391-2026-10-10.md)  
**裁决：** **本号交** Director 同构 rooms/boss + DP idle + 装 play · **未动 afk / gate_daily / 观察三开关 / K3**

## FAIL 根因

短征能进三房通关+Cast，但体力净扣 0、S40 无结算。

| 环节 | 当时 | 结果 |
|------|------|------|
| `short.sx01` | **无** `rooms` / `boss` | Director 无房触发，状态停在 `ENTERED`，**不进 FIGHTING**，不 track MM |
| DP `EmberSx01` | 自管 wave1/wave2/boss + `$end` | 玩家面可通关，但 CoreRpg 未开战 |
| 实例结束 | `onUnload`：`!fightStarted()` → `abort(..., refund=true)` | **退还**预留体力；Boss 击杀结算钩未走 |

## 修复

1. `ember-v1-runs.yml`（src + live）`short.sx01` 补与 Q01 **同构**瘦表：`spawn/safe/holo/mobs/rooms×3/boss/event`；MM=`EmberSx01*`；坐标沿用 Q01 白盒（地图本为拷贝）。
2. DP `EmberSx01` 改 Q01 同构：**idle 无刷怪**；option 只建实例+门闩，不启 wave。
3. `ember-v1.yml` `scope.world_prefixes` 增 `dungeon_EmberSx`（短征实例进 P1 作用域）；**保留** Stage2 `six_slot` 三 true。
4. `EmberRunMaps.validate` 短征改为要求 `MapDef.validate()`（rooms/boss）。
5. 单测 `sx01HasRoomsAndBoss`；结算路径既有 `EmberShortRulesTest` 仍绿。
6. runs `balance_version` **63 → 64**。

## 不动

afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-rooms | `sx01` parse 有 3 rooms + boss Warden | 单测 PASS |
| V-S40 | 有奖/首通/帽后发放仍绿 | EmberShortRulesTest 8/8 |
| V-live | resources ↔ live runs 对齐；six_slot 仍 true | 手术核对 |
| V-play | jar 装 play · Enabling | 见装服 tip |

