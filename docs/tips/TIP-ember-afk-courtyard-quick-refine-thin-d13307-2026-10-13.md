# tip · TIP-CB 挂机庭「顺手精工」（第三件可感 · 零抬产）· 拟 D13307

> **状态：** OPEN · 待批 A · 荐方案 M · docs-only · 2026-10-13 21:45 Asia/Shanghai  
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-refine-2026-10-13.md`](../design/DESIGN-ember-afk-courtyard-quick-refine-2026-10-13.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-refine`  
> **施工岗（批后）：** 余烬-物品 · TrMenu-only · **不派插件**  
> **硬禁：** 零抬 AFK 日表/tiers · 零改 UpgradeRules · 零 sx20/L13/K3 · 零静默精工 · **禁止** `recipe_gap_refine1 <= 0`（拼接串非数字 · D13306 教训）· 成色本窗不做

## 一句话
挂机庭本页可预览/确认精工手持；护甲灰；缺料靠 lack+命令失败诚实；进度挂 hope。

## 布局钉死（在 TIP-CA 之上）
- 第三行：`'#US4 V E#'` → `'#US4 VRE#'`（保留 U/S/4/V/E，加 R）
- 第四行：`'#ueI F H#'` → `'#ueI FrH#'`（保留 u/e/I/F/H，加 r）
- Icon **R**：`corerpg p1 refine craft`；lore：held_next_c + refine_lack + refine_hope + gap_refine1 半行（≤3 优先花费/缺料/进度，gap 可并一行）；护甲 `held_is_armor==1` 灰
- Icon **r**：`corerpg p1 refine craft confirm`；护甲同灰
- W1b：Open +半行「精工也可本页点」；S +半行「本页也可顺手精工」（保留强化/升阶半行）
- update:20；对齐 forge R/r；不改 E/e、U/u
- **禁**用 `recipe_gap_refine1 <= 0` 做亮/灰（非数字）

## 验收（批后施工）
- [ ] 总控批 A·M
- [ ] 物品交 menu tip + push
- [ ] TrMenu reload；测岗联验

## STATUS: OPEN · 待批 A · 荐 M · 拟 D13307 · 2026-10-13 21:45 Asia/Shanghai
