# 状态 · 下一档硬债选定 · 需策划（六槽 Stage1 关窗/开工决策页 · D169 HOLD）

> **上游结案：** D311 样本窗周报脚本方案 **M+R 已落仓**（docs+tools 同号 · `5284ff21`）；证据链 D298→D308→D310→D311 **已闭**；D309 副手并轨方案 A 已关§8-5；加固 1→5 **主债已关**；战斗/经济 R **全表不得开**。见 [`STATUS-ember-sample-week-report-script-d311.md`](STATUS-ember-sample-week-report-script-d311.md) · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md)。

**日期：** 2026-10-08（上海时间）  
**上游：** D311 周报脚本已收 · D310 检查单已收 · D309 副手关窗 · D308 就绪硬表已收 · §1 M 波 + D301–D307 已收 · 样本 R 全表不得开 · 灰印副招 **T0/T0b ❌ · HOLD** · 天赋换机制 **HOLD** · Pack6 **硬禁** · 六槽 Stage1 **文档 SETTLED · 代码 HOLD**（D169；Stage0 前置「已满足」· 加固硬约束仍写 **六槽不做**）  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up · **不开任一样本门禁 R** · **不写六槽代码**  
**硬规格（待策划交稿）：** [`DESIGN-ember-six-slot-stage1-decision-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage1-decision-2026-10-08.md)（待建）· STATUS=待批 A · backlog `B-six-slot-stage1-decision`  
**交稿旁注（2026-10-08 · 策划）：** 已交 [`DESIGN-ember-six-slot-stage1-decision-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage1-decision-2026-10-08.md) · **STATUS=待批 A** · 策划据证据改荐 **方案 B**（权威 §9「前置已满足、待显式开工」已核实；加固「六槽不做」作用域 = 已收的加固窗；D309 关副手后甲槽为唯一正规横向槽位；p1sim 已有六槽模型，T0 = bv60 复跑 + 小接线，不过即回 HOLD）；本窗零代码、不跑 p1sim、不开任一 R；可选方案 A；W 否决。待总控批注。  
**打开理由：** D169 / 装备权威写「Stage0 前置已满足 · Stage1 待总控/服主显式开工」；加固稿与近档 tip 一律 **六槽不做**。两套口径并存 = 假悬空硬债。要在**不开样本 R、不写六槽 Java/NI、不偷开 Pack6**前提下，出一张正式决策页：**继续 HOLD 关窗（荐 A）** 或 **显式开工规格（B · 仍本窗零代码）**——对齐副手 D309 关窗范式。

---

## 0. 局势一句话（样本门禁声明）

D311 证据工具已收；战斗/经济 R **全表不得开**。加固 1→5（含§8-5）已关。下一档若去偷开 R / 天赋灰印 / Pack6 / 直接写六槽代码 / 薄 UX 抬硬债 = 假活。D169 Stage1 前置「已满足」与加固「六槽不做」冲突未正式拍板——本 tip **不**开任一 R，也 **不**开工六槽代码；改采纳 **六槽 Stage1 关窗/开工决策页（docs-only）**。

---

## 1. 选题（八选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D308 硬表：须完整 P 周 + `p1_pf_runs`≥30；**当前不得开** |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / **工坊方案 R（价/节奏）** / 深渊 **R** / 周本 **R** / Boss 预警 **R** / 挂机 **R** | **否** | D308 全表不得开；**禁偷开** |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / **直接写六槽 Stage1 代码** / 新模式 / 抬体力掉落 | **否** | Pack6 硬禁；六槽代码须先决策页批 A/B；禁默默开工 |
| F | 组队·结算余感 / 图录对齐 / 经济钱包 / VIP·战令 / 技能页 / 副手菜单再拧半行 | **否（本窗主交付）** | 薄 UX / 已收诚实窗；**非当前最尖硬债**；勿挡六槽决策关窗 |
| G | 重开 D307–D311 / 改 D308 门槛数字 / 加固 §1 纯 STATUS 同步当主债 | **否** | 刚上线禁再拧；门槛钉死；同步可旁注，不占硬规格主交付 |
| **H** | **六槽 Stage1 关窗/开工决策页（docs-only · 对齐 D309 范式）** | **采纳 · 需策划** | 范围：只 `docs/design/`；钉死 **A=继续 HOLD 关窗**（荐，对齐加固「六槽不做」）或 **B=显式开工规格**（替换预算表 + 掉落 `w80_cap` 钉死 + 独立 `balance_version` + p1sim 门禁；**本窗仍不写代码**）；**W 否决**默默开六槽 / 与样本 R 同号 |

