# Ember Variety Affix Pack 17：浪脊 / 积潭（D12221，2026-10-12）

> 状态：**CoreRpg 1.65.739-d12221.local / balance_version 104**（routine-0142）。门禁 `tools/p1sim/affixpack17.py` → `tools/p1sim/out-affixpack17-d12221.md`。
> 前置：D12220 Affix Pack 16（扇刃/沉渊，池 32→34）。
> 范围：只动词缀池（34 → 36）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**浪脊**（`crest`，预警锁定玩家脚底，相对精英→玩家水平轴垂直细带横斩；预警 ≥1.4 s，周期 ≥6.5 s，长 ≤6、宽 ≤1.2，≤0.55×atk，每人每施放最多 1 次）和**积潭**（`pond`，预警开始时落点在精英→玩家水平向量 75% 处；预警 ≥1.3 s，周期 ≥7 s，半径 ≤2.0，≤0.5×atk，每人每施放最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| Diablo/PoE lateral strip | 前后躲开 | **浪脊**：穿玩家脚、垂直于朝向 | 穿精英 rift / 沿轴 pierce |
| Diablo/PoE delayed near-player circle | 侧移 | **积潭**：0.75 落点 | 中点 sink / 身前 hook / 贴脚 jailer |
| Pack5–16 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

选型说明：`crest` 与 rift（穿精英）、pierce/lance（沿轴）、shear（平行双带）、fan（三前）、skew（±45 穿精英）、venom（世界轴十字）均不重叠；`pond` 与 sink（0.5）、hook（过身）、jailer/spore/mirror（脚下）、brace（双圈）、flank（侧）均不重叠。

## 1. 规则（yml）

```yaml
  affixes: […, fan, sink, crest, pond]
  crest: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
  pond: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：crest every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；pond every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **浪脊**：原点 = 玩家脚底（预警锁定）；朝向 = 精英→玩家水平垂直 `(-dz,0,dx)`；预警 WATER_WAKE；落地 WATER_WAKE / CRIT；`crestHit` 每施放每人最多 1 次；名牌 `§6[浪脊]`。
- **积潭**：原点 = 精英 + 0.75×(玩家−精英) 水平（Y 取精英 floor）；预警 WATER_BUBBLE；落地 WATER_BUBBLE / CRIT_MAGIC；`pondHit` 每施放每人最多 1 次；名牌 `§6[积潭]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 浪脊叠伤 | crestHit 每施放每人最多 1 次 |
| 积潭叠伤 | pondHit 每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixCrest` / `AffixPond` | 新原语 |
| `EmberAffixes` | 注册 36 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 crest/pond |
| TrMenu | 无硬编码 34 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack17CrestPond_D12221`；门禁 affixpack17 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
