# tip · TIP-EL BaitPath.offerPick / applyAndReply「诱空路径选打」菜单化（选路径聊天钮去指令化 · ≠闪招路径选打同题 · ≠撞墙/闪线/侧安/收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠其它 *Path 同窗 · ≠复开 D592 玩法本体）· 拟 D13369

> **状态：** **待批 A** · 荐 M · 2026-10-14 20:41 Asia/Shanghai · 勿勾 CLOSED
> **DESIGN** [`DESIGN-ember-bait-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-bait-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-bait-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（BaitPath.offerPick / applyAndReply / 无参 baitpath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_bait_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零闪招路径选打同题复读（TIP-EK） · 零撞墙/闪线/侧安/收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题复读（TIP-EJ–DE / TIP-DM–DN） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零 PacePath 同窗 · 零其它 *Path 同窗 · 零复开 D592 玩法本体（auto/ask/mute 真改诱空闪出预警语义零改）· 零改 slip_path / crash_path / bolt_path / shift_path / gap_path / phase_path / wave_path / soak_path / shell_path / clone_path / press_path / cut_path / rush_path / clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path 等已齐
> **ancestor：** TIP-EK CLOSED live `4e4a8b979`（feat `606eea48f` · tip `5119931a6` · PASS `e7648a86e` · jar `1.65.861-d13368.local` · 批 sha `5119931a6` · menu tip `dbd986adb` · 证据 `/workspace/tmp/tip-ek-slip-path/`）
> **下一窗提示（旁注）：** 其它剩支 D593+（Sequel…） **后置**（逐支另窗 · 禁全族大扫）· PacePath（本仓无）后置 / 挂机若新硬债 / legacy evacuate 后置 · ancestor TIP-EL（本窗批后）

## 一句话
周选/改诱空路径时一点开「诱空路径」薄页：自动 / 提醒 / 静默 / 清空 / 技能组 / 冒险可点；清 ConfirmTokens 教 `baitpath` 主 UI；伤害·CD表·掉落·体力价不动；局内诱空战斗钮「闪出」「改路径」语义零改。

## 为何不是半行
- **主交付：** 插件 **改 EmberBaitPath.offerPick + applyAndReply + 无参 baitpath / 周要约入口** + 薄页 `ember_p1_bait_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 闪招路径选打同题（TIP-EK 已齐；`ember_p1_slip_path` 零改本窗）· ≠ 撞墙路径选打同题（TIP-EJ）· ≠ 闪线路径选打同题（TIP-EI）· ≠ 侧安路径选打同题（TIP-EH）· ≠ 收招路径选打同题（TIP-EG）· ≠ 半血路径选打同题（TIP-EF）· ≠ 增援路径选打同题（TIP-EE）· ≠ 分摊路径选打同题（TIP-ED）· ≠ 铁壁路径选打同题（TIP-EC）· ≠ 分身路径选打同题（TIP-EB）· ≠ 压伤路径选打同题（TIP-EA）· ≠ 打断路径选打同题（TIP-DZ）· ≠ 限时路径选打同题（TIP-DY）· ≠ 净伤路径选打同题（TIP-DX）· ≠ 连斩路径选打同题（TIP-DW）· ≠ 避焰路径选打同题（TIP-DV）· ≠ 避旋路径选打同题（TIP-DU）· ≠ 断链路径选打同题（TIP-DT）· ≠ 避毒路径选打同题（TIP-DS）· ≠ 解锢路径选打同题（TIP-DR）· ≠ 闪榴路径选打同题（TIP-DQ）· ≠ 闪冲路径选打同题（TIP-DP）· ≠ 身法路径选打同题（TIP-DO）· ≠ 轻技路径选打同题（TIP-DN）· ≠ 步态路径选打同题（TIP-DM）· ≠ 破绽路径选打同题（TIP-DL）· ≠ 誓约路径选打同题（TIP-DK）· ≠ 试炼路径选打同题（TIP-DJ）· ≠ 深渊路径选打同题（TIP-DI）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ Pace同窗 · ≠ 其它 *Path 同窗 · ≠ 全族 *Path.offerPick 大扫 · ≠ 复开 D592 玩法本体（auto/ask/mute 真改诱空闪出预警）

## 选题为何合法（改题有证据 · 未改题）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**（总控：gem gap 未亮 → 勿交 gem）
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle CW–DD + 精选/硬本/团本/短征/深渊/试炼/誓约/破绽/步态/轻技/身法/闪冲/闪榴/解锢/避毒/断链/避旋/避焰/连斩/净伤/限时/打断/压伤/铁壁/分摊/增援/半血/收招/侧安/闪线/撞墙/闪招路径选打 DE–EK **全齐**
4. **故落菜单去指令残留 · 只钉一支：** 总控钉死 **BaitPath.offerPick**；D592 紧接 Slip D591。实扫 BaitPath：`offerPick` ConfirmTokens→`baitpath auto|ask|mute|clear`；`applyAndReply` 下一步 ConfirmTokens→`/corerpg p1`/`baitpath`；无参/周要约/`scheduleOfferAfterQ01` 同入口；glance 未选教手打 `/corerpg p1 baitpath`；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_bait_path`。**注：** 局内诱空战斗钮有「闪出」+「改路径」（ASK 路径提醒）≠ 选打专用薄页已齐——选打链仍教裸指令硬债成立。与刚 CLOSED Slip 同构 auto/ask/mute；其它剩支 D593+（Sequel…）后置；PacePath（本仓无）后置。

## 施工钉死（批后）
- W1a：`EmberBaitPath.offerPick` → 开 `/ember_p1_bait_path`；无参 `/corerpg p1 baitpath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页或菜单优先（自动/提醒/静默/清空/技能组/冒险可点，禁以 ConfirmTokens 教裸 `baitpath` 为主 UI）；清用法「手打 baitpath auto|ask|mute|clear」教指令主文案；glance 去手打主教；可选新增 `bait_path_*` PAPI；单测。局内 `maybeAfterBait`/`maybeProbe` 的「闪出」战斗钮语义 **零改**；其「改路径」可随 offerPick 改开薄页（同批可选，禁独批改战斗语义）
- W1b：薄页 `ember_p1_bait_path`：自动 / 提醒 / 静默 / 清空（菜单 actions `baitpath auto|ask|mute|clear`）/ 技能组 `ember_skill_kit` / 冒险 `ember_p1_adventure` / 可选工坊；lore 挂当前诱空路径+tip；灰态诚实（需本人首通 Q01 · `%corerpg_p1_q01done%`）；玩家入口 TrMenu 点击 + `corerpg p1 from/back`；**不教玩家打指令**
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 诱空规则语义（D592 auto/ask/mute 真改诱空闪出预警） / Cast/Aid/variety / 已齐 settle·选打薄页（含 `ember_p1_slip_path` / `ember_p1_crash_path` / `ember_p1_bolt_path` / `ember_p1_shift_path` / `ember_p1_gap_path` / `ember_p1_phase_path` / `ember_p1_wave_path` / `ember_p1_soak_path` / `ember_p1_shell_path` / `ember_p1_clone_path` / `ember_p1_press_path` / `ember_p1_cut_path` / `ember_p1_rush_path` / `ember_p1_clean_path` / `ember_p1_streak_path` / `ember_p1_blaze_path` / `ember_p1_spin_path` / `ember_p1_snap_path` / `ember_p1_veil_path` / `ember_p1_free_path` / `ember_p1_dodge_path` / `ember_p1_sidestep_path` / `ember_p1_step_path` / `ember_p1_flex_path` / `ember_p1_stride_path` / `ember_p1_counter_path`） / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath/SidestepPath/DodgePath/FreePath/VeilPath/SnapPath/SpinPath/BlazePath/StreakPath/CleanPath/RushPath/CutPath/PressPath/ClonePath/ShellPath/SoakPath/WavePath/PhasePath/GapPath/ShiftPath/BoltPath/CrashPath/SlipPath / 其它 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [ ] 总控批 A·M
- [ ] 插件交 offerPick / applyAndReply / 无参 baitpath / 周要约改开菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 bait_path 页 + TrMenu reload
- [ ] 抽测 PASS live（选自动/提醒/静默/清空/技能组/冒险真开·PAPI·零抬伤害/CD表/掉落/体力价·零改 slip_path·零已齐选打同题复读·零改 D592 诱空语义）

## STATUS: **待批 A** · 荐 M · L不批 · D13369 · TIP-EL · 2026-10-14 20:41 Asia/Shanghai · 勿勾 CLOSED
