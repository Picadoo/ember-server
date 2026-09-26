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

### EmberGuildBoss：纯配置做不到，**奖励修复未应用**
- 合法路径 `/corerpg guild boss`（GuildService）扣贡献，然后 **以玩家身份** `Bukkit.dispatchCommand(p, "dp start EmberGuildBoss")`
  （`guild.yml boss.start_command` 只能改命令文本，执行者仍然是玩家本人）。
- 因此合法路径能过的 DP 条件，玩家手打 `/dp start EmberGuildBoss` 同样能过：同一个人、同一条命令，
  CoreRpg 也不会暴露"已付费未开本"这样的状态或占位符。实测非 OP 直开成功（见下）。
- 所以按要求停手：`<player.name>` 保持不动（奖励仍然不发，也就刷不出来），只更新了 option.yml 的注释。
- 代码方案（需要改 CoreRpg）：
  1. GuildService 扣完贡献后改由 console 执行 DP 的控制台开本命令（`dp start-console`/等价命令），再给 DP 加 OP 门（同 Calamity）；或者
  2. GuildService 扣完贡献后先通过 NiBridge 发一张一次性 `ticket_ember_guildboss`，DP 再加票据条件并消耗它。

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
| 非 OP `/dp start EmberGuildBoss` | **开本成功** ❌（门控缺口已确认，需代码，奖励仍不发） |
| OP（CalA926，临时 op）从 `world` `/dp start EmberCalamity` → 实战击杀 Boss | 进本 ✅，20 s 通关 ✅，回 ember_hub ✅ |
| 通关奖励只发一次 | `gear_ember_t3_talisman` **+1**（恰好 1 次）✅；DP 箱 core_fragment 2 / crystal 1 / calamity_ember 2 各发 1 次；另外 Boss MM 掉落 + CoreRpg kill_rewards 照常（总计 core_fragment +7、crystal +2、calamity_ember +6、cosmetic +1、gem_sharp +1、core_compact +1） |
| 正式灾厄公共窗口（CalamityService） | 不经 DP，本次未改动（上一轮 smoke-calamity-combat 已验证） |
| MM 掉落：在非 OP 机器人旁刷 EmberRaidFootman×8 + EmberAbyssZombie×8 并击杀 | `mat_ember_shard` **+7**（期望约 8×0.5+8×0.4≈7.2），修前是 0 ✅ |

## 4. 服务器状态
- 已 `/dp reload`；最后一次停服后写入 `ops.json = []` 再启动，21:46 CST 启动完成，`ops.json` 为 `[]`。
- 测试用的临时 op（RpgBot、CalA926）已随 ops.json 清空全部撤销。
