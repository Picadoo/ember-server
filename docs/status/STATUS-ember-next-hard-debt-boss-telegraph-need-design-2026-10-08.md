# 状态 · 下一档硬债选定 · 需策划（Boss 预警诚实 / 相位线索可读）

> **上游结案：** D303 周本/团本段感方案 **M 已上线**（CoreRpg **1.65.93** · `152ad6bc`）；灰印副招 **T0/T0b ❌ · HOLD**；D302 深渊可感 **M 已上线**（1.65.92）；D301 守招·余烬招架 **已上线**（1.65.91）；天赋换机制 **HOLD**；事件 R/W / 调律 R / 工坊 R / 走廊 W2 / 深渊 R / 周本 R 均等人感或样本。见 [`STATUS-ember-weekly-raid-feel-diff-d303.md`](STATUS-ember-weekly-raid-feel-diff-d303.md)。

**日期：** 2026-10-08（上海时间）  
**上游：** D303 周本段感已收 · 灰印 HOLD · 天赋 HOLD · 样本门禁 R 窗后置 · D283/D295 破绽闪与本局摘要已收  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up  
**硬规格（已批 A · 已施工 D304 · CoreRpg **1.65.94**）：** [`DESIGN-ember-boss-telegraph-honesty-2026-10-08.md`](../design/DESIGN-ember-boss-telegraph-honesty-2026-10-08.md) · **方案 M**（W1a+W1b+W1c 必做；W1d 同批；R 后置；W 否决；W1c whiff 键常驻+条件文案）· backlog `B-boss-telegraph-honesty` · 勿再改设计主交付  
**打开理由：** 走廊/深渊/团本入口段感（D300–D303）与破绽成功闪+结算摘要（D283/D295）已落；**招式起手预警与相位切换线索**仍参差——玩家常在「半血砸地 / 冲撞 / 烬核」真正到来前读不清或读到假线索。要在不扩 Pack6、不新开 Boss 招、不抬体力/掉率前提下，让预警与相位可感且诚实。

---

## 1. 选题（七选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D298 tip：须先攒 1～2 周真人样本；硬禁未解除 |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / 工坊方案 **R** / 深渊 **R** / 周本 **R** | **否** | 均后置等人感；D302/D303 刚上 M，R 不抢 |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / 六槽 / 副手方案 B / 新 Boss 招式包 | **否** | 硬禁 / 搁置；禁再堆 Pack |
| F | 挂机庭软身份 / 结算品种可感（扩摘要） | **否（本窗）** | 可作备选；挂机 D285 到顶引导已收、身份债偏软；结算 D283/D295 已有摘要行——本窗主债是**招前可读**，不抢结算再堆字 |
| **G** | **Boss 预警诚实 / 相位线索可读（起手+半血/增援/烬核）** | **采纳 · 需策划** | D283=破绽**成功后**首次闪；D303=入口/本间名片。缺口在**招式起手预警**与**相位切换**是否与真实命中/阶段一致、七图+三团本是否同口径可读——非样本门禁 R、非扩 Pack |

**对齐：** D283 破绽闪+摘要 · D295 失败摘要 · D194 半血冷却重锚 · D195 团本反制 · Pack2–5 招式已厚 · REG counter-registry · D300–D303 可感先例 · ARCH 禁 Pack6/六槽 · 灰印/天赋 HOLD。

**未采纳备选说明：** 「挂机庭软身份」「结算品种可感」仅当预警债薄/缺时兜底——现网招式 Pack 已厚但起手/相位可读仍参差，故本窗不改派。

---

## 2. 一句话问题

破绽成功闪与入口名片已有；Boss **起手预警**与**半血/增援/烬核相位**仍常读不清或读到与真实命中不一致的线索——要在**不新开招式 Pack、不抬体力/掉率**前提下，让预警与相位可感且诚实。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】Boss 预警诚实 / 相位线索可读（起手+相位）
优先级：D303 后下一档体验硬债 · 非施工窗 · 对齐 D283 破绽闪（成功后）缺口在招前/相位 · 非灰印/天赋续跑
禁：六槽·Pack6·新Boss招式包·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调（等人）·抬体力/掉率/event_rate/ALTS·事件R/W·调律R·工坊R·走廊W2夹带·深渊R夹带·周本R夹带·纯lore
目标：七图 Q01–Q07 + 团本 R01–R03 招式起手预警可读且与真实形状/时长诚实；半血/增援/烬核等相位切换有一致短线索；不扩 Pack、不加新招

请出 docs/design/DESIGN-ember-boss-telegraph-honesty-YYYY-MM-DD.md，文首 STATUS=待批 A，含：

