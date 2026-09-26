# STATUS — MythicMobs 4.11.0 on Paper 1.12.2 custom

**Updated:** 2026-09-09 13:07 Asia/Shanghai (UTC+8)  
**Test run:** UTC 05:04–05:07 (CST 13:04–13:07)

## Result: SUCCESS

| Criterion | Status |
|-----------|--------|
| MythicMobs jar version | **4.11.0** (Premium; `plugins/MythicMobs.jar`) |
| Enable on custom Paper 1.12.2 | **OK** — `MythicMobs Premium v4.11.0 已经成功加载!` |
| Demo mob | **EmberZombie** (`plugins/MythicMobs/Mobs/EmberZombie.yml`, Type: ZOMBIE) |
| Spawn command | **`/mm m spawn EmberZombie`** → chat: `[MythicMobs] Spawned 1x EmberZombie!` |
| Entity observed (mineflayer) | **OK** — new `zombie` at ≈(-52.7, 80, 260.3) near Tester |
| Mineflayer join/quit while MM loaded | **PASS** (`scripts/smoke.js`) |
| CoreWorldRules vanilla NATURAL/SPAWNER ban | **ACTIVE** (`natural=true spawners=true allowCustom=true`) |
| Server left running | **No** — stopped after test |

## Spawn command syntax (4.11)

From `/mm mobs` / `/mm m` help:

```
/mm m spawn <-t> [type] <amount> <w,x,y,z> ► Spawn mobs
```

Working form used: `/mm m spawn EmberZombie` (at op player location).  
Aliases: `/mm` → MythicMobs; `/mm m` → mobs subcommands. Also `/spawnmob` / `/spawnmythicmob` registered in `plugin.yml` (not required for this pass).

Loaded mob IDs (6): `AngrySludge, EmberZombie, SkeletalKnight, SkeletalMinion, SkeletonKing, StaticallyChargedSheep`.

## Demo mob YAML

Path: `plugins/MythicMobs/Mobs/EmberZombie.yml`

```yaml
EmberZombie:
  Type: ZOMBIE
  Display: '&6Ember Zombie'
  Health: 30
  Damage: 5
  Options:
    MovementSpeed: 0.25
    AlwaysShowName: true
    PreventOtherDrops: true
```

First-run also generated stock `ExampleMobs.yml`, `VanillaMobs.yml`, Items/Skills/DropTables/RandomSpawns, and `config.yml` (Configuration.Version: 4.11).

## CoreWorldRules spawn ban

Config (`plugins/CoreWorldRules/config.yml`):

- `spawns.disable_natural_spawns: true`
- `spawns.disable_spawners: true`
- `spawns.allow_custom_spawns: true` (MythicMobs / plugins)

Enable log:

```
[CoreWorldRules] Spawn policy: disable_natural_spawns=true disable_spawners=true allow_custom_spawns=true ...
[CoreWorldRules] CUSTOM spawns ALLOWED (MythicMobs / plugins / Bukkit spawnEntity).
```

Bot `/cwr check` after MM spawn:

```
spawnBans: natural=true spawners=true allowCustom=true allowEggs=true allowBuild=true
cancelledSpawns=0 allowedCustomSpawns=2
```

(`allowedCustomSpawns` includes earlier `/cwr testspawn` CUSTOM zombie + MM EmberZombie.)

Also: `server.properties` has `spawn-monsters=false` / `spawn-animals=false` (belt-and-suspenders with CWR).

## Errors / soft-depends (non-blocking)

| Issue | Severity | Notes |
|-------|----------|-------|
| Example item `KingsCrown` — material `GOLDEN_HELMET` not found | WARN | 1.13+ material name; 1.12 wants `GOLD_HELMET`. Example-only; does not block enable/spawn. |
| Drop `heroesexp 200` in `ExampleDropTables.yml` — drop type not found | WARN | Heroes plugin absent (MM softdepend). Example-only. |
| ProtocolLib | Soft-missing | Listed in MM `plugin.yml` softdepend; **no MM enable failure**. NeigeItems also logged `未发现前置插件: ProtocolLib`. Optional for advanced packets/disguise features. |
| Other MM softdepends absent | Soft | Vault, PlaceholderAPI, WorldGuard, LibsDisguises, Citizens, etc. — not required for load + `/mm m spawn`. |
| `api-version: 1.13` in plugin.yml | Info | Paper 1.12.2 still loaded MM fine (platform log: `Paper (MC: 1.12.2)`). |

No hard enable errors; no crash.

## How this test was run

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime
./stop.sh          # clear prior listener on 25565
./start.sh custom  # paper-custom.jar + plugins incl. MythicMobs.jar
# wait for Done + MM enable
cd /workspace/minecraft/mineflayer-tests
node mm-spawn-test.js   # /mm m spawn EmberZombie + entity count
node scripts/smoke.js   # join/quit PASS
cd /workspace/minecraft/server-runtime && ./stop.sh
```

Artifacts:

- `server-runtime/logs/mm-spawn-test.log`
- `server-runtime/logs/mm-smoke.log`
- `server-runtime/logs/latest.log` / `stdout.log`

## Notes / follow-ups

1. Stock example packs use some 1.13+ material names (`GOLDEN_*`); fix or ignore for 1.12.2 demos.
2. ProtocolLib not installed — add later if packet/disguise features are needed.
3. CWR `exclude_name_contains` still empty; MM/NI death rewards not wired yet (drops cleared for vanilla deaths).
4. Underground leftover vanilla mobs may exist from pre-ban chunks; CWR blocks new NATURAL/SPAWNER, not despawn of already-loaded entities.
5. Do **not** use CloudAgent for this stack; always `./start.sh custom` with JDK8 above.
