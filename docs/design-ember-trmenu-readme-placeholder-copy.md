# B2.59 · TrMenu README 占位段措辞薄窗

- **STATUS：PASS · 勾销（总控 · 2026-09-30 19:55 Asia/Shanghai）** · 测 `2ae758f` · 施工 `5fea72e` · 批准 `ffde8fc` · 设计 `57558b3`
- **范围：**仅维护者文档 `plugins/TrMenu/menus/README-ember.md` 的「占位待改（插件岗）」段；不是玩家 UI。
- **tip 路径：**`docs/design-ember-trmenu-readme-placeholder-copy.md`
- **施工岗：**批后施工交 **插件岗（TrMenu 文档）**。
- **本窗纪律：**文档可 commit；未批前不改玩法 YAML；勿 git push；不做长测/挑刺。

## 1. 现况与问题

README 当前第 29–35 行段落标题和 5 条正文如下（全文照录）：

```markdown
## 占位待改（插件岗）

1. **挂机传送**：现为 `tp %player_name% -40 65 270`（RpgBot 测试点）。可改 `warp afk` 或真实大厅坐标。
2. **进本命令**：`dp start EmberDaily` / `dp start EmberWeekly`（官方命令）。
3. **次数占位符**：菜单文案暂写死 3/1；PAPI `%ember_daily_left%` / `%ember_weekly_left%` 待接入后改 lore。
4. **音效**：用了 1.12 风格 `BLOCK_NOTE_PLING`；若 TrMenu 映射失败再换成插件支持名。
5. **材质**：`stained glass pane` / `watch` / `ender chest` 等若在 1.12 + TrMenu 3.12 解析异常，改成 `glass` / `clock` / `chest`。
```

问题仅是维护者文档采用了「插件岗」指派式标题和偏重的施工口吻；本窗不改变任何菜单 YAML、命令、坐标、占位符或材质/音效事实。

## 2. 荐案：维护备忘式标题与措辞

推荐段标题为 `## 占位 / 待接线备忘`，保留 5 条事实 soft 项，去掉指派口吻：

```markdown
## 占位 / 待接线备忘

1. **挂机传送**：当前为 `tp %player_name% -40 65 270`（RpgBot 测试点）；后续可评估改为 `warp afk` 或真实大厅坐标。
2. **进本命令**：当前记录为 `dp start EmberDaily` / `dp start EmberWeekly`（官方命令）；入口接线时再核对实际用法。
3. **次数占位符**：菜单文案目前写死 3/1；PAPI `%ember_daily_left%` / `%ember_weekly_left%` 尚未接入，接线后再改 lore。
4. **音效**：当前使用 1.12 风格 `BLOCK_NOTE_PLING`；若 TrMenu 映射失败，再换成插件支持名。
5. **材质**：`stained glass pane` / `watch` / `ender chest` 等若在 1.12 + TrMenu 3.12 解析异常，再改成 `glass` / `clock` / `chest`。
```

### 2.1 新旧段对照

| 位置 | 旧（live 全文） | 新（荐案，批后全文） |
|------|------------------|----------------------|
| 段标题 | `## 占位待改（插件岗）` | `## 占位 / 待接线备忘` |
| 1 | `**挂机传送**：现为 \`tp %player_name% -40 65 270\`（RpgBot 测试点）。可改 \`warp afk\` 或真实大厅坐标。` | `**挂机传送**：当前为 \`tp %player_name% -40 65 270\`（RpgBot 测试点）；后续可评估改为 \`warp afk\` 或真实大厅坐标。` |
| 2 | `**进本命令**：\`dp start EmberDaily\` / \`dp start EmberWeekly\`（官方命令）。` | `**进本命令**：当前记录为 \`dp start EmberDaily\` / \`dp start EmberWeekly\`（官方命令）；入口接线时再核对实际用法。` |
| 3 | `**次数占位符**：菜单文案暂写死 3/1；PAPI \`%ember_daily_left%\` / \`%ember_weekly_left%\` 待接入后改 lore。` | `**次数占位符**：菜单文案目前写死 3/1；PAPI \`%ember_daily_left%\` / \`%ember_weekly_left%\` 尚未接入，接线后再改 lore。` |
| 4 | `**音效**：用了 1.12 风格 \`BLOCK_NOTE_PLING\`；若 TrMenu 映射失败再换成插件支持名。` | `**音效**：当前使用 1.12 风格 \`BLOCK_NOTE_PLING\`；若 TrMenu 映射失败，再换成插件支持名。` |
| 5 | `**材质**：\`stained glass pane\` / \`watch\` / \`ender chest\` 等若在 1.12 + TrMenu 3.12 解析异常，改成 \`glass\` / \`clock\` / \`chest\`。` | `**材质**：\`stained glass pane\` / \`watch\` / \`ender chest\` 等若在 1.12 + TrMenu 3.12 解析异常，再改成 \`glass\` / \`clock\` / \`chest\`。` |

