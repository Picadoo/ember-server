# D12211 affixpack7 gate — rules balance_version 94, N=80 × seeds [7, 11, 13]
# pulse {'every': 6.0, 'warn': 1.2, 'radius': 3.5, 'dmg': 0.55} / mines {'every': 7.0, 'warn': 1.4, 'radius': 1.4, 'count': 2, 'behind': 2.5, 'spread': 1.8, 'dmg': 0.6}

| 图 | 装备 | 躲避 | 压力 毒十字 | 压力 脉爆 | 压力 埋雷 | 脉爆/埋雷 − 毒十字 (pp) |
|---|---|---:|---:|---:|---:|---:|
| q01 | 书参考 | 0.3 | 59.6% | 62.1% | 63.8% | +2.5 / +4.2 |
| q01 | 书参考 | 0.5 | 97.5% | 97.9% | 97.9% | +0.4 / +0.4 |
| q01 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |
| q03 | 书参考 | 0.3 | 0.8% | 0.8% | 0.8% | +0.0 / +0.0 |
| q03 | 书参考 | 0.5 | 39.2% | 39.2% | 40.4% | +0.0 / +1.2 |
| q03 | 书参考 | 0.7 | 97.5% | 97.5% | 97.5% | +0.0 / +0.0 |
| q05 | 书参考 | 0.3 | 71.7% | 71.7% | 70.4% | +0.0 / -1.3 |
| q05 | 书参考 | 0.5 | 98.3% | 98.3% | 98.3% | +0.0 / +0.0 |
| q05 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |
| q07 | 书参考 | 0.3 | 8.8% | 9.2% | 8.3% | +0.4 / -0.4 |
| q07 | 书参考 | 0.5 | 80.8% | 81.2% | 77.9% | +0.4 / -2.9 |
| q07 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |

MAX_HARDER_THAN_VENOM_PP (stress) = 2.9 (cap 3.0) → WITHIN RANGE
NOTE: run --econ twice (default 16-pool, then --old 14-pool) and --diff the two md tables for Part B.

## Part B (p2econ 120 players × 12 weeks, dodge 0.5, P1SIM_CRN=1)
```
MAX_ABS_DPP (p2econ % columns, new 16 vs old 14 pool) = 2.0 at (('1', '无轮换'), 0) (cap 3.0) → WITHIN RANGE
```

Verdict: WITHIN RANGE (stress max harder-than-venom 2.9 pp; p2econ MAX_ABS_DPP 2.0).
