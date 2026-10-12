# RESULT · 防具签名门禁 stage1 离线（S3 / S4）· 2026-10-12

> **状态：** PENDING · 等待 S3/S4 跑数填入 · TIP-AC 开窗  
> **上游 DESIGN：** [`DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md)  
> **stage0：** TIP-AB CLOSED `a5b264fce`  
> **硬句：** FAIL → 停、禁抬表凑线；**PASS ≠ 开工**（仍须总控显式批「开工」才开施工 tip）

---

## 0. 继承门禁

| ID | 结果 | 注 |
|----|------|-----|
| S1 规则规格 | ☐ PASS（继承 stage0） | 5 取 1 · §2.6 · tag/excl |
| S2 烙印扩甲 / 掉落不扩 | ☐ PASS（继承 stage0） | STAMP/价不变 |

---

## 1. 跑次元数据（填）

| 项 | 值 |
|----|-----|
| 日期 (Asia/Shanghai) | |
| rules sha256 | |
| balance_version | |
| players / weeks / dodge | |
| ML_SEEDS / NPROC | |
| 操作者 | |

---

## 2. S3 结果

### 2.1 S3-R 规则单测

```
命令：python3 tools/p1sim/armor_sig_gate_s1.py rules
结果：☐ PASS / ☐ FAIL
摘录：
```

### 2.2 S3-T / S3-P / S3-E

| 包 | 产物路径 | 判定 |
|----|----------|------|
| S3-T tol | `docs/design/out-armor-sig-stage1-tol.md`（或 `tools/p1sim/out-*-armor-sig-42.md`） | ☐ |
| S3-P pairs | `docs/design/out-armor-sig-stage1-pairs.md` | ☐ |
| S3-E talent | `docs/design/out-armor-sig-stage1-talent.md` | ☐ |

过线精神（D174 §8）：目标 ±2 pp；硬线 mean\|Δ\|≤1 pp 且无格超 ±3 pp。

失败格（如有）：

| 变体 | 格 | Δ pp | 处置 |
|------|-----|------|------|

### 2.3 S3-X ×0.97 正交清单

- [ ] 无新伤乘 / skill_mult  
- [ ] 未改 ×0.97 / 共鸣 F  
- [ ] 行为型叙事  
- [ ] FAIL 路径不含抬表  

**S3 总判：** ☐ PASS / ☐ FAIL

---

## 3. S4 结果（W30）

| 路线 | BASE W30 | CAND W30 | Δ 周 | 带内 (±0.5) |
|------|----------|----------|------|-------------|
| 无轮换 | | | | ☐ |
| P2-1 轮换 | | | | ☐ |
| P2-2 深渊 | | | | ☐ |
| 轮换 + 团本 | | | | ☐ |
| 团本 + 周目标 | | | | ☐ |
| 深渊 + 周目标 | | | | ☐ |

产物：

- BASE：`docs/design/out-armor-sig-stage1-w30-base.md`
- CAND：`docs/design/out-armor-sig-stage1-w30-cand-*.md`

若总控重钉阈值：写此处 ______ 周，签 ______。

**S4 总判：** ☐ PASS / ☐ FAIL

---

## 4. STAGE1_OFFLINE 总判

```
STAGE1_OFFLINE = PENDING | PASS | FAIL
```

- PASS 后：**仍 HOLD 施工**；下一步 = 总控显式「开工」令 → 另开施工 tip（单主题）。  
- FAIL：**停**；回 stage0 / 本 DESIGN 迭代；禁 Affix / 伤乘 / STAMP 凑线。

---

## 5. 备注

（跑数异常、seed 噪声、未建模键等）
