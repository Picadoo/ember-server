# Ember Variety Affix Pack 32：刻斩 / 坑洼（D12236，2026-10-12）

> 状态：**CoreRpg 1.65.754-d12236.local / balance_version 119**（routine-0918）。门禁 `tools/p1sim/affixpack32.py` → `tools/p1sim/out-affixpack32-*.md`。
> 前置：D12235 Affix Pack 31（剜斩/坳洼，池 62→64）。
> 范围：只动词缀池（64 → 66）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, carve, hollow, score, pit]
score: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
pit: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：score every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；pit every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **刻斩**：原点 = 玩家脚底（预警锁定）；朝向 = **精英**视角 yaw **+90°** 吸附最近 8 向（k·π/4）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`scoreHit` 每施放每人最多 1 次；名牌 `§6[刻斩]`。精英朝向偏移族：slash(=+0) → carve(+45) → score(+90)。
- **坑洼**：原点 = 精英脚底沿精英→玩家 **1.625**（略过 hollow 1.5 / slade 1.375 / combe 1.25 …）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`pitHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[坑洼]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 刻斩叠伤 | scoreHit 每施放每人最多 1 次 |
| 坑洼叠伤 | pitHit 每施放每人最多 1 次 |
| 坑洼变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixScore` / `AffixPit` | 新原语 |
| `EmberAffixes` | 注册 66 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 score/pit |
| TrMenu | 无硬编码列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack32ScorePit_D12236`；门禁 affixpack32 within range；冒烟 FreshQ1096/1097。

STATUS: **shipped**
