# STATUS · Affix Pack 9 D12213（贯穿 / 散射）· 2026-10-11

| 项 | 值 |
|----|----|
| 决策 | D12213 |
| jar | CoreRpg **1.65.731-d12213.local** |
| balance_version | 95 → **96** |
| 池 | 18 → **20**（+`pierce` / `scatter`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack9-2026-10-11.md` |
| 门禁 | affixpack9 WITHIN RANGE（stress ≤2.9 pp vs venom；p2econ CRN 20vs18 MAX_ABS_DPP 0.0）|
| 单测 | `varietyPack9PierceScatter_D12213` + AffixExport 绿 |
| 冒烟 | DEFER（mineflayer 检查后记；POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-2143 |
| backup | `/workspace/backup/CoreRpg-1.65.730-pre-1.65.731-d12213.jar` |
| commit | `95063911` |
| live | Enabling CoreRpg v1.65.731-d12213.local @21:53:34 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
