# STATUS · Affix Pack 18 D12222（刃边 / 浅井）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12222 |
| jar | CoreRpg **1.65.740-d12222.local** |
| balance_version | 104 → **105** |
| 池 | 36 → **38**（+`edge` / `well`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack18-2026-10-12.md` |
| 门禁 | affixpack18 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ 38vs36 MAX_ABS_DPP 1.0）|
| 单测 | `varietyPack18EdgeWell_D12222` + AffixExport |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0213 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.739-pre-1.65.740-d12222.jar` |
| commit | d9f19676 |
| live | Enabling CoreRpg v1.65.740-d12222.local @02:23:32 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
