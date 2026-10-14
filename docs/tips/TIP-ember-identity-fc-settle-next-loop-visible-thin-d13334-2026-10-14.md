# tip · TIP-DC 图身份首通结算「下一环」菜单化（settle 聊天钮去指令化 · ≠深渊settle下一环同题 · ≠挑战settle下一环同题 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠精选周帽满D794同窗 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬掉落/体力价）· 拟 D13334

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13334 · TIP-DC · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改掉落/体力价/首通包 · EmberSettleService D673→开 ember_p1_identity_settle · 清 ConfirmTokens 教 forge 主 UI · D794 后置 · 2026-10-14 09:55 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-identity-fc-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-identity-fc-settle-next-loop-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-identity-fc-settle-next-loop-visible`
> **施工岗（批后）：** 余烬-插件（EmberSettleService 图身份首通 D673 下一环改开菜单）+ 余烬-物品（薄页 `ember_p1_identity_settle`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零深渊/短征/团本/挑战 settle 同题复读 · 零 D794 精选周帽满同窗 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬掉落/体力价/首通包 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察
> **ancestor：** TIP-DB CLOSED live `fd1152e1f` · feat `6e22b5d2f` · tip `8a3f93564` · PASS `4503d824e` · jar `1.65.826-d13333.local` · 批 sha `8a3f93564`

## 一句话
图身份首通保底庆祝后一点开「下一环」：工坊/装备/冒险（可选图装追）可点；清 ConfirmTokens 教 `/corerpg p1 forge` 主 UI；掉落·体力价·首通包不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberSettleService 图身份首通（D673）下一环入口** + 薄页 `ember_p1_identity_settle` 真开菜单去向 + 清 ConfirmTokens 三钮教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 深渊settle下一环同题（TIP-DB 已齐）· ≠ 挑战settle下一环同题（TIP-DA 已齐）· ≠ 团本settle下一环同题（TIP-CZ 已齐）· ≠ 短征settle下一环同题（TIP-CW 已齐）· ≠ 短征settle回看同题（TIP-CY 已齐）· ≠ 精选周帽满 D794 同窗（§6 另支）· ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬掉落/体力价 · ≠ 教裸指令 · ≠ legacy evacuate 大扫

## 选题为何合法（改题有证据）
1. **挂机庭资源环：** 实扫旁路（强化/升阶/精工/成色/推荐花/拆/换/洗练/烙纹/转化/随机锻）**已齐**；afk 面 **零** `recipe_gap_gem`/`forge_gap_gem` → 宝石同构未过 → **无新硬债**（近饱和）
2. **短征日更乐趣：** Cast/Aid/todayRot/variety/日差/路径追/settle下一环/soft-reward/settle回看 **已齐**；sx20 **硬禁**；sx15 非薄窗 → **无薄窗新硬债**
3. **故落 §6⑥：** 图身份首通 D673 ConfirmTokens 仍教 `/corerpg p1 forge` + 装备/冒险；**无** `ember_p1_identity_settle` → 本窗钉死 D673 一支；D794 后置

## 施工钉死（批后）
- W1a：`EmberSettleService` 图身份首通（firstClear + 有效 family）下一环→开 `/ember_p1_identity_settle`（或等价 open）；记住 settle 图/族/槽；清以 ConfirmTokens 三钮（`/corerpg p1 forge` / `/ember_p1_gear` / `/ember_p1_adventure`）为主 UI；可选 `identity_settle_*` PAPI；单测
- W1b：薄页 `ember_p1_identity_settle`：工坊 `ember_p1_forge` / 装备 `ember_p1_gear` / 冒险 `ember_p1_adventure` / 可选图装追 `mapfarm`（菜单 actions）；lore 挂图名+族+槽诚实
- W1c：同批辅指针若有 → **不可独批**
- **不改** 掉落 / 体力价 / 首通包 / 图身份语义 / Cast/Aid/variety / 短征·团本·挑战·深渊薄页 / 庭内 / soft-reward / D794

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 09:55 Asia/Shanghai
- [x] 插件交图身份首通 settle 入口改菜单 + 清裸指令主路径 + 单测 · W1a 2026-10-14
- [ ] 物品交薄 identity settle 页 + TrMenu reload
- [ ] 抽测 PASS live（工坊/装备/冒险真开·PAPI·零抬掉落/体力价）· **勿勾 CLOSED**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L不批 · D13334 · TIP-DC · 2026-10-14 09:55 Asia/Shanghai
