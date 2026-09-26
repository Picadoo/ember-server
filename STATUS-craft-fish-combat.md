# STATUS — Craft / Fish / Totem / Shield

**Updated:** 2026-09-09 12:58 Asia/Shanghai (UTC+8)

## Result: SUCCESS

| Criterion | Status |
|-----------|--------|
| Custom Paper jar boots | **OK** `server-runtime/paper-custom.jar` |
| CoreCraft wipe + NI recipes | **OK** removed=433 keptWhitelist=10 keptFurnace=4 NI=3 |
| CoreFish NI loot + wait hooks | **OK** overrideLoot=true wait=40-120 entries=7 |
| CoreCombat totem/shield | **OK** Totem/Shield hooks active; demos OK |
| NI items load | **OK** give totem/shield/fish/plate |
| Villager ban + mob loot ban | **OK** CoreWorldRules still enabled |
| Blocks untouched | **OK** |
| Mineflayer `check-craft-fish-combat.js` | **OK** PROOF OK |
| CoreSmelt / CoreEnchant healthy | **OK** |

## Proof (UTC 04:58 ≈ CST 12:58)

Enable:

```
[CoreCombat] Pushed TotemNmsHooks override=true ids=[totem_ember_life] cooldown=15s active=true
[CoreCombat] Pushed ShieldNmsHooks override=true ids=[shield_ember_guard] durMult=1.5 active=true
[CoreFish] Pushed FishNmsHooks overrideLoot=true wait=40-120 entries=7 active=true
[CoreCraft] Craft wipe removed=433 keptWhitelist=10 keptFurnace=4 NI registered=3
```

Bot check:

```
[CoreCraft] NI_registered=3 CraftNmsHooks.active=true
[CoreFish] FishNmsHooks.active=true overrideLoot=true
[CoreCombat] TotemNmsHooks.active=true ShieldNmsHooks.active=true
NeigeItems > 成功给予 … 余烬续命图腾 / 余烬守护盾 / 余烬鳕鱼 / 余烬铁板
[check] PROOF OK
```

## What changed

### Paper hooks

| Class | Role |
|-------|------|
| `CraftNmsHooks` | wipe/register stats for check |
| `FishNmsHooks` | loot provider + wait tick override |
| `TotemNmsHooks` | NI totem validator (EntityLiving resurrect) |
| `ShieldNmsHooks` | block validator + durability multiplier |

NMS: `EntityFishingHook`, `EntityLiving` (totem + isBlocking), `EntityHuman.damageShield`

### Addons / NI

| Artifact | Notes |
|----------|-------|
| CoreCraft | whitelist + 3 ember craft recipes |
| CoreFish | fish/treasure/junk YAML |
| CoreCombat | totem_ember_life + shield_ember_guard |
| `plugins/NeigeItems/Items/ember-craft-fish-combat.yml` | new NI ids |

### Docs

- `DESIGN-craft-fish-combat.md`
- This file
- `mineflayer-tests/scripts/check-craft-fish-combat.js`

## How to start

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime
./start.sh custom
```

In-game: `/corecraft check|reload` · `/corefish check|reload` · `/corecombat check|reload`

## Give commands

```
/ni give <player> ingot_ember_iron 16
/ni give <player> plate_ember_iron 1
/ni give <player> rod_ember_iron 1
/ni give <player> core_ember_compact 1
/ni give <player> crystal_ember_carbon 1
/ni give <player> ingot_ember_gold 1
/ni give <player> fish_ember_cod 1
/ni give <player> fish_ember_salmon 1
/ni give <player> fish_ember_puffer 1
/ni give <player> treasure_ember_relic 1
/ni give <player> treasure_ember_pearl 1
/ni give <player> junk_ember_boot 1
/ni give <player> junk_ember_bone 1
/ni give <player> totem_ember_life 1
/ni give <player> shield_ember_guard 1
```

## How to rebuild

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/Paper && mvn install -pl Paper-API,Paper-Server -DskipTests
cp -f Paper-Server/target/paper-1.12.2.jar /workspace/minecraft/server-runtime/paper-custom.jar
for p in CoreCraft CoreFish CoreCombat; do
  (cd /workspace/minecraft/$p && mvn -DskipTests package)
  cp -f /workspace/minecraft/$p/target/$p.jar /workspace/minecraft/plugins/
  cp -f /workspace/minecraft/$p/src/main/resources/config.yml /workspace/minecraft/plugins/$p/
done
```

## Notes

1. Crafting whitelist is configurable; demo keeps WORKBENCH/FURNACE/CHEST/STICK/WOOD only.
2. CoreCraft always preserves FurnaceRecipe so CoreSmelt chain stays intact.
3. Fishing wait demo 40–120 ticks (faster than default Paper range) for feel-testing.
4. Totem cooldown demo 15s; shield durability multiplier 1.5.
5. Always `./start.sh custom` — stock 1620 lacks these hooks.
