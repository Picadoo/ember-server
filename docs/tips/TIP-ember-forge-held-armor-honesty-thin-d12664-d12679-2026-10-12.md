# tip · TIP-AK ForgeHeldNext 手持护甲诚实对齐（可施工薄 · 可感 UX）· D12664–D12679

> **状态：** OPEN · 荐批 **A · 方案 M** · 2026-10-12  
> **上游：** RESEARCH [`RESEARCH-ember-forge-held-armor-honesty-2026-10-12.md`](../design/RESEARCH-ember-forge-held-armor-honesty-2026-10-12.md) · DESIGN [`DESIGN-ember-forge-held-armor-honesty-2026-10-12.md`](../design/DESIGN-ember-forge-held-armor-honesty-2026-10-12.md) · TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AH CLOSED live `1.65.774-d12631` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径（非 densify）** — ForgeHeldNext 甲闸诚实；正交 AJ 签名轴 / L13 HOLD  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberForgeHeldNext.line` 甲早退 + 单测；吃 SoT：`EmberForgeService#heldNextLine` / `EmberCommand` status）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip**

---

## 0. 一句话

手持护甲时「下一确定」与 K0 / 菜单同句拒绝；聊天与抬头不再假推成色强化。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **ForgeHeldNext 手持护甲诚实对齐** | **采纳** | 菜单真、聊天假；SoT 一处可施工；零功率表 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 甲待命半行 / 签名四甲格 | **否** | AJ 另债；易 densify |
| Hope/SetChase/gem glance 再 twin | **否** | TIP-J/T 停窗 |
| ForgeChoice densify | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12664 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12665 | DESIGN / tip power-paths 进度戳：**TIP-AJ CLOSED live**；**TIP-AK OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12666 | 错位钉死：E3–E5 HeldNext/聊天假推 vs E1–E2 菜单/K0 诚实（RESEARCH §1） | 可核 |
| D12667 | 「对齐」钉死：`line` 甲早退 ≡ `ARMOR_REFUSE`（±分解半行）；刃/护符不变；零新 glance（DESIGN §2） | 可核 |
| D12668 | **插件：** `EmberForgeHeldNext.line` — `d.isArmor()` 早退诚实句 | 代码+单测 |
| D12669 | **插件：** 可选分解零头半行（`dismantleCheck`/`armorDismantleTenths`）；禁假推 quality/refine/enhance/upgrade | 可核 |
| D12670 | **插件：** 单测最小包（DESIGN §2.3）+ 刃回归 | PASS |
| D12671 | 调用方：`heldNextLine` / status 焦点 **不改吃 SoT**；RunPapi **本窗不改** | 可核 |
| D12672 | TrMenu：**禁**加 lore；默认不改菜单 | lore≤3 |
| D12673 | NON 核：`git show` 无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无 ForgeChoice densify | PASS |
| D12674 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲待命/四甲格；glance/深化 densify；ForgeChoice densify | 文内硬句 |
| D12675 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12676 | 玩家口径：甲手持 → 拒绝/分解诚实；刃护符路径与价/掉落/数字不变 | 可核 |
| D12677–D12679 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工 | 超额新 tip |

### 玩家口径（固定）

- 手里拿着护甲再开工会话或看成长：「下一确定」会说 **护甲随护符成长**（或分解零头），不会假装能成色/强化。  
- 刃/护符怎么养本窗不动；价钱、掉落、数字不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-forge-held-armor-honesty-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-forge-held-armor-honesty-2026-10-12.md` |
| SoT | `CoreRpg/.../EmberForgeHeldNext.java` `line` |
| 复用 | `EmberItemData.isArmor` · `EmberUpgradeRules.ARMOR_REFUSE` · dismantle 零头 API |
| 吃 SoT | `EmberForgeService#heldNextLine` · `EmberCommand` status 焦点 |
| 已诚实对照 | `EmberRunPapi#heldNext` · `EmberSixPapi.heldArmorLine` |
| 单测 | `CoreRpg/.../EmberForgeHeldNextTest.java` |
| 旁轨 HOLD | TIP-AI L13 FAIL · TIP-AD exclude L11/L13 · TIP-AJ 签名轴已闭合 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / glance / sx20 / 深化 densify / ForgeChoice densify  
- 开 L13 施工 tip（**HOLD** 至显式开工）  
- 签名页甲待命半行 / 四甲独立格；改 `ARMOR_REFUSE` 为可锻  
- 新 hope/glance PAPI 族  

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12668–D12670；D12673 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] 插件 jar live 后策划 docs close  
- [ ] **L13 施工仍 HOLD**  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AJ 后 · **可感 UX** — ForgeHeldNext 手持护甲诚实对齐（非 densify）  
- **批注：** 菜单 `held_next_*` 已 K0 拒绝，聊天/AB/`status` 仍经 `ForgeHeldNext.line` 假推成色强化；SoT 甲早退 ≡ `ARMOR_REFUSE`（±分解半行）；零功率表；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance / sx20 / skill_mult / STAMP / L11·L13 mods / ForgeChoice densify 仍冻  

## STATUS: OPEN · 荐批 A·M · 2026-10-12
