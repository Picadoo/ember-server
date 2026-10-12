# DESIGN · 防具签名门禁 stage0 补丁 · S3 评测集砍刃 L11/L13 · 2026-10-12

> **状态：** OPEN · 对接 TIP-AD · docs-only · **HOLD 施工 / 零业务 jar** · 2026-10-12 19:19 Asia/Shanghai  
> **对接 tip：** [`TIP-ember-armor-signature-gate-s3-eval-patch-thin-d12576-d12583-2026-10-12.md`](../tips/TIP-ember-armor-signature-gate-s3-eval-patch-thin-d12576-d12583-2026-10-12.md)  
> **上游：** TIP-AC CLOSED FAIL `469cc1a46` · RESULT [`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md) · stage0 [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md) · stage1 [`DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md)  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · **不改 SIGS / 修饰键数值** · 零业务 jar · PASS≠开工

---

## 0. 勘误一句

TIP-AC `STAGE1_OFFLINE=FAIL` 的根因是 **刃签 L11 / L13 既有债** 穿透进 S3-T/P 全表；**防具侧 alone（charm 升格集）可达格 0 条超 D174 ±3pp 硬线**。  
本补丁把 **防具签名门禁** 的 S3-T/P **评测集** 钉为：**排除刃 L11、L13**（alone 与含二者的 pairs）；刃债 **HOLD 另轨研究 tip**，**不**绑防具签名开工，**不**抬表凑线。

---

## 1. 为何选「砍评测集」而非「刃另轨先修」

| 选项 | 裁决 | 理由 |
|------|------|------|
| (1) stage0 补丁：S3 评测集砍 L11/L13 后重判/重跑 S3-T/P | **采纳** | 防具侧 alone 已净；L11/L13 是刃轨污染门禁透镜；不抬表即可解绑防具签名路径 |
| (2) 刃签 L11/L13 另轨研究（先修刃） | **HOLD 后继** | 真债仍在；另开 tip，不阻塞本门禁评测口径修正 |

---

## 2. S3 评测集（防具门禁口径 · 补丁后）

### 2.1 纳入（armor-gate set）

| 子集 | 成员 | 用途 |
|------|------|------|
| 防具侧 alone | `L02 L03 L06 L08 L10 L12 L14 L15`（原 charm / 防具侧升格） | S3-T 主透镜 |
| 合法刃×防具对 | `legal_pairs` **减去** 任一端为 L11 或 L13 的对 | S3-P 主透镜 |
| 其它刃 alone（参考） | `L01 L04 L05 L07 L09`（非 L11/L13） | 可并列报告；不因刃债否决防具升格 |

### 2.2 排除（HOLD 另轨 · 不进本门禁 PASS/FAIL）

| ID | 槽 | 说明 |
|----|-----|------|
| **L11** | blade · guard | TIP-AC 可达超 ±3pp；刃签既有债 |
| **L13** | blade · shape | TIP-AC 三族可达超硬线；刃签既有债 |
| 含 L11 / L13 的 pairs | — | 随刃债扩散；不进防具门禁判定 |

**明确：** 排除 = **评测成员资格**；**不是**从 `SIGS` 删行、不是改 mods、不是关掉落、不是抬 STAMP。

### 2.3 仍继承（不变）

- S3-R 规则单测（§2.6 / 5 取 1 / excl / 同 tag）  
- S3-E talent（既有 ML_BEST 无 L11/L13 最强对则保持）  
- S3-X ×0.97 正交清单  
- S4 W30（TIP-AC 已 PASS；本补丁不重开）  
- D174 §8 硬线口径（可达格；mean\|Δ\|≤1 且无格超 ±3）  
- **PASS ≠ 开工**；零 jar 直至门禁过线 + 总控显式开工

---

## 3. 对 stage0 / stage1 的修改点

| 文档 | 改动 |
|------|------|
| 本补丁 DESIGN | S3 评测集勘误钉死（权威） |
| stage0 overlay DESIGN §3 **S3** 行 | 加注：防具门禁 S3-T/P **排除刃 L11/L13**；见本补丁 |
| stage1 offline DESIGN §3 | 用例包 / 命令 / PASS 口径写明 exclude；重跑或 `judge-s3` 过滤 |
| `tools/p1sim/armor_sig_gate_s1.py` | `ARMOR_GATE_S3_EXCLUDE`；`cmds` / `checklist` / `judge-s3` 仅评测成员，无平衡常数 |

---

## 4. 重判 / 重跑

| 路径 | 说明 |
|------|------|
| **A · 过滤既有 out（优先）** | `python3 tools/p1sim/armor_sig_gate_s1.py judge-s3` 读 `out-armor-sig-stage1-tol.md` / `pairs.md`，只判非 L11/L13 可达行 |
| **B · 重跑（可选）** | 仍可全表 `mainline.py tol|pairs`；**门禁总判**只采 A 的成员集（或日后 harness 包装 exclude 后再 report） |

FAIL（补丁后仍有非 L11/L13 可达超线）→ **停**；禁抬表。  
PASS（补丁后 S3-T/P 净）→ 可与既有 S3-R/E/X + S4 PASS 合并记 `STAGE1_OFFLINE` 候选绿；**仍 ≠ 开工**。

---

## 5. HOLD · 刃 L11/L13 另轨

- 另开 tip（docs-only 研究 → 规格/砍对/调律）；**不**绑防具签名施工。  
- 硬禁：抬表 / Affix / 伤乘 / skill_mult / 深化 densify 凑 L11·L13。  
- 本补丁 **不**预授权刃侧改数。

---

## 6. NON-goals

- 改 `SIGS` mods / 删 L11·L13 定义 / 抬 STAMP / Affix / 伤乘 / skill_mult  
- 开施工 tip / 业务 jar / 插件 YAML  
- 把本补丁 PASS 写成开工令  
- sx20 / glance / 深化 densify

---

## 7. 成功标准（本补丁 tip）

- [ ] 本 DESIGN + TIP-AD 可点；勘误 / 评测集 / HOLD 刃另轨可核  
- [ ] stage0 §3 S3 注 + stage1 §3 + harness exclude 对齐  
- [ ] power-paths：TIP-AC CLOSED FAIL `469cc1a46`；TIP-AD OPEN  
- [ ] 零业务 jar；不改平衡数值表  
- [ ] （可后继）`judge-s3` 或重跑后 RESULT 记 S3-T/P 补丁后判；FAIL 停；PASS≠开工
