# tip · TIP-DG RaidPath.offerPick / applyAndReply「团本路径选打」菜单化（选路径聊天钮去指令化 · ≠硬本路径选打同题 · ≠精选路径选打同题 · ≠团本settle下一环同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬掉落/体力价/周帽 · ≠全族 *Path.offerPick 大扫 · ≠FeaturedPath/ChallengePath同窗）· 拟 D13338

> **状态：** **抽测 PASS live · 待总控收口** · APPROVED A·M · D13338 · TIP-DG · jar `1.65.831-d13338.local` · L不批 · **勿勾 CLOSED** · 零改掉落/体力价/周帽 · 2026-10-14 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-raid-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-raid-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-raid-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（RaidPath.offerPick / applyAndReply / 无参 raidpath 改开菜单）+ 余烬-物品（薄页 `ember_p1_raid_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零硬本路径选打同题复读（TIP-DF） · 零精选路径选打同题复读（TIP-DE） · 零团本settle下一环同题复读（TIP-CZ） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬掉落/体力价/周帽 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零 FeaturedPath/ChallengePath 同窗
> **ancestor：** TIP-DF CLOSED live `6b1fc942d`（feat `5927286ca` · tip `17d0f7ddf` · PASS `6d98cb62a` · jar `1.65.830-d13337.local` · 批 sha `17d0f7ddf` · menu tip `bebeb7f17`）

## 一句话
周选/改团本路径时一点开「团本路径」薄页：R01 锈轨 / R02 霜封 / R03 断塔 / 清空 / 冒险（可选工坊·团本大厅）可点；清 ConfirmTokens 借 settle 页当选打主 UI；掉落·体力价·周帽·团本语义不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberRaidPath.offerPick + applyAndReply + 无参 raidpath 入口** + 薄页 `ember_p1_raid_path` 真开菜单选打 + 清 ConfirmTokens 教指令/借 settle 主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 硬本路径选打同题（TIP-DF 已齐）· ≠ 精选路径选打同题（TIP-DE 已齐）· ≠ 团本settle下一环同题（TIP-CZ 已齐；`ember_p1_raid_settle` 零改本窗）· ≠ 挑战/精选/图身份/深渊/短征 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬掉落/体力价/周帽 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ FeaturedPath/ChallengePath 同窗 · ≠ 全族 *Path.offerPick 大扫

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle下一环/soft-reward/settle回看 **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle 下一环 CW–DD + 精选路径选打 TIP-DE + 硬本路径选打 TIP-DF **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DF §6⑦ 明文后置本支；实扫 `EmberRaidPath.offerPick` 仍 ConfirmTokens→`/ember_p1_raid_settle`（借结算面当选打）；无参 `raidpath` + 冒险 Open L49 同入口；`applyAndReply` 下一步仍 ConfirmTokens→settle；用法仍教 `/corerpg p1 raidpath <r01|r02|r03|clear>`；TrMenu **有** `ember_p1_raid_settle` / `ember_raid*`，**无** `ember_p1_raid_path` → 本窗钉死 **团本选打面**（镜像 TIP-DE/DF；≠ settle 同题；≠ 硬本/精选选打同题）

## 施工钉死（批后）
- W1a：`EmberRaidPath.offerPick` → 开 `/ember_p1_raid_path`；无参 `/corerpg p1 raidpath` 与冒险 Open 同开薄页（或等价）；`applyAndReply` 下一步→同薄页（冒险/重选/可选进本·工坊可点，禁借 settle ConfirmTokens 为主 UI）；清 nudge/用法「手打 raidpath …」教指令主文案；可选复用既有 `raid_path_*` PAPI；单测
- W1b：薄页 `ember_p1_raid_path`：R01 锈轨 / R02 霜封 / R03 断塔 / 清空（菜单 actions `raidpath r01|r02|r03|clear`）/ 冒险 `ember_p1_adventure` / 可选团本大厅 `ember_raid` / 可选工坊 `ember_p1_forge`；lore 挂当前路径+解锁态；灰态诚实（需首通 Q07）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 掉落 / 体力价 / 周帽 / 团本语义 / Cast/Aid/variety / 已齐 settle 薄页（含 `ember_p1_raid_settle`）/ 庭内 / soft-reward / FeaturedPath / ChallengePath / 其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 11:16 Asia/Shanghai
- [x] 插件交 offerPick / applyAndReply / 无参 raidpath 改菜单 + 清裸指令主路径 + 单测 · jar `1.65.831-d13338.local` · 2026-10-14 Asia/Shanghai
- [x] 物品交薄 raid_path 页 + TrMenu reload · W1b 并行已交 `b9b8f688f`
- [x] 抽测 PASS live（选 R01/R02/R03/清空/冒险真开·PAPI·零抬掉落/体力价/周帽·零改 raid_settle）· **勿勾 CLOSED** · 证据 `/workspace/tmp/tip-dg-raid-path/` · 2026-10-14 Asia/Shanghai

## STATUS: **抽测 PASS live · 待总控收口** · APPROVED A·M · 勿勾 CLOSED · L不批 · D13338 · TIP-DG · 2026-10-14 Asia/Shanghai
