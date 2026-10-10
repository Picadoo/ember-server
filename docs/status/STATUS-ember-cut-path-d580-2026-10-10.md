# 状态 · D580：打断路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D580**  
**jar：** `1.65.271-d580.local`  
**实现：** `EmberCutPath` + pathCutRegen + regen window hook + `cutpath`/`cut`/`cutseed`

| 验 | 结果 |
|----|------|
| AUTO | cutseed → 打断·自动 待命 |
| ASK | `[打断]` |
| MUTE | 静默 |
| unit | EmberCutPathTest |

*D580 · 新再生打断战斗获取环（≠ Rush 限时 / Streak 连斩 / telegraph 拉开）。*
