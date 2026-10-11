# Ember Variety Affix Pack 9：贯穿 / 散射（D12213，2026-10-11）

> 状态：**CoreRpg 1.65.731-d12213.local / balance_version 96**（routine-2143）。门禁 `tools/p1sim/affixpack9.py` → `tools/p1sim/out-affixpack9-d12213.md`。
> 前置：D12212 Affix Pack 8（孢雾/侧袭，池 16→18）。
> 范围：只动词缀池（18 → 20）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**贯穿**（`pierce`，朝最近玩家一条线穿过精英，预警 ≥1.4 s，周期 ≥6.5 s，half_len 如毒十字臂、半宽薄，≤0.55×atk，每人每施放最多 1 次）和**散射**（`scatter`，精英周围环上 3 小圈，预警 ≥1.3 s，周期 ≥7 s，半径 ≤1.3，环距 ~2.6，≤0.5×atk，每人每施放跨三圈最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| D3 Arcane / venom half | 十字线压力 | **贯穿**：单轴线朝玩家 | 完整十字叠伤、反弹 |
| 空间压力 spore/flank/hail | 脚下 / 侧翼 / 前方 | **散射**：环上 3 落点 | 改奖励 / 新实体 |
| Pack5–8 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, spore, flank, pierce, scatter]
  pierce: {every: 6.5, warn: 1.4, half_len: 4.0, half_w: 1.0, dmg: 0.55}
  scatter: {every: 7.0, warn: 1.3, radius: 1.3, ring: 2.6, count: 3, dmg: 0.5}
```

钳位：pierce every≥6.5、warn≥1.4、half_len≤6、half_w≤1.5、dmg≤0.55；scatter every≥7、warn≥1.3、radius≤1.3、ring≤3.5、count 固定 3、dmg≤0.5。未知 id 丢弃。等权。

- **贯穿**：原点 = 精英脚下；方向 = 朝最近玩家；预警 CRIT；落地 CRIT；`pierceHit` 每施放每人最多 1 次；名牌 `§6[贯穿]`。
- **散射**：原点 = 环距 ring、120°×3（相对朝玩家方向偏 60°，不叠脚下/纯前方）；预警 SPELL_INSTANT；落地 SPELL_WITCH/CRIT_MAGIC；`scatterHit` 跨三圈每人最多 1 次；名牌 `§6[散射]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 贯穿叠伤 | pierceHit 每施放每人最多 1 次 |
| 散射三圈叠伤 | scatterHit 跨三圈每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixPierce` / `AffixScatter` | 新原语 |
| `EmberAffixes` | 注册 20 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 pierce/scatter |
| TrMenu | 无硬编码 18 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack9PierceScatter_D12213`；门禁 affixpack9 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
