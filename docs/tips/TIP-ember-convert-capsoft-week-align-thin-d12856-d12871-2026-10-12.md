# tip · TIP-AW 转化周次容错 CapSoft 预览/weekLine 诚实对齐（可施工薄 · 可感 UX）· D12856–D12871

> **状态：** **CLOSED** · live `1.65.788-d12871` · feat `07923c868` · 2026-10-12 22:05 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-convert-capsoft-week-align-2026-10-12.md`](../design/RESEARCH-ember-convert-capsoft-week-align-2026-10-12.md) · DESIGN [`DESIGN-ember-convert-capsoft-week-align-2026-10-12.md`](../design/DESIGN-ember-convert-capsoft-week-align-2026-10-12.md) · TIP-AV CLOSED live `1.65.787-d12855` · feat `901632f09` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径** — **离开 soft-glance / pity-remain / 甲闸 / 追烙摘要族**（AS→AV / AP–AR / AK–AM / AN–AO / AU 已齐）；ConvertCapSoft 周次容错 leftover  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `GrowthService.convertHeld` 预览 peek / 确认 consume · `EmberConvertPath.weekLine` / afterSet 余次 · ConvertSure 直办同 SoT · 可选 CapSoft `effectiveExtra` + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 WEEKLY_CAP/C_CONV · 零 ConvertCap densify · 零再叠 CostSoft glance · 零 pity-remain 再叠 · 零 soft-glance 费用族再叠 · 零发明第二容错**

---

## 0. 一句话

周转化已满且周次容错待用时，weekLine/预览明示还可容错 1 次，且只在确认成功时消耗容错；预览不吞 soft。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **转化周次容错 CapSoft 预览/weekLine 诚实对齐** | **采纳** | 离开 soft-glance/pity/甲闸/追烙；peek 误吞+假完成；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| gearlibHope 撤销窗 +5 展示 | **否** | 欠报非假负；UndoWindow densify STOP |
| MapFarmCue imprint soft 下一环 | **否** | 追烙摘要邻域 |
| mapid 扩甲 haveNeed | **否** | 扩 stampable / SetChase 邻域 |
| CostSoft / pity / 甲闸再扫 | **否** | 链已齐 |
| TrMenu hub lore | **否** | 禁加 lore |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| ConvertCap densify | **否** | post STOP |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12856 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12857 | DESIGN / tip power-paths 进度戳：**TIP-AV CLOSED live**；**TIP-AW OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12858 | 错位钉死：E1–E10 预览 consume + weekLine 假完成 + 确认拒办（RESEARCH §1–2） | 可核 |
| D12859 | 「对齐」钉死：预览 peek；确认 consume；weekLine 容错待用文案；零改 WEEKLY_CAP；零新 PAPI；零发明第二容错（DESIGN §2） | 可核 |
| D12860 | **插件：** `convertHeld` `!confirm` 仅 `CapSoft.ready` peek；禁预览 consume | 代码+单测 |
| D12861 | **插件：** `convertHeld` `confirm=true` 才 `consume`；成功再喊容错句 | 可核 |
| D12862 | **插件：** 预览 weekLeft / AB：满周+ready →「容错待用 · 还可 1 次」 | 可核 |
| D12863 | **插件：** `ConvertPath.weekLine` / afterSet 余次同口径 | 可核 |
| D12864 | **插件：** ConvertSure 直办走 confirm 吞 soft；单测最小包（DESIGN §2.3）+ AT/AV 回归 | PASS |
| D12865 | CostSoft glance / pity remain / TrMenu lore / 甲闸 / SetChase / soft-glance 费用族：**本窗不改**；禁加 lore | lore≤3 |
| D12866 | NON 核：`git show` 无 WEEKLY_CAP/C_CONV/CapSoft 周键改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 CostSoft/pity 再叠；无 ConvertCap densify；无发明第二容错 | PASS |
| D12867 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft glance 再叠；pity-remain 再叠；soft-glance 费用族再叠 | 文内硬句 |
| D12868 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12869 | 玩家口径：满周+容错待用 → 明示还可 1 次；预览不吞；确认才消耗；表帽/费用不变 | 可核 |
| D12870–D12871 | 缓冲：错链/错戳/文案过长压行；禁借号抬 WEEKLY_CAP 或开 L13 施工或扩 stampable/SetChase 六槽/甲闸再扫/ConvertCap densify/CostSoft·pity 再叠 | 超额新 tip |

### 玩家口径（固定）

- 本周转化次数已用完、且本周容错还没用：weekLine / 转化预览写 **周次容错待用 · 还可 1 次**；点确认才消耗容错。  
- 预览（未确认）**不消耗**容错。  
- 容错已用或本周未满帽：跟以前一样。  
- 每周转化上限仍是 1；容错仍是额外 1 次/每周一次；费用 / CostSoft 本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-convert-capsoft-week-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-convert-capsoft-week-align-2026-10-12.md` |
| SoT CapSoft | `CoreRpg/.../EmberConvertCapSoft.java` |
| SoT weekLine | `CoreRpg/.../EmberConvertPath.java` weekLine / afterSet |
| SoT 接线 | `CoreRpg/.../EmberGrowthService.java` convertHeld |
| 复用 | ConvertSure · ConvertRules.WEEKLY_CAP · ConvertCostSoft（AT 已齐 · 本窗不改） |
| 已齐对照 | TIP-AT Convert CostSoft glance · TIP-AV LockShard soft glance · TIP-AU Hope soft |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · ConvertCap densify 禁 · CostSoft/pity/soft-glance 再叠禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `WEEKLY_CAP` / `C_CONV` / CapSoft `C_USED` 周键 / 发明第二容错或抬额外次数  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- CostSoft glance 再叠；pity nudge remain 再叠；soft-glance 费用族再叠；ConvertCap densify  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 批后插件：D12860–D12864；D12866 `git show` 干净  
- [x] 菜单默认不动；lore≤3  
- [x] 预览不吞 soft；确认才 consume；live `1.65.788-d12871` · feat `07923c868`；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AV 后 · **离开 soft-glance/pity/甲闸/追烙** — 转化周次容错 CapSoft 诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify**）  
- **批注：** AS→AV CostSoft glance 已齐；ConvertCapSoft 已 live，但 `convertHeld` 预览即 `consume` 且 weekLine 满周写「环已完成」→ 预览吞容错、确认拒办、假完成拦人；预览 peek + 确认 consume + weekLine「容错待用 · 还可 1 次」；零改 WEEKLY_CAP；零新 PAPI；零发明第二容错；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / ConvertCap densify / CostSoft·pity·soft-glance 再叠 仍冻  

## STATUS: CLOSED · live 1.65.788-d12871 · 07923c868 · 2026-10-12 22:05 CST
