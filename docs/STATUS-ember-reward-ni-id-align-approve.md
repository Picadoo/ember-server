# STATUS · B2.18 奖励预览页 NI 灰字对齐批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 01:35 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B） |
| Design tip | `5c2f7f2` · `docs/design-ember-reward-ni-id-align.md` |
| Parent | 灵活三窗 close / picky `43bf843` |
| Next | **余烬-插件** 改五份 `*_rewards.yml` → 测试 |
| Scope | 删除五入口奖励预览 lore 中 34 行玩家可见 `§8NI id:` / `§8NI:`；中文主名保留；零改掉落/体力 |
| Acceptance | `rg '§8NI' ember_*_rewards.yml` → 0 + 五入口菜单轻点 |
| Excluded | loot/DP/MM；体力 cost；`ember_set` 方案 B；NI 物品 lore 裸 id；精英厚壳；wall-clock/DPS；挑刺 |

## Conclusion

批准 **A**：只清五入口奖励预览 TrMenu 灰字裸 NI id。方案 B（套装 `gear_ember_*` 中文）**勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit；勿碰 runtime/player/world。

## Hard boundary

- 零改掉落表、体力、概率句语义。
- 不捆 B-flex / B-anvil；**不宣称 B0.1 已清**。
