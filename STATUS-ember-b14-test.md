# STATUS · B1.4 主线去指令文案抽检

- 时间：2026-09-27 22:31 CST（Asia/Shanghai）
- 执行：余烬-测试
- 依据：`docs/STATUS-ember-b1-quest-copy.md`、`docs/STATUS-ember-b14-quest-copy.md`
- 方式：静态 `rg`/`grep`/上下文复核；未启动服，live join 聊天 **SKIP（非必须）**
- 总评：**PASS**

## 命中表

| 检查范围 | 扫描结果 | 上下文判定 |
|---|---:|---|
| `plugins/CoreRpg/config.yml` `join_message`（L4–8） | 0 命中 | 四行均为玩家可见菜单/NPC/玩法引导；无 `/corerpg`、`/dp`、`/hub`、`/ember`，无“指令”或参数教学。关键文案为“右键灰烛”“打开枢纽菜单”。 |
| `plugins/CoreRpg/quest.yml` 第一卷 ch1–6（L25–287）的 `intro`/`hint`/`done` | 0 命中 | 玩家字段均为枢纽菜单、右键 NPC、场景/玩法提示；无 `/corerpg`、`/dp`、`/hub`、`/ember`，无 `covenant set`、`talent unlock`、`afk N` 参数教学。 |
| 第二卷 ch7–10「打开 /ember」 | 0 命中 | `rg -F '打开 /ember'` 无输出；全文件出现的 `/ember` 仅是头部注释路径 `docs/ember-mainline-spec.md`，不是玩家文案。 |

关键清零扫描：`/(corerpg|dp|hub|ember)`（目标玩家字段）= **0**；参数教学模式 = **0**。

## 旁注

- CoreRpg 版本：`plugins/CoreRpg.jar` 内 `plugin.yml` 为 **1.15.9**（仅读取，未改 jar）。
- 数值/jar：未改动。
- ops：`[]`（未动）。
