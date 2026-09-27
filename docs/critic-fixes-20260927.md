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
- AFK skeletons suffocating in the spawner hill: fixed in round 3 (§12).
- I also tried a CoreRpg "lift suffocating mobs" handler. I reverted it because lifted mobs vanished from the room and stalled waves.

# Round 3 (2026-09-27 ~03:00–05:30 CST) — real-combat balance, CoreRpg 1.9.0

## 11. Gear stats were cosmetic → CoreRpg stat layer
- AttributePlus is parked, so the lore `物理伤害 / 生命力 / 物理防御` on every Ember blade and talisman did nothing: a T2 blade was a diamond sword, and talismans added nothing.
- CoreRpg 1.9.0 `StatService` applies lore, enhance levels, gems, reforge affixes and talent stats 1:1. Details and the per-gate player numbers are in `docs/ember-gear-stats.md`.
- Vanilla wither-skeleton on-hit Wither (10 s, ignores defense) is stripped for players. It was the main solo boss killer.
- MythicMobs HP is capped at 2048 (spigot.yml), so the big bosses use `DamageModifiers ENTITY_ATTACK` for effective HP. The CoreRpg bonus runs at LOWEST priority so the modifier scales the whole hit.

## 12. Dungeon balance (bots on foot, non-op; op only for level, gear, tickets)
Harness: `mineflayer-tests/dungeon-balance.js` (`D=weekly,abyss,calamity,guild,raid`).
- Fresh non-op bots are pushed to the gate level with the mainline gear, then enhanced for real with `/corerpg enhance`, and get Sharpness II.
- They fight with pathfinder and attack at about full charge (every 650 ms). Bots face-tank and never dodge, so real players should end a bit higher.
- Mob weapons and body armour were removed, so `Damage` is the real number; difficulty is easy.
- Wave-end heals: Instant Health III (16 HP) in weekly, abyss, raid and guild; II in the daily. All `scattered` values are 1.0.

| Dungeon | Party | Boss TTK | End HP (display) | Key mob numbers |
|---|---|---|---|---|
| Weekly | 1× Lv20, T1 blade +2, T0 charm +1 | 37 s (32–38 over 3 clears) | 31 % (29–65 %) | zombie 90/4, skeleton 70, BruteA 350/4, BruteB 1300/1 (skills do the damage) |
| Abyss (8 floors) | 1× Lv25, T1 blade +3, T1 talisman +2 | floor-8 watcher 30 s (floor 5: ~30 s) | 69 % (last), earlier 29 % / 6 % before the last cut | zombie 110/5, skel 80, mix 130/5, brute 450/4, watcher 1100/1, KB-res 0; floor 8 = watcher + mix |
| Calamity (public boss) | 1× Lv30, T2 blade +3, T1 talisman +2 | 43 s | 52 % | 2000 HP ×0.8 melee taken, dmg 1, Rampage 2, Shred = Weakness I |
| Guild boss | 2× Lv30, T2 | 69 s | 32 % / 100 % | own variant `EmberGuildCalamity`: 2000 ×0.45, dmg 2, KB-res 0.3; waves 5 zombies + 3 skeletons, then brute + 3 zombies |
| Raid | 3× Lv35, T2 blade +4, T2 talisman +3 | 64 s | 69 / 17 / 40 % (avg 42 %) | footman 200/8, archer 150, elite 900/13, boss 2000 ×0.45 / dmg 3, Smash 4, SummonAid 1 footman |

- **Bosses that could never fight back:** the raid boss and the guild boss spawned at z 276. That is the raid map's 3-high outer ring, and the 2.4-block-tall wither skeleton cannot step onto the 2-high quartz ledge. They now spawn at the room centre (-40,65,270). The raid numbers above are with the boss actually engaging.
- **Large run-to-run variance:** the same weekly setup ended at 2 %, 29 %, 31 % and 65 % across runs. The tables show the last passing run for each dungeon.
- **Daily:** retuned for the T0 stats (14 per hit, 40 HP): zombie 45/3, skeleton 35, brute 110/2, heals III.
  - Non-op newbie retest (`newbie-path-smoke.js`, 05:30 CST): all checks PASS.
  - AFK chapter: 45 s on the moved spawners.
  - Daily: clear in 31 s, 0 deaths, ending at 33/40 HP. Elite XP counts (15/150).

