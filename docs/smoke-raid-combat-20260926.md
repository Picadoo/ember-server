# 团本 EmberRaid · 实战通关冒烟 + 灾厄助攻份额（2026-09-26，Asia/Shanghai CST）

**结论：PASS（修 2 处 DungeonPlus 配置后）**
**规格：** `docs/ember-raid-channel-spec.md`（§3.2 周首通团戒、§4 同袍 2 件套、§6 验收）
**版本：** CoreRpg **1.4.8 未变**（本刀没改插件源码，没出新 jar）；DungeonPlus 1.4.5；MythicMobs 4.11.0；NeigeItems 1.21.151；Paper 1.12.2 custom（未改）
**脚本：** `mineflayer-tests/raid-combat-smoke.js`（新增，风格同 `calamity-combat-smoke.js`）· `mineflayer-tests/op-cmd.js`（小工具：op 机器人执行几条命令）
**日志（本机，logs/ 不入库）：** `mineflayer-tests/logs/raid-combat-20260926-run{1..4}.log`（run4 = 最终全绿）· `logs/calamity-helper-20260926.log`

---

## 1. 测法

- 机器人真打：3 人队 `R4A926`（队长）/ `R4B926` / `R4C926`，场外非参与者 `R4Out926` 在 ember_hub 挂着；操作员 `RpgBot` 负责 /tp、发票、发装。
- 队员**不给 op**。只加测试增益：`resistance 4`、`regeneration 4`、`strength 9`。怪、技能、掉落链都没动。
- A、B 拿 NI `gear_ember_blade` 当主手武器；C 只拿原版钻石剑、不给刃（同袍的对照组）。
- 组队：`/dungeon-team create` → `invite` → `/dungeon-team request join <队长>`；`/dp start EmberRaid`。
- 按 NI lore 里的 id 统计背包增量；同袍触发看 `entity_effect` 包里的 Saturation（id 23），也就是 `set.yml effect.on_kill: saturation_1`。
- 同周二刷：每人再发 1 张票，等 DP 5 秒冷却后再开。
- 跨周：A 下线 → MySQL 里把 A 的 `raidRingWeek` 改成 `2000-W01`（模拟上周已领）→ A 上线 → `corerpg raid grant-ring`（就是通关箱调用的那条命令）打两次。

## 2. 发现并修复的问题

### Bug 1：wave2 卡波，团本打不到终厅（`EmberRaid/monster.yml`）
- 现象（run2 日志）：【右道】这一波的 3 卫兵 + 4 射手全部击杀后，场上 MM 活怪 = 0（在副本世界里跑 `/mm mobs listactive` 核实过），但一直不出「通道打开」，精英和 Boss 都不刷。
- 原因：`$kill-any{...;amount=13}`。DP 的 kill 条件**按本组计数**，这一波只刷 7 只，13 永远达不到（作者把它当成跨波累计了：6+7=13）。
- 修复：`amount=13 → 7`。复测时 wave1 结束后 12 秒左右 wave2 正常结束，结束时场上几乎不剩怪，符合按组计数（如果是累计计数，砍掉第 1 只就会过）。

### Bug 2：通关箱、团戒、回城从来没发过（`EmberRaid/option.yml`）
- 现象（run3）：打通后只有 `$message` 播报，背包什么都没加；控制台 15 行 `NeigeItems > 玩家 <player.name> 不在线`，另有 `玩家不在线: <player.name>`（grant-ring）、`couldn't find player: <player.name>`（mvtp）。
- 原因：DP `$command` 不解析 `<player.name>`，命令原样执行了。
- 修复：7 处 `<player.name>` 改成 PAPI `%player_name%`（DP 自带的 `actionscript/example.yml` 就是这么写的；`Expansion-player` 已装）。复测每人都拿到通关箱和团戒。

两个文件的旧版备份在 `/tmp/EmberRaid-{monster,option}.yml.bak-20260926`（box 上）。已 `/dp reload`。

