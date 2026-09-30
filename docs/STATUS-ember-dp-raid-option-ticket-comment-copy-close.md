# STATUS — B2.96 DP EmberRaid option.yml 注释改体力口径 · 结案

Date: 2026-10-01 02:27 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**

## Tips
- 设计 `5552822` · 批准 `273a4f6` · 插件 `179a7a9` · 测 `d62e20c`
- 报告：`docs/TEST-B2.96-dp-raid-option-comment.md`

## Acceptance (测岗)
- 仅 EmberRaid/option.yml L3/L4/L17/L19/L30 5+/5-，与荐案逐字一致；解析前后 same；L31、`text=` L22/L45、脚本未动
- 无「扣票 / B0.1 / 团本票 / 团票 / 额外票」；无「B0.1 已清 / 票已废」
- 注释与现行一致：发起者每周首免（`weeklyGrantCreditRaid`，ISO 周 · Asia/Shanghai）→ 之后扣体力 50；未进本退还首免/体力；团戒按 `raidRingWeek` 每人每周 1 枚
- 更正：团戒配置在 `set.yml:29-32`（设计稿写「live 无 raid_ring」有误），值等于代码缺省，行为与注释不受影响
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Later candidates（范围外）
- `set.yml:27`「团票每周 1 张 ⇒ …」、`cash.yml:86`「菜单承诺每周团本票×1」旧注
- 三份 option.yml `text=`「已消耗体力 ×1」
- `CoreRpgPlugin.java:1356` 兜底文案；深渊状态页背包票数
- `mineflayer-tests/raid-combat-smoke.js:155`、`killany-live-retest.js:122` 旧正则

## Next
- **B2.97**：DP `EmberEliteWeekly/option.yml`（DP option 注释最后一份；精英壳勿硬开）
