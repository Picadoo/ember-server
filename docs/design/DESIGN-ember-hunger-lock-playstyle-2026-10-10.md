# 余烬 · 成长花样 · 饥饿锁定（D550）

STATUS=**已批 A · 方案 M · D550 · PASS** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-hunger-lock-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-hunger-lock-need-design-2026-10-10.md) · backlog `B-hunger-lock` · **≠改伤害/CD表 ≠AFK ≠K3 ≠sx20 ≠组合粘性 ≠外观 ≠工坊花法 ≠同族 HUD ≠入场cue**

> **一句话玩家价值：** P1 开启且 `hunger_lock: true` 时，饱食度永不下降；进服/换世界/重生补满。果腹/面包路径保留不动。

## 方案 M

| 窗 | 内容 |
|----|------|
| W1a | `ember-v1.yml` `hunger_lock: true`（默认开） |
| W1b | `EmberHungerLock`：`FoodLevelChangeEvent` 降级取消 + join/world/respawn saturate |
| W1c | Multiverse-Core `worlds.yml` 全部 `hunger: false` |
| 禁 | 改 bite/bread 逻辑 · 改战斗表 · ActionBar · K3/AFK/sx |

- [x] 批注：总控 **已批 A · 方案 M · D550** · 2026-10-10
