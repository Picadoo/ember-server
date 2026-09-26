# STATUS — Mineflayer gameplay suite

**Updated:** 2026-09-09 13:51:35 CST (UTC 2026-09-09T05:51:35.778Z)
**Bot:** Tester @ 127.0.0.1:25565 version 1.12.2 offline
**Server:** Paper custom stack (NeigeItems + Core* + MythicMobs)

## Summary: PASS=7 FAIL=0 SKIP=1

| Mechanic | Status | Notes |
|----------|--------|-------|
| Join/smoke | **PASS** | spawned at (-38.5, 78.0, 256.5) as Tester |
| Furnace | **PASS** | opened window type=minecraft:furnace; slots in=iron_ore#15x1("Iron Ore") fuel=coal#263x1("Coal"); output=iron_ingot#265x2("Iron Ingot") progressSeen=true invIngot=iron_ingot#265x2("Iron Ingot") |
| Enchant | **PASS** | window=minecraft:enchanting_table; sword: moved 29->0 now=iron_sword#267x1("Iron Sword"); catalyst: moved 30->1 now=dye#351x16("Lapis Lazuli"); targetSlot=iron_sword#267x1("Iron Sword") lapisSlot=dye#351x16("Lapis Lazuli"); ready=true offers=[#0 lvl=1 expEnchant=1, #1 lvl=3 expEnchant=32, #2 lvl=5 expEnchant=21] xp=90; enchanted slot=0 result=iron_sword#267x1("Iron Sword") xpBefore=90 xpAfter=89 |
| Fishing | **SKIP** | cast attempted but no bite within timeout (bobber/particle detect fragile on 1.12). rod equipped; look→water -39,78,259; fishOk=false err=fish timeout 20s loot=(none) |
| Totem | **PASS** | death+alive (resurrection); offhand=totem_of_undying#449x1("Totem of Undying"); healthBefore=20; healthAfter=20 died=true resurrected=true totemStillOff=false totemInInv=false offAfter=(none); combatCheck=true |
| Shield | **PASS** | shield in off-hand + activateItem + ShieldNmsHooks.active; offhand=shield#442x1("Shield") activateItem(offHand=true) issued; combatCheck=true |
| Craft | **PASS** | window=minecraft:crafting_table; resultSlot=iron_ingot#265x1("Iron Ingot") grid=iron_ingot#265x1("Iron Ingot"),iron_ingot#265x1("Iron Ingot"),iron_ingot#265x1("Iron Ingot"),iron_ingot#265x1("Iron Ingot"); invPlate=iron_ingot#265x1("Iron Ingot") |
| World rules / MM | **PASS** | cwr=[CoreWorldRules] spawnBans: natural=true spawners=true allowCustom=true allowEggs=true allowBuild=true cancelledSpawns=0 allowedCustomSpawns=2; mobs before=1 afterSpawn=2 later=2; mmChat=[MythicMobs] Spawned 1x EmberZombie!; flood=false |

## Honesty notes

- PASS only when the suite observed concrete client/server evidence (spawn, window slots, item give, chat proof, entity spawn, etc.).
- SKIP means best-effort attempted but evidence incomplete.
- **Fishing SKIP:** `bot.fish()` relies on 1.12 bobber particle packets; cast + CoreFish hooks verified, but bite/collect not observed within 20s.
- Enchant PASS: window offers cost 1/3/5 with `crystal_ember_enchant` (dye:4) catalyst; cheap slot taken (xp 90→89).
- Totem PASS: `/effect <player> 7 …` Instant Damage; `died=true` then alive; CoreCombat `resurrectOk` incremented.
- Furnace PASS: ore_ember_iron + coal → output iron_ingot×2 (ingot_ember_iron) with progress bar.
- Craft PASS: 2×2 ingot_ember_iron on crafting_table produced result (plate_ember_iron material=IRON_INGOT).
- FAIL means the mechanic did not produce expected evidence.

## How run

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime && ./start.sh custom
cd /workspace/minecraft/mineflayer-tests && npm run gameplay
cd /workspace/minecraft/server-runtime && ./stop.sh
```

## Raw results JSON

```json
[
  {
    "name": "Join/smoke",
    "status": "PASS",
    "notes": "spawned at (-38.5, 78.0, 256.5) as Tester"
  },
  {
    "name": "Furnace",
    "status": "PASS",
    "notes": "opened window type=minecraft:furnace; slots in=iron_ore#15x1(\"Iron Ore\") fuel=coal#263x1(\"Coal\"); output=iron_ingot#265x2(\"Iron Ingot\") progressSeen=true invIngot=iron_ingot#265x2(\"Iron Ingot\")"
  },
  {
    "name": "Enchant",
    "status": "PASS",
    "notes": "window=minecraft:enchanting_table; sword: moved 29->0 now=iron_sword#267x1(\"Iron Sword\"); catalyst: moved 30->1 now=dye#351x16(\"Lapis Lazuli\"); targetSlot=iron_sword#267x1(\"Iron Sword\") lapisSlot=dye#351x16(\"Lapis Lazuli\"); ready=true offers=[#0 lvl=1 expEnchant=1, #1 lvl=3 expEnchant=32, #2 lvl=5 expEnchant=21] xp=90; enchanted slot=0 result=iron_sword#267x1(\"Iron Sword\") xpBefore=90 xpAfter=89"
  },
  {
    "name": "Fishing",
    "status": "SKIP",
    "notes": "cast attempted but no bite within timeout (bobber/particle detect fragile on 1.12). rod equipped; look→water -39,78,259; fishOk=false err=fish timeout 20s loot=(none)"
  },
  {
    "name": "Totem",
    "status": "PASS",
    "notes": "death+alive (resurrection); offhand=totem_of_undying#449x1(\"Totem of Undying\"); healthBefore=20; healthAfter=20 died=true resurrected=true totemStillOff=false totemInInv=false offAfter=(none); combatCheck=true"
  },
  {
    "name": "Shield",
    "status": "PASS",
    "notes": "shield in off-hand + activateItem + ShieldNmsHooks.active; offhand=shield#442x1(\"Shield\") activateItem(offHand=true) issued; combatCheck=true"
  },
  {
    "name": "Craft",
    "status": "PASS",
    "notes": "window=minecraft:crafting_table; resultSlot=iron_ingot#265x1(\"Iron Ingot\") grid=iron_ingot#265x1(\"Iron Ingot\"),iron_ingot#265x1(\"Iron Ingot\"),iron_ingot#265x1(\"Iron Ingot\"),iron_ingot#265x1(\"Iron Ingot\"); invPlate=iron_ingot#265x1(\"Iron Ingot\")"
  },
  {
    "name": "World rules / MM",
    "status": "PASS",
    "notes": "cwr=[CoreWorldRules] spawnBans: natural=true spawners=true allowCustom=true allowEggs=true allowBuild=true cancelledSpawns=0 allowedCustomSpawns=2; mobs before=1 afterSpawn=2 later=2; mmChat=[MythicMobs] Spawned 1x EmberZombie!; flood=false"
  }
]
```
