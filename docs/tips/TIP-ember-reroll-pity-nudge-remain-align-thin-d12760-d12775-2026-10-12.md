# tip · TIP-AQ 洗练保底日首加码 remain 诚实对齐（可施工薄 · 可感 UX）· D12760–D12775

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-插件** · 2026-10-12 21:07 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-reroll-pity-nudge-remain-align-2026-10-12.md`](../design/RESEARCH-ember-reroll-pity-nudge-remain-align-2026-10-12.md) · DESIGN [`DESIGN-ember-reroll-pity-nudge-remain-align-2026-10-12.md`](../design/DESIGN-ember-reroll-pity-nudge-remain-align-2026-10-12.md) · TIP-AP CLOSED live `1.65.781-d12759` · feat `c5c7ae391` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径（非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify）** — 续 AP 同构轴：洗练日首加码 vs remain/希望说谎  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberRerollPity` remain/attempts 三元 · GrowthService 预览喂 ready · HopeMeter 洗练轨改吃 remain · 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 reroll.pity/BONUS**

---

## 0. 一句话

日首洗练保底加码待用时，「含本次≤n 次必出」少报虚次并明示加码；不加码表、不叠 twin。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **洗练保底日首加码 remain 诚实对齐** | **采纳** | 续 AP 宝石同构；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| DropCraft / DropQuality 加码 remain | **否本窗** | 旁债另 tip；单主题 |
| SigFeel / 追烙 leftover | **否** | AN→AO 已齐 |
| mapid / 图鉴扩六槽 | **否** | SetChase densify 硬禁 |
| GearNextHint 甲闸 | **否** | 总控禁再扫 |
| TrMenu hub lore | **否** | 禁加 lore |
| HopeMeter 其它 densify / glance twin | **否** | TIP-J/T 停窗 |
| 主线下一环施工 | **否** | 本窗同构诚实债优先 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12760 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12761 | DESIGN / tip power-paths 进度戳：**TIP-AP CLOSED live**；**TIP-AQ OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12762 | 错位钉死：E1–E10 加码真生效 vs remain/希望多报（RESEARCH §1–2） | 可核 |
| D12763 | 「对齐」钉死：`ready && incl>1 → incl-1`；尾缀「今日加码待用」；零改 pity/BONUS；零新 PAPI（DESIGN §2） | 可核 |
| D12764 | **插件：** `attemptsUntilCap(pity, need, nudgeReady)` + 旧二元委托 | 代码+单测 |
| D12765 | **插件：** `remainLine(pity, need, nudgeReady)` / 确认预览喂 `RerollPityNudge.ready` | 可核 |
| D12766 | **插件：** HopeMeter 洗练轨改吃 remain（…, ready）；glance 二元可留测 | 可核 |
| D12767 | **插件：** 单测最小包（DESIGN §2.3）+ D1248 二元回归 | PASS |
| D12768 | DropCraft/DropQuality / TrMenu / 甲闸 / SetChase / mapid / GearNextHint：**本窗不改**；禁加 lore | lore≤3 |
| D12769 | NON 核：`git show` 无 reroll.pity/BONUS 常量改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI | PASS |
| D12770 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；DropCraft/Q 加码 remain 另 tip | 文内硬句 |
| D12771 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12772 | 玩家口径：加码待用 → 少报虚次 +「今日加码待用」；已用/满档/价/pity 表不变 | 可核 |
| D12773–D12775 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或扩 stampable/SetChase 六槽 | 超额新 tip |

### 玩家口径（固定）

- 今日洗练保底加码还没用：确认页和希望轨会写 **今日加码待用**，且「含本次≤n 次」按本次会多计一次 pity 来算，不再多报。  
- 今日加码已用或已 forced：跟以前一样。  
- 保底阈值与加码 +1 不变；洗练币价、锁碎、词条池本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-reroll-pity-nudge-remain-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-reroll-pity-nudge-remain-align-2026-10-12.md` |
| SoT | `CoreRpg/.../EmberRerollPity.java` `remainLine` · `attemptsUntilCap` |
| 复用 | `EmberRerollPityNudge.ready` / `BONUS`（D10977） |
| 接线 | `EmberGrowthService` 确认预览 · `EmberHopeMeter.priorityTracks` 洗练轨 |
| 单测 | `EmberRerollPityTest`（扩）· 可选 `EmberRerollPityNudgeTest` |
| 已齐对照 | TIP-AP gem 加码 remain · TIP-AO 追烙链 · TIP-AM 甲闸链 |
| 旁轨 HOLD | TIP-AI L13 FAIL · DropCraft/Q 加码 remain · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `reroll.pity` / `BONUS` / `plan` 结算序  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint 甲闸  
- SetChase 六槽 densify；mapid haveNeed 扩甲；TrMenu 加 lore  
- 本窗顺手修 DropCraft / DropQuality 加码 remain（另 tip）

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12764–D12767；D12769 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] 插件 jar live 后 CLOSED  
- [ ] **L13 施工仍 HOLD**  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AP 后 · **续可感 UX** — 洗练保底日首加码 remain 诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify**）  
- **批注：** AP 宝石加码 remain 已齐；`RerollPityNudge` 日首 +1 live 但 remain/Hope 仍按裸 pity/need 多报「含本次≤n 次」；`ready && incl>1 → incl-1` + 尾缀「今日加码待用」；Hope 洗练轨改吃 remain；零改 pity/BONUS；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；DropCraft/Q 加码 remain 另 tip；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 仍冻  

## STATUS: OPEN · pending A·M · 2026-10-12
