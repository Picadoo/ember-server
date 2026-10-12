# RESEARCH · 补给店 Soft 假耗（扣空）诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-BA · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AZ CLOSED live 后总控显式旁债 **SupplyShop Soft 假耗**（同构 AZ：soft latch 在币/空格门禁前 → 仍买不起时白吞日首减负）。硬禁 densify / 甲闸 / 扩 stampable / SetChase 六槽 / ConvertCap densify / CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 / Anvil densify。本债 = **补给店购药 Soft 假耗**，**非** 生活八族再叠 / cook 批次 / CostSoft glance / Anvil。  
> **上游：** TIP-AZ CLOSED live `1.65.791-d12919` · feat `1984f551b` · LifeService.buy peekCoin 范本 · CookCoinSoft.peekFee（TIP-AY）· power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/C_USED/C14 表价 · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 LifeCap / UndoWindow densify · 零 FestivalEntry densify · 零 AnvilRepair densify · 零 topUpTo 扩 soft densify**

---

## 0. 一句话

日首补给店减负待用时，只有真正买成扣币才消耗 soft；预览与拒买（币不够 / 空格不够）不得闩日闩。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberSupplyShopSoft.apply(d,price,consume)`：ready→×MULT；`consume=true` 闩 `C_USED`；**无独立 peek 名**（`apply(...,false)` ≡ peek） | `EmberSupplyShopSoft.java` | **减负真存在 · peek 能力已有 · 接线漏** |
| E2 | `EmberSupplyService.cmd`：`buy=true` 时 **先** `apply(pd, price, buy)`（即 consume），**再** `affordable`；`n<=0` 拒「币不足/空格」 | EmberSupplyService ~293–321 | **拒买可白吞日闩** |
| E3 | C14 `coin=10` · soft→5 · 袋币 3 · soft ready · buy 1 | economy.yml + cmd | **consume 闩 → 拒「需要 5」→ soft 已耗** |
| E4 | soft ready · 币够软价 · `freeSlots=0` · buy | 同上 | **空格拒买亦假耗** |
| E5 | `!buy` 预览：`apply(...,false)` 不 latch；按钮标 soft 价 | cmd ~304–309 | **预览路径已诚实** |
| E6 | TIP-AZ：八族 `peekCoin` + buy 币门禁 peek / spend 前 consume | Life Soft / LifeService.buy | **同构假耗范本已立** |
| E7 | TIP-AY：`CookCoinSoft.peekFee` 裁批/拒不 latch | Cook | **生活币 soft peek 范本** |
| E8 | `topUpTo` 备药自动补货 **不走** SupplyShopSoft（全额 C14） | EmberSupplyService ~123–153 | **本窗不扩 soft 到 prep（禁 densify）** |
| E9 | AnvilRepair Soft 假耗 · Festival/LifeCap · 甲闸 / SetChase / TrMenu lore · CostSoft·pity·firstsoft·cook·life-buy 再叠 | 链 | **硬禁 / 已齐** |
| E10 | tip-J：禁 **新** glance twin；本债 = **改既有 shop soft 接线 + 显式 peek** | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真能力（soft ready） | 现行 cmd buy | 体感 |
|------|---------------------|--------------|------|
| C14 10→5 · 袋币 3 · soft ready | 不够买 · **soft 应保留** | consume 闩 → 拒 | **假耗日首减负** |
| soft ready · 币够 · 空格 0 | 应保留 soft | consume 闩 → 拒空格 | **假耗** |
| soft ready · 币够 · 有空格成交 | 应付半价 · 闩一次 | 半价成交 · 闩一次 | **对** |
| soft 已用 | 全额 | 全额 | **对** |
| 预览 !buy | 标半价 · 不 latch | apply false | **对** |

---

## 3. 「对齐」定义（钉死）

1. **复用** 既有 `EmberSupplyShopSoft`：`ready` / `apply(...,consume)`；**加**纯 `peekPrice(d,price)`（≡ ready ? max(1,floor(price×MULT)) : price；price≤0 不动；**禁第二日闩**；禁改 `MULT`/`C_USED`/C14 `coin`）。  
2. **cmd 接线：** `priceShow = peekPrice(...)`；预览与 `affordable` 用 `priceShow`；**仅**在 `n>0` 且即将 `spendCoin` 前 `apply(...,true)`（返回值作实付单价）；拒买（币/空格/数量非法/副本内）**永不** latch。  
3. **可选：** 预览 ready 时尾「今日减负待用」；减负喊话仅成交 consume 后；**禁新 PAPI**；**禁** TrMenu 加 lore。  
4. **零**改 Life 八族（AZ 已齐）· Cook（AY）· Anvil / Festival / LifeCap / CostSoft / pity。  
5. **零**给 `topUpTo` 接 soft（扩覆盖 = densify）。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽  
- ConvertCap densify · CostSoft glance 再叠 · pity-remain 再叠 · soft-glance 工坊费用族再叠 · firstsoft 再叠 · cook soft 再叠 · **life-buy soft 再叠**  
- LifeDaily/Weekly/LevelGate Cap soft densify · UndoWindow densify · FestivalEntry densify · **AnvilRepair soft densify**  
- topUpTo / prep 接 soft densify · mapid 扩甲 / TrMenu hub lore / 主线下一环大 DESIGN  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **补给店 Soft 假耗（扣空）诚实对齐（可施工薄）** | **采纳** — 总控显式旁债；同构 AZ；假耗可感；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽 · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠** |
| AnvilRepair Soft 假耗 | **否** — 硬禁 densify；旁债 |
| topUpTo 接 soft | **否** — 扩 soft 覆盖 densify |
| FestivalEntry / LifeCap show | **否** — Cap/Festival densify |
| CostSoft / pity / ConvertCap / firstsoft / cook / life-buy / 甲闸再扫 | **否** — 链已齐 / 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感假耗债 |

## STATUS: OPEN · TIP-BA · 2026-10-12
