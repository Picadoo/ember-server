# DESIGN · 补给店 Soft 假耗（扣空）诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-BA 批 A·M · 已派插件 · 2026-10-12 22:36 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-supply-shop-soft-latch-align-2026-10-12.md`](RESEARCH-ember-supply-shop-soft-latch-align-2026-10-12.md)  
> **对接 live：** TIP-AZ CLOSED live `1.65.791-d12919` · feat `1984f551b` · Life peekCoin 范本 · TIP-AY Cook peekFee · EmberSupplyShopSoft / EmberSupplyService.cmd · C14 `coin=10`  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 MULT/C_USED/C14 coin · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify · 零 topUpTo 扩 soft**

---

## 0. 一句话

让补给店日首减负只在真正买成扣币时消耗；拒买（币/空格）不得白吞 soft。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| Soft | `apply(d,price,consume)` 已支持 peek/consume；无具名 peek | **+ peekPrice；consume 仅成交前** |
| `cmd` buy | 先 `apply(...,buy=true)` 再 `affordable` | **peek 比币/空格；n>0 且 spend 前 consume** |
| 预览 !buy | apply false · 标 soft 价 | **保持；可选「今日减负待用」** |
| MULT / C_USED / C14 | 0.5 / 日闩 / coin=10 | **零改** |
| Life / Cook / Anvil / topUpTo | AZ/AY 已齐 / 硬禁 densify / 不接 soft | **本窗不改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// EmberSupplyShopSoft — 纯 peek（禁第二日闩）
peekPrice(d, price):
  if price <= 0 || !ready(d): return price
  return max(1, floor(price * MULT))
apply(d, price, consume):  // 已有 · 仅 n>0 且 spendCoin 前 consume=true

EmberSupplyService.cmd (伪码):
  priceFull = price()
  priceShow = peekPrice(pd, priceFull)
  if !buy:
    展示 priceShow + 可选 ready「今日减负待用」；return
  // mode / 副本 / want 校验 — 失败不 latch
  n = affordable(want, coins, priceShow, freeSlots)
  if n <= 0:
    拒「需要 priceShow …」或「空格」；return   // 不 latch
  pricePay = apply(pd, priceFull, true)       // 真扣前才闩
  if pricePay < priceFull: 喊「补给减负」
  spendCoin(C14, n * pricePay)；give …
```

- **复用** 既有 Soft；**禁止**改 `MULT`/`C_USED`/C14 `coin`。  
- **禁止**新 `%corerpg_p1_*` / TrMenu 加 lore。  
- Life 八族 / Cook / Anvil / Festival / LifeCap / CostSoft / pity / **topUpTo** **本窗不改接线**。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberSupplyShopSoft` | 加 `peekPrice`（同构 Cook.peekFee / Life.peekCoin） |
| `EmberSupplyService.cmd` | 预览/门禁用 peek；consume 移到 spend 前；减负喊话仅 consume 后 |
| `topUpTo` / Life / Cook / CostSoft / pity / Cap / 甲闸 / SetChase / TrMenu / Anvil | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| soft ready · coin=3 · C14=10 | peek=5；buy 拒；**不** consume；`usedToday=false` |
| soft ready · coin=20 · freeSlots=0 | peek=5；拒空格；**不** consume |
| soft ready · coin=20 · slots≥1 · buy 1 | peek=5；成交实付 5；consume 一次 |
| soft 已用 · coin=20 | 全额 10；不二次 soft |
| peekPrice 不改 periodCount | usedToday 仍 false |
| `git show` | 无 MULT/C_USED/C14 coin 改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无 CostSoft/pity/Cap/firstsoft/cook/life-buy densify；无 Anvil/Festival/LifeCap/topUpTo soft densify |

### 2.4 玩家口径（固定）

- 今日补给减负还没用：预览/拒买按 **减负后单价** 说话；可标 **今日减负待用**；**买成才消耗减负**。  
- 币不够减负后价 / 空格不够：提示原因；**减负仍留着**。  
- 减负已用：跟表价全额一样。  
- 减负仍是 ×0.5、一日一次；C14 表价本窗不动。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：peek + cmd 接线 + 单测；`git show` 干净  
- 菜单默认不动；lore≤3  
- peek 不吞 soft；成交才 consume；L13 施工仍 HOLD  

## 4. 旁债（下一 prefer · 非本窗）

1. AnvilRepair Soft 假耗（硬禁 densify 未解冻前不开）  
2. 主线解锁 / stampable 空 / L13 HOLD 观察  
3. topUpTo 是否接 soft（须另批 · 现禁 densify）

## STATUS: APPROVED · TIP-BA · 2026-10-12 22:36 Asia/Shanghai · 已派余烬-插件
