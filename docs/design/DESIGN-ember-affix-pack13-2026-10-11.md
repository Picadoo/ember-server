# Ember Variety Affix Pack 13：枪刺 / 新星（D12217，2026-10-11）

> 状态：**CoreRpg 1.65.735-d12217.local / balance_version 100**（routine-2339）。门禁 `tools/p1sim/affixpack13.py` → `tools/p1sim/out-affixpack13-d12217.md`。
> 前置：D12216 Affix Pack 12（残影/绽放，池 24→26）。
> 范围：只动词缀池（26 → 28）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**枪刺**（`lance`，预警开始时锁定朝向最近玩家的细条带；预警 ≥1.4 s，周期 ≥6.5 s，长 ≤6、宽 ≤1.4，≤0.55×atk，每人每施放最多 1 次）和**新星**（`nova`，精英四周固定半径细环，内安全环上挨打；预警 ≥1.3 s，周期 ≥7 s，半径 ≤3.5、半宽 ≤0.7，≤0.5×atk，每人每施放最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| Diablo/PoE telegraphed lance / line | 侧移出细带 | **枪刺**：单向细条、预警锁朝向 | 穿墙瞬移 / reflect |
| Diablo nova / PoE ring | 出环或贴身 | **新星**：固定细环、内安全 | 全屏无预警 / root |
| Pack5–12 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, mirror, bloom, lance, nova]
  lance: {every: 6.5, warn: 1.4, length: 6.0, width: 1.4, dmg: 0.55}
  nova: {every: 7.0, warn: 1.3, radius: 3.5, half_w: 0.7, dmg: 0.5}
```

钳位：lance every≥6.5、warn≥1.4、length≤6、width≤1.4、dmg≤0.55；nova every≥7、warn≥1.3、radius≤3.5、half_w≤0.7、dmg≤0.5。未知 id 丢弃。等权。

- **枪刺**：原点 = 精英脚底；朝向 = 预警开始时最近玩家（水平单位向量）；预警 CRIT 条带；落地 CRIT；`lanceHit` 每施放每人最多 1 次；名牌 `§6[枪刺]`。
- **新星**：原点 = 精英脚底；预警 SPELL_INSTANT 内外环；落地 SPELL_WITCH / END_ROD；`novaHit` 每施放每人最多 1 次；名牌 `§6[新星]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 枪刺叠伤 | lanceHit 每施放每人最多 1 次 |
| 新星叠伤 | novaHit 每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixLance` / `AffixNova` | 新原语 |
| `EmberAffixes` | 注册 28 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 lance/nova |
| TrMenu | 无硬编码 26 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack13LanceNova_D12217`；门禁 affixpack13 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
