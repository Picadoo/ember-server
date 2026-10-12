# DESIGN · 进本日首减负（体力+层费）门禁/Hope 诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** CLOSED · live `1.65.789-d12887` · feat `95ea4e614` · 2026-10-12 22:13 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-stamina-abyss-firstsoft-hope-gate-align-2026-10-12.md`](RESEARCH-ember-stamina-abyss-firstsoft-hope-gate-align-2026-10-12.md)  
> **对接 live：** TIP-AW CLOSED live `1.65.788-d12871` · feat `07923c868` · StaminaFirstSoft D8481 · AbyssFeeSoft D8529 · EntryService.admit · AbyssService.entryProblems · HopeMeter staminaHope/abyssHope · CorePapiStamina  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 LESS/MULT/C_USED/体力表/层费表 · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance 再叠 · 零 LifeCap / UndoWindow densify**

---

## 0. 一句话

让进本体力门禁、深渊层费门禁与体力/深渊希望口在日首减负待用时按减负后费用说话，预留才消耗 soft。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| StaminaFirstSoft | live · consume 在预留 · **无 peek** | **peekCost；admit/hope/blocked 吃 peek** |
| AbyssFeeSoft | live · consume 在预留 · **无 peek** | **peekFee；entryProblems/abyss_hope 吃 peek** |
| admit staminaProblem | 全额 cost | **peekCost** |
| abyss entryProblems | 全额 fee0 | **peekFee** |
| stamina_hope / abyss_hope | 全额 | **peek + 可选 SOFT_TAIL** |
| stamina_blocked_* | 全额 | **peekCost** |
| LESS / MULT / 表 | 5 / 0.8 / 表价 | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// StaminaFirstSoft — 纯 peek（禁第二日闩）
peekCost(d, cost):
  if cost <= 1 || !ready(d): return cost
  return max(1, cost - LESS)
consumeCost(d, cost):  // 已有 · 仅 SessionService 预留调用

// AbyssFeeSoft — 纯 peek
peekFee(d, fee):
  if fee <= 0 || !ready(d): return fee
  return max(1, floor(fee * MULT))
consumeFee(d, fee):  // 已有 · 仅 SessionService 预留调用

EntryService.admit:
  need = EmberStaminaFirstSoft.peekCost(pd, maps.cost(m))
  staminaProblem(name, have, need)   // 文案「需 need」；ready 时可尾「今日减负待用」

AbyssService.entryProblems:
  feeShow = EmberAbyssFeeSoft.peekFee(d, fee0)
  if coin < feeShow && feeMarks(d, feeShow) == 0:
    feeShortText(..., feeShow, ...)  // 可选 · 今日减负待用

CorePapiStamina.stamina_hope:
  hopeCost = peekCost(d, costOf("daily"))
  EmberHopeMeter.staminaHope(cur, max, hopeCost)  // 可选 SOFT_TAIL when ready

RunPapi.abyss_hope:
  feeShow = peekFee(d, next.fee)   // 经 feeFor 后的表费再 peek
  afford = feeShow<=0 || coin>=feeShow || feeMarks(d, feeShow)>0
  abyssHope(best, max, feeShow, afford)  // 可选 SOFT_TAIL

stamina_blocked_*:
  compare stamina vs peekCost(d, costOf(*))   // 周免费抵扣分支不变
```

- **复用** 既有 soft；**禁止**改 `LESS`/`MULT`/`C_USED`/体力表/层费表。  
- SessionService consume 路径 **本窗不改接线**（已诚实）。  
- **禁止**新 `%corerpg_p1_*` / TrMenu 加 lore / ConvertCap densify / CostSoft densify。  
- `stamina_cost_*` 原表数字键 **不改**（表价可读）。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberStaminaFirstSoft` / `EmberAbyssFeeSoft` | 加 peekCost / peekFee |
| `EmberEntryService.admit` | 体力门禁 peekCost |
| `EmberAbyssService.entryProblems` | 层费短欠 peekFee |
| `CorePapiStamina` | stamina_hope + blocked_* peek |
| `EmberRunPapi` abyss_hope | peekFee + afford |
| `EmberHopeMeter` | 可选接受 softTail 参数或调用方拼接；零新 PAPI |
| SessionService consume | **本窗不改** |
| CostSoft / pity / CapSoft / 甲闸 / 追烙 / TrMenu lore | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| Stamina soft ready · have=cost−1 · have≥peekCost | admit 不因体力拒；peek 不 consume |
| Stamina soft ready · have<peekCost | 仍拒；文案需=peek |
| Stamina soft 已用 | 全额门禁 |
| Abyss soft ready · coin∈[peekFee, fee) · marks=0 | entryProblems 不因层费拒；abyss_hope 费用够 |
| Abyss soft 已用 / fee=0 | 旧口径 |
| stamina_hope ready | 场次按 peek；ready 仍 true |
| stamina_blocked_* ready | 与 peek 一致 |
| `git show` | 无 LESS/MULT/体力表/层费表改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase/ConvertCap/CostSoft/pity densify |

### 2.4 玩家口径（固定）

- 今日首发体力减负还没用：进本门禁 / 体力希望按 **减负后体力** 判够不够；可标 **今日减负待用**；点进本才消耗减负。  
- 今日深渊层费减负还没用：层费门禁 / 深渊希望按 **×0.8 后费用** 判够不够；可标减负待用；进层预留才消耗。  
- 减负已用：跟以前全额一样。  
- 减负仍是 −5 体力 / ×0.8 层费、一日一次；体力表与层费表本窗不动。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：§2.1–2.3；NON 核 `git show` 干净  
- 菜单默认不动；lore≤3  
- L13 施工仍 HOLD；ConvertCap densify / CostSoft·pity·soft-glance 再叠 / 甲闸 / SetChase 六槽仍冻  

## STATUS: CLOSED · live 1.65.789-d12887 · 95ea4e614 · 2026-10-12 22:13 CST
