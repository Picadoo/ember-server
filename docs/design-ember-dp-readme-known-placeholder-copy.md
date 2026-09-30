# B2.70 · DP README「已知占位」段标题维护备忘薄窗

- **STATUS：待批 A（总控 · 2026-09-30 20:39 Asia/Shanghai）**
- **范围：**仅 `plugins/DungeonPlus/README-ember-dungeons.md` 约 L57 的段标题一行；未批准前不改目标 README，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-dp-readme-known-placeholder-copy.md`
- **施工岗：**批后施工交 **插件岗（DungeonPlus 文档）**。
- **前序对齐：**B2.69 已批 A `9c833a7`，本窗只承接其后的 B2.70 标题维护，不回改已批事实。
- **本窗纪律：**仅调整段标题口吻；三条 bullet 事实与正文零改；勿 git push。

## 1. 现况与范围

已读 live README 约 L57 段及三条 bullet。目标只把「已知占位」改为维护备忘口吻；不改三条 bullet，不改其它 README 段或任何玩法 YAML。

## 2. 段标题旧/新对照

旧（live，约 L57）：

```text
## 已知占位
```

新（荐案，批后）：

```text
## 维护备忘（地图与脚本）
```

## 3. 荐案口径与三条 bullet 原文锁定

- 标题使用维护备忘口吻，范围提示保持在地图与脚本维护记录；不把标题写成玩家操作说明或完成宣称。
- 以下三条 bullet 为 live 原文，批后必须逐字保留，零改：

```text
- 地图已按本拆分（仍为测试区切片，出生 `-40,65,270`）；近出生点有主题方块标记；正式艺术面由 WorldEdit 再调。详见 `STATUS-ember-maps.md`。
- `$kill` 依赖 MM Display「余烬地窟僵尸/骷髅/蛮兵」；若对不上，看 DP debug 或改 monster.yml。
- 周本装备保底目前固定发刃。
```

- 仅处理约 L57 标题一行；不捆次数段标题「入场券」、L31、票表、发放句、地牢 YAML、`mail`。
- 禁止把本窗写成票制结论、B0.1 总体清账或地图正式化结论；不做长测/挑刺，精英壳勿硬开。

## 4. 施工与验收

- [ ] 批后仅目标 README 约 L57 标题与荐案逐字一致，行号以施工后复核为准。
- [ ] 上述三条 bullet 逐字保留：地图切片 / `$kill` Display 依赖 / 周本保底发刃三项事实零改。
- [ ] 次数段标题「入场券」、L31、票表、发放句、其它 README 段、地牢 YAML 与 `mail` 零改。
- [ ] `plugins/DungeonPlus/dungeon/` 下地牢 YAML 零 diff；不改玩法、门控、奖励或数值。
- [ ] 未批前不改目标 README；本窗只提交本 tip 与 backlog。
- [ ] 批后施工交插件岗（DungeonPlus 文档）；验收仅做标题与范围静态复核，不扩成长测/挑刺。

## 5. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧标题/荐案标题、三条 bullet 零改确认、验收结果与零 diff 范围。
- 禁捆次数段标题「入场券」、L31、票表、发放句、地牢 YAML、`mail`；勿顺手改其它 README 文案。
- 禁票制废止、B0.1 清账、地图正式化等越界宣称；精英壳勿硬开；勿 git push。
