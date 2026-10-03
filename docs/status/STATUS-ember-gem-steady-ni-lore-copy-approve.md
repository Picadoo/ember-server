# STATUS · B2.34 NI 稳固石 lore 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 03:16 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B；不附斜杠；不动其余 gem / cosmetic / pet） |
| Design tip | `baa2705` · `docs/design/design-ember-gem-steady-ni-lore-copy.md` |
| Parent | B2.33 close `973e941`（`&7gem_*` 剩 3） |
| Next | **余烬-物品** 删 `ember-enhance-gems.yml` 内 gem_steady lore L43 → 测试 |
| Scope | 删 `- '&7gem_ember_steady'`；保留 name「稳固石」、L44/L45；零改数值/配方/给物/镶嵌；勿厚批 drain/gale；施工后 live `&7gem_*` 预期剩 2 |
| Acceptance | lore 无字面 gem_ember_steady（键名可留）+ 悬停 + `&7gem_` 计数=2 |
| Excluded | drain/gale；斜杠；cosmetic/pet；精英壳；wall-clock/DPS；挑刺；B0.1 声称 |

## Conclusion

批准 **A**：只删 NI 稳固石 lore 1 行裸 id。其余 gem / 斜杠 / cosmetic / pet **勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit。
