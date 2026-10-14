# tip · TIP-DK PledgePath.offerPick / applyAndReply「誓约路径选打」菜单化（选路径聊天钮去指令化 · ≠试炼路径选打同题 · ≠深渊/短征/团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价/誓约徽记表 · ≠全族 *Path.offerPick 大扫 · ≠破绽/步态同窗 · ≠复开 D481 玩法本体）· 拟 D13342

> **状态：** **待批 A** · 荐 M · L 不荐 · 2026-10-14 12:22 Asia/Shanghai · docs-only · 勿勾 CLOSED · 勿派施工
> **DESIGN** [`DESIGN-ember-pledge-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-pledge-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-pledge-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（PledgePath.offerPick / applyAndReply / 无参 pledgepath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_pledge_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零试炼路径选打同题复读（TIP-DJ） · 零深渊/短征/团本/硬本/精选路径选打同题复读（TIP-DI–DE） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价/誓约徽记表 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零破绽/步态同窗 · 零复开 D481 玩法本体
> **ancestor：** TIP-DJ CLOSED live `f63d095b2`（feat `845a99920` · tip `dbbc499b0` · PASS `61e500086` · jar `1.65.834-d13341.local` · 批 sha `dbbc499b0` · menu tip `9dabb653e`）
> **下一窗提示（旁注）：** 破绽/步态等 *Path.offerPick **逐支**（禁全族大扫）/ 挂机若新硬债 / legacy evacuate 后置

## 一句话
周选/改誓约路径时一点开「誓约路径」薄页：限药 / 逆行 / 双挂 / 清空 / 誓约页 / 进阶模式 / 冒险可点；清 ConfirmTokens 教 `pledgepath`/`pledge`/`modes` 主 UI；伤害·CD表·掉落·体力价·誓约规则语义不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberPledgePath.offerPick + applyAndReply + 无参 pledgepath / 周要约入口** + 薄页 `ember_p1_pledge_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 试炼路径选打同题（TIP-DJ 已齐；`ember_p1_trial_path` 零改本窗）· ≠ 深渊路径选打同题（TIP-DI）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价/誓约徽记表 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ 破绽/步态同窗 · ≠ 全族 *Path.offerPick 大扫 · ≠ 复开 `ember_p1_pledge` 自选规则开关页为选打主交付（本窗新建选打页；可链到既有誓约页）

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem`（仅 enhance/upgrade/refine gap）→ 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle 下一环 CW–DD + 精选/硬本/团本/短征/深渊/试炼路径选打 DE–DJ **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DJ §6⑦ 明文后置签律/破绽/步态等 *Path.offerPick 逐支；实扫三支均仍 ConfirmTokens 教裸指令；**择最硬一支 = PledgePath（誓约/签律）**：①列表首位 ②与刚 CLOSED 试炼内容相邻（Trial 写入誓约+破绽；Trial 历史入口亦教 pledgepath）③具备周要约 + Q06 `scheduleOfferAfterQ06` 全链（镜像 Trial）④TrMenu **有** `ember_p1_pledge`（规则开关）**无** `ember_p1_pledge_path` 选打页 → 本窗钉死 **誓约选打面**（镜像 TIP-DE–DJ）；破绽 `CounterPath` / 步态 `StridePath` **后置本窗**（逐支；禁全族大扫）

## 施工钉死（批后）
- W1a：`EmberPledgePath.offerPick` → 开 `/ember_p1_pledge_path`；无参 `/corerpg p1 pledgepath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ06` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页或菜单优先（限药/逆行/双挂/清空/誓约页 `ember_p1_pledge`/进阶 `ember_p1_modes`/冒险可点，禁以 ConfirmTokens 教裸 `pledgepath`/`pledge`/`modes` 为主 UI）；清用法「手打 pledgepath lean|reverse|both|clear」教指令主文案；glance/nudge 去手打主教；可选新增 `pledge_path_*` PAPI；单测
- W1b：薄页 `ember_p1_pledge_path`：限药 / 逆行 / 双挂 / 清空（菜单 actions `pledgepath lean|reverse|both|clear`）/ 誓约页 `ember_p1_pledge` / 进阶模式 `ember_p1_modes` / 冒险 `ember_p1_adventure` / 可选工坊；lore 挂当前誓约路径+tip；灰态诚实（需首通 Q06 / 自选誓约解锁）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 誓约规则语义（D481 lean/reverse pool） / Cast/Aid/variety / 已齐 settle·选打薄页（含 `ember_p1_trial_path`） / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath / CounterPath·StridePath 等其他 *Path / `ember_p1_pledge` Layout 主交付（仅可被链入）

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [ ] 总控批 A·M
- [ ] 插件交 offerPick / applyAndReply / 无参 pledgepath / 周要约改开菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 pledge_path 页 + TrMenu reload
- [ ] 抽测 PASS live（选限药/逆行/双挂/清空/誓约页/进阶/冒险真开·PAPI·零抬伤害/CD表/掉落/体力价/誓约徽记表·零改 trial_path·零已齐选打同题复读）

## STATUS: **待批 A** · 荐 M · L 不荐 · 拟 D13342 · TIP-DK · 2026-10-14 12:22 Asia/Shanghai · ancestor TIP-DJ CLOSED `f63d095b2`
