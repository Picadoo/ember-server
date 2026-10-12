# tip · TIP-BE 烬斩 SF4 战斗微效用 DESIGN stage0（docs-only · HOLD 施工）· D12984–D12999

> **状态：** CLOSED · 批 **A · 方案 M** · docs-only · HOLD 施工（总控 2026-10-12 23:06 Asia/Shanghai）  
> **上游：** RESEARCH [`RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md`](../design/RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md) · DESIGN [`DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md`](../design/DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md) · TIP-BD CLOSED live `1.65.795-d12983` · feat `9a89f68a6` · items `35b6c7e3c` · TIP-Y CLOSED · TIP-K CLOSED · D301 招架 LIVE · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** soft/开孔文案链已齐 · Anvil densify 仍冻 → 换轴 **可感变强途径**：SF4 形状战斗微效用 stage0（**非 densify · 非 skill_mult · 非 Anvil**）  
> **施工岗：** **无码**（策划 RESEARCH + DESIGN + tip；**不**派插件；勿借机改 skill_mult / CD / 几何 / Affix / 招架 / soft）  
> **硬禁：** **零抬 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify · 零深化 densify · 零 sx20 · 零粒子 densify** · **不重开 TIP-K 同构 cue** · **零改 PARRY_CD/flat · 零复活守墓壁垒** · **零抬 STAMP/C12 · 零扩 stampable · 零改 L11/L13 mods · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 xp-boost cue 再叠 · 零 GemHole Open/拒线再叠 · 零 enhance+5 开孔文案再叠 · 零 LifeCap/UndoWindow/FestivalEntry/AnvilRepair densify · 零 topUpTo 扩 soft · 零再降开孔门槛 densify · 零灰印/聚火夹带**

---

## 0. 一句话

离开已齐 soft/开孔链：把 SF4（形状短窗微缓/微疗）钉成可批 DESIGN stage0；**HOLD 施工**至 p1sim within + 显式开工；本窗 **无码**。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **SF4 战斗微效用 DESIGN stage0** | **采纳** | TIP-Y P2 真债；Stage2 已点名；离开 soft/开孔；守招已 live 不抢；**非 densify · 非 skill_mult · 非 Anvil** |
| AnvilRepair Soft 假耗 | **否** | 硬禁 densify 未解冻；旁债仍冻 |
| topUpTo 接 soft | **否** | 扩 soft densify |
| soft-latch / CostSoft / pity / Cap / firstsoft / cook·life-buy / xp-boost / GemHole / enhance+5 再叠 | **否** | 链已齐 / 硬禁 |
| TIP-K cue / 粒子 densify | **否** | 身份债 CLOSED |
| 守招再调 / 改招架 CD·flat | **否** | D301 LIVE；禁 densify |
| 灰印 / 聚火重设计 | **否（本窗）** | 旁债；先钉 SF4 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可钉 stage0 真债 |

---

## 2. 方案 M（定稿荐 · 无码）

