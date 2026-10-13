# STATUS · TIP-CG hotfix · chase 解锁谓词对齐 ShortPath · D13312b

> 2026-10-13 Asia/Shanghai · 余烬-插件岗 · 上游测岗 FAIL `71d4f3356`

## 问题
chase 用 `runs.unlocked`（C_UNLOCK+sx，要 admin unlock）；短征进本用 `EmberShortPath.unlocked`（progressFlag req/q01）。Q01 首通后 chase 空 / chase_ok=0 / enter short-goal 拒。

## 交付
- `EmberRunPapi.goalShortChase` + `EmberRunService` enter short-goal：谓词 → `EmberShortPath.unlocked`
- 拒文案「先首通 Q01」
- 单测：`onlyQ01ProgressFlagOpensChase`（仅 q01 fact、无 C_UNLOCK → chase 非空；adminUnlock 谓词仍空）
- jar：`1.65.809-d13312b.local` · sha256 `495731c9d25a…` · ECONOMY_BV **123** · **零改** addGoal/targets/徽/日帽/Stage2/菜单/EmberGoalShortChase 纯函数
- play：live `1.65.809-d13312b.local` · storage=mysql · bv123

## 零改边界
- EmberGoalShortChase.chaseKey/done/chaseOk 纯函数
- weekly_goals.targets / 徽 / 日帽 / SoftPity / bv123
- TrMenu（物品岗）

## 下步
- 测岗复测 chase_ok=1（Q01 首通 alone）
