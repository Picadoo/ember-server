# RESULT · 刃签 L13 stage1 离线门禁 · 2026-10-12

> **状态：** `STAGE1_L13_OFFLINE = **PENDING**` · TIP-AI 开窗空壳 · 2026-10-12 19:53 Asia/Shanghai  
> **上游 DESIGN：** [`DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md`](DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md)  
> **stage0：** TIP-AG CLOSED `7d61761e4`  
> **假设钉死：** **R1+R2**（蓄力窗结构 + 蓄力中输入）· R5 全量 resignature = FAIL 后升级轨  
> **硬句：** FAIL → 停、禁抬表凑线；PASS ≠ 自动开工；**本窗零改 L13 mods · 零业务 jar**

---

## 0. 继承 / 基线

| ID | 结果 | 注 |
|----|------|-----|
| stage0 规格 | ✅ PASS（继承 TIP-AG） | 身份核 + R1–R5 授权集 |
| TIP-AD exclude | ✅ 保留 | 甲门禁不采 L11/L13 |
| TIP-AE 甲轨 | ✅ 正交 | 不回滚 |
| **BASELINE L13 alone** | ❌ FAIL（继承 TIP-AC） | burst −8.2 / scorch −9.8 / sustain −13.8 · 可达 |

---

## 1. 跑次元数据（填数时抄）

| 项 | 值 |
|----|-----|
| 日期 (Asia/Shanghai) | PENDING |
| rules sha256 | PENDING |
| balance_version | PENDING |
| S3 N | PENDING（荐 2000） |
| 操作者 | PENDING |
| 假设 ID | **H-R12**（R1+R2） |

---

## 2. 门禁结果

| 门 | 结果 | 注 / 链 |
|----|------|---------|
| G-alone | PENDING | `out-blade-l13-stage1-tol.md` · `blade_sig_l13_gate_s1.py judge-alone` |
| G-pair | ✅ 声明继承 | **仍 exclude 出甲门禁**（TIP-AD）；本窗不重纳入 |
| G-W30 | PENDING / SKIP | 无候选建模则 SKIP；有则 \|ΔW30\|≤0.5 |
| G-X | PENDING | 勾 DESIGN §3.3 |
| G-split | ✅ 书面 | 刃≠甲；L11 HOLD；不回滚 TIP-AE |
| G-id | ✅ 书面（假设层） | 环核 + L09 差分保留在 H-R12 叙事 |
| G-charge | ✅ 书面（假设层） | 对准死窗方差；禁伤乘补偿 |

---

## 3. 总判定

```
STAGE1_L13_OFFLINE = PENDING
```

- 开窗交付 = 假设钉死 + 命令包 + RESULT 壳 + harness；**不**强制跑满。  
- 填数后改为 `PASS` 或 `FAIL`；**PASS 仍 HOLD 施工**，直至总控显式开工。  
- **禁止**为绿线改 live `SIGS['L13']` / 拧 skill_mult / 抬表。

---

## 4. 处置备忘（填数后用）

| 若 | 则 |
|----|-----|
| 仅基线确认 FAIL | 预期；继续 HOLD；可开影子评测窗（仍禁 live SIGS commit） |
| 影子 H-R12 PASS | 仍 HOLD；等显式开工 → 施工 tip |
| 影子 H-R12 FAIL | 停；迭代结构说明或升级 **R5** 另 tip；禁抬表 |
