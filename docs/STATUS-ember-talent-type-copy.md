# STATUS · B2.16 天赋菜单类型文案 A

| 字段 | 结果 |
|---|---|
| 日期 | 2026-09-29 00:40 Asia/Shanghai |
| 执行 | 余烬-插件 executor |
| 设计 tip | `34e61dd` |
| 批准 tip | `7c1c408`（批准 A） |
| 施工文件 | `plugins/TrMenu/menus/ember_talent.yml` |
| 状态 | **施工完成 · 静态验收 PASS** |

## 施工范围

仅改 13 个玩家可见类型 lore 行：

- 根节点 ×3：`passive` → `被动`，`cost 2` 原样。
- 烬斩 ×1：去掉 `skill`，`cost 4` 原样。
- 二层 ×9：`passive` → `被动`，`cost 3/4` 原样。

未执行方案 B：没有把 `cost` 改为 `消耗`。

## 验收证据

- `rg -n 'passive|skill' plugins/TrMenu/menus/ember_talent.yml`：**0**。
- `git diff -- plugins/TrMenu/menus/ember_talent.yml`：仅上述 13 行替换（13 insertions / 13 deletions）。
- `git diff --check`：通过。
- 热更已发出：`printf 'trmenu reload\n' > server-runtime/console.in`，2026-09-29 00:40:36 Asia/Shanghai。
- `server-runtime/logs/stdout.log`：今日 `00:39:57` 记录 `[TrMenu] 良好 | 自动重新载入菜单 ember_talent.yml (14ms)`；`00:40:00` 记录 36 个菜单加载完成。

## 禁项核对

**EMPTY**：未改 `talent.yml`、unlock 参数、节点键、requires/cost/stats、其它菜单、runtime/player/world 内容；仅按交付要求写入 `server-runtime/console.in` 触发热更；未改 cost 数值；未宣称 B0.1 已清。
