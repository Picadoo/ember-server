# B2.72 · DP README 表说明列「入场券条件」维护备忘薄窗

- **STATUS：PASS · 勾销（总控 · 2026-09-30 20:53 Asia/Shanghai）** · 设计 `a5d1ce1` · 批准 `9a7bd68` · 插件 `db12737` · 测 `f092865`
- **范围：**仅 `plugins/DungeonPlus/README-ember-dungeons.md` 约 L11 的表说明列措辞；未批准前不改目标 README，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-dp-readme-table-entry-copy.md`
- **施工岗：**批后施工交 **插件岗（DungeonPlus 文档）**。
- **前序对齐：**B2.71 已批 A `1227e0c`；本窗只承接其后的 B2.72 L11 表说明列维护，不回改已批事实。
- **本窗纪律：**只调整「入场券条件」这段维护口吻；路径列与其它表行零改；勿改 `option.yml`、地牢 YAML 或 `mail`；勿 git push。

## 1. 现况与范围

已读 `plugins/DungeonPlus/README-ember-dungeons.md` L1–L25，确认 L11 位于「地牢配置」表，当前说明列为地图、出生、入场券条件及通关管理命令。目标只把「入场券条件」改成维护备忘口吻；不改路径列、表头、其它表行或 README 其它段落。

## 2. 旧文案 / 荐案

旧（live，约 L11）：

```text
地图/出生/入场券条件/通关 ni give
```

新（荐案，批后）：

```text
地图/出生/遗留票物入场条件/通关 ni give
```

## 3. 荐案口径与零改锁定

- 仅将说明列中的「入场券条件」改为「遗留票物入场条件」，使其成为维护备忘口吻。
- `地图`、`出生`、`通关 ni give` 保留；不改路径列，不改表头，不改任何其它表行。
- 不把本窗写成票务完成/废止宣称；不写「票已废」「B0.1 已清」或等价表述。
- 不捆 `plugins/DungeonPlus/dungeon/` 下地牢 YAML、`option.yml`、其它玩法配置或 `mail`。

## 4. 施工与验收

- [ ] 批后仅 README 约 L11 说明列按荐案更新：`地图/出生/遗留票物入场条件/通关 ni give`。
- [ ] L11 路径列逐字保留；表头及 L12–L25 其它表行零改。
- [ ] README 除 L11 说明列外零 diff；其它段落与 `mail` 零改。
- [ ] `plugins/DungeonPlus/dungeon/` 下地牢 YAML（含 `option.yml`）零 diff；不改玩法、门控、奖励或数值。
- [ ] 未批前不改目标 README；本窗提交仅本 tip 与 backlog。
- [ ] 批后施工交插件岗；验收仅做目标文案与范围静态复核，不扩成长测/挑刺。

## 5. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧文案/荐案、路径列与其它表行零改确认、验收结果、pull 与零 diff 范围。
- 禁写「票已废」「B0.1 已清」或等价完成/废止宣称；禁改 `option.yml`、地牢 YAML、`mail`。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改其它 README 文案；勿 git push。
