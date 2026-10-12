# DESIGN · 工坊日首 CostSoft 预览/glance 诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** OPEN · TIP-AS · 2026-10-12 21:18 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-forge-costsoft-glance-align-2026-10-12.md`](RESEARCH-ember-forge-costsoft-glance-align-2026-10-12.md)  
> **对接 live：** TIP-AR CLOSED live `1.65.783-d12791` · TIP-AK/AL/AM 甲诚实 · EnhanceCostSoft D9569 · UpgradeCostSoft D9601 · RefineCostSoft D9617 · QualityCostSoft D9921 · GearNextHint D307/D299  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 MULT/经济表/日闩键 · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零 Brand/Gem/Convert/Roll soft 本窗 · 零 pity-remain 再叠**

---

## 0. 一句话

让手持下一确定 / Sink 消耗 / 枢纽费用行在日首减负待用时写半价并明示减负，且「料够/还差料」跟真付费一致。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| ForgeService 付费+`!go` 预览 | 已吃 `*CostSoft.apply` | 保持 |
| GearNextHint `*Line` / HeldNext canPay / Sink / Hope 费用 / RunPapi | 全额表价 | **认 ready；soft 费用；尾缀减负；canPay 同 peek** |
| MULT / C_USED / 经济表 | 0.5 / 日闩 | **零改** |
| 甲路径 | AK/AM 早退 | **本窗不动（禁再扫）** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// peek only — never consume from glance
softCost(kind, pd, full):
  if pd==null || full==null: return full
  switch kind:
    enhance → EmberEnhanceCostSoft.apply(pd, full, false)
    quality → EmberQualityCostSoft.apply(pd, full, false)
    refine  → EmberRefineCostSoft.apply(pd, full, false)
    upgrade → EmberUpgradeCostSoft.apply(pd, full, false)
  // ready=false → apply returns full

GearNextHint.enhanceLine(d, pd):
  plan = enhanceCheck(d); if !ok → error
  cost = softCost(enhance, pd, plan.cost)
  base = "+" + enh + "→+" + t + " · " + costShort(cost) + " · " + pct + "%（" + pity + "）"
  if EnhanceCostSoft.ready(pd) && plan.ok(): return base + " · 今日减负待用"
  return base
// qualityLine / craftLine / upgradeLine 同构：费用用 soft；ready 尾缀「今日减负待用」
// 旧一元 *Line(d) / upgradeLine(d,gate) ≡ *Line(d, null) — 全额回归

ForgeHeldNext.line(d, m, gateOk, goal, pd):
  // 甲早退不变（TIP-AK）
  // 对 quality/refine/enhance/upgrade 候选：Cost 先 softCost peek 再 canPay/shortfall/展示
  // 展示串改调 soft-aware GearNextHint；尾缀已在 Line 内
  // brand/gem/convert/lock/roll 本窗仍全额（旁债）

SinkGlance 非甲：*Line(d, pd)
HopeMeter.quality/refine：有 pd 时 costShort(soft) + 可选「 · 今日减负待用」
RunPapi *_next_* / held_*_cost：传 pd → soft-aware 重载
```

- **复用** 既有 `*CostSoft.ready` / `apply(..., false)`；**禁止**改 MULT / C_USED / 付费 `consume=true` 路径。  
- ForgeService enhance/simple **本窗不改接线**（已诚实）。  
- **禁止**新 `%corerpg_p1_*` / 新 remain 族 / TrMenu 加 lore / 抬表。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `GearNextHint` 四 Line 重载 | 增 pd 重载；旧一元委托 null |
| `ForgeHeldNext.line` | 增 pd 重载；ForgeService/Command 下传 pd；测试无 pd 回归 |
| `SinkGlance.tellHeld` 非甲 | 喂 soft-aware Line；**甲早退不动** |
| `HopeMeter.quality/refine` | 可选 pd 重载或 tracks 构建处喂 soft costShort |
| `EmberRunPapi` next/cost | 喂 pd |
| ForgeService 付费/预览 | **本窗不改** |
| Brand/Gem/Convert/Roll soft · TrMenu lore · 甲闸 · SetChase · haveNeed | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| enhance 全额 碎12 · soft ready · pd | enhanceLine 含半价（≥6）+「今日减负待用」 |
| 同上 · soft 已用 | 全额；**无**减负尾 |
| enhanceLine(d) 一元 | 与 D307 旧断言一致 |
| quality/craft/upgrade soft ready | 半价 + 减负尾 |
| HeldNext：袋料只够半价 · soft ready | 「料够」路径（非「还差料」） |
| HeldNext：甲 | ARMOR_REFUSE 回归（TIP-AK） |
| SinkGlance 非甲 soft ready | 费用串含减负尾 |
| `git show` | 无 MULT/经济表/STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无 Brand/Gem soft 改 MULT |

### 2.4 玩家口径（固定）

- 今日工坊强化/成色/精工/升阶减负还没用：手持下一确定、Sink 消耗、枢纽费用行会写 **半价** 并标 **今日减负待用**；「料够/还差料」按半价算。  
- 今日减负已用：跟以前全额一样。  
- 减负仍是 ×0.5、一日一次；强化率/保底/表价本体本窗不动。  
- 护甲手持：仍「护甲随护符」早退，不投影成色/精工/强化费用。

---

## 3. 验收

- [ ] soft ready：HeldNext/Sink/hub 费用半价 + 今日减负待用  
- [ ] soft ready + 袋料仅够半价：HeldNext 报料够（非还差料）  
- [ ] soft 已用 / 一元无 pd 回归  
- [ ] 甲早退回归；无新 PAPI / lore≤3 不加成行  
- [ ] 未改 MULT / 经济表 / 付费序 / stampable / L11·L13 / 甲闸 / SetChase / pity  
- [ ] L13 施工仍 HOLD  

---

## 4. 批后派单

**余烬-插件**（CoreRpg · `EmberGearNextHint` soft-aware Line · `EmberForgeHeldNext` pd/canPay · `EmberSinkGlance` / `EmberHopeMeter` quality·refine 费用 · `EmberRunPapi` next/cost + 单测）

策划本窗：docs-only（零业务 jar）

---

## 5. HOLD

- **L13 施工 HOLD**（TIP-AI FAIL · 显式开工前不开施工 tip）  
- 刃 L11/L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 densify / glance twin / 深化 densify / ForgeChoice densify / Affix / 伤乘 / 抬表 / sx20 / skill_mult / STAMP  
- Brand/Gem/Convert/ForgeRoll CostSoft glance 旁债；pity-remain 再叠禁  

## STATUS: OPEN · TIP-AS · 2026-10-12
