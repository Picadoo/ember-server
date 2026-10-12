# DESIGN · 生活购币 Soft 假耗（扣空）诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AZ 批 A·M · 已派插件 · 2026-10-12 22:27 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-life-buy-soft-latch-align-2026-10-12.md`](RESEARCH-ember-life-buy-soft-latch-align-2026-10-12.md)  
> **对接 live：** TIP-AY CLOSED live `1.65.790-d12903` · feat `5fdccde66` · CookCoinSoft.peekFee 范本 · LifeService.buy 八族 Soft · life.yml / C17 `*.coin`  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 MULT/C_USED/表价 coin · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft 再叠 · 零 LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify**

---

## 0. 一句话

让生活购币日首减负只在真正成交扣币时消耗；拒买与扣材失败不得白吞 soft。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 八族 Soft | 仅 `consumeCoin`（即 latch） | **+ peekCoin；consume 仅成交前** |
| `LifeService.buy` | 先 consume 再比币 / 再扣材 | **peek 比币；spend 前 consume；失败不 latch** |
| show() offer 行 | 只报表价 | **可选 ready 尾「今日减负待用」** |
| MULT / C_USED / `*.coin` | 0.5 / 日闩 / 表价 | **零改** |
| Cook / Anvil / Supply / Festival | AY 已齐 / 硬禁 densify / 旁债 | **本窗不改** |

八族：`EmberBrewCoinSoft` · `EmberRodBuySoft` · `EmberBreadBuySoft` · `EmberSoulDustCoinSoft` · `EmberReforgeStoneSoft` · `EmberOffhandBuySoft` · `EmberStableCharmSoft` · `EmberPetHatchSoft`。

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// 各 Soft — 纯 peek（禁第二日闩）
peekCoin(d, coin):
  if coin <= 0 || !ready(d): return coin
  return max(1, floor(coin * MULT))
consumeCoin(d, coin):  // 已有 · 仅 spendCoin 前调用

LifeService.buy (币段，伪码):
  payFull = o.coin
  payShow = peekByOffer(o.id, d, payFull)   // 选族 Soft.peekCoin；非 soft 族 → payFull
  if d.getCoin() < payShow:
    拒「需要 payShow …」+ 可选「今日减负待用」；return false   // 不 latch
  // … 材料已在此前检查 …
  payCoin = payFull
  soft = consumeByOffer(o.id, d, payFull)   // 真扣前才闩
  if soft < payFull: 喊减负；payCoin = soft
  if !spendCoin(d, payCoin): return false   // 极端：已 latch；保持与现 spend 失败口径一致（几乎不可达）
  // 材料 consumeExact 失败 → 退币；**本窗加：若本笔刚 consume 且交易失败，不额外发明 undo API** —
  //   优先序：材料检查已在 soft 前 → 成交路径材料失败极稀；
  //   若仍失败：允许保持 latch（与现同）或薄窗仅保证「币不足拒」不 latch（本 tip 验收主钉）。
```

- **复用** 既有 Soft；**禁止**改 `MULT`/`C_USED`/C17 `*.coin` / life.yml 表价。  
- **禁止**新 `%corerpg_p1_*` / TrMenu 加 lore。  
- Cook / Anvil / Festival / LifeCap / CostSoft / pity / SupplyShop **本窗不改接线**。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| 八族 Soft | 各加 `peekCoin`（同构 Cook.peekFee） |
| `LifeService.buy` | 币门禁用 peek；consume 移到 spend 前 |
| `LifeService.show` offer 行 | 可选 ready 尾「今日减负待用」 |
| 可选 `needsRod` | 币 peek（薄 · 非验收硬钉） |
| CostSoft / pity / Cap / firstsoft / cook / 甲闸 / SetChase / TrMenu / Anvil / Supply | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| Rod soft ready · coin=25 · 表价 60 | peek=30；拒买；**不** consume；`usedToday=false` |
| Bread soft ready · coin=15 · 表价 40 | 同上假耗不发生 |
| Rod soft ready · coin=40 · 表价 60 | peek=30；成交实付 30；consume 一次 |
| soft 已用 · coin=40 · rod | 拒或全额（40<60 拒）；不二次 soft |
| Brew soft ready · 材料不足 | 材料门先拒；不 consume |
| `git show` | 无 MULT/C_USED/`*.coin` 改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无 CostSoft/pity/Cap/firstsoft/cook densify；无 Anvil/Festival/LifeCap densify |

### 2.4 玩家口径（固定）

- 今日该生活项减负还没用：列表/拒买按 **减负后价格** 说话；可标 **今日减负待用**；**买成才消耗减负**。  
- 币不够减负后价：提示还差多少；**减负仍留着**。  
- 减负已用：跟表价全额一样。  
- 减负仍是 ×0.5、一日一次；表价本窗不动。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：peek + buy 接线 + 单测；`git show` 干净  
- 菜单默认不动；lore≤3  
- peek 不吞 soft；成交才 consume；L13 施工仍 HOLD  

## 4. 旁债（下一 prefer · 非本窗）

1. SupplyShop Soft 假耗（buy=true 先 latch 再 affordable）  
2. AnvilRepair Soft 假耗（硬禁 densify 未解冻前不开）  
3. 主线解锁 / stampable 空 / L13 HOLD 观察  

## STATUS: APPROVED · TIP-AZ · 2026-10-12 22:27 Asia/Shanghai · 已派余烬-插件
