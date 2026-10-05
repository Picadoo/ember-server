# RELEASE · CoreRpg 1.65.46（D215，2026-10-06）

| 项 | 值 |
|---|---|
| 决策 | D215 · ARCH S2-2：把高流量发放 / 消耗改走 EmberEconomy（REG §6 第 1–2 条第一步） |
| 内容 | EmberEconomy.amount / grantCoin / spendCoin / sourceForGrantKey；EmberRunRules.settle S01–S03 数量读登记表；通关币发放（base / treasure / elite / bounty / fc / honor / vb）走 grantCoin；C14 回复药价与扣币走登记表 |
| 不变 | bv57；所有数量（金样仍钉 BASE_* / yml）；未改材料 / 印记 / 经验发放入口；p1sim 未动 |
| 测试 | unit **376 / 0**（+3：settle 用登记表、C14 spend、grantCoin 路由） |
| 配置 | 无改动（价仍与 shop.heal_potion.price 金样一致） |
| jar sha256 | `297124a2e2c616eaf821bd2ad39dae393037c5f340011086cf918c25d833d7d2`（JDK 8u504，class 52） |
| 备份 | `/workspace/backup/CoreRpg-1.65.45-pre-1.65.46.jar` |
| 服务器 | 02:43 重启；Enabling CoreRpg v1.65.46；CoreRpg / CoreGacha MySQL connected；SEVERE 0；play PID 2058743 |
| 冒烟 | FreshQ777 PASS（docs/tests/smoke-2026-10-06-d215-economy-route.md）；首会话 papi 已知 quirk；二会话 papi 4/4；资产路径未改 → 不跑 persist-roundtrip |
| 下一步 | S2-3：签到 / 在线金额改读登记表；材料 / 印记 / XP grant；未登记发放扫描；徽记账户进 p1sim（REG §5 缺口 1–2） |

