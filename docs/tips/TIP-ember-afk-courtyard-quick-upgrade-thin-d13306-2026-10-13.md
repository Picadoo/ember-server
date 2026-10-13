# tip · TIP-CA 挂机庭「顺手升阶」（庭内第二件 · 零抬产）· 拟 D13306

> **状态：** OPEN · 待批 A · 荐方案 **M** · docs-only · 2026-10-13 21:39 Asia/Shanghai  
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md`](../design/DESIGN-ember-afk-courtyard-quick-upgrade-2026-10-13.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-upgrade`  
> **施工岗（批后）：** 余烬-物品 · TrMenu-only · **不派插件**  
> **硬禁：** 零抬 AFK 日表/tiers · 零改 UpgradeRules · 零 sx20/L13/K3 · 零静默扣料 · 零诚实空转半行族 · 禁假用 `gap_upgrade_t2<=0`

## 一句话
挂机庭本页可点「顺手升阶」（预览→确认，对齐工坊）；护甲灰；缺料靠 lack+命令失败诚实；零抬产。

## 布局钉死（批后施工）
- 荐键 **U/u**（避开 F/H/I/S/E/e/层格/B）；示例把左玻璃改为 `US 4 V E#` / `ue I F H#`（施工可微调空槽）
- Icon **U**：`command: corerpg p1 upgrade` · lore cost/lack/gap_upgrade_t2 · 护甲条件灰
- Icon **u**：`command: corerpg p1 upgrade confirm`（对齐工坊 u）
- W1b：Open +半行「升阶也可本页点」；S 去哪花 +半行「本页也可顺手升阶」
- update: 20；lore≤4；**不**用 `recipe_gap_upgrade_t2 <= 0`（键为拼接串）

## 验收（批后）
- [ ] 总控批 A·M
- [ ] 物品交 menu tip + push
- [ ] TrMenu reload；E/e 顺手强化行为不变；无改产量/价

## STATUS: 待批 A · 荐 M · 拟 D13306 · 2026-10-13 21:39 Asia/Shanghai
