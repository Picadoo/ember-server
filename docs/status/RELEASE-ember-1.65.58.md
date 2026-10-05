# RELEASE · CoreRpg 1.65.58（D232）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-3：从 `EmberRunService` 抽出 `EmberPledgeService`（行为不变） |
| 版本 | CoreRpg **1.65.58**；`balance_version` **58**（数量不变） |
| 代码 | `EmberPledgeService`（counter/toggle/menu/sessionKey/settle/PAPI）；`EmberRunService` 薄委托；`EmberPledgeServiceTest` ×6；`EmberCounters` 归属；S09 登记文案 |
| 不变 | bv58；所有发放 / 消耗数量；技能；外观 C15；echotune；p1sim；R2 swap；Raid |
| 测试 | unit **428/0**（JDK8 class 52，+6）；冒烟 FreshQ804+（见 `docs/tests/smoke-2026-10-06-d232-arch-s3-pledge.md`） |
| 部署 | jar sha256 `9de29f8306be1b7e…`；play PID 2217552；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.57-pre-1.65.58.jar` |
| 文档 | `STATUS-ember-arch-s3-3-1.65.58.md`；ARCH §6.1；source-table D232 |
| 为何 Pledge | `p1_pledge_*` + toggle/off/list + encode/count 形成封闭缝；enter 仅在周规则空时回落，非内部缠结 |
