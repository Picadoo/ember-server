# 状态 · D296：房间事件「本局必感」方案 M（W1a+W1b+W1c）

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-room-event-mustfeel-2026-10-07.md`](../design/DESIGN-ember-room-event-mustfeel-2026-10-07.md) 方案 M  
**版本：** CoreRpg **1.65.86** · `balance_version` **60**

## 人话

合格重打约一半局不出房间事件；开房只有长 chat。本窗把出事件率抬到 0.85，并给每种事件账户首次短闪 + 每局首次事件房 ActionBar 短名——不扩 Pack、不抬核心张数。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | `variety.event_rate` **0.5 → 0.85**（双路径 `ember-v1-runs.yml`）；`balance_version` **59 → 60**；economy / affix-table 同步 60 |
| **W1b** | `EmberEventTeach`：`p1_evteach_<kind>` 账户种首次短闪（§3.4 动词）；开房时 ActionBar+标题 |
| **W1c** | 每局首次事件房 ActionBar「本房事件：〈短名〉」（`eventHudShown`） |
| 版本 | **1.65.85 → 1.65.86** |

## 不动

- Pack6 / 新 kind / `event_core`（仍 **1**）
- 六槽 · 天赋 · 首通/挑战/深渊/团本/连战开事件
- 方案 R 软保底 / W3 权重（续窗）

## 门禁 tip

| 项 | 结果 |
|---|---|
| 单测 | `EmberEventTeachTest` MC 出事件率 ∈ [0.80, 0.90]；`event_core==1`；种首次只记一次 |
| 期望核心 | MC：出事件率 ≈0.85；E[S05 core/重打]@70%成功 ≈ **×1.68**（相对 0.5）；**未**抬 `event_core` |
| 全量 p2econ | **PASS** · [`STATUS-ember-d296-event-rate-econ-tip.md`](STATUS-ember-d296-event-rate-econ-tip.md) · `out-d296-event-rate-econ.md`（Δ两件极品 max +4pp · E[core] ×1.72） |

## 验收

- `rg 'event_rate:' plugins/CoreRpg/ember-v1-runs.yml` → `0.85`
- `rg 'balance_version:' plugins/CoreRpg/ember-v1-runs.yml` → `60`
- `rg 'p1_evteach_|EmberEventTeach|eventHudShown' CoreRpg/`
- play Enabling **1.65.86**；login/proxy 未停
- 回滚：`/workspace/backup/CoreRpg-1.65.85-pre-d296.jar`

## 下一窗 tip

- 方案 **R**（软保底 / 中段房偏好）仅当真人仍喊空廊  
- **W3** timed 降权须另批数字  
- 禁：Pack6 / 六槽 / 天赋盲调 / 纯 lore
