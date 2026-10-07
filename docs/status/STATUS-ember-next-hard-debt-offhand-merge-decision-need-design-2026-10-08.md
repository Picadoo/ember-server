# 状态 · 下一档硬债选定 · 需策划（副手并轨 / 不并轨决策页 · 加固§8-5）

> **上游结案：** D308 样本窗就绪清单方案 **M 已落仓**（docs-only · `27750279`）；加固稿 **§1A–§1C 主债 M 波已清**；战斗可感波 D301–D304、挂机 D305、枢纽 D306、工坊菜单 D307 已收；**样本门禁 R 全表不得开**（事件R/W · 调律R · 工坊R · 走廊W2 · 深渊R · 周本R · Boss预警R · 挂机R）。见 [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md)。

**日期：** 2026-10-08（上海时间）  
**上游：** D308 样本窗门禁已收 · §1 M 波 + D301–D307 已收 · 样本 R 全表不得开 · 灰印副招 **T0/T0b ❌ · HOLD** · 天赋换机制 **HOLD** · Pack6 / 六槽 **HOLD**  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up · **不开任一样本门禁 R**  
**硬规格（已上线 D309 · 方案 A · docs-only）：** [`DESIGN-ember-offhand-merge-decision-2026-10-08.md`](../design/DESIGN-ember-offhand-merge-decision-2026-10-08.md) · STATUS=已批 A · 批方案 A · [`STATUS-ember-offhand-merge-decision-d309.md`](STATUS-ember-offhand-merge-decision-d309.md) · backlog `B-offhand-merge-decision` · 加固§8-5 **已关窗**。  
**打开理由：** D308 钉死后，样本门禁 R 全表不得开；下一档真硬债若仍去偷开 R / 天赋灰印 / Pack6 六槽 = 假活。加固§8 仅余 **窗 5 副手并轨决策页**未在加固稿上关指针——要在**不开 R、不写新 NI、不开六槽**前提下，把「并轨 / 不并轨 + 替换预算表 + p1sim 怎么验」写成可批硬规格（docs/design only），并同步加固§8-5。

> **关窗：** [`DESIGN-ember-offhand-merge-decision-2026-10-08.md`](../design/DESIGN-ember-offhand-merge-decision-2026-10-08.md) · **STATUS 已批 A · 方案 A · D309** · 永不进 P1 B/H；与 10-07 已批 A 对齐；方案 B 搁置；W 否决 · 加固§8-5 **已正式关窗** · tip/backlog/旧决策页已同步 · [`STATUS-ember-offhand-merge-decision-d309.md`](STATUS-ember-offhand-merge-decision-d309.md)。

---

## 0. 局势一句话（样本门禁声明）

D308 就绪清单已收；战斗/经济 R **全表不得开**。加固§1～§8-4 体验主债已清；§8-5 副手双轨（灰粮 StatService 守腕/生坠 ‖ P1 EmberLoadout 2 槽）仍是长期双计风险口——既有决策页已荐并批 **方案 A**（永不进 P1 B/H · 展示/微量生存），但加固稿仍写「待另派」。本 tip **不**开任一 R，也 **不**把薄 UX / 菜单半行再抬成硬债；改采纳 **副手并轨 / 不并轨决策页（加固§8-5）**（docs-only · 零物品施工 · 零样本门禁偷开）。

---

## 1. 选题（八选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D308 硬表：须完整 P 周 + `p1_pf_runs`≥30；**当前不得开** |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / **工坊方案 R（价/节奏）** / 深渊 **R** / 周本 **R** / Boss 预警 **R** / 挂机 **R** | **否** | D308 全表不得开；**禁偷开** |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / **六槽** / 新模式 / 抬体力掉落 | **否** | 硬禁 / 搁置；六槽 D169 HOLD；本决策页**不解锁**六槽 |
| F | 组队·结算余感 / 图录对齐 / 经济钱包 / VIP·战令 / 技能页 / 副手菜单再拧半行 | **否（本窗主交付）** | 薄 UX / 已收诚实窗（D289/D294）；**非当前最尖硬债**；勿挡§8-5 关窗 |
| G | 重开 D307 / 重开 D308 门禁改数 / 加固 §1 纯 STATUS 同步当主债 | **否** | 刚上线禁再拧；同步可旁注，不占硬规格主交付 |
| **H** | **副手并轨 / 不并轨决策页（替换预算表 · 加固§8-5）** | **采纳 · 需策划** | 范围：只 `docs/design/` + **p1sim 怎么验**；**不施工物品**；六槽 HOLD；现网 B-flex-3 守腕/生坠薄获取（StatService）与 P1 2 槽并行——须拍死 **A 永不进 B/H** 或 **B 替换预算并轨**，禁止默默叠；并同步加固§8-5「仍待另派」指针 |

