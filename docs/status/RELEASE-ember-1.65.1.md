# 发布凭证 · CoreRpg 1.65.1（2026-10-04，review Q02）

可复查的「此刻线上是什么」清单。对应 GPT 综合审阅 Q02：源码 SHA、插件版本、JAR 哈希、规则版本与哈希、测试报告、部署时间、回滚包。

## 线上状态（核对于 2026-10-04 12:13 Asia/Shanghai）

| 项 | 值 |
|---|---|
| 源码 tip（main） | `70eaf12`（p1sim B01 文档；CoreRpg 代码 tip = `64a0593` D161/D162） |
| CoreRpg 版本 | **1.65.1**（`plugin.yml`） |
| CoreRpg JAR sha256 | `f415f897386404f2c649f8c435daccdb0b157875a296d069319a71c74e482504` |
| CoreRpg class major | **52**（JDK 8） |
| CoreGacha 版本 | **1.0.1** |
| CoreGacha JAR sha256 | `4526359f4e961733287ca8bc0674b03c4c844d58fde2daa615f83180b705b2c6` |
| 部署时间 | CoreRpg 1.65.1 于 **11:31 CST** 上线（之后无再换 jar） |
| Paper | 1.12.2（`server-runtime/paper-custom.jar`） |
| `balance_version` | **29**（`ember-v1-runs.yml`） |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.1.jar`（sha256 与线上一致） |
| 故障注入 | **关**：进程环境无 `CORERPG_TEST_FAULTS`；`/corerpg p1 fault` 拒绝执行 |
| MySQL | 启动日志：`[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected`（11:44 最近一次重启） |

## 规则文件哈希（sha256）

| 文件 | 源码 / JAR 内嵌 | 线上 `plugins/CoreRpg/` |
|---|---|---|
| `ember-v1-runs.yml` | `7db1ed20d49d797e74a4062bde9aeb15c9dc7ef9960998a25a4d528ec71f35f8` | 同左 |
| `ember-v1-growth.yml` | `5cd4b6c95a0ed7c07da062b957501dafa7dd69e44ac33fa7f0dcdcb2a562e79d` | 同左 |
| `ember-v1-festival.yml` | `67faeeab1f3a9f22368ac3e49c2c62ba9b6d9732bef05f79d19f7df8f9f9ce68` | 同左 |
| `ember-v1.yml` | `0fb125bd04fa4786253e2621591004538e2276fbe1e5d105f084538cbbb05c62` | `aebd2d51…`（仅注释路径 + bot 前缀 `MapV2` 差一行；**数值表一致**；勿把 runtime 副本提交进 git） |

p1sim 导出规则内容哈希（`rules.py` stamp）：`9806c4d10d1fc270`（bv29）。

## 本版包含的裁决

| 裁决 | 提交 | 说明 |
|---|---|---|
| D160 余烬连战 | `7b1bd1c` | 每周首通领奖，失败无限重试；练习局不上奖 |
| D161 存档同步 | `64a0593` | 存取/快照后同 tick `p.saveData()` |
| D162 资产交付 | `64a0593` | `cr_p1_delivery`、同步自动入库、工坊 hold、AssetGuard |

## 验收报告

| 报告 | 路径 | 结果摘要 |
|---|---|---|
| 持久化往返 + 故障注入 | `docs/tests/TEST-ember-persist-roundtrip-2026-10-04.md` | 最终 a–f 48/0；g 52/0 |
| 真实装备试玩 | `docs/tests/TEST-ember-realplay-2026-10-04.md` | 由 COORD-rush-retry-persist 撰写中 |
| 状态说明 | `docs/status/STATUS-ember-rush-persist-d160-d162.md` | D160–D162 部署说明 |
| 模拟器修正 M01–M06 | `tools/p1sim/out-simfix-m01-m06.md` 等 | 已进 main（`a3d1bbe`…`70eaf12`） |

## 地图

P1 主线书白盒：`plugins/DungeonPlus/map/ember_daily_*_v1`（见根目录 `RELEASE-MANIFEST.txt`；地图 gitignored，Release `maps-v1-2026-10-02`）。

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# 从 64a0593 的 git archive 重编后逐文件比对 plugins/CoreRpg.jar
# 期望 sha256 = f415f897386404f2c649f8c435daccdb0b157875a296d069319a71c74e482504
```

## 下一次发版时请更新本表

换 jar / 改 `balance_version` / 换地图包时，复制本文件为新版本号，填新哈希与测试链接；旧文件保留作历史。
