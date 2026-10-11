# STATUS · Room-event W3 kind weights D12208 · 2026-10-11

| 项 | 值 |
|----|----|
| 决策 | D12208 |
| jar | CoreRpg **1.65.726-d12208.local** |
| balance_version | 90 → **91** |
| 权重 | timed **0.65**；其它缺省 1.0；池仍 9 |
| 不动 | event_rate 0.85 / event_core 1 / affix / rewards |
| 设计 | `docs/design/DESIGN-ember-event-kind-weights-w3-2026-10-11.md` |
| 单测 | `EmberEventKindWeightTest` + RoomWeight 绿 |
| 冒烟 | DEFER（mineflayer 不可用；POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-1845 |
| commit | `08f95dfc` |
| live | Enabling CoreRpg v1.65.726-d12208.local @18:50:19 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
| backup | `/workspace/backup/CoreRpg-1.65.725-pre-1.65.726-d12208.jar` |
