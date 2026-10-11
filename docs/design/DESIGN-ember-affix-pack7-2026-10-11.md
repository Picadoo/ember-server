# Ember Variety Affix Pack 7：脉爆 / 埋雷（D12211，2026-10-11）

> 状态：**CoreRpg 1.65.729-d12211.local / balance_version 94**（routine-2013）。门禁 `tools/p1sim/affixpack7.py` → `tools/p1sim/out-affixpack7-d12211.md`。
> 前置：D12207 Affix Pack 6（雹霰/牵丝，池 12→14）、D196 Pack 5。
> 范围：只动词缀池（14 → 16）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack6 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**脉爆**（`pulse`，精英脚下扩展环，预警 ≥1.2 s，周期 ≥6 s，半径 3.5，≤0.55×atk，每人每施放最多 1 次）和**埋雷**（`mines`，精英身后 2 个小圈，预警 ≥1.4 s，周期 ≥7 s，半径 ≤1.4，≤0.6×atk，每人每施放跨两雷最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| D3 Desecrator / Mortar | 地面延时圈 / 多点落弹 | **脉爆**：精英中心单环 linger 感（mortar tick 骨架） | 永久毒地、叠层 DoT 乘区 |
| D3 Electrified / Arcane | 环绕球 / 旋转光束 | — | **已否决** orbit-lite（旋光已覆盖） |
| 现网 mortar / hail / jailer | 单圈脚下 / 前方多圈 / 定身 | 复用 warn→land、kb 0、skillHit；**埋雷** = hail 反向（behind） | 改奖励 / 加怪 / 新实体掉落 |
| Pack5/6 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, hail, tether, pulse, mines]
  pulse: {every: 6.0, warn: 1.2, radius: 3.5, dmg: 0.55}
  mines: {every: 7.0, warn: 1.4, radius: 1.4, count: 2, behind: 2.5, spread: 1.8, dmg: 0.6}
```

钳位：pulse every≥6、warn≥1.2、radius≤3.5、dmg≤0.55；mines every≥7、warn≥1.4、radius≤1.4、dmg≤0.6、count 2、behind≤4、spread≤3。未知 id 丢弃。等权。

- **脉爆**：原点 = 精英脚下；预警 CRIT + EXPLOSION_LARGE；落地 EXPLOSION_LARGE；`pulseHit` 每施放每人最多 1 次。名牌 `§6[脉爆]`。
- **埋雷**：原点 = 精英朝最近玩家方向的**身后** 2 点（behind + spread）；预警 SMOKE/FLAME；落地 LAVA；`minesHit` 跨两雷每人最多 1 次。名牌 `§6[埋雷]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 |
| 周上限 / 重试滥用 | 奖励路径未改；无 weekly grant |
| 脉爆叠伤 | pulseHit 每施放每人最多 1 次 |
| 埋雷双圈叠伤 | minesHit 跨两雷每施放每人最多 1 次 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixPulse` / `AffixMines` | 新原语 |
| `EmberAffixes` | 注册 16 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 pulse/mines |
| TrMenu | 无硬编码 14 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack6 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack7PulseMines_D12211`；`EmberEventRoomWeightTest` 仍绿；门禁 affixpack7 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
