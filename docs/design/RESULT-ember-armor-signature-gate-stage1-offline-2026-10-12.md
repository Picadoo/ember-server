# RESULT · 防具签名门禁 stage1 离线（S3 / S4）· 2026-10-12

> **状态：** STAGE1_OFFLINE = **PASS（TIP-AD patched）** · TIP-AC 全表 FAIL 历史保留 `469cc1a46` · 跑数 18:34–19:18 · judge-s3 19:22  
> **TIP-AD：** CLOSED 批 A·M `abc3875d0` · 见 §6 · 总控显式开工令已发 → **TIP-AE** 施工 tip OPEN  
> **上游 DESIGN：** [`DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md)  
> **stage0：** TIP-AB CLOSED `a5b264fce`  
> **硬句：** FAIL → 停、禁抬表凑线；PASS ≠ 自动开工；**总控已显式开工**（防具签名最小切片 · 见 STATUS-AD）→ tip [`TIP-ember-armor-signature-min-slice-build-thin-d12584-d12599-2026-10-12.md`](../tips/TIP-ember-armor-signature-min-slice-build-thin-d12584-d12599-2026-10-12.md)

---

## 0. 继承门禁

| ID | 结果 | 注 |
|----|------|-----|
| S1 规则规格 | ✅ PASS（继承 stage0） | 5 取 1 · §2.6 · tag/excl |
| S2 烙印扩甲 / 掉落不扩 | ✅ PASS（继承 stage0） | STAMP/价不变；本窗未改掉落/STAMP |

---

## 1. 跑次元数据

| 项 | 值 |
|----|-----|
| 日期 (Asia/Shanghai) | 2026-10-12 17:43–19:18 CST |
| rules sha256 | `070e08cdb5c42e6d` |
| balance_version | 123 |
| players / weeks / dodge | S4：300 / 12 / 0.5（`--abyss --raid --goals`） |
| ML_SEEDS / NPROC | `7,11` / 6 |
| S3 N | **2000** / cell·seed |
| ML_BEST（S3-E） | `{"burst":["L04+L14"],"scorch":["L01+L14"],"sustain":["L05+L14"]}`（pairs 可达硬线内各族最高均 Δ） |
| 操作者 | Grok Bot executor（docs-only · 零业务 jar） |

---

## 2. S3 结果

### 2.1 S3-R 规则单测

```
命令：python3 tools/p1sim/armor_sig_gate_s1.py rules
结果：✅ PASS
摘录：PASS zero_worn / one_charm / prio_charm / prio_head / dual_locked /
      blade_same_tag / blade_same_tag_all / talent_excl / cap_one → S3-R PASS
```

### 2.2 S3-T / S3-P / S3-E

判定口径：D174 §8；表以 **可达格** 为主（历史同）；🟡 = 硬线内（mean\|Δ\|≤1 且无格超 ±3）可记 PASS 行；❌ = 超硬线。

| 包 | 产物路径 | 判定 |
|----|----------|------|
| S3-T tol | [`out-armor-sig-stage1-tol.md`](out-armor-sig-stage1-tol.md) | ❌ FAIL |
| S3-P pairs | [`out-armor-sig-stage1-pairs.md`](out-armor-sig-stage1-pairs.md) | ❌ FAIL |
| S3-E talent | [`out-armor-sig-stage1-talent.md`](out-armor-sig-stage1-talent.md) | ✅ PASS |

**分拆（防具侧 vs 刃侧既有债）：**

| 透镜 | 结果 |
|------|------|
| 防具侧 alone（charm L02/L03/L06/L08/L10/L12/L14/L15）可达 | 全部 ✅/🟡 · **0 条超硬线** |
| 刃侧 alone 可达 | **L11** burst/sustain ❌；**L13** 三族 ❌（既有刃签债，非本窗新乘区） |
| 合法对（无 L11/L13）可达 | 绝大多数 ✅/🟡（例 L01+L02 / L04+L03 / L05+L06 ✅） |
| 含 L11/L13 的对可达 | 大量 ❌（随刃债） |
| S3-E 最强对+tal（EXCL 开）可达 | 三族全部 ✅ |

失败格（可达 · 代表；完整见 out）：

| 变体 | 格/摘要 | Δ pp | 处置 |
|------|---------|------|------|
| L11 · burst · 可达 | 最低 q02c d0.5 | **−3.025**（恰超 ±3） | 刃签另轨改规格/砍对；**禁抬表** |
| L11 · sustain · 可达 | 最低 q01c d0.3；mabs 1.02 | **−5.5** | 同上 |
| L13 · burst/scorch/sustain · 可达 | 多格 \|Δ\|≫3；mabs 1.85–2.70 | 至 **−13.8** | 同上（skill_charge 等既有方差） |
| L11+\* / L13+\* pairs · 可达 | 同上族扩散 | 多格超硬线 | 砍对或先修刃 alone |

### 2.3 S3-X ×0.97 正交清单

- [x] 无新伤乘 / skill_mult（未改 `SIGS` / 未发明新 mods 键）  
- [x] 未改 ×0.97 / 共鸣 F  
- [x] 行为型叙事（防具侧 = 原 charm 语义升格）  
- [x] FAIL 路径不含抬表（处置=改规格/砍对）

**S3 总判：** ❌ **FAIL**（S3-T / S3-P 刃侧 L11/L13 超 D174 ±3pp 硬线；S3-R / S3-E / S3-X / 防具侧 alone PASS）

---

## 3. S4 结果（W30）

度量：`growthrun.py weeks` · 「两件极品」≥30% 插值周。  
CAND = day-1 佩戴既有刃+护符（与防具侧升格后 mods 同构；S2 不扩掉落）。

| 路线 | BASE W30 | CAND W30 (L01+L02) | Δ 周 | 带内 (±0.5) |
|------|----------|---------------------|------|-------------|
| 无轮换 | 6.46 | 6.73 | +0.27 | ✅ |
| P2-1 轮换 | 6.43 | 6.67 | +0.24 | ✅ |
| P2-2 深渊 | 5.10 | 4.95 | −0.15 | ✅ |
| 轮换 + 团本 | 7.75 | 7.56 | −0.19 | ✅ |
| 团本 + 周目标 | 6.53 | 6.17 | −0.37 | ✅ |
| 深渊 + 周目标 | 5.88 | 5.88 | +0.00 | ✅ |

产物：

- BASE：[`out-armor-sig-stage1-w30-base.md`](out-armor-sig-stage1-w30-base.md)
- CAND：[`out-armor-sig-stage1-w30-cand-L01L02.md`](out-armor-sig-stage1-w30-cand-L01L02.md)

未另跑第二族对（可选；主代表对已满 ±0.5）。

**S4 总判：** ✅ **PASS**（六路线 \|ΔW30\|≤0.5；符合「掉落不扩 → Δ≈0」预期）

---

## 4. STAGE1_OFFLINE 总判

```
STAGE1_OFFLINE = PASS  # TIP-AD patched (excl L11/L13); was FAIL on full table
```

- 因：**S3-T / S3-P** 在 D174 §8 硬线下 FAIL（刃 L11/L13 既有债穿透到 alone + 组合）。  
- S1/S2 继承 PASS · S3-R PASS · S3-E PASS · S3-X PASS · **S4 PASS** · 防具侧 alone 无新乘区超线。  
- **仍 HOLD 施工**；即使日后 S3 补绿，亦须总控显式「开工」令 → 另开施工 tip。  
- **FAIL 处置：** 停；回 stage0 / 刃签另轨改 L11·L13 规格或砍对后重跑 S3-T/P；**禁止** Affix / 伤乘 / STAMP / skill_mult 凑线。防具侧升格语义本身未证明引入新乘区。

---

## 5. 备注

- 全格（含不可达）❌ 中，L10 alone / 部分对的早期格大负偏为「未掉落即可戴」噪声；历史口径以可达为准。  
- L11 burst 可达硬线贴边：max\|Δ\|=3.025 → 判定 ❌（非 🟡）。  
- 零业务 jar；未改 CoreRpg / TrMenu / Economy / loot / STAMP 表。  
- pkl 缓存：`/tmp/ml-armor-sig/{tol,pairs,talent}.pkl`（未入库）。

---

## 6. TIP-AD · S3 评测集补丁重判（2026-10-12 19:19 CST）

> 补丁 DESIGN：[`DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md)  
> tip：[`TIP-ember-armor-signature-gate-s3-eval-patch-thin-d12576-d12583-2026-10-12.md`](../tips/TIP-ember-armor-signature-gate-s3-eval-patch-thin-d12576-d12583-2026-10-12.md)  
> 命令：`python3 tools/p1sim/armor_sig_gate_s1.py judge-s3`（过滤既有 out；**未**改 SIGS / **未**抬表 / **零**业务 jar）

