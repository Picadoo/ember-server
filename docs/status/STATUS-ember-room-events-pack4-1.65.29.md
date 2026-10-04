# STATUS · D191 Room Events Pack 4 · CoreRpg 1.65.29

- **状态**：SHIPPED（routine-0745，2026-10-05 07:57 Asia/Shanghai）
- **版本**：CoreRpg 1.65.29 / balance_version 52
- **设计**：`docs/design/DESIGN-ember-room-events-pack4-2026-10-05.md`（参考：PoE Breach / D3 Massacre + 传奇连斩 / PoE Sanctum 决心 / WoW 无伤成就；否决 Ritual / Harbinger / Legion / Delirium / 诅咒箱波次）
- **发布**：`docs/status/RELEASE-ember-1.65.29.md`
- **内容**：房间事件池 6 → 9（+裂隙 `breach` / 连斩 `chain` / 无伤 `unscathed`）；奖励（`event_core` 1）、`event_rate` 0.5、闸门、词缀池不动
- **Java**：EmberRunMaps.Variety（EVENTS 9、参数 + 钳位、中文名、无倒计时）· EmberRunDirector（裂隙放置 / 缩圈 / 圈内击杀撑开 / 合上失败；连斩计数；无伤计数；清房判定；清理）· EmberRunService（`onRunHitTaken` MONITOR 落地命中、账单前缀）· EmberRunRules（源键 `var_event_breach/chain/unscathed`）
- **文案**：TrMenu 图鉴 + 冒险页事件列表 6→9，图鉴加一行三事件说明；`ember-v1.yml` 委托注释
- **单测**：291 / 0（+6 D191）
- **sim 门禁**：`tools/p1sim/eventpack4.py` → `tools/p1sim/out-eventpack4-d191.md` **within range**（新事件 ≤ 参考带；每次事件期望核心 0.969 → 0.952；p2econ 新旧池一致）
- **冒烟**：合批 deferred（新一批未测上线第 1 个；下批须含 D188 撞墙定点检查）——见 `/workspace/COORD-POLICY-testing.txt`
- **资产路径**：未改 → 不需要 persist-roundtrip
