# DESIGN · 烬斩 SF4 U1 微缓 T0 离线 p1sim（G-within）· 2026-10-12

> **状态：** APPROVED · TIP-BF CLOSED · docs+tools · **sim PASS** · **HOLD 施工 · PASS≠开工** · 总控 2026-10-12 23:15 Asia/Shanghai  
> **文首硬句：** 本文件 = **T0 离线门禁协议 + 可跑包**；**PASS ≠ 开工**。须总控 **显式开工** tip 才改 CoreRpg。**零改 skill_mult / CD / 几何盒 · 零业务 jar · 零插件派单** 直至显式开工。  
> **对接 tip：** [`TIP-ember-skill-sf4-u1-t0-p1sim-thin-d13000-d13015-2026-10-12.md`](../tips/TIP-ember-skill-sf4-u1-t0-p1sim-thin-d13000-d13015-2026-10-12.md)  
> **上游：** TIP-BE CLOSED stage0 APPROVED HOLD push `02db68148` · DESIGN [`DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md`](DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md) · RESEARCH [`RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md`](RESEARCH-ember-skill-sf4-micro-utility-2026-10-12.md)  
> **结果落点：** [`RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md`](RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md)  
> **旁注 HOLD：** **L13 施工 HOLD**（TIP-AI FAIL）；Anvil soft densify / topUpTo **仍冻**；灰印/聚火旁债；soft/开孔文案勿再叠  
> **硬禁：** 零 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零 glance twin · 零 sx20 · 零粒子 densify · 零 ForgeChoice densify · 零改 PARRY_CD/flat · 零 Anvil densify · 零 topUpTo · 零开 L13 施工 · 零再扫甲闸 · 零扩 stampable · 零 SetChase 六槽 · 零 soft 再叠 · 零 GemHole/enhance+5 再叠 · 零灰印/聚火夹带

---

## 0. 一句话

把 TIP-BE 已批的 **U1 微缓** 钉成可跑 T0 离线门禁（G-within）；跑 skillkit 格网；**过线只解锁「可报显式开工」**，本窗 **不**改 live 技能。

---

## 1. 分阶

| 阶 | 内容 | 本窗 |
|----|------|------|
| 0 · 规格 | TIP-BE DESIGN stage0 APPROVED · U1 主荐 | **已 CLOSED** |
| **T0 · 离线门禁** | 钉 cand + 跑 G-within；docs+tools；禁业务 jar | **本窗** |
| T1 · 施工 | 仅 T0 ✅ **且** 显式开工：CoreRpg 接线 | **TIP-BG OPEN**（显式开工令） |

---

## 2. 候选钉死（H-U1）

### 2.1 假设

**H-U1：** 烬斩命中后短窗 **微缓**（非伤乘）；自然 ICD = 烬斩充能（每充能至多一次）；**套装无关**；形状偏好（LINE / 套装推荐）属 **施工接线层**——本离线代理 = `kit_sf4_*` 开即在命中时生效（禁常驻光环）。

### 2.2 参数表（终表候选 · 本窗可测）

| ID | mark_secs | slow | 角色 | 备注 |
|----|-----------|------|------|------|
| **U1_sl10（主荐）** | **1.0** | **0.12** | 主 cand | 对齐 DESIGN「≤1.0s」+ D438 S_sl10 量级 |
| U1_sl08 | 0.8 | 0.12 | 收窄备 | 主荐越线时降幅再测 |

**禁：** 拧 `skill_mult` / 1.5B / CD / FAN·LINE·RING 命中盒；打包 U2/U3 同窗；永久缓光环。

### 2.3 离线模型键（tools-only）

| 键 | 语义 |
|----|------|
| `kit_sf4_mark_secs` | 命中后标记持续秒 |
| `kit_sf4_slow` | 标记期内每次攻击间隔加量（Slow-I 代理 · 同 ash_slow） |

落点：`tools/p1sim/p1sim.py` → `kit_slash_flavor`（**不**改 Java / **不**改 `kit_slash_*` 套装落点路径）。

---

## 3. 门禁表（PASS≠开工）

| 门 | 要求 | 工具 |
|----|------|------|
| **G-within** | 三套装 × 可达格：硬线 mean\|Δ\|≤1 **且** 无格 \|Δ\|>3；目标全格 \|Δ\|≤2（skill-kit D174） | `sf4_u1_gate_t0.py` + skillkit 格网 |
| **G-prefer** | 乱按不净赚通关；主判 always-on cand（非仅择优行） | report 主行 |
| **G-x** | 无 skill_mult / 伤乘 / Affix / 抬表 / 改招架 / 改 CD·几何 | 清单 |
| **G-ortho** | 不改 SF1–3 文案墙；不改 D301；不改身法/烬突 | 书面 |
| **G-id** | 可读「形状短窗微缓」；禁读「永久更痛」 | 书面 |
| **G-hold** | T0 ✅ ≠ 开工；须显式开工 tip | 文内硬句 |

**FAIL → 停**；禁放宽硬线；禁抬 skill_mult 凑绿。

### 3.1 精确命令

```bash
python3 tools/p1sim/sf4_u1_gate_t0.py checklist
mkdir -p /tmp/sk-sf4-u1 && cd tools/p1sim
python3 sf4_u1_gate_t0.py run 1500 /tmp/sk-sf4-u1/t0.pkl
python3 sf4_u1_gate_t0.py report /tmp/sk-sf4-u1/t0.pkl \
  > ../../docs/design/out-sf4-u1-t0-p1sim.md
python3 sf4_u1_gate_t0.py judge ../../docs/design/out-sf4-u1-t0-p1sim.md
```

### 3.2 G-x 清单（书面 · 必勾）

- [x] 候选 **未**新增伤害乘区 / **未**改 `skill_mult`  
- [x] 候选 **未**改烬斩 CD / 1.5B / FAN·LINE·RING 命中盒  
- [x] 候选 **未**改招架 / 身法 / 烬突 / 灰印 / 聚火  
- [x] 候选 **未** Affix densify / 抬表 / glance twin / sx20 / 粒子 densify  
- [x] 本窗 **零**业务 jar / **零**插件派单  

---

## 4. W 切分（相对 stage0）

| 窗 | 做什么 | 产出 |
|----|--------|------|
| stage0（TIP-BE） | 身份核 + 门禁草案 | CLOSED |
| **T0（本窗）** | cand + 跑 G-within + RESULT | docs+tools |
| T1 | 显式开工后 CoreRpg 接线 | **TIP-BG OPEN** |

---

## 5. 对接

| 层 | 路径 |
|----|------|
| stage0 | `DESIGN-ember-skill-sf4-micro-utility-stage0-2026-10-12.md` |
| harness | `tools/p1sim/sf4_u1_gate_t0.py` |
| 格网 | `tools/p1sim/skillkit.py`（复用 · 不改 skill_mult） |
| 模型 | `tools/p1sim/p1sim.py` `kit_sf4_*` |
| 范式 | tip-AI L13 stage1 · guard-parry T0 · D438 slash |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 |

## STATUS: APPROVED · TIP-BF CLOSED T0 · sim PASS · HOLD 施工 · PASS≠开工 · 总控 2026-10-12 23:15 Asia/Shanghai · 不派插件
