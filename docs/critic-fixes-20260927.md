# Fixes for the 2026-09-26 playtest (docs/critic-20260926.md) — 2026-09-27 CST, CoreRpg 1.8.1

## 1. Spawn boxed in (ember_hub)
- `/fill -24 59 103 -12 60 113 air` opens a 3-high plaza from the spawn (-18.5,58,110.5) to the NPC (-16.5,58,106.5) and the workshop (craft -24 / enchant -22 / anvil -20, z104). Stray glowstone on the floor is removed, and 4 ceiling lights at y61 replace it.
- Region backup (not in git): `backups/world-snap/ember_hub-r.-1.0-20260927-0030.mca`.
- Verified with no op: a fresh non-op bot walks from spawn to the NPC with pathfinder (digging and towering off) and ends up 1.4 blocks away.

## 2. AFK zone deaths
- `keepInventory true` in ember_hub, ember_afk, ember_event and world.
- The ember_afk spawn/respawn moved to (-4.5,73,250.5). That is more than 32 blocks (the leash range) from all three spawners.
- New player commands `/hub`, `/spawn`, `/lobby` and `/回城` go to ember_hub with a 5 s cooldown. They are refused inside dungeon instances, where `/dp leave` applies.
- Chapter 1 gives the T0 blade and charm at the start and only asks for 6 + 4 AFK kills (zombies or skeletons). A lost blade is reissued once per day by the NPC. `PreventSunburn` is set on the AFK mobs.
- Test: 0–1 deaths in the AFK part, and the blade was kept.

## 3. DP entry skeleton + kill credit
- The baked template entity '余烬地窟骷髅' (not a live MM mob) is removed on chunk load and when a player enters an instance world. The log shows "Removed baked template mob".
- Entering an instance gives a full heal, food 20 and Resistance V for 5 s.
- Kill credit:
  - Every Ember MM death reward now uses `corerpg mmgive|mmxp <caster.uuid> …` instead of `ni give <trigger.name>` / `xpreward <trigger.name>`. That is 77 lines across 7 mob files.
  - CoreRpg credits the reward to the last player who hit the mob within 15 s. It records hits at LOWEST priority, even if the event is cancelled later. The fallbacks are the vanilla killer, then the nearest player within 16 blocks.
  - Damage from non-players to MM mobs is cancelled, so mobs no longer infight and steal the last hit.
- Result: no more `玩家 Unknown 不在线`, and elite/boss XP now counts. The test daily went from 0/150 to 15/150.

## 4. Raid ticket, calamity T3
- Weekly login grant of 1 raid ticket (`cash.yml raid.free_tickets: 1`, PlayerData `raidTicketGrantWeekId`), which matches the menu's "每周团本票×1".
- Calamity last hit: T3 blade 1% and T3 talisman 1% (each ≤ 0.02). The menu text now says "尾刀低概率 T3 装备（刃 / 护符各 1%）".

## 5. Guaranteed loot toned down
- New `/corerpg loot <p> <key>` (`loot.yml`): the `first_per_week` items drop on the first clear of each ISO week; after that, each clear rolls a chance.
  - `abyss_t2`: first clear each week gives the T2 blade + talisman; later clears 4% / 3%.
  - `weekly_t1`: first clear each week gives the T1 set; later clears 10% / 8%.
  - `raid_gem` and `guild_gem`: 50%.
- Abyss settle gem changed from guaranteed to `gem_chance: 0.1`. Abyss MM gems dropped from .03/.02 to .003/.002, and the watcher from .18/.17 to .03/.02.
- Calamity last-hit gem changed from 100% to 30%, and its other gems from 4% to 2%.
- Gems per day for an active player (1 abyss run, the calamity window, weekly raid and guild boss spread over the week):

| Source | Gems per day |
|---|---|
| Abyss settle | 0.10 |
| Abyss mobs + watcher | ~0.10–0.15 |
| Calamity (30% last hit, shared across the group, + 2% ×2) | ~0.05–0.15 |
| Raid (0.5/7) | 0.07 |
| Guild boss (0.5/7) | 0.07 |
| **Total** | **≈0.35–0.5**, about 1 gem every 2–3 days (was ≈3.2/day) |

## Vanilla levels for enchanting
- The mainline gives +5 levels (ch1), +3 (ch2), and +5 at each of Lv25/30/35.
- DP clears already give levels (for example the daily "Given 3 levels").
- The newbie test enchanted at the workshop right after chapter 2's daily.

## Retest (no op, `mineflayer-tests/newbie-path-smoke.js`)
spawn free → walk to NPC and talk → menu 挂机庭 → 10 kills on foot → `/corerpg sign` → `/hub` → walk to NPC (ch1 done, +5 vanilla levels) → menu daily 开始挑战 → solo clear on foot in under 1 min with 0 deaths → ch2 kill + clear steps done. All checks PASS, and elite XP counts.

## Not addressed yet
Other menu text inaccuracies, the AuthMe account-list leak, the cheap talent tree, pass pacing.
