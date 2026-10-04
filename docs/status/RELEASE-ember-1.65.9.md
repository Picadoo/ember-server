# 发布凭证 · CoreRpg 1.65.9（2026-10-04）

P1 挂机庭（D177）。研究：`docs/design/RESEARCH-ember-afk-2026-10-04.md` · 设计：`docs/design/DESIGN-ember-afk-p1-2026-10-04.md`。

## 线上状态（核对于 2026-10-04 约 18:32 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.9**（日志 `Enabling CoreRpg v1.65.9`，18:23:53）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `f373004924d2e8a60802226ca432fa37c2e4754d656054caa1325765261f5238` |
| jar 条目数 | 2352 |
| balance_version | **33** |
| 代码提交 | `0d336e1` |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.8-pre-1.65.9.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 848434（`server-runtime/stop.sh` → `start.sh`，18:23）|
| 启动日志 | `[P1 afk] on world=ember_afk round=10m daily=12 offline=1/4 max 6 tiers=4 legacy_payouts=false` |

**18:35 起被 1.65.10（D174 stage 1.5，COORD-mainline-unlocks，提交 `108a0a3`）接替**：1.65.10 基于 `0d336e1` 构建，D177 全部包含；
重启后日志同样有上面那行 `[P1 afk] on …`、两条 MySQL connected、SEVERE 0。

## 本版裁决

| 裁决 | 说明 |
|---|---|
| D177 | P1 挂机庭：复用 `ember_afk`；四层按首通 Q01/Q03/Q05/Q07 开放；只发账号绑定的余烬币 + 经验（每 10 分钟一轮，每日 12 轮，离线 1/4 最多 6 轮）；P1 下旧挂机怪掉落 / 击杀币 / 击杀经验与旧寄售关闭；修订 D63「P1 收入只来自通关」（材料 / 装备仍只来自通关）|

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 252 / 0（含 `EmberAfkServiceTest`）|
| p1sim | `tools/p1sim/afk.py`（`tools/p1sim/out-afk-d177.md`）：21 格 21/21（1600 人复测，bv33）；W30 share 0.5 −0.24 周 ✔，上界 share 1.0 −0.56 周（略超 ±0.5）→ Stage 1.1 降每轮币（设计 §7.3）|
| 冒烟（FreshQ145，约 6 分钟）| 未首通锁定 ✔ · `/corerpg ah` P1 关闭 ✔ · 主菜单 → `ember_p1_afk` → 灰坡传送 ✔ · PAPI 进度 ✔ · 在线 1 轮 +8 币 +3 经验 ✔ · 下线 3 分钟补 3 轮 ✔ · 立即重连不补 ✔ · 挂机庭 `mmgive` 不发 ✔（计时临时 1 分钟 / 离线 1:1，测完恢复并 reload）|
| 资产回归 | 无物品资产路径变化（只用现有账本的 coin / xp 行）→ 未跑 persist-roundtrip |
| config.yml | **未改**（全部数值在 `ember-v1.yml afk:`）|

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# 检出 0d336e1 的 CoreRpg 后：mvn -o -q package
sha256sum CoreRpg/target/CoreRpg.jar
```
