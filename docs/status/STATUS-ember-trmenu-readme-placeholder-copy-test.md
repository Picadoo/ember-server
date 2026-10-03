# B2.59 · TrMenu README 占位文案 · 纯静态薄验收测报

- **总评：PASS**
- **测岗：**余烬-测试岗执行器
- **测时：**2026-09-30 19:54 Asia/Shanghai（CST）
- **施工 tip SHA：**`5fea72e`（`5fea72e4e1cbf5e3fb0a4a1abcd3f3370990d8f3` · `docs: update TrMenu README placeholder copy`）
- **批准 tip：**`ffde8fc` · tip 文档 `docs/design/design-ember-trmenu-readme-placeholder-copy.md`
- **范围：**仅静态核对 `plugins/TrMenu/menus/README-ember.md`；未改配置、未长测、未挑刺

## 各点对照

| # | PASS 条件 | 结果 | 旁证 |
|---|-----------|------|------|
| 1 | 段标题为 `## 占位 / 待接线备忘`（无「插件岗」） | **PASS** | live L29；段内无「插件岗」 |
| 2 | 5 条正文与 tip §2 荐案逐字一致；含「PAPI … 尚未接入」 | **PASS** | Python 逐字 `EXACT_MATCH: True`；第 3 条含「尚未接入」 |
| 3 | 段外 DungeonPlus 句保留；`plugins/TrMenu/menus/*.yml` 相对施工前零 diff | **PASS** | live L37 原文保留；`git diff 5fea72e^..5fea72e -- '*.yml'` 空；施工 commit 仅改 README |
| 4 | 勿宣称 PAPI/日限已接入、勿宣称 B0.1 | **PASS** | 段内无「已接入」宣称、无「B0.1」字样 |

## 段标题原文

```text
## 占位 / 待接线备忘
```

## 5 条正文原文 vs tip §2

live 与 tip §2 荐案 fenced 块逐字一致（含空行）：

```markdown
## 占位 / 待接线备忘

1. **挂机传送**：当前为 `tp %player_name% -40 65 270`（RpgBot 测试点）；后续可评估改为 `warp afk` 或真实大厅坐标。
2. **进本命令**：当前记录为 `dp start EmberDaily` / `dp start EmberWeekly`（官方命令）；入口接线时再核对实际用法。
3. **次数占位符**：菜单文案目前写死 3/1；PAPI `%ember_daily_left%` / `%ember_weekly_left%` 尚未接入，接线后再改 lore。
4. **音效**：当前使用 1.12 风格 `BLOCK_NOTE_PLING`；若 TrMenu 映射失败，再换成插件支持名。
5. **材质**：`stained glass pane` / `watch` / `ender chest` 等若在 1.12 + TrMenu 3.12 解析异常，再改成 `glass` / `clock` / `chest`。
```

## yml 零 diff 旁证

- 施工 commit `5fea72e` 变更文件仅：`plugins/TrMenu/menus/README-ember.md`（+6/−6）
- `git diff 5fea72e^..5fea72e -- 'plugins/TrMenu/menus/*.yml'` → 空
- 工作区相对 `HEAD`：clean；menus `*.yml` 无未提交改动

## DungeonPlus 句旁证

段落后紧随原文（README L37）：

```text
DungeonPlus 配置等 jar 到位后再落，不在本目录。
```

与 tip §2.2 / §4 要求的段外保留句一致。

## 阻塞点

无。

## 纪律自检

- 纯静态；未改任何 menus YAML / 玩法配置
- 未宣称 PAPI/日限已接入；未宣称 B0.1
- 未长测、未挑刺
