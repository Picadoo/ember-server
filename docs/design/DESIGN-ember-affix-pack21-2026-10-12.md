# Ember Variety Affix Pack 21：刺斩 / 沼洼（D12225，2026-10-12）

> 状态：**CoreRpg 1.65.743-d12225.local / balance_version 108**（routine-0344）。门禁 `tools/p1sim/affixpack21.py` → `tools/p1sim/out-affixpack21-d12225.md`。
> 前置：D12224 Affix Pack 20（脊斩/泽地，池 40→42）。
> 范围：只动词缀池（42 → 44）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, ridge, marsh, spur, fen]
spur: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
fen: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：spur every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；fen every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **刺斩**：原点 = 玩家脚底（预警锁定）；朝向 = 精英→玩家水平吸附最近 8 向（cardinal+diagonal，k·π/4）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`spurHit` 每施放每人最多 1 次；名牌 `§6[刺斩]`。
- **沼洼**：原点 = 精英脚底沿精英→玩家 0.125（介于 mire 0 / well 0.25）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`fenHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[沼洼]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 刺斩叠伤 | spurHit 每施放每人最多 1 次 |
| 沼洼叠伤 | fenHit 每施放每人最多 1 次 |
| 沼洼变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixSpur` / `AffixFen` | 新原语 |
| `EmberAffixes` | 注册 44 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 spur/fen |
| TrMenu | 无硬编码 42 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack21SpurFen_D12225`；门禁 affixpack21 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
