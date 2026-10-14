# RESEARCH · SidestepPath.offerPick / applyAndReply「闪冲路径选打」菜单化 · 拟 D13347 · TIP-DP

> **状态：** **CLOSED live** · feat `adf9efa71` · tip `116eea312` · jar `1.65.840-d13347.local` · PASS `2fc016f96` · 批 sha `116eea312` · menu tip `52e6da32b` · 证据 `/workspace/tmp/tip-dp-sidestep-path/` · 2026-10-14 14:22 Asia/Shanghai · 闪冲路径选打菜单化同题勿再交
> **起因：** 总控【派下一窗 tip · ancestor TIP-DO CLOSED】。祖先 TIP-DO CLOSED live `439b37f46` · feat `bb9accd40` · jar `1.65.839-d13346.local` · PASS `65ef1d315` · menu `a8c288f57` · APPROVED `057b6b380`。DESIGN-DO §6⑦ 明文后置闪冲 SidestepPath 等 *Path.offerPick 逐支另窗。选题闸：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径）③若无新硬债：只钉 **SidestepPath 闪冲选打**（或实扫仍残留的一支 *Path.offerPick）· 镜像 DE–DO。**禁：** 半行独批 · 已齐 settle/选打同题复读 · 抬伤害/CD表/掉落/体力价 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫 · PacePath 等剩支后置 · legacy evacuate 后置 · L13 / 灰烬/R / 抬 AFK 表 / Affix 加密 / menu-honesty/observe idle / P兼确认 / 第五庭花 / Cast particle 主交付 / disaster reopen / reopen D423。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DO CLOSED live `439b37f46` · feat `bb9accd40` · tip `057b6b380` · PASS `65ef1d315` · jar `1.65.839-d13346.local` · 批 sha `057b6b380` · menu tip `a8c288f57` · D13346

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`（仅 enhance/upgrade/refine/brand/convert/roll gap）；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–DO | **跳过** | CW–DD settle + TIP-DE–DO 选打 **全齐勿复读** |
| ④ PacePath | **后置** | 本仓无 EmberPacePath；派单明文后置 |
| ⑤ 其他仍 ConfirmTokens 的 *Path（Wave/Thaw/Soak…） | **后置** | 派单钉 Sidestep；禁全族大扫；逐支另窗 |
| ⑥ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **SidestepPath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DO §6⑦；ConfirmTokens 全链；**无**选打专用薄页；局内闪冲钮≠选打页；镜像 TIP-DE–DO；≠ 身法选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 落 DESIGN-DO §6⑦ 后置桶 · 实扫 SidestepPath 仍教裸指令且无薄页 → **钉 = SidestepPath**（周要约/`scheduleOfferAfterQ01`/无参/`offerPick` 全链；`applyAndReply` 下一步 ConfirmTokens 教裸 `sidesteppath`/`p1`；glance 未选教手打；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_sidestep_path`）。**局内冲锋条带「闪冲」战斗钮**（ASK 路径提醒）**存在但不消解选打硬债**——选打链仍 ConfirmTokens。Pace 不存在后置。故交本支；**证据表见 §2**；**为何不是半行见 E13**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DO CLOSED · 派下一窗：闪冲逐支；只钉一支 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–DO 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DO §6⑦ 明文后置闪冲逐支 | DESIGN-DO §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 sidesteppath auto\|ask\|mute\|clear` | EmberSidestepPath.offerPick L149–157 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ01` → `offerPick` | EmberSidestepPath L160–180 | **周要约/Q01 入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→`/corerpg p1` / `sidesteppath` | EmberSidestepPath.applyAndReply L212–214 | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `auto\|ask\|mute\|clear`；glance 未选仍教手打 | EmberCommand L5320–5345 · glance L89 | **无参入口无开薄页** |
| E10 | TrMenu **无** `ember_p1_sidestep_path`（已有 step/flex/stride/counter/…_path） | menus/ | **缺选打专用薄页** |
| E11 | 局内 `maybeAfterCharge`/`maybeProbe` 有「闪冲」+「改路径」ConfirmTokens | EmberSidestepPath L122–146 | **局内战斗钮≠选打薄页；选打硬债仍成立** |
| E12 | 本仓无 EmberPacePath | Java | **后置（§6）** |
| E13 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E14 | ≠ 身法选打同题；≠ 轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态选打同题；≠ 全族 Path 大扫；≠ 复开 D572 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `sidesteppath` + 周要约/Q01 + `applyAndReply` 下一步→开 `/ember_p1_sidestep_path`；清 ConfirmTokens 教指令为主 UI；可选 `sidestep_path_*` PAPI；单测；局内闪冲战斗钮语义零改
2. **菜单：** 新薄页 `ember_p1_sidestep_path`：自动/提醒/静默/清空/技能组/冒险/可选工坊；lore 挂路径；TrMenu 点击 + `corerpg p1 from/back`
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 闪冲规则语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** step_path / flex_path / stride_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath / Cast / Aid / variety / 庭内 / soft-reward；**Pace / 其他 *Path / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/身法选打同题/全族Path大扫/Pace同窗/legacy evacuate 大扫/复开 D572 | 硬禁 |

---

*RESEARCH · SidestepPath.offerPick / applyAndReply 闪冲路径选打菜单化 · **CLOSED live** · feat `adf9efa71` · PASS `2fc016f96` · tip `116eea312` · menu `52e6da32b` · jar `1.65.840-d13347.local` · TIP-DP / D13347 · 半行 REJECT `d00ed84bc` 保持 · PacePath等剩支/挂机若新硬债/legacy evacuate 后置 · 2026-10-14 14:23 Asia/Shanghai.**已批 A · 方案 M** · APPROVED A·M · TIP-DP / D13347 · 半行 REJECT `d00ed84bc` 保持 · PacePath后置 · ancestor TIP-DO CLOSED `439b37f46` · 2026-10-14 14:08 Asia/Shanghai.*

## STATUS: **CLOSED live** · feat `adf9efa71` · tip `116eea312` · PASS `2fc016f96` · jar `1.65.840-d13347.local` · menu `52e6da32b` · D13347 · TIP-DP · 2026-10-14 14:23 Asia/Shanghai · 闪冲路径选打菜单化同题勿再交
