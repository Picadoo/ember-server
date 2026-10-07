# 样本窗周报模板（D308 方案 R 后置 · docs 检查单）· 2026-10-08

> **STATUS：已批 A**（总控 · 2026-10-08 Asia/Shanghai）  
> **日期：** 2026-10-08 Asia/Shanghai  
> **来源：** 总控派单「硬设计待批 A」· tip [`STATUS-ember-next-hard-debt-sample-week-report-need-design-2026-10-08.md`](../status/STATUS-ember-next-hard-debt-sample-week-report-need-design-2026-10-08.md)（`adb09d7d`）· D309 副手关窗后下一档**证据工具**硬债 · 对齐 D308 方案 **R 后置**  
> **性质：** 硬设计 **已批 A**；本批注仍 **只 docs**；施工号 **D310**（**docs-only** · 落可复用周报检查单 + tip/backlog/D308 STATUS 同步 · 禁自动开闸 · 禁开任一 R · 禁改 D308 门槛数 · 脚本方案 R 仍后置）。  
> **批注：** 总控批 · 采纳 **方案 M**（只 docs 周报 markdown 检查单 + 空周/半满周示例）；**未勾**批 R / M+R（脚本另号后置）；**W 否决**。  
> **硬约束：** **六槽不做** · **禁 Pack6 / 新模式图包** · **禁抬体力 / 掉率 / event_rate / ALTS** · **禁改 refineCost / qualityCost / enhance 价表（= 偷开 forge R）** · **禁天赋续跑（HOLD）** · **禁灰印续跑（HOLD）** · **禁守招邻域再调** · **禁偷开：事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R** · **禁重开 D307 / D308 / D309** · **禁纯 lore** · **禁玩家面遥测 KPI / 排行榜 / 成就** · **禁无总控签字自动「建议开 R」变施工** · 门槛口径**以 D308 DESIGN 为准（本模板不得改门槛数字）** · 玩家面不写「请执行 /corerpg p1 telemetry …」。  
> **Backlog 指针：** `B-sample-week-report` · [`STATUS-ember-sample-week-report-d310.md`](../status/STATUS-ember-sample-week-report-d310.md) · 检查单 [`STATUS-ember-sample-week-report-checklist-d310.md`](../status/STATUS-ember-sample-week-report-checklist-d310.md)  
> **邻域边界：** D308 就绪硬表 **已批 M · 当前全表不得开**（周报模板后置 → **本页已批 M · D310 已落**）；D298 证据管 **已收**（只读复用）；D309 副手方案 A **已关 §8-5**；灰印/天赋 **HOLD**。

---

## 0. 证据（D308 方案 R 后置 · STATUS · D298 读数 · 为何不开 R / 不重开 D307–D309 / HOLD）

