# STATUS · Affix Pack 8 D12212（孢雾 / 侧袭）· 2026-10-11

| 项 | 值 |
|----|----|
| 决策 | D12212 |
| jar | CoreRpg **1.65.730-d12212.local** |
| balance_version | 94 → **95** |
| 池 | 16 → **18**（+`spore` / `flank`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack8-2026-10-11.md` |
| 门禁 | affixpack8 WITHIN RANGE（stress ≤2.9 pp vs venom；p2econ CRN 18vs16 MAX_ABS_DPP 1.0）|
| 单测 | `varietyPack8SporeFlank_D12212` + AffixExport 绿 |
| 冒烟 | DEFER（mineflayer 检查后记；POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-2112 |
| backup | `/workspace/backup/CoreRpg-1.65.729-pre-1.65.730-d12212.jar` |
| commit | `a3206034` |
| live | Enabling CoreRpg v1.65.730-d12212.local @21:23:37 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
