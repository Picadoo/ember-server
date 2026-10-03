# STATUS · B2.26 NI 余烬核心碎片 lore 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 02:36 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B） |
| Design tip | `0e76453` · `docs/design/design-ember-mat-core-fragment-ni-lore-copy.md` |
| Parent | B2.25 close `35abc98`（次对齐 `ae55928`） |
| Next | **余烬-物品** 删 `ember-dungeon.yml` 内 core_fragment lore 灰字裸 id 行 → 测试 |
| Scope | 删 `- '&7mat_ember_core_fragment'`；保留 name「余烬核心碎片」与说明行 / enchantments / hideflags；零改数值/配方/给物；勿厚批其余 6 件 |
| Acceptance | lore 列表无字面 mat_ember_core_fragment（键名可留）+ 悬停轻测 |
| Excluded | 其余 `&7mat_*` / 斜杠批 B；精英壳；wall-clock/DPS；挑刺；B0.1 声称 |

## Conclusion

批准 **A**：只删 NI 余烬核心碎片 lore 1 行裸 id。方案 B（其余 6 件 / 斜杠）与精英壳 **勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit。
