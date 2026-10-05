# D193 Boss Moves Pack 5 破招 — clear rate base → new (5 seeds × 4000; normal = book ref, challenge = T3+6)

Base = live 1.65.30 (p1sim.BREAK False: the half-HP channel does not exist). New = Q06 霜潮汲取 / Q07 炉心聚爆 (circle round the
boss, every 25, below 0.5, warn 3.0, break_hp 0.065, break_stun 0.3 / 0.5; Q06 dmg 30 r5, Q07 dmg 36 r5.5; challenge = heavy 61).
Solo model: swings in the 3 s window = 3 × BREAK_UPTIME 0.85 / 0.64 s ≈ 4, each B × crit; broken → no hit + stagger; else it lands
like any telegraph (dodge + tele_bonus). A gated break skill waits for its HP gate (Java dueSkill).

## Gate (shipped values)

- 0.5 q06 c 0.3:1→1(+0.0) c 0.5:49→49(+0.5) c 0.7:100→100(+0.0) n 0.3:20→20(+0.2) n 0.5:88→88(+0.2) n 0.7:100→100(+0.0) · broken 100% of 91945 channels (all six cells)
- 1.6 q07 c 0.3:1→1(+0.1) c 0.5:50→50(+0.2) c 0.7:100→100(+0.0) n 0.3:9→9(+0.0) n 0.5:78→80(+1.6) n 0.7:100→100(+0.0) · broken 100% of 83786 channels (all six cells)

MAX_ABS_DPP = 1.6 (cap 3.0, prefer ≤2.0; BREAK_UPTIME 0.85; overrides none)

→ within range. At the map's reference loadout a committed solo player breaks every channel (4 swings ≥ 6.5 %); below the
reference (or not committing) it fails and must be dodged — the check is a gear / commitment check, not a free stagger.

## Sweep that set the numbers (same tool, N 500–4000; informational)

| break_hp | stun | Q06 max dpp | Q07 max dpp | broken (all cells) | verdict |
|---|---|---:|---:|---:|---|
| 0.06 | 1.0 | 4.9 | 2.8 | 100 % | too easy, stun too big |
| 0.075 | 1.0 / 0.5 / 0 | 3.7–4.0 | 5.5–5.9 | 58 % (normal yes, challenge never) | challenge eats heavy 61 every 25 s → −4…−6 |
| 0.09–0.11 | 1.0 | 5.3–5.9 | 7.1 | 0–8 % | never breaks: pure extra damage |
| 0.065 | 0.5 | 4.4 | 1.6 | 100 % | Q06 challenge 0.5 +4.4 |
| 0.065 | 0.4 | 3.5 | 1.6 | 100 % | Q06 over cap |
| **0.065** | **0.3 (Q06) / 0.5 (Q07)** | **0.5** | **1.6** | 100 % | **shipped** |

## 余烬连战 (rushsim, 2000 per cell)

With the channel in the rush chain: Q07 ref T2+8 dodge 0.7 83→81, T3+6 dodge 0.3 24→31 (any stun 0.3–0.5 gives the same step;
stun 0 gives 24 — schedule-phase effect on a knife-edge cell). Decision: the rush keeps its own tuned move table — the break
channel never comes due when def.rush (EmberRunDirector) and rushsim strips it; rushsim output identical to the live baseline.
