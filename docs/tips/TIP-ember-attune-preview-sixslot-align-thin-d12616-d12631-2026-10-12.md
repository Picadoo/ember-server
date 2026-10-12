# tip · TIP-AH attune 预览六槽对齐（可施工薄 · 正交刃轨）· D12616–D12631

> **状态：** **CLOSED live** `1.65.774-d12631.local` · 批 **A · 方案 M** · feat `5e1b43c96` · chore `57efb60b8` · 2026-10-12 19:50 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-attune-preview-sixslot-align-2026-10-12.md`](../design/RESEARCH-ember-attune-preview-sixslot-align-2026-10-12.md) · DESIGN [`DESIGN-ember-attune-preview-sixslot-align-2026-10-12.md`](../design/DESIGN-ember-attune-preview-sixslot-align-2026-10-12.md) · TIP-AE CLOSED live `1.65.773-d12599`（defense-side 5 取 1）· TIP-AG CLOSED `7d61761e4`（L13 DESIGN stage0 APPROVED · **HOLD 施工**）  
> **选题：** 总控 **(2) attune 预览六槽对齐** — 可感 UX；**非** densify；L13 stage1 **下一 tip HOLD**  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberGrowthService` runPreview* 主责）；**余烬-物品** 仅可选 TrMenu **注释**勘误（**禁**加 lore 行）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods**

---

## 0. 一句话

把 `%corerpg_p1_sig_run%` / `run_cost` 从「只报刃+护符」改成与战斗相同的 **刃 1 + 防具侧 5 取 1**；甲上生效时写出头盔/胸甲等真槽名。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| (1) L13 stage1 离线门禁 | **否（本窗）** · **下一 tip HOLD** | TIP-AG 后可开；本窗总控点 (2) |
| **(2) attune 预览六槽对齐** | **采纳** | 可感；AE 后硬缺口；零功率表；正交刃轨 |
| (3) 其它可感内容轴 | 次选 | 本窗已定 (2) |
| 冒险/挑战 Q lore 塞 run | **否** | lore≤3 已满；另开需挤行 DESIGN |
| Affix / 伤乘 / 抬表 / densify / sx20 / skill_mult / STAMP / L13 mods | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12616 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12617 | DESIGN / tip power-paths 进度戳：**TIP-AG CLOSED `7d61761e4`**；**TIP-AH OPEN**；旁注 **L13 stage1 HOLD 下一 tip** | 戳可见 |
| D12618 | 错位钉死：E3 `runPreview*` 仅 blade+charm vs E1 `signatures()` 含甲（RESEARCH §1） | 可核 |
| D12619 | 「对齐」钉死：预览槽 ≡ 战斗生效槽；标签=`slotName`；≤2 段；空串闸不变（DESIGN §2） | 可核 |
| D12620 | **插件：** `runPreviewLine`/`runPreviewCost` 防具侧按 charm→head→chest→legs→boots 取首个非空 `previewSlot` | 代码+单测 |
| D12621 | **插件：** `EmberSigAttunePreview` 注释槽标签扩至防具侧五名；**不**改格式前缀 / **不**新 PAPI key | 可核 |
| D12622 | **插件：** 单测最小包（DESIGN §2.4）：charm-only / head-win / prio charm>head / Q07 关空串 | PASS |
| D12623 | **物品（可选）：** `ember_p1_sig_runconfirm.yml` 头注对齐 AE；adventure/challenge 注释勘误 W1a live 面；**禁**加 lore 行、禁塞 Q 图 | lore≤3 |
| D12624 | NON 核：`git show` 无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无新签名 id | PASS |
| D12625 | HOLD：L13 stage1；刃 L11/L13 mods；冒险 lore densify；签名页六槽大改；扩 stampable | 文内硬句 |
| D12626 | 批后派单句：**余烬-插件**；物品可选注释；策划 docs-only | 派单句 |
| D12627 | 玩家口径：真槽名 + 仍最多两条 + 价/掉落/数字不变 | 可核 |
| D12628–D12631 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工 | 超额新 tip |

### 玩家口径（固定）

- 进本确认里「本局生效」会写你 **真正生效的那件防具**（可能是头盔等），不再死写护符。  
- 还是最多 **刃 + 一件防具侧**。  
- 调律价、掉落、签名数值本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-attune-preview-sixslot-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-attune-preview-sixslot-align-2026-10-12.md` |
| 预览收集 | `CoreRpg/.../EmberGrowthService.java` `runPreviewLine` / `runPreviewCost` / `previewSlot` / `sigWhy` |
| 生效核 | `EmberSignature.active` / `pickDefense` / `defenseRank` · `defenseWorn` |
| 文案 | `CoreRpg/.../encounter/EmberSigAttunePreview.java` |
| 槽名 | `EmberItemData.slotName` |
| 单测 | `CoreRpg/.../EmberSigAttunePreviewTest.java` |
| 菜单 | `plugins/TrMenu/menus/ember_p1_sig_runconfirm.yml`（主感面） |
| PAPI | `sigPapi` keys `run` / `run_cost` — **不改键名** |
| 旁轨 HOLD | TIP-AG L13 DESIGN · TIP-AD exclude L11/L13 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / glance / sx20 / 深化 densify / ForgeChoice densify  
- 开 L13 stage1 离线门禁或改蓄力逻辑（**下一 tip**）  
- 往冒险/挑战 Q 图标加 `%p1_sig_run%`（破 lore≤3）  
- 签名页大改六槽摘要；改 `attuneCheck` / ALTS 价；战斗热换调律  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 批后插件：D12620–D12622；D12624 `git show` 干净（feat `5e1b43c96` 仅 3 Java）  
- [ ] 可选物品：D12623 注释 only · lore≤3（本窗未做 · tip 可选）  
- [x] 插件 jar live `1.65.774-d12631.local`；策划 docs close 本窗  
- [x] L13 stage1 仍 HOLD 下一 tip  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AG 后 · **(2) attune 预览六槽对齐**（可施工薄 · 非 densify）  
- **批注：** AE 后预览硬编码刃+护符 vs 战斗 5 取 1；修 `runPreview*` 收集；槽标签真名；零功率表；runconfirm lore≤3 不加成行；批后派 **余烬-插件**；**L13 stage1 HOLD 下一 tip**；深化 densify / Affix / 抬表 / glance / sx20 / skill_mult / STAMP / L11·L13 mods 仍冻  

## STATUS: CLOSED live · 1.65.774-d12631.local · feat 5e1b43c96 · chore 57efb60b8 · 2026-10-12 19:50 CST
