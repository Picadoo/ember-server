# STATUS · B2.13 talent `*_cap` copy approval A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:07 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A** |
| Design tip | `c86ebd6` · `docs/design-ember-talent-cap-copy.md` (verified on `main` / `origin/main`) |
| Next | **余烬-插件 · priority true · 待插件施工** |
| Scope | `plugins/TrMenu/menus/ember_talent.yml` player-visible strings only: 14 literal `*_cap` → 燎原 / 烟幕 / 永护 |
| Excluded | `talent.yml` keys / requires / cost / stats; unlock `command:` args; §8 `nodeId:` lines; plan B; other menus; all runtime/player/world files |

## Conclusion

Approved **A** for B2.13. Replace only the 14 player-visible `*_cap` literals in `plugins/TrMenu/menus/ember_talent.yml` with the approved layer-1 names. This approval changes documentation only; the plugin role performs the one-file YAML replacement and TrMenu reload separately.

**Hard boundary:** do not alter `talent.yml`, node keys, unlock command arguments, cost, requires, stats, covenant logic, point formulas, reset price, §8 `nodeId:` / internal-id lines, plan B cleanup, other menus, or any runtime/player/world file. Do not claim B0.1 or any other residual debt is cleared. Do not make any YAML change in this approval commit.

## A scope — exact 14 replacements

| # | Area / literal count | Current player-visible string | Approved player-visible string |
|---:|---|---|---|
| T1 | Open tell ×1 | `二层需先点满一层顶（*_cap）` | `二层需先点满一层顶（燎原 / 烟幕 / 永护）` |
| T2 | U lore ×1 | `二层需先点满一层顶（*_cap）` | `二层需先点满一层顶（燎原 / 烟幕 / 永护）` |
| T3 | U tell ×1 | `tell: §d[天赋] §7点击节点解锁；二层需先点满 §f*_cap§7。` | `tell: §d[天赋] §7点击节点解锁；二层需先点满一层顶 §f燎原 / 烟幕 / 永护§7。` |
| T4 | 规则速览 lore #4 ×1 | `二层需先点满一层顶（*_cap）` | `二层需先点满一层顶（燎原 / 烟幕 / 永护）` |
| T5 | 规则速览 tell ×1 | `tell: §d[天赋] §7先誓约 → 再点击节点解锁；二层需 *_cap；日免洗 1 次。` | `tell: §d[天赋] §7先誓约 → 再点击节点解锁；二层需一层顶（燎原 / 烟幕 / 永护）；日免洗 1 次。` |
| A2-a | 烬刃 icon a ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「燎原」` |
| A2-b | 烬刃 icon b ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「燎原」` |
| A2-c | 烬刃 icon c ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「燎原」` |
| A2-d | 灰行 icon d ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「烟幕」` |
| A2-e | 灰行 icon e ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「烟幕」` |
| A2-f | 灰行 icon f ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「烟幕」` |
| A2-g | 守墓 icon g ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「永护」` |
| A2-h | 守墓 icon h ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「永护」` |
| A2-i | 守墓 icon i ×1 | `§e先点满一层顶（*_cap）` | `§e先点满一层顶「永护」` |

Preserve surrounding YAML, icon keys, `command: corerpg talent unlock <nodeId>` arguments, formatting, all non-string values, and every non-approved string. The 14 rows above are the complete A scope.

## Acceptance boundary

- `rg -n '\*_cap' plugins/TrMenu/menus/ember_talent.yml` returns zero after construction.
- The approved names appear in the corresponding Open/U/rules strings and tier-2 lore: `燎原 / 烟幕 / 永护`.
- `git diff -- plugins/TrMenu/menus/ember_talent.yml` is exactly the 14 player-visible string replacements above; `talent.yml` and all unlock command arguments are unchanged.
- §8 `nodeId:` / internal-id lines remain unchanged; no plan B cleanup is claimed.
- No cost, requires, stats, point cap, level gate, reset price, covenant logic, other menu, or runtime/player/world change.
- Do not mark PASS or claim B0.1, nodeId/NI id cleanup, or any other residual is cleared.

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按批准 tip `c86ebd6` / `docs/STATUS-ember-talent-cap-copy-approve.md` 施工 B2.13 方案 A。先确认当前 `main` / `origin/main` 已包含 `c86ebd6`，再只改一个文件：`plugins/TrMenu/menus/ember_talent.yml`。仅替换以下 14 个玩家可见字面，不改任何逻辑：

1. Open tell：`二层需先点满一层顶（*_cap）` → `二层需先点满一层顶（燎原 / 烟幕 / 永护）`。
2. U lore：`二层需先点满一层顶（*_cap）` → `二层需先点满一层顶（燎原 / 烟幕 / 永护）`。
3. U tell：`tell: §d[天赋] §7点击节点解锁；二层需先点满 §f*_cap§7。` → `tell: §d[天赋] §7点击节点解锁；二层需先点满一层顶 §f燎原 / 烟幕 / 永护§7。`。
4. 规则速览 lore #4：`二层需先点满一层顶（*_cap）` → `二层需先点满一层顶（燎原 / 烟幕 / 永护）`。
5. 规则速览 tell：`tell: §d[天赋] §7先誓约 → 再点击节点解锁；二层需 *_cap；日免洗 1 次。` → `tell: §d[天赋] §7先誓约 → 再点击节点解锁；二层需一层顶（燎原 / 烟幕 / 永护）；日免洗 1 次。`。
6. 二层 icon a（烬刃）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「燎原」`。
7. 二层 icon b（烬刃）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「燎原」`。
8. 二层 icon c（烬刃）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「燎原」`。
9. 二层 icon d（灰行）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「烟幕」`。
10. 二层 icon e（灰行）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「烟幕」`。
11. 二层 icon f（灰行）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「烟幕」`。
12. 二层 icon g（守墓）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「永护」`。
13. 二层 icon h（守墓）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「永护」`。
14. 二层 icon i（守墓）：`§e先点满一层顶（*_cap）` → `§e先点满一层顶「永护」`。

保留 `talent.yml` 的 `blaze_cap` / `ash_cap` / `warden_cap` 键、requires/cost/stats、所有 `command: corerpg talent unlock <nodeId>` 实参、§8 `nodeId:` / 前置内部 id 行、誓约逻辑、点数/等级门槛、洗点价、其它菜单及其余 YAML。禁止方案 B，禁止改任何其它文件；禁止碰 runtime/player/world。

施工后验收：`rg -n '\*_cap' plugins/TrMenu/menus/ember_talent.yml` 返回 0 行；逐项确认「燎原 / 烟幕 / 永护」；`git diff` 仅上述 14 条玩家可见字符串；再按服约定 reload TrMenu 并回报施工 tip、reload 结果与静态检查。不要宣称 B0.1 或其它残余债已清。

### 余烬-策划 — priority false FYI

FYI（not sent）：B2.13 天赋菜单 `*_cap` 玩家可见文案已批准 **A**。设计 tip `c86ebd6` 已推至 `origin/main`；批准状态 **已批 A · 待插件**。范围仅 `plugins/TrMenu/menus/ember_talent.yml` 的 14 个玩家可见字符串，按誓约改为「燎原 / 烟幕 / 永护」；不改 `talent.yml` 键、requires/cost/stats、unlock command 实参、§8 `nodeId:` / 方案 B、其它菜单或 runtime/player/world。插件施工后再做 `*_cap`=0 静态验收；不宣称 B0.1 或其它残余债已清。

## Blocker

None. This approval commit is documentation-only; plugin YAML construction and reload remain pending.
