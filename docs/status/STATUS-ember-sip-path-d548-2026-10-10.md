# 状态 · D548：喝药路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D548**  
**jar：** `1.65.239-d548.local`  
**实现：** `EmberSipPath` + Supply needsSip/pathSipOne + Set HUD hook + `sippath`/`sip`/`sipseed`

| 验 | 结果 |
|----|------|
| AUTO | sipseed → 自动喝药 |
| ASK | `[喝药]` |
| MUTE | 静默（抑低血条） |
| unit | EmberSipPathTest |

*D548 · 新副本战斗续航环（≠ Prep/Brew/Bite/生活店）。*
