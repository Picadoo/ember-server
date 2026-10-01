# STATUS — Mineflayer gameplay suite

**Updated:** 2026-10-01 19:10:24 CST
**Bot:** EmberTestOp via proxy 127.0.0.1:25565 → AuthMe → play · 1.12.2 offline · **not op, no permissions**
**Privileged setup:** play-server console via `mineflayer-tests/lib/console.js` (FIFO stdin); scratch pad (3000,200,3000) cleared after run; no gamerule / ops.json changes

## Summary: PASS=9 FAIL=0 SKIP=0

| Mechanic | Status | Notes |
|----------|--------|-------|
| Join/smoke | **PASS** | proxy→AuthMe→play as EmberTestOp; spawn (-18.5, 58.0, 110.5) in overworld; auth=" 镶嵌 · 签到 · 生活补给。工坊 NPC 也可右键进入。 \| 进本门槛：周本 Lv.20 · 深渊 Lv.25 · 灾厄 Lv.30 · 团本 Lv.35——打开枢纽菜单进入。 \| EmberTestOp joined the game" |
| Hub menu (/ember, non-op) | **PASS** | title="{"text":"余烬 · 冒险枢纽"}" items=41 missing=[] unreplacedPAPI=0 |
| Furnace (CoreSmelt ore_ember_iron→ingot_ember_iron) | **PASS** | in=(none); out=iron_ingot#265x2("Iron Ingot") name="余烬铁锭" |
| Enchant (CoreEnchant gear_ember_blade + 附魔晶) | **PASS** | offers=[1,2,3] xp=127; target=iron_sword#267x1("Iron Sword") ench=[16:1,34:1] xpAfter=126 |
| Fishing (CoreFish loot table) | **PASS** | err=none loot=fish#349x1("Raw Fish") "余烬鳕鱼" items 1→2 |
| Totem (CoreCombat totem_ember_life) | **PASS** | lethal damage survived, totem consumed; hp 4→4; died=false; offhandAfter=(none); check="[CoreCombat] resurrectOk=14 resurrectBlockedApprox=1" |
| Shield (CoreCombat shield_ember_guard) | **PASS** | offhand=shield#442x1("Shield") hooks="[CoreCombat] ShieldNmsHooks.active=true hasOverride=true durMult=1.5 allowedCount=1" |
| Craft (CoreCraft 2×2 余烬铁锭→余烬铁板) | **PASS** | result=iron_ingot#265x1("Iron Ingot") name="余烬铁板" |
| World rules / MM (CoreWorldRules + EmberZombie) | **PASS** | cwr="[CoreWorldRules] spawnBans: natural=true spawners=true allowCustom=true allowEggs=true allowBuild=true cancelledSpawns=36 allowedCustomSpawns=28" pad=true mm="[MythicMobs] Spawned 1x EmberZombie!" mobs 0→1→1→0 (after mm kill: "Mobs Killed: EmberZombie, ") |

## How run

```bash
# play server must be started with the console FIFO (see mineflayer-tests/README.md)
cd /workspace/minecraft/mineflayer-tests && npm run gameplay
```
