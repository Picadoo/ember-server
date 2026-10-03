# Paper NMS SOURCE performance patches — changelog

**Date:** 2026-09-10 (Asia/Shanghai, CST / UTC+8)  
**Paper base:** 1.12.2 custom (`git-Paper-"e9c4141dab"`, branch `paper-1.12.2-1620`)  
**Tree:** `/workspace/minecraft/Paper/Paper-Server/src/main/java` (edits applied sources directly; not regenerated as Spigot-Server-Patches)  
**Deployed jar:** `/workspace/minecraft/server-runtime/paper-custom.jar` (copied from `Paper-Server/target/paper-1.12.2.jar`)  
**Marker comment:** all new logic tagged `// CoreSystem-PERF:` for grep/revert  
**Hand off to stronger model for review**

Related prior work (config-only, not NMS): `server-runtime/STATUS-paper-opt.md` (`config-backups/opt-20260910/`).

---

## Rebuild / deploy

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/Paper
mvn install -pl Paper-API,Paper-Server -DskipTests
cp -f Paper-Server/target/paper-1.12.2.jar /workspace/minecraft/server-runtime/paper-custom.jar
cd /workspace/minecraft/server-runtime && ./start.sh custom   # then ./stop.sh
```

Build log: `/workspace/minecraft/paper-nms-perf-build.log` (`BUILD SUCCESS`, ~1m21s).

---

## Patches applied

### 1. Entity collisions — `EntityLiving.cB()`

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/net/minecraft/server/EntityLiving.java` |
| **Method** | `protected void cB()` |
| **What** | (a) Early-return when `paperConfig.maxCollisionsPerEntity <= 0` (no `getEntities`). (b) Cramming passenger-count loop breaks once over threshold. (c) Skip collide against peers whose `numCollisions` already >= max. Local `maxCol` reused instead of re-reading config in loop. |
| **Why** | Config already sets max to 2; harden so zero/exhausted budgets stay cheap under entity pile-ups. |
| **Risk** | **Low.** Behavior identical when max≥1 and peers under budget; cramming still applies damage when over limit. |
| **Revert** | `rg -n "CoreSystem-PERF" EntityLiving.java` around `cB()`; restore stock Paper loop (`getEntities` → cramming full scan → `numCollisions - max` → collide). Or: `cd Paper && git diff -- Paper-Server/src/main/java/net/minecraft/server/EntityLiving.java` and discard PERF hunks only (keep Totem/Shield CoreSystem hybrid hooks). |

### 2. Inactive entity savings — `ActivationRange` + `EntityInsentient`

#### 2a. Deeper skip cadence for animals/misc

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/org/spigotmc/ActivationRange.java` |
| **Method** | `checkIfActive(Entity)` |
| **What** | Paper skipped 1/4 of ticks for non-immune active entities (`ticksLived % 4 == 0`). Now: monsters/water (`activationType` 1/4) keep `% 4`; animals/misc (2/3) use `% 3` (~33% skip). Immunity check still gates the skip. |
| **Why** | Extra idle savings without shrinking activation *range* (MM pathing near players unchanged). |
| **Risk** | **Low–med.** Animals/misc may feel slightly less “alive” far from combat; monsters/MM combat path preserved. |
| **Revert** | Restore `else if (... ticksLived % 4 == 0 && !checkEntityImmunities(...)) isActive=false;` — delete `skipMod` block marked CoreSystem-PERF. |

#### 2b. Clear navigation while inactive

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/net/minecraft/server/EntityInsentient.java` |
| **Method** | `public void inactiveTick()` (**new override**) |
| **What** | Calls `super.inactiveTick()` then every 16 ticks (`ticksLived & 15 == 0`), if navigation has a path (`!navigation.o()`), clears it via `navigation.p()`. |
| **Why** | Inactive mobs no longer keep stale paths that resume expensive work on wake. Entities with `getGoalTarget()` / hurt / effects stay **active** via `checkEntityImmunities` — MythicMobs near players unaffected. |
| **Risk** | **Low.** Far-away MM may re-path on approach (expected). Do **not** clear goals/targets here. |
| **Revert** | Delete the entire `inactiveTick` override block marked CoreSystem-PERF. |

### 3. Item merge — slightly more aggressive (grounded)

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/net/minecraft/server/EntityItem.java` |
| **Method** | `B_()` (tick) |
| **What** | Merge attempt interval: grounded `15` ticks vs airborne `25` (was always 25). Merge **radius** still from `spigot.yml` (`merge-radius.item`, already 3.5). |
| **Why** | Coalesce ground piles faster without widening AABB every tick. |
| **Risk** | **Low.** More `ItemMergeEvent` / lookups when many items on ground — acceptable for small player counts. |
| **Revert** | Remove `mergeInterval`; restore `ticksLived % 25 == 0`. |

### 4. Hopper/container — safe early-out only

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/net/minecraft/server/TileEntityHopper.java` |
| **Method** | `private boolean o()` |
| **What** | Cache `empty=p()` / `full=r()`; if both true, return false immediately. Reuse booleans for push/pull branches. **Does not** set `hopper.disable-move-event`; `InventoryMoveItemEvent` still fires on real transfers (NeigeItems-safe). |
| **Why** | Avoid redundant push+pull attempts when hopper is empty and every slot is at max stack. |
| **Risk** | **Low.** Logic equivalent to prior branch skips; slightly less work when stuck empty+full. |
| **Revert** | Restore original `if (!p()) s(); if (!r()) { mayAcceptItems; a(); }` structure. |