**对齐：** D169 SETTLED · 代码 HOLD · Stage0 GO（D246 `w80_cap`）· Stage1 前置「已满足」但须显式开工 · 装备权威 §1/§9 · 加固硬约束 **六槽不做** · D309 副手关窗范式 · D308 全表不得开 · 灰印/天赋 HOLD · ARCH 禁 Pack6 · **D307–D311 刚收 · 禁重开玩法**。

**未采纳备选说明：** 样本门禁 R 等直播周样本——本窗**绝不**改派去「假装再开 R」。薄 UX 若仍尖，可在批注勾「驳回改派」另选题，**不得**借机开 forge R / 事件 R / Pack6 / **默默写六槽 Java**。

---

## 2. 一句话问题

D169 Stage1 前置已满足、加固却写「六槽不做」——要在**不开样本 R、不写六槽代码**前提下，把「继续 HOLD 关窗 vs 显式开工规格（仍零代码）」写成可批硬规格，结束假悬空。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】六槽 Stage1 关窗/开工决策页（D169 HOLD · docs-only）
优先级：D311 后下一档结构规格硬债 · 非施工玩法窗 · 证据链 D298→D308→D310→D311 已闭 · 样本门禁 R 不偷开 · 非灰印/天赋续跑 · 非重开 D307–D311 · 非薄 UX 余感抬硬 · 零六槽代码 · 禁 Pack6
禁：六槽 Stage1 Java/NI/Loadout 施工本体·Pack6·新模式图包·抬体力/掉率/event_rate/ALTS·改 refineCost/qualityCost/enhance 价表（=偷开 forge R）·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调·事件R/W施工·调律R施工·工坊R施工·走廊W2夹带·深渊R/周本R/Boss预警R/挂机R施工·重开D307–D311·纯lore·玩家面遥测KPI·默默开六槽代码·与样本战斗/经济 R 同号兼开

目标：一张可批决策页写清「六槽 Stage1：继续 HOLD 正式关窗（A）还是显式开工规格（B）」；若 B 须钉替换预算表 + 掉落 w80_cap + 独立 bv + p1sim 门禁；本窗零代码、零玩法 yml/jar

请出 docs/design/DESIGN-ember-six-slot-stage1-decision-2026-10-08.md，文首 STATUS=待批 A，含：

0. 证据：D169 [`DESIGN-ember-gear-staged-plan-2026-10-04.md`](../design/DESIGN-ember-gear-staged-plan-2026-10-04.md) SETTLED·代码 HOLD；装备权威 [`DESIGN-ember-gear-structure-2026-10-06.md`](../design/DESIGN-ember-gear-structure-2026-10-06.md) §1「6 槽未上线」+ §9「Stage1 前置已满足、待显式开工」；Stage0 / D246 `w80_cap`（护符 80% / 每件甲 5%，费用 ×0.85/×0.05，护甲升阶≤护符阶级，每局另掉 1 甲）；加固 [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md) 硬约束「六槽不做」+ 债表禁 Stage1；近档 tip/D301–D311 一律「六槽 HOLD/不做」；D309 副手关窗范式（docs-only 拍死）；D308 后为何仍不得借本窗开任一 R / 写六槽代码
1. 玩家感知目标（≤3 条）：对玩家——本窗无新感知（零代码）；对总控/服主——能回答「六槽现在到底做不做、要不要显式开工令」；对施工——知道批 A 后禁写哪些槽位/公式/掉落改，批 B 后另号才能动 Java 且须带哪些钉死门禁
2. 方案表（至少 2 选 1 荐）：
   - 方案 A（荐 / 对齐加固硬约束）：**继续 HOLD · 正式关窗**——六槽 Stage1 在加固期与样本门禁期内**不做**；文档 SETTLED 保留为历史计划；权威 §9 / D169 旁注「关窗至总控另派显式开工」；零代码；可同步 tip/backlog/加固旁注指针；不跑 p1sim
   - 方案 B：显式开工规格（**本窗仍不写代码**）——写清：替换预算表（护符生命/防御迁出比例；禁新增攻击轴）；掉落钉死 D246 `w80_cap`（不得另开稀释刃/护符掉率）；独立 `balance_version`；p1sim 门禁（42 格 ±2pp；动态/W30 对照 Stage0 已过线口径）；迁移最小路径（测试/管理号；线上无老玩家存量口径可引 D245）；批 B 后**另号**插件+数值窗，**禁**与样本战斗/经济 R 同号
   - 方案 W（否决默认）：默默开六槽 Stage1 代码 / 与样本 R 同号兼开 / 借本窗偷开任一战斗经济 R / Pack6 / 改 D308 门槛 —— 写清否决理由
