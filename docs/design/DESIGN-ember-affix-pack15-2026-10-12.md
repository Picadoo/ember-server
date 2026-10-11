# Ember Variety Affix Pack 15：钩落 / 斜切（D12219，2026-10-12）

> 状态：**CoreRpg 1.65.737-d12219.local / balance_version 102**（routine-0040）。门禁 `tools/p1sim/affixpack15.py` → `tools/p1sim/out-affixpack15-d12219.md`。
> 前置：D12218 Affix Pack 14（剪切/夹击，池 28→30）。
> 范围：只动词缀池（30 → 32）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**钩落**（`hook`，预警开始时落点在最近玩家身前沿精英→玩家轴 reach 格处；预警 ≥1.3 s，周期 ≥7 s，半径 ≤1.5、reach 1.0–3.0，≤0.5×atk，每人每施放最多 1 次）和**斜切**（`skew`，预警锁定朝向，精英脚下 ±45° 双细带穿过；预警 ≥1.4 s，周期 ≥6.5 s，长 ≤6、宽 ≤1.2，≤0.55×atk，每人每施放最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| Diablo/PoE foreshadow / ahead circle | 后退或侧移 | **钩落**：玩家身前落圈 | 瞬移贴脸 / root |
| Diablo/PoE diagonal cross / X-strips | 站空隙 | **斜切**：相对朝向 ±45° 双带 | 轴对齐十字（已有 venom） |
| Pack5–14 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, shear, brace, hook, skew]
  hook: {every: 7.0, warn: 1.3, radius: 1.5, reach: 2.0, dmg: 0.5}
  skew: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
```

钳位：hook every≥7、warn≥1.3、radius≤1.5、reach 1.0–3.0、dmg≤0.5；skew every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55。未知 id 丢弃。等权。

- **钩落**：原点 = 玩家脚底 + 精英→玩家水平单位向量 × reach；预警 CLOUD；落地 CLOUD / CRIT_MAGIC；`hookHit` 每施放每人最多 1 次；名牌 `§6[钩落]`。
- **斜切**：原点 = 精英脚底；朝向 = 预警开始时最近玩家；双带方向 = 朝向 ±45°；预警 SWEEP_ATTACK；落地 SWEEP / CRIT；`skewHit` 每施放每人最多 1 次（跨两侧带）；名牌 `§6[斜切]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 钩落叠伤 | hookHit 每施放每人最多 1 次 |
| 斜切叠伤 | skewHit 每施放每人最多 1 次（跨双带） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixHook` / `AffixSkew` | 新原语 |
| `EmberAffixes` | 注册 32 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 hook/skew |
| TrMenu | 无硬编码 30 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack15HookSkew_D12219`；门禁 affixpack15 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
