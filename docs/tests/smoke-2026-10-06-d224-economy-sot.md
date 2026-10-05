# Smoke · D224 / CoreRpg 1.65.52 — amount() yml SoT

Bot: **FreshQ785**. Script: `tools/p1map/d224-economy-sot-smoke.sh`.

| Check | Result |
|---|---|
| Enabling CoreRpg v1.65.52 | PASS |
| MySQL ×2 (CoreRpg + CoreGacha) | PASS |
| `ember-v1-economy.yml loaded as amount() SoT (bv57)` | PASS |
| no FAIL-CLOSED | PASS |
| Q01 first-clear settle | PASS (`settle … (first clear)`) |
| C07 marks exchange (−8 T1 → 焚烬刃) | PASS (9→1) |
| C03 forge enhance (+0→+1, cost coin 40) | PASS (`forge enhance … ok`) |
| registry / grantMat refusal | none |
| SEVERE | 0 |
| 2nd-session papi | q01 已首通 / fc 已领取 / q02 yes (3/0) |

Note: first-session `papi parse` hit "Failed to find player" mid-run (same quirk as D221/D223); gameplay + forge spend succeeded; 2nd session confirmed unlocks.

Play PID 2126169; jar sha e1c79eea7a8791d9…; Asia/Shanghai 2026-10-06 ~03:54.
