# 冒烟：灾厄/盟 Boss DP 门控 + MM 几率掉落修复（2026-09-26）

服务器：Ember 游戏服 :25565（Paper 1.12.2, CoreRpg 1.4.8）。时间均为 CST（UTC+8）。
脚本：`mineflayer-tests/gate-drops-smoke.js`（门控 + MM 掉落），`mineflayer-tests/dungeon-clear-smoke.js`（合法通关）。
日志（不入库）：`mineflayer-tests/logs/gate-drops-20260926.log`、`logs/calamity-legit-20260926.log`。

## 1. 门控

### EmberCalamity：已门控，并已修 `%player_name%`
- 背景：正式灾厄 = CoreRpg CalamityService 在 `ember_event` 的公共窗口（`mm spawn`），不走 DP。DP 的 EmberCalamity
  只是 OP 调试实例（ember_calamity 菜单 T 位 `dp start EmberCalamity`）。
- 服务器没有权限插件，DP 也没有按副本的 start 权限。所以用 DP 自带的 dungeon-start 条件 + PAPI：
  ```
  $js-condition{text='%player_is_op%'=='yes';message=§c灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：/ember → 灾厄} @system
  ```
  DP 会逐个队员判定，只要队里有一个非 OP，整队都会被拒。
- 之后在 `dungeon-reward-script` 里把 5 行 `<player.name>` 改成 `%player_name%`（4 条 ni give + mvtp ember_hub）。

### EmberGuildBoss：CoreRpg 1.4.9 代码门控 + 已修 `%player_name%`（第四刀，21:49–21:57 CST）
- 旧问题：`/corerpg guild boss` 以**玩家身份** dispatch `dp start EmberGuildBoss`，所以手打同一条命令就能绕过贡献门控，纯配置堵不住。
- **CoreRpg 1.4.9（GuildService）改动：**
  1. 贡献/周次检查通过后，通过反射（不加编译依赖）读取 DP 队伍：只有队长能发起，队伍已在地牢中则拒绝。
  2. 扣贡献后，给**全队成员**发一次性通行（内存，5 s 过期），PAPI 占位符为 `%corerpg_guildboss_pass%`（yes/no，在 CoreRpgExpansion 中实现）。
  3. 由**控制台**执行 `dp start-console <队长> EmberGuildBoss`（`guild.yml boss.start_command: "dp start-console {player} EmberGuildBoss"`；模板里不含 `{player}` 时退回旧的以玩家身份执行）。
  4. 开本结果：立即检查一次，2 s 后再复核队伍是否已进地牢，确认后清除通行。没进本就退还贡献和周次。实测 DP 是在下一 tick 才开本，由 2 s 复核确认。
- **为什么不能只靠控制台：** `start-console` 和 `start` 走同一个 `startDungeon`，开本条件仍然会逐个队员判定，
  所以单纯 OP 条件会把合法的非 OP 队伍也拒掉。因此改成 OP **或** 一次性通行：
  ```
  $js-condition{text='%corerpg_guildboss_pass%'=='yes'||'%player_is_op%'=='yes';message=§c盟 Boss 需由队长执行 /corerpg guild boss（消耗盟约贡献）开启} @system
  ```
- 之后把奖励段 6 行 `<player.name>` 改成 `%player_name%`（5 条 ni give + mvtp ember_hub）。
- 构建：先确认 1.4.8 源码重新构建后与线上 1.4.8 jar **逐字节一致**（750 个 class 加资源，`diff -r` 无差异），再改代码。
  1.4.8 jar 已备份到 `backups/CoreRpg-1.4.8.jar`（sha256 a935780b…，不入库）。1.4.9 jar 的 sha256 为 b5106adb…。

## 2. MM 几率掉落

