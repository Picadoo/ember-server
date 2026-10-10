# STATUS · D623 房间事件软保底 · 2026-10-11

| 项 | 状态 |
|----|------|
| 设计 | mustfeel §3.2 W2a · 已批 A·M |
| 实现 | `EmberEventSoftPity` + Session start ensure + Settle onSettle |
| 单测 | `EmberEventSoftPityTest` |
| jar | **1.65.312-d623** · bv 未抬 |
| W2b | **HOLD**（本窗不做） |

行为：合格花样重打连续 1 局无事件房 → 下一局强制抽事件（不叠第二件）。