### 2.2 口径硬线

- 新段**不宣称** PAPI `%ember_daily_left%` / `%ember_weekly_left%` 已接入；次数仍明确为文案写死 3/1 的占位事实。
- 新段**不宣称** B0.1 已清；B0.1 仍按 backlog 现状保留。
- 「进本命令」只记录当前文档事实，不把命令记录等同于已完成接线或运行验收。
- `DungeonPlus 配置等 jar 到位后再落，不在本目录。` 段外原文保持不动。

## 3. 批后施工说明（待批 A，不在本窗执行）

1. 批 A 后仅由**插件岗（TrMenu 文档）**施工 README 上述段标题与 5 条措辞。
2. 仅替换该段；README 其它段落原样保留。
3. 不改任何 `plugins/TrMenu/menus/*.yml`，不改其它 menus YAML，不改玩法逻辑、命令、坐标、PAPI 接线、音效或材质配置。
4. 施工后做静态全文对照与 diff 即可；不做长测、不做挑刺。

## 4. 验收与禁项

- [ ] 段标题为 `## 占位 / 待接线备忘`，不含「插件岗」；5 条新文案与上方荐案逐字一致。
- [ ] 旧标题和 5 条旧文案与本 tip 记录的 live 全文一致；段外 `DungeonPlus` 句及 README 其它内容零改。
- [ ] README 外的 `plugins/TrMenu/menus/*.yml` 零 diff；本窗未施工玩法 YAML。
- [ ] 文案明确 PAPI `%ember_daily_left%` / `%ember_weekly_left%` 尚未接入；不宣称 B0.1 已清。
- [ ] 批后施工岗固定为**插件岗（TrMenu 文档）**。
- [ ] 本设计 commit 仅含本 tip 与 backlog；不做长测/挑刺，勿 git push。

**禁项：**禁改任何 menus YAML；禁宣称 PAPI/日限已接入；禁宣称 B0.1 已清；禁长测/挑刺；禁把 CoreRpg 文件头 soft 项并入本窗；勿 git push。

## 5. 并行旁记（不捆）

CoreRpg `cash` / `covenant` / `talent` 文件头仍有 `STUB` 句，作为独立 soft 观察，不纳入 B2.59，不在本窗处理。

## 6. 回总控摘要

- **STATUS：PASS · 勾销** · 测 `2ae758f` · 报告 `docs/STATUS-ember-trmenu-readme-placeholder-copy-test.md`。
- **荐案标题：**`## 占位 / 待接线备忘`。
- **荐案正文：**保留挂机传送、进本命令、次数占位符、音效、材质 5 条事实；去掉「插件岗」指派口吻。
- **明确未完成：**PAPI `%ember_daily_left%` / `%ember_weekly_left%` 未接入；不宣称 B0.1 已清。
- **批后施工：**交**插件岗（TrMenu 文档）**；只改该 README 段，不改其它 menus YAML。
- **旁记：**CoreRpg cash/covenant/talent 文件头 `STUB` 句不捆。

## 7. 总控批示

- [x] **批 A** · 仅按上方荐案改 README「占位 / 待接线备忘」段；其它 README 内容及所有 menus YAML 零改
- [ ] **驳回** · 说明