**对齐：** D308 全表不得开 · D298 遥测 · 灰印/天赋 HOLD · ARCH 禁 Pack6/六槽 · B-flex-1/3 PASS · [`DESIGN-ember-offhand-budget-decision-2026-10-07.md`](../design/DESIGN-ember-offhand-budget-decision-2026-10-07.md) 既有 **方案 A** · D289/D294 诚实半行 · **D307/D308 刚收 · 禁重开玩法**。

**未采纳备选说明：** 样本门禁 R 等直播周样本——本窗**绝不**改派去「假装再开 R」。薄 UX 若仍尖，可在批注勾「驳回改派」另选题，**不得**借机开 forge R / 事件 R / 六槽 / 新副手 DPS 物品。

---

## 2. 一句话问题

灰粮副手（StatService）与 P1 刃+护符（EmberLoadout）双轨并行；加固§8-5 仍标待另派——要在**不开样本 R、不写新物品、不开六槽**前提下，把「并轨 / 不并轨 + 替换预算表 + p1sim 验法」写成可批硬规格并关窗指针。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】副手并轨 / 不并轨决策页（替换预算表 · 加固§8-5 · docs-only）
优先级：D308 后下一档横向规格硬债 · 非施工玩法窗 · §1 M 波已收 · 样本门禁 R 不偷开 · 非灰印/天赋续跑 · 非重开 D307/D308 · 非薄 UX 余感抬硬 · 不施工物品 · 六槽 HOLD
禁：六槽·Pack6·新模式图包·抬体力/掉率/event_rate/ALTS·改 refineCost/qualityCost/enhance 价表（=偷开 forge R）·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调·事件R/W施工·调律R施工·工坊R施工·走廊W2夹带·深渊R/周本R/Boss预警R/挂机R施工·重开D307/D308门禁改数·新 NI 副手 DPS 物品·默默把 StatService 副手叠进 EmberFormula B/H·纯lore挡窗·玩家面遥测KPI/排行榜
目标：一张可批决策页写清「灰粮副手 vs P1 Loadout：永不并轨（A）还是替换预算并轨（B）」；附替换表（若 B）与 p1sim 怎么验；本窗零物品施工、零玩法 yml/jar

请出 docs/design/DESIGN-ember-offhand-merge-decision-2026-10-08.md（或修订并关窗既有 DESIGN-ember-offhand-budget-decision-2026-10-07.md + 同步加固稿文首/§8-5 指针），文首 STATUS=待批 A，含：

0. 证据：B-flex-1/3 守腕/生坠薄获取（StatService whitelist）与 P1 EmberLoadout 2 槽并行；加固§1.1/§3.2/§5-5/§8-5 原文；既有决策页（若沿用）已批方案 A + D289/D294 诚实半行；D308 后为何仍须正式关§8-5 指针；为何本窗不开任一 R / 不开六槽
1. 玩家感知目标（≤3 条）：对玩家——副手是「展示/微量生存」还是「占生存预算的正式横向」（择一话术）；对总控——能回答「能不能进 P1 B/H」；对施工——知道批 A 后禁写哪些物品/公式改
2. 方案表（至少 2 选 1 荐）：
   - 方案 A（荐 / 与既有决策页对齐）：灰粮副手**永不进** P1 B/H；维持展示/微量生存；禁止物伤/暴击/技能系数；可列「已落诚实半行」与仍禁清单；零物品新作；若仅关窗同步加固§8-5 指针可同批
   - 方案 B：替换预算并轨——从既有护符 H（或软帽）挪预算进副手；**禁止新增 DPS 轴**；写清扣源表 + p1sim 验法（42 格 ±2pp；B 全格 0 漂移；offhand=None 与现基线逐位不变）；废弃灰粮独立战斗轴避免双计；**本窗仍不施工物品**（批 B 后另派数值+插件窗）
   - 方案 W（否决默认）：默默让 Stat 副手与 EmberFormula 双计伤害；或借本窗开六槽 / 新副手 DPS NI / 偷开样本 R —— 写清否决理由
