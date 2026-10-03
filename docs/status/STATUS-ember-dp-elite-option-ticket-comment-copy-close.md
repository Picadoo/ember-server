# STATUS — B2.97 DP EmberEliteWeekly option.yml 注释改体力口径 · 结案

Date: 2026-10-01 02:36 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**

## Tips
- 设计 `aeb2bc1` · 批准 `810f0cd` · 插件 `79257fd` · 测 `8e12ca3`
- 报告：`docs/tests/TEST-B2.97-dp-elite-option-comment.md`

## Acceptance (测岗)
- 仅 EmberEliteWeekly/option.yml L3/L4/L16/L18 4+/4-，与荐案逐字一致；解析前后 same；`text=`（含 L20）与脚本未动
- 无「扣票 / B0.1 / 精英票 / 有票 / 持有上限 / 开放 / 上线」；无「B0.1 已清 / 票已废」
- 注释与现行一致：`%corerpg_gate_elite%` 只判 Lv≥40 + 本周未通关；发起者每周首免 → 之后扣体力 40；预检不符不扣；未进本退还；通关标记每人每周 1 次
- 更正（不影响结论）：菜单入口 `corerpg elite start` 与 `corerpg enter elite` 同入 tryEnter；level_gates 段缺 elite 键时门槛为 0（live 已配 40）
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Milestone
- DP `dungeon/*/option.yml` 历史「扣票 / B0.1」注释四份（Weekly / Abyss / Raid / EliteWeekly）本轨清零（B2.94–B2.97）

## Next
- **B2.98**：策划对票时代旧文案余项排序后出稿（候选见 backlog L20）
