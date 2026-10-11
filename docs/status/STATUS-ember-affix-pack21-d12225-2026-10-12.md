# STATUS · Affix Pack 21 D12225（刺斩 / 沼洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12225 |
| jar | CoreRpg **1.65.743-d12225.local** |
| balance_version | 107 → **108** |
| 池 | 42 → **44**（+`spur` / `fen`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack21-2026-10-12.md` |
| 门禁 | affixpack21 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ 44vs42 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack21SpurFen_D12225` + AffixExport + bv golden |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0344 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.742-pre-1.65.743-d12225.jar` + runtime twin |
| commit | d0bedeb6 |
| live | Enabling CoreRpg v1.65.743-d12225.local @03:53:53 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
