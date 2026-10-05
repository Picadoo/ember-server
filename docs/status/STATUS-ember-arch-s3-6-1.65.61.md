# STATUS · ARCH S3-6（CoreRpg 1.65.61 / D235）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.60 / D234 / bv58（ebcda24）

## 为什么拆 Entry（门槛半）

ARCH §5 S3：「`EmberRunService` 按分节拆为 Entry / Settlement / Abyss / Raid / Rush / Pledge / Recruit / Papi（行为不变）」。D234 手递：Rush / Abyss / Pledge / Raid 在 `enter` 里只剩一行调用，主路径可单独成服务。

`enter` 本身 ~235 行，分两段：**门槛 / 提醒 / 周规则**（只读玩家数据 + 发提示）和 **会话 / 预留 / 派发**（写会话表 + 账本 + 体力 / 层费，与 `verifyEntry` / `release` / 结算共享状态）。本刀按「可拆一半就拆一半」只拆前段，后段文档化为余部。

## 做了什么

1. 新建 `EmberEntryService`（原顺序、原文案）：
   - `admit`：模式开启 / 图已知 / 挑战·深渊已配置 / 队长开本 → 收队员（不在线写名字，D101）→ 人数 → 团本·活动·连战没有挑战/深渊版本 → 体力服务 → 每人：深渊 | 挑战 | 活动 | 连战 | 团本 | 解锁（互斥分支）+ 已在另一局 / 仍在副本 / 体力不足 → 问题行发给全队（队长不在队里也发）。
   - `readinessHold`：D96 Q02+ 首通 T1 刃/护符提醒（`force` 一次性跳过）、D104 挑战 T3 刃提醒（每服务器会话一次）；`forcedReady` / `warnedT3` 随迁。
   - `applyWeeklyRule`：P2-8 挑战周规则、D94 精选图普通版规则（`normal: true` + 全员已首通）、管理员 `runs modifier` 一次性强制（`forcedModifier` 随迁，`forceModifier` 钩子）、D174 自选誓约回落（`EmberPledgeService.sessionKey`）。
   - `enteringLine`：深渊 / 连战 / 国庆 / 团本·主线「正在创建实例……」行。
   - Bukkit-free：`partySizeProblem` / `variantProblems` / `staminaProblem` / `challengeLockedText` / `lockedText` / `busyText` / `inDungeonWorld` / `plainMainRun` / `challengeRule` / `normalRule` / D96·D104 文案。
2. 每模式门槛一行调用：深渊 → `EmberAbyssService.entryProblems`（开放 / 层数 / 层费，0–2 行，原顺序；`lockedText`/`tierTooHighText`/`feeShortText` Bukkit-free）；连战 → `EmberRushService.entryProblem`（`entryProblemText` Bukkit-free）；团本 `EmberRaidService.entryProblem`、活动 `EmberFestival.entryProblem` 原样。
3. `EmberRunService.enter` 变薄：`entry.admit` → `entry.readinessHold` → 建会话 → `entry.applyWeeklyRule` → 种子 / 额外事件 / 花样 → 预留 → `entry.enteringLine` → DP 派发。`openSessionOf` 改 package 可见；`cmd enter … force` → `entry.markForced`；`runs modifier` → `entry.forceModifier`。约 −150 行。
4. `EmberEntryServiceTest` ×6：打包 q01 / r01 人数与体力（1～3 / 30，3～5 / 50）；体力 / 不在线 / 已在局 / 仍在副本文案；变体 / 挑战 / 解锁 / 深渊 / 连战门槛文案与顺序；`plainMainRun` 对主线 / 团本 / 连战；P2-8 / D94 规则选择（含 98 周循环只有 `normal: true` 规则进普通版）；D96 / D104 文案。
5. 版本 **1.65.61**；`balance_version` **58** 不变。

## 余部（Entry 第二刀，未做）

- 会话创建（runId / 快照 / 种子 / 额外事件 / D138 花样 + 管理员 forcedExtra / forcedVariety）。
- 体力 `reserveFlat` + 深渊层费 `applyFeeSpend` 预留与回滚、`ledgerRow cost / cost_coin`、`passes`。
- DP `dispatchStart` + `verifyEntry`（commit / release / 未进入退还 / A18 人数倍率 / 开场行）。
- 这些与 `release` / `failRefund` / 结算共享 `sessions` + 账本 → 建议与 **Settlement** 一起拆为 `EmberSessionService`。

## 不变

- `balance_version` **58**；所有发放 / 消耗数量（主线 30 体力、团本 50 体力 3 次/周、深渊层费）；问题行文案与顺序；D96 / D104 一次性语义；周规则 / 誓约选择；技能；C15；echotune；p1sim；R2 swap；内容包。

## 下一刀候选

1. **S3 遭遇原语接口**：房间 / 首领招式 / 破绽 / 复活点作为数据驱动原语（D106 `onBossPhase` 已在 Raid 服务；D188/D192/D193 破绽在 Director）。
2. **Session/Settlement**：Entry 余部 + 结算分节 → `EmberSessionService`。
3. **Papi** 分节；S14/S15 经 grant*（体力 / 退药）— S2 leftover。

## 冒烟

FreshQ816–818（run 2，见 `docs/tests/smoke-2026-10-06-d235-arch-s3-entry.md`）：PASS 30 / FAIL 0 / SOFT 0。run 1（FreshQ813–814）26/2：2 条为脚本顺序问题（已首通 Q02 直接进本后再测挑战被副本命令拦），已修脚本。
