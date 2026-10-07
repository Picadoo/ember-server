# 状态 · 下一档硬债选定 · 需策划（样本窗周报模板 · D308 方案 R 后置）

> **上游结案：** D309 副手并轨决策页方案 **A 已落仓**（docs-only · `e0e8ded6`）；加固稿 **§8-5 已关窗**；D308 样本窗就绪清单方案 **M 已落仓**（`27750279`），其 **方案 R 周报模板后置**（未勾 M+R）；加固 **1→5** 已关；战斗/经济 R **全表不得开**。见 [`STATUS-ember-offhand-merge-decision-d309.md`](STATUS-ember-offhand-merge-decision-d309.md) · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md)。

**日期：** 2026-10-08（上海时间）  
**上游：** D309 副手关窗已收 · D308 就绪硬表已收（R 周报后置）· §1 M 波 + D301–D307 已收 · 样本 R 全表不得开 · 灰印副招 **T0/T0b ❌ · HOLD** · 天赋换机制 **HOLD** · Pack6 / 六槽 **HOLD**  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up · **不开任一样本门禁 R**  
**硬规格（待策划交稿）：** `docs/design/DESIGN-ember-sample-week-report-2026-10-08.md` · STATUS=待批 A · backlog `B-sample-week-report`  
**打开理由：** D308 钉死「何时算够、谁签字」的**手查硬表**；仍缺从 `p1-telemetry/<week>.yml` 生成的 **markdown 周报检查单**（减 OP 抄写、统一勾选栏）。D308 批注明文 **R 周报后置**——本 tip 把它升为下一档 docs 硬债，**仍零玩法、禁自动开闸、禁玩家面 KPI**。

---

## 0. 局势一句话（样本门禁声明）

D308 M 表已是唯一开闸对照；当前全表不得开。加固 1→5（含§8-5）已关。下一档若去偷开战斗/经济 R / 天赋灰印 / Pack6 六槽 / 重开 D307–D309 = 假活。本 tip **不**开任一 R，也 **不**把薄 UX 抬成硬债；改采纳 **样本窗周报模板（D308 方案 R 后置）**——从周遥测文件出可勾检查单（docs 模板；脚本可另号），**禁**无总控签字自动建议开 R，**禁**玩家面看板。

---

## 1. 选题（八选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D308 硬表：须完整 P 周 + `p1_pf_runs`≥30；**当前不得开** |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / **工坊方案 R（价/节奏）** / 深渊 **R** / 周本 **R** / Boss 预警 **R** / 挂机 **R** | **否** | D308 全表不得开；**禁偷开** |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / **六槽** / 新模式 / 抬体力掉落 | **否** | 硬禁 / 搁置 |
| F | 组队·结算余感 / 图录对齐 / 经济钱包 / VIP·战令 / 技能页 / 副手菜单再拧半行 | **否（本窗主交付）** | 薄 UX / 已收诚实窗；**非当前最尖硬债**；勿挡周报模板 |
| G | 重开 D307 / 重开 D308 门禁改数 / 重开 D309 副手 / 加固 §1 纯 STATUS 同步当主债 | **否** | 刚上线禁再拧；D309 已关§8-5；同步可旁注，不占硬规格主交付 |
| **H** | **样本窗周报模板（D308 方案 R 后置 · docs checklist）** | **采纳 · 需策划** | 范围：从 `p1-telemetry/<week>.yml`（及可选 JSON 旁路）出 markdown **检查单**（周滚/`runs`/事件两率/调律两率/排除脏表/各 R 红线/签字栏）；零玩法；脚本可另号；**禁**无签字自动开 R；**禁**玩家面 KPI |

**对齐：** D308 方案 R 后置原文 · D298 `/corerpg p1 telemetry` · `p1-telemetry/<week>.yml` · 灰印/天赋 HOLD · ARCH 禁 Pack6/六槽 · D309 方案 A 关窗 · **D307–D309 刚收 · 禁重开玩法**。

**未采纳备选说明：** 样本门禁 R 等直播周样本——本窗**绝不**改派去「假装再开 R」。薄 UX 若仍尖，可在批注勾「驳回改派」另选题，**不得**借机开 forge R / 事件 R / 六槽 / 玩家面遥测 KPI。

---

## 2. 一句话问题

D308 手查硬表已有；OP 仍要手抄 `p1-telemetry/<week>.yml`——要在**不开战斗/经济 R、不改数值、不自动开闸**前提下，把**周报 markdown 检查单**（模板；可选脚本路径）写成可批硬规格。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】样本窗周报模板（D308 方案 R 后置 · docs-only）
优先级：D309 后下一档证据工具硬债 · 非施工玩法窗 · D308 M 表已收 · 样本门禁 R 不偷开 · 非灰印/天赋续跑 · 非重开 D307–D309 · 非薄 UX 余感抬硬 · 零玩法 · 禁玩家面 KPI
禁：六槽·Pack6·新模式图包·抬体力/掉率/event_rate/ALTS·改 refineCost/qualityCost/enhance 价表（=偷开 forge R）·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调·事件R/W施工·调律R施工·工坊R施工·走廊W2夹带·深渊R/周本R/Boss预警R/挂机R施工·重开D307/D308/D309·纯lore·玩家面遥测KPI/排行榜/成就·无总控签字自动「建议开 R」变施工
目标：一张可批 DESIGN 写清「周报检查单长什么样、从哪读数、勾哪些门槛、签字栏怎么留、脚本是否另号」；本窗零玩法改、零数值改；模板本身不得自动开闸

请出 docs/design/DESIGN-ember-sample-week-report-2026-10-08.md，文首 STATUS=待批 A，含：

