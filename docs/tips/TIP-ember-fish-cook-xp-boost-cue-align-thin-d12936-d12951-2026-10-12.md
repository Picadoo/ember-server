# tip · TIP-BB 生活首钓/烹饪日首加验预告诚实对齐（可施工薄 · 可感 UX）· D12936–D12951

> **状态：** **OPEN** · 待批 A·M · 2026-10-12 22:41 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-fish-cook-xp-boost-cue-align-2026-10-12.md`](../design/RESEARCH-ember-fish-cook-xp-boost-cue-align-2026-10-12.md) · DESIGN [`DESIGN-ember-fish-cook-xp-boost-cue-align-2026-10-12.md`](../design/DESIGN-ember-fish-cook-xp-boost-cue-align-2026-10-12.md) · TIP-BA CLOSED live `1.65.792-d12935` · feat `366b0a645` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **离开 soft-latch/假耗链** — **可感 UX / 变强途径**；brew/fish **non-soft** 加验预告（FishXpBoost+CookXpBoost 已 live、show 静默）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `LifeService.show` Fish/Cook XP boost ready 尾 · 可选 Boost 旁 CUE 常量 · 单测；零改 MULT/fish_xp/xp_each）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/C_USED/fish_xp/cook.xp_each · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 LifeCap/UndoWindow/FestivalEntry/AnvilRepair densify · 零 topUpTo 扩 soft densify**

---

## 0. 一句话

日首钓鱼/烹饪加验待用时，生活页先说清楚；打开列表不消耗。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **生活首钓/烹饪日首加验预告诚实对齐** | **采纳** | 离开 soft-latch；brew/fish non-soft；变强途径可见；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase · 非 soft 假耗再叠** |
| AnvilRepair Soft 假耗 | **否** | 硬禁 densify |
| topUpTo 接 soft | **否** | 扩 soft densify |
| LifeCap / LevelGate 容错预告 | **否** | Cap/firstsoft densify |
| GemHole soft 门槛展示 | **旁债** | soft 展示邻域；本窗优先 fish/cook |
| CostSoft / pity / soft-glance / firstsoft / cook soft / life-buy soft / 甲闸再扫 | **否** | 链已齐 / 硬禁 |
| mapid 扩甲 / HopeMeter densify / TrMenu lore | **否** | 硬禁 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only（主线解锁 / stampable 空 / L13） | **否本窗主轴** | 有可感预告债；列旁债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12936 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12937 | DESIGN / tip power-paths 进度戳：**TIP-BA CLOSED live**；**TIP-BB OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12938 | 错位钉死：E1–E8 show 无加验预告（RESEARCH §1–2） | 可核 |
| D12939 | 「对齐」钉死：ready 尾；show 零 consume；零改 MULT/fish_xp/xp_each；零新 PAPI（DESIGN §2） | 可核 |
| D12940 | **插件：** `LifeService.show` FishXpBoost.ready →「今日首钓加验×2待用」 | 代码+单测 |
| D12941 | **插件：** cook 行 CookXpBoost.ready →「今日烹饪加验×2待用」（可与币减负尾并存） | 可核 |
| D12942 | **插件：** 可选 Boost 旁短 CUE 常量便于断言；成交 consume 路径不动 | 可核 |
| D12943 | **插件：** 单测最小包（DESIGN §2.3）+ show 不 latch | PASS |
| D12944 | soft-latch / CostSoft / pity / Cap / firstsoft / cook soft / life-buy / TrMenu lore / 甲闸 / SetChase / Anvil / topUpTo：**本窗不改**；禁加 lore | lore≤3 |
| D12945 | NON 核：`git show` 无 MULT/C_USED/fish_xp/cook.xp_each 改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 soft-latch/CostSoft/pity/Cap/firstsoft densify；无 Anvil/Festival/LifeCap/topUpTo soft densify | PASS |
| D12946 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠；soft-latch 假耗再叠；LifeCap/UndoWindow/FestivalEntry/Anvil densify；topUpTo 扩 soft | 文内硬句 |
| D12947 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12948 | 玩家口径：ready → 预告 ×2 待用；成钓/成烤才消耗；已用无尾；表额不动 | 可核 |
| D12949–D12951 | 缓冲：错链/错戳/文案过长压行；禁借号抬 MULT/fish_xp/xp_each 或开 L13 施工或扩 stampable/SetChase/甲闸再扫/soft 假耗再叠；GemHole 门槛展示另 tip | 超额新 tip |

### 玩家口径（固定）

- 今日还没钓成：生活页标 **今日首钓加验×2待用**；钓成才消耗。  
- 今日还没烤成：cook 行标 **今日烹饪加验×2待用**；烤成才消耗。  
- 已用：不再显示待用尾。  
- 加验仍是 ×2、一日一次；`fish_xp` / `cook.xp_each` 本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-fish-cook-xp-boost-cue-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-fish-cook-xp-boost-cue-align-2026-10-12.md` |
| SoT Boost | `CoreRpg/.../EmberFishXpBoost.java` · `EmberCookXpBoost.java` |
| SoT show | `CoreRpg/.../LifeService.java` show |
| 对照已齐 | TIP-AY cook 币减负尾 · TIP-AZ/BA soft-latch（本窗离开）· D8865/D8961 live 加验 |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · soft-latch/CostSoft/pity/Cap/firstsoft 再叠禁 · Anvil/topUpTo soft densify 禁 · GemHole 门槛旁债 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `MULT` / `C_USED` / `fish_xp` / `cook.xp_each`  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- soft-latch 假耗再叠；ConvertCap densify；CostSoft glance 再叠；pity nudge remain 再叠；soft-glance 工坊费用族再叠；firstsoft 再叠；cook soft / life-buy soft 再叠；LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify；topUpTo 扩 soft  

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12940–D12943；D12945 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] show 不吞加验；成钓/成烤才 consume；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-BA 后 · **离开 soft-latch/假耗** — 生活首钓/烹饪日首加验预告诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify · 非 soft 假耗再叠**）  
- **批注：** FishXpBoost/CookXpBoost 已 live，show 不预告 → 变强途径静默；ready 尾「今日…加验×2待用」；零改 MULT/fish_xp/xp_each；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；GemHole 门槛旁债；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / soft-latch·CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠 / Anvil densify / topUpTo 扩 soft 仍冻  

## STATUS: OPEN · tip D12936–D12951 · 2026-10-12 22:41 Asia/Shanghai · 待批 A·M
