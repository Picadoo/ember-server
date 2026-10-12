# STATUS · Affix Pack 34 D12238（断斩 / 壑洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12238 |
| jar | CoreRpg **1.65.756-d12238.local** |
| balance_version | 120 → **121** |
| 池 | 68 → **70**（+`sever` / `chasm`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack34-2026-10-12.md` |
| 门禁 | affixpack34 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 70vs68 MAX_ABS_DPP 0.0 @players=80）|
| 单测 | `varietyPack34SeverChasm_D12238` + AffixExport + bv golden + EmberRunRulesTest |
| 冒烟 | PASS 10/0 FreshQ1100 sever:r1 + FreshQ1101 chasm:r1（断斩/壑洼 cast log） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD；dp-work 无未推提交 |
| COORD | COORD-routine-1010 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.755-pre-1.65.756-d12238.jar` + runtime twin |
| commit | pending |
| live | Enabling CoreRpg v1.65.756-d12238.local @10:19:54 CST · MySQL×2 · SEVERE 0 · smoke PASS |
