# RELEASE · CoreRpg 1.65.67（D241 / ARCH S3-12）

| 项 | 内容 |
|---|---|
| 版本 | CoreRpg **1.65.67** · deliverable **D241** · `balance_version` **58** |
| 玩家可见 | `%ember_daily_left%` = 体力 ÷ 日常单次体力（满 90 → 3）；`%ember_weekly_left%` = 周常免费次数（1 → 用后 0，周一重置）。此前没有提供方，显示原文。`ember_daily` / `ember_weekly`（旧版菜单）各加一行 lore |
| 代码 | `EmberPapiCounts` + `EmberLadderExpansion` 路由；`p1.encounter` 词缀原语：`AffixFamily` / `AffixBehavior` / `AffixCycle` / `EmberShape` / `EmberAffixes` + 12 个 `Affix*` 类；`EmberRunDirector` 改走原语（2448→2328 行） |
| 单测 | `EmberPapiCountsTest` ×5 + `EmberAffixPrimitivesTest` ×15 + `EmberAffixReplayTest` ×2（200 种子 × 12 词缀，旧 ↔ 新 18133 行轨迹 sha256 相同）→ **518/0**（JDK8） |
| 冒烟 | 部署前 4/0/0；部署后 47/6/15 + 补跑 21/0/1（6 FAIL = 脚本时序，已修复后重跑通过）；FreshQ828 跨 jar 只变这 2 个键（752 键）；FreshQ829 第 2 会话 3 / 1，第 2↔3 会话 752 键逐字相同；Q01 回归通过；12 个词缀 live 全部 on + done；MySQL×2；SEVERE 0 |
| 回滚 | `/workspace/backup/CoreRpg-1.65.66-pre-1.65.67.jar` |
| 余部 | S3 完成 → S4 装备结构合并文档 + `source_map` |
