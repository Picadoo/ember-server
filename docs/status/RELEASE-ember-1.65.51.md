# RELEASE · CoreRpg 1.65.51（D223）

| 项 | 内容 |
|---|---|
| 主题 | ARCH S2-6：EmberDelivery 币扣款打 REG 标签 + E1 `ember-v1-economy.yml` 镜像防漂移 |
| 版本 | CoreRpg **1.65.51**；`balance_version` **57**（数量不变） |
| 代码 | `spendCoinDelivery` / `sinkForDeliveryRequest`；Delivery 负额走登记表；`economyYmlDrift` + 启动校验；`EmberDelivery` 出 takeCoin 白名单 |
| 不变 | bv57；所有数量；未改 p1sim / 技能；yml **不是** `amount()` 真源 |
| 测试 | unit（见 COORD）；冒烟 FreshQ784+ |
| 文档 | `STATUS-ember-economy-s2-6-1.65.51.md`；设计稿 E1 状态更新 |
