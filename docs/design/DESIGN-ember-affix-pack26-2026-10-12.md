# Ember Variety Affix Pack 26：削斩 / 峡洼（D12230，2026-10-12）

> 状态：**CoreRpg 1.65.748-d12230.local / balance_version 113**（routine-0610）。门禁 `tools/p1sim/affixpack26.py` → `tools/p1sim/out-affixpack26-*.md`。
> 前置：D12229 Affix Pack 25（斫斩/谷洼，池 50→52）。
> 范围：只动词缀池（52 → 54）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, hew, dell, chip, glen]
chip: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
glen: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：chip every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；glen every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **削斩**：原点 = 玩家脚底（预警锁定）；朝向 = **玩家**视角 yaw **+90°** 吸附最近 8 向（k·π/4）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`chipHit` 每施放每人最多 1 次；名牌 `§6[削斩]`。相对 hew（玩家 yaw−90）/ gleam（玩家 yaw）/ draw（玩家 yaw+180）/ slash（精英 yaw）。
- **峡洼**：原点 = 精英脚底沿精英→玩家 **0.875**（超 dell 0.75 / tarn 0.625 / basin 0.5）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`glenHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[峡洼]`。相对 dell/tarn/basin（精英相对偏移）/ jailer（有定身）。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 削斩叠伤 | chipHit 每施放每人最多 1 次 |
| 峡洼叠伤 | glenHit 每施放每人最多 1 次 |
| 峡洼变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixChip` / `AffixGlen` | 新原语 |
| `EmberAffixes` | 注册 54 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 chip/glen |
| TrMenu | 无硬编码列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack26ChipGlen_D12230`；门禁 affixpack26 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