## 3. 结果（run4，修复后）

| # | 检查 | 结果 |
|---|------|------|
| 1 | 2 人开本被拒：「团本人数 3～5，当前 (2)」 | PASS |
| 1b | 3 人持票进本；每人各扣 1 张（DP 的 js-condition 对**每个队员**都查票，缺一个全队拒进） | PASS |
| 2 | 播报：进本 / 【左道】/【右道】/【汇合】/【终厅】使徒 / 通关 | PASS（两轮都齐） |
| — | 实战通关：wave1→2→3→Boss，击杀终厅使徒 | PASS（约 51s / 53s） |
| 3 | 通关箱全队：核心碎片 4、附魔晶 2、碎片 10、骨尘 6、锋利石 1；**没有**必给 T3 | PASS |
| 4a | 本周首通：3 人各得 `acc_ember_raid_ring` ×1，「获得余烬团戒（周首通 2026-W39）」 | PASS |
| 4b | 同周再通：3 人都没拿到戒，提示「本周团戒已领取」；材料箱照发 | PASS |
| 4c | 跨周：`raidRingWeek` 是上周 → grant-ring 发 1 枚；同周再调拒发 | PASS |
| 5a | `/corerpg set`：通关前 A「未激活 刃✓ 戒✗」→ 通关后 A、B「已激活 · 套装：余烬同袍 ✓」；C（没刃）「未激活 刃✗ 戒✓」 | PASS |
| 5b | 击杀触发：第 1 轮（没戒）三人 0 次；第 2 轮 A 14 次、B 4 次 Saturation；C 0 次 | PASS |
| — | 非参与者 R4Out926：两轮背包增量 `{}`，没收到任何团戒/结算消息 | PASS |
| — | 本内提前领戒：B（非 op）在 wave1 里输 `/corerpg raid claim-ring` → 没反应、没戒（DP 本内命令白名单只放行 `dp`） | PASS |
| 6 | 深渊撤离、灾厄刷怪点：团本相关没改这两套；灾厄见 §5 | PASS |

Boss 的 MM 概率掉落（T2/T3）两轮都没出，属于正常概率。尾刀者 A 每轮多拿凝核 1 + 核心碎片 2（MM onDeath）。

## 4. 服务器状态 / 异常（需要知道）

> **21:40 CST 更新：** 第 1、2 条已在 `docs/smoke-dungeons-clear-20260926.md` 里处理：Daily/Weekly/Abyss 已修复并实测 PASS；另外修了 GuildBoss 的 mobname 和 Abyss floor3。Calamity/GuildBoss 的奖励占位符因为没有门控暂缓。第 3 条（ops 为空）仍然成立。