| 包 | 口径 | 结果 |
|----|------|------|
| S3-T 可达 · excl L11/L13 | kept=23 dropped=6 | **PASS**（0 ❌ · soft 3） |
| S3-P 可达 · excl L11/L13 | kept=40 dropped=25 | **PASS**（0 ❌ · soft 8） |
| S3-R / E / X | 继承 TIP-AC | PASS |
| S4 | 继承 TIP-AC | PASS |
| 刃 L11/L13 | **HOLD 另轨** | 不进本门禁总判 |

**说明：** TIP-AC 全表 `STAGE1_OFFLINE=FAIL` 戳 **保留**（历史事实）。补丁后 **防具门禁透镜** S3-T/P 净；合并候选绿须总控批 TIP-AD 后写入正式总判句。  
**硬句仍有效：** FAIL→停禁抬表；**PASS ≠ 开工**（仍须总控显式「开工」才开施工 tip）。

---

## 6. TIP-AD 评测集补丁（2026-10-12 19:22）

| 项 | 值 |
|----|-----|
| 补丁 tip | TIP-AD CLOSED · 批 A·M |
| 方法 | `python3 tools/p1sim/armor_sig_gate_s1.py judge-s3` |
| EXCLUDE | 刃 L11、L13（alone + 含二者 pairs） |
| S3-T kept | 23（dropped 6）· FAIL=0 · soft=3 · **PASS** |
| S3-P kept | 40（dropped 25）· FAIL=0 · soft=8 · **PASS** |
| 继承 | S1/S2/S3-R/E/X/S4 PASS（TIP-AC） |
| **STAGE1_OFFLINE** | **PASS（patched）** |
| 刃 L11/L13 | **HOLD 另轨**；本窗不修 mods |
| 开工 | **仍须总控显式开工令**（本 RESULT 的 PASS ≠ 自动开工） |
