# STATUS — HYBRID Paper NMS hooks + CoreSmelt / CoreBrew

**Updated:** 2026-09-09 12:09 Asia/Shanghai (UTC+8)

## Result: SUCCESS

| Criterion | Status |
|-----------|--------|
| Custom Paper jar boots | **OK** `server-runtime/paper-custom.jar` |
| `FurnaceNmsHooks` present & active | **OK** (`hooksAvailable=true`, `ruleCount=4`) |
| Non-default cook times pushed | **OK** 100 / 300 / 150 / 80 (not all 200) |
| `result_amount` pushed | **OK** 2 / 1 / 3 / 1 |
| `BrewNmsHooks` active | **OK** `ruleCount=3`, `vanillaEnabled=false` |
| Non-default brew times | **OK** 100 / 200 / 80 |
| `/coresmelt reload` + `/corebrew reload` | **OK** |
| Mineflayer join + `/coresmelt check` + `/corebrew check` | **OK** |
| Existing ember furnace NI IDs preserved | **OK** (brew items appended) |

## Proof log excerpts (UTC 04:07–04:09 ≈ CST 12:07–12:09)

```
[CoreSmelt] CoreSmelt HYBRID scheduled (FurnaceNmsHooks available=true).
[CoreBrew] CoreBrew HYBRID scheduled (BrewNmsHooks available=true).
[CoreSmelt] Removed 67 vanilla/other FurnaceRecipe(s).
[CoreSmelt] Hook rule: ore_ember_iron cook=100 amount=2 -> ingot_ember_iron
[CoreSmelt] Hook rule: ore_ember_gold cook=300 amount=1 -> ingot_ember_gold
[CoreSmelt] Hook rule: ore_ember_coal cook=150 amount=3 -> crystal_ember_carbon
[CoreSmelt] Hook rule: raw_ember_flesh cook=80 amount=1 -> steak_ember
[CoreSmelt] Pushed 4 rule(s) into FurnaceNmsHooks (active=true).
[CoreBrew] Hook brew: ... time=100 / 200 / 80
[CoreBrew] Pushed 3 brew rule(s) into BrewNmsHooks (active=true).
```

Console / bot:

```
[CoreSmelt] FurnaceNmsHooks.active=true ruleCount=4
[CoreBrew] BrewNmsHooks.active=true ruleCount=3 vanillaEnabled=false
[CoreSmelt] reloaded YAML + re-pushed FurnaceNmsHooks (4 rules)
[CoreBrew] reloaded YAML + re-pushed BrewNmsHooks (3 rules)
```

## Paths

| What | Path |
|------|------|
| Hook API (Paper-API) | `Paper/Paper-API/src/main/java/pers/coresystem/paper/FurnaceNmsHooks.java` |
| | `Paper/Paper-API/src/main/java/pers/coresystem/paper/BrewNmsHooks.java` |
| NMS patches | `Paper/Paper-Server/.../TileEntityFurnace.java` |
| | `TileEntityBrewingStand.java`, `ContainerBrewingStand.java` |
| Rebuilt server jar | `Paper/Paper-Server/target/paper-1.12.2.jar` |
| Runtime custom jar | `server-runtime/paper-custom.jar` |
| CoreSmelt source | `CoreSmelt/` → `plugins/CoreSmelt.jar` |
| CoreBrew source | `CoreBrew/` → `plugins/CoreBrew.jar` |
| NI items | `plugins/NeigeItems/Items/ember-furnace.yml` |
| Design | `docs/design/DESIGN-hybrid.md`, updated `docs/design/DESIGN-furnace.md` |

## API (plugin-facing, no CraftBukkit import)

```java
// Furnace
FurnaceNmsHooks.setRule(niInputId, cookTimeTicks, resultAmount);
FurnaceNmsHooks.setResolver(input -> { /* NI match → Rule */ });
FurnaceNmsHooks.resolveCookTime(bukkitStack);    // TileEntityFurnace.a()
FurnaceNmsHooks.resolveResultAmount(bukkitStack); // canBurn / burn

// Brew
BrewNmsHooks.setVanillaEnabled(false);
BrewNmsHooks.setRule(ruleId, brewTimeTicks, resultAmount, resultNiId);
BrewNmsHooks.setResolver(new BrewResolver() { ... });
BrewNmsHooks.resolveBrewTime(ingredient, bottles); // replaces 400
```

Paper never depends on NeigeItems; plugins supply NI-aware resolvers.

## How to rebuild Paper

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/Paper
mvn install -pl Paper-API,Paper-Server -DskipTests
cp -f Paper-Server/target/paper-1.12.2.jar /workspace/minecraft/server-runtime/paper-custom.jar
```

(Full `./paper jar` also works if regenerating from patches; current workflow edits applied sources directly.)

## How to rebuild addons

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/CoreSmelt && mvn -DskipTests package
cd /workspace/minecraft/CoreBrew && mvn -DskipTests package
cp -f CoreSmelt/target/CoreSmelt.jar /workspace/minecraft/plugins/
cp -f CoreBrew/target/CoreBrew.jar /workspace/minecraft/plugins/
cp -f CoreSmelt/src/main/resources/config.yml /workspace/minecraft/plugins/CoreSmelt/
cp -f CoreBrew/src/main/resources/config.yml /workspace/minecraft/plugins/CoreBrew/
```

## How to start custom jar

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime
./start.sh custom    # uses paper-custom.jar
# ./start.sh stock   # official 1620 (hooks absent → plugins log and skip NMS enforce)
./stop.sh
```

Commands in-game / console: `/coresmelt check|reload`, `/corebrew check|reload`.

## Related

Brew stack limits (>64, ceiling 127): see `docs/status/STATUS-brew-stack.md`.

## Gaps (honest)

1. Vanilla `PotionBrewer` recipe lists are not cleared in NMS; CoreBrew sets `BrewNmsHooks.setVanillaEnabled(false)` so TE ignores vanilla matches.
2. Brew stack limits: custom NI items + slots raised to 127 via BrewNmsHooks (see `docs/status/STATUS-brew-stack.md`); wart→swift demo uses `result_amount: 8`.
3. In-world timed smelt/brew tick observation was not instrumented beyond hook registration + event wiring; cook/brew durations are read from hooks at TE start (`a()` / brew start). Use iron (100) vs gold (300) in a furnace to feel the difference in-game.
4. Stock Paper 1620 lacks these classes — always use `./start.sh custom` for HYBRID.

## Coop notes

- Did not use CloudAgent / Origin.
- Java 8 only (`jdk8u504-b01`).
- Ember furnace NI IDs unchanged; brew items appended to same YAML.
