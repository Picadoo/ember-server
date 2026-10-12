# STATUS · Affix Pack 36 D12240（裂斩 / 澳洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12240 |
| jar | CoreRpg **1.65.758-d12240.local** |
| balance_version | 122 → **123** |
| 池 | 72 → **74**（+`rip` / `cove`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack36-2026-10-12.md` |
| 门禁 | affixpack36 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 74vs72 MAX_ABS_DPP 0.0 @players=80）|
| 单测 | `varietyPack36RipCove_D12240` + AffixExport + bv golden + EmberRunRulesTest |
| 冒烟 | PASS 11/1 FreshQ1104 rip:r1 + FreshQ1105 cove:r1（裂斩/澳洼 cast log；1 FAIL = PlaceholderAPI ConnectException，非 CoreRpg SEVERE） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD；dp-work 无未推提交 |
| COORD | COORD-routine-1117 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.757-pre-1.65.758-d12240.jar` + runtime twin |
| commit | PLACEHOLDER_CODE (code) + STATUS stamp |
| live | Enabling CoreRpg v1.65.758-d12240.local @11:28:20 CST · MySQL×2 · SEVERE 0 · smoke cast OK |
