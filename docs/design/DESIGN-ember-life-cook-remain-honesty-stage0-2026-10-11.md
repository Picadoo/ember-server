# DESIGN · 生活/烹饪剩余诚实 Stage0 · 2026-10-11

> **状态：** Stage0 + **D1543– / D1571– SHIP** · 已批 A · 方案 M  
> **硬禁：** 无 K3/SF4 / hope PAPI / AFK raise / 改掉率 / skill_mult

## 问题
成套/周标/路径/技能/专精诚实面近饱和。生活线（烹饪/买料）缺「还差币/料 / 下一可做」肉感。

## 方案 M（薄 · 待批）
| ID | 项 | 改动 |
|----|----|------|
| LF1 | cook 缺料/币 AB | LifeService cook preview |
| LF2 | buy 还差币 | buy confirm AB |
| LF3 | 饱食/就绪下一餐 | 非战斗表 · 仅展示 |
| LF4 | 生活经验还差 | addXp/show AB |
| LF5 | 铁砧还差币 | anvil take |
| LF6 | 周/日帽·等级闸 AB | buy fail |
| LF7 | show 还差次数 | offer 行 |
| LG1 | 账本补领 AB | LedgerGlance/claim |
| SU1 | 备药缺币/满格 | Supply topUp |
| CP1 | 补领路径 AB | ClaimPath |
| ST1 | 体力药帽还差 | StaminaService |
| BK1 | 取银路径银行数 | BankPath |

不改产量、价格表、AFK。
