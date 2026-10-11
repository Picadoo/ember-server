# D12212 affixpack8 gate — rules balance_version 95, N=80 × seeds [7, 11, 13]
# spore {'every': 7.0, 'warn': 1.5, 'radius': 2.2, 'dmg': 0.5} / flank {'every': 6.5, 'warn': 1.3, 'radius': 1.4, 'side': 2.8, 'dmg': 0.55}

| 图 | 装备 | 躲避 | 压力 毒十字 | 压力 孢雾 | 压力 侧袭 | 孢雾/侧袭 − 毒十字 (pp) |
|---|---|---:|---:|---:|---:|---:|
| q01 | 书参考 | 0.3 | 59.6% | 63.8% | 63.8% | +4.2 / +4.2 |
| q01 | 书参考 | 0.5 | 97.5% | 97.9% | 97.9% | +0.4 / +0.4 |
| q01 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |
| q03 | 书参考 | 0.3 | 0.8% | 0.8% | 0.8% | +0.0 / +0.0 |
| q03 | 书参考 | 0.5 | 39.2% | 41.2% | 40.4% | +2.1 / +1.2 |
| q03 | 书参考 | 0.7 | 97.5% | 97.5% | 97.5% | +0.0 / +0.0 |
| q05 | 书参考 | 0.3 | 71.7% | 70.4% | 70.4% | -1.3 / -1.3 |
| q05 | 书参考 | 0.5 | 98.3% | 98.3% | 98.3% | +0.0 / +0.0 |
| q05 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |
| q07 | 书参考 | 0.3 | 8.8% | 8.3% | 8.3% | -0.4 / -0.4 |
| q07 | 书参考 | 0.5 | 80.8% | 77.9% | 77.9% | -2.9 / -2.9 |
| q07 | 书参考 | 0.7 | 100.0% | 100.0% | 100.0% | +0.0 / +0.0 |

MAX_HARDER_THAN_VENOM_PP (stress) = 2.9 (cap 3.0) → WITHIN RANGE
NOTE: run --econ twice (default 18-pool, then --old 16-pool) and --diff the two md tables for Part B.

## Part B (p2econ 120 players × 12 weeks, dodge 0.5, P1SIM_CRN=1)
```
MAX_ABS_DPP (p2econ % columns, new 18 vs old 16 pool) = 1.0 at (('6', '无轮换'), 4) (cap 3.0) → WITHIN RANGE
```

Verdict: WITHIN RANGE (stress max harder-than-venom 2.9 pp; p2econ MAX_ABS_DPP (p2econ % columns, new 18 vs old 16 pool) = 1.0 at (('6', '无轮换'), 4) (cap 3.0) → WITHIN RANGE).
