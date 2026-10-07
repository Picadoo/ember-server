# 状态 · D294：灰箍获取口诚实半行（方案 A 收口）

**日期：** 2026-10-07（上海时间）  
**上游：** `DESIGN-ember-offhand-budget-decision` 已批 A · D289 守腕/生坠诚实 · 烬砧灰箍获取口仍缺「不进 P1」  
**性质：** 零 jar · 菜单 + NI lore

## 人话

灰粮守腕/生坠已写「微量守势 · 不计入主线刃/护符战力」，烬砧炼灰箍仍只写「放副手槽生效」，玩家会以为第三条副手是另一套规则。本窗把灰箍获取口对齐同一诚实半行。

## 改了什么

| 处 | 改动 |
|---|---|
| `ember_forge.yml` | 炼部件悬停补「不计入主线刃/护符战力」 |
| `ember_part.yml` | 灰箍格 + 说明格补微量守势 / 不进 P1 B/H |
| `NeigeItems/.../ember-gear-parts.yml` | 灰箍物品 lore 同口径 |

## 不动

- 物防 +1 数值 · StatService 白名单 · EmberLoadout / B/H
- 六槽 · 天赋一排 · Pack · 新副手物品

## 验收

- `rg '不计入主线刃/护符战力' plugins/TrMenu/menus/ember_{forge,part}.yml plugins/NeigeItems/Items/ember-gear-parts.yml`
- `trmenu reload`；`ni reload`；play 保持 up（无需换 jar）
- 已持有旧灰箍栈可能仍显示旧 lore，新炼/新发以新 lore 为准
