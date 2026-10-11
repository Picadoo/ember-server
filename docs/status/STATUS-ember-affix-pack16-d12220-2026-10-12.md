# STATUS · Affix Pack 16 D12220（扇刃 / 沉渊）· 2026-10-12

| 项 | 值 |
|----|----|
| 决策 | D12220 |
| jar | CoreRpg **1.65.738-d12220.local** |
| balance_version | 102 → **103** |
| 池 | 32 → **34**（+`fan` / `sink`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack16-2026-10-12.md` |
| 门禁 | affixpack16 WITHIN RANGE（stress ≤2.0 pp vs venom；p2econ 34vs32 MAX_ABS_DPP 2.0）|
| 单测 | `varietyPack16FanSink_D12220` + AffixExport |
| 冒烟 | DEFER if botd down（POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-0115 |
| backup | `/workspace/backup/CoreRpg-plugins-1.65.737-pre-1.65.738-d12220.jar` |
| commit | （待） |
| live | （待部署） |
