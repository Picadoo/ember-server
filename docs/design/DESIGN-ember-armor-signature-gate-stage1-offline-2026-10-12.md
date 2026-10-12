# DESIGN · 防具签名叠合门禁 stage1 离线（S3 p1sim + S4 W30）· 2026-10-12

> **状态：** APPROVED · TIP-AC 批 A·M · docs+tools · **HOLD 施工 / 零业务 jar** · RESULT 已填 FAIL `469cc1a46` · **TIP-AD S3 评测集补丁** · 2026-10-12 19:19 Asia/Shanghai  
> **对接 tip：** [`TIP-ember-armor-signature-gate-stage1-offline-thin-d12560-d12575-2026-10-12.md`](../tips/TIP-ember-armor-signature-gate-stage1-offline-thin-d12560-d12575-2026-10-12.md)  
> **上游：** TIP-AB CLOSED DESIGN stage0 APPROVED HOLD `a5b264fce` · [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md) · RESEARCH [`RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md`](RESEARCH-ember-mainline-unlock-2c-sixslot-2026-10-12.md)  
> **结果落点：** [`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md)（TIP-AC 已填 FAIL）  
> **TIP-AD 补丁：** [`DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md) · S3-T/P 防具门禁集 **排除刃 L11/L13** · `armor_sig_gate_s1.py judge-s3`  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice·wear·pity·awkDelta densify · **零业务 jar / 零插件 YAML 表** · 不过线不停 · **PASS ≠ 开工**（仍须总控显式开工才开施工 tip）

---

## 0. 一句话

把 stage0 已钉的 **S3（p1sim 单条/组合/excl · 不与 ×0.97 叠新乘区）** 与 **S4（W30 vs 现行六槽+签名基线 ±0.5 周）** 写成可执行离线门禁窗：复用既有 `tools/p1sim/mainline.py`，**不发明新乘区数**；结果写入 RESULT；FAIL→停迭代规格/sim；PASS 后仍 HOLD 施工。

---

## 1. 分阶与本窗边界

| 阶 | 内容 | 本窗 |
|----|------|------|
| 0 · 规格 | stage0 APPROVED · S1 规则书面冻结 · S2 烙印扩甲/掉落不扩 | **已 CLOSED**（TIP-AB `a5b264fce`） |
| **1 · 离线门禁** | **跑/可跑 S3 + S4**；docs+tools；禁业务 jar | **本窗** |
| 2 · 施工 tip | 仅 S1–S4 PASS **且** 总控显式开工后 | **另 tip**；本窗 **不是**开工令 |

**S1 / S2 本窗处理：** 视为 stage0 已书面过线（规格冻结 + 默认「可烙不可掉」）；本窗 **不重开** S1/S2 施工，只在 RESULT 文首勾「S1/S2 继承 PASS」。若跑 S4 时发现必须改掉落/STAMP → **FAIL S2 精神**，停，回 stage0 改规格，**禁抬表凑线**。

---

## 2. 为何可不改 SIGS 数也能测 S3/S4

stage0 §2.1：**本 stage0 不新加签名 id / 不改修饰键数值**；防具侧 = 原 `charm` 行语义升格。  
因此：

- **战斗预算（B/H 当量）** 与「该签名烙在 charm 还是甲」在 p1sim 现模型下 **同 mods 键** → 直接复用 `mainline.py tol / pairs / talent` 的既有门槛精神（D174 §8）。  
- **规则债（5 取 1、§2.6 优先级、excl、同 tag、不与 ×0.97 叠新乘区叙事）** 用薄 harness 离线单测 + 书面清单核；**不**碰服务器 jar。  
- **经济 W30**：S2 默认「烙印扩甲 · 掉落不扩」→ 掉落件数/成色/`STAMP_RATE`/徽记价 **不变** → 相对「现行刃+护符签名经济」上界，候选应落在 **±0.5 周**（预期 Δ≈0）；用 `mainline.py econ` 复跑基线对照。

---

## 3. S3 · 生效优先级 + 组合可达（p1sim）

### 3.1 用例包（须入 RESULT）

| 包 | 内容 | 工具 | 过线 |
|----|------|------|------|
| **S3-R** 规则单测 | 0/1/多甲带签 → 生效恰好 ≤1；双签前；excl；与刃同 tag；§2.6 `charm>head>chest>legs>boots` | `python3 tools/p1sim/armor_sig_gate_s1.py rules` | 全部 assert PASS |
| **S3-T** 单条 42 格 | 各签名 alone vs base；7 图 × r/c × dodge 0.3/0.5/0.7；**防具门禁判定排除刃 L11/L13 alone**（TIP-AD） | `mainline.py tol` + `armor_sig_gate_s1.py judge-s3` | D174 精神：目标每格 ±2 pp；硬线 mean\|Δ\|≤1 pp 且无格超 ±3 pp（**仅 armor-gate 成员**） |
| **S3-P** 合法刃×防具侧组合 | 不同 tag 的 blade+charm（=防具侧）对；**排除含 L11/L13 的对**（TIP-AD） | `mainline.py pairs` + `judge-s3` | 同上（仅 armor-gate 成员） |
| **S3-E** 天赋 excl | 最强对 + 满天赋；`EXCL` 关掉同 tag 签 | `mainline.py talent`（默认可 EXCL） | 同 tag 签被 excl 后 **不**叠出额外上偏；无新乘区 |
| **S3-X** ×0.97 正交 | 书面：签名 **不**改四件套减伤、不新开伤乘/`skill_mult`、不把套装×0.97 写成「再乘一层」 | 清单勾选（见 §3.3） | 全勾 + 无新乘区键进入候选 |

