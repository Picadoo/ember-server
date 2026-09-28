# STATUS · B2.27 NI 誓约重置券 lore 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 02:41 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B；不附斜杠） |
| Design tip | `8bb16c9` · `docs/design-ember-mat-covenant-reset-ni-lore-copy.md` |
| Parent | B2.26 close `f796f3a` |
| Next | **余烬-物品** 删 `ember-covenant-talent.yml` 内 covenant_reset lore L8 → 测试 |
| Scope | 删 `- '&7mat_ember_covenant_reset'`；保留 name「誓约重置券」、L9 洗约说明、**L10 `/corerpg` 斜杠不动**；零改数值/配方/给物；勿厚批其余 5 件 |
| Acceptance | lore 列表无字面 mat_ember_covenant_reset（键名可留）+ 悬停轻测；斜杠行仍在 |
| Excluded | 其余 `&7mat_*` / 斜杠批 B（含本物 L10）；精英壳；wall-clock/DPS；挑刺；B0.1 声称 |

## Conclusion

批准 **A**：只删 NI 誓约重置券 lore 1 行裸 id。同物 `/corerpg`、其余 5 件 mat、精英壳 **勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit。
