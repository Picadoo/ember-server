# tip · TIP-CE 房间事件「冲一下」可感（事件窗小乐趣 · 零抬率）· 拟 D13310

> **状态：** CLOSED live · 批 **A · 方案 M** · jar `1.65.807-d13310b.local` · 插件 `59b6cf85b` · 物品 `1b4a9d78e` · TrMenu reload 77 · PASS `abc14a205` · 2026-10-13 23:05 Asia/Shanghai · **勾毕**  
> **DESIGN** [`DESIGN-ember-room-event-chase-visible-2026-10-13.md`](../design/DESIGN-ember-room-event-chase-visible-2026-10-13.md) · RESEARCH 同日 · backlog `B-room-event-chase-visible`  
> **施工岗（批后）：** 余烬-插件（薄 chase/ready PAPI + 可选 enter 别名）+ 余烬-物品（TrMenu）· **不派怪物/地图** · **不**动 calamity legacy  
> **硬禁：** 零抬 event_rate/event_core/NEED · 零 Pack6 · 零抬 AFK/日帽 · 零 sx20/L13/K3 · 零重开 calamity join · 零只半行无进本

## 一句话
冒险页一眼房间事件保底，一点进该冲的已通本；就绪更醒目；出率与 Pack 不动。

## 布局钉死
- 冒险第三行空位落 **E**（保留 W/s/b/u/n；例 `'#WsEb u n'` 或等价）
- Icon **E**：名 `§e房间事件 · 冲一下`；lore：`%corerpg_p1_event_soft_hope%` + chase_name +「仅已通普通重打出事件」；`corerpg enter %corerpg_p1_event_chase%`（或 `enter event-chase`）
- ready=`%corerpg_p1_event_soft_ready%`≥1 亮态；无 chase / 体力不足 / 未解锁灰态诚实
- W1c：Open 短指针「冲一下见本页 E」（可保留一行 soft_hope）
- update:20；不改 Q 七格 / H 今日推 / 挂机庭花料

## jar 钉死（W1a）
- `%corerpg_p1_event_chase%` = featured（若已首通）否则已通最高 q01–q07，否则空
- `%corerpg_p1_event_chase_name%` · `%corerpg_p1_event_soft_ready%`（0/1←SoftPity dry≥NEED）
- `%corerpg_p1_event_chase_ok%` = chase 非空→1 否则 0（TIP-CE FAIL 修 · TrMenu 单条件灰）
- **零改** SoftPity.onSettle / event_rate / weights / event_core

## 验收（批后施工）
- [x] 总控批 A·M
- [x] 插件交 chase*/ready + 单测
- [x] 物品交 menu tip `1b4a9d78e` + TrMenu reload 77
- [x] 测岗 PASS `abc14a205` · 无chase灰绿 · 零抬率

## STATUS: CLOSED live · menu `1b4a9d78e` · PASS `abc14a205` · 2026-10-13 23:05 Asia/Shanghai