| # | 证据 | 出处 | 对本稿含义 |
|---|------|------|------------|
| E1 | **D308 DESIGN §2.2 方案 R 原文：**「从 `p1-telemetry/<week>.yml`（及可选 JSON 旁路）生成 markdown **检查单**（勾选：周滚是否满、`runs` 是否≥30、事件两率、调律两率、排除表是否脏、各 R 红线、签字栏）」；**仍禁**改战斗/经济数值、玩家面看板、自动「建议开 R」变成无签字施工；脚本进仓须**另派施工号**（docs/tools only）；**禁**与事件/调律/forge 等同号；相对 M：「M 已够总控手查；R 减 OP 抄写成本。**不荐纯 R 首批**；可勾 **批 M+R**」 | [`DESIGN-ember-sample-window-readiness-2026-10-08.md`](DESIGN-ember-sample-window-readiness-2026-10-08.md) §2.2 | **本页 = 把后置 R 写成可批模板**；门槛数字**不得改**；脚本另号 |
| E2 | **D308 STATUS「周报模板后置」：** 方案 R 未勾 M+R；下一窗 tip 已升为 `B-sample-week-report`；**禁**与战斗/经济 R 同号 · **禁**无签字自动开闸 · **禁**玩家面 KPI | [`STATUS-ember-sample-window-readiness-d308.md`](../status/STATUS-ember-sample-window-readiness-d308.md) | tip/`adb09d7d` 正式派本债；**仍零玩法** |
| E3 | **D298 周文件路径：** `plugins/CoreRpg/p1-telemetry/<week>.yml`（`week` = P 周 `weekKey`）；文件字段：`week` · `updated` · `note` · `counts.<p1_pf_*>`；W1c 排除 `leaderboard_exclude` ∪ `telemetry.exclude_uuids`；旁路 `[P1 pf] {json…}` | [`DESIGN-ember-playfeel-telemetry-2026-10-07.md`](DESIGN-ember-playfeel-telemetry-2026-10-07.md) · [`STATUS-ember-playfeel-telemetry-d298.md`](../status/STATUS-ember-playfeel-telemetry-d298.md) · CoreRpg **1.65.88** · `EmberPlayfeelTelemetry.logServerSnap` | 周报**真源**；模板/脚本只读，不改键名 |
| E4 | **D298 命令与键名：** `/corerpg p1 telemetry [player\|server]`（`corerpg.admin`）；分母 `p1_pf_runs`；`p1_pf_wall` / `whiff` / `break`；`p1_pf_evt_roll` / `evt_ok`；`p1_pf_sig_wear` / `sig_alt`；`p1_pf_vb_hit`；派生：出房率=`evt_roll`/`runs`、成功率=`evt_ok`/`max(evt_roll,1)`、佩戴率=`sig_wear`/`runs`、调律率=`sig_alt`/`max(sig_wear,1)`（及 /`runs`） | D298 DESIGN §3 · STATUS W1a/W1b | OP 填表对照；**观察性、非硬砍** |
| E5 | **D308 §2.1 门槛（本模板原样引用 · 不得改数）：** 战斗向最小周滚 = **≥1 个完整 P 周** 真人样本，且该周全服（排除测试号）`p1_pf_runs` **≥ 30**；荐观察满 **2** 个完整 P 周再批事件/调律 R；编排/演出/挂机类 = ≥3 非测试人感 **或** 总控记档 + 距对应 M **≥3 自然日**；签字人 = **总控批 A**；当前态（2026-10-08）**全表不得开** | D308 DESIGN §2.1.1–§2.1.2 | 周报只**勾对照**，不发明新门槛 |
| E6 | **D308 §2.1.3 红线（任一触发 → 禁止批该 R）：** 样本周未满；刚收 M 不足 3 日；排除表脏；薄 UX 当开闸；偷换 forge R；玩家面遥测；HOLD 轨夹带；日刷 `pf_runs` 冒充深渊/周本/挂机；无总控批 A；重开 D307 | D308 DESIGN §2.1.3 | 检查单必须有红线勾选栏 |
| E7 | tip 否 A–G：样本门禁 R / 天赋灰印 / Pack6 六槽 / 薄 UX / 重开 D307–D309 = 假活或刚收禁再拧 | tip §1 | **本窗只做周报模板** |
| E8 | 灰印 T0/T0b **❌ · HOLD**；天赋换机制 **HOLD**（禁 T0'''） | tip · backlog | **本窗不续跑** |
| E9 | D309 副手方案 A 已关加固 §8-5；D307 工坊菜单诚实刚收（≠ forge R） | STATUS D309 / D307 | **禁重开**；周报 ≠ 改价 / 并轨 |

**为何本窗不开任一战斗/经济 R：**  
D308 硬表当前全表「未满 / 不得开」；本债是 **docs 证据工具**（检查单），零率/价/房压。开闸仍须对照 D308 §2.1 + **总控批 A**——模板本身**不得自动开闸**。

**为何不重开 D307–D309：**  
刚落仓禁再拧；D307 ≠ forge R；D308 门槛口径勿借周报改数；D309 副手已关 §8-5。周报只链指针，不重开主交付。

**为何灰印/天赋 HOLD：**  
模拟未过 42±2 / 换机制轨停；再开须另起非通关率敏感规格，**不**占本证据工具窗。

**一句话问题：** D308 手查硬表已有；OP 仍要手抄 `p1-telemetry/<week>.yml`——要在**不开战斗/经济 R、不改数值、不自动开闸**前提下，把**周报 markdown 检查单**（模板；可选脚本路径）写成可批硬规格。

---

## 1. 玩家感知目标（≤3）

1. **对玩家：本窗无新感知。** 零玩法、零菜单改、零新 HUD；**不**出现玩家面遥测 KPI / 排行榜 /「请看周报」。  
2. **对总控/策划：周报勾完就能回答「本周样本满不满、能否进入某债待批 A」。** 对照 D308 §2.1 当前态 + 本检查单勾选；满 ≠ 自动施工，仍须该债硬设计 + 总控批 A。  
3. **对 OP：照模板填/生成，少手抄；签字栏仍须总控。** 读 `/corerpg p1 telemetry server` + `plugins/CoreRpg/p1-telemetry/<week>.yml`；脚本（若批 R）只吐「门槛勾选 + 当前态摘要」，**禁**输出「建议立即开 X R」而无签字占位。

---

## 2. 方案表

### 2.1 方案 M（**荐** · 只 docs 周报 markdown 模板 · 零 jar/yml 玩法）

**做什么：** 仓内落一份可复制的 **markdown 周报检查单**（下文 §2.1.1 全文草案）。OP/策划按周复制填勾；门槛、关键指标、红线、当前态表述**原样对照 D308 §2.1**（本模板**不得改门槛数字**）。**不含**「自动建议开 R」按钮文案；签字栏留空给总控。模板落地可同窗 **docs-only** 施工号。

**读数来源（只读）：**

| 来源 | 用途 |
|------|------|
| `/corerpg p1 telemetry server` | 全服本周合计摘要（排除测试号后） |
| `/corerpg p1 telemetry` / `<player>` | 点名抽查；**单号满 ≠ 全服满** |
| `plugins/CoreRpg/p1-telemetry/<week>.yml` | 周文件真源：`counts.p1_pf_runs` 等 |
| 可选旁路 `[P1 pf] {json…}` | 对账；非玩家面 |
| D308 DESIGN §2.1 硬表 | 门槛 / 关键指标 / 红线 / 当前态唯一口径 |

#### 2.1.1 周报检查单 markdown 草案（可整段复制）

> **落地（D310）：** 空白可填真源 → [`STATUS-ember-sample-week-report-checklist-d310.md`](../status/STATUS-ember-sample-week-report-checklist-d310.md)；下文草案保留对照；空周/半满周示例仍见 §2.1.2 / §2.1.3。

````markdown
# 余烬 · 样本窗周报检查单

> **周键（P 周 `weekKey`）：** ______________  
> **填表人（OP/策划）：** ______________  
> **填表日（Asia/Shanghai）：** ______________  
> **真源文件：** `plugins/CoreRpg/p1-telemetry/<week>.yml`  
> **命令对照：** `/corerpg p1 telemetry server`（须 `corerpg.admin`）  
> **口径声明：** 门槛数字以 [`DESIGN-ember-sample-window-readiness-2026-10-08.md`](DESIGN-ember-sample-window-readiness-2026-10-08.md) **§2.1 为准**；本单**不得改门槛**；**勾满 ≠ 自动开 R**。

---

## A. 周滚与分母

| # | 检查项 | 读哪 | 门槛（D308 原样） | 本周实填 | 勾选 |
|---|--------|------|-------------------|----------|------|
| A1 | 是否为**完整 P 周**（非「仅日级」半周） | 日历 / `week` 字段 | 战斗向：≥1 完整 P 周（荐满 2 周再批事件/调律） | 是 / 否 · 第 __ 个完整周 | [ ] |
| A2 | 全服 `p1_pf_runs`（排除测试号后） | `counts.p1_pf_runs` / `telemetry server` | **≥ 30** | runs = ____ | [ ] |
| A3 | 合格重打口径仍为 q01–q07 已扣体力结算（通关+失败） | D298/D308 | 是 | 确认 / 有疑 | [ ] |
| A4 | 深渊/团本**未**误计入本分母冒充样本满 | D308 红线 8 | 未冒充 | 确认 | [ ] |

---

## B. 事件两率（观察性 · 非硬砍）

| # | 指标 | 公式 / 键 | 本周实填 | 勾选（已抄录） |
|---|------|-----------|----------|----------------|
| B1 | `evt_roll` / `evt_ok` | `p1_pf_evt_roll` · `p1_pf_evt_ok` | roll=____ ok=____ | [ ] |
| B2 | 出房率 | `evt_roll` / `runs` | ____% | [ ] |
| B3 | 成功率 | `evt_ok` / `max(evt_roll,1)` | ____% | [ ] |
| B4 | 人感「事件仍空/撞不到」记档？ | 总控/策划笔记 | 有 / 无 | [ ] |

> 事件 R/W 开闸仍须：A1+A2 满 + 人感记档 + **总控另批该债硬设计 A**。本单勾满**不**等于开闸。

---

## C. 调律两率（观察性 · 非硬砍）

| # | 指标 | 公式 / 键 | 本周实填 | 勾选（已抄录） |
|---|------|-----------|----------|----------------|
| C1 | `sig_wear` / `sig_alt` | `p1_pf_sig_wear` · `p1_pf_sig_alt` | wear=____ alt=____ | [ ] |
| C2 | 佩戴率 | `sig_wear` / `runs` | ____% | [ ] |
| C3 | 调律率（占佩戴） | `sig_alt` / `max(sig_wear,1)` | ____% | [ ] |
| C4 | 调律率（占局） | `sig_alt` / `runs` | ____% | [ ] |
| C5 | 人感「进本前看不见 / 懒得进调律页」？ | 笔记 | 有 / 无 | [ ] |

---

## D. 排除表是否脏

| # | 检查项 | 读哪 | 本周实填 | 勾选 |
|---|--------|------|----------|------|
| D1 | `telemetry.exclude_uuids` 已写入已知冒烟/压测 UUID？ | `ember-v1.yml` → `telemetry.exclude_uuids` | 已写 / 空 / 不全 | [ ] |
| D2 | `leaderboard_exclude` 与名缀（如 `FreshQ*`）是否仍生效？ | 配置 + 周文件 note | 是 / 否 | [ ] |
| D3 | 周文件是否明显被 bot 污染（runs 虚高且未排除）？ | 对照点名 telemetry | 干净 / **脏** | [ ] |

> 若 D3=脏 → **本周不得用全服合计开闸**（D308 红线 3）。

---

## E. 各 R 红线对照（D308 §2.1.2 当前态 · 2026-10-08 钉死「不得开」；升「满」须总控改表）

| 债 | 最小门槛摘要（D308 原样 · 勿改数） | 本周是否满 | 红线触达？ | 当前态勾选 |
|----|--------------------------------------|------------|------------|------------|
| 事件 R/W | ≥1 完整 P 周（荐 2）且 `runs`≥30 + 人感「空」 | 是/否 | 是/否 | [ ] **未满 · 不得开** |
| 调律 R | 同上周滚 + 人感「看不见/懒」 | 是/否 | 是/否 | [ ] **未满 · 不得开** |
| 工坊 R（forge 价） | ≥3 非测试人感「养不起」或总控记档 + D307≥3 日 + 经济草稿；**禁**借 D307 拧价 | 是/否 | 是/否 | [ ] **未满 · 不得开** |
| 走廊 W2 | ≥3 人感「一条廊」+ D300≥3 日；W2-D 须 p1sim 42±2 | 是/否 | 是/否 | [ ] **未满/等人感 · 不得开** |
| 深渊 R | ≥3 人感 + D302≥3 日；**不以** `pf_runs` 冒充 | 是/否 | 是/否 | [ ] **未满 · 不得开** |
| 周本 R | ≥3 人感 + D303≥3 日；不以日刷分母冒充 | 是/否 | 是/否 | [ ] **未满 · 不得开** |
| Boss 预警 R | ≥3 人感 + D304≥3 日 | 是/否 | 是/否 | [ ] **未满 · 不得开** |
| 挂机 R | ≥3 人感 + D305≥3 日；禁抬产能 | 是/否 | 是/否 | [ ] **未满 · 不得开** |

**通用红线速勾（任一 [x] → 本周禁止批对应 R）：**

- [ ] 样本周未满 / `runs`<30（战斗向）
- [ ] 对应 M 收仓不足 3 自然日
- [ ] 排除表脏 / 未配
- [ ] 仅因薄 UX（组队/图录/钱包/VIP/技能页）想「顺便」开 R
- [ ] 任何改 refine/quality/enhance/upgrade 价却挂菜单热修名义
- [ ] 玩家面遥测 KPI / 排行 / 成就挂钩提议
- [ ] 天赋 T0''' / 灰印续跑 / 守招再调 / Pack6 / 六槽 / 抬体力掉落夹带
- [ ] 用日刷 `pf_runs` 冒充深渊/周本/挂机样本满
- [ ] 无总控批 A 想自开 R
- [ ] 想重开 D307 / 改 D308 门槛数 / 重开 D309 副手

