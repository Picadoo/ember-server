# RELEASE · CoreRpg 1.65.60（D234）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-5：从 `EmberRunService` 抽出 `EmberRecruitService`（行为不变） |
| 版本 | CoreRpg **1.65.60**；`balance_version` **58**（数量不变） |
| 代码 | `EmberRecruitService`（招募板 TTL/CD、post/list/join、PAPI 标签、登录闪板、拒绝入队行）；`EmberRunService` 薄委托（`@EventHandler` 原位转发，season apply 仍在 join handler）；`EmberRecruitServiceTest` ×6 |
| 不变 | bv58；所有发放 / 消耗数量；技能；外观 C15；echotune；p1sim；R2 swap；团本 Raid 服务 |
| 测试 | unit **440/0**（JDK8 class 52，+6）；冒烟 FreshQ810–812 PASS 22/0/SOFT 1（见 `docs/tests/smoke-2026-10-06-d234-arch-s3-recruit.md`） |
| 部署 | jar sha256 `9a2547c4105864d72803281af0bc6a4a…`；play PID 2239873；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.59-pre-1.65.60.jar` |
| 文档 | `STATUS-ember-arch-s3-5-1.65.60.md`；ARCH §6.1；source-table D234 |
| 为何 Recruit | 与 Raid 零共享状态；D233 手递最低风险下一刀 |
