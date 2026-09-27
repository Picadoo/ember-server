# Ember covenant skills (CoreRpg 1.10.0), gear passives and covenant stats (CoreRpg 1.13.0, phase 2)

2026-09-27 CST. All numbers are in `plugins/CoreRpg/skills.yml`.

## Skills: scaling
- **Full-charge basic hit** = vanilla attack attribute (weapon) + Sharpness (0.5·lvl + 0.5) + gear 物理伤害 (lore + enhance + gems + affixes + talent stats). No crit.
  - Computed live by `StatService.fullHitDamage`. `/corerpg skill info` shows it.
- **Skill damage** = `damage_mult` + `talent_mult_per_node` (0.1) × unlocked talent nodes of that skill (at most `talent_bonus_max`) × the basic hit.
  - Clamped to `max_hit_mult` (**2.5**) × the basic hit.
- **How the damage is dealt:**
  - It is the player's attack, so MythicMobs damage modifiers and calamity participant scaling still apply.
  - `noDamageTicks` and `lastDamage` are restored afterwards, so a skill never eats the player's next swing (hurt i-frames).
  - The StatService flat bonus does not apply a second time, and skill damage never procs passives (`SkillService.internalDamage`).
- Cooldowns are unchanged (8 / 10 / 12 s), in memory.

## The three skills

| Skill | CD | Effect | Numbers |
|---|---|---|---|
| 烬斩 (blaze) | 8 s | Frontal arc, 3.5 blocks, 100°, up to 6 targets | 1.5× basic hit per target (talent up to 1.8×) |
| 灰印 (ash) | 10 s | Light strike, then marks the target for 6 s | Strike 0.5×. Marked target takes +20% damage from **all** players (+2% per talent node). Slowness II for 4 s. Range 8 |
| 守墓壁垒 (warden) | 12 s | Self Resistance II (−40%) for 3 s. Every mob within 5 blocks (max 8): 0.5× shockwave, Slowness I for 2 s, **taunt** 4 s | Taunted mobs are set to target the warden; retargets away from the warden are redirected back while the taunt lasts |

- **Stacking example:** 烬斩 on a 灰印-marked target = 1.5 × 1.2 = 1.8× a basic hit. The 2.5× cap applies before the mark multiplier; the mark itself is capped at +150%.
- **Measured basic hit / skill damage** (bots, Sharpness II):

  | Setup | Basic hit | 烬斩 |
  |---|---|---|
  | L20, T1 +2 | 29 | 43 |
  | L25, T1 +3 | 32 | 47 |
  | L35, T2 +4 | 47 | 70 |

  - 灰印 strike: 23. Warden shockwave: 21.

## Test with skills (bots press the skill whenever it is off cooldown in melee)
Harness: `COV=blaze,ash,warden D=... node dungeon-balance.js` (covenant per bot index).

| Run | Covenant stats | Boss TTK | End HP | Casts |
|---|---|---|---|---|
| Weekly, solo blaze | on (1% life steal) | 37 s | 93 % | 9 |
| Abyss, solo blaze | on | 35 s | 37 % | 16 |
| Raid, blaze / ash / warden | on | 62 s | 100 / 30 / 84 % | 13 / 11 / 9 |
| Weekly, solo blaze ×2 | **off** | 32 / 33 s | 49 / 48 % | 9 / 9 |
| Raid, blaze / ash / warden | **off** | 63 s | 35 / 65 / 100 % | 15 / 12 / 9 |

- **Before skills:** weekly 34–36 s, abyss about 30 s, raid 64 s.
- **Effect:** with covenant stats off (the deployed config), skills cut weekly boss TTK by about 8% (34–36 s → 32–33 s). Weekly end HP stays in 30–60%, raid TTK is unchanged. No boss becomes trivial, and no retune was needed.
- **Covenant stats were parked in 1.10.0** because blaze's 1% basic-hit life steal pushed the solo weekly to 93%. Since 1.13.0 they are on, with skill-only life steal (see below).

## Gear passives (live since 1.13.0)
Code: `GearPassiveService` (from the `/workspace/wip` draft). Numbers in `plugins/CoreRpg/passives.yml`. Only basic swings proc; skill and passive damage never procs (`SkillService.internalDamage`).
- **T1 blade 余烬引燃:** 15% on a charged hit (≥ 0.7 charge). Fire, plus an ember DoT of 0.12× basic hit per second for 3 s. Internal cooldown 6 s.
- **T2 blade 炽愈:** on a crit (CoreRpg crit or vanilla jump crit), heal 2 HP. Internal cooldown 3 s.
- **T3 blade 烬爆:** every 5th charged hit, an ember burst of 0.6× basic hit on the target and up to 4 mobs within 3 blocks. Internal cooldown 2.5 s.
- **余烬同袍 set:** any ember blade + raid ring in the inventory. +5% damage while at least 2 set wearers (including self) are within 24 blocks (cap 25%). Kill heal 2% max HP (`set.yml on_kill: saturation_1,heal`). `/corerpg set` shows the resonance.
- `passives.yml debug: true` logs procs. Across the phase-2 runs: ignite 105, crit heal 59, set bonus 34 (sampled at 5%), skill heal 142. T3 burst was not exercised (no harness run uses a T3 blade).

