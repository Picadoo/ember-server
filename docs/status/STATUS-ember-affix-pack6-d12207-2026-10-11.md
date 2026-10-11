# STATUS · Affix Pack 6 D12207（雹霰 / 牵丝）· 2026-10-11

| 项 | 值 |
|----|----|
| 决策 | D12207 |
| jar | CoreRpg **1.65.725-d12207.local** |
| balance_version | 89 → **90** |
| 池 | 12 → **14**（+`hail` / `tether`） |
| 奖励 | `affix_shard: 2` 不变 |
| 设计 | `docs/design/DESIGN-ember-affix-pack6-2026-10-11.md` |
| 门禁 | affixpack6 WITHIN RANGE（stress ≤2.5 pp vs venom；p2econ 14vs12 MAX_ABS_DPP 3.0）|
| 单测 | `varietyPack6HailTether_D12207` + EventRoomWeight 绿 |
| 冒烟 | DEFER（mineflayer 不可用；POLICY batch） |
| 资产 | 未改 → skip persist-roundtrip |
| 挑刺 | stage1-6slot HOLD 未推 |
| COORD | COORD-routine-1811 |
| commit | `4ef13cc6` |
| live | Enabling CoreRpg v1.65.725-d12207.local @18:28:37 CST · MySQL×2 · SEVERE 0 · smoke DEFER |
| backup | `/workspace/backup/CoreRpg-1.65.724-pre-1.65.725-d12207.jar` |
