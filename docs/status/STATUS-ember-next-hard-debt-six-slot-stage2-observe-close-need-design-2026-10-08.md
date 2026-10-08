# 状态 · 下一档硬债选定 · 需策划（六槽 Stage2 观察关窗检查单 · docs-only）

> **上游结案：** D325 Stage2 档 C **线上 bv62 部署+烟测 PASS**（[`STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md`](STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md) · `6e734dc7`）；现态 jar `1.65.99-d325.local` tip `b02ca9f3` · sha `4c273c5a…` · **`set_bonus=true`** · **bv=62**；Stage1（D324 · enabled/migrate）+ Stage2 护甲路径**同开观察**。样本 R 全表不得开（D308）；天赋 **HOLD**；灰印 **HOLD**；Pack6 **硬禁**；K3 **未授权**；禁重开 D301–D311 与六槽 Stage1/2 **施工本体**。

**日期：** 2026-10-08（上海时间）  
**上游：** D325 live PASS · 隔离验收 `baa6306a` · 施工 tip `b02ca9f3` · D324 Stage1 S3 观察继续 · D308 门槛钉死 · D309–D311 证据链已闭 · 灰印/天赋 HOLD · K3 备选未授权 · Pack6 硬禁  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up · **不开任一样本门禁 R** · **不改线上 set_bonus / bv / jar** · **不写六槽新代码**  
**硬规格（待策划交稿）：** [`DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md)（待建）· STATUS=待批 A · backlog `B-six-slot-stage2-observe-close`  
**打开理由：** Stage2 已上线进观察，但**尚无正式「观察关窗」检查单**——何时可签「观察结束 / 维持观察 / 回滚」、要采哪些人感与抽查、红线是什么、与 Stage1 观察如何并行。要在**不动 jar/yml、不开 R、不偷开 K3/S 档/Pack6/天赋/灰印**前提下，出一张 docs-only 关窗规格。

---

## 0. 局势一句话（样本门禁声明）

D325 live 已绿；战斗/经济 R **全表不得开**。下一档若去开 K3 / 档 S / Pack6 / 天赋灰印 / 任一样本 R / 再拧六槽施工 = 假活。Stage2 刚进观察、关窗口径未钉——本 tip **不**动线上，也 **不**开 R；改采纳 **Stage2 观察关窗检查单（docs-only）**。

---

## 1. 选题（八选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D308 硬表：须完整 P 周 + `p1_pf_runs`≥30；**当前不得开** |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / **工坊方案 R（价/节奏）** / 深渊 **R** / 周本 **R** / Boss 预警 **R** / 挂机 **R** | **否** | D308 全表不得开；**禁偷开** |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / **K3 同部位熔炼** / **档 S 分族行为** / 六槽 Stage1·2 **再施工** / 新模式 / 抬体力掉落 | **否** | Pack6 硬禁；K3 备选未授权；档 S 须另号 T0；Stage1/2 施工刚闭禁重开；禁默默拧 jar |
| F | 组队·结算余感 / 图录对齐 / 经济钱包 / VIP·战令 / 技能页 / 工坊菜单再拧半行 | **否（本窗主交付）** | 薄 UX / 已收诚实窗；**非当前最尖硬债**；勿挡 Stage2 观察关窗规格 |
| G | 重开 D301–D311 / 改 D308 门槛数字 / 加固 §1 纯 STATUS 同步当主债 | **否** | 刚上线禁再拧；门槛钉死；同步可旁注，不占硬规格主交付 |
| **H** | **六槽 Stage2 观察关窗检查单（docs-only）** | **采纳 · 需策划** | 范围：只 `docs/design/`；钉死 **观察维持门槛**、**可签关观察出口**、**回滚红线**、与 Stage1 观察并行口径；荐 **方案 M**（检查单 + 签字栏）；可选 **R** 仅指「观察期满后另号再议档 S/K3」**不**等于开样本战斗 R；**W 否决**观察期内偷开 R / K3 / Pack6 / 天赋 / 灰印 / 改 ×0.97 |

**对齐：** D325 live bv62 · `set_bonus=true` · Stage1+Stage2 同开观察 · DESIGN Stage2 已部署指针 · D308 全表不得开 · 灰印/天赋 HOLD · ARCH 禁 Pack6 · K3 备选未授权 · **禁重开 D301–D311 与六槽施工本体**。

**未采纳备选说明：** 样本门禁 R 等直播周样本——本窗**绝不**改派去「假装再开 R」。薄 UX 若仍尖，可在批注勾「驳回改派」另选题，**不得**借机开 forge R / 事件 R / Pack6 / K3 / 档 S 施工 / 默默改受伤倍率。

---

## 2. 一句话问题

Stage2 档 C 已上线进观察，但「观察到什么算可关、什么必须回滚、与 Stage1 观察如何并行」未写成可批硬规格——要在**零 jar/yml、零样本 R**前提下钉死关窗检查单。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】六槽 Stage2 观察关窗检查单（docs-only · 非样本 R）
优先级：D325 live 后下一档结构规格硬债 · 非施工玩法窗 · Stage1+Stage2 护甲路径已同开观察 · 样本门禁 R 不偷开 · 非灰印/天赋续跑 · 非重开 D301–D311 · 非薄 UX 抬硬 · 零六槽新代码 · 禁 Pack6 · 禁 K3
禁：六槽 Stage1/2 Java/NI/Loadout 再施工·改 set_bonus/bv/jar·改受伤×0.97·档 S 施工·K3 熔炼·Pack6·新模式图包·抬体力/掉率/event_rate/ALTS·改 refineCost/qualityCost/enhance 价表（=偷开 forge R）·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调·事件R/W施工·调律R施工·工坊R施工·走廊W2夹带·深渊R/周本R/Boss预警R/挂机R施工·重开D301–D311·纯lore·玩家面遥测KPI·与样本战斗/经济 R 同号兼开

目标：一张可批观察关窗检查单——写清「维持观察 / 签关观察 / 触发回滚」三出口；本窗零代码、零玩法 yml/jar、不切线上开关

请出 docs/design/DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md，文首 STATUS=待批 A，含：

0. 证据：D325 live [`STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md`](STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md) `6e734dc7`（bv62 · set_bonus=true · 烟测 PASS）；隔离 `baa6306a`；施工 tip `b02ca9f3`；DESIGN Stage2 [`DESIGN-ember-six-slot-stage2-set-bonus-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage2-set-bonus-2026-10-08.md)；Stage1 T3/观察 [`DESIGN-ember-six-slot-t3-rollout-2026-10-08.md`](../design/DESIGN-ember-six-slot-t3-rollout-2026-10-08.md) D324；备份路径；D308 后为何仍不得借本窗开任一 R / K3 / 档 S
1. 玩家感知目标（≤3 条）：对玩家——观察期内无新感知（零施工）；对总控——能回答「观察还要多久、什么绿/红可签关或回滚」；对施工——知道批后另号才能动 jar/yml，且禁与样本 R 同号
2. 方案表（至少 2 选 1 荐）：
   - 方案 M（荐）：**观察关窗检查单**——钉：最短观察窗（建议日历日或真人局次数下限，可用 D310/D311 周报键只读对照，**不**改门槛）；必抽项（菜单/PAPI 三态、激活 poison ×0.97、关/拆套 ×1.0、B/H 不变、Stage1 mig/待领、资产 loss/dup 申告）；红线（双计减伤、面板 B/H 漂移、迁移回归、跨服复制）→ 立即走备份回滚路径；绿出口签字栏（总控签「观察结束·维持 bv62」或「继续观察」）；与 Stage1 S3 观察**并行不互关**（关 Stage2 观察 ≠ 关 enabled/migrate）
   - 方案 R（后置 · 非样本战斗 R）：仅当 M 绿出口已签后，**另号**再议「是否开档 S T0 / 是否开 K3 决策」——本窗**只写门闩**，**不**授权施工，**不**等于 D308 样本 R
   - 方案 W（否决默认）：观察期内偷开任一样本 R / 默默改 ×0.97 / 开 K3 / 开档 S / Pack6 / 天赋或灰印续跑 / 关 set_bonus 却不记 STATUS —— 写清否决理由
