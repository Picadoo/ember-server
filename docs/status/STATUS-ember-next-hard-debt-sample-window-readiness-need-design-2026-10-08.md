# 状态 · 下一档硬债选定 · 需策划（样本窗就绪清单 / 何时再开 R）

> **上游结案：** D307 工坊菜单诚实方案 **M 已上线**（CoreRpg **1.65.97** · `3c04b9d6`）；加固稿 **§1A–§1C 主债 M 波已清**；战斗可感波 D301–D304、挂机 D305、枢纽 D306、工坊菜单 D307 已收。Live **D301–D307**。灰印副招 **T0/T0b ❌ · HOLD**；天赋换机制 **HOLD**；事件 R/W / 调律 R / **工坊 R（价/节奏）** / 走廊 W2 / 深渊 R / 周本 R / Boss 预警 R / 挂机 R 均等 **人感或 D298 周样本**。见 [`STATUS-ember-workshop-menu-honesty-d307.md`](STATUS-ember-workshop-menu-honesty-d307.md)。

**日期：** 2026-10-08（上海时间）  
**上游：** D307 工坊菜单诚实已收 · 加固 §1 主循环/枢纽/挂机软债 M 波已清 · 非样本门禁薄体验债（走廊/深渊/团本/预警/挂机层/日路由/工坊菜单）已连收 · 灰印/天赋 HOLD · **余下真硬战斗/经济债几乎全是样本门禁 R**  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up  
**硬规格（已上线 D308 · docs-only）：** [`DESIGN-ember-sample-window-readiness-2026-10-08.md`](../design/DESIGN-ember-sample-window-readiness-2026-10-08.md) · STATUS=已批 A · 批 M · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) · backlog `B-sample-window-readiness`  
**打开理由：** D298 遥测管已上线，但各 R 窗「何时算样本够、谁签字再开、最小门槛是什么」仍散落在各 DESIGN 后置句里——总控/策划容易误把「人感一句」或「薄 UX 余感」当成可开 R 的门槛。要在**不开任何战斗/经济 R、不改玩法数值**前提下，把**样本窗就绪清单 + 再开门禁**写成可批硬规格（docs-only）。

---

## 0. 局势一句话（样本门禁声明）

加固稿 §1 与玩法可感 M 波（D283–D307 主链）已收；仍开的「真硬」若只论战斗压/价压，几乎全是 **事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊/周本/Boss/挂机 R**（等人感或 D298 周样本）。本 tip **不**把其中任一单独采纳为下一档施工主债，也 **不**把组队/图录/钱包/VIP/技能页薄余感抬成硬债；改采纳 **样本窗就绪清单 / 何时再开 R**（docs-only · 零玩法改），诚实承认：**战斗/经济 R 等真人周样本，本窗只定门禁与读数口径**。

---

## 1. 选题（八选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D298 tip：须先攒 1～2 周真人样本；硬禁未解除；**本窗不偷开** |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / **工坊方案 R（价/节奏）** / 深渊 **R** / 周本 **R** / Boss 预警 **R** / 挂机 **R** | **否** | 均样本/人感门禁；D300–D307 刚上或近上 M，R 不抢；**工坊 R ≠ 已收的菜单诚实 D307** |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / 六槽 / 新模式 / 抬体力掉落 | **否** | 硬禁 / 搁置 |
| F | 组队·结算余感 / 图录↔D304 预警对齐 / 经济钱包显示 / VIP·战令余感 / 技能页余感 | **否（本窗主交付）** | 结算 D283/D295 已有摘要，组队余感偏软；图录 B2.178+D304 分轨，对齐属薄维护；钱包/付费 D284 + 经济 B2.xx 已大量诚实；VIP/pass 入口已挡发文案；技能页 D287/D290 已收——**均非当前最尖硬债** |
| G | 重开 D307 / 加固 §1 纯 STATUS 同步 | **否** | 刚上线禁再拧；同步可旁注，不占硬规格主交付 |
| **H** | **样本窗就绪清单 / 何时再开 R（docs-only）** | **采纳 · 需策划** | D298 已造证据管，缺的是**跨债统一的再开门禁表**（最小局数/周数、合格重打定义、排除测试号、OP 读数口径、签字人）；零玩法改、零样本门禁偷开；为后续任一 R 窗提供可批开关条件 |

