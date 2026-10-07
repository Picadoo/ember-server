# 状态 · D268：周体力药 NI lore 假「分池」文案

**日期：** 2026-10-07（上海时间）  
**上游：** D267（`dd05116b`）· **非**英文 id 灰字类  
**范围：** 仅 `plugins/NeigeItems/Items/ember-stamina.yml` · `consumable_ember_stamina_45`

## 债证（修前）

| 位置 | 原文 | 为何假 |
|---|---|---|
| lore | `周限购 · 不与日常药剂池混算（由插件计）` | `StaminaService.onPotionConsume` 对 30/45 瓶均走 `potionAmountOf` → `addPotionStamina`，共用 `potion_daily_cap`（cash.yml 90）。商城「周体力包」SKU 才是 `addStamina` 直加（不计入药剂池）；**本 NI 瓶不是分池** |

## 改后

- 使用计入当日药剂回体上限（与日常体力药同池）
- 注明商城周体力包为直加、本瓶为药剂  

零改数值 / id / 饮用逻辑。

## 验收

- `neigeitems reload`
- rg：无「不与日常药剂池混算」
