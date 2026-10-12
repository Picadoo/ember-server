# DESIGN · Brand/Gem/Convert/Roll 日首 CostSoft 预览/glance 诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AT 批 A·M · 已派插件 · 2026-10-12 21:31 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-brand-gem-convert-roll-costsoft-glance-align-2026-10-12.md`](RESEARCH-ember-brand-gem-convert-roll-costsoft-glance-align-2026-10-12.md)  
> **对接 live：** TIP-AS CLOSED live `1.65.784-d12807` · BrandCraftSoft D9697 · BrandPinCostSoft D9729 · ConvertCostSoft D9761 · ForgeRollCostSoft D9889 · GemCraftCostSoft D10657 · GearNextHint softCost/SOFT_TAIL（TIP-AS）  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 MULT/经济表/日闩键 · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零再叠锻四软 densify · 零 pity-remain 再叠 · 零为 LOCK 发明 soft**

---

## 0. 一句话

让手持下一确定 / Sink 缺料 / 枢纽仓差在烙纹·宝石·转化·随机锻（及烙纹定向）日首减负待用时写半价并明示减负，且「料够/还差料」跟真付费一致。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| GrowthService / GemAcquire / RunService forgeroll 付费+命令预览 | 已吃 soft | 保持 |
| ForgeHeldNext brand/gem/convert/roll canPay | 全额 | **soft peek；ready 可标减负尾** |
| SinkGlance goal BRAND/CONVERT/ROLL lack | 全额 | **soft peek** |
| RunPapi forge_gap_* / recipe_gap_gem | 全额 | **soft 算差** |
| MULT / C_USED / 表价 | 0.5 / 日闩 | **零改** |
| LOCK / 甲路径 | 无 soft / AK–AM 早退 | **本窗不动** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// peek only — never consume from glance
// extend GearNextHint.softCost kinds (or sibling helpers):
softCost(kind, pd, full):
  ... existing enhance/quality/refine/upgrade ...
  brand   → Cost(BrandCraftSoft.apply(pd, full.shards, false), 0,0,0,0)  // shards-only craft
  gem     → GemCraftCostSoft.peek(pd, full)
  convert → Apply ConvertCostSoft to blanks+coins fields of full
  roll    → ForgeRollCostSoft.ready(pd) ? softCost(full) : full
  // ready=false / pd=null → full

softPin(pd, brands, coins): BrandPinCostSoft.apply(pd, brands, coins, false)  // for forge_gap_pin

ForgeHeldNext.line(..., pd):
  // 甲早退不变（TIP-AK）
  // 锻四软已 soft（TIP-AS）
  conv  = softCost("convert", pd, ConvertRules.cost(tier))
  brand = softCost("brand", pd, Cost(craftShards(),0,0,0,0))
  gem   = softCost("gem", pd, GemAcquire.craftCost())
  roll  = softCost("roll", pd, ForgeRollRules.matCost())
  lock  = lockRerollCost(...)  // 仍全额 · 本窗不 soft
  // canPay/shortfall 用上列 soft Cost
  // ready 时返回「料够」串可加「 · 今日减负待用」（复用 SOFT_TAIL）

SinkGlance goal BRAND/CONVERT/ROLL(/GEM if wired):
  cost = softCost(kind, pd, full); lack = lackHalf(lackingFor(cost))

RunPapi forge_gap_brand|roll|roll_floor|convert|pin / recipe_gap_gem:
  有 pd → soft 后算 recipeGap；无 pd → 全额回归
```

- **复用** 既有 soft `ready` / peek·apply(false)；**禁止**改 MULT / C_USED / 付费 `consume=true`。  
- GrowthService / GemAcquire / RunService forgeroll **本窗不改接线**（已诚实）。  
- **禁止**新 `%corerpg_p1_*` / 新 remain 族 / TrMenu 加 lore / 抬表 / 为 LOCK 发明 soft。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `GearNextHint.softCost` | 增 brand/gem/convert/roll；可选 `softPin` |
| `ForgeHeldNext.line` | brand/gem/convert/roll soft peek canPay；LOCK 全额；甲早退不动 |
| `SinkGlance.tellHeld` | BRAND/CONVERT/ROLL（+GEM 若有）soft lack；甲早退不动 |
| `EmberRunPapi` forge_gap_* / recipe_gap_gem | 喂 pd soft |
| GrowthService / GemAcquire / forgeroll 付费预览 | **本窗不改** |
| 锻四软 Line / 甲闸 / SetChase / TrMenu lore / LOCK soft | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| BrandCraft soft ready · 袋碎仅够半价 | HeldNext BRAND →「料够」（非还差碎）；可含减负尾 |
| 同上 · soft 已用 | 「还差」按全额 |
| Gem soft ready | HeldNext GEM / recipe_gap_gem 半价差 |
| Convert / Roll soft ready | Sink lack / forge_gap_* 半价 |
| Pin soft ready | forge_gap_pin 半价差 |
| LOCK / 一元无 pd | 全额回归 |
| 甲 | ARMOR_REFUSE 回归（TIP-AK/AM） |
| `git show` | 无 MULT/经济表/STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无锻四软 MULT 改 |

### 2.4 玩家口径（固定）

- 今日烙纹合成 / 烙纹定向 / 宝石合成 / 跨族转化 / 随机锻减负还没用：手持下一确定、Sink 缺料、枢纽仓差按 **半价** 算，并标 **今日减负待用**（有费用/仓差可见处）。  
- 今日减负已用：跟以前全额一样。  
- 减负仍是 ×0.5、一日一次；各轨表价本体本窗不动。  
- 锁定洗练：仍全额（本窗不发明 soft）。  
- 护甲手持：仍「护甲随护符」早退。

---

## 3. 验收

- [ ] soft ready：HeldNext BRAND/GEM/CONVERT/ROLL 「料够」跟半价一致；可含今日减负待用  
- [ ] Sink / forge_gap_* / recipe_gap_gem soft ready：缺料/仓差按半价  
- [ ] soft 已用 / 无 pd / LOCK 全额回归  
- [ ] 甲早退回归；无新 PAPI / lore≤3 不加成行  
- [ ] 未改 MULT / 经济表 / 付费序 / stampable / L11·L13 / 甲闸 / SetChase / pity / 锻四软  
- [ ] L13 施工仍 HOLD  

---

## 4. 批后派单

**余烬-插件**（CoreRpg · `EmberGearNextHint` softCost 扩 brand/gem/convert/roll(+pin) · `EmberForgeHeldNext` 四轨 soft canPay · `EmberSinkGlance` BRAND/CONVERT/ROLL soft lack · `EmberRunPapi` forge_gap_*/recipe_gap_gem soft + 单测）

策划本窗：docs-only（零业务 jar）

---

## 5. HOLD

- **L13 施工 HOLD**（TIP-AI FAIL · 显式开工前不开施工 tip）  
- 刃 L11/L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 densify / glance twin / 深化 densify / ForgeChoice densify / Affix / 伤乘 / 抬表 / sx20 / skill_mult / STAMP  
- LOCK 发明 soft；锻四软再 densify；pity-remain 再叠禁  

## STATUS: OPEN · TIP-AT · 2026-10-12
