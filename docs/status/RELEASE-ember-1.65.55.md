# RELEASE · CoreRpg 1.65.55（D229）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S2-9：深渊层 SourceId S13 + vault 写入扫描（REG §6.4） |
| 版本 | CoreRpg **1.65.55**；`balance_version` **58**（数量不变） |
| 代码 | `EmberEconomy.isAbyssHead` / `sourceForGrant` 深渊 head → S13；S01 名去掉深渊；vault 调用点 `econ-ok:`；`EmberEconomyTest` +2 |
| 不变 | bv58；所有发放 / 消耗数量；技能；外观 C15；echotune；p1sim；R2 swap |
| 测试 | unit **409/0**（JDK8 class 52，+2）；冒烟 FreshQ797 PASS 17/0（见 `docs/tests/smoke-2026-10-06-d229-economy-s13-vault.md`） |
| 部署 | jar sha256 `20c0e4577d1c23cb…`；play PID 2172246；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.54-pre-1.65.55.jar`（`52254406…`） |
| 文档 | `STATUS-ember-economy-s2-9-1.65.55.md`；REG §2.1 / §6.4 / §7 / §8；ARCH §6.1；source-table D229 |
