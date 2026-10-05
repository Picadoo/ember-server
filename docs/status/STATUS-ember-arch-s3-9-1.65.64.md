# STATUS · ARCH S3-9（CoreRpg 1.65.64 / D238）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.63 / D237 / bv58（bbf7dc6）

## 为什么拆 Settlement

ARCH §5 S3：「`EmberRunService` 按分节拆为 Entry / Settlement / …（行为不变）」。D237 收了入场写路径；结算树（`settleFor` / `onBossKilled` / `failRefund`）与 Session 共享会话表 + 账本，本刀收成 `EmberSettleService`。

## 做了什么

1. 新建 `EmberSettleService`（原顺序、原文案）：
   - `onBossKilled`：资格判定 → 国庆 / 连战 / `settleFor` → COMPLETE → `endInstance`。
   - `settleFor`：基础 settle + `appendBonuses`（raid / rot / signature / pledge / fc_sig / variety / bounty）→ 花样委托 / 勋记 / 账本 / 赛季 / 深渊纪录 → `deliver`。
   - `failRefund` / `failRefundLabel`：D128 当日首次挑战/深渊失败退体力。
   - `endOfP1`：Q07 首通完结行随迁。
   - Bukkit-free：失败退还文案、`rotationMarkAmount`、`appendBonuses` / `grantKeys`。
2. `EmberRunService` 薄委托：`fail` → `settle.failRefund`；死亡事件 → `settle.onBossKilled`；PAPI → `settle.failRefundLabel`。`endInstance` / `leaderboard()` package 给 Settle 用。
3. `EmberSettleServiceTest` ×7：失败退还文案与数额；开场/资格行；旋转印记；Q01 首通 / 重打 grant 顺序与数额；团本 grant 顺序；bv58。
4. 版本 **1.65.64**；`balance_version` **58** 不变。

## 不变

- `balance_version` **58**；主线 30 / 团本 50 体力；失败退还 50%；精选 / 签名 / 誓约 / 花样 / 委托数额；技能；C15；echotune；p1sim；R2 swap；内容包。

## 下一刀候选

1. **BossMove / RoomObjective** 适配器（遭遇原语第二刀）。
2. **Papi** 分节；词缀原语。

## 冒烟

FreshQ822+（见 `docs/tests/smoke-2026-10-06-d238-arch-s3-settle.md`）：Q01 进本 + 三房 + 结算（+ 失败退还若易做）。
