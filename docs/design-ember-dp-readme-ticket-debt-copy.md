# B2.68 · DungeonPlus README DEBT 段维护备忘

- **STATUS：待批 A**
- **范围：**仅 `plugins/DungeonPlus/README-ember-dungeons.md` 约 L40 的 DEBT 行；未批准前不改目标 README，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-dp-readme-ticket-debt-copy.md`
- **施工岗：**批后施工交 **插件岗（DungeonPlus 文档）**。
- **本窗纪律：**只把 DEBT 标签改为维护备忘口吻；保留 DP 显示名扣次与 NI ID 给票/计数的技术事实；勿 git push。

## 1. 现况与范围

已读 live README 约 L40 的 DEBT 行。目标只将「DEBT（票务 NI 对齐）」改成「维护备忘（票务 NI 对齐）」；不改 DP 扣次写法、NI ID 事实、引用路径或其它 README 段。

## 2. DEBT 行旧/新全文对照

旧（live 全文，约 L40）：

```text
> **DEBT（票务 NI 对齐）：** 进本扣次仍用 DP `<item:显示名>`（官方只认物品名）；给票/计数已走 NI ID。详见 `/workspace/minecraft/STATUS-ember-ticket-ni-audit.md`。
```

新（荐案，批后全文）：

```text
> **维护备忘（票务 NI 对齐）：** 进本扣次仍用 DP `<item:显示名>`（官方只认物品名）；给票/计数已走 NI ID。详见 `/workspace/minecraft/STATUS-ember-ticket-ni-audit.md`。
```

## 3. 荐案口径

- 仅将 `DEBT` 标签改为 **维护备忘**，保留「票务 NI 对齐」主题。
- 保留技术事实：进本扣次仍用 DP `<item:显示名>`，官方只认物品名；给票/计数已走 NI ID。
- 保留现有审计文档路径；不把本行改写成玩家操作说明或票务状态宣告。
- 不写「票已废」或「B0.1 已清」；不借本窗宣称玩法债务已完成。

## 4. 验收

- [ ] 批后仅上述 DEBT 行与荐案逐字一致，行号以施工后复核为准。
- [ ] `DEBT` 标签改为「维护备忘」；「票务 NI 对齐」保留。
- [ ] DP `<item:显示名>` 扣次、官方只认物品名、给票/计数走 NI ID 三层技术事实保留。
- [ ] README 次数段导语「入场卷」、票表、发放句、其它段与 `mail` 零改。
- [ ] `plugins/DungeonPlus/dungeon/` 下地牢 YAML 零 diff；不改玩法、门控、奖励或数值。
- [ ] 未批前不改目标 README；本稿只提交 tip 与 backlog。

## 5. 施工与禁项

- 批后施工岗：**插件岗（DungeonPlus 文档）**。
- 禁捆次数段导语「入场卷」、票表、发放句、地牢 YAML、`mail`；不顺手改票表或其它 README 文案。
- 禁写「票已废」或「B0.1 已清」及等价完成宣称。
- 禁长测/挑刺；精英壳勿硬开；勿 git push。
