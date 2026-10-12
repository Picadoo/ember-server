# DESIGN · 防具签名最小切片施工（stage2 · 烙印扩甲 / defense-side fits）· 2026-10-12

> **状态：** OPEN · TIP-AE 待批 A·M · 总控显式开工令已发 · 2026-10-12 19:23 Asia/Shanghai  
> **对接 tip：** [`TIP-ember-armor-signature-min-slice-build-thin-d12584-d12599-2026-10-12.md`](../tips/TIP-ember-armor-signature-min-slice-build-thin-d12584-d12599-2026-10-12.md)  
> **权威规格：** stage0 [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md) §2 · TIP-AD 评测补丁 [`DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md) · RESULT STAGE1_OFFLINE=PASS(patched)  
> **上游开工令：** TIP-AD CLOSED `abc3875d0` / RESULT head `b4c43551e` · STATUS-AD 显式「开工 · 防具签名最小切片」  
> **硬禁：** 零扩掉落 · 零改 L11/L13 mods · 零抬 STAMP / imprint 价 · 零 Affix densify · 零伤乘 · 零 skill_mult · 零 glance twin · 零深化 densify · 零 sx20 · 零 ForgeChoice·wear·pity·awkDelta densify · 刃 L11/L13 **HOLD 另轨**

---

## 0. 一句话

在 **不扩掉落、不抬表、不改刃 L11/L13** 前提下，把 stage0 已批的 **防具侧签名** 落到现网：原 `slot=charm` 的 Def **可烙**到 charm / head / chest / legs / boots；生效仍 **刃 1 + 防具侧 5 取 1**。

---

## 1. 真债钉死（现网只读）

| 现网 | 路径 / 行为 | 本切片要改 |
|------|-------------|------------|
| `EmberSignature.DEFS` | L02/L03/L06/L08/L10/L12/L14/L15 的 `slot` 字段仍为 `"charm"`；L01/L04/L05/L07/L09/L11/L13 为 `"blade"` | **不**改 Def 行 id / mods / map / tag / excl；**不**改 L11/L13 |
| `fits(Def, slot, family)` | `d.slot.equals(slot)` 严格相等 | **烙印 / 生效** 路径：charm-def 接受五防具槽；**掉落 stampable 不得跟扩** |
| `stampable` → `rollStamp` / `pickStamp` | 经 `fits`；甲槽现为空 | **保持甲槽空**（S2）；实现上 stamp 路径继续 **严格槽相等** |
| `active(Worn blade, Worn charm)` | 仅刃 + 护符 | 扩成刃 + **防具侧候选 5 取 1**（§2.6 优先级） |
| `EmberGrowthService.signatures` | 只 `lo.blade` / `lo.charm` | 六槽时纳入 `lo.armor(0..3)` |
| `EmberImprintBreak.findEmptyTarget` | worn 仅 `blade` / `charm` | 防具侧可落甲；优先佩戴空签件再袋内 |
| `imprintCheck` 文案 | 「没有正在用的刃/护符」 | 防具侧目标可核；价仍 `C12` / `imprintMarks()` **不变** |

**防具侧 Def（可烙可生效 · 本窗不改数值）：** `L02 L03 L06 L08 L10 L12 L14 L15`  
**刃 HOLD 另轨（本窗禁动 mods）：** `L11`（guard）· `L13`（shape）

---

## 2. 实现口径（插件可施工）

### 2.1 fits 分轨（关键 · 防掉落跟扩）

| API | 口径 | 调用方 |
|-----|------|--------|
| **stamp / 掉落** | 保持 **严格** `d.slot.equals(slot)`（甲槽 stampable 仍空） | `stampable` / `rollStamp` / `pickStamp` / `EmberRunRules` 结算 stamp |
| **imprint / 佩戴生效** | **defense-side fits**：若 `d.slot` 为 `blade` → 仅 blade；若为 `charm`（防具侧升格）→ 接受 `charm` **或** `head`/`chest`/`legs`/`boots`；族规则不变（`any` 或同 family） | `imprintCheck` · `EmberImprintBreak` · `offReason`/`active` · 图鉴烙印目标校验 |

推荐形状（示意 · 非强制命名）：`fitsStamp` = 旧严格；`fitsDefense` / 扩后的 `fits` 仅给烙印与生效；**禁止**让 `stampable` 改调 defense fits。

### 2.2 5 取 1 生效（stage0 §2.6）

1. 双签未解锁 → 防具侧整侧不生效（刃优先精神不变）。  
2. 天赋 `excl` 剔除。  
3. 与刃同 tag → 防具侧剔除。  
4. 剩余 ≥2：固定优先级 **charm > head > chest > legs > boots**。  
5. UI：未生效悬停「防具侧已有生效签名」/「与刃同 tag」等（可复用 / 扩 `offReason`）。

`EmberGrowthService.signatures`：收集 `charm` + 四甲上带签且 defense-fits 的 `Worn`，交给扩后的 `active`；`wornInactive` / `sigFeelLine` 同步扫甲。

### 2.3 烙印目标

- 图鉴 / `/corerpg p1 sig imprint <id>`：防具侧 Def 可选 **正在用的** 同族合法五槽之一（优先：已选目标槽 → 佩戴空签 → 既有确认流）。  
- `EmberImprintBreak.findEmptyTarget`：勿再写死 `lo.charm`；按 defense fits 扫 charm+armor（及袋内）。  
- **费用：** `imprintMarks()` / `imprintCoin(tier)` / Economy `C12` **零抬**。

### 2.4 明确不动

- `STAMP_RATE` / `stampRate()` / S08 · 徽记周池 · pity 期望  
- `DEFS` 修饰键数值 · 新签名 id · 删/改 L11·L13  
- 六槽三开关 / ×0.97 / 共鸣 F · Affix · skill_mult · 伤乘  
- TrMenu/NeigeItems **新掉落表**（本窗无物品 YAML 必改；若菜单仅引用「护符」文案，插件/物品可各改一行白话，非数值）

---

## 3. 单测最小包（插件验收）

| 用例 | 期望 |
|------|------|
| charm-def `fitsDefense` on head/chest/legs/boots + 同族 | true |
| charm-def `stampable(map, "head", …)` | **empty** |
| blade-def fits 甲槽 | false |
| 多甲带签：仅 charm 生效（prio） | active 防具侧 = charm 那条 |
| 仅 head+boots 带签：生效 head | prio |
| 与刃同 tag | 防具侧 off |
| dual 未解锁 | 防具侧 off |
| imprintCheck 目标 = 佩戴胸甲 + L02 | null（价够时） |
| `git show`：无 `STAMP_RATE`/S08/C12 抬；无 L11/L13 mods 改 | PASS |

---

## 4. 成功标准（本切片）

- [ ] 烙印可将 L02 等烙到甲；掉落甲仍无签名 stamp  
- [ ] 生效 ≤2 = 刃 1 + 防具侧 1（5 取 1）  
- [ ] 零抬表 / 零扩掉落 / 零动 L11/L13 mods  
- [ ] 单测最小包 PASS；live 后 RESULT/STATUS 另记  

---

## 5. 参考

- stage0 §2.2–§2.6 / §3 S2  
- TIP-AD / RESULT stage1 offline PASS(patched)  
- `CoreRpg/.../EmberSignature.java` · `EmberSignatureTest.java` · `EmberGrowthService.signatures` · `EmberImprintBreak` · `EmberItemData.ARMOR_SLOTS` · `EmberLoadout.armor(i)`
