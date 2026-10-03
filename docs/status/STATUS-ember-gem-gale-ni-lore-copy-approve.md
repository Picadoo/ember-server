# STATUS · B2.36 NI 疾风石 lore 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 03:27 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B；不附斜杠；不动 cosmetic / pet） |
| Design tip | `eca16cd` · `docs/design/design-ember-gem-gale-ni-lore-copy.md` |
| Parent | B2.35 close `bab3b39`（`&7gem_*` 剩 1） |
| Next | **余烬-物品** 删 `ember-enhance-gems.yml` 内 gem_gale lore L65 → 测试 |
| Scope | 删 `- '&7gem_ember_gale'`；保留 name「疾风石」、L66/L67；零改数值/配方/给物/镶嵌；施工后 live Items `&7gem_*` 预期 **0**（仅 gem 本轨归零） |
| Acceptance | lore 无字面 gem_ember_gale（键名可留）+ 悬停 + `&7gem_` 计数=0 |
| Excluded | 斜杠；cosmetic/pet；精英壳；wall-clock/DPS；挑刺；B0.1 声称；宣称其它 soft 已清 |

## Conclusion

批准 **A**：只删 NI 疾风石 lore 1 行裸 id。斜杠 / cosmetic / pet **勿与 A 同窗双上**。施工验收可写 gem 本轨归零，**勿**宣称斜杠/cosmetic/pet/B0.1 已清。

本 tip 仅 docs；施工另 commit。
