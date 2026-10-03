# STATUS · 主线第二卷 ch7～10 阶段 4.5 验收（测试岗）

- 时间：2026-09-27 19:51:09～19:53:47（Asia/Shanghai）
- 执行：余烬-测试岗
- 依据：`docs/status/STATUS-ember-quest-vol2-4.5.md`、`docs/design/design-stage4-quest-vol2.md`
- 脚本：`mineflayer-tests/quest-vol2-4.5-smoke.js`（日志 `/tmp/quest-vol2-4.5.log`）
- 账号：`Qv2_1187`（新号）；测中 OP：`RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`）
- **未修改玩法数值**、深渊/精英/挂机平衡、Paper/MM

## 总评

**PASS**。

CoreRpg **1.15.1** 已加载 **10 chapters**；`quest set 7 0` 正确进入「第二卷 · 烬原之下」；`forge` / `abyss_floor` / `elite_weekly_clear`（经 `progress elite_weekly`）均可推进；ch6 末 talk 完成后自动开 ch7；TrMenu `/ember` 主线按钮为「主线 · 旧誓余烬 / 烬火未灭」；ch7～10 hint 无 `/corerpg` `/dp` `/dungeon` `/hub`；定额 XP 合计 **4600**。

## 分项结果

| # | 项 | 结果 | 证据摘要 |
|---|---|---|---|
| 1 | `quest set <p> 7 0` 开 ch7 | **PASS** | OP：`quest set Qv2_1187 → 第二卷 · 烬原之下 · 向 灰烛…`；玩家 `/corerpg quest` 同章名 |
| 1b | ch6 完成后自动开 ch7 | **PASS** | `quest set 6 3` + 灰烛 talk →「第6章「同袍之誓」完成！」→「第7章 · 烬原之下」intro；随后 quest 为第二卷 · 烬原之下 |
| 2a | event `forge` | **PASS** | `quest set 7 2` + `quest event forge` →「刃认新主了。」→ 目标变 Lv.40 |
| 2b | event `abyss_floor`（层数） | **PASS** | `quest set 8 2` + `quest event abyss_floor 9` → `best=9`、「第 9 层的蛮压，你顶住了。」 |
| 2c | `elite_weekly_clear`（progress 双触发） | **PASS** | `quest set 9 2` + `progress … elite_weekly` →「词缀怪比普通周本难缠。你过了。」（progress 兼发 quest） |
| 3 | ch7～10 步骤类型抽检 | **PASS** | talk（灰烛）/ kill 目标 `EmberAfk4*` / forge / abyss_floor / elite_weekly / talent / level 门均可强制或 state 推进；ch10 复合 `abyss_floor\|elite_weekly_clear` + `floor:10` → 下一步「回到 灰烛」 |
| 4 | TrMenu hub 主线文案 | **PASS** | `/ember` 运行时按钮名含「主线 · 旧誓余烬 / 烬火未灭」；静态 `ember_hub.yml` 同文案 |
| 5 | hint 抽样（ch7～10） | **PASS** | YAML 汇总：无 `/corerpg` `/dp` `/dungeon` `/hub`；运行时提示亦为「打开 /ember…」 |
| 6 | 定额 XP 章末合计 | **PASS** | 900+1100+1200+1400 = **4600**（与设计一致） |

## 环境与版本

| 项 | 值 |
|---|---|
| CoreRpg jar / 运行时 | **1.15.1**（`plugins/CoreRpg.jar`；日志 `Enabling CoreRpg v1.15.1`） |
| Quest 章数 | **10 chapters loaded** |
| 游玩服 | `server-runtime` 短重启（测前写 ops → `./stop.sh` → `./start.sh custom` → Done `19:51:20` CST） |
| 端口 | proxy **25565** / play **25567** |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |

## 测后状态

- RpgBot / 测试号已 `/deop`
- `server-runtime/ops.json`：`[]`
- `login-runtime/ops.json`：`[]`
- 游玩服与 proxy 仍运行；**未**改玩法数值

## 备注

- 管理命令（`quest set/event`、`progress`）仅测试用，未写入玩家文案。
- kill 步未真打完 12 只 EmberAfk4*（按验收允许 `quest set` 强制推进）；步骤描述与 hint 已在 quest UI 确认。
- `%corerpg_quest_chapter%` 侧边栏/ lore 依赖已有 PAPI；hub lore 配置为 `%corerpg_quest_chapter%`，本次以按钮显示名 + quest UI「第二卷 · …」为准。
