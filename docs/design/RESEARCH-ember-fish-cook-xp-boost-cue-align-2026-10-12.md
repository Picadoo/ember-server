# RESEARCH · 生活首钓/烹饪日首加验预告诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-BB · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-BA CLOSED live 后总控 **离开 soft-latch/假耗链**，换轴 **可感 UX / 变强途径**。扫 mapid / HopeMeter other / GearNextHint / TrMenu hub / mainline next-ring / wear·boss / **brew·fish non-soft honesty**。本债 = **FishXpBoost + CookXpBoost 日首加验已 live，但生活 show 不预告 → 玩家错过「今日还能多涨一截生活经验」**，**非** soft 假耗 / CostSoft glance / Anvil densify。  
> **上游：** TIP-BA CLOSED live `1.65.792-d12935` · feat `366b0a645` · AZ+BA soft-latch 假耗链已齐 · FishXpBoost D8865 / CookXpBoost D8961 live · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/fish_xp/cook.xp_each · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 AnvilRepair densify · 零 topUpTo 扩 soft densify · 零 LifeCap/UndoWindow/FestivalEntry densify**

---

## 0. 一句话

日首钓鱼/烹饪加验已在结算时生效，生活页应在 **ready 时预告**「今日首钓/烹饪 ×2 待用」，让变强途径可见；show 不得 consume。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberFishXpBoost`：ready→首成钓 `consumeXp` ×`MULT=2`；`fish_xp: 2` → 4 | `EmberFishXpBoost.java` · `life.yml` · `LifeService` ~183 | **加验真存在 · 仅成交后喊** |
| E2 | `EmberCookXpBoost`：ready→首批 cook `consumeXp` ×2；`cook.xp_each` | `EmberCookXpBoost.java` · cook 路径 ~879 | **同上 · 烹饪轴** |
| E3 | `LifeService.show`：头行只报累计 xp/next；cook 行仅 CostSoft「减负待用」；**无** Fish/Cook XP boost ready 尾 | `LifeService.show` ~379–426 | **变强途径静默** |
| E4 | 成交后才有「首钓加验 · 经验 ×2」「烹饪加验 · 经验 ×2」 | LifeService ~186 / ~882 | **事后告知 · 预告缺口** |
| E5 | `ready`/`usedToday`/`consumeXp` 已可测；show 只读 ready **不** latch | Soft 类 | **预告 = 纯展示 · 零改 MULT** |
| E6 | AZ/BA soft-latch 假耗链已齐；Anvil soft / topUpTo 扩 soft / CostSoft·pity·firstsoft·cook soft·life-buy soft 再叠 | 总控硬禁 | **本窗离开假耗族** |
| E7 | mapid 扩甲 haveNeed · SetChase 六槽 · TrMenu 加 lore · HopeMeter densify · GearNextHint 甲闸再扫 | 硬禁 / 已齐 | **否** |
| E8 | tip-J：禁 **新** glance twin；本债 = **改既有 show 串 + ready 尾** | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真能力 | 现行 show | 体感 |
|------|--------|-----------|------|
| FishXpBoost.ready · 打开生活 | 下一竿 +4（表 2×2） | 只见累计 xp · 无预告 | **错过日首变强途径** |
| CookXpBoost.ready · 看 cook 行 | 下一批 ×2 经验 | 只见币减负尾（若有） | **加验静默** |
| 已用今日加验 | 表额 xp | 无尾（对） | **对** |
| show 多次 | ready 不变 | （现无尾） | **预告不得 latch** |

---

## 3. 「对齐」定义（钉死）

1. **复用** 既有 `EmberFishXpBoost.ready` / `EmberCookXpBoost.ready`；**禁**改 `MULT` / `C_USED` / `fish_xp` / `cook.xp_each`。  
2. **`LifeService.show`：** Fish ready → 头行或脚注半句「今日首钓加验×2待用」；Cook ready → cook 行尾「今日烹饪加验×2待用」（可与币减负尾并存，压行）。  
3. **零** consume / 零第二日闩；show 纯 peek ready。  
4. **禁新 PAPI**；**禁** TrMenu 加 lore。  
5. **零** soft-latch 假耗再叠 · Anvil / topUpTo / LifeCap / CostSoft / pity / firstsoft densify。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽  
- soft-latch 假耗再叠 · CostSoft glance 再叠 · pity-remain 再叠 · firstsoft 再叠 · cook soft / life-buy soft 再叠  
- AnvilRepair soft densify · topUpTo 扩 soft · LifeCap / UndoWindow / FestivalEntry densify  
- mapid 扩甲 / TrMenu hub lore / 主线解锁大 DESIGN / stampable 空施工  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  
- 抬 fish_xp / cookXp / MULT（禁「假变强」抬表）

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **生活首钓/烹饪日首加验预告诚实（可施工薄）** | **采纳** — 离开 soft-latch；brew/fish non-soft；变强途径可见；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase · 非 soft 假耗再叠** |
| AnvilRepair Soft 假耗 | **否** — 硬禁 densify |
| topUpTo 扩 soft | **否** — 扩 soft densify |
| LifeCap / LevelGate 容错预告 | **否** — Cap/firstsoft densify |
| GemHole soft 门槛展示 | **旁债→TIP-BC OPEN** — soft 展示邻域；本窗优先 fish/cook non-soft |
| mapid 扩甲 / HopeMeter densify / GearNextHint 甲闸 / TrMenu lore | **否** — 硬禁 / 已齐 |
| 主线解锁 DESIGN / stampable 空 / L13 HOLD 观察 only | **否本窗主轴** — 有可感预告债；观察列旁债 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |

## STATUS: OPEN · TIP-BB · 2026-10-12
