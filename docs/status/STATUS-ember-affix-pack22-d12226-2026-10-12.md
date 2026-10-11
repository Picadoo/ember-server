# STATUS · Affix Pack 22 D12226（掠斩 / 泥潭）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12226 |
| jar | CoreRpg **1.65.744-d12226.local** |
| balance_version | 108 → **109** |
| 池 | 44 → **46**（+`gleam` / `bog`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack22-2026-10-12.md` |
| 门禁 | affixpack22 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ 46vs44 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack22GleamBog_D12226` + AffixExport + bv golden |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0419 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.743-pre-1.65.744-d12226.jar` + runtime twin |
| commit | 564af475 |
| live | Enabling CoreRpg v1.65.744-d12226.local @04:25:15 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
