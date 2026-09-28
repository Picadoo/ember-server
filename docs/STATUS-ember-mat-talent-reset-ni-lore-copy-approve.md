# STATUS · B2.28 NI 天赋重置券 lore 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 02:46 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B；不附斜杠） |
| Design tip | `2c50336` · `docs/design-ember-mat-talent-reset-ni-lore-copy.md` |
| Parent | B2.27 close `2aaffe3` |
| Next | **余烬-物品** 删 `ember-covenant-talent.yml` 内 talent_reset lore 灰字裸 id 行 → 测试 |
| Scope | 删 `- '&7mat_ember_talent_reset'`；保留 name「天赋重置券」、洗点说明、**`/corerpg` 斜杠不动**；零改数值/配方/给物；勿厚批其余 4 件 |
| Acceptance | lore 列表无字面 mat_ember_talent_reset（键名可留）+ 悬停轻测；斜杠行仍在 |
| Excluded | 其余 `&7mat_*` / 斜杠批 B（含本物斜杠）；精英壳；wall-clock/DPS；挑刺；B0.1 声称 |

## Conclusion

批准 **A**：只删 NI 天赋重置券 lore 1 行裸 id。同物 `/corerpg`、其余 4 件 mat、精英壳 **勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit。