### 5. Chunk unload — hard cap per tick

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/net/minecraft/server/ChunkProviderServer.java` |
| **Method** | `unloadChunks()` |
| **What** | After each successful `unloadChunk`, increment `unloadedThisTick`; break when `>= 40` **or** existing Spigot targetSize + `activityAccountant` exhausted. Autosave still uses `maxAutoSaveChunksPerTick` from config (already 8). |
| **Why** | Cap hitch size if unload queue spikes; time accountant alone can still allow many fast unloads. |
| **Risk** | **Low.** Unloads may stretch across more ticks under mass teleport/unload; safer for TPS. |
| **Revert** | Remove `unloadedThisTick` / `maxUnloadPerTick` and restore original break condition. |

### 6. Armor stand / experience orb — skip unnecessary ticks

#### 6a. Marker armor stands

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/net/minecraft/server/EntityArmorStand.java` |
| **Method** | `B_()` |
| **What** | After Paper `canTick` early-return, also return if `isMarker()`. Config already has `armor-stands-tick: false` for new stands; this hardens markers even if `canTick` true. |
| **Why** | Markers are display-only; living tick is wasted. |
| **Risk** | **Low.** Marker pose/equipment still via datawatcher when needed; no gameplay rules changed. |
| **Revert** | Delete `if (this.isMarker()) return;` line. |

