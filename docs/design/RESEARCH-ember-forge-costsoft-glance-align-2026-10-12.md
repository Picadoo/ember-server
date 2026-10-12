# RESEARCH · 工坊日首 CostSoft 预览/glance 诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AS · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AR CLOSED live 后 AP/AQ/AR **pity nudge remain 链已齐**；总控下一窗继续 **可感 UX / 变强途径**，**优先离开 pity-remain 族**。扫 HopeMeter 其它行 / mapid / GearNextHint / TrMenu / 主线下一环 / **锻造非甲诚实 leftover** / SigFeel 非追烙：发现 **Enhance/Quality/Refine/Upgrade CostSoft 日首 ×0.5 已在 ForgeService 付费+预览真生效**，但 `GearNextHint` / `ForgeHeldNext` / `SinkGlance` / Hope 成色·精工费用半行 / RunPapi `*_next_*` / `held_*_cost` **仍报全额表价** → 「还差料」可假负、「碎片N 币N」可多报一倍（SoT 说谎）。  
> **上游：** TIP-AR CLOSED live `1.65.783-d12791` · feat `489271b08` · TIP-AK/AL/AM 甲诚实链齐 · TIP forgecost-softs D9569–D9632 / QualityCostSoft D9921 · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/经济表 · 零改 CostSoft 日闩键 · 零 Brand/Gem/Convert/Roll soft densify（旁债）· 零 pity-remain 再叠**

---

## 0. 一句话

