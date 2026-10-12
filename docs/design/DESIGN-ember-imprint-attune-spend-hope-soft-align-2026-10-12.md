# DESIGN · 烙印/调律花费 Hope 诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AU 批 A·M · 已派插件 · 2026-10-12 21:41 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-imprint-attune-spend-hope-soft-align-2026-10-12.md`](RESEARCH-ember-imprint-attune-spend-hope-soft-align-2026-10-12.md)  
> **对接 live：** TIP-AT CLOSED live `1.65.785-d12823` · ImprintCoinSoft D9793 · ImprintMarkSoft D9985 · AttuneUnlockSoft D9825 · HopeMeter imprintSpend/attuneUnlock D615 · TIP-AO readyTail · GearNextHint SOFT_TAIL（TIP-AS）  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 MULT/经济表/日闩键 · 零抬 IMPRINT_MARKS/ALT_MARKS/币表 · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零再叠 CostSoft glance · 零 pity-remain 再叠 · 零 LOCK LockShard soft 本窗**

---

## 0. 一句话

让签名页花费希望、烙印页花费行、追烙就绪阈值在日首烙印/调律减负待用时写半门槛并明示减负，且「够烙/够解锁」跟真付费一致。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| GrowthService imprint/attune 付费 + imprintCheck/attuneCheck | 已吃 soft peek | 保持 |
| `imprint_hope` / `attune_hope` | 全额门槛 | **soft peek；ready 可标减负尾** |
| `impPapi cost` | 全额（check 已 soft） | **与 needM/needC 同一步** |
| 签名页按钮门槛文案 | 全额 | **soft peek** |
| SigChase remain / readyCue 阈值 | 全额 imprintMarks | **ImprintMarkSoft.need peek** |
| `autoAttuneUnlock` 扣费 | 全额 altMarks | **AttuneUnlockSoft.need consume** |
| MULT / C_USED / 表价 / STAMP | 0.5 / 日闩 | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// peek only — never consume from glance/hope/cost
needM = ImprintMarkSoft.need(pd, false)
needC = ImprintCoinSoft.apply(pd, imprintCoin(tier), false)
needA = AttuneUnlockSoft.need(pd, false)

imprint_hope:
  HopeMeter.imprintSpend(marks, needM, coinHave, needC)
    + softTail(ImprintMarkSoft.ready(pd) || ImprintCoinSoft.ready(pd))

attune_hope:
  HopeMeter.attuneUnlock(marks, needA)
    + softTail(AttuneUnlockSoft.ready(pd))

impPapi cost:
  展示 needM / needC（已算）+ softTail；禁再读 imprintMarks()/imprintCoin 全额拼串

SigChase.remainLine / HopeMeter.sigChase / maybeReadyCue:
  need = ImprintMarkSoft.need(pd, false)   // 有 pd
  // pickUnderMarkForMap 可仍用全额（保守 · 本窗不改选目标）
  // ConfirmTokens tip 写 soft need

autoAttuneUnlock:
  needPay = AttuneUnlockSoft.need(d, true)
  spendInsignia(..., needPay)
```

- **复用** 既有 soft `ready` / peek·apply(false)；**禁止**改 MULT / C_USED / 付费 `consume` 语义（除 autoAttuneUnlock 补齐 soft consume）。  
- GrowthService imprint/attune **手点付费序本窗不改接线**（已诚实）。  
- **禁止**新 `%corerpg_p1_*` / 新 remain 族 / TrMenu 加 lore / 抬表 / LOCK LockShard soft 本窗 / SetChase 六槽 densify。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `GrowthService.sigPapi` imprint_hope / attune_hope | soft peek + softTail |
| `GrowthService.impPapi` cost | 用 needM/needC + softTail |
| 签名页烙印/调律按钮门槛文案 | soft peek 同口径 |
| `EmberSigChase` remain/progress/readyCue/tip 展示 | soft need（有 pd） |
| `autoAttuneUnlock` | soft need consume 扣费 |
| HopeMeter.imprintSpend / attuneUnlock 签名 | **可不动**（调用方喂 soft 数） |
| CostSoft glance / pity remain / 甲闸 / SetChase 六槽 / TrMenu lore / LOCK soft | **本窗不改** |

### 2.3 单测（最小包）

| 测 | 期望 |
|----|------|
| soft ready · imprint_hope | 半徽记/半币 + 含「今日减负待用」；够烙不写币差假负 |
| soft 已用 / 无 pd | 全额回归 |
| impPapi cost | 与 imprintCheck soft 同数 |
| attune_hope soft ready | 半徽记门槛 + softTail |
| SigChase remain soft ready · marks=softNeed | 「徽记够」/ ready 态 |
| autoAttuneUnlock soft ready | 扣半徽记（或 soft need） |
| AS/AT/AO/AK 回归 | 不回归破 |

### 2.4 玩家口径（固定）

- 今日烙印减负还没用：希望/花费行按 **半徽记+半币**（各 soft 独立 ready）并标 **今日减负待用**；够烙按半门槛。  
- 今日调律解锁减负还没用：希望/按钮按 **半徽记** 并标减负。  
- 减负已用：跟以前全额一样。  
- 减负仍是 ×0.5、一日一次；`IMPRINT_MARKS` / `ALT_MARKS` / 币表 / STAMP **本窗不动**。  
- 追烙就绪 cue：徽记达今日可烙门槛即响（与真付费同）。

---

## 3. 验收清单

- [ ] soft ready 时 hope/cost/remain 半门槛 + 减负尾；料够不假负  
- [ ] soft 已用 / 无 pd 全额回归  
- [ ] autoAttuneUnlock 与手点同 soft 扣费  
- [ ] `git show` 无 MULT/表价/STAMP/C12/loot/L11/L13/Affix 改；无新 PAPI；无 CostSoft/pity/甲闸/SetChase 六槽 densify；无 LOCK soft  
- [ ] L13 施工仍 HOLD  

---

## 4. 批后派单

**余烬-插件**（CoreRpg · `GrowthService` sig/imp PAPI + 签名页门槛文案 · `EmberSigChase` remain/readyCue soft need · `autoAttuneUnlock` soft 扣费 · 复用 `Imprint*Soft`/`AttuneUnlockSoft`/`GearNextHint.softTail` + 单测）

## 5. HOLD

- L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；CostSoft glance 再叠；pity-remain 再叠；LOCK LockShard soft 本窗  

## STATUS: OPEN · TIP-AU · 2026-10-12
