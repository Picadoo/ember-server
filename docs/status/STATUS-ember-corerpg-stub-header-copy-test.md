# STATUS · B2.60 CoreRpg stub 文件头 · 纯静态薄验收

| 字段 | 值 |
|---|---|
| 时间 | 2026-09-30 19:59 Asia/Shanghai |
| 角色 | 余烬-测试岗执行器 |
| 施工 tip | `96b931a`（`B2.60: update CoreRpg stub header comments`） |
| 设计 tip | `docs/design/design-ember-corerpg-stub-header-copy.md` |
| 批准 tip | `45f2dcf` |
| 范围 | 仅 `plugins/CoreRpg/cash.yml` L2、`covenant.yml` L2、`talent.yml` L4；纯静态薄验收；本岗未改配置 |

## 总评

**PASS**

## 验收点

1. **PASS**：三行与 tip §2 荐案逐字一致（含全角冒号/分号/括号）。
   - `cash.yml` L2 = `# 维护备忘：配置可先落盘；落地后纳入 CoreRpg reload`
   - `covenant.yml` L2 = `# 维护备忘：配置可先落盘；热重载纳入 CoreRpg reload（落地后）`
   - `talent.yml` L4 = `# 维护备忘：扩树与 unlock/reset 尚待实现；本文件为契约形状`
2. **PASS**：三文件无 `STUB`、无「插件岗」。`rg -n 'STUB|插件岗' plugins/CoreRpg/cash.yml plugins/CoreRpg/covenant.yml plugins/CoreRpg/talent.yml` 无匹配（exit 1）。
3. **PASS**：除指定三行外零 diff。施工 tip `96b931a` numstat 各文件 `1	1`（仅替换一注释行）；`git show 96b931a` 仅触及上述三路径，未动玩法数值/键/其它 YAML。工作区三文件 clean。
4. **PASS（口径）**：文案保留「可先落盘」「reload」「契约形状」；含「尚待实现」，未宣称命令/扩树/unlock/reset 已完成；本报不宣称 B0.1。

## 三行原文 vs tip §2

| 文件 / 行 | tip §2 荐案 | live 原文 | 一致 |
|---|---|---|---|
| `cash.yml` L2 | `# 维护备忘：配置可先落盘；落地后纳入 CoreRpg reload` | 同左 | 是 |
| `covenant.yml` L2 | `# 维护备忘：配置可先落盘；热重载纳入 CoreRpg reload（落地后）` | 同左 | 是 |
| `talent.yml` L4 | `# 维护备忘：扩树与 unlock/reset 尚待实现；本文件为契约形状` | 同左 | 是 |

## STUB / 插件岗 rg

```text
rg -n 'STUB|插件岗' plugins/CoreRpg/cash.yml plugins/CoreRpg/covenant.yml plugins/CoreRpg/talent.yml
# → 无输出，exit 1
```

## 零 diff 旁证

```text
git show --numstat --format='' 96b931a -- plugins/CoreRpg/cash.yml plugins/CoreRpg/covenant.yml plugins/CoreRpg/talent.yml
1	1	plugins/CoreRpg/cash.yml
1	1	plugins/CoreRpg/covenant.yml
1	1	plugins/CoreRpg/talent.yml
# name-only 仅上述三文件；各 hunk 仅 ± 一行注释
```

## 边界与操作

- 未开服、未长测、未挑刺；纯静态核对。
- 本岗未改任何 YAML/配置；仅新增本测报文档。
- 未宣称命令/扩树已完成；未宣称 B0.1。
- `ops=[]`（测报提交前）

## 阻塞点

无。
