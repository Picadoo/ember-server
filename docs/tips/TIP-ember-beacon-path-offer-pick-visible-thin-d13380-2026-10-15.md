# tip · TIP-EW BeaconPath.offerPick / applyAndReply「护灯路径选打」菜单化（选路径聊天钮去指令化 · ≠传火路径选打同题 · ≠裂隙/预警/备药/急清/集火/灾临/卡点/终救/续招/诱空/闪招/撞墙/闪线/侧安/收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠其它 *Path 同窗 · ≠复开 D567 玩法本体）· 拟 D13380

> **状态：** **已批 A** · 方案 M · APPROVED A·M · 2026-10-15 10:36 Asia/Shanghai · 勿勾 CLOSED · 可施工 W1a插件+W1b物品
> **DESIGN** [`DESIGN-ember-beacon-path-offer-pick-visible-2026-10-15.md`](../design/DESIGN-ember-beacon-path-offer-pick-visible-2026-10-15.md) · RESEARCH 同日 · backlog `B-beacon-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（BeaconPath.offerPick / applyAndReply / 无参 beaconpath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_beacon_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零传火路径选打同题复读（TIP-EV） · 零裂隙/预警/备药/急清/集火/灾临/卡点/终救/续招/诱空/闪招/撞墙/闪线/侧安/收招/半血/增援/分摊/铁壁/分身/压伤/打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题复读（TIP-EU–DE / TIP-DM–DN） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零 PacePath 同窗 · 零其它 *Path 同窗 · 零复开 D567 玩法本体（auto/ask/mute 真改护灯传到灯旁守护语义零改）· 零改 relay_path / breach_path / alert_path / stock_path / hurry_path / focus_path / doom_path / nest_path / save_path / sequel_path / bait_path / slip_path / crash_path / bolt_path / shift_path / gap_path / phase_path / wave_path / soak_path / shell_path / clone_path / press_path / cut_path / rush_path / clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path 等已齐
> **ancestor：** TIP-EV 已批 A·M · PASS live `ffd10969f`（feat `6eec10d81` · tip批 `0acf03feb` · menu `c9b84f30e` · jar `1.65.872-d13379.local` · **100/100** · 证据 `/workspace/tmp/tip-ev-relay-path/` · **传火路径选打同题勿再交** · 待总控 CLOSED live）
> **下一窗提示（旁注）：** Hold/Guard 等另窗证据逐支（禁全族大扫 · 禁与 Hold/Guard 等同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置 · ancestor TIP-EW（本窗批后）

## 一句话
周选/改护灯路径时一点开「护灯路径」薄页：自动 / 提醒 / 静默 / 清空 / 技能组 / 冒险可点；清 ConfirmTokens 教 `beaconpath` 主 UI；伤害·CD表·掉落·体力价不动；局内护灯战斗钮「护灯」「改路径」语义零改。

## 为何不是半行
- **主交付：** 插件 **改 EmberBeaconPath.offerPick + applyAndReply + 无参 beaconpath / 周要约入口** + 薄页 `ember_p1_beacon_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 传火路径选打同题（TIP-EV PASS；`ember_p1_relay_path` 零改本窗）· ≠ 裂隙路径选打同题（TIP-EU）· ≠ 预警路径选打同题（TIP-ET）· ≠ 备药路径选打同题（TIP-ES）· ≠ 急清路径选打同题（TIP-ER）· ≠ 集火路径选打同题（TIP-EQ）· ≠ 灾临路径选打同题（TIP-EP）· ≠ 卡点路径选打同题（TIP-EO）· ≠ 终救路径选打同题（TIP-EN）· ≠ 续招路径选打同题（TIP-EM）· ≠ 诱空路径选打同题（TIP-EL）· ≠ 闪招路径选打同题（TIP-EK）· ≠ 撞墙路径选打同题（TIP-EJ）· ≠ 闪线路径选打同题（TIP-EI）· ≠ 侧安路径选打同题（TIP-EH）· ≠ 收招路径选打同题（TIP-EG）· ≠ 半血路径选打同题（TIP-EF）· ≠ 增援路径选打同题（TIP-EE）· ≠ 分摊路径选打同题（TIP-ED）· ≠ 铁壁路径选打同题（TIP-EC）· ≠ 分身路径选打同题（TIP-EB）· ≠ 压伤路径选打同题（TIP-EA）· ≠ 打断路径选打同题（TIP-DZ）· ≠ 限时路径选打同题（TIP-DY）· ≠ 净伤路径选打同题（TIP-DX）· ≠ 连斩路径选打同题（TIP-DW）· ≠ 避焰路径选打同题（TIP-DV）· ≠ 避旋路径选打同题（TIP-DU）· ≠ 断链路径选打同题（TIP-DT）· ≠ 避毒路径选打同题（TIP-DS）· ≠ 解锢路径选打同题（TIP-DR）· ≠ 闪榴路径选打同题（TIP-DQ）· ≠ 闪冲路径选打同题（TIP-DP）· ≠ 身法路径选打同题（TIP-DO）· ≠ 轻技路径选打同题（TIP-DN）· ≠ 步态路径选打同题（TIP-DM）· ≠ 破绽路径选打同题（TIP-DL）· ≠ 誓约路径选打同题（TIP-DK）· ≠ 试炼路径选打同题（TIP-DJ）· ≠ 深渊路径选打同题（TIP-DI）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ Pace同窗 · ≠ 其它 *Path 同窗 · ≠ 全族 *Path.offerPick 大扫 · ≠ 复开 D567 玩法本体（auto/ask/mute 真改护灯传到灯旁守护）

