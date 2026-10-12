# RESULT · 烬斩 SF4 U1 微缓 T0 离线门禁 · 2026-10-12

> **状态：** `STAGE0_SF4_U1_T0 = **PASS**`（主荐 U1_sl10 三族 ✅）· TIP-BF CLOSED 批 A·M · **HOLD 本窗 jar · PASS≠开工** · 总控 2026-10-12 23:15 Asia/Shanghai · **显式开工 → TIP-BG OPEN**  
> **上游 DESIGN：** [`DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md`](DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md)  
> **stage0：** TIP-BE CLOSED `02db68148`  
> **假设钉死：** **H-U1** · 主荐 **U1_sl10** · 备 U1_sl08（亦 ✅）  
> **硬句：** FAIL → 停、禁抬 skill_mult；**PASS ≠ 开工**；本窗零业务 jar · **HOLD 施工** · **不派插件**

---

## 0. 继承 / 基线

| ID | 结果 | 注 |
|----|------|-----|
| stage0 规格 | ✅ PASS（继承 TIP-BE） | U1 主荐 · 门禁草案 |
| 守招 D301 | ✅ LIVE 正交 | 本窗不改 |
| D438 slash 套装落点 | ✅ 正交 | `kit_slash_*` 路径未改；SF4 用 `kit_sf4_*` |
| L13 施工 | ❌ HOLD | TIP-AI FAIL · PASS≠开工 |
| Anvil soft densify | ❌ 仍冻 | — |

---

## 1. 跑次元数据

| 项 | 值 |
|----|-----|
| 日期 (Asia/Shanghai) | 2026-10-12 23:14 CST |
| n | **1500** |
| 格网 | skillkit · 三套装 · 可达自 q01 · 39 格 |
| 假设 ID | **H-U1** |
| harness | `sf4_u1_gate_t0.py judge` → focus=U1_sl10 PASS=3 FAIL=0 |
| 报告 | [`out-sf4-u1-t0-p1sim.md`](out-sf4-u1-t0-p1sim.md) |
| 模型 | `kit_sf4_mark_secs=1.0` · `kit_sf4_slow=0.12`（tools-only） |

### 1.1 主荐 U1_sl10 摘要

| 套装 | ±2 内 | 最高 Δpp | 最低 Δpp | 平均 \|Δ\| | 判定 |
|------|-------|----------|----------|-----------|------|
| burst | 39/39 | +2.0 | −0.1 | 0.21 | ✅ |
| scorch | 39/39 | +2.0 | +0.0 | 0.12 | ✅ |
| sustain | 39/39 | +2.0 | −0.2 | 0.14 | ✅ |

备 U1_sl08：三族同构 ✅（本窗不替换主荐）。

---

## 2. 门禁结果

| 门 | 结果 | 注 / 链 |
|----|------|---------|
| **G-within** | ✅ **PASS** | U1_sl10 三族全 ✅；硬线+目标均满足 |
| G-prefer | ✅ | always-on 主行过线；受伤 Δ% −0.1～−0.2（微缓侧移可读） |
| G-x | ✅ | 无 skill_mult/伤乘/Affix/招架/CD/几何 |
| G-ortho | ✅ | SF1–3 / D301 / 身法·烬突不动 |
| G-id | ✅ | 短窗微缓 · 非永久伤 |
| G-hold | ✅ | **PASS≠开工** 文内硬句有效 |

---

## 3. 总判定

```
STAGE0_SF4_U1_T0 = PASS
```

- 门禁包已交付并跑通（H-U1 + harness + 离线钩 + 报告）。  
- **仍 HOLD 施工**；可报总控 **显式开工**（另 tip）后才改 CoreRpg。  
- 形状偏好（LINE / 套装推荐）= 施工接线层，本窗离线代理未建模形状分叉。

---

## 4. 处置

| 若 | 则 |
|----|-----|
| **本窗 PASS（已达成）** | 仍 HOLD 本窗 jar；**显式开工 → TIP-BG OPEN** |
| 显式开工 | **TIP-BG**：CoreRpg 接线 U1_sl10；禁 skill_mult；形状条件按 stage0；批后派余烬-插件 |
| 总控拒开工 | 观察；禁借号改 jar |
