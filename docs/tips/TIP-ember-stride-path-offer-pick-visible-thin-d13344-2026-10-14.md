# tip · TIP-DM StridePath.offerPick / applyAndReply「步态路径选打」菜单化（选路径聊天钮去指令化 · ≠破绽路径选打同题 · ≠誓约/试炼/深渊/短征/团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠轻技/身法/闪冲同窗 · ≠复开 D499 玩法本体）· 拟 D13344

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13344 · TIP-DM · W1a插件已交 · 待测岗/物品W1b · L不批 · 勿勾 CLOSED · 零改伤害/CD表/掉落/体力价 · offerPick/applyAndReply/无参 stridepath/周要约→开 ember_p1_stride_path · 清 ConfirmTokens 教 stridepath 主 UI · 2026-10-14 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-stride-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-stride-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-stride-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（StridePath.offerPick / applyAndReply / 无参 stridepath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_stride_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零破绽路径选打同题复读（TIP-DL） · 零誓约/试炼/深渊/短征/团本/硬本/精选路径选打同题复读（TIP-DK–DE） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零轻技 FlexPath / 身法 StepPath / 闪冲 SidestepPath 同窗 · 零复开 D499 玩法本体
> **ancestor：** TIP-DL CLOSED live `999b7a383`（feat `2b7617600` · tip `7490a03eb` · PASS `2b54c0d0b` · jar `1.65.836-d13343.local` · 批 sha `7490a03eb` · menu tip `c2b3b37bd`）
> **下一窗提示（旁注）：** 轻技 FlexPath / 身法 StepPath / 闪冲 SidestepPath 等 *Path.offerPick 逐支（禁全族大扫）/ PacePath 等剩支后置 / 挂机若新硬债 / legacy evacuate 后置

## 一句话
周选/改步态路径时一点开「步态路径」薄页：突进踏 / 后撤踏 / 卸轻技 / 清空 / 进本练步态 / 冒险可点；清 ConfirmTokens 教 `stridepath`/`enter`/`flexpath`/`steppath` 主 UI；伤害·CD表·掉落·体力价不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberStridePath.offerPick + applyAndReply + 无参 stridepath / 周要约入口** + 薄页 `ember_p1_stride_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 破绽路径选打同题（TIP-DL 已齐；`ember_p1_counter_path` 零改本窗）· ≠ 誓约路径选打同题（TIP-DK）· ≠ 试炼路径选打同题（TIP-DJ）· ≠ 深渊路径选打同题（TIP-DI）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ 轻技/身法/闪冲同窗 · ≠ 全族 *Path.offerPick 大扫 · ≠ 复开 D499 玩法本体（push/bail/bare 语义与轻技+身法写入零改）

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem`（仅 enhance/upgrade/refine/brand/convert/roll gap）→ 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle CW–DD + 精选/硬本/团本/短征/深渊/试炼/誓约/破绽路径选打 DE–DL **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DL §6⑦ 明文后置步态 StridePath.offerPick 等 *Path.offerPick 逐支；实扫 Stride 仍 ConfirmTokens 教裸指令、**无** `ember_p1_stride_path` 选打薄页；本仓 **无** EmberPacePath；Flex/Step/Sidestep 仍教裸指令但 DESIGN-DL §6⑧ 明文后置另窗 → **本窗钉死 StridePath 步态选打面**（镜像 TIP-DE–DL）

## 施工钉死（批后）
- W1a：`EmberStridePath.offerPick` → 开 `/ember_p1_stride_path`；无参 `/corerpg p1 stridepath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ05` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页或菜单优先（突进踏/后撤踏/卸轻技/清空/进本练步态/冒险可点，禁以 ConfirmTokens 教裸 `stridepath`/`enter`/`flexpath`/`steppath` 为主 UI）；清用法「手打 stridepath push|bail|bare|clear」教指令主文案；glance 去手打主教；可选新增 `stride_path_*` PAPI；单测
- W1b：薄页 `ember_p1_stride_path`：突进踏 / 后撤踏 / 卸轻技 / 清空（菜单 actions `stridepath push|bail|bare|clear`）/ 进本（按当前 enterCmd：PUSH→q05 / BAIL→q02 / BARE→runs 或冒险）/ 冒险 `ember_p1_adventure` / 可选工坊；lore 挂当前步态路径+tip；灰态诚实（需本人首进/首通 Q01+Q05）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 步态规则语义（D499 push/bail/bare 写轻技+身法） / Cast/Aid/variety / 已齐 settle·选打薄页（含 `ember_p1_counter_path`） / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath / FlexPath/StepPath/SidestepPath 等其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 13:04 Asia/Shanghai
- [x] 插件交 offerPick / applyAndReply / 无参 stridepath / 周要约改开菜单 + 清裸指令主路径 + 单测 · feat `3daefff99` · jar `1.65.837-d13344.local`
- [ ] 物品交薄 stride_path 页 + TrMenu reload
- [ ] 抽测 PASS live（选突进踏/后撤踏/卸轻技/清空/进本/冒险真开·PAPI·零抬伤害/CD表/掉落/体力价·零改 counter_path·零已齐选打同题复读）· **CLOSED by 总控**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · W1a插件已交（勿勾 CLOSED · 待测岗/物品W1b） · L不批 · D13344 · TIP-DM · 2026-10-14 Asia/Shanghai
