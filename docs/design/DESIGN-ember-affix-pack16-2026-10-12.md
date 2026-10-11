# Ember Variety Affix Pack 16：扇刃 / 沉渊（D12220，2026-10-12）

> 状态：**CoreRpg 1.65.738-d12220.local / balance_version 103**（routine-0115）。门禁 `tools/p1sim/affixpack16.py` → `tools/p1sim/out-affixpack16-d12220.md`。
> 前置：D12219 Affix Pack 15（钩落/斜切，池 30→32）。
> 范围：只动词缀池（32 → 34）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**扇刃**（`fan`，预警锁定朝向，精英脚下 −30°/0°/+30° 三道细射线向前；预警 ≥1.4 s，周期 ≥6.5 s，长 ≤6、宽 ≤1.0，≤0.55×atk，每人每施放最多 1 次）和**沉渊**（`sink`，预警开始时落点在精英↔最近玩家中点；预警 ≥1.3 s，周期 ≥7 s，半径 ≤2.0，≤0.5×atk，每人每施放最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| Diablo/PoE fan / triple ray | 站空隙 | **扇刃**：相对朝向 ±30°+正前三带 | 锥形 cleave（已有）/ 单枪刺 |
| Diablo/PoE midpoint delayed circle | 侧移 | **沉渊**：中点落圈 | 贴脚 jailer/spore / 身前 hook |
| Pack5–15 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

选型说明：`fan` 与 lance（单前）、shear（平行双带）、skew（±45° 贯穿）、cleave（锥）均不重叠；`sink` 与 jailer/spore（脚下）、hook（身前）、brace（双圈）均不重叠。

## 1. 规则（yml）

```yaml
  affixes: […, hook, skew, fan, sink]
  fan: {every: 6.5, warn: 1.4, length: 6.0, width: 1.0, dmg: 0.55}
  sink: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：fan every≥6.5、warn≥1.4、length≤6、width≤1.0、dmg≤0.55；sink every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **扇刃**：原点 = 精英脚底；朝向 = 预警开始时最近玩家；三带 = 朝向 −30°/0°/+30°；预警 CRIT_MAGIC；落地 CRIT_MAGIC / CRIT；`fanHit` 每施放每人最多 1 次（跨三带）；名牌 `§6[扇刃]`。
- **沉渊**：原点 = 精英↔玩家水平中点（Y 取精英 floor）；预警 WATER_SPLASH；落地 SMOKE_LARGE / CRIT_MAGIC；`sinkHit` 每施放每人最多 1 次；名牌 `§6[沉渊]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 扇刃叠伤 | fanHit 每施放每人最多 1 次（跨三带） |
| 沉渊叠伤 | sinkHit 每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixFan` / `AffixSink` | 新原语 |
| `EmberAffixes` | 注册 34 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 fan/sink |
| TrMenu | 无硬编码 32 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack16FanSink_D12220`；门禁 affixpack16 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
