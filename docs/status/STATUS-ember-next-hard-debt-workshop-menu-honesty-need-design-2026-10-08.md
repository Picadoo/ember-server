# 状态 · 下一档硬债选定 · 需策划（工坊菜单诚实 / 费用同屏 · 非 forge R）

> **上游结案：** D306 枢纽日路由诚实方案 **M 已上线**（CoreRpg **1.65.96** · `c3a804d2`）；加固稿 **§1B 枢纽反馈散已收**。Live **D301–D306**。灰印副招 **T0/T0b ❌ · HOLD**；天赋换机制 **HOLD**；事件 R/W / 调律 R / **工坊 R（价/节奏）** / 走廊 W2 / 深渊 R / 周本 R / Boss 预警 R / 挂机 R 均等人感或样本。见 [`STATUS-ember-hub-daily-routing-d306.md`](STATUS-ember-hub-daily-routing-d306.md)。

**日期：** 2026-10-08（上海时间）  
**上游：** D306 日路由已收 · 加固 §1A–§1C 主债 M 波已清（走廊 D300 · 挂机 D285+D305 · 枢纽 §1B D306）· 战斗可感波 D301–D304 已收 · 灰印/天赋 HOLD · **样本门禁 R 窗仍为余下「真硬」战斗/经济债**  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up  
**硬规格（已上线 D307）：** 派单策划出 [`DESIGN-ember-workshop-menu-honesty-2026-10-08.md`](../design/DESIGN-ember-workshop-menu-honesty-2026-10-08.md) · STATUS=待批 A · 荐 M/R/W · backlog `B-workshop-menu-honesty`  
**打开理由：** 非样本门禁的体验硬债（走廊/深渊/团本/预警/挂机层/枢纽日路由）M 波已收；余下战斗/价窗真硬债几乎全是 **样本门禁 R**。现网工坊页仍多处写「**消耗以聊天为准**」——D299 只补了近档/相对穿着短反馈，**费用仍不跟菜单同屏**。要在**不改 refineCost/qualityCost、不改掉落、不抬体力**前提下，让强化/升阶/精工/成色/互换的消耗与材料闸**菜单内诚实可读**（**≠ forge R 价/节奏样本窗**）。

---

## 0. 局势一句话（样本门禁声明）

加固稿 §1 主循环/枢纽/挂机软债的 **M 波已收**；仍开的「难活」若只论战斗压/价压，几乎全是 **事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊/周本/Boss/挂机 R**（等人感或 D298 周样本）。本 tip **不**把其中任一单独采纳为下一档施工主债；改采纳仍薄、仍可设计、且**零样本门禁**的工坊**菜单诚实**（费用同屏 · 禁改价）。

---

