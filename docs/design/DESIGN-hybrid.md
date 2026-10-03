# HYBRID architecture — Paper NMS hooks + CoreSmelt / CoreBrew

**Date:** 2026-09-09 Asia/Shanghai

## Goal

Enforce **custom furnace cook times & result amounts** and **custom brewing times & NI recipes** on Paper 1.12.2 without embedding NeigeItems inside Paper.

| Layer | Responsibility |
|-------|----------------|
| **Paper NMS** | `TileEntityFurnace` / `TileEntityBrewingStand` (+ brewing slots) consult runtime hooks |
| **Paper API** | `pers.coresystem.paper.FurnaceNmsHooks` / `BrewNmsHooks` — maps + plugin callbacks only |
| **CoreSmelt** | Wipe vanilla furnace recipes; YAML NI recipes; push cook/amount rules; NI gate on `FurnaceSmeltEvent` |
| **CoreBrew** | Disable vanilla brew matches; YAML NI brew recipes; push brew times/results |
| **NeigeItems** | Item definitions only (`plugins/NeigeItems/Items/`) |

## API

### FurnaceNmsHooks

```java
FurnaceNmsHooks.setRule(String ruleId, int cookTimeTicks, int resultAmount);
FurnaceNmsHooks.setResolver(RuleResolver); // plugin: NI id → Rule
FurnaceNmsHooks.resolveCookTime(ItemStack);   // used by TileEntityFurnace.a()
FurnaceNmsHooks.resolveResultAmount(ItemStack); // used by canBurn/burn
```

Defaults: cook **200**, amount **1**.

### BrewNmsHooks

```java
BrewNmsHooks.setVanillaEnabled(false); // ignore PotionBrewer when CoreBrew says so
BrewNmsHooks.setRule(ruleId, brewTimeTicks, resultAmount, resultKey);
BrewNmsHooks.setResolver(BrewResolver); // isCustomIngredient/Bottle/Result + resolve(ing, bottle)
BrewNmsHooks.resolveBrewTime(ingredient, bottles[]); // replaces hardcoded 400
BrewNmsHooks.configureStack(127, true, true, true); // ingredients/bottles/results
BrewNmsHooks.getMaxStackSize(ItemStack); // -1 = vanilla Item max; else configured ceiling
BrewNmsHooks.setDefaultMaxStack(int);    // clamped to 127
BrewNmsHooks.getSlotMaxStackSize();      // bottle/ingredient Slot ceiling when enabled
```

Default brew time **400**. Default max stack **127** (see below).

## NMS patch points (1.12.2)

| Class | Change |
|-------|--------|
| `TileEntityFurnace.a(ItemStack)` | `FurnaceNmsHooks.resolveCookTime` |
| `TileEntityFurnace.canBurn` / `burn` | custom `resultAmount` |
| `TileEntityBrewingStand.e` | brew start time from hooks |
| `TileEntityBrewingStand.o` / `p` | custom match + result; optional vanilla bypass |
| `TileEntityBrewingStand.b` + `ContainerBrewingStand` slots | allow custom ingredient/bottle; Slot max via `BrewNmsHooks` |
| `ItemStack.getMaxStackSize` / `CraftItemStack` | raise max for CoreBrew NI bottles/ingredients/results |

Paper has **zero** NeigeItems dependency.

## Demo recipes

### Furnace (CoreSmelt)

| input | result | cook | amount |
|-------|--------|------|--------|
| ore_ember_iron | ingot_ember_iron | 100 | 2 |
| ore_ember_gold | ingot_ember_gold | 300 | 1 |
| ore_ember_coal | crystal_ember_carbon | 150 | 3 |
| raw_ember_flesh | steak_ember | 80 | 1 |

### Brew (CoreBrew)

| ingredient | bottle | result | time |
|------------|--------|--------|------|
| ingredient_ember_wart | bottle_ember_water | potion_ember_swift (×8) | 100 |
| ingredient_ember_blaze | bottle_ember_water | potion_ember_strength | 200 |
| ingredient_ember_crystal | bottle_ember_water | potion_ember_night | 80 |

## Stack limits (why 127)

Vanilla bottle slots are max **1**; ingredient slots **64**. CoreBrew raises **custom** brew NI item max stacks and bottle/ingredient **Slot** ceilings via `BrewNmsHooks` (configurable, default **127**).

**127** is chosen because 1.12 stores item `Count` as a **signed byte** in NBT — values above 127 overflow. The vanilla client/protocol often glitches past 64–127. True “unlimited” is not honest on 1.12 without a custom inventory UI.

NeigeItems has no native max-stack YAML key; lore notes point at CoreBrew.

## Gaps (honest)

- Full PotionBrewer list wipe is not done; **vanilla matches are bypassed** via `BrewNmsHooks.setVanillaEnabled(false)` when CoreBrew loads.
- Brewing bottle slots still prefer potion-like materials; demo NI bottles use `POTION`.
- Vanilla potions/glass bottles in the stand stay **max 1** per-item even when the slot no-arg ceiling is raised.
- Client may **visually cap at 64** while the server allows up to 127 for CoreBrew NI items.
- Stock `paper-1.12.2-1620.jar` lacks hooks — plugins log and skip enforcement. Use `./start.sh custom`.
- See also `docs/status/STATUS-brew-stack.md`.

## Rebuild / run

See `docs/status/STATUS-hybrid.md`.