3. 不动清单与门禁：体力/掉落/event_rate/ALTS；价表；六槽 Stage1；Pack6；天赋轨；灰印轨；守招邻域；**任一 R 窗施工本体**；D307/D308 主交付；**不施工物品**（无新 NI / 不改编 ward·vita·灰箍数值）。方案 A 零战斗压**不跑 p1sim**；方案 B 仅在批后另窗跑 p1sim 报告。本 tip 窗默认不部署
4. 批注勾选：批 A / 批 B / 驳回改派（其它薄 UX · 须点名且不得偷开 R / 六槽 / 新 DPS 副手）

交付：只 docs；批准前禁改 YAML/Java/NI；批准后若仅同步加固§8-5 指针 / 关窗 STATUS 可同窗 docs 施工号，**不得**借施工号开事件/调律/forge/走廊W2 等 R，**不得**写新副手物品或并轨插件（除非另派且批 B）。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / **工坊 R（价表）** · **不**开深渊 R · **不**开周本 R · **不**开 Boss 预警 R · **不**开挂机 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**改 refineCost / qualityCost / enhance 价 · **不**抬掉率 / 新材料轨  
- **不**扩 Pack6 / 新模式 · **不**开六槽 · **不**抬体力  
- **不**写新 NI 副手 / **不**改编守腕·生坠·灰箍 **数值** · **不**动 EmberLoadout / EmberFormula / StatService 并轨逻辑（待批且另派）  
- **不**重开 D307 / D308 主交付邻域  
- **不**把薄 UX / 菜单半行再拧当本窗主施工  
- **不**写假「已施工 R / 已并轨」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 A 后，另派施工号（本荐 **docs-only 关窗 / 零物品**；若批方案 B 须**另号** p1sim+插件，禁与样本 R 同号）。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D308：未满完整 P 周 / `runs`≥30；**不得开** |
| 走廊 W2 · **工坊 R（价）** · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | D308 全表不得开 |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 刚上线等人 |
| 组队 / 图录 / 钱包 / VIP / 技能页薄余感 | 已收或偏软；非最尖 |
| 副手菜单再拧半行（无决策） | D289/D294 已落；再拧=薄 UX 挡§8-5 |
| 重开 D307 / D308 | 刚上线禁再拧 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / 六槽 / 抬体力掉落 | 硬禁 / 搁置 |

---

## 6. 刚结指针

- D308 · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) · docs-only · `27750279` · **样本 R 全表不得开 · 本 tip 下一档**  
- 样本窗 tip · [`STATUS-ember-next-hard-debt-sample-window-readiness-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-sample-window-readiness-need-design-2026-10-08.md)  
- D307 · [`STATUS-ember-workshop-menu-honesty-d307.md`](STATUS-ember-workshop-menu-honesty-d307.md) · CoreRpg **1.65.97** · **工坊菜单诚实已收 · ≠ forge R**  
- D306 · [`STATUS-ember-hub-daily-routing-d306.md`](STATUS-ember-hub-daily-routing-d306.md) · CoreRpg **1.65.96**  
- D305 · [`STATUS-ember-hang-farm-soft-identity-d305.md`](STATUS-ember-hang-farm-soft-identity-d305.md) · CoreRpg **1.65.95**  
- D304–D301 · 预警 / 周本 / 深渊 / 守招 · 见各 STATUS  
- D298 · [`STATUS-ember-playfeel-telemetry-d298.md`](STATUS-ember-playfeel-telemetry-d298.md) · 证据管已收  
- 既有副手决策 · [`DESIGN-ember-offhand-budget-decision-2026-10-07.md`](../design/DESIGN-ember-offhand-budget-decision-2026-10-07.md) · **已批 A · 方案 A** · B-flex-5  
- D289 · [`STATUS-ember-offhand-honesty-d289.md`](STATUS-ember-offhand-honesty-d289.md) · 守腕/生坠诚实半行  
- D294 · [`STATUS-ember-ash-brace-honesty-d294.md`](STATUS-ember-ash-brace-honesty-d294.md) · 灰箍诚实半行  
- 灰印 T0b **HOLD** · 天赋 HOLD  
- 加固债总览 · [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md) · §8-5 **已关窗 · 方案 A · D309**  
- D309 · [`STATUS-ember-offhand-merge-decision-d309.md`](STATUS-ember-offhand-merge-decision-d309.md) · docs-only · **副手永不进 P1 B/H · 零物品**

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。样本门禁 R 全表不得开——本 tip 已关窗（已批 A · 方案 A · D309 · docs-only · 不施工物品）。*
