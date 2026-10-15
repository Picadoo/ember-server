# RESEARCH · RallyPath.offerPick / applyAndReply「清房突进选打」菜单化 · 拟 D13385 · TIP-FB

> **状态：** **待批 A** · 荐 M · 2026-10-15 16:20 Asia/Shanghai · 勿勾 CLOSED
> **起因：** 总控【下一窗 TIP-FB · 只钉一支 · 禁全族大扫】。祖先 TIP-FA CLOSED live `46e138454`（批 `d876a2dfe` · feat `fd9bcf3d5` · menu `d8b9676df` · jar `1.65.877-d13384.local` · PASS `c9047b97a` · **104/104** · **破绽爆发路径选打同题勿再交**）。**破绽爆发路径选打同题勿再交**。选题钉死：**RallyPath.offerPick**「清房突进选打」菜单化 → TrMenu `ember_p1_rally_path`（拟 D13385 · TIP-FB · 玩法本体 D562）。硬证：`EmberRallyPath.offerPick` 仍 ConfirmTokens 教 rallypath auto|ask|mute|clear；无参/周要约同入口；applyAndReply 下一步仍 ConfirmTokens；glance 教手打；局内清房钮「[突进]」（旧文案）+「改路径」≠选打薄页；TrMenu **无** `ember_p1_rally_path.yml`；与刚 CLOSED Burst 同构 auto/ask/mute。**禁：** 挂机 gem gap 未亮/短征已齐 → 勿交 gem/sx20；DE–FA 选打勿复；半行 REJECT `d00ed84bc` 保持；禁全族 *Path.offerPick 大扫；禁与其它 *Path 同窗；PacePath（本仓无）后置；legacy evacuate 后置；本窗只钉 Rally 一支 · 剩支另窗证据逐支（扫后再定 · 禁全族大扫 · 禁与其它 *Path 同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置；零抬伤害/CD表/掉落/体力价；零改 D562 清房突进语义本体；零改已齐 `ember_p1_burst_path` 等；勿勾 CLOSED。
> **上游：** TIP-FA CLOSED live `46e138454`（批 `d876a2dfe` · feat `fd9bcf3d5` · menu `d8b9676df` · jar `1.65.877-d13384.local` · PASS `c9047b97a` · **104/104** · **破绽爆发路径选打同题勿再交**）

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk **零** gem gap；总控 gem gap 未亮 → 勿交 gem |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–FA | **跳过** | CW–DD settle + TIP-DE–FA 选打（含破绽爆发）**全齐勿复读** |
| ④ PacePath | **后置** | 本仓无 EmberPacePath；派单明文后置 |
| ⑤ 本窗只钉 Rally 一支 · 剩支另窗证据逐支（扫后再定 · 禁全族大扫 · 禁与其它 *Path 同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 及其他仍 ConfirmTokens 的 *Path | **后置** | 禁全族大扫；本窗只钉一支；逐支另窗 |
| ⑥ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **RallyPath.offerPick / applyAndReply（荐）** | **采纳** | 总控钉死；ConfirmTokens 全链；**无**选打专用薄页；局内清房突进钮≠选打页；镜像 TIP-DE–FA；≠ 破绽爆发选打同题 · ≠ 破绽反击(Counter)选打同题（TIP-DL） |

