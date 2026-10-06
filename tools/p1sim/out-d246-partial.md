# D246 Part B — PARTIAL offline results (stopped 10:44 per routine; not a gate verdict)

Saved 2026-10-06 10:44 Asia/Shanghai. Raw files in /workspace/d245/. Sim code edits (opt-in, uncommitted): tools/p1sim/p1sim.py (roll_base / base_armor / base_mapslot / chest_armor / arm_repeat / arm_front), tools/p1sim/gear6.py (docstring), tools/p1sim/gear6diag.py, tools/p1sim/gear6w30.py (new). Byte-identical check 2026-10-06 10:52 vs a 22624a7 worktree: `p1sim.py` default, `p1sim.py --ref`, `p2econ.py --players 40 --weeks 4 --abyss --raid --goals`, and `gear6.py prog` base + Stage 0 rec (200 players) are all IDENTICAL; selfcheck shows the same 2 known FAILs (the worktree only lacked the JDK path for M03).

## 1. Farm-only W30 (p2econ, 300 players, dodge 0.5, no abyss/raid; weeks after own Q07 FC)

```
== farm-2slot 
farm-2slot.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 6.86
  无轮换: W30 6.86 · P50 10.67 · P90 未达 · 第 12 周仍未拥有双极品 46%
  P2-1 轮换: W30 7.14 · P50 11.00 · P90 未达 · 第 12 周仍未拥有双极品 48%
== farm-rec 
farm-rec.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 5.50
  P2-1 轮换: W30 5.50 · P50 9.17 · P90 未达 · 第 12 周仍未拥有双极品 37%
  无轮换: W30 5.57 · P50 9.25 · P90 未达 · 第 12 周仍未拥有双极品 39%
== farm-d05 {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":0.5}
farm-d05.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 6.80
  无轮换: W30 6.80 · P50 10.00 · P90 未达 · 第 12 周仍未拥有双极品 40%
  P2-1 轮换: W30 6.80 · P50 10.50 · P90 未达 · 第 12 周仍未拥有双极品 42%
== farm-w100 {"w":[1.0,0,0,0,0],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":1}
farm-w100.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 6.80
  无轮换: W30 6.80 · P50 10.00 · P90 未达 · 第 12 周仍未拥有双极品 42%
  P2-1 轮换: W30 7.00 · P50 10.33 · P90 未达 · 第 12 周仍未拥有双极品 43%
== farm-capall {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":1,"arm_cap":"all"}
farm-capall.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 6.80
  无轮换: W30 6.80 · P50 10.14 · P90 未达 · 第 12 周仍未拥有双极品 38%
  P2-1 轮换: W30 7.33 · P50 10.33 · P90 未达 · 第 12 周仍未拥有双极品 41%
== farm-rep {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":1,"arm_repeat":true}
farm-rep.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 6.43
  无轮换: W30 6.43 · P50 9.57 · P90 未达 · 第 12 周仍未拥有双极品 40%
  P2-1 轮换: W30 6.43 · P50 10.25 · P90 未达 · 第 12 周仍未拥有双极品 43%
== farm-front {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":1,"arm_front":true}
farm-front.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 6.14
  无轮换: W30 6.14 · P50 9.67 · P90 未达 · 第 12 周仍未拥有双极品 37%
  P2-1 轮换: W30 6.67 · P50 9.86 · P90 未达 · 第 12 周仍未拥有双极品 38%
== farm-b25 {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":0,"base_armor":0.25}
farm-b25.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 7.83
  无轮换: W30 7.83 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 52%
  P2-1 轮换: W30 8.29 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 52%
== farm-b33 {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":0,"base_armor":0.333}
farm-b33.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 8.20
  P2-1 轮换: W30 8.20 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 52%
  无轮换: W30 9.25 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 57%
== farm-b33c {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":0,"base_armor":0.333,"chest_armor":true}
farm-b33c.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 9.00
  无轮换: W30 9.00 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 58%
  P2-1 轮换: W30 9.33 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 61%
== farm-b50 {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":0,"base_armor":0.5}
farm-b50.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 11.25
  无轮换: W30 11.25 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 67%
  P2-1 轮换: W30 11.33 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 68%
== farm-b50m {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":0,"base_armor":0.5,"base_mapslot":true}
farm-b50m.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 6.71
  无轮换: W30 6.71 · P50 10.00 · P90 未达 · 第 12 周仍未拥有双极品 37%
  P2-1 轮换: W30 6.89 · P50 10.80 · P90 未达 · 第 12 周仍未拥有双极品 44%
== farm-b67 {"w":[0.6,0.1,0.1,0.1,0.1],"start":"q01","up_all":true,"cost":{"charm":0.7,"armor":0.1},"drop":0,"base_armor":0.667}
farm-b67.md  W30（候选策略中达到 30% 双极品拥有率的最早插值周）= 未达
  无轮换: W30 未达 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 82%
  P2-1 轮换: W30 未达 · P50 未达 · P90 未达 · 第 12 周仍未拥有双极品 86%
```