## 13. AFK skeletons suffocating
- `EmberAfk_S1/Z1/Z2` were at Y 70 in hills where the surface is at y 69–88, so mobs spawned inside blocks.
- They moved to flat grass west-southwest of the landing point (surface 73–75): Z1 (-51,76,230), S1 (-57,75,224), Z2 (-63,76,221), radius 4. That is more than 45 blocks from the AFK respawn.
- The chapter 1 hint now reads "落点往西偏南五十来格的草坡", and the newbie test walks toward (-55,74,226) when no mob is in view.

## 14. AuthMe per-IP limits behind Waterfall
- Waterfall has `ip_forward: true`, and both backends have `bungeecord: true`.
- A bot joining via the box LAN address 172.30.0.2 was seen as 172.30.0.2 by the proxy, by AuthMe ("registered 172.30.0.2") and by the play server.
- A second registration from that IP was refused (`maxRegPerIp: 1`). AuthMe exempts 127.0.0.1, which is why local test bots can register many accounts.
- If a tunnel (playit/bore) is used later, it must speak PROXY protocol, and Waterfall `proxy_protocol` must be enabled. Otherwise every player shows as the tunnel's IP.
- The test account IpTest8968 was deleted from the AuthMe table on 2026-09-27 at 05:40 CST (row id 69); its bot password entry is removed too.

## 15. Calamity public boss scales with participants (CoreRpg 1.9.1)
- Player damage to the public calamity boss is divided by `m(n) = 1 + per_extra·(n−1)`. Here n = players within 32 blocks plus players who hit it in the last 20 s (`calamity.yml` → `scaling.*`). When n changes, nearby players see "灾厄使感应到 N 名挑战者 · 护体 ×m".
- `per_extra` is 0.15, so 3 players → ×1.3, 5 → ×1.6, 10 → ×2.35. Vanilla hurt i-frames (10 ticks) drop most overlapping hits, so team DPS barely grows with head count. At 0.6, 3 bots took 96 s against 36 s solo.
- MM `EmberCalamity` ENTITY_ATTACK modifier is back to 1.0.
- Measured (T2 blade, L30, on foot): 1 bot → boss TTK 36 s, ended at 43% HP. 3 bots → 48 s, ended at 82–100% HP, 0 deaths.
- Fix: the MONITOR damage tracking and the death settlement now check the boss's world (`ember_event`). A guild boss with the same display name no longer settles as a calamity kill.

## 16. Weekly end-HP variance (was 2–65%)
- Brute A and Brute B are ZOMBIE now (HUSK hunger blocked natural regen between waves), with PreventSunburn. Wave 1 is 3 zombies.
- Brute B (1300 HP):
  - Random bursts removed (ChainSlam knock-up and GuardPulse procs).
  - Melee lowered to 2, KnockbackResistance raised to 0.6.
  - New steady ember aura: 1 dmg every 2 s within 8 blocks, so damage still lands when the brute is kited or doesn't engage. Before this, fast-kill runs ended at 80–100%.
  - Every 15 s it heals players within 20 blocks by 6 HP ("余烬回暖", a predictable mid-fight heal).
- Result, 3 runs (L20, T1 blade, on foot): end HP 33 / 40 / 53%, boss TTK 34–36 s, 0 deaths.
  - For comparison, melee 3.25 without the aura gave 82 / 46 / 35%.

## 17. Daily tuned from ~80–100% end HP to ~50–60% (newbie on foot, 0 deaths)
- Brute (`EmberDailyBrute`):
  - ZOMBIE instead of HUSK (hunger), PreventSunburn.
  - 180 HP, melee 2, KnockbackResistance 0.6.
  - Random tap and soft-hit procs removed; they were replaced by a steady aura (0.5 dmg every 2 s within 6 blocks).
- Wave-end heals are back to instant_health III (+12 HP). Waves are unchanged (zombie 45 HP / 4 dmg, skeleton 35 / 4).
- The newbie harness (`newbie-path-smoke.js`, non-op, fresh account) also counts the vanilla death message now. The `death` event missed an instant respawn, and one earlier run died while reporting 0 deaths.
- Newbie results:
  - Brute 180: end 48%, lowest HP 47%, 0 deaths.
  - Brute 200: end 58% and 42%, 0 deaths.
  - The lowest point is at the end of wave 2, just before the heal.
- The stronger balance bot (L16, blade + charm, Sharpness II) ended at 60–76% on the earlier, harder variant.

