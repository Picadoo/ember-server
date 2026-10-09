# 状态 · 下一档硬债选定 · 需策划（样本窗周报脚本规格 · D310 方案 R 后置）
> **旁注（已关 · 已落仓 D311）：** tip `STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md` 已关；指向 STATUS [`STATUS-ember-sample-week-report-script-d311.md`](STATUS-ember-sample-week-report-script-d311.md) · 样本窗周报脚本已落仓 · **≠关观察** · **≠开样本 R** · **≠自动开闸**。

> **上游结案：** D310 样本窗周报模板方案 **M 已落仓**（docs-only · `1cdbc233`）；可复用检查单 [`STATUS-ember-sample-week-report-checklist-d310.md`](STATUS-ember-sample-week-report-checklist-d310.md) 已入库；DESIGN §2.2 **方案 R（脚本）仍后置**；D308 门槛数字 **未改**；战斗/经济 R **全表不得开**。见 [`STATUS-ember-sample-week-report-d310.md`](STATUS-ember-sample-week-report-d310.md) · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md)。

**日期：** 2026-10-08（上海时间）  
**上游：** D310 周报检查单已收（R 脚本后置）· D309 副手关窗 · D308 就绪硬表已收 · §1 M 波 + D301–D307 已收 · 样本 R 全表不得开 · 灰印副招 **T0/T0b ❌ · HOLD** · 天赋换机制 **HOLD** · Pack6 / 六槽 **HOLD**  
**本窗性质：** tip 已关 · 已落仓 D311 · **零改本号** · **不开样本 R**
**硬规格（已批 A · 已落仓 D311）：** [`DESIGN-ember-sample-week-report-script-2026-10-08.md`](../design/DESIGN-ember-sample-week-report-script-2026-10-08.md) · STATUS=已批 A · 批 M+R · [`STATUS-ember-sample-week-report-script-d311.md`](STATUS-ember-sample-week-report-script-d311.md) · 脚本 [`tools/p1-telemetry-week-report.py`](../../tools/p1-telemetry-week-report.py) · backlog `B-sample-week-report-script`  

> **策划交稿旁注（2026-10-08 Asia/Shanghai）：** 硬规格已交 [`DESIGN-ember-sample-week-report-script-2026-10-08.md`](../design/DESIGN-ember-sample-week-report-script-2026-10-08.md) · 曾 STATUS=**待批 A** · 荐方案 M · 可选 R/M+R · **W 否决** · 门槛钉 D308 **未改**。  

> **关窗（2026-10-08 Asia/Shanghai）：** 总控 **批 A · 批 M+R · 同号 D311** · 契约 docs + [`tools/p1-telemetry-week-report.py`](../../tools/p1-telemetry-week-report.py) 已落 · [`STATUS-ember-sample-week-report-script-d311.md`](STATUS-ember-sample-week-report-script-d311.md) · 门槛钉 D308 **未改** · **不开任一 R** · 禁自动开闸 · 禁玩家面 KPI。

> **关窗：** [`DESIGN-ember-sample-week-report-script-2026-10-08.md`](../design/DESIGN-ember-sample-week-report-script-2026-10-08.md) · **STATUS 已批 A · 批 M+R · 同号 D311** · 契约 docs + `tools/p1-telemetry-week-report.py` 已落 · [`STATUS-ember-sample-week-report-script-d311.md`](STATUS-ember-sample-week-report-script-d311.md)；门槛钉 D308 **未改**；W 否决 · tip/backlog/D310 旁注已同步 · **禁**借号开任一 R / 自动开闸 / 玩家面 KPI。

**打开理由：** D310 已落手填检查单；OP 仍要手抄 `p1-telemetry/<week>.yml` 数字。D310 DESIGN §2.2 / 批注明文 **方案 R 脚本后置**——本 tip 把它升为下一档 docs/tools 硬债：**写清脚本契约**（读 yml → 同结构检查单 md；D/E/G 人感与签字留空；禁无总控签字输出「建议立即开 X R」），**仍零玩法、禁自动开闸、禁玩家面 KPI、禁改 D308 门槛数**。

---

## 0. 局势一句话（样本门禁声明）

D310 M 检查单已是手填真源；D308 硬表仍钉「当前全表不得开」。加固 1→5（含§8-5）已关。下一档若去偷开战斗/经济 R / 改 D308 门槛 / 天赋灰印 / Pack6 六槽 / 重开 D307–D310 = 假活。本 tip **不**开任一 R，也 **不**把薄 UX 抬成硬债；改采纳 **周报脚本规格（D310 方案 R 后置）**——从周遥测 yml 出同结构检查单（docs 契约；脚本可同批或另号落地），**禁**无总控签字自动建议开 R，**禁**玩家面看板。

---

