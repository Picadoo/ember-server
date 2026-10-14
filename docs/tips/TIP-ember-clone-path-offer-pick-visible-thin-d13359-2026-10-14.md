# tip · TIP-EB ClonePath.offerPick / applyAndReply「分身路径选打」菜单化（选路径聊天钮去指令化 · ≠压伤路径选打同题 · ≠打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠其它 *Path 同窗 · ≠复开 D582 玩法本体）· 拟 D13359

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13359 · TIP-EB · W1a+W1b+抽测 PASS · L不批 · 勿勾 CLOSED · 零改伤害/CD表/掉落/体力价 · offerPick/applyAndReply/无参 clonepath/周要约→开 ember_p1_clone_path · 清 ConfirmTokens 教 clonepath 主 UI · 2026-10-14 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-clone-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-clone-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-clone-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（ClonePath.offerPick / applyAndReply / 无参 clonepath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_clone_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零压伤路径选打同题复读（TIP-EA） · 零打断/限时/净伤/连斩/避焰/避旋/断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题复读（TIP-DZ–DE / TIP-DM–DN） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零 PacePath 同窗 · 零其它 *Path 同窗 · 零复开 D582 玩法本体（auto/ask/mute 真改分身清掉语义零改）· 零改 press_path / cut_path / rush_path / clean_path / streak_path / blaze_path / spin_path / snap_path / veil_path / free_path 等已齐
> **ancestor：** TIP-EA CLOSED live `8772b030c`（feat `afa2cf969` · tip `1b4f236e6` · PASS `19c58c9ee` · jar `1.65.851-d13358.local` · 批 sha `1b4f236e6` · menu tip `9a9c42d6e` · 证据 `/workspace/tmp/tip-ea-press-path/`）
> **下一窗提示（旁注）：** Shell… 等 D583+ 剩支 **后置**（逐支另窗 · 禁全族大扫）· PacePath（本仓无）后置 / 挂机若新硬债 / legacy evacuate 后置 · ancestor TIP-EB（本窗批后）

## 一句话
周选/改分身路径时一点开「分身路径」薄页：自动 / 提醒 / 静默 / 清空 / 技能组 / 冒险可点；清 ConfirmTokens 教 `clonepath` 主 UI；伤害·CD表·掉落·体力价不动；局内分裂战斗钮「清分身」「改路径」语义零改。

## 为何不是半行
- **主交付：** 插件 **改 EmberClonePath.offerPick + applyAndReply + 无参 clonepath / 周要约入口** + 薄页 `ember_p1_clone_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 压伤路径选打同题（TIP-EA 已齐；`ember_p1_press_path` 零改本窗）· ≠ 打断路径选打同题（TIP-DZ）· ≠ 限时路径选打同题（TIP-DY）· ≠ 净伤路径选打同题（TIP-DX）· ≠ 连斩路径选打同题（TIP-DW）· ≠ 避焰路径选打同题（TIP-DV）· ≠ 避旋路径选打同题（TIP-DU）· ≠ 断链路径选打同题（TIP-DT）· ≠ 避毒路径选打同题（TIP-DS）· ≠ 解锢路径选打同题（TIP-DR）· ≠ 闪榴路径选打同题（TIP-DQ）· ≠ 闪冲路径选打同题（TIP-DP）· ≠ 身法路径选打同题（TIP-DO）· ≠ 轻技路径选打同题（TIP-DN）· ≠ 步态路径选打同题（TIP-DM）· ≠ 破绽路径选打同题（TIP-DL）· ≠ 誓约路径选打同题（TIP-DK）· ≠ 试炼路径选打同题（TIP-DJ）· ≠ 深渊路径选打同题（TIP-DI）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ Pace同窗 · ≠ 其它 *Path 同窗 · ≠ 全族 *Path.offerPick 大扫 · ≠ 复开 D582 玩法本体（auto/ask/mute 真改分身清掉）

## 选题为何合法（改题有证据 · 未改题）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**（总控：gem gap 未亮 → 勿交 gem）
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle CW–DD + 精选/硬本/团本/短征/深渊/试炼/誓约/破绽/步态/轻技/身法/闪冲/闪榴/解锢/避毒/断链/避旋/避焰/连斩/净伤/限时/打断/压伤路径选打 DE–EA **全齐**
4. **故落菜单去指令残留 · 只钉一支：** 总控钉死 **ClonePath.offerPick**；D582 紧接 Press D581。实扫 ClonePath：`offerPick` ConfirmTokens→`clonepath auto|ask|mute|clear`；`applyAndReply` 下一步 ConfirmTokens→`/corerpg p1`/`clonepath`；无参/周要约/`scheduleOfferAfterQ01` 同入口；glance 未选教手打 `/corerpg p1 clonepath`；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_clone_path`。**注：** 局内分裂战斗钮有「清分身」+「改路径」（ASK 路径提醒）≠ 选打专用薄页已齐——选打链仍教裸指令硬债成立。与刚 CLOSED Press 同构 auto/ask/mute；Shell… D583+ 后置；PacePath（本仓无）后置。