## 18. Skill rework (CoreRpg 1.10.0)
- Skills now scale off the full-charge basic hit, with a hard cap of 2.5×. Details and tests are in `docs/ember-skills-passives.md`.
  - 烬斩: 1.5× in the arc.
  - 灰印: +20% damage taken from all players for 6 s, plus Slowness II.
  - 壁垒: Resistance II for 3 s, plus a 4 s taunt.
- Covenant pseudo-stats are coded but off: `stats.apply_covenant_stats: false`. With them on, blaze's 1% life steal lifted a solo weekly to 93% end HP.
- Result with skills (covenant stats off):
  - Weekly: boss TTK 32–33 s, end HP 48–49%.
  - Raid: TTK 63 s, end HP 35 / 65 / 100%.
- Play-server restarts, each about 13 s (the playtester was online):
  - 08:06:40–08:06:53 CST: deploy 1.10.0.
  - 08:15:16–08:15:29 CST: deploy the covenant-stats flag.

## 19. Phase 0 (CoreRpg 1.11.0, CoreEnchant 1.2.0) — life skills + report top items
Design and resource numbers: `docs/ember-master-plan.md` §8.
- **Food.** Chapter 1 starts with 16 余烬面包. The hub vendor (`/corerpg life`, /ember → 补给 · 生活) sells bread 40 币/8 and a rod for 60 币. It also cooks cod or salmon for 1 币 each into 炭烤余烬鱼 (Regeneration I for 6 s). A CoreSmelt furnace recipe does the same.
- **Brewing and exchanges.**
  - 余烬回复药: Instant Health II, 20 s cooldown.
  - Relic ×5 → 重铸石: 5 per week.
  - Pearl ×3 + core fragment ×3 + 300 币 → 稳定符: 1 per week.
  - Life level (fishing, cooking, brewing) only unlocks recipes.
- **Quest state checks.** When a step starts, and again every 10 s, it completes if its state is already true:
  - signed today, or bounty claimed today;
  - covenant chosen, or a talent point spent;
  - any ember gear enchanted, or at +N;
  - level reached.
  - Steps may also list alternative events as `a|b`.
- **Chapter 2** is now: daily kills → daily clear (T1 blade + 2 crystals + 10 shards) → covenant → talent → enchant → enhance +3 → Lv20 (weekly ticket).
  - Players already past step 1 of the old flow get the T1 blade once on join.
  - Resources for a new player are enough: 18 shards, 3 crystals, 100+ 币, 9 talent points.
- **Duplicate gear removed.**

  | Source | Now gives |
  |---|---|
  | Weekly first clear | T1 talisman only |
  | Ch3 end | Shards 15 + crystals 2 + abyss tickets 2 |
  | Ch4 end | Sharp gem + core fragments 5 + protect scroll |
  | Ch5 end | Stable charm + raid ticket |
  | Abyss first clear | T2 blade + T2 talisman (unchanged, the only T2 guarantee) |

- **Menus.**
  - Weekly: about 2–3 min per run, 1 free ticket per week, 1 more from the shop, and 1 from chapter 2.
  - The ch2 intro and the ch5 hint no longer say "once a day".
  - Calamity: the OP test button is removed, and 灰烬巨像 is renamed 余烬灾厄使.
- **Dungeons.**
  - The DP whitelist now allows `corerpg quest|stats|skill`.
    - **Before this fix `/corerpg skill` was silently blocked inside dungeons, so the 1.10.0 "with skills" numbers in §18 were really without skills.**
  - `/hub`, `/spawn`, `/ember` and similar now reply "离开请用 /dp leave". Any other blocked command gets a short hint.
  - Reconnecting inside an instance gives a full heal, Resistance V for 7 s and a message.
  - On entry, the heal is repeated at 5, 25 and 45 ticks. A bot sample showed 40/40 on arrival.
- **Small fixes.**
  - Clicking an enchant slot without enough crystals or levels now explains why (ProtocolLib ENCHANT_ITEM listener).
  - `/corerpg stats` shows the covenant and whether its stats are applied.
- **Economy.**
  - AFK caps (ember_afk/world only): shards 150, bone dust 80, core fragments 10, kill coin 150 per day, then 25%.
  - Enhance fee: 10 + 10 × target level.
  - Anvil repair fee: 20 币.
