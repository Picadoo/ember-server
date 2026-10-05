# RELEASE · CoreRpg 1.65.38（D201，2026-10-05 17:55 CST）

| 项 | 值 |
|---|---|
| 决策 | D201 · ARCH S0-5（`docs/design/AUDIT-ember-legacy-reachability-2026-10-05.md` §5.2）：竞技场对战币上限（L3 根因） |
| 内容 | ① 每人每天最多 `arena.yml match.daily_coin_matches`（5）场发对战币，超出后只记积分 / 胜负；② 认输 / 掉线方不发参与币；③ 对手认输 / 离场取胜、开打不足 `match.forfeit_min_coin_seconds`（30）秒不发胜利币；④ `/corerpg pass` 在 P1 下（非管理员）不再提示 `pass free` 和旧战令经验来源，改为指向 /ember 签到与在线奖励 |
| 不变 | 竞技场在 P1 下仍被 S0-3 路由闸关着（本次是防日后重开）；积分 / 段位 / 日奖励箱；P1 数值（balance_version 57 不动，p1sim 不动） |
| 配置 | `plugins/CoreRpg/arena.yml match.daily_coin_matches: 5`、`forfeit_min_coin_seconds: 30`（`<= 0` = 旧行为） |
| 代码 | 新 `ArenaCoinRules.java`（纯规则）+ `ArenaCoinRulesTest` 6 条；`ArenaService.java`（payMatchCoin / payCoin / coinNote，记 quitters；计数器 PlayerData `arena_coin_matches@<日期>`）；`CashService.cmdPassShow` |
| 提交 | `c818b5c`（代码 + 配置 + 测试 + 源表 D201 + AUDIT 更新） |
| 构建 | JDK 8（`tools/jdk8u504-b01`，class major 52），`mvn -o package` 单测 **320 / 0** |
| jar | `plugins/CoreRpg.jar` sha256 `be46051fc1e1b3e0d08c35a2416d2cc7e51494c311c7894fe0a859a979353de3`（由 `c818b5c` 的工作树构建；CoreRpg/ 无未提交改动） |
| 备份 | `/workspace/backup/CoreRpg-1.65.37-pre-1.65.38.jar`（sha256 `649cf125…` = 上一版线上） |
| 服务器 | PID **1754575**（was 1733729；`stop.sh` 优雅停 → `start.sh`），Enabling CoreRpg v1.65.38 17:55:28，两条 MySQL connected，Done 7.3 s，SEVERE 0 |
| 冒烟 | `docs/tests/smoke-2026-10-05-1.65.38-s0arena.md`：改动相关项全 PASS；Q01 三次都在首领前后倒下（kite 脚本问题，与本次改动无关），见冒烟文档 |
| 资产回归 | 不需要（没碰仓库 / 断线 / 重启 / 拆解 / 撤销 / 快照 / 扭蛋 / 投递路径；币是加法发奖，计数器复用现有持久化） |
| 回退 | `arena.yml` 两个键设 0 后 `/corerpg reload`；或换回备份 jar |
| 下一窗 | S0-8（手打 `trmenu open` 旧菜单）实测；N2 计数器登记 / N3 货币来源-去处-上限表（纯文档） |
