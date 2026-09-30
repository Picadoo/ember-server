# STATUS — B2.102 TrMenu ember_hub.yml L249+L250 精英 lore · 结案

Date: 2026-10-01 03:31 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**（reload 实测：待恢复服后实测，SKIP 不挡）

## Tips
- 设计 `4045245` · 批准 `6c2913a` · 插件 `862d21c` · 测 `0884f55`
- 报告：`docs/TEST-B2.102-hub-elite-lore.md`

## Acceptance (测岗)
- 仅 ember_hub.yml L249/L250 2+/2-，与荐案逐字一致（8 空格缩进）
- `/tmp/chk-b2102.js 862d21c` → only-L249-L250（旧对旧/新对新 exit 1）；独立 diff 仅 `/Icons/2/display/lore/1`、`/lore/2`
- 旧文案与扣费字样 rg 0 命中；`stamina_cost_elite` 恰在 L235/L240/L250
- 占位符：identifier `corerpg`（`CoreRpgExpansion.java:10`）→ `:103` `costOf("elite")` → cash.yml `stamina.costs.elite` = 40；图标 update/refresh 20
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Corrections (不影响结论)
- 设计稿「StaminaService L110-119」应为 reload `:96` 起、costs `:119-124`
- 稿 §4 脚本带 markdown 缩进，落 /tmp 前需去缩进；其忽略空对象/数组变化，由独立 diff 补

## Later candidates
- **B2.114**：`plugins/CoreRpg/quest.yml:389` hint「精英试炼（每周 1 次）」（src 模板同句可不排）
- B2.109 追加：主城精英图标 lore 断言

## Next
- **B2.112**：DP `EmberEliteWeekly/task/timeout.yml` L6「体力已扣，下周再来」→ 策划出稿