3. 不动清单与门禁：体力/掉落/event_rate/ALTS；价表；Pack6；天赋轨；灰印轨；守招邻域；**任一 R 窗施工本体**；D301–D311 主交付；D308 §2.1 门槛数字；**set_bonus / bv / jar**（本窗禁改）；Stage1 enabled/migrate（本窗不关）。零战斗压**不跑 p1sim**。本 tip 窗默认不部署
4. 批注勾选：批 M / 批 M+R门闩（R 仅后置另号） / 驳回改派（其它薄 UX · 须点名且不得偷开 R / Pack6 / K3 / 档 S）

交付：只 docs；批准前禁改 YAML/Java/NI；批准后若仅同步观察指针 / STATUS 可同窗 docs 施工号，**不得**借施工号开事件/调律/forge/走廊W2 等 R，**不得**改线上 jar/yml（除非总控另签部署/回滚号）。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / **工坊 R（价表）** · **不**开深渊 R · **不**开周本 R · **不**开 Boss 预警 R · **不**开挂机 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**改 refineCost / qualityCost / enhance 价 · **不**抬掉率 / 新材料轨  
- **不**扩 Pack6 / 新模式 · **不**写六槽 Stage1/2 新 Java / NI · **不**开 K3 · **不**开档 S  
- **不**改线上 `set_bonus` / bv / jar · **不**关 Stage1 enabled/migrate  
- **不**重开 D301–D311 主交付邻域 · **不**改 D308 门槛数字  
- **不**把薄 UX / 菜单半行再拧当本窗主施工  
- **不**写假「已关观察 / 已开 R」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 M 后，另派 docs 施工号落检查单；**线上观察维持**直至总控另签关观察或回滚。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D308：未满完整 P 周 / `runs`≥30；**不得开** |
| 走廊 W2 · **工坊 R（价）** · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | D308 全表不得开 |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 刚上线等人 |
| 组队 / 图录 / 钱包 / VIP / 技能页薄余感 | 已收或偏软；非最尖 |
| 工坊菜单再拧半行 | D307 已收；再拧=薄 UX |
| 重开 D301–D311 · 改 D308 门槛 | 刚上线禁再拧；门槛钉死 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / **K3** / **档 S 施工** / 六槽再施工 / 抬体力掉落 | 硬禁 / 未授权 / 须另号 / 观察期禁拧 |
| 玩家面遥测 KPI | D298-W / D308 / D310 / D311 否决 |

