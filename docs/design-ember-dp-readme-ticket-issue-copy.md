# B2.66 · DungeonPlus README 次数段发放维护备忘

- **STATUS：已批 A（总控 · 2026-09-30 20:22 Asia/Shanghai）** · 设计 `9499ef8`
- **范围：**仅 `plugins/DungeonPlus/README-ember-dungeons.md` 次数段末「发放」句；批前不改目标 README，不改地牢 YAML。
- **tip 路径：**`docs/design-ember-dp-readme-ticket-issue-copy.md`
- **施工岗：**批后施工交 **插件岗（DungeonPlus 文档）**。
- **本窗纪律：**去「插件/CoreRpg 岗」指派口吻；管理/测试句须明确仅物测；不教玩家手打斜杠；勿 git push。

## 1. 现况与范围

已读 live README 次数段并录下发放句全文。目标只把次数段末发放说明改为维护备忘：保留自动发放尚未落地前的管理/测试物测信息，但不把指令写成玩家操作说明；不改次数表、表内路径、README 其它段或任何玩法 YAML。

## 2. 发放句新旧全文对照

旧（live 全文，L41）：

```text
发放：插件/CoreRpg 岗（未做自动发放前用 `/ni give <玩家> ticket_ember_daily 3` 测）
```

新（荐案，批后全文）：

```text
维护备忘：自动发放未落地前，仅管理/测试可用 `/ni give <玩家> ticket_ember_daily 3` 做物测；不作为玩家操作说明。
```

## 3. 荐案口径

- 去掉「插件/CoreRpg 岗」指派，不指定岗位或团队领取发放工作。
- `/ni give …` 仅作为管理/测试物测备忘保留，并明确不作为玩家操作说明。
- 只改上述发放句；次数表「日发/周发/深渊发」、物品草案、DEBT、表路径和其它 README 段不在本窗。
- 不写「B0.1 已清」或任何等价完成宣称；禁长测、禁挑刺；精英壳勿硬开。

## 4. 验收

- [ ] 批后仅 README 次数段末发放句与荐案逐字一致，行号以施工后复核为准。
- [ ] 新句无「插件/CoreRpg 岗」指派口吻。
- [ ] 新句若保留 `/ni give …`，明确「仅管理/测试」及「不作为玩家操作说明」。
- [ ] README 次数表、票表旧债、表路径、mail 与其它段零改。
- [ ] `plugins/DungeonPlus/dungeon/` 下地牢 YAML 零 diff；不改玩法、门控、奖励或数值。
- [ ] 未批前不改目标 README；本稿只提交 tip 与 backlog。

## 5. 禁项、施工与旁记 soft

- 禁捆票表「日发/周发/深渊发」体力时代旧债；另窗处理。
- `mail.yml` 键 `season_pass_stub` 不捆本窗。
- 批后施工岗：**插件岗（DungeonPlus 文档）**。
- 不宣称 B0.1 已清；禁长测/挑刺；精英壳勿硬开；勿 git push。
