# 发布凭证 · CoreRpg 1.65.5（2026-10-04）

可复查的「此刻线上是什么」清单（格式同 `RELEASE-ember-1.65.3.md`）。纯漏洞修复（D172）。

## 线上状态（核对于 2026-10-04 17:15 Asia/Shanghai）

| 项 | 值 |
|---|---|
| 源码 | 本版提交见下表；`git archive` 重编后与线上 jar 逐文件一致（2273 个条目，0 差异） |
| CoreRpg 版本 | **1.65.5**（`plugin.yml`、`pom.xml`；日志 `Enabling CoreRpg v1.65.5`） |
| CoreRpg JAR sha256 | `c4e92b133b6ff06dc2bc4325652f2a73065a4fe667280db4d4085445de2d757b`（= `/workspace/backup/CoreRpg-1.65.5.jar`） |
| CoreRpg class major | **52**（JDK 8，`tools/jdk8u504-b01`） |
| CoreGacha 版本 / sha256 | 1.0.1 / `4526359f4e961733287ca8bc0674b03c4c844d58fde2daa615f83180b705b2c6`（未换） |
| 部署 | jar 16:33 CST 换上；最终关钩子重启 **17:10 CST**（PID 763512） |
| `balance_version` | **29**（不变） |
| growth.yml `version` | **3**（不变） |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.4-pre-1655.jar`（1.65.4，sha256 `21feda2d7f45e4640e7da5339677f542d9ab6eb5e6033bcd4c66d077e758bcb5`）。规则 yml 没变，直接换 jar 即可；回滚后 1.65.5 留下的 `kind=mark` 送货行（只在兑换失败退款时产生，冒烟后 0 条 pending）1.65.4 不认识 |
| 故障注入 | **关**（play JVM 环境无 `CORERPG_TEST_FAULTS`；`/corerpg p1 fault` 被拒） |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected`；SEVERE 0 |
| ops.json | `server-runtime/ops.json` 与 `login-runtime/ops.json` 均为 `[]` |

## 规则文件哈希（sha256，本版未改）

| 文件 | 源码 / JAR 内嵌 | 线上 `plugins/CoreRpg/` |
|---|---|---|
| `ember-v1-runs.yml` | `37c818a585e6fad3178d7443537c7cce3be6bb7f5078e74fe08fd586e44b6ab7` | 同左 |
| `ember-v1-growth.yml` | `56576ddbd00517afeaf47c5b8663bb9af2a3340754fc0a14e0de5ffd91fef5b9` | 同左 |
| `ember-v1-festival.yml` | `67faeeab1f3a9f22368ac3e49c2c62ba9b6d9732bef05f79d19f7df8f9f9ce68` | 同左 |

## 本版包含的裁决

| 裁决 | 说明 |
|---|---|
| D172 | 撤销先扣胚料（X5）；印记兑换一笔事务 + `mark` 欠款（X1 / X4）；洗练耐久付款、付款落盘后才抽（X15）。见源表 §13.87 |

## 验收

| 报告 | 路径 | 摘要 |
|---|---|---|
| 状态 + 测试表 + 冒烟 | `docs/status/STATUS-ember-asset-fix-1.65.5.md` | persist-roundtrip g,h PASS 203 / FAIL 0；FreshQ120 Q01 kite CLEAR；洗练 / 兑换 / 撤销手动各一次 |
| 单测 | `CoreRpg` `mvn -o package` | 238 / 0（新增 `EmberPayTest` 8 个） |
| 设计 | `docs/design/DESIGN-ember-forge-random-2026-10-04.md` §5 / §6.6 | 前置标为 DONE |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# git archive <本版 CoreRpg 提交> CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg && mvn -o -q package
# （把 plugins/PlaceholderAPI.jar、plugins/NeigeItems-1.21.151.jar 链到 /tmp/x/plugins/ 供 systemPath 依赖）
# 逐文件比对 target/CoreRpg.jar 与 plugins/CoreRpg.jar 的条目内容（jar 时间戳不同，sha256 不比）
```
