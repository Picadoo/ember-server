# STATUS — Ember dungeon maps split (2026-09-13)

## Problem
All Ember dungeons (`EmberDaily` / `EmberWeekly` / `EmberAbyss` / `EmberCalamity` / `EmberRaid`) pointed at the same map folder `plugins/DungeonPlus/map/ember_arena` and the same spawn `-40,65,270`. Instances felt identical.

## Fix summary
| Item | Result |
|------|--------|
| Per-dungeon map folders | **Done** — recursive copies of `ember_arena` |
| `$setmap{name=...}` in each `option.yml` | **Done** |
| `config.yml` `dungeon-pre-folder` | **Done** — each map registered |
| MCA theme markers near spawn | **Succeeded** (1.12 `OldBlock` numeric ids via `anvil-parser`) |
| `ember_arena` kept as shared fallback / arena dungeon | **Untouched** (still stone at spawn) |

## Map folders
```
plugins/DungeonPlus/map/ember_arena/     # shared fallback + ember_arena dungeon
plugins/DungeonPlus/map/ember_daily/
plugins/DungeonPlus/map/ember_weekly/
plugins/DungeonPlus/map/ember_abyss/
plugins/DungeonPlus/map/ember_calamity/
plugins/DungeonPlus/map/ember_raid/
```

## `$setmap` mapping
| Dungeon | Map name |
|---------|----------|
| EmberDaily | `ember_daily` |
| EmberWeekly | `ember_weekly` |
| EmberAbyss | `ember_abyss` |
| EmberCalamity | `ember_calamity` |
| EmberRaid | `ember_raid` |
| ember_arena | `ember_arena` (unchanged) |

Spawn remains `-40,65,270` for all (teleport + `$setspawn`).

## `dungeon-pre-folder` (config.yml)
Registered mirrors of the old `ember_arena` entry:
- `ember_arena` → `"ember_arena": 1`
- `ember_daily` → `"EmberDaily": 2`
- `ember_weekly` → `"EmberWeekly": 1`
- `ember_abyss` → `"EmberAbyss": 1`
- `ember_calamity` → `"EmberCalamity": 1`
- `ember_raid` → `"EmberRaid": 1`

## Theme markers (MCA edit **succeeded**)
Edited `region/r.-1.0.mca` around spawn `(-40,65,270)` (chunk `-3,16`). Terrain there is buried stone; a small air box was carved so markers are visible at spawn height. 1.12 ids used.

| Map | Marker |
|-----|--------|
| `ember_daily` | Green wool (35:5) platform/ring + emerald (133) pillar + fence/sign posts |
| `ember_weekly` | Blue wool (35:11) ring + lapis (22) pillar/platform |
| `ember_abyss` | Obsidian (49) platform + taller obsidian / purple stained clay (159:10) walls |
| `ember_calamity` | Netherrack (87) platform + magma (213) ring/pillars |
| `ember_raid` | Larger quartz (155) platform (half-extent 5) + corner pillars |

Verified sample reads after save (e.g. daily floor `35:5`, weekly lapis `22`, abyss purple clay `159:10`, calamity magma `213`, raid quartz `155`). `ember_arena` spawn floor still stone `1`.

Tooling: `/workspace/minecraft/tools/mca-venv` + `anvil-parser` (lightweight). Formal art / layout still needs **WorldEdit** in-game later.

## Reload
After deploying these files to the live server:
```
/dp reload
```
Also clear or ignore stale `plugins/DungeonPlus/dungeon-caches/*` if an old shared-map cache is reused (or restart if caches stick).

Do **not** overwrite `CoreRpg.jar`; Paper unchanged.

## Paths touched
- `plugins/DungeonPlus/map/ember_{daily,weekly,abyss,calamity,raid}/` (new copies + MCA)
- `plugins/DungeonPlus/dungeon/Ember{Daily,Weekly,Abyss,Calamity,Raid}/option.yml`
- `plugins/DungeonPlus/config.yml` (`dungeon-pre-folder`)
- `plugins/DungeonPlus/README-ember-dungeons.md`
- `STATUS-ember-maps.md` (this file)

## Remaining
- WorldEdit pass for real distinct arenas (current markers are identification-only stubs).
- Optional: per-map spawn offsets once layouts diverge.
