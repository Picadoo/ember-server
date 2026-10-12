# DESIGN · 烬斩 SF4 战斗微效用（stage0 · HOLD 施工）· 2026-10-12

> **状态：** OPEN stage0 · TIP-BE · **HOLD 施工** · 2026-10-12 23:04 Asia/Shanghai  
> **文首硬句：** 本文件 = **可批 stage0 规格**；**施工须** p1sim within 门禁过线 **且** 总控 **显式开工**。本 tip / 本 DESIGN **不是**开工令。**零改 skill_mult / CD / 几何盒 · 零 jar · 零插件派单** 直至显式开工。  
> **对接 tip：** [`TIP-ember-skill-sf4-micro-utility-design-stage0-thin-d12984-d12999-2026-10-12.md`](../tips/TIP-ember-skill-sf4-micro-utility-design-stage0-thin-d12984-d12999-2026-10-12.md)  
> **上游 RESEARCH：** [`RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md`](RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md) · TIP-Y · Stage2 micro DESIGN · TIP-K · D301 招架 LIVE  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo **仍冻**；soft 展示族勿再叠  
> **硬禁：** 零 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零粒子 densify · 不重开 TIP-K cue · 零改 PARRY_CD/flat · 零复活守墓壁垒旧表 · 零灰印/聚火夹带 · 零 Anvil/soft 假耗 densify · 零开 L13 施工 · 零再扫甲闸 · 零扩 stampable · 零 SetChase 六槽 densify

---

## 0. 一句话

让烬斩三形状在 **短触发窗** 内带上 **非伤乘** 战斗微差（微缓 / 微疗择一主效用）；规格可批，施工 **HOLD** 至 p1sim within + 显式开工。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| SF1–SF3 | 命中反馈 / 命中数 / 套装落点半行 **SHIP** | **保留 · 不 twin** |
| 形状几何 | FAN/LINE/RING 命中盒已分 | **零改几何盒本窗** |
| 战斗微结果 | 换形状 **不改变** 缓/疗/抗 | **SF4：形状×短窗微效用** |
| 招架 D301 | 防御时机键 LIVE | **正交不动** |
| skill_mult / CD | 硬禁抬 | **仍硬禁** |

---

## 2. 方案 M（定稿荐 · stage0）

### 2.1 身份核（改版不得删）

| 核 | 规格 |
|----|------|
| 键 | 仍 **烬斩 F**（三形状）；**不**新主动键 |
| 承诺 | 形状选择影响 **短窗微控/微疗**，不是皮 |
| 行为型 | **非**永久伤乘 / skill_mult / 新 Affix |
| 正交 | **不**改招架 / 身法 / 烬突数值与 CD |

### 2.2 主效用（T0 可测 · 三选一主荐）

| ID | 效用 | 触发意向 | 禁夹带 |
|----|------|----------|--------|
| **U1（主荐）** | **微缓** | LINE（或套装推荐形状）命中首领/精英短窗 → Slowness 短时弱档（例 I · ≤1.0s 量级，终表 T0 定） | 禁改基础伤；禁整场永久缓 |
| **U2** | **微疗** | RING 命中 ≥N 目标或命中后短窗 → 自疗固定额（flat HP，禁 %最大生命大系数） | 禁战斗外刷疗；禁抬药水表 |
| **U3** | **微抗** | FAN 命中后短窗 → 自抗性弱档极短（禁均匀 taken 永久乘） | 禁替代招架；禁整场 −red |

**荐批 M：** 先 **U1 微缓** 单 cand 进 T0；U2/U3 仅当 U1 择优失败再换，**禁止**同窗打包三效用。

### 2.3 触发窗与代价（结构 · 非终表）

| 项 | 意向 |
|----|------|
| 窗 | 命中成功后短触发（与 SF1 反馈同拍可感）；**无**常驻光环 |
| ICD | **独立** 短 ICD 或「每次烬斩充能至多一次 SF4」（T0 定）；**不**共享招架 CD / 身法 CD |
| 失败 | 空挥 / 未达形状条件 → **无**效用（防白嫖） |
| 展示 | 成功可 **一行** ActionBar（禁 cue 墙；禁 TIP-K twin） |

### 2.4 明确不授权

- 拧 `skill_mult` / 1.5B / 烬斩 CD / FAN·LINE·RING 命中盒  
- TIP-K 形状/身法 AB densify · 粒子 densify · glance twin  
- 改 `PARRY_CD` / parry flat · 复活 `kit_guard_red` 旧壁垒  
- 灰印 / 聚火 / 独立副招 CD 夹带  
- Anvil soft densify · soft 展示族再叠 · L13 施工 · 扩 stampable  

---

## 3. 门禁表（p1sim within · PASS≠开工）

| 门 | 一句话 |
|----|--------|
| **G-within** | 三套装 × 代表格：通关率 Δ ∈ **既有 skill-kit / 侧移惯例硬线**（意向 ±2pp；无格 \|\|Δ\|\|>3）；规则哈希入库 |
| **G-prefer** | 会换推荐形状的人可感侧移；乱按不净赚通关 |
| **G-x** | **无** skill_mult / 伤乘 / Affix / 抬表 / 改招架 |
| **G-ortho** | 不改 SF1–3 文案墙；不改 D301 招架；不改身法/烬突 CD |
| **G-id** | 效用可读作「形状承诺」；禁读作「永久更痛」 |
| **G-hold** | T0 ✅ **≠** 开工；须总控显式开工 tip 才改 CoreRpg |

**FAIL → 停**；禁放宽硬线；禁无建模宣称过线。

---

## 4. W 切分

| 窗 | 做什么 | 产出 |
|----|--------|------|
| **本窗 stage0** | RESEARCH + 本 DESIGN + tip · power-paths 戳 | docs-only |
| **T0** | p1sim 登记 SF4 cand（主荐 U1）· within 门禁 | STATUS ✅/❌ |
| **T1** | 仅 T0 ✅ + 显式开工：CoreRpg 接线 + 可选 TrMenu 一句 | jar |
| **T2** | 单测 + 轻冒烟 | 测报 |

---

## 5. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md` |
| Stage2 上游 | `DESIGN-ember-skill-feel-stage2-micro-stage0-2026-10-11.md` |
| TIP-Y | `RESEARCH-ember-skill-feel-post-k-next-2026-10-12.md` |
| 形状 SoT | SkillService / EmberSetRules（本窗只读） |
| 招架正交 | `EmberParry` · D301（本窗不动） |
| 模拟 | `tools/p1sim/`（T0 另开；本窗不跑） |
| 旁轨 HOLD | L13 · Anvil soft densify · soft 展示再叠 · 灰印/聚火 |

---

## 6. 旁债（下一 prefer · 非本窗）

1. SF4 T0 p1sim（须本 stage0 批后另窗）  
2. 灰印 / 聚火重设计（另 tip）  
3. AnvilRepair Soft 假耗（硬禁 densify 未解冻前不开）  
4. L13 施工 HOLD 观察  

## STATUS: OPEN · TIP-BE stage0 · HOLD 施工 · 2026-10-12 23:04 Asia/Shanghai
