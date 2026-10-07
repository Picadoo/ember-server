# 状态 · D265：余烬附魔晶 NI lore 删英文 id 灰字

**日期：** 2026-10-07（上海时间）  
**上游：** D264（`bc98e711`）  
**范围：** 仅 `plugins/NeigeItems/Items/ember-furnace.yml` · `crystal_ember_enchant` lore 首行

## 债证（修前）

| 位置 | 原文 | 为何是玩家债 |
|---|---|---|
| L127 | `&7crystal_ember_enchant` | 显示名已是「余烬附魔晶」；背包/附魔台悬停仍印管理 id；周本预览等常提到附魔晶，曝光高 |

## 改动

删该灰字 1 行；保留催化剂说明 / 合成 / 青金石无效。零改 id / 合成 / 掉落。

## 验收

- rg：`ember-furnace.yml` 无 `&7crystal_ember_enchant`
- `neigeitems reload`（未重启）

## 旁扫未并入（同模式勿双上）

- 炉经济其它 `&7ore_/ingot_/potion_*` 灰字
- `gear_ember_blade` / `gear_ember_charm` 灰字
- 渔获 `fish_/treasure_/junk_*` 灰字
