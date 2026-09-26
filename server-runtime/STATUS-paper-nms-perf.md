# STATUS — Paper 1.12.2 NMS SOURCE perf

**Date:** 2026-09-10 ~20:45 CST (UTC+8)  
**Result:** **OK** — custom jar rebuilt (incl. XP-orb reject), boots to Done, Core hooks intact, server stopped after verify.

| Item | Status |
|------|--------|
| Patches (`CoreSystem-PERF`) | Prior 8 sites + **World.java XP-orb reject** (addEntity + chunk load) — see `PERF-CHANGELOG-paper-nms.md` |
| XP orbs + chunk bandwidth | See **`STATUS-xp-chunk.md`** |
| `mvn install -pl Paper-API,Paper-Server -DskipTests` | BUILD SUCCESS (`paper-xp-chunk-build.log`) |
| Deploy | `server-runtime/paper-custom.jar` updated |
| Boot | `Done (~1.1s)` — MythicMobs + NeigeItems + Core* enabled; View Distance 6; orbs REMOVED |
| Hooks in jar | Totem/Brew/Enchant/Shield/Fish/Furnace/Anvil/Craft present |
| InventoryMoveItemEvent | **Not** disabled |
| Server after verify | **Stopped** |

## Quick revert

```bash
rg -n "CoreSystem-PERF" /workspace/minecraft/Paper/Paper-Server/src/main/java
# Remove marked blocks / restore from git diff hunks, then rebuild as in changelog
```

## Docs

- Detailed audit log: `/workspace/minecraft/PERF-CHANGELOG-paper-nms.md`
- XP + chunk bandwidth: `/workspace/minecraft/STATUS-xp-chunk.md`
- Prior **config-only** opts: `/workspace/minecraft/server-runtime/STATUS-paper-opt.md`

**Hand off to stronger model for review**