---

## F. 辅助观察（可选 · 不算开闸）

| 项 | 键 / 公式 | 本周实填 |
|----|-----------|----------|
| 破绽局均 wall/whiff/break | 各 / `runs` | ____ / ____ / ____ |
| `p1_pf_vb_hit` 率 | vb / runs | ____% |
| 备注（人感原话摘录） | — | |

---

## G. 签字栏（总控 · 必填才算「可进入某债待批 A」讨论）

| 角色 | 结论 | 签名 | 日 |
|------|------|------|-----|
| 填表人 | 本周样本：**未满** / **观察满（仍不得自动开）** / **有脏表** | ________ | ____ |
| 策划复核 | 同意上表勾选；**不**改门槛数字 | ________ | ____ |
| **总控** | [ ] 仅收悉周报 · [ ] 批准进入 **____** 债硬设计待批 A（点名债名）· [ ] 驳回（理由：________） | ________ | ____ |

> **禁止文案：** 本单**不得**出现「建议立即开事件 R / 调律 R / forge R …」而无上表总控签字占位。  
> **开闸路径：** 总控勾「批准进入某债待批 A」→ 另派该债 DESIGN → 再批 A → 另派施工号。周报施工号**不得**兼开战斗/经济 R。
````

#### 2.1.2 示例一：空周（当前态 · 2026-10-08 量级）

