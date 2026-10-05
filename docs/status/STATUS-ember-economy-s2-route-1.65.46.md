# STATUS · ARCH S2-2 经济路由（CoreRpg 1.65.46 / D215）

## 已路由

| REG | 路径 | 怎么走 |
|---|---|---|
| S01 | 主线 / 挑战 / 深渊结算基线 | EmberRunRules.settle 用 EmberEconomy.amount(S01, …)；币发放 grantCoin(…, S01, …) |
| S02 | 宝箱怪币 | settle amount(S02, coin) + grantCoin via extra_treasure_* |
| S03 | 精英房碎片 / 核心 | settle amount(S03, …)；材料发放入口未改（本条主要是材料） |
| S06 / S20 / S21 / S25 | 首通 / 委托 / 花样委托 / 勋记 **币** | 数量仍来自 yml / 公式；发放键映射后走 grantCoin |
| C14 | 回复药购买 | EmberSupplyService.price() = amount(C14, coin)；扣币 spendCoin(…, C14, …) |

## 未路由（下一切）

- 签到 / 在线（S23 / S24）数量与发放
- 材料 / 印记 / 徽记 / XP / 体力的统一 grant/spend
- 工坊消耗（C03–C06）、深渊层费（C08）等
- yaml 真源 ember-v1-economy.yml（REG §6.3）

## 后撤步

仅设计备忘（DESIGN-ember-skill-kit-2026-10-06.md §4b）：无套装亲和 / 菜单选型定稿前不写代码，不发明亲和。
