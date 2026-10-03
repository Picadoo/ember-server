# STATUS — B2.95 DP EmberAbyss option.yml 注释改体力口径 · 结案

Date: 2026-10-01 02:19 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**

## Tips
- 设计 `37a01ca` · 批准 `e384dab` · 插件 `50a946c` · 测 `3c82e34`
- 报告：`docs/tests/TEST-B2.95-dp-abyss-option-comment.md`

## Acceptance (测岗)
- 仅 EmberAbyss/option.yml L3/L4/L18/L20 4+/4-，与荐案逐字一致；解析前后 same；`text=` L22/L23 与脚本未动
- 无「扣票 / B0.1 / 深渊票 / 日 1 张」；无「B0.1 已清 / 票已废」
- 注释与现行一致：深渊无首免，扣体力 30；未进本 `refundEnter` 退还；进本后撤离走 AbyssSettleService，不碰体力；入口无日次数门；`ticket_convert` 含 ticket_ember_abyss
- 边界（记录，不入注释）：2 秒前下线不退；只扣发起人；DP 拉人超过约 2 秒可能先退后进
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Later candidates（测岗发现，范围外）
- `CoreRpgPlugin.java:1356` 兜底文案「日限 1 次 · 进本扣余烬深渊票」（仅 abyssSettleService 为 null 时）
- 深渊状态页仍显示背包票数（`AbyssSettleService.java:189,196`）
- 三份 option.yml 开本提示 `text=`「已消耗体力 ×1」

## Next
- **B2.96**：DP `EmberRaid/option.yml` 历史注释（含 L30 团票注释）→ B2.97 EmberEliteWeekly
