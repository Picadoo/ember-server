# Ember Variety Affix Pack 27：剪斩 / 谷地（D12231，2026-10-12）

> 状态：**CoreRpg 1.65.749-d12231.local / balance_version 114**（routine-0649）。门禁 `tools/p1sim/affixpack27.py` → `tools/p1sim/out-affixpack27-*.md`。
> 前置：D12230 Affix Pack 26（削斩/峡洼，池 52→54）。
> 范围：只动词缀池（54 → 56）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, chip, glen, snip, vale]
snip: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
vale: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：snip every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；vale every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **剪斩**：原点 = 玩家脚底（预警锁定）；朝向 = **玩家**视角 yaw **+45°** 吸附最近 8 向（k·π/4）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`snipHit` 每施放每人最多 1 次；名牌 `§6[剪斩]`。相对 gleam（玩家 yaw）/ chip（玩家 yaw+90）/ hew（玩家 yaw−90）/ draw（玩家 yaw+180）。
- **谷地**：原点 = 精英脚底沿精英→玩家 **1.0**（玩家脚下；超 glen 0.875 / dell 0.75 / tarn 0.625）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`valeHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[谷地]`。相对 glen/dell/tarn（精英相对偏移）/ jailer（有定身）。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 剪斩叠伤 | snipHit 每施放每人最多 1 次 |
| 谷地叠伤 | valeHit 每施放每人最多 1 次 |
| 谷地变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixSnip` / `AffixVale` | 新原语 |
| `EmberAffixes` | 注册 56 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 snip/vale |
| TrMenu | 无硬编码列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack27SnipVale_D12231`；门禁 affixpack27 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
