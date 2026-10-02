# STATUS — Mineflayer gameplay suite

**Updated:** 2026-10-03 00:06:13 CST
**Bot:** EmberTestOp via proxy 127.0.0.1:25565 → AuthMe → play · 1.12.2 offline · **not op, no permissions**
**Privileged setup:** play-server console via `mineflayer-tests/lib/console.js` (FIFO stdin); scratch pad (3000,200,3000) cleared after run; no gamerule / ops.json changes

## Summary: PASS=9 FAIL=0 SKIP=0

| Mechanic | Status | Notes |
|----------|--------|-------|
| Join/smoke | **PASS** | proxy→AuthMe→play as EmberTestOp; spawn (-18.5, 58.0, 110.5) in overworld; auth="+ 回复药（绑定）。手持刃按 F 放烬斩；回复药在快捷栏右侧，按数字键切过去、按住右键喝。 \| 每张图 30 体力 · 1～3 人 · 击败首领统一结算；不懂就点菜单里的「帮助」。 \| EmberTestOp joined the game" |
| Hub menu (/ember, non-op) | **PASS** | title="{"text":"余烬 · 冒险枢纽"}" items=16 missing=[] legacyShown=[] unreplacedPAPI=0 |
| Furnace (CoreSmelt ore_ember_iron→ingot_ember_iron) | **PASS** | in=(none); out=iron_ingot#265x2("Iron Ingot") name="余烬铁锭" |
| Enchant (CoreEnchant gear_ember_blade + 附魔晶) | **PASS** | offers=[1,2,3] xp=424; target=iron_sword#267x1("Iron Sword") ench=[16:1,34:1] xpAfter=423 |
| Fishing (CoreFish loot table) | **PASS** | err=none loot=fish#349x1("Raw Salmon") "余烬鲑鱼" items 1→2 |
| Totem (CoreCombat totem_ember_life) | **PASS** | lethal damage survived, totem consumed; hp 20→4; died=false; offhandAfter=(none); check="[CoreCombat] resurrectOk=1 resurrectBlockedApprox=14" |
| Shield (CoreCombat shield_ember_guard) | **PASS** | offhand=shield#442x1("Shield") hooks="[CoreCombat] ShieldNmsHooks.active=true hasOverride=true durMult=1.5 allowedCount=1" |
| Craft (CoreCraft 2×2 余烬铁锭→余烬铁板) | **PASS** | result=iron_ingot#265x1("Iron Ingot") name="余烬铁板" |
| World rules / MM (CoreWorldRules + EmberZombie) | **PASS** | cwr="[CoreWorldRules] spawnBans: natural=true spawners=true allowCustom=true allowEggs=true allowBuild=true cancelledSpawns=104 allowedCustomSpawns=34" pad=true mm="[MythicMobs] Spawned 1x EmberZombie!" mobs 0→1→1→0 (after mm kill: "Mobs Killed: EmberZombie, ") |

## How run

```bash
# play server must be started with the console FIFO (see mineflayer-tests/README.md)
cd /workspace/minecraft/mineflayer-tests && npm run gameplay
```
