# STATUS · Affix Pack 14 D12218（剪切 / 夹击）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12218 |
| jar | CoreRpg **1.65.736-d12218.local** |
| balance_version | 100 → **101** |
| 池 | 28 → **30**（+`shear` / `brace`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack14-2026-10-12.md` |
| 门禁 | affixpack14 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ CRN 30vs28 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack14ShearBrace_D12218` + AffixExport |
| 冒烟 | DEFER if botd down（POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0010 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.735-pre-1.65.736-d12218.jar` |
| commit | (pending) |
| live | Enabling CoreRpg v1.65.736-d12218.local @00:19:35 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
