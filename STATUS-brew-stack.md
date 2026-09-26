# STATUS — Brew stack limits (>64, ceiling 127)

**Updated:** 2026-09-09 22:46 Asia/Shanghai (UTC+8)

## Result: SUCCESS

| Criterion | Status |
|-----------|--------|
| `BrewNmsHooks.getMaxStackSize` / `setDefaultMaxStack` / `configureStack` | **OK** |
| `ContainerBrewingStand` bottle + ingredient Slot overrides | **OK** |
| NMS `ItemStack` + `CraftItemStack` max stack hooks for brew NI items | **OK** |
| CoreBrew `stack.max_stack: 127` pushed on enable/reload | **OK** |
| Demo `result_amount: 8` on wart→swift | **OK** |
| Custom jar boot + `/corebrew check` | **OK** (`max_stack=127`, `stackLimits=true`, `defaultMaxStack=127`) |
| Docs: why 127 | **OK** (`DESIGN-hybrid.md`, this file) |
| Furnace/enchant/other systems | **untouched** |

## Why 127 (not “unlimited”)

1.12 stores item `Count` as a **signed byte** in NBT (`ItemStack` save/load). Values above **127** overflow. The vanilla client/protocol also often glitches past 64–127 without a custom inventory UI. Claiming unlimited stacks on stock 1.12 brewing UI is not honest — **127** is the real ceiling.

## Proof (CST 22:42–22:46 ≈ UTC 14:42–14:46)

```
[CoreBrew] Loaded 3 brew recipe definitions (disable_vanilla=true, max_stack=127)
[CoreBrew] Hook brew: ... -> potion_ember_swift time=100 amount=8
[CoreBrew] Stack limits: max_stack=127 ingredients=true bottles=true results=true enabled=true
[CoreBrew] Pushed 3 brew rule(s) into BrewNmsHooks (active=true, max_stack=127).
```

Mineflayer `/corebrew check`:

```
[CoreBrew] hooksAvailable=true pushed=3 disable_vanilla=true max_stack=127
[CoreBrew] BrewNmsHooks.active=true ruleCount=3 vanillaEnabled=false stackLimits=true defaultMaxStack=127
[CoreBrew] ingredient_ember_wart + bottle_ember_water -> potion_ember_swift OK time=100 amount=8
```

## Config

```yaml
stack:
  max_stack: 127
  apply_to_ingredients: true
  apply_to_bottles: true
  apply_to_results: true
```

## Gaps (honest)

1. **Client visuals** may still show/cap at 64 while the server allows up to 127 for CoreBrew NI items.
2. NeigeItems YAML has **no** native max-stack key — limits come from `BrewNmsHooks` + CoreBrew (lore notes only).
3. Vanilla potion/glass-bottle slots remain **max 1** per-item (`getMaxStackSize(ItemStack)` keeps vanilla at 1).
4. Brewing **consumes one** bottle from a stacked bottle slot and **drops** any remainder; the slot is replaced by the result stack (`result_amount`).
5. Fuel (blaze powder) slot left at vanilla 64 unless TE inventory ceiling rises with hooks (does not change furnace/enchant).

## Paths

| What | Path |
|------|------|
| Hook API | `Paper/Paper-API/.../BrewNmsHooks.java` |
| Slots | `Paper/Paper-Server/.../ContainerBrewingStand.java` |
| Item max | `ItemStack.java`, `CraftItemStack.java` |
| TE | `TileEntityBrewingStand.java` |
| Plugin | `CoreBrew/` → `plugins/CoreBrew.jar` |
| Design | `DESIGN-hybrid.md` |
