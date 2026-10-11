# Ember Variety Affix Pack 10：裂隙 / 连落（D12214，2026-10-11）

> 状态：**CoreRpg 1.65.732-d12214.local / balance_version 97**（routine-2218）。门禁 `tools/p1sim/affixpack10.py` → `tools/p1sim/out-affixpack10-d12214.md`。
> 前置：D12213 Affix Pack 9（贯穿/散射，池 18→20）。
> 范围：只动词缀池（20 → 22）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**裂隙**（`rift`，穿过精英、与朝最近玩家方向垂直的一条线，预警 ≥1.4 s，周期 ≥6.5 s，half_len≤6、半宽薄≤1.5，≤0.55×atk，每人每施放最多 1 次）和**连落**（`cascade`，从精英朝最近玩家步进 3 小圈，距离 step·1/2/3，预警 ≥1.3 s，周期 ≥7 s，半径 ≤1.3，step 1.5–3.0，≤0.5×atk，每人每施放跨三圈最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| pierce 正交 | 朝玩家单轴线 | **裂隙**：垂直轴线 | 叠成十字连伤 |
| scatter / flank 空间 | 环/侧翼落点 | **连落**：朝玩家三步进圈 | 改奖励 / 新实体 |
| Pack5–9 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, pierce, scatter, rift, cascade]
  rift: {every: 6.5, warn: 1.4, half_len: 4.0, half_w: 1.0, dmg: 0.55}
  cascade: {every: 7.0, warn: 1.3, radius: 1.2, step: 2.0, count: 3, dmg: 0.5}
```

钳位：rift every≥6.5、warn≥1.4、half_len≤6、half_w≤1.5、dmg≤0.55；cascade every≥7、warn≥1.3、radius≤1.3、step 1.5–3.0、count 固定 3、dmg≤0.5。未知 id 丢弃。等权。

- **裂隙**：原点 = 精英脚下；方向 = 朝玩家向量的水平垂直；预警 CRIT；落地 CRIT；`riftHit` 每施放每人最多 1 次；名牌 `§6[裂隙]`。
- **连落**：原点 = elite + normalize(player−elite)·step·i（i=1..3），floor Y；预警 SPELL_INSTANT；落地 SPELL_WITCH/CRIT_MAGIC；`cascadeHit` 跨三圈每人最多 1 次；名牌 `§6[连落]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 裂隙叠伤 | riftHit 每施放每人最多 1 次 |
| 连落三圈叠伤 | cascadeHit 跨三圈每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixRift` / `AffixCascade` | 新原语 |
| `EmberAffixes` | 注册 22 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 rift/cascade |
| TrMenu | 无硬编码 20 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack10RiftCascade_D12214`；门禁 affixpack10 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
