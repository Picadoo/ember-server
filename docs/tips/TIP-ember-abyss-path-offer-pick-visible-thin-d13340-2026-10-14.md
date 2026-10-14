# tip · TIP-DI AbyssPath.offerPick / applyAndReply「深渊路径选打」菜单化（选路径聊天钮去指令化 · ≠深渊settle下一环同题 · ≠短征路径选打同题 · ≠团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬层费/强度/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠TrialPath同窗）· 拟 D13340

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13340 · TIP-DI · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改层费/强度/掉落/体力价/层表 · offerPick/applyAndReply/无参 abysspath/周要约→开 ember_p1_abyss_path · 清 ConfirmTokens 教 abysspath 主 UI · 2026-10-14 11:50 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-abyss-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-abyss-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-abyss-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（AbyssPath.offerPick / applyAndReply / 无参 abysspath / 周要约改开菜单）+ 余烬-物品（薄页 `ember_p1_abyss_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零深渊settle下一环同题复读（TIP-DB） · 零短征路径选打同题复读（TIP-DH） · 零团本/硬本/精选路径选打同题复读（TIP-DG/DF/DE） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬层费/强度/掉落/体力价/层表 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零 TrialPath 同窗
> **ancestor：** TIP-DH CLOSED live `016bfef1f`（feat `ec5264ef9` · tip `fbe2cce5c` · PASS `191f0be8c` · jar `1.65.832-d13339.local` · 批 sha `fbe2cce5c` · menu tip `9a81a840e`）

## 一句话
周选/改深渊路径时一点开「深渊路径」薄页：冲层 / 刷层 / 清空 / 深渊表 / 冒险（可选工坊）可点；清 ConfirmTokens 教 `abysspath`/进本主 UI；层费·强度·掉落·体力价·层表不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberAbyssPath.offerPick + applyAndReply + 无参 abysspath / 周要约入口** + 薄页 `ember_p1_abyss_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 深渊settle下一环同题（TIP-DB 已齐；`ember_p1_abyss_settle` 零改本窗）· ≠ 短征路径选打同题（TIP-DH 已齐）· ≠ 团本路径选打同题（TIP-DG）· ≠ 硬本路径选打同题（TIP-DF）· ≠ 精选路径选打同题（TIP-DE）· ≠ 已齐 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬层费/强度/掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ TrialPath 同窗 · ≠ 全族 *Path.offerPick 大扫

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/**路径选打 TIP-DH** **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle 下一环 CW–DD + 精选/硬本/团本/短征路径选打 DE–DH **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DH §6⑦ 明文后置 Abyss/Trial 逐支；实扫 `EmberAbyssPath.offerPick` 仍 ConfirmTokens→`/corerpg p1 abysspath push|farm|clear`；无参 `abysspath` + 周要约 `maybeOfferWeekly`/`scheduleOfferAfterQ07` 同入口；`applyAndReply`「换一条」仍 `/corerpg p1 abysspath`、进本/深渊表仍 ConfirmTokens；用法仍教手打；TrMenu **有** `ember_p1_abyss` / `ember_p1_abyss_settle`，**无** `ember_p1_abyss_path` → 本窗钉死 **深渊选打面**（镜像 TIP-DE–DH；≠ settle 同题；≠ 短征选打同题）；TrialPath **后置本窗**

## 施工钉死（批后）
- W1a：`EmberAbyssPath.offerPick` → 开 `/ember_p1_abyss_path`；无参 `/corerpg p1 abysspath` 与周要约 `maybeOfferWeekly` / `scheduleOfferAfterQ07` 同开薄页（或等价）；`applyAndReply` 下一步→同薄页（冲层进本/深渊表/重选/可选冒险·工坊可点，禁以 ConfirmTokens 教裸 `abysspath`/`abyss` 为主 UI）；清用法「手打 abysspath push|farm|clear」教指令主文案；可选复用既有 `abyss_path_*` PAPI；单测
- W1b：薄页 `ember_p1_abyss_path`：冲层 / 刷层 / 清空（菜单 actions `abysspath push|farm|clear`）/ 深渊表 `ember_p1_abyss` / 冒险 `ember_p1_adventure` / 可选工坊 `ember_p1_forge`；lore 挂当前路径+最高通关/可开层；灰态诚实（需首通 Q07）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 层费 / 强度 / 掉落 / 体力价 / 层表 / Cast/Aid/variety / 已齐 settle 薄页（含 `ember_p1_abyss_settle`）/ 庭内 / soft-reward / ShortPath/RaidPath/FeaturedPath/ChallengePath / TrialPath / 其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 11:50 Asia/Shanghai
- [ ] 插件交 offerPick / applyAndReply / 无参 abysspath / 周要约改开菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 abyss_path 页 + TrMenu reload
- [ ] 抽测 PASS live（选冲层/刷层/清空/深渊表/冒险真开·PAPI·零抬层费/强度/掉落/体力价·零改 abyss_settle·零短征选打同题复读）· **勿勾 CLOSED**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L不批 · D13340 · TIP-DI · 2026-10-14 11:50 Asia/Shanghai
