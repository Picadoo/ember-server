# 冒烟 · CoreRpg 1.65.41 / D207 周期领取计数器的时钟回拨防护（ARCH S1-5 · REG-counter-registry §4-5）— 2026-10-05

- 版本：CoreRpg 1.65.41 / bv57；线上 jar `818ca557…`（= `CoreRpg/target/CoreRpg.jar`，JDK8 构建，class major 52，单测 343/0，新增 `PlayerDataClockGuardTest` 7 条）；备份 `/workspace/backup/CoreRpg-1.65.40-pre-1.65.41.jar`。
- 部署：routine-2155 于 22:01 只重启 play 后端（login 未动）：`Enabling CoreRpg v1.65.41`、`[CoreRpg] [storage] MySQL connected`、`[CoreGacha] [db] MySQL connected`，SEVERE 0。play PID 1887840。
- 脚本：`tools/p1map/d207-clockguard-smoke.sh`（本地，tools/ 在 .gitignore 里），bot FreshQ748。

## 结果：通过（14 PASS + 第二会话 6/6；8 个 FAIL 全是 PlaceholderAPI 首会话 "Failed to find player" 的已知限制）

| 检查 | 结论 |
|---|---|
| 启动 | 版本行 + 两条 MySQL 连接行，无连战领取键冲突报错 |
| 签到（月位图领取族 `p1_sign_mask`，受防护） | 第一次 `/corerpg p1 sign claim` 签到成功（余烬币 20、经验 5），第二次「今天已经签到了」；没有出现「日期异常」误报 |
| 真实 Q01（三房间 + Boss） | `settle … rows+11 (first clear)` |
| 第二会话补查 | `sign_today=1`、`q01_state=已首通`、`q01_cleared=yes`、`q01_fc=已领取`、`q02_open=yes`；重登后再签仍被拒 |
| 异常 / 收尾 | 无 SEVERE / Exception；botd `list=[]` |

正常时钟下防护不会触发（只有同名键已存在**更晚**周期时才生效），所以线上冒烟只能证明「正常路径不变」；回拨行为由单测覆盖（日 / P 周 / 旧周 / 月 / 位图 / 不受防护的族 / 同周期与更晚周期写入）。

## 漏洞审查
- 防护只作用于**周期性领取类**键族（`EmberCounters` 里类别 CLAIM 且周期为 日 / P 周 / 旧周 / 月，共 19 族）；资产（如 `p1_afk_acc_`）、进度（`p1_on_min`、`p3_goal_`）、统计、`@all`、内容版本键不受影响，行为与 1.65.40 相同。
- 回拨时旧周期读作 `0x3FFFFFFF`（低 30 位全 1）：`>= 上限`、位图「第 i 档已领」、`已用 + n` 判断都会视为「已领满」且不会整数溢出；旧周期写入被拒，更晚周期的键保留，时钟恢复后照常换期。所有对这些族做减法的地方都有 `Math.max(0, …)`，只会在异常时钟下把「本周 x/3」显示成很大的数（只是显示）。
- 挂机结算：`p1_afk_kill` 回拨时饱和 → `grantable` 为 0，不再记账；结算金额只看资产累加器，旧日为 0 → 不发。
- 不改数值（bv57），不改资产存取路径（仓库 / 断线 / 重启 / 分解撤销 / 快照 / 扭蛋 / 补发）→ 不需要 persist-roundtrip；p1sim 不读这段逻辑。
