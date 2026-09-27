# Ember covenant skills (CoreRpg 1.10.0) and gear passives (parked)

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
- **Why covenant stats are parked:** turning on the covenant pseudo-stats (blaze 1% life steal) is what pushed the solo weekly to 93%.
  - They are coded, but off (`config.yml stats.apply_covenant_stats: false`) until the phase-2 rebalance in `docs/ember-master-plan.md`.

## Gear passives: designed, parked (not deployed)
Code: `GearPassiveService` (kept out of the build). Planned numbers for `passives.yml`, per `docs/ember-master-plan.md` phase 2:
- **T1 blade 余烬引燃:** 15% on a charged hit (≥ 0.7 charge). Fire, plus an ember DoT of 0.12× basic hit per second for 3 s. Internal cooldown 6 s.
- **T2 blade 炽愈:** on a crit (CoreRpg crit or vanilla jump crit), heal 2 HP. Internal cooldown 3 s.
- **T3 blade 烬爆:** every 5th charged hit, an ember burst of 0.6× basic hit on the target and up to 4 mobs within 3 blocks. Internal cooldown 2.5 s.
- **余烬同袍 set:** +5% damage while at least 2 set wearers (including self) are within 24 blocks. Kill heal from `set.yml`.
