# 状态 · 下一档硬债选定 · 需策划（六槽 K3 同部位熔炼决策页 · docs-only）

> **上游结案：** D326 Stage2 观察关窗 **已批 A · 批 M · 观察执行中**（[`STATUS-ember-six-slot-stage2-observe-close-d326-2026-10-08.md`](STATUS-ember-six-slot-stage2-observe-close-d326-2026-10-08.md) · tip @ `b2f9b744` · DESIGN [`DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md) · 批 commit `00473c04`）；最短窗 **48h** 自 live 17:40 CST 起（不早于 **10-10 17:40**）+ §2 必抽整轮≥1 PASS → 绿出口**另签**。D325 Stage2 档 C **线上 bv62**（[`STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md`](STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md) · `6e734dc7`）· jar `1.65.99-d325.local` tip `b02ca9f3` · **`set_bonus=true`** · **bv=62**。K3 自 D318 起为 backlog 备选 **未授权**（`B-six-slot-k3-same-slot-refine`）。样本 R 全表不得开（D308）；天赋 **HOLD**；灰印 **HOLD**；Pack6 **硬禁**；禁重开 Stage1/2 **施工本体**；禁改 ×0.97 / 关 set_bonus。

**日期：** 2026-10-08（上海时间）  
**上游：** D326 观察批 M 执行中 · D325 live PASS · D324 Stage1 S3 观察继续 · D318 批 A·K0（K3 转备选、不授权 T0‴）· D308 门槛钉死 · 灰印/天赋 HOLD · Pack6 硬禁  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up · **不开任一样本门禁 R** · **不改线上 set_bonus / bv / jar** · **观察期内不部署 K3** · **不写六槽新代码（本 tip 窗）**  
**硬规格（已批）：** [`DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md`](../design/DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md) · STATUS=**已批 A · 批 M** · backlog `B-six-slot-k3-same-slot-refine` · D327  
> **交稿旁注（D327 · 策划执行手）：** 硬规格已交 [`DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md`](../design/DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md) · STATUS=待批 A · 荐方案 M（观察期可离线 T0‴；施工/部署等绿出口或另签）· **本旁注零施工**（未改 jar/yml/开关）。
>
> **结案旁注（D327 · 总控）：** 本 tip **已关 · 批 A · 批 M · D327**；授权另号离线 T0‴；施工/部署默认等 Stage2 绿出口；不批 L；本号不另签并行施工。见 [`STATUS-ember-six-slot-k3-refine-d327-2026-10-08.md`](STATUS-ember-six-slot-k3-refine-d327-2026-10-08.md)。
**打开理由：** Stage2 已进 48h 观察，总控要求**持续推进**、不闲置在观察文档 alone。下一档真结构硬债 = D318 搁置的 **K3 同部位熔炼**——须先出 **docs-only 决策页**（是否做、规则钉死、何时授权另号 T0‴），**本窗零 live jar/yml**，观察期内**禁止部署**。

---

## 0. 局势一句话（样本门禁声明）

D326 观察执行中；战斗/经济 R **全表不得开**。下一档若去偷开样本 R / Pack6 / 天赋灰印 / 改 ×0.97 / 关 set_bonus / 重开 Stage1·2 施工 = 假活。K3 方向对（同部位取高精工、零价、材料销毁、不成色/族/阶），但从未交决策稿——本 tip **不**动线上，也 **不**在观察期部署；改采纳 **K3 同部位熔炼决策页（docs-only）**，并钉死「观察期内可离线 T0‴ / 施工部署须绿出口或另签」。

---

## 1. 选题（八选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D308 硬表：须完整 P 周 + `p1_pf_runs`≥30；**当前不得开** |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / **工坊方案 R（价/节奏）** / 深渊 **R** / 周本 **R** / Boss 预警 **R** / 挂机 **R** | **否** | D308 全表不得开；**禁偷开** |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / **档 S 分族行为** / 六槽 Stage1·2 **再施工** / 改 ×0.97 / 关 set_bonus / 新模式 / 抬体力掉落 | **否** | Pack6 硬禁；档 S 须另号；Stage1/2 施工刚闭禁重开；观察期禁拧线上 |
| F | 组队·结算余感 / 图录对齐 / 经济钱包 / VIP·战令 / 技能页 / 工坊菜单再拧半行 | **否（本窗主交付）** | 薄 UX / 已收诚实窗；**非当前最尖硬债**；勿挡 K3 决策规格 |
| G | 重开 D301–D311 / 改 D308 门槛数字 / 纯观察 STATUS 同步当主债 | **否** | 门槛钉死；观察执行已有 D326；同步可旁注，不占硬规格主交付 |
| **H** | **六槽 K3 同部位熔炼决策页（docs-only）** | **采纳 · 需策划** | 范围：只 `docs/design/`；钉死规则（同部位取高精工、零价、材料销毁、不成色/族/阶）+ **是否授权另号 T0‴** 的时机；荐 **方案 M**（决策页 + **允许观察期内离线 T0‴（p1sim only · 零 live）**；施工/部署仅 Stage2 绿出口后 **或** 总控另签）；**W 否决**样本 R / Pack6 / 天赋 / 灰印 / 改 ×0.97 / 关 set_bonus / 观察期部署 / 重开 Stage1·2 施工 |

**对齐：** D326 观察批 M 执行中 · D325 live bv62 · `set_bonus=true` · D318 K3 备选未授权 · D308 全表不得开 · 灰印/天赋 HOLD · ARCH 禁 Pack6 · **禁观察期部署 K3** · **禁重开 Stage1/2 施工本体**。

**未采纳备选说明：** 样本门禁 R 等直播周样本——本窗**绝不**改派去「假装再开 R」。薄 UX 若仍尖，可在批注勾「驳回改派」另选题，**不得**借机开 forge R / 事件 R / Pack6 / 改受伤倍率 / 观察期上线熔炼。

---

## 2. 一句话问题

甲精工无锻造途径（D318 K0），重复掉落缺「取高精工」出口；K3 规则方向已在 T1 规格 §1.3 写过，但**从未交可批决策页**、也**未授权 T0‴**——要在 **Stage2 观察并行、零 live jar/yml、零样本 R** 前提下，把「做不做 / 规则钉死 / 何时离线 T0‴ / 何时才可施工部署」写成可批硬规格。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】六槽 K3 同部位熔炼决策页（docs-only · 观察期禁部署）
优先级：D326 观察执行中下一档结构规格硬债 · 非施工玩法窗 · 非样本 R · 非灰印/天赋续跑 · 非重开 Stage1/2 施工 · 非薄 UX 抬硬 · 零 live jar/yml · 禁 Pack6 · 禁改 ×0.97 / 关 set_bonus
禁：观察期内部署 K3·改线上 set_bonus/bv/jar·改受伤×0.97·关 set_bonus 不记 STATUS·重开 Stage1/2 Java/NI/Loadout 施工本体·档 S 施工·Pack6·新模式图包·抬体力/掉率/event_rate/ALTS·改 refineCost/qualityCost/enhance 价表（=偷开 forge R）·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调·事件R/W施工·调律R施工·工坊R施工·走廊W2夹带·深渊R/周本R/Boss预警R/挂机R施工·重开D301–D311·纯lore·玩家面遥测KPI·与样本战斗/经济 R 同号兼开·默默改成色/族/阶

目标：一张可批 K3 决策页——钉死规则与「离线 T0‴ / 施工部署」门闩；本 tip 窗零代码、零玩法 yml/jar、不切线上开关

请出 docs/design/DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md，文首 STATUS=待批 A，含：

0. 证据：D318 T1 规格 [`DESIGN-ember-six-slot-t1-spec-revision-2026-10-08.md`](../design/DESIGN-ember-six-slot-t1-spec-revision-2026-10-08.md) §1.3 K3（同部位取高精工、零价、材料销毁、目标保留族/掉落阶/成色、不产生极品）；D318 批注「K3 不进 T1 · backlog 备选 · 不授权 T0‴」；D325 live `6e734dc7` · D326 观察批 M（48h+必抽 · 绿出口另签）；D308 后为何仍不得借本窗开任一 R / Pack6 / 改 ×0.97
1. 玩家感知目标（≤3 条）：对玩家——重复同部位甲有「熔进更高精工」的诚实出口（批后施工另号才可见）；对总控——能回答「观察期能不能跑离线 T0‴、什么时候才能上线」；对施工——知道本窗只 docs，Java/菜单/部署各需另签
2. 方案表（至少 2 选 1 荐）：
   - 方案 M（荐）：**决策页钉死 K3 规则**（同部位甲做材料 → 目标精工=两件较高值；材料销毁+审计；零价；**不成色/族/阶**；不产生极品）+ **授权时机**：允许在 Stage2 观察期内另号跑 **离线 T0‴（p1sim only · 零 live · 不部署）**；**施工 / 测服开关 / 线上部署**仅当 (a) Stage2 绿出口已签 **或** (b) 总控另签授权号；T0‴ 门禁对齐 D318：p1sim 熔炼建模 → G2/G3/G4/G8 团本+挂机/G10；过线后另号 T1′/T2′（禁与样本 R 同号）
   - 方案 L（更严）：决策页仍交，但 **T0‴ 也须等 Stage2 绿出口后**才授权（观察期内连离线 sim 也不开）——写清与 M 的利弊（拖慢推进 vs 更干净隔离）
   - 方案 W（否决默认）：观察期内部署熔炼 / 默默改 ×0.97 或关 set_bonus / 开样本 R / Pack6 / 天赋或灰印续跑 / 重开 Stage1·2 施工本体 / 让熔炼改成色·族·阶 —— 写清否决理由
3. 不动清单与门禁：体力/掉落/event_rate/ALTS；价表；Pack6；天赋轨；灰印轨；守招邻域；**任一 R 窗施工本体**；D301–D311 主交付；D308 §2.1 门槛数字；**set_bonus / bv / jar**（本窗禁改）；Stage1 enabled/migrate（本窗不关）；Stage2 ×0.97（钉死）。本 tip 窗默认**不部署**；批 M 后离线 T0‴ 另号（tools/p1sim），**仍零 live**
4. 批注勾选：批 M（荐 · 观察期可离线 T0‴） / 批 L（绿出口后才 T0‴） / 驳回改派（其它薄 UX · 须点名且不得偷开 R / Pack6 / 改线上）

交付：只 docs；批准前禁改 YAML/Java/NI；批准后若仅同步 backlog / tip 指针可同窗 docs；**离线 T0‴ / 插件施工 / 部署**各自另号，**不得**借本号开事件/调律/forge/走廊W2 等 R，**不得**观察期改线上 jar/yml。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / **工坊 R（价表）** · **不**开深渊 R · **不**开周本 R · **不**开 Boss 预警 R · **不**开挂机 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**改 refineCost / qualityCost / enhance 价 · **不**抬掉率 / 新材料轨  
- **不**扩 Pack6 / 新模式 · **不**重开 Stage1/2 施工本体 · **不**开档 S  
- **不**改线上 `set_bonus` / bv / jar · **不**改受伤 ×0.97 · **不**关 Stage1 enabled/migrate  
- **不**在观察期部署 K3 / 写熔炼 Java/菜单进 live  
- **不**重开 D301–D311 主交付邻域 · **不**改 D308 门槛数字  
- **不**把薄 UX / 菜单半行再拧当本窗主施工  
- **不**写假「已部署 K3 / 已开 R」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 M 后：策划落 DESIGN；**离线 T0‴**可另号（p1sim only）；**施工/部署**等 Stage2 绿出口或总控另签。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D308：未满完整 P 周 / `runs`≥30；**不得开** |
| 走廊 W2 · **工坊 R（价）** · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | D308 全表不得开 |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 等人 |
| 组队 / 图录 / 钱包 / VIP / 技能页薄余感 | 已收或偏软；非最尖 |
| 工坊菜单再拧半行 | D307 已收；再拧=薄 UX |
| 重开 D301–D311 · 改 D308 门槛 | 门槛钉死；禁再拧 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / **档 S 施工** / Stage1·2 再施工 / 改 ×0.97 / 关 set_bonus | 硬禁 / 须另号 / 观察期禁拧 |
| 玩家面遥测 KPI | D298-W / D308 / D310 / D311 否决 |
| 仅再写观察 STATUS（无 K3） | D326 已批执行；用户要持续推进结构债 |

---

## 6. 刚结指针

- D326 观察 · [`STATUS-ember-six-slot-stage2-observe-close-d326-2026-10-08.md`](STATUS-ember-six-slot-stage2-observe-close-d326-2026-10-08.md) · **批 A · 批 M · 执行中** · tip @ `b2f9b744` · 批 `00473c04`  
- D326 DESIGN · [`DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md`](../design/DESIGN-ember-six-slot-stage2-observe-close-2026-10-08.md)  
- D325 live · [`STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md`](STATUS-ember-six-slot-stage2-set-bonus-live-d325-bv62-2026-10-08.md) · **PASS · `6e734dc7`** · bv62 · set_bonus=true  
- D318 T1 · [`DESIGN-ember-six-slot-t1-spec-revision-2026-10-08.md`](../design/DESIGN-ember-six-slot-t1-spec-revision-2026-10-08.md) · §1.3 K3 · 批 A·K0 · K3 备选  
- D324 Stage1 观察 · [`DESIGN-ember-six-slot-t3-rollout-2026-10-08.md`](../design/DESIGN-ember-six-slot-t3-rollout-2026-10-08.md)  
- D308 · [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) · **当前全表不得开**  
- 灰印 T0b **HOLD** · 天赋 HOLD · Pack6 硬禁  
- 备份 · `/workspace/tmp/d325-bv62-backup-20261008173047/`

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。**已关 · 批 A · 批 M · D327**——授权另号离线 T0‴；施工/部署默认等 Stage2 绿出口；不批 L；本号不另签并行施工；样本门禁 R 全表不得开；禁偷开 R / Pack6 / 天赋 / 灰印 / 改 ×0.97 / 关 set_bonus / 观察期上线。*
