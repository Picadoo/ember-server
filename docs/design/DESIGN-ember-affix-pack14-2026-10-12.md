# Ember Variety Affix Pack 14：剪切 / 夹击（D12218，2026-10-12）

> 状态：**CoreRpg 1.65.736-d12218.local / balance_version 101**（routine-0010）。门禁 `tools/p1sim/affixpack14.py` → `tools/p1sim/out-affixpack14-d12218.md`。
> 前置：D12217 Affix Pack 13（枪刺/新星，池 26→28）。
> 范围：只动词缀池（28 → 30）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 0. 一段话裁决

重打已首通普通主线时，发光词缀精英多两种：**剪切**（`shear`，预警开始时锁定朝向最近玩家的两侧平行细带，中间走廊可躲；预警 ≥1.4 s，周期 ≥6.5 s，长 ≤6、宽 ≤1.2、间距 1.0–3.0，≤0.55×atk，每人每施放最多 1 次）和**夹击**（`brace`，精英脚下与最近玩家位置同时落圈，预警锁定；预警 ≥1.3 s，周期 ≥7 s，半径 ≤1.6，≤0.5×atk，每人每施放最多 1 次）。击败奖励仍是 `affix_shard: 2`。

## R. 参考研究

| 参考 | 原作玩家在做什么 | 我们拿什么 | 我们不拿什么 |
|---|---|---|---|
| Diablo/PoE dual-line / flanking strips | 站中间走廊或外侧 | **剪切**：双平行细带、预警锁朝向 | 穿墙瞬移 / reflect |
| Diablo/PoE pin / sandwich circles | 错开双落点 | **夹击**：精英+玩家双圈 | 全屏无预警 / root |
| Pack5–13 否决表 | reflect / waller / teleporter / avenger / orbiter | — | 维持否决 |

## 1. 规则（yml）

```yaml
  affixes: […, lance, nova, shear, brace]
  shear: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, gap: 2.0, dmg: 0.55}
  brace: {every: 7.0, warn: 1.3, radius: 1.6, dmg: 0.5}
```

钳位：shear every≥6.5、warn≥1.4、length≤6、width≤1.2、gap 1.0–3.0、dmg≤0.55；brace every≥7、warn≥1.3、radius≤1.6、dmg≤0.5。未知 id 丢弃。等权。

- **剪切**：原点 = 精英脚底；朝向 = 预警开始时最近玩家（水平单位向量）；左右条带原点偏移 ±gap/2 垂直方向；预警 CRIT 条带；落地 CRIT；`shearHit` 每施放每人最多 1 次；名牌 `§6[剪切]`。
- **夹击**：原点 = 精英脚底 + 玩家脚底（预警锁定）；预警 SPELL_INSTANT；落地 SPELL_WITCH / END_ROD；`braceHit` 每施放每人最多 1 次；名牌 `§6[夹击]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 剪切叠伤 | shearHit 每施放每人最多 1 次（跨两侧带） |
| 夹击叠伤 | braceHit 每施放每人最多 1 次（跨双圈） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixShear` / `AffixBrace` | 新原语 |
| `EmberAffixes` | 注册 30 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 shear/brace |
| TrMenu | 无硬编码 28 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack14ShearBrace_D12218`；门禁 affixpack14 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
