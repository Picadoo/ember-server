# tip · TIP-AR DropCraft/Q 日首加码 remain 诚实对齐（可施工薄 · 可感 UX）· D12776–D12791

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-插件** · 2026-10-12 21:14 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-drop-craft-q-pity-nudge-remain-align-2026-10-12.md`](../design/RESEARCH-ember-drop-craft-q-pity-nudge-remain-align-2026-10-12.md) · DESIGN [`DESIGN-ember-drop-craft-q-pity-nudge-remain-align-2026-10-12.md`](../design/DESIGN-ember-drop-craft-q-pity-nudge-remain-align-2026-10-12.md) · TIP-AQ CLOSED live `1.65.782-d12775` · feat `58e9ac299` · TIP-AP CLOSED live gem 加码 remain · TIP-Q CLOSED live craft settle · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **显式下一 tip：DropCraft/Q 加码 remain 旁债** — 同构 AP 宝石 + AQ 洗练；可感 UX（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify**）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberDropCraftPity` / `EmberDropQualityPity` remain·clearsLeft·hope + 单测；Settle/Hope/RunPapi 吃 SoT）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 NEED/FLOOR/BONUS / CRAFT_WEIGHTS / QUALITY_WEIGHTS · 零新开精工 Hope 轨**

---

## 0. 一句话

日首掉落精工/成色保底加码待用时，「再连出几次触发底」少报虚次并明示加码；不加码表、不叠 twin。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **DropCraft/Q 日首加码 remain 诚实对齐** | **采纳** | 总控显式；续 AP/AQ 同构；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| 仅修 C 或仅修 Q | **否** | 同窗同构单主题=掉落加码 remain |
| 新开精工 hub Hope 轨 | **否** | TIP-Q HOLD；禁新 PAPI |
| SigFeel / 追烙 leftover | **否** | AN→AO 已齐 |
| mapid / 图鉴扩六槽 | **否** | SetChase densify 硬禁 |
| GearNextHint 甲闸 | **否** | 总控禁再扫 |
| TrMenu hub lore | **否** | 禁加 lore |
| HopeMeter 其它 densify / glance twin | **否** | TIP-J/T 停窗 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12776 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12777 | DESIGN / tip power-paths 进度戳：**TIP-AQ CLOSED live**；**TIP-AR OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12778 | 错位钉死：E1–E11 加码真生效 vs remain/hope 多报（RESEARCH §1–2） | 可核 |
| D12779 | 「对齐」钉死：`ready && left>1 → left-1`；尾缀「今日加码待用」；零改 NEED/FLOOR/BONUS；零新 PAPI（DESIGN §2） | 可核 |
| D12780 | **插件：** `DropCraftPity.clearsLeft` + `remainLine(cur, nudgeReady)` / pd 喂 `DropCraftNudge.ready` | 代码+单测 |
| D12781 | **插件：** `DropQualityPity.clearsLeft(…, ready)` + `remainLine(…, ready)` / pd 喂 `DropQualityNudge.ready` | 可核 |
| D12782 | **插件：** `DropQualityPity.hope(pd)` → `remainLine(pd)`（Hope/RunPapi 吃 SoT） | 可核 |
| D12783 | **插件：** 单测最小包（DESIGN §2.4）+ TIP-Q/D1323 一元回归 | PASS |
| D12784 | SettleService / HopeMeter drop_q / RunPapi：**本窗不改接线**（吃 SoT）；禁新开精工 Hope 轨；禁加 lore | lore≤3 |
| D12785 | NON 核：`git show` 无 NEED/FLOOR/BONUS/权重常量改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI | PASS |
| D12786 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；新开精工 Hope 轨 | 文内硬句 |
| D12787 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12788 | 玩家口径：加码待用 → 少报虚次 +「今日加码待用」；已用/满档/权重/NEED 不变 | 可核 |
| D12789–D12791 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或扩 stampable/SetChase 六槽 | 超额新 tip |

### 玩家口径（固定）

- 今日掉落精工或成色保底加码还没用：结算（和成色枢纽）会写 **今日加码待用**，且「再连出几次」按下次会多计一次来算，不再多报。  
- 今日加码已用或保底已满：跟以前一样。  
- 保底仍要满 5、加码仍是 +1；掉落权重、NEED、FLOOR 本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-drop-craft-q-pity-nudge-remain-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-drop-craft-q-pity-nudge-remain-align-2026-10-12.md` |
| SoT C | `CoreRpg/.../EmberDropCraftPity.java` `remainLine` · `clearsLeft` |
| SoT Q | `CoreRpg/.../EmberDropQualityPity.java` `remainLine` · `clearsLeft` · `hope` |
| 复用 | `EmberDropCraftNudge.ready` / `BONUS`（D10737）· `EmberDropQualityNudge.ready` / `BONUS`（D10721） |
| 吃 SoT（本窗不改接线） | `EmberSettleService` C/Q remain · `EmberHopeMeter` drop_q · `EmberRunPapi` `drop_q_hope` |
| 单测 | `EmberDropCraftPityTest`（扩）· `EmberDropQualityPityRemainTest`（扩）· 可选 NudgeTest |
| 已齐对照 | TIP-AP gem 加码 remain · TIP-AQ reroll 加码 remain · TIP-Q craft settle |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · 新开精工 Hope 轨禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `NEED` / `FLOOR` / `BONUS` / `CRAFT_WEIGHTS` / `QUALITY_WEIGHTS` / `applyToGrants` 结算序  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint 甲闸  
- SetChase 六槽 densify；mapid haveNeed 扩甲；TrMenu 加 lore  
- 新开精工 HopeMeter 轨 / 新 `%corerpg_p1_*`

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12780–D12783；D12785 `git show` 干净  
- [ ] 菜单默认不动；lore≤3；无新精工 Hope 轨  
- [ ] 插件 jar live 后 stamp CLOSED  
- [ ] **L13 施工仍 HOLD**  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AQ 后 · **总控显式** — DropCraft/Q 日首加码 remain 诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify**）  
- **批注：** AP/AQ 加码 remain 已齐；`DropCraftNudge`/`DropQualityNudge` 日首 +1 live 但 remain/hope/结算仍按裸 cur/5 多报「再连出×n」；`ready && left>1 → left-1` + 尾缀「今日加码待用」；Q hope→remain；零改 NEED/FLOOR/BONUS/权重；零新 PAPI；零新开精工 Hope 轨；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 仍冻  

## STATUS: OPEN · TIP-AR · 待批 A·M · 2026-10-12 21:12 CST
