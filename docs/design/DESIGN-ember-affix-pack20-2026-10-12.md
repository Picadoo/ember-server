# Ember Variety Affix Pack 20：脊斩 / 泽地（D12224，2026-10-12）

> 状态：**CoreRpg 1.65.742-d12224.local / balance_version 107**（routine-0313）。门禁 `tools/p1sim/affixpack20.py` → `tools/p1sim/out-affixpack20-d12224.md`。
> 前置：D12223 Affix Pack 19（缝斩/泥沼，池 38→40）。
> 范围：只动词缀池（40 → 42）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, seam, mire, ridge, marsh]
ridge: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
marsh: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：ridge every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；marsh every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **脊斩**：原点 = 玩家脚底（预警锁定）；朝向 = 精英→玩家水平吸附最近对角（±45°，sign(dx)/sign(dz) 归一）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`ridgeHit` 每施放每人最多 1 次；名牌 `§6[脊斩]`。
- **泽地**：原点 = 玩家脚底 floor（offset 0 on player）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`marshHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[泽地]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 脊斩叠伤 | ridgeHit 每施放每人最多 1 次 |
| 泽地叠伤 | marshHit 每施放每人最多 1 次 |
| 泽地变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixRidge` / `AffixMarsh` | 新原语 |
| `EmberAffixes` | 注册 42 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 ridge/marsh |
| TrMenu | 无硬编码 40 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack20RidgeMarsh_D12224`；门禁 affixpack20 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