> 示意：D298 上线次日级；**非**完整 P 周；`runs` 远低于 30。

| 项 | 示例填 |
|----|--------|
| weekKey | （例）`2026W41` 半周 / 日级 |
| A1 完整 P 周 | **否** |
| A2 `p1_pf_runs` | 例：`4`（**< 30**） |
| B/C 两率 | 可抄但**观察意义弱** |
| D 排除表 | 若 `exclude_uuids: []` 且有冒烟 → 标「不全/脏风险」 |
| E 各 R | **全部勾「未满 · 不得开」** |
| G 总控 | **仅收悉**；**不**批准进入任一 R 债待批 A |

#### 2.1.3 示例二：半满周（仍不得开）

> 示意：已有接近完整周，但 `runs` 未达 30，或满 30 仍缺人感记档 / 第二周观察。

| 项 | 示例填 |
|----|--------|
| A1 | 是（第 1 个完整周） |
| A2 | 例：`22`（仍 **< 30**）→ A2 不勾 |
| 或 A2=`35` 且 A1 勾 | B/C 已抄录；B4/C5 人感 **无** → 事件/调律仍 **不得开** |
| E | 事件/调律仍标 **未满 · 不得开**（荐满 2 周）；工坊/走廊等仍等人感 |
| G | 可「收悉」；**不可**因半满周跳过签字直接施工 R |

