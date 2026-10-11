# STATUS · Affix Pack 24 D12228（曳斩 / 山洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12228 |
| jar | CoreRpg **1.65.746-d12228.local** |
| balance_version | 110 → **111** |
| 池 | 48 → **50**（+`draw` / `tarn`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack24-2026-10-12.md` |
| 门禁 | affixpack24 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 50vs48 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack24DrawTarn_D12228` + AffixExport + bv golden |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0516 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.745-pre-1.65.746-d12228.jar` + runtime twin |
| commit | e8ba4299 |
| live | Enabling CoreRpg v1.65.746-d12228.local @05:23:12 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
