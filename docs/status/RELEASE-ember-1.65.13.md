# 发布凭证 · CoreRpg 1.65.13（2026-10-04）

D174 主线签名传奇 · 第 2b 阶段 · 连战·前哨（Q05）。设计：`docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md`（§5 状态、§9 上线记录）。

## 线上状态（核对于 2026-10-04 19:52 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.13**（日志 `Enabling CoreRpg v1.65.13`）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `771b4c1fa02a00542d2a5305b7a3b74749bdfa16da3ed7c68d467bd3f87dc708` |
| jar 条目数 | 2352 |
| balance_version | **36** |
| 代码提交 | `fbd4176`（rebase 前是 `c2d6565`） |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.12-pre-16513.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 949720（19:52），`server-runtime/stop.sh` → `start.sh` |

## 内容

连战引擎通用化（MapDef rushMode / rushLabel / rushClaim / rushWeekly / rushMarkTier / rushSig）；入口 `outpost`：首通 Q05 后开，Q01→Q02→Q03 首领在同一个大厅连打（首领生命 ×3、伤害 ×2.2），每周首通领奖：三张图各 2 枚徽记 + 1 个 T2 印记（自己的 `p4_outpost_claim`），之后练习；新 DP 地牢 `EmberQ0B2`（首领厅，按地牢判通行）；进阶模式页图标；`qXXdone` 占位符

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 256 / 0（新增 `outpostRunsTheT1ChainOnItsOwnClaim_D174`） |
| p1sim | W30 见设计 §8.5（`P1_OUTPOST` 上界） |
| 冒烟 | FreshQ192 7 / 7：Q05 前被拒、图标「本周已领 0/1」、规则行、进本绑到 `dungeon_EmberQ0B2_…`、首领现身。FreshQ200 完整一局（管理员 weaken 每个首领、机器人补刀）：3 段都过、结算「T2 锻造印记 1、Q01 / Q02 / Q03 首领徽记各 2」、1/1。回归 FreshQ198：原余烬连战（Q07）照旧绑 `EmberQ0B1`、计次数 |
| 资产回归 | 新 DP 地牢目录 + config 一行；没有物品资产路径变化 → 没跑 persist-roundtrip |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# git archive fbd4176 CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg && mvn -o -q package
# （把 plugins/PlaceholderAPI.jar、plugins/NeigeItems-1.21.151.jar 链到 /tmp/x/plugins/）
```
