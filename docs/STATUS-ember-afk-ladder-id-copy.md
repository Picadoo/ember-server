# STATUS · B2.9 挂机庭 / 天梯去内部代号 A

**时间：** 2026-09-28 23:25 CST（Asia/Shanghai）
**依据：** 批准 tip `ed4f54c` · 设计 tip `a8b8ef2` · `docs/design-ember-afk-ladder-id-copy.md` 方案 A（A1+A2+A3）
**交付：** `fix(ux): B2.9 afk/ladder copy no internal ids`（施工 tip `d0c9ef4`，已 push）
**结果：** **PASS · 勾销**（测试 tip `796dc1b`）

## 施工结果

- **A1 · HD `ember_afk_hub`**：标题、掉落说明、怪物说明均改为玩家文案；location、board 键未动。
- **A2 · HD 天梯**：仅改说明/底注文案：战力展示分与周结算、深渊层数口径、周本通关用时；竞排行榜人名/分值行、location、board 键未改动。本次同时清除竞速底注残留英文 `only`，以满足验收禁词。
- **A3 · TrMenu `plugins/TrMenu/menus/ember_ladder.yml`**：Open、全息榜说明钮、战力/深渊/竞速 lore/tell 已改为“挂机庭三块全息”等玩家口径；字面 board id、`EmberAbyss`、`EmberWeekly`、`power_score`、`only` 已清除。所有 `%ember_*%` PAPI 占位符原样保留。

源文件与运行时文件为同 inode，且施工后已确认内容一致：
- `plugins/HolographicDisplays/database.yml` ↔ `server-runtime/plugins/HolographicDisplays/database.yml`
- `plugins/TrMenu/menus/ember_ladder.yml` ↔ `server-runtime/plugins/TrMenu/menus/ember_ladder.yml`

## 热更证据

2026-09-28 23:25:18 CST 向 `server-runtime/console.in` 写入：

```text
hd reload
trmenu reload
```

`server-runtime/logs/latest.log`：

- `[23:25:18] ... Configuration reloaded successfully in 4ms!`（HD）
- `[23:25:18] ... [TrMenu] 良好 | 36 个菜单已加载 (35 ms)`（TrMenu）

## 验收与禁项

- HD 禁词扫描：**EMPTY**（`MM→NI` / `EmberAfk` / `Ember AFK` / `EmberAbyss` / `EmberWeekly` / 字面 `power_score` / 英文 `only`）。
- 天梯菜单禁词扫描：**EMPTY**（字面 `ember_ladder_power|abyss|speed` / `EmberAbyss` / `EmberWeekly` / 字面 `power_score` / `only`；PAPI `%ember_*%` 占位符按硬保豁免）。
- PAPI：**保留** `%ember_power_score%`、三榜 name/value 占位符，共 7 个，未改字节。
- 禁项零 diff（相对本次施工）：`location`、board 键、排行榜人名/分值行、`over_chance*`、MM id、其它菜单、`EmberCalamity/option.yml`、`ember_weekly.yml` 均未施工。
- 工作树中 HD 排行榜数据行原有 live 变更未纳入本次提交，保持未 staged；本次只提交文案替换。

## 版本控制

- 施工 commit：`d0c9ef4` · 已 push。
- 测试 commit：`796dc1b` · 已 push。
