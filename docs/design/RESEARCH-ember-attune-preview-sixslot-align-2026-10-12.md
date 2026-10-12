# RESEARCH · attune 预览 × 六槽 / 防具签名对齐 · 2026-10-12

> **状态：** OPEN · TIP-AH · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AE live 后 defense-side（charm+四甲 · 5 取 1）已进战斗；D297 进本前「本局生效」预览仍硬编码刃+护符。总控选题 **(2) attune 预览六槽对齐**（可感 UX · 正交刃轨）。  
> **上游：** TIP-AE CLOSED live `1.65.773-d12599` · TIP-AG CLOSED DESIGN stage0 `7d61761e4`（L13 HOLD）· D297 [`DESIGN-ember-sig-attune-decision-2026-10-07.md`](DESIGN-ember-sig-attune-decision-2026-10-07.md) · stage0 [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md)  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零改 L11/L13 · 零扩掉落**

---

## 0. 一句话

玩家进本前看到的「本局生效」必须与 **EmberSignature 现网生效规则**（刃 1 + 防具侧 charm|头|胸|腿|靴 **5 取 1**）同槽同件；今天预览仍只扫刃+护符 → 甲上生效时 **说谎或漏显**。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 战斗收集 `signatures()` = `active(blade, defenseWorn)`；`defenseWorn` = charm + `armor(0..3)` | `EmberGrowthService` ~1019–1055 | **真生效**已含六槽防具侧 |
| E2 | `pickDefense` 优先级 charm > head > chest > legs > boots；同 dual/excl/tag 规则 | `EmberSignature.pickDefense` / `defenseRank` | 5 取 1 已钉 |
| E3 | `runPreviewLine` / `runPreviewCost` **写死** `previewSlot(…,"blade")` + `previewSlot(…,"charm")` | `EmberGrowthService` ~1136–1143 | **预览未跟 AE** |
| E4 | `previewSlot` 本身经 `sigWhy`→`offReason` 可正确判甲槽是否生效；只是调用方不扫甲 | 同文件 ~1122–1132 · ~1216–1225 | 修复点 = 收集列表，非重写生效核 |
| E5 | `anyUnlockedAttuneWorn` **已**扫六槽 `blade,charm,head,chest,legs,boots` | ~1147–1156 | 周确认弹窗可因甲签触发，但文案仍可能只写刃/护符 → 体验割裂 |
| E6 | 活菜单消费点：`ember_p1_sig_runconfirm.yml` H/G lore = `%corerpg_p1_sig_run%` + `run_cost` + 1 静态行（**已 =3**） | TrMenu | 对齐 **不得**再加 lore 行 |
| E7 | `ember_p1_adventure.yml` / `ember_p1_challenge.yml` 仅 **注释**提 D297 W1a；Q 图 lore 已满 3 行（状态/掉落/进本），**无** live `p1_sig_run` | TrMenu | 本窗 **不**挤进冒险/挑战图 lore（lore≤3）；主感面 = runconfirm + PAPI |
| E8 | `EmberSigAttunePreview.Slot.slotLabel` 注释写「刃 / 护符」；`EmberItemData.slotName` 已有头盔/胸甲/护腿/靴子 | Preview · ItemData | 标签 SoT 已够；改调用即可 |
| E9 | 签名页摘要仍 `p1_sig_blade` / `p1_sig_charm` 两格；无独立甲摘要格 | `ember_p1_sig.yml` | 本窗可选跟进：charm 行在甲胜出时是否改称「防具」——**默认不做**，以免扩菜单；预览对齐优先 |

---

## 2. 错位场景（玩家面）

| 场景 | 战斗 | 今日预览 | 体感 |
|------|------|----------|------|
| 仅刃+护符有签且护符胜出 | 刃 + 护符 | 刃·… / 护符·… | OK（旧路径） |
| 头盔烙了防具侧签，护符无签或被 prio 挤掉 | 刃 + **头盔** | 刃·… / **空或错误护符** | **假** |
| 多甲有签，胸甲胜出 | 刃 + 胸甲 | 仍只看护符 | **假** |
| 周确认因甲上已解锁调律而弹出 | 弹窗开 | lore 不写甲槽 | **半假** |

---

## 3. 「对齐」定义（钉死）

1. `%corerpg_p1_sig_run%` / `run_cost` 列出的槽位集合 = 本局 `signatures()` 对应的佩戴槽（最多 **2**：刃 + 防具侧胜出件）。  
2. 防具侧标签用 **真实槽名**（护符/头盔/胸甲/护腿/靴子），不是永远写「护符」。  
3. 原版/调律 + 代价串与该 **胜出件** 一致（复用 `badOf` / 现 `previewSlot`）。  
4. 空串规则不变：无 Q07 调律闸 / 无生效签 → 空（菜单无行感）。  
5. TrMenu：**不**新增 lore 行；runconfirm 保持 ≤3；冒险/挑战图 lore **本窗不改**。

---

## 4. 非目标

- 抬 STAMP / C12 / Affix / 伤乘 / skill_mult / glance / 深化 densify / sx20 / ForgeChoice densify  
- 改 L11/L13 mods；开 L13 stage1 施工  
- 扩甲 stampable / 掉落  
- 重做签名页六格摘要 UI；挤爆冒险 lore≤3  
- 改调律解锁价 `ALT_MARKS` / ALTS 表

---

## 5. 荐下一窗

| 候选 | 裁决 |
|------|------|
| **本窗 TIP-AH：预览收集对齐（可施工薄）** | **采纳** — 缺口硬、零功率表、正交刃轨 |
| L13 stage1 离线门禁 | **HOLD 下一 tip**（TIP-AG 后；本窗不抢） |
| 冒险图 lore 再塞 run 行 | **否** — lore≤3；另开需挤行 DESIGN |

