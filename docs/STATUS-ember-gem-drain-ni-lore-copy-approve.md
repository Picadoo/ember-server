# STATUS · B2.35 NI 汲取石 lore 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 03:21 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B；不附斜杠；不动 gale / cosmetic / pet） |
| Design tip | `c9cdb28` · `docs/design-ember-gem-drain-ni-lore-copy.md` |
| Parent | B2.34 close `5b47896`（`&7gem_*` 剩 2） |
| Next | **余烬-物品** 删 `ember-enhance-gems.yml` 内 gem_drain lore L54 → 测试 |
| Scope | 删 `- '&7gem_ember_drain'`；保留 name「汲取石」、L55/L56；零改数值/配方/给物/镶嵌；勿厚批 gale；施工后 live `&7gem_*` 预期剩 1 |
| Acceptance | lore 无字面 gem_ember_drain（键名可留）+ 悬停 + `&7gem_` 计数=1 |
| Excluded | gale；斜杠；cosmetic/pet；精英壳；wall-clock/DPS；挑刺；B0.1 声称 |

## Conclusion

批准 **A**：只删 NI 汲取石 lore 1 行裸 id。gale / 斜杠 / cosmetic / pet **勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit。
