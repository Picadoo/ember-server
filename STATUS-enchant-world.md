# STATUS — Enchanting / Anvil / Villagers / Mob loot

**Updated:** 2026-09-09 12:24 Asia/Shanghai (UTC+8)

## Result: SUCCESS

| Criterion | Status |
|-----------|--------|
| Custom Paper jar boots | **OK** `server-runtime/paper-custom.jar` |
| `EnchantNmsHooks` active + YAML tables | **OK** 2 tables, override=true |
| Cheap XP costs (1/2/3 and 1/3/5) + bookshelf bump cap 0 | **OK** |
| NI catalyst `crystal_ember_enchant` (余烬附魔晶); vanilla lapis rejected | **OK** |
| `AnvilNmsHooks` active (rename 5, max 30, material-only) | **OK** |
| Villager trading disabled (log on enable) | **OK** |
| Vanilla mob item drops cleared (XP kept) | **OK** |
| Blocks untouched | **OK** (no block hardness/drop changes) |
| `/coreenchant` `/coreanvil` `/coreworldrules` check+reload | **OK** |
| Mineflayer smoke / check-enchant | **OK** |
| CoreSmelt / CoreBrew still healthy | **OK** |

## Proof (UTC 04:24 ≈ CST 12:24)

Enable logs:

```
[CoreEnchant] CoreEnchant scheduled (EnchantNmsHooks available=true).
[CoreEnchant] Pushed 2 offer table(s) into EnchantNmsHooks (override=true, catalyst=crystal_ember_enchant, rejectVanillaLapis=true, bookshelfBumpCap=0, active=true).
```

Bot `/coreenchant check`:

```
[CoreEnchant] catalyst_ni_id=crystal_ember_enchant reject_vanilla_lapis=true bookshelf_cost_bump_cap=0
[CoreEnchant] EnchantNmsHooks.active=true ... hasCatalystOverride=true catalystNiId=crystal_ember_enchant rejectVanillaLapis=true
  slot 0: ... cost=1 lapis=1
  slot 1: ... cost=2 lapis=2
  slot 2: ... cost=3 lapis=3
[CoreEnchant] give catalyst: /ni give <player> crystal_ember_enchant 16
NeigeItems > 成功给予 Tester 1 个 余烬附魔晶
```

## What changed (Paper 1.12.2 CoreEnchant catalyst + cheap XP)

### Paper (NMS + API)

| Path | Role |
|------|------|
| `Paper/Paper-API/.../EnchantNmsHooks.java` | Offer provider + **CatalystValidator** / `isValidCatalyst` / reject-vanilla-lapis API |
| `Paper/Paper-API/.../AnvilNmsHooks.java` | Rename/max/prior-work/combine policy API |
| `Paper-Server/.../ContainerEnchantTable.java` | Custom offers; secondary slot accepts/consumes configured catalyst (not vanilla lapis) |
| `Paper-Server/.../ContainerAnvil.java` | Gate combines; rename cost; max cost; prior-work factor |

### Addons / NI

| Artifact | Notes |
|----------|-------|
| CoreEnchant jar + config | `catalyst_ni_id: crystal_ember_enchant`, costs 1/2/3 & 1/3/5, `bookshelf_cost_bump_cap: 0` |
| NI item | `plugins/NeigeItems/Items/ember-furnace.yml` → `crystal_ember_enchant` (**余烬附魔晶**, INK_SACK:4 + glow) |
| Give | `/ni give <player> crystal_ember_enchant 16` |

### Docs

- `DESIGN-enchant-anvil.md` — cheap costs + catalyst
- This file — start / rebuild / verification

## How to start

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime
./start.sh custom
./stop.sh
```

In-game / bot: `/coreenchant check|reload`, `/coreanvil check|reload`, `/coreworldrules check|reload` (alias `/cwr`).
Catalyst: `/ni give <player> crystal_ember_enchant 16`

## How to rebuild

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"

cd /workspace/minecraft/Paper
mvn install -pl Paper-API,Paper-Server -DskipTests
cp -f Paper-Server/target/paper-1.12.2.jar /workspace/minecraft/server-runtime/paper-custom.jar

for p in CoreEnchant CoreAnvil CoreWorldRules; do
  (cd /workspace/minecraft/$p && mvn -DskipTests package)
  cp -f /workspace/minecraft/$p/target/$p.jar /workspace/minecraft/plugins/
  cp -f /workspace/minecraft/$p/src/main/resources/config.yml /workspace/minecraft/plugins/$p/
done
```

## Notes / gaps

1. Enchant offer tables are **fixed demos** (two sets); not a full weighted pool DSL yet.
2. Catalyst uses **INK_SACK:4** base so the 1.12 client can place it in the lapis slot; vanilla lapis is still rejected via NI id (`reject_vanilla_lapis: true`).
3. Anvil demo **blocks enchant combines**; material repair uses vanilla repair materials unless `require_ni_repair_ingredient` is enabled.
4. Villager career/restock cannot create trade items while trading is disabled (recipes cleared + UI cancelled).
5. MythicMobs exclusion list is empty for now (`exclude_name_contains`); MM should add NI rewards on its own.
6. Stock Paper 1620 lacks these hooks — always `./start.sh custom`.

---

## Follow-up: vanilla spawn bans (2026-09-09 13:04 CST)

See **STATUS-spawns.md**. CoreWorldRules **v1.1.0** now cancels NATURAL/CHUNK_GEN/SPAWNER/ecosystem spawn reasons; CUSTOM + SPAWNER_EGG allowed. `bukkit.yml` spawn-limits=0; `spawn-animals/monsters=false`. Villager/loot/enchant systems unchanged. `/cwr check|testspawn`.

