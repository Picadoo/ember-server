# tip · TIP-AD 防具签名门禁 S3 评测集补丁（砍刃 L11/L13 · docs-only）· D12576–D12583

> **状态：** CLOSED · 批 **A · 方案 M** · docs-only · judge-s3 PASS · HOLD 施工至显式开工（总控 2026-10-12 19:22 Asia/Shanghai）  
> **上游：** TIP-AC CLOSED FAIL `469cc1a46` · RESULT [`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](../design/RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md) · 补丁 DESIGN [`DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md`](../design/DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md) · stage0 / stage1 DESIGN  
> **前置：** S1/S2/S3-R/S3-E/S3-X/S4 已 PASS；S3-T/P FAIL 仅刃 L11/L13；深化 densify **仍冻**；烬斩门闩 TIP-Y **仍有效**  
> **施工岗：** **无码业务**（策划 docs + harness 评测成员注释）；**零业务 jar**；**不**派插件；**不**改 SIGS 数值  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零 sx20 / 粒子 densify** · **零 skill_mult** · **无施工 tip** · PASS≠开工

---

## 0. 一句话

TIP-AC 后开 **stage0 评测集补丁**：S3-T/P 防具门禁判定 **排除刃 L11/L13**（防具侧 alone 已净）；刃债 HOLD 另轨；不抬表；过线仍 ≠ 开工。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| (1) stage0 补丁：S3 砍 L11/L13 后重判 S3-T/P | **采纳** | 防具侧 alone 无超线；刃债污染透镜；解绑防具路径且禁抬表 |
| (2) 刃签 L11/L13 另轨研究先修 | **HOLD 后继** | 真债另 tip；不阻塞本口径 |
| 当场开防具签名施工 / 抬表凑线 | **否** | PASS≠开工；抬表硬禁 |
| Affix / 伤乘 / sx20 / skill_mult / 深化 densify | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐 · docs-only）

| ID | 项 | 验收 |
|----|-----|------|
| D12576 | 本 tip + 补丁 DESIGN `DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md` | 路径可点 |
| D12577 | DESIGN power-paths 进度戳：**TIP-AC CLOSED FAIL `469cc1a46`**；**TIP-AD OPEN**（S3 评测集补丁 · docs-only） | 戳可见 |
| D12578 | 勘误钉死：S3-T/P 防具门禁集 **exclude 刃 L11/L13**；防具 alone + 合法无 L11/L13 对 | 可核 |
| D12579 | stage0 overlay §3 S3 行加注指向本补丁；stage1 §3 命令/过线口径对齐 | 可核 |
| D12580 | harness：`ARMOR_GATE_S3_EXCLUDE` + `cmds`/`checklist`/`judge-s3`（仅评测成员；无平衡常数） | 零 jar |
| D12581 | HOLD：刃 L11/L13 **另轨研究 tip**；本窗不修刃 mods、不抬表 | 文内硬句 |
| D12582 | 批后：重判/重跑 S3-T/P（S3-R/E/X/S4 已 PASS 可继承）；FAIL→停；PASS≠开工 | 文内硬句 |
| D12583 | 本 commit 仅 docs + harness 评测成员；密码检查 0；缓冲错链/错戳热修 | PASS |

### 玩家口径（固定）

- 六槽甲已能穿；签名暂时仍主要在刃/护符。  
- 本窗**只改「测什么算过门禁」**：不把两把有问题的刃签算进「甲签名能不能上」的考试里。  
- **不加新签名、不改掉落、不改徽记价、不改战斗数值表**。  
- 考试过了仍要总控说「开工」才会动游戏里的烙印/甲签名。

---

## 3. 明确不做

- 任何 CoreRpg / TrMenu / p1map / Economy 业务改码或 YAML 数值表  
- 改 `SIGS` 里 L11/L13 mods、删定义、抬 STAMP/Affix/伤乘/skill_mult  
- 开防具签名 Java 施工 tip（仍须门禁过线 + 显式开工）  
- glance twin、sx20、ForgeChoice·wear·pity·awkDelta densify、深化 densify  
- 把本 tip / 补丁后 PASS 当成开工令

---

## 4. 门禁影响摘要

| ID | TIP-AC | 本补丁后 |
|----|--------|----------|
| S1/S2 | PASS | 继承 |
| S3-R / E / X | PASS | 继承 |
| S3-T / P | FAIL（刃 L11/L13） | **评测集砍 L11/L13 后重判**；防具侧预期净 |
| S4 | PASS | 继承 |
| 开工 | HOLD | **仍 HOLD**（过线 ≠ 开工） |

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] 补丁 DESIGN + tip 路径可点；exclude / HOLD 刃另轨可核  
- [x] power-paths：TIP-AC CLOSED FAIL `469cc1a46` + TIP-AD CLOSED  
- [x] harness exclude + `judge-s3` PASS（总控复跑）  
- [x] **无**插件施工派单；**无**业务 jar；**无** SIGS 数值改动  
- [x] `git show` 仅 docs + harness 评测成员；密码检查 0  
- [x] （批后）RESULT 记补丁后 S3-T/P；FAIL 停；PASS≠开工

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AC FAIL 后 · **S3 评测集补丁（砍刃 L11/L13）**（选项 1 · docs-only）  
- **批注：** 防具 alone 已净；刃 L11/L13 HOLD 另轨；禁抬表；S3-R/E/X/S4 继承；重判后 FAIL 停、PASS≠开工；零业务 jar；深化 densify / Affix / glance / sx20 / skill_mult 仍冻；无派插件  

## STATUS: OPEN · 待批 A·M · 2026-10-12 19:19 CST · 无码业务
