# STATUS — B2.101 DP EmberEliteWeekly option.yml L20 开本提示改每人每周限通关一次 · 结案

Date: 2026-10-01 03:19 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**（reload 实测：待恢复服后实测，SKIP 不挡）

## Tips
- 设计 `86d83c1` · 批准 `910d284` · 插件 `e55540a` · 测 `713b407`
- 报告：`docs/tests/TEST-B2.101-dp-elite-start-text.md`

## Acceptance (测岗)
- 仅 EmberEliteWeekly/option.yml L20 1+/1-：「本周只有一次」→「每人每周限通关一次，没过可以再来」，与荐案逐字一致
- js-yaml `B^` 对 `B`，断言旧值/新值/长度 3 → only-L20（旧对旧 exit 4、新对新 exit 3，能发现漏改）
- 收窄 rg 0 命中；B0.1 已清/票已废 0
- 口径：`EliteService.java:40-47` 门槛；通关标记仅 COMPLETE 逐人写入（`ProgressService.java:471-481`）；失败不写（timeout.yml reward=false），同周可再进（首免后扣 40 体力）；无过度承诺
- 入口 `corerpg elite start` / `corerpg enter elite` 均至 `tryEnter` → 私聊；无 mineflayer 依赖旧 L20
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Corrections (不影响结论)
- 「DP 检查每个成员、团灭/撤出不写标记」依据为实服证据与唯一 reward=true 出口，非 DP 源码
- 「每人每周最多一份奖励」对 OP 有例外

## Later candidates
- **B2.112**：`EmberEliteWeekly/task/timeout.yml:6`「体力已扣，下周再来」与现行矛盾（排 B2.102 后）
- B2.109 追加：精英本 mineflayer 覆盖缺失；`quest-vol2-4.5-smoke.js:78` 写精英通关标记

## Next
- **B2.102**：`TrMenu/menus/ember_hub.yml` L249+L250 精英 lore → 策划出稿
