# D12207 affixpack6 gate — rules balance_version 90, N=80 × seeds [7, 11, 13]
# hail {'every': 5.0, 'warn': 1.2, 'radius': 1.5, 'count': 3, 'ahead': 3.0, 'spread': 2.0, 'dmg': 0.7} / tether {'every': 7.0, 'warn': 1.2, 'radius': 1.8, 'root': 0.8, 'dmg': 0.4, 'pull': 0.8}

| 图 | 装备 | 躲避 | 压力 毒十字 | 压力 雹霰 | 压力 牵丝 | 雹霰/牵丝 − 毒十字 (pp) |
|---|---|---:|---:|---:|---:|---:|
| q01 | 书参考 | 0.3 | 59.6% | 62.1% | 63.8% | +2.5 / +4.2 |
| q01 | 书参考 | 0.5 | 97.5% | 97.5% | 97.9% | +0.0 / +0.4 |
| q01 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |
| q03 | 书参考 | 0.3 | 0.8% | 0.8% | 0.8% | +0.0 / +0.0 |
| q03 | 书参考 | 0.5 | 39.2% | 38.8% | 41.2% | -0.4 / +2.1 |
| q03 | 书参考 | 0.7 | 97.5% | 97.1% | 97.5% | -0.4 / +0.0 |
| q05 | 书参考 | 0.3 | 70.8% | 71.2% | 69.6% | +0.4 / -1.3 |
| q05 | 书参考 | 0.5 | 98.3% | 98.3% | 98.3% | +0.0 / +0.0 |
| q05 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |
| q07 | 书参考 | 0.3 | 8.8% | 8.3% | 8.3% | -0.4 / -0.4 |
| q07 | 书参考 | 0.5 | 80.8% | 80.8% | 78.3% | +0.0 / -2.5 |
| q07 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |

MAX_HARDER_THAN_VENOM_PP (stress) = 2.5 (cap 3.0) → WITHIN RANGE
NOTE: run --econ twice (default 14-pool, then --old 12-pool) and --diff the two md tables for Part B.
MAX_ABS_DPP (p2econ % columns, new 14 vs old 12 pool) = 4.0 at (('6', '无轮换'), 4) (cap 3.0) → OUT OF RANGE
MAX_ABS_DPP (p2econ % columns, new 14 vs old 12 pool) = 3.0 at (('1', '无轮换'), 0) (cap 3.0) → WITHIN RANGE

## Part B (p2econ 120 players × 12 weeks, dodge 0.5)
```
MAX_ABS_DPP (p2econ % columns, new 14 vs old 12 pool) = 3.0 at (('1', '无轮换'), 0) (cap 3.0) → WITHIN RANGE
```

Verdict: WITHIN RANGE (stress max harder-than-venom 2.5 pp; p2econ MAX_ABS_DPP 3.0).
