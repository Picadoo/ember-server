# 灾厄公共窗 · 实战击杀 + 日箱冒烟（2026-09-26，Asia/Shanghai）

**结论：PASS（修复 1 处后）**
**版本：** CoreRpg 1.4.7 → **1.4.8**（仅 `CalamityService` 改动）；MythicMobs 4.11.0；NeigeItems 1.21.151；Paper 1.12.2 custom（未改）
**规格：** `docs/ember-calamity-window-spec.md`
**脚本：** `mineflayer-tests/calamity-combat-smoke.js`（`FIGHTER` / `WATCHER` / `ASSIST=1` 环境变量）

## 0. 环境恢复

| 项 | 结果 |
|----|------|
| MariaDB | 本机原先未安装（迁移机）。apt 装 Debian `mariadb-server 11.8.6`，`mysqld_safe --user=mysql` 启动，建 `ember`/`authme` 库与 `ember` 用户（口令取自 secrets），导入 `minecraft-export/sql/{ember,authme}.sql` |
| 登录服 :25566 | 监听 ✔ · `[AuthMe] MySQL setup finished` |
| 游玩服 :25565 | 监听 ✔ · `[CoreRpg] [storage] MySQL connected: 127.0.0.1:3306/ember pool=10` |

## 1. 测法

- 真人机器人（mineflayer）实打：op 后 `/clear`、原版钻石剑、`strength 9`/`resistance 4`/`regeneration 4` 效果（仅测试增益，Boss 与掉落链未动）。
- `forceopen` → Boss 在 `ember_event (-96.5,64,266.5)` 刷出 → 机器人贴身近战直至 `EntityDeathEvent`（killer = 机器人）。
- 旁观者机器人站在 Boss 22 格外、**零伤害**（首轮）；第三轮让其补 3 刀但不补尾刀（助攻）。
- 按 NeigeItems lore 中的 NI id 统计背包增量。
- 同日第二窗：`forceend` → `forceopen` → 再击杀。

奖励来源对照：
- **A 参战**：MM `EmberCalamityBoss` `~onDeath @Trigger`（仅尾刀者：`mat_calamity_ember`1、`gem_ember_sharp`1、`core_ember_compact`1、`mat_ember_core_fragment`2 + 概率项）+ CoreRpg `kill_rewards`（全体参战者：`mat_calamity_ember`1、`cosmetic_calamity_shard`1）。
- **B 日箱**：`mat_ember_core_fragment`×2、`crystal_ember_enchant`×1、`mat_calamity_ember`×2、保护券 10%。`crystal_ember_enchant` 只来自 B，可用来判定 B。

## 2. 结果

### Run 1（1.4.7 原版）— 发现问题

| 检查 | 结果 |
|------|------|
| forceopen 公告 + 刷 1 只 Boss | PASS |
| 实打击杀（28 刀 / 21s） | PASS |
| 击杀者 A + B（碎片+4、晶+1、余烬+4…） | PASS |
| 同窗再 forceopen →「本窗已刷过 Boss，同窗不二刷」，场上 0 Boss | PASS |
| forceend →「本轮窗口结束」；status →「灾厄未苏醒。下一窗：20:00」 | PASS |
| 同日第二窗击杀 → 只有 A、「今日日箱已领，仅参战掉落」、无晶 | PASS |
| **零伤害旁观者不应获得奖励** | **FAIL**：22 格外的 CalWatch 和操作员 RpgBot 都拿到了 A 与**日箱 B**（原逻辑 = 80 格内所有玩家）。与规格 §1/§3「有伤害贡献」不符，可挂机白拿日箱 |

### 修复（CoreRpg 1.4.8）

`CoreRpg/src/main/java/town/sunshine/corerpg/CalamityService.java`：
- 改为 `Listener`，监听 `EntityDamageByEntityEvent`（MONITOR，ignoreCancelled，finalDamage>0；含玩家射出的弹射物），把对灾厄 Boss 造成伤害的玩家记入 `damagers`。
- 结算对象 = 击杀者 + 本窗有伤害的在线玩家（原先是 80 格内所有玩家）；开窗与结算后清空。
- 版本号 1.4.7→1.4.8（`pom.xml`、`plugin.yml`）。其余类编译产物与线上 1.4.7 jar 逐字节大小一致（源码与线上一致已核对）。
- 旧 jar 备份：`cache/jar-backup/CoreRpg-1.4.7-20260926.jar`。

### Run 2（1.4.8，CalBotB + 零伤害 CalWatch2）

| 检查 | 结果 |
|------|------|
| 窗 1 击杀 → 击杀者 A+B（碎片+4、晶+1、余烬+4、外观+1、凝核+1、锋利石+1）；「领取本日灾厄日箱」 | PASS |
| 零伤害旁观者无任何奖励 | PASS |
| 同窗不二刷（场上 0 Boss） | PASS |
| forceend / status | PASS |
| 窗 2 同日击杀 → 仅 A（碎片+2、余烬+2…，**无晶**）；「今日日箱已领，仅参战掉落」 | PASS |
| MySQL `cr_players.calamityChestDate = '2026-09-26'` 已落库 | PASS |
| 日志 `Calamity kill settled: killer=CalBotB recipients=1` | PASS |

### Run 3（1.4.8，助攻：CalAssist 砍 3 刀不补尾刀）

| 检查 | 结果 |
|------|------|
| 助攻者窗 1：A（CoreRpg 部分）+ B（晶+1）；`recipients=2` | PASS |
| 助攻者窗 2 未出手 → 无奖励 | PASS |
| 击杀者窗 2 → 仅 A | PASS |

## 3. 备注 / 后续

- MM 的 `@Trigger ~onDeath` 掉落只给尾刀者；助攻者的 A 只有 CoreRpg `kill_rewards` 两件。规格允许「先用现 MM 表」，本刀未改；若要助攻者也拿核心/碎片，可以在 `config.yml calamity.kill_rewards` 里加 `ni give` 行。
- 本次 `forceopen` 只记了 `force` 槽，今晚 20:00 / 22:00 的正式窗会照常自动开。
- 测试号 `CalBotA/B/C`、`CalWatch`、`CalAssist`、`RpgBot` 今天的日箱已标记为已领（测试数据）。`CalBotA/B/C` 测后已 deop。
- MariaDB 由 `mysqld_safe` 手动拉起（容器里没有 systemd），重启机器后需重新执行：`sudo mkdir -p /run/mysqld && sudo chown mysql:mysql /run/mysqld && sudo mysqld_safe --user=mysql &`。

## 4. 追加（2026-09-26 21:05 CST）· 助攻份额

`config.yml calamity.kill_rewards` 加 `ni give {player} mat_ember_core_fragment 1`：所有有伤害的参战者（含击杀者）各得核心碎片 ×1，比尾刀 MM 的 2 枚少；没加凝核（固定数量最少 1，会和尾刀持平）。三人实战复测（击杀者 / 砍 3 刀的助攻者 / 零伤害旁观者）全部 PASS，日箱每天限 1 次仍然有效。详见 `docs/smoke-raid-combat-20260926.md` §5。
