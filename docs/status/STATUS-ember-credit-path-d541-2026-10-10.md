# 状态 · D541：周免路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D541**  
**jar：** `1.65.232-d541.local`  
**实现：** `EmberCreditPath` + consumeForEnter preferCredit + join tip + `creditpath`/`creditseed`/`creditprobe`

| 验 | 结果 |
|----|------|
| SPEND | creditprobe → 用了免费抵扣 |
| HOLD | creditprobe → 扣体力（有体力时） |
| ASK | creditseed → 进服提醒按钮 |
| unit | EmberCreditPathTest |

*D541 · 新周本免费抵扣经济环。*
