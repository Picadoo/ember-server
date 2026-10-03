# B2.64 · DungeonPlus README 表说明维护备忘薄窗

- **STATUS：PASS · 勾销（总控 · 2026-09-30 20:15 Asia/Shanghai）** · 测 `2f517c4` · 施工 `b589616` · 批准 `2f1a25f` · 设计 `8a6ba77`
- **范围：**仅 `plugins/DungeonPlus/README-ember-dungeons.md` 表内 L19/L20 说明；批前不改 README，不改地牢 YAML。
- **tip 路径：**`docs/design/design-ember-dp-readme-stub-copy.md`
- **施工岗：**批后施工交 **插件岗（DungeonPlus 文档）**。
- **本窗纪律：**禁长测、禁挑刺；不宣称深渊/灾厄完整阶段已落地；勿宣称 B0.1；勿 git push。

## 1. 现况与范围

已读 live 表格 L19/L20 整行。目标只涉及两个路径说明单元格；票表、指令行及其它 README 段不在本窗修改范围。地牢 YAML（含 `EmberAbyss/`、`EmberCalamity/`）不在本窗修改范围。

## 2. L19/L20 新旧全文对照

旧（live 全行）：

```text
|| `plugins/DungeonPlus/dungeon/EmberAbyss/` | 深渊 5 层 stub + 通关箱 5～9 档 |
|| `plugins/DungeonPlus/dungeon/EmberCalamity/` | 灾厄 Boss stub + 日箱表 |
```

新（荐案，批后全行）：

```text
|| `plugins/DungeonPlus/dungeon/EmberAbyss/` | 维护备忘：深渊 5 层 + 通关箱 5～9 档 |
|| `plugins/DungeonPlus/dungeon/EmberCalamity/` | 维护备忘：灾厄 Boss + 日箱表 |
```

## 3. 荐案

```text
|| `plugins/DungeonPlus/dungeon/EmberAbyss/` | 维护备忘：深渊 5 层 + 通关箱 5～9 档 |
|| `plugins/DungeonPlus/dungeon/EmberCalamity/` | 维护备忘：灾厄 Boss + 日箱表 |
```

只去掉 `stub` 口吻，保留深渊 5 层、通关箱 5～9 档、灾厄 Boss、日箱表四项现行事实；不扩写阶段、不暗示深渊或灾厄完整阶段已落地。

## 4. 验收

- [ ] 批后仅 README L19/L20 两个表格说明单元格与荐案逐字一致。
- [ ] README 票表、指令行及其它段落零改。
- [ ] `plugins/DungeonPlus/dungeon/` 下地牢 YAML 零 diff；不改玩法、门控、奖励或数值。
- [ ] 两行均无 `stub`；仍保留深渊 5 层 + 通关箱 5～9 档、灾厄 Boss + 日箱表事实。
- [ ] 不宣称深渊/灾厄完整阶段已落地，不宣称 B0.1 已完成；不做长测、挑刺。
- [ ] 验收确认待批期间只提交本 tip + backlog，目标 README 与地牢 YAML 未施工。

## 5. 禁项与施工口径

- 未批前不改 `README-ember-dungeons.md`；批后仅改 L19/L20 相关表说明文案。
- 不顺带修改票表、指令行或其它 README 段；不顺带改任何地牢 YAML。
- 批后施工交 **插件岗（DungeonPlus 文档）**；不把文案维护表述为玩法阶段交付。
- 旁记 soft 不捆本窗：`mail.yml` 键 `season_pass_stub`；README 票/指令旧口径另窗。
