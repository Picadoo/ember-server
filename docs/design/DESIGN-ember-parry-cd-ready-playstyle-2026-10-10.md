# 余烬 · 成长花样 · 招架 CD 就绪上升沿（D476）

STATUS=**已批 A · 方案 M · D476 · PASS** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-parry-cd-ready-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-parry-cd-ready-need-design-2026-10-10.md) · backlog `B-parry-cd-ready` · **≠改 CD 秒数 ≠AFK ≠K3 ≠sx20**

> **一句话玩家价值：** 余烬招架独立 CD 结束后 ActionBar「守招 · 就绪」一闪——回进战斗节奏。

## 方案 M

| 窗 | 内容 |
|----|------|
| W1a | `armCd` 写 cdUntil + schedule 到期任务 |
| W1b | 到期且仍就绪且已解锁 → ActionBar + 轻 pling |
| 禁 | 改 `PARRY_CD_SECONDS` / 窗口 / 伤害 |

- [x] 批注：总控 **已批 A · 方案 M · D476** · 2026-10-10
