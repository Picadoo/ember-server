# 状态 · D445：技能落点确认短闪 · PASS · LIVE

**裁决：** **PASS · 批 A · 方案 M · D445** · 2026-10-10  
**上游：** D444 HUD 抛光 PASS jar 1.65.142 · DESIGN `DESIGN-ember-growth-feel-residual-2026-10-10.md`  
**版本：** jar **1.65.143-d445.local** · bv **未抬** · commit `f22d4e2e`  
**代码：** `SkillService` 烬斩命中 / 烬突落地 · 防抖 0.4s · `EmberSetService.flashSkillConfirm` 让位套装 `procFlashUntil` · ActionBar-only ≤1.2s  
**禁改：** skill_mult / CD / EmberSetRules coef/every/ICD / AFK tiers / balance_version  

## 行为

| 触发 | ActionBar | 不闪 |
|------|-----------|------|
| 烬斩对 ≥1 有效目标造成伤害 | `烬斩命中`（金） | 无目标 / 防抖窗内多段 |
| 烬突位移成功落地 | `烬突落地`（青） | 「前方受阻」取消 |

## 单测

`SkillHitFlashTest` 5/0 · `EmberSetEngineTest` 21/0（JDK8 class 52）

## 部署

| 项 | 结果 |
|----|------|
| 线上 | **2026-10-10 15:17 CST** · play PID 186108 · Enabling `v1.65.143-d445.local` |
| MySQL | CoreRpg + CoreGacha connected · SEVERE **0** |
| 冒烟 | FreshQ972 烬斩命中 PASS · FreshQ969 烬突落地 PASS · botd [] |
| 备份 | `/workspace/backup/CoreRpg-1.65.142-d444-pre-d445-deploy-151647.jar` |
| 脚本 | `tools/p1map/d445-skill-flash-smoke.sh` |

*D445 PASS · LIVE*
