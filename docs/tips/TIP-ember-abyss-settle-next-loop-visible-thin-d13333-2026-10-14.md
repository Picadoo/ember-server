# tip · TIP-DB 深渊结算「下一环」菜单化（settle/evacuate 去指令化 · ≠挑战settle下一环同题 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠variety再拧 · ≠soft-reward同题 · ≠路径追同题 · ≠日差同题 · ≠半行 · ≠庭内旁路 · ≠抬深渊掉落/体力价/层表/日帽）· 拟 D13333

> **状态：** **已批 A · 方案 M** · APPROVED A·M · D13333 · TIP-DB · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改深渊掉落/体力价/层表/日帽 · EmberSettleService 深渊 newBest→开 ember_p1_abyss_settle · 清 ConfirmTokens/Open 教 abysstarget·forge 主 UI · 2026-10-14 09:34 Asia/Shanghai
> **DESIGN** [`DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md`](../design/DESIGN-ember-abyss-settle-next-loop-visible-2026-10-14.md) · RESEARCH 同日 · backlog `B-abyss-settle-next-loop-visible`
> **施工岗（批后）：** 余烬-插件（深渊 settle 下一环改开菜单 + 清 abysstarget/forge 教指令主 UI）+ 余烬-物品（薄页 `ember_p1_abyss_settle`）· **不派怪物/地图**
> **硬禁：** 零半行独批 · 零短征/团本/挑战 settle 同题复读 · 零 variety再拧/完整经济 · 零 Cast·Aid 再拧 · 零庭内旁路再拧 · 零 sx20 · 零抬深渊掉落/体力价/层表/日帽 · 零 densify · 零教裸指令主 UI · 零 Pack6 · 零开 K3 · 零关观察
> **ancestor：** TIP-DA CLOSED live `8e12b6985` · feat `3d218ee83` · tip `d0f10a344` · PASS `020a8f2a7` · jar `1.65.825-d13332.local` · 批 sha `d0f10a344`

## 一句话
深渊结算后一点开「下一环」：再深渊/回选层/冒险/工坊等可点；清 ConfirmTokens 与 `abysstarget` 教手打主 UI；掉落·体力价·层表不动。

## 为何不是半行
- **主交付：** 插件 **改 EmberSettleService 深渊 settle 下一环入口** + 薄页 `ember_p1_abyss_settle` 真开菜单去向 + 清 `/corerpg p1 abysstarget` / `/corerpg p1 forge` / Open 教指令主路径
- Open/半行 **不可独批**（REJECT `d00ed84bc` 保持）

## ≠ 清单
- ≠ 挑战settle下一环同题（TIP-DA 已齐）· ≠ 团本settle下一环同题（TIP-CZ 已齐）· ≠ 短征settle下一环同题（TIP-CW 已齐）· ≠ 短征settle回看同题（TIP-CY 已齐）· ≠ variety再拧 · ≠ soft-reward同题 · ≠ 路径追同题 · ≠ 日差同题 · ≠ Cast·Aid 再拧 · ≠ 庭内旁路再拧 · ≠ 半行空转 · ≠ 抬掉落/体力价/层表/日帽 · ≠ 教裸指令 · ≠ 复开 legacy `/corerpg abyss evacuate` 大扫（本窗钉 P1 深渊 settle 一支）

## 施工钉死（批后）
- W1a：`EmberSettleService` 深渊 `newBest` 下一环→开 `/ember_p1_abyss_settle`（或等价 open）；记住 settle 层；工坊钮→菜单；清以 ConfirmTokens/文案教 `/corerpg p1 abysstarget` / `/corerpg p1 forge` 为主 UI；同批清 `ember_p1_abyss` Open 教 `abysstarget` 旁路（改菜单点设目标）；单测
- W1b：薄页 `ember_p1_abyss_settle`：再深渊下一层（菜单 actions `corerpg p1 abyss next` 或可开层）/ 回选层 `ember_p1_abyss` / 冒险 `ember_p1_adventure` / 工坊 `ember_p1_forge` / 可选层目标薄格（菜单 actions `abysstarget`，玩家不手打）/ 可选深渊路径薄格；层号+路径 lore 诚实；灰态诚实（体力/层费/未开层）
- W1c：同批辅指针若有 → **不可独批**
- **不改** 深渊掉落 / 体力价 / 层表 / 日帽 / Cast/Aid/variety / 短征·团本·挑战薄页 / 庭内 / soft-reward

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧
- **L** = 仅改一条聊天钮目标命令无薄页 · **本窗不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 09:34 Asia/Shanghai
- [ ] 插件交深渊 settle 入口改菜单 + 清裸指令主路径 + 单测
- [ ] 物品交薄 abyss settle 页 + TrMenu reload（含清 `ember_p1_abyss` Open 教指令旁路）
- [ ] 抽测 PASS live（再深渊/选层/冒险/工坊真开·PAPI·零抬掉落/体力价/层表）· **勿勾 CLOSED**

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 W1a插件+W1b物品 · 勿勾 CLOSED · L不批 · D13333 · TIP-DB · 2026-10-14 09:34 Asia/Shanghai
