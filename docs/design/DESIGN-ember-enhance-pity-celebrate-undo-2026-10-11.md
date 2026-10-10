# DESIGN · 强化下次必成庆祝 + 失败软撤销（D614）

> **状态：** 已批 A · 方案 M

1. `EmberEnhanceLoop` / `EmberForgeFeel`：识别失败后 `guaranteed(after)` 与成功 note「保底」。
2. `EmberEnhanceUndo`：内存 Snap（uid/rev/pityBefore/cost/at）；`/corerpg p1 enhance undo`；item txn 写回 pity；`EmberPay` 退材料。
3. 零改 RATE / MAX_TRIES / enhanceCost。
