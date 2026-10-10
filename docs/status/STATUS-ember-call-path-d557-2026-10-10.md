# 状态 · D557：召战路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D557**  
**jar：** `1.65.248-d557.local`  
**实现：** `EmberCallPath` + Director pathCallBoss + bossAt arm hook + `callpath`/`call`/`callseed`

| 验 | 结果 |
|----|------|
| AUTO | callseed → 召战·自动 待命/现身 |
| ASK | `[召战]` |
| MUTE | 静默 |
| unit | EmberCallPathTest |

*D557 · 新首领预备召战循环（≠ GatePath 开门 / Pack）。*
