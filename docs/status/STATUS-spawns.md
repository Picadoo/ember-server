# STATUS — Vanilla mob spawn bans (CoreWorldRules)

**Updated:** 2026-09-09 13:04 Asia/Shanghai (UTC+8)

## Result: SUCCESS

| Criterion | Status |
|-----------|--------|
| Natural / ecosystem CreatureSpawnEvent cancelled | **OK** (NATURAL, CHUNK_GEN, JOCKEY, MOUNT, BREEDING, SLIME_SPLIT, REINFORCEMENTS, VILLAGE_*, NETHER_PORTAL, DEFAULT, …) |
| SPAWNER cage spawns cancelled | **OK** (`disable_spawners: true`) |
| CUSTOM still allowed (MythicMobs / Bukkit `spawnEntity`) | **OK** (`/cwr testspawn` → CUSTOM zombie) |
| SPAWNER_EGG allowed for admin testing | **OK** (config) |
| bukkit.yml spawn-limits all 0 | **OK** monsters/animals/water/ambient |
| server.properties spawn-animals/monsters=false | **OK** belt-and-suspenders (CUSTOM bypasses) |
| Villager trade ban + mob loot clear unchanged | **OK** |
| Blocks untouched | **OK** |
| CoreSmelt / CoreEnchant still healthy after reboot | **OK** |
| Paper rebuild | **Not needed** (plugin jar only) |

## Proof (UTC 05:04 ≈ CST 13:04)

Enable logs:

```
[CoreWorldRules] Loading CoreWorldRules v1.1.0
[CoreWorldRules] Spawn policy: disable_natural_spawns=true disable_spawners=true allow_custom_spawns=true allow_spawner_eggs=true allow_build_spawns=true
[CoreWorldRules] Vanilla natural/ecosystem spawns CANCELLED (NATURAL, CHUNK_GEN, JOCKEY, MOUNT, BREEDING, SLIME_SPLIT, REINFORCEMENTS, VILLAGE_*, NETHER_PORTAL, DEFAULT, …).
[CoreWorldRules] Vanilla SPAWNER cage spawns CANCELLED.
[CoreWorldRules] CUSTOM spawns ALLOWED (MythicMobs / plugins / Bukkit spawnEntity).
[CoreWorldRules] SPAWNER_EGG ALLOWED (admin/testing).
```

Bot checks:

```
[CoreWorldRules] spawnBans: natural=true spawners=true allowCustom=true allowEggs=true allowBuild=true cancelledSpawns=0 allowedCustomSpawns=0
[CoreWorldRules] testspawn OK — CUSTOM zombie id=132 … (allowedCustomSpawns=1)
[CoreWorldRules] spawnBans: … allowedCustomSpawns=1
[CoreSmelt] FurnaceNmsHooks.active=true …
[CoreEnchant] EnchantNmsHooks.active=true …
```

Natural spawn attempts stay quiet with limits=0 + `spawn-animals/monsters=false` (often `cancelledSpawns=0` because vanilla never fires NATURAL). Event cancel remains the hard gate if those flags change.

## Config (`plugins/CoreWorldRules/config.yml`)

```yaml
spawns:
  disable_natural_spawns: true
  disable_spawners: true
  allow_custom_spawns: true
  allow_spawner_eggs: true
  allow_build_spawns: true
```

## Commands

- `/coreworldrules check` (alias `/cwr`) — reports spawn bans + counters
- `/coreworldrules reload`
- `/coreworldrules testspawn` — spawns a short-lived CUSTOM zombie (proof for MythicMobs path)

## Notes

1. Paper **1.12.2** has **no** `RAID` / `PATROL` SpawnReason (N/A).
2. Vanilla `/summon` uses `SpawnReason.DEFAULT` via `World.addEntity`; with `spawn-monsters/animals=false` it fails before the event. Prefer MythicMobs / `spawnEntity` (CUSTOM) or SPAWNER_EGG.
3. Pre-existing mobs in already-generated chunks remain until killed; new NATURAL/CHUNK_GEN spawns are blocked going forward.
4. Rebuild plugin only:

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/CoreWorldRules && mvn -DskipTests package
cp -f target/CoreWorldRules.jar /workspace/minecraft/plugins/CoreWorldRules.jar
cp -f src/main/resources/config.yml /workspace/minecraft/plugins/CoreWorldRules/config.yml
```
