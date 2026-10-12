# RESEARCH · Brand/Gem/Convert/Roll 日首 CostSoft 预览/glance 诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AT · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AS CLOSED live 后 **锻四软**（Enhance/Quality/Refine/Upgrade）glance 已齐；总控显式下一窗 = **Brand/Gem/Convert/Roll soft glance 旁债**（同构 CostSoft peek 诚实）。扫 HeldNext / Sink / forge_gap_* / recipe_gap_gem：付费+命令预览已吃 soft，glance 口仍全额 → 「还差料」假负、仓差多报。  
> **上游：** TIP-AS CLOSED live `1.65.784-d12807` · feat `468a08943` · TIP forgecost-softs BrandCraftSoft D9697 / BrandPinCostSoft D9729 / ConvertCostSoft D9761 / ForgeRollCostSoft D9889 / GemCraftCostSoft D10657 · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/经济表/日闩 · 零再叠锻四软 densify · 零 pity-remain 再叠 · 零 lock-reroll 发明 soft**

---

## 0. 一句话

烙纹合成/宝石合成/跨族转化/随机锻（及烙纹定向）日首减负已生效时，手持下一确定 / Sink 缺料 / 枢纽仓差必须跟真付费一致，并明示今日减负可用。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberBrandCraftSoft` / `BrandPinCostSoft` / `ConvertCostSoft` / `GemCraftCostSoft` / `ForgeRollCostSoft`：`ready`→×`MULT=0.5`；日首一次 | soft 五类 + GrowthService brand craft ~2785 / pin ~2863 / convert ~3038 · GemAcquire craft ~153 · RunService forgeroll ~3626–3746 | **减负真生效（付费+命令预览）** |
| E2 | `ForgeHeldNext.line`：brand/gem/convert/lock/roll **仍全额** `canPay`/`shortfall`；TIP-AS 显式旁债 | `EmberForgeHeldNext` ~71–95 · ~98–183 | **目标锁这些槽时「料够/还差料」可假** |
| E3 | `SinkGlance.tellHeld`：goal ROLL/CONVERT/BRAND 用 `matCost` / `ConvertRules.cost` / `craftShards` **全额**（锻四软已 soft） | `EmberSinkGlance` ~55–66 | **Sink 缺料口同谎** |
| E4 | RunPapi：`forge_gap_brand` / `forge_gap_roll` / `forge_gap_roll_floor` / `forge_gap_convert` / `forge_gap_pin` / `recipe_gap_gem` **全额表价** | `EmberRunPapi` ~224–273 · ~428–445 | **枢纽仓差可多报一倍** |
| E5 | GrowthService brand/pin/convert **!confirm 预览已 soft**；GemAcquire craft **peek soft**；forgeroll **!confirm 已 soft+「· 减负」** | GrowthService / GemAcquire / RunService | **命令预览诚实 vs glance 口说谎 = 错位（同 AS）** |
| E6 | lock-reroll（`lockRerollCost`）**无** CostSoft 类；本窗 **不发明** soft | HeldNext LOCK 分支 | **LOCK 保持全额；非本债** |
| E7 | TIP-AS 锻四软 glance **已齐**；甲闸 AK→AM / 追烙 AN→AO / pity remain AP→AR **已齐** | TIP-AS…AR | **本债=Brand/Gem/Convert/Roll(+Pin) soft glance；离开锻四软再叠** |
| E8 | TIP-J/T：禁 **新** glance twin；本债 = **改既有 canPay/缺料/仓差串** 跟 soft SoT，不新开 PAPI | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真付费（今日减负未用） | HeldNext / Sink / hub 仓差 | 体感 |
|------|----------------------|---------------------------|------|
| 烙纹合成表价碎40 · 袋内碎25 · BrandCraft soft ready | 付碎20 · **料够** | 「合成烙纹 · **还差碎**」/ forge_gap 多报 | **假负 · 拦人** |
| 宝石合成 soft ready · 袋料仅够半价 | 付 ×0.5 · 料够 | recipe_gap_gem / HeldNext GEM 全额还差 | **同谎** |
| 转化 / 随机锻 mat soft ready | 付 ×0.5 | Sink lack / forge_gap_* 全额 | **同谎** |
| 烙纹定向 pin soft ready | 付 brands/coins ×0.5 | forge_gap_pin 全额 | **同谎（Brand 旁支）** |
| soft 已用 / pd=null | 全额 | 全额 | **对** |
| LOCK 洗练 | 全额（无 soft） | 全额 | **本窗不动** |
| 手持甲 | ARMOR_REFUSE | 早退 | **不变（禁再扫甲闸）** |

---

## 3. 「对齐」定义（钉死）

1. **复用** `BrandCraftSoft.apply(pd,need,false)` / `BrandPinCostSoft.apply(...,false)` / `ConvertCostSoft.apply(...,false)` / `GemCraftCostSoft.peek` / `ForgeRollCostSoft.softCost` + `softMarks(...,false)`；**禁**第二套日闩或抬 `MULT` / 经济表。  
2. 扩展 `GearNextHint.softCost` kind（或等价 helper）：`brand` / `gem` / `convert` / `roll`（+ pin 用 int[] soft helper）；`ready` 时费用用 soft；展示尾缀复用既有 **`今日减负待用`**（`SOFT_TAIL`）。  
3. `ForgeHeldNext.line(..., pd)`：brand/gem/convert/roll 的 Cost **先 soft peek** 再 `canPay`/`shortfall`；ready 时返回串可加 ` · 今日减负待用`；**LOCK 仍全额**；无 pd 保持旧全额。  
4. `SinkGlance` goal ROLL/CONVERT/BRAND（及若接线 GEM）：缺料 Cost **soft peek**；**甲早退不动**。  
5. RunPapi `forge_gap_brand|roll|roll_floor|convert|pin` / `recipe_gap_gem`：有 pd 时按 soft 算差；**禁新 `%corerpg_p1_*`**；TrMenu **不加 lore**。  
6. GrowthService / GemAcquire / RunService forgeroll **付费+命令预览本窗不改接线**（已诚实）。  
7. **零**改 `MULT` / `C_USED` / 表价本体；**零**再叠锻四软 densify；**零**甲闸再扫；**零** pity-remain 再叠；**零**为 LOCK 发明 soft。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫  
- SetChase 六槽 densify / mapid haveNeed 扩甲  
- 锻四软（Enhance/Quality/Refine/Upgrade）再 densify（AS 已齐）  
- pity nudge remain 再叠（AP/AQ/AR 已齐）  
- LOCK 洗练发明 CostSoft  
- Hope/SetChase **新** glance twin；ForgeChoice densify  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify  
- 改 craftShards / gem craftCost / convert 表 / forgeroll mat·marks 本体

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **Brand/Gem/Convert/Roll(+Pin) CostSoft glance 诚实对齐（可施工薄）** | **采纳** — 总控显式旁债；同构 AS；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| 锻四软再扫 | **否** — AS 已齐 |
| LOCK 发明 soft | **否** — 无 live soft；禁抬表式 densify |
| SigStamp pity remain | **否** — pity 族 + STAMP 邻域 |
| 甲闸再扫 / SetChase 六槽 | **否** — 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 总控点名旁债 |

## STATUS: OPEN · TIP-AT · 2026-10-12
