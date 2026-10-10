# 状态 · 饥饿不掉 · 仅服务端配置（无 CoreRpg jar）

**日期：** 2026-10-10  
**裁决：** 用户纠正 — **不要** CoreRpg `FoodLevelChange` 监听；Paper 1.12 无 hunger-off 开关。

## 改动

| 文件 | 改动 |
|------|------|
| `server-runtime/spigot.yml` | `world-settings.*.hunger.*` 全部 exhaustion / multiplier → **0.0** |
| `login-runtime/spigot.yml` | 同上 |
| `plugins/Multiverse-Core/worlds.yml` | 8 世界 `hunger: 'false'` |

## 未改

- **无** CoreRpg jar / `EmberHungerLock` / `hunger_lock`（已中止并回滚）
- live jar 仍为 **1.65.240-d549.local**

## 烟测

FreshQ1091/1092：冲刺+跳跃数秒后 `food=20` `sat=20`。
