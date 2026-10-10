# 状态 · D556：开门路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D556**  
**jar：** `1.65.247-d556.local`  
**实现：** `EmberGatePath` + Director pathForceOpenDoor + door_delay hook + `gatepath`/`gate`/`gateseed`

| 验 | 结果 |
|----|------|
| AUTO | gateseed → 开门·自动 待命/开启 |
| ASK | `[开门]` |
| MUTE | 静默 |
| unit | EmberGatePathTest |

*D556 · 新门扇缓开推进循环（≠ RoomPath 房序 / Pack）。*
