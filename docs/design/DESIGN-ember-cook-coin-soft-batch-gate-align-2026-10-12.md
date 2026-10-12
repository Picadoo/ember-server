# DESIGN · 生活烹饪日首 CookCoinSoft 批次门禁/费用诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** OPEN · TIP-AY · 2026-10-12  
> **上游 RESEARCH：** [`RESEARCH-ember-cook-coin-soft-batch-gate-align-2026-10-12.md`](RESEARCH-ember-cook-coin-soft-batch-gate-align-2026-10-12.md)  
> **对接 live：** TIP-AX CLOSED live `1.65.789-d12887` · feat `95ea4e614` · CookCoinSoft D8849 · BreadBuySoft/BrewCoinSoft 门禁 peek 对照 · LifeService.cook · cook.coin_each C17  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 MULT/C_USED/cook.coin_each · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft 再叠 · 零 LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify**

---

## 0. 一句话

让代烤鱼在日首币减负待用时按减负后费用裁批、扣币与报账，peek 不吞 soft。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| CookCoinSoft | live · consume 在扣币前 · **无 peek** | **peekFee；裁批吃 peek** |
| cook 裁批 | `coin/cookCoinEach` 全额 | **按 peek 后有效单价（或批费）裁** |
| 成功文案 / 部分退款 | `cooked*cookCoinEach` 全额 | **对齐实付 cookFee** |
| show() cook 行 | 只报表价 | **可选 ready 尾「今日减负待用」** |
| MULT / C_USED / coin_each | 0.5 / 日闩 / 表价 | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// CookCoinSoft — 纯 peek（禁第二日闩）
peekFee(d, fee):
  if fee <= 0 || !ready(d): return fee
  return max(1, floor(fee * MULT))
consumeFee(d, fee):  // 已有 · 仅扣币前调用

LifeService.cook:
  // 1) 点鱼得 total（不变）
  feeFull = total * cookCoinEach
  // 2) 有效批费 / 单价用 peek（不 latch）
  feeShow = CookCoinSoft.peekFee(d, feeFull)
  unitShow = total > 0 ? max(1, feeShow / total) : cookCoinEach
  // 等价：ready 时 unitShow = max(1, floor(cookCoinEach * MULT))
  if coin < feeShow:
    total = unitShow > 0 ? coin / unitShow : total
    if total <= 0: 拒「余烬币不足（每条 unitShow 币）」+ 可选「今日减负待用」；return
    feeFull = total * cookCoinEach
    // 重算 take 配额（同现逻辑）
  cookFee = feeFull
  soft = CookCoinSoft.consumeFee(d, cookFee)   // 真扣前才吞
  if soft < cookFee: 喊减负；cookFee = soft
  spendCoin(cookFee)
  // 部分烤失败退款：按实付比例
  if cooked < total && cookFee > 0 && total > 0:
    back = cookFee - cookFee * cooked / total   // 整除差可归玩家
    if back > 0: addCoin(back); cookFee -= back
  成功文案：「烤好了 …（-cookFee 币）」  // 禁再写 cooked*cookCoinEach

show() cook 行（可选）：
  ready → 「每条 cookCoinEach 币 · 今日减负待用（×0.5）」
```

- **复用** 既有 soft；**禁止**改 `MULT`/`C_USED`/`cook.coin_each`。  
- Bread/Brew/Anvil/Festival/LifeCap/firstsoft/CostSoft **本窗不改接线**。  
- **禁止**新 `%corerpg_p1_*` / TrMenu 加 lore。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberCookCoinSoft` | 加 `peekFee` |
| `LifeService.cook` | 裁批 peek；consume 仍扣币前；文案/退款对齐实付 |
| `LifeService.show` cook 行 | 可选 SOFT_TAIL / 减负待用 |
| CostSoft / pity / Cap / firstsoft / 甲闸 / SetChase / TrMenu lore | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| ready · coin=5 · each=10 · 鱼≥1 | peek 可烤 1；consume 一次；实付 5；文案 -5 |
| ready · coin=15 · each=10 · 鱼 2 | 烤 2；实付 10；非欠烤 1 |
| ready · coin=4 · each=10 | 拒；不 consume |
| soft 已用 · coin=15 · 鱼 2 | 烤 1（全额裁）；不二次 soft |
| 部分烤失败 | 退款 ≤ 实付剩余；不超退 |
| `git show` | 无 MULT/C_USED/coin_each 改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无 CostSoft/pity/Cap/firstsoft densify |

### 2.4 玩家口径（固定）

- 今日烹饪减负还没用：代烤按 **减负后每条币** 判断能烤几条；可标 **今日减负待用**；点烤才消耗减负。  
- 减负已用：跟表价全额一样。  
- 减负仍是 ×0.5、一日一次；`cook.coin_each` 表价本窗不动。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：§2.1–2.3；NON 核 `git show` 干净  
- 菜单默认不动；lore≤3  
- L13 施工仍 HOLD；CostSoft·pity·soft-glance·firstsoft / ConvertCap / 甲闸 / SetChase 六槽仍冻  

## STATUS: OPEN · TIP-AY · 2026-10-12
