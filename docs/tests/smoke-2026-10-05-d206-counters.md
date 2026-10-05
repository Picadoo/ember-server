# 冒烟 · CoreRpg 1.65.40 / D206 计数器注册表（ARCH S1-1 · REG §4-1/§4-2）— 2026-10-05

- 版本：CoreRpg 1.65.40 / bv57，提交 `c700cfe`；线上 jar `d0f12299…`（与 `CoreRpg/target/CoreRpg.jar` 一致，源码无未提交改动）；备份 `/workspace/backup/CoreRpg-1.65.39-pre-1.65.40.jar`。
- 部署：routine-2045 于 20:51 部署并重启；该轮在 21:02:57 正常关停了 login 和 play 两台后端后中断，服务器一直处于停机状态（代理仍在）。routine-2127 于 21:28 重启两台后端：`[CoreRpg] [storage] MySQL connected`、`[CoreGacha] [db] MySQL connected`，SEVERE 0。play PID 1869885，login PID 1869851。
- 脚本：`tools/p1map/d206-counters-smoke.sh`（本地，tools/ 在 .gitignore 里），bot 号换成 FreshQ747；先让 FreshQ747 登录一次再退出，绕开 PlaceholderAPI 首会话找不到玩家的已知限制。

## 结果：通过（17 PASS / 0 FAIL）

| 检查 | 结论 |
|---|---|
| 启动 | `Enabling CoreRpg v1.65.40`，两条 MySQL 连接行，日志里没有连战领取键冲突报错 |
| 首通前 | `q01_state=已解锁`，`q01_fc` 显示奖励内容（未领），`q02_open=no` |
| 真实 Q01（三房间 + Boss） | 结算 `settle … rows+11 (first clear)`，首通包只发一次 |
| 首通后 | `q01_state=已首通`、`q01_cleared=yes`、`q01_fc=已领取`、`q02_open=yes` |
| 异常 | 本次日志段内无 SEVERE / Exception |
| 收尾 | botd `list=[]` |

routine-2045 的首跑（FreshQ745/746）有 8 个 FAIL，全是 `papi parse` 返回 "Failed to find player"（首会话已知限制）；FreshQ745 第二会话已补查通过。

## 漏洞审查
- 只新增一张登记表和单测，运行时不读写任何新键，数值不变（bv57）。
- `EmberRunMaps` 新校验只会拒绝加载重名的连战领取键配置，不会放出新奖励。
- 资产存取路径（仓库存取 / 断线 / 重启 / 分解撤销 / 快照 / 扭蛋 / 补发）未改动，不需要 persist-roundtrip。
