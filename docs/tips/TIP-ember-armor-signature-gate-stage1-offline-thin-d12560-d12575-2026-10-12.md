# tip · TIP-AC 防具签名门禁 stage1 离线（S3 p1sim + S4 W30 · docs+tools）· D12560–D12575

> **状态：** OPEN · 待批 **A · 方案 M** · docs+tools · HOLD 施工（2026-10-12 17:41 Asia/Shanghai）  
> **上游：** [`DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](../design/DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md) · TIP-AB CLOSED DESIGN stage0 APPROVED HOLD `a5b264fce` · stage0 [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](../design/DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md)  
> **结果落点：** [`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](../design/RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md)  
> **前置：** S1/S2 书面继承 stage0；深化 densify **仍冻**；烬斩门闩 TIP-Y **仍有效**  
> **施工岗：** **可 docs+tools**（p1sim harness）；**零业务 jar / 零插件 YAML 表**；**不**派插件改 Signature/Loadout/loot/STAMP/TrMenu/Economy  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零 sx20 / 粒子 densify** · **零 skill_mult** · **不过线不停** · **PASS ≠ 开工**（仍须总控显式开工）

---

## 0. 一句话

TIP-AB 后开 **stage1 离线门禁**：可执行 **S3（p1sim 单条/组合/excl · 不与 ×0.97 叠新乘区）** + **S4（W30 vs 现行六槽+签名基线 ±0.5 周）**；复用 `mainline.py`；结果进 RESULT；本窗 **不是**施工开工令。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| 当场开防具签名 Java 施工 | **否** | 须 S3/S4 过线 + 总控显式开工 |
| 重开 S1/S2 规格 densify | **否** | stage0 已批；本窗继承 |
| 抬 STAMP / 甲掉签名凑 W30 | **否** | 抬表；S2 默认可烙不可掉 |
| 烬斩 / sx20 / Affix | **否** | 硬禁 / 门闩 |
| **stage1 离线门禁 tip（S3+S4）** | **采纳** | 总控点名；docs+tools；零业务 jar |

---

## 2. 方案 M（定稿荐 · docs+tools）

| ID | 项 | 验收 |
|----|-----|------|
| D12560 | 本 tip + DESIGN `DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md` | 路径可点 |
| D12561 | DESIGN power-paths 进度戳：**TIP-AB CLOSED `a5b264fce`**；**TIP-AC OPEN**（stage1 离线门禁 · docs+tools · 零业务 jar） | 戳可见 |
| D12562 | S1/S2 继承 stage0 PASS 写入 DESIGN/RESULT（本窗不重开规格施工） | 可核 |
| D12563 | S3 用例包：R 规则 / T tol / P pairs / E talent / X ×0.97 正交 | DESIGN §3 |
| D12564 | S3 精确命令：`mainline.py tol|pairs|talent|report`（不发明新 SIGS 数） | 可复制 |
| D12565 | S3-X 清单：无新伤乘/skill_mult；不动 ×0.97 / 共鸣 F | 可勾 |
| D12566 | S4 基线：现行六槽 live + 现行签名经济；CAND=S2 默认可烙不可掉同构 | DESIGN §4.1 |
| D12567 | S4 命令：`mainline.py econ` BASE `[]` vs CAND 既有对；ΔW30 ±0.5 周（或总控重钉） | 可复制 |
| D12568 | RESULT 路径空壳：`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md` | 可点 |
| D12569 | 薄 harness `tools/p1sim/armor_sig_gate_s1.py`（rules/cmds/checklist）；S3-R 可本地 PASS | 零 jar |
| D12570 | FAIL=停迭代；禁抬 STAMP/Affix/伤乘凑线 | 文内硬句 |
| D12571 | PASS≠施工；过线后仍须总控显式开工才开施工 tip | 文内硬句 |
| D12572 | HOLD：本 tip **不**派插件；零业务 jar / 零 Economy-pom 脏改 | 无 jar |
| D12573 | NON-goals / 硬禁入 DESIGN §7（含 glance/sx20/skill_mult/深化 densify） | 可核 |
| D12574 | 本 commit 仅 docs + `tools/p1sim/armor_sig_gate_s1.py`；不强制跑满 tol/econ | PASS |
| D12575 | 缓冲：错链/错戳/RESULT 表头热修 | 仅本文档 |

### 玩家口径（固定）

- 六槽甲已能穿；签名暂时仍主要在刃/护符。  
- 本窗只做**离线模拟门禁**，**不加新签名、不改掉落、不改徽记价、不改战斗数值表**。  
- 模拟过线后还要总控说「开工」才会动游戏里的烙印/甲签名；过线 ≠ 已经上线。

---

## 3. 明确不做

- 任何 CoreRpg / TrMenu / p1map / ember-v1-runs / Economy 业务改码或 YAML 数值表  
- 防具签名 Java、`mods()` 扩甲、烙印目标扩甲、stampable 扩甲（属过门禁+显式开工后另 tip）  
- 抬 STAMP_RATE / 徽记周池 / Affix / 伤乘 / skill_mult  
- 关或拧六槽三开关；改 ×0.97 / 共鸣 F  
- glance twin、sx20、ForgeChoice·wear·pity·awkDelta densify、深化 densify  
- 把本 tip / PASS 当成开工令或数值终表发明

---

## 4. 门禁摘要（详 DESIGN）

| ID | 一句话 | 本窗 |
|----|--------|------|
| S1 | 规则规格冻结 | 继承 stage0 |
| S2 | 烙印扩甲 · 掉落不扩 | 继承 stage0 |
| **S3** | p1sim 单条/组合/excl；不与 ×0.97 叠新乘区 | **本窗可执行** |
| **S4** | W30 vs 现行六槽+签名基线 ±0.5 周 | **本窗可执行** |

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] DESIGN + tip + RESULT 路径可点；S3/S4 命令可核  
- [ ] power-paths：TIP-AB CLOSED `a5b264fce` + TIP-AC OPEN  
- [ ] harness `armor_sig_gate_s1.py rules` 本地可 PASS（本 commit 已跑）  
- [ ] **无**插件施工派单；**无**业务 jar  
- [ ] `git show` 仅 docs + 该 tools 薄文件；密码检查 0  
- [ ] （可后继）S3/S4 数字填入 RESULT → `STAGE1_OFFLINE=PASS|FAIL`

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AB 后 · **防具签名门禁 stage1 离线（S3 p1sim + S4 W30）**  
- **批注：** 复用 mainline.py；S3-R harness PASS；RESULT PENDING；不过线不停；PASS 仍 HOLD 至显式开工；零业务 jar；深化 densify / Affix / 抬表 / glance / sx20 / skill_mult 仍冻；无派插件  

## STATUS: OPEN · 待批 A·M · 2026-10-12 17:41 CST · docs+tools · 零业务 jar
