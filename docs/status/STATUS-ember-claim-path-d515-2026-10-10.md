# 状态 · D515：补领路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D515**  
**版本：** jar **1.65.206-d515.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberClaimPath` · Settle `settleDeliver` · ambient join/town · Q01 offer @285L · `/corerpg p1 claimpath` |
| 禁 | 未改奖励表 · 无 ActionBar · 非日委/仓库双胞 · 非组合粘性/工坊/入场 cue |

## 冒烟 FreshQ1051

| 路径 | 结果 |
|------|------|
| hold/brief/auto UX | 设置文案 **PASS** |
| hold + pending join | 不自动到账 · `补领·暂存：有 1 项待领` **PASS** |
| `/corerpg p1 claim` | `结算到账：余烬币 7` **PASS** |

单元：`EmberClaimPathTest` hold/brief PASS。

*D515 · 新补领到账打法。*
