# tip · TIP-AI 刃签 L13 stage1 离线门禁（R1+R2 · docs+tools）· D12632–D12647

> **状态：** CLOSED · 批 **A · 方案 M** · `STAGE1_L13_OFFLINE=FAIL`（基线 alone 三族 · 预期）· HOLD 施工 · 零改 L13 mods · 零业务 jar · 2026-10-12 19:56 Asia/Shanghai  
> **上游：** [`DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md`](../design/DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md) · TIP-AG CLOSED DESIGN stage0 APPROVED HOLD `7d61761e4` · TIP-AH CLOSED live `1.65.774-d12631` · RESEARCH [`RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md`](../design/RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md) · 范式 TIP-AC stage1  
> **结果落点：** [`RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md`](../design/RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md)（PENDING）  
> **前置：** L13 stage0 已批；TIP-AD exclude **保留**；深化 densify / STAMP / skill_mult **仍冻**；烬斩门闩 TIP-Y **仍有效**  
> **施工岗：** **可 docs+tools**（p1sim harness）；**零业务 jar / 零插件 YAML**；**不**派插件改 Signature/SkillService/L13 mods  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零 sx20 / 粒子 densify** · **零 skill_mult** · **零 STAMP 抬** · **零改 L13/L11 mods** · **不过线不停** · **PASS ≠ 开工**

---

## 0. 一句话

TIP-AH 后开 **L13 stage1 离线门禁**：钉死评测假设 **R1+R2**（蓄力窗结构 + 蓄力中输入）；交可执行 G-alone/… 门禁包 + RESULT PENDING + 薄 harness；本窗 **不是**施工开工令。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **L13 stage1 离线门禁（R1+R2）** | **采纳** | 总控点名；接 TIP-AG HOLD；docs+tools；零改 mods |
| 当场改 L13 mods / 缩短 charge 凑绿 | **否** | 硬禁；历史 0.25–0.45 上偏；须门禁+显式开工 |
| R5 全量 resignature 本窗评测 | **否（升级轨）** | 高成本；R1+R2 FAIL 后再开 |
| attune / 甲 stampable / L11 施工 | **否** | 正交 / HOLD |
| Affix / 伤乘 / skill_mult / sx20 / 深化 densify / STAMP | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐 · docs+tools）

| ID | 项 | 验收 |
|----|-----|------|
| D12632 | 本 tip + DESIGN `DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md` | 路径可点 |
| D12633 | DESIGN power-paths 进度戳：**TIP-AH CLOSED live `1.65.774-d12631`**；**TIP-AI OPEN**（L13 stage1 离线门禁 · docs+tools · 零改 mods） | 戳可见 |
| D12634 | 钉死假设 **H-R12 = R1+R2**；R5 = FAIL 后升级轨；禁 skill_mult/抬表 | DESIGN §2 |
| D12635 | 门禁表 G-alone / G-pair / G-W30 / G-X / G-split / G-id / G-charge | DESIGN §3 |
| D12636 | G-pair 默认：**仍 TIP-AD exclude**（不重纳入甲门禁） | 可核 |
| D12637 | BASELINE L13 alone FAIL 继承 TIP-AC 写入 RESULT | RESULT §0 |
| D12638 | 精确命令：`mainline.py tol|econ` + harness `judge-alone`（不改 SIGS） | DESIGN §3.2 |
| D12639 | G-X 清单：无伤乘/skill_mult/Affix/STAMP/甲 stampable | DESIGN §3.3 |
| D12640 | RESULT 路径空壳 PENDING：`RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md` | 可点 |
| D12641 | 薄 harness `tools/p1sim/blade_sig_l13_gate_s1.py`（checklist/cmds/judge-alone） | 零 jar |
| D12642 | harness `judge-alone` 对既有 armor stage1 tol：**L13 alone 三族 FAIL**（基线预期） | 本地可复现 |
| D12643 | FAIL=停；禁抬表/拧 skill_mult/缩短 charge 硬凑 | 文内硬句 |
| D12644 | PASS≠施工；过线后仍须总控显式开工才开施工 tip | 文内硬句 |
| D12645 | HOLD：本 tip **不**派插件；零业务 jar；零改 L13/L11 mods | 无 jar |
| D12646 | NON-goals / 硬禁入 DESIGN §7（含 glance/sx20/STAMP/深化 densify） | 可核 |
| D12647 | 本 commit 仅 docs + `tools/p1sim/blade_sig_l13_gate_s1.py`；不强制跑满新 tol/econ；缓冲仅错链/错戳/RESULT 表头 | PASS |

### 玩家口径（固定）

- 炉锁巨锤 **还在**；本窗 **不改它的数字、不加新招牌**。  
- 本窗只做 **离线门禁说明书**：以后若修，修的是 **蓄力怎么打 / 蓄力中能不能动**，不是加伤害。  
- 考试里暂时仍不拿这把刃当「甲签名能不能上」的考题（TIP-AD）。  
- 模拟过线 ≠ 已经改游戏；还要总控说「开工」。

---

## 3. 明确不做

- 任何 CoreRpg / TrMenu / p1map / Economy 业务改码或 YAML 数值表  
- 改 `DEFS` / `SIGS['L13']` / SkillService 蓄力逻辑；改 L11 mods  
- 抬 Affix / STAMP / 伤乘 / skill_mult；glance twin；sx20；深化 densify  
- 扩甲 stampable；回改 TIP-AE；把刃债绑回防具  
- 把本 tip / PASS 当成开工令；本窗开 R5 全量换签施工

---

## 4. 门禁摘要（详 DESIGN）

| 门 | 一句话 | 本窗 |
|----|--------|------|
| G-alone | L13 alone 三族可达过 D174 硬线 | 可执行（基线预期 FAIL） |
| G-pair | 仍 exclude 出甲门禁 | 声明继承 |
| G-W30 | 动结构则 \|ΔW30\|≤0.5 | 可选 / SKIP |
| G-X | 无伤乘 / skill_mult / Affix / STAMP / 甲 stampable | 清单 |
| G-split | 刃≠甲；L11 HOLD | 书面 |
| G-id | 环核 + L09 差分 | 书面 |
| G-charge | 对准死窗；禁伤乘补偿 | 书面 |

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] DESIGN + tip + RESULT 路径可点；假设 R1+R2 可核  
- [x] power-paths：TIP-AH CLOSED live + TIP-AI OPEN  
- [x] harness checklist/cmds；`judge-alone` 基线 L13 FAIL 可复现  
- [x] **无**插件施工派单；**无** L13/L11 数值改动；**无**业务 jar  
- [x] `git show` 仅 docs + 该 tools 薄文件；密码检查 0  
- [x] RESULT 填基线 → `STAGE1_L13_OFFLINE=FAIL`（预期 · HOLD）

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AH 后 · **刃 L13 stage1 离线门禁**（钉 R1+R2；docs+tools；零改 mods）  
- **批注：** 假设对准 skill_charge 死窗；R5 升级轨；RESULT PENDING；基线 alone FAIL 预期；不过线不停；PASS 仍 HOLD 至显式开工；TIP-AD exclude 保留；零业务 jar；深化 densify / Affix / 抬表 / glance / sx20 / skill_mult / STAMP 仍冻；无派插件  

## STATUS: OPEN · 待批 A·M · 2026-10-12 19:53 CST · docs+tools · 零改 L13 mods · 零业务 jar
