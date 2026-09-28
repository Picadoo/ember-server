# STATUS · B-flex-2 可装配轻技 1 槽试点批准 A

| Field | Value |
|---|---|
| Date | 2026-09-29 01:03 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A**（不附 B） |
| Design tip | `d1e88e4` · `docs/design-ember-flex-skill-slot-pilot.md`（已推送并核验 `main` / `origin/main`） |
| Parent close | B-flex-1 `d333b01` |
| Next | **余烬-插件 · priority true**；待插件施工 |
| Scope | 1 槽可装配轻技 `flex_ember_step`「余烬踏步」；TrMenu 装配/卸下/释放；CD 14s；零体力 |
| Acceptance | 仅静态 `rg` + 菜单轻测 |
| Excluded | 誓约主动技改动；体力日/周门；四件甲；锻炉重做；T0–T3 刃/护符数值；B2.x 文案窗；B0.1 声称；wall-clock/DPS；挑刺口径 |

## Conclusion

批准 **A**：在现有誓约主动之外增加 **1 个**可装配轻技槽，仅上线位移试点 `flex_ember_step`「余烬踏步」。效果为朝看向方向短位移约 5 格，落地不穿墙；无伤害；CD 14s；**零体力消耗**。玩家通过 TrMenu 点击装配、卸下和释放，誓约主动仍保持原路径。不得把方案 B 保命技与 A 同窗上线。

本批准 tip 仅更新 docs，不改代码、玩法 YAML、NI、TrMenu 或 runtime/player/world。插件在设计 tip 落地后施工；NI 不是本设计的必需项，除非插件实现证明确有需要，否则不派物品岗。

## A scope

| Area | Approved A | Not approved |
|---|---|---|
| Skill | `flex_ember_step`；看向方向约 5 格短位移；失败原地并短提示；无伤害 | Covenant 三主动变更；保命 B1；位移+保命双上；DPS/数值扩展 |
| Slot | 每玩家 1 槽 `flex_skill_id`，空值 `none` | 多槽、连招、多技能栏 |
| UX | TrMenu 装配、卸下、释放；短清楚 lore | 玩家路径教手打 `/corerpg`；厚菜单树 |
| Cost | CD 14s；**零体力** | 体力日/周门、进本 cost、技能耗体力 |
| Test | 静态 `rg` + 菜单轻测 | wall-clock、DPS、长本、挑刺口径 |

## Hard boundary

- 不改誓约三主动的 id、效果、数值或 CD。
- 不动体力日/周门、进本 cost、四件甲、锻炉重做、T0–T3 刃/护符数值。
- 不捆 B2.x 文案窗；**不宣称 B0.1 已清**。
- 烬砧材料→部件只作 soft hang，后续再做。
- 不触碰 runtime/player/world 文件；本 commit 不含代码/YAML/NI/TrMenu 施工。

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按已推送设计 tip `d1e88e4` / `docs/design-ember-flex-skill-slot-pilot.md` 与本批准 STATUS 施工 B-flex-2 **APPROVED A**。只实现 1 槽 `flex_skill_id`（空值 `none`）和 `flex_ember_step`「余烬踏步」：朝玩家看向方向短位移约 5 格，落地不穿墙，失败原地并短提示；无伤害；CD 14s；**零体力**。在 TrMenu 提供点击装配、卸下、释放，玩家 lore 不教手打 `/corerpg`；誓约三主动继续走原路径。验收只做静态 `rg` + 菜单轻测。

硬禁：不要改誓约主动技能或数值、体力日周门/进本 cost、四件甲、锻炉、T0–T3 刃护符、B2.x 文案窗；不要同时上线保命 B1；不要声称 B0.1 已清；不要做 wall-clock/DPS 或挑刺测试。烬砧材料→部件是后续 soft hang。NI 不是必需项；仅在设计明确需要时提出最小配套，否则不要改 NI。不要触碰 runtime/player/world 文件；完成后只回报施工 tip、静态检查结果和菜单轻测准备，不要自行发送消息。

### 余烬-策划 — priority false FYI

FYI（not sent）：B-flex-2 可装配轻技 1 槽试点已批准 **A**（不附 B），设计 tip `d1e88e4` 已推至 `origin/main`；状态为 **已批 A · 待插件施工**。范围仅 `flex_ember_step`「余烬踏步」：1 槽、TrMenu 装配/卸下/释放、看向方向约 5 格短位移、CD 14s、零体力。验收仅静态 `rg` + 菜单轻测。誓约主动、体力门、四件甲、锻炉、T0–T3 刃/护符、B2.x、B0.1 声称、wall-clock/DPS、挑刺均禁止；烬砧材料→部件为后续 soft hang。NI 当前不需要；插件施工后再交测试。

### 余烬-物品 — N/A

NI 不是 APPROVED A 的必需项；若插件后续证明需要 NI，另行提出最小需求，不在本窗口默认施工。

**总控会 SendToAgent；本次不发送。**

## Blocker

None. Design tip is pushed and verified; documentation approval is ready. Plugin implementation and the static/menu acceptance remain pending.
