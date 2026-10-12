# tip · TIP-AO SigFeel/Hope/remain 追烙就绪目标件对齐（可施工薄 · 可感 UX）· D12728–D12743

> **状态：** OPEN · 荐批 **A · 方案 M** · 2026-10-12  
> **上游：** RESEARCH [`RESEARCH-ember-sigfeel-chase-ready-target-align-2026-10-12.md`](../design/RESEARCH-ember-sigfeel-chase-ready-target-align-2026-10-12.md) · DESIGN [`DESIGN-ember-sigfeel-chase-ready-target-align-2026-10-12.md`](../design/DESIGN-ember-sigfeel-chase-ready-target-align-2026-10-12.md) · TIP-AN CLOSED live `1.65.779-d12727` · TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径（非 densify · 非再扫甲闸 · 非扩 stampable）** — AN 后 SigFeel leftover + Hope/remain 就绪摘要对齐  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `HopeMeter.sigChase` / `SigFeel.menuLine` / `remainLine`+接线 + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore**

---

## 0. 一句话

徽记够时枢纽手感 / 希望追烙 / remain 与 AN 就绪钮写同一目标件；缺件明示，不再摘要糊成「满可烙印」。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **SigFeel/Hope/remain 追烙就绪目标件对齐** | **采纳** | AN leftover；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable** |
| GearNextHint 全局甲闸 | **否** | 甲闸已齐；总控禁再扫 |
| SetChase / mapid / gem pity 再 twin | **否** | TIP-J/T 停窗；本债非新 glance |
| SetChase 图鉴扩六槽 | **否** | 内容 densify |
| 扩 stampable 甲 | **否** | 硬禁 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| TrMenu hub lore | **否** | 禁加 lore |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12728 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12729 | DESIGN / tip power-paths 进度戳：**TIP-AN CLOSED live**；**TIP-AO OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12730 | 错位钉死：E3–E6 摘要无槽 vs E1 AN readyCueSlotWord（RESEARCH §1） | 可核 |
| D12731 | 「对齐」钉死：满档尾=`readyTail` 复用 `readyCueSlotWord`/`imprintWornTarget`；缺件明示；未满零改；零新 PAPI（DESIGN §2） | 可核 |
| D12732 | **插件：** `remainLine(def,marks,lo)` + 旧重载委托 | 代码+单测 |
| D12733 | **插件：** `HopeMeter.sigChase(def,marks,lo)` 满档换 readyTail；`progressLine` 可喂 lo | 可核 |
| D12734 | **插件：** `SigFeel.menuLine(..., lo)` 满档「追烙就绪 · readyTail」 | 可核 |
| D12735 | **插件：** `GrowthService.sigFeelLine` + `maybeReadyCue` 喂 lo | 可核 |
| D12736 | **插件可选：** Hope resolve / MapFarmCue tooltip 有 lo 则喂 | 可核 |
| D12737 | **插件：** 单测最小包（DESIGN §2.3）+ 未满/L01 回归 | PASS |
| D12738 | 甲闸三文件 / GearNextHint / TrMenu / SetChase 图鉴：**本窗不改**；禁加 lore | lore≤3 |
| D12739 | NON 核：`git show` 无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸 densify；无新 hope/glance PAPI | PASS |
| D12740 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽 densify | 文内硬句 |
| D12741 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12742 | 玩家口径：满徽记+穿可烙甲 → 摘要写可烙该部位；无目标 → 缺可烙穿着件；未满/价/掉落不变 | 可核 |
| D12743 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或扩 stampable | 超额新 tip |

### 玩家口径（固定）

- 追烙徽记够、穿着可烙防具：枢纽手感与希望追烙会写 **可烙该部位**，和聊天就绪钮同词。  
- 徽记够但身上没有可烙件：写 **缺可烙穿着件**。  
- 徽记未满、价钱、掉落、徽记数本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-sigfeel-chase-ready-target-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-sigfeel-chase-ready-target-align-2026-10-12.md` |
| SoT | `EmberHopeMeter.sigChase` · `EmberSigFeel.menuLine` · `EmberSigChase.remainLine`/`progressLine` |
| 复用 | `EmberSigChase.readyCueSlotWord` · `imprintWornTarget`（TIP-AN） |
| 接线 | `EmberGrowthService.sigFeelLine` · `maybeReadyCue` |
| 单测 | `EmberHopeMeterTest` · `EmberSigFeelTest`（+ 可选 SigChase） |
| 已齐对照 | TIP-AN ready cue · TIP-AJ imprint_hope · TIP-AH preview |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸链 AM 闭合勿再扫 · 扩 stampable 禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint 甲闸  
- SetChase 六槽 densify；TrMenu 加 lore；签名四甲格 densify  

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12732–D12737；D12739 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] 插件 jar live 后策划 docs close  
- [ ] **L13 施工仍 HOLD**  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AN 后 · **可感 UX** — SigFeel/Hope/remain 追烙就绪目标件对齐（**非 densify · 非再扫甲闸 · 非扩 stampable**）  
- **批注：** AN 按钮已认甲，hub/hope/remain 满档仍糊「满可烙印」；复用 `readyCueSlotWord`/`imprintWornTarget` 改既有串；缺件明示；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 仍冻  

## STATUS: OPEN · 荐 A·M · 2026-10-12
