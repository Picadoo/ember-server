# STATUS — B2.94 DP EmberWeekly option.yml 注释改体力口径 · 结案

Date: 2026-10-01 02:11 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**

## Tips
- 设计 `9d20149` · 批准 `4c7e7eb` · 插件 `ef7e350` · 测 `b6501af`
- 报告：`docs/TEST-B2.94-dp-weekly-option-comment.md`

## Acceptance (测岗)
- 仅 EmberWeekly/option.yml L4/L5/L16/L18 4+/4-，与荐案逐字一致；解析前后 same；`text=`/脚本/L20 未动
- 该文件无「扣票 / B0.1 / 余烬周票」；无「B0.1 已清 / 票已废」
- 注释与现行一致：每周首免 → 之后扣体力 45；未进本 `refundEnter` 退还；`ticket_convert` 含 ticket_ember_weekly
- 边界（记录，不算不符）：2 秒检查前下线不退；只扣/退发起者
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Next
- **B2.95**：DP `EmberAbyss/option.yml` 历史注释（一文件一窗）→ B2.96 EmberRaid → B2.97 EmberEliteWeekly
- 之后单开窗：三处开本提示 `text=`「已消耗体力 ×1」与现行不符
