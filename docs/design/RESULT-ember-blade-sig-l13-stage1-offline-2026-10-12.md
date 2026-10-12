# RESULT · 刃签 L13 stage1 离线门禁 · 2026-10-12

> **状态：** `STAGE1_L13_OFFLINE = **FAIL**`（基线 alone 三族 · 预期）· TIP-AI CLOSED · 2026-10-12 19:56 Asia/Shanghai  
> **上游 DESIGN：** [`DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md`](DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md)  
> **stage0：** TIP-AG CLOSED `7d61761e4`  
> **假设钉死：** **H-R12 = R1+R2** · R5 = FAIL 后升级轨  
> **硬句：** FAIL → 停、禁抬表；PASS ≠ 开工；本窗零改 L13 mods · 零业务 jar · **HOLD 施工**

---

## 0. 继承 / 基线

| ID | 结果 | 注 |
|----|------|-----|
| stage0 规格 | ✅ PASS（继承 TIP-AG） | 身份核 + R1–R5 |
| TIP-AD exclude | ✅ 保留 | 甲门禁不采 L11/L13 |
| TIP-AE 甲轨 | ✅ 正交 | 不回滚 |
| **BASELINE L13 alone** | ❌ FAIL | burst −8.2 / scorch −9.8 / sustain −13.8 · 可达 |

---

## 1. 跑次元数据

| 项 | 值 |
|----|-----|
| 日期 (Asia/Shanghai) | 2026-10-12 19:56 |
| tol 源 | `docs/design/out-armor-sig-stage1-tol.md`（TIP-AC 继承 · 本窗不强制重跑 full tol） |
| S3 N | 2000（继承） |
| 操作者 | 总控 |
| 假设 ID | **H-R12**（R1+R2） |
| harness | `blade_sig_l13_gate_s1.py judge-alone` → kept=3 PASS=0 FAIL=3 |

---

## 2. 门禁结果

| 门 | 结果 | 注 / 链 |
|----|------|---------|
| G-alone | ❌ FAIL | judge-alone 三族可达 ❌；基线预期 |
| G-pair | ✅ 声明继承 | 仍 TIP-AD exclude |
| G-W30 | SKIP | 无 H-R12 候选建模 |
| G-X | ✅ N/A 基线 | 本窗零 candid mods（无伤乘/skill_mult/Affix/STAMP） |
| G-split | ✅ 书面 | 刃≠甲；L11 HOLD |
| G-id | ✅ 书面 | 环核 + L09 差分在假设层 |
| G-charge | ✅ 书面 | 对准死窗；禁伤乘补偿 |

---

## 3. 总判定

```
STAGE1_L13_OFFLINE = FAIL
```

- 门禁包已交付（假设钉死 + harness + RESULT）。  
- 基线 alone FAIL = 预期；**不停抬表**；**不开工**。  
- 下一施工 tip 须：影子 H-R12 结构说明 **且** 总控显式开工；否则另开合规 UX 薄窗。

---

## 4. 处置

| 若 | 则 |
|----|-----|
| 仅基线确认 FAIL（本窗） | ✅ 已确认；HOLD；可感内容走正交 tip |
| 影子 H-R12 PASS | 仍 HOLD；等显式开工 → 施工 tip |
| 影子 H-R12 FAIL | 停；迭代或升级 R5 另 tip；禁抬表 |
