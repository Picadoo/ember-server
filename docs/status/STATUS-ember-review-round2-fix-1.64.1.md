# Review round2 follow-up — CoreRpg 1.64.1 + CoreGacha 1.0.1 (D158) — 2026-10-04

This builds on CoreRpg 1.64.0 (D157, b5ac165, routine-0942, FreshQ49). It covers the reviewer's `docs/reviews/review-2026-10-04-round2.md`.
- **Deploy:** 10:01 CST restart (CoreRpg 1.64.1 + CoreGacha 1.0.1). Then a short 10:09 CST restart for the CoreGacha 1.0.1 pull-summary spark display fix.
- **Startup log:** `Enabling CoreRpg v1.64.1`, `[storage] MySQL connected`, `Enabling CoreGacha v1.0.1`, `[db] MySQL connected`, `Done (5.97s)`. No SEVERE lines. 0 players online. `ops.json` = `[]`.
- **Tests:** CoreRpg 219/219, CoreGacha 11/11. Built with JDK8 (class major 52).

## What changed on top of 1.64.0

| # | 1.64.0 (routine) | 1.64.1 / 1.0.1 (this) |
|---|---|---|
| 1 InvSnap dupe | Restore **always** held back every vault material and every gacha ticket. This stopped the dupe, but materials the player had really lost were not given back. | Held-back amounts are now based on the ledgers. The snapshot stores vault baselines (`meta.vault.*`). New table `cr_vault_log` records stash / auto / pickup / spend / withdraw. Tickets are checked against `gacha_ledger reason='redeem'` since the snapshot. Only the amount deposited (or consumed from the vault) after the snapshot is held back; anything really lost is restored. Old snapshots, or a log that can't be read, fall back to holding back everything. New `/corerpg invsnap preview <p> <id>`. Every restore prints 「X：快照 N → 恢复 M（扣 K：原因）」. Gear-library pieces were already skipped (`已在装备库`), and that is re-verified. |
| 2 Bulk dismantle | Skips invested pieces by default. | The confirm screen shows counts by quality (按成色) and how many pieces are enhanced. It lists the pieces it keeps back in red as 「✖ label（有投入：…）」. Gear-library icons are tagged 「[投入]」. |
| 3 gq26 spark | deferred | gq26 now uses `spark: 30` and `spark_items: [gq_pet_koi, gq_aura_firework]`. With `spark_leftover: carry`, the banner end (10-08) moves leftover spark 1:1 into `standard` spark (ledger `spark_carry`) instead of converting it to 光屑. Pull summary, `/gacha spark`, rates page and placeholders all show the per-banner value. |
| 5 Weekly twists | wording | D158: each conversion rule now changes the mobs it converts (`converted:` multipliers, balance_version 29). 卸甲 hp 0.8 / attack interval 0.75 (attacks more often). 卫士潮 hp 0.8 / speed 1.35. 铁卫 hp 0.85 / atk 1.5 / interval 1.25. |
| 7 Undo | deferred | Bulk txns are tagged `[批 tag]`. After a bulk dismantle the player gets a 「[撤销这一批]」 button. `/corerpg p1 undo` shows 「共 N 件」 and per-batch buttons, capped at 20 rows. New `/corerpg p1 undo batch [tag]` takes back the total blanks and refunds any item that fails. |
| 10 Text | 余烬徽 / 含仓库 | Hub now says 「团本三张 R01–R03」. Single-dismantle confirm now says it can be undone within 10 minutes. |

## Simulation (#3)

`docs/tests/TEST-gacha-sim-gq26-spark-2026-10-04.md`, 1M pulls, all checks PASS. Chance of 锦鲤灵 during the event:
- 21 pulls (free tickets only): 4.4%
- 30 pulls: 100%
- 41 pulls: 100%

Mean pulls to the first koi without spark is **132**, not the 176 in the earlier report (corrected in `TEST-gacha-sim-2026-10-04.md`).

## Smoke

- `tools/p1map/review2-smoke.sh FreshQ50` → **PASS=14 FAIL=0** (10:07 CST)
  - **A1:** snapshot, then stash 30 余烬碎片 and redeem 2 tickets. Preview shows 「30 → 0（一键存入仓库 30）」 and 「2 → 0（gacha_ledger redeem）」. After the restore the backpack has 0 shards and 0 tickets (no dupe). Wallet still has 2.
  - **A2:** clear 12 核心碎片 without depositing them. Restore gives back 12.
  - **A3:** 2 T1 blades go into the gear library after the snapshot. Restore lists 「跳过 … 已在装备库」 and the backpack blade count stays the same.
  - **B:** 5 duplicates (3 标准, 1 精良, 1 极品). Confirm shows 「4 件 · 按成色：精良 1 · 标准 3」 and 「✖ … 成色极品（有投入：成色极品）」. Dismantle 4, then 「批量分解的一批 4 件 [撤销这一批]」, then 「已整批撤销 4 件」.
- **Gacha, FreshG05:** 30 gq26 pulls. `/gacha spark gq26` shows 30/30 with only 锦鲤灵 and 繁花烟火 offered. Redeemed 锦鲤灵, spark back to 0/30. A non-limited id was refused.
- **Gacha, FreshG06 (after the 10:09 restart):** the pull summary now shows 「火花 10/30」. It showed /200 before the fix.

## Not tested live / left open

- Spark carry at the real banner end (10-08). Covered by config and unit tests only.
- D158 twists in a real challenge run (unit tests only).
- Net-out on an offline-queued restore.
- Items given away / dropped to other players are not tracked by invsnap. The help text says so.
- #9 (gacha items that duplicate shop items): deferred.
- #10 still open: the 洗练 gear-library hint, and the same nether-star icon used for gacha and season.

**Next bots:** FreshQ51 / FreshG07.
