# tip · TIP-CL 挂机庭「顺手互换」（真 swap · ≠第五件花料 · ≠半行空转 · ≠抬表）· 拟 D13317

> **状态：** **已批 A · 方案 M** · APPROVED · 批 sha `ac5ba6436` · 布局 `#P1W2w3D#` · 已派 **余烬-物品**（TrMenu）· 2026-10-14 01:05 Asia/Shanghai · **勿勾 CLOSED**（等 live CLOSED）
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-swap-2026-10-14.md`](../design/DESIGN-ember-afk-courtyard-quick-swap-2026-10-14.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-swap`  
> **施工岗（批后）：** 余烬-物品 · TrMenu-only · **不派插件/怪物/地图**（默认）  
> **硬禁：** 零第五件花料 · 零推荐花复读 · 零半行独批 · 零 P 兼确认 · 零抬日表/改价 · 零静默换 · 零 sx20/L13/K3 · 零 Cast 粒子主交付

## 一句话
挂机庭一点「顺手互换」→ 真 `p1 swap` 预览 → 菜单确认；产→换轨不断跳。

## 为何不是半行
- **主交付：** 新 Icon **W/w** + `command: corerpg p1 swap` / `swap confirm`（可点→预览→确认成交）  
- Open「本页也可顺手换轨」**仅同批辅**，不可独批（否决方案 A）

## 布局/命令钉死
- 布局钉死：**总控建议** `'#P1W2w3D#'`（9 格 W+w 同排）；空位落 **W**；邻位落 **w** 确认
- Icon **W**：名 `§b顺手互换`；lore 挂 `%corerpg_p1_held_swap_cost%` + 免费确定 + 点预览·再确认；`command: corerpg p1 swap`
- Icon **w**：名 `§b顺手互换 · 确认`；`command: corerpg p1 swap confirm`
- 甲灰：`held_is_armor==1` → 灰+tell（对齐 forge S/s）
- **不撤** E/e/U/u/R/r/Q/q/P/D/S
- W1b：Open / 去哪花 +半行「本页也可顺手换轨」（辅）
- update:20；lore≤3；与工坊 S/s 同命令同确认

## 施工钉死（批后）
- W1a/W1b/W1c：TrMenu-only；**零**改 Swap 价 / UpgradeRules / afk.tiers / daily_kills
- **禁**静默换 / 第五花料 / 推荐花复读 / P 兼确认

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 01:05 Asia/Shanghai
- [ ] 物品交 W/w 格 + Open 辅半行；抽测预览/确认/甲灰诚实
- [ ] E/U/R/Q/P/D/S 仍在；零价表 diff；零 sx20

## STATUS: **已批 A · 方案 M** · APPROVED · 批 sha `ac5ba6436` · 布局 `#P1W2w3D#` · 已派物品 · 拟 D13317 · TIP-CL · **勿勾 CLOSED** · 2026-10-14 01:05 Asia/Shanghai
