# STATUS · B2.12 calamity OP gate copy approval A

| Field | Value |
|---|---|
| Date | 2026-09-28 23:57 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A** |
| Design tip | `1e109b3` · `docs/design/design-ember-calamity-op-gate-copy.md` (verified on `main` / `origin/main`) |
| Next | **余烬-插件 · priority true · 待插件施工** |
| Scope | `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` L17 `message=` one-string replacement only |
| Excluded | `text=` / team condition / 人数门 / loot / MM / TrMenu / `calamity.yml` / lowercase stub `ember_calamity` |

## Conclusion

Approved **A** for B2.12. Replace only the C6 player-visible `message=` string in `EmberCalamity/option.yml`. This approval changes documentation only; the plugin role performs the one-line YAML replacement and reload separately.

**Hard boundary:** do not alter `text=` or the team/OP condition, 人数门, loot, MM, TrMenu, `calamity.yml`, or lowercase stub `ember_calamity`. Do not claim B0.1 or any other residual debt is cleared. Do not make any other YAML change in this approval commit.

## A scope — exact C6 replacement

| # | File | Location | Current player-visible string | Approved player-visible string |
|---:|---|---|---|---|
| C6 | `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` | L17 `message=` | `§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：/ember → 灾厄` | `§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：枢纽菜单 → 灾厄` |

Preserve the surrounding YAML, `text=` expression, team/OP condition, formatting, and all non-string values. Only the exact `message=` value above may change.

## Acceptance boundary

- `rg -n '/ember' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` returns zero after construction.
- `rg -n '枢纽菜单 → 灾厄' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` finds the approved message.
- `git diff -- plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` is one player-visible string replacement only; `text=` / team condition, 人数门, loot, MM, TrMenu, `calamity.yml`, and lowercase stub are unchanged.
- Non-OP remains rejected; formal calamity still goes through the public hub menu.
- Do not mark PASS or claim B0.1, other ticket debt, or any other residual is cleared.

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按批准 tip `1e109b3` / `docs/status/STATUS-ember-calamity-op-gate-copy-approve.md` 施工 B2.12 方案 A。先确认当前 `main` 已包含 `1e109b3`，再只改一个文件、一个玩家可见字符串：

1. 文件：`plugins/DungeonPlus/dungeon/EmberCalamity/option.yml`，L17 `message=`。
2. 精确替换：
   `§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：/ember → 灾厄`
   →
   `§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：枢纽菜单 → 灾厄`
3. 保留 `text=` / `%player_is_op%`（team/OP）条件、人数门、loot、MM、TrMenu、`calamity.yml`、格式及其余所有值；不要改小写 stub `ember_calamity`，不要碰其它 YAML。
4. 按服约定 reload DungeonPlus/地牢配置；回报施工 tip、reload 结果与静态检查。

验收：
- `rg -n '/ember' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` 返回 0 行；
- `rg -n '枢纽菜单 → 灾厄' plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` 命中该 `message=`；
- `git diff` 仅这一条 `message=` 字符串替换，`text=` / team condition、人数门、loot、MM、TrMenu、`calamity.yml`、lowercase stub 均零改；
- 非 OP 仍被拒，正式灾厄仍走枢纽菜单 → 灾厄。

禁项：不改 `text=` / team condition，不开放非 OP，不改人数/loot/MM/TrMenu/`calamity.yml`，不改小写 `ember_calamity` stub，不新增斜杠教学，不宣称 B0.1 或其它残余债已清，不在本施工中顺手改其它配置。

### 余烬-策划 — priority false FYI

FYI（not sent）：B2.12 灾厄 OP 调试拒门文案已批准 **A**。设计 tip `1e109b3` 已推至 `origin/main`；范围仅 `plugins/DungeonPlus/dungeon/EmberCalamity/option.yml` L17 `message=`：`/ember → 灾厄` → `枢纽菜单 → 灾厄`。当前状态 **已批 A · 待插件**，插件施工后再测试；不改 `text=` / team condition、人数、loot、MM、TrMenu、`calamity.yml` 或小写 stub，也不宣称其它残余债已清。
