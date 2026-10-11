# Ember Variety Affix Pack 6：雹霰 / 牵丝（D12207，2026-10-11）

> 状态：**CoreRpg 1.65.725-d12207.local / balance_version 90**（routine-1811）。门禁 `tools/p1sim/affixpack6.py` → `tools/p1sim/out-affixpack6-d12207.md`。
> 前置：D196 Affix Pack 5（旋光/火链，池 10→12）、D189 Pack 4、D181 Pack 3。
> 范围：只动词缀池（12 → 14）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径：**不动**。无 Pack6 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**雹霰**（`hail`，精英朝最近玩家方向前方落下 3 个半径 ≤1.5 的小圈，预警 ≥1.2 s，周期 ≥5 s，每圈 ≤0.7×atk，每人每施放最多中 1 次）和**牵丝**（`tether`，随机一名射程内玩家脚下亮紫圈，预警后轻拉 + 定身 ≤0.8 s，≤0.4×atk）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| D3 Frozen / Orbiter | 延时冰爆圈 / 环绕球 | **雹霰**：多点延时小圈（mortar 多弹），锥形 ahead | Orbiter（旋光已覆盖）；大范围永久冰地 |
| D3 Vortex / Jailer | 拉人真空 / 定身圈 | **牵丝**：轻拉 + 短定身（jailer 软化） | 硬真空秒杀、长定身、造墙 |
| 现网 mortar / jailer | 单圈脚下 / 定身 1 s | 复用 tick 骨架（warn→land、kb 0、skillHit） | 改奖励 / 加怪 |
| Pack5 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, arcane, firechain, hail, tether]
  hail: {every: 5.0, warn: 1.2, radius: 1.5, count: 3, ahead: 3.0, spread: 2.0, dmg: 0.7}
  tether: {every: 7.0, warn: 1.2, radius: 1.8, root: 0.8, dmg: 0.4, pull: 0.8}
```

钳位：hail every≥5、warn≥1.2、radius≤1.5、dmg≤0.7、count 2–3；tether every≥7、warn≥1.2、radius≤1.8、root≤0.8、dmg≤0.4。未知 id 丢弃。

- **雹霰**：原点 = 精英朝目标 ahead 锥内 3 点；预警 SNOWBALL/WATER_SPLASH；落地 CRIT；`hailHit` 每施放每人最多 1 次。名牌 `§6[雹霰]`。
- **牵丝**：随机射程内玩家脚下；PORTAL/SPELL_WITCH；落地轻拉 + SLOW/JUMP 定身。名牌 `§6[牵丝]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 |
| 雹霰叠伤 | hailHit 每施放每人最多 1 次（跨 3 圈） |
| 牵丝真空杀 | pull≤1.5、root≤0.8、dmg≤0.4；无持续吸 |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixHail` / `AffixTether` | 新原语 |
| `EmberAffixes` | 注册 14 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 hail/tether |
| TrMenu | 无硬编码 12 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack6 房间事件、六槽、化妆品、改 affix_shard/rate。

## 5. 验收

单测 `varietyPack6HailTether_D12207`；`EmberEventRoomWeightTest` 仍绿；门禁 affixpack6 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