0. 证据：D308 DESIGN §2.2 方案 R 后置原文（从 p1-telemetry 生成 markdown 检查单；仍禁改战斗/经济；禁玩家面看板；禁自动开闸；脚本另号）；D308 STATUS「周报模板后置」；D298 周文件路径 `plugins/CoreRpg/p1-telemetry/<week>.yml` + `/corerpg p1 telemetry` 键名；为何本窗不开任一战斗/经济 R；为何不重开 D307–D309；灰印/天赋为何 HOLD
1. 玩家感知目标（≤3 条）：对玩家——本窗无新感知（零玩法、无 KPI 板）；对总控/策划——周报勾完就能回答「本周样本满不满、能否进入某债待批 A」；对 OP——照模板填/生成，少手抄，签字栏仍须总控
2. 方案表（至少 2 选 1 荐）：
   - 方案 M（荐）：**只 docs 周报 markdown 模板**——检查单勾选：周滚是否满、全服 `p1_pf_runs` 是否≥30（排除测试号）、事件两率、调律两率、排除表是否脏、各 R 红线（对照 D308 §2.1 当前态）、签字栏（总控批 A）；示例填一空周/半满周；**零 jar/yml 玩法**；**不**含「自动建议开 R」按钮文案；模板落地可同窗 docs 施工号
   - 方案 R：在 M 模板之上写清 **tools 脚本路径**（读 `p1-telemetry/<week>.yml` → 吐同一检查单 md；可选 JSON 旁路）——仍零玩法；脚本落地须**另派施工号**且禁改战斗/经济；脚本输出**只得**「门槛勾选 + 当前态摘要」，**禁**输出「建议立即开 X R」而无签字占位
   - 方案 W（否决默认）：周报或脚本**自动建议开 R**且无总控签字即可施工；或把周报做成玩家面 KPI/排行榜；或借本窗偷开任一战斗/经济 R / 重开 D307–D309 —— 写清否决理由
3. 不动清单与门禁：体力/掉落/event_rate/ALTS；价表；六槽；Pack6；天赋轨；灰印轨；守招邻域；**任一 R 窗施工本体**；D307/D308/D309 主交付；玩家面遥测 KPI。零战斗/零经济压 **不跑 p1sim**；本窗默认不部署。门槛口径**以 D308 DESIGN 为准**（本模板不得改门槛数字）
4. 批注勾选：批 M / 批 R / 批 M+R / 驳回改派（其它薄 UX · 须点名且不得偷开 R / 玩家面 KPI）

交付：只 docs；批准前禁改 YAML/Java；批准后若仅 docs 落模板可同窗施工号，**不得**借施工号开事件/调律/forge/走廊W2 等 R；若批 R/M+R，脚本另号、禁与战斗/经济 R 同号。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / **工坊 R（价表）** · **不**开深渊 R · **不**开周本 R · **不**开 Boss 预警 R · **不**开挂机 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**改 refineCost / qualityCost / enhance 价 · **不**抬掉率 / 新材料轨  
- **不**扩 Pack6 / 新模式 · **不**开六槽 · **不**抬体力  
- **不**重开 D307 / D308 / D309 主交付邻域  
- **不**把薄 UX / 菜单半行再拧当本窗主施工  
- **不**做玩家面遥测 KPI / 排行榜 / 成就  
- **不**写「无签字即可开 R」的自动建议逻辑  
- **不**写假「已施工 R / 样本已满可开」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 A 后，另派施工号（本荐 **M = docs-only 模板**；若批 R/M+R，**脚本另号** · 禁与样本战斗/经济 R 同号）。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D308：未满完整 P 周 / `runs`≥30；**不得开** |
| 走廊 W2 · **工坊 R（价）** · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | D308 全表不得开 |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 刚上线等人 |
| 组队 / 图录 / 钱包 / VIP / 技能页薄余感 | 已收或偏软；非最尖 |
| 副手菜单再拧半行 | D309 已关§8-5；再拧=薄 UX |
| 重开 D307 / D308 / D309 | 刚上线禁再拧 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / 六槽 / 抬体力掉落 | 硬禁 / 搁置 |
| 玩家面遥测 KPI | D298-W / D308 否决 |

---

## 6. 刚结指针

- D309 · [`STATUS-ember-offhand-merge-decision-d309.md`](STATUS-ember-offhand-merge-decision-d309.md) · docs-only · `e0e8ded6` · **§8-5 已关 · 方案 A · 本 tip 上游**  
- 副手 tip · [`STATUS-ember-next-hard-debt-offhand-merge-decision-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-offhand-merge-decision-need-design-2026-10-08.md) · 已关窗  
- D308 · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) · docs-only · `27750279` · **M 已落 · 方案 R 周报后置 → 本 tip**  
- 样本窗 tip · [`STATUS-ember-next-hard-debt-sample-window-readiness-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-sample-window-readiness-need-design-2026-10-08.md)  
- D307 · [`STATUS-ember-workshop-menu-honesty-d307.md`](STATUS-ember-workshop-menu-honesty-d307.md) · CoreRpg **1.65.97** · **≠ forge R**  
- D306–D301 · 枢纽 / 挂机 / 预警 / 周本 / 深渊 / 守招 · 见各 STATUS  
- D298 · [`STATUS-ember-playfeel-telemetry-d298.md`](STATUS-ember-playfeel-telemetry-d298.md) · 证据管已收 · 周文件真源  
- 灰印 T0b **HOLD** · 天赋 HOLD  
- 加固债总览 · [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md) · §1～§8-5 **主债已关**

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。样本门禁 R 全表不得开——本 tip 只派周报模板（D308 方案 R 后置），不开任一 R。*
