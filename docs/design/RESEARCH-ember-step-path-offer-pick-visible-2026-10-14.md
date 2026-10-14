# RESEARCH · StepPath.offerPick / applyAndReply「身法路径选打」菜单化 · 拟 D13346 · TIP-DO

> **状态：** **CLOSED live** · feat `bb9accd40` · tip `057b6b380` · PASS `65ef1d315` · jar `1.65.839-d13346.local` · 批 sha `057b6b380` · TIP-DO · 2026-10-14 14:01 Asia/Shanghai · 身法路径选打菜单化同题勿再交 · 下一窗：闪冲 SidestepPath 等 *Path.offerPick 逐支（禁全族大扫）/ PacePath 等剩支后置 / 挂机若新硬债 / legacy evacuate 后置；身法路径选打本支已齐
> **起因：** 总控【派下一窗 tip · ancestor TIP-DN CLOSED】。祖先 TIP-DN CLOSED live `300697d79` · feat `53be7080e` · jar `1.65.838-d13345.local` · PASS `89c68de74` · menu `55f8f3ea0` · APPROVED `68230fe44`。DESIGN-DN §6⑦ 明文后置身法 StepPath / 闪冲 SidestepPath 等 *Path.offerPick 逐支另窗。选题闸：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径）③若无新硬债：只钉 **一支**（StepPath 身法 或 SidestepPath 闪冲，以实扫 ConfirmTokens 教裸指令证据择最硬）· 镜像 DE–DN。**禁：** 半行独批 · 已齐 settle/选打同题复读 · 抬伤害/CD表/掉落/体力价 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫 · PacePath 等剩支后置 · legacy evacuate 后置。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DN CLOSED live `300697d79` · feat `53be7080e` · tip `68230fe44` · PASS `89c68de74` · jar `1.65.838-d13345.local` · 批 sha `68230fe44` · menu tip `55f8f3ea0` · D13345

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`（仅 enhance/upgrade/refine/brand/convert/roll gap）；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–DN | **跳过** | CW–DD settle + TIP-DE–DN 选打 **全齐勿复读** |
| ④ PacePath | **后置** | 本仓无 EmberPacePath；派单明文后置 |
| ⑤ SidestepPath 闪冲 | **后置本窗** | 仍 ConfirmTokens+无薄页，但证据硬度次于 Step（见 E14）；DESIGN-DN §6⑦ 逐支另窗；本窗只钉一支 |
| ⑥ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **StepPath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DN §6⑦ 首位；ConfirmTokens 全链+交叉教 enter；**无**选打专用薄页；镜像 TIP-DE–DN；≠ 轻技选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 落 DESIGN-DN §6⑦ 后置桶 · 实扫 Step/Sidestep 两支均教裸指令且无薄页 → **择最硬 = StepPath**（DESIGN-DN §6⑦ 首位；周要约/`scheduleOfferAfterQ05`/无参/`offerPick` 全链；`applyAndReply` 下一步 ConfirmTokens 教裸 `enter`/`steppath`；glance 未选教手打；用法教 `forward|back|clear`；TrMenu **无** `ember_p1_step_path`；纯选打债、无局内旁路 UX）。Sidestep（Q01·另有局内闪冲钮，但选打链同类）后置。故交本支；**证据表见 §2**；**为何不是半行见 E12**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；闪冲后置）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DN CLOSED · 派下一窗：身法/闪冲逐支；只钉一支 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–DN 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DN §6⑦ 明文后置身法/闪冲逐支 | DESIGN-DN §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 三钮 → `/corerpg p1 steppath forward\|back\|clear` | EmberStepPath.offerPick L93–100 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ05` → `offerPick` | EmberStepPath L103–125 | **周要约/Q05 入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→`enter q05\|q02` / `trmenu open ember_skill_kit` / `steppath` | EmberStepPath.applyAndReply L160–163 | **选后下一步仍聊天钮（交叉教 enter）** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `forward\|back\|clear`；glance 未选仍教手打 | EmberCommand L3714–3734 · glance L77 | **无参入口无开薄页** |
| E10 | TrMenu **无** `ember_p1_step_path`（亦无 `ember_p1_sidestep_path`） | menus/ | **缺选打专用薄页** |
| E11 | SidestepPath 仍 ConfirmTokens；本仓无 EmberPacePath | Java · menus | **后置本窗（§6）** |
| E12 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E13 | ≠ 轻技选打同题；≠ 破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态选打同题；≠ 全族 Path 大扫；≠ 复开 D491 | 本 RESEARCH | 边界 |
| E14 | 为何钉 Step 为最硬 | DESIGN-DN §6⑦ 首位；纯选打债无局内旁路；全链 ConfirmTokens+教 enter；缺专用选打薄页；Sidestep 有局内闪冲钮·选打链同类后置；见 §1 择优 | 最硬一支 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `steppath` + 周要约/Q05 + `applyAndReply` 下一步→开 `/ember_p1_step_path`；清 ConfirmTokens 教指令为主 UI；可选 `step_path_*` PAPI；单测
2. **菜单：** 新薄页 `ember_p1_step_path`：前冲/后撤/清空/进本练身法/技能组/冒险/可选工坊；lore 挂路径；TrMenu 点击 + `corerpg p1 from/back`
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 身法规则语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** flex_path / stride_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath / Cast / Aid / variety / 庭内 / soft-reward；**闪冲 / Pace / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/轻技选打同题/全族Path大扫/闪冲同窗/legacy evacuate 大扫/复开 D491 | 硬禁 |

---

*RESEARCH · StepPath.offerPick / applyAndReply 身法路径选打菜单化 · **CLOSED live** · `bb9accd40` / `057b6b380` · PASS `65ef1d315` · TIP-DO / D13346 · 半行 REJECT `d00ed84bc` 保持 · 身法路径选打菜单化同题勿再交 · 下一窗：闪冲 SidestepPath 等 *Path.offerPick 逐支（禁全族大扫）/ PacePath 等剩支后置 / 挂机若新硬债 / legacy evacuate 后置；身法路径选打本支已齐.*

## STATUS: **CLOSED live** · `bb9accd40` / `057b6b380` · PASS `65ef1d315` · D13346 · TIP-DO · 2026-10-14 14:01 Asia/Shanghai
