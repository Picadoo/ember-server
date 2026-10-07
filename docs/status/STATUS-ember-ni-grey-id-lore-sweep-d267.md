# 状态 · D267：NeigeItems lore 灰字 snake_case id 扫清

**日期：** 2026-10-07（上海时间）  
**上游：** D266（`aa4fbca9`）· 含点名 `shield_ember_guard`  
**模式：** lore 整行仅为 `&7`/`&8` + snake_case 内部 id（与附魔晶/图腾同构）

## 改动

删除上述灰字行 **39** 条；保留其余中文人话 lore；零改 NI id / 数值 / 合成 / CoreCombat 白名单。

## 清单（39）

**ember-craft-fish-combat.yml（11）**  
plate_ember_iron · rod_ember_iron · core_ember_compact · fish_ember_cod · fish_ember_salmon · fish_ember_puffer · treasure_ember_relic · treasure_ember_pearl · junk_ember_boot · junk_ember_bone · **shield_ember_guard**

**ember-furnace.yml（15）**  
ore_ember_iron · ingot_ember_iron · ore_ember_gold · ingot_ember_gold · ore_ember_coal · crystal_ember_carbon · raw_ember_flesh · steak_ember · bottle_ember_water · ingredient_ember_wart · potion_ember_swift · ingredient_ember_blaze · potion_ember_strength · ingredient_ember_crystal · potion_ember_night

**ember-dungeon.yml（2）**  
gear_ember_blade · gear_ember_charm

**ember-gear-t1.yml（3）**  
gear_ember_t1_blade · gear_ember_t1_talisman · acc_ember_raid_ring

**ember-gear-t2.yml（2）**  
gear_ember_t2_blade · gear_ember_t2_talisman

**ember-gear-t3.yml（2）**  
gear_ember_t3_blade · gear_ember_t3_talisman

**ember-life.yml（4）**  
food_ember_bread · food_ember_grilled_fish · potion_ember_heal（原 `&8`）· tool_ember_rod

## 验收

- rg：`plugins/NeigeItems/Items` 无 `^\s+- '&[78][a-z][a-z0-9_]*'$`
- `neigeitems reload`（未重启）

## 说明

D265/D266 已先清附魔晶、续命图腾；本窗收齐剩余同模式。未开六格。
