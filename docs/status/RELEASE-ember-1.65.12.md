# 发布凭证 · CoreRpg 1.65.12（2026-10-04）

D174 主线签名传奇 · 第 2b 阶段 · 自选誓约（Q06）。设计：`docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md`（§5 状态、§9 上线记录）。

## 线上状态（核对于 2026-10-04 19:44 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.12**（日志 `Enabling CoreRpg v1.65.12`）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `bba463145be6c55a1fb139a1a18e4affe00e0f1d0ffd395f3d649d1f33bfaf97` |
| jar 条目数 | 2352 |
| balance_version | **35** |
| 代码提交 | `90251d1` |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.11-pre-16512.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 940686（19:44），`server-runtime/stop.sh` → `start.sh` |

## 内容

首通 Q06 后，队长在重打普通 Q01–Q06 时可以挂「限药」「逆行」（周规则里 normal: true 的那两条），每挂一条本局本图徽记 +1；本周轮换图正好有周规则时以周规则为准、誓约不生效；全队都要首通过这张图。`/corerpg p1 pledge`、`/corerpg p1 modes`；TrMenu `ember_p1_modes`（进阶模式页）+ `ember_p1_pledge`；冒险页「进阶模式」图标；Q06 首通提示

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 255 / 0（新增 `pledgeCombinesTheNormalRules`） |
| p1sim | W30 见设计 §8.5（`P1_PLEDGE=lean+reverse` 上界） |
| 冒烟 | FreshQ197 13 / 13：Q06 前被拒、冒险页 → 进阶模式 → 自选誓约（点击开限药、开逆行、标题「已挂：限药+逆行」、全部取消）、真实重打 Q02 进本日志 `pledge pledge:lean` + 提示。FreshQ191 / FreshQ196 的进本在 Q01（本周轮换图，周规则正好是限药）→ 誓约按规则不生效；FreshQ196 没开 Q02 解锁 → 脚本补了 `runs unlock` |
| 资产回归 | 没有新物品 / 资产路径 → 没跑 persist-roundtrip |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# git archive 90251d1 CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg && mvn -o -q package
# （把 plugins/PlaceholderAPI.jar、plugins/NeigeItems-1.21.151.jar 链到 /tmp/x/plugins/）
```
