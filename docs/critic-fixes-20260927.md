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


# Round 2 (2026-09-27 ~02:00 CST)

## 6. AuthMe account-list leak (security)
- `login-runtime/plugins/AuthMe/config.yml`: `displayOtherAccounts: false`. Every player reaches AuthMe from 127.0.0.1 (Waterfall on the same box), so "You own N accounts" listed every registered name to anyone who logged in. The login server was restarted, and a fresh bot login no longer shows the line.
- The "Welcome … on Unknown Server" line was already replaced by `welcome.txt` ("Welcome {PLAYER} to Ember").
- Note: `maxRegPerIp: 1` means little behind a local proxy, since everyone shares one IP. Not changed.
- The playtest report is committed only as `docs/critic-20260926-redacted.md`, with account names and the leaked list removed.

## 7. Kill credit, no distance limit
- `mmgive` / `mmxp` / kill counting credit the vanilla killer, or else the **last player who ever damaged the mob** (no time or distance limit, same world, online).
- If no player ever damaged the mob, it drops nothing. The 16-block nearest-player fallback is removed.
- Debug switch: `debug.mm_credit: true` in `plugins/CoreRpg/config.yml` logs every credit decision, including the entity's last damage cause.
- That log showed the real reason for most "Unknown" kills: **SUFFOCATION** (see §10).

## 8. Menu text (the list in the report)
- **Daily:** "2 波小怪 → 蛮兵（首领）", "单人约 1～2 分钟" (was 3 waves / 8–12 min).
- **Abyss:**
  - Tiers are "1～4 / 5～8（共 8 层）" with the correct rewards (10% gem).
  - Clear text is "本周首通 T2 刃+护符 · 之后 4% / 3%".
  - The entry tell no longer says "深渊已开启" before the level gate. It now says "尝试下潜…需 Lv.25…等级不足不扣票".
  - "stub" removed.
- **Weekly:** "每周免费票 1 张（可再购 1 张）" (was "周限一次").
- **AFK zone:** "刷碎片与骨尘（新手先打僵尸…）· 死亡不掉落 · /hub 回城" (was "无限刷…").
- **Leftover placeholder text removed from lore/tells:**
  - Hub: "壳" ×5, "扩展页待接线", the `docs/…` path.
  - Pet / mail / friends / settings: "逻辑待接线". Those features all work and were checked with commands.
  - Mail: admin-only commands shown to players.
  - Every "详见 DESIGN-…" / `docs/…` reference in player menus.
  - Pass / shop: "占位价" (now the real price: pass 48, monthly 68), "累计日票×5（规划）" (the paid track really gives 5 at Lv6/12/18/24/30), "欢迎礼邮件演示".
- **Bestiary:** marked "暂未开放" because it really isn't implemented.
- **Enchant button:** gives the workshop coordinates and runs `/hub`.
- **Join message:** points to the mainline NPC, keepInventory and `/hub`, and lists the level gates (it used to say "击杀地窟亡灵", but the crypt has no spawns, and `/dp start EmberAbyss`, which is gated at Lv25).
- **Pass text** (`/corerpg pass` + menu) lists every source: sign 10, daily 20, weekly 40, abyss 20, raid 40, guild 20, bounty 15.

## 9. Talent tree and battle pass (conservative)
**Talent:**
- Node costs raised so each tree totals **20** (blaze 2/3/3/3/4/5, ash 2/3/4/5/6, warden 2/3/5/3/7). It was 8/7/7, which a new Lv10 player with 9 points could max on day one.
- `level_points_from: 16`: new players start with 5 points (root + one tier-1 node), then +1 per level from Lv16, for 20 points at Lv30 (the calamity gate). Levels 16–30 now each unlock something.
- Level-up gains are now capped at `max_spendable_points` (before this, Lv40 players had 39 points).
- One-time migration on join (marker `migr_talent=20260927`): earned points are capped at the new formula and the allocation is refunded with a free reset and a chat notice. Only test accounts had points.
- Menu lore updated.

**Battle pass:**
- `xp_per_level` 100 → 80 and `daily_cap` 100 → 120; still 30 levels (2400 total).
- Before: 3000 total at ≤100/day meant 30 days with zero misses. F2P reached ~Lv26 by day 30.
- Now:

| Player | Pass XP per day | Result |
|---|---|---|
| Active F2P before Lv20 | ~85 (sign 10 + 3 dailies 60 + bounty 15) | Max in ~28 days |
| Active F2P after Lv25 | ~105 (+ abyss 20, weekly 40/wk) | Max in ~23–25 days |
| Casual | ~45 | ~Lv17 in 30 days |
| 6 bought daily tickets | cap 120 | ~20 days |

- The season still needs steady play but tolerates a few missed days.

## 10. Dungeon mobs were spawning inside solid rock (found while checking kill credit)
- I read the DP map templates with `anvil-parser`. **Every** Ember dungeon map (daily, weekly, abyss, raid/guild, calamity) is a small room around the landing point (-40,65,270): the daily is x -42..-38 × z 268..272, the raid about 13×13.
- Most spawn points in `monster.yml` (-40,65,275 / -40,65,278 / -48,65,275 / -32,65,273 …) were inside stone. Mobs and **bosses** spawned in rock and suffocated. That produced the "~1 min daily", the 0/150 elite XP, "Unknown" kills and free boss kills.
- Every blocked point now moves to the nearest open cell in the reachable room (`scripts/dp_map_reach.py` + `scripts/dp_fix_spawns.py`):
  - Abyss: 10 points
  - Calamity boss: 1
  - Guild boss: 3
  - Raid: 5
  - Weekly: 6
  - Daily: 4
- **Effect: weekly, abyss, raid, guild boss and calamity bosses now have to actually be fought.** They were effectively free before. Their balance is untested with real fights and may need tuning (the raid is meant for 3–5 players).
- Daily rebalanced for a solo Lv16 newbie with T0 gear:
  - Waves are 2 zombies, then 1 zombie + 2 skeletons, then the brute.
  - Each wave end heals 8 HP.
  - Zombies drop the wood sword (+4 damage) and are 20 hp / 2 dmg.
  - The brute drops the iron sword (+6) and is 40 hp / 4 dmg.
  - The chapter 2 kill step is 6.
  - Result: the no-op bot clears solo on foot in about 30 s with 0 deaths and ends at 4 HP. The brute is killed by the player, so elite XP counts (15/150).
- Still open: AFK skeletons also suffocate inside the spawner hill (they give no drop, which is correct, but they waste spawns).
- I also tried a CoreRpg "lift suffocating mobs" handler. I reverted it because lifted mobs vanished from the room and stalled waves.
