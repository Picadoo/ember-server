# DESIGN · 精选/周目标/觉醒追诚实 Stage0 · 2026-10-11

> **状态：** Stage0 + **D1409– SHIP** · 已批 A · 方案 M  
> **硬禁：** 无 K3/SF4 / hope PAPI / 锁通关 / 改掉率 / skill_mult / AFK raise

## 问题
setchase-mapid（SC/MI/CD）已近饱和。精选入场、周目标进度、觉醒下一档仍偏粗「完成感」——缺「还差几 / 下一该刷哪」肉感。

## 方案 M（薄）
| ID | 项 | 改动 |
|----|----|------|
| FG1 | 周目标 AB 还差 N | addGoal / goalsCommand remain |
| FG2 | 精选入场附地图身份缺 | enterCue + MapIdentity |
| AW2 | 觉醒短剩余 helper | EmberLoadout.awakeningRemain |
| AW3 | 成套完成→觉醒工坊钮 | setchase ConfirmTokens |
| GP1 | 周标 chase 还差 | GoalPath.chaseLine |
| GP2 | glance/afterSet AB | maybeGlance / afterSet |
| HN1 | 勋记下一缺 AB | honor list |
| DP1 | 日委路径 AB | DailyPath glance/settle |
| FP1 | 套装打法图身份 | FamilyPath afterSet |
| AB1 | 深渊路径 AB | AbyssPath apply |
| ON1 | 在线档还差分 | Sign claimOnline |
| MC1 | 入场名片缺件 | MapCardCue |
| SX1 | 短征首通还差 | ShortPath AB |
| FC1 | 首通空态对齐 | cmdFirstClear |
| RT1 | 突破路线 AB | routeCommand |
| SK1 | 下一技能剩余 | runs/join · SkillUnlock.nextHope |
| SP1 | 专精空点 AB | specRemainCue |

不改奖励数、周帽、掉率、通关闸。