`~onDeath >0 X` 里的 `>0` 是血量条件，怪死时血量不大于 0，所以这 47 行从来没有触发过。现已全部改成纯几率 `~onDeath X`。
改之前按 `docs/ember-gear-drop-t0-t3.md` §3、`docs/ember-abyss-calamity.md` §1.4/附录 A 逐行审计，下调了会导致通胀的值：
孔石 gem 原来 0.15～0.7，在普通/深渊杂兵身上属于"几乎必掉"，设计里孔石是稀有产出，所以降到 0.02～0.04（Watcher 精英 0.17/0.18）；
T2 在深渊杂兵上降到 0.01～0.03；灾厄 Boss 的 gem 0.5～0.7 → 0.04；核心碎片/护符/保护卷在杂兵上降到 0.05～0.08；
周本/团本杂兵的 T1 降到 0.01～0.02。团本 Boss 的 T2 0.15/0.12、T3 0.08/0.06，以及 Crypt 的 T0，都与设计表一致，保留。

| # | 文件 | 怪 | 物品 | 数量 | 旧（`>0 X`，从未生效） | 新（纯几率） | 处理 |
|---|---|---|---|---|---|---|---|
| 1 | EmberAbyss.yml | EmberAbyssZombie | gem_ember_sharp | 1 | 0.35 | 0.03 | **下调** |
| 2 | EmberAbyss.yml | EmberAbyssZombie | gem_ember_steady | 1 | 0.15 | 0.02 | **下调** |
| 3 | EmberAbyss.yml | EmberAbyssZombie | mat_ember_shard | 1 | 0.4 | 0.4 | 按设计保留 |
| 4 | EmberAbyss.yml | EmberAbyssSkeleton | gem_ember_gale | 1 | 0.35 | 0.03 | **下调** |
| 5 | EmberAbyss.yml | EmberAbyssSkeleton | gem_ember_drain | 1 | 0.15 | 0.02 | **下调** |
| 6 | EmberAbyss.yml | EmberAbyssSkeleton | mat_ember_bone_dust | 1 | 0.4 | 0.4 | 按设计保留 |
| 7 | EmberAbyss.yml | EmberAbyssMix | gem_ember_sharp | 1 | 0.3 | 0.03 | **下调** |
| 8 | EmberAbyss.yml | EmberAbyssMix | gem_ember_gale | 1 | 0.3 | 0.02 | **下调** |
| 9 | EmberAbyss.yml | EmberAbyssMix | mat_ember_core_fragment | 1 | 0.2 | 0.08 | **下调** |
| 10 | EmberAbyss.yml | EmberAbyssBrute | gem_ember_sharp | 1 | 0.45 | 0.03 | **下调** |
| 11 | EmberAbyss.yml | EmberAbyssBrute | gem_ember_steady | 1 | 0.35 | 0.02 | **下调** |
| 12 | EmberAbyss.yml | EmberAbyssBrute | mat_ember_protect_scroll | 1 | 0.12 | 0.05 | **下调** |
| 13 | EmberAbyss.yml | EmberAbyssBrute | mat_ember_core_fragment | 1 | 0.25 | 0.08 | **下调** |
| 14 | EmberAbyss.yml | EmberAbyssBrute | gear_ember_t2_blade | 1 | 0.04 | 0.01 | **下调** |
| 15 | EmberAbyss.yml | EmberAbyssWatcher | gem_ember_drain | 1 | 0.5 | 0.18 | **下调** |
| 16 | EmberAbyss.yml | EmberAbyssWatcher | gem_ember_gale | 1 | 0.5 | 0.17 | **下调** |
| 17 | EmberAbyss.yml | EmberAbyssWatcher | mat_ember_stable_charm | 1 | 0.2 | 0.05 | **下调** |
| 18 | EmberAbyss.yml | EmberAbyssWatcher | gear_ember_t2_blade | 1 | 0.08 | 0.03 | **下调** |
| 19 | EmberAbyss.yml | EmberAbyssWatcher | gear_ember_t2_talisman | 1 | 0.05 | 0.02 | **下调** |
| 20 | EmberCalamity.yml | EmberCalamityBoss | gem_ember_steady | 1 | 0.7 | 0.04 | **下调** |
| 21 | EmberCalamity.yml | EmberCalamityBoss | gem_ember_drain | 1 | 0.5 | 0.04 | **下调** |
| 22 | EmberCalamity.yml | EmberCalamityBoss | gem_ember_gale | 1 | 0.5 | 0.04 | **下调** |
| 23 | EmberCalamity.yml | EmberCalamityBoss | mat_ember_stable_charm | 1 | 0.25 | 0.05 | **下调** |
| 24 | EmberCalamity.yml | EmberCalamityBoss | cosmetic_calamity_shard | 1 | 0.35 | 0.2 | **下调** |
| 25 | EmberCrypt.yml | EmberCryptZombie | gear_ember_blade | 1 | 0.05 | 0.05 | 按设计保留 |
| 26 | EmberCrypt.yml | EmberCryptSkeleton | gear_ember_charm | 1 | 0.05 | 0.05 | 按设计保留 |
| 27 | EmberCrypt.yml | EmberCryptBrute | gear_ember_blade | 1 | 0.18 | 0.18 | 按设计保留 |
| 28 | EmberCrypt.yml | EmberCryptBrute | gear_ember_charm | 1 | 0.12 | 0.12 | 按设计保留 |
| 29 | EmberDaily.yml | EmberDailyBrute | gear_ember_blade | 1 | 0.12 | 0.12 | 按设计保留 |
| 30 | EmberDaily.yml | EmberDailyBrute | gear_ember_charm | 1 | 0.08 | 0.08 | 按设计保留 |
| 31 | EmberDaily.yml | EmberDailyBrute | gear_ember_t1_blade | 1 | 0.03 | 0.03 | 按设计保留 |
| 32 | EmberRaid.yml | EmberRaidFootman | mat_ember_shard | 1 | 0.5 | 0.5 | 按设计保留 |
| 33 | EmberRaid.yml | EmberRaidFootman | gear_ember_t1_blade | 1 | 0.05 | 0.02 | **下调** |
| 34 | EmberRaid.yml | EmberRaidArcher | mat_ember_bone_dust | 1 | 0.5 | 0.5 | 按设计保留 |
| 35 | EmberRaid.yml | EmberRaidArcher | gear_ember_t1_talisman | 1 | 0.04 | 0.02 | **下调** |
| 36 | EmberRaid.yml | EmberRaidElite | gear_ember_t2_blade | 1 | 0.02 | 0.02 | 按设计保留 |
| 37 | EmberRaid.yml | EmberRaidElite | gear_ember_t1_blade | 1 | 0.05 | 0.05 | 按设计保留 |
| 38 | EmberRaid.yml | EmberRaidBoss | gear_ember_t2_blade | 1 | 0.15 | 0.15 | 按设计保留 |
| 39 | EmberRaid.yml | EmberRaidBoss | gear_ember_t2_talisman | 1 | 0.12 | 0.12 | 按设计保留 |
| 40 | EmberRaid.yml | EmberRaidBoss | gear_ember_t3_blade | 1 | 0.08 | 0.08 | 按设计保留 |
| 41 | EmberRaid.yml | EmberRaidBoss | gear_ember_t3_talisman | 1 | 0.06 | 0.06 | 按设计保留 |
| 42 | EmberWeekly.yml | EmberWeeklyZombie | gear_ember_t1_blade | 1 | 0.04 | 0.01 | **下调** |
| 43 | EmberWeekly.yml | EmberWeeklySkeleton | gear_ember_t1_talisman | 1 | 0.04 | 0.01 | **下调** |
| 44 | EmberWeekly.yml | EmberWeeklyBruteA | gear_ember_t1_blade | 1 | 0.18 | 0.18 | 按设计保留 |
| 45 | EmberWeekly.yml | EmberWeeklyBruteA | gear_ember_t2_blade | 1 | 0.03 | 0.03 | 按设计保留 |
| 46 | EmberWeekly.yml | EmberWeeklyBruteB | gear_ember_t1_talisman | 1 | 0.12 | 0.12 | 按设计保留 |
| 47 | EmberWeekly.yml | EmberWeeklyBruteB | gear_ember_blade | 1 | 0.2 | 0.2 | 按设计保留 |

