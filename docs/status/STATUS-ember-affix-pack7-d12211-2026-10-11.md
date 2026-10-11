# STATUS · Affix Pack 7 D12211（脉爆 / 埋雷）· 2026-10-11

| 项 | 值 |
|----|----|
| 决策 | D12211 |
| jar | CoreRpg **1.65.729-d12211.local** |
| balance_version | 93 → **94** |
| 池 | 14 → **16**（+`pulse` / `mines`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack7-2026-10-11.md` |
| 门禁 | affixpack7 WITHIN RANGE（stress ≤2.9 pp vs venom；p2econ CRN 16vs14 MAX_ABS_DPP 2.0）|
| 单测 | `varietyPack7PulseMines_D12211` + AffixExport 绿 |
| 冒烟 | DEFER（mineflayer 检查后记；POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-2013 |
| backup | `/workspace/backup/CoreRpg-1.65.728-pre-1.65.729-d12211.jar` |
| commit | `3af4b65a` |
| live | Enabling CoreRpg v1.65.729-d12211.local @20:41:13 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
