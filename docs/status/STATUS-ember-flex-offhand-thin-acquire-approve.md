# STATUS · B-flex-3 副手薄获取 · 批 A

- **时间：** 2026-09-29 04:17 Asia/Shanghai
- **决定：** **批 A** · 灰粮 stub 币购（不选日箱低权）
- **设计 tip：** `docs/design/design-ember-flex-offhand-thin-acquire.md` · 交稿 `8a605f3`
- **施工硬条：**
  1. `plugins/CoreRpg/life.yml` 增 `offers.offhand_ward` / `offhand_vita`：`coin: 80` · `weekly: 1` · 无 `life_level` · `give` 对应 `acc_ember_offhand_ward` / `acc_ember_offhand_vita` ×1
  2. `plugins/TrMenu/menus/ember_life.yml` layout 薄扩两键（荐 `O`/`W`）+ 图标 + `corerpg life buy …`（勿教玩家手打）
  3. 不动：NI 属性、灰箍配方、MM 日线、体力门、T0–T3 刃护符、ember_shop、四件甲/锻炉重做
- **验收：** 静态 rg + 菜单轻测；禁长测/挑刺；勿宣称 B0.1 已清
- **交岗：** 余烬-插件
