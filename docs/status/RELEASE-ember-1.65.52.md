# RELEASE · CoreRpg 1.65.52（D224）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S2-7 / E1：`EmberEconomy.amount()` 以 `ember-v1-economy.yml` 为真源 |
| 版本 | CoreRpg **1.65.52**；`balance_version` **57**（数量不变） |
| 代码 | `loadEconomyYml` / `economyYmlReady`；`amount()` 读 yml + dual-assert golden；缺/坏 fail-closed；启动 SEVERE；`grantCoin` 拒未就绪 |
| 不变 | bv57；所有玩法数量；未改 p1sim / 技能 |
| 测试 | unit（见 COORD）；冒烟 FreshQ785+（forge spend + settle） |
| 文档 | `STATUS-ember-economy-s2-7-1.65.52.md`；设计稿 E1 SoT 勾选 |
