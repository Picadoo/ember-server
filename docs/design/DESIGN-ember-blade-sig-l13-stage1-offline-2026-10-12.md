# DESIGN · 刃签 L13 stage1 离线门禁（R1+R2 假设 · docs+tools）· 2026-10-12

> **状态：** CLOSED · TIP-AI 批 A·M · `STAGE1_L13_OFFLINE=FAIL`（基线）· **HOLD 施工** · 2026-10-12 19:56 Asia/Shanghai  
> **文首硬句：** 本窗 **只** 钉评测假设 + 可执行门禁包；**不是**开工令。**零改** `DEFS` / `SIGS` / SkillService / 业务 jar。FAIL→**停**、**禁抬表**；PASS ≠ 开工（仍须总控显式开工才开施工 tip）。  
> **对接 tip：** [`TIP-ember-blade-sig-l13-stage1-offline-thin-d12632-d12647-2026-10-12.md`](../tips/TIP-ember-blade-sig-l13-stage1-offline-thin-d12632-d12647-2026-10-12.md)  
> **上游：** TIP-AG CLOSED DESIGN stage0 APPROVED HOLD `7d61761e4` · [`DESIGN-ember-blade-sig-l13-charge-identity-stage0-2026-10-12.md`](DESIGN-ember-blade-sig-l13-charge-identity-stage0-2026-10-12.md) · TIP-AH CLOSED live `1.65.774-d12631` · RESEARCH [`RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md`](RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md) · 范式 [`DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md)  
> **结果落点：** [`RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md`](RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md)（本窗 PENDING 空壳）  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · **零 skill_mult 再拧** · 零 STAMP 抬 · 零 ForgeChoice·wear·pity·awkDelta densify · **零改 L13/L11 mods** · **零业务 jar** · 不绑防具 stampable / 不回改 TIP-AE · TIP-AD exclude **保留**

---

## 0. 一句话

把 stage0 已批的 L13 蓄力身份规格，钉成 **单一评测假设（R1+R2）** + **可执行离线门禁包**（G-alone / G-pair / G-W30 / G-X / G-split / G-id）；复用 `mainline.py` + 薄 harness；本窗 **不改** live SIGS 数、**不派**施工。

---

## 1. 分阶与本窗边界

| 阶 | 内容 | 本窗 |
|----|------|------|
| 0 · 规格 | stage0 APPROVED · R1–R5 授权集 · 门禁表草案 | **已 CLOSED**（TIP-AG `7d61761e4`） |
| **1 · 离线门禁** | **钉假设 + 跑/可跑门禁包**；docs+tools；禁业务 jar；禁改 L13 mods | **本窗** |
| 2 · 施工 tip | 仅门禁 PASS **且** 总控显式开工后：单主题改 L13 行为 | **另 tip**；本窗 **不是**开工令 |

**继承：** stage0 §2.2 身份核（blade · 环/不必对准 · 行为型禁伤乘）与 §3 门禁精神 **全部有效**；本窗只 **钉死评测方向** 并写成可执行包。

---

## 2. 钉死候选方向（stage1 评测假设）

### 2.1 裁决

| 方向 | 裁决 | 理由 |
|------|------|------|
| **R1 蓄力窗结构 + R2 蓄力中输入** | **钉死（本窗假设）** | 对准 `skill_charge` 死窗方差（RESEARCH §3.2）；保留「蓄力重环」身份核与 L09 差分；**不**需 skill_mult/抬表；可写成离线门禁而不改 live SIGS |
| R3 几何采样时机 | 本窗不单钉 | 可作施工细项；不单独改 alone 主方差叙事 |
| R4 与 L09 差分 | **继承约束**（非本窗主假设） | 假设必须保持 L09=轻瞬时环 / L13=蓄力重环；评测勾 G-id |
| R5 全量 resignature（非 charge 代价集） | **HOLD 升级轨** | RESEARCH 路径 D 对症但高成本；历史缩短 charge（0.25–0.45）已见上偏 → 先评 R1+R2 结构重设；**G-alone FAIL 且结构空间穷尽** 后再开 R5 窗 |
| 拧 skill_mult / 抬表 / Affix | **否** | 硬禁 |

**一句话假设（H-R12）：**  
炉锁巨锤仍是 **蓄力重环**；stage1 评测的「若施工」目标是重写 **如何付出蓄力代价**——**R1**（窗结构：缩短/分段有效死窗、CD 改释放起算、可取消不扣满代价）+ **R2**（蓄力中弱普攻或可取消回普攻）——**不是**把 `skill_charge` 小数拧绿，也 **不是**加 skill_mult。

### 2.2 与 RESEARCH / 历史扫的对齐

| 源 | 对齐 |
|----|------|
| RESEARCH 路径 C | 「L13 蓄力时长/蓄力中可取消」→ 本窗 R1+R2 |
| RESEARCH 路径 D | 全量换签 = FAIL 后升级；本窗不评 |
| mainline `SIGS['L13']` 注 | charge 0.25–0.45 曾致部分格 **上偏** → **禁止**「只降 charge 凑 alone 绿」当唯一出路 |
| TIP-AC/AD | L13 alone 三族大负已证实；甲门禁 **exclude 保留** |

### 2.3 明确不在假设内

- 改 `skill_mult`（L13 本无；禁止新加）  
- 抬 STAMP / Affix / 永久伤乘 / glance  
- 删环核或做成第二把 L09  
- 本窗改 L11；扩甲 stampable；回改 TIP-AE  
- 把 TIP-AD exclude 说成「签已删」

---

## 3. 门禁表（解冻 L13 施工才启用 · 本窗可跑包）

> 任一门 FAIL → **停**；**禁抬表凑线**。PASS ≠ 自动开工。

| 门 | 要求 | 本窗工具 |
|----|------|----------|
| **G-alone** | `mainline.py tol`：L13 **alone** · 三族可达 · D174 硬线（mean\|Δ\|≤1 且 **无格超 ±3 pp**） | `tol` + harness `judge-alone` |
| **G-pair** | 本窗默认：**仍 exclude 出甲门禁**（继承 TIP-AD）；若总控改口重纳入，则含 L13 的合法 pairs 可达不过线 | 书面声明 + 可选 `pairs` |
| **G-W30** | 若候选动代价/收益/蓄力结构：至少一条代表佩戴路线 \|ΔW30\|≤0.5（禁只靠抬掉落/STAMP） | `mainline.py econ` |
| **G-X** | 清单：无新伤乘 · 无 skill_mult 再拧 · 无 Affix densify · 无改甲 stampable · 无 sx20/深化 densify · 无 STAMP 抬 | 书面勾选 |
| **G-split** | 刃轨 ≠ 甲轨；不回滚 TIP-AE；**L11 仍 HOLD 另轨**；TIP-AD exclude 保留 | 书面 |
| **G-id** | 对照 stage0 §2.2：环/不必对准核仍在；与 L09 差分可核（蓄力重环 vs 瞬时轻环） | 书面 |
| **G-charge**（本窗加） | 叙事/结构对准 **charge 死窗方差**；候选说明不得以「永久伤乘补偿」消负偏 | 书面 + RESULT 注 |

### 3.1 基线（已知 · 不重开争论）

| 项 | 状态 |
|----|------|
| Live L13 alone（TIP-AC） | 三族可达 **FAIL**（burst −8.2 / scorch −9.8 / sustain −13.8） |
| 污染源 | 刃 alone · `skill_charge` 方差（非甲升格） |
| TIP-AD | 甲评测集 exclude L11/L13 · **本窗保留** |

本窗 RESULT 须先勾 **BASELINE_L13_ALONE=FAIL（继承）**；再填候选跑数（若跑）或保持 PENDING。

### 3.2 精确命令（既有工具 · 禁改 live SIGS）

工作目录：仓库根或 `tools/p1sim`。

```bash
# 0) 门禁口诀 + 基线 judge（薄 harness · 零 jar · 零改 SIGS）
python3 tools/p1sim/blade_sig_l13_gate_s1.py checklist
python3 tools/p1sim/blade_sig_l13_gate_s1.py cmds
python3 tools/p1sim/blade_sig_l13_gate_s1.py judge-alone

# 1) L13 焦点：全表 tol 后只判 L13 alone（可复用既有 out）
mkdir -p /tmp/ml-blade-l13
cd tools/p1sim
python3 mainline.py tol    2000 /tmp/ml-blade-l13/tol.pkl
python3 mainline.py report /tmp/ml-blade-l13/tol.pkl \
  > ../../docs/design/out-blade-l13-stage1-tol.md
python3 blade_sig_l13_gate_s1.py judge-alone \
  --tol ../../docs/design/out-blade-l13-stage1-tol.md

# 2) G-W30（仅当候选已建模且总控要求本窗跑；否则 RESULT 记 SKIP）
#    BASE：无 L13；CAND：含 L13 代表路线（例 L13+L02）— 勿新造 mods
python3 mainline.py econ '["L01","L02"]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \
  > ../../docs/design/out-blade-l13-stage1-w30-base-ref.md
python3 mainline.py econ '["L13","L02"]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \
  > ../../docs/design/out-blade-l13-stage1-w30-cand-L13L02.md
```

**硬句：** harness / 命令包 **不得** 写入或改写 `mainline.py` 内 `SIGS['L13']` 数值。任何「缩短 charge」类实验只允许在 **另开** 且总控批准的 **影子评测窗**（仍禁 live 提交）；本 TIP-AI 开窗 **不**强制跑影子数。

### 3.3 G-X 清单（书面 · 必勾）

- [ ] 候选 **未**新增伤害乘区键 / **未**改或新加 `skill_mult`  
- [ ] 候选 **未**抬 STAMP / Affix / 徽记价 / 掉落  
- [ ] 候选 **未**改甲 stampable / defense-side fits / 回滚 TIP-AE  
- [ ] 候选叙事仍是 **蓄力结构/输入**，不写成「加伤害修好」  
- [ ] FAIL 路径 = 改假设（保 R1+R2 或升级 R5）或继续 HOLD，**禁止**抬表凑线

---

## 4. PASS / FAIL / 处置

| 结果 | 动作 |
|------|------|
| **BASELINE 仍 FAIL**（预期） | 记入 RESULT；**不**因此开施工；假设仍成立待影子/施工窗验证 |
| **候选影子 PASS**（若另窗跑） | 勾 RESULT；**仍 HOLD** 至总控显式开工 |
| **候选影子 FAIL** | **停**；禁抬表；可迭代 R1+R2 结构说明或升级 **R5** 另 tip；不改 live mods 硬拧 |
| **本窗仅交包未跑满** | RESULT = PENDING；允许；不算开工 |

---

## 5. 结果记录（强制路径）

主文档：[`RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md`](RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md)

须含：

1. 假设钉死句：R1+R2（H-R12）  
2. BASELINE L13 alone 继承勾选  
3. G-alone / G-pair / G-W30 / G-X / G-split / G-id / G-charge 表  
4. 总判定：`STAGE1_L13_OFFLINE = PASS | FAIL | PENDING`  
5. 明确句：**PASS 仍 HOLD 施工，直至总控显式批「开工」**；**零改 L13 mods 本窗**

---

## 6. 薄 harness

| 路径 | 作用 |
|------|------|
| `tools/p1sim/blade_sig_l13_gate_s1.py` | `checklist` / `cmds` / `judge-alone`：从 tol out 过滤 L13 alone 可达行；**不**改 SIGS |

**不做：** 改 `SIGS` / 写插件表 / 连服 / 发明新平衡常数并提交进 mainline。

---

## 7. NON-goals / 硬禁（本窗）

- 业务 jar、CoreRpg/TrMenu/p1map 改码、插件 YAML 数值表  
- 改 L13 或 L11 `DEFS`/`SIGS`/SkillService 蓄力逻辑  
- skill_mult 再拧；Affix densify；永久伤乘；抬表；glance twin；STAMP 抬  
- 深化 densify；sx20；粒子 densify；ForgeChoice·wear·pity·awkDelta densify  
- 把 PASS / 本 DESIGN 写成开工令；夹带 attune / 甲 stampable / L11 调律  
- 本窗强制跑满影子 charge 扫描（可选后继；禁把扫描结果直接 commit 进 SIGS）

---

## 8. 成功标准（本 stage1 tip 开窗）

- [x] 钉死 R1+R2 假设；R5 升级轨写清  
- [x] 门禁表 G-alone / pair / W30 / X / split / id / charge 可核  
- [x] RESULT 路径 PENDING 空壳  
- [x] harness checklist/cmds/judge-alone · 零改 SIGS  
- [x] power-paths：TIP-AH CLOSED live；TIP-AI OPEN  
- [ ] 数字跑满（可后继窗；本开窗不强制）

---

## 9. 参考

- stage0 [`DESIGN-ember-blade-sig-l13-charge-identity-stage0-2026-10-12.md`](DESIGN-ember-blade-sig-l13-charge-identity-stage0-2026-10-12.md) §2–§3  
- RESEARCH [`RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md`](RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md) §3.2 · §5–§6  
- 范式 [`DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md)  
- `tools/p1sim/mainline.py` `SIGS['L13']` · `p1sim.py` `skill_variant` / `skill_charge`  
- TIP-AC RESULT · TIP-AD exclude
