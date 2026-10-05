# 发布凭证 · CoreRpg 1.65.35（2026-10-05）

D198 架构加固 S0-1 + S0-2（不是内容包）：P1 模式开着时，旧（P1 之前的）副本对普通玩家关门——`/dp start EmberDaily` 等 12 个旧本被 DungeonPlus 拒绝（不管谁开），`/corerpg enter <日常 / 周本 / 深渊 / 团本 / 精英>` 和 `/corerpg elite` 回「P1 模式下旧副本已关闭」。封住审计 `docs/design/AUDIT-ember-legacy-reachability-2026-10-05.md` 的 L1 / L2（新号可无限刷旧日常、周本等每周一次不扣体力）。OP / `corerpg.admin` / 控制台放行，P1 关掉时行为不变。P1 数值一点不动（只关 p1sim 从未建模的旧来源），所以不跑 p1sim、不 bump balance_version。

## 线上状态（部署后核对于 2026-10-05 15:49 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.35**（`Enabling CoreRpg v1.65.35` 15:49:00）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `91e6069b28a007afff13dc7281ce4c9cb7bfdc1fd0271857af1098bb3e93f9d9` |
| balance_version | **57**（不变：balance_version 只跟 P1 参数变化走，本包没有）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.34-pre-1.65.35.jar`（sha256 `917af486…` = 1.65.34 线上）|
| 服务器 PID | **1686032**（was 1527320；`server-runtime/stop.sh` 优雅停 → `start.sh`）|
| MySQL | `[CoreRpg] [storage] MySQL connected` 15:49:00 · `[CoreGacha] [db] MySQL connected` 15:49:03 |
| SEVERE | 0 |
| 部署文件 | 只有 `plugins/CoreRpg.jar`；配置 / 菜单 / DP option.yml 都没动 |
| 源码对账 | jar 由提交 `d4c873c` 的干净 worktree（/workspace/d198-wt）构建；`git archive d4c873c` 重建的 jar 解包后与线上 jar **逐文件一致**（除 META-INF/maven）；部署后 /workspace/minecraft 快进到同一提交 |

## 改动

- 新 `J/LegacyGate.java`（纯函数）：`gateClosed(p1Active, gateId)`（旧 gate id = daily / weekly / abyss / raid / elite）、`guildBossPassClosed(p1Active)`、`refuseLegacyEnter(p1Active, kindIsP1, privileged)`、`CLOSED_MSG`。
- `J/CoreRpgExpansion.java`：`%corerpg_gate_<旧 id>%` 与 `%corerpg_guildboss_pass%` 在 `EmberMode.active()` 时回 `no`。DP 对全队每人求值 → `/dp start` 被拒；OP 由各 option.yml 已有的 `||'%player_is_op%'=='yes'` 放行。静态核对：`rg gate_ plugins/DungeonPlus/dungeon/*/option.yml` 只有 EmberDaily×7 / Weekly / Abyss / Raid / EliteWeekly 读 `gate_*`、EmberGuildBoss 读 `guildboss_pass`；EmberQ01–Q07 / Q0R1–R3 / Q0B1–B2 / Q0F1 一个都不读。
- `J/TicketEntryService.java` `tryEnter`：P1 分支之后，P1 开着 + 非 OP 且无 `corerpg.admin` → 拒绝并返回。`/corerpg elite [start]` 经 `EliteService.cmdStart → tryEnter(ELITE)` 一并覆盖。控制台不经过此路径（`cmdEnter` / `elite start` 只给玩家），DP / MM 发奖脚本照常。

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package`（JDK 8）| **306 / 0**（+6 `LegacyGateTest`：P1 开 / 关 × 玩家 / 管理员；11 个旧 Kind 全覆盖、P1 Kind 一个不碰）|
| p1sim | 不需要（不改 P1 数值；`tools/p1sim` 未动）|
| 冒烟 | `docs/tests/smoke-2026-10-05-1.65.35-s0gate.md`：FreshQ717–720 **22 PASS / 0 FAIL**（第 1 轮 FreshQ713–716 的 2 条 FAIL 是脚本判定问题，已修）|
| persist-roundtrip | 不需要（无资产路径变化）|

## 下一步

S0-3：`/corerpg` 路由级默认拒绝白名单（AUDIT §5.2，封 L3 竞技场币、L4 / L5 / L10 日领、L7 灾厄、L8 战令、L9 拆解、L11 盟约的入口）。