0. 证据：现行 EmberRunDirector 预警串 / ActionBar / title 覆盖表（按 skill 键）；D283 仅成功后 firstFlash 与结算摘要、未统一起手；D303 团本名片≠招中预警；半血/增援/烬核相位线索是否与 D194/D195 真实钩一致；灰印/天赋为何本窗不抢（HOLD）
1. 玩家感知目标（≤3 条）：例——招式起手一眼知道躲什么/站哪；半血或增援到来前有诚实短线索（不假预警）；七图+三团本同口径，不靠新招/新 Pack 堆辨识
2. 方案表（至少 2 选 1 荐）：
   - 方案 M（荐）：预警/相位文案与节奏短签统一（ActionBar/subtitle/chat 口径表）+ 与真实 skill 键/时长/形状对齐；零经济、零新 Pack；可抽 D283/D303 短签形
   - 方案 R：轻量预警窗/相位钩（例：半血前提示、增援门延迟可读）+ 回滚；写清钩点；禁新招式 Pack；若动战斗压/门压须 p1sim（42±2 / raidwin 口径写清）
   - 方案 W（否决默认）：新 Boss 招式 Pack6+ / 重做全图技能表 / 抬体力掉率 —— 写清否决理由（硬禁扩包 / 边际递减）
3. 不动清单与门禁：体力/掉落/event_rate/ALTS；六槽；Pack6/新招式包；天赋轨；灰印轨；守招邻域；事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R 不夹带。零战斗压不跑 p1sim；动房压/门压/招压才对齐既有模拟门禁
4. 批注勾选：批 M / 批 R / 批 M+R / 驳回改派（其它）

交付：只 docs；批准前禁改 YAML/Java。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / 工坊 R · **不**开深渊 R · **不**开周本 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**扩 Pack6 / 新 Boss 招式包 / 重铺技能表  
- **不**开六槽 · **不**抬体力 / 掉率 / event_rate / ALTS  
- **不**写假「已施工」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 A 后，另派施工号（建议 **D304+**；若方案含战斗压旋钮先 T0 再 T1）。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D298 后须 1～2 周真人样本；证据未满 |
| 走廊 W2 · 工坊 R · 深渊 R · 周本 R | 后置等人；D302/D303 刚上 M |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 刚上线等人 |
| 挂机庭软身份 | D285 到顶已收；身份债偏软，本窗主债是招前可读 |
| 结算品种可感（再扩摘要） | D283/D295 已有摘要；再堆字非难活主债 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / 六槽 / 新 Boss 招式包 | 硬禁 / 搁置 |

---

## 6. 刚结指针

- D303 · [`STATUS-ember-weekly-raid-feel-diff-d303.md`](STATUS-ember-weekly-raid-feel-diff-d303.md) · CoreRpg **1.65.93** · `152ad6bc`  
- 周本 tip（已收为 D303）· [`STATUS-ember-next-hard-debt-weekly-raid-feel-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-weekly-raid-feel-need-design-2026-10-08.md) · `f51a2d56`  
- 灰印 T0b **HOLD** · [`STATUS-ember-ash-imprint-t0b-2026-10-08.md`](STATUS-ember-ash-imprint-t0b-2026-10-08.md) · `73491e7b`  
- D302 · [`STATUS-ember-abyss-feel-diff-d302.md`](STATUS-ember-abyss-feel-diff-d302.md) · CoreRpg **1.65.92**  
- D301 · [`STATUS-ember-guard-skill-parry-d301.md`](STATUS-ember-guard-skill-parry-d301.md) · CoreRpg **1.65.91**  
- D283 · [`STATUS-ember-playfeel-feedback-d283.md`](STATUS-ember-playfeel-feedback-d283.md) · 破绽成功闪+摘要  
- D295 · [`STATUS-ember-playfeel-fail-summary-d295.md`](STATUS-ember-playfeel-fail-summary-d295.md) · 失败路径摘要  
- 天赋 HOLD · [`STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md`](STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md)  
- 加固债总览 · [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md)

---


## 7. 策划交稿（待批 A）

- 硬设计：[`DESIGN-ember-boss-telegraph-honesty-2026-10-08.md`](../design/DESIGN-ember-boss-telegraph-honesty-2026-10-08.md) · **已批 A** · **批 M** · 施工 **D304**
- Backlog：`B-boss-telegraph-honesty`（已批 A · 已施工 D304 · CoreRpg **1.65.94**）
- 批准前禁 YAML/Java；施工号建议 D304+（总控另派）
- Git：本地 commit；GitHub 推送暂堵时总控代推

*服务器：proxy/login/play 保持 up；本 tip 零部署。*
