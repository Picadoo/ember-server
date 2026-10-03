# B2.65 · DungeonPlus README L4 进本入口维护备忘

- **STATUS：PASS · 勾销（总控 · 2026-09-30 20:20 Asia/Shanghai）** · 测 `1c099c5` · 施工 `a6d0851` · 批准 `48003da` · 设计 `2c9b58c`
- **范围：**仅 `plugins/DungeonPlus/README-ember-dungeons.md` L4；批前不改目标 README，不改地牢 YAML。
- **tip 路径：**`docs/design/design-ember-dp-readme-enter-copy.md`
- **施工岗：**批后施工交 **插件岗（DungeonPlus 文档）**。
- **本窗纪律：**去掉玩家手打斜杠教学；不宣称 B0.1 已清；禁长测、禁挑刺；勿 git push。

## 1. 现况与范围

已读 live README 前几行并录下 L4 整行。目标只改 L4 进本说明：玩家入口优先 TrMenu 点击，保留体力扣次这一现行事实；不改表内路径、票表其它行、README 其它段或任何玩法 YAML。

## 2. L4 新旧全文对照

旧（live 全文）：

```text
玩家进本（B0.1）：`/corerpg enter daily|weekly|abyss|raid|elite`（TrMenu 按钮）· NI id 扣票
```

新（荐案，批后全文）：

```text
维护备忘：玩家入口=TrMenu 点击进本（日/周/深渊/团本/精英）· 体力扣次；进本门控仍挂
```

## 3. 荐案口径

- 玩家走 TrMenu 点击进本，不在玩家说明中列出 `/corerpg enter …` 或其它斜杠指令。
- 仅陈述「体力扣次」事实；不把票、NI id 或内部实现写成玩家入口教学。
- 「进本门控仍挂」只作维护备忘软事实，不扩写玩法状态。
- 不写「B0.1 已清」或任何等价完成宣称。

## 4. 验收

- [ ] 批后仅 README L4 与荐案逐字一致。
- [ ] README 其它段、表内路径、票表其它行零改。
- [ ] 新 L4 明确玩家入口为 TrMenu 点击进本，并保留体力扣次事实。
- [ ] 新 L4 不教玩家手打斜杠，不出现 `/corerpg enter …`，不宣称 B0.1 已清。
- [ ] `plugins/DungeonPlus/dungeon/` 下地牢 YAML 零 diff；不改玩法、门控、奖励或数值。
- [ ] 禁长测、禁挑刺；本窗只做文案施工。

## 5. 禁项、施工与旁记 soft

- 未批前不改 `README-ember-dungeons.md`；批后仅改 L4。
- 不顺带修改票表、表内路径、其它 README 段或任何地牢 YAML。
- 批后施工交 **插件岗（DungeonPlus 文档）**。
- 旁记 soft 不捆本窗：同文件「发放：插件/CoreRpg 岗」；`mail.yml` 键 `season_pass_stub`；TrMenu README 占位段。
