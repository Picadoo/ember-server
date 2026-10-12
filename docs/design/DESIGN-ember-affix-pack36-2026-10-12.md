# Ember Variety Affix Pack 36：裂斩 / 澳洼（D12240，2026-10-12）

> 状态：**CoreRpg 1.65.758-d12240.local / balance_version 123**（routine-1117）。门禁 `tools/p1sim/affixpack36.py` → `tools/p1sim/out-affixpack36-*.md`。
> 前置：D12239 Affix Pack 35（撕斩/湾洼，池 70→72）。
> 范围：只动词缀池（72 → 74）。房间事件、奖励、掉落、首通/挑战/深渊/团本/连战/挂机、资产路径、首领招式：**不动**。无 Pack5 房间事件、无永久乘区。

## 1. 词缀

```yaml
affixes: […, rend, gulf, rip, cove]
rip: {every: 6.5, warn: 1.4, length: 6.0, width: 1.2, dmg: 0.55}
cove: {every: 7.0, warn: 1.3, radius: 2.0, dmg: 0.5}
```

钳位：rip every≥6.5、warn≥1.4、length≤6、width≤1.2、dmg≤0.55；cove every≥7、warn≥1.3、radius≤2.0、dmg≤0.5。未知 id 丢弃。等权。

- **裂斩**：原点 = 玩家脚底（预警锁定）；朝向 = **精英**视角 yaw **+270°** 吸附最近 8 向（k·π/4）；预警 CRIT；落地 CRIT / SWEEP_ATTACK；`ripHit` 每施放每人最多 1 次；名牌 `§6[裂斩]`。精英朝向偏移族：slash(=+0) → carve(+45) → score(+90) → etch(+135) → sever(+180) → rend(+225) → rip(+270)。
- **澳洼**：原点 = 精英脚底沿精英→玩家 **2.125**（略过 gulf 2.0 / chasm 1.875 / crater 1.75 / pit 1.625 / hollow 1.5 / slade 1.375 …）；预警 SMOKE_NORMAL；落地 SMOKE_NORMAL / CRIT_MAGIC；`coveHit` 每施放每人最多 1 次；**无定身**；名牌 `§6[澳洼]`。

## 2. 漏洞审查

| 风险 | 处理 |
|---|---|
| 双领 / 改奖励 | affix_shard 仍 2；无新发放点 |
| 复制 | 无实体/方块/物品 → 无资产面 → skip persist-roundtrip |
| 周上限 / 重试滥用 | 奖励路径未改 |
| 裂斩叠伤 | ripHit 每施放每人最多 1 次 |
| 澳洼叠伤 | coveHit 每施放每人最多 1 次 |
| 澳洼变禁锢 | 无 root/CC（≠ jailer） |
| 挑战/深渊 | 闸门不变（已首通普通重打） |

## 3. Java 触点

| 文件 | 改动 |
|---|---|
| `AffixRip` / `AffixCove` | 新原语 |
| `EmberAffixes` | 注册 74 |
| `EmberRunMaps.Variety` | KNOWN + 字段钳位 + label |
| `EmberRunDirector` | affixTick + helpers |
| `EmberAffixExportTest` | 导出 rip/cove |
| TrMenu | 无硬编码列表 → 不改 |

## 4. 明确不做

新怪、额外掉落、伤害乘区、新货币、Pack5 房间事件、六槽、化妆品、改 affix_shard/rate、首领招式。

## 5. 验收

单测 `varietyPack36RipCove_D12240`；门禁 affixpack36 within range；冒烟 FreshQ1104/1105。

STATUS: **shipped**
