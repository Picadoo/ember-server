# Ember Variety Affix Pack 29：锉斩 / 幽洼（D12233，2026-10-12）

> 状态：**CoreRpg 1.65.751-d12233.local / balance_version 116**（routine-0742）。门禁 `tools/p1sim/affixpack29.py` → `tools/p1sim/out-affixpack29-*.md`。
> 前置：D12232 Affix Pack 28（切斩/谷原，池 56→58）。
> 范围：只动词缀池（58 → 60）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, nick, dale, rasp, combe]
rasp: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
combe: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：rasp every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；combe every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **锉斩**：原点 = 玩家脚底（预警锁定）；朝向 = **玩家**视角 yaw **+135°** 吸附最近 8 向（k·π/4）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`raspHit` 每施放每人最多 1 次；名牌 `§6[锉斩]`。相对 gleam(+0) / snip(+45) / chip(+90) / hew(−90) / draw(+180) / nick(−45) / rasp(+135)。
- **幽洼**：原点 = 精英脚底沿精英→玩家 **1.25**（略过玩家；超 dale 1.125 / vale 1.0 / glen 0.875 / dell 0.75 / tarn 0.625）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`combeHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[幽洼]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 锉斩叠伤 | raspHit 每施放每人最多 1 次 |
| 幽洼叠伤 | combeHit 每施放每人最多 1 次 |
| 幽洼变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixRasp` / `AffixCombe` | 新原语 |
| `EmberAffixes` | 注册 60 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 rasp/combe |
| TrMenu | 无硬编码列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack29RaspCombe_D12233`；门禁 affixpack29 within range；冒烟 POLICY batch（mineflayer 缺则 defer）。

STATUS: **shipped**
