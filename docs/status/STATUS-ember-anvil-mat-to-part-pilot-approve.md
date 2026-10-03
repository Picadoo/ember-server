# STATUS · B-anvil-1 烬砧材料→部件短链批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 01:25 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B） |
| Design tip | `ab33c21` · `docs/design/design-ember-anvil-mat-to-part-pilot.md` |
| Parent close | B-flex-2 `ba47f3e` |
| Next | **余烬-物品** → **余烬-插件**（可同窗并行）→ 测试 |
| Scope | `mat_ember_shard`×12 + `ingot_ember_iron`×2 → NI `part_ember_ash_brace`「余烬灰箍」· 副手物防 +1 · TrMenu `ember_forge` 点击炼制；复用 B-flex-1 OffHand |
| Acceptance | 仅静态 `rg` + 菜单轻测 |
| Excluded | 改 `forge.yml` 升阶；四件甲/多部位锻炉；誓约/体力；B-flex 已结物；B1 骨饰与 A 双上；B2.x；B0.1 声称；wall-clock/DPS；挑刺 |

## Conclusion

批准 **A**：在现网烬砧（升阶+强化/镶嵌/分解）之上增加 **一条**材料→可装备部件短链。产物 `part_ember_ash_brace`「余烬灰箍」，消耗碎片×12 + 余烬铁锭×2，副手槽物防 +1（薄于守腕 +2），经 TrMenu 点击炼制。**不改** `forge.yml` 升阶四条。方案 B（骨饰/换皮配方）**勿与 A 同窗双上**。

本批准 tip 仅更新 docs。施工另 commit；勿碰 runtime/player/world。

## A scope

| Area | Approved A | Not approved |
|---|---|---|
| NI | `part_ember_ash_brace` · 物防 +1 · 副手 | B1 骨饰；多部件同窗 |
| Recipe | shard×12 + ingot×2 → 1 件；无币无体力 | 改升阶/强化表；plate 全沉底表 |
| UX | `ember_forge`（或薄页 `ember_part`）点击；lore 不教斜杠 | 玩家手打 `/corerpg` 主路径 |
| Hook | 加入 `stats.offhand`；复用 OffHand Stat | 新装备槽；护符 accessories best |
| Test | 静态 `rg` + 菜单轻测 | wall-clock、DPS、挑刺 |

## Hard boundary

- **零动** `forge.yml` 升阶成本与 T 档目标；**零动** `enhance.yml` / 誓约 / 体力日周门。
- 不回改 B-flex-1 守腕/生坠、B-flex-2 踏步。
- 不捆 B2.x 文案窗；**不宣称 B0.1 已清**。
- 不触碰 runtime/player/world。

## Handoff drafts (not sent)

### 余烬-物品 — priority true

按设计 tip `ab33c21` / 本批准施工 NI `part_ember_ash_brace`「余烬灰箍」：副手部件 lore、物防 +1；材料 id 复用既有 `mat_ember_shard` / `ingot_ember_iron`（勿改其定义数值）。勿改刃护符/升阶物。完成后 tip + push，回报。

### 余烬-插件 — priority true

按同设计/批准：薄配方表（`part.yml` 或独立段，**勿改**升阶 4 条）+ TrMenu `ember_forge` 炼部件钮 + `stats.offhand` 加 `part_ember_ash_brace`；玩家 lore 不教手打。可与物品并行（id 已锁定）。验收静态 rg + 菜单轻测准备。勿碰 runtime/player/world。

**总控会 SendToAgent；本次不发送。**

## Blocker

None. Push design tips then dispatch 物品 + 插件.
