# 状态 · D409 W1a：短征有奖结算半句（批 A·M · 插件薄）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派施工 · DESIGN [`DESIGN-ember-short-reward-spend-chase-2026-10-10.md`](../design/DESIGN-ember-short-reward-spend-chase-2026-10-10.md) tip `9a22f1a0` · **方案 M · W1a**  
**裁决：** **本号交** 有奖 settle 第二行进仓/工坊半句 + 可选本本日帽满半句 · 单测 · 装 play · **W1b/c TrMenu 另号** · **未动** grant 表 / 日帽 bump / 首通 / S40–S46 / afk / gate_daily / 观察三开关 / ×0.97

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| W1a 有奖结算半句 | `r.fresh && r.pays` 现有 tell **后**再 `sendMessage`：`§7材料已进仓 · 工坊可花（强化/精工/成色）` | — |
| W1a 可选本本满 | `rewardedAfter >= dailyCap` 同行走 `§8· 本本今日有奖已满` | — |
| `!pays` / 重复通关 | **不**假喊进仓 | — |
| W1b/c 日帽满选页 | — | TrMenu 另号 |

## 落点

| 文件 | 改动 |
|------|------|
| `EmberShortRules` | `spendChaseLine` / `spendChaseDayFullHalf` / `spendChaseTell`（Bukkit-free） |
| `EmberShortService.settle` | pays 分支第二行 tell |
| `EmberShortRulesTest` | 文案 + settle 源路径只在 pays 调 chase |
| 本 STATUS | W1a 结案 |

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V1 有奖文案 | spendChaseLine 含进仓·工坊可花（强化/精工/成色） | EmberShortRulesTest |
| V1b 本本满可选 | after≥cap 含「本本今日有奖已满」；under 不含 | EmberShortRulesTest |
| V2 无奖不假喊 | settle 源：chase 仅在 `fresh&&pays` 内；无奖分支无 spendChase | EmberShortRulesTest |
| V-grant | 未改 settleGrants / economy / 日帽 bump | 纪律 |
| V-play | jar 装 play · Enabling | 见 tip/jar |

## tip / jar

| 项 | 值 |
|----|----|
| tip | `a34ea8b7` |
| jar | `CoreRpg-1.65.117-d409.local.jar` |
| sha256 | _(装服后填)_ |
| Enabling | `CoreRpg v1.65.117-d409.local` |

## 不动

grant 表 / 日帽 bump / 首通包 / S40–S46 金额 · afk.tiers / daily_kills · gate_daily · Stage2 三开关 / ×0.97 · K3 · sx08 · W1b/c 菜单

## 下一号

菜单岗 W1b/c：`sx_day_left_sum==0` 时 Open/I/B 满态追工坊。

---

*D409 W1a · 短征有奖→工坊可追结算半句 · ≠抬日表 ≠开旧日常 ≠关观察。*
