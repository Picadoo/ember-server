# RELEASE · CoreRpg 1.65.57（D231）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-2：从 `EmberRunService` 抽出 `EmberAbyssService`（行为不变） |
| 版本 | CoreRpg **1.65.57**；`balance_version` **58**（数量不变） |
| 代码 | `EmberAbyssService`（fee/floor/menu）；`EmberRunService` 薄委托；`EmberAbyssServiceTest` ×6；`EmberCounters` 归属；Economy addCoin 白名单 |
| 不变 | bv58；所有发放 / 消耗数量；技能；外观 C15；echotune；p1sim；R2 swap；Pledge |
| 测试 | unit **422/0**（JDK8 class 52，+6）；冒烟 FreshQ800+（见 `docs/tests/smoke-2026-10-06-d231-arch-s3-abyss.md`） |
| 部署 | jar sha256 `b81b2196381f20e9…`；play PID 2207315；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.56-pre-1.65.57.jar` |
| 文档 | `STATUS-ember-arch-s3-2-1.65.57.md`；ARCH §6.1；source-table D231 |
| 为何 Abyss | 层费/层纪录/菜单缝比 Pledge 更清晰（Pledge 与周规则共享 modifier） |
