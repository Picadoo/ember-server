# 冒烟 · CoreRpg 1.65.39 / D205 首通拆分（ARCH S1 · REG §4-3）— 2026-10-05

- 版本：CoreRpg 1.65.39 / bv57，提交 `82c1b7d`；线上 jar `3dad25b4…`（与提交构建一致，源码无未提交改动）；备份 `/workspace/backup/CoreRpg-1.65.38-pre-1.65.39.jar`。
- 启动：20:01 重启后 `[CoreRpg] [storage] MySQL connected`、`[CoreGacha] [db] MySQL connected`，SEVERE 0。play PID 1826845。
- 脚本：`tools/p1map/d205-firstclear-smoke.sh`（本地，tools/ 在 .gitignore 里）+ 第二会话补查（见下）。

## 结果：通过

| 步骤 | 机器人 | 结论 |
|---|---|---|
| 真实 Q01 首通（三房间 + Boss） | FreshQ741（19:59）、FreshQ743（20:17） | 结算日志 `settle … (first clear)`，各发一次首通包；无 SEVERE |
| 重启后 / 重新登录后读状态 | FreshQ741（重启后）、FreshQ743（第二会话） | `q01_state=已首通`、`q01_cleared=yes`、`q01_fc=已领取`、`q02_open=yes` |
| 管理员桩 `runs firstclear q01` 后再打一次 Q01 | FreshQ742（20:00）、FreshQ744（20:17） | 重打结算没有 `first clear`，**不重发首通包** |
| 管理员桩 `p1 flag q04` | FreshQ744（第二会话） | `forge_t2` 从「需本人首通 Q04」变「已开放」，`q04done=1`，但 `q04_fc` 仍显示奖励内容（未领）——桩只写首通事实，不标记首通包 |
| 清掉两个桩 | FreshQ744 | `forge_t2` 回到「需本人首通 Q04」；`q01_state=已解锁`、`q01_cleared=no` |
| 收尾 | — | botd `list=[]`，全部机器人已退出 |

第二会话补查 16/16 PASS；第一次跑（routine-1945）22 PASS / 14 FAIL、复跑（FreshQ743/744）15 PASS / 14 FAIL，FAIL 全是 `papi parse` 返回 "Failed to find player"。这是已知的测试工具限制（PlaceholderAPI 在玩家第一次登录的会话里找不到他，见 `d180-signin-smoke.sh` / `d198-s0gate-smoke.sh` 注释），不是插件问题；重新登录后同样的检查全部通过。

## 漏洞审查（发版前）
- 结算 `record` 同版本幂等，重复结算 / 断线重进不会二次领首通包。
- 旧键 `p1_first_clear_<map>@v1` 视为「事实 + 已领」，写入前迁移，不会因为改版重发。
- 管理员桩只动事实键，不再删除真实 `@v1` 记录（旧版能借此重领首通包）。
- 没有新增发放来源；数值不变（bv57），p1sim 不受影响。
- 资产存取路径（仓库存取 / 分解撤销 / 快照 / 扭蛋 / 补发）未改动，不需要 persist-roundtrip。
