# tip · TIP-DN FlexPath.offerPick / applyAndReply「轻技路径选打」菜单化（选路径聊天钮去指令化 · ≠步态路径选打同题 · ≠破绽/誓约/试炼/深渊/短征/团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠身法/闪冲同窗 · ≠复开 D494 玩法本体）· 拟 D13345

> **状态：** **抽测 PASS** · APPROVED A·M · D13345 · TIP-DN · feat `53be7080e` · menu `55f8f3ea0` · jar `1.65.838-d13345.local` · L不批 · **勿勾 CLOSED**（总控收口）· 零改伤害/CD表/掉落/体力价 · offerPick/无参 flexpath/周要约→开 ember_p1_flex_path · 清 ConfirmTokens 教 flexpath 主 UI · 2026-10-14 13:36 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-flex-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-flex-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-flex-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（FlexPath.offerPick / applyAndReply / 无参 flexpath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_flex_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零步态路径选打同题复读（TIP-DM） · 零破绽/誓约/试炼/深渊/短征/团本/硬本/精选路径选打同题复读（TIP-DL–DE） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零身法 StepPath / 闪冲 SidestepPath 同窗 · 零复开 D494 玩法本体
> **ancestor：** TIP-DM CLOSED live `584e17b53`（feat `3daefff99` · tip `d2441d9f8` · PASS `1e7d58049` · jar `1.65.837-d13344.local` · 批 sha `d2441d9f8` · menu tip `e16208193`）
> **下一窗提示（旁注）：** 身法 StepPath / 闪冲 SidestepPath 等 *Path.offerPick 逐支（禁全族大扫）/ PacePath 等剩支后置 / 挂机若新硬债 / legacy evacuate 后置

## 一句话
周选/改轻技路径时一点开「轻技路径」薄页：装配踏步 / 卸下轻技 / 清空 / 身法方向（菜单开页或后置）/ 技能组 / 冒险可点；清 ConfirmTokens 教 `flexpath`/`steppath` 主 UI；伤害·CD表·掉落·体力价不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberFlexPath.offerPick + applyAndReply + 无参 flexpath / 周要约入口** + 薄页 `ember_p1_flex_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 步态路径选打同题（TIP-DM 已齐；`ember_p1_stride_path` 零改本窗）· ≠ 破绽路径选打同题（TIP-DL）· ≠ 誓约路径选打同题（TIP-DK）· ≠ 试炼路径选打同题（TIP-DJ）· ≠ 深渊路径选打同题（TIP-DI）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ 身法/闪冲同窗 · ≠ 全族 *Path.offerPick 大扫 · ≠ 复开 D494 玩法本体（on/off 装配轻技语义与潜行+Q 写入零改）

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem`（仅 enhance/upgrade/refine/brand/convert/roll gap）→ 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle CW–DD + 精选/硬本/团本/短征/深渊/试炼/誓约/破绽/步态路径选打 DE–DM **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DM §6⑦ 明文后置轻技 FlexPath / 身法 StepPath / 闪冲 SidestepPath 等 *Path.offerPick 逐支；实扫三支均 ConfirmTokens 教裸指令且 **无** 专用选打薄页；**择最硬 = FlexPath**（Q01 最早解锁 · 周要约/无参/offerPick 全链 · `applyAndReply` 下一步仍 ConfirmTokens 教 `steppath`/`flexpath` · glance 教手打 · TrMenu **无** `ember_p1_flex_path`）→ 身法/闪冲后置另窗；本仓 **无** EmberPacePath → **本窗钉死 FlexPath 轻技选打面**（镜像 TIP-DE–DM）

## 施工钉死（批后）
- W1a：`EmberFlexPath.offerPick` → 开 `/ember_p1_flex_path`；无参 `/corerpg p1 flexpath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页或菜单优先（装配踏步/卸下轻技/清空/技能组/冒险可点；身法方向可开 `ember_p1_step_path` 若已有，否则后置 Step 窗，**禁**以 ConfirmTokens 教裸 `flexpath`/`steppath` 为主 UI）；清用法「手打 flexpath on|off|clear」教指令主文案；glance 去手打主教；可选新增 `flex_path_*` PAPI；单测
- W1b：薄页 `ember_p1_flex_path`：装配踏步 / 卸下轻技 / 清空（菜单 actions `flexpath on|off|clear`）/ 技能组 `ember_skill_kit` / 冒险 `ember_p1_adventure` / 可选工坊；lore 挂当前轻技路径+tip；灰态诚实（需本人首进/首通 Q01）；玩家入口 TrMenu 点击 + `corerpg p1 from/back`；**不教玩家打指令**
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 轻技规则语义（D494 on/off 装配潜行+Q） / Cast/Aid/variety / 已齐 settle·选打薄页（含 `ember_p1_stride_path` / `ember_p1_counter_path`） / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath / StepPath/SidestepPath 等其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 13:24 Asia/Shanghai
- [x] 插件交 offerPick / applyAndReply / 无参 flexpath / 周要约改开菜单 + 清裸指令主路径 + 单测 · feat_commit `53be7080e08572f2f981287e74b814cea7d45be3` · jar `1.65.838-d13345.local`
- [x] 物品交薄 flex_path 页 + TrMenu reload · menu `55f8f3ea0` · TrMenu **93**
- [x] 抽测 PASS live · STATUS spot · 证据 `/workspace/tmp/tip-dn-flex-path/` · **勿勾 CLOSED**（总控收口）

## STATUS: **抽测 PASS** · APPROVED A·M · feat `53be7080e` · menu `55f8f3ea0` · jar `1.65.838-d13345.local` · **勿勾 CLOSED**（总控收口）· L不批 · D13345 · TIP-DN · 2026-10-14 13:36 Asia/Shanghai
