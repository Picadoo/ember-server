# STATUS · Affix Pack 13 D12217（枪刺 / 新星）· 2026-10-11

| 项 | 值 |
|----|----|
| 决策 | D12217 |
| jar | CoreRpg **1.65.735-d12217.local** |
| balance_version | 99 → **100** |
| 池 | 26 → **28**（+`lance` / `nova`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack13-2026-10-11.md` |
| 门禁 | affixpack13 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ CRN 28vs26 MAX_ABS_DPP 1.0）|
| 单测 | `varietyPack13LanceNova_D12217` + AffixExport |
| 冒烟 | DEFER（botd 未监听 8765；POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-2339 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.734-pre-1.65.735-d12217.jar` |
| commit | ba9e72f5 |
| live | Enabling CoreRpg v1.65.735-d12217.local @23:50:03 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
