# 状态 · D554：撤离路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D554**  
**jar：** `1.65.245-d554.local`  
**实现：** `EmberFleePath` + RunService pathFleeLeave + onRespawn hook + `flepath`/`flee`/`fleeseed`

| 验 | 结果 |
|----|------|
| AUTO | fleeseed → 撤离·自动 待命/离本 |
| ASK | `[离本]` |
| MUTE | 静默 |
| unit | EmberFleePathTest |

*D554 · 新副本倒下后撤离循环（≠ Fail/Refund / Chest）。*
