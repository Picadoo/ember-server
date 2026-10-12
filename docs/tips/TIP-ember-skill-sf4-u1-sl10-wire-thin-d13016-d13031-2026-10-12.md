# tip · TIP-BG 烬斩 SF4 U1_sl10 显式开工接线（可施工薄 · CoreRpg）· D13016–D13031

> **状态：** APPROVED · 批 **A · 方案 M** · **显式开工令** · 已派 **余烬-插件** · 2026-10-12 23:18 Asia/Shanghai  
> **上游门禁：** TIP-BF CLOSED · `STAGE0_SF4_U1_T0=PASS` · push `9ab79c257`（OPEN `98b797459`）· RESULT [`RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md`](../design/RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md) · TIP-BE CLOSED stage0 APPROVED HOLD push `02db68148` · DESIGN [`DESIGN-ember-skill-sf4-u1-sl10-wire-2026-10-12.md`](../design/DESIGN-ember-skill-sf4-u1-sl10-wire-2026-10-12.md) · RESEARCH [`RESEARCH-ember-skill-sf4-u1-sl10-wire-2026-10-12.md`](../design/RESEARCH-ember-skill-sf4-u1-sl10-wire-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **开工令引用：** 总控「下一可开『显式开工』施工 tip（U1_sl10 量级接线 · 禁 skill_mult / CD / 几何）」——**本 tip 即该令**；BF PASS **不是**开工令。  
> **选题：** 门禁已齐 → **CoreRpg 接线 U1_sl10 形状短窗微缓**（**非 densify · 非 skill_mult · 非 Anvil · 非 L13**）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · 新 `p1/EmberSkillSf4` · `SkillService.landEmberSlash` 后置钩 · 单测 `EmberSkillSf4Test`；零改 skill_mult/CD/几何/`slashLandSlowTicks`/招架/灰印）；**不**派余烬-物品（无 TrMenu 必改）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零抬 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify · 零 sx20 · 零粒子 densify** · **零改烬斩/身法/招架 CD · 零改 FAN·LINE·RING 命中盒** · **零改 PARRY_CD/flat** · **零 Anvil densify · 零 topUpTo · 零开 L13 施工 tip · 零再扫甲闸 · 零扩 stampable · 零 SetChase 六槽 densify · 零 soft-latch·CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠 · 零 xp-boost · 零 GemHole/enhance+5 再叠 · 零灰印/聚火夹带 · 零 ConvertCap densify · 零打包 U2/U3**

---

## 0. 一句话

BF PASS 后显式开工：烬斩在推荐形状（或无套装时直线）命中时挂 **Slow I · 1s**；不抬伤、不改 CD/几何。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **SF4 U1_sl10 CoreRpg 显式开工接线** | **采纳** | 总控点名；BE+BF 门禁已清；本 tip=开工令 |
| 把 BF PASS 当已开工 / 不写 tip 直接改 jar | **否** | PASS≠开工；须本显式令 |
| 抬 skill_mult / 改 CD / 改几何盒凑「更痛」 | **否** | 硬禁 |
| 同窗打包 U2 微疗 / U3 微抗 | **否** | stage0 单 cand |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| soft/开孔/xp-boost/GemHole 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火夹带 | **否** | 旁债 |
| Affix / 伤乘 / 抬表 / sx20 / 甲闸再扫 / 扩 stampable | **否** | 硬禁 |
| 观察停窗 only | **否** | 开工令已点接线 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后插件）

| ID | 项 | 验收 |
|----|-----|------|
| D13016 | 本 tip + RESEARCH 薄 + DESIGN 开工（上文链） | 路径可点 |
| D13017 | power-paths 进度戳：**TIP-BF CLOSED** PASS；**TIP-BG OPEN** 显式开工；旁注 **L13 施工 HOLD** | 戳可见 |
| D13018 | 文首钉死：本 tip=显式开工令；BF PASS≠开工 | 可核 |
| D13019 | 门禁引用：BE stage0 APPROVED + BF `STAGE0_SF4_U1_T0=PASS` · U1_sl10 | 可核 |
| D13020 | 参数锁：MARK_TICKS=20 · SLOW_AMP=0 · HIT_LIM=3 · cand=U1_sl10 | DESIGN §2 |
| D13021 | 形状门：`setShapeSynced`；fam 空/none → `SHAPE_LINE` | DESIGN §3.1 |
| D13022 | **插件：** 新 `EmberSkillSf4`（常量 + `maybeApply`） | 代码+单测 |
| D13023 | **插件：** `SkillService.landEmberSlash` 在 `applySlashSetIdentity` 后、`maybeFlashSlashHit` 前接线 | 可核 |
| D13024 | **插件：** 成功可选一行 AB「烬斩·形缓」；禁 TIP-K twin / 粒子 densify | 可核 |
| D13025 | **插件：** 单测最小包（DESIGN §4） | PASS |
| D13026 | ICD=本击自然拍；**不**新 CD 表；**不**共享招架/身法 CD | 可核 |
| D13027 | D438 / 蓄力 Slow / 烬突 / 招架 / 灰印：**本窗不改** | 可核 |
| D13028 | NON 核：`git show` 无 skill_mult/CD/几何/Affix/伤乘/STAMP/L11/L13/PARRY/Anvil/soft/开孔 | PASS |
| D13029 | HOLD：L13 施工；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；Affix/伤乘/抬表/glance twin/sx20/粒子 densify/ForgeChoice densify/甲闸再扫/扩 stampable/SetChase 六槽 | 文内硬句 |
| D13030 | 批后派单句：**余烬-插件**；策划 docs-only；**不**派物品 | 派单句 |
| D13031 | 缓冲：错链/错戳/误改 D438；禁借号抬 skill_mult/改几何/开 L13/Anvil densify/打包 U2U3 | 超额新 tip |

### 玩家口径（固定）

- 套装推荐形状命中 → 约 1 秒缓速 I；无套装时直线穿刺才有。  
- 空挥无效；伤害/充能/招架不变。  
- 开孔/强化文案、招架、Anvil 本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-skill-sf4-u1-sl10-wire-2026-10-12.md` |
| DESIGN 开工 | `docs/design/DESIGN-ember-skill-sf4-u1-sl10-wire-2026-10-12.md` |
| stage0 / T0 / RESULT | TIP-BE / TIP-BF 链 |
| 接线 | `CoreRpg/src/main/java/town/sunshine/corerpg/SkillService.java` |
| 形状 SoT | `CoreRpg/src/main/java/town/sunshine/corerpg/p1/EmberSkillKit.java` |
| 新 util | `CoreRpg/src/main/java/town/sunshine/corerpg/p1/EmberSkillSf4.java`（批后建） |
| 展示只读 | `EmberSkillFeelSf` |
| D438 只读 | `applySlashSetIdentity` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · soft/开孔再叠 |

---

## 4. 明确不做

- 抬 skill_mult · 改 1.5B · 改烬斩/身法/招架 CD · 改 FAN/LINE/RING 命中盒  
- Affix densify · 伤乘 · 抬表 · glance twin · sx20 · 粒子 densify · ForgeChoice densify  
- 改 D438 `slashLandSlowTicks` / 蓄力 Slow / 烬突 / `EmberParry` / 灰印 pct  
- Anvil densify · topUpTo · soft/开孔/xp-boost/GemHole 再叠 · 灰印/聚火夹带  
- 开 L13 施工 tip · 扩 stampable · 甲闸再扫 · SetChase 六槽  
- 同窗 U2/U3 · 把 BF PASS 当成已授权改 jar（须本 tip 批 A·M）

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] DESIGN + tip + RESEARCH + STATUS + power-paths 戳可点  
- [x] 文首显式开工令；BF PASS≠开工 可核  
- [ ] 批后 **余烬-插件**：D13022–D13025；D13028 `git show` NON 核  
- [x] 策划本窗 commit **docs-only**；**无**业务 jar  
- [x] L13 / Anvil densify / soft·开孔再叠 / 灰印·聚火 仍 HOLD  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 已批 · 已派余烬-插件  
- 选题：TIP-BF PASS 后 · **SF4 U1_sl10 显式开工接线**（CoreRpg · 禁 skill_mult/CD/几何）  
- **批注：** 门禁 BE+BF 已清；参数锁 U1_sl10（20t · Slow I · lim3）；形状门=套装推荐/无套装 LINE；批后派 **余烬-插件**；不派物品；L13 施工 HOLD；Anvil densify / topUpTo / soft·开孔再叠 / 灰印·聚火 / Affix / 伤乘 / 抬表 / glance twin / sx20 / 粒子 densify / ForgeChoice densify / 甲闸再扫 / 扩 stampable / SetChase 六槽 densify 仍冻  

## STATUS: APPROVED · 显式开工 · 批 A·M · 已派余烬-插件 · HOLD L13 · 总控 2026-10-12 23:18 Asia/Shanghai