#### 6b. Grounded idle XP orbs

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/net/minecraft/server/EntityExperienceOrb.java` |
| **Method** | `B_()` |
| **What** | If onGround, no `targetPlayer`, near-zero velocity, and `a % 8 != 0`: only age (`a`,`b`) / despawn check, then return (skip gravity, player seek, move). Full tick every 8th age tick. Spawn-time XP merge unchanged (`World.addEntity` + `expMerge`). |
| **Why** | Idle orb clouds after grind sessions. |
| **Risk** | **Low–med.** Magnet pull starts on a later tick; pickup via player collision still works when orb is in world. Watch if any plugin depends on every-tick orb motion. |
| **Revert** | Delete the opening CoreSystem-PERF idle block in `B_()`. |

---

## What was NOT done (and why)

| Idea | Why skipped |
|------|-------------|
| Full Moonrise / modern chunk system | Out of scope; huge 1.12 rewrite risk |
| Folia / region threading | Not applicable to 1.12.2 single-threaded Paper |
| Netty pipeline rewrite | Avoided per mandate |
| Huge AI / PathfinderGoalSelector rewrite | Goal selector not even in edited Paper-Server src tree as a first-class patched file; high MM risk |
| Global `InventoryMoveItemEvent` disable (`hopper.disable-move-event`) | **NeigeItems / custom items may listen** — left false in config and NMS |
| Shrinking entity-activation-range further in code | Config already at 24/24/12/12; smaller ranges hurt MythicMobs AI feel |
| Async chunk save rewrite | Risky; only unload hard-cap one-liner |
| Changing Core* gameplay / NMS hook contracts | Explicitly forbidden; Totem/Brew/Enchant/Shield/Fish/Furnace/Anvil/Craft hooks left intact |
| XP merge radius code change | Already config-driven at 4.0; spawn merge path already Paper-optimized |
| Disabling armor stand ticks in code globally | Config `armor-stands-tick: false` already covers new stands; only marker harden added |

---

## Hook integrity (post-build)

Jar contains `pers/coresystem/paper/*NmsHooks*.class` including:  
`TotemNmsHooks`, `BrewNmsHooks`, `EnchantNmsHooks`, `ShieldNmsHooks`, `FishNmsHooks`, `FurnaceNmsHooks`, `AnvilNmsHooks`, `CraftNmsHooks`.

Boot log confirmed plugins pushed hooks (`available=true` / `active=true`) for CoreCombat/CoreBrew/CoreEnchant/CoreSmelt/CoreFish/CoreAnvil/CoreCraft.

Source grep still shows hybrid hook call sites in:  
`EntityLiving`, `TileEntityBrewingStand`, `ContainerBrewingStand`, `ContainerEnchantTable`, `ContainerAnvil`, `EntityFishingHook`, `TileEntityFurnace`, `ItemStack`, `EntityHuman`, `CraftItemStack`.

---

## Boot proof snippet

```
[10:12:18] [Server thread/INFO]: Starting minecraft server version 1.12.2
[10:12:18] [Server thread/INFO]: This server is running Paper version git-Paper-"e9c4141dab" (MC: 1.12.2) ...
...
[10:12:21] [Server thread/INFO]: [CoreBrew] CoreBrew HYBRID scheduled (BrewNmsHooks available=true).
[10:12:21] [Server thread/INFO]: [CoreCombat] CoreCombat scheduled (TotemHooks=true ShieldHooks=true).
[10:12:21] [Server thread/INFO]: Done (1.349s)! For help, type "help" or "?"
[10:12:21] [Server thread/INFO]: [CoreCombat] Pushed TotemNmsHooks override=true ids=[totem_ember_life] ...
[10:12:22] [Server thread/INFO]: [CoreBrew] Pushed 3 brew rule(s) into BrewNmsHooks (active=true, max_stack=127).
```

(Log timestamps are server-local wall clock as written; boot ~18:12 CST / 10:12 UTC on 2026-09-10.)  
Server stopped via `./stop.sh` after verify.

---

## Audit checklist for stronger model

1. Grep `CoreSystem-PERF` across `Paper-Server/src` — every hunk should be reversible in isolation.
2. Confirm no change to `pers.coresystem.paper.*` hook APIs or call sites (except nearby collision method which still has Totem/Shield hooks elsewhere in `EntityLiving`).
3. Load-test MythicMobs CUSTOM spawn + combat with player within activation range — AI should remain responsive.
4. Hopper transfer with NeigeItems stacks — `InventoryMoveItemEvent` must still fire.
5. Consider promoting safe hunks into `Spigot-Server-Patches` via `./paper rb` if this tree must stay patch-based long-term (currently direct source edits, same as hybrid Core workflow).
6. Re-evaluate XP orb idle skip if orb-magnet plugins misbehave.
7. `EntityLiving.cB` peer-skip with `continue` can scan more of the list looking for under-budget entities — OK at maxCol=2; revisit if profiling shows getEntities-dominated cost instead.

**Hand off to stronger model for review**


---

## 2026-09-10 later — XP orbs removed + chunk bandwidth (CST)

**Also:** CoreWorldRules **1.2.0** plugin + runtime config edits (backups `server-runtime/config-backups/xp-chunk-20260910/`).  
**Status doc:** `docs/status/STATUS-xp-chunk.md`

### 7. Reject experience orb entities entirely

| Field | Value |
|-------|--------|
| **File** | `Paper-Server/src/main/java/net/minecraft/server/World.java` |
| **Methods** | `addEntity(Entity, SpawnReason)` and `a(Collection)` / `addChunkEntities` |
| **What** | Early-reject `EntityExperienceOrb`: mark `dead`, return false / `continue`. Covers kill/mine/furnace/fish/bottle/breed/trade spawn paths **and** orbs loaded from chunk NBT. |
| **Why** | No floating XP orbs (entity + packet + tick cost). Paper 1.12.2 does **not** fire Bukkit spawn events for XP orbs in `addEntity`, so plugin-only cancel is insufficient. |
| **XP grant** | **Not** applied silently via `giveExp`/`setExp`. Orb-based XP from kills/mining/etc. is **gone**. Enchant/anvil still use stored player levels. |
| **Plugin** | CoreWorldRules `experience_orbs.remove: true`, `mob_loot.clear_xp: true`, `EntitySpawnEvent` cancel defense, periodic sweep every 100 ticks. |
| **Risk** | **Low.** Vanilla orb gameplay removed by design. Prior idle-orb PERF skip (6b) becomes mostly moot for new orbs; left in place harmlessly. |
| **Revert** | Delete the two `CoreSystem-PERF` orb-reject blocks in `World.java`; set CoreWorldRules `experience_orbs.remove: false` / `clear_xp: false`. |

### 8. Chunk bandwidth (config; no extra NMS throttle)

Paper already throttles sends via `paperConfig.maxChunkSendsPerTick` in `PlayerChunkMap` — **config only**, no new NMS send-rate patch.

| File | Knob | Before | After |
|------|------|--------|-------|
| server.properties | `view-distance` | 8 | **6** |
| spigot.yml | `view-distance` | 8 | **6** |
| paper.yml | `max-chunk-sends-per-tick` | 81 | **48** |
| paper.yml | `keep-spawn-loaded-range` | 8 | **4** |
| server.properties | `network-compression-threshold` | 512 | **512 (kept)** |

**Compression note:** Raised earlier from 256→512 (see `STATUS-paper-opt.md`). Higher threshold means **fewer** small packets are compressed → often **less CPU**, sometimes **more raw bandwidth** on tiny packets; at 512, mid/large chunk payloads still compress. Tuning further (e.g. 256 for more compression / less bandwidth at CPU cost, or `-1` to disable) is environment-dependent — left at 512.

**Boot:** View Distance: 6 logged; CoreWorldRules “Experience orbs REMOVED”; Done (~1.1s); MM/NI/Core hooks OK; server stopped after verify.

**Rebuild:** `mvn install -pl Paper-API,Paper-Server -DskipTests` → `paper-xp-chunk-build.log` BUILD SUCCESS; jar → `server-runtime/paper-custom.jar`.
