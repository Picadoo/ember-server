# RESEARCH · 签名页防具侧生效摘要对齐 · 2026-10-12

> **状态：** OPEN · TIP-AJ · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AE live 后防具侧 5 取 1 已进战斗；TIP-AH 已对齐进本 `runPreview*`；**签名传奇页 C 格仍写「已选护符」且 `%corerpg_p1_sig_charm%` 只扫 charm** → 甲胜出时 **说谎**。AH DESIGN §2.3 / RESEARCH E9 已标「刃/护符摘要另债」。总控下一窗：**可感 UX / 变强途径（非 densify）**。  
> **上游：** TIP-AH CLOSED live `1.65.774-d12631` · TIP-AE CLOSED live `1.65.773-d12599` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL` · **L13 施工 HOLD** · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md)  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice·wear·pity·awkDelta densify · 零抬 STAMP · 零改 L11/L13 · 零扩掉落 stampable**

---

## 0. 一句话

签名页「正在用」摘要必须与战斗 / AH 预览同一套 **刃 1 + 防具侧 5 取 1**；今天 C 格死写护符 → 头盔等胜出时玩家以为没生效。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 战斗 `signatures()` = 刃 + `defenseWorn`（charm+四甲）5 取 1 | `EmberGrowthService` · `EmberSignature.active` | 真生效已含甲 |
| E2 | TIP-AH：`runPreview*` / `%p1_sig_run%` 已按同 prio 取胜出槽 | live `1.65.774-d12631` | **进本确认已诚实** |
| E3 | 签名页 C：`name: §6已选护符` · lore=`%corerpg_p1_sig_charm%` + `charm_o` | `ember_p1_sig.yml` W/C | **菜单文案仍护符** |
| E4 | `sigPapi`：`blade`/`charm` → `wornLine(d,lo,key)` **按字面槽** | Growth ~1884 | charm 键 = 只看护符件 |
| E5 | `sigToggle` 仅允许 `blade`/`charm`；甲槽 `sigOff` 已存在但无 UI 入口 | Growth ~1573–1579 · `C_SIGOFF+slot` | 甲生效时点 C **关不到** 胜出槽 |
| E6 | `/corerpg p1 sig` 状态只 `wornLine(blade)` + `wornLine(charm)` | Growth ~1378–1379 | 聊天摘要同样漏甲 |
| E7 | 图鉴 action `a_`：`烙印到自己的` + `slotName(sd.slot)`；charm-def → **护符** | Growth ~1920 一带 | 可烙甲却写死护符 → **半假**（click 确认串已写「护符或防具」） |
| E8 | `imprint_hope` 追烙花费：非刃时取 `lo.charm` 算阶，**不**走 `imprintWornTarget` | Growth ~1848–1854 | 甲上追烙时币阶可能错显 |
| E9 | AH RESEARCH E9 / DESIGN §2.3：`ember_p1_sig.yml` **本窗不改（另债）** | AH docs | **本 TIP 即该债** |
| E10 | `sigFeelLine` / hub glance 已用 `signatures()` | Growth · TIP-G | feel 一眼已对；**页内摘要仍假** |

---

## 2. 错位场景（玩家面）

| 场景 | 战斗 / run 预览 | 今日签名页 C | 体感 |
|------|-----------------|--------------|------|
| 仅护符有签且胜出 | 刃 + 护符 | 护符·生效 | OK（旧路径） |
| 头盔有签、护符无签 | 刃 + **头盔** | **护符：没有 / 无签名** | **假** |
| 护符与胸甲皆有签（prio 护符） | 刃 + 护符 | 护符·生效（甲不显） | OK（prio）；可选半行「甲待命」**本窗不做** |
| 点 C 想关甲上生效签 | — | 只 toggle charm | **关不到** |
| 图鉴点防具侧签 | 可烙甲 | 「烙到护符上」 | **半假** |

---

## 3. 「对齐」定义（钉死）

1. C 格展示的防具侧摘要 = **当前防具侧胜出槽** 的 `wornLine`（prio 同 `pickDefense`：charm→head→chest→legs→boots）；无胜出 → 诚实「没有 / 无签名」。  
2. 槽标签用 `EmberItemData.slotName`（护符/头盔/…），**禁止**永远写「已选护符」。  
3. `charm_o` / toggle 提示作用于 **同一胜出槽**（点 C = 开关该槽 `sigOff`）。  
4. **不新开**头/胸/腿/靴四格菜单（禁六槽大改 densify）；仍 **两格摘要**（刃 + 防具侧）。  
5. 图鉴 `a_` 防具侧文案 =「护符或防具」（与 confirm 串一致）；`imprint_hope` 阶用 `imprintWornTarget`。  
6. 零新 PAPI 丛林：优先 **扩语义** 现有 `charm` / `charm_o` / `charm_t` 键；菜单名可静态改「防具侧生效」或挂一短 PAPI 名（若需动态槽名，允许 **一个** 可选 `charm_n` ——默认用静态名 + lore 内已含槽名即可）。  
7. lore≤3 保持；不扩 stampable；不改功率表。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**；TIP-AI FAIL 预期）  
- 扩甲 stampable / 抬 STAMP / C12 / Affix / 伤乘 / skill_mult / glance twin / 深化 densify / sx20 / ForgeChoice densify  
- 签名页加四甲独立格；冒险 Q lore 塞摘要  
- 重做追烙 / 调律价；改 ALTS

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **签名页防具侧生效摘要对齐（可施工薄）** | **采纳** — AH 显式另债；玩家可感；零功率表；正交 L13 HOLD |
| 再开观察窗 / glance densify | **否** — TIP-J/T 已停；本窗要可施工诚实 |
| ForgeHeldNext / Hope 再 twin | **否** — 已 SHIP；禁 densify |
| 刃 L13 施工 tip | **否** — PASS≠开工；显式开工前不开 |