### 3.2 精确命令（既有工具 · 禁发明新数）

工作目录：`tools/p1sim`（或仓库根下同相对路径）。

```bash
# 0) 规则单测（薄 harness · 零 jar）
python3 tools/p1sim/armor_sig_gate_s1.py rules

# 1) 打印本窗命令包（可复制）
python3 tools/p1sim/armor_sig_gate_s1.py cmds

# 2) S3-T 单条
mkdir -p /tmp/ml-armor-sig
cd tools/p1sim
python3 mainline.py tol    2000 /tmp/ml-armor-sig/tol.pkl
python3 mainline.py report /tmp/ml-armor-sig/tol.pkl \
  > ../../docs/design/out-armor-sig-stage1-tol.md

# 3) S3-P 合法对
python3 mainline.py pairs  2000 /tmp/ml-armor-sig/pairs.pkl
python3 mainline.py report /tmp/ml-armor-sig/pairs.pkl \
  > ../../docs/design/out-armor-sig-stage1-pairs.md

# 4) S3-E excl（需 ML_BEST 最强对；无则先 pairs 选后写入环境）
#    例：ML_BEST='{"burst":["L04+L03"],"scorch":["L01+L02"],"sustain":["L05+L06"]}'
ML_BEST='…' python3 mainline.py talent 2000 /tmp/ml-armor-sig/talent.pkl
python3 mainline.py report /tmp/ml-armor-sig/talent.pkl \
  > ../../docs/design/out-armor-sig-stage1-talent.md
```

命名亦可落到 `tools/p1sim/out-*-armor-sig-42.md`（stage0 曾写）；**以 RESULT 文内链为准**。

### 3.3 S3-X · ×0.97 / 乘区正交清单（书面 · 必勾）

- [ ] 候选 **未**新增伤害乘区键 / **未**改 `skill_mult`  
- [ ] 候选 **未**改 Stage2 四件套 ×0.97 系数或触发件数  
- [ ] 候选 **未**改 Stage1 F 共鸣权重 / 成色输入  
- [ ] 叙事上签名仍是**行为修饰**，不写成「再叠一层减伤/伤乘」  
- [ ] FAIL 时路径 = 改规格或砍组合，**禁止**抬 STAMP / Affix / 伤乘凑线

### 3.4 S3 PASS / FAIL

| 结果 | 动作 |
|------|------|
| **PASS** | 勾 RESULT §S3；进入 S4（可并行开工跑） |
| **FAIL** | **停**；记失败格/规则断言；回 stage0 改优先级或砍对；**禁**抬表/伤乘/Affix |

### 3.5 TIP-AD · S3-T/P 评测集（防具门禁）

| 项 | 口径 |
|----|------|
| **排除** | 刃 **L11**、**L13** alone，以及 **含 L11/L13 的 pairs** |
| **纳入** | 防具侧 alone（原 charm 升格）+ 其余合法刃×防具对 + 非 L11/L13 刃 alone（参考） |
| **工具** | `python3 tools/p1sim/armor_sig_gate_s1.py judge-s3`（过滤既有 out；零改 SIGS） |
| **HOLD** | 刃 L11/L13 另轨研究 tip；本窗不修刃 mods |
| **硬句** | 排除 = 评测成员资格；**不是**抬表 / 删 SIGS / 开工令 |

