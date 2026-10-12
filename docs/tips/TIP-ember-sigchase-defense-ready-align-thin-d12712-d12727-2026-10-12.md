# tip · TIP-AN SigChase 追烙就绪防具侧对齐（可施工薄 · 可感 UX）· D12712–D12727

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-插件** · 2026-10-12 20:37 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-sigchase-defense-ready-align-2026-10-12.md`](../design/RESEARCH-ember-sigchase-defense-ready-align-2026-10-12.md) · DESIGN [`DESIGN-ember-sigchase-defense-ready-align-2026-10-12.md`](../design/DESIGN-ember-sigchase-defense-ready-align-2026-10-12.md) · TIP-AM CLOSED live `1.65.778-d12711` · TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AE CLOSED live `1.65.773-d12599` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **换轴 · 可感 UX / 变强途径（非 densify · 非再扫甲闸）** — SigChase 追烙就绪防具侧对齐；正交甲闸链已闭合  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberSigChase.sameSlotPiece` / held-ready / ready cue 槽名 + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫 ForgeHeldNext/CommitNext/SinkGlance/GearNextHint 甲闸** · **零 Hope/SetChase/gem glance twin**

---

## 0. 一句话

追烙徽记够时「可烙」提示认防具侧目标件；不再漏甲或假写只烙护符。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **SigChase 追烙就绪防具侧对齐** | **采纳** | AE/AJ 后硬缺口；SoT 一处；零功率表；**换轴非甲闸** |
| GearNextHint 全局甲闸 | **否** | 甲闸 AK→AL→AM 已齐；总控禁再扫 |
| Hope/SetChase/gem glance 再 twin | **否** | TIP-J/T 停窗 |
| 扩 stampable 甲 | **否** | 硬禁 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12712 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12713 | DESIGN / tip power-paths 进度戳：**TIP-AM CLOSED live**；**TIP-AN OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12714 | 错位钉死：E4–E6 SigChase 严格槽/只扫刃符 vs E1–E3/E7 烙印页 fitsDefense（RESEARCH §1） | 可核 |
| D12715 | 「对齐」钉死：`sameSlotPiece`=`fitsDefense`+`imprintWornTarget` 序；ready 槽名=目标件；零新 glance（DESIGN §2） | 可核 |
| D12716 | **插件：** `EmberSigChase.sameSlotPiece` — held/worn 走 `fitsDefense` / 防具侧序 | 代码+单测 |
| D12717 | **插件：** `maybeHeldReadyTip` worn 判定跟目标 uid（含甲） | 可核 |
| D12718 | **插件：** `maybeReadyCue` 按钮槽名跟实际目标件（禁 charm-def 恒「护符」） | 可核 |
| D12719 | **插件可选：** GrowthService `imprintWornTarget` 委托同一 static | 可核 |
| D12720 | **插件：** 单测最小包（DESIGN §2.3）+ L01 刃回归 | PASS |
| D12721 | 甲闸三文件 / GearNextHint / TrMenu：**本窗不改**；禁加 lore | lore≤3 |
| D12722 | NON 核：`git show` 无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸 densify；无新 hope/glance PAPI | PASS |
| D12723 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify | 文内硬句 |
| D12724 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12725 | 玩家口径：追烙+穿/持可烙甲 → 就绪认甲；刃路径与价/掉落/数字不变 | 可核 |
| D12726–D12727 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或扩 stampable | 超额新 tip |

### 玩家口径（固定）

- 追一条护符侧签名、徽记够、身上穿着可烙的防具：会提示烙印就绪指向 **该防具**，不会只说护符。  
- 手里拿着可烙同签防具：手持就绪会认，不再静默。  
- 刃怎么追、价钱、掉落、徽记数本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-sigchase-defense-ready-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-sigchase-defense-ready-align-2026-10-12.md` |
| SoT | `CoreRpg/.../EmberSigChase.java` `sameSlotPiece` · `maybeHeldReadyTip` · `maybeReadyCue` |
| 复用 | `EmberSignature.fitsDefense` · GrowthService `imprintWornTarget` 序 |
| 单测 | `EmberSigHeldReadyTipTest`（扩防具侧） |
| 已齐对照 | TIP-AE fitsDefense · TIP-AJ imprint_hope · TIP-AH preview · 烙印页 pickTarget |
| 旁轨 HOLD | TIP-AI L13 FAIL · TIP-AD exclude L11/L13 · 甲闸链 AM 闭合勿再扫 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint 甲闸  
- Hope/SetChase/gem glance twin；签名四甲格 densify  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12716–D12720；D12722 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] 插件 jar live 后策划 docs close  
- [ ] **L13 施工仍 HOLD**  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AM 后 · **换轴可感 UX** — SigChase 追烙就绪防具侧对齐（**非 densify · 非再扫甲闸**）  
- **批注：** 甲闸 AK→AL→AM 已齐；AE/AJ 后烙印页已认防具侧，追烙 held-ready/ready 仍严格槽+只扫刃符 → 漏/假写护符；`sameSlotPiece`+cue 槽名对齐 `fitsDefense`/`imprintWornTarget`；零功率表；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 仍冻  

## STATUS: OPEN · 荐 A·M · 2026-10-12
