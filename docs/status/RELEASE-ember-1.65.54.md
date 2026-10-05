# RELEASE · CoreRpg 1.65.54（D228）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S2-8：首领徽记 / 余烬徽 / 剩余印记发放走 `EmberEconomy`；账户计数直写 + `grantFlatEmberXp` 扫描（REG §6.4 第二刀） |
| 版本 | CoreRpg **1.65.54**；`balance_version` **58**（数量不变） |
| 代码 | `EmberEconomy.grantInsignia` / `grantBadge` / `creditMarkLedger` / `creditInsigniaLedger` / `sourceForRushMode`；`sourceForGrant` 新键：sig_mark S08、fc_sigmark S07、pledge_sigmark S09、raid_mark S12、rot_mark S10/S11、rush_mark S16/S17、rush_sig_* S17/S18；`EmberRunService` deliver MARK/SIGMARK + 连战徽、`EmberSeason` 周目标徽、`EmberFestival` 兑换徽改走登记入口；管理钩子 / 退款 / C15（暂停）行加 `econ-ok:` 注释 |
| 不变 | bv58；所有发放 / 消耗数量；技能；外观 C15 逻辑；p1sim |
| 测试 | unit 407/0（JDK8 class 52，+6 新测试）；冒烟 FreshQ790 PASS 25/0（见 `docs/tests/smoke-2026-10-06-d228-economy-insignia-badge.md`） |
| 部署 | jar sha256 `52254406d220c58e…`；play PID 2158083；MySQL×2；SEVERE 0；回滚包 `/workspace/backup/CoreRpg-1.65.53-pre-1.65.54.jar`（`264973a1…`） |
| 文档 | `STATUS-ember-economy-s2-8-1.65.54.md`；REG §6.4 / §7 / §8；ARCH §6.1 进度表；source-table D228 |
