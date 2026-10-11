# STATUS · Boss Moves Pack 6 mid-game 破招 D12209 · 2026-10-11

| 项 | 值 |
|----|----|
| 决策 | D12209 |
| jar | CoreRpg **1.65.727-d12209.local** |
| balance_version | 91 → **92** |
| 招 | Q03 誓魂汲取 / Q05 塔心聚裂（half-HP break_hp 0.065，stun 0.3/0.3；reuse D193） |
| 不动 | rush / charge / share / rewards / event_core / affix_shard |
| 设计 | `docs/design/DESIGN-ember-boss-moves-pack6-2026-10-11.md` |
| 门禁 | `tools/p1sim/breakmove.py` MAX_ABS_DPP **2.2**（cap 3.0）· `out-breakmove-d12209.md` |
| 单测 | EmberBossMoveTest + RulesTest break channel / D173 sizes / bv pins + cue |
| 冒烟 | DEFER（mineflayer 不可用；POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-1917 |
| backup | `/workspace/backup/CoreRpg-1.65.726-pre-1.65.727-d12209.jar` |
| commit | `a8ee5238` |
| live | Enabling CoreRpg v1.65.727-d12209.local @19:37:30 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
