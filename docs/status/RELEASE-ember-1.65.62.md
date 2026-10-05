# RELEASE · CoreRpg 1.65.62（D236）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S3-7：遭遇原语接口 + 破绽适配器（wall/whiff/break）+ 复活点枚举（行为不变） |
| 版本 | CoreRpg **1.65.62**；`balance_version` **58**（数量不变） |
| 代码 | `p1.encounter`：`EmberEncounter` / `CounterplayKind` / `EmberCounterplay` / `RevivePoint` / `BossMove` / `RoomObjective`；Director / RunService 薄委托；`EmberCounterplayTest` ×11 + `RevivePointTest` ×3 |
| 不变 | bv58；破绽秒数 / 复活 why 原文；技能；外观 C15；echotune；p1sim；R2 swap；内容包 |
| 余部 | Session/Settlement；BossMove / RoomObjective 适配器 |
| 测试 | unit **460/0**（JDK8 class 52，+14）；冒烟 FreshQ819+（见 `docs/tests/smoke-2026-10-06-d236-arch-s3-encounter.md`） |
| 部署 | jar sha256 `7db6e84917e2ce4b3be7eacb9941c7bbbb75ae51f917fbb296b88ba031f4c954`；play PID 2264301；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.61-pre-1.65.62.jar` |
| 文档 | `STATUS-ember-arch-s3-7-1.65.62.md`；ARCH §6.1；source-table D236 |
