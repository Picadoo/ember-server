# STATUS · Affix Pack 25 D12229（斫斩 / 谷洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12229 |
| jar | CoreRpg **1.65.747-d12229.local** |
| balance_version | 111 → **112** |
| 池 | 50 → **52**（+`hew` / `dell`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack25-2026-10-12.md` |
| 门禁 | affixpack25 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 52vs50 MAX_ABS_DPP 1.0）|
| 单测 | `varietyPack25HewDell_D12229` + AffixExport + bv golden |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0539 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.746-pre-1.65.747-d12229.jar` + runtime twin |
| commit | 3b25720e |
| live | Enabling CoreRpg v1.65.747-d12229.local @05:48:24 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
