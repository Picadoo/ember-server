# Ember Variety Affix Pack 8：孢雾 / 侧袭（D12212，2026-10-11）

> 状态：**CoreRpg 1.65.730-d12212.local / balance_version 95**（routine-2112）。门禁 `tools/p1sim/affixpack8.py` → `tools/p1sim/out-affixpack8-d12212.md`。
> 前置：D12211 Affix Pack 7（脉爆/埋雷，池 14→16）。
> 范围：只动词缀池（16 → 18）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**孢雾**（`spore`，随机在场玩家脚下延迟圈，预警 ≥1.5 s，周期 ≥7 s，半径 2.2，≤0.5×atk，**无定身**，每人每施放最多 1 次）和**侧袭**（`flank`，精英左右 2 小圈，预警 ≥1.3 s，周期 ≥6.5 s，半径 ≤1.4，≤0.55×atk，每人每施放跨两圈最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| D3 Plagued | 玩家脚下延时毒地 | **孢雾**：脚下圈 + warn→land；无 root | 叠层 DoT、永久毒地 |
| 空间压力 hail/mines/pulse | 前 / 后 / 脚下 | **侧袭**：垂直左右填侧翼 | 改奖励 / 新实体 |
| Pack5–7 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, pulse, mines, spore, flank]
  spore: {every: 7.0, warn: 1.5, radius: 2.2, dmg: 0.5}
  flank: {every: 6.5, warn: 1.3, radius: 1.4, side: 2.8, dmg: 0.55}
```

钳位：spore every≥7、warn≥1.5、radius≤2.2、dmg≤0.5；flank every≥6.5、warn≥1.3、radius≤1.4、side≤3.5、count 固定 2、dmg≤0.55。未知 id 丢弃。等权。

- **孢雾**：原点 = REACH16 内随机玩家脚下；预警 SPELL_WITCH；落地 SLIME/CRIT_MAGIC；`sporeHit` 每施放每人最多 1 次；名牌 `§6[孢雾]`。
- **侧袭**：原点 = 朝最近玩家方向的**左右**（±side × 垂直向量）；预警 SMOKE/CRIT；落地 EXPLOSION_NORMAL；`flankHit` 跨两圈每人最多 1 次；名牌 `§6[侧袭]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 孢雾叠伤 | sporeHit 每施放每人最多 1 次 |
| 侧袭双圈叠伤 | flankHit 跨两圈每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixSpore` / `AffixFlank` | 新原语 |
| `EmberAffixes` | 注册 18 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 spore/flank |
| TrMenu | 无硬编码 16 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack8SporeFlank_D12212`；门禁 affixpack8 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