## 2. Pooled farm W30 (gear6w30.py, 1200 players, 无轮换 only)

```
base       base n=1200 W30 6.99 (halves [7.03, 6.93]) w12 0.562 off@Q07 0.251 off@w12 0.157 T3tgt2@w12 0.843 rej_curoff 0.43
rec        base n=1200 W30 6.12 (halves [6.03, 6.18]) w12 0.598 off@Q07 0.101 off@w12 0.085 T3tgt2@w12 0.915 rej_curoff 0.27
w100       base n=1200 W30 6.42 (halves [6.54, 6.3]) w12 0.590 off@Q07 0.232 off@w12 0.138 T3tgt2@w12 0.862 rej_curoff 0.40
d05        base n=1200 W30 6.22 (halves [6.3, 6.14]) w12 0.605 off@Q07 0.096 off@w12 0.082 T3tgt2@w12 0.918 rej_curoff 0.23
capall     base n=1200 W30 6.55 (halves [6.81, 6.23]) w12 0.602 off@Q07 0.107 off@w12 0.102 T3tgt2@w12 0.898 rej_curoff 0.32
rep        base n=1200 W30 6.16 (halves [6.09, 6.21]) w12 0.597 off@Q07 0.104 off@w12 0.092 T3tgt2@w12 0.907 rej_curoff 0.26
front      base n=1200 W30 6.38 (halves [6.63, 6.07]) w12 0.608 off@Q07 0.116 off@w12 0.094 T3tgt2@w12 0.906 rej_curoff 0.28
```

## 3. Cause diagnosis (gear6diag.py, 300 players, 无轮换)

