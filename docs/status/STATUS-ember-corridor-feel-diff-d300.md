# 状态 · D300：日刷走廊感轻差异方案 M（W1a+W1b+W1c）

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-corridor-feel-diff-2026-10-07.md`](../design/DESIGN-ember-corridor-feel-diff-2026-10-07.md) 方案 M  
**版本：** CoreRpg **1.65.90** · `balance_version` **60**（未抬）

## 人话

七图合格重打仍像同一条走廊，是因为事件房都挂在 R2、清房后门立即开。本窗**零经济 / 零 Pack**：只改事件挂点、门喘息、菜单节奏短签——不抬掉率、不动 event_rate、不重做地图。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | 七图 `event.after`：Q01 **r1** / Q02 **r2** / Q03 **r3** / Q04 **r2** / Q05 **r1** / Q06 **r3** / Q07 **r2**（至少 4 档不同；邻图挂点叙事不撞） |
| **W1b** | `door_delay`：Q01 r1 **0.8s** · Q03 r2 **1.0s** · Q06 r1 **1.2s** · Q07 r3 **0.9s**；其余图/门缺省 0（立即开门）。Director 清房后延时拆铁栅 +「门扇缓缓开启…」 |
| **W1c** | 冒险 / 挑战 lore 在 D283 名片下加 `§b节奏：…`（与真实挂点/门延迟一致） |
| jar | `EmberRunMaps.Room.doorDelay`；`EmberRunDirector.openDoorNow` + tick 延时开门 |
| 版本 | **1.65.89 → 1.65.90** |

### 七图表

| 图 | event.after | door_delay | 节奏短签 |
|----|-------------|------------|----------|
| Q01 灰烬 | r1 | r1 0.8s | 事件偏早 · 门慢半拍 |
| Q02 焦骨 | r2 | — | 事件居中 |
| Q03 残誓 | r3 | r2 1.0s | 事件偏晚 · 门慢半拍 |
| Q04 潮蚀 | r2 | — | 事件居中 |
| Q05 断塔 | r1 | — | 事件偏早 |
| Q06 霜封 | r3 | r1 1.2s | 事件偏晚 · 门慢半拍 |
| Q07 锈轨 | r2 | r3 0.9s | 事件居中 · 门慢半拍 |

## 不动

- Pack6 / 新 kind / 重做七图 / 刷点拓扑  
- `event_rate` / `event_core` / 事件 R/W / 9 kind  
- 调律 R / 六槽 / 天赋 / 掉率 / 单局币 / W2 房压重排 / 方案 R 钩  
- bv **60** · login/proxy 未停（仅 play 重启换 jar）

## 验收

- play Enabling **1.65.90**；`version CoreRpg` = 1.65.90  
- TrMenu reload 69 菜单；冒险/挑战可见「节奏：…」  
- 单测：`EmberRunRulesTest`（eventAfter + doorDelay）  
- 回滚：`/workspace/backup/CoreRpg-1.65.89-pre-d300.jar` + 还原 `ember-v1-runs.yml` / TrMenu lore

## 下一窗 tip

- **W2**（精英位 / 房压形状轻排）**后置**，等真人是否仍喊「一条廊」  
- **R** 钩表后置；**禁** Pack6 / 事件 R/W / 调律 R / 六槽 / 天赋盲调  
