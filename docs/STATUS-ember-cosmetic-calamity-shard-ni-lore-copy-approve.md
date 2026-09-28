# STATUS · B2.37 NI 灾厄外观碎片 lore 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 03:32 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B；不附斜杠；不动 pet） |
| Design tip | `3717031` · `docs/design-ember-cosmetic-calamity-shard-ni-lore-copy.md` |
| Parent | B2.36 close `54ef5c4`（gem/mat 本轨归零） |
| Next | **余烬-物品** 删 `ember-abyss-calamity.yml` 内 cosmetic lore L19 → 测试 |
| Scope | 删 `- '&7cosmetic_calamity_shard'`；保留 name「灾厄外观碎片」、L20/L21；零改数值/配方/给物/掉落；施工后 live Items `&7cosmetic_*` 预期 **0**（仅本件） |
| Acceptance | lore 无字面 cosmetic_calamity_shard（键名可留）+ 悬停 + `&7cosmetic_` 计数=0 |
| Excluded | pet；斜杠；精英壳；wall-clock/DPS；挑刺；B0.1 声称；宣称其它 soft 已清 |

## Conclusion

批准 **A**：只删 NI 灾厄外观碎片 lore 1 行裸 id。pet / 斜杠 **勿与 A 同窗双上**。施工验收可写本件 cosmetic 归零，**勿**宣称 pet/斜杠/B0.1 已清。

本 tip 仅 docs；施工另 commit。