```
== diag6-2slot
players 300 reached Q07 300
median runs to Q07 FC 23.0  H0 164.1  B0 60.1
at Q07 FC: blade q3 0.000 charm q3 0.017  blade T3 0.357 charm T3 0.457
can farm challenge by week: w1 0.02 w2 1.00 w3 1.00 w4 1.00 w5 1.00 w6 1.00 w7 1.00 w8 1.00 w9 1.00 w10 1.00 w11 1.00 w12 1.00
two q3 by week: w1 0.000 w2 0.033 w3 0.067 w4 0.117 w5 0.180 w6 0.243 w7 0.310 w8 0.367 w9 0.420 w10 0.477 w11 0.510 w12 0.537
q3 piece origins at the two-q3 week: T3/p2/ch 298, T3/p2/n 23, T3/p1/- 1
median H by week: 302 322 332 347 352 361 364 364 364 364 364 364
off-family worn at Q07 FC: blade 0.147 charm 0.193
off-family worn by week (blade/charm): w1 0.19/0.19 w2 0.19/0.19 w3 0.19/0.19 w4 0.19/0.19 w5 0.19/0.19 w6 0.19/0.19 w7 0.19/0.19 w8 0.19/0.19 w9 0.19/0.19 w10 0.19/0.19 w11 0.19/0.19 w12 0.19/0.19
phase-2 totals per player: ch_clears 231.11, q3_blade_off 0.87, q3_blade_tgt 1.72, q3_charm_off 0.93, q3_charm_tgt 1.51, q3_lost_charm_off 0.01, q3_lost_charm_tgt 0.01, q3_taken 1.41, rej_curoff 0.53, rej_curq3 1.34
== diag6-rec
players 300 reached Q07 300
median runs to Q07 FC 22.0  H0 198.4  B0 60.1
at Q07 FC: blade q3 0.023 charm q3 0.017  blade T3 0.380 charm T3 0.463
can farm challenge by week: w1 0.06 w2 1.00 w3 1.00 w4 1.00 w5 1.00 w6 1.00 w7 1.00 w8 1.00 w9 1.00 w10 1.00 w11 1.00 w12 1.00
two q3 by week: w1 0.007 w2 0.037 w3 0.080 w4 0.167 w5 0.257 w6 0.330 w7 0.367 w8 0.427 w9 0.493 w10 0.533 w11 0.567 w12 0.610
q3 piece origins at the two-q3 week: T3/p2/ch 340, T3/p2/n 24, T2/p1/- 2
median H by week: 293 313 323 338 344 349 352 352 352 352 352 352
off-family worn at Q07 FC: blade 0.097 charm 0.070
off-family worn by week (blade/charm): w1 0.10/0.10 w2 0.10/0.10 w3 0.10/0.10 w4 0.10/0.10 w5 0.10/0.10 w6 0.10/0.10 w7 0.10/0.10 w8 0.10/0.10 w9 0.10/0.10 w10 0.10/0.10 w11 0.10/0.10 w12 0.10/0.10
phase-2 totals per player: ch_clears 231.63, q3_blade_off 0.90, q3_blade_tgt 1.83, q3_charm_off 0.85, q3_charm_tgt 1.49, q3_lost_blade_off 0.01, q3_lost_blade_tgt 0.02, q3_lost_charm_tgt 0.02, q3_taken 1.54, rej_curoff 0.34, rej_curq3 1.46
```

## 4. Dynamic 21 cells under today's rules (gear6 prog, 800 players)


### 躲避 0.3：首通前在前线的平均进入次数；括号内 = 首通前通关率（首通数 / 前线进入数）相对 2 槽变化 pp

| 方案 | Q01 | Q02 | Q03 | Q04 | Q05 | Q06 | Q07 | ±2 内 |
|---|---|---|---|---|---|---|---|---|
| base | 1.54 (—) | 3.01 (—) | 4.17 (—) | 7.64 (—) | 4.28 (—) | 5.24 (—) | 10.91 (—) | — |
| b50m | 1.54 (+0.0) | 2.82 (+2.2) | 4.41 (-1.3) | 7.93 (-0.5) | 5.37 (-4.7) | 4.51 (+3.1) | 10.94 (-0.0) | 4/7 |
| cap | 1.54 (+0.0) | 3.04 (-0.3) | 4.28 (-0.6) | 7.69 (-0.1) | 3.98 (+1.8) | 4.87 (+1.4) | 10.96 (-0.0) | 7/7 |
| capall | 1.54 (+0.0) | 3.04 (-0.3) | 4.28 (-0.6) | 7.69 (-0.1) | 4.07 (+1.2) | 4.73 (+2.1) | 10.66 (+0.2) | 6/7 |
| d05 | 1.54 (+0.0) | 3.08 (-0.8) | 4.23 (-0.4) | 7.74 (-0.2) | 3.89 (+2.4) | 4.78 (+1.8) | 11.10 (-0.2) | 6/7 |
| front | 1.54 (+0.0) | 3.04 (-0.3) | 4.28 (-0.6) | 7.69 (-0.1) | 3.93 (+2.1) | 5.00 (+0.9) | 10.86 (+0.0) | 6/7 |
| noupall | 1.54 (+0.0) | 3.04 (-0.3) | 4.28 (-0.6) | 7.69 (-0.1) | 4.07 (+1.2) | 4.82 (+1.6) | 10.50 (+0.4) | 7/7 |
| rec | 1.54 (+0.0) | 3.04 (-0.3) | 4.28 (-0.6) | 7.69 (-0.1) | 3.91 (+2.2) | 4.97 (+1.0) | 10.85 (+0.1) | 6/7 |
| rep | 1.54 (+0.0) | 3.03 (-0.2) | 4.26 (-0.5) | 7.61 (+0.1) | 4.03 (+1.5) | 4.75 (+1.9) | 10.93 (-0.0) | 7/7 |
| rep_front | 1.54 (+0.0) | 3.03 (-0.2) | 4.26 (-0.5) | 7.61 (+0.1) | 4.03 (+1.5) | 4.75 (+1.9) | 10.93 (-0.0) | 7/7 |

