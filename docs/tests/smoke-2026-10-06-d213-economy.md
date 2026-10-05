# 冒烟 · CoreRpg 1.65.44（D213 EmberEconomy 登记表）· 2026-10-06 02:24–02:28 CST

- 脚本：`tools/p1map/d213-economy-smoke.sh`（由 d206 模板改版本号与机器人号）
- 机器人：FreshQ773（新号），结束后已经 botd 退出，`list=[]`
- 启动：`Enabling CoreRpg v1.65.44`；`[CoreRpg] [storage] MySQL connected`；`[CoreGacha] [db] MySQL connected`；SEVERE 0
- 首会话：在线、Q01 三房清完、首领结算 `settle … rows+11 (first clear)`、无 SEVERE —— 11 PASS。
  6 条 FAIL 全是首会话 `papi parse` 返回 "Failed to find player"（已知限制，同 D206 routine-2045）。
- 第二会话 papi 复查：`q01_state = 已首通`、`q01_cleared = yes`、`q01_fc = 已领取`、`q02_open = yes` —— 4 / 4 PASS。
- 结论：PASS。本版只加纯数据类，运行时行为不变；未动仓库 / 投递 / 分解 / 快照路径 → 不需要 persist-roundtrip。
- 构建一致：提交树重编译后与线上 jar 的 `town/**` class 逐字节相同（注释改号 D212→D213 不影响字节码）。

```
PASS CoreRpg 1.65.44
PASS CoreRpg MySQL
PASS CoreGacha MySQL
PASS no claim-error log
PASS FreshQ773 online
  FreshQ773 q01_state = Failed to find player: FreshQ773
FAIL FreshQ773 q01_state~已解锁 (got: Failed to find player: FreshQ773)
  FreshQ773 q01_fc = Failed to find player: FreshQ773
PASS FreshQ773 q01_fc!~已领取
  FreshQ773 q02_open = Failed to find player: FreshQ773
FAIL FreshQ773 q02_open~no (got: Failed to find player: FreshQ773)
PASS FreshQ773 room1
PASS FreshQ773 room2
PASS FreshQ773 room3
PASS FreshQ773 settled-first-clear
  FreshQ773 q01_state = Failed to find player: FreshQ773
FAIL FreshQ773 q01_state~已首通 (got: Failed to find player: FreshQ773)
  FreshQ773 q01_cleared = Failed to find player: FreshQ773
FAIL FreshQ773 q01_cleared~yes (got: Failed to find player: FreshQ773)
  FreshQ773 q01_fc = Failed to find player: FreshQ773
FAIL FreshQ773 q01_fc~已领取 (got: Failed to find player: FreshQ773)
  FreshQ773 q02_open = Failed to find player: FreshQ773
FAIL FreshQ773 q02_open~yes (got: Failed to find player: FreshQ773)
PASS FreshQ773 no-SEVERE
RESULT PASS=11 FAIL=6
q01_state = 已首通
q01_cleared = yes
q01_fc = 已领取
q02_open = yes
```