## 施工钉死（批后）
- W1a：`EmberClonePath.offerPick` → 开 `/ember_p1_clone_path`；无参 `/corerpg p1 clonepath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页或菜单优先（自动/提醒/静默/清空/技能组/冒险可点，禁以 ConfirmTokens 教裸 `clonepath` 为主 UI）；清用法「手打 clonepath auto|ask|mute|clear」教指令主文案；glance 去手打主教；可选新增 `clone_path_*` PAPI；单测。局内 `maybeAfterClone`/`maybeProbe` 的「清分身」战斗钮语义 **零改**；其「改路径」可随 offerPick 改开薄页（同批可选，禁独批改战斗语义）
- W1b：薄页 `ember_p1_clone_path`：自动 / 提醒 / 静默 / 清空（菜单 actions `clonepath auto|ask|mute|clear`）/ 技能组 `ember_skill_kit` / 冒险 `ember_p1_adventure` / 可选工坊；lore 挂当前分身路径+tip；灰态诚实（需本人首通 Q01 · `%corerpg_p1_q01done%`）；玩家入口 TrMenu 点击 + `corerpg p1 from/back`；**不教玩家打指令**
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 分身规则语义（D582 auto/ask/mute 真改分身清掉） / Cast/Aid/variety / 已齐 settle·选打薄页（含 `ember_p1_press_path` / `ember_p1_cut_path` / `ember_p1_rush_path` / `ember_p1_clean_path` / `ember_p1_streak_path` / `ember_p1_blaze_path` / `ember_p1_spin_path` / `ember_p1_snap_path` / `ember_p1_veil_path` / `ember_p1_free_path` / `ember_p1_dodge_path` / `ember_p1_sidestep_path` / `ember_p1_step_path` / `ember_p1_flex_path` / `ember_p1_stride_path` / `ember_p1_counter_path`） / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath/SidestepPath/DodgePath/FreePath/VeilPath/SnapPath/SpinPath/BlazePath/StreakPath/CleanPath/RushPath/CutPath/PressPath / Shell 等其它 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 17:47 Asia/Shanghai
- [x] 插件交 offerPick / applyAndReply / 无参 clonepath / 周要约改开菜单 + 清裸指令主路径 + 单测 · feat_commit `fdd409b970467b476be0f360b63a21b36181533c` · jar `1.65.852-d13359.local`
- [x] 物品交薄 clone_path 页 + TrMenu reload · `938e20faa`
- [x] 抽测 PASS live（选自动/提醒/静默/清空/技能组/冒险真开·PAPI·零抬伤害/CD表/掉落/体力价·零改 press_path·零已齐选打同题复读·零改 D582 分身语义）· spot STATUS 见 STATUS-ember-clone-path-offer-pick-tip-eb-spot-d13359 · **勿勾 CLOSED**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · W1a+W1b+抽测 PASS（勿勾 CLOSED · 待总控收口） · L不批 · D13359 · TIP-EB · 2026-10-14 Asia/Shanghai
