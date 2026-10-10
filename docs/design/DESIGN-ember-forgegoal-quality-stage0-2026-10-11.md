# Stage0 · 工坊目标·成色主路径 · 2026-10-11

> **状态：** Stage0 COMPLETE · 已批 A · 方案 M · **D1928– SHIP**  
> **上游：** RESEARCH「材料→确定 +1 档」· GS3 表盘 · forgegoal 缺成色槽

## 问题
成色升档已是**确定成功**（C06），但周工坊目标只有强化/精工/烙纹/随机锻/转化——成色只能从精工按钮旁路点，连续赋能感弱。

## 方案 M
| ID | 项 | 态 |
|----|----|----|
| FG-Q1 | `EmberForgeGoal.QUALITY=6` parse/label/tip/valid/offer/afterSet | SHIP |
| FG-Q2 | `gapLine` + `recipeGapQuality1`（镜像 qualityCost(0)，不改价） | SHIP |
| FG-Q3 | `EmberQualityLoop` 命中目标完成 UX（同 RefineLoop） | SHIP |
| FG-Q4 | AfkForgePath QUALITY 建议行 | SHIP |
| FG-Q5 | Counters 注释 · 命令用法 · 接 GS3 dial 半行 | SHIP |

**禁：** 不发明 pity；不抬 C06；无新 hope PAPI key；不 EM1-B。

## 批注
- [x] 总控批 A · 方案 M · 2026-10-11
