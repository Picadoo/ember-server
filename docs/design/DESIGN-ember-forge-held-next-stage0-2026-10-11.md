# Stage0 · 手持件下一确定养成 · 2026-10-11

> **状态：** Stage0 COMPLETE · 已批 A · 方案 M · **D1942– SHIP**  
> **上游：** RESEARCH PoE「材料永远能换成确定一步」· FG-Q 成色入周目标后，工坊入口仍缺「这件下一步」

## 问题
玩家手持刃/护符开工坊时，要自己在强化/成色/精工里猜；连续赋能感靠记忆。数值已全在 `EmberUpgradeRules`，缺统一「下一确定步」半行。

## 方案 M（纯展示 · 不改价/率）
| ID | 项 | 态 |
|----|----|----|
| FH1 | `EmberForgeHeldNext.line`：按料够优先可执行确定步，否则最近缺料步 | SHIP |
| FH2 | forge `helpLines` AB/chat 接线（手持时） | SHIP |
| FH3 | 单测钉死优先级 | SHIP |

优先级（可执行 > 缺料最少）：成色可升 → 精工可升 → 强化未满；全满则「手持已满档」。  
接 GS3 dial 短尾。禁 K3/SF4/pity invent/hope PAPI twin。

## 批注
- [x] 总控批 A · 方案 M · 2026-10-11
