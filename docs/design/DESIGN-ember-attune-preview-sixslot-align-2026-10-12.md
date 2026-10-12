# DESIGN · attune 预览六槽对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AH 批 A·M · 已派 **余烬-插件** · 2026-10-12 19:44 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-attune-preview-sixslot-align-2026-10-12.md`](RESEARCH-ember-attune-preview-sixslot-align-2026-10-12.md)  
> **对接 live：** TIP-AE defense-side fits / 5 取 1 · D297 `EmberSigAttunePreview` / `%corerpg_p1_sig_run%`  
> **HOLD 旁注：** L13 stage1 **下一 tip**；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零 STAMP 抬 · 零扩掉落 · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance/深化 densify/sx20/ForgeChoice densify

---

## 0. 一句话

让进本前「本局生效」PAPI 与 `EmberSignature.active`（刃 1 + 防具侧 5 取 1）**同槽同件**；不改数值、不扩菜单行。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 战斗 | `signatures()` 扫 charm+四甲，`pickDefense` 5 取 1 | 保持 |
| 预览 | `runPreviewLine/Cost` 只 `blade`+`charm` | 防具侧按 **同 prio** 取胜出槽 |
| 文案 | 槽标签常暗示「护符」 | `EmberItemData.slotName`（头盔…） |
| 菜单 | runconfirm lore=3 已满；冒险/挑战无 live run 行 | **只修 PAPI**；不加成行；不挤 Q 图 |

---

## 2. 方案 M（定稿荐）

### 2.1 核心行为

`runPreviewLine` / `runPreviewCost`（`EmberGrowthService`）：

1. 闸：`ALT_UNLOCK`（Q07 调律）未开 → `""`（不变）。  
2. `bladeSlot = previewSlot(d, lo, "blade")`（不变；`sigWhy` 刃支路）。  
3. `defSlot = null`；按序扫描 `"charm","head","chest","legs","boots"`：  
   - 调用现有 `previewSlot(d, lo, slot)`；  
   - **第一个非 null** 即为防具侧胜出（因 `sigWhy`→`offReason`→`pickDefense` 已含 prio/dual/excl/tag）。  
4. `EmberSigAttunePreview.runLine/costLine(listOf(bladeSlot, defSlot))`。  
5. **禁止**再发明第三段；**禁止**绕过 `sigWhy` 手写「有签就显示」。

### 2.2 Preview 类

- `EmberSigAttunePreview`：注释改为「刃 / 防具侧（护符|头盔|胸甲|护腿|靴子）」；`listOf` 保持双槽即可。  
- **不**改格式串前缀（仍 `§7本局生效：` / `§8代价：`）。

### 2.3 TrMenu / 物品

| 文件 | 动作 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_sig_runconfirm.yml` | **可选**头注：对齐 TIP-AE 五防具取 1；**禁止**加 lore 行（H/G 已 3 行） |
| `ember_p1_adventure.yml` / `ember_p1_challenge.yml` | **可选**注释勘误：「W1a live 面 = runconfirm + PAPI；Q 图 lore≤3 不塞 run」；**禁止**往 Q 图标塞 `%p1_sig_run%` |
| `ember_p1_sig.yml` | **本窗不改**（刃/护符摘要另债） |
| `ember_p1_sig_attune.yml` | **本窗不改**（调律页 6 ALTS 格与预览正交） |

### 2.4 单测（最小包）

| 用例 | 期望 |
|------|------|
| 仅 blade+charm 生效 | run 含 `刃·` 与 `护符·`；与改前同构 |
| charm 无签、head 有签且胜出 | run 含 `头盔·`；**不含** `护符·` |
| charm 与 head 皆有签 | 仅 `护符·`（prio）；无头盔 |
| 双甲签、无护符 | 仅高 prio 甲槽标签一条 |
| Q07 未开 | 两键皆 `""` |
| 纯文案：`EmberSigAttunePreview` list 含「胸甲·调律」 | runLine 含该串 |

实现位置建议：`EmberSigAttunePreviewTest`（纯串）+ Growth 级若已有 loadout fixture 则加 1～2；**禁**借机改 `DEFS` mods。

---

## 3. 验收

- [ ] 预览槽集合 ≡ 战斗 `signatures()` 对应佩戴槽（刃 + ≤1 防具侧）  
- [ ] 甲胜出时标签为头盔/胸甲/护腿/靴子之一，非误标护符  
- [ ] runconfirm 仍 lore≤3；无新 PAPI key（沿用 `run` / `run_cost`）  
- [ ] `git show` 无 STAMP/C12/loot/L11/L13/Affix/skill_mult 改动  
- [ ] 单测最小包 PASS  

---

## 4. 明确不做

- 抬表 / 扩 stampable / 改 ALTS 价 / 开 L13 stage1  
- 签名页六槽摘要大改；冒险/挑战 Q lore 塞 run  
- 战斗中热换调律；改 `attuneCheck` 闸门  

---

## 5. 施工指针（真路径）

| 岗 | 路径 |
|----|------|
| 插件主责 | `CoreRpg/.../EmberGrowthService.java` `runPreviewLine` / `runPreviewCost`（~1136–1143） |
| 复用 | `previewSlot` · `sigWhy` · `defenseWorn` · `EmberSignature.pickDefense` |
| 文案核 | `CoreRpg/.../encounter/EmberSigAttunePreview.java` |
| 标签 | `EmberItemData.slotName` |
| 单测 | `EmberSigAttunePreviewTest.java`（+ Growth 若有） |
| 菜单（可选注释） | `plugins/TrMenu/menus/ember_p1_sig_runconfirm.yml` 等 |
| PAPI 键 | `sigPapi`：`run` / `run_cost`（~1908–1909）— **不改键名** |

---

## 6. 玩家口径（固定）

- 进本前那两行「本局生效 / 代价」现在会写出 **真正生效的那件防具**（可能是头盔等），不再假装一定是护符。  
- 同时最多还是 **两条**：一把刃 + 一件防具侧。  
- 徽记价、掉落、签名数字都不变。
