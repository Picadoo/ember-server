# 发布凭证 · CoreRpg 1.65.8（2026-10-04）

Weekly Modifier Pack 2（D175→D178）。设计：`docs/design/DESIGN-ember-weekly-mod-pack2-2026-10-04.md`。

## 线上状态（核对于 2026-10-04 约 18:20 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.8**（日志 `Enabling CoreRpg v1.65.8`，18:17:44）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `54910a1c63c8daa73d27f7c3f22768d8b206a40e711f8819272a820ca185efa9` |
| jar 条目数 | 2350 |
| balance_version | **32** |
| 代码提交 | `3e77c48`（D178 Pack 2）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.7-pre-1658.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 839633（`server-runtime/stop.sh` → `start.sh`，18:17）|

## 本版裁决

| 裁决 | 说明 |
|---|---|
| D178 | 精选图周规则 +3（弓潮 / 龟甲 / 压阵）；池 6→9；全部 challenge-only；实现 D175 |

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 249 / 0 |
| p1sim `--mods` | **DEFERRED**（p1sim 占用） |
| 冒烟 | FreshQ139 bolters@q03、FreshQ140 shell@q03、FreshQ141 press@q01：聊天规则名 + forced 日志均 PASS |
| 资产回归 | 无物品资产路径变化 → 未跑整套 persist-roundtrip |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# 从本版代码提交检出 CoreRpg 后：
# mvn -o -q package
# unzip -p target/CoreRpg.jar plugin.yml | head -5   # version: 1.65.8
# sha256sum target/CoreRpg.jar
```
