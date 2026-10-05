# STATUS · ARCH S2-6 经济路由（CoreRpg 1.65.51 / D223）

## 本窗新路由

| REG | 路径 | 怎么走 |
|---|---|---|
| EmberDelivery 负额扣币 | `EmberDelivery.mat` kind=coin amount&lt;0 | `EmberEconomy.spendCoinDelivery(pd, request, n)` → `sinkForDeliveryRequest`（`enh:`→C03 … `imp:`→C12；`refund:` 前缀剥掉）→ `spendCoin`；未映射则 helper 内 `takeCoin` |
| — | 扫描 | `EmberDelivery` 移出 `takeCoin` 白名单（已无直接 `takeCoin`） |
| E1 yml 镜像 | `ember-v1-economy.yml` | 从 `EmberEconomy.golden` 导出；`economyYmlDrift` 双钉（缺文件=回退；有文件必须与 golden 一致）；`amount()` **仍读 Java golden**（yml 尚未真源）；启动时 WARNING on drift |

## 已路由（累计）

来源：S01–S06 / S20–S25 settle·签到·在线·挂机；消耗：C03–C14、C18；Delivery 负额扣币标签。

## 未做 / 下一刀

- E1 迁移：yml 作 `amount()` 真源（需独立迁移窗 + 回归）
- C15 外观（暂停）；yml→p1sim 同读（E3）
