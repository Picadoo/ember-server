# STATUS · Affix Pack 32 D12236（刻斩 / 坑洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12236 |
| jar | CoreRpg **1.65.754-d12236.local** |
| balance_version | 118 → **119** |
| 池 | 64 → **66**（+`score` / `pit`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack32-2026-10-12.md` |
| 门禁 | affixpack32 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 66vs64 MAX_ABS_DPP 0.0 @players=80）|
| 单测 | `varietyPack32ScorePit_D12236` + AffixExport + bv golden + EmberRunRulesTest 107 |
| 冒烟 | PASS 10/0 FreshQ1096 score:r1 + FreshQ1097 pit:r1（刻斩/坑洼 cast log） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推（fdcda0c4） |
| COORD | COORD-routine-0918 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.753-pre-1.65.754-d12236.jar` + runtime twin |
| commit | TBD |
| live | Enabling CoreRpg v1.65.754-d12236.local @09:27:25 CST · MySQL×2 · SEVERE 0 · smoke PASS |
