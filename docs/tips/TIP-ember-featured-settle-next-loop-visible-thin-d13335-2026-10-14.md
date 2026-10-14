# tip · TIP-DD 精选周帽满 D794 / FeaturedPath.afterSettle「精选下一环」菜单化（settle 聊天钮去指令化 · ≠图身份首通settle下一环同题 · ≠深渊settle下一环同题 · ≠挑战settle下一环同题 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬掉落/体力价/周帽 · ≠D673同题复读）· 拟 D13335

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13335 · TIP-DD · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改掉落/体力价/周帽 · afterSettle+D794→开 ember_p1_featured_settle · 清 ConfirmTokens 教 enter/featurepath/forge 主 UI · 2026-10-14 10:22 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-featured-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-featured-settle-next-loop-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-featured-settle-next-loop-visible`
> **施工岗（批后）：** 余烬-插件（FeaturedPath.afterSettle + Settle D794 下一环改开菜单）+ 余烬-物品（薄页 `ember_p1_featured_settle`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零图身份/深渊/短征/团本/挑战 settle 同题复读 · 零 D673 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬掉落/体力价/周帽 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察
> **ancestor：** TIP-DC CLOSED live `d5e5185a3` · feat `e1eb0f9b2` · tip `0a92cb3c8` · PASS `a0fcf6d66` · jar `1.65.827-d13334.local` · 批 sha `0a92cb3c8`

## 一句话
精选图结算 / 周帽满后一点开「精选下一环」：再进精选同图、换精选路径、换硬本路径、冒险、工坊、图装追可点；清 ConfirmTokens 教 `enter`/`featurepath`/`challengepath`/`forge`/`mapfarm` 主 UI；掉落·体力价·周帽不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberFeaturedPath.afterSettle + EmberSettleService D794 下一环入口** + 薄页 `ember_p1_featured_settle` 真开菜单去向 + 清 ConfirmTokens 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 图身份首通settle下一环同题（TIP-DC 已齐）· ≠ 深渊settle下一环同题（TIP-DB 已齐）· ≠ 挑战settle下一环同题（TIP-DA 已齐）· ≠ 团本settle下一环同题（TIP-CZ 已齐）· ≠ 短征settle下一环同题（TIP-CW 已齐）· ≠ 短征settle回看同题（TIP-CY 已齐）· ≠ D673 同题复读 · ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬掉落/体力价/周帽 · ≠ 教裸指令 · ≠ legacy evacuate 大扫

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 实扫旁路（强化/升阶/精工/成色/推荐花/拆/换/洗练/烙纹/转化/随机锻）**已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**（近饱和）
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle下一环/soft-reward/settle回看 **已齐**；sx20 **硬禁** → **无薄窗新硬债**
3. **故落 §6⑤：** DESIGN-DC 明文后置「精选周帽满 D794 / FeaturedPath.afterSettle」；`afterSettle` ConfirmTokens 仍教 enter/forge/mapfarm；Settle D794 仍教 featurepath/challengepath；**无** `ember_p1_featured_settle` → 本窗钉死 D794 一支

## 施工钉死（批后）
- W1a：`EmberFeaturedPath.afterSettle` 下一环→开 `/ember_p1_featured_settle`；`EmberSettleService` D794 周帽满「下一步」→同薄页（或等价 open）；记住 settle 图/路径/周帽态；清以 ConfirmTokens 教 `/corerpg p1 enter …` / `featurepath` / `challengepath` / `forge` / `mapfarm` 为主 UI；可选 `featured_settle_*` PAPI；单测
- W1b：薄页 `ember_p1_featured_settle`：再进精选同图（菜单 actions 调既有 enter）/ 换精选路径（菜单 actions `featurepath` 别名或薄格）/ 换硬本路径（菜单 actions `challengepath`）/ 冒险 `ember_p1_adventure` / 工坊 `ember_p1_forge` / 图装追（菜单 actions mapfarm）；lore 挂精选图名+路径+周帽；灰态诚实（未选路径/周帽已满）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 掉落 / 体力价 / 周帽数值 / 精选语义 / Cast/Aid/variety / 短征·团本·挑战·深渊·图身份薄页 / 庭内 / soft-reward

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 10:22 Asia/Shanghai
- [ ] 插件交 FeaturedPath.afterSettle + D794 入口改菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 featured settle 页 + TrMenu reload
- [ ] 抽测 PASS（再进/换路径/冒险/工坊真开·PAPI·零抬掉落/体力价/周帽）· **勿勾本窗 CLOSED**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L不批 · D13335 · TIP-DD · 2026-10-14 10:22 Asia/Shanghai
