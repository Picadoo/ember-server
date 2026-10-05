# RELEASE · CoreRpg 1.65.61（D235）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-6：从 `EmberRunService.enter` 抽出 `EmberEntryService`（门槛 / 提醒 / 周规则半，行为不变） |
| 版本 | CoreRpg **1.65.61**；`balance_version` **58**（数量不变） |
| 代码 | `EmberEntryService`（`admit` 人数 / 体力 / 问题行 / 每模式门槛；`readinessHold` D96·D104；`applyWeeklyRule` P2-8·D94·强制·D174 誓约；`enteringLine`）；`EmberAbyssService.entryProblems`、`EmberRushService.entryProblem`（每模式一行调用）；`EmberRunService` 薄委托；`EmberEntryServiceTest` ×6 |
| 不变 | bv58；所有发放 / 消耗数量；问题行文案与顺序；技能；外观 C15；echotune；p1sim；R2 swap |
| 余部 | 会话创建 / 体力+层费预留 / DP 派发 / verifyEntry 留在 `EmberRunService`（随 Settlement 拆） |
| 测试 | unit **446/0**（JDK8 class 52，+6）；冒烟 FreshQ816–818 PASS 30/0/0（见 `docs/tests/smoke-2026-10-06-d235-arch-s3-entry.md`） |
| 部署 | jar sha256 `0158fd07c0b434e3263e9ffd1f08b13a…`；play PID 2247175；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.60-pre-1.65.61.jar` |
| 文档 | `STATUS-ember-arch-s3-6-1.65.61.md`；ARCH §6.1；source-table D235 |
