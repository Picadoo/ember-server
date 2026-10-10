# 状态 · D561：解冻路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D561**  
**jar：** `1.65.252-d561.local`  
**实现：** `EmberThawPath` + Director pathThawFrost + frost apply hook + `thawpath`/`thaw`/`thawseed`

| 验 | 结果 |
|----|------|
| AUTO | thawseed → 解冻·自动 已解除/待命 |
| ASK | `[解冻]` |
| MUTE | 静默 |
| unit | EmberThawPathTest |

*D561 · 新寒霜解冻战斗循环（≠ DuckPath 闪爆）。*