补丁 DESIGN：[`DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md)

---

## 4. S4 · W30 / 经济叠合

### 4.1 基线定义（钉死）

| 项 | 定义 |
|----|------|
| **基线 BASE** | **现行六槽 live**（Stage1 F + Stage2 C 已在服；本窗 **不**改 `P1SIM_SIX` 业务开关）+ **现行签名经济**：刃 1 + 护符 1；`STAMP_RATE` / 徽记周池 / imprint 价 **不变** |
| **候选 CAND** | 防具签名语义升格后、在 **S2 默认**下：烙印可扩甲、**掉落仍不扩甲** → 掉落件数/成色输入与 BASE **同构**；combat mods 仍为既有 SIGS（无新 id） |
| **度量** | 「两件极品」时间线 **W30**（与 `mainline.py econ` / p2econ 既有定义一致：≥30% 玩家两件极品的周，线性插值） |
| **过线** | CAND 相对 BASE：**ΔW30 ∈ [−0.5, +0.5] 周**（各主路线：无轮换 / 轮换 / 深渊 / 团本组合等，与 D190 报告同族）；或 **总控书面重钉** 阈值后记入 RESULT |
| **输入含** | Stage1 F / Stage2 C 精神（不抬表）；**不含** Affix densify / 伤乘 / STAMP 抬 |

**预期（非放宽）：** 因 S2 不扩掉落、不改价，CAND 的 econ 上界应 **≈ BASE**；若出现 >0.5 周加速，视为模型/假设错误或隐性抬表 → **FAIL**，查是否误开 stampable 扩甲掉落。

### 4.2 精确命令

```bash
cd tools/p1sim
# BASE：无签名 day-1 上界对照（空列表）
python3 mainline.py econ '[]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \
  > ../../docs/design/out-armor-sig-stage1-w30-base.md

# CAND 上界：既有刃+护符从 day 1 佩戴（与现行签名经济同构；防具侧 mods 相同）
# 例：全阶段代表对（按族；勿新造 mods）
python3 mainline.py econ '["L01","L02"]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \
  > ../../docs/design/out-armor-sig-stage1-w30-cand-L01L02.md

# 可选：六槽农场 W30 大样本（仅当需核对「六槽 live 基线」件数轴；仍禁改 SIX 业务数）
# python3 gear6w30.py '{"base": null}' 1200 base > /tmp/ml-armor-sig/gear6-base.json
```

对照表写法对齐既有 [`tools/p1sim/out-d190-l11b-w30.md`](../../tools/p1sim/out-d190-l11b-w30.md)：同 rules sha / 同 players / 同 weeks，并排 BASE vs CAND，算 ΔW30。

### 4.3 S4 PASS / FAIL

| 结果 | 动作 |
|------|------|
| **PASS** | ΔW30 全路线在 ±0.5 周（或总控重钉已签）；勾 RESULT §S4 |
| **FAIL** | **停**；禁止抬 STAMP/价/伤乘凑线；可改 stage0 默认（例如进一步收烙印）后重跑 |

---

## 5. 结果记录（强制路径）

主文档：[`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md)

须含：

1. 规则 sha / balance_version（跑时从 mainline/p2econ 头行抄）  
2. S1/S2 继承勾选  
3. S3-R / T / P / E / X 表 + 链到 `out-armor-sig-stage1-*.md`  
4. S4 BASE/CAND ΔW30 表 + 链到 `out-armor-sig-stage1-w30-*.md`  
5. 总判定：`STAGE1_OFFLINE = PASS | FAIL`  
6. 明确句：**PASS 仍 HOLD 施工，直至总控显式批「开工」**

本 TIP-AC 开窗时 RESULT 可为 **PENDING 空壳**；施工/插件岗或后续薄窗填数。

---

## 6. 薄 harness（可选 · 已加）

| 路径 | 作用 |
|------|------|
| `tools/p1sim/armor_sig_gate_s1.py` | `rules`：§2.6 / 5 取 1 / excl / 同 tag；`cmds`/`checklist`：命令包+口诀；**`judge-s3`**：TIP-AD 过滤 L11/L13 后判 S3-T/P（仅评测成员） |

**不做：** 改 `SIGS` / 改 economy yml / 写插件表 / 连服 / 发明新平衡常数。

---

## 7. NON-goals / 硬禁（本窗）

- 业务 jar、CoreRpg/TrMenu/p1map 改码、插件 YAML 数值表  
- 新签名 id、新 mods 键、抬 `STAMP_RATE` / 徽记周池 / imprint 价  
- Affix densify、伤乘、抬表、glance twin、深化 densify、sx20、skill_mult、ForgeChoice·wear·pity·awkDelta densify  
- 把 PASS 写成开工令；夹带 K3 / 档 S / 白板 / Pack6 / 烬斩门闩施工  
- 改四件套 ×0.97 / 共鸣 F 权重

---

## 8. 成功标准（本 stage1 tip 开窗）

- [x] S3 用例包 + 精确 `mainline.py` 命令可核  
- [x] S4 基线定义 + ±0.5 周 + econ 命令可核  
- [x] RESULT 路径命名；FAIL=停 / PASS≠施工 写清  
- [x] harness 仅 rules+cmds（或等价文档）· 零业务 jar  
- [x] power-paths：TIP-AB CLOSED `a5b264fce`；TIP-AC OPEN  
- [ ] S3/S4 **跑完**数字（可后继窗；本开窗不强制跑满）

---

## 9. 参考

- stage0 [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md) §2–§3  
- `tools/p1sim/mainline.py`（tol/pairs/talent/econ）· `gear6w30.py`  
- `tools/p1sim/out-d190-l11b-w30.md` · `out-insignia-d222-w30.md`  
- D174 [`DESIGN-ember-mainline-unlocks-2026-10-04.md`](DESIGN-ember-mainline-unlocks-2026-10-04.md) §8
