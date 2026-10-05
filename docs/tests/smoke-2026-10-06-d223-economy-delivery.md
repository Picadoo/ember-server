# Smoke · D223 / CoreRpg 1.65.51 — Delivery debit tags + E1 economy yml

Bot: **FreshQ784**. Script: `tools/p1map/d223-economy-delivery-smoke.sh`.

| Check | Result |
|---|---|
| Enabling CoreRpg v1.65.51 | PASS |
| MySQL ×2 (CoreRpg + CoreGacha) | PASS |
| `ember-v1-economy.yml mirrors registry golden (bv57)` | PASS |
| Q01 first-clear settle | PASS |
| C18 fest trail buy (−30 国庆币 → 10 left) | PASS (buy logged; 2nd-session papi 10) |
| registry / grantMat refusal | none |
| SEVERE | 0 |
| 2nd-session papi | q01 已首通 / fc 已领取 / q02 yes / fest_coins 10 / trail 已拥有 (4/0) |

Note: first-session `papi parse` hit "Failed to find player" mid-run (same quirk as D221); gameplay + fest buy succeeded; 2nd session confirmed.

Play PID 2117076; jar sha 8c408160db8f017a…; Asia/Shanghai 2026-10-06 ~03:45.
