# STATUS · D12206 房间事件中段房偏好（W2b）· 2026-10-11

| 项 | 值 |
|----|----|
| 设计 | mustfeel R · W2b（`DESIGN-ember-room-event-mustfeel-2026-10-07.md`） |
| 行为 | 事件房抽签权重 **r2:r1:r3 = 2:1:1**；词缀房仍均匀；软保底 `ensureEvent` 同权重 |
| 禁 | Pack6 · 第二事件 · 抬 event_rate/core · 新乘区 · bv |
| jar | **1.65.724-d12206.local**（live Enabling + `version CoreRpg`） |
| 单测 | `EmberEventRoomWeightTest` + `EmberEventSoftPityTest` PASS（JDK8） |
| MySQL | CoreRpg + CoreGacha connected · SEVERE 0 · TPS~20 · MV idle unloaded |
| 冒烟 | **defer**：botd 缺 `mineflayer` 模块；POLICY 合批；无资产路径改动故无 persist-roundtrip |
| backup | `/workspace/backup/CoreRpg-1.65.723-pre-1.65.724-d12206.jar` |
