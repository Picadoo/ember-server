# RESEARCH · 宝石开孔日首门槛 Soft 预告/拒线诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-BC · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-BB CLOSED live 后总控显式旁债 **GemHole soft 门槛展示** — soft 已 live、展示/拒线仍报硬门槛或静默。离开 soft-latch / xp-boost cue 族；**非** densify（禁抬 DELTA / 禁再降门槛）。  
> **上游：** TIP-BB CLOSED live `1.65.793-d12951` · feat `6f7d01e91` · DESIGN-BB 旁债#1 · GemHoleUnlockSoft D10785 live · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 DELTA/C_USED/sockUnlocked 硬表 · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 AnvilRepair densify · 零 topUpTo 扩 soft densify · 零 xp-boost cue 再叠 · 零抬开孔门槛外第二 soft**

---

## 0. 一句话

日首开孔门槛−1（刃二孔≥4 / 护符≥2）已在镶嵌路径生效，宝石页与拒线应在 **ready 时说清**；打开列表 / 拒线 peek **不得**调用 `unlocked()`（会 consume）。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberGemHoleUnlockSoft`：ready→刃 hole1 `enhance≥(5−DELTA)`、护符 hole0 `≥(3−DELTA)`；`DELTA=1`；首过 soft 解锁 `addPeriodCount` | `EmberGemHoleUnlockSoft.java` D10785 | **soft 真存在 · 仅镶嵌路径 consume** |
| E2 | `EmberItemData.sockUnlocked` 硬表：刃 hole1 ≥5、护符 ≥3；`socketLoreLine` / gem ActionBar 只用硬表 | `EmberItemData` ~421–456 · `EmberGemSocket` ~57–60 | **展示永远硬锁 · soft 档件仍标「锁」** |
| E3 | 拒线固定：「刃第二孔需强化≥5 · 护符孔需强化≥3」——**不读 ready** | `EmberGemSocket.insert` ~101–102 | **SoT 说谎 / 多报一档** |
| E4 | insert 门禁：`!sockUnlocked && !UnlockSoft.unlocked(...)` — soft 可过；`unlocked()` 内 ready 时 latch | 同文件 ~101 | **能力活 · 文案死** |
| E5 | gem Open 无 UnlockSoft ready 尾；无 softTail | `EmberGemSocket` Open/list | **变强途径静默** |
| E6 | soft-latch / CostSoft / pity / firstsoft / cook·life-buy soft / xp-boost cue / Anvil / topUpTo | 总控硬禁 / BB 已齐 | **本窗离开这些族 · 只做 GemHole 门槛展示诚实** |
| E7 | tip-J：禁 **新** glance twin；本债 = **改既有 gem Open/拒线串 + ready peek** | power-paths | 正交可感 |
| E8 | 若「再降 DELTA / 扩 soft 到 hole0」= densify → **否**；本窗零改 DELTA | RESEARCH 裁决 | **诚实 ≠ densify** |

---

## 2. 错位场景（玩家面）

| 场景 | 真能力 | 现行展示 | 体感 |
|------|--------|----------|------|
| UnlockSoft.ready · 刃 +4 · 开 gem | 可 insert 孔1（soft） | Open 标「锁」· 无预告 | **错过日首开孔减负** |
| UnlockSoft.ready · 刃 +3 · insert 孔1 | 拒（仍不够 soft） | 拒线仍喊 ≥5 | **门槛多报一档** |
| UnlockSoft.ready · 护符 +2 | 可 insert 孔0 | 同左 | **静默** |
| 已用今日 soft | 硬表 ≥5/≥3 | 拒线 ≥5/≥3（对） | **对** |
| Open 多次 | ready 不变 | （现无尾） | **预告不得调用 unlocked()** |

---

## 3. 「对齐」定义（钉死）

1. **复用** 既有 `EmberGemHoleUnlockSoft.ready` / `usedToday`；**禁**改 `DELTA` / `C_USED` / `sockUnlocked` 硬表。  
2. **`EmberGemSocket` Open/list：** ready → 半句「今日开孔门槛−1待用（刃二孔≥4·护符≥2）」；**零** `unlocked()`。  
3. **拒线：** ready 时报 soft 门槛（≥4/≥2）或等价「今日门槛−1」+ 硬表对照压行；已用报硬表 ≥5/≥3。  
4. **可选：** soft-ok 且硬锁且 ready 时 ActionBar「可软开」短标（禁改永久 lore / TrMenu）。  
5. **禁新 PAPI**；**禁** TrMenu 加 lore；**禁** Anvil / topUpTo / CostSoft / pity / soft-latch / xp-boost 再叠。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽  
- 抬 DELTA / 再降开孔门槛 / 发明第二开孔 soft / 改 sockUnlocked 硬语义  
- soft-latch 假耗再叠 · CostSoft glance 再叠 · pity-remain 再叠 · firstsoft 再叠 · cook/life-buy soft 再叠 · xp-boost cue 再叠  
- AnvilRepair soft densify · topUpTo 扩 soft · ConvertCap densify  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **GemHoleUnlockSoft 门槛预告/拒线诚实（可施工薄）** | **采纳** — soft 已 live；展示/拒线撒谎或静默；零改 DELTA；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase · 非 soft-latch/xp-boost 再叠** |
| 再降 DELTA / 扩 soft 孔 | **否** — densify |
| AnvilRepair Soft 假耗 | **否** — 硬禁 densify |
| topUpTo 扩 soft | **否** — densify |
| soft-latch / CostSoft / pity / firstsoft / cook·life-buy / xp-boost cue 再叠 | **否** — 链已齐 / 硬禁 |
| mapid 扩甲 / HopeMeter densify / TrMenu lore / 甲闸再扫 | **否** — 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |

## STATUS: OPEN · TIP-BC · 2026-10-12
