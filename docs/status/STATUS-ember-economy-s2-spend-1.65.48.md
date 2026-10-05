# STATUS · ARCH S2-4 经济消耗路由（CoreRpg 1.65.48 / D218）

## 本窗新路由（消耗）

| REG | 路径 | 怎么走 |
|---|---|---|
| C03 强化 | `EmberForgeService.enhance` → `payDurable(kind)` | `EmberPay.Price.of(cost).at(sinkForForge("enhance"))`；币 `spendCoin(C03)`，碎片/核心 `spendMat(C03)` 校验后由 Ni 扣 |
| C04 升阶 | `simple("upgrade")` | 同上，`C04`（碎片/核心/胚料/币） |
| C05 精工 | `simple("refine")` | 同上，`C05`（胚料/骨尘/币） |
| C06 成色 | `simple("quality")` | 同上，`C06`（胚料/骨尘/币） |
| C07 8 印记兑换 | `EmberRunService.redeemDurable`（MySQL）+ YAML 旧路径 | 数量 `EmberEconomy.amount("C07","marks")`（金样 = `MARKS_PER_EXCHANGE` 8）；扣 `spendMark(C07)` |
| C08 深渊层费 | `EmberRunService` 进本预留 | 币 `spendCoin(C08)`；币不够时 T3 印记 `spendMark(C08, 3, n)`（feeMarks 仍留兑换储备）；退回路径不变 |
| C09 天赋学习 | `EmberGrowthService.pick` | `spendCoin(C09)`；行币仍由 growth yml 驱动，金样 row1/2/3 = 800/2000/4000 钉 yml |
| C10 天赋重置 | `EmberGrowthService.reset` | `spendCoin(C10)`；金样 respec_coin 2000 钉 yml |
| C11 洗练（邻） | `EmberGrowthService` reroll | `Price.at("C11")` |
| C12 烬炉烙印（邻） | `EmberGrowthService` imprint | `Price.insignia(...).at("C12")` → `spendCoin` + `spendInsignia(C12)` |
| C13 签名调律（邻） | 调律解锁 | `spendInsignia(C13, map, ALT_MARKS)` |
| — | 扫描 | `EmberEconomyTest.p1TakeCoinIsEconomyOrAllowlisted`：`p1/` 内新 `.takeCoin(` 必须走 `spendCoin` 或在白名单 |

`EmberPay.take` 在扣任何东西之前先跑 `Price.sinkRefusal()`：标签行不收的账户（例如 C03 收胚料）→ 整单不扣、log `[P1 pay] registry refused`。`everyWorkshopCostIsTakenByItsSink` 把每一档强化/升阶/精工/成色价都过一遍，保证上线不会误拒。

## 已路由（累计）

来源：S01–S03 settle 数量；S06/S20/S21/S25 币；S23/S24；mat/mark/XP 校验入口。
消耗：C03–C14（C14 药价+扣币 D215；C03–C13 本窗）。

## 未路由（下一切）

- C15 外观（暂停，OUT）、C18 国庆商店、EmberDelivery 负额扣币（白名单）
- 撤销分解（undo）= 退回 S29 产出，不是消耗，保持不打标签
- C01/C02 体力（StaminaService），C16/C17（CoreGacha / life）
- S22 挂机庭发放入口统一；yaml 真源 `ember-v1-economy.yml`（REG §6.3）；徽记进 p1sim
