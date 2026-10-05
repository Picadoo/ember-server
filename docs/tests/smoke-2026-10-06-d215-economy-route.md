# 冒烟 · CoreRpg 1.65.46（D215 EmberEconomy S2-2 路由）· 2026-10-06 02:43–02:45 CST

- 脚本：`tools/p1map/d215-economy-route-smoke.sh`（d213 模板，版本 / FreshQ777）
- 机器人：FreshQ777（新号），结束后 botd 退出，`list=[]`
- 启动：`Enabling CoreRpg v1.65.46`；`[CoreRpg] [storage] MySQL connected`；`[CoreGacha] [db] MySQL connected`；SEVERE 0
- 首会话：在线、Q01 三房清完、首领结算 `settle … rows+11 (first clear)`、无 SEVERE —— 11 PASS。
  6 条 FAIL 全是首会话 `papi parse` 返回 "Failed to find player"（已知限制，同 D213 / D206）。
- 第二会话 papi 复查：`q01_state = 已首通`、`q01_cleared = yes`、`q01_fc = 已领取`、`q02_open = yes` —— 4 / 4 PASS。
- 结论：PASS。S01–S03 settle 与 C14 / grantCoin 路由后首通包与解锁仍正常；未动仓库 / 投递 / 分解 / 快照 → 不需要 persist-roundtrip。

```
PASS CoreRpg 1.65.46
PASS CoreRpg MySQL
PASS CoreGacha MySQL
PASS no claim-error log
PASS FreshQ777 online
  FreshQ777 q01_state = Failed to find player: FreshQ777
FAIL FreshQ777 q01_state~已解锁 (got: Failed to find player: FreshQ777)
  FreshQ777 q01_fc = Failed to find player: FreshQ777
PASS FreshQ777 q01_fc!~已领取
  FreshQ777 q02_open = Failed to find player: FreshQ777
FAIL FreshQ777 q02_open~no (got: Failed to find player: FreshQ777)
PASS FreshQ777 room1
PASS FreshQ777 room2
PASS FreshQ777 room3
PASS FreshQ777 settled-first-clear
  FreshQ777 q01_state = Failed to find player: FreshQ777
FAIL FreshQ777 q01_state~已首通 (got: Failed to find player: FreshQ777)
  FreshQ777 q01_cleared = Failed to find player: FreshQ777
FAIL FreshQ777 q01_cleared~yes (got: Failed to find player: FreshQ777)
  FreshQ777 q01_fc = Failed to find player: FreshQ777
FAIL FreshQ777 q01_fc~已领取 (got: Failed to find player: FreshQ777)
  FreshQ777 q02_open = Failed to find player: FreshQ777
FAIL FreshQ777 q02_open~yes (got: Failed to find player: FreshQ777)
PASS FreshQ777 no-SEVERE
RESULT PASS=11 FAIL=6
q01_state = 已首通
q01_cleared = yes
q01_fc = 已领取
q02_open = yes
```