## 1. 选题（八选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D308 硬表：须完整 P 周 + `p1_pf_runs`≥30；**当前不得开** |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / **工坊方案 R（价/节奏）** / 深渊 **R** / 周本 **R** / Boss 预警 **R** / 挂机 **R** | **否** | D308 全表不得开；**禁偷开** |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / **六槽** / 新模式 / 抬体力掉落 | **否** | 硬禁 / 搁置 |
| F | 组队·结算余感 / 图录对齐 / 经济钱包 / VIP·战令 / 技能页 / 副手菜单再拧半行 | **否（本窗主交付）** | 薄 UX / 已收诚实窗；**非当前最尖硬债**；勿挡周报脚本 |
| G | 重开 D307–D310 / 改 D308 门槛数字 / 加固 §1 纯 STATUS 同步当主债 | **否** | 刚上线禁再拧；门槛钉死；同步可旁注，不占硬规格主交付 |
| **H** | **样本窗周报脚本规格（D310 方案 R 后置 · docs/tools 契约）** | **采纳 · 需策划** | 范围：把 D310 DESIGN §2.2 写成可批可跑契约——`tools/p1-telemetry-week-report.py` 读 `p1-telemetry/<week>.yml`（可选 JSON）→ 吐同结构检查单 md（预填 A–C；**D/E/G 人感与签字栏留空**）；输出只得门槛勾选+当前态摘要；**禁**「建议立即开 X R」无签字占位；零玩法；禁玩家面 KPI；禁改 D308 门槛数 |

**对齐：** D310 DESIGN §2.2 方案 R 后置 · D310 检查单已落 · D308 §2.1 门槛钉死 · D298 `/corerpg p1 telemetry` · `p1-telemetry/<week>.yml` · 灰印/天赋 HOLD · ARCH 禁 Pack6/六槽 · D309 方案 A 关窗 · **D307–D310 刚收 · 禁重开玩法**。

**未采纳备选说明：** 样本门禁 R 等直播周样本——本窗**绝不**改派去「假装再开 R」。薄 UX 若仍尖，可在批注勾「驳回改派」另选题，**不得**借机开 forge R / 事件 R / 六槽 / 玩家面遥测 KPI / 改门槛数。

---

## 2. 一句话问题

D310 手填检查单已有；OP 仍要手抄 `p1-telemetry/<week>.yml`——要在**不开战斗/经济 R、不改 D308 门槛、不自动开闸**前提下，把**周报脚本契约**（读 yml → 同结构 md；人感/签字留空；禁无签字「建议开 R」）写成可批硬规格。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】样本窗周报脚本规格（D310 方案 R 后置 · docs/tools）
优先级：D310 后下一档证据工具硬债 · 非施工玩法窗 · D310 M 检查单已收 · 样本门禁 R 不偷开 · 非灰印/天赋续跑 · 非重开 D307–D310 · 非薄 UX 余感抬硬 · 零玩法 · 禁玩家面 KPI · 禁改 D308 门槛数
禁：六槽·Pack6·新模式图包·抬体力/掉率/event_rate/ALTS·改 refineCost/qualityCost/enhance 价表（=偷开 forge R）·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调·事件R/W施工·调律R施工·工坊R施工·走廊W2夹带·深渊R/周本R/Boss预警R/挂机R施工·重开D307/D308/D309/D310·纯lore·玩家面遥测KPI/排行榜/成就·无总控签字自动「建议开 R」变施工·改 D308 §2.1 门槛数字
目标：一张可批 DESIGN（新建 DESIGN-ember-sample-week-report-script-2026-10-08.md，或修订 D310 DESIGN §2.2 成可跑脚本契约）写清「脚本路径、入参、出参 md 结构、预填哪些栏、哪些必须留空、禁止句、退出码、落地是否另号」；本窗零玩法改、零数值改；脚本本身不得自动开闸

请出 docs/design/DESIGN-ember-sample-week-report-script-2026-10-08.md（或明确「修订 D310 DESIGN §2.2」为唯一交付），文首 STATUS=待批 A，含：

0. 证据：D310 DESIGN §2.2 方案 R 原文（tools/p1-telemetry-week-report.py；读 p1-telemetry/<week>.yml → 同结构检查单；预填 A–C；D/E/G 留空；禁「建议立即开 X R」无签字占位；脚本另号；禁与战斗/经济 R 同号）；D310 STATUS「脚本方案 R 仍后置」+ 检查单真源 STATUS-ember-sample-week-report-checklist-d310.md；D308 §2.1 门槛钉死（本契约不得改数）；D298 周文件路径 + /corerpg p1 telemetry 键名；为何本窗不开任一战斗/经济 R；为何不重开 D307–D310；灰印/天赋为何 HOLD
1. 玩家感知目标（≤3 条）：对玩家——本窗无新感知（零玩法、无 KPI 板）；对总控/策划——脚本吐出的检查单勾完仍回答「本周样本满不满、能否进入某债待批 A」（满 ≠ 自动施工）；对 OP——一条命令少手抄，签字栏与人感栏仍须人工+总控
2. 方案表（至少 2 选 1 荐）：
   - 方案 M（荐）：**只 docs 脚本契约**——把 §2.2 伪接口写成可验收规格：CLI（--yml / 可选 --json-log / --out）、必读键（week·counts.p1_pf_*）、派生两率公式（对照 D298/D310）、预填 A–C 勾选逻辑（对照 D308：未满则标不得开）、**D/E/G 人感与签字栏必须留空**、禁输出句清单（含「建议立即开 X R」）、退出码（0=可读；非0=缺文件/缺 counts）、输出 md 必须含签字栏空位；**零 jar/yml 玩法**；脚本代码本窗不写；落地可同窗 docs 施工号或后置
   - 方案 R：在 M 契约之上 **docs+落脚本** `tools/p1-telemetry-week-report.py`——仍零玩法；输出只得门槛勾选+当前态摘要；**禁**无签字「建议立即开 X R」；**禁**与战斗/经济 R 同施工号；可勾 **批 R** 或 **批 M+R**（契约同批 docs，脚本可同号或另号，须在稿内钉死）
   - 方案 W（否决默认）：脚本/周报**自动建议开 R**且无总控签字即可施工；或做成玩家面 KPI/排行榜；或借本窗偷开任一战斗/经济 R / 改 D308 门槛 / 重开 D307–D310 —— 写清否决理由
