# D160–D162：连战重试 + 存档同步 + 资产交付闭环（CoreRpg 1.65.0 → 1.65.1，2026-10-04）

## 部署
- **线上版本：** CoreRpg **1.65.1**，部署时间 10-04 11:31 CST。JDK8 构建，class major 52。1.65.0 在 10:54 CST 上过线。
- **jar 校验：** 从提交 64a0593 的 `git archive` 重新构建，jar 内容逐文件与线上 `plugins/CoreRpg.jar` 一致（sha256 `f415f897…`）。
- **备份：** `/workspace/backup/CoreRpg-1.65.1.jar`，以及之前的 1.64.2 jar。
- **故障注入：** 线上**关闭**。Paper 进程环境里没有 `CORERPG_TEST_FAULTS`；`/corerpg p1 fault` 会拒绝执行（10-04 11:50 CST 核对过）。
- **单测：** 226 个，全过（含 `EmberRunRulesTest` +2、新增 `EmberDeliveryTest`）。
- **提交：**
  - 7b1bd1c：D160 连战，以及 runs.yml、DP、菜单。
  - 64a0593：D161 / D162 代码和 review 修复状态。
  - 文档提交：本文件、测试报告、源表、P2 草案、handoff。

## D160 余烬连战（`p4_rush`）
- 取消每周进入次数，失败可以无限重试，每次尝试没有成本。
- 每周第一次结算通关发奖：T3 印记 + 余烬徽。之后本周再通关算练习：不发奖，但仍然上 `time_rush` 周榜。
- 旧数据兼容：本周旧计数 > 0 且已在周榜上的，算已领。
- 文案同步改了 `ember_help`、`ember_p1_adventure`、`ember_p1_codex`、`ember_p1_season`、两份 `ember-v1-runs.yml`、DungeonPlus `EmberQ0B1/option.yml`。
- 其它周内容逐项复查，结论都是**不改**：团本、精选周挑战、深渊、gq26。理由见源表 §13.78。
- 经济模型：`p2econ.py --rush-tries`。领奖周数变化 < 0.1%，每周印记仍 ≤ 1。
  - 注意：脚本改动被并行的 sim worker 一起提交进了 a3d1bbe。

## D161 存档同步
- 存仓、存库、取出、快照恢复之后，在同一 tick 执行 `p.saveData()`。
- 修复了两个问题：kill -9 之后 .dat 比 DB 旧导致的复制；取出时断线，库里行还是 active 但没有实物。

## D162 资产交付闭环（review §03 A01–A04）
- **A01：** 新表 `cr_p1_delivery`。分解得到的胚料、取出的装备、撤销时要扣回的东西，都和装备事务在同一个事务里写入。
  - 投递恰好一次：靠 `p1dlv_<id>` 标记和 uid 扫描保证。
  - 在线时由回调投递；离线时在下次进服 60 tick 后投递；`/corerpg p1 deliver` 可以重试。
- **A02：** 自动入库是一个同步事务，COMMIT 之后才提示「已入库」。失败时物品留在背包里。
- **A03：** 工坊扣费流程：
  1. 写 hold；
  2. 扣费，并同步保存 `p1paid_`；
  3. 执行事务，作废 hold。
  - 事务失败：释放 hold → 退款。
  - 服务器崩溃：进服时对账。
  - `cr_players` 和 `cr_warehouse` 在一个事务里写。
  - 连续 3 次写失败时，`EmberAssetGuard` 暂停资产变更。
- **A04：** 离线恢复只在应用成功之后才出队，失败时记录 attempts / last_error。恢复期间冻结仓库、装备库、工坊、投递，以及拾取进仓。

## 测试
- 持久化 / 故障测试：`docs/tests/TEST-ember-persist-roundtrip-2026-10-04.md`。
  - 最终 jar：a–f 48 / 0，g 52 / 0。
  - 1.64.2 基线是 40 / 3，暴露出真实问题，已修。
- 真实装备试玩：`docs/tests/TEST-ember-realplay-2026-10-04.md`。

## 已知限制
- 没做真实的 DB 断线测试，只用了故障钩子。
- 没用 bot 测过：自动入库故障、恢复失败重试、多人同一件物品。
- 残留窗口：.dat 和 DB 之间仍有亚 tick 级的窗口。完整回滚另行裁决。

## 下一测号
FreshQ78+ / FreshG10+。下一个裁决号：D163。
