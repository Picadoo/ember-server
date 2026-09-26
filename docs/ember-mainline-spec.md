# 余烬主线 · 第一卷「旧誓余烬」— spec (2026-09-26 CST)

Why: new Lv10 players had only dailies and grinding, and no guidance. The mainline walks them through existing content up to Lv35.
Implemented in CoreRpg 1.8.0, revised in 1.8.1 after the 09-26 playtest (`QuestService`, `plugins/CoreRpg/quest.yml`). Progress is stored in the player YAML blob in MySQL (`questChapter/questStep/questCount`).

## Flow
- First join → chapter 1 auto-starts and gives the **starter weapon** `gear_ember_blade` + `gear_ember_charm` (T0). Before this, new players got no weapon. If a player has lost the blade, talking to the NPC reissues it once per day.
- Safety (1.8.1): keepInventory is on, `/hub` (`/spawn`, `/回城`) returns to the hub, the ember_afk respawn is more than 32 blocks from the spawners, and entering a dungeon gives a full heal plus 5 s Resistance.
- Hub NPC **引路人·灰烛** (Adyeshach villager `ember_guide`, 4 blocks north of the hub spawn next to the workshop, `ember_hub -16.5 58 106.5`; CoreRpg creates it on startup if missing, `/corerpg quest npc` re-places it). Right-click = talk step / shows the current objective.
- Objective display: sidebar line `主线 …`, action bar on progress, `/corerpg quest`, `%corerpg_quest%`, and a 主线 button in `/ember`.
- Step types: `talk` (NPC), `kill` (MythicMobs internal names, `Prefix*` allowed), `event` (sign · bounty · daily_clear · weekly_clear · abyss_clear · raid_clear · guild_boss_clear · enchant · anvil · craft · calamity_join · calamity_boss), `level`.
- Mainline XP is flat and **not** subject to the daily kill/combat caps. Level-gate steps (Lv20/25/30/35) close each chapter and hand out the tier gear.

## Chapters (quest XP only; normal sources come on top)
| Ch | 名称 | Lv | Steps | Quest XP | End reward |
|---|---|---|---|---|---|
| 1 | 余烬初醒 | 10 | talk 灰烛 · 6 EmberAfkZombie · 4 AFK zombie/skeleton · sign · talk (+5 vanilla levels) | 520 | daily ticket, enchant crystal, shards |
| 2 | 残窟之门 | ~16 | 6 EmberDaily* · clear daily · enchant ×1 · anvil ×1 · **Lv20** (+3 levels) | 580 | **T1 blade**, weekly ticket |
| 3 | 周烬试炼 | 20 | talk · 15 EmberWeekly* · clear weekly · claim bounty · **Lv25** | 630 | T1 talisman, 2 abyss tickets |
| 4 | 深渊回响 | 25 | 20 EmberAbyss* · clear abyss · clear abyss again · clear daily · **Lv30** | 820 | **T2 blade**, gem |
| 5 | 灾厄之窗 | 30 | calamity join · fight the calamity boss · clear abyss · clear daily ×2 · **Lv35** | 1100 | T2 talisman, raid ticket |
| 6 | 同袍之誓 | 35 | talk · 20 EmberRaid* · clear raid · talk | 1060 | compact core, protect scrolls |

Vanilla levels for enchanting and repair: +5 (ch1), +3 (ch2), +5 at each of Lv25/30/35, on top of the levels the DP clears already give (`Given N levels`).

## Pacing (curve 60+12·max(0,L−10) per level)
| Gate | XP needed | From mainline | From the steps' own sources + dailies | Sessions |
|---|---|---|---|---|
| Lv10→20 weekly | 1140 | 1100 | sign 20 + daily 60 + kills | 1 (≈60–90 min) |
| Lv20→25 abyss | 1020 | 630 | weekly 200 + bounty 40 + ~1 day dailies | 2 |
| Lv25→30 calamity | 1320 | 820 | abyss 2×80 + daily 60 + 1–2 days dailies | 2–3 |
| Lv30→35 raid | 1620 | 1100 | boss 40 + abyss 80 + daily 2×60 + 1–2 days | 2–3 |
Roughly 8–9 play sessions from a new player to raid, not weeks.

## Story tone
Short, grounded lines: the ember-keeper 灰烛 in a ruined town, ash, old oaths, the crypt under the hub. 2–3 lines per step.

## Implementation notes
- Dungeon kill steps carry `complete_on: <clear>`: if the run clears before the kill count is reached (a teammate took kills, or the daily only has 9 mobs), the clear finishes the kill step and also counts for the following clear step, so nobody is stuck waiting for next week.
- MM ids are resolved through the MythicMobs API (reflection) and cached on hit and at LOWEST death priority, because MM unregisters the mob before MONITOR.
- Enchant = `EnchantItemEvent`, anvil = taking the anvil result, craft = `CraftItemEvent`; clears = `/corerpg progress <p> <source>` (the DP end scripts); calamity = `/corerpg calamity join` + boss damagers at settlement.
- Admin: `/corerpg quest set <p> <ch> <step>` · `reset <p>` · `event <p> <event>` · `npc`. Player: `/corerpg quest`, `/corerpg quest talk` (near the NPC).
- Existing players get chapter 1 (and the T0 blade) on their next login; level steps they already meet complete at once.

## Test (mineflayer-tests/mainline-smoke.js)
Fresh bot via proxy (op-assisted tp/buffs): auto-start + blade → NPC right-click → 6+4 AFK MM kills → sign → NPC → solo EmberDaily → enchant → anvil → Lv20 → chapter 3 starts. Measured: Lv16 after ch1, Lv19 after the daily, Lv20 after ch2.
`mineflayer-tests/newbie-path-smoke.js`: **no op help at all**. A fresh non-op bot walks (pathfinder) from spawn to the NPC, uses the menu to reach 挂机庭, fights on foot, signs in, uses `/hub`, walks back, and clears the menu daily on foot.
Second fresh bot: chapters 3–6 via `quest set` + real hooks (progress weekly/abyss/daily/raid clears, level steps, `calamity join`, NPC talk; `quest event` for bounty and calamity boss) → volume complete.
