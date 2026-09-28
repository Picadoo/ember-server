# STATUS · B2.16 talent passive/skill type copy approval A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:37 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A** |
| Design tip | `34e61dd` · `docs/design-ember-talent-type-copy.md` (pushed and verified on `main` / `origin/main`) |
| Next | **余烬-插件 · priority true · 待插件施工** |
| Scope | Only `plugins/TrMenu/menus/ember_talent.yml`: 13 player-visible type lines; root passive×3 → 被动, active skill×1 → drop `skill`, second-layer passive×9 → 被动 |
| Excluded | `cost`→消耗 (方案 B); `talent.yml`; unlock `command:` args; cost values/requires/stats; other menus; all runtime/player/world files; B0.1 claims |

## Conclusion

Approved **A** for B2.16. Replace only the thirteen approved player-visible type-line strings in `plugins/TrMenu/menus/ember_talent.yml`. This approval changes documentation only; the plugin role performs the one-file YAML replacement and TrMenu reload separately.

**Hard boundary:** do not alter `talent.yml`, node keys, unlock command arguments, cost values, requires, stats, covenant logic, point formulas, reset price, other menus, or any runtime/player/world file. Do not execute方案 B: do not change any type-line `cost` to `消耗`. Do not claim B0.1, reward-page `NI id:`, or set `gear_ember_*` debt is cleared. Do not make any YAML change in this approval commit.

## A scope — exact 13 replacements

| # | Area | Current player-visible lore | Approved player-visible lore |
|---:|---|---|---|
| A1–A3 | 根节点 · 燃锋 / 灰踪 / 守碑 | `§7根节点 · passive · cost 2` | `§7根节点 · 被动 · cost 2` |
| A4 | 烬刃 · 烬斩 | `§7主动技 · skill · cost 4` | `§7主动技 · cost 4` |
| A5–A13 | 二层 · 烬刃 / 灰行 / 守墓 | `§7二层 · passive · cost N` | `§7二层 · 被动 · cost N`（N=3 或 4 原样） |

Preserve surrounding YAML, icon keys, formatting, numeric values, separators, every `command: corerpg talent unlock <nodeId>` argument, and every non-approved string. The thirteen rows above are the complete A scope. Keep every `cost 2/3/4` literal unchanged.

## Acceptance boundary

- `rg -n 'passive|skill' plugins/TrMenu/menus/ember_talent.yml` returns zero after construction.
- The three root lines read `根节点 · 被动 · cost 2`; the active line reads `主动技 · cost 4`; the nine second-layer lines read `二层 · 被动 · cost 3/4` with values unchanged.
- `cost` remains `cost` in all thirteen approved lines; no `消耗` is introduced. This is not方案 B.
- `git diff -- plugins/TrMenu/menus/ember_talent.yml` is exactly the thirteen player-visible type-line replacements.
- `talent.yml` stats/keys/requires/cost and all unlock command arguments are unchanged.
- No cost value, requires, stats, point cap, level gate, reset price, covenant logic, other menu, or runtime/player/world change.
- Do not mark PASS or claim B0.1, reward-page `NI id:`, or `gear_ember_*` residuals are cleared.

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按批准设计 tip `34e61dd` / `docs/STATUS-ember-talent-type-copy-approve.md` 施工 B2.16 **批准 A**。先确认当前 `main` / `origin/main` 已包含 `34e61dd`，再只改一个文件：`plugins/TrMenu/menus/ember_talent.yml`。仅替换以下 13 个玩家可见类型行，不改任何逻辑：

1. `§7根节点 · passive · cost 2` → `§7根节点 · 被动 · cost 2`（×3）
2. `§7主动技 · skill · cost 4` → `§7主动技 · cost 4`（×1）
3. `§7二层 · passive · cost N` → `§7二层 · 被动 · cost N`（×9，N=3 或 4 原样）

保留 `cost` 字面及 2/3/4 数值、`+`/`-`/`·`、周边 YAML、图标键、`talent.yml` stats/键名/requires/cost、所有 `command: corerpg talent unlock <nodeId>` 实参、誓约逻辑、点数/等级门槛、洗点价及其它菜单。明确禁止方案 B：不要把 `cost` 改为 `消耗`。禁止碰 runtime/player/world；禁止改其他文件；禁止声称 B0.1 已清。

施工后验收：`rg -n 'passive|skill' plugins/TrMenu/menus/ember_talent.yml` 返回 0；确认 13 个类型行、cost 数值及周边内容；`git diff` 仅上述 13 行文案；按服约定 reload TrMenu 并回报施工 tip、reload 结果与静态检查。不要宣称 B0.1、奖励页 `NI id:` 或套装 `gear_ember_*` 已清。

### 余烬-策划 — priority false FYI

FYI（not sent）：B2.16 天赋菜单类型英词已批准 **A**。设计 tip `34e61dd` 已推至 `origin/main`；批准状态为 **已批 A · 待插件**。范围仅 `plugins/TrMenu/menus/ember_talent.yml` 的 13 个玩家可见类型行：根节点 3 处 `passive`→被动、主动技 1 处去掉 `skill`、二层 9 处 `passive`→被动；`cost` 与数值原样。验收为 `rg -n 'passive|skill' plugins/TrMenu/menus/ember_talent.yml` = 0。方案 B 的 `cost`→`消耗`、`talent.yml`、unlock 实参、其它菜单、runtime/player/world 均不在本轮；不宣称 B0.1 已清。插件施工后再测。

## Blocker

None. This approval commit is documentation-only; plugin YAML construction and reload remain pending.
