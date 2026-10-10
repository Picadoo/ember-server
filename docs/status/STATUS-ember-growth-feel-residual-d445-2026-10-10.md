# 状态 · D445：技能落点确认短闪 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D445 · PASS**  
**方案 M：** 烬斩命中 / 烬突落地 ActionBar 确认短闪 · 零改 skill_mult/CD/套装表  
**版本：** jar **1.65.143-d445.local** · bv **未抬**  
**上游：** D444 HUD 抛光 · D440 HOLD · docs `4456677c`

## 交付
| 项 | 状态 |
|----|------|
| `SkillService.shouldFlashSlashHit` / `shouldFlashDashLand` | 防抖 ≥400ms · 无目标不闪 · 受阻不闪 |
| `EmberSetService.flashSkillConfirm` | ActionBar-only · 让位套装 proc/forceHud · `skillFeelUntil` |
| 单测 `SkillHitFlashTest` | OK |
| 冒烟 FreshQ971 | PASS=7 FAIL=0 · 见「烬斩命中」「烬突落地」 |
| SEVERE | 0 |
| EmberSetRules / AFK / 奖励表 | **未改** |

## 禁夹带
sx20 · 聚火复活 · 抬日表 · Stage2 提前关 · K3 · D440

## 下债
工坊随机成色赌档 tip · D440 另号 · 主线 Q 地图审计 D447
