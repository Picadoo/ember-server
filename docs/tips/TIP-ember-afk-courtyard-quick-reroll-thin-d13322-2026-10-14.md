# tip · TIP-CQ 挂机庭「顺手洗练」（真 reroll 入口 · ≠第五件花料 · ≠推荐花复读 · ≠半行 · ≠抬表）· 拟 D13322

> **状态：** APPROVED · 批 **A · 方案 M** · W1a 已交插件 · 待物品 W1b/c · 2026-10-14 02:46 Asia/Shanghai · **勿勾 CLOSED**（等 live CLOSED）  
> **DESIGN** [`DESIGN-ember-afk-courtyard-quick-reroll-2026-10-14.md`](../design/DESIGN-ember-afk-courtyard-quick-reroll-2026-10-14.md) · RESEARCH 同日 · backlog `B-afk-courtyard-quick-reroll`  
> **施工岗（批后）：** 余烬-插件（W1a 返回源）+ 余烬-物品（W1b/c TrMenu）· **不派怪物/地图**（默认）  
> **硬禁：** 零半行独批 · 零第五件花料 · 零推荐花复读 · 零 P 兼确认 · 零抬日表/改洗练价/pity · 零 Cast/Aid/todayRot/variety 再拧 · 零 W1e 独批 · 零 sx20/L13/K3  
> **ancestor：** TIP-CP CLOSED live `d87c3d89d` · feat `64f08f47b` · PASS `1696ccd17` · jar `1.65.813-d13321.local` · 批 sha `e976fe6d8`

## 一句话
挂机庭一点「顺手洗练」进既有词条洗练页；返回回庭；产碎喂洗炼轨。

## 为何不是半行
- **主交付：** 插件补 `REROLL_FROM[afk]` + 新 Icon **L** + `command: corerpg p1 reroll from afk`（可点→洗练页→可花）  
- Open「本页也可顺手洗练」**仅同批辅**，不可独批（否决方案 A）

## ≠ 清单
- ≠ 第五件花料 · ≠ 推荐花复读 · ≠ 四花顺手镜像 · ≠ 半行空转 · ≠ Cast/Aid打磨 · ≠ todayRot/W1e再拧 · ≠ variety 再拧 · ≠ 抬 event_core · ≠ 庭内拆/换复读 · ≠ 导航三冲复读

## 施工钉死（批后）
- W1a：`REROLL_FROM.put("afk","ember_p1_afk")` + 返回「挂机庭」
- W1b：布局建议 `'#PLW2w3D#'`；**L**=顺手洗练；lore 挂 blade/charm cur；`p1 reroll from afk`
- W1c：Open/去哪花辅半行
- **不改** reroll 价/pity/pool · afk.tiers · daily_kills · Cast/Aid/variety · 四花+P+D+W/w

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧（庭内闭环）  
- **L** = 仅 TrMenu `from forge`（返回落工坊）· **默认不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 02:43 Asia/Shanghai
- [x] 插件交 afk 返回源 · 1.65.814-d13322.local · 2026-10-14 02:46
- [ ] 物品交 L 格 + Open 辅半行
- [ ] 抽测：庭内进洗练→返回庭；价/pity 零 diff；零 sx20；零半行独档

## STATUS: APPROVED · 批 A·M · W1a 已交插件 · 待物品 W1b/c · D13322 · TIP-CQ · **勿勾 CLOSED** · 2026-10-14 02:46 Asia/Shanghai
