# RESEARCH · 护甲零头→胚料承诺环 · 2026-10-11

## 参考
D4 分解材料进度感；挂机服「攒满再花」。

## Ember 缺口
护甲分解常只加 `p1_blank_tenths`、不出整胚 → `afterDismantle` 不触发，玩家无下一步花法。

## 方案
零改 0.1×tier；成功后若 blanks=0 仍推零头进度 + ConfirmTokens（再分解 / 手持成色·精工·升阶若已有胚）。
