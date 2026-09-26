# Level gates, bounty XP, proxy test ports — 2026-09-26 (CoreRpg 1.7.0)

## Level gates (余烬等级)
Source: DESIGN-ember-growth-curve §1. Configurable in `plugins/CoreRpg/progress.yml` → `level_gates`.

| Content | Gate | Where enforced |
|---|---|---|
| 余烬窟·日 EmberDaily | Lv.10 | DP option.yml js-condition (existing Lv10 players keep access) |
| 余烬窟·周 EmberWeekly | Lv.20 | DP option.yml + TrMenu hint |
| 深渊 EmberAbyss | Lv.25 | DP option.yml + TrMenu hint |
| 灾厄公共窗 | Lv.30 | `/corerpg calamity join` (menu 奔赴灾厄 now uses it) |
| 团本 EmberRaid | Lv.35 | DP option.yml + TrMenu hint |
| 盟 Boss EmberGuildBoss | none | spec: "在盟即可" (guild membership + contribution gate) |

- Curve reference (60+12·max(0,L−10) XP/level): Lv20 ≈ 1–2 days of daily+sign+kills, Lv25 ≈ 3–4 days, Lv30 ≈ ~1 week, Lv35 ≈ ~1.5 weeks.
- The gate condition sits **before** the ticket-deduct condition, so a refused team keeps its ticket.
- Every team member is checked; the message names the level: `§c<副本>需要余烬等级 Lv.N · 队伍中有人等级不足（/corerpg level 查看）`.
- OPs bypass (`%player_is_op%`). New PAPI: `%corerpg_ember_level%`, `%corerpg_gate_<id>%` (yes/no).

## Bounty
`/corerpg bounty claim` (1/day, natural cap) → ember XP +40 and pass XP +15 (`sources.bounty`), pass XP still subject to the daily pass cap (100).

## Tests
- `gates-smoke.js`: daily at Lv10 OK; weekly refused + ticket kept; abyss refused; calamity refused; weekly OK at Lv20 + ticket used; bounty XP; second claim refused — all PASS.
- Ported to `lib/proxy-login.js` (`joinPlay`, new `ensureLevel(op, bot, lvl)` for gated content):
  `dungeon-clear-smoke.js` (PASS), `raid-combat-smoke.js` (all PASS with fresh bot names), `guildboss-gate-smoke.js` (all PASS with fresh L/M names;
  guild weekly limit temporarily raised during the test, restored), `guild-boss-smoke.js` (DONE), `calamity-raid-set-smoke.js` (all PASS),
  `calamity-combat-smoke.js` (both windows killed, drops, no 2nd spawn), `guildboss-refund-smoke.js` (ported, needs its temporary DP condition to run).
- Re-running with the same bot names can fail for state reasons (weekly raid ring already claimed, guild weekly boss used, leftover contribution).
