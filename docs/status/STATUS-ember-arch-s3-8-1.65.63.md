# STATUS · ARCH S3-8（CoreRpg 1.65.63 / D237）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.62 / D236 / bv58（c55d68d）

## 为什么拆 Session（Entry 余部）

ARCH §5 S3：「`EmberRunService` 按分节拆为 Entry / Settlement / …（行为不变）」。D235 只拆了门槛半；会话创建 / 体力+层费预留 / DP 派发 / `verifyEntry` 与结算共享会话表 + 账本，本刀先把入场写路径收成 `EmberSessionService`。结算树（`settleFor` / `onBossKilled` / `failRefund`）过大 → 留 TODO。

## 做了什么

1. 新建 `EmberSessionService`（原顺序、原文案）：
   - `start`：建 `EmberRunSession`（runId / 快照 / 种子 / 额外事件 / D138 花样 + 管理员 `forcedExtra` / `forcedVariety`）→ 队员与目标族 → 体力 `reserveFlat` + 深渊层费 `applyFeeSpend`（失败回滚 `release`）→ `sessions` + `passes` + `enteringLine` → `dispatchStart` → 40 tick 后 `verifyEntry`。
   - `verifyEntry`：在实例内 `commit`、未进入 `release`、无人进入作废、A18 人数倍率锁定、开场行（深渊 / 国庆 / 连战 / 团本 / 誓约 / 周规则 / 主线）+ `potionCheck`。
   - `commit` / `release` / `releaseFee`：账本 `cost` / `cost_coin` 状态翻转（abort / 换世界 commit 薄委托）。
   - Bukkit-free：`runId` / `varietyEligible` / `applyForcedVariety` / `stampVariety` / 开场行文案 / 层费印记提示。
2. `EmberRunService.enter` 变薄：`entry.admit` → `entry.readinessHold` → `session.start`。`runs variety|extra` → `session.forceVariety|forceExtra`。`abort` / `commit` / `ledgerRow` / `putSession` 等改 package 给 Session 用。
3. **Settlement TODO**：`onBossKilled` / `settleFor` / `failRefund` 仍在 `EmberRunService`（与 Session 共享会话表 + 账本，下一刀 Settlement）。
4. `EmberSessionServiceTest` ×6：runId 形状；D138 资格与强制花样解析；层费 / 未进入文案；开场行；bv58 / 体力 30 / passSeconds。
5. 版本 **1.65.63**；`balance_version` **58** 不变。

## 不变

- `balance_version` **58**；主线 30 / 团本 50 体力；深渊层费与印记抵扣；开场行与回滚文案；D138 花样；技能；C15；echotune；p1sim；R2 swap；内容包。

## 下一刀候选

1. **Settlement**：`settleFor` / `onBossKilled` / `failRefund` → `EmberSettlementService`（或并入 Session 第二刀）。
2. `BossMove` / `RoomObjective` 适配器；词缀原语；Papi 分节。

## 冒烟

FreshQ821+（见 `docs/tests/smoke-2026-10-06-d237-arch-s3-session.md`）：Q01 进本行 + 三房 + 结算。
