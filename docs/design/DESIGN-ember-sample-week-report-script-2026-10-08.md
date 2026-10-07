# 样本窗周报脚本规格（D310 方案 R 后置 · docs/tools 契约）· 2026-10-08

> **STATUS：已批 A · 批 M+R · D311 同号**（总控 · 2026-10-08 Asia/Shanghai）  
> **日期：** 2026-10-08 Asia/Shanghai  
> **来源：** 总控派单「硬设计待批 A」· tip [`STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md`](../status/STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md)（`13883180`）· D310 周报检查单已收后下一档**证据工具**硬债 · 对齐 D310 DESIGN **§2.2 方案 R 后置**  
> **性质：** 硬设计 **已批 A**；施工号 **D311**（**docs+tools 同号** · 契约批注 + 落 `tools/p1-telemetry-week-report.py` · 禁借号开任一战斗/经济 R · 禁自动开闸 · 禁玩家面 KPI · 禁改 D308 门槛数）。  
> **批注：** 总控批 · 采纳 **批 M+R** · **同号 D311**（契约 docs + 脚本）；**W 否决**；门槛钉 D308 **未改**。  
> **硬约束：** **六槽不做** · **禁 Pack6 / 新模式图包** · **禁抬体力 / 掉率 / event_rate / ALTS** · **禁改 refineCost / qualityCost / enhance 价表（= 偷开 forge R）** · **禁天赋续跑（HOLD）** · **禁灰印续跑（HOLD）** · **禁守招邻域再调** · **禁偷开：事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R** · **禁重开 D307 / D308 / D309 / D310** · **禁纯 lore** · **禁玩家面遥测 KPI / 排行榜 / 成就** · **禁无总控签字自动「建议开 R」变施工** · **禁改 D308 §2.1 门槛数字** · 检查单结构**以 D310 检查单为准**（脚本只填数字栏，不发明新栏目/新门槛）· 玩家面不写「请执行 /corerpg p1 telemetry …」。  
> **Backlog 指针：** `B-sample-week-report-script` · [`STATUS-ember-sample-week-report-script-d311.md`](../status/STATUS-ember-sample-week-report-script-d311.md) · 脚本 [`tools/p1-telemetry-week-report.py`](../../tools/p1-telemetry-week-report.py)  
> **邻域边界：** D310 检查单 **已批 M · 已落** [`STATUS-ember-sample-week-report-checklist-d310.md`](../status/STATUS-ember-sample-week-report-checklist-d310.md)（**不改**已批 M 结论）；D308 就绪硬表 **已批 M · 当前全表不得开**；D298 证据管 **已收**；D309 副手方案 A **已关 §8-5**；灰印/天赋 **HOLD**。

---

## 0. 证据（D310 §2.2 · 检查单真源 · D308 门槛 · D298 键 · 为何不开 R / 不重开 / HOLD）

