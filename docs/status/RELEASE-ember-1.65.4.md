# 发布凭证 · CoreRpg 1.65.4（2026-10-04）

可复查的「此刻线上是什么」清单（格式同 `RELEASE-ember-1.65.3.md`）。

## 线上状态（核对于 2026-10-04 15:58 Asia/Shanghai）

| 项 | 值 |
|---|---|
| 版本 | CoreRpg **1.65.4**（日志 `Enabling CoreRpg v1.65.4`） |
| 进程 | PID 684468（`server-runtime/server.pid`） |
| MySQL | `[CoreRpg] [storage] MySQL connected`；`[CoreGacha] [db] MySQL connected` |
| SEVERE | 0（自本 boot） |
| balance_version | 29（未变） |
| 内容 | D170 房提示；D169 仅文档（装备 staged plan） |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.3-pre-1654.jar`（sha256 `3a22e1085bb0081262bd0952379bb9492a529cd0de252682b52e4bb482e2d260`）；回滚时把 `plugins/CoreRpg/ember-v1-runs.yml` 换回 1.65.3（去掉 D170 hint 行） |
| 本版 jar | `/workspace/backup/CoreRpg-1.65.4.jar`（sha256 `21feda2d7f45e4640e7da5339677f542d9ab6eb5e6033bcd4c66d077e758bcb5`） |
| 源码 | 本版提交见 git log；`git archive` 重编后与线上 jar 逐文件一致（见文末） |

## 单测

230 / 0（JDK8）。新增 `EmberRunRulesTest.textHints_D170`。

## jar 身份核对

```bash
# git archive <本版 CoreRpg 提交> CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg &&
# JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01 mvn -o -q package
# 逐文件比对 target/CoreRpg.jar 与 plugins/CoreRpg.jar 的条目内容（jar 时间戳不同，sha256 不比）
```
