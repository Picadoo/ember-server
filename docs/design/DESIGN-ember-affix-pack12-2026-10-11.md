# Ember Variety Affix Pack 12：残影 / 绽放（D12216，2026-10-11）

> 状态：**CoreRpg 1.65.734-d12216.local / balance_version 99**（routine-2311）。门禁 `tools/p1sim/affixpack12.py` → `tools/p1sim/out-affixpack12-d12216.md`。
> 前置：D12215 Affix Pack 11（劈斩/涟漪，池 22→24）。
> 范围：只动词缀池（24 → 26）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**残影**（`mirror`，预警开始时锁定最近玩家脚下圆，落地落在该点；预警 ≥1.4 s，周期 ≥6.5 s，半径 ≤2.0，≤0.55×atk，每人每施放最多 1 次）和**绽放**（`bloom`，精英四周等角 4 瓣圆，环半径 `orbit` 默认 3.0 钳 2.0–4.0，瓣半径 ≤1.2，≤0.5×atk，预警 ≥1.3 s，周期 ≥7 s，跨瓣每人每施放最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| jailer 脚下圈 | 定身脚下 | **残影**：无定身、锁定预警起点 | 根/减速 |
| scatter 环落 | 环上多点 | **绽放**：固定 4 瓣 90° | 随机朝向偏移 |
| Pack5–11 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, cleave, ripple, mirror, bloom]
  mirror: {every: 6.5, warn: 1.4, radius: 2.0, dmg: 0.55}
  bloom: {every: 7.0, warn: 1.3, orbit: 3.0, radius: 1.2, count: 4, dmg: 0.5}
```

钳位：mirror every≥6.5、warn≥1.4、radius≤2.0、dmg≤0.55；bloom every≥7、warn≥1.3、orbit 2.0–4.0、radius≤1.2、count 固定 4、dmg≤0.5。未知 id 丢弃。等权。

- **残影**：原点 = 预警开始时最近玩家脚底（floor）；预警 SPELL_INSTANT；落地 SPELL_WITCH；`mirrorHit` 每施放每人最多 1 次；名牌 `§6[残影]`。
- **绽放**：4 原点 = 精英 + (cos(i·π/2), 0, sin(i·π/2))·orbit；预警 SPELL_INSTANT；落地 CRIT_MAGIC；`bloomHit` 跨瓣每施放每人最多 1 次；名牌 `§6[绽放]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 残影叠伤 | mirrorHit 每施放每人最多 1 次 |
| 绽放叠伤 | bloomHit 跨瓣每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixMirror` / `AffixBloom` | 新原语 |
| `EmberAffixes` | 注册 26 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 mirror/bloom |
| TrMenu | 无硬编码 24 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack12MirrorBloom_D12216`；门禁 affixpack12 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
