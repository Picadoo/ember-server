# STATUS · B2.20 拆解菜单重铸石灰字批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 01:50 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B） |
| Design tip | `c6d8b8b` · `docs/design/design-ember-disassemble-reforge-copy.md` |
| Parent | B2.19 close `b3a00b8` |
| Next | **余烬-插件** 删 `ember_disassemble.yml` 重铸 lore 裸 id 行 → 测试 |
| Scope | 删 L67 `§8mat_ember_reforge_stone`；保留 L66「§8消耗 §6余烬重铸石」；零改拆解数值/掉落/体力 |
| Acceptance | `rg mat_ember_reforge_stone ember_disassemble.yml` → 0 + 拆解页悬停轻点 |
| Excluded | NI 物品 lore B；精英壳；scrap/reforge 逻辑；wall-clock/DPS；挑刺；B0.1 声称 |

## Conclusion

批准 **A**：只删 `ember_disassemble.yml` 重铸 lore 1 行玩家可见裸 NI id。方案 B（NI 物品 lore）与精英壳 **勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit。