下调 26 行，保留 21 行。没有任何 T3 超过 0.08，也没有任何高档装备（T2/T3）超过 0.15。

## 3. 测试结果（2026-09-26 21:40–21:46 CST）

| 检查 | 结果 |
|---|---|
| 非 OP 两人队（GtA926+GtB926）`/dp start EmberCalamity` | **拒绝** ✅ 提示「灾厄 DP 实例仅供 OP 调试 · 正式灾厄请走公共窗口：/ember → 灾厄」 |
| 非 OP `/dp start EmberGuildBoss`（第三刀，1.4.8） | 开本成功 ❌ → 第四刀已用 CoreRpg 1.4.9 堵上，见下方盟 Boss 测试 |
| OP（CalA926，临时 op）从 `world` `/dp start EmberCalamity` → 实战击杀 Boss | 进本 ✅，20 s 通关 ✅，回 ember_hub ✅ |
| 通关奖励只发一次 | `gear_ember_t3_talisman` **+1**（恰好 1 次）✅；DP 箱 core_fragment 2 / crystal 1 / calamity_ember 2 各发 1 次；另外 Boss MM 掉落 + CoreRpg kill_rewards 照常（总计 core_fragment +7、crystal +2、calamity_ember +6、cosmetic +1、gem_sharp +1、core_compact +1） |
| 正式灾厄公共窗口（CalamityService） | 不经 DP，本次未改动（上一轮 smoke-calamity-combat 已验证） |
| MM 掉落：在非 OP 机器人旁刷 EmberRaidFootman×8 + EmberAbyssZombie×8 并击杀 | `mat_ember_shard` **+7**（期望约 8×0.5+8×0.4≈7.2），修前是 0 ✅ |

