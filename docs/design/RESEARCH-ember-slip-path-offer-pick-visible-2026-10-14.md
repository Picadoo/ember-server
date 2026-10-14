# RESEARCH · SlipPath.offerPick / applyAndReply「闪招路径选打」菜单化 · 拟 D13368 · TIP-EK

> **状态：** **CLOSED live** · feat `606eea48f` · tip `5119931a6` · menu `dbd986adb` · jar `1.65.861-d13368.local` · PASS `e7648a86e` · 下一窗 BaitPath.offerPick · 2026-10-14 20:39 Asia/Shanghai
> **起因：** 总控【下一窗 TIP-EK · 只钉一支 · 禁全族大扫】。祖先 TIP-EJ **CLOSED live** `9a81d121a` · feat `09343d2bd` · tip `77c64770c` · menu `aa8a16fda` · jar `1.65.860-d13367.local` · PASS `30deaf257` · 证据 `/workspace/tmp/tip-ej-crash-path/`。**撞墙路径选打菜单化同题勿再交**。选题钉死：**SlipPath.offerPick**「闪招路径选打」菜单化 → TrMenu `ember_p1_slip_path`（拟 D13368 · TIP-EK · 玩法本体 D591 紧接 Crash D590）。硬证：`EmberSlipPath.offerPick` 仍 ConfirmTokens 教 slippath auto|ask|mute|clear；无参/周要约同入口；applyAndReply 下一步仍 ConfirmTokens；glance 教手打；局内闪招「闪招」+「改路径」≠选打薄页；TrMenu **无** `ember_p1_slip_path.yml`；与刚 CLOSED Crash 同构 auto/ask/mute。**禁：** 挂机 gem gap 未亮/短征已齐 → 勿交 gem/sx20；DE–EJ 选打勿复；半行 REJECT `d00ed84bc` 保持；禁全族 *Path.offerPick 大扫；禁与其它 *Path 同窗；PacePath（本仓无）后置；legacy evacuate 后置；其它剩支 D592+ 后置（逐支另窗）；零抬伤害/CD表/掉落/体力价；零改 D591 闪招语义本体；零改已齐 `ember_p1_crash_path` 等；勿勾 CLOSED。
> **上游：** TIP-EJ CLOSED live `9a81d121a` · feat `09343d2bd` · tip `77c64770c` · PASS `30deaf257` · jar `1.65.860-d13367.local` · 批 sha `77c64770c` · menu tip `aa8a16fda` · D13367

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk **零** gem gap；总控 gem gap 未亮 → 勿交 gem |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–EJ | **跳过** | CW–DD settle + TIP-DE–EJ 选打（含铁壁）**全齐勿复读** |
| ④ PacePath | **后置** | 本仓无 EmberPacePath；派单明文后置 |
| ⑤ 其它剩支 D592+ 及其他仍 ConfirmTokens 的 *Path | **后置** | 禁全族大扫；本窗只钉一支；逐支另窗 |
| ⑥ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **SlipPath.offerPick / applyAndReply（荐）** | **采纳** | 总控钉死；ConfirmTokens 全链；**无**选打专用薄页；局内闪招钮≠选打页；镜像 TIP-DE–EJ；≠ 撞墙选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 总控钉死 Slip（D591 紧接 Crash D590）→ 实扫残留 *Path.offerPick 仍教裸指令且无薄页者：Slip 与 Crash 同构 → **钉 SlipPath**（offerPick ConfirmTokens→slippath auto|ask|mute|clear；applyAndReply 下一步 ConfirmTokens 教裸 `slippath`/`p1`；glance 未选教手打；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_slip_path`）。**局内闪招预警「闪招」+「改路径」战斗钮**（ASK 路径提醒）**存在但不消解选打硬债**——选打链仍 ConfirmTokens。其它剩支 D592+ / PacePath（本仓无）后置。故交本支；**证据表见 §2**；**为何不是半行见 E13**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；总控选题已钉死）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-EJ CLOSED · 派下一窗：只钉 SlipPath；禁全族大扫 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–EJ 选打 **已齐** | tips | **禁止同题复读** |
| E5 | 总控钉死 Slip；D591 紧接 Crash D590；本仓无 PacePath；其它剩支 D592+ 后置 | 派单 · TIP-EJ | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 slippath auto\|ask\|mute\|clear` | EmberSlipPath.offerPick L151–159 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ01` → `offerPick` | EmberSlipPath L162–179 | **周要约/Q01 入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→`/corerpg p1` / `slippath` | EmberSlipPath.applyAndReply L214–216 | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `auto\|ask\|mute\|clear`；glance 未选仍教手打 | EmberCommand L6382–6415 · glance L89–92 | **无参入口无开薄页** |
| E10 | TrMenu **无** `ember_p1_slip_path.yml`（已有 crash/bolt/shift/gap/phase/wave/soak/shell/clone/press/cut/rush/clean/streak/blaze/spin/snap/veil/free/dodge/…_path） | menus/ | **缺选打专用薄页** |
| E11 | 局内闪招有「闪招」+「改路径」ConfirmTokens | EmberSlipPath L124–126 · L146–148 | **局内战斗钮≠选打薄页；选打硬债仍成立** |
| E12 | PacePath 本仓无；其它剩支 D592+ 另窗 | Java · 派单 | **后置（§6）** |
| E13 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E14 | ≠ 撞墙选打同题；≠ 闪线/侧安/收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态选打同题；≠ 全族 Path 大扫；≠ 其它 *Path 同窗；≠ 复开 D591 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `slippath` + 周要约/Q01 + `applyAndReply` 下一步→开 `/ember_p1_slip_path`；清 ConfirmTokens 教指令为主 UI；可选 `slip_path_*` PAPI；单测；局内闪招战斗钮语义零改
2. **菜单：** 新薄页 `ember_p1_slip_path`：自动/提醒/静默/清空/技能组/冒险/可选工坊；lore 挂路径；TrMenu 点击 + `corerpg p1 from/back`
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 闪招规则语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** crash_path / bolt_path / shift_path / gap_path / phase_path / wave_path / soak_path / shell_path / clone_path / press_path / cut_path / rush_path / clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath/SidestepPath/DodgePath/FreePath/VeilPath/SnapPath/SpinPath/BlazePath/StreakPath/CleanPath/RushPath/CutPath/PressPath/ClonePath/ShellPath/SoakPath/WavePath/PhasePath/GapPath/ShiftPath/BoltPath/CrashPath / Cast / Aid / variety / 庭内 / soft-reward；**其它剩支 D592+ / Pace / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/撞墙选打同题/全族Path大扫/Pace同窗/其它 *Path 同窗/legacy evacuate 大扫/复开 D591 | 硬禁 |

---

*RESEARCH · SlipPath.offerPick 闪招路径选打 · **CLOSED live** · feat `606eea48f` · PASS `e7648a86e` · 下一窗 BaitPath.offerPick · 2026-10-14 20:39 Asia/Shanghai**待批 A** · 荐 M · TIP-EK / D13368 · 半行 REJECT `d00ed84bc` 保持 · 其它剩支 D592+ / PacePath（本仓无）/挂机若新硬债/legacy evacuate 后置 · ancestor TIP-EJ CLOSED `9a81d121a` · 2026-10-14 20:24 Asia/Shanghai · 勿勾 CLOSED.*

## STATUS: **待批 A** · 荐 M · 勿勾 CLOSED · L不批 · D13368 · TIP-EK · 2026-10-14 20:24 Asia/Shanghai
