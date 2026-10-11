# Ember 房间事件 W3 kind 权重（D12208，2026-10-11）

> 状态：**CoreRpg 1.65.726-d12208.local / balance_version 91**（routine-1845）。
> 前置：mustfeel R · W2a soft pity · W2b mid-room。授权：timed=0.65（0.6–0.7 中值）。

## 行为

| 项 | 值 |
|----|----|
| yml | 可选 `variety.weights:`（kind → 相对权） |
| 缺省 | 未写的 kind = **1.0**；未知 key 忽略；≤0 跳过；>100 钳到 100 |
| 实装 | `timed: 0.65`；其余 8 种不写 → 1.0 |
| 池 | 仍 9 kinds；**不删** timed |
| 抽法 | `Variety.pickEventKind` 袋装加权；`roll` + SoftPity 同路径 |
| 不动 | event_rate 0.85 / event_core 1 / affix_* / rewards / gates |

## 期望份额

P(timed) = 0.65/(0.65+8) ≈ **0.075**；其它各 ≈ **0.116**。

## 漏洞表

| 风险 | 结论 |
|------|------|
| 奖励变多 | 否 — event_core / 结算不变 |
| 双领 / 叠第二事件 | 否 — SoftPity 仍禁叠 |
| 永久乘区 / 新 kind | 否 |
| Pack5 / 化妆 | 否 |
