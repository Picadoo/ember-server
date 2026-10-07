# 状态 · D297：签名调律决策密度方案 M（W1a+W1b+轻量 W1c）

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-sig-attune-decision-2026-10-07.md`](../design/DESIGN-ember-sig-attune-decision-2026-10-07.md) 方案 M  
**版本：** CoreRpg **1.65.87** · `balance_version` **60**（未抬；顺修 D296 漏同步的 `ECONOMY_BV` 58→60）

## 人话

调律版解锁后，城内进本路径看不见「本局用原版还是调律」。本窗只加城内一眼 + 本周首次轻量确认——不改 ALTS / 烙印价 / 掉率 / event_rate，不做局内热换。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | 冒险/挑战图 lore：Q07 开且有生效签名时 `%corerpg_p1_sig_run%` / `run_cost`（本局生效：刃·原版\|调律 · 护符·… + 短代价） |
| **W1b** | 装备摘要 / 签名页穿着行：`wornLine` 标 ·原版/·调律；`blade_o`/`charm_o` 另一版代价一行 |
| **W1c** | 本周首次进 q01–q07（日刷/挑战）且至少一件已解锁调律 → `ember_p1_sig_runconfirm`（去调律 / 就这样进本 / 取消）；`p1_attuneprompt` 周计数，同周不重复 |
| jar | `EmberSigAttunePreview`；`EmberGrowthService` PAPI + `maybeAttunePrompt`；`EmberRunService.tryEnter` 钩子 + `tryEnterAfterAttuneConfirm` |
| 顺修 | `EmberEconomy.ECONOMY_BV` **58→60**（与 yml bv60 对齐；D296 漏改导致停服后 enable 失败） |
| 版本 | **1.65.86 → 1.65.87** |

## 不动

- ALTS 数值 · 烙印价 · 掉率 · `event_rate` · 六槽 · 天赋 · 战斗热换（方案 W）
- Pack6 / 房间事件 R/W · 深渊/团本进本确认

## 验收

- play Enabling **1.65.87**；login/proxy 未停
- TrMenu 有 `ember_p1_sig_runconfirm`
- `rg 'p1_sig_run|maybeAttunePrompt|EmberSigAttunePreview' CoreRpg/`
- 单测：`EmberSigAttunePreviewTest` · `EmberEconomyTest` · `EmberSettleServiceTest`
- 回滚：`/workspace/backup/CoreRpg-1.65.86-pre-d297.jar`

## 下一窗 tip

- 方案 **R**（更强对照 / 推荐）后置，待真人是否仍「只挂原版」
- 禁：局内热换 · 六槽 · 天赋盲调 · Pack6 · 纯 lore
