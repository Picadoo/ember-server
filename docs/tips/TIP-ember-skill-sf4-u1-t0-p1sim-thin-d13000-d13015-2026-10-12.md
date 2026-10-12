# tip · TIP-BF 烬斩 SF4 U1 微缓 T0 离线 p1sim（G-within · docs+tools）· D13000–D13015

> **状态：** OPEN · 待批 **A · 方案 M** · docs+tools · **sim `STAGE0_SF4_U1_T0=PASS`** · HOLD 施工 · PASS≠开工 · 2026-10-12 23:14 Asia/Shanghai  
> **上游：** TIP-BE CLOSED stage0 APPROVED HOLD push `02db68148`（OPEN `6553fd1b`）· DESIGN [`DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md`](../design/DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md) · stage0 [`DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md`](../design/DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md) · RESEARCH [`RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md`](../design/RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md) · 范式 TIP-AI / D438 / guard-parry T0  
> **结果落点：** [`RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md`](../design/RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md)  
> **选题：** 总控点名 **另窗 T0 p1sim（U1 cand · G-within）**；离开 soft/开孔；**勿开 Anvil densify / L13 施工**  
> **施工岗：** **可 docs+tools**（p1sim harness + 离线 `kit_sf4_*` 钩）；**零业务 jar / 零插件 YAML / 零改 skill_mult**；**不**派插件  
> **硬禁：** **零抬 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零 glance twin** · **零 ForgeChoice densify · 零 sx20 / 粒子 densify** · **零改 PARRY_CD/flat · 零 Anvil densify · 零 topUpTo · 零开 L13 施工 · 零再扫甲闸 · 零扩 stampable · 零 SetChase 六槽 · 零 soft-latch·CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠 · 零 xp-boost · 零 GemHole/enhance+5 再叠 · 零灰印/聚火夹带 · 零 ConvertCap densify**

---

## 0. 一句话

TIP-BE 后开 **SF4 U1 微缓 T0 离线门禁**：钉 H-U1 cand → 跑 G-within → 填 RESULT；**过线 ≠ 开工**。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **SF4 U1 T0 p1sim（G-within）** | **采纳** | 总控点名；接 TIP-BE HOLD；docs+tools；零 skill_mult |
| 当场 CoreRpg 接线 / 改几何 / 抬 skill_mult | **否** | PASS≠开工；硬禁 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| U2/U3 同窗打包 | **否** | stage0：单 cand；U1 失败再换 |
| soft/开孔/xp-boost/GemHole 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火夹带 | **否** | 旁债 |
| Affix / 伤乘 / 抬表 / sx20 / 甲闸再扫 / 扩 stampable | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐 · docs+tools）

| ID | 项 | 验收 |
|----|-----|------|
| D13000 | 本 tip + DESIGN T0 + RESULT 路径 | 路径可点 |
| D13001 | power-paths 进度戳：**TIP-BE CLOSED**；**TIP-BF OPEN**（T0 p1sim · docs+tools）；旁注 **L13 施工 HOLD** | 戳可见 |
| D13002 | 钉死假设 **H-U1**（微缓短窗 · 套装无关 · 自然 ICD=烬斩充能） | DESIGN §2 |
| D13003 | 主荐 cand **U1_sl10**（mark 1.0 / slow 0.12）；备 **U1_sl08** | DESIGN §2.2 |
| D13004 | 门禁表 G-within / G-prefer / G-x / G-ortho / G-id / G-hold | DESIGN §3 |
| D13005 | 离线键 `kit_sf4_mark_secs` / `kit_sf4_slow` 入 `p1sim.kit_slash_flavor`（零 Java） | 可核 |
| D13006 | 薄 harness `tools/p1sim/sf4_u1_gate_t0.py`（checklist/cmds/run/report/judge） | 零 jar |
| D13007 | 精确命令包：skillkit 格网 n=1500 · 三套装 | DESIGN §3.1 |
| D13008 | 跑 T0 → `out-sf4-u1-t0-p1sim.md` + RESULT **PASS**（U1_sl10 三族 ✅） | 可复现 |
| D13009 | G-x 清单勾选（无 skill_mult/伤乘/Affix/招架/CD/几何） | DESIGN §3.2 |
| D13010 | FAIL=停；禁放宽硬线；禁抬 skill_mult 凑绿 | 文内硬句 |
| D13011 | PASS≠开工；过线后仍须总控显式开工才开施工 tip | 文内硬句 |
| D13012 | HOLD：本 tip **不**派插件；零业务 jar；L13 / Anvil densify / soft 再叠仍冻 | 无 jar |
| D13013 | stage0 DESIGN 旁注：T0 已开窗；仍 HOLD 施工 | 可核 |
| D13014 | NON-goals：无 CoreRpg/TrMenu/p1map 业务；无 U2/U3；无灰印/聚火；无 Anvil/L13 | 可核 |
| D13015 | 缓冲：错链/错戳/RESULT 表头/跑数未齐则 PENDING；禁借号开工或 Anvil densify | 超额新 tip |

