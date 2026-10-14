# tip · TIP-DO StepPath.offerPick / applyAndReply「身法路径选打」菜单化（选路径聊天钮去指令化 · ≠轻技路径选打同题 · ≠破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠闪冲同窗 · ≠复开 D491 玩法本体）· 拟 D13346

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13346 · TIP-DO · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改伤害/CD表/掉落/体力价 · offerPick/applyAndReply/无参 steppath/周要约→开 ember_p1_step_path · 清 ConfirmTokens 教 steppath 主 UI · 2026-10-14 13:45 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-step-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-step-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-step-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（StepPath.offerPick / applyAndReply / 无参 steppath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_step_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零轻技路径选打同题复读（TIP-DN） · 零破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态路径选打同题复读（TIP-DL–DE / TIP-DM） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零闪冲 SidestepPath 同窗 · 零复开 D491 玩法本体
> **ancestor：** TIP-DN CLOSED live `300697d79`（feat `53be7080e` · tip `68230fe44` · PASS `89c68de74` · jar `1.65.838-d13345.local` · 批 sha `68230fe44` · menu tip `55f8f3ea0`）
> **下一窗提示（旁注）：** 闪冲 SidestepPath 等 *Path.offerPick 逐支（禁全族大扫）/ PacePath 等剩支后置 / 挂机若新硬债 / legacy evacuate 后置

## 一句话
周选/改身法路径时一点开「身法路径」薄页：前冲 / 后撤 / 清空 / 进本练身法 / 技能组 / 冒险可点；清 ConfirmTokens 教 `steppath`/`enter` 主 UI；伤害·CD表·掉落·体力价不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberStepPath.offerPick + applyAndReply + 无参 steppath / 周要约入口** + 薄页 `ember_p1_step_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 轻技路径选打同题（TIP-DN 已齐；`ember_p1_flex_path` 零改本窗）· ≠ 步态路径选打同题（TIP-DM）· ≠ 破绽路径选打同题（TIP-DL）· ≠ 誓约路径选打同题（TIP-DK）· ≠ 试炼路径选打同题（TIP-DJ）· ≠ 深渊路径选打同题（TIP-DI）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ 闪冲同窗 · ≠ 全族 *Path.offerPick 大扫 · ≠ 复开 D491 玩法本体（forward/back 真改方向语义零改）

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem`（仅 enhance/upgrade/refine/brand/convert/roll gap）→ 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle CW–DD + 精选/硬本/团本/短征/深渊/试炼/誓约/破绽/步态/轻技路径选打 DE–DN **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DN §6⑦ 明文后置身法 StepPath / 闪冲 SidestepPath 等 *Path.offerPick 逐支；实扫两支均 ConfirmTokens 教裸指令且 **无** 专用选打薄页；**择最硬 = StepPath**（DESIGN-DN §6⑦ 首位 · 周要约/无参/offerPick 全链 · `applyAndReply` 下一步仍 ConfirmTokens 教 `enter`/`steppath` · glance 教手打 · 纯选打债无局内旁路 · TrMenu **无** `ember_p1_step_path`）→ 闪冲后置另窗；本仓 **无** EmberPacePath → **本窗钉死 StepPath 身法选打面**（镜像 TIP-DE–DN）

## 施工钉死（批后）
- W1a：`EmberStepPath.offerPick` → 开 `/ember_p1_step_path`；无参 `/corerpg p1 steppath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ05` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页或菜单优先（前冲/后撤/清空/进本练身法/技能组/冒险可点，禁以 ConfirmTokens 教裸 `steppath`/`enter` 为主 UI）；清用法「手打 steppath forward|back|clear」教指令主文案；glance 去手打主教；可选新增 `step_path_*` PAPI；单测
- W1b：薄页 `ember_p1_step_path`：前冲 / 后撤 / 清空（菜单 actions `steppath forward|back|clear`）/ 进本（按当前 enterCmd：FORWARD→q05 / BACK→q02）/ 技能组 `ember_skill_kit` / 冒险 `ember_p1_adventure` / 可选工坊；lore 挂当前身法路径+tip；灰态诚实（需本人首通 Q05）；玩家入口 TrMenu 点击 + `corerpg p1 from/back`；**不教玩家打指令**
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 身法规则语义（D491 forward/back 真改方向） / Cast/Aid/variety / 已齐 settle·选打薄页（含 `ember_p1_flex_path` / `ember_p1_stride_path` / `ember_p1_counter_path`） / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath / SidestepPath 等其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 13:45 Asia/Shanghai
- [ ] 插件交 offerPick / applyAndReply / 无参 steppath / 周要约改开菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 step_path 页 + TrMenu reload
- [ ] 抽测 PASS live（选前冲/后撤/清空/进本/技能组/冒险真开·PAPI·零抬伤害/CD表/掉落/体力价·零改 flex_path·零已齐选打同题复读）

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L不批 · D13346 · TIP-DO · 2026-10-14 13:45 Asia/Shanghai