### 盟 Boss 测试（`mineflayer-tests/guildboss-gate-smoke.js`，21:53–21:55 CST；两名非 OP：GbL926 队长 + 盟员，GbM926 仅 DP 队友）

| 检查 | 结果 |
|---|---|
| 非 OP 两人队手打 `/dp start EmberGuildBoss` | **拒绝** ✅「盟 Boss 需由队长执行 /corerpg guild boss（消耗盟约贡献）开启」 |
| 贡献 19 执行 `/corerpg guild boss` | **拒绝** ✅「贡献不足：需要 20，当前 19」，贡献仍为 19，未开本 |
| 贡献 20 执行 `/corerpg guild boss` | 扣贡献 20→0 ✅，控制台开本，两人都进本 ✅（队友靠通行通过逐人判定），41 s 通关 ✅ |
| 奖励只发一次 | 每人「通关奖励已发放」1 次，`ni give 8 余烬碎片` / `4 骨尘` 各 1 次 ✅（其余增量来自 MM 掉落和灾厄结算） |
| 回 ember_hub | 两人都回来了 ✅（开始时都在 `world`） |
| 通关后再手打 `/dp start EmberGuildBoss` | **拒绝** ✅（通行只能用一次，已清除） |
| 退款分支（22:05–22:07 CST，`guildboss-refund-smoke.js`，非 OP GbR926） | 临时给 DP 加一条永假条件 → `/corerpg guild boss` 扣 20 后提示「启动失败（已退还贡献与次数）」，贡献回到 20 ✅；撤掉条件并 reload 后重试：显示「本周 1/1」且开本成功，贡献 20→0 ✅（说明周次也退了）；再执行第三次 → 「本周次数已用尽（1/1）」 ✅ |

## 4. 服务器状态
- 线上 CoreRpg 1.4.9。最后一次停服后写入 `ops.json = []` 再启动，21:56 CST 启动完成，`ops.json` 为 `[]`。
- 测试用的临时 op（RpgBot、CalA926）已随 ops.json 清空全部撤销。