### 玩家口径（固定）

- 本窗 **不改** 游戏内任何技能数字、CD、命中盒。  
- 只做 **离线模拟考试**：烬斩打中后短缓是否会把通关率拉歪。  
- 考试过线 **≠** 已经改游戏；还要总控说「开工」。  
- 招架照旧；强化/开孔文案链已齐，本窗不重做。

---

## 3. 对接文件

| 层 | 路径 |
|----|------|
| DESIGN T0 | `docs/design/DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md` |
| RESULT | `docs/design/RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md` |
| 报告 | `docs/design/out-sf4-u1-t0-p1sim.md` |
| harness | `tools/p1sim/sf4_u1_gate_t0.py` |
| stage0 | TIP-BE DESIGN / RESEARCH |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · soft 展示再叠 |

---

## 4. 明确不做

- 任何 CoreRpg / TrMenu / p1map / Economy 业务改码或数值表  
- 抬 skill_mult · 改 CD · 改 FAN/LINE/RING 命中盒 · Affix · 伤乘  
- TIP-K cue twin · 粒子 densify · glance twin · sx20 · ForgeChoice densify  
- 改招架 · 灰印/聚火夹带 · Anvil densify · topUpTo · soft/开孔再叠  
- 开 L13 施工 tip · 扩 stampable · 甲闸再扫 · SetChase 六槽  
- 把本 tip / PASS 当成开工令；同窗打包 U2/U3

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [x] DESIGN + tip + RESULT 路径可点；H-U1 / cand 可核  
- [x] power-paths：TIP-BE CLOSED + TIP-BF OPEN；L13 HOLD  
- [x] harness checklist/cmds；离线 `kit_sf4_*` 钩  
- [x] T0 跑数入库 · `STAGE0_SF4_U1_T0=PASS`  
- [x] **无**插件施工派单；**无**业务 jar；**无** skill_mult 改动  
- [x] `git show` 仅 docs + tools/p1sim 薄改  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-BE 后 · **SF4 U1 T0 离线 p1sim（G-within）**（docs+tools；零 skill_mult；PASS≠开工）  
- **批注：** H-U1 主荐 U1_sl10；形状偏好施工层；离线代理 kit_sf4_*；FAIL 停；PASS 仍 HOLD 至显式开工；不派插件；L13 施工 HOLD；Anvil densify / topUpTo / soft·开孔再叠 / 灰印·聚火 / Affix / 伤乘 / 抬表 / glance twin / sx20 / 粒子 densify / ForgeChoice densify / 甲闸再扫 / 扩 stampable / SetChase 六槽 densify 仍冻  

## STATUS: OPEN · 待批 A·M · sim PASS · 2026-10-12 23:14 CST · docs+tools · 零 skill_mult · 零业务 jar · HOLD 施工 · PASS≠开工
