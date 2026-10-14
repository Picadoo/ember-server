# tip · TIP-DJ TrialPath.offerPick / applyAndReply「试炼路径选打」菜单化（选路径聊天钮去指令化 · ≠深渊路径选打同题 · ≠短征/团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠签律/破绽/步态同窗）· 拟 D13341

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13341 · TIP-DJ · W1a插件已交 · 待W1b物品+抽测 · L不批 · 勿勾 CLOSED · 零改伤害/CD表/掉落/体力价 · offerPick/applyAndReply/无参 trialpath/周要约→开 ember_p1_trial_path · 清 ConfirmTokens 教 trialpath 主 UI · 2026-10-14 12:11 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-trial-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-trial-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-trial-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（TrialPath.offerPick / applyAndReply / 无参 trialpath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_trial_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零深渊路径选打同题复读（TIP-DI） · 零短征/团本/硬本/精选路径选打同题复读（TIP-DH/DG/DF/DE） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬伤害/CD表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零签律/破绽/步态同窗
> **ancestor：** TIP-DI CLOSED live `326f7d3bd`（feat `724f53462` · tip `39e38c8b1` · PASS `22aed762e` · jar `1.65.833-d13340.local` · 批 sha `39e38c8b1` · menu tip `27541e2cb`）

## 一句话
周选/改试炼路径时一点开「试炼路径」薄页：稳慎 / 猛开 / 加压 / 清空 / 冒险（可选工坊·进本）可点；清 ConfirmTokens 教 `trialpath`/进本主 UI；伤害·CD表·掉落·体力价不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberTrialPath.offerPick + applyAndReply + 无参 trialpath / 周要约入口** + 薄页 `ember_p1_trial_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 深渊路径选打同题（TIP-DI 已齐；`ember_p1_abyss_path` 零改本窗）· ≠ 短征路径选打同题（TIP-DH）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬伤害/CD表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ 签律/破绽/步态同窗 · ≠ 全族 *Path.offerPick 大扫

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/路径选打 TIP-DH **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle 下一环 CW–DD + 精选/硬本/团本/短征/深渊路径选打 DE–DI **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DI §6⑦ 明文后置 TrialPath 等逐支；实扫 `EmberTrialPath.offerPick` 仍 ConfirmTokens→`/corerpg p1 trialpath cautious|aggressive|pressure|clear`；无参 `trialpath` + 周要约 `maybeOfferWeekly`/`scheduleOfferAfterQ06` 同入口；`applyAndReply` 下一步仍 ConfirmTokens（进本/誓约路径/破绽路径/换一条）；用法仍教手打；glance 仍教 `/corerpg p1 trialpath`；TrMenu **无** `ember_p1_trial_path`（亦无其他 trial 选打页）→ 本窗钉死 **试炼选打面**（镜像 TIP-DE–DI；≠ 深渊选打同题）；签律/破绽/步态等 *Path **后置本窗**

## 施工钉死（批后）
- W1a：`EmberTrialPath.offerPick` → 开 `/ember_p1_trial_path`；无参 `/corerpg p1 trialpath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ06` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页（稳慎/猛开/加压/清空/进本/可选冒险·工坊可点，禁以 ConfirmTokens 教裸 `trialpath`/`pledgepath`/`counterpath` 为主 UI）；清用法「手打 trialpath cautious|aggressive|pressure|clear」教指令主文案；可选新增 `trial_path_*` PAPI；单测
- W1b：薄页 `ember_p1_trial_path`：稳慎 / 猛开 / 加压 / 清空（菜单 actions `trialpath cautious|aggressive|pressure|clear`）/ 冒险 `ember_p1_adventure` / 可选工坊 `ember_p1_forge` / 可选进本（按当前试炼 enter）；lore 挂当前试炼+tip；灰态诚实（需首通 Q06 / 自选誓约解锁）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 伤害 / CD 表 / 掉落 / 体力价 / 试炼语义（誓约+破绽写入规则） / Cast/Aid/variety / 已齐 settle 薄页 / 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath / 签律·破绽·步态等其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 12:10 Asia/Shanghai
- [x] 插件交 offerPick / applyAndReply / 无参 trialpath / 周要约改开菜单 + 清裸指令主路径 + 单测 · W1a `1.65.834-d13341.local`
- [ ] 物品交薄 trial_path 页 + TrMenu reload
- [ ] 抽测 PASS live（选稳慎/猛开/加压/清空/冒险真开·PAPI·零抬伤害/CD表/掉落/体力价·零改 abyss_path·零已齐选打同题复读）· **勿勾 CLOSED**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · W1a插件已交 · 待W1b物品+抽测 · 勿勾 CLOSED · L不批 · D13341 · TIP-DJ · 2026-10-14 12:11 Asia/Shanghai
