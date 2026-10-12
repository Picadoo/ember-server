# STATUS · Affix Pack 35 D12239（撕斩 / 湾洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12239 |
| jar | CoreRpg **1.65.757-d12239.local** |
| balance_version | 121 → **122** |
| 池 | 70 → **72**（+`rend` / `gulf`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack35-2026-10-12.md` |
| 门禁 | affixpack35 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 72vs70 MAX_ABS_DPP 0.0 @players=80）|
| 单测 | `varietyPack35RendGulf_D12239` + AffixExport + bv golden + EmberRunRulesTest |
| 冒烟 | PASS 11/1 FreshQ1102 rend:r1 + FreshQ1103 gulf:r1（撕斩/湾洼 cast log；1 FAIL = AuthLib ConnectException，非 CoreRpg SEVERE） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD；dp-work 无未推提交 |
| COORD | COORD-routine-1044 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.756-pre-1.65.757-d12239.jar` + runtime twin |
| commit | c1dedb2f3a335b21c18be47c17f3b7f41603339b (code) + STATUS stamp |
| live | Enabling CoreRpg v1.65.757-d12239.local @10:52:21 CST · MySQL×2 · SEVERE 0 · smoke cast OK |