**荐批：** **批 M**（只落 docs 模板 + 空周/半满周示例；零玩法）。

### 2.2 方案 R（M 模板之上 · tools 脚本路径 · 仍零玩法）

| 项 | 内容 |
|----|------|
| **做什么** | 在方案 M 同一检查单结构上，写清脚本落地路径：建议 `tools/p1-telemetry-week-report.py`（或 `.sh`）— **读** `plugins/CoreRpg/p1-telemetry/<week>.yml`（可选 stdin/JSON 旁路）→ **吐** 同结构 markdown 检查单（预填 A–C 数字与派生两率；D/E/G 人感与签字栏留空勾选） |
| **输出硬限** | **只得**「门槛勾选 + 当前态摘要」；预填「对照 D308：未满则标不得开」；**禁**输出「建议立即开 X R」而无总控签字占位行 |
| **仍禁** | 改战斗/经济数值；写玩家面看板；与事件/调律/forge/走廊等 R **同施工号** |
| **落地** | 脚本进仓须**另派施工号**（docs/tools only）；**不得**与本荐 M 的 docs 施工号兼开战斗 R |
| **相对 M** | M 已够手填；R 减抄写。可勾 **批 R**（仅脚本规格+后置）或 **批 M+R**（模板同批 docs，脚本另号） |

