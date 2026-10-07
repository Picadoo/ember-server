# 状态 · D279：P1 下竞技场发币纵深关闭

**日期：** 2026-10-07（上海时间）  
**上游：** ARCH R6「竞技胜场币未关」· CoreRpg **1.65.76** · `balance_version` **58**

## 人话

普通玩家本来就不能进竞技场命令（S0-3）。但只要有人对战结算或领日箱，代码仍会发余烬币。P1 经济不建模这条来源 → 本窗在**发币端**再堵一层：P1 开着时胜场/参与/日箱币一律 0。

## 改了什么

| 处 | 改动 |
|---|---|
| `ArenaCoinRules.p1BlocksCoin` | 纯规则：P1 开 → true |
| `ArenaService.payCoin` | P1 开 → 直接 0 |
| `ArenaService.cmdClaim` | P1 开 → 拒领币并人话提示 |
| 单测 | `p1BlocksCoinWhenModeOn` |

## 验收

- `mvn -o test -Dtest=ArenaCoinRulesTest` 通过
- play Enabling **1.65.76**；login/proxy 不停

## 不变

- 积分 / 胜负记录逻辑不删；只是不发币
- 六槽 / S0-9 / 票文案不动

## 下一扇候选

- 其它 R6 未核项（图腾 NMS 是否在 P1 仍耗图腾等）
- 或有实机证据的玩法问题