日首工坊减负已生效时，手持下一确定 / 枢纽费用行 / Sink 消耗预览必须跟真付费一致，并明示今日减负可用。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberEnhanceCostSoft` / `UpgradeCostSoft` / `RefineCostSoft` / `QualityCostSoft`：`ready`→`apply(pd,c,consume)` ×`MULT=0.5`；日首一次 | soft 四类 + ForgeService enhance ~392 / simple ~479–489 | **减负真生效（付费+`!go` 预览）** |
| E2 | `GearNextHint.enhanceLine/qualityLine/craftLine/upgradeLine` **无 pd**；只用 `UpgradeRules.*Cost` 全额 | `EmberGearNextHint` ~37–108 | **摘要不知减负** |
| E3 | `ForgeHeldNext.line`：`canPay`/`shortfall` 吃全额 Cost；展示串吃 `GearNextHint.*Line` 全额；**无 soft** | `EmberForgeHeldNext` ~66–190 · 调用方 ForgeService/Command 有 pd 但未下传 | **「还差料」可假负；费用可多报** |
| E4 | `SinkGlance.tellHeld` 非甲路径：`enhanceLine/qualityLine/craftLine/upgradeLine` 全额（甲早退 TIP-AM 已齐） | `EmberSinkGlance` ~135–142 | **Sink 消耗口同谎** |
| E5 | `HopeMeter.quality/refine` 嵌 `costShort(全额)`；enhance 轨为保底 glance（无费用） | HopeMeter ~74–97 | **成色/精工希望串费用可多报** |
| E6 | RunPapi：`blade_next_q/c` · `charm_next_q/c` · `held_next_*` · `held_enhance_cost` / `held_*_cost` 吃 GearNextHint 全额 | `EmberRunPapi` ~763–821 · TrMenu `ember_p1_gear` 等 | **枢纽一眼同谎** |
| E7 | ForgeService 点强化/成色/精工/升阶 **预览已吃 soft**（`pay.label()`） | enhance ~404–424 · simple ~505–508 | **命令预览诚实 vs glance 口说谎 = 错位** |
| E8 | AP/AQ/AR pity remain 链 **已齐**；甲闸 AK→AL→AM **已齐**；AN→AO 追烙 **已齐**；AH attune / AJ sig page **已齐** | TIP-AR…AH | **本债=锻造非甲 CostSoft glance 诚实；离开 pity-remain** |
| E9 | BrandCraftSoft / GemCraftCostSoft / ConvertCostSoft / ForgeRollCostSoft **另族**；本窗不扩 | GrowthService / RunService | **旁债另 tip** |
| E10 | TIP-J/T：禁 **新** glance twin；本债 = **改既有费用串** 跟 CostSoft SoT，不新开 PAPI | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真付费（今日减负未用） | HeldNext / Sink / hub 费用行 | 体感 |
|------|----------------------|------------------------------|------|
| 强化表价碎12 币120 · 袋内碎7 币70 · soft ready | 付碎6 币60 · **料够** | 「碎片12 币120 · **还差料**」 | **假负 · 拦人** |
| 同上 · 袋内碎12+ | 付半价 | 行仍写全额 · **无减负提示** | **多报一倍 · 假** |
| 成色/精工/升阶 soft ready | 付 ×0.5 | qualityLine/craftLine/upgradeLine 全额 | **同谎** |
| soft 已用 / pd=null | 全额 | 全额 | **对** |
| 手持甲 | ARMOR_REFUSE（AK/AM） | 早退 · 不投影费用 | **不变（禁再扫甲闸）** |

---

## 3. 「对齐」定义（钉死）

1. **复用** 四类 `*CostSoft.ready(pd)` / `apply(pd,c,false)` peek；**禁**第二套日闩或抬 `MULT` / 经济表。  
2. `GearNextHint`：增 `enhanceLine(d,pd)` / `qualityLine(d,pd)` / `craftLine(d,pd)` / `upgradeLine(d,gateOk,pd)`；`ready` 时费用用 soft Cost + 尾缀 **`今日减负待用`**；旧一元无 pd = 全额（单测回归）。  
3. `ForgeHeldNext.line(..., PlayerData pd)`（或等价重载）：`canPay`/`shortfall` 与展示串对 **同一步** 用 soft peek Cost；无 pd 保持旧全额。  
4. `SinkGlance` 非甲路径：有 pd 则喂 soft-aware GearNextHint；**甲早退不动**。  
5. `HopeMeter.quality/refine`：有 pd 时 `costShort(soft)` + 可选短尾；enhance 保底 glance **本窗可不改**（无费用）。  
6. RunPapi `*_next_*` / `held_*_cost`：喂 `get(pd)` → soft-aware 重载；**禁新 `%corerpg_p1_*`**；TrMenu **不加 lore**（吃 SoT）。  
7. **零**改 `MULT` / `C_USED` 键 / ForgeService 付费序；**零** Brand/Gem/Convert/Roll soft 本窗；**零**甲闸再扫；**零** pity-remain 再叠。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / GearNextHint 甲闸  
- SetChase 六槽 densify / mapid haveNeed 扩甲  
- pity nudge remain 再叠（AP/AQ/AR 已齐）  
- Brand/Gem/Convert/ForgeRoll CostSoft glance（旁债）  
- Hope/SetChase **新** glance twin；ForgeChoice densify  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify  
- 改强化率 / 保底 / 成色·精工表价本体  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **工坊日首 CostSoft 预览/glance 诚实对齐（可施工薄）** | **采纳** — 离开 pity-remain；锻造非甲诚实 leftover；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| SigStamp pity+nudge remain | **否** — 仍 pity 族；STAMP 邻域；总控荐离开 |
| SigFeel 非追烙 leftover | **否** — AO 后无新说谎证据 |
| mapid / 图鉴扩六槽 | **否** — SetChase densify 硬禁 |
| GearNextHint 甲闸再扫 | **否** — 总控禁再扫 |
| TrMenu hub lore | **否** — 禁加 lore |
| 主线下一环 / unlock densify | **否** — TIP-Z/AA 观察·研究后须独立 DESIGN |
| Brand/Gem soft glance | **否** — 旁债；本窗钉锻四软 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感诚实债 |

## STATUS: CLOSED · live 1.65.784-d12807 · 468a08943 · TIP-AS · 2026-10-12
