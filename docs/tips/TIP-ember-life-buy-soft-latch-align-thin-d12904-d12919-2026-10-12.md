# tip · TIP-AZ 生活购币 Soft 假耗（扣空）诚实对齐（可施工薄 · 可感 UX）· D12904–D12919

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-插件** · 2026-10-12 22:27 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-life-buy-soft-latch-align-2026-10-12.md`](../design/RESEARCH-ember-life-buy-soft-latch-align-2026-10-12.md) · DESIGN [`DESIGN-ember-life-buy-soft-latch-align-2026-10-12.md`](../design/DESIGN-ember-life-buy-soft-latch-align-2026-10-12.md) · TIP-AY CLOSED live `1.65.790-d12903` · feat `5fdccde66` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径** — **离开 CostSoft·pity·soft-glance·firstsoft·cook soft / ConvertCap / 甲闸 / 追烙**（AS→AV / AP–AR / AW / AX / AY / AK–AM / AN–AO 已齐）；生活购币 Soft 假耗 leftover（Cook peek 范本已立、buy 八族仍先 latch）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · 八族 Soft.`peekCoin` · `LifeService.buy` 币门禁 peek / spend 前 consume · 可选 show SOFT_TAIL + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/C_USED/表价 coin · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft 再叠 · 零 LifeCap/UndoWindow/FestivalEntry/AnvilRepair densify**

---

## 0. 一句话

日首生活购币减负待用时，拒买不吞 soft；买成扣币才消耗。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **生活购币 Soft 假耗（扣空）诚实对齐** | **采纳** | 离开 CostSoft/pity/Cap/firstsoft/cook 批次/甲闸/追烙；假耗可感；Cook peek 范本可复用；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽 · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft·cook soft 再叠** |
| SupplyShop Soft 假耗 | **旁债** | 同构；本窗不并入 |
| AnvilRepair Soft 假耗 | **否** | 硬禁 densify |
| FestivalEntrySoft 满次预览 | **否** | firstsoft / Festival densify |
| LifeDailyCapSoft show「满」 | **否** | Cap densify |
| mapid 扩甲 haveNeed | **否** | 扩 stampable / SetChase 邻域 |
| CostSoft / pity / ConvertCap / firstsoft / cook soft / 甲闸再扫 | **否** | 链已齐 / 硬禁 |
| TrMenu hub lore | **否** | 禁加 lore |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感假耗债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12904 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12905 | DESIGN / tip power-paths 进度戳：**TIP-AY CLOSED live**；**TIP-AZ OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12906 | 错位钉死：E1–E10 先 latch 再拒买（RESEARCH §1–2） | 可核 |
| D12907 | 「对齐」钉死：peekCoin；币门禁吃 peek；consume 仅 spend 前；零改 MULT/表价；零新 PAPI（DESIGN §2） | 可核 |
| D12908 | **插件：** 八族 Soft 加 `peekCoin` | 代码+单测 |
| D12909 | **插件：** `LifeService.buy` 币门禁 peek；spend 前 consume | 可核 |
| D12910 | **插件：** 可选 `show()` offer 行 ready 尾「今日减负待用」 | 可核 |
| D12911 | **插件：** 单测最小包（DESIGN §2.3）+ 拒买不 latch | PASS |
| D12912 | CostSoft glance / pity remain / ConvertCap / firstsoft / cook / TrMenu lore / 甲闸 / SetChase / Anvil / Supply：**本窗不改**；禁加 lore | lore≤3 |
| D12913 | NON 核：`git show` 无 MULT/C_USED/`*.coin` 改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 CostSoft/pity/Cap/firstsoft/cook densify；无 LifeCap/UndoWindow/FestivalEntry/Anvil densify | PASS |
| D12914 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft·pity·soft-glance·firstsoft·cook soft 再叠；LifeCap/UndoWindow/FestivalEntry/Anvil densify | 文内硬句 |
| D12915 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12916 | 玩家口径：减负待用 → 按减负后价判够不够；买成才消耗；拒买保留减负；MULT/表价不变 | 可核 |
| D12917–D12919 | 缓冲：错链/错戳/文案过长压行；禁借号抬 MULT/表价或开 L13 施工或扩 stampable/SetChase 六槽/甲闸再扫/ConvertCap densify/CostSoft·pity·soft-glance·firstsoft·cook soft 再叠；SupplyShop 假耗另 tip | 超额新 tip |

### 玩家口径（固定）

- 今日该生活项减负还没用：按 **减负后价格** 判断买不买得起；可标 **今日减负待用**；**买成才消耗减负**。  
- 币不够减负后价：提示还差；**减负仍留着**。  
- 减负已用：跟表价全额一样。  
- 减负仍是 ×0.5、一日一次；表价本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-life-buy-soft-latch-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-life-buy-soft-latch-align-2026-10-12.md` |
| SoT Soft | `CoreRpg/.../EmberBreadBuySoft.java` · Brew · Rod · SoulDust · Reforge · Offhand · Stable · Pet |
| SoT buy | `CoreRpg/.../LifeService.java` buy / show |
| 对照已齐 | CookCoinSoft.peekFee · TIP-AY · needsBread/needsBrew peek · SupplyShopSoft.apply(consume) 旁债 |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · ConvertCap densify 禁 · CostSoft/pity/soft-glance/firstsoft/cook soft 再叠禁 · Anvil/Festival/LifeCap densify 禁 · SupplyShop 假耗旁债 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `MULT` / `C_USED` / 表价 `*.coin`  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- ConvertCap densify；CostSoft glance 再叠；pity nudge remain 再叠；soft-glance 工坊费用族再叠；firstsoft 再叠；**cook soft 再叠**；LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify；SupplyShop 假耗本窗并入  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12908–D12911；D12913 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] peek 不吞 soft；成交才 consume；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 已批 · 已派插件  
- 选题：TIP-AY 后 · **离开 CostSoft/pity/ConvertCap/firstsoft/cook 批次/甲闸/追烙** — 生活购币 Soft 假耗诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance·firstsoft·cook soft 再叠**）  
- **批注：** Cook 已 peek+扣币才 consume；`LifeService.buy` 八族仍先 latch 再比币 → 软价仍不够时白吞日首减负；peekCoin + 币门禁/成交 consume；零改 MULT/表价；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；SupplyShop/Anvil 假耗旁债；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / ConvertCap densify / CostSoft·pity·soft-glance·firstsoft·cook soft 再叠 仍冻  

## STATUS: APPROVED · tip D12904–D12919 · 2026-10-12 22:27 Asia/Shanghai · 已派余烬-插件
