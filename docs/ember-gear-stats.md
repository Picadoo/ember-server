# Ember gear stats — how NI lore turns into combat numbers (CoreRpg 1.9.0, 2026-09-27 CST)

> **D242 指针（2026-10-06）：** 现行装备结构的权威说明已合并到 [`docs/design/DESIGN-ember-gear-structure-2026-10-06.md`](design/DESIGN-ember-gear-structure-2026-10-06.md)（CoreRpg 1.65.67 / bv58）。本文描述**旧 StatService 词条**，P1 结算不读 lore 词条；现行数值见新文档 §2 / P1 书第 07 章。

Why: AttributePlus is parked (`plugins/_parked`, the server hangs on startup with it), so the lore lines on Ember gear
(`物理伤害: +14`, `生命力: +35`, `物理防御: +6`) did nothing. A T1/T2 blade was just a diamond sword and talismans were decoration.
CoreRpg `StatService` now applies them. Config: `plugins/CoreRpg/config.yml` → `stats:`.

## Sources (summed)
| Source | Where it counts | Stats |
|---|---|---|
| Blade lore (`gear_ember_blade`, `_t1_`, `_t2_`, `_t3_blade`) | main hand only | `物理伤害` → phys_damage |
| Charm / talisman lore (`gear_ember_charm`, `_t1/_t2/_t3_talisman`) | best one anywhere in the inventory or offhand | `生命力` → max_health, `物理防御` → phys_defense |
| Enhance level | that item | level × `enhance.yml stat_per_level` |
| Socket gems | that item | `enhance.yml gems` (sharp +2 dmg, steady +2 def, drain 1% leech) |
| Reforge affixes | that item | crit_chance %, max_health, phys_defense |
| Unlocked talent nodes | always | phys_damage, max_health, phys_defense, crit, life steal, damage_taken_pct, charm_stat_bonus_pct |

## Effects
- **Damage** (player → mob melee, PvP untouched): `+ phys_damage × damage_scale (1.0)`, scaled by attack charge (time since
  the player's last hit, vanilla curve 0.2 + 0.8·t² over 625 ms) so click-spam gets ~20 %. Applied at LOWEST priority, so
  MythicMobs `DamageModifiers` scale the whole hit.
- **Max HP**: `20 + max_health × health_scale (1.0)` via GENERIC_MAX_HEALTH, refreshed every second, on join/respawn/hotbar change.
  The heart bar is scaled to at most 40 (2 rows).
- **Defense** (mob → player, incl. MM skill damage): damage × `(1 − def/(def+20))` × `(1 + damage_taken_pct)`, min ×0.2.
- **Wither skeletons**: their vanilla 10 s Wither I on hit ignores defense and dominated solo boss fights; it is stripped
  the next tick (`stats.strip_wither_on_hit`). MM skills that apply wither with other durations still work.
- `/corerpg stats [player]` (alias `属性`) prints the totals.

## What the mainline hands out (gear used for balance)
| Gate | Gear (mainline) | Assumed upgrades | Hit (full charge) | HP | Damage taken |
|---|---|---|---|---|---|
| Lv16 daily | T0 blade (iron) + T0 charm | none | 6+8 = 14 | 40 | −13 % |
| Lv20 weekly | T1 blade (ch2) + T0 charm | blade +2, charm +1, Sharpness II | 7+20+1.5 ≈ 28.5 | 44 | −17 % |
| Lv25 abyss | T1 blade + T1 talisman (ch3) | blade +3, talisman +2, Sharp II | 7+23+1.5 ≈ 31.5 | 65 | −33 % |
| Lv30 calamity / guild | T2 blade (ch4) + T1 talisman | blade +3, talisman +2, Sharp II | 7+34+1.5 ≈ 42.5 | 65 | −33 % |
| Lv35 raid | T2 blade + T2 talisman (ch5) | blade +4, talisman +3, Sharp II | 7+38+1.5 ≈ 46.5 | 96 | −49 % |

Enhance +2…+4 costs 8–26 shards (100/90/80/70 %), which the mainline and weekly boxes cover; Sharpness II is one enchant
with the ch1 crystal. Dungeon mob numbers are in `docs/reviews/critic-fixes-20260927.md` round 3.
