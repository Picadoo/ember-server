# STATUS · Affix Pack 20 D12224（脊斩 / 泽地）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12224 |
| jar | CoreRpg **1.65.742-d12224.local** |
| balance_version | 106 → **107** |
| 池 | 40 → **42**（+`ridge` / `marsh`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack20-2026-10-12.md` |
| 门禁 | affixpack20 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ 42vs40 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack20RidgeMarsh_D12224` + AffixExport |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0313 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.741-pre-1.65.742-d12224.jar` + runtime twin |
| commit | ae6cb6b9 |
| live | Enabling CoreRpg v1.65.742-d12224.local @03:21:28 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
