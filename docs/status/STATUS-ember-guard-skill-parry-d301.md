# 状态 · D301：守招·余烬招架 T1（G2_parry_B2）

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批施工 · [`DESIGN-ember-guard-skill-pivot-2026-10-07.md`](../design/DESIGN-ember-guard-skill-pivot-2026-10-07.md) 方案 M · T0b ✅ [`STATUS-ember-guard-skill-parry-t0b-2026-10-07.md`](STATUS-ember-guard-skill-parry-t0b-2026-10-07.md)（`7d5bcce1`）  
**版本：** CoreRpg **1.65.91** · `balance_version` **60**（未抬）

## 人话

旧守招「均匀减伤 × 共享身法税」五轮都夹不住。T0b 过线的换思路：**预警砸中你后半秒内按 Q**，对首领反打 **0.45B**（预警伤仍生效），独立 28 秒冷却——不挡伤、不花烬斩、不抢身法。

## 改了什么

| 项 | 内容 |
|---|---|
| 解锁 | 首通 **Q03**（与 T0b 可达格一致） |
| 键位 | 解锁后 **单按 Q** = 余烬招架（替换扔刃）；未解锁仍旧；**潜行+Q** 仍是身法 |
| 窗 | Boss 非分摊预警**砸中你**后开窗 **450ms** |
| 成功 | 对该首领 flat **×0.45B**（`EmberCombatListener.dealP1` SKILL） |
| 失败 | 窗外 / 无窗：副本内耗独立 CD；城内提示无预警 |
| CD | **28s** 独立（内存）；**不**共享烬斩充能 / 身法 |
| TrMenu | `ember_skill_kit` 格 Q：怎么用 + 好/坏一句；状态条挂解锁/CD |
| PAPI | `kit_parry` / `kit_parry_unlock` / `kit_parry_name` / `kit_parry_cd` |
| jar | `EmberParry` · `EmberSkillKit` 常量 · Director `execute` 开窗 |
| 版本 | **1.65.90 → 1.65.91** |

### 冻结键（禁回潮）

- **无** `kit_guard_red` 均匀乘 / `kit_q_shared` / `kit_guard_charge`
- **无** 一次无效 / 半伤抹预警（A / A_weak）
- **不**改烬斩 / 烬突 / 身法数值与 CD
- **不**放宽 ±2 · **不**抬通关率预算

## 不动

- 六槽 / Pack6 / 天赋续跑（HOLD） / 旧壁垒表
- 事件 R/W / 调律 R / 掉率 / ALTS / bv **60**
- login/proxy（仅 play 换 jar）

## 验收

- play Enabling **1.65.91**；`version CoreRpg` = 1.65.91
- TrMenu reload；技能页可见「守招 · 余烬招架」
- 单测：`EmberSkillKitTest`（parry*）· `CorePapiTest`（kit_parry*）
- 回滚：`/workspace/backup/CoreRpg-1.65.90-pre-d301.jar` + 还原 `ember_skill_kit.yml`

## 下一窗 tip

- **禁** 旧壁垒表再交 · **禁** 天赋续跑 T0'''
- 事件 R/W · 调律 R **后置**（等遥测样本）
- 真人招架窗手感若偏紧/偏松，只允许换窗长/CD/flat 在 T0b 邻域内另开模拟，**不**放宽 ±2