---

## 6. 刚结指针

- D325 live · [`STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md`](STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md) · **PASS · `6e734dc7`** · **本 tip 下一档**  
- D325 隔离 · [`STATUS-ember-six-slot-stage2-set-bonus-test-d325-2026-10-08.md`](STATUS-ember-six-slot-stage2-set-bonus-test-d325-2026-10-08.md) · `baa6306a`  
- D325 施工 · tip `b02ca9f3` · DESIGN [`DESIGN-ember-six-slot-stage2-set-bonus-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage2-set-bonus-2026-10-08.md)  
- D324 Stage1 观察 · [`DESIGN-ember-six-slot-t3-rollout-2026-10-08.md`](../design/DESIGN-ember-six-slot-t3-rollout-2026-10-08.md)  
- D311 · [`STATUS-ember-sample-week-report-script-d311.md`](STATUS-ember-sample-week-report-script-d311.md) · 周报脚本可只读对照（**不**改门槛）  
- D308 · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) · **当前全表不得开**  
- 灰印 T0b **HOLD** · 天赋 HOLD · K3 备选未授权 · Pack6 硬禁  
- 备份 · `/workspace/tmp/d325-bv62-backup-20261008173047/`

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。样本门禁 R 全表不得开——本 tip 只升 Stage2 观察关窗检查单（docs-only · 荐 M · 禁偷开 R / K3 / Pack6 / 天赋 / 灰印 / 改线上 jar）。*
