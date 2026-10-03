# STATUS · B2.14 talent `nodeId` / 前置 copy approval A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:16 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A** |
| Design tip | `9e129cc` · `docs/design/design-ember-talent-nodeid-copy.md` (verified on `main` / `origin/main`) |
| Next | **余烬-插件 · priority true · 待插件施工** |
| Scope | `plugins/TrMenu/menus/ember_talent.yml` player-visible lore only: delete 13 `§8nodeId:` lines and humanize 7 `§8前置：` lines |
| Excluded | **方案 B / bare attribute keys**; `talent.yml`; node keys / requires / cost / stats; unlock `command:` args; other menus; runtime/player/world files; B0.1 |

## Conclusion

Approved **A** for B2.14. The plugin role may make only the approved player-visible copy changes in `plugins/TrMenu/menus/ember_talent.yml`. This approval commit is documentation-only; plugin construction and reload happen separately after tip `9e129cc` lands.

**Hard boundary:** do not alter `talent.yml`, node keys, `requires`, `cost`, `stats`, bare attribute keys such as `phys_damage`, unlock command arguments, covenant logic, point formulas, reset price, other menus, or any runtime/player/world file. **Do not do plan B. Do not claim B0.1 is cleared.** Do not make any YAML change in this approval commit.

## A scope — exact 20 changes

### A1 — delete 13 complete lines

Delete every complete lore line matching `§8nodeId: …` in `plugins/TrMenu/menus/ember_talent.yml` (13 lines total). Keep the node's logical id and every `command: corerpg talent unlock <nodeId>` argument unchanged.

### A2 — replace exactly 7 prerequisite lines

| # | Current player-visible line | Approved line |
|---:|---|---|
| P1 | `§8前置：blaze_crit1 + blaze_leech1` | `§8前置：燃眼 + 饮烬` |
| P2/P3 | `§8前置：blaze_ember2` (炽脉 / 再燃) | `§8前置：余烬脉` |
| P4/P5 | `§8前置：ash_veil2` | `§8前置：灰纱` |
| P6/P7 | `§8前置：warden_bulwark2` | `§8前置：叠壁` |

Preserve YAML structure, indentation, ordering, formatting, icon keys, all non-approved strings, and every logical/unlock value.

## Acceptance boundary

- `rg -n '§8nodeId:' plugins/TrMenu/menus/ember_talent.yml` returns zero.
- `rg -n '§8前置：.*(blaze_|ash_|warden_)' plugins/TrMenu/menus/ember_talent.yml` returns zero.
- The approved Chinese prerequisite lines appear with counts: `燃眼 + 饮烬` ×1, `余烬脉` ×2, `灰纱` ×2, `叠壁` ×2.
- `git diff -- plugins/TrMenu/menus/ember_talent.yml` contains only the 13 complete-line deletions and 7 exact prerequisite replacements; `talent.yml`, all unlock command arguments, and bare attribute keys are unchanged.
- No plan B, no cost/requires/stats/point-cap/level-gate/reset-price/covenant change, no other-menu change, and no runtime/player/world change. **Do not claim B0.1 cleared.**

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按批准 tip `9e129cc` / `docs/status/STATUS-ember-talent-nodeid-copy-approve.md` 施工 B2.14 方案 A。先确认当前 `main` / `origin/main` 已包含 `9e129cc`，再只改一个文件：`plugins/TrMenu/menus/ember_talent.yml`。

只做以下 20 处玩家可见文案变更：
1. 删除全部 13 条完整 `§8nodeId: …` lore 行（整行删除，不改对应节点逻辑 id）。
2. 精确替换 1 条：`§8前置：blaze_crit1 + blaze_leech1` → `§8前置：燃眼 + 饮烬`。
3. 精确替换 2 条：`§8前置：blaze_ember2` → `§8前置：余烬脉`（炽脉、再燃）。
4. 精确替换 2 条：`§8前置：ash_veil2` → `§8前置：灰纱`。
5. 精确替换 2 条：`§8前置：warden_bulwark2` → `§8前置：叠壁`。

禁止方案 B：不要改任何裸属性键（`phys_damage`、`crit_*`、`max_health` 等）或 `passive`/`skill`。也不要改 `talent.yml`、节点键、requires/cost/stats、`command: corerpg talent unlock <nodeId>` 实参、誓约逻辑、点数/等级门槛、洗点价、其它菜单或任何 runtime/player/world 文件；不要宣称 B0.1 已清。

施工后验收并回报施工 tip、reload 结果与静态检查：
- `rg -n '§8nodeId:' plugins/TrMenu/menus/ember_talent.yml` = 0；
- `rg -n '§8前置：.*(blaze_|ash_|warden_)' plugins/TrMenu/menus/ember_talent.yml` = 0；
- `rg -n '§8前置：燃眼 \+ 饮烬|§8前置：余烬脉|§8前置：灰纱|§8前置：叠壁' plugins/TrMenu/menus/ember_talent.yml` 命中 7 条；
- `git diff` 仅含上述 13 行删除 + 7 条前置替换；确认裸属性键、`talent.yml` 与 unlock args 零改。

### 余烬-策划 — priority false FYI

FYI（not sent）：B2.14 天赋菜单 §8 `nodeId` / 前置内部 id 文案已批准 **A**。设计 tip `9e129cc` 已推至 `origin/main`；当前状态 **已批 A · 待插件**。插件范围仅 `plugins/TrMenu/menus/ember_talent.yml`：删除 13×`§8nodeId:` 行，并将 7 条英前置替换为「燃眼 + 饮烬 / 余烬脉 / 灰纱 / 叠壁」。不做方案 B，不改裸属性键、`talent.yml`、unlock args、其它菜单或 runtime/player/world；插件施工后再做 rg 静态验收；**不宣称 B0.1 已清**。

## Blocker

None. This approval commit is documentation-only; plugin YAML construction, reload, and test remain pending.
