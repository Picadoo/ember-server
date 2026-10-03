# STATUS — Ember Crypt dungeon (余烬地窟)

**Updated:** 2026-09-11 18:40 Asia/Shanghai (UTC+8)

## Result: CONTENT READY (AttributePlus jar pending upload)

| Criterion | Status |
|-----------|--------|
| Design doc | **OK** `docs/design/DESIGN-ember-dungeon.md` |
| NeigeItems mats + gear | **OK** `plugins/NeigeItems/Items/ember-dungeon.yml` |
| MythicMobs 3 mobs | **OK** `plugins/MythicMobs/Mobs/EmberCryptMobs.yml` |
| DropTables (NI via command) | **OK** `plugins/MythicMobs/DropTables/EmberCryptDrops.yml` |
| Setup console script | **OK** `scripts/ember-crypt-setup.txt` |
| PlaceholderAPI | **OK** `plugins/PlaceholderAPI.jar` |
| AttributePlus jar | **MISSING** — see `plugins/AttributePlus.MISSING.txt` |
| Paper rebuild | **Skipped** (not required) |
| Server boot / live spawn test | **Not run** (optional; content only) |

## Paths

| What | Absolute path |
|------|----------------|
| Design | `/workspace/minecraft/docs/design/DESIGN-ember-dungeon.md` |
| This status | `/workspace/minecraft/docs/status/STATUS-ember-dungeon.md` |
| NI items | `/workspace/minecraft/plugins/NeigeItems/Items/ember-dungeon.yml` |
| MM mobs | `/workspace/minecraft/plugins/MythicMobs/Mobs/EmberCryptMobs.yml` |
| MM drops | `/workspace/minecraft/plugins/MythicMobs/DropTables/EmberCryptDrops.yml` |
| Setup script | `/workspace/minecraft/scripts/ember-crypt-setup.txt` |
| Dungeon folder copy | `/workspace/minecraft/dungeons/ember-crypt/` |
| Plugins dir | `/workspace/minecraft/plugins/` |
| Runtime plugins symlink | `/workspace/minecraft/server-runtime/plugins` → `../plugins` |
| AP missing note | `/workspace/minecraft/plugins/AttributePlus.MISSING.txt` |
| PAPI jar | `/workspace/minecraft/plugins/PlaceholderAPI.jar` |

`server-runtime/plugins` already symlinks to `plugins/`, so NI/MM YAML written under `plugins/` is the live tree — no separate copy needed.

## NeigeItems IDs

| ID | Material | Role |
|----|----------|------|
| `mat_ember_shard` | REDSTONE | Zombie mat |
| `mat_ember_bone_dust` | SULPHUR | Skeleton mat |
| `mat_ember_core_fragment` | MAGMA_CREAM | Brute rare mat |
| `gear_ember_blade` | IRON_SWORD | Weapon; lore `攻击力: +8`, `生命值: +20` |
| `gear_ember_charm` | GOLD_NUGGET | Charm; lore `生命值: +20`, `防御力: +3` |

Give test: `/ni give <player> gear_ember_blade 1`

## MythicMobs IDs (4.11)

| ID | Type | Drops |
|----|------|-------|
| `EmberCryptZombie` | ZOMBIE | `mat_ember_shard` (always), `gear_ember_blade` 8% |
| `EmberCryptSkeleton` | SKELETON | `mat_ember_bone_dust`, `gear_ember_charm` 8% |
| `EmberCryptBrute` | HUSK | `mat_ember_core_fragment`, blade 25%, charm 20% |

Spawn (at op / or coords):

```
/mm m spawn EmberCryptZombie
/mm m spawn EmberCryptSkeleton
/mm m spawn EmberCryptBrute
```

Console with world coords (from setup script):

```
mm m spawn EmberCryptZombie 1 world,-41,65,269
```

### Drop integration note

No NeigeItems drop type is bundled in MythicMobs 4.11 (`CommandDrop` / `ItemDrop` / `MythicItemDrop` only). Drops use:

```yaml
- command{c="ni give <trigger.name> mat_ember_shard 1";asop=true} 1 1
```

If `<trigger.name>` fails in a given kill context, try `<killer.name>` / player name after a live test, or fall back to vanilla `REDSTONE{display="余烬碎片"}` display-name drops.

## AttributePlus

- **Jar:** not present. Public URLs checked (MCBBS archive, ersha site, Spiget, Modrinth, Hangar, CurseForge, klpbbs) — **no free direct jar**.
- **Action:** upload `AttributePlus.jar` (1.12.2-capable 3.x, e.g. 3.2.1) into `plugins/` like MythicMobs, then restart.
- **PAPI:** installed.
- **Lore format:** demo uses `攻击力` / `生命值` / `防御力` per design doc. Stock AP `attribute.yml` often uses `物理伤害` / `生命力` / `物理防御` — **may need tweak** after jar is online (edit lore or rename attribute keys).

## How to demo

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime
./start.sh custom
# After Done: paste scripts/ember-crypt-setup.txt into console
# Kill mobs as op; confirm /ni inventory items
./stop.sh
```

## Follow-ups

1. Upload AttributePlus jar → delete `AttributePlus.MISSING.txt`.
2. Align AP attribute display names with gear lore.
3. Live spawn + kill test (mineflayer optional).
4. Formal dungeon portal / region later.