- **Tests.**
  - `mineflayer-tests/phase0-smoke.js`: all checks pass (starter food, vendor, cook, level gate, covenant/talent/enhance quest steps, already-done check, fee, dungeon hints, stats/quest/skill in dungeon, reconnect grace).
  - `newbie-path-smoke.js`: ch1 → daily pass, and the quest moves on to the covenant step.
  - Fishing: a bot's bobber lands in water, but about 20 casts saw no bite (no BITE event). This needs a human check.
  - Weekly re-test now that skills really fire (L20, T1 blade +2, charm +1, Sharpness II; the harness now also gives coins for the enhance fee):

    | Covenant | Boss TTK | End HP |
    |---|---|---|
    | blaze + 烬斩 | 33 s | 62% |
    | blaze + 烬斩 | 35 s | 47% |
    | no skill | 31 s | 27% |

    Still inside the 30–60% band, so no retune.
- Play-server restarts: 08:38:22–08:38:34 and 08:51:19–08:51:32 CST.

## 20. Phase 1 (CoreRpg 1.12.0) — fishing root cause, forge pity, free sources, paid-track balance

- **Fishing ("no bites in ~20 casts").**
  - CoreFish (wait 40–120 ticks, override loot) and Paper's FishNmsHooks are fine. No core change.
  - Root cause: the old spot (33, 57, 107) is 1-deep **flowing** water over obsidian, under the hub roof (sky light 0), next to lava. `/corerpg life fishdebug` (new, op) showed the bobber drifting onto stone or lava within about 4 s, so it never reached the bobbing state. The only event was FAILED_ATTEMPT on reel.
  - In still water at the east pond (stand at 62, 70, 104 and cast north), a non-op bot fishing for 30 min got 87 catches. Bite median 12.9 s (p10 9.0 s, p90 17.1 s); about 2.9 fish per minute including recasts. 14 casts timed out or missed because the pond is small.
  - The rod button in the `ember_life` menu now names the pond and warns about flowing water or lava.
- **End to end (non-op bot, fished items).** relic ×5 → reforge stone; pearl ×3 + core fragment ×3 → stable charm (a second one is blocked by the weekly limit of 1); bone ×2 → soul dust; soul dust ×10 → pet egg; `/corerpg pet summon` unlocked the pet. Top-ups printed by `life-e2e.js`: life XP 300 (the bot was Lv4 with 174 XP), core fragment 3 (a dungeon material) and soul dust 9.
- **Forge pity** (`/corerpg forge [confirm]`, TrMenu `ember_forge`, hub button 'l', `forge.yml`).
  - T1→T2: core fragment ×10 + shard ×40 + 1500 coins.
  - T2→T3: 凝核 ×3 + core fragment ×25 + 灾厄余烬 ×6 + 5000 coins.
  - Keeps floor(enhance ÷ 2), vanilla enchants (the higher of old and new), affix lines, and gems in slots that are still unlocked. Gems in slots that become locked go back to the inventory.
  - Bot test: T1 +7 → T2 +3 (Fire Aspect kept, steady gem returned) → T3 +1 (base Sharpness I and Unbreaking II plus Fire Aspect).
- **凝核 sources.**
  - Raid boss: `corerpg mmgiveall` gives one to every player within 64 blocks in the instance. A 3-bot raid cleared (boss TTK 51 s) and all 3 got one.
  - Calamity: the first daily chest of each week gives 1 more. A bot saw "本周首个灾厄日箱：额外获得 余烬凝核×1".
  - That makes 2 per week, so one T3 takes about 1.5 weeks of 凝核, and a blade plus talisman takes about 3–4 weeks (coins are the bottleneck).
- **Free sources.**
  - Soul dust: 旧靴 ×2 or 碎骨 ×2 + 5c (2 per day each, life Lv2), and AFK mobs at 2% (cap 2 per day).
  - Pet eggs: soul dust ×10 + 600c (weekly 1 each, life Lv4).
  - Stable charm: weekly Brute B 4% and raid boss 5%, on top of life 1 per week.
- **Paid track (progress.yml).** Protect scroll 3 → 1 and steady gem 1 → 0. Sharp gem 1, protect scroll 1 and stable charm 1 are each about 18–25% of the free monthly amount (master plan §7.2).
- **Master plan** §2.1, §2.2, §3.3, §6.1, §7.2 and §8 updated with these numbers.
- Play-server restart: 09:25:25–09:25:38 CST. Config reload (`/corerpg reload`, `/trm reload`) at 10:05 CST.
