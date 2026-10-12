# RESEARCH · 烙印/调律花费 Hope 诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AU · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AT CLOSED live 后 AS+AT **工坊 CostSoft glance 链已齐**；总控下一窗继续 **可感 UX / 变强途径**，**优先离开 CostSoft / pity-remain 族**。扫 HopeMeter 其它行 / mapid / GearNextHint 非 soft / TrMenu / 主线下一环 / LOCK cost / SigFeel 非追烙：发现 **ImprintCoinSoft / ImprintMarkSoft / AttuneUnlockSoft 日首 ×0.5 已在 GrowthService 付费+`imprintCheck`/`attuneCheck` 真生效**，但 `imprint_hope` / `attune_hope` / 烙印页 `impPapi cost` / 签名页解锁文案 / SigChase remain·readyCue **仍报全额门槛** → 「币差/徽记不够」可假负、花费多报一倍（SoT 说谎）。  
> **上游：** TIP-AT CLOSED live `1.65.785-d12823` · feat `5d29316a6` · TIP imprint-attune-spend-hope D615 · ImprintCoinSoft D9793 · ImprintMarkSoft D9985 · AttuneUnlockSoft D9825 · TIP-AO chase 目标件 · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/经济表/日闩 · 零再叠 CostSoft glance · 零 pity-remain 再叠 · 零为 LOCK 发明 CostSoft（LockShard soft 旁债另 tip）**

---

## 0. 一句话

日首烙印/调律减负已生效时，签名页花费希望、烙印页花费行、追烙就绪口必须跟真付费门槛一致，并明示今日减负可用。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberImprintCoinSoft` / `ImprintMarkSoft` / `AttuneUnlockSoft`：`ready`→×`MULT=0.5`；日首一次 | soft 三类 · GrowthService imprint ~1438–1543 · attune ~1741–1766 | **减负真生效（付费+check peek）** |
| E2 | `sigPapi imprint_hope`：`coinNeed = imprintCoin(tier)` **全额**；`imprintSpend(..., imprintMarks(), …)` **全额徽记** | GrowthService ~1884–1892 | **希望串不知减负** |
| E3 | `sigPapi attune_hope`：`attuneUnlock(..., altMarks())` **全额** | GrowthService ~1894–1907 | **调律希望同谎** |
| E4 | `impPapi` 已算 `needM`/`needC` soft peek 喂 `imprintCheck`，但 `case "cost"` **仍拼全额** `imprintMarks()`+`imprintCoin` | GrowthService ~2016–2039 | **check 诚实 vs cost 行说谎 = 错位** |
| E5 | 签名页烙印/调律按钮文案仍写全额门槛（~1961 / ~1990） | GrowthService lore | **点前一眼多报** |
| E6 | `SigChase.remainLine` / `HopeMeter.sigChase` / `maybeReadyCue` 阈值 = `imprintMarks()` 全额；按钮 tip 同 | SigChase ~119 / HopeMeter ~129 / SigChase ~379–404 | **半徽记已可烙时仍「还差」/ 就绪 cue 晚一拍** |
| E7 | `autoAttuneUnlock` 扣徽记仍 `altMarks()` 全额；手点路径已 `AttuneUnlockSoft.need` | GrowthService ~1662 vs ~1766 | **自动解锁路径付费不跟 soft** |
| E8 | AS+AT CostSoft glance **已齐**；AP/AQ/AR pity remain **已齐**；AN→AO 追烙目标件 **已齐**；甲闸 AK→AM **已齐** | TIP-AT…AM | **本债=HopeMeter 烙印/调律花费诚实；离开 CostSoft/pity** |
| E9 | `RerollLockShardSoft` live；HeldNext LOCK 仍全额 | ForgeHeldNext ~95 · GrowthService ~2236 | **LOCK soft 旁债 · 本窗不收（仍属费用 soft 族）** |
| E10 | TIP-J/T：禁 **新** glance twin；本债 = **改既有希望/花费串** 跟 soft SoT | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真付费（今日减负未用） | Hope / cost / chase 口 | 体感 |
|------|----------------------|------------------------|------|
| 烙印表价徽记5 币300 · 袋内徽记3 币160 · soft ready | 付徽记3 币150 · **够烙** | imprint_hope「徽记 3/5 · 币差」· cost「5枚+300币」 | **假负 · 拦人** |
| 同上 · 袋内够半价 | 付半价 | 行仍写全额 · **无减负提示** | **多报一倍 · 假** |
| 调律解锁 soft ready · 徽记半够 | 付半徽记解锁 | attune_hope / 按钮仍全额 | **同谎** |
| soft 已用 / 无 pd | 全额 | 全额 | **对** |
| 追烙徽记达 soft need | 可烙 | remain「还差」· readyCue 不响 | **就绪晚 · 假** |

---

## 3. 「对齐」定义（钉死）

1. **复用** `ImprintCoinSoft.apply(pd,c,false)` / `ImprintMarkSoft.need(pd,false)` / `AttuneUnlockSoft.need(pd,false)`；**禁**第二套日闩或抬 `MULT` / `IMPRINT_MARKS` / `ALT_MARKS` / 币表 / STAMP。  
2. `imprint_hope`：`needM`/`needC` soft peek → `HopeMeter.imprintSpend`；ready 时尾缀 **`今日减负待用`**（复用 `GearNextHint.SOFT_TAIL`/`softTail`）。  
3. `attune_hope`：`AttuneUnlockSoft.need` peek → `attuneUnlock`；ready 可加 softTail。  
4. `impPapi cost`：展示用已算 `needM`/`needC`（与 check 同一步）；ready 加 softTail。  
5. 签名页烙印/调律按钮门槛文案：soft peek 同口径。  
6. SigChase **展示+readyCue 阈值**：`ImprintMarkSoft.need(pd,false)`；ConfirmTokens tip 同 soft need；**禁**扩 SetChase 六槽 / 改 auto-pin 选目标算法（`pickUnderMarkForMap` 可仍用全额保守）。  
7. `autoAttuneUnlock`：扣费改 `AttuneUnlockSoft.need(d,true)`（与手点路径同 SoT）。  
8. **零**新 `%corerpg_p1_*`；TrMenu **不加 lore**；**零**再叠 CostSoft glance；**零** pity-remain；**零** LOCK LockShard soft 本窗。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫  
- SetChase 六槽 densify / mapid haveNeed 扩甲  
- CostSoft glance 再叠（AS+AT 已齐）· pity nudge remain 再叠  
- LOCK / RerollLockShardSoft glance（旁债另 tip；禁发明整轨 CostSoft）  
- Hope/SetChase **新** glance twin；ForgeChoice densify  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **烙印/调律花费 Hope + 烙印页 cost 诚实对齐（可施工薄）** | **采纳** — 离开 CostSoft/pity；HopeMeter other + SigFeel 花费 leftover；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| LOCK LockShard soft glance | **否** — 仍费用 soft 族；总控荐离开 CostSoft；旁债 |
| mapid / 图鉴扩甲 | **否** — 扩 stampable / SetChase 邻域硬禁 |
| GearNextHint 非 soft densify | **否** — 无新说谎证据优先于本债 |
| TrMenu hub lore | **否** — 禁加 lore |
| 主线下一环 | **否** — 须独立 DESIGN |
| CostSoft / pity 再扫 | **否** — 链已齐 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感诚实债 |

## STATUS: OPEN · TIP-AU · 2026-10-12
