# STATUS · B2.61 CoreRpg schema 文件头 · 纯静态薄验收

| 字段 | 值 |
|---|---|
| 时间 | 2026-09-30 20:03 Asia/Shanghai |
| 角色 | 余烬-测试岗执行器 |
| 施工 tip | `8421383`（`docs: B2.61 CoreRpg schema header comments`） |
| 设计 tip | `docs/design/design-ember-corerpg-schema-header-copy.md` |
| 批准 tip | `6c5fd45` |
| 范围 | 仅 `plugins/CoreRpg/players/_schema-example.yml` L16、L27；纯静态薄验收；本岗未改配置 |

## 总评

**PASS**

## 验收点

1. **PASS**：L16、L27 与 tip §2 荐案逐字一致（含全角破折号/括号）。
   - L16 = `# —— 誓约 / 天赋（docs/design/DESIGN-ember-covenant-talent.md §4）——`
   - L27 = `# —— 晶钻 / 日票硬顶 / 月卡（docs/design/DESIGN-ember-cash-monthly.md §4）——`
2. **PASS**：两行均无「插件岗写入」；DESIGN 引用保留。`rg -n '插件岗写入' plugins/CoreRpg/players/_schema-example.yml` 无匹配（exit 1）。两行均含对应 DESIGN 路径与 §4。
3. **PASS**：除两行外零 diff；字段/键/数值未动。施工 tip `8421383` numstat 目标文件 `2	2`（仅替换两行分区注释）；`git show 8421383` 仅触及该路径。`cash.yml` 勋阶 stub（L98 / L104）未动（施工 tip 对该文件 diff 空；最近相关提交仍为 B2.60 `96b931a`）。
4. **PASS（口径）**：标题为维护备忘式分区标题；本报不宣称玩法已完成；不宣称 B0.1。

## L16 / L27 原文 vs tip §2

| 文件 / 行 | tip §2 荐案 | live 原文 | 一致 |
|---|---|---|---|
| `_schema-example.yml` L16 | `# —— 誓约 / 天赋（docs/design/DESIGN-ember-covenant-talent.md §4）——` | 同左 | 是 |
| `_schema-example.yml` L27 | `# —— 晶钻 / 日票硬顶 / 月卡（docs/design/DESIGN-ember-cash-monthly.md §4）——` | 同左 | 是 |

## 「插件岗写入」rg

```text
rg -n '插件岗写入' plugins/CoreRpg/players/_schema-example.yml
# → 无输出，exit 1
```

## DESIGN 旁证

```text
L16: docs/design/DESIGN-ember-covenant-talent.md §4 保留
L27: docs/design/DESIGN-ember-cash-monthly.md §4 保留
```

## 零 diff 与 cash stub 旁证

```text
git show --numstat --format='' 8421383 -- plugins/CoreRpg/players/_schema-example.yml
2	2	plugins/CoreRpg/players/_schema-example.yml
# name-only 仅上述一文件；hunk 仅 ± 两行分区注释（去掉「；插件岗写入」）

git diff 8421383^..8421383 -- plugins/CoreRpg/cash.yml
# → 空（0 行）

cash.yml L98: # 勋阶轻量 stub（无 LuckPerms）
cash.yml L104:   # tiers stub — PlayerData.vipTier default 0
# 与 tip §5 旁记 soft 一致；本窗未改
```

## 边界与操作

- 未开服、未长测、未挑刺；纯静态核对。
- 本岗未改任何 YAML/配置；仅新增本测报文档。
- 未宣称玩法已完成；未宣称 B0.1。
- `ops=[]`（测报提交前）

## 阻塞点

无。
