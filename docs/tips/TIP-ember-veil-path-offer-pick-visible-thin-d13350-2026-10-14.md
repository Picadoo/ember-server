# tip · TIP-DS VeilPath.offerPick / applyAndReply「避毒路径选打」菜单化（选路径聊天钮去指令化 · ≠解锢路径选打同题 · ≠闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠Pace同窗 · ≠Snap/Spin/Blaze同窗 · ≠复开 D573 玩法本体）· 拟 D13350

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13350 · TIP-DS · W1a+W1b+抽测 PASS · L不批 · 勿勾 CLOSED · 零改伤害/CD表/掉落/体力价 · offerPick/applyAndReply/无参 veilpath/周要约→开 ember_p1_veil_path · 清 ConfirmTokens 教 veilpath 主 UI · 2026-10-14 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-veil-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-veil-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-veil-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（VeilPath.offerPick / applyAndReply / 无参 veilpath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_veil_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零解锢路径选打同题复读（TIP-DR） · 零闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题复读（TIP-DQ–DE / TIP-DM–DN） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零 PacePath 同窗 · 零 Snap/Spin/Blaze 同窗 · 零复开 D573 玩法本体（auto/ask/mute 真改毒十字避毒语义零改）· 零改 free_path / dodge_path 等已齐
> **ancestor：** TIP-DR CLOSED live `8d35ba6d7`（feat `446b2d907` · tip `66bfe1203` · PASS `36db6d583` · jar `1.65.842-d13349.local` · 批 sha `66bfe1203` · menu tip `b1f0db650` · 证据 `/workspace/tmp/tip-dr-free-path/`）
> **下一窗提示（旁注）：** Snap/Spin/Blaze 等 *Path.offerPick **逐支**（禁全族大扫）/ PacePath（本仓无）后置 / 挂机若新硬债 / legacy evacuate 后置 · ancestor TIP-DS（批后）

## 一句话
周选/改避毒路径时一点开「避毒路径」薄页：自动 / 提醒 / 静默 / 清空 / 技能组 / 冒险可点；清 ConfirmTokens 教 `veilpath` 主 UI；伤害·CD表·掉落·体力价不动；局内「毒十字」战斗钮语义零改。

## 为何不是半行
- **主交付：** 插件 **改 EmberVeilPath.offerPick + applyAndReply + 无参 veilpath / 周要约入口** + 薄页 `ember_p1_veil_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 解锢路径选打同题（TIP-DR 已齐；`ember_p1_free_path` 零改本窗）· ≠ 闪榴路径选打同题（TIP-DQ）· ≠ 闪冲路径选打同题（TIP-DP）· ≠ 身法路径选打同题（TIP-DO）· ≠ 轻技路径选打同题（TIP-DN）· ≠ 步态路径选打同题（TIP-DM）· ≠ 破绽路径选打同题（TIP-DL）· ≠ 誓约路径选打同题（TIP-DK）· ≠ 试炼路径选打同题（TIP-DJ）· ≠ 深渊路径选打同题（TIP-DI）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ Pace同窗 · ≠ Snap/Spin/Blaze同窗 · ≠ 全族 *Path.offerPick 大扫 · ≠ 复开 D573 玩法本体（auto/ask/mute 真改毒十字避毒）

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**（总控：gem gap 未亮 → 勿交 gem）
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle CW–DD + 精选/硬本/团本/短征/深渊/试炼/誓约/破绽/步态/轻技/身法/闪冲/闪榴/解锢路径选打 DE–DR **全齐**
4. **故落菜单去指令残留 · 只钉一支：** 总控钉死 **VeilPath.offerPick**；DESIGN-DQ§6⑦ / Free 后下一位。实扫 VeilPath：`offerPick` ConfirmTokens→`veilpath auto|ask|mute|clear`；`applyAndReply` 下一步 ConfirmTokens→`/corerpg p1`/`veilpath`；无参/周要约/`scheduleOfferAfterQ01` 同入口；glance 未选教手打 `/corerpg p1 veilpath`；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_veil_path`。**注：** 局内「毒十字」有「避毒」战斗钮（ASK 路径提醒）≠ 选打专用薄页已齐——选打链仍教裸指令硬债成立。与刚 CLOSED Free 同构 auto/ask/mute；Snap/Spin/Blaze 后置。

## 施工钉死（批后）
- W1a：`EmberVeilPath.offerPick` → 开 `/ember_p1_veil_path`；无参 `/corerpg p1 veilpath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页或菜单优先（自动/提醒/静默/清空/技能组/冒险可点，禁以 ConfirmTokens 教裸 `veilpath` 为主 UI）；清用法「手打 veilpath auto|ask|mute|clear」教指令主文案；glance 去手打主教；可选新增 `veil_path_*` PAPI；单测。局内 `maybeAfterVenom`/`maybeProbe` 的「避毒」战斗钮语义 **零改**；其「改路径」可随 offerPick 改开薄页（同批可选，禁独批改战斗语义）
- W1b：薄页 `ember_p1_veil_path`：自动 / 提醒 / 静默 / 清空（菜单 actions `veilpath auto|ask|mute|clear`）/ 技能组 `ember_skill_kit` / 冒险 `ember_p1_adventure` / 可选工坊；lore 挂当前避毒路径+tip；灰态诚实（需本人首通 Q01 · `%corerpg_p1_q01done%`）；玩家入口 TrMenu 点击 + `corerpg p1 from/back`；**不教玩家打指令**
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 避毒规则语义（D573 auto/ask/mute 真改毒十字避毒） / Cast/Aid/variety / 已齐 settle·选打薄页（含 `ember_p1_free_path` / `ember_p1_dodge_path` / `ember_p1_sidestep_path` / `ember_p1_step_path` / `ember_p1_flex_path` / `ember_p1_stride_path` / `ember_p1_counter_path`） / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath/SidestepPath/DodgePath/FreePath / Snap/Spin/Blaze 等其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 15:03 Asia/Shanghai
- [x] 插件交 offerPick / applyAndReply / 无参 veilpath / 周要约改开菜单 + 清裸指令主路径 + 单测 · feat_commit `3e3a8f876` · jar `1.65.843-d13350.local`
- [x] 物品交薄 veil_path 页 + TrMenu reload · menu `bf2645adf` · TrMenu 98
- [x] 抽测 PASS live（选自动/提醒/静默/清空/技能组/冒险真开·PAPI·零抬伤害/CD表/掉落/体力价·零改 free_path·零已齐选打同题复读·零改 D573 避毒语义）· spot STATUS 见 STATUS-ember-veil-path-offer-pick-tip-ds-spot-d13350 · **勿勾 CLOSED**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · W1a+W1b+抽测 PASS（勿勾 CLOSED · 待总控收口） · L不批 · D13350 · TIP-DS · 2026-10-14 Asia/Shanghai
