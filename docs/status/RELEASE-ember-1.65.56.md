# RELEASE · CoreRpg 1.65.56（D230）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-1：从 `EmberRunService` 抽出 `EmberRushService`（行为不变） |
| 版本 | CoreRpg **1.65.56**；`balance_version` **58**（数量不变） |
| 代码 | `EmberRushService`（settle / menu / stage / week）；`EmberRunService` 薄委托；`EmberRushServiceTest` ×7；`EmberCounters` 归属 |
| 不变 | bv58；所有发放 / 消耗数量；技能；外观 C15；echotune；p1sim；R2 swap |
| 测试 | unit **416/0**（JDK8 class 52，+7）；冒烟 FreshQ798 rush 菜单+绑本 + FreshQ799 Q01 settle（见 `docs/tests/smoke-2026-10-06-d230-arch-s3-rush.md`） |
| 部署 | jar sha256 `93acf02ae298bd5f…`；play PID 2196183；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.55-pre-1.65.56.jar` |
| 文档 | `STATUS-ember-arch-s3-1-1.65.56.md`；ARCH §6.1；source-table D230 |
