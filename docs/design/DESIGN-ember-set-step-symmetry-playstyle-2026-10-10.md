# 余烬 · 成长花样 · 套装身法对称（烬爆/承烬 · skill-kit S3a · 拟→D434）

STATUS=**已批 A · 方案 M · D434 · PASS** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-set-step-symmetry-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-set-step-symmetry-need-design-2026-10-10.md) · backlog `B-set-step-symmetry` · STATUS [`STATUS-ember-set-step-symmetry-d434-2026-10-10.md`](../status/STATUS-ember-set-step-symmetry-d434-2026-10-10.md) · **≠守招 ≠抬日表 ≠关 Stage2（＜17:40）≠天赋/灰印 ≠sx20**

**上游：** D433 PASS（薄诚实收束）· skill-kit S1/S2 已落（烬突/三形状/火痕/后撤）· 守招 D301 已另轨 · Stage2 观察至 **≥17:40 CST**。

## 0. 问题（证据）

| # | 证据 | 含义 |
|---|------|------|
| E1 | 仅**焚烬**两件有身法身份（火痕步）；烬爆/承烬两件装仍是通用踏步 | 套装手感不对称 |
| E2 | skill-kit 删灰印/聚火；守招已换思路落地，**禁**再开旧壁垒/聚火 cand | 下一刀应走**身法变体**而非守招/副招重交 |
| E3 | 火痕 S0 ✅（点1、同系数、无额外耗时） | 对称变体须同等预算纪律：共享 14s、不暴击/不吸血/不套装计数 |
| E4 | Stage2 绿出口不早于 17:40；本窗可用两件 `activeSet`，**不**关观察、不拧 set_bonus | 变体读现有两件族，不依赖四件 Stage2 关窗 |

**一句话：** 给烬爆/承烬各一只「戴上就手感不同」的身法落点效用，补齐火痕对称；先 S0 再 jar。

## 1. 方案对照

| 方案 | 内容 | 裁定 |
|------|------|------|
| **M（荐）** | **爆闪步**（烬爆2pc+Q05）+ **承护步**（承烬2pc+Q05）；共享踏步 CD；S0→T1 | **主推** |
| A | 只改技能页文案 | **否决**（薄诚实刚收束） |
| L | 抬挂机 / 改火痕数值 / 关 Stage2 | **否决** |
| W | 守招加码 / 灰印·聚火重交 / sx20 | **否决** |

### 批 A 勾选

- [x] **方案 M**
- [x] 否决 A / L / W
- [x] 批注：总控 **已批 A · 方案 M · D434** · 2026-10-10 12:15 CST · **≠守招** · 先 S0 门禁再 T1 jar · Stage2 不提前关

## 2. 玩法钉死（M）

| 项 | 爆闪步（burst） | 承护步（sustain） |
|----|-----------------|-------------------|
| 解锁 | Q05 首通 + **烬爆**两件 `activeSet` | Q05 + **承烬**两件 |
| 键 | 潜行+Q（同身法） | 同 |
| CD | **14s 共享**（与踏步/火痕） | 同 |
| 位移 | 前冲/后撤照 `p1_step_dir` | 同 |
| 落点效用（意向·S0 可调） | 落点 3 格最近 1 敌：**短暂缓速 I ≤1.5s**（非聚火、不加炸） | 落点：**自身抗性 I ≤2s** 或 微额自疗 flat（S0 二选一；禁永久减伤） |
| 后撤变体 | 起跳点施效（对齐火痕·后撤） | 同 |
| 禁 | 暴击/吸血/套装计数；穿墙跳房；改火痕系数 | 同 |

火痕步不变。三族互斥：同时只激活一族身法变体。

## 3. 门禁

1. **W1a S0（本窗先做）：** p1sim 加 `kit_step_burst_*` / `kit_step_sustain_*`；三套装×42 Δ∈±2；择优成立；逐位无键一致。  
2. **W1b T1（S0 PASS 后）：** CoreRpg 镜像火痕接线 + TrMenu 技能页一行 + PAPI 名。  
3. **不做：** 守招、AFK 表、Stage2 关窗、天赋/灰印。

## 4. 本窗施工起点

- EmberSkillKit：`baoshanActive` / `chenghuActive`（命名可改）族检测 + 展示名  
- FlexSkillService：接线钩子（效用数值等 S0 锁）  
- tools/p1sim：键位与首轮探针

*D434 · PASS · jar 1.65.137-d434 · S0 报告已落 · 承护抗性闸 · COORD DONE。*

## 5. S0 结果（不调参）

见 [`out-skillkit-d434-set-step.md`](../../tools/p1sim/out-skillkit-d434-set-step.md)。承护 Resist I 超预算 → live 关药效；改设计见拟 D435。

