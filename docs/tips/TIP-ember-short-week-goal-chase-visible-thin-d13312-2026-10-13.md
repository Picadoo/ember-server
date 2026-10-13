# tip · TIP-CG 短征选页「本周短征可冲」（≠抬目标数 · ≠sx20）· 拟 D13312

> **状态：** CLOSED live · 批 **A · 方案 M** · jar `1.65.809-d13312b.local` · 插件 hotfix `e0be3251f` · 物品 `0abdb2746` · TrMenu reload 77 · PASS `20907456a` · 2026-10-13 23:52 Asia/Shanghai · **勾毕** · 策划旁注 23:54  
> **DESIGN** [`DESIGN-ember-short-week-goal-chase-visible-2026-10-13.md`](../design/DESIGN-ember-short-week-goal-chase-visible-2026-10-13.md) · RESEARCH 同日 · backlog `B-short-week-goal-chase-visible`  
> **施工岗（批后）：** 余烬-插件（薄 chase PAPI + enter `short-goal`）+ 余烬-物品（TrMenu F 格）· **不派怪物/地图**  
> **硬禁：** 零抬 `weekly_goals.targets.short`/徽/日帽 · 零 sx20/L13/K3 · 零第五件花料 · 零只半行无进本

## 一句话
短征选页一眼本周短征周目标进度，一点进还能计有奖的本；short:5 与日帽不动。

## Hotfix D13312b
- chase 谓词：`EmberShortPath.unlocked`（progressFlag/q01），**非** `runs.unlocked`（C_UNLOCK）
- 测岗 FAIL `71d4f3356`

## 键清单（W1a）
- `goal_short_chase` / `_name` / `_day_line` / `goal_short_done` / `goal_short_chase_ok`
- enter `short-goal`（`goal-short-chase` / `goal_short_chase`）
- **零改** addGoal / weekly_goals.targets / 徽 / 日帽 / SoftPity / bv123

## 施工钉死
- W1a：`goal_short_chase` / `_name` / `_day_line` / `goal_short_done`；`enter short-goal`；优先 day_left>0 已解锁短征（稳定序；可选对齐 sx_today）
- W1b：`ember_p1_short` Layout 落 **F**（`'#PRSJDF #'`）；名「本周短征可冲」；lore 挂 goal_short + chase；真 enter；灰态诚实
- W1c：Open 半行「可冲见本页 F」
- 不改十九本 / 今日推 D / targets / 徽 / 日帽

## 验收（批后施工）
- [x] 总控批 A·M
- [x] 插件交 chase* + short-goal + 单测 · W1a `1.65.808-d13312.local`
- [x] 物品交 F 格 + TrMenu reload · `0abdb2746` · reload 77
- [x] 测岗 PASS `20907456a` · Q01 alone chase=sx11 · enter short-goal · 零抬 short:5/日帽 · hotfix d13312b

## STATUS: CLOSED live · jar `1.65.809-d13312b.local` · menu `0abdb2746` · PASS `20907456a` · hotfix `e0be3251f` · 2026-10-13 23:52 Asia/Shanghai
