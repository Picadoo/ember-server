# tip · TIP-CD 短征「今日推」轮换可见（日更选本 · 零抬日帽）· 拟 D13309

> **状态：** CLOSED live · 批 **A · 方案 M** · jar `1.65.805-d13309.local` · 插件 `47c8d5af8` · 物品 `5611a4337` · TrMenu reload 77 · PASS `8c4b8ce2d` · 2026-10-13 22:15 Asia/Shanghai · **勾毕**  
> **DESIGN** [`DESIGN-ember-short-today-rot-visible-2026-10-13.md`](../design/DESIGN-ember-short-today-rot-visible-2026-10-13.md) · RESEARCH 同日 · backlog `B-short-today-rot-visible`  
> **施工岗（批后）：** 余烬-插件（薄 PAPI + 可选 enter 别名）+ 余烬-物品（TrMenu）· **不派怪物/地图**  
> **硬禁：** 零抬短征日帽/S表/体力 · 零抬 AFK 日表 · 零 sx20/L13/K3 · 零假写固定今日本 · 事件窗本窗不做

## 一句话
选页一眼「今日推哪本」，一点进本；上海 0 点换下一本；规则与各本相同。

## 布局钉死
- 末行：`'#PRSJ   #'` → `'#PRSJD  #'`（保留 P/R/S/J，加 D；Z 返回不动）
- Icon **D**：名 `§e今日推 · %corerpg_p1_sx_today_name%`；lore：`sx_today_day_line` +「0 点换下一本」+「同各本规则」；`corerpg p1 enter %corerpg_p1_sx_today%`（或 `enter short-today`）
- 灰态：未解锁 Q01 / 体力<30 对齐该本格诚实
- W1c：短征 Open +半行「今日推：%sx_today_name%」；冒险短征入口 lore +半行
- update:20；不改十九本格 / B / Z / 满态去工坊

## jar 钉死（W1a）
- `%corerpg_p1_sx_today%` = sx01…sx19 · `floorMod(epochDay, 19)` Asia/Shanghai
- `%corerpg_p1_sx_today_name%` · `%corerpg_p1_sx_today_day_line%`（复用既有 day_line）
- **零改** claim 计数 / 经济表

## 验收（批后施工）
- [x] 总控批 A·M
- [x] 插件交 sx_today* + 单测
- [x] 物品交 menu tip `5611a4337` + TrMenu reload 77
- [x] 测岗 PASS `8c4b8ce2d` · 今日 sx11 · 门禁同该本

## STATUS: CLOSED live · menu `5611a4337` · PASS `8c4b8ce2d` · 2026-10-13 22:15 Asia/Shanghai
