# Ember Variety Affix Pack 23：扫斩 / 洼池（D12227，2026-10-12）

> 状态：**CoreRpg 1.65.745-d12227.local / balance_version 110**（routine-0445）。门禁 `tools/p1sim/affixpack23.py` → `tools/p1sim/out-affixpack23-d12227.md`。
> 前置：D12226 Affix Pack 22（掠斩/泥潭，池 44→46）。
> 范围：只动词缀池（46 → 48）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, gleam, bog, slash, basin]
slash: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
basin: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：slash every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；basin every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **扫斩**：原点 = 玩家脚底（预警锁定）；朝向 = **精英**视角 yaw 吸附最近 8 向（k·π/4，**非**精英→玩家、**非**玩家视角）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`slashHit` 每施放每人最多 1 次；名牌 `§6[扫斩]`。相对 gleam（玩家 yaw）/ spur（精英→玩家八向）/ seam（正四）/ ridge（对角）/ edge（自由）/ crest（垂直）/ lance（精英射线）。
- **洼池**：原点 = 精英脚底沿精英→玩家 **0.5**（超 bog 0.375 / fen 0.125 / well 0.25）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`basinHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[洼池]`。相对 bog/fen/well/mire（精英相对偏移）/ marsh（玩家脚底）/ jailer（有定身）。id 用 `basin`（避 `pool` 歧义）。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 扫斩叠伤 | slashHit 每施放每人最多 1 次 |
| 洼池叠伤 | basinHit 每施放每人最多 1 次 |
| 洼池变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixSlash` / `AffixBasin` | 新原语 |
| `EmberAffixes` | 注册 48 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 slash/basin |
| TrMenu | 无硬编码 46 列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack23SlashBasin_D12227`；门禁 affixpack23 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
