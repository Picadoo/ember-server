# 发布凭证 · CoreRpg 1.65.7（2026-10-04）

D174 主线签名传奇 · 第 1 阶段（Q01–Q03）。设计：`docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md`；调研：`docs/design/RESEARCH-ember-mainline-unlocks-2026-10-04.md`。

## 线上状态（核对于 2026-10-04 约 18:10 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.7**（日志 `Enabling CoreRpg v1.65.7`，18:04:59）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `6e8b56f783c5718d890e1fc721d6727cc03d3611d37fd167d4ce3031558f3aef` |
| jar 条目数 | 2350 |
| balance_version | **31** |
| 代码提交 | `11b55be`（D174 stage 1）+ `134d985`（版本 / balance_version 31）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.6-pre-1657.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 823495（`server-runtime/stop.sh` → `start.sh`，18:04）|

## 本版裁决

| 裁决 | 说明 |
|---|---|
| D174 | 每张主线图自己的签名传奇 + 首领徽记 + 每图首通新解锁；第 1 阶段 Q01–Q03：6 件签名、徽记、烬炉烙印（Q02）、双签名（Q03）|

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 249 / 0（新增 `EmberSignatureTest` 6 个）|
| p1sim | `tools/p1sim/out-d174-mainline.md`（单条 / 组合 / 叠天赋 42 格；W30 上界）|
| 冒烟 | `tools/p1map/d174-sig-smoke.sh`：FreshQ136 真实 Q01 重打结算发 1 枚徽记；FreshQ138 图鉴 / 烙印 / 生效 / mods / 二次拒绝 / 退出重进 10 / 10 |
| 资产回归 | 无物品资产路径变化 → 未跑整套 persist-roundtrip；持久化（退出重进）在冒烟里通过 |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# git archive 134d985 CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg && mvn -o -q package
# （把 plugins/PlaceholderAPI.jar、plugins/NeigeItems-1.21.151.jar 链到 /tmp/x/plugins/）
```
