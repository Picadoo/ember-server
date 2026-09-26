# DungeonPlus 其余副本 · 占位符 / 卡波修复 + 实战通关冒烟（2026-09-26 21:28–21:40 CST）

**结论：** Daily / Weekly / Abyss **PASS**（能打通、通关箱到账、回城、扣票）；GuildBoss / Calamity 能打通。但这两个副本的奖励段**故意没改**，原因见 §2。
**承接：** `docs/smoke-raid-combat-20260926.md`（EmberRaid 同类修复）
**脚本：** `mineflayer-tests/dungeon-clear-smoke.js`（新增，通用版，风格同 `raid-combat-smoke.js`）
**日志（本机，logs/ 不入库）：** `mineflayer-tests/logs/dungeon-{daily,weekly,abyss,guildboss,calamity}-20260926.log`
**版本：** 插件和 Paper 都没改，只动了 DungeonPlus 配置。改前的备份在 box 上 `/tmp/dp-bak-20260926b/`。

## 1. 改了什么

### 1a. `<player.name>` → `%player_name%`（DP `$command` 不解析 `<player.name>`）
| 文件 | 处数 | 效果 |
|------|------|------|
| `EmberDaily/option.yml` | 4 | 通关箱 + 回城 |
| `EmberWeekly/option.yml` | 7 | 通关箱（含 T1 刃/符保底）+ 回城 |
| `EmberAbyss/option.yml` | 5 | 进本 `abyss progress 0`、`abyss settle`、T2 刃/符、回城 |
| `EmberAbyss/monster.yml` | 8 | 每层 `corerpg abyss progress N`（原先最高层永远记不上） |
| `EmberAbyss/task/timeout.yml` | 1 | 超时强制结算 |

**故意没改：**
- `DungeonPlus/config.yml` 里的 `dungeon-chat.prefix`（`<player.name>`）：这是 DP 队内聊天自己的格式符（jar 默认配置就这么写）。实测能正常渲染成 `[队内消息] ChA926: teamchat-probe`，改了反而会坏。
- `EmberCalamity/option.yml`、`EmberGuildBoss/option.yml`：见 §2。已在文件里加注释说明原因。

### 1b. 逐波核对击杀要求和刷怪数（DP 的 kill 条件按**本组**计数；mobname 要和 MM Display 去色后完全一致）
| 副本 · 波 | 原来 | 本组实刷 | 修成 |
|-----------|------|----------|------|
| EmberDaily wave2 | kill-any 8 | 2 僵尸 + 2 骷髅 = 4 | **4** |
| EmberWeekly wave2 | kill-any 13 | 3 + 4 = 7 | **7** |
| EmberAbyss floor3 | kill 骨潮 7 | 骨潮 ×4 | **4** |
| EmberGuildBoss wave1 | mobname `余烬深渊僵尸,余烬深渊骷髅` | Display 实为 `余烬深渊·潮尸` / `余烬深渊·骨潮` | **改名**（数量 6 本来就对） |
| EmberGuildBoss wave2 | mobname `余烬深渊蛮兵` | Display 实为 `余烬深渊·蛮层` | **改名** |

其余各波（Abyss 1/2/4–8、Daily 1/boss、Weekly 1/3/boss、GuildBoss boss、Calamity boss）数量和名字都对得上。只杀首领的波次允许有添头活着，属于正常设计。

### 1c. 生效方式
用 `server-runtime/stop.sh` 停服 → `ops.json` 临时只放 `RpgBot` → `start.sh` 启动（重启即重新加载全部 DP 配置，5 个副本导入/初始化都正常）→ 测完再停服 → `ops.json` 写回 `[]` → 启动。两次停服时服上都只有测试机器人，没有真人。

## 2. ⚠️ 为什么 Calamity / GuildBoss 的奖励占位符没改（需要你拍板）

