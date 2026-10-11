# D12209 breakmove gate · 2026-10-11

Ship values: Q03 誓魂汲取 dmg22 break_hp0.065 break_stun0.3; Q05 塔心聚裂 dmg28 break_hp0.065 break_stun0.3 (proposed 0.4→0.3; MAX unchanged).

```
- 0.2 q03 c 0.3:18→18(-0.2) c 0.5:86→86(-0.0) c 0.7:100→100(+0.0) n 0.3:2→2(+0.0) n 0.5:40→40(+0.0) n 0.7:98→98(+0.0) · broken 97% of 55681 channels (all six cells)
- 2.2 q05 c 0.3:3→3(+0.0) c 0.5:60→60(+0.2) c 0.7:100→100(+0.0) n 0.3:73→75(+2.2) n 0.5:99→99(+0.1) n 0.7:100→100(+0.0) · broken 100% of 84805 channels (all six cells)
- 0.5 q06 c 0.3:1→1(+0.0) c 0.5:49→49(+0.5) c 0.7:100→100(+0.0) n 0.3:20→20(+0.1) n 0.5:88→89(+0.2) n 0.7:100→100(+0.0) · broken 100% of 91763 channels (all six cells)
- 1.6 q07 c 0.3:1→1(+0.1) c 0.5:50→50(+0.2) c 0.7:100→100(+0.0) n 0.3:9→9(+0.0) n 0.5:78→80(+1.6) n 0.7:100→100(+0.0) · broken 100% of 83786 channels (all six cells)

MAX_ABS_DPP = 2.2 (cap 3.0, prefer ≤2.0; BREAK_UPTIME 0.85; overrides none)
```

WITHIN RANGE (≤3.0). Prefer ≤2.0 not met on Q05 n0.3 (+2.2 from break success uptime); harder hp / lower dmg did not shrink that cell. Accept ship.
