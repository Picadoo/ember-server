# STATUS · ARCH S2-5 经济路由（CoreRpg 1.65.50 / D221）

## 本窗新路由

| REG | 路径 | 怎么走 |
|---|---|---|
| C18 国庆商店 | `EmberFestival.buy` / `exchange` | 活动期 / 售后足迹 / 纪念称号 / 兑徽：`spendFestCoin("C18")` 校验后 Ni `consumeExact`；售后符：`spendCoin("C18", afterCoin)` 或 `spendBadge("C18", afterBadge)` |
| S22 挂机庭 | `EmberAfkService.settle` → ledger `p1afk-<day>` → `deliver` | `sourceForGrant(…, "p1afk-…")` → S22；币 `grantCoin`、经验 `grantXp`、绑定材料 `grantMat`（BOUND_MAT 认四仓料）；BMAT 交付前同样校验 |
| — | 金样 | C18 charm 60 / trail 30 / after 15000+300 / memo 120 / badge 5:40 钉 `ember-v1-festival.yml` |
| — | 扫描 | `EmberFestival` 移出 `takeCoin` 白名单（已无直接 `takeCoin`） |

## 已路由（累计）

来源：S01–S06 / S20–S25 settle·签到·在线·挂机；消耗：C03–C14、C18。

## 未做 / 下一刀

- E1 `ember-v1-economy.yml` 加载器（D220 草案；本窗跳过 stub，避免半成品）
- EmberDelivery 负额扣币标签化；C15 外观（暂停）；徽记进 p1sim