| # | 证据 | 出处 | 对本稿含义 |
|---|------|------|------------|
| E1 | **D310 DESIGN §2.2 方案 R 原文：** 在方案 M 同一检查单结构上，落地路径建议 `tools/p1-telemetry-week-report.py`（或 `.sh`）— **读** `plugins/CoreRpg/p1-telemetry/<week>.yml`（可选 stdin/JSON 旁路）→ **吐** 同结构 markdown 检查单（预填 A–C 数字与派生两率；**D/E/G 人感与签字栏留空勾选**）；输出硬限 **只得**「门槛勾选 + 当前态摘要」；预填「对照 D308：未满则标不得开」；**禁**输出「建议立即开 X R」而无总控签字占位行；仍禁改战斗/经济、玩家面看板、与事件/调律/forge 等 R **同施工号**；脚本进仓须**另派施工号**（docs/tools only）；伪接口含 `--yml` / `--json-log` / `--out`；退出码 0=可读、非 0=缺文件/缺 counts | [`DESIGN-ember-sample-week-report-2026-10-08.md`](DESIGN-ember-sample-week-report-2026-10-08.md) §2.2 | **本页 = 把后置 R 写成可验收脚本契约**；门槛数字**不得改**；脚本代码本窗（荐 M）不写 |
| E2 | **D310 STATUS「脚本方案 R 仍后置」：** 批 M 只落检查单；`tools/p1-telemetry-week-report.py` **未写**；R / M+R 后置另号；禁自动开闸 · 禁玩家面 KPI · 禁改 D308 门槛 · 禁开任一 R | [`STATUS-ember-sample-week-report-d310.md`](../status/STATUS-ember-sample-week-report-d310.md) · tip `13883180` | tip 正式派本债；**仍零玩法** |
| E3 | **检查单真源（D310）：** [`STATUS-ember-sample-week-report-checklist-d310.md`](../status/STATUS-ember-sample-week-report-checklist-d310.md) — 栏目 A～G + 通用红线速勾 + 空周/半满周示例指针；OP-only · 禁玩家面 | D310 落地 | 脚本输出 **必须同结构**；只填可从 yml 派生的数字栏；**不**发明新栏目/新门槛 |
| E4 | **D308 §2.1 门槛（本契约原样引用 · 不得改数）：** 战斗向最小周滚 = **≥1 个完整 P 周** 真人样本，且该周全服（排除测试号）`p1_pf_runs` **≥ 30**；荐观察满 **2** 个完整 P 周再批事件/调律 R；编排/演出/挂机类 = ≥3 非测试人感 **或** 总控记档 + 距对应 M **≥3 自然日**；签字人 = **总控批 A**；当前态（2026-10-08）**全表不得开**；红线见 D308 §2.1.3 | [`DESIGN-ember-sample-window-readiness-2026-10-08.md`](DESIGN-ember-sample-window-readiness-2026-10-08.md) §2.1 | 脚本预填 A–C / E「未满·不得开」只**对照**此表，**不改数** |
| E5 | **D298 周文件路径：** `plugins/CoreRpg/p1-telemetry/<week>.yml`（`week` = P 周 `weekKey`）；字段：`week` · `updated` · `note` · `counts.<p1_pf_*>`；W1c 排除 `leaderboard_exclude` ∪ `telemetry.exclude_uuids`；旁路 `[P1 pf] {json…}` | D298 DESIGN / STATUS · CoreRpg **1.65.88+** | 脚本**只读**真源；不改键名、不写回 yml |
| E6 | **D298 命令与键名：** `/corerpg p1 telemetry [player\|server]`（`corerpg.admin`）；分母 `p1_pf_runs`；`p1_pf_wall` / `whiff` / `break`；`p1_pf_evt_roll` / `evt_ok`；`p1_pf_sig_wear` / `sig_alt`；`p1_pf_vb_hit`；派生：出房率=`evt_roll`/`runs`、成功率=`evt_ok`/`max(evt_roll,1)`、佩戴率=`sig_wear`/`runs`、调律率=`sig_alt`/`max(sig_wear,1)`（及 /`runs`） | D298 DESIGN §3 · D310 §0 E4 | CLI 必读键与派生公式钉死于此 |
| E7 | tip 否 A–G：样本门禁 R / 天赋灰印 / Pack6 六槽 / 薄 UX / 重开 D307–D310 / 改门槛 = 假活或刚收禁再拧 | tip §1 | **本窗只做脚本契约（荐 M）** |
| E8 | 灰印 T0/T0b **❌ · HOLD**；天赋换机制 **HOLD**（禁 T0'''） | tip · backlog | **本窗不续跑** |
| E9 | D309 副手方案 A 已关加固 §8-5；D307 工坊菜单诚实刚收（≠ forge R）；D308/D310 刚落 | STATUS D309/D307/D308/D310 | **禁重开**；脚本 ≠ 改价 / 开 R |

**为何本窗不开任一战斗/经济 R：**  
D308 硬表当前全表「未满 / 不得开」；本债是 **docs/tools 证据工具契约**（读 yml→检查单），零率/价/房压。开闸仍须对照 D308 §2.1 + **总控批 A**——脚本本身**不得自动开闸**。

**为何不重开 D307–D310：**  
刚落仓禁再拧；D307 ≠ forge R；D308 门槛口径勿借脚本改数；D309 副手已关；D310 检查单已是手填真源。本稿只把脚本契约写清，不重开主交付。

**为何灰印/天赋 HOLD：**  
模拟未过 42±2 / 换机制轨停；再开须另起非通关率敏感规格，**不**占本证据工具窗。

**一句话问题：** D310 手填检查单已有；OP 仍要手抄 `p1-telemetry/<week>.yml`——要在**不开战斗/经济 R、不改 D308 门槛、不自动开闸**前提下，把**周报脚本契约**（路径、入参、出参结构、预填/留空、禁句、退出码、落地是否另号）写成可批可验收硬规格。

---

## 1. 玩家感知目标（≤3）

1. **对玩家：本窗无新感知。** 零玩法、零菜单改、零新 HUD；**不**出现玩家面遥测 KPI / 排行榜 /「请看周报」。  
2. **对总控/策划：脚本吐出的检查单勾完仍回答「本周样本满不满、能否进入某债待批 A」。** 满 ≠ 自动施工；仍须该债硬设计 + 总控批 A。  
3. **对 OP：一条命令少手抄；签字栏与人感栏仍须人工+总控。** 脚本只预填可从周文件派生的数字与门槛对照勾；**D/E/G** 人感与签字**必须留空**。

---

## 2. 方案表

### 2.1 方案 M（**荐** · 只 docs 脚本契约 · 零 jar/yml 玩法 · 本窗不写 .py）

**做什么：** 把 D310 DESIGN §2.2 伪接口写成可验收规格（下文 §2.1.1～§2.1.7）。脚本代码**本窗不写进仓**；批准后可同窗 **docs-only** 施工号落契约正文（或后置），**不得**借号开战斗/经济 R。

#### 2.1.1 路径与 CLI（验收钉死）

| 项 | 规格 |
|----|------|
| **建议路径** | `tools/p1-telemetry-week-report.py`（落地时；本窗荐 M **不写文件**） |
| **调用** | `python3 tools/p1-telemetry-week-report.py --yml <path> [--json-log <path>] --out <md-path>` |
| **`--yml`** | **必填**。周文件绝对/相对路径，真源形态 `plugins/CoreRpg/p1-telemetry/<week>.yml` |
| **`--json-log`** | **可选**。旁路 `[P1 pf] {json…}` 行文件或等价 JSON 数组；仅作对账备注，**不得**覆盖 yml `counts` 作门槛判定主源；缺省=不读 |
| **`--out`** | **必填**。输出 markdown 路径；建议 `docs/status/reports/sample-week-<week>.md`（目录可后置创建；**禁**写进玩家菜单/lore/成就目录） |
| **stdin** | 不要求；若实现可读 stdin，须与 `--yml` 互斥且同结构，本契约不强制 |

#### 2.1.2 必读键（yml）

| 键 | 要求 |
|----|------|
| `week` | 必有；写入检查单「周键」抬头 |
| `counts` | 必有且为 mapping；缺 → 非 0 退出 |
| `counts.p1_pf_runs` | 必有（分母）；缺 → 非 0 |
| `counts.p1_pf_evt_roll` / `p1_pf_evt_ok` | 有则填 B；缺则该格填 `—` 并在摘要注「缺键」；**不** invent |
| `counts.p1_pf_sig_wear` / `p1_pf_sig_alt` | 同理填 C |
| `counts.p1_pf_wall` / `p1_pf_whiff` / `p1_pf_break` / `p1_pf_vb_hit` | 可选填 F；缺则 `—` |
| `updated` / `note` | 可选写入抬头旁注；不参与门槛数字改写 |

键名**以 D298 为准**；脚本**不得**改写源 yml。

#### 2.1.3 派生两率公式（对照 D298 / D310 · 观察性 · 非硬砍）

令 `runs = counts.p1_pf_runs`（整数 ≥0）。

| 指标 | 公式 | 写入栏 |
|------|------|--------|
| 出房率 | `evt_roll / runs`（runs=0 → 标 `n/a`） | B2 |
| 成功率 | `evt_ok / max(evt_roll, 1)` | B3 |
| 佩戴率 | `sig_wear / runs`（runs=0 → `n/a`） | C2 |
| 调律率（占佩戴） | `sig_alt / max(sig_wear, 1)` | C3 |
| 调律率（占局） | `sig_alt / runs`（runs=0 → `n/a`） | C4 |
| 破绽局均 wall/whiff/break | 各 `/ runs` | F（可选） |
| `vb_hit` 率 | `vb_hit / runs` | F（可选） |

百分比输出建议 1 位小数 + `%`；整数 counts 原样。

#### 2.1.4 预填逻辑（A–C · 对照 D308：未满则标不得开）

| 栏 | 脚本行为 |
|----|----------|
| **抬头** | 填 `week`；填表人/填表日 **留空**（`______________`） |
| **A1** | **不得自动判定「完整 P 周」**（日历/人判）。实填列写：`week=<week> · 【须人工：是/否 · 第 __ 个完整周】`；勾选列 `[ ]` |
| **A2** | 实填 `runs = <n>`；若 `runs ≥ 30` → 勾选可预标 `[x]` **仅表示「分母数字已达门槛」**，并旁注「仍须 A1 人工确认完整周」；若 `runs < 30` → 勾选 `[ ]` + 旁注 **`未满 · 不得开（对照 D308：runs≥30）`** |
| **A3 / A4** | **留空勾选**（口径确认 / 未冒充 = 人工） |
| **B1–B3** | 预填 counts + 派生率；勾选「已抄录」可预 `[x]`（表示数字已写入，**非**开闸） |
| **B4** | **必须留空**（人感） |
| **C1–C4** | 同 B1–B3 |
| **C5** | **必须留空**（人感） |

**E 表（各 R 当前态）：** 脚本**默认**对全部债行预填当前态勾选为 **`[x] 未满 · 不得开`**（钉 D308 2026-10-08 当前态；**不得**因 `runs≥30` 自动改成「可开」）。「本周是否满 / 红线触达」两列写 `【须人工】` 或留空。  
**通用红线速勾：** 若 `runs < 30`，可预勾「样本周未满 / `runs`<30」一项；**其余红线全部留空**（禁自动勾「建议开 R」类）。

#### 2.1.5 必须留空（D / E 人感侧 / G）

| 栏 | 理由 |
|----|------|
| **D1–D3** | 排除表脏否依赖 `ember-v1.yml` / 点名对照 / 人工；脚本**默认不读**玩法配置写死结论；三行实填与勾选**留空**（可在摘要提示「请人工对照 exclude_uuids」） |
| **E 人感与「是否满」** | 人感与编排类门槛非 yml 可判；见上 |
| **G 签字栏全文** | 填表人 / 策划复核 / **总控** 三行签名与日、总控三选一勾选 **必须留空占位**（空位结构同 D310 检查单 §G） |
| **B4 / C5 / F 备注** | 人感原话 · 留空 |

#### 2.1.6 禁输出句清单（硬验收 · 任一出现 = 不合格）

脚本生成的 md / stdout **禁止**出现（含子串等价改写）：

1. `建议立即开` + 任一 `事件 R` / `调律 R` / `forge R` / `工坊 R` / `走廊 W2` / `深渊 R` / `周本 R` / `Boss` / `挂机 R`  
2. `可以开闸` / `批准施工 R` / `自动开 R` / `无需总控签字`  
3. `样本已满可开`（无签字占位语境下的断言）  
4. 玩家面导向：`请玩家查看周报` / `排行榜` / `成就进度` / 把 `/corerpg p1 telemetry` 写成玩家指令  
5. 改门槛暗示：`将门槛改为` / `runs 门槛降至` / 任何改写 D308 §2.1 数字的句子  
6. 假进度：`已施工事件 R` / `D307–D310 重开` 等

**允许：** `对照 D308：未满 · 不得开`；`勾满 ≠ 自动开 R`；`须总控签字后方可进入某债待批 A`；空签字表。

#### 2.1.7 退出码与出参结构

| 码 | 含义 |
|----|------|
| **0** | `--yml` 可读，且存在 `week` + `counts`（至少含 `p1_pf_runs`）；已写出 `--out` md |
| **2** | 缺文件 / 路径不可读 |
| **3** | yml 可打开但缺 `week` 或缺 `counts` 或缺 `counts.p1_pf_runs` |
| **4** | `--out` 无法写入 |
| **其它非 0** | 实现可扩展，但须 stderr 一行人话原因；**不得**在失败时写出「建议开 R」md |

**出参 md 结构（必须对照 D310 检查单栏目，顺序一致）：**

1. 抬头（周键预填；填表人/日留空；真源路径；口径声明钉 D308）  
2. **A** 周滚与分母（按 §2.1.4）  
3. **B** 事件两率（B4 留空）  
4. **C** 调律两率（C5 留空）  
5. **D** 排除表（整段留空勾选 + 人工提示）  
6. **E** 各 R 红线对照（默认「未满 · 不得开」；人感列留空）+ 通用红线（仅 runs\<30 可预勾一项）  
7. **F** 辅助观察（有键则填；备注留空）  
8. **G** 签字栏（**必须含空位**，与 D310 §G 同表）  
9. 文末固定声明：`本文件由脚本生成数字栏；人感与总控签字须人工；勾满 ≠ 自动开 R；门槛数字以 D308 §2.1 为准（未改）。`

**荐批：** **批 M**（只落 docs 契约；零 .py；零玩法）。

### 2.2 方案 R（M 契约之上 · docs+落脚本 · 仍零玩法）

| 项 | 内容 |
|----|------|
| **做什么** | 在 §2.1 契约全部钉死后，**另**落仓 `tools/p1-telemetry-week-report.py`（实现须通过 §2.1.1–§2.1.7 验收） |
| **输出硬限** | 同 §2.1.6–§2.1.7；只得门槛勾选 + 当前态摘要；**禁**无签字「建议立即开 X R」 |
| **施工号钉死** | **禁**与事件/调律/forge/走廊 W2/深渊/周本/Boss/挂机等战斗·经济 R **同施工号**。可选：① **批 R** = 契约已有则本号只 tools（或 docs 小补指针 + tools）；② **批 M+R** = **契约 docs 与脚本可同号**（仍 docs/tools only），**或** 契约同批 docs、脚本**另号**——**若勾 M+R，总控须在批注点名「同号」或「脚本另号」**；未点名则默认 **脚本另号** |
| **仍禁** | 改战斗/经济数值；玩家面看板；改 D308 门槛；与样本 R 同号兼开 |
| **相对 M** | M 已够验收契约；R 减抄写。**不荐纯 R 无契约**；契约未批前禁写 .py |

### 2.3 方案 W（**否决默认**）

| 项 | 内容 |
|----|------|
| **提议（不采纳）** | ① 脚本/周报**自动建议开 R**且无总控签字即可施工；② 做成玩家面 KPI / 排行榜 / 成就；③ 借本窗偷开任一战斗/经济 R；④ **改 D308 §2.1 门槛数字**；⑤ 重开 D307–D310 |
| **否决理由** | ① D308/D310 明文开闸须总控批 A；无签字自动建议 = 假开闸；② D298-W / D308 / D310 已否决玩家面遥测；③ 当前全表不得开；偷开 = 无证据拧率/价/房压；④ 门槛钉死，脚本不得改数；⑤ 刚收窗再拧 = 假进度 |
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
| D307 / D308 / D309 / D310 主交付 | **不重开** |
| **D308 §2.1 门槛数字** | **不改**（脚本只引用） |
| 检查单结构 | **以 D310 检查单为准**；不发明新栏目/新门槛 |
| 玩家面遥测 KPI / 排行榜 / 成就 | **否决** |
| **p1sim** | **不跑**（零战斗/零经济压） |
| **部署玩法** | **默认不部署** |
| 批准前 `.py` 进仓 | **禁**（若仅批 / 荐 M） |

**本窗门禁结论：** 荐 **M** → 只交 docs 脚本契约；批准后 docs-only 施工号可落契约，**不得**借号开 R、**不得**写 .py（除非另批 R/M+R）。若批 **R / M+R**，仍 docs/tools only，禁与战斗/经济 R 同号（M+R 同号范围仅限契约+脚本，不含玩法 R）。

---

## 4. 批注勾选（总控）

- [ ] **批 M**（只 docs 脚本契约 §2.1；零 .py；零玩法；门槛钉 D308；D/E/G 留空规则钉死；禁自动建议开 R）→ 可同窗 docs 施工号 · **策划荐** · **未勾**（总控改勾 M+R）
- [ ] **批 R**（在已有/同批契约上落 `tools/p1-telemetry-week-report.py`；禁与战斗/经济 R 同号；输出禁无签字「建议开 R」）· **未勾**
- [x] **批 M+R**（契约 docs + 脚本同批；**总控点名同号 D311**；仍零玩法）· **已勾 · 施工 D311**
- [ ] **驳回改派**（其它薄 UX · **须点名**且 **不得**偷开 R / 玩家面 KPI / 改门槛）· **未勾**

**否决：** 方案 W（自动建议开 R / 玩家面 KPI / 偷开 R / 改 D308 门槛 / 重开 D307–D310）——无「批 W」勾选。

---

## 5. 切窗建议（批 A 后）

1. 文首已改「已批 A」+ **批 M+R · 同号 D311**；**设计主交付表体勿改门槛口径与检查单栏目**（除非总控改派）。  
2. **已批 M+R · D311：** 契约 docs + `tools/p1-telemetry-week-report.py` **同号落地**；验收空周/半满周 fixture → md；禁句与 G 空位自检；**不开**任一战斗/经济 R。  
3. **禁同塞：** 事件 R/W、调律 R、forge R、走廊 W2、深渊 R、周本 R、Boss R、挂机 R、天赋/灰印、Pack6、六槽、重开 D307–D310、玩家看板、改门槛。  
4. 真开某 R：先周报（手填或脚本预填）勾到「可讨论」+ 总控签字进入该债待批 A → 再派该债硬设计（不得跳过）。  
5. 指针：[`STATUS-ember-sample-week-report-script-d311.md`](../status/STATUS-ember-sample-week-report-script-d311.md) · [`tools/p1-telemetry-week-report.py`](../../tools/p1-telemetry-week-report.py)。

---

## 6. 禁止项（自检清单）

- [ ] 六槽  
- [ ] Pack6 / 新 kind / 新模式图包  
- [ ] 抬体力 / 掉率 / `event_rate` / ALTS  
- [ ] 改 refine/quality/enhance/upgrade 价（偷开 forge R）  
- [ ] 天赋续跑 / 灰印续跑 / 守招再调  
- [ ] 偷开事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R  
- [ ] 重开 D307 / D308（改门槛数）/ D309 / D310  
- [ ] 玩家面遥测 KPI / 排行榜 / 成就挂钩  
- [ ] 无总控签字自动「建议开 R」变施工  
- [ ] 本契约 / 脚本改 D308 门槛数字  
- [ ] 发明检查单新栏目 / 新门槛  
- [x] ~~批准前（批 M）写 `tools/p1-telemetry-week-report.py` 进仓~~ → **已批 M+R · D311 同号落脚本**  
- [ ] 纯 lore / 假「样本已满可开」报告  
- [ ] 本窗跑 p1sim / 部署玩法 jar

---

## 7. 邻域边界

| 邻域 | 关系 |
|------|------|
| **D310** | 检查单真源 + §2.2 后置原文；本稿 = 脚本可验收契约；**不改** D310 已批 M 结论 |
| **D308** | 门槛 / 红线 / 当前态唯一口径；脚本只引用 |
| **D298** | 周文件路径与键名真源；脚本只读 |
| **D309** | 副手已关；本窗不重开 |
| **D307** | 菜单诚实已收；≠ forge R；本窗不重开 |
| **灰印 / 天赋 HOLD** | 不占本窗 |
| **各 R 后置稿** | 开闸仍走 D308 表 + 该债另派硬设计；脚本不得代批 |

---

## 8. 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-08 | 策划 · 初稿 STATUS **待批 A** · 荐方案 **M**（只 docs 契约：CLI/键/公式/预填 A–C/留空 D·E·G/禁句/退出码/出参对照 D310 检查单）；R / M+R 可勾（脚本落地号钉死）；W 否决；门槛数字钉 D308 未改；本窗不写 .py |
| 2026-10-08 | 总控批 **已批 A · 批 M+R · 同号 D311**（契约 docs + `tools/p1-telemetry-week-report.py`）；W 否决；门槛数字未改 · [`STATUS-ember-sample-week-report-script-d311.md`](../status/STATUS-ember-sample-week-report-script-d311.md) |

---

## 9. 交付与参考

- 路径：`docs/design/DESIGN-ember-sample-week-report-script-2026-10-08.md`  
- backlog：`B-sample-week-report-script` → **已批 M+R · D311**  
- tip：[`STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md`](../status/STATUS-ember-next-hard-debt-sample-week-report-script-need-design-2026-10-08.md)（`13883180`）· **已关窗**  
- STATUS：[`STATUS-ember-sample-week-report-script-d311.md`](../status/STATUS-ember-sample-week-report-script-d311.md)  
- 脚本：[`tools/p1-telemetry-week-report.py`](../../tools/p1-telemetry-week-report.py)  
- D310：[`DESIGN-ember-sample-week-report-2026-10-08.md`](DESIGN-ember-sample-week-report-2026-10-08.md) · [`STATUS-ember-sample-week-report-d310.md`](../status/STATUS-ember-sample-week-report-d310.md) · 检查单 [`STATUS-ember-sample-week-report-checklist-d310.md`](../status/STATUS-ember-sample-week-report-checklist-d310.md)（**已批 M 结论不改**）  
- D308：[`DESIGN-ember-sample-window-readiness-2026-10-08.md`](DESIGN-ember-sample-window-readiness-2026-10-08.md) · [`STATUS-ember-sample-window-readiness-d308.md`](../status/STATUS-ember-sample-window-readiness-d308.md)  
- D298：[`DESIGN-ember-playfeel-telemetry-2026-10-07.md`](DESIGN-ember-playfeel-telemetry-2026-10-07.md) · [`STATUS-ember-playfeel-telemetry-d298.md`](../status/STATUS-ember-playfeel-telemetry-d298.md)  
- **docs + tools only**；施工号 **D311**；**不得**借号开事件/调律/forge/走廊 W2 等 R

---

*已批 A · 批 M+R · 施工 **D311**（docs+tools 同号）。本号落契约批注 + `tools/p1-telemetry-week-report.py`；**禁**借号开任一 R / 改 D308 门槛数 / 无签字自动开闸 / 玩家面 KPI / 跑 p1sim / 部署玩法。*
