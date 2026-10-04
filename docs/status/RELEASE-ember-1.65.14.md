# 发布凭证 · CoreRpg 1.65.14（2026-10-04）

D174 主线签名传奇 · 第 2b 阶段 · 首领残响（Q04）。设计：`docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md`（§5 状态、§9 上线记录）。

## 线上状态（核对于 2026-10-04 19:55 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.14**（日志 `Enabling CoreRpg v1.65.14`）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `3638e68d098b95f3aae6d00a7aa44fc23ac9abd116d446a4a1aaf0b239ed34e0` |
| jar 条目数 | 2352 |
| balance_version | **37** |
| 代码提交 | `07b3ec3` |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.13-pre-16514.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 951548（19:55），`server-runtime/stop.sh` → `start.sh` |

## 内容

入口 `echo_q01`..`echo_q04`：首通 Q04 后开，单打一个已首通的 Q01–Q04 首领（T2 缩放：生命 / 伤害 q01 12 / 3.0、q02 3.5 / 2.2、q03 3.2 / 2.0、q04 2.0 / 1.8），每周前 3 次通关（四个入口共用 `p4_echo_claim`）各领那张图 2 枚徽记；进阶模式页一排 4 个图标

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 257 / 0（新增 `echoFightsOneBossOnASharedWeeklyClaim_D174`） |
| p1sim | 只发徽记（不进极品时间线）→ 不跑 W30；通关率调参 `tools/p1sim`（T2+4、躲 0.5：100 / 89 / 64 / 89%） |
| 冒烟 | FreshQ193 6 / 6：图标「本周已领 0/3」、规则行、进本绑 `EmberQ0B2`、首领现身。FreshQ199 完整一局 echo_q04：对首领施放烬斩成功 → weaken → 击杀 → 「Q04 首领徽记 +2 · 本周已领 1/3」，退出重进后 q04=2 还在 |
| 资产回归 | 没有新物品 / 资产路径 → 没跑 persist-roundtrip |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# git archive 07b3ec3 CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg && mvn -o -q package
# （把 plugins/PlaceholderAPI.jar、plugins/NeigeItems-1.21.151.jar 链到 /tmp/x/plugins/）
```
