# Paper 1.12.2 config optimization status

**Date:** 2026-09-10 (Asia/Shanghai)  
**Target:** `/workspace/minecraft/server-runtime` — small-player RPG stack (MythicMobs + NeigeItems + Core*)  
**Backups:** `config-backups/opt-20260910/` (`paper.yml`, `spigot.yml`, `bukkit.yml`, `server.properties`)  
**Verify:** YAML `safe_load` OK; `./start.sh custom` reached `Done (1.423s)` then `./stop.sh` OK.

Vanilla natural spawns remain disabled (`spawn-monsters/animals=false`, all `spawn-limits: 0`). Plugin jars and Paper NMS source untouched.

---

## Change table (before → after)

| File | Knob | Before | After | Notes |
|------|------|--------|-------|-------|
| **paper.yml** | `optimize-explosions` | `false` | `true` | Safer explosion path |
| paper.yml | `hopper.cooldown-when-full` | `true` | `true` | Kept |
| paper.yml | `hopper.disable-move-event` | `false` | **`false` (left)** | Present on 1.12 Paper; **not enabled** — NeigeItems / custom item stack may rely on `InventoryMoveItemEvent`; common practice: only enable after confirming no listeners |
| paper.yml | `hopper.push-based` | `false` | `true` | Available; safer hopper opt than killing move events |
| paper.yml | `container-update-tick-rate` | `1` | `3` | Mild client-update throttle |
| paper.yml | `grass-spread-tick-rate` | `1` | `4` | Less grass tick work |
| paper.yml | `max-auto-save-chunks-per-tick` | `24` | `8` | Smoother autosave spikes |
| paper.yml | `max-entity-collisions` | `8` | `2` | Collision cost down |
| paper.yml | `armor-stands-tick` | `true` | `false` | Decorative stands skip tick |
| paper.yml | `mob-spawner-tick-rate` | `1` | `2` | Spawners mostly unused |
| paper.yml | `max-chunk-gens-per-tick` | `10` | `5` | Cap gen spikes |
| paper.yml | `prevent-moving-into-unloaded-chunks` | `false` | `true` | Avoid unloaded-chunk movement issues |
| paper.yml | `skip-entity-ticking-in-chunks-scheduled-for-unload` | `true` | `true` | Kept |
| **spigot.yml** | `entity-activation-range` animals/monsters/misc/water | 32/32/16/16 | **24/24/12/12** | Mild trim — not crushed (MM AI-friendly) |
| spigot.yml | `tick-inactive-villagers` | `true` | `false` | Unused villagers don’t tick |
| spigot.yml | `entity-tracking-range` animals/monsters/misc | 48/48/32 | 40/40/24 | Slight network trim; players 48 / other 64 unchanged |
| spigot.yml | `ticks-per.hopper-transfer` | `8` | `8` | Kept |
| spigot.yml | `ticks-per.hopper-check` | `1` | `8` | Fewer empty hopper checks |
| spigot.yml | `hopper-amount` | `1` | `3` | Mild; fewer transfers for same throughput |
| spigot.yml | `merge-radius` item/exp | 2.5 / 3.0 | 3.5 / 4.0 | Fewer item/XP entities |
| spigot.yml | `max-entity-collisions` | `8` | `2` | Align with paper |
| spigot.yml | `max-tnt-per-tick` | `100` | `50` | Cap TNT storms |
| spigot.yml | `max-tick-time` tile/entity | 50 / 50 | **1000 / 1000** | Prefer disable harsh skipping — default 50ms can skip AI/tile ticks under load and hurt MM combat feel |
| spigot.yml | `view-distance` | `8` | `8` | Kept |
| **bukkit.yml** | `spawn-limits.*` | all `0` | all `0` | **Not reverted** |
| bukkit.yml | `chunk-gc.period-in-ticks` | `600` | `400` | More frequent unloaded chunk GC |
| **server.properties** | `view-distance` | `8` | `8` | Kept |
| server.properties | `network-compression-threshold` | `256` | `512` | Mild; less compress CPU on small player counts |
| server.properties | `online-mode` | `false` | `false` | Left |
| server.properties | `spawn-monsters` / `spawn-animals` | `false` / `false` | unchanged | **Not reverted** |

---

## Decision notes

1. **`hopper.disable-move-event` left `false`**  
   Key exists on this Paper 1.12 config. Docs/common practice allow `true` only when no plugin listens to `InventoryMoveItemEvent` (protection/shops/item plugins). This stack includes **NeigeItems** and Core* item hooks → keep event firing.

2. **`max-tick-time: 1000/1000`**  
   Setting tile/entity to ~50 causes Spigot to skip remaining work when a tick overruns; that can stall MythicMobs AI / projectiles mid-fight. 1000 effectively disables aggressive skipping so gameplay stays correct; rely on other knobs for TPS.

3. **Activation ranges 24/24**  
   Deliberately not reduced to tiny values so MythicMobs pathing/AI stays responsive for a small RPG test world.

---

## Post-edit validation

- Backups present under `config-backups/opt-20260910/`
- `python3` + PyYAML `safe_load` on paper/spigot/bukkit: OK; nested asserts passed
- Brief boot: `JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01` → `./start.sh custom` → `Done` → `./stop.sh`
