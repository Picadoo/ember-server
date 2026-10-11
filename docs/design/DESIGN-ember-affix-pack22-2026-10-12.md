# Ember Variety Affix Pack 22：掠斩 / 泥潭（D12226，2026-10-12）

> 状态：**CoreRpg 1.65.744-d12226.local / balance_version 109**（routine-0419）。门禁 `tools/p1sim/affixpack22.py` → `tools/p1sim/out-affixpack22-d12226.md`。
> 前置：D12225 Affix Pack 21（刺斩/沼洼，池 42→44）。
> 范围：只动词缀池（44 → 46）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, spur, fen, gleam, bog]
gleam: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
bog: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：gleam every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；bog every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **掠斩**：原点 = 玩家脚底（预警锁定）；朝向 = 玩家视角 yaw 吸附最近 8 向（k·π/4，**非**精英→玩家）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`gleamHit` 每施放每人最多 1 次；名牌 `§6[掠斩]`。相对 spur（精英→玩家八向）/ seam（正四）/ ridge（对角）/ edge（自由）/ crest（垂直）/ lance（精英射线）。
- **泥潭**：原点 = 精英脚底沿精英→玩家 0.375（超 fen 0.125 / well 0.25）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`bogHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[泥潭]`。相对 fen/well/mire（精英相对偏移）/ marsh（玩家脚底）/ jailer（有定身）。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 掠斩叠伤 | gleamHit 每施放每人最多 1 次 |
| 泥潭叠伤 | bogHit 每施放每人最多 1 次 |
| 泥潭变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixGleam` / `AffixBog` | 新原语 |
| `EmberAffixes` | 注册 46 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 gleam/bog |
| TrMenu | 无硬编码 44 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack22GleamBog_D12226`；门禁 affixpack22 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
