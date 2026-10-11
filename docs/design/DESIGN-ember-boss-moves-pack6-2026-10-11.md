# D12209 Boss Moves Pack 6 · mid-game 破招（设计 + 上线，2026-10-11）

## 0. 一句话

Q03 残誓守卫 / Q05 断塔斧卫各加半血 3s 蓄力圈（誓魂汲取 / 塔心聚裂），复用 Pack5 `break_hp`：蓄力窗内打满 break_hp×maxHP → 破招（不落地 + 踉跄）；否则打中仍在圈内的人。

## 1. 参考

| 参考 | 机制 | 本服取用 | 不取 |
|---|---|---|---|
| WoW 打断读条 | 读条可打断 | 「读条中可打断」 | 专用打断技能 / 读条 UI |
| Lost Ark Stagger | 限时打满硬直 | 限时伤害阈值 | 失败团灭 |
| FFXIV 大圈读条 | 提前跑出 | 3s warn 够跑 | — |
| Pack5 D193 | Q06/Q07 break_hp | 同一套字段与 Java | 仅在终局有破招 → Pack6 补中段 |

## 2. 规则（yml）

| 图 | 招 | every | below | warn | r | dmg | break_hp | break_stun |
|---|---|---|---|---|---|---|---|---|
| Q03 | 誓魂汲取 | 25 | 0.5 | 3.0 | 5 | 22 | 0.065 | 0.3 |
| Q05 | 塔心聚裂 | 25 | 0.5 | 3.0 | 5.5 | 28 | 0.065 | 0.3 |

- 不加到 charge / share；`def.rush` 已跳过 break_hp（Director）。
- 门禁失败：先降 break_stun，再略升 break_hp 或降 dmg；**永不涨奖励**。

## 3. 模拟

`python3 tools/p1sim/breakmove.py q03 q05`（可附带 q06 q07 复检）。原始 `tools/p1sim/out-breakmove-d12209.md`。门禁 MAX_ABS_DPP ≤3.0（建议 ≤2.0）。

## 4. 漏洞检查

| 风险 | 结论 |
|---|---|
| 新奖励 / 领取 | 无 |
| 永久强度 | 无 |
| 连战绕过 | rush 排除 |
| 组队缩放 | 需求跟 maxHP |
| 双领 | 无计数/领取 |

## 5. 禁

Pack6 房间事件、外观、六槽、永久伤害倍率、event_core / affix_shard 改动。
