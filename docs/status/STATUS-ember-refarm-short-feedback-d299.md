# 状态 · D299：再刷短反馈方案 M（W1a+W1b+W1c）

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-refarm-short-feedback-2026-10-07.md`](../design/DESIGN-ember-refarm-short-feedback-2026-10-07.md) 方案 M  
**版本：** CoreRpg **1.65.89** · `balance_version` **60**（未抬）

## 人话

毕业后再刷仍像赌极品，是因为装备页 / 工坊 / 结算不告诉你「离下一档成色·精工还差什么、这局掉的件比身上好在哪」。本窗**零经济**：只加看得见的近档与对照——不改权重、不改工坊价、不加新材料。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | `ember_p1_gear`：刃/护符 lore 加 `%corerpg_p1_blade_next_q/c%` · `charm_next_q/c%`（当前成色·精工 → 下一档费用 / 极品仅掉落 / 已满） |
| **W1b** | `ember_p1_forge`：精工/成色预览旁 `%corerpg_p1_held_next_c/q%`（手持件当前→下一档+费用摘要；费用仍以聊天为准） |
| **W1c** | 通关发 ITEM 时：相对同部位穿着追加一行 `相对穿着：成色↑/精工↑/…/低于`；**无变化静默**；补领 quietDeliver 不发；失败无 ITEM 对照；不改 D295 破绽摘要 |
| jar | `EmberGearNextHint`；`EmberRunPapi` LOADOUT 键；`EmberRunService.deliver` 对照 |
| 版本 | **1.65.88 → 1.65.89** |

## 不动

- `QUALITY_WEIGHTS` / `CRAFT_WEIGHTS` / 深渊成色表  
- `refineCost` / `qualityCost` 数值（R 后置）  
- `event_rate` / 调律 R / 六槽 / 天赋 / Pack6 / 单局币 / 新材料 / 图专属基础件  
- 玩家面不写 `/dp start`

## 验收

- play Enabling **1.65.89**；login/proxy 尽量不动  
- TrMenu：`blade_next_*` / `held_next_*`  
- `rg 'EmberGearNextHint|blade_next_q|held_next_q|相对穿着' CoreRpg/ plugins/TrMenu/`  
- 单测：`EmberGearNextHintTest` · `EmberRunPapiTest`  
- 回滚：`/workspace/backup/CoreRpg-1.65.88-pre-d299.jar`

## 下一窗 tip

- 方案 **R**（工坊单档轻调）**后置**，等真人是否仍「看得见但养不起」  
- **禁抬掉率** / 新材料轨 / 事件 R/W / 调律 R / 六槽 / 天赋盲调 / Pack6
