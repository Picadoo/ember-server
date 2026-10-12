# tip · TIP-AE 防具签名最小切片施工（烙印扩甲 / defense-side fits）· D12584–D12599

> **状态：** OPEN · 待批 **A · 方案 M** · 施工 tip（总控显式开工令已发）· 2026-10-12 19:23 Asia/Shanghai  
> **上游：** stage0 [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](../design/DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md) · TIP-AD CLOSED `abc3875d0` / RESULT head `b4c43551e` · STAGE1_OFFLINE=**PASS（patched · excl L11/L13）** · 补丁 [`DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md`](../design/DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md) · 本窗 build DESIGN [`DESIGN-ember-armor-signature-min-slice-build-2026-10-12.md`](../design/DESIGN-ember-armor-signature-min-slice-build-2026-10-12.md)  
> **开工令引用：** STATUS-AD「**开工 · 防具签名最小切片**（烙印扩甲 / defense-side fits · 不扩掉落 · 不改 L11/L13 · 不抬 STAMP）」  
> **前置：** S1–S4 门禁过线（patched）；深化 densify **仍冻**；烬斩门闩 TIP-Y **仍有效**；刃 L11/L13 **HOLD 另轨**  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberSignature` / Growth / ImprintBreak 主责）；**余烬-物品** 仅当菜单/图鉴白话 YAML 需跟「护符→防具侧」文案（**无**新掉落表 / **无**数值 YAML）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零 sx20 / 粒子 densify** · **零 skill_mult** · **零扩掉落 stampable 甲** · **零改 L11/L13 mods** · **零抬 STAMP/C12**

---

## 0. 一句话

总控显式开工后：把原护符签名升格为 **防具侧**——**可烙**到 charm+四甲，生效 **5 取 1**；**掉落仍不打甲签**；不抬表、不动刃 L11/L13。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 扩掉落甲可 stamp | **否** | S2 / 开工令硬禁；须另开+重跑 S4 |
| 改 L11/L13 mods 凑绿 | **否** | HOLD 另轨；禁抬表 |
| 抬 STAMP / imprint 价 | **否** | 硬禁 |
| Affix / 伤乘 / skill_mult / sx20 | **否** | 硬禁 |
| 观察窗 / 再 docs densify | **否** | 开工令已点最小切片 |
| **烙印扩甲 + defense-side fits（最小切片）** | **采纳** | 总控显式开工；对接 stage0+TIP-AD |

---

## 2. 方案 M（定稿荐 · 施工）

| ID | 项 | 验收 |
|----|-----|------|
| D12584 | 本 tip + build DESIGN `DESIGN-ember-armor-signature-min-slice-build-2026-10-12.md` | 路径可点 |
| D12585 | DESIGN power-paths 进度戳：**TIP-AD CLOSED PASS patched** `abc3875d0`/`b4c43551e`；**TIP-AE OPEN**（防具签名最小切片 · 开工） | 戳可见 |
| D12586 | 开工令写入 tip 文首；权威规格=stage0 §2 + TIP-AD exclude；本窗不重开门禁辩论 | 可核 |
| D12587 | **fits 分轨**：imprint/生效 = defense-side（charm-def → charm\|head\|chest\|legs\|boots）；**stampable 保持严格槽相等**（甲仍空） | 单测+代码注释 |
| D12588 | `imprintCheck` / `kindText` / 目标缺失文案：防具侧可核；**C12 / imprintMarks / imprintCoin 零抬** | git show 无抬 |
| D12589 | `active` + `offReason`：刃 1 + 防具侧 5 取 1；prio **charm > head > chest > legs > boots**；dual/excl/同 tag 按 stage0 §2.6 | 单测 |
| D12590 | `EmberGrowthService.signatures` / `wornInactive` / `sigFeelLine`：六槽纳入 `lo.armor(0..3)` | 可核 |
| D12591 | `EmberImprintBreak.findEmptyTarget`（及 imprint 命令目标挑选）：防具侧可落甲；禁写死仅 charm | 可核 |
| D12592 | 单测最小包（build DESIGN §3）：defense fits · stampable 甲空 · prio · dual · 同 tag · imprintCheck | PASS |
| D12593 | **NON 核：** `git show` 无 loot/STAMP_RATE/S08/C12 抬；无 L11/L13 `DEFS` mods 改；无新签名 id | PASS |
| D12594 | 防具侧 Def 清单钉死（不改数）：`L02 L03 L06 L08 L10 L12 L14 L15`；刃 L11/L13 HOLD | 文内硬句 |
| D12595 | 批后派单：**余烬-插件**；物品仅可选文案跟进；策划本窗 **docs-only**（零业务 jar） | 派单句 |
| D12596 | HOLD：掉落扩甲；刃 L11/L13 调律；Affix/伤乘/深化 densify/sx20/skill_mult | 文内硬句 |
| D12597–D12599 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或扩 stampable | 超额新 tip |

### 玩家口径（固定）

- 图鉴里原来的「护符招牌」现在也能烙到**正在穿的头盔/胸甲/护腿/靴子**（以及护符）上。  
- **同时生效仍然最多两条**：一把刃的 + 一件防具侧的；多件甲都烙了也只生效一件（护符优先，其次头→胸→腿→靴）。  
- **打本掉落不会突然在甲上出签名**；徽记价和烙印花费不变。  
- 两把有问题的刃签（L11/L13）本窗不修。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| 规格 | `docs/design/DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md` §2 |
| 评测补丁 | `docs/design/DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md` |
| 本窗 build | `docs/design/DESIGN-ember-armor-signature-min-slice-build-2026-10-12.md` |
| 门禁结果 | `docs/design/RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md` |
| 签名 SoT | `CoreRpg/src/main/java/town/sunshine/corerpg/p1/EmberSignature.java`（`DEFS` · `fits` · `stampable` · `active` · `imprintCheck`） |
| 单测 | `CoreRpg/src/test/java/town/sunshine/corerpg/p1/EmberSignatureTest.java` |
| 生效收集 | `EmberGrowthService.signatures` / `wornInactive` |
| 追烙自动 | `EmberImprintBreak.findEmptyTarget` |
| 六槽 | `EmberItemData.ARMOR_SLOTS` · `EmberLoadout.armor(i)` |
| 掉落 stamp 调用 | `EmberRunRules` → `rollStamp`/`pickStamp`（**不得**因 fits 扩而让甲进表） |
| 价 | `EmberEconomy` sink **C12** · `imprintMarks()` / `stampRate()` → **只读不改** |

---

## 4. 明确不做

- 扩 `stampable` 到 head/chest/legs/boots；抬 `STAMP_RATE` / pity / 徽记周池  
- 改 `DEFS` 里 L11/L13（或任一签名）mods；新加 L16+  
- Affix densify / 伤乘 / skill_mult / glance twin / sx20 / 粒子 densify / ForgeChoice·wear·pity·awkDelta densify / 深化 densify  
- 拧六槽三开关、×0.97、共鸣 F；开 K3 / 档 S  
- 把本 tip 当成「可顺手修刃债」或「可顺手扩掉落」

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + build DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12587–D12592 落地；D12593 `git show` 无抬无改 L11/L13  
- [ ] 策划本窗 commit **docs-only**；**无**业务 jar  
- [ ] 刃 L11/L13 仍 HOLD 另轨  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AD PASS(patched) 后 · **防具签名最小切片施工**（烙印扩甲 / defense-side fits）  
- **批注：** 对接 stage0+TIP-AD；fits 分轨保掉落不扩；5 取 1；零抬 STAMP/C12；零动 L11/L13；批后派 **余烬-插件**；深化 densify / Affix / glance / sx20 / skill_mult 仍冻  

## STATUS: OPEN · 待批 A·M · 2026-10-12 19:23 CST · docs-only（施工归插件）
