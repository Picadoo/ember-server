# 状态 · D295：失败路径也发「本局摘要」（玩法反馈）

**日期：** 2026-10-07（上海时间）  
**上游：** D283 本局摘要仅 settle 通关路径 · DESIGN playfeel §1A 缺口（死在 Boss 只见失败行）· CoreRpg **1.65.85**（**bv 不变 59**）

## 人话

通关才看得到「本局：撞墙 ×N · 破招 ×N · 词缀✔ · 事件✘」，翻车时最需要这行课却没有。本窗把同一摘要挂到 `fail()`，wipe / 超时失败也能复盘。

## 改了什么

| 处 | 改动 |
|---|---|
| `EmberSettleService.failPlayfeelLine` | 从 Session 计数调 `playfeelSummary`（null/空计数 → `""`） |
| `EmberRunService.fail` | 挑战提示之后、`endInstance` 之前，对在线参与者发本局摘要 |
| 单测 | `failPlayfeelLine_D295_usesSessionCounters`；`bundledBalanceVersion` 钉 **59**（D293） |
| 版本 | **1.65.84 → 1.65.85**（**不**动 `balance_version`） |

## 不动

- 六槽 · 天赋一排 · Pack · lore 债 · 掉落 / 退还公式 · 破绽计数本身
- `playfeelSummary` 文案格式（与 D283 通关行同口径）

## 验收

- `rg 'failPlayfeelLine|D295' CoreRpg/src/main/java/town/sunshine/corerpg/p1/`
- 单测 `EmberSettleServiceTest`（含 D295）PASS
- play Enabling **1.65.85**；MySQL connected；login/proxy 未停
- 回滚：`/workspace/backup/CoreRpg-1.65.84-pre-d295.jar`

## 下一窗 tip（手感优先 · 禁六槽/天赋盲调/Pack/纯 lore）

| 候选 | 性质 | 注 |
|---|---|---|
| 副手 §7 已批未做中窗（若有） | 已批 | 方案 A 诚实半行已 D289+D294；再开须总控点名非文案窗 |
| 真实 bug / wipe 复盘后的破绽教学闪提示缺口 | 玩法反馈 | 需策划点哪一招种仍弱 |
| balance 已有模拟可施工 | 数值 | 一排 HOLD；无新 sim 勿盲调 |