1. **其他副本也有同样的 `<player.name>` 占位符 bug，没修（不在本刀范围，也没实测）：** EmberAbyss（option 5 处 + monster 8 处，含每层 `corerpg abyss progress`）、EmberCalamity 5、EmberDaily 4、EmberGuildBoss 6、EmberWeekly 7。这些副本的通关箱、结算、回城很可能也从来没执行过。照 Bug 2 改成 `%player_name%` 就行，建议单开一刀修完再实测。
2. **同样的 kill-any 累计写法：** EmberDaily wave2 `amount=8`（本波只刷 4 只）、EmberWeekly wave2 `amount=13`（本波只刷 7 只），大概率一样卡在第二波。没改。
3. **已按要求 de-op 全部测试机器人**：MenuBot、PassBot2、RpgBot、OpReload、AbyssBot、Tester、DpBot、SetBot、WareBot（CalKill926 和团本机器人由脚本 deop）。`server-runtime/ops.json` 现在是 `[]`，login 服本来就是空的。**现在没有任何游戏内 op**，服务器也没开 RCON，进程是 nohup 起的（没有可交互的控制台）。以后跑冒烟要先停服改 `ops.json`，或者在控制台执行 `op RpgBot`，用完再 deop。
4. **安全提示：** 游玩服 `online-mode=false`、没开白名单、:25565 直接可连。之前 ops.json 里挂着 9 个机器人名，任何人用这些名字登录就是 op。这次 de-op 已经消除了这个风险，但离线模式直连本身仍是个口子（是否只允许经登录服/代理进入，请确认）。
5. DP 每次建团本实例，Multiverse 都会报 WARN「does not know about this world … /mv import dungeon_EmberRaid_xxx」。无害。`plugins/DungeonPlus/dungeon-caches/` 里留着 8 个旧实例缓存目录。
6. 灾厄：今天为测试 forceopen/forceend 了 2 窗（只占 force 槽），22:00 的正式窗照常自动开（20:00 那窗自动开了，没人打，到点关）。
7. 服务在跑：MariaDB（mysqld_safe，18:48 起）、登录服 :25566（pid 890274）、游玩服 :25565（pid 894247，18:53 起），全程没重启。`server-runtime/hs_err_pid45801.log` 是 09-21 的旧 JVM 崩溃文件。
8. 测试数据：`R4A926` 因为跨周测试背包里有 2 枚团戒，`RdA926` 1 枚（run1 票没发够，只跑了跨周那一段）；`R3*`/`RaidA926`/`RaidB926`/`RaidC926` 有半截或未发奖的记录；所有 Raid/R3/R4 机器人本周 `raidRingWeek=2026-W39`（只要领过的）。

---

## 5. 灾厄助攻份额（CoreRpg `config.yml calamity.kill_rewards`）

**改动：** 加了 1 行 `ni give {player} mat_ember_core_fragment 1`（NI id 在 `NeigeItems/Items/ember-dungeon.yml` 里有）。`kill_rewards` 发给击杀者和本窗所有造成过伤害的在线玩家（CoreRpg 1.4.8 逻辑）；MM 的 `~onDeath` 只发给尾刀者（凝核 `core_ember_compact`×1 + 核心碎片×2 + 孔石…）。
- 助攻者新得：**核心碎片 ×1**（少于尾刀 MM 的 2 枚）。
- **没加凝核：** `ni give` 数量是固定的，最少 1 个，发 1 个就和尾刀的 MM 凝核一样多，不满足「比击杀者少」。如果要给助攻者凝核，得做成概率掉落（例如 NI ItemPack 带几率，或给 CoreRpg kill_rewards 加 chance 前缀），这一步留给策划决定。
- 已 `/corerpg reload`（成功，storage=mysql）。

**复测**（`calamity-combat-smoke.js`，新增可选的 `HELPER` 环境变量：第 3 个机器人每窗砍 3 刀、不补尾刀；`WATCHER` 保持零伤害旁观；脚本结束时 deop FIGHTER）：
`FIGHTER=CalKill926 HELPER=CalHelp926 WATCHER=CalBy926`

| 检查 | 结果 |
|------|------|
| 窗 1 击杀者：核心碎片 +5（MM 2 + 日箱 2 + 新行 1）、晶 +1、凝核 +1、锋利石 +1… | PASS |
| 窗 1 助攻者：核心碎片 +3（日箱 2 + **新行 1**）、晶 +1、余烬 +3、外观 +1；「领取本日灾厄日箱」 | PASS |
| 窗 1 旁观者：`{}` | PASS |
| 日志 `Calamity kill settled: killer=CalKill926 recipients=2`（两窗都是） | PASS |
| 同窗不二刷；forceend / status | PASS |
| 窗 2 助攻者：核心碎片 +1、余烬 +1、外观 +1，**没有晶**，「今日日箱已领，仅参战掉落」→ 日箱每天限 1 次仍然有效 | PASS |
| 窗 2 击杀者：没有晶（日箱已领）；窗 2 旁观者 `{}` | PASS |
