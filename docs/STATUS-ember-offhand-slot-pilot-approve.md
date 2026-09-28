# STATUS · B-flex-1 offhand slot pilot approval A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:50 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B） |
| Design tip | `7dda194` · `docs/design-ember-offhand-slot-pilot.md`（已推送并核验 `main` / `origin/main`） |
| Next | **余烬-物品 · priority true** + **余烬-插件 · priority true** · 待物品+插件施工 |
| Scope | 独立 Off Hand 白名单 + 两件 NI 试点 + hub/set 薄展示 + `StatService` OffHand 钩子 |
| Acceptance | 仅静态 `rg` + 菜单打开目视 |
| Excluded | 全套四件甲；技能重做；锻炉重做；与 B2.17 捆绑；B0.1 声称；wall-clock/DPS 测试 |

## Conclusion

Approved **A** for B-flex-1. The thin window is: an independent Off Hand whitelist, NI pilots `acc_ember_offhand_ward` (`phys_defense +2`) and `acc_ember_offhand_vita` (`max_health +8`), a thin hub/set display, and the `StatService` OffHand hook. The two pilot ids stay out of the existing charm/talisman `accessories` best-one logic.

This approval is documentation-only. No YAML or code is changed in this commit; NI and plugin implementation happen after this tip lands. The acceptance boundary is static `rg` plus opening the relevant menu for visual confirmation only. Do not run wall-clock or DPS tests.

**Hard boundary:** do not add a full armor set, rework skills, rework the forge, tie this window to B2.17, or claim B0.1 is cleared. Soft hang only (not this window): equippable skill and forge material→parts. Do not touch runtime/player/world files.

## A scope

| Area | Approved A | Not approved |
|---|---|---|
| NI | `acc_ember_offhand_ward` · `物理防御: +2`; `acc_ember_offhand_vita` · `生命力: +8` / `max_health +8` | Other gear tiers, existing T0–T3 blade/charm/talisman values |
| Stat | New independent Off Hand whitelist; `StatService` reads the Off Hand item and applies only a whitelisted pilot | Reworking charm best-one selection; adding a DPS/physical-damage axis |
| UI | Thin `ember_hub` or `ember_set` line: `副手饰品 · 放副手槽生效` | Thick new menu tree or command teaching |
| Test | Static rg and menu-open visual only | Wall-clock, DPS blind tuning, long tests |

## Acceptance boundary

- `rg` finds both `acc_ember_offhand_ward` and `acc_ember_offhand_vita` in the NI items area.
- `rg` finds the independent `offhand` whitelist and the `StatService` OffHand branch/hook.
- `rg` finds the thin 「副手」 display in `ember_hub` or `ember_set`; opening that menu confirms the line visually.
- Existing T0–T3 blade/charm/talisman values, daily/weekly stamina gates, forge recipes, and player/world/runtime data are untouched.
- No full armor set, skill rework, forge rework, B2.17 coupling, B0.1 claim, wall-clock test, or DPS test.

## Handoff drafts (not sent)

### 余烬-物品 — priority true

请按已推送设计 tip `7dda194` / `docs/STATUS-ember-offhand-slot-pilot-approve.md` 施工 B-flex-1 **批准 A**。先确认当前 `main` / `origin/main` 已包含 `7dda194`，再只创建设计指定的两件 NI 试点：`acc_ember_offhand_ward`（显示名「余烬守腕」，lore `物理防御: +2`）与 `acc_ember_offhand_vita`（显示名「余烬生坠」，lore `生命力: +8`）。不要创建其它装备 tier、四件甲或额外副手样例；不要改既有 T0–T3 刃/护符/饰品数值、日周体力门、锻炉配方或 runtime/player/world 文件。两件物品供插件的独立 Off Hand 白名单使用；完成后只回报施工 tip 与静态 id/lore 检查，不宣称 B0.1 已清。

### 余烬-插件 — priority true

请按已推送设计 tip `7dda194` / `docs/STATUS-ember-offhand-slot-pilot-approve.md` 施工 B-flex-1 **批准 A**。在 NI ids 已落地后或与 NI ids 同步，加入独立 `offhand` 白名单，令 `StatService` 读取 `getItemInOffHand()` 并仅对 `acc_ember_offhand_ward` / `acc_ember_offhand_vita` 应用对应 lore 属性；不要把它们并入既有护符 `accessories` best-one 逻辑。并在既有 `ember_hub` 或 `ember_set` 做一行薄展示「副手饰品 · 放副手槽生效」，不新开厚菜单、不教手打命令。

硬禁：全套四件甲、技能重做、锻炉重做、与 B2.17 捆绑、改既有 T0–T3 刃护符数值、改日周体力门、声称 B0.1 已清、wall-clock/DPS 测试；技能可装配与烬砧材料→部件为 soft hang，不施工。不要触碰 runtime/player/world。完成后仅回报施工 tip、静态 `rg` 结果和菜单打开目视准备；不要自行发送消息。

### 余烬-策划 — priority false FYI

FYI（not sent）：B-flex-1 副手/饰品位试点已批准 **A**，设计 tip `7dda194` 已推至 `origin/main`；状态为 **已批 A · 待物品+插件施工**。范围仅独立 Off Hand 白名单、`acc_ember_offhand_ward`（物防 +2）、`acc_ember_offhand_vita`（生命 +8）、hub/set 薄展示和 `StatService` OffHand 钩子；不附 B，不与 B2.17 捆绑。全套甲、技能/锻炉重做、B0.1 声称、wall-clock/DPS 测试均禁止；技能可装配和烬砧材料→部件为 soft hang。施工后测试仅静态 rg + 菜单打开目视。

**总控会 SendToAgent；本次不发送。测试稍后仅 static-only。**

## Blocker

None. The approval and design docs are ready; NI and plugin construction remain pending.
