# tip · TIP-AT Brand/Gem/Convert/Roll 日首 CostSoft 预览/glance 诚实对齐（可施工薄 · 可感 UX）· D12808–D12823

> **状态：** **CLOSED** · live `1.65.785-d12823` · feat `5d29316a6` · 2026-10-12 21:37 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-brand-gem-convert-roll-costsoft-glance-align-2026-10-12.md`](../design/RESEARCH-ember-brand-gem-convert-roll-costsoft-glance-align-2026-10-12.md) · DESIGN [`DESIGN-ember-brand-gem-convert-roll-costsoft-glance-align-2026-10-12.md`](../design/DESIGN-ember-brand-gem-convert-roll-costsoft-glance-align-2026-10-12.md) · TIP-AS CLOSED live `1.65.784-d12807` · feat `468a08943` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **显式旁债** — Brand/Gem/Convert/Roll soft glance（同构 AS CostSoft peek 诚实）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberGearNextHint` softCost 扩 brand/gem/convert/roll(+pin) · `EmberForgeHeldNext` 四轨 soft canPay · `EmberSinkGlance` BRAND/CONVERT/ROLL soft lack · `EmberRunPapi` forge_gap_*/recipe_gap_gem soft + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/经济表/日闩 · 零再叠锻四软 densify · 零 pity-remain 再叠 · 零为 LOCK 发明 soft**

---

## 0. 一句话

烙纹/宝石/转化/随机锻（及烙纹定向）日首减负待用时，手持下一确定 / Sink 缺料 / 枢纽仓差按半价并明示减负，「料够」跟真付费一致；不改减负表、不叠 twin。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **Brand/Gem/Convert/Roll(+Pin) CostSoft glance 诚实对齐** | **采纳** | 总控显式 AS 旁债；同构 soft peek；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| 锻四软再扫 | **否** | AS 已齐 |
| LOCK 发明 soft | **否** | 无 live soft；禁 densify |
| SigStamp pity remain | **否** | pity 族 + STAMP 邻域 |
| 甲闸再扫 / SetChase 六槽 | **否** | 硬禁 |
| TrMenu hub lore | **否** | 禁加 lore |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 总控点名旁债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12808 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12809 | DESIGN / tip power-paths 进度戳：**TIP-AS CLOSED live**；**TIP-AT OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12810 | 错位钉死：E1–E8 付费+命令预览已 soft vs HeldNext/Sink/Papi 全额（RESEARCH §1–2） | 可核 |
| D12811 | 「对齐」钉死：brand/gem/convert/roll(+pin) soft peek；canPay/仓差同 peek；`SOFT_TAIL`；零改 MULT；零新 PAPI；LOCK 全额（DESIGN §2） | 可核 |
| D12812 | **插件：** `GearNextHint.softCost` 扩 brand/gem/convert/roll；可选 softPin | 代码+单测 |
| D12813 | **插件：** `ForgeHeldNext` brand/gem/convert/roll soft canPay/shortfall；ready 可加减负尾；LOCK/甲不动 | 可核 |
| D12814 | **插件：** `SinkGlance` BRAND/CONVERT/ROLL soft lack；RunPapi forge_gap_*/recipe_gap_gem soft | 可核 |
| D12815 | **插件：** 单测最小包（DESIGN §2.3）+ AS/AK 回归 | PASS |
| D12816 | GrowthService/GemAcquire/forgeroll 付费预览 / 锻四软 / TrMenu lore / 甲闸 / SetChase / LOCK soft：**本窗不改**；禁加 lore | lore≤3 |
| D12817 | NON 核：`git show` 无 MULT/经济表/日闩改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无锻四软再 densify；无 pity-remain 再叠 | PASS |
| D12818 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；LOCK 发明 soft；锻四软再 densify；pity-remain 再叠 | 文内硬句 |
| D12819 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12820 | 玩家口径：减负待用 → 半价 +「今日减负待用」+ 料够按半价；已用/LOCK/甲早退/表价/MULT 不变 | 可核 |
| D12821–D12823 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或扩 stampable/SetChase 六槽/甲闸再扫/LOCK soft | 超额新 tip |

### 玩家口径（固定）

- 今日烙纹合成 / 烙纹定向 / 宝石合成 / 跨族转化 / 随机锻减负还没用：手持下一确定、Sink 缺料、枢纽仓差按 **半价** 并标 **今日减负待用**；料够按半价算。  
- 今日减负已用：跟以前全额一样。  
- 减负仍是 ×0.5、一日一次；各轨表价本体本窗不动。  
- 锁定洗练：仍全额（本窗不发明 soft）。  
- 护甲：仍早退「护甲随护符」。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-brand-gem-convert-roll-costsoft-glance-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-brand-gem-convert-roll-costsoft-glance-align-2026-10-12.md` |
| SoT 扩 | `CoreRpg/.../EmberGearNextHint.java` softCost brand/gem/convert/roll(+pin) |
| SoT 手持 | `CoreRpg/.../EmberForgeHeldNext.java` 四轨 soft canPay |
| 复用 | `EmberBrandCraftSoft` · `EmberBrandPinCostSoft` · `EmberGemCraftCostSoft` · `EmberConvertCostSoft` · `EmberForgeRollCostSoft`（ready/peek/apply false） |
| 吃 SoT | `EmberSinkGlance` BRAND/CONVERT/ROLL · `EmberRunPapi` forge_gap_*/recipe_gap_gem |
| 已齐对照 | GrowthService/GemAcquire/forgeroll 付费+预览已 soft · TIP-AS 锻四软 glance · TIP-AK/AL/AM 甲诚实 |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · LOCK 无 soft · 锻四软勿再 densify · pity-remain 再叠禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `MULT` / `C_USED` / 经济表价 / GrowthService·GemAcquire·forgeroll 付费序  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- 为 LOCK 发明 CostSoft；锻四软再 densify；pity nudge remain 再叠  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 批后插件：D12812–D12815；D12817 `git show` 干净  
- [x] 菜单默认不动；lore≤3  
- [x] 甲早退回归；LOCK 全额；live `1.65.785-d12823` · feat `5d29316a6`；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AS 后 · **总控显式旁债** — Brand/Gem/Convert/Roll(+Pin) CostSoft glance 诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify**）  
- **批注：** AS 锻四软 glance 已齐；BrandCraft/Pin/Gem/Convert/ForgeRoll 付费+命令预览已 live，但 HeldNext/Sink/Papi 仓差仍全额 → 「还差料」假负、仓差多报；soft peek +「今日减负待用」+ canPay 同 peek；零改 MULT/经济表；零新 PAPI；LOCK 不发明 soft；甲早退不动；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / 锻四软再 densify / pity-remain 再叠 / LOCK soft 仍冻  

## STATUS: CLOSED · live 1.65.785-d12823 · 5d29316a6 · 2026-10-12 21:37 CST
