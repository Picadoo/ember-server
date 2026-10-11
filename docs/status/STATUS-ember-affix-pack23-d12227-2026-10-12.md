# STATUS · Affix Pack 23 D12227（扫斩 / 洼池）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12227 |
| jar | CoreRpg **1.65.745-d12227.local** |
| balance_version | 109 → **110** |
| 池 | 46 → **48**（+`slash` / `basin`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack23-2026-10-12.md` |
| 门禁 | affixpack23 WITHIN RANGE（stress 2.0 pp vs venom；p2econ 48vs46 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack23SlashBasin_D12227` + AffixExport + bv golden |
| 冒烟 | DEFER（botd:8765 down · POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0445 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.744-pre-1.65.745-d12227.jar` + runtime twin |
| commit | 8e8c1449 |
| live | Enabling CoreRpg v1.65.745-d12227.local @04:55:42 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