## Covenant stats (on since 1.13.0, `config.yml stats.apply_covenant_stats: true`)

| Covenant | Stats | Layer |
|---|---|---|
| 烬刃 blaze | crit 3%; **skill life steal 4%** | Skill damage actually dealt per cast (after MM modifiers). The heal is capped at 3% max HP per cast (`stats.caps.skill_heal_per_cast_pct`). Basic-hit life steal stays gem-only (汲 gem, cap 5%) |
| 灰行 ash | attack speed 4%; move speed 2% | `GENERIC_ATTACK_SPEED` ×1.04 and a faster CoreRpg charge curve (cap 10%); walk speed ×1.02 (cap 8%, shared with the 疾 gem, which previously had no effect) |
| 守墓 warden | damage taken −4%; charm stats +5% | Existing defense layer (floor −25%) |

- Why blaze changed: 1% basic-hit life steal took a solo weekly to 93% end HP (1.10.0). With skill-only life steal the blaze heal is about 1.3–2 HP per 烬斩 (every 8 s).

## Phase-2 combat tests (1.13.0: passives + covenant stats + skills pressed on cooldown)
Harness: `mineflayer-tests/dungeon-balance.js` (`RING=1` gives every bot the raid ring for 同袍). End HP is the client value (health scale caps the display at 40, so it is a percentage of max). "r1" means the numbers before retuning mobs.

| Run (covenant per bot) | Boss TTK | End HP | Mob values |
|---|---|---|---|
| Weekly, solo blaze — r1 | 26 s | 51% | Brute B 1300 |
| Weekly, solo ash / warden — r1 | 38 / 33 s | 33 / 34% | Brute B 1300 |
| **Weekly, solo blaze / ash** | **36 / 37 s** | **41 / 41%** | Brute B **1500** |
| Abyss L8, solo blaze — r1 | 27 s | 78% | watcher 1100 / dmg 1 |
| Abyss L8, blaze / warden (dmg 4) | 37 / 35 s | 6 / 80% | watcher 1500 / dmg 4 |
| **Abyss L8, solo blaze / warden** | **35 / 35 s** | **38 / 91%** | watcher **1500 / dmg 3** |
| Calamity, 1 bot blaze — r1 | 38 s | 100% (boss rarely hit the bot) | dmg 1 |
| Calamity, 1 bot blaze / ash (dmg 2) | 34 / 32 s | 47 / 66% | dmg 2 |
| **Calamity, 1 bot blaze (final)** | **35 s** | **27%** | dmg **2** + shred **5 to all in 18** |
| Calamity, 3 bots — r1 | 33 s | 100 / 95 / 79% | per_extra 0.15 |
| **Calamity, 3 bots (final)** | **65 s** | **83 / 76 / 83%** | per_extra **0.30** + shred damage |
| Raid, 3 bots + ring — r1 | 51 s | 100 / 100 / 90% | apostle dmg 3 |
| Raid, dmg 4 / 3.5 + rain 8 / 3 + rain 8 (×2) | 50 / 47 / 48 / 45 s | warden tank 20 / 12 / 9 / 11%, blaze + ash 74–100% | — |
| **Raid, 3 bots + ring (final, ×2)** | **56 / 53 s** | **59 / 60 / 100%; 87 / 34 / 100% (warden died once)** | apostle dmg **2.5** + **EmberRaidEmberRain 12 to all in 24, every 12 s** |

- **Verdict:** every boss is inside 30–90 s. Solo weekly, abyss (blaze) and calamity end at 27–41%.
- **What is still off:**
  - Solo warden in the abyss ends at 91% (Resistance II plus −4% damage taken).
  - 3-bot calamity ends at 76–83%, because the public boss splits its damage across the group.
  - Raid end HP swings run to run, depending on whether the warden's taunt holds the apostle. Group average is about 65–75%. The raid-wide ember rain now puts damage on the dps too.
- Real players are not saturated the way the harness bots are (the harness gives Saturation, so vanilla regen is fast), so live end HP should come in lower.
