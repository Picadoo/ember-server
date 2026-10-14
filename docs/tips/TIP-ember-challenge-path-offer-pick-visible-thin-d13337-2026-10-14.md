# tip · TIP-DF ChallengePath.offerPick / applyAndReply「硬本路径选打」菜单化（选路径聊天钮去指令化 · ≠精选路径选打同题 · ≠挑战settle下一环同题 · ≠精选/图身份/深渊/团本/短征 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠RaidPath同窗）· 拟 D13337

> **状态：** **施工中 · W1a 已交待测** · APPROVED A·M · D13337 · TIP-DF · jar `1.65.830-d13337.local` · L不批 · 勿勾 CLOSED · 零改掉落/体力价 · 2026-10-14 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-challenge-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-challenge-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-challenge-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（ChallengePath.offerPick / applyAndReply / 无参 challengepath 改开菜单）+ 余烬-物品（薄页 `ember_p1_challenge_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零精选路径选打同题复读（TIP-DE） · 零挑战settle下一环同题复读（TIP-DA） · 零已齐 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬掉落/体力价 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫 · 零 RaidPath 同窗大扫
> **ancestor：** TIP-DE CLOSED live `678cd409a`（feat `1465d7622` · tip `c72e8a964` · PASS `687033a51` · jar `1.65.829-d13336.local` · 批 sha `c72e8a964` · menu tip `c161fff0d`）

## 一句话
周选/改硬本路径时一点开「硬本路径」薄页：偏挑战 / 偏普通 / 随令 / 清空 / 冒险（可选工坊·挑战选图）可点；清 ConfirmTokens 借 settle 页当选打主 UI；掉落·体力价·硬本语义不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberChallengePath.offerPick + applyAndReply + 无参 challengepath 入口** + 薄页 `ember_p1_challenge_path` 真开菜单选打 + 清 ConfirmTokens 教指令/借 settle 主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 精选路径选打同题（TIP-DE 已齐）· ≠ 挑战settle下一环同题（TIP-DA 已齐；`ember_p1_challenge_settle` 零改本窗）· ≠ 精选settle下一环同题（TIP-DD）· ≠ 图身份/深渊/团本/短征 settle 下一环同题 · ≠ 短征settle回看同题 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ RaidPath.offerPick 同窗 · ≠ 全族 *Path.offerPick 大扫

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 旁路 TIP-BY–CT **已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle下一环/soft-reward/settle回看 **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **已齐勿复读：** settle 下一环 CW–DD + 精选路径选打 TIP-DE **全齐**
4. **故落菜单去指令残留 · 只钉一支：** DESIGN-DE §6⑦ 明文后置其他 *Path.offerPick；实扫 `EmberChallengePath.offerPick` 仍 ConfirmTokens→`/ember_p1_challenge_settle`（借结算面当选打）；无参 `challengepath` + 冒险 Open L48 同入口；`applyAndReply` 下一步仍 ConfirmTokens→settle；用法仍教 `/corerpg p1 challengepath <prefer|easy|follow|clear>`；TrMenu **无** `ember_p1_challenge_path`（仅有 settle 混装路径格）→ 本窗钉死 **硬本选打面**（镜像 TIP-DE；≠ settle 同题；RaidPath 后置 §6）

## 施工钉死（批后）
- W1a：`EmberChallengePath.offerPick` → 开 `/ember_p1_challenge_path`；无参 `/corerpg p1 challengepath` 与冒险 Open 同开薄页（或等价）；`applyAndReply` 下一步→同薄页（冒险/重选/可选选图可点，禁借 settle ConfirmTokens 为主 UI）；清 nudge/用法「手打 challengepath …」教指令主文案；可选复用既有 `challenge_path_*` PAPI；单测
- W1b：薄页 `ember_p1_challenge_path`：偏挑战 / 偏普通 / 随令 / 清空（菜单 actions `challengepath prefer|easy|follow|clear`）/ 冒险 `ember_p1_adventure` / 可选挑战选图 `ember_p1_challenge` / 可选工坊 `ember_p1_forge`；lore 挂当前路径+解锁态；灰态诚实（需首通 Q07）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 掉落 / 体力价 / 硬本语义 / Cast/Aid/variety / 已齐 settle 薄页（含 `ember_p1_challenge_settle`）/ 庭内 / soft-reward / FeaturedPath / RaidPath.offerPick / 其他 *Path

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 10:59 Asia/Shanghai
- [x] 插件交 offerPick / applyAndReply / 无参 challengepath 改菜单 + 清裸指令主路径 + 单测 · jar `1.65.830-d13337.local` · 2026-10-14 Asia/Shanghai
- [x] 物品交薄 challenge_path 页 + TrMenu reload · W1b 并行已交
- [ ] 抽测 PASS live（选偏挑战/偏普通/随令/清空/冒险真开·PAPI·零抬掉落/体力价·零改 challenge_settle）· **勿勾 CLOSED**

## STATUS: **施工中 · W1a 已交待测** · APPROVED A·M · 勿勾 CLOSED · L不批 · D13337 · TIP-DF · 2026-10-14 Asia/Shanghai
