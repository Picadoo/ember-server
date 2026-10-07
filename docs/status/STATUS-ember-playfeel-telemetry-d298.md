# 状态 · D298：玩法可感轻量遥测方案 M（W1a+W1b+W1c+旁路 R）

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-playfeel-telemetry-2026-10-07.md`](../design/DESIGN-ember-playfeel-telemetry-2026-10-07.md) 方案 M  
**版本：** CoreRpg **1.65.88** · `balance_version` **60**（未抬）

## 人话

破绽/事件/签名调律已有反馈，但 OP 看不清周样本。本窗只做**证据基建**：合格重打结算写入周计数、admin 只读摘要、全服周合计（排除测试号）、可选 JSON 日志行——无玩家看板、不改玩法数值。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | q01–q07 日刷/挑战结算（通关+失败）落盘 `p1_pf_*` 周 `periodCount`（§3 九键含分母 `p1_pf_runs`） |
| **W1b** | `/corerpg p1 telemetry [玩家\|server]` · `corerpg.admin` 只读 ≤15 行（本周+上周） |
| **W1c** | 内存全服周聚合（排除 `leaderboard_exclude` + `telemetry.exclude_uuids`）；每 25 局 / `telemetry server` 打日志并写 `plugins/CoreRpg/p1-telemetry/<week>.yml` |
| **R** | 结算旁路 `[P1 pf] {json…}`（`telemetry.json_log` 默认 true；uuid 短哈希） |
| 注册表 | `EmberCounters`：`p1_pf_*` + 补登记 D296 `p1_evteach_` / D297 `p1_attuneprompt` |
| 配置 | `ember-v1.yml` → `telemetry.json_log` / `exclude_uuids` |
| 版本 | **1.65.87 → 1.65.88** |

## 不动

- 玩家面看板 / 成就 / 排行  
- `event_rate` / ALTS / 掉落 / 六槽 / 天赋  
- 事件 R/W · 调律 R

## 验收

- play Enabling **1.65.88**；login/proxy 未停  
- `rg 'p1_pf_|EmberPlayfeelTelemetry' CoreRpg/`  
- 单测：`EmberPlayfeelTelemetryTest` · `EmberCountersTest`  
- 回滚：`/workspace/backup/CoreRpg-1.65.87-pre-d298.jar`

## 下一窗 tip

- 攒 1～2 周真人样本后再议：事件 R/W、调律 R、是否扩 Pack  
- 禁：玩家面遥测 KPI · 六槽 · 天赋盲调 · Pack6  
- **下一档硬债 tip（非遥测续窗）：** [`STATUS-ember-next-hard-debt-refarm-short-feedback-need-design-2026-10-07.md`](STATUS-ember-next-hard-debt-refarm-short-feedback-need-design-2026-10-07.md) · 再刷短反馈 / 成色·精工可感节奏 · **需策划**（对齐加固债 #3）
