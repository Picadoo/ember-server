# tip · TIP-DE FeaturedPath.offerPick / applyAndReply「精选路径选打」菜单化（选路径聊天钮去指令化 · ≠精选结算下一环同题 · ≠图身份/深渊/挑战/团本/短征 settle 下一环同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬掉落/体力价/周帽 · ≠全族 *Path.offerPick 大扫）· 拟 D13336

> **状态：** **CLOSED live** · feat `1465d7622` · tip `c72e8a964` · jar `1.65.829-d13336.local` · PASS `687033a51` · 批 sha `c72e8a964` · 证据 `/workspace/tmp/tip-de-featured-path/` · 2026-10-14 10:57 Asia/Shanghai · 精选路径选打菜单化同题勿再交
> **DESIGN** [`DESIGN-ember-featured-path-offer-pick-visible-2026-10-14.md`](../design/DESIGN-ember-featured-path-offer-pick-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-featured-path-offer-pick-visible`
> **施工岗（批后）：** 余烬-插件（FeaturedPath.offerPick / applyAndReply / 无参 featurepath 改开菜单）+ 余烬-物品（薄页 `ember_p1_featured_path`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零精选结算下一环同题复读（TIP-DD） · 零图身份/深渊/短征/团本/挑战 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬掉落/体力价/周帽 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察 · 零全族 *Path.offerPick 大扫
> **ancestor：** TIP-DD CLOSED live `36ecc1b43` · feat `b07074070` · tip `b7160ee19` · PASS `f864e36b9` · jar `1.65.828-d13335.local` · 批 sha `b7160ee19` · menu tip `7a098dda6`

## 一句话
周选/改精选打法时一点开「精选路径」薄页：精选·挑战 / 精选·普通 / 清空 / 进本周精选 / 冒险（可选工坊）可点；清 ConfirmTokens 教 `featurepath`/`enter` 主 UI；掉落·体力价·周帽·精选语义不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberFeaturedPath.offerPick + applyAndReply + 无参 featurepath 入口** + 薄页 `ember_p1_featured_path` 真开菜单选打 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 精选结算下一环同题（TIP-DD 已齐）· ≠ 图身份首通settle下一环同题（TIP-DC 已齐）· ≠ 深渊settle下一环同题（TIP-DB 已齐）· ≠ 挑战settle下一环同题（TIP-DA 已齐）· ≠ 团本settle下一环同题（TIP-CZ 已齐）· ≠ 短征settle下一环同题（TIP-CW 已齐）· ≠ 短征settle回看同题（TIP-CY 已齐）· ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬掉落/体力价/周帽 · ≠ 教裸指令 · ≠ legacy evacuate 大扫 · ≠ 全族 *Path.offerPick 大扫（试炼/签律/步态等另窗）

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 实扫旁路（强化/升阶/精工/成色/推荐花/拆/换/洗练/烙纹/转化/随机锻）**已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**（近饱和）
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle下一环/soft-reward/settle回看 **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **其余 settle 聊天钮：** D673/D794 **已齐勿复读**（短征/团本/挑战/深渊/图身份/精选 settle 下一环全齐）
4. **故落 DESIGN-DD §6⑤：** 明文后置「FeaturedPath.offerPick / glance 全链教指令大扫」；`offerPick` ConfirmTokens 仍教 `featurepath challenge|normal|clear`；`applyAndReply` 仍教裸 `enter`/`featurepath`；冒险 Open 仍 `corerpg p1 featurepath` → 聊天选；**无** `ember_p1_featured_path` → 本窗钉死 **选打面**（≠ settle 同题）

## 施工钉死（批后）
- W1a：`EmberFeaturedPath.offerPick` → 开 `/ember_p1_featured_path`；无参 `/corerpg p1 featurepath` 与冒险 Open 同开薄页（或等价）；`applyAndReply` 下一步→同薄页（进本/冒险/重选可点，禁裸 enter ConfirmTokens 为主 UI）；清 glance「/corerpg p1 featurepath」教指令主文案；可选 `featured_path_*` PAPI；单测
- W1b：薄页 `ember_p1_featured_path`：精选·挑战 / 精选·普通 / 清空（菜单 actions `featurepath …`）/ 进本周精选（菜单 actions 调既有 enter）/ 冒险 `ember_p1_adventure` / 可选工坊 `ember_p1_forge`；lore 挂本周精选图名+当前路径+解锁态；灰态诚实（未首通 Q01 / 挑战需 Q07）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 掉落 / 体力价 / 周帽数值 / 精选语义 / Cast/Aid/variety / 已齐 settle 薄页（含 `ember_p1_featured_settle`）/ 庭内 / soft-reward / 其他 *Path.offerPick

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 10:41 Asia/Shanghai
- [x] 插件交 offerPick / applyAndReply / 无参 featurepath 改菜单 + 清裸指令主路径 + 单测 · jar `1.65.829-d13336.local` · 2026-10-14 Asia/Shanghai
- [x] 物品交薄 featured_path 页 + TrMenu reload · W1b `c161fff0d`
- [x] 抽测 PASS live（选挑战/普通/清空/进本/冒险真开·PAPI·零抬掉落/体力价/周帽）· STATUS-ember-featured-path-offer-pick-tip-de-spot-d13336-2026-10-14.md · PASS `687033a51` · 证据 `/workspace/tmp/tip-de-featured-path/` · **CLOSED by 总控**

## STATUS: **CLOSED live** · `1465d7622` / `c161fff0d` · PASS `687033a51` · jar `1.65.829-d13336.local` · D13336 · TIP-DE · 2026-10-14 10:57 Asia/Shanghai · 精选路径选打菜单化同题勿再交
