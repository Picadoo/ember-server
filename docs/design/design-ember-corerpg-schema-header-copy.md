# B2.61 · CoreRpg schema 分区注释维护备忘薄窗

- **STATUS：PASS · 勾销（总控 · 2026-09-30 20:04 Asia/Shanghai）** · 测 `0098df7` · 施工 `8421383` · 批准 `6c5fd45` · 设计 `72ad8a7`
- **范围：**仅 `plugins/CoreRpg/players/_schema-example.yml` L16、L27 两行分区注释；字段、键、数值及其它文件零改。
- **tip 路径：**`docs/design/design-ember-corerpg-schema-header-copy.md`
- **施工岗：**批后施工交 **插件岗（CoreRpg 文档注释）**。
- **本窗纪律：**未批前不改玩法 YAML；文档可 commit；勿 git push；不宣称玩法已完成；不宣称 B0.1；禁长测/挑刺。

## 1. 现况与问题

以下为 live 两行全文，问题仅是分区标题带有「插件岗写入」指派口吻；本窗不改变任何字段、键或数值。

```text
# —— 誓约 / 天赋（docs/design/DESIGN-ember-covenant-talent.md §4；插件岗写入）——
# —— 晶钻 / 日票硬顶 / 月卡（docs/design/DESIGN-ember-cash-monthly.md §4；插件岗写入）——
```

对应位置：`plugins/CoreRpg/players/_schema-example.yml` L16、L27。

## 2. 荐案：维护备忘式分区标题

仅替换上述两行，推荐如下：

```text
# —— 誓约 / 天赋（docs/design/DESIGN-ember-covenant-talent.md §4）——
# —— 晶钻 / 日票硬顶 / 月卡（docs/design/DESIGN-ember-cash-monthly.md §4）——
```

### 2.1 新旧两行全文对照

| 文件 / 行 | 旧（live 全文） | 新（荐案，批后全文） |
|---|---|---|
| `plugins/CoreRpg/players/_schema-example.yml` L16 | `# —— 誓约 / 天赋（docs/design/DESIGN-ember-covenant-talent.md §4；插件岗写入）——` | `# —— 誓约 / 天赋（docs/design/DESIGN-ember-covenant-talent.md §4）——` |
| `plugins/CoreRpg/players/_schema-example.yml` L27 | `# —— 晶钻 / 日票硬顶 / 月卡（docs/design/DESIGN-ember-cash-monthly.md §4；插件岗写入）——` | `# —— 晶钻 / 日票硬顶 / 月卡（docs/design/DESIGN-ember-cash-monthly.md §4）——` |

## 3. 口径硬线与禁项

- 新案只做分区标题去指派口吻，保留 DESIGN 引用；不描述玩法完成状态。
- 批前不得改目标 YAML；批后也仅替换上述两行，字段、键、数值及其它文件零改。
- 不宣称玩法已完成，不宣称 B0.1；不做长测、不做挑刺；勿 git push。
- 本窗不捆其它 CoreRpg 注释；旁记 soft 见下节。

## 4. 验收

- [ ] L16、L27 与荐案逐字一致，且两行均不含「插件岗写入」。
- [ ] 目标 schema 文件除两行分区注释外零 diff；字段、键、数值零改。
- [ ] DESIGN 引用保留；标题为维护备忘式分区标题，不形成插件施工指派。
- [ ] 批后施工交 **插件岗（CoreRpg 文档注释）**；本待批窗仅 tip + backlog 可提交。

## 5. 旁记 soft（不捆）

`plugins/CoreRpg/cash.yml` 勋阶 stub 注释仍为旁记 soft，不纳入 B2.61，也不在本窗修改：

- L98：`# 勋阶轻量 stub（无 LuckPerms）`
- L104：`# tiers stub — PlayerData.vipTier default 0`