**脚本伪接口（规格级 · 本窗不写代码）：**

```
tools/p1-telemetry-week-report.py \
  --yml plugins/CoreRpg/p1-telemetry/<week>.yml \
  [--json-log optional] \
  --out docs/status/reports/sample-week-<week>.md
# 退出码：0=文件可读；非 0=缺文件/缺 counts
# stdout md 必须含 §G 签字栏空位；禁止「建议立即开」句
```

### 2.3 方案 W（**否决默认** · 自动建议开 R / 玩家面 KPI / 偷开 R）

| 项 | 内容 |
|----|------|
| **提议（不采纳）** | ① 周报或脚本**自动建议开 R**且无总控签字即可施工；② 把周报做成玩家面 KPI / 排行榜 / 成就；③ 借本窗偷开任一战斗/经济 R 或重开 D307–D309 |
| **否决理由** | ① D308 明文开闸须总控批 A；无签字自动建议 = 假开闸，破坏证据门禁；② D298-W / D308 已否决玩家面遥测；③ 当前全表不得开；偷开 = 无证据拧率/价/房压；重开刚收窗 = 假进度 |
| **结论** | **默认否决**；批注**不提供「批 W」勾选** |

---

## 3. 不动清单与门禁

| 项 | 本稿 |
|----|------|
| 体力消耗 / 日回 / 上限 | **不动** |
| 掉落 / `STAMP_RATE` / 成色权重 | **不动** |
| `event_rate` / ALTS / 烙印价 | **不动** |
| `refineCost` / `qualityCost` / enhance / upgrade 价表 | **不动**（= 禁偷开 forge R） |
| 六槽 / Pack6 / 新 kind / 新模式图包 | **不做** |
| 天赋轨 / 灰印轨 / 守招邻域 | **HOLD / 不夹带** |
| **任一 R 窗施工本体**（事件/调律/工坊/走廊W2/深渊/周本/Boss/挂机） | **本窗不开** |
| D307 / D308 / D309 主交付 | **不重开**；D308 门槛数字**不改** |
| 玩家面遥测 KPI / 排行榜 / 成就 | **否决** |
| **p1sim** | **不跑**（零战斗/零经济压） |
| **部署** | **默认不部署**（docs-only；若批 R 脚本另号仍零玩法部署） |
| 门槛口径 | **以 D308 DESIGN 为准**；本模板只引用、不改数 |

**本窗门禁结论：** 荐 **M** → 只交 docs 周报模板；批准后 docs-only 施工号可落模板，**不得**借号开 R。若批 **R / M+R**，脚本**另号**，禁与战斗/经济 R 同号。

---

## 4. 批注勾选（总控）

- [x] **批 M**（只 docs 周报 markdown 检查单 + 空周/半满周示例；零玩法；门槛钉 D308；**不含**自动建议开 R）→ 可同窗 docs 施工号 · **策划荐** · **已勾 · 施工 D310**
- [ ] **批 R**（仅脚本路径规格；落地另号；输出禁无签字「建议开 R」）· **后置（未勾）**
- [ ] **批 M+R**（模板同批 docs + 脚本路径同批规格；**脚本另号**落地）· **未勾**
- [ ] **驳回改派**（其它薄 UX · **须点名**且 **不得**偷开 R / 玩家面 KPI）· **未勾**

**否决：** 方案 W（自动建议开 R / 玩家面 KPI / 偷开 R / 重开 D307–D309）——无「批 W」勾选。

---

## 5. 切窗建议（批 A 后）

1. 文首改「已批 A」+ 勾选；**设计主交付表体勿改门槛口径**（除非总控改派）。  
2. **批 M 已落（D310）：** §2.1.1 → [`STATUS-ember-sample-week-report-checklist-d310.md`](../status/STATUS-ember-sample-week-report-checklist-d310.md)；STATUS / tip / backlog / D308 旁注已链 · **禁玩家面**。  
3. 若嗣后 **批 R / M+R**：脚本**另派施工号**；只读 yml→md；禁改战斗/经济；禁与样本 R 同号。  
4. **禁同塞：** 事件 R/W、调律 R、forge R、走廊 W2、深渊 R、周本 R、Boss R、挂机 R、天赋/灰印、Pack6、六槽、重开 D307–D309、玩家看板。  
5. 真开某 R：先周报勾到「可讨论」+ 总控签字进入该债待批 A → 再派该债硬设计（不得跳过）。

