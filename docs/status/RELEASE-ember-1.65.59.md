# RELEASE · CoreRpg 1.65.59（D233）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-4：从 `EmberRunService` 抽出 `EmberRaidService`（行为不变） |
| 版本 | CoreRpg **1.65.59**；`balance_version` **58**（数量不变） |
| 代码 | `EmberRaidService`（`p2_raid_` 周计数器 / 进本问题行 / 菜单 + PAPI 标签 / S12 settle grants / 失败行 / 开场行 / D106 倒下观战 + 复活 + 牵引 + 拦离开 + watch）；`EmberRunService` 薄委托（−174 行，`@EventHandler` 原位转发）；`EmberRaidServiceTest` ×6；`EmberCounters` 归属 |
| 不变 | bv58；所有发放 / 消耗数量；技能；外观 C15；echotune；p1sim；R2 swap；招募板 |
| 测试 | unit **434/0**（JDK8 class 52，+6）；冒烟 FreshQ806–809 PASS 32/0/SOFT 1（见 `docs/tests/smoke-2026-10-06-d233-arch-s3-raid.md`） |
| 部署 | jar sha256 `c71dc7a06a23a943…`；play PID 2226117；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.58-pre-1.65.59.jar` |
| 文档 | `STATUS-ember-arch-s3-4-1.65.59.md`；ARCH §6.1；source-table D233 |
| 为何 Raid | 周计数器 3 处读写 + D106 倒下流程只依赖 session/director，形成封闭缝；Recruit 与之零共享状态，单独一刀（下一刀） |
