# 状态 · D599：备药路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D599**  
**jar：** `1.65.290-d599.local`  
**实现：** `EmberStockPath` + pathStockCheck + PRE_BOSS_HINT hook + `stockpath`/`stock`/`stockseed`

| 验 | 结果 |
|----|------|
| AUTO | stockseed → 备药·自动 待命/核对 |
| ASK | `[核对]` |
| MUTE | 静默 |
| unit | EmberStockPathTest |

*D599 · 新首领厅预备备药核对战斗获取环（≠ Prep 进城补药 / Sip 低血喝药）。*