3. 不动清单与门禁：体力/掉落/event_rate/ALTS（本窗）；价表；Pack6；天赋轨；灰印轨；守招邻域；**任一 R 窗施工本体**；D307–D311 主交付；D308 §2.1 门槛数字；**六槽 Java/NI/EmberLoadout 扩槽**（批 A 永久本窗禁；批 B 仅另号）。方案 A 零战斗压**不跑 p1sim**；方案 B 仅在批后另窗跑 p1sim 报告。本 tip 窗默认不部署
4. 批注勾选：批 A / 批 B / 驳回改派（其它薄 UX · 须点名且不得偷开 R / Pack6 / 默默写六槽代码）

交付：只 docs；批准前禁改 YAML/Java/NI；批准后若仅同步关窗指针 / STATUS 可同窗 docs 施工号，**不得**借施工号开事件/调律/forge/走廊W2 等 R，**不得**写六槽代码（除非另派且批 B）。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / **工坊 R（价表）** · **不**开深渊 R · **不**开周本 R · **不**开 Boss 预警 R · **不**开挂机 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**改 refineCost / qualityCost / enhance 价 · **不**抬掉率 / 新材料轨  
- **不**扩 Pack6 / 新模式 · **不**写六槽 Stage1 Java / NI / Loadout · **不**抬体力  
- **不**重开 D307 / D308 / D309 / D310 / D311 主交付邻域 · **不**改 D308 门槛数字  
- **不**把薄 UX / 菜单半行再拧当本窗主施工  
- **不**写假「已开工六槽 / 已施工 R」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 A 后，另派施工号（本荐 **A = docs-only 关窗**；若批方案 B 须**另号** p1sim+插件，禁与样本 R 同号）。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D308：未满完整 P 周 / `runs`≥30；**不得开** |
| 走廊 W2 · **工坊 R（价）** · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | D308 全表不得开 |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 刚上线等人 |
| 组队 / 图录 / 钱包 / VIP / 技能页薄余感 | 已收或偏软；非最尖 |
| 副手菜单再拧半行 | D309 已关§8-5；再拧=薄 UX |
| 重开 D307–D311 · 改 D308 门槛 | 刚上线禁再拧；门槛钉死 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / **直接写六槽代码** / 抬体力掉落 | 硬禁 / 须先决策页 |
| 玩家面遥测 KPI | D298-W / D308 / D310 / D311 否决 |

---

## 6. 刚结指针

- D311 · [`STATUS-ember-sample-week-report-script-d311.md`](STATUS-ember-sample-week-report-script-d311.md) · docs+tools · **批 M+R · 脚本已落 · `5284ff21`** · **本 tip 下一档**  
- 周报脚本 tip · [`STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md) · 已关窗（批 M+R · D311）  
- D310 · [`STATUS-ember-sample-week-report-d310.md`](STATUS-ember-sample-week-report-d310.md) · docs-only · 检查单已落  
- D309 · [`STATUS-ember-offhand-merge-decision-d309.md`](STATUS-ember-offhand-merge-decision-d309.md) · docs-only · **§8-5 已关 · 方案 A · 关窗范式**  
- D308 · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) · **门槛钉死 · 当前全表不得开**  
- D169 · [`DESIGN-ember-gear-staged-plan-2026-10-04.md`](../design/DESIGN-ember-gear-staged-plan-2026-10-04.md) · SETTLED · 代码 HOLD · Stage0/D246 前置已满足  
- 装备权威 · [`DESIGN-ember-gear-structure-2026-10-06.md`](../design/DESIGN-ember-gear-structure-2026-10-06.md) §1 / §9  
- Stage0 · [`DESIGN-ember-gear-6slot-stage0-2026-10-04.md`](../design/DESIGN-ember-gear-6slot-stage0-2026-10-04.md) · `w80_cap`  
- 灰印 T0b **HOLD** · 天赋 HOLD  
- 加固债总览 · [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md) · §1～§8-5 **主债已关** · 硬约束 **六槽不做**

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。样本门禁 R 全表不得开——本 tip 只升六槽 Stage1 关窗/开工决策页（docs-only · 荐 A 继续 HOLD · 禁默默写六槽代码 · 禁与样本 R 同号）。*
