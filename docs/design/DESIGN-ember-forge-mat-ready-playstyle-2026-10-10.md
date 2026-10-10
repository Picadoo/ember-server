# 余烬 · 成长花样 · 工坊强化材料就绪上升沿（D478）

STATUS=**已批 A · 方案 M · D478 · PASS** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-forge-mat-ready-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-forge-mat-ready-need-design-2026-10-10.md) · backlog `B-forge-mat-ready` · **≠改强化率/价 ≠AFK ≠K3 ≠sx20**

> **一句话玩家价值：** 主手 T1+ 件刚够下一档强化材料时，聊天+ActionBar 提示并给「去工坊强化」——获取环接到养成。

## 方案 M

| 窗 | 内容 |
|----|------|
| W1a | delivery finish / settle deliver → `maybeReadyCue` |
| W1b | latch `p1_forge_mat_ready` = 目标强化档；不够则清；enhance 成功清 |
| W1c | 跳过 T0；按钮 `/corerpg p1 forge` |
| 禁 | 改 RATE / Cost / AFK |

- [x] 批注：总控 **已批 A · 方案 M · D478** · 2026-10-10
