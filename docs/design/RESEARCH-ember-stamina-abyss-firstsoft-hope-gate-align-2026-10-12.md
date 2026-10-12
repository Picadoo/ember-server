# RESEARCH · 进本日首减负（体力+层费）门禁/Hope 诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AX · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AW CLOSED live 后 AS→AV soft-glance / AP–AR pity / AW ConvertCapSoft / AU Hope 花费 **已齐**；总控下一窗继续 **可感 UX / 变强途径**，**硬禁 CostSoft·pity·soft-glance 再叠 · ConvertCap densify · 甲闸再扫 · 扩 stampable · SetChase 六槽**。扫 mapid / HopeMeter other / GearNextHint 非 soft / TrMenu / 主线下一环 / wear·boss cue：发现 **`EmberStaminaFirstSoft`（日首体力 −5）与 `EmberAbyssFeeSoft`（日首层费 ×0.8）付费已在 SessionService 真生效**，但 **admit 体力门禁 / abyss entryProblems 层费门禁 / stamina_hope / abyss_hope / stamina_blocked_*** 仍按**全额表价**判定 → 「体力不足 / 币不够 / 约还能打 N 场 / 费用不足」可 **假负拦人**（SoT 说谎）。  
> **上游：** TIP-AW CLOSED live `1.65.788-d12871` · feat `07923c868` · StaminaFirstSoft D8481 · AbyssFeeSoft D8529 · HopeMeter staminaHope D629 / abyssHope D624 · EntryService.admit · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 LESS/MULT/C_USED/体力表/层费表 · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance 费用族再叠 · 零 LifeCap soft densify · 零 UndoWindow densify**

---

## 0. 一句话

日首进本体力减负 / 深渊层费减负待用时，进本门禁与希望口必须按减负后费用判定「够不够」，且只在真正预留时消耗 soft。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberStaminaFirstSoft`：`ready`→`consumeCost` 日首 `cost−LESS(5)`（min 1）；**无 peek API** | `EmberStaminaFirstSoft.java` | **减负真存在 · 无安全 peek** |
| E2 | `EmberAbyssFeeSoft`：`ready`→`consumeFee` 日首 `fee×MULT(0.8)`（min 1）；**无 peek API** | `EmberAbyssFeeSoft.java` | **层费减负真存在 · 无安全 peek** |
| E3 | SessionService 预留：先 `StaminaFirstSoft.consumeCost` / `AbyssFeeSoft.consumeFee` 再 reserve/spend | SessionService ~302–326 | **真进本已吃 soft** |
| E4 | `EntryService.admit`：`staminaProblem(name, have, maps.cost(m))` **全额**；soft ready 且 have∈[peek,cost) → **假拒** | EntryService ~188–221 · ~109–111 | **门禁假负** |
| E5 | `AbyssService.entryProblems`：`fee0=feeFor` 全额；`coin<fee0 && feeMarks==0` → feeShortText **全额** | AbyssService ~223–229 | **层费门禁假负** |
| E6 | `CorePapiStamina.stamina_hope` → `staminaHope(cur,max,costOf("daily"))` **不认 soft** → 「约还能打 N 场」可欠报 | CorePapiStamina ~13–14 · HopeMeter ~300–304 | **希望口欠报** |
| E7 | `RunPapi.abyss_hope`：`fee=next.fee` 全额；`afford=coin>=fee\|\|feeMarks` **不认 soft** → 「费用不足」可假 | RunPapi ~704–710 · HopeMeter ~268–274 | **深渊希望假负** |
| E8 | `stamina_blocked_{weekly,raid,elite}`：`stamina < costOf(*)` **全额**（无周免费抵扣时） | CorePapiStamina ~27–40 | **枢纽灰显可假挡** |
| E9 | AS→AV CostSoft / AP–AR pity / AW CapSoft / AU Hope 花费 / AK–AM 甲闸 / AN–AO 追烙 **已齐** | TIP 链 | **本债=进本日首减负门禁/Hope；离开 soft-glance/pity/cap/甲闸/追烙** |
| E10 | tip-J：禁 **新** glance twin；本债 = **改既有门禁串 + hope 串 + blocked 判定** + 纯 peek 助手 | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真能力（soft ready） | 门禁 / hope 口 | 体感 |
|------|---------------------|----------------|------|
| 日常 cost=30 · 体力 28 · StaminaFirst ready | 预留应付 25 · **可进** | admit「需 30 · 当前 28」**拒** | **假负拦人** |
| 深渊下层 fee=100 · 币 85 · marks 不够 · AbyssFee ready | 应付 80 · **可开** | entryProblems / abyss_hope「币不足 / 费用不足」 | **假负拦人** |
| soft ready · stamina_hope | 首场 −5 后场次可能 +1 | 「约还能打 N」按全额 | **欠报场次** |
| soft 已用 / fee=0 / 首层礼免费 | 跟表一致 | 跟表一致 | **对** |

---

## 3. 「对齐」定义（钉死）

1. **复用** `StaminaFirstSoft.ready` / `consumeCost` 与 `AbyssFeeSoft.ready` / `consumeFee`；**加**纯 peek（禁第二日闩；禁改 `LESS`/`MULT`/`C_USED`/体力表/层费表）。  
2. **peek 建议：** `peekCost(d,cost)` ≡ ready ? max(1,cost−LESS) : cost；`peekFee(d,fee)` ≡ ready ? max(1,floor(fee×MULT)) : fee（fee≤0 不动）。  
3. **admit 体力门禁：** `staminaProblem` 用 peekCost（有 pd）；**consume 仍只在 SessionService 预留**。  
4. **abyss entryProblems：** 短欠判定用 peekFee；文案报 peek 后费用（可 +「今日减负待用」短尾）；**consume 仍只在 SessionService**。  
5. **stamina_hope / abyss_hope：** 展示/afford/场次估算吃 peek；ready 时可加 `SOFT_TAIL`（复用 `GearNextHint.SOFT_TAIL` 或同文案）；**禁新 PAPI**。  
6. **stamina_blocked_***：与 admit 同 peekCost（无周免费抵扣分支不变）。  
7. **零**改 `stamina_cost_*` 原表数字键（表价仍可读）；**零** TrMenu 加 lore；**零** CostSoft/pity/ConvertCap/甲闸/SetChase densify。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽  
- ConvertCap densify · CostSoft glance 再叠 · pity-remain 再叠 · soft-glance 工坊费用族再叠  
- LifeDaily/Weekly/LevelGate Cap soft densify · UndoWindow densify · gearlibHope +5 展示（欠报旁债 · 另 tip）  
- mapid 扩甲 / TrMenu hub lore / 主线下一环大 DESIGN / boss telegraph  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **进本日首减负（体力+层费）门禁/Hope 诚实对齐（可施工薄）** | **采纳** — 离开 soft-glance/pity/cap/甲闸/追烙；HopeMeter other + 进本门禁；假负可感；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽 · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance 再叠** |
| gearlibHope 撤销窗 +5 | **否** — 欠报非假负；UndoWindow densify STOP；优先级低于本债 |
| LifeDailyCapSoft show「满」 | **否** — Cap soft 同构刚做完 AW；易叠 densify 感 |
| mapid 扩甲 haveNeed | **否** — 扩 stampable / SetChase 邻域硬禁 |
| TrMenu hub lore | **否** — 禁加 lore |
| CostSoft / pity / ConvertCap / 甲闸再扫 | **否** — 链已齐 / 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感假负门禁债 |

## STATUS: OPEN · TIP-AX · 2026-10-12
