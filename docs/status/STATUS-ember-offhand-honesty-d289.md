# 状态 · D289：副手诚实半行（方案 A）

**日期：** 2026-10-07（上海时间）  
**上游：** `DESIGN-ember-offhand-budget-decision-2026-10-07.md` **已批 A · 方案 A** · A1 文案薄窗 · CoreRpg **1.65.81**（零 jar）

## 人话

灰粮副手（守腕 / 生坠）永不进 P1 B/H。菜单以前只写「放副手槽生效」，玩家容易当成第三战力轴。本窗只加诚实半行：**微量守势 · 不计入主线刃/护符战力**。

## 改了什么

| 处 | 改动 |
|---|---|
| `ember_set.yml` | Open tell + O 格 lore / tell |
| `ember_life.yml` | 守腕 / 生坠购买 lore |
| `ember_hub.yml` | 装备入口补两行 |
| `ember_p1_gear.yml` | 已选护符旁半行（装备相关） |
| 决策页 A1 | 标已施工 D289 |

## 不动

- NI 数值 · StatService 白名单属性数字 · EmberLoadout / EmberFormula / B/H
- 新副手物品 · 方案 B · 六槽

## 验收

- `rg '微量守势|不计入主线刃/护符战力' plugins/TrMenu/menus/ember_{set,life,hub}.yml plugins/TrMenu/menus/ember_p1_gear.yml`
- `trmenu reload`；play 保持 up（无需换 jar）
