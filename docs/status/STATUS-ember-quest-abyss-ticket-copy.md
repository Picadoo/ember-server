# STATUS · B2.11 主线 quest「深渊票」两句 A

- 批准 tip：`ca68b64`
- 设计 tip：`4a2a671`
- 范围：方案 A；仅双路径 `quest.yml` 的 2 处玩家可见文案
- 时间：2026-09-28 23:49 Asia/Shanghai
- 状态：**PASS · 已关闭**（施工 `8605d10` · 验收 `a538d26`）

## 两处改动

| 编号 | 文件（两路径同改） | 旧 → 新 |
|---|---|---|
| Q1 hint | `plugins/CoreRpg/quest.yml`、`CoreRpg/src/main/resources/quest.yml` | `打开枢纽菜单 → 深渊（需深渊票）· 本次结算也算完成` → `打开枢纽菜单 → 深渊（耗体力）· 本次结算也算完成` |
| Q2 done | `plugins/CoreRpg/quest.yml`、`CoreRpg/src/main/resources/quest.yml` | `§6灰烛：§f票还是一天一张。能走多深，就走多深。` → `§6灰烛：§f体力日回，能走多深就走多深。` |

## 验收证据

- 热更命令：`printf 'corerpg reload\\n' > server-runtime/console.in`
- 日志（Asia/Shanghai）：`23:49:26` `Quest: 10 chapters loaded (enabled=true)`；`23:49:27` `[CoreRpg] 配置已重载 (storage=mysql)`。
- `rg '深渊票|票还是一天一张' plugins/CoreRpg/quest.yml CoreRpg/src/main/resources/quest.yml`：**EMPTY**。
- 两路径 `rg` 均命中 Q1/Q2 新文案；`git diff --check`：通过。
- 当前 diff 仅两路径各 2 行替换；两路径内容一致。

## 禁项核验

以下均为 **零 diff / 未触碰**：

- 步骤 `type` / `event` / `count` / `mobs` / `complete_on`；
- `items` 键与数量；体力 `costs`；进本；TrMenu；灾厄 OP #6；
- 其它 DP / MM / loot / 玩法结构；
- `git push`：未执行。

建议 commit：`fix(quest): B2.11 abyss ticket copy→stamina (dual-path)`