| ID | 项 | 验收 |
|----|-----|------|
| D12984 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12985 | DESIGN / tip power-paths 进度戳：**TIP-BD CLOSED live**；**TIP-BE OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12986 | RESEARCH 校正：TIP-Y「守招 HOLD」→ D301 **LIVE**；SF4 仍 OPEN；soft/开孔链已齐 | 可核 |
| D12987 | DESIGN 文首硬句：stage0 可批 ≠ 开工；须 p1sim within + 显式开工 | 可核 |
| D12988 | 问题陈述：SF1–3 SHIP · 形状无战斗微差 · 招架正交 | 可核 |
| D12989 | 身份核：烬斩 F 三形状 · 非伤乘 · 不改招架/身法/烬突 | 可核 |
| D12990 | 主效用 U1 微缓（荐）· U2 微疗 · U3 微抗；**单 cand 进 T0** | 可核 |
| D12991 | 触发窗 / ICD 结构意向；禁常驻光环；禁 cue 墙 | 可核 |
| D12992 | 门禁表 G-within / G-prefer / G-x / G-ortho / G-id / G-hold | 可核 |
| D12993 | W 切分：本窗 stage0 · T0 p1sim · T1 显式开工 · T2 测 | 可核 |
| D12994 | 插件岗：本 tip **无码**；总控批 A·M 后 **不**自动派插件 | 无 jar |
| D12995 | soft/开孔/Anvil/招架 densify：**本窗不改** | 可核 |
| D12996 | NON 核：`git show` docs-only；无 CoreRpg/TrMenu/p1map 业务；无 skill_mult/CD/几何/Affix/STAMP | PASS |
| D12997 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠；soft-latch 假耗再叠；xp-boost cue 再叠；GemHole Open/拒线再叠；enhance+5 开孔再叠；LifeCap/UndoWindow/FestivalEntry/Anvil densify；topUpTo 扩 soft；再降开孔门槛 densify；灰印/聚火夹带；无 sim 上 SF4 施工 | 文内硬句 |
| D12998 | 玩家口径：形状以后才可能带短缓/短疗；本窗不改手感数字；招架照旧 | 可核 |
| D12999 | 缓冲：错链/错戳/文案过长压行；禁借号抬 skill_mult 或开 L13 施工或 Anvil densify 或 soft 再叠；SF4 T0 / 灰印另 tip | 超额新 tip |

### 玩家口径（固定）

- 本窗 **不改** 任何技能数字、CD、命中盒。  
- 以后若开 SF4：换形状可能带 **很短** 的缓或疗——**不是**加伤害。  
- 招架（Q）照旧；强化/开孔/日首减负文案链已齐，本窗不重做。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md` |
| Stage2 上游 | `docs/design/DESIGN-ember-skill-feel-stage2-micro-stage0-2026-10-11.md` |
| TIP-Y | `docs/design/RESEARCH-ember-skill-feel-post-k-next-2026-10-12.md` |
| 招架正交 | `EmberParry` · STATUS-D301（只读） |
| 旁轨 HOLD | TIP-AI L13 FAIL · Anvil soft densify · soft 展示再叠 · 灰印/聚火 |

---

## 4. 明确不做

- 任何 CoreRpg / TrMenu / p1map / Economy 业务改码或数值表  
- 抬 skill_mult · 改 CD · 改 FAN/LINE/RING 命中盒 · Affix · 伤乘  
- TIP-K cue twin · 粒子 densify · glance twin · sx20 · 深化 densify · ForgeChoice densify  
- 改招架 CD/flat · 复活守墓壁垒 · 灰印/聚火夹带  
- soft-latch / CostSoft / pity / Cap / firstsoft / cook·life-buy / xp-boost / GemHole / enhance+5 再叠  
- Anvil densify · topUpTo 扩 soft · 再降开孔门槛 · 扩 stampable · 甲闸再扫 · SetChase 六槽 · 开 L13 施工 tip  
- 把本 DESIGN 当开工令；开 T1 施工 tip / 派插件（须门禁+显式开工）

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 文首 HOLD / 门禁 / 选题否决表可核  
- [x] **无**插件施工派单；**无**业务 jar  
- [x] `git show` docs-only；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 已批  
- 选题：TIP-BD 后 · **离开 soft/开孔链** · **SF4 战斗微效用 DESIGN stage0**（docs-only · HOLD 施工 · 须 p1sim within）  
- **批注：** soft 展示族+开孔文案已齐；Anvil soft densify / topUpTo 仍冻；守招 D301 LIVE（TIP-Y 守招 HOLD 过时）；SF4 = 形状短窗微缓（U1 荐）非伤乘；零 skill_mult / CD / 几何；本窗无码无派插件；T0 p1sim 另窗；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / soft-latch·CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠 / xp-boost / GemHole / enhance+5 再叠 / Anvil densify / topUpTo 扩 soft / 再降开孔门槛 仍冻  

## STATUS: CLOSED · 批 A·M · tip D12984–D12999 · 总控 2026-10-12 23:06 Asia/Shanghai · 无码 · HOLD 施工 · 不派插件
