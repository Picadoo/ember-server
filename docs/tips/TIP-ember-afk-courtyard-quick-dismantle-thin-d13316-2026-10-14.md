# tip · TIP-CK 挂机庭「顺手分解」（真 dismantle · ≠第五件花料 · ≠半行空转 · ≠抬表）· 拟 D13316（重开）

> **状态：** **已批 A · 方案 M** · APPROVED · 批 sha `80dc1c28e` · 已派 **余烬-物品**（TrMenu）· 2026-10-14 00:53 Asia/Shanghai · **勿勾 CLOSED**（等 live CLOSED）
> **头注：** **取代**被否半行题「选页三房可读可躲」`d00ed84bc`（同 TIP-CK / D13316 · 半行档 **REJECTED · 勿勾 CLOSED**）  
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-dismantle-2026-10-14.md`](../design/DESIGN-ember-afk-courtyard-quick-dismantle-2026-10-14.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-dismantle`  
> **施工岗（批后）：** 余烬-物品 · TrMenu-only · **不派插件/怪物/地图**（默认）  
> **硬禁：** 零第五件花料 · 零半行独批 · 零 P 兼确认 · 零抬日表/拆表 · 零静默拆 · 零 sx20/L13/K3 · 零 Cast 粒子主交付

## 一句话
挂机庭一点「顺手分解」→ 真 `p1 dismantle` 预览 → 聊天确认；产→拆不断跳。

## 为何不是半行
- **主交付：** 新 Icon **D** + `command: corerpg p1 dismantle`（可点→预览→确认成交）  
- Open「本页也可顺手拆」**仅同批辅**，不可独批（否决方案 A）

## 布局/命令钉死
- 第二行：`'#P1 2 3 #'` → `'#P1 2 3D#'`（或等价空槽）
- Icon **D**：名 `§7顺手分解`；lore 挂 `%corerpg_p1_held_dismantle_yield%` + 甲零头说明 + 点预览·聊天确认；`command: corerpg p1 dismantle` + close
- 可选 **d** 指引格对齐 forge（挤则并入 D lore）
- **不撤** E/e/U/u/R/r/Q/q/P
- W1b：Open / 去哪花 +半行「本页也可顺手拆」（辅）
- update:20；lore≤3；与工坊 D 同命令同确认

## 施工钉死（批后）
- W1a/W1b/W1c：TrMenu-only；**零**改 dismantleYield / YieldBoost / afk.tiers / daily_kills
- **禁**静默拆 / 第五花料 / P 兼确认

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 00:53 Asia/Shanghai
- [ ] 物品交 D 格 + Open 辅半行；抽测预览/确认/不可拆诚实
- [ ] E/U/R/Q/P 仍在；零拆表 diff；零 sx20

## STATUS: **已批 A · 方案 M** · APPROVED · 批 sha `80dc1c28e` · 已派物品 · 拟 D13316 · TIP-CK 重开 · **勿勾 CLOSED** · 2026-10-14 00:53 Asia/Shanghai
