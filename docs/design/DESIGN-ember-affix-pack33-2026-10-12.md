# Ember Variety Affix Pack 33：蚀斩 / 陨洼（D12237，2026-10-12）

> 状态：**CoreRpg 1.65.755-d12237.local / balance_version 120**（routine-0947）。门禁 `tools/p1sim/affixpack33.py` → `tools/p1sim/out-affixpack33-*.md`。
> 前置：D12236 Affix Pack 32（刻斩/坑洼，池 64→66）。
> 范围：只动词缀池（66 → 68）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, score, pit, etch, crater]
etch: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
crater: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：etch every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；crater every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **蚀斩**：原点 = 玩家脚底（预警锁定）；朝向 = **精英**视角 yaw **+135°** 吸附最近 8 向（k·π/4）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`etchHit` 每施放每人最多 1 次；名牌 `§6[蚀斩]`。精英朝向偏移族：slash(=+0) → carve(+45) → score(+90) → etch(+135)。
- **陨洼**：原点 = 精英脚底沿精英→玩家 **1.75**（略过 pit 1.625 / hollow 1.5 / slade 1.375 …）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`craterHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[陨洼]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 蚀斩叠伤 | etchHit 每施放每人最多 1 次 |
| 陨洼叠伤 | craterHit 每施放每人最多 1 次 |
| 陨洼变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixEtch` / `AffixCrater` | 新原语 |
| `EmberAffixes` | 注册 68 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 etch/crater |
| TrMenu | 无硬编码列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack33EtchCrater_D12237`；门禁 affixpack33 within range；冒烟 FreshQ1098/1099。

STATUS: **shipped**
