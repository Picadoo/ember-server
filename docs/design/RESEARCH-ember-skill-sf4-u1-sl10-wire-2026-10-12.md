# RESEARCH · 烬斩 SF4 U1_sl10 显式开工接线（薄 · 只读路径）· 2026-10-12

> **状态：** OPEN · TIP-BG 前置 · 2026-10-12 23:16 Asia/Shanghai  
> **起因：** TIP-BF CLOSED · `STAGE0_SF4_U1_T0=PASS` · push `9ab79c257`（OPEN `98b797459`）；总控可开「显式开工」施工 tip（U1_sl10 · 禁 skill_mult）。本窗只盘 **真类路径**，不发明表。  
> **上游：** TIP-BE stage0 APPROVED HOLD `02db68148` · TIP-BF RESULT PASS · DESIGN T0 / stage0 / RESEARCH SF4  
> **硬禁：** 零 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零改 CD / FAN·LINE·RING 命中盒 · 零 Anvil densify · 零 L13 施工 · 零 soft/开孔再叠 · 零灰印/聚火夹带

---

## 0. 一句话

门禁已过；开工只接 **形状短窗 Slow I**，落点在现网 `landEmberSlash` 后置钩，参数锁 U1_sl10。

---

## 1. 门禁继承（只读）

| ID | 结果 | 链 |
|----|------|-----|
| TIP-BE stage0 | ✅ APPROVED · U1 主荐 · HOLD 施工 | `DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md` |
| TIP-BF T0 | ✅ `STAGE0_SF4_U1_T0=PASS` · U1_sl10 三族 | `RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md` |
| 守招 D301 | ✅ LIVE 正交 | `EmberParry` · 本窗不动 |
| D438 slash 套装落点 | ✅ 正交 | `applySlashSetIdentity` · 本窗 **不改** burst Slow / sustain heal / scorch 粒子 |
| L13 / Anvil densify | ❌ 仍冻 | — |

---

## 2. 现网只读路径（施工落点）

| # | 事实 | 路径 | 开工含义 |
|---|------|------|----------|
| E1 | 烬斩落地唯一入口 | `SkillService.landEmberSlash(...)` | **后置钩** SF4（命中列表已齐 · shapeId 已知） |
| E2 | 形状 id / 名 / 偏好 | `EmberSkillKit.shapeId` · `SHAPE_LINE=1` · `preferredShape(family)` · `setShapeSynced(fam, shapeId)` | 形状门 = 套装推荐形；无套装 → LINE |
| E3 | SF1–3 展示墙 | `EmberSkillFeelSf` · `maybeFlashSlashHit` | **只读**；SF4 成功可 **一行** AB，禁 cue 墙 / TIP-K twin |
| E4 | D438 burst 已施 Slow I 1.0s | `applySlashSetIdentity` → `slashLandSlowTicks` · `SLASH_BURST_SLOW_TICKS=20` | **正交不动**；SF4 同 amp 刷新时长可接受，禁抬 amp / 禁改 D438 表 |
| E5 | 蓄力落地另有 Slow | `landEmberSlash` charged 分支 · `chargeLandSlowTicks` | **正交**；SF4 不绑蓄力 |
| E6 | 契约灰印 AshMark | `SkillService.AshMark` / `castAshMark` | **禁夹带**；SF4 ≠ 灰印 pct 伤 |
| E7 | 套装规则只读 | `EmberSetRules` / `EmberLoadoutService.activeSet` | 取 family 判 `setShapeSynced` |
| E8 | 离线代理键 | `kit_sf4_mark_secs` / `kit_sf4_slow`（tools-only） | live **不**读这些键；终表 = U1_sl10 → **Slow I · 20t** |

---

## 3. 参数锁（对齐 RESULT）

| 项 | 值 | 注 |
|----|-----|-----|
| cand | **U1_sl10** | 主荐；U1_sl08 本窗不施工 |
| mark | **1.0 s = 20 ticks** | = `SLASH_BURST_SLOW_TICKS` 量级；非抬表 |
| slow | **Slow I · amp 0** | 对齐离线 `kit_sf4_slow=0.12` 代理 |
| ICD | 每次烬斩充能至多一次（本击落地） | 自然 ICD；**不**新 CD 表；**不**共享招架/身法 CD |
| 形状门 | `setShapeSynced(fam, shapeId)`；fam 空/none → `shapeId==SHAPE_LINE` | stage0「LINE / 套装推荐」 |
| 目标 | 本击 `hit` 列表前 lim≤3（优先首领/精英若现网可分类，否则全命中前 lim） | 空挥无效用 |
| 禁 | skill_mult / 1.5B / CD / 几何盒 / Affix / 伤乘 / 灰印 pct / 聚火 | 硬禁 |

---

## 4. 荐类切分（给 DESIGN / 插件）

| 层 | 荐 | 不做 |
|----|-----|------|
| 新薄 util | `town.sunshine.corerpg.p1.EmberSkillSf4`（常量 + `maybeApply` 纯逻辑） | 不塞进 `EmberSkillFeelSf`（展示族） |
| 接线 | `SkillService.landEmberSlash` 在 `applySlashSetIdentity` 之后、`maybeFlashSlashHit` 之前调 `EmberSkillSf4.maybeApply(...)` | 不改蓄力分支 / 烬突 |
| 单测 | `EmberSkillSf4Test`：形状门 · 空 hit · ticks/amp · 禁改 kit 几何 | 不改 `EmberSkillKitTest` 几何断言 |
| 展示 | 可选一行 AB「烬斩·形缓」；失败静默 | 禁 TIP-K twin / 粒子 densify |

---

## 5. 本 RESEARCH 验收

- [x] 引用 BE PASS 规格 + BF T0 PASS  
- [x] 真类路径可点（SkillService / EmberSkillKit / EmberSkillFeelSf）  
- [x] 参数锁 U1_sl10；硬禁清单  
- [x] **零**业务 jar 指引外的数值发明  
