# tip · TIP-BY 挂机庭「顺手花料」（庭内强化+1 · 零抬产）· D13304

> **状态：** CLOSED live menu · 批 **A · 方案 M** · 物品 `e214b0399` · TrMenu reload 77 · 2026-10-13 21:29 Asia/Shanghai  
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-spend-2026-10-13.md`](../design/DESIGN-ember-afk-courtyard-quick-spend-2026-10-13.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-spend`  
> **施工岗：** 余烬-物品 · TrMenu-only · **不派插件**  
> **硬禁：** 零抬 AFK 日表/tiers · 零改 UpgradeRules · 零 sx20/L13/K3 · 零静默扣料 · 零诚实空转半行族

## 一句话
挂机庭仓够时本页可点「顺手强化+1」（预览→确认），不够灰态进工坊；产→花不断跳。

## 布局钉死
- 第三行：`'# S 4 V #'` → `'#S 4 V E#'`
- 第四行：`'# I F H #'` → `'#e I F H#'`
- Icon **E**：够料（`check papi %corerpg_p1_recipe_gap_enhance1% <= 0`）亮「§a顺手强化+1」· lore 花费/仓扣/点预览 · `command: corerpg p1 enhance`；不够灰「§7顺手花料 · 还差」· lore 挂 gap · `menu: ember_p1_forge`
- Icon **e**：`command: corerpg p1 enhance confirm`（对齐工坊 e）；不够/不可用时灰+tell 或同跳工坊
- W1b：Open +半行「仓够时可在本页顺手花」；S 去哪花 +半行「本页也可顺手强化」
- update: 20；lore≤3；对齐 forge E/e 文案口径

## 验收
- [x] 总控批 A·M
- [x] 物品交 menu tip + push `e214b0399`
- [x] TrMenu reload 77（静态结构齐；测岗 gameplay 联验中）

## STATUS: CLOSED live menu `e214b0399` · 测岗 PASS @`ddcf87a00` · 2026-10-13 21:29 Asia/Shanghai
