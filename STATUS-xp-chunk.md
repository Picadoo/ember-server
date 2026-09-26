# STATUS — XP orbs removed + chunk bandwidth

**Date:** 2026-09-10 ~20:45 CST (UTC+8)  
**Result:** **OK** — no XP orb entities; view/chunk send tightened; docs updated; server stopped after verify.

| Item | Status |
|------|--------|
| NMS `World.addEntity` / chunk-load reject `EntityExperienceOrb` | **OK** (`// CoreSystem-PERF:`) |
| CoreWorldRules 1.2.0 — remove orbs + `clear_xp` + sweep | **OK** deployed to `plugins/CoreWorldRules.jar` |
| view-distance 8→**6** (server.properties + spigot.yml) | **OK** (boot: `View Distance: 6`) |
| `max-chunk-sends-per-tick` 81→**48** | **OK** |
| `keep-spawn-loaded-range` 8→**4** | **OK** |
| `network-compression-threshold` | **512 kept** (was 256→512 earlier) |
| Extra NMS chunk-send throttle | **Not needed** — Paper `PlayerChunkMap` already uses `maxChunkSendsPerTick` |
| Silent `giveExp` on kill | **Not implemented** — orb-based XP gone by design |
| MM / NI / Core* hooks | **OK** on boot |
| Backups | `server-runtime/config-backups/xp-chunk-20260910/` |

## Behavior notes

- **No floating XP orbs.** NMS rejects spawn + chunk NBT load; plugin cancels `EntitySpawnEvent` for `EXPERIENCE_ORB` (defense) and sweeps every 100 ticks.
- **Orb-based XP is gone** (kills, mining, furnace, fishing, bottles, breeding, trades). Player levels already stored still work for enchant/anvil.
- MythicMobs / NeigeItems reward XP separately if configured — not blocked except when they spawn orb entities (those orbs are rejected).

## Config backups

```
server-runtime/config-backups/xp-chunk-20260910/
  server.properties  spigot.yml  paper.yml  bukkit.yml
```

## Rebuild / boot

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/Paper && mvn install -pl Paper-API,Paper-Server -DskipTests
cp -f Paper-Server/target/paper-1.12.2.jar /workspace/minecraft/server-runtime/paper-custom.jar
cd /workspace/minecraft/CoreWorldRules && mvn -q package -DskipTests
cp -f target/CoreWorldRules.jar /workspace/minecraft/plugins/CoreWorldRules.jar
cd /workspace/minecraft/server-runtime && ./start.sh custom   # then ./stop.sh
```

Build log: `/workspace/minecraft/paper-xp-chunk-build.log`  
Detailed NMS/changelog: `/workspace/minecraft/PERF-CHANGELOG-paper-nms.md` (§7–8)