### 躲避 0.5：首通前在前线的平均进入次数；括号内 = 首通前通关率（首通数 / 前线进入数）相对 2 槽变化 pp

| 方案 | Q01 | Q02 | Q03 | Q04 | Q05 | Q06 | Q07 | ±2 内 |
|---|---|---|---|---|---|---|---|---|
| base | 1.01 (—) | 1.97 (—) | 1.80 (—) | 2.43 (—) | 2.52 (—) | 1.99 (—) | 2.85 (—) | — |
| b50m | 1.01 (+0.0) | 1.72 (+7.4) | 1.84 (-1.4) | 2.48 (-0.8) | 3.02 (-6.6) | 2.44 (-9.3) | 2.64 (+2.8) | 3/7 |
| cap | 1.01 (+0.0) | 1.93 (+1.1) | 1.79 (+0.2) | 2.46 (-0.6) | 2.58 (-0.8) | 2.13 (-3.4) | 2.69 (+2.1) | 5/7 |
| capall | 1.01 (+0.0) | 1.93 (+1.1) | 1.79 (+0.2) | 2.46 (-0.6) | 2.75 (-3.3) | 2.05 (-1.6) | 2.76 (+1.0) | 6/7 |
| d05 | 1.01 (+0.0) | 2.03 (-1.4) | 1.83 (-1.2) | 2.34 (+1.6) | 2.34 (+3.1) | 2.19 (-4.7) | 2.73 (+1.5) | 5/7 |
| front | 1.01 (+0.0) | 1.93 (+1.1) | 1.79 (+0.2) | 2.46 (-0.6) | 2.34 (+3.0) | 2.17 (-4.2) | 2.82 (+0.3) | 5/7 |
| noupall | 1.01 (+0.0) | 1.93 (+1.1) | 1.79 (+0.2) | 2.46 (-0.6) | 2.50 (+0.3) | 2.09 (-2.5) | 2.71 (+1.7) | 6/7 |
| rec | 1.01 (+0.0) | 1.93 (+1.1) | 1.79 (+0.2) | 2.46 (-0.6) | 2.30 (+3.7) | 2.22 (-5.2) | 2.70 (+1.9) | 5/7 |
| rep | 1.01 (+0.0) | 1.97 (+0.0) | 1.84 (-1.2) | 2.43 (-0.1) | 2.38 (+2.3) | 2.17 (-4.2) | 2.79 (+0.7) | 5/7 |
| rep_front | 1.01 (+0.0) | 1.97 (+0.0) | 1.84 (-1.2) | 2.43 (-0.1) | 2.38 (+2.3) | 2.17 (-4.2) | 2.79 (+0.7) | 5/7 |

### 躲避 0.7：首通前在前线的平均进入次数；括号内 = 首通前通关率（首通数 / 前线进入数）相对 2 槽变化 pp

