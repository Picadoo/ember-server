# STATUS · Affix Pack 10 D12214（裂隙 / 连落）· 2026-10-11

| 项 | 值 |
|----|----|
| 决策 | D12214 |
| jar | CoreRpg **1.65.732-d12214.local** |
| balance_version | 96 → **97** |
| 池 | 20 → **22**（+`rift` / `cascade`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack10-2026-10-11.md` |
| 门禁 | affixpack10 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ CRN 22vs20 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack10RiftCascade_D12214` + AffixExport 绿 |
| 冒烟 | DEFER（botd 未监听 8765；POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-2218 |
| backup | `/workspace/backup/CoreRpg-1.65.731-pre-1.65.732-d12214.jar` |
| commit | `57e115a4` |
| live | Enabling CoreRpg v1.65.732-d12214.local @22:27:07 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
