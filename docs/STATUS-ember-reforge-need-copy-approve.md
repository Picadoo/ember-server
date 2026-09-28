# STATUS · B2.21 CoreRpg 重铸缺料 chat 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 01:57 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B） |
| Design tip | `e81c904` · `docs/design-ember-reforge-need-copy.md` |
| Parent | B2.20 close `5bbaab0` · 测旁证 `492f996` |
| Next | **余烬-插件** 改 `ScrapService.java` L385 展示句 → 编译部署 → 测试 |
| Scope | 缺料 chat：`需要 `+stoneNiId → 字面「需要 余烬重铸石 ×1」；count/consume 仍用 stoneNiId；零改消耗×1 / stone_ni_id |
| Acceptance | 无石点重铸 chat 人话且无字面 mat_ember_reforge_stone；消耗逻辑不变 |
| Excluded | NI 物品 lore B；精英壳；TrMenu；wall-clock/DPS；挑刺；B0.1 声称 |

## Conclusion

批准 **A**：只改 `ScrapService.cmdReforge` 缺料 **1** 句玩家可见 chat。方案 B（NI 物品 lore）与精英壳 **勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit（含 Java/jar 按专岗约定）。