## 1. 选题（七选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D298 tip：须先攒 1～2 周真人样本；硬禁未解除 |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2** / **工坊方案 R（价/节奏）** / 深渊 **R** / 周本 **R** / Boss 预警 **R** / 挂机 **R** | **否** | 均样本/人感门禁；**工坊 R≠菜单诚实**（见 G）；D300–D306 刚上 M，R 不抢 |
| D | 天赋续跑（T0'''）/ **灰印续跑** / 守招邻域再调 / 纯 lore | **否** | 天赋 **HOLD**；灰印 T0/T0b **HOLD**；招架等人；纯 lore 薄非难 |
| E | Pack6 / 六槽 / 新模式 / 抬体力掉落 | **否** | 硬禁 / 搁置 |
| F | 组队·结算余感 / 图录↔预警对齐 / 经济显示诚实 / 重开 D306 / 加固 §1 docs 同步窗 | **否（本窗主交付）** | 结算 D283/D295 已有摘要，组队余感偏软；图录 B2.178+D304 局内预警已分轨，对齐属薄维护；经济显示 B2.xx / D284 已大量诚实化；**禁**重开刚上线 D306；§1 同步可作旁注，不占硬规格主交付 |
| **G** | **工坊菜单诚实（费用/材料闸同屏 · 非 forge R）** | **采纳 · 需策划** | 现网 `ember_p1_forge` 多格仍「消耗以聊天为准」；D299=`held_next_*` 近档，**未**把预览费用嵌进菜单；零改价 = 非样本门禁；与 forge **R**（养不起→调价）严格切开 |

**对齐：** D299 再刷短反馈（近档 · R 后置）· `ember_p1_forge.yml`「消耗以聊天为准」· `EmberGearNextHint` / `EmberForgeService` 预览仍走聊天 · 加固债 #3 再刷动机 · ARCH 禁 Pack6/六槽 · 灰印/天赋 HOLD · **forge R 样本门禁不偷换**。

**未采纳备选说明：** 「组队/结算余感」「图录↔D304 预警对齐」「经济显示诚实」仅当工坊菜单诚实债薄/缺时兜底——现网确认键仍把费用推到聊天，故本窗不改派。加固 §1 已收债可在 backlog/hardening 旁注同步，**不**单独开「只同步 STATUS」当硬债主交付。

---

## 2. 一句话问题

近档进度已可见；工坊确认前仍要「先预览再读聊天」——要在**不改工坊价表、不改掉落、不抬体力**前提下，让费用与材料闸**菜单同屏诚实**（≠ forge R）。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】工坊菜单诚实（费用/材料闸同屏 · 非 forge R）
优先级：D306 后下一档体验硬债 · 非施工窗 · §1B 已收 · 样本门禁 R 不抢 · 非灰印/天赋续跑 · 非重开 D306
禁：六槽·Pack6·新模式图包·抬体力/掉率/event_rate/ALTS·改 refineCost/qualityCost/enhance 价表（=偷换 forge R）·天赋续跑（HOLD）·灰印续跑（HOLD）·守招邻域再调（等人）·事件R/W·调律R·工坊R（价/节奏样本窗）·走廊W2夹带·深渊R/周本R/Boss预警R/挂机R夹带·重开D306·纯lore
目标：工坊强化/升阶/精工/成色/互换（及必要分解口径）在菜单内一眼看到「要花什么、够不够」；预览与确认同屏对齐现网规则；费用仍以真源表为准，本窗零改价

请出 docs/design/DESIGN-ember-workshop-menu-honesty-2026-10-08.md，文首 STATUS=待批 A，含：

0. 证据：现行 `ember_p1_forge` / 旧 `ember_forge`·`ember_enhance`·`ember_socket`·`ember_disassemble` 哪些格仍「消耗以聊天为准」；D299 `held_next_*` / `blade_next_*` 覆盖了什么、没覆盖费用同屏；`EmberForgeService` 预览/确认聊天流；为何 ≠ forge R（价/节奏样本窗）；灰印/天赋为何本窗不抢（HOLD）；为何不重开 D306
1. 玩家感知目标（≤3 条）：例——点确认前菜单已写清币/胚/骨等消耗；材料不够诚实灰显或短句原因；预览与确认口径一致，不再「菜单空白、真相只在聊天」
2. 方案表（至少 2 选 1 荐）：
   - 方案 M（荐）：PAPI/短签把现网预览费用嵌进菜单 lore（手持件 + 操作种）；材料不足诚实提示；零改价表；可抽 D299 近档形 + D306 同屏诚实形；钉「费用真源仍是 UpgradeRules/聊天预览算法，菜单只镜像」
   - 方案 R：轻量交互偏置（例：不够时确认键灰锁 / 预览自动刷新密度）+ 回滚；**禁**改 refineCost/qualityCost/enhance 数值；若误动价表须升格为 forge R 样本窗并另批
   - 方案 W（否决默认）：顺手调低工坊价 / 新材料轨 / Pack6 新工坊玩法 —— 写清否决理由（偷换 forge R / 经济过敏 / 硬禁扩包）
3. 不动清单与门禁：refineCost / qualityCost / enhance·upgrade 价与权重；QUALITY_WEIGHTS / CRAFT_WEIGHTS；体力/掉落/event_rate/ALTS；六槽；Pack6；天赋轨；灰印轨；守招邻域；事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R · D306 主交付 不夹带。零经济压不跑 p1sim；仅当误动价/收益压才对齐既有模拟门禁（本窗默认不做）
4. 批注勾选：批 M / 批 R / 批 M+R / 驳回改派（其它）

交付：只 docs；批准前禁改 YAML/Java。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / **工坊 R（价表）** · **不**开深渊 R · **不**开周本 R · **不**开 Boss 预警 R · **不**开挂机 R  
- **不**开天赋 T0''' / 灰印续跑 / 守招邻域再调  
- **不**改 refineCost / qualityCost / enhance 价 · **不**抬掉率 / 新材料轨  
- **不**扩 Pack6 / 新模式 · **不**开六槽 · **不**抬体力  
- **不**重开 D306 主交付邻域  
- **不**写假「已施工」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 A 后，另派施工号（建议 **D307+**；本荐 M **无**价旋钮，通常不跑 p1sim）。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D298 后须 1～2 周真人样本；证据未满 |
| 走廊 W2 · **工坊 R（价）** · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | 样本/人感门禁；本窗明文切开「菜单诚实 ≠ forge R」 |
| 天赋 T0''' / 灰印续跑 / 守招再调 | HOLD / HOLD / 刚上线等人 |
| 组队 / 结算余感（再扩摘要） | D283/D295 已有摘要；组队余感偏软 |
| 图录 ↔ D304 预警对齐 | 图录静态 + 局内短签分轨；对齐偏维护薄窗 |
| 经济显示诚实（钱包/源汇玩家面） | B2.xx / D284 已大量口径诚实；非本窗最尖缺口 |
| 重开 D306 / 加固 §1 纯 STATUS 同步 | 刚上线禁再拧；同步可旁注，不占硬规格主交付 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / 六槽 / 抬体力掉落 | 硬禁 / 搁置 |

---

## 6. 刚结指针

- D306 · [`STATUS-ember-hub-daily-routing-d306.md`](STATUS-ember-hub-daily-routing-d306.md) · CoreRpg **1.65.96** · `c3a804d2` · **§1B 已收**  
- 枢纽 tip（已收为 D306）· [`STATUS-ember-next-hard-debt-hub-daily-routing-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-hub-daily-routing-need-design-2026-10-08.md) · `c31384ba`  
- D305 · [`STATUS-ember-hang-farm-soft-identity-d305.md`](STATUS-ember-hang-farm-soft-identity-d305.md) · CoreRpg **1.65.95**  
- D304 · [`STATUS-ember-boss-telegraph-honesty-d304.md`](STATUS-ember-boss-telegraph-honesty-d304.md) · CoreRpg **1.65.94**  
- D303 · [`STATUS-ember-weekly-raid-feel-diff-d303.md`](STATUS-ember-weekly-raid-feel-diff-d303.md) · CoreRpg **1.65.93**  
- D302 · [`STATUS-ember-abyss-feel-diff-d302.md`](STATUS-ember-abyss-feel-diff-d302.md) · CoreRpg **1.65.92**  
- D301 · [`STATUS-ember-guard-skill-parry-d301.md`](STATUS-ember-guard-skill-parry-d301.md) · CoreRpg **1.65.91**  
- D299 · [`STATUS-ember-refarm-short-feedback-d299.md`](STATUS-ember-refarm-short-feedback-d299.md) · 近档已收 · **费用同屏未还** · forge R 后置  
- 灰印 T0b **HOLD** · [`STATUS-ember-ash-imprint-t0b-2026-10-08.md`](STATUS-ember-ash-imprint-t0b-2026-10-08.md)  
- 天赋 HOLD · [`STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md`](STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md)  
- 加固债总览 · [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md) · §1B 经 D306 已收

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。*
