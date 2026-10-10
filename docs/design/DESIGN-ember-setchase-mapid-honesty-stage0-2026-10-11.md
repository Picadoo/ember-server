# DESIGN · 成套追 / 图身份诚实 Stage0 · 2026-10-11

> **状态：** Stage0 + **D1339– / D1361– SHIP** · 已批 A · 方案 M  
> **硬禁：** 无 K3/SF4 / hope celebration PAPI / 锁通关 / 改掉率 / skill_mult

## 问题
工坊 remain/AB 面已近饱和。成套追 AB 仍粗「焦点未齐」；图装追未把图鉴身份 have/need 写成肉感剩余；烙纹定向预览缺烙纹/币 have。

## 方案 M（薄）
| ID | 项 | 改动 |
|----|----|------|
| SC1 | 成套追 AB 带缺口半行 | focusGap 去色进 ActionBar |
| MI1 | 图身份剩余非 hope 文案 | `EmberMapIdentity.remainLine` |
| MI2 | mapfarm/cue 刷身份剩余 | 聊天+AB |
| BP1 | 烙纹定向 have/need | brandPin 预览 |
| MX1 | 印记兑换 have/need | marks exchange | **D1353– SHIP** |
| SC2 | 追烙徽记剩余 | SigChase.remainLine | **D1355– SHIP** |

| CD1 | 图录阶段剩余 | EmberCodex.progressRemain | **D1361–** |
| MI3 | 图身份下一缺件 | EmberMapIdentity.nextMissing | **D1363–** |
| SC3 | 设焦点后 AB=缺口 | setfocus → stripSection(focusGap) | **D1365–** |
| MF1 | mapfarm 无记录时身份板 | 主线 Q 身份一瞥 | **D1367–** |

不改套装战力、掉率、通关闸。
