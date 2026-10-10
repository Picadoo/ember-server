# 余烬 · 成长花样 · 挂机装效澄清（D454）

STATUS=**已批 A · 方案 M · D454 · PASS** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-afk-gear-eff-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-afk-gear-eff-need-design-2026-10-10.md) · backlog `B-afk-gear-eff` · **≠抬日表 ≠sx20 ≠K3 ≠改 enhance 价 ≠天赋/灰印**

> **一句话玩家价值：** 打开挂机庭一眼知「我这身装挂这层够不够」——相对层卡推荐装，够稳/勉强/偏弱；**不**靠抬产量或改怪表。

## 0. 证据

| # | 口径 |
|---|------|
| E1 | `ember_p1_afk.yml` 层格有静态「推荐：约攻击 N / 生命 M」· **无**与玩家 loadout 对照 |
| E2 | `%corerpg_p1_stats%` 已有攻击/生命 · 挂机页未挂装效对照 |
| E3 | D432 仓差 / D405 remain **已有** · 本债不重开 |

## 1. 方案表

| 方案 | 内容 | 裁决 |
|------|------|------|
| **M（荐）** | PAPI `afk_gear_you` + `afk_gear_eff` / `_t1..t4`；战况+四层格挂行；三档阈值对齐现网推荐数字 | **主推** |
| A | 只改静态文案「记得看装备页」 | 否决独批 |
| R | 抬日表 / 改怪 hp·atk 让偏弱层变易 | **禁** |

## 2. 规格钉

- 推荐常数（与菜单推荐行对齐）：T1 攻18/生55 · T2 30/85 · T3 50/145 · T4 70/200
- 档：min(攻比,生比) ≥0.95 够稳 · ≥0.70 勉强 · 否则 偏弱
- **禁**改 `afk.tiers` coin/xp/shard/… · **禁**改怪表

## 3. 验收

| V | 条件 |
|---|------|
| V1 | 有装玩家开挂机页：层格可见 vs 推荐 + 够稳/勉强/偏弱 |
| V2 | 战况可见「你的装」+ 对本层 gear_eff |
| V3 | afk.tiers 日表键值与施工前一致 |
| V4 | 单测 `EmberAfkGearEffTest` PASS |

- [x] 批注：总控 **已批 A · 方案 M · D454** · 2026-10-10 · 装效澄清 · 禁止抬日表

*D454 · 挂机装效 · PASS · FreshQ985。*