## 选题为何合法（改题有证据 · 未改题）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**（总控：gem gap 未亮 → 勿交 gem）
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle CW–DD + 精选/硬本/团本/短征/深渊/试炼/誓约/破绽/步态/轻技/身法/闪冲/闪榴/解锢/避毒/断链/避旋/避焰/连斩/净伤/限时/打断/压伤/铁壁/分摊/增援/半血/收招/侧安/闪线/撞墙/闪招/诱空/续招/终救/卡点/灾临/集火/急清/备药/预警/裂隙/传火路径选打 DE–EV **全齐**
4. **故落菜单去指令残留 · 只钉一支：** 总控钉死 **BeaconPath.offerPick**；D567；Relay D568 后按残留 D# 钉 Beacon。实扫 BeaconPath：`offerPick` ConfirmTokens→`beaconpath auto|ask|mute|clear`；`applyAndReply` 下一步 ConfirmTokens→`/corerpg p1`/`beaconpath`；无参/周要约/`scheduleOfferAfterQ01` 同入口；glance 未选教手打 `/corerpg p1 beaconpath`；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_beacon_path`。**注：** 局内护灯战斗钮有「护灯」+「改路径」（ASK 路径提醒）≠ 选打专用薄页已齐——选打链仍教裸指令硬债成立。与刚 PASS Relay 同构 auto/ask/mute（护灯传到灯旁守护；D567 玩法本体已 PASS，本窗只去指令化选打）；本窗只钉 Beacon 一支 · Hold/Guard 等另窗证据逐支（禁全族大扫 · 禁与 Hold/Guard 等同窗）· 勿默念 Pace（本仓无）· 挂机若新硬债 / legacy evacuate 后置；PacePath（本仓无）后置。

## 施工钉死（批后）
- W1a：`EmberBeaconPath.offerPick` → 开 `/ember_p1_beacon_path`；无参 `/corerpg p1 beaconpath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页或菜单优先（自动/提醒/静默/清空/技能组/冒险可点，禁以 ConfirmTokens 教裸 `beaconpath` 为主 UI）；清用法「手打 beaconpath auto|ask|mute|clear」教指令主文案；glance 去手打主教；可选新增 `beacon_path_*` PAPI；单测。局内 `maybeAfterBeacon`/`maybeProbe` 的「护灯」战斗钮语义 **零改**；其「改路径」可随 offerPick 改开薄页（同批可选，禁独批改战斗语义）
- W1b：薄页 `ember_p1_beacon_path`：自动 / 提醒 / 静默 / 清空（菜单 actions `beaconpath auto|ask|mute|clear`）/ 技能组 `ember_skill_kit` / 冒险 `ember_p1_adventure` / 可选工坊；lore 挂当前护灯路径+tip；灰态诚实（需本人首通 Q01 · `%corerpg_p1_q01done%`）；玩家入口 TrMenu 点击 + `corerpg p1 from/back`；**不教玩家打指令**
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 护灯规则语义（D567 auto/ask/mute 真改护灯传到灯旁守护） / Cast/Aid/variety / 已齐 settle·选打薄页（含 `ember_p1_relay_path` / `ember_p1_breach_path` / `ember_p1_alert_path` / `ember_p1_stock_path` / `ember_p1_hurry_path` / `ember_p1_focus_path` / `ember_p1_doom_path` / `ember_p1_nest_path` / `ember_p1_save_path` / `ember_p1_sequel_path` / `ember_p1_bait_path` / `ember_p1_slip_path` / `ember_p1_crash_path` / `ember_p1_bolt_path` / `ember_p1_shift_path` / `ember_p1_gap_path` / `ember_p1_phase_path` / `ember_p1_wave_path` / `ember_p1_soak_path` / `ember_p1_shell_path` / `ember_p1_clone_path` / `ember_p1_press_path` / `ember_p1_cut_path` / `ember_p1_rush_path` / `ember_p1_clean_path` / `ember_p1_streak_path` / `ember_p1_blaze_path` / `ember_p1_spin_path` / `ember_p1_snap_path` / `ember_p1_veil_path` / `ember_p1_free_path` / `ember_p1_dodge_path` / `ember_p1_sidestep_path` / `ember_p1_step_path` / `ember_p1_flex_path` / `ember_p1_stride_path` / `ember_p1_counter_path`） / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath/SidestepPath/DodgePath/FreePath/VeilPath/SnapPath/SpinPath/BlazePath/StreakPath/CleanPath/RushPath/CutPath/PressPath/ClonePath/ShellPath/SoakPath/WavePath/PhasePath/GapPath/ShiftPath/BoltPath/CrashPath/SlipPath/BaitPath/SequelPath/SavePath/NestPath/DoomPath/FocusPath/HurryPath/StockPath/AlertPath/BreachPath/RelayPath / 其它 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M
- [ ] 插件交 offerPick / applyAndReply / 无参 beaconpath / 周要约改开菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 beacon_path 页 + TrMenu reload
- [ ] 抽测 PASS live（选自动/提醒/静默/清空/技能组/冒险真开·PAPI·零抬伤害/CD表/掉落/体力价·零改 relay_path·零已齐选打同题复读·零改 D567 护灯语义）

## STATUS: **已批 A** · 方案 M · APPROVED A·M · D13380 · TIP-EW · 2026-10-15 10:36 Asia/Shanghai · 勿勾 CLOSED · 可施工