> **后续（2026-09-26 第三刀）：** Calamity 已加 OP 门（`%player_is_op%` js-condition），并已修 `%player_name%`；GuildBoss 纯配置堵不住直开，需要改代码，奖励仍然暂缓。MM `>0` 几率行已审计并修复。见 [smoke-gating-drops-20260926.md](smoke-gating-drops-20260926.md)。
- **EmberCalamity**：没有票，也没有权限门。`/dp start EmberCalamity` 人人可用，`ember_calamity` 菜单的 T 位（标着「测试实例（OP）」）也能直接点。一旦占位符生效，每次通关都会**必发 `gear_ember_t3_talisman`** + 核心碎片 2 + 附魔晶 + 灾厄余烬 2，变成无限刷 T3 的口子。
- **EmberGuildBoss**：`/corerpg guild boss` 扣贡献后，是**以玩家身份** dispatch `dp start EmberGuildBoss`，所以玩家自己直接 `/dp start EmberGuildBoss` 就能绕开贡献和周次限制（本次测试就是这样直接开的）。
- **已经存在的漏洞（和本次改动无关）：** 这两个实例的 Boss 都是 `EmberCalamityBoss`，所以现在就会触发 CoreRpg 灾厄结算（每次 kill_rewards + 每天 1 次日箱）和尾刀者的 MM 掉落（凝核、锋利石、碎片）。实测 GuildBoss 直开一次：尾刀者拿到核心碎片 +5、凝核 1、锋利石 1、晶 1…；Calamity 实例同理。
- 建议：先加门（只允许 OP / 要票 / 改成 start-console 启动），再把这两个文件的 `<player.name>` 换掉。换法同 1a，一行 sed 就行。

## 3. 实战结果（每个副本 2 人队，队员不给 op，只加测试增益 resistance 4 / regeneration 4 / strength 9）
开本前队员在 `world`（不在大厅），操作员在 ember_hub 出生点等着。通关后能在大厅看到队员，就说明回城的 mvtp 执行了。

| 副本 | 打通 | 用时 | 扣票（每人） | 通关箱（每人） | 回 ember_hub | 结果 |
|------|------|------|--------------|----------------|--------------|------|
| **EmberDaily** | ✔ 两波 + 蛮兵 | 23s | 日票 −1 ✔ | 核心碎片 1、晶 1、碎片 5 ✔ | ✔ | **PASS** |
| **EmberWeekly** | ✔ 前厅→中核→甲→乙 | 34s | 周票 −1 ✔ | 核心碎片 3、晶 2、碎片 8、骨尘 4、T1 刃 1、T1 符 1 ✔ | ✔ | **PASS** |
| **EmberAbyss** | ✔ 8 层全通（floor3 按 4 只放行） | 76s | 深渊票 −1 ✔ | `abyss progress` 0→8 逐层播报；settle「层 8 → 碎片×12 骨尘×4 核心×1 孔石×1」；T2 刃 1、T2 符 1 ✔ | ✔ | **PASS** |
| EmberGuildBoss（直开） | ✔ 两波 + 灾厄使（改名后能过波） | 40s | 无票 | DP 奖励段没改，所以没发（只有灾厄结算 + MM 掉落） | 没回（mvtp 没改） | 能打通；奖励暂缓 |
| EmberCalamity | ✔ 灾厄使 | 22s | 无票 | 同上 | 没回 | 能打通；奖励暂缓 |

## 4. 新发现：MythicMobs 里带几率的死亡掉落全部没生效（没修，需要决定）
- MM 技能行 `... @Trigger ~onDeath >0 0.35` 里的 `>0` 是**血量条件**（要求血量 > 0）。怪死的时候血量是 0，所以这一行**永远不会执行**，后面的几率也就没意义。不带 `>0` 的固定掉落（例如 Daily/Weekly 小怪的碎片、骨尘）是正常的。
- 证据：今天所有冒烟里，NI 从来没发过 `gem_ember_steady` / `gem_ember_drain` / `gem_ember_gale` / `mat_ember_stable_charm`。灾厄 Boss 被击杀了 6 次以上，稳固石（标称 70%）一次都没掉。深渊 2 人打通 8 层、杀了约 30 只怪，MM 掉落是 0。团本 Boss 的 T2/T3 几率也从来没出过。
- 受影响的行数：EmberAbyss 19、EmberRaid 10、EmberWeekly 6、EmberCalamity 5、EmberCrypt 4、EmberDaily 3，**共 47 行**（`grep -c '~onDeath >0' plugins/MythicMobs/Mobs/*.yml`）。
- 修法：把 `>0 0.35` 改成只写几率 `0.35`（MM 4.x 的写法是 `~trigger [血量条件] [几率]`）。**但这会一次性打开全部几率掉落（包括 T2/T3 装备），是经济层面的改动**，所以本刀没动，请策划确认后再改，改完再实测一轮。

## 5. 服务器状态
- 游玩服 :25565 重启了 2 次（21:29、21:39 CST）。现在 pid 960526，MySQL 已连上，5 个副本 + EmberRaid 的 DP 内容都导入并初始化了。登录服 :25566 和 MariaDB 没动过。
- `server-runtime/ops.json` = `[]`（login 服也是 `[]`）。
- 测试机器人 `Dc*/Wk*/Ab*/Gb*/Cd*/Ch*926` 今天留下了票和掉落记录；`Gb*`/`Cd*` 的灾厄日箱今天已经领过。
- 22:00 的灾厄正式窗会照常开。