**W1a 定名改字（总控定名「清房突进」· 必做）：** ① `EmberRallyPath.label` L71–73 旧标签「突进·自动/提醒/静默」→「清房突进·自动/提醒/静默」；② `glance` L85–87「突进路径」→「清房突进路径」；③ `EmberCommand` **L4937**「当前：… · 改路径再 /corerpg p1 rallypath」与 **L4944**「用法：/corerpg p1 rallypath <auto|ask|mute|clear>」两处教手打 → 改为指向薄页 `ember_p1_rally_path`（不再教裸 `rallypath`）；④ 同函数 L4924/L4931「突进路径需本人首通 Q01」、L4956–4957「无法突进 / 突进·手点」、L4966「突进路径探测」玩家可见字同步改「清房突进」；⑤ 局内清房钮源码现文案「[突进]」（`maybeAfterClear` L133 / `maybeProbe`）→「[清房突进]」，**语义零改**。**不改：** 类名 `EmberRallyPath` · 命令 id `rallypath`/`rally` · 菜单 id `ember_p1_rally_path` · 别名「突进路径」（保留直达）· Stride「突进踏」/ Posture「突进点名」/ Step lore 一字不动。（行号 2026-10-15 16:20 复核于 main `46e138454`。）

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 总控钉死 Rally（D562；Burst D563 后按残留 D# 钉 Rally）→ 实扫残留 *Path.offerPick 仍教裸指令且无薄页者：Rally 与 Burst 同构 → **钉 RallyPath**（offerPick ConfirmTokens→rallypath auto|ask|mute|clear；applyAndReply 下一步 ConfirmTokens 教裸 `rallypath`/`p1`；glance 未选教手打；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_rally_path`）。**局内清房钮「[突进]」（旧文案）+「改路径」战斗钮**（ASK 路径提醒）**存在但不消解选打硬债**——选打链仍 ConfirmTokens。本窗只钉 Rally 一支 · 剩支另窗证据逐支（扫后再定 · 禁全族大扫 · 禁与其它 *Path 同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 / PacePath（本仓无）后置。故交本支；**证据表见 §2**；**为何不是半行见 E13**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；总控选题已钉死）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-EX CLOSED · 派下一窗：只钉 RallyPath；禁全族大扫 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–FA 选打 **已齐** | tips | **禁止同题复读** |
| E5 | 总控钉死 Rally；D562；Burst D563 后按残留 D# 钉 Rally；本仓无 PacePath；本窗只钉 Rally 一支 · 剩支另窗证据逐支（扫后再定 · 禁全族大扫 · 禁与其它 *Path 同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 | 派单 · TIP-EX | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 rallypath auto\|ask\|mute\|clear` | EmberRallyPath.offerPick L141–149 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ01` → `offerPick` | EmberRallyPath L152–169 | **周要约/Q01 入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→`/corerpg p1` / `rallypath` | EmberRallyPath.applyAndReply L204–206 | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `auto\|ask\|mute\|clear`；glance 未选仍教手打 | EmberCommand rallyPathCmd · glance L85 | **无参入口无开薄页** |
| E10 | TrMenu **无** `ember_p1_rally_path.yml`（已有 beacon/relay/breach/alert/stock/hurry/focus/doom/nest/save/sequel/bait/slip/crash/bolt/shift/gap/phase/wave/soak/shell/clone/press/cut/rush/clean/streak/blaze/spin/snap/veil/free/dodge/…_path） | menus/ | **缺选打专用薄页** |
| E11 | 局内清房钮有「[突进]」（旧文案）+「改路径」ConfirmTokens | EmberRallyPath L114–116 · L136–138 | **局内战斗钮≠选打薄页；选打硬债仍成立** |
| E12 | PacePath 本仓无；本窗只钉 Rally 一支 · 剩支另窗证据逐支（扫后再定 · 禁全族大扫 · 禁与其它 *Path 同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 另窗 | Java · 派单 | **后置（§6）** |
| E13 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E14 | ≠ 破绽爆发选打同题 · ≠ 破绽反击(Counter)选打同题（TIP-DL）；≠ 闪招/撞墙/闪线/侧安/收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽反击(Counter)/誓约/试炼/深渊/短征/团本/硬本/精选/步态选打同题；≠ 全族 Path 大扫；≠ 其它 *Path 同窗；≠ 复开 D562 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `rallypath` + 周要约/Q01 + `applyAndReply` 下一步→开 `/ember_p1_rally_path`；清 ConfirmTokens 教指令为主 UI；可选 `rally_path_*` PAPI；单测；局内破绽战斗钮语义零改
2. **菜单：** 新薄页 `ember_p1_rally_path`：自动/提醒/静默/清空/技能组/冒险/可选工坊；lore 挂路径；TrMenu 点击 + `corerpg p1 from/back`
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 清房突进规则语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** slip_path / crash_path / bolt_path / shift_path / gap_path / phase_path / wave_path / soak_path / shell_path / clone_path / press_path / cut_path / rush_path / clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath/SidestepPath/DodgePath/FreePath/VeilPath/SnapPath/SpinPath/BlazePath/StreakPath/CleanPath/RushPath/CutPath/PressPath/ClonePath/ShellPath/SoakPath/WavePath/PhasePath/GapPath/ShiftPath/BoltPath/CrashPath/SlipPath/BaitPath/SequelPath/SavePath/NestPath/DoomPath/FocusPath/HurryPath/StockPath/AlertPath/BreachPath/RelayPath/HoldPath/BeaconPath/GuardPath/SmashPath/RallyPath / Cast / Aid / variety / 庭内 / soft-reward；**本窗只钉 Rally 一支 · 剩支另窗证据逐支（扫后再定 · 禁全族大扫 · 禁与其它 *Path 同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 / Pace / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/诱空选打同题/全族Path大扫/Pace同窗/其它 *Path 同窗/legacy evacuate 大扫/复开 D562 | 硬禁 |

---

*RESEARCH · RallyPath.offerPick / applyAndReply 清房突进选打菜单化 · **待批 A** · 荐 M · TIP-FB / D13385 · 半行 REJECT `d00ed84bc` 保持 · 本窗只钉 Rally 一支 · 剩支另窗证据逐支（扫后再定 · 禁全族大扫 · 禁与其它 *Path 同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 · ancestor TIP-FA CLOSED `46e138454` / PASS `c9047b97a` · 2026-10-15 16:20 Asia/Shanghai · 勿勾 CLOSED.*

## STATUS: **待批 A** · 荐 M · L不批 · 勿勾 CLOSED · D13385 · TIP-FB · 2026-10-15 16:20 Asia/Shanghai
