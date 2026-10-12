# STATUS · Affix Pack 30 D12234（裂斩 / 坡洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12234 |
| jar | CoreRpg **1.65.752-d12234.local** |
| balance_version | 116 → **117** |
| 池 | 60 → **62**（+`gash` / `slade`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack30-2026-10-12.md` |
| 门禁 | affixpack30 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 62vs60 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack30GashSlade_D12234` + AffixExport + bv golden + EmberRunRulesTest 101 |
| 冒烟 | PASS 10/0 FreshQ1092 gash:r1 + FreshQ1093 slade:r1（裂斩/坡洼 cast log） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推（fdcda0c4） |
| COORD | COORD-routine-0818 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.751-pre-1.65.752-d12234.jar` + runtime twin |
| commit | d6a39dd54599973f29545b3b6f66d0467be36d6a (code) + STATUS stamp |
| live | Enabling CoreRpg v1.65.752-d12234.local @08:27:15 CST · MySQL×2 · SEVERE 0 · smoke PASS |
