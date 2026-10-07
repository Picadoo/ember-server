# 状态 · D266：余烬续命图腾 NI lore 删英文 id 灰字

**日期：** 2026-10-07（上海时间）  
**上游：** D265（`b8a078d6`）  
**范围：** 仅 `plugins/NeigeItems/Items/ember-craft-fish-combat.yml` · `totem_ember_life` lore 首行

## 债证（修前）

| 位置 | 原文 | 为何高曝光 |
|---|---|---|
| L85 | `&7totem_ember_life` | 显示名已是「余烬续命图腾」；`CoreCombat` 唯一有效图腾，副手/死亡相关悬停必见；灰字盖住「唯一有效 / 原版无效」人话 |

## 改动

删该灰字 1 行；保留唯一有效与原版无效说明。零改 id / CoreCombat 白名单。

## 验收

- rg：本文件无 `&7totem_ember_life`
- `neigeitems reload`

## 旁扫未并入

- `shield_ember_guard` 同模式灰字（下一件最薄）
- 炉/渔其余 `&7…` 灰字；`gear_ember_*` 模板灰字