**对齐：** D298 `/corerpg p1 telemetry` · `p1-telemetry/<week>.yml` · 事件/调律/forge/走廊 W2/深渊·周本·Boss·挂机各稿「R 后置」句 · 灰印/天赋 HOLD · ARCH 禁 Pack6/六槽 · **D307 刚收 · 禁重开**。

**未采纳备选说明：** 「组队/图录/钱包/VIP/技能页」仅当仍有尖锐玩家面缺口时兜底——现网核查均为已收或偏软维护；**战斗/经济 R 等直播样本**，本窗不改派去「假装再开 R」。若策划交稿时发现某薄 UX 仍尖，可在批注勾「驳回改派」另选题，**不得**借机开 forge R / 事件 R。

---

## 2. 一句话问题

遥测管已有；各 R 窗「样本够不够、谁说了算、最小门槛是什么」仍散落——要在**不开战斗/经济 R、不改数值**前提下，把**就绪清单 + 再开门禁**写成可批硬规格。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】样本窗就绪清单 / 何时再开 R（docs-only）
优先级：D307 后下一档证据门禁硬债 · 非施工玩法窗 · §1 M 波已收 · 样本门禁 R 不偷开 · 非灰印/天赋续跑 · 非重开 D307 · 非薄 UX 余感抬硬
禁：六槽·Pack6·新模式图包·抬体力/掉率/event_rate/ALTS·改 refineCost/qualityCost/enhance 价表（=偷开 forge R）·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调·事件R/W施工·调律R施工·工坊R施工·走廊W2夹带·深渊R/周本R/Boss预警R/挂机R施工·重开D307·纯lore·玩家面遥测KPI/排行榜
目标：一张可批表写清「哪些 R 窗仍等人感/周样本、最小门槛、读哪条遥测、谁签字再开」；本窗零玩法改、零数值改

请出 docs/design/DESIGN-ember-sample-window-readiness-2026-10-08.md，文首 STATUS=待批 A，含：

0. 证据：D298 已落指标/命令/落盘路径；事件 mustfeel / 调律 / forge R / 走廊 W2 / 深渊·周本·Boss·挂机各稿「R 后置」原文；为何组队/图录/钱包/VIP/技能页本窗不抬硬；为何不重开 D307；灰印/天赋为何 HOLD
1. 玩家感知目标（≤3 条）：对玩家——本窗无新感知（零玩法）；对总控/策划——能回答「现在能不能批开某 R」；对 OP——知道读 `/corerpg p1 telemetry` 哪几行才算数
2. 方案表（至少 2 选 1 荐）：
   - 方案 M（荐）：统一「样本窗就绪清单」硬表（按债行列：事件R/W · 调律R · 工坊R · 走廊W2 · 深渊R · 周本R · Boss预警R · 挂机R）+ 每行：最小合格重打局数或周滚、关键指标阈值口径（可「观察性、非硬砍」）、排除测试号规则、签字人（总控批 A）、与 D298 键名对照；附「何时仍不得开」红线；零 jar/yml 玩法改（若需补 OP 帮助文案/docs 旁注可同批，禁玩家面）
   - 方案 R：在 M 表之上加「自动周报模板」（从 p1-telemetry 周文件生成 markdown 检查单）——仍零玩法；若要脚本落地须另派施工号且禁改战斗/经济
   - 方案 W（否决默认）：未满样本就开任一战斗/经济 R，或把组队/图录/钱包薄余感改派成「假硬债」挡着样本窗 —— 写清否决理由
3. 不动清单与门禁：体力/掉落/event_rate/ALTS；价表；六槽；Pack6；天赋轨；灰印轨；守招邻域；**任一 R 窗施工本体**；D307 主交付。零战斗/零经济压 **不跑 p1sim**；本窗默认不部署
4. 批注勾选：批 M / 批 R / 批 M+R / 驳回改派（其它薄 UX · 须点名且不得偷开 R）

