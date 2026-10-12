# tip · TIP-BA 补给店 Soft 假耗（扣空）诚实对齐（可施工薄 · 可感 UX）· D12920–D12935

> **状态：** **CLOSED live `1.65.792-d12935`** · feat `366b0a645` · 批 A·M · 2026-10-12 22:39 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-supply-shop-soft-latch-align-2026-10-12.md`](../design/RESEARCH-ember-supply-shop-soft-latch-align-2026-10-12.md) · DESIGN [`DESIGN-ember-supply-shop-soft-latch-align-2026-10-12.md`](../design/DESIGN-ember-supply-shop-soft-latch-align-2026-10-12.md) · TIP-AZ CLOSED live `1.65.791-d12919` · feat `1984f551b` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **显式旁债** SupplyShop Soft 假耗 — **离开 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft / ConvertCap / 甲闸 / 追烙**（AS→AV / AP–AR / AW / AX / AY / AZ / AK–AM / AN–AO 已齐）；同构 AZ latch-before-gate  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberSupplyShopSoft.peekPrice` · `EmberSupplyService.cmd` 门禁 peek / spend 前 consume · 可选预览 SOFT_TAIL + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/C_USED/C14 表价 · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 LifeCap/UndoWindow/FestivalEntry/AnvilRepair densify · 零 topUpTo 扩 soft densify**

---

## 0. 一句话

日首补给店购药减负待用时，拒买不吞 soft；买成扣币才消耗。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **补给店 Soft 假耗（扣空）诚实对齐** | **采纳** | 总控显式旁债；同构 AZ；假耗可感；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽 · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠** |
| AnvilRepair Soft 假耗 | **否** | 硬禁 densify |
| topUpTo 接 soft | **否** | 扩 soft densify |
| FestivalEntrySoft / LifeCap show | **否** | Cap/Festival densify |
| CostSoft / pity / ConvertCap / firstsoft / cook / life-buy / 甲闸再扫 | **否** | 链已齐 / 硬禁 |
| TrMenu hub lore | **否** | 禁加 lore |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感假耗债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12920 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12921 | DESIGN / tip power-paths 进度戳：**TIP-AZ CLOSED live**；**TIP-BA OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12922 | 错位钉死：E1–E10 先 latch 再拒买（RESEARCH §1–2） | 可核 |
| D12923 | 「对齐」钉死：peekPrice；门禁吃 peek；consume 仅 spend 前；零改 MULT/C14；零新 PAPI（DESIGN §2） | 可核 |
| D12924 | **插件：** `EmberSupplyShopSoft` 加 `peekPrice` | 代码+单测 |
| D12925 | **插件：** `cmd` 预览/affordable 用 peek；n>0 且 spend 前 consume | 可核 |
| D12926 | **插件：** 可选预览 ready 尾「今日减负待用」；减负喊话仅成交后 | 可核 |
| D12927 | **插件：** 单测最小包（DESIGN §2.3）+ 拒买不 latch | PASS |
| D12928 | CostSoft glance / pity remain / ConvertCap / firstsoft / cook / life-buy / TrMenu lore / 甲闸 / SetChase / Anvil / topUpTo：**本窗不改**；禁加 lore | lore≤3 |
| D12929 | NON 核：`git show` 无 MULT/C_USED/C14 coin 改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 CostSoft/pity/Cap/firstsoft/cook/life-buy densify；无 LifeCap/UndoWindow/FestivalEntry/Anvil/topUpTo soft densify | PASS |
| D12930 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠；LifeCap/UndoWindow/FestivalEntry/Anvil densify；topUpTo 扩 soft | 文内硬句 |
| D12931 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12932 | 玩家口径：减负待用 → 按减负后单价判够不够；买成才消耗；拒买保留减负；MULT/C14 不变 | 可核 |
| D12933–D12935 | 缓冲：错链/错戳/文案过长压行；禁借号抬 MULT/C14 或开 L13 施工或扩 stampable/SetChase 六槽/甲闸再扫/ConvertCap densify/CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠；Anvil 假耗另 tip | 超额新 tip |

### 玩家口径（固定）

- 今日补给减负还没用：按 **减负后单价** 判断买不买得起；可标 **今日减负待用**；**买成才消耗减负**。  
- 币不够减负后价 / 空格不够：提示原因；**减负仍留着**。  
- 减负已用：跟表价全额一样。  
- 减负仍是 ×0.5、一日一次；C14 表价本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-supply-shop-soft-latch-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-supply-shop-soft-latch-align-2026-10-12.md` |
| SoT Soft | `CoreRpg/.../EmberSupplyShopSoft.java` |
| SoT cmd | `CoreRpg/.../EmberSupplyService.java` cmd |
| 对照已齐 | CookCoinSoft.peekFee · Life peekCoin · TIP-AY/AZ · `apply(...,false)` 预览 |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · ConvertCap densify 禁 · CostSoft/pity/soft-glance/firstsoft/cook/life-buy soft 再叠禁 · Anvil/Festival/LifeCap/topUpTo soft densify 禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `MULT` / `C_USED` / C14 `coin`  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- ConvertCap densify；CostSoft glance 再叠；pity nudge remain 再叠；soft-glance 工坊费用族再叠；firstsoft 再叠；cook soft 再叠；**life-buy soft 再叠**；LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify；topUpTo 扩 soft  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 批后插件：D12924–D12927；D12929 `git show` 干净  
- [x] 菜单默认不动；lore≤3  
- [x] peek 不吞 soft；成交才 consume；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 已批 · 已派插件  
- 选题：TIP-AZ 后 · **总控显式旁债** SupplyShop Soft 假耗诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠**）  
- **批注：** AZ 生活购已 peek+成交 consume；`EmberSupplyService.cmd` buy=true 仍先 `apply(consume)` 再 `affordable` → 软价仍不够或空格不够时白吞日首减负；peekPrice + 门禁/成交 consume；零改 MULT/C14；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；Anvil 假耗旁债；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / ConvertCap densify / CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠 / topUpTo 扩 soft 仍冻  

## STATUS: CLOSED live `1.65.792-d12935` · tip D12920–D12935 · 2026-10-12 22:39 Asia/Shanghai · feat `366b0a645`
