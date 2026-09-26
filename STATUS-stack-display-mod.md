# STATUS — Ember Stack Display (Forge 1.12.2 client)

**Updated:** 2026-09-10 17:34 Asia/Shanghai (UTC+8)

## Result: SUCCESS (compile + reobf jar)

| Criterion | Status |
|-----------|--------|
| ForgeGradle 3 project under `/workspace/minecraft/ember-stack-display/` | **OK** |
| `./gradlew build` | **OK** (`BUILD SUCCESSFUL`) |
| Reobfuscated jar (SRG overrides, e.g. `func_180453_a`) | **OK** |
| Output jar at `/workspace/minecraft/client-mods/ember-stack-display-1.12.2.jar` | **OK** (6344 bytes) |
| `README-zh.md` (install + display-vs-interaction honesty) | **OK** |
| Client-only (`clientSideOnly=true`, install on CLIENT init) | **OK** |
| Minecraft client GUI smoke test | **skipped** (headless box; compile success is enough) |
| Paper / CoreBrew server project | **untouched** |

## What the mod does

- Wraps `Minecraft.renderItem` with `StackAwareRenderItem`.
- Forces overlay label to `String.valueOf(stack.getCount())` for counts ≠ 1 (and custom text).
- Never clamps the drawn string to 64; scales down for 3-digit labels (e.g. `127`).
- Preserves durability bar / cooldown by calling `super` with temporary `setCount(1)`.

## What it does **not** do (honest)

1. Does **not** change `ItemStack.getMaxStackSize` / `Item.getItemStackLimit` on the client — mouse-split / craft grid feel may still follow vanilla max (often 64).
2. Does **not** raise server stack limits — that remains Paper `BrewNmsHooks` + CoreBrew `stack.max_stack: 127`.
3. Does **not** need to be installed on the dedicated server.

## Build proof

```
> Task :compileJava
> Task :reobfJar
> Task :build
BUILD SUCCESSFUL in 5s
```

Jar verify (`javap`):

```
public void func_180453_a(FontRenderer, ItemStack, int, int, String);
// uses ItemStack.func_190916_E (getCount), func_190920_e (setCount)
```

## Paths

| What | Path |
|------|------|
| Project | `/workspace/minecraft/ember-stack-display/` |
| Sources | `.../src/main/java/com/ember/stackdisplay/` |
| Built libs | `.../build/libs/ember-stack-display-1.0.0.jar` |
| Deliverable | `/workspace/minecraft/client-mods/ember-stack-display-1.12.2.jar` |
| README (ZH) | `.../README-zh.md` |
| Build log | `.../build.log` |
| Related server status | `/workspace/minecraft/STATUS-brew-stack.md` |

## Config (server side, unchanged)

```yaml
stack:
  max_stack: 127
```

Client mods folder: drop `ember-stack-display-1.12.2.jar` into Forge 1.12.2 `mods/`.
