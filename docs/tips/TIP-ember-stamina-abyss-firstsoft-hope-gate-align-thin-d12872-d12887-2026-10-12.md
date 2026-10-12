# tip · TIP-AX 进本日首减负（体力+层费）门禁/Hope 诚实对齐（可施工薄 · 可感 UX）· D12872–D12887

> **状态：** **OPEN** · docs-only · 待总控批 A·M · 2026-10-12 22:07 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-stamina-abyss-firstsoft-hope-gate-align-2026-10-12.md`](../design/RESEARCH-ember-stamina-abyss-firstsoft-hope-gate-align-2026-10-12.md) · DESIGN [`DESIGN-ember-stamina-abyss-firstsoft-hope-gate-align-2026-10-12.md`](../design/DESIGN-ember-stamina-abyss-firstsoft-hope-gate-align-2026-10-12.md) · TIP-AW CLOSED live `1.65.788-d12871` · feat `07923c868` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径** — **离开 soft-glance / pity-remain / ConvertCapSoft / 甲闸 / 追烙摘要族**（AS→AV / AP–AR / AW / AK–AM / AN–AO / AU 已齐）；进本日首减负门禁/Hope leftover  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `StaminaFirstSoft.peekCost` / `AbyssFeeSoft.peekFee` · `EntryService.admit` 体力门禁 · `AbyssService.entryProblems` 层费门禁 · `CorePapiStamina` hope/blocked · `RunPapi` abyss_hope · 可选 SOFT_TAIL + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 LESS/MULT/C_USED/体力表/层费表 · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance 再叠 · 零 LifeCap/UndoWindow densify**

---

## 0. 一句话

日首进本体力/层费减负待用时，门禁与希望口按减负后费用判够不够；预览门禁不吞 soft，进本预留才消耗。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **进本日首减负（体力+层费）门禁/Hope 诚实对齐** | **采纳** | 离开 soft-glance/pity/cap/甲闸/追烙；假负可感；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽 · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance 再叠** |
| gearlibHope 撤销窗 +5 | **否** | 欠报非假负；UndoWindow densify STOP |
| LifeDailyCapSoft show「满」 | **否** | Cap soft 刚齐 AW；易叠 densify 感 |
| mapid 扩甲 haveNeed | **否** | 扩 stampable / SetChase 邻域 |
| CostSoft / pity / ConvertCap / 甲闸再扫 | **否** | 链已齐 / 硬禁 |
| TrMenu hub lore | **否** | 禁加 lore |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感假负门禁债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12872 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12873 | DESIGN / tip power-paths 进度戳：**TIP-AW CLOSED live**；**TIP-AX OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12874 | 错位钉死：E1–E10 预留已 soft vs admit/entryProblems/hope/blocked 全额（RESEARCH §1–2） | 可核 |
| D12875 | 「对齐」钉死：peekCost/peekFee；门禁+hope+blocked 吃 peek；consume 仅预留；零改 LESS/MULT；零新 PAPI（DESIGN §2） | 可核 |
| D12876 | **插件：** `StaminaFirstSoft.peekCost` + `AbyssFeeSoft.peekFee` | 代码+单测 |
| D12877 | **插件：** `EntryService.admit` 体力门禁 peekCost（可选 SOFT_TAIL） | 可核 |
| D12878 | **插件：** `AbyssService.entryProblems` 层费短欠 peekFee | 可核 |
| D12879 | **插件：** `CorePapiStamina` stamina_hope + stamina_blocked_* peek；`RunPapi` abyss_hope peek+afford | 可核 |
| D12880 | **插件：** 单测最小包（DESIGN §2.3）+ Session consume 回归（不吞于 peek） | PASS |
| D12881 | CostSoft glance / pity remain / ConvertCap / TrMenu lore / 甲闸 / SetChase：**本窗不改**；禁加 lore；`stamina_cost_*` 表价键不改 | lore≤3 |
| D12882 | NON 核：`git show` 无 LESS/MULT/C_USED/体力表/层费表改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 CostSoft/pity/ConvertCap densify；无 LifeCap/UndoWindow densify | PASS |
| D12883 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft·pity·soft-glance 再叠；LifeCap/UndoWindow densify | 文内硬句 |
| D12884 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12885 | 玩家口径：减负待用 → 门禁/希望按减负后费用；预览不吞；进本才消耗；LESS/MULT/表不变 | 可核 |
| D12886–D12887 | 缓冲：错链/错戳/文案过长压行；禁借号抬 LESS/MULT 或开 L13 施工或扩 stampable/SetChase 六槽/甲闸再扫/ConvertCap densify/CostSoft·pity·soft-glance 再叠 | 超额新 tip |

### 玩家口径（固定）

- 今日首发体力减负还没用：进本门禁 / 体力希望按 **减负后体力需求** 判够不够；可标 **今日减负待用**；点进本才消耗。  
- 今日深渊层费减负还没用：层费门禁 / 深渊希望按 **×0.8 后费用** 判够不够；可标减负待用；进层预留才消耗。  
- 减负已用：跟以前全额一样。  
- 减负仍是 −5 体力 / ×0.8 层费、一日一次；体力表与层费表本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-stamina-abyss-firstsoft-hope-gate-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-stamina-abyss-firstsoft-hope-gate-align-2026-10-12.md` |
| SoT soft | `CoreRpg/.../EmberStaminaFirstSoft.java` · `EmberAbyssFeeSoft.java` |
| SoT 门禁 | `CoreRpg/.../EmberEntryService.java` admit · `EmberAbyssService.java` entryProblems |
| SoT hope | `CoreRpg/.../EmberHopeMeter.java` · `CorePapiStamina.java` · `EmberRunPapi.java` abyss_hope |
| 复用 | SessionService consume（已诚实 · 本窗不改）· `GearNextHint.SOFT_TAIL`（可选） |
| 已齐对照 | TIP-AW CapSoft · TIP-AU Hope soft · TIP-AS–AV CostSoft glance |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · ConvertCap densify 禁 · CostSoft/pity/soft-glance 再叠禁 · LifeCap/UndoWindow densify 禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `LESS` / `MULT` / `C_USED` / 体力表 / 层费表 / SessionService consume 序（已诚实）  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- ConvertCap densify；CostSoft glance 再叠；pity nudge remain 再叠；soft-glance 工坊费用族再叠；LifeCap / UndoWindow densify  

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12876–D12880；D12882 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] peek 不吞 soft；进本预留才 consume；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AW 后 · **离开 soft-glance/pity/ConvertCap/甲闸/追烙** — 进本日首减负门禁/Hope 诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify · 非 ConvertCap densify · 非 CostSoft·pity·soft-glance 再叠**）  
- **批注：** AS→AV / AP–AR / AW CapSoft 已齐；StaminaFirstSoft/AbyssFeeSoft 预留已 live，但 admit/entryProblems/stamina_hope/abyss_hope/stamina_blocked_* 仍全额 → 「体力不足/币不够/费用不足」假负；peekCost/peekFee + 门禁/hope/blocked 对齐；零改 LESS/MULT；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / ConvertCap densify / CostSoft·pity·soft-glance 再叠 仍冻  

## STATUS: OPEN · TIP-AX · D12872–D12887 · 2026-10-12 22:07 CST
