# STATUS · Affix Pack 29 D12233（锉斩 / 幽洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12233 |
| jar | CoreRpg **1.65.751-d12233.local** |
| balance_version | 115 → **116** |
| 池 | 58 → **60**（+`rasp` / `combe`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack29-2026-10-12.md` |
| 门禁 | affixpack29 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 60vs58 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack29RaspCombe_D12233` + AffixExport + bv golden + EmberRunRulesTest 100 |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推（fdcda0c4） |
| COORD | COORD-routine-0742 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.750-pre-1.65.751-d12233.jar` + runtime twin |
| commit | 3909ff9627a35243386884e14fcd7b8c0fd592dc |
| live | Enabling CoreRpg v1.65.751-d12233.local @07:51:22 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
