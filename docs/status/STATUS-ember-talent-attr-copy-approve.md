# STATUS · B2.15 talent bare-attr copy approval A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:27 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A** |
| Design tip | `26d7895` · `docs/design/design-ember-talent-attr-copy.md` (pushed and verified on `main` / `origin/main`) |
| Next | **余烬-插件 · priority true · 待插件施工** |
| Scope | Only `plugins/TrMenu/menus/ember_talent.yml`: 9 player-visible bare-attribute lore lines → 物攻 / 暴伤 / 暴击率 / 移速 / 攻速 / 击中减速 / 受伤 / 生命 / 物防 |
| Excluded | `talent.yml` stats/keys/requires/cost; unlock `command:` args; passive/skill type words; other menus; all runtime/player/world files; B0.1 claims |

## Conclusion

Approved **A** for B2.15. Replace only the nine approved player-visible lore lines in `plugins/TrMenu/menus/ember_talent.yml`. This approval changes documentation only; the plugin role performs the one-file YAML replacement and TrMenu reload separately.

**Hard boundary:** do not alter `talent.yml`, node keys, unlock command arguments, cost, requires, stats, covenant logic, point formulas, reset price, passive/skill type words, other menus, or any runtime/player/world file. Do not claim B0.1, `passive`/`skill`, reward-page `NI id:`, or set `gear_ember_*` debt is cleared. Do not make any YAML change in this approval commit.

## A scope — exact 9 replacements

| # | Area | Current player-visible lore | Approved player-visible lore |
|---:|---|---|---|
| A1 | 烬刃 · 余烬脉 | `§7phys_damage +1` | `§7物攻 +1` |
| A2 | 烬刃 · 炽脉 | `§7crit_damage_pct +0.03` | `§7暴伤 +0.03` |
| A3 | 烬刃 · 再燃 | `§7crit_chance_pct +0.005 · phys_damage +1` | `§7暴击率 +0.005 · 物攻 +1` |
| A4 | 灰行 · 灰纱 | `§7move_speed_pct +0.01` | `§7移速 +0.01` |
| A5 | 灰行 · 细尘 | `§7attack_speed_pct +0.01` | `§7攻速 +0.01` |
| A6 | 灰行 · 掩息 | `§7on_hit_slow_pct +0.03 · attack_speed_pct +0.01` | `§7击中减速 +0.03 · 攻速 +0.01` |
| A7 | 守墓 · 叠壁 | `§7damage_taken_pct -0.01` | `§7受伤 -0.01` |
| A8 | 守墓 · 护冢 | `§7max_health +2` | `§7生命 +2` |
| A9 | 守墓 · 誓碑 | `§7phys_defense +1 · damage_taken_pct -0.01` | `§7物防 +1 · 受伤 -0.01` |

Preserve surrounding YAML, icon keys, formatting, numeric values, separators, every `command: corerpg talent unlock <nodeId>` argument, and every non-approved string. The nine rows above are the complete A scope.

## Acceptance boundary

- The following nine-key `rg` check returns zero after construction:
  `rg -n 'phys_damage|crit_damage_pct|crit_chance_pct|move_speed_pct|attack_speed_pct|on_hit_slow_pct|damage_taken_pct|max_health|phys_defense' plugins/TrMenu/menus/ember_talent.yml`
- The nine approved Chinese labels and original values/separators appear exactly as listed above.
- `git diff -- plugins/TrMenu/menus/ember_talent.yml` is exactly the nine player-visible lore replacements.
- `talent.yml` stats/keys/requires/cost and all unlock command arguments are unchanged.
- `passive` / `skill` type words remain unchanged; no plan B cleanup is claimed.
- No cost, requires, stats, point cap, level gate, reset price, covenant logic, other menu, or runtime/player/world change.
- Do not mark PASS or claim B0.1, `passive`/`skill`, reward-page NI id, or `gear_ember_*` residuals are cleared.

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按批准设计 tip `26d7895` / `docs/status/STATUS-ember-talent-attr-copy-approve.md` 施工 B2.15 **批准 A**。先确认当前 `main` / `origin/main` 已包含 `26d7895`，再只改一个文件：`plugins/TrMenu/menus/ember_talent.yml`。仅替换以下 9 个玩家可见 lore 字面，不改任何逻辑：

1. `§7phys_damage +1` → `§7物攻 +1`
2. `§7crit_damage_pct +0.03` → `§7暴伤 +0.03`
3. `§7crit_chance_pct +0.005 · phys_damage +1` → `§7暴击率 +0.005 · 物攻 +1`
4. `§7move_speed_pct +0.01` → `§7移速 +0.01`
5. `§7attack_speed_pct +0.01` → `§7攻速 +0.01`
6. `§7on_hit_slow_pct +0.03 · attack_speed_pct +0.01` → `§7击中减速 +0.03 · 攻速 +0.01`
7. `§7damage_taken_pct -0.01` → `§7受伤 -0.01`
8. `§7max_health +2` → `§7生命 +2`
9. `§7phys_defense +1 · damage_taken_pct -0.01` → `§7物防 +1 · 受伤 -0.01`

保留数值、`+`/`-`/`·`、周边 YAML、图标键、`talent.yml` stats/键名/requires/cost、所有 `command: corerpg talent unlock <nodeId>` 实参、誓约逻辑、点数/等级门槛、洗点价及其它菜单。禁止方案 B：不要改 `passive`/`skill` 类型词。禁止碰 runtime/player/world；禁止改其他文件。

施工后验收：上述 9-key `rg` 命令在 `plugins/TrMenu/menus/ember_talent.yml` 返回 0 行；确认 9 个中文效果行与原数值/分隔符；`git diff` 仅上述 9 行文案；按服约定 reload TrMenu 并回报施工 tip、reload 结果与静态检查。不要宣称 B0.1 或任何范围外残余债已清。

### 余烬-策划 — priority false FYI

FYI（not sent）：B2.15 天赋菜单裸属性键文案已批准 **A**。设计 tip `26d7895` 已推至 `origin/main`；批准状态为 **已批 A · 待插件**。范围仅 `plugins/TrMenu/menus/ember_talent.yml` 的 9 个玩家可见 lore 替换：`phys_damage`→物攻、`crit_damage_pct`→暴伤、`crit_chance_pct`→暴击率、`move_speed_pct`→移速、`attack_speed_pct`→攻速、`on_hit_slow_pct`→击中减速、`damage_taken_pct`→受伤、`max_health`→生命、`phys_defense`→物防；验收为上述 9-key `rg` = 0。`passive`/`skill`、`talent.yml` stats/逻辑、unlock 实参、其它菜单、runtime/player/world 均不在本轮；不宣称 B0.1 已清。插件施工后再测。

## Blocker

None. This approval commit is documentation-only; plugin YAML construction and reload remain pending.
