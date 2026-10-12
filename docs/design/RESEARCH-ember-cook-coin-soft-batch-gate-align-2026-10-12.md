# RESEARCH · 生活烹饪日首 CookCoinSoft 批次门禁/费用诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AY · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AX CLOSED live 后总控继续 **可感 UX / 变强途径**，硬禁 densify / 甲闸 / 扩 stampable / SetChase 六槽 / ConvertCap densify / CostSoft·pity·soft-glance 再叠 / prefer 非 firstsoft 再叠。扫 mapid / HopeMeter other / GearNextHint / TrMenu / 主线下一环 / wear·boss：发现 **面包/熬药日首币减负门禁已 peek**（`needsBread`/`needsBrew`），**烹饪 `LifeService.cook` 批次仍按全额 `cookCoinEach` 裁条数**，而 `EmberCookCoinSoft.consumeFee` 仅在裁完后生效 → 日首减负待用时可 **假拒整批 / 欠烤条数 / 成功文案多报扣币**（SoT 说谎）。本债 = **生活烹饪币 soft**，**非** 工坊 CostSoft / 进本 firstsoft / ConvertCap / pity。  
> **上游：** TIP-AX CLOSED live `1.65.789-d12887` · feat `95ea4e614` · CookCoinSoft D8849 · BreadBuySoft / BrewCoinSoft 门禁 peek 对照 · cook.coin_each C17 · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/C_USED/cook.coin_each · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance 再叠 · 零 firstsoft 再叠 · 零 LifeCap / UndoWindow densify · 零 FestivalEntry densify**

---

## 0. 一句话

日首烹饪币减负待用时，裁批与扣币/成功文案必须按减负后费用说话；peek 不吞 soft，真正扣币才 consume。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberCookCoinSoft`：`ready`→`consumeFee` 日首 `fee×MULT(0.5)`（min 1）；**无 peek API** | `EmberCookCoinSoft.java` | **减负真存在 · 无安全 peek** |
| E2 | `LifeService.cook`：先 `fee=total*cookCoinEach` 全额；`coin<fee` → `total=coin/cookCoinEach`；**之后**才 `consumeFee` | LifeService ~799–817 | **裁批不认 soft → 假拒/欠烤** |
| E3 | 成功文案 / 部分失败退款用 `cooked*cookCoinEach` 全额，可与实付 soft 费错位 | LifeService ~828–835 | **多报扣币 / 退款可偏** |
| E4 | `needsBread` / `needsBrew`：ready 时 `needC/needCoin` 已 ×MULT 再比袋币 | LifeService ~637–672 | **同族生活币 soft 门禁已诚实 · 烹饪漏对齐** |
| E5 | AnvilRepairSoft：先 consume 再比袋币（付费路径诚实）；本窗不扫铁砧 densify | LifeService ~314–322 | **对照 · 非本债** |
| E6 | AS→AV CostSoft / AP–AR pity / AW CapSoft / AX firstsoft / AK–AM 甲闸 / AN–AO 追烙 **已齐** | TIP 链 | **离开上述族；本债=烹饪 CookCoin 批次** |
| E7 | tip-J：禁 **新** glance twin；本债 = **改既有 cook 裁批 + peek + 文案** | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真能力（CookCoin soft ready） | 现行 cook | 体感 |
|------|------------------------------|-----------|------|
| `coin_each=10` · 袋币 5 · 鱼≥1 · soft ready | 应付 5 · **可烤 1** | `5/10=0` →「余烬币不足」 | **假拒** |
| 袋币 15 · 鱼 2 · soft ready | 批费 20→10 · **可烤 2** | `15/10=1` → 只烤 1 | **欠烤** |
| soft ready · 烤成 | 实付半价 | 文案「-满额币」 | **多报** |
| soft 已用 / fee=0 | 跟表一致 | 跟表一致 | **对** |

---

## 3. 「对齐」定义（钉死）

1. **复用** `CookCoinSoft.ready` / `consumeFee`；**加**纯 `peekFee(d,fee)`（禁第二日闩；禁改 `MULT`/`C_USED`/`cook.coin_each`）。  
2. **peek：** `peekFee(d,fee)` ≡ ready ? max(1,floor(fee×MULT)) : fee（fee≤0 不动）。  
3. **裁批：** 用 peek 后的有效单价（或等价：对 `feeFull` peek 后再反推可烤条数）；**禁止**用全额单价裁完再 soft。  
4. **consume** 仍只在真正 `spendCoin` 前调用 `consumeFee`（与现序同；peek 永不 latch）。  
5. **成功文案 / 部分退款** 对齐 **实付 cookFee**（soft 后）；禁再写 `cooked*cookCoinEach` 当实付。  
6. **可选：** `show()` cook 行 ready 时尾「今日减负待用」（复用 `SOFT_TAIL` 口径）；**禁新 PAPI**；**禁** TrMenu 加 lore。  
7. **零**改 Bread/Brew/Anvil/Festival/LifeCap/firstsoft/CostSoft/pity 本窗。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽  
- ConvertCap densify · CostSoft glance 再叠 · pity-remain 再叠 · soft-glance 工坊费用族再叠 · firstsoft 再叠  
- LifeDaily/Weekly/LevelGate Cap soft densify · UndoWindow densify · FestivalEntry densify · AnvilRepair soft densify  
- mapid 扩甲 / TrMenu hub lore / 主线下一环大 DESIGN / boss telegraph  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **生活烹饪 CookCoinSoft 批次门禁/费用诚实对齐（可施工薄）** | **采纳** — 离开 CostSoft/pity/Cap/firstsoft/甲闸/追烙；面包/熬药同族已 peek、烹饪漏；假拒/欠烤可感；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽 · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft 再叠** |
| FestivalEntrySoft 满次预览 | **否** — firstsoft 同构；prefer 非再叠 |
| LifeDailyCapSoft show「满」 | **否** — Cap densify 感；AX 已否 |
| gearlibHope 撤销窗 +5 | **否** — UndoWindow densify STOP |
| mapid 扩甲 haveNeed | **否** — 扩 stampable |
| TrMenu hub lore | **否** — 禁加 lore |
| CostSoft / pity / ConvertCap / firstsoft / 甲闸再扫 | **否** — 链已齐 / 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感假拒/欠烤债 |

## STATUS: OPEN · TIP-AY · 2026-10-12
