# STATUS · Affix Pack 15 D12219（钩落 / 斜切）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12219 |
| jar | CoreRpg **1.65.737-d12219.local** |
| balance_version | 101 → **102** |
| 池 | 30 → **32**（+`hook` / `skew`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack15-2026-10-12.md` |
| 门禁 | affixpack15 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ 32vs30 @200 MAX_ABS_DPP 3.0）|
| 单测 | `varietyPack15HookSkew_D12219` + AffixExport |
| 冒烟 | DEFER if botd down（POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0040 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.736-pre-1.65.737-d12219.jar` |
| commit | 2220d618 |
| live | Enabling CoreRpg v1.65.737-d12219.local @00:53:06 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
