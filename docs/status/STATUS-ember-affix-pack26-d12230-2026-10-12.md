# STATUS · Affix Pack 26 D12230（削斩 / 峡洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12230 |
| jar | CoreRpg **1.65.748-d12230.local** |
| balance_version | 112 → **113** |
| 池 | 52 → **54**（+`chip` / `glen`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack26-2026-10-12.md` |
| 门禁 | affixpack26 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 54vs52 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack26ChipGlen_D12230` + AffixExport + bv golden |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推（fdcda0c4） |
| COORD | COORD-routine-0610 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.747-pre-1.65.748-d12230.jar` + runtime twin |
| commit | 12239fba |
| live | Enabling CoreRpg v1.65.748-d12230.local @06:20:39 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
