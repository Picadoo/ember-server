# tip · TIP-AJ 签名页防具侧生效摘要对齐（可施工薄 · 可感 UX）· D12648–D12663

> **状态：** **CLOSED live** `1.65.775-d12663.local` · 批 **A · 方案 M** · feat `82d6a03fa` · chore `e5de8a922` · items `a167862a8` · 2026-10-12 20:05 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-sig-page-defense-summary-align-2026-10-12.md`](../design/RESEARCH-ember-sig-page-defense-summary-align-2026-10-12.md) · DESIGN [`DESIGN-ember-sig-page-defense-summary-align-2026-10-12.md`](../design/DESIGN-ember-sig-page-defense-summary-align-2026-10-12.md) · TIP-AH CLOSED live `1.65.774-d12631`（run 预览已齐）· TIP-AE CLOSED live `1.65.773-d12599` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径（非 densify）** — AH E9「签名页摘要另债」；正交刃 L13 HOLD  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberGrowthService` sigPapi/toggle/imprint_hope/图鉴 a_）；**余烬-物品** TrMenu `ember_p1_sig.yml` C 名（**禁**加 lore 行）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip**

---

## 0. 一句话

把签名传奇页原「已选护符」格改成 **防具侧胜出摘要**（与战斗 / AH 预览同 prio），点格开关该槽签名；图鉴/希望花费跟「护符或防具」诚实口径。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **签名页防具侧生效摘要对齐** | **采纳** | AH 显式另债；甲胜出时 C 格说谎；可施工；零功率表 |
| L13 施工 tip | **否** | TIP-AI FAIL 预期；PASS≠开工；显式开工前 **HOLD** |
| Hope/SetChase/gem glance 再 twin | **否** | TIP-J/T 停窗；禁 densify |
| ForgeHeldNext / GearNextHint densify | **否** | 已 SHIP；非本缺口 |
| 扩甲 stampable | **否** | AE 硬禁延续；掉落另轨 |
| 观察停窗 only | **否** | 有可感可施工诚实债，优先施工薄 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12648 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12649 | DESIGN / tip power-paths 进度戳：**TIP-AI CLOSED FAIL**；**TIP-AJ OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12650 | 错位钉死：E3–E8 签名页 charm 字面 vs 战斗 5 取 1（RESEARCH §1） | 可核 |
| D12651 | 「对齐」钉死：C 摘要 ≡ 防具侧胜出槽；toggle 同槽；仍两格；零新 glance 族（DESIGN §2） | 可核 |
| D12652 | **插件：** `defenseActiveSlot`（或等价）复用 AH 序；`sigPapi` `charm`/`charm_o`/`charm_t` 语义扩 | 代码+单测 |
| D12653 | **插件：** `sigToggle("charm")` → 翻转胜出槽 `sigOff`；null 诚实提示 | 可核 |
| D12654 | **插件：** 图鉴 `a_` 非刃 →「护符或防具」；`imprint_hope` 用 `imprintWornTarget`；`/corerpg p1 sig` 摘要跟胜出槽 | 可核 |
| D12655 | **插件：** 单测最小包（DESIGN §2.6） | PASS |
| D12656 | **物品：** `ember_p1_sig.yml` C `name` → `§6防具侧生效`；**禁**加 lore 行 | lore≤3 |
| D12657 | NON 核：`git show` 无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无新签名 id；无新甲格 | PASS |
| D12658 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；签名页四甲独立格；glance/深化 densify | 文内硬句 |
| D12659 | 批后派单句：**余烬-插件** + 物品 C 名；策划 docs-only | 派单句 |
| D12660 | 玩家口径：真槽名 + 仍最多刃+1 防具侧 + 价/掉落/数字不变 | 可核 |
| D12661–D12663 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工 | 超额新 tip |

### 玩家口径（固定）

- 签名页右边那格会写你 **真正生效的那件防具侧**（可能是头盔等），不再死写「已选护符」。  
- 点它 = 开关 **这件** 上的签名（回城）。  
- 还是最多 **刃 + 一件防具侧**；烙印花费/掉落/数字本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-sig-page-defense-summary-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-sig-page-defense-summary-align-2026-10-12.md` |
| PAPI / toggle | `CoreRpg/.../EmberGrowthService.java` `sigPapi` · `sigToggle` · 列表摘要 · `imprint_hope` |
| 生效核 | `EmberSignature.active` / `pickDefense` · `defenseWorn` · `imprintWornTarget` |
| 预览序（复用） | AH `previewActiveSlots` / `previewSlot` / `sigWhy` |
| 菜单 | `plugins/TrMenu/menus/ember_p1_sig.yml` Icon C |
| PAPI 键 | `charm` / `charm_o` / `charm_t` — **不改键名、扩语义** |
| 旁轨 HOLD | TIP-AI L13 FAIL · TIP-AD exclude L11/L13 · TIP-AE 甲 stampable 仍空 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / glance / sx20 / 深化 densify / ForgeChoice densify  
- 开 L13 施工 tip（**HOLD** 至显式开工）  
- 签名页加头/胸/腿/靴四格；冒险 Q lore densify  
- 扩甲 stampable；改 `attuneCheck` / ALTS 价  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 批后插件：D12652–D12655；D12657 `git show` 干净（feat `82d6a03fa` 仅 2 Java）  
- [x] 物品：D12656 C 名 only · lore≤3（`a167862a8`）  
- [x] 插件 jar live `1.65.775-d12663.local`；策划 docs close 本窗  
- [x] **L13 施工仍 HOLD**  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AI 后 · **可感 UX** — 签名页防具侧生效摘要对齐（AH E9 另债 · 非 densify）  
- **批注：** AE+AH 后战斗/预览已 5 取 1，签名页 C 仍 charm 字面说谎；扩 `charm*` PAPI 语义 + toggle 胜出槽 + C 名「防具侧生效」+ 图鉴/imprint_hope 诚实；零功率表；仍两格；批后派 **余烬-插件**（+物品 C 名）；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance / sx20 / skill_mult / STAMP / L11·L13 mods / stampable 扩 仍冻  

## STATUS: CLOSED live · 1.65.775-d12663.local · feat 82d6a03fa · chore e5de8a922 · items a167862a8 · 2026-10-12 20:05 CST
