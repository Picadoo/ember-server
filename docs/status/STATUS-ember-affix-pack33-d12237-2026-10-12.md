# STATUS · Affix Pack 33 D12237（蚀斩 / 陨洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12237 |
| jar | CoreRpg **1.65.755-d12237.local** |
| balance_version | 119 → **120** |
| 池 | 66 → **68**（+`etch` / `crater`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack33-2026-10-12.md` |
| 门禁 | affixpack33 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 68vs66 MAX_ABS_DPP 0.0 @players=80）|
| 单测 | `varietyPack33EtchCrater_D12237` + AffixExport + bv golden + EmberRunRulesTest |
| 冒烟 | PASS 10/0 FreshQ1098 etch:r1 + FreshQ1099 crater:r1（蚀斩/陨洼 cast log） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推（fdcda0c4） |
| COORD | COORD-routine-0947 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.754-pre-1.65.755-d12237.jar` + runtime twin |
| commit | TBD |
| live | Enabling CoreRpg v1.65.755-d12237.local @09:59:20 CST · MySQL×2 · SEVERE 0 · smoke PASS |
