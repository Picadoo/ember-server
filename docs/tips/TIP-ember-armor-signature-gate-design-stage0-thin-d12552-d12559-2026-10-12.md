# tip · TIP-AB 防具签名 + 叠合门禁 DESIGN stage0（docs-only · HOLD 施工）· D12552–D12559

> **状态：** CLOSED · 批 **A · 方案 M** · docs-only · HOLD 施工（总控 2026-10-12 17:41 Asia/Shanghai）  
> **上游：** [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](../design/DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md) · TIP-AA CLOSED docs-only `79247dab2` · RESEARCH [`RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md`](../design/RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md) · D174 2c 勘误  
> **前置：** TIP-AA 真债钉死=防具签名+叠合；甲槽另轨 live；深化 densify **仍冻**；烬斩门闩 TIP-Y **仍有效**  
> **施工岗：** **无码**（策划 DESIGN + tip；**不**派插件；勿借机改 Signature / Loadout / loot / STAMP / TrMenu / Economy）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零 sx20 / 粒子 densify** · **零 skill_mult** · **零 jar 直至门禁过线 + 总控显式开工** · 不改 Economy/pom 脏树

---

## 0. 一句话

TIP-AA 后开 **独立 DESIGN stage0**：钉防具签名规格 + 叠合门禁 S1–S4；文首勘误 D174「2c」；**HOLD 施工**，本窗 **docs-only**。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 当场开防具签名 Java 施工 | **否** | 须门禁 S1–S4 + 总控显式开工；禁薄 densify |
| 六槽 honesty / 菜单 densify | **否** | 非真债 |
| 抬 STAMP / 甲掉签名 | **否** | 抬表；默认「可烙不可掉」 |
| 烬斩再开码 | **否** | TIP-Y 门闩未满足 |
| sx20 / 深化 densify | **否** | 硬禁 |
| **独立 DESIGN stage0（防具签名+门禁）** | **采纳** | 总控点名；可批不可施工 |

---

## 2. 方案 M（定稿荐 · 无码）

| ID | 项 | 验收 |
|----|-----|------|
| D12552 | 本 tip + DESIGN `DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md` | 路径可点 |
| D12553 | DESIGN power-paths 进度戳：**TIP-AA CLOSED `79247dab2`**；**TIP-AB OPEN**（防具签名门禁 DESIGN stage0 · 无码）；深化 densify 仍冻 | 戳可见 |
| D12554 | 文首勘误核：D174「2c」= 甲槽另轨 live；本 DESIGN 管防具签名 + 叠合门禁，**不是**再开甲槽 | 可核 |
| D12555 | 防具签名规格可批：槽族、5 防具取 1、tag/excl、烙印扩甲/掉落不扩、§2.6 优先级 | 可核 |
| D12556 | 叠合门禁表 S1–S4 过线条件 + HOLD 门槛 +「非开工令」分离 | 可核 |
| D12557 | legendary 预算关系 + NON-goals / 硬禁入 DESIGN | 可核 |
| D12558 | HOLD 施工：本 tip **无码**；**不**派插件；零 jar 直至门禁过线 + 总控显式开工 | 无 jar |
| D12559 | 本 commit docs-only；密码检查 0；缓冲仅本文/DESIGN 热修（错链/错戳） | PASS |

### 玩家口径（固定）

- 六槽甲已能穿；**签名暂时仍主要在刃 / 护符**——「甲也能烙招牌效果」等本设计批过、模拟过线、总控说开工再做。  
- 本窗**不加新签名、不改掉落、不改徽记价**。  
- 变强仍走装备 / 锻造 / 宝石 / 已有签名；本窗不给新乘区。

---

## 3. 明确不做

- 任何 CoreRpg / TrMenu / p1map / ember-v1-runs 改码  
- 防具签名 Java、`mods()` 扩甲、烙印目标扩甲、stampable 扩甲（施工属过门禁后另 tip）  
- 抬 STAMP_RATE / 徽记周池 / echo cap / Affix / 伤乘 / skill_mult  
- 关或拧六槽三开关；开 K3 / 档 S / 白板补建模  
- glance twin、sx20、ForgeChoice·wear·pity·awkDelta densify、深化 densify  
- 把本 DESIGN 当成开工令或数值终表

---

## 4. 门禁 S1–S4（摘要 · 详 DESIGN §3）

| ID | 一句话 |
|----|--------|
| S1 | 规则规格冻结可核（5 取 1、刃不变、优先级） |
| S2 | 烙印扩甲 · 掉落不扩甲；不抬 STAMP/价 |
| S3 | p1sim 单条/组合/excl 过线；不与 ×0.97 叠新乘区 |
| S4 | W30 vs 现行六槽+签名基线 ±0.5 周（或总控重钉） |

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] DESIGN + tip 路径可点；勘误 / 规格 / S1–S4 可核  
- [x] DESIGN power-paths：TIP-AA CLOSED `79247dab2` + TIP-AB CLOSED  
- [x] **无**插件施工派单；**无** jar  
- [x] `git show` docs-only；密码检查 0  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AA 后 · **防具签名 + 叠合门禁 DESIGN stage0**（docs-only · HOLD 施工）  
- **批注：** 勘误 2c 已收；S1–S4 为施工硬门槛；零 jar 直至过线 + 显式开工；深化 densify / Affix / 抬表 / glance / sx20 / skill_mult 仍冻；无派插件  

## STATUS: OPEN · 待批 A·M · 2026-10-12 17:40 CST · 无码
