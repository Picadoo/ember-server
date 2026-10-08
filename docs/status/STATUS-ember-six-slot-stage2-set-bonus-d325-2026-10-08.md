# 余烬 · 六槽 Stage2 档 C · D325 施工 STATUS（插件岗 · 2026-10-08）

- 上游：总控 D325 批 A·档 C·开工；规格 `docs/design/DESIGN-ember-six-slot-stage2-set-bonus-2026-10-08.md`
- tip：见本提交（施工完成后填）
- 测服 jar：`/workspace/tmp/d325/CoreRpg-1.65.99-d325.local.jar`（plugin.yml `1.65.99-d325.local`，**未**提交版本戳；sha 见同目录 `JAR.sha256`）
- 线上：**未换 jar、未开 set_bonus、未动 bv / enabled / migrate**；login / proxy / MariaDB 未碰

## 效果口径（已落地）

| 项 | 值 |
|----|-----|
| 开关 | `gear.six_slot.set_bonus`，代码默认 **false**；须 master `gear.six_slot.enabled` |
| 减伤 | 四件套激活时受伤 ×**0.97**（三族相同） |
| 进 B/H？ | **不进** `EmberFormula`；仅 `EmberCombatListener.playerVictim` 敌方伤害管道乘一次 |
| 触发 | 两件套已激活（刃+护符同族 X）+ 穿着甲中族=X 且**自身掉落阶≥T2** ≥2 件（D169） |
| 不动 | K0 / F 跟随 / 护符×1.0 / 两件套被动；不上 K3/S |

## 静态敏感度（档 C · 跳过完整动态 T0）

- EHP 等价 ≈ **+3.1%**（`1/0.97 − 1`）；面板 H / B / D / M **不变**
- 相对 Stage1 通关率 ±2pp 敏感带：纸面远小于该带；**不预期**推动 W30 / 团本点估越线
- 与 K0 精工差（约 0.5% H）比：−3% 受伤是更清楚的「凑套理由」，另 2 甲位仍可追成色

## 改动摘要

| 区 | 文件 |
|----|------|
| Java | `EmberSixSlot`（开关+常量+lore）· `EmberSixRank`（D169 `setProgress` + takenMult）· `EmberSixPapi`（进度/状态机/fam）· `EmberCombatListener`（单一受伤钩子） |
| 单测 | `EmberSixSetBonusTest`（新建）· `EmberSixRankTest` / `EmberSixSlotTest` 文案对齐 |
| 菜单 | `docs/design/staged/d318-six-slot/trmenu/ember_p1_armor.yml` · `plugins/TrMenu/menus/ember_p1_armor.yml`（S 格三态） |
| 测服产物 | `/workspace/tmp/d325/`（jar + snippet + offline-smoke） |

## 单测

| 套件 | 结果 |
|------|------|
| `EmberSixSetBonusTest` | **9/9 PASS**（触发边界 / 掉落阶 vs 跟随 / 不进 Formula / 开关关=×1 / PAPI 状态机 / 单一钩子审阅） |
| `EmberSixRankTest` | **14/14 PASS** |
| `EmberSixSlotTest` | **8/8 PASS** |
| `EmberSixSlotOffGoldenTest` | **1/1 PASS** |
| `EmberSixSlotRulesTest` | **6/6 PASS** |

离线冒烟脚本：`/workspace/tmp/d325/offline-smoke.sh` → **ALL OFFLINE SMOKE PASS**

## 测服冒烟摘要

| 项 | 结论 | 说明 |
|----|------|------|
| 开关键默认关 | **PASS** | `setBonusEnabled()` 默认 false；关 = takenMult 1.0 |
| 激活 vs 未激活受伤 | **PASS（离线）** | 激活 ×0.97；未激活 / 开关关 ×1.0（`EmberSixSetBonusTest`） |
| 菜单文案 | **PASS** | staged + plugins 均无「后续开放」；S 格 未激活/进行中/已激活；PAPI `/2` |
| G-S0~G-S2 / G-S6 | **PASS** | 见单测 |
| 关开关回退 | **PASS（离线）** | `set_bonus=false` → 无减伤；进度文案仍可用 |
| 实机进服 | **未跑** | 线上观察服占 play；本号**不**启第二 play / **不**动线上开关。测服 jar+片段已交 `/workspace/tmp/d325/`，供测试岗隔离实例验收 |

## bv62 建议（总控另签）

**建议：本号施工不升 bv；D4 部署签时再升 bv62（只含 Stage2 四件套）。观察期继续 bv61。**

## 热更 / 重启策略

线上 Stage1 观察期**不停服、不关** `enabled`/`migrate`。本号**不部署** Stage2。测服：换 jar 需停测服 play → 换档 → 启；`set_bonus` 可写测服 `ember-v1.yml` 后 `/corerpg reload` 切换。失败回退：关 `set_bonus` → 无减伤；Stage1 甲属性与迁移数据保留。

## 门禁对照

| # | 结果 |
|---|------|
| G-S0 开关关=基线 | PASS（单测） |
| G-S1 仅掉落阶≥T2 计件 | PASS |
| G-S2 激活 ×0.97；B/H 不变 | PASS |
| G-S3 菜单进度 | PASS（菜单+单测）；实机点击待测服岗 |
| G-S4 资产不回归 | 本号未改迁移/换装路径；待测服岗抽查 |
| G-S6 不改 F/护符×1.0/K0 | PASS（审阅+golden） |

---
*D325 插件岗施工 · 档 C · 不部署线上*
