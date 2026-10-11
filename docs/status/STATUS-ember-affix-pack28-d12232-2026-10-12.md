# STATUS · Affix Pack 28 D12232（切斩 / 谷原）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12232 |
| jar | CoreRpg **1.65.750-d12232.local** |
| balance_version | 114 → **115** |
| 池 | 56 → **58**（+`nick` / `dale`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack28-2026-10-12.md` |
| 门禁 | affixpack28 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 58vs56 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack28NickDale_D12232` + AffixExport + bv golden + EmberRunRulesTest 99 |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推（fdcda0c4） |
| COORD | COORD-routine-0711 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.749-pre-1.65.750-d12232.jar` + runtime twin |
| commit | baac0fda5fdac9683c75aa2a6a4518f138861875 |
| live | Enabling CoreRpg v1.65.750-d12232.local @07:19:17 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
