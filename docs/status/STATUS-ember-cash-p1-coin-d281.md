# 状态 · D281：P1 下旧商城/战令/月卡发币纵深关闭

**日期：** 2026-10-07（上海时间）  
**上游：** AUDIT L4 / L8 / L10 · 月卡 `onJoin` 直发绕过 LegacyGate · CoreRpg **1.65.77** · `balance_version` **58**

## 人话

S0-3 已经挡住玩家敲 `/corerpg vip|pass free|claim|monthly`。但月卡登录礼走的是**进服自动发**（`CashService.onJoin` → `processMonthlyLogin`），不经命令闸。谁手里有有效月卡（例如管理员发过晶钻买过），P1 开着仍会每天 +200 币 + 体力。勋阶日礼 / 战令补给同理：OP 或以后重开入口还会发。本窗在**发奖端**再堵一层。

## 改了什么

| 处 | 改动 |
|---|---|
| `CashCoinRules.p1BlocksCoin` | 纯规则：P1 开 → true |
| `CashService.processMonthlyLogin` | P1 开 → 不发币、不发体力（不写 lastGrantDate，关 P1 当日仍可补发） |
| `CashService.cmdVipClaim` | P1 开 → 拒领 |
| `CashService.cmdPassFree` | P1 开 → 拒领 |
| `ProgressService.cmdPassClaim` | P1 开 → 拒领 |
| 单测 | `p1BlocksCoinWhenModeOn` |
| 版本 | **1.65.76 → 1.65.77** |

## 验收

- `mvn -o test -Dtest=CashCoinRulesTest`（JDK8）通过
- play Enabling **1.65.77**；login/proxy 不停

## 不变

- 六槽 / S0-9 / 票文案不动
- 不改养成数值；P1 主线 / 签到 / 挂机庭发币不变
- 命令路由 LegacyGate 仍在；本窗只补发奖纵深

## 下一扇候选

- Flex 冲刺：实为 P1 技能组设计内，ARCH R6 措辞待 sync（非 bug）
- 盾牌：BLOCKING 已在 EmberCombatListener 清零；可选 CoreCombat 拒选（纵深）
- 或有实机证据的玩法问题
