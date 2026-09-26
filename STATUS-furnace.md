# STATUS — Furnace redesign (CoreSmelt + NeigeItems)

**Updated:** 2026-09-09 11:56 Asia/Shanghai (UTC+8)

## Result: SUCCESS

| Criterion | Status |
|-----------|--------|
| `DESIGN-furnace.md` | Done — ember economy table + 1.12.2 caveats |
| Server starts with NI + CoreSmelt | OK — Paper stock `paper-1.12.2-1620.jar` |
| Vanilla furnace recipes removed (>0) | **Removed 67** |
| NI recipes registered | **Registered 4** (`ore_ember_*` / `raw_ember_flesh` chain) |
| Mineflayer smoke | **PASS** — connect/spawn/quit offline on `:25565` |
| NI jar on 1.12.2 | `NeigeItems-1.21.151` loads despite `api-version: 1.13` |

## Proof log excerpts (server UTC 03:55:50 ≈ 11:55 CST)

```
[NeigeItems] Enabling NeigeItems v1.21.151
[CoreSmelt] Enabling CoreSmelt v1.0.0
[CoreSmelt] Removed 67 vanilla/other FurnaceRecipe(s).
[CoreSmelt] Registered furnace: ore_ember_iron (IRON_ORE:0) -> ingot_ember_iron exp=0.7
[CoreSmelt] Registered furnace: ore_ember_gold (GOLD_ORE:0) -> ingot_ember_gold exp=1.0
[CoreSmelt] Registered furnace: ore_ember_coal (COAL_ORE:0) -> crystal_ember_carbon exp=0.1
[CoreSmelt] Registered furnace: raw_ember_flesh (RAW_BEEF:0) -> steak_ember exp=0.35
[CoreSmelt] Registered 4 custom NI FurnaceRecipe(s).
```

Mineflayer:

```
[smoke] logged in as Tester
[smoke] spawned at (-53.5, 80, 259.5)
[smoke] quitting — OK
```

## Layout

- Design: `/workspace/minecraft/DESIGN-furnace.md`
- NI items: `plugins/NeigeItems/Items/ember-furnace.yml` (folder = `Items/`)
- Plugin source: `CoreSmelt/` (Maven, Java 8, paper-api 1.12.2 from local m2 + system-scope NI jar)
- Deployed: `plugins/CoreSmelt.jar` + `plugins/CoreSmelt/config.yml`
- Runtime: `server-runtime/` (`start.sh` / `stop.sh`, motd=`CoreSmelt test`, online-mode=false, max-players=20)
- Tests: `mineflayer-tests` (`npm run smoke`)

## API note

Java uses `pers.neige.neigeitems.manager.ItemManager.INSTANCE` (`hasItem` / `getItemStack` / `isNiItem`).  
`FurnaceSmeltEvent` cancels non-matching NI inputs so vanilla ores of the same Material do not complete smelts.  
`cook_time_ticks` is config/docs-only on 1.12.2 (no recipe cook-time setter).

## Coop notes

- Reused existing Temurin 8 + Maven under `tools/` and paper-api already installed to `~/.m2` by the Paper build agent.
- Did not touch Paper source fork; test server uses stock jar.
- Server was verified started for logs + mineflayer; not left required-running afterward. Restart: `cd server-runtime && ./start.sh stock`.
