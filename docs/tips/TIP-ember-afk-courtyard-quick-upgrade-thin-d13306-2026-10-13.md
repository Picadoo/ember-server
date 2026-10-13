# tip · TIP-CA 挂机庭「顺手升阶」（第二件可感 · 零抬产）· D13306

> **状态：** CLOSED live menu · 批 **A · 方案 M** · tip APPROVED `328d0db1e` · 物品 `68544f966` · TrMenu reload 77 · 2026-10-13 21:42 Asia/Shanghai · **勾毕**  
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md`](../design/DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-upgrade`  
> **施工岗：** 余烬-物品 · TrMenu-only · **不派插件**  
> **硬禁：** 零抬 AFK 日表/tiers · 零改 UpgradeRules · 零 sx20/L13/K3 · 零静默升阶 · **禁止** `recipe_gap_upgrade_t2 <= 0`（拼接串非数字）· 精工本窗不做

## 一句话
挂机庭本页可预览/确认升阶手持；护甲灰；缺料靠 lack+命令失败诚实。

## 布局钉死（在 TIP-BY 之上）
- 第三行：`'#S 4 V E#'` → `'#US4 V E#'`（保留 S/4/V/E，加 U）
- 第四行：`'#e I F H#'` → `'#ueI F H#'`（保留 e/I/F/H，加 u）
- Icon **U**：`corerpg p1 upgrade`；lore：cost + lack + gap_upgrade_t2 半行（≤3 优先 cost/lack/点预览，gap 可并一行）；护甲 `held_is_armor==1` 灰
- Icon **u**：`corerpg p1 upgrade confirm`；护甲同灰
- W1b：Open +半行「升阶也可本页点」；S +半行「本页也可顺手升阶」（保留顺手强化半行）
- update:20；对齐 forge U/u；不改 E/e

## 验收
- [x] 总控批 A·M
- [x] 物品交 menu tip + push `68544f966`
- [x] TrMenu reload 77；测岗 gameplay 联验中

## STATUS: CLOSED live menu `68544f966` · tip `328d0db1e` · **勾毕** · 测岗 PASS · 2026-10-13 21:45 Asia/Shanghai
