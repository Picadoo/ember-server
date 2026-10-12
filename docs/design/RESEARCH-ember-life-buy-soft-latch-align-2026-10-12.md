# RESEARCH · 生活购币 Soft 假耗（扣空）诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AZ · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AY CLOSED live 后总控继续 **可感 UX / 变强途径**，硬禁 densify / 甲闸 / 扩 stampable / SetChase 六槽 / ConvertCap densify / CostSoft·pity·soft-glance·firstsoft·cook soft 再叠。扫 mapid / HopeMeter other / GearNextHint / TrMenu / 主线下一环 / wear·boss / brew·fish·life 同族：发现 **烹饪批次门禁已 peek+扣币才 consume**（TIP-AY），但 **`LifeService.buy` 对 Bread/Brew/Rod/SoulDust/Reforge/Offhand/Stable/Pet 仍先 `consumeCoin` 闩日闩，再比袋币 / 再扣材料** → 软价仍不够或材料扣失败时 **日首减负被白吞（假耗）**（SoT 说谎）。本债 = **生活购币 Soft 假耗**，**非** 工坊 CostSoft glance / cook 批次再叠 / firstsoft / ConvertCap / pity。  
> **上游：** TIP-AY CLOSED live `1.65.790-d12903` · feat `5fdccde66` · CookCoinSoft peekFee 对照 · SupplyShopSoft.apply(consume) 对照 · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/C_USED/表价 coin · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft 再叠 · 零 LifeCap / UndoWindow densify · 零 FestivalEntry densify · 零 AnvilRepair densify（旁债）**

---

## 0. 一句话

日首生活购币减负待用时，只有真正扣成币/成交才消耗 soft；预览与拒买不得闩日闩。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberRodBuySoft` / Bread / Brew / SoulDust / Reforge / Offhand / Stable / Pet：仅 `consumeCoin`（ready→闩 `C_USED`→×MULT）；**无 peek** | `Ember*Soft.java` | **减负真存在 · 无安全 peek** |
| E2 | `LifeService.buy`：材料够后 **先** `consumeCoin` 改 `payCoin`，**再** `d.getCoin()<payCoin` 拒；再 `spendCoin`；材料 `consumeExact` 失败才退币 | LifeService ~493–575 | **拒买/扣材失败可白吞日闩** |
| E3 | 例：`rod.coin=60` · soft→30 · 袋币 25 · soft ready | 同上 | **consume 闩 → 拒「还差 5」→ soft 已耗** |
| E4 | 例：`bread.coin=40` soft→20 · 袋币 15；`heal_potion.coin=10` soft→5 · 袋币 4 | 同上 | **同构假耗** |
| E5 | `needsBread`/`needsBrew` 已 ×MULT 比袋币（path 安全）；`needsRod` **只查背包无竿**，不 peek 币；`/life buy` 直购仍走 E2 | LifeService ~633–722 | **path 半齐 · buy 漏 peek/延后 consume** |
| E6 | TIP-AY `CookCoinSoft.peekFee`：裁批/拒不 latch；扣币前才 consume | CookCoinSoft / cook | **同族生活币 soft 已立 peek 范本** |
| E7 | `EmberSupplyShopSoft.apply(d,price,consume)` 已支持 peek；但 `SupplyService.cmd` buy=true 时 **先 apply(consume=true) 再 affordable** → 币/空格不够亦可假耗 | EmberSupplyService ~293–321 | **旁债同构 · 本窗可不并入** |
| E8 | `AnvilRepairSoft.consumeFee` 在比袋币前闩（同假耗）；AY/本窗硬禁 **AnvilRepair densify** | LifeService ~316–322 | **旁债 · 本窗不改** |
| E9 | FestivalEntrySoft / LifeCap 满次预览 densify · 甲闸 / SetChase / mapid 扩甲 / TrMenu lore / CostSoft·pity·firstsoft·cook 再叠 | 链 | **硬禁 / 已齐** |
| E10 | tip-J：禁 **新** glance twin；本债 = **改既有 buy soft 接线 + peek** | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真能力（soft ready） | 现行 buy | 体感 |
|------|---------------------|----------|------|
| rod 60→30 · 袋币 25 · soft ready | 不够买 · **soft 应保留** | consume 闩 → 拒 | **假耗日首减负** |
| bread 40→20 · 袋币 15 | 同上 | 同上 | **假耗** |
| soft ready · 币够 · 材料扣失败退币 | soft 应保留或随成交 | 已 latch · 币退 soft 不退 | **假耗** |
| soft ready · 币够成交 | 应付半价 · 闩一次 | 半价成交 · 闩一次 | **对** |
| soft 已用 | 全额 | 全额 | **对** |

---

## 3. 「对齐」定义（钉死）

1. **复用** 既有八族 Soft：`ready` / `consumeCoin`；**加**纯 `peekCoin(d,coin)`（≡ ready ? max(1,floor(coin×MULT)) : coin；fee≤0 不动；**禁第二日闩**；禁改 `MULT`/`C_USED`/表价 `*.coin`）。  
2. **buy 接线：** `payShow = peekCoin(...)`（按 offer 族选 Soft）；用 `payShow` 做袋币门禁；**仅**在 `spendCoin` 即将成功前调用 `consumeCoin`（返回值作实付）；拒买 / 材料失败路径 **永不** latch。  
3. **可选：** `show()` 对应 offer 行 ready 时尾「今日减负待用」或标 peek 价（复用 cook `SOFT_TAIL` 口径）；**禁新 PAPI**；**禁** TrMenu 加 lore。  
4. **零**改 Cook 批次（AY 已齐）；**零**改 Anvil / Festival / LifeCap / CostSoft / pity / firstsoft / SupplyShop（Supply 假耗 = 旁债下一 tip）。  
5. **零**改 needsBread/needsBrew 已 peek 逻辑（可保留）；可选 needsRod 加币 peek（薄 · 非必须，因 buy 修好后 pathBuy 亦安全）。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽  
- ConvertCap densify · CostSoft glance 再叠 · pity-remain 再叠 · soft-glance 工坊费用族再叠 · firstsoft 再叠 · **cook soft 再叠**  
- LifeDaily/Weekly/LevelGate Cap soft densify · UndoWindow densify · FestivalEntry densify · **AnvilRepair soft densify**  
- SupplyShop 假耗 densify（旁债）· mapid 扩甲 / TrMenu hub lore / 主线下一环大 DESIGN / boss telegraph  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **生活购币 Soft 假耗（扣空）诚实对齐（可施工薄）** | **采纳** — 离开 CostSoft/pity/Cap/firstsoft/cook 批次/甲闸/追烙；假耗可感；零功率表；Cook peek 范本可复用；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽 · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft·cook soft 再叠** |
| SupplyShop Soft 假耗 | **旁债** — 同构；本窗不并入以保薄 |
| AnvilRepair Soft 假耗 | **否** — 硬禁 densify；旁债 |
| FestivalEntry 满次预览 | **否** — firstsoft / Festival densify |
| LifeDailyCapSoft show「满」 | **否** — Cap densify |
| mapid 扩甲 haveNeed | **否** — 扩 stampable |
| TrMenu hub lore | **否** — 禁加 lore |
| CostSoft / pity / ConvertCap / firstsoft / cook soft / 甲闸再扫 | **否** — 链已齐 / 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感假耗债 |

## STATUS: OPEN · TIP-AZ · 2026-10-12
