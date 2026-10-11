# Ember Variety Affix Pack 24：曳斩 / 山洼（D12228，2026-10-12）

> 状态：**CoreRpg 1.65.746-d12228.local / balance_version 111**（routine-0516）。门禁 `tools/p1sim/affixpack24.py` → `tools/p1sim/out-affixpack24-d12228.md`。
> 前置：D12227 Affix Pack 23（扫斩/洼池，池 46→48）。
> 范围：只动词缀池（48 → 50）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, slash, basin, draw, tarn]
draw: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
tarn: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：draw every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；tarn every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **曳斩**：原点 = 玩家脚底（预警锁定）；朝向 = **玩家**视角 yaw **+180°** 吸附最近 8 向（k·π/4，**对立于** gleam 玩家 yaw）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`drawHit` 每施放每人最多 1 次；名牌 `§6[曳斩]`。相对 gleam（玩家 yaw）/ slash（精英 yaw）/ spur（精英→玩家八向）/ seam（正四）/ ridge（对角）/ edge（自由）/ crest（垂直）/ lance（精英射线）。
- **山洼**：原点 = 精英脚底沿精英→玩家 **0.625**（超 basin 0.5 / bog 0.375 / fen 0.125 / well 0.25）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`tarnHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[山洼]`。相对 basin/bog/fen/well/mire（精英相对偏移）/ marsh（玩家脚底）/ jailer（有定身）。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 曳斩叠伤 | drawHit 每施放每人最多 1 次 |
| 山洼叠伤 | tarnHit 每施放每人最多 1 次 |
| 山洼变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixDraw` / `AffixTarn` | 新原语 |
| `EmberAffixes` | 注册 50 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 draw/tarn |
| TrMenu | 无硬编码 48 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack24DrawTarn_D12228`；门禁 affixpack24 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
