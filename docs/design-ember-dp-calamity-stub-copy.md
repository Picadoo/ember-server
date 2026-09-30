# B2.63 · DungeonPlus 灾厄 Boss 注释维护备忘薄窗

- **STATUS：已批 A（总控 · 2026-09-30 20:09 Asia/Shanghai）**
- **范围：**仅拟改 `plugins/DungeonPlus/dungeon/EmberCalamity/monster.yml` L3 注释；批前不改玩法 YAML。
- **tip 路径：**`docs/design-ember-dp-calamity-stub-copy.md`
- **施工岗：**批后施工交 **插件岗（DungeonPlus 文档注释）**。
- **本窗纪律：**禁长测、禁挑刺；勿宣称 B0.1；勿宣称灾厄完整阶段已落地；勿 git push。

## 1. 现况与范围

已读 live 文件头部，目标为 L3；L4 的阶段说明及其后 `groups`、消息、坐标、条件均不在本窗修改范围。此窗只交注释 tip，未施工目标 YAML。

## 2. L3 新旧全文对照

| 文件 / 行 | 旧（live 全文） | 新（荐案，批后全文） |
|---|---|---|
| `plugins/DungeonPlus/dungeon/EmberCalamity/monster.yml` L3 | `# 灾厄 Boss 战 stub — EmberCalamityBoss（Display：余烬灾厄使）` | `# 维护备忘：灾厄 Boss 战 — EmberCalamityBoss（Display：余烬灾厄使）` |

## 3. 荐案

```text
# 维护备忘：灾厄 Boss 战 — EmberCalamityBoss（Display：余烬灾厄使）
```

仅去掉 `stub` 口吻，改为维护备忘式事实；保留 Boss id `EmberCalamityBoss` 与 Display「余烬灾厄使」。不改变 Boss 行为、消息、坐标、条件，也不代表灾厄完整阶段已落地。

## 4. 验收

- [ ] 批后仅 `monster.yml` L3 与荐案逐字一致。
- [ ] `groups`、消息、坐标、条件及 L3 以外内容零改；目标 YAML 除 L3 注释外零 diff。
- [ ] 验收确认本窗没有玩法 YAML 施工；待批期间只提交本 tip + backlog。
- [ ] 确认目标文件保留 `EmberCalamityBoss` / Display「余烬灾厄使」事实。

## 5. 禁项与施工口径

- 不把本次注释维护表述为 B0.1 已完成；不把它表述为灾厄完整阶段已落地。
- 不做长测、不做挑刺；不顺带修改 `groups`、消息、坐标、条件。
- 批后施工交 **插件岗（DungeonPlus 文档注释）**；未批前不改 `monster.yml`。
- 旁记 soft 不捆本窗：`README-ember-dungeons.md` 表内「深渊/灾厄 stub」；`mail.yml` 键名 `season_pass_stub`（键改需对源码）。
