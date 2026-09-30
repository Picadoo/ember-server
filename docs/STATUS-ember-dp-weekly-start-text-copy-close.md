# STATUS — B2.98 DP EmberWeekly option.yml L20 开本提示去扣费字样 · 结案

Date: 2026-10-01 02:45 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**（reload 实测：待恢复服后实测，SKIP 不挡）

## Tips
- 设计 `58103e9` · 批准 `baccdf4` · 插件 `94d8759` · 测 `b4cec8c`
- 报告：`docs/TEST-B2.98-dp-weekly-start-text.md`

## Acceptance (测岗)
- 仅 EmberWeekly/option.yml L20 1+/1-，与荐案逐字一致（4 空格缩进，无 CR/尾随空格）
- js-yaml `only-L20`：仅 `dungeon-start.action-script[0]` 变化，列表长度仍为 3
- 文件内「×1 / 已消耗体力 / 消耗 / B0.1 已清 / 票已废」0 命中
- `TicketEntryService.java:149` 仍私聊发起者「正在进入……（costHint）」；新 L20 广播全队不含费用、不重复
- `stamina-s0-smoke.js` 只靠私聊判定（L111/L120），不依赖 L20
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Corrections (不影响结论)
- costHint 共 4 种：:140 管理免扣 / :142 本周首次免费 / :144 体力 -N / :146 无消耗（周本费用 45，当前走不到）

## Later candidates
- B2.109：`gates-smoke.js:26-30` 仍断言扣 `ticket_ember_weekly`；`stamina-s0-smoke.js:120` 仅凭「正在进入」判定
- 只记不排：七个日本 option.yml「已消耗体力」（`stamina-s0-smoke.js:64-65` 依赖）

## Next
- **B2.99**：DP `EmberRaid/option.yml` L22 仅扣费提示一行 → 策划出稿（删行须证 DP 不依赖行序）