| 方案 | Q01 | Q02 | Q03 | Q04 | Q05 | Q06 | Q07 | ±2 内 |
|---|---|---|---|---|---|---|---|---|
| base | 1.00 (—) | 1.12 (—) | 1.04 (—) | 1.04 (—) | 1.14 (—) | 1.20 (—) | 1.20 (—) | — |
| b50m | 1.00 (+0.0) | 1.09 (+2.0) | 1.03 (+1.2) | 1.05 (-0.8) | 1.18 (-3.6) | 1.20 (-0.3) | 1.27 (-4.7) | 4/7 |
| cap | 1.00 (+0.0) | 1.12 (+0.1) | 1.04 (+0.0) | 1.04 (+0.2) | 1.18 (-3.3) | 1.17 (+2.3) | 1.21 (-0.5) | 5/7 |
| capall | 1.00 (+0.0) | 1.12 (+0.1) | 1.04 (+0.0) | 1.04 (+0.2) | 1.25 (-7.9) | 1.24 (-2.4) | 1.21 (-0.8) | 5/7 |
| d05 | 1.00 (+0.0) | 1.12 (-0.4) | 1.05 (-0.3) | 1.05 (-0.3) | 1.12 (+1.2) | 1.13 (+4.9) | 1.20 (+0.3) | 6/7 |
| front | 1.00 (+0.0) | 1.12 (+0.1) | 1.04 (+0.0) | 1.04 (+0.2) | 1.12 (+1.2) | 1.14 (+4.5) | 1.19 (+0.7) | 6/7 |
| noupall | 1.00 (+0.0) | 1.12 (+0.1) | 1.04 (+0.0) | 1.04 (+0.2) | 1.16 (-1.8) | 1.14 (+4.2) | 1.21 (-0.3) | 6/7 |
| rec | 1.00 (+0.0) | 1.12 (+0.1) | 1.04 (+0.0) | 1.04 (+0.2) | 1.09 (+3.2) | 1.14 (+4.5) | 1.21 (-0.3) | 5/7 |
| rep | 1.00 (+0.0) | 1.12 (-0.4) | 1.05 (-0.3) | 1.04 (+0.7) | 1.12 (+1.0) | 1.13 (+5.3) | 1.21 (-0.3) | 6/7 |
| rep_front | 1.00 (+0.0) | 1.12 (-0.4) | 1.05 (-0.3) | 1.04 (+0.7) | 1.12 (+1.0) | 1.13 (+5.3) | 1.21 (-0.3) | 6/7 |

## 5. Interim reading (NOT final; to be confirmed in the D246 run)

- **Most of the farm-route lead is not about how many drops there are.** Both models give the same number of challenge clears and target-family q3 blade/charm drops (≈231 clears; 3.2 vs 3.3 target q3 drops per player in 12 weeks). The lead comes from **equip decisions**. With the charm at 60 % of the HP side, a charm-for-charm swap moves EHP only about 0.6× as much, while the dps factor from awakening stays the same. So fewer players trade their target-family set for an off-family upgrade. Off-family "locked" players at the Q07 first clear: 2-slot 25 % vs 6-slot 10 % (pooled 1200). Those players reject target q3 drops (`rej_curoff` 0.43 vs 0.27), and most never reach two 极品.
- **Control `w100`** (armor drops present but zero stat weight): lock-in comes back to the 2-slot level (23 %). W30 is 6.42 vs base 6.99, so about −0.5 wk is still unexplained. Candidates: charm cost 0.7 / starter armor / RNG. Unresolved.
- **The 300-player W30 per route is noisy (about ±0.4 wk per config).** Pooled 1200-player numbers: base 6.99 · rec 6.12 (−0.87) · d05 6.22 · rep 6.16 · front 6.38 · capall 6.55 (−0.44) · w100 6.42.
- **Turning part of the base roll into armor (`base_armor`, i.e. "base covers all slots")** brings farm W30 to parity: b50m −0.15 / −0.25 at 300 players. But it cuts blade/charm drop rates, which violates staged-plan §4.4 ("不稀释刃 / 护符掉率来塞护甲"). It also fails dynamic cells (b50m 11/21). Rejected unless the owner lifts §4.4.
- **The dynamic 21 cells under today's rules at 800 players look noise-limited.** Cell swings are ±3–5 pp between near-identical variants. rec = 16/21 today, vs 19/21 on 10-04. A 4000-player run with a noise control (`ctl`) was started and then killed at the routine stop (no output). Next run: 4000+ players with the control before choosing a variant.
- **No variant passes every gate yet → Stage 1 is NOT clear to open.**
