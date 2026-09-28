# STATUS · B2.17 talent `cost`→消耗 approval A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:48 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B） |
| Design tip | `3a3a226` · `docs/design-ember-talent-cost-copy.md`（已推送并核验 `main` / `origin/main`） |
| Parent close | B2.16 `22c2d40` |
| Next | **余烬-插件 · priority true · 已批 A · 待插件施工** |
| Scope | Only `plugins/TrMenu/menus/ember_talent.yml`: 13 player-visible type lines, `cost`→`消耗`; numbers 2/3/4 unchanged |
| Excluded | Plan B / any other menu; `talent.yml`; unlock `command:` args; cost values/requires/stats; all runtime/player/world files; B0.1 claims |

## Conclusion

Approved **A** for B2.17. After this tip lands, replace only the thirteen approved player-visible type-line strings in `plugins/TrMenu/menus/ember_talent.yml`. This approval changes documentation only; the plugin role performs the one-file YAML replacement and TrMenu reload separately.

**Hard boundary:** do not alter `talent.yml`, node keys, unlock command arguments, cost values, requires, stats, covenant logic, point formulas, reset price, other menus, or any runtime/player/world file. Do not attach Plan B. Do not claim B0.1, reward-page `NI id:`, `gear_ember_*`, or any other residual debt is cleared. Do not make any YAML change in this approval commit.

## A scope — exact 13 replacements

| # | Area | Current player-visible lore | Approved player-visible lore |
|---:|---|---|---|
| A1–A3 | 根节点 · 燃锋 / 灰踪 / 守碑 | `§7根节点 · 被动 · cost 2` | `§7根节点 · 被动 · 消耗 2` |
| A4 | 烬刃 · 烬斩 | `§7主动技 · cost 4` | `§7主动技 · 消耗 4` |
| A5–A13 | 二层 · 烬刃 / 灰行 / 守墓 | `§7二层 · 被动 · cost N` | `§7二层 · 被动 · 消耗 N`（N=3 或 4 原样） |

Preserve surrounding YAML, icon keys, formatting, numeric values, separators, every `command: corerpg talent unlock <nodeId>` argument, and every non-approved string. The thirteen rows above are the complete A scope.

## Acceptance boundary

- `rg -n '· cost ' plugins/TrMenu/menus/ember_talent.yml` returns zero after construction.
- The 13 approved lines read `被动 · 消耗 2` ×3, `主动技 · 消耗 4` ×1, and `被动 · 消耗 3/4` ×9; all numbers remain unchanged.
- `git diff -- plugins/TrMenu/menus/ember_talent.yml` is exactly the thirteen player-visible cost-label replacements.
- `talent.yml` keys/stats/requires/cost values and all unlock command arguments are unchanged.
- No Plan B, cost-value, other-menu, runtime/player/world, B0.1, NI-id, or `gear_ember_*` claim.

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按批准设计 tip `3a3a226` / `docs/STATUS-ember-talent-cost-copy-approve.md` 施工 B2.17 **批准 A**。先确认当前 `main` / `origin/main` 已包含 `3a3a226`，再只改一个文件：`plugins/TrMenu/menus/ember_talent.yml`。仅替换以下 13 个玩家可见类型行，不改任何逻辑：

1. `§7根节点 · 被动 · cost 2` → `§7根节点 · 被动 · 消耗 2`（×3）。
2. `§7主动技 · cost 4` → `§7主动技 · 消耗 4`（×1）。
3. `§7二层 · 被动 · cost N` → `§7二层 · 被动 · 消耗 N`（×9，N=3 或 4 原样）。

保留 2/3/4 数值、`+`/`-`/`·`、周边 YAML、图标键、`talent.yml` stats/键名/requires/cost、所有 `command: corerpg talent unlock <nodeId>` 实参、誓约逻辑、点数/等级门槛、洗点价及其它菜单。禁止附方案 B；禁止碰 `talent.yml`、unlock 实参、其它菜单或 runtime/player/world；禁止声称 B0.1、奖励页 `NI id:` 或套装 `gear_ember_*` 已清。

施工后验收：目标 13 条类型行的英文 `cost` 残留 `rg` = 0（`rg -n '· cost ' plugins/TrMenu/menus/ember_talent.yml` = 0）；确认 13 个「消耗」行及 2/3/4 数值不变；`git diff` 仅上述 13 行文案；按服约定 reload TrMenu 并回报施工 tip、reload 结果与静态检查。不要宣称 B0.1 已清。

### 余烬-策划 — priority false FYI

FYI（not sent）：B2.17 天赋菜单同句英词已批准 **A**。设计 tip `3a3a226` 已推至 `origin/main`；批准状态为 **已批 A · 待插件**。范围仅 `plugins/TrMenu/menus/ember_talent.yml` 的 13 条玩家可见类型行：根节点×3、主动技×1、二层×9 的 `cost`→`消耗`，数值 2/3/4 原样；不附方案 B，不改 `talent.yml`、unlock 实参、其它菜单或 runtime/player/world；不宣称 B0.1、奖励页 `NI id:` 或 `gear_ember_*` 已清。插件施工后再测目标行英文 `cost`=0。

## Blocker

None. This approval commit is documentation-only; plugin YAML construction and reload remain pending.
