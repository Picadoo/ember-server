# STATUS — B2.99 DP EmberRaid option.yml L22 开本提示去扣费字样 · 结案

Date: 2026-10-01 02:57 Asia/Shanghai
Owner: 余烬-总控（Grok Bot）

## Verdict
**PASS · 勾销**（reload 实测：待恢复服后实测，SKIP 不挡）

## Tips
- 设计 `4007fb3` · 批准 `27633ca` · 插件 `b90e999` · 测 `16d74cb`
- 报告：`docs/TEST-B2.99-dp-raid-start-text.md`

## Acceptance (测岗)
- 仅 EmberRaid/option.yml L22 1+/1-，与荐案 (b) 逐字一致：`§8通关箱 · 全队每人结算 · 周首通保底团戒`
- js-yaml `only-L22`：仅 `dungeon-start.action-script[1]` 变化，列表长度 4、顺序不变（未删行，DP 行序依赖无需证明）
- 收窄后 `rg "已消耗体力|体力 ×|体力 -|扣票|团本票"` 0 命中；L29「孔石锋利×1」为奖励数量，范围外
- 文案出处：`ember_raid_rewards.yml:46`、`option.yml:30,42-44`、`ember_raid.yml:69/93`；grantRing 每人每周 1 次，不暗示扣费
- 入口 `corerpg enter raid` → `tryEnter(RAID)` → `TicketEntryService:148-149` 私聊 costHint；无 mineflayer 依赖 L22
- HANDOFF §8 查密码输出 0；`ops.json` = `[]`

## Corrections (不影响结论)
- 设计稿 §4 rg 清单含 `×1` 会命中 L29，预期写错；以派单收窄口径为准
- DP `@player` 按人执行仅有实服冒烟文档佐证（`docs/smoke-raid-combat-20260926.md`），无源码

## Later candidates
- 玩家可见：`TrMenu/menus/ember_raid.yml:76-77` 菜单恒报「消耗 §e50 §8体力」，与首免私聊冲突 → 待策划排位
- B2.109 追加：`raid-combat-smoke.js:153-155,170`、`abyss-followup.js:58`、`abyss-calamity-supplement.js:111`、`dungeon-balance.js:21`、`killany-live-retest.js:122`

## Next
- **B2.100**：DP `EmberAbyss/option.yml` L22 开本提示去扣费句 → 策划出稿
