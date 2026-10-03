# STATUS · B2.32 NI 余烬稳固符 lore 批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 03:05 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B；不附斜杠；不动 gem_*） |
| Design tip | `cf7eaaf` · `docs/design/design-ember-mat-stable-charm-ni-lore-copy.md` |
| Parent | B2.31 close `c8ab22c` |
| Next | **余烬-物品** 删 `ember-enhance-gems.yml` 内 stable_charm lore L20 → 测试 |
| Scope | 删 `- '&7mat_ember_stable_charm'`；保留 name「余烬稳固符」、L21/L22；零改数值/配方/给物；勿动 gem_*；施工后 live Items `rg "&7mat_"` 归零（仅本轨，非其它 soft） |
| Acceptance | lore 无字面 mat_ember_stable_charm（键名可留）+ 悬停 + live `&7mat_` 归零 |
| Excluded | gem_*；斜杠批；精英壳；wall-clock/DPS；挑刺；B0.1 声称 |

## Conclusion

批准 **A**：只删 NI 余烬稳固符 lore 1 行裸 id。本轨 live 最后 1 件；gem_* / 斜杠 / 精英壳 **勿与 A 同窗双上**。

本 tip 仅 docs；施工另 commit。
