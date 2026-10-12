# DESIGN · 生活首钓/烹饪日首加验预告诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** CLOSED live `1.65.793-d12951` · TIP-BB · feat `6f7d01e91` · 2026-10-12 22:46 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-fish-cook-xp-boost-cue-align-2026-10-12.md`](RESEARCH-ember-fish-cook-xp-boost-cue-align-2026-10-12.md)  
> **对接 live：** TIP-BA CLOSED live `1.65.792-d12935` · feat `366b0a645` · FishXpBoost D8865 · CookXpBoost D8961 · `LifeService.show` · `life.yml` `fish_xp: 2` · cook.xp_each  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 MULT/C_USED/fish_xp/cook.xp_each · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify · 零 topUpTo 扩 soft**

---

## 0. 一句话

让生活页在日首钓鱼/烹饪加验仍待用时说清楚「今日还能 ×2」；打开列表不得消耗加验。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| FishXpBoost | 成钓才 ×2 + 喊话 | **show ready 预告；consume 仍仅成钓** |
| CookXpBoost | 成烤才 ×2 + 喊话 | **cook 行 ready 预告；consume 仍仅成烤** |
| `LifeService.show` | 无加验尾 | **ready →「今日首钓/烹饪加验×2待用」** |
| MULT / fish_xp / xp_each | 2 / 2 / 表 | **零改** |
| soft-latch / Anvil / Cap | BA 已齐 / 硬禁 | **本窗不改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// 既有 — 零改 API 语义
EmberFishXpBoost.ready(d) / consumeXp(d, base)   // consume 仍仅 PlayerFishEvent 成功路径
EmberCookXpBoost.ready(d) / consumeXp(d, base)   // consume 仍仅 cook 成交路径

LifeService.show (伪码):
  head = "—— 枢纽补给 · 生活 —— … 经验 xp/next …"
  if FishXpBoost.ready(d):
    head += " · 今日首钓加验×2待用"   // 或紧随头行下一短句；禁 latch
  …
  cookLine = " cook — 代烤鱼…币"
  if CookCoinSoft.ready: cookLine += " · 今日减负待用（×0.5）"  // 已有 AY
  if CookXpBoost.ready(d):
    cookLine += " · 今日烹饪加验×2待用"
  …
  footer 可保留；禁新 PAPI / TrMenu lore
```

- **复用** 既有 Boost；**禁止**改 `MULT`/`C_USED`/`fish_xp`/`cook.xp_each`。  
- **禁止**新 `%corerpg_p1_*` / TrMenu 加 lore。  
- soft-latch / Anvil / Cap / CostSoft / pity / Supply **本窗不改接线**。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `LifeService.show` | Fish/Cook ready 尾；零 consume |
| `EmberFishXpBoost` / `EmberCookXpBoost` | **本窗可不改类**（仅 read ready）；可选常量文案抽到 Boost 旁 `CUE` 短串便于单测 |
| soft-latch / Anvil / Cap / CostSoft / pity / 甲闸 / SetChase / TrMenu | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| Fish ready · show 文案含「首钓加验×2待用」 | `usedToday=false`（未 consume） |
| Cook ready · cook 行含「烹饪加验×2待用」 | 未 consume |
| Fish 已用 · show | **无**首钓加验尾 |
| Cook 已用 · cook 行 | **无**烹饪加验尾（币减负尾仍按 AY） |
| `git show` | 无 MULT/C_USED/fish_xp/cook.xp_each 改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无 soft-latch/CostSoft/pity/Cap/firstsoft densify；无 Anvil/topUpTo soft densify |

### 2.4 玩家口径（固定）

- 今日还没钓成过：生活页可见 **今日首钓加验×2待用**；钓成才消耗，喊「首钓加验」。  
- 今日还没烤成过：cook 行可见 **今日烹饪加验×2待用**；烤成才消耗。  
- 已用：不再显示待用尾；经验按表额。  
- 加验仍是 ×2、一日一次；`fish_xp` / `cook.xp_each` 本窗不动。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：show 预告 + 单测；`git show` 干净  
- 菜单默认不动；lore≤3  
- show 不吞加验；成钓/成烤才 consume；L13 施工仍 HOLD  

## 4. 旁债（下一 prefer · 非本窗）

1. GemHole soft 门槛展示诚实（孔 glance「锁」vs 日首门槛−1）→ **TIP-BC OPEN**  
2. AnvilRepair Soft 假耗（硬禁 densify 未解冻前不开）  
3. 主线解锁 DESIGN / stampable 空 / L13 HOLD 观察  
4. topUpTo 是否接 soft（须另批 · 现禁 densify）

## STATUS: CLOSED live `1.65.793-d12951` · TIP-BB · 2026-10-12 22:46 Asia/Shanghai · feat `6f7d01e91`
