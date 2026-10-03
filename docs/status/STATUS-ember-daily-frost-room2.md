# STATUS · 霜晶房2链式重叠

**日期：** 2026-09-28 ~08:55 CST · 岗：余烬-插件  
**依据：** 总控【派工 · 霜晶房2链式重叠】· `docs/design/design-ember-daily-frost-room2.md`  
**Verdict：** ✅ · `dp reload` ✅ · **未 push**

## 改动
仅 `plugins/DungeonPlus/dungeon/EmberDailyFrost/monster.yml` 的 wave2a/wave2b：
- wave2a.start：`霜晶尸来袭 —— 霜矢将交错压上` + `$monstergroup{wave2b;delay=2}`
- wave2a.end：仅「折台尸侧已清」+heal（不再拉 wave2b / 无 door/boss）
- wave2b.start：`霜矢骷重叠压上 —— 清完开霜厅门`
- wave2b.end：霜厅门 + boss **未改**

**硬禁遵守：** wave1/wave1b 零动；坐标/门位/HP/体力/掉落/其它六线零动；无 kill-any / 跨组合并。

## 热更
FIFO `dp reload` · **08:56:00** CST · `[DungeonPlus] 插件重载完毕` · EmberDailyFrost 初始化完毕。

## 验收
折台开打约 2s 霜矢重叠；门仅 wave2b `$kill` 后开；房1左右交错仍旧。
