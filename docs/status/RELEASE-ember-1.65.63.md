# RELEASE · CoreRpg 1.65.63（D237）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-8：`EmberSessionService` 抽出（会话创建 / 预留 / DP 派发 / verifyEntry；结算 TODO） |
| 版本 | CoreRpg **1.65.63**；`balance_version` **58**（数量不变） |
| 代码 | `EmberSessionService`；`EmberRunService` 薄委托；`EmberSessionServiceTest` ×6 |
| 不变 | bv58；体力 / 层费 / 开场行；技能；外观 C15；echotune；p1sim；R2 swap；内容包 |
| 余部 | Settlement（`settleFor` / `onBossKilled` / `failRefund`）；BossMove / RoomObjective 适配器 |
| 测试 | unit **466/0**（JDK8 class 52，+6）；冒烟 FreshQ821+（见 `docs/tests/smoke-2026-10-06-d237-arch-s3-session.md`） |
| 部署 | jar sha256 `9b7211cc5af1d35b357268b5e39566325cd50ec8bbbacadb22a9a7a2503ac99b`；play PID 2274533；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.62-pre-1.65.63.jar` |
| 文档 | `STATUS-ember-arch-s3-8-1.65.63.md`；ARCH §6.1；source-table D237 |