---

## 6. 禁止项（自检清单）

- [ ] 六槽  
- [ ] Pack6 / 新 kind / 新模式图包  
- [ ] 抬体力 / 掉率 / `event_rate` / ALTS  
- [ ] 改 refine/quality/enhance/upgrade 价（偷开 forge R）  
- [ ] 天赋续跑 / 灰印续跑 / 守招再调  
- [ ] 偷开事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R  
- [ ] 重开 D307 / D308（改门槛数）/ D309  
- [ ] 玩家面遥测 KPI / 排行榜 / 成就挂钩  
- [ ] 无总控签字自动「建议开 R」变施工  
- [ ] 本模板改 D308 门槛数字  
- [ ] 纯 lore / 假「样本已满可开」报告  
- [ ] 本窗跑 p1sim / 部署玩法 jar

---

## 7. 邻域边界

| 邻域 | 关系 |
|------|------|
| **D308** | 就绪硬表真源；本稿只做**周报检查单**，门槛原样引用 |
| **D298** | 遥测键/命令/周文件真源；本稿不改键名 |
| **D309** | 副手已关；本窗不重开 |
| **D307** | 菜单诚实已收；≠ forge R；本窗不重开 |
| **灰印 / 天赋 HOLD** | 不占本窗 |
| **各 R 后置稿** | 开闸仍走 D308 表 + 该债另派硬设计 |

---

## 8. 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-08 | 策划 · 初稿 STATUS **待批 A** · 荐方案 **M**；R / M+R 可勾；W 否决；检查单全文 + 空周/半满周示例；门槛数字钉 D308 未改 |
| 2026-10-08 | 总控批 **已批 A · 批 M · D310 docs-only**；R / M+R 未勾（脚本仍后置）；W 否决；门槛数字未改 · [`STATUS-ember-sample-week-report-d310.md`](../status/STATUS-ember-sample-week-report-d310.md) · 检查单 [`STATUS-ember-sample-week-report-checklist-d310.md`](../status/STATUS-ember-sample-week-report-checklist-d310.md) |

---

## 9. 交付与参考

- 路径：`docs/design/DESIGN-ember-sample-week-report-2026-10-08.md`  
- backlog：`B-sample-week-report` → **已上线 D310**（批 M · docs-only）  
- tip：[`STATUS-ember-next-hard-debt-sample-week-report-need-design-2026-10-08.md`](../status/STATUS-ember-next-hard-debt-sample-week-report-need-design-2026-10-08.md)（`adb09d7d`）· **已关窗**  
- D308：[`DESIGN-ember-sample-window-readiness-2026-10-08.md`](DESIGN-ember-sample-window-readiness-2026-10-08.md) · [`STATUS-ember-sample-window-readiness-d308.md`](../status/STATUS-ember-sample-window-readiness-d308.md)  
- D298：[`DESIGN-ember-playfeel-telemetry-2026-10-07.md`](DESIGN-ember-playfeel-telemetry-2026-10-07.md) · [`STATUS-ember-playfeel-telemetry-d298.md`](../status/STATUS-ember-playfeel-telemetry-d298.md)  
- STATUS：[`STATUS-ember-sample-week-report-d310.md`](../status/STATUS-ember-sample-week-report-d310.md) · 检查单 [`STATUS-ember-sample-week-report-checklist-d310.md`](../status/STATUS-ember-sample-week-report-checklist-d310.md)  
- **只 docs**；施工号 **D310**；脚本方案 R **仍后置**

---

*已批 A · 批 M · 施工 **D310**（docs-only）。本号只落可复用周报检查单 + tip/backlog/D308 STATUS 同步；**禁**借号开任一 R / 改 D308 门槛数 / 无签字自动开闸 / 玩家面 KPI / 跑 p1sim / 部署玩法。脚本另号。*
