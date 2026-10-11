# STATUS · Affix Pack 27 D12231（剪斩 / 谷地）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12231 |
| jar | CoreRpg **1.65.749-d12231.local** |
| balance_version | 113 → **114** |
| 池 | 54 → **56**（+`snip` / `vale`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack27-2026-10-12.md` |
| 门禁 | affixpack27 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 56vs54 MAX_ABS_DPP 2.0）|
| 单测 | `varietyPack27SnipVale_D12231` + AffixExport + bv golden + EmberRunRulesTest 98 |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推（fdcda0c4） |
| COORD | COORD-routine-0649 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.748-pre-1.65.749-d12231.jar` + runtime twin |
| commit | e51e407d92ff19d5c315544b1c0e947e1ea10874 |
| live | Enabling CoreRpg v1.65.749-d12231.local @06:57:31 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
