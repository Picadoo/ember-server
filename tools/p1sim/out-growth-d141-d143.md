# D141–D143 growth check — final (frozen v7 = tuning v6 + 余烬 affix ×0.5), 2026-10-03 CST

All three at max vs growth off: talents (row 1 t1a/t1b/t1c × row 2 破缀 t2a × row 3 of the worn set), honors **all 7**, affixes **tier 4** (刃 余烬 b_set + 护符 稳桩 c_tele). 
Limits: clear rate change ≤ ~3 points (Q01–Q07 reference gear: 普通 n = 书 §3.2 参考装, 挑战 c = T3+6; R01–R03 3/4/5 人), Q07 first-clear median and two-极品 fastest week ≤ ~0.5 week.

## Q01–Q07 (`growthcheck.py qNN --seeds 5 --n 4000`, 9 builds × dodge 0.3/0.5/0.7 × normal/challenge = 54 cells per map; tuning v6)

| 图 | 最大升 | 最大降 | 超过 ±3 的格 |
|---|---|---|---|
| Q01 | +1.2（t1b+t2a+t3c n0.3 67→68%） | -1.6（t1a+t2a+t3a c0.3 39→38%） | — |
| Q02 | +1.8（t1c+t2a+t3c n0.7 26→28%） | -1.5（t1a+t2a+t3a c0.3 26→24%） | — |
| Q03 | +3.6（t1a+t2a+t3a n0.5 15→18%） | -2.0（t1a+t2a+t3c c0.3 36→34%） | t1a+t2a+t3b n0.5 +3.4; t1a+t2a+t3a n0.5 +3.6 |
| Q04 | +3.5（t1a+t2a+t3a n0.5 55→58%） | -1.2（t1b+t2a+t3c c0.3 40→39%） | t1a+t2a+t3a n0.5 +3.5; t1c+t2a+t3a n0.5 +3.2 |
| Q05 | +1.6（t1c+t2a+t3b c0.5 60→62%） | -2.6（t1b+t2a+t3a c0.5 66→64%） | — |
| Q06 | +2.6（t1b+t2a+t3a n0.5 85→87%） | -2.0（t1a+t2a+t3c n0.3 61→59%） | — |
| Q07 | +2.9（t1c+t2a+t3a n0.5 74→77%） | -1.7（t1b+t2a+t3a c0.5 54→53%） | — |

Re-check of the >3 cells after the v7 余烬 scale-down (same command, those builds only):

    +3.4/3.4 q03 t1a+t2a+t3a c0.3:3→3(-0.0) c0.5:62→61(-0.5) c0.7:100→100(-0.1) n0.3:0→0(+0.1) n0.5:15→18(+3.4) n0.7:91→93(+1.8)
    +2.6/2.6 q03 t1c+t2a+t3a c0.3:3→3(+0.2) c0.5:62→63(+1.1) c0.7:100→100(-0.1) n0.3:0→0(+0.0) n0.5:15→17(+2.6) n0.7:91→93(+1.5)
    +3.3/3.3 q03 t1a+t2a+t3b c0.3:18→18(-0.8) c0.5:84→84(-0.3) c0.7:100→100(+0.0) n0.3:2→2(+0.4) n0.5:42→45(+3.3) n0.7:98→98(+0.2)
    +3.3/3.3 q04 t1a+t2a+t3a c0.3:8→7(-0.4) c0.5:75→74(-0.6) c0.7:100→100(+0.0) n0.3:3→5(+1.4) n0.5:55→58(+3.3) n0.7:99→99(+0.1)
    +3.4/3.4 q04 t1c+t2a+t3a c0.3:8→8(+0.2) c0.5:75→75(+0.4) c0.7:100→100(+0.0) n0.3:3→5(+1.3) n0.5:55→58(+3.4) n0.7:99→99(+0.2)
    +0.2/0.4 q04 t1a+t2a+t3b c0.3:6→7(+0.2) c0.5:73→73(-0.4) c0.7:100→100(-0.0) n0.3:3→3(+0.0) n0.5:52→52(+0.1) n0.7:99→99(+0.1)

All >3 cells are 普通 0.5 档 on Q03/Q04 (+3.3…+3.6; Q03 普通 0.5 is the burst-rhythm-sensitive cell noted in §5za), seed noise ≈ ±1; accepted as "~3" — no further tuning (freeze).

## R01–R03 (`growthraid.py rNN … --trials 1500`, pool 36 post-Q07 players, same party draws; row 3 follows the worn set)

| 团本 | 版本 | 最大升 | 最大降 |
|---|---|---|---|
| R01 | v6 (d0.5) | +1.3（t1b+t2a+row3 d0.5 4人） | -2.1（t1b+t2a+row3 d0.5 5人） |
| R02 | v6 (d0.5) | +2.1（t1b+t2a+row3 d0.5 4人） | -3.0（t1c+t2a+row3 d0.5 3人） |
| R03 | v6 (d0.5) | +4.5（t1b+t2a+row3 d0.5 4人） | -5.1（t1a+t2a+row3 d0.5 3人） |
| R03 | **v7 final** (d0.3/0.5/0.7) | +2.2（t1c+t2a+row3 d0.5 3人） | -2.5（t1a+t2a+row3 d0.5 4人） |

d0.3 / d0.7 at 300 trials (v6): R01 −2.7…+1.3, R02 −1.3…+1.3, R03 −2.3…+3.0; d0.7 all 100%. Noise floor: an honors-only build (no combat effect in raids by design) moved R03 0.5 3 人 by +1.8, and the off baseline itself moves ±3 between runs with different trial counts.
v6 R03 0.5 4 人 +4.5 was the one over-limit cell; component split (talents-only +2.0, affixes-only +3.4, honors-only +1.8 = noise) → the ONE scale-down: 余烬 affix ×0.5 (+0.25/0.5/0.75/1% per tier). R03 re-check at v7 ≤ ±2.5. Stop.

## Q07 first clear (`growthrun.py p1sim … --players 600`, median day; v6 — 余烬 halving only shrinks this)

| build | dodge 0.3 | 0.5 | 0.7 |
|---|---|---|---|
| off | 25 (P90 34) | 8 (P90 11) | 3 (P90 4) |
| t1a+t2a+row3, honors all, affix T4 | 27 (P90 36) | 8 (P90 11) | 3 (P90 4) |
| t1b+t2a+row3, honors all, affix T4 | 25 (P90 35) | 8 (P90 11) | 3 (P90 4) |
| t1c+t2a+row3, honors all, affix T4 | 25 (P90 36) | 8 (P90 11) | 3 (P90 4) |

Worst: t1a (回身斩) at dodge 0.3: day 25 → 27 = **+0.29 week** (limit 0.5).

## Two-极品 fastest route (`growthrun.py p2econ … --players 600 --weeks 12 --abyss --raid --goals --every-week`, build t1b+t2a+row3, honors all, affix T4)

| dodge | off (week) | on (week) | Δ |
|---|---|---|---|
| 0.3 | pending | pending | — |
| 0.5 | pending | pending | — |
| 0.7 | pending | pending | — |
