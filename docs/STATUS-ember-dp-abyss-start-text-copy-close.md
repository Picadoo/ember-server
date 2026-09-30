# STATUS — B2.100 DP EmberAbyss option.yml L22 开本提示去扣费句 · 结案

Date: 2026-10-01 03:08 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**（reload 实测：待恢复服后实测，SKIP 不挡）

## Tips
- 设计 `0ef491e` · 批准 `2033207` · 插件 `252fe8e` · 测 `c3d662c`
- 报告：`docs/TEST-B2.100-dp-abyss-start-text.md`

## Acceptance (测岗)
- 仅 EmberAbyss/option.yml L22 1+/1-：旧行仅删「已消耗体力 ×1 —— 」27 字节，其余字节不变，与荐案逐字一致
- 独立 deep diff：仅 `dungeon-start.action-script[0]` 变化，列表 5 项、顺序不变
- 收窄 rg 0 命中；B0.1 已清/票已废 0
- 「12」= 现行上限：`monster.yml` floor12 COMPLETE、`AbyssSettleService.java:33`、`AbyssShaftService.java:42-47`
- 入口 `corerpg enter abyss` → `tryEnter(ABYSS)` → 私聊；深渊不走周首免，仅「管理免扣 / 体力 -30」；`abyss-calamity-supplement.js:66` 的「深渊已开启」仍在
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Corrections (不影响结论)
- 稿内 js-yaml 命令未校验改前原文（新对新也 only-L22）；今后须 `<build>^` 对 `<build>` 并断言旧值
- 设计稿 §6 称 `ember_abyss.yml:77` 不带费用有误：其硬编码「体力 30」

## Later candidates
- **B2.111**：`TrMenu/menus/ember_abyss.yml:77` 硬编码「体力 30」（排 B2.110 后；另文件另开窗）
- B2.109 追加：`dungeon-balance.js:16-17`、`abyss-reload-smoke.js:17-18`、`abyss-calamity-supplement.js:58,62`、`gates-smoke.js:22-23`、`ticket-grant-smoke.js`

## Next
- **B2.101**：DP `EmberEliteWeekly/option.yml` L20「本周只有一次」→ 每人每周通关 1 次口径 → 策划出稿
