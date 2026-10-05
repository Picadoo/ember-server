# RELEASE · CoreRpg 1.65.64（D238）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-9：`EmberSettleService` 抽出（settleFor / onBossKilled / failRefund） |
| 版本 | CoreRpg **1.65.64**；`balance_version` **58**（数量不变） |
| 代码 | `EmberSettleService`；`EmberRunService` 薄委托；`EmberSettleServiceTest` ×7 |
| 不变 | bv58；体力 / 失败退还 / 精选与签名 grant；技能；外观 C15；echotune；p1sim；R2 swap；内容包 |
| 余部 | BossMove / RoomObjective 适配器；Papi；词缀原语 |
| 测试 | unit **473/0**（JDK8 class 52，+7）；冒烟 FreshQ822+（见 `docs/tests/smoke-2026-10-06-d238-arch-s3-settle.md`） |
| 部署 | jar sha256 `8541e3f4a6de5a594b0d038ffb8b61080f15e68e6ae3230d2c3317da85be6870`；play PID 2283561；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.63-pre-1.65.64.jar` |
| 文档 | `STATUS-ember-arch-s3-9-1.65.64.md`；ARCH §6.1；source-table D238 |
