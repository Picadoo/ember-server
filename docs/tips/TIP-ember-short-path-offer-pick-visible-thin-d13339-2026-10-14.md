# tip · TIP-DH ShortPath.offerPick / applyAndReply「短征路径选打」菜单化（选路径聊天钮去指令化 · ≠路径追同题 · ≠短征settle下一环同题 · ≠短征settle回看同题 · ≠团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬日表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠AbyssPath/TrialPath同窗）· 拟 D13339

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13339 · TIP-DH · W1a插件已交（勿勾 CLOSED）· 待W1b物品+抽测 · L不批 · 零改日表/掉落/体力价/resolve · offerPick/无参 shortpath/周要约→开 ember_p1_short_path · 清 ConfirmTokens 教 shortpath/enter 主 UI · 2026-10-14 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-short-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-short-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（ShortPath.offerPick / 无参 shortpath / 周要约改开菜单）+ 余烬-物品（既有 `ember_p1_short_path` 接线/补洞；清选页 Open 旁路）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零路径追同题复读（TIP-CV） · 零短征settle下一环同题复读（TIP-CW） · 零短征settle回看同题复读（TIP-CY） · 零团本/硬本/精选路径选打同题复读（TIP-DG/DF/DE） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬日表/掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零 AbyssPath/TrialPath 同窗
> **ancestor：** TIP-DG CLOSED live `f8fef8025`（feat `c81c9e7e2` · tip `fd80fd696` · PASS `7ec45e0a1` · jar `1.65.831-d13338.local` · 批 sha `fd80fd696` · menu tip `b9b8f688f`）

## 一句话
周选/改短征路径时一点开已有「路径追」薄页：首通追 / 有奖追 / 后段追 / 清空 / 进下一本 / 短征选页（可选冒险·工坊）可点；清 ConfirmTokens 教 `shortpath`/`enter` 主 UI；日表·掉落·体力价·resolve 不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberShortPath.offerPick + 无参 shortpath + 周要约入口** → 开已有 `ember_p1_short_path` + 清 ConfirmTokens 教指令主路径 + W1b 接线/补洞（非必新建薄页）
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 路径追同题（TIP-CV 已齐；薄页 `ember_p1_short_path` **已在**；本窗=入口接线≠新建路径追）· ≠ 短征settle下一环同题（TIP-CW 已齐；`ember_p1_short_settle` 零改本窗）· ≠ 短征settle回看同题（TIP-CY）· ≠ soft-reward同题 · ≠ 日差同题 · ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ variety再拧 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬日表/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ AbyssPath/TrialPath 同窗 · ≠ 全族 *Path.offerPick 大扫

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追**页**/settle下一环/soft-reward/settle回看 **已齐**；sx20 **硬禁**；但 **offerPick / 周要约 / 无参 shortpath / 选页 Open** 仍 ConfirmTokens 教裸 `shortpath` → **仍有薄窗硬债**（≠路径追同题）
3. **已齐勿复读：** settle 下一环 CW–DD + 精选/硬本/团本路径选打 DE–DG **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DG §6⑦ 后置其他 *Path 逐支；内容优先钉 **短征**（非试炼）：薄页已在 TIP-CV，settle「换一条」已 `SETTLE_PATH_CMD=/ember_p1_short_path`，唯 offerPick/周要约/无参/选页 Open 未开薄页 → W1b 多半是接线/补洞非新建；AbyssPath/TrialPath **后置本窗**

## 施工钉死（批后）
- W1a：`EmberShortPath.offerPick` → 开 `/ember_p1_short_path`；无参 `/corerpg p1 shortpath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ01` 同开薄页（或等价）；`applyAndReply` 下一步禁以裸 `enter` ConfirmTokens 为主 UI（进本可走薄页「进下一本」/选页；「换一条」已指薄页可保持）；清用法「手打 shortpath fc|day|late|clear」教指令主文案；可选复用既有 `short_path_*` PAPI；单测
- W1b：既有薄页 `ember_p1_short_path` **接线/补洞**（非必新建）：确认首通追/有奖追/后段追/清空/进下一本/回选页真开；清选页 `ember_p1_short` Open 旁路 `command: corerpg p1 shortpath`（避免 Open 再弹 ConfirmTokens）；lore/灰态若缺则补（需首通 Q01）；可选冒险/工坊入口
- W1c：同批辅指针若有 → **不可独批**
- **不改** 日表 / 掉落 / 体力价 / ShortPath resolve / Cast/Aid/variety / 已齐 settle 薄页（含 `ember_p1_short_settle`）/ 庭内 / soft-reward / RaidPath/FeaturedPath/ChallengePath / AbyssPath/TrialPath / 其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无开薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 11:32 Asia/Shanghai
- [x] 插件交 offerPick / 无参 shortpath / 周要约改开菜单 + 清裸指令主路径 + 单测 · W1a `1.65.832-d13339.local` · 2026-10-14 Asia/Shanghai
- [ ] 物品交 short_path 接线/补洞 + 清选页 Open 旁路 + TrMenu reload
- [ ] 抽测 PASS（选首通/有奖/后段/清空/进下一本真开·PAPI·零抬日表/掉落/体力价·零改 short_settle·零路径追同题复读）

## STATUS: **已批 A · 方案 M** · APPROVED A·M · W1a插件已交 · 待W1b物品+抽测 · 勿勾 CLOSED · L不批 · D13339 · TIP-DH · 2026-10-14 Asia/Shanghai