交付：只 docs；批准前禁改 YAML/Java；批准后若仅 docs 落清单可同窗施工号（建议 D308+ docs-only），**不得**借施工号开事件/调律/forge/走廊W2 等 R。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / **工坊 R（价表）** · **不**开深渊 R · **不**开周本 R · **不**开 Boss 预警 R · **不**开挂机 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**改 refineCost / qualityCost / enhance 价 · **不**抬掉率 / 新材料轨  
- **不**扩 Pack6 / 新模式 · **不**开六槽 · **不**抬体力  
- **不**重开 D307 主交付邻域  
- **不**把组队/图录/钱包/VIP/技能页薄余感当本窗主施工  
- **不**写假「已施工 R」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 A 后，另派施工号（建议 **D308+**；本荐 M **docs-only / 零 p1sim**）。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D298 后须 1～2 周真人样本；证据未满；**等本清单门禁写清后再议** |
| 走廊 W2 · **工坊 R（价）** · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | 样本/人感门禁；D300–D307 M 刚收或近收 |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 刚上线等人 |
| 组队 / 结算余感（再扩摘要） | D283/D295 已有摘要；组队余感偏软 |
| 图录 ↔ D304 预警对齐 | 图录静态 + 局内短签分轨；对齐偏维护薄窗 |
| 经济钱包显示诚实 | B2.xx / D284 已大量口径诚实；非最尖缺口 |
| VIP / 战令入口余感 | D284 + shop/pass 挡发文案已落；非新硬债 |
| 技能页余感 | D287/D290 已收二选一/深链 |
| 重开 D307 / 加固 §1 纯 STATUS 同步 | 刚上线禁再拧；同步可旁注 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / 六槽 / 抬体力掉落 | 硬禁 / 搁置 |

---

## 6. 刚结指针

- D307 · [`STATUS-ember-workshop-menu-honesty-d307.md`](STATUS-ember-workshop-menu-honesty-d307.md) · CoreRpg **1.65.97** · `3c04b9d6` · **工坊菜单诚实已收 · ≠ forge R**  
- 工坊 tip（已收为 D307）· [`STATUS-ember-next-hard-debt-workshop-menu-honesty-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-workshop-menu-honesty-need-design-2026-10-08.md) · `ba0ac686`  
- D306 · [`STATUS-ember-hub-daily-routing-d306.md`](STATUS-ember-hub-daily-routing-d306.md) · CoreRpg **1.65.96** · **§1B 已收**  
- D305 · [`STATUS-ember-hang-farm-soft-identity-d305.md`](STATUS-ember-hang-farm-soft-identity-d305.md) · CoreRpg **1.65.95**  
- D304 · [`STATUS-ember-boss-telegraph-honesty-d304.md`](STATUS-ember-boss-telegraph-honesty-d304.md) · CoreRpg **1.65.94**  
- D303 · [`STATUS-ember-weekly-raid-feel-diff-d303.md`](STATUS-ember-weekly-raid-feel-diff-d303.md) · CoreRpg **1.65.93**  
- D302 · [`STATUS-ember-abyss-feel-diff-d302.md`](STATUS-ember-abyss-feel-diff-d302.md) · CoreRpg **1.65.92**  
- D301 · [`STATUS-ember-guard-skill-parry-d301.md`](STATUS-ember-guard-skill-parry-d301.md) · CoreRpg **1.65.91**  
- D298 · [`STATUS-ember-playfeel-telemetry-d298.md`](STATUS-ember-playfeel-telemetry-d298.md) · CoreRpg **1.65.88** · **证据管已收 · 本 tip 定再开门禁**  
- 灰印 T0b **HOLD** · [`STATUS-ember-ash-imprint-t0b-2026-10-08.md`](STATUS-ember-ash-imprint-t0b-2026-10-08.md)  
- 天赋 HOLD · [`STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md`](STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md)  
- 加固债总览 · [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md) · §1 主债 M 波经 D300–D307 已收

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。战斗/经济 R 等直播样本——本窗只写就绪清单。*
