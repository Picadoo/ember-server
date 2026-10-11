# Ember Variety Affix Pack 11：劈斩 / 涟漪（D12215，2026-10-11）

> 状态：**CoreRpg 1.65.733-d12215.local / balance_version 98**（routine-2240）。门禁 `tools/p1sim/affixpack11.py` → `tools/p1sim/out-affixpack11-d12215.md`。
> 前置：D12214 Affix Pack 10（裂隙/连落，池 20→22）。
> 范围：只动词缀池（22 → 24）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**劈斩**（`cleave`，精英朝最近玩家正前扇形，预警 ≥1.4 s，周期 ≥6.5 s，全角 ≤90°／半角 ≤45°，range≤5，≤0.55×atk，每人每施放最多 1 次）和**涟漪**（`ripple`，以精英为心外扩环带，环上受伤、环内/环外安全，预警 ≥1.3 s，周期 ≥7 s，半径 ≤4，半宽 ≤1.0，≤0.5×atk，每人每施放最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| hail 前方多点 | 锥内小圈落点 | **劈斩**：连续扇形 | 叠成 hail 多段 |
| pulse 满圆 | 脚下整圈 | **涟漪**：环带（内/外安全） | 地面 DoT 水洼 |
| Pack5–10 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, rift, cascade, cleave, ripple]
  cleave: {every: 6.5, warn: 1.4, angle: 90, range: 5.0, dmg: 0.55}
  ripple: {every: 7.0, warn: 1.3, radius: 4.0, half_w: 1.0, dmg: 0.5}
```

钳位：cleave every≥6.5、warn≥1.4、angle≤90、range≤5、dmg≤0.55；ripple every≥7、warn≥1.3、radius≤4、half_w≤1.0、dmg≤0.5。未知 id 丢弃。等权。

- **劈斩**：原点 = 精英脚下；朝向 = 最近玩家水平方向；预警/落地 CRIT；`cleaveHit` 每施放每人最多 1 次；名牌 `§6[劈斩]`。
- **涟漪**：原点 = 精英脚下；预警 SPELL_INSTANT（外圈+内/外边界）；落地 SPELL_WITCH；`rippleHit` 每施放每人最多 1 次；名牌 `§6[涟漪]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 劈斩叠伤 | cleaveHit 每施放每人最多 1 次 |
| 涟漪叠伤 | rippleHit 每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixCleave` / `AffixRipple` | 新原语 |
| `EmberAffixes` | 注册 24 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 cleave/ripple |
| TrMenu | 无硬编码 22 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack11CleaveRipple_D12215`；门禁 affixpack11 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
