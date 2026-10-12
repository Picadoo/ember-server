# STATUS · Affix Pack 31 D12235（剜斩 / 坳洼）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12235 |
| jar | CoreRpg **1.65.753-d12235.local** |
| balance_version | 117 → **118** |
| 池 | 62 → **64**（+`carve` / `hollow`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack31-2026-10-12.md` |
| 门禁 | affixpack31 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 64vs62 MAX_ABS_DPP 2.0 @players=300）|
| 单测 | `varietyPack31CarveHollow_D12235` + AffixExport + bv golden + EmberRunRulesTest 102 |
| 冒烟 | PASS 10/0 FreshQ1094 carve:r1 + FreshQ1095 hollow:r1（剜斩/坳洼 cast log） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推（fdcda0c4） |
| COORD | COORD-routine-0840 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.752-pre-1.65.753-d12235.jar` + runtime twin |
| commit | 2e3b049e265b8c89860cda97b4fee0ea8d522148 (code) + STATUS stamp |
| live | Enabling CoreRpg v1.65.753-d12235.local @09:06:01 CST · MySQL×2 · SEVERE 0 · smoke PASS |
