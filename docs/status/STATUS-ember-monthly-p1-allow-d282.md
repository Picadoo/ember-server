# 状态 · D282：P1 下月卡登录礼照常发

**日期：** 2026-10-07（上海时间）  
**上游：** 业主裁决「月卡照常发」· 回滚 D281 对 `processMonthlyLogin` 的拦截 · CoreRpg **1.65.78** · `balance_version` **58**

## 人话

D281 把月卡进服礼、勋阶日礼、战令补给/等级奖在 P1 下一起挡了。业主要：**月卡照常发**；勋阶日礼与战令领取仍挡。

## 改了什么

| 处 | 改动 |
|---|---|
| `CashService.processMonthlyLogin` | 去掉 P1 拦截 → 币 + 体力照常 |
| `CashCoinRules` | 注释标明只闸 vip/pass；月卡不在此列 |
| vip claim / pass free / pass claim | **仍挡**（D281 保留） |
| 版本 | **1.65.77 → 1.65.78** |

## 验收

- play Enabling **1.65.78**；login/proxy 不停
- 单测 `CashCoinRulesTest` 仍过（规则本身未改）

## 不变

- 六槽 / S0-9 不动
- LegacyGate 命令路由不变
