# 发布凭证 · CoreRpg 1.65.34（2026-10-05）

D196 Affix Pack 5：重打已首通普通主线时的发光词缀精英 10 选 1 → 12 选 1，新增 **旋光**（精英脚下的光束预览 1.5 秒后转半圈，扫到吃 1 下；退到 5 格外或站另半边就没事）和 **火链**（精英和身边一只同房怪之间的火链，碰到每秒最多烧 1 次；先杀被连的那只，链会冒烟 1.2 秒后换人）。奖励（余烬碎片 +2）、闸门、掉落全部不变。设计：`docs/design/DESIGN-ember-affix-pack5-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 11:41 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.34**（`Enabling CoreRpg v1.65.34` 11:40:43）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `917af486839c4f9e4fc5311e41b7a42edace4676c7a9b25dee641d27c95194b5` |
| balance_version | **57**（rule_version g04-1/b57）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.33-pre-1.65.34.jar`（sha256 `4f37b4eb…` = 1.65.33）|
| 服务器 PID | **1527320**（was 1500928；`server-runtime/stop.sh` 优雅停 → `start.sh`）|
| MySQL | `[CoreRpg] [storage] MySQL connected` 11:40:43 · `[CoreGacha] [db] MySQL connected` 11:40:45 |
| SEVERE | 0（Adyeshach `duplicate class definition` WARN 上一次启动同样存在，与本包无关）|
| 部署文件 | `plugins/CoreRpg.jar`、`plugins/CoreRpg/ember-v1-runs.yml`（variety 两缀 + bv57）、`plugins/TrMenu/menus/ember_p1_codex.yml` / `ember_p1_adventure.yml`（词缀列表）；config.yml 未动 |
| 源码对账 | jar 由提交 `2698fe8` 的干净 worktree（/workspace/d196-wt）构建；`git archive 2698fe8` 重建的 jar 解包后与线上 jar **逐文件一致**（除 META-INF/maven）；jar 内 ember-v1-runs.yml 与 `plugins/CoreRpg/ember-v1-runs.yml` 逐字节一致；部署后 /workspace/minecraft 快进到同一提交 |

## 内容

- `arcane` 旋光：`{every: 8.0, warn: 1.5, length: 5.0, width: 1.2, sweep: 180, spin: 3.0, dmg: 1.0}`；扫掠区间判定，每次施放每人最多 1 次；转向交替；钳 sweep ≤ 180、spin ≥ 1.5 s
- `firechain` 火链：`{warn: 1.2, width: 1.0, tick: 1.0, dmg: 0.3, range: 10}`；只连同房非首领非护宝兔的活怪；每人每秒最多 1 次；钳 dmg ≤ 1.0、tick ≥ 0.5
- kb 0、无实体 / 方块；奖励 / 掉落 / 周 / 日上限 / 闸门 / 资产路径：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package`（JDK 8）| **300 / 0**（+1 `varietyPack5ArcaneFirechain_D196`；KNOWN 计数 10 → 12 三处；bv57）|
| p1sim | `tools/p1sim/affixpack5.py`：Part A（Q01–Q07 × 书参考 / T3+6 × 躲避 0.3 / 0.5 / 0.7，1,500 局 × 3 种子）MAX_ABS_DPP **0.8**；压力列（首发提前到开房 + 1.5 秒）新缀比毒十字难 ≤ **0.4**；Part B p2econ（120 人 & 300 人 × 12 周，d0.5）新 12 缀池 vs 旧 10 缀池 MAX_ABS_DPP **3.0**（第 6 周「两件极品」，新池略慢、不更肥；W12 币 ±0.4%）→ **within range**；报告 `tools/p1sim/out-affixpack5-d196.md` |
| 冒烟 | 合批 deferred（新一批第 3 个：1.65.32 D194 + 1.65.33 D195 + 1.65.34 D196）。D196 冒烟：已首通 Q01 的 FreshQ 号 `/corerpg p1 runs variety arcane:r1` → Q01 普通重打：进房「词缀精英「旋光」」、紫线 + 半圆预览 → 光束转过，站弧里 `latest.log` `arcane ccw|cw hit=1`、站另半边 / 5 格外 `hit=0`，下次转向相反；`variety firechain:r1` → `firechain link <role>`，烟线 → 火线，站两怪之间每秒掉血一次，杀被连的那只 → 新 `firechain link`，只剩精英无链；结算「余烬碎片 +2」、无残留 |
| persist-roundtrip | 不需要（无资产路径变化：仓库存取 / 断线 / 重启 / 拆解 / 撤销 / 快照 / 扭蛋 / 补发均未改）|