3. 不动清单与门禁：体力/掉落/event_rate/ALTS；价表；六槽；Pack6；天赋轨；灰印轨；守招邻域；**任一 R 窗施工本体**；D307–D310 主交付；D308 §2.1 门槛数字；玩家面遥测 KPI。零战斗/零经济压 **不跑 p1sim**；本窗默认不部署玩法。检查单结构**以 D310 检查单为准**（脚本只填数字栏，不发明新栏目/新门槛）
4. 批注勾选：批 M / 批 R / 批 M+R / 驳回改派（其它薄 UX · 须点名且不得偷开 R / 玩家面 KPI / 改门槛）

交付：批准前禁改 YAML/Java、禁写脚本进仓（若仅批 M）；批准后若批 M = docs-only 施工号落契约；若批 R/M+R = docs/tools only，**不得**借施工号开事件/调律/forge/走廊W2 等 R。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / **工坊 R（价表）** · **不**开深渊 R · **不**开周本 R · **不**开 Boss 预警 R · **不**开挂机 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**改 refineCost / qualityCost / enhance 价 · **不**抬掉率 / 新材料轨  
- **不**扩 Pack6 / 新模式 · **不**开六槽 · **不**抬体力  
- **不**重开 D307 / D308 / D309 / D310 主交付邻域 · **不**改 D308 门槛数字  
- **不**把薄 UX / 菜单半行再拧当本窗主施工  
- **不**做玩家面遥测 KPI / 排行榜 / 成就  
- **不**写「无签字即可开 R」的自动建议逻辑  
- **不**写假「已施工 R / 样本已满可开」报告  
- **不**在本 tip 窗提前落 `tools/p1-telemetry-week-report.py`（等批 A）  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 A 后，另派施工号（本荐 **M = docs-only 契约**；若批 R/M+R，**docs/tools only** · 禁与样本战斗/经济 R 同号）。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D308：未满完整 P 周 / `runs`≥30；**不得开** |
| 走廊 W2 · **工坊 R（价）** · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | D308 全表不得开 |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 刚上线等人 |
| 组队 / 图录 / 钱包 / VIP / 技能页薄余感 | 已收或偏软；非最尖 |
| 副手菜单再拧半行 | D309 已关§8-5；再拧=薄 UX |
| 重开 D307 / D308 / D309 / D310 · 改 D308 门槛 | 刚上线禁再拧；门槛钉死 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / 六槽 / 抬体力掉落 | 硬禁 / 搁置 |
| 玩家面遥测 KPI | D298-W / D308 / D310 否决 |

---

## 6. 刚结指针

- D310 · [`STATUS-ember-sample-week-report-d310.md`](STATUS-ember-sample-week-report-d310.md) · docs-only · **批 M · 检查单已落 · 方案 R 后置 → 本 tip** · `1cdbc233`  
- 周报 tip · [`STATUS-ember-next-hard-debt-sample-week-report-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-sample-week-report-need-design-2026-10-08.md) · 已关窗（批 M · D310）  
- D309 · [`STATUS-ember-offhand-merge-decision-d309.md`](STATUS-ember-offhand-merge-decision-d309.md) · docs-only · `e0e8ded6` · **§8-5 已关 · 方案 A**  
- D308 · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) · docs-only · `27750279` · **M 已落 · 门槛钉死 · 当前全表不得开**  
- D307 · [`STATUS-ember-workshop-menu-honesty-d307.md`](STATUS-ember-workshop-menu-honesty-d307.md) · CoreRpg **1.65.97** · **≠ forge R**  
- D306–D301 · 枢纽 / 挂机 / 预警 / 周本 / 深渊 / 守招 · 见各 STATUS  
- D298 · [`STATUS-ember-playfeel-telemetry-d298.md`](STATUS-ember-playfeel-telemetry-d298.md) · 证据管已收 · 周文件真源  
- 灰印 T0b **HOLD** · 天赋 HOLD  
- 加固债总览 · [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md) · §1～§8-5 **主债已关**

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。样本门禁 R 全表不得开——本 tip 只升周报脚本规格（D310 方案 R 后置 · docs/tools · 禁自动开闸 · 禁玩家面 KPI · 禁改门槛）。*
