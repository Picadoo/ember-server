# STATUS · Affix Pack 19 D12223（缝斩 / 泥沼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12223 |
| jar | CoreRpg **1.65.741-d12223.local** |
| balance_version | 105 → **106** |
| 池 | 38 → **40**（+`seam` / `mire`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack19-2026-10-12.md` |
| 门禁 | affixpack19 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ 40vs38 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack19SeamMire_D12223` + AffixExport |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0243 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.740-pre-1.65.741-d12223.jar` |
| commit | 1a0b505a |
| live | Enabling CoreRpg v1.65.741-d12223.local @02:53:57 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
