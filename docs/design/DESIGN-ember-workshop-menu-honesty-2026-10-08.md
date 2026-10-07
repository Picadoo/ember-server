# 工坊菜单诚实（费用 / 材料闸同屏 · 非 forge R）· 2026-10-08

> **STATUS：待批 A**  
> **日期：** 2026-10-08 Asia/Shanghai  
> **来源：** 总控派单「硬设计待批 A」· tip [`STATUS-ember-next-hard-debt-workshop-menu-honesty-need-design-2026-10-08.md`](../status/STATUS-ember-next-hard-debt-workshop-menu-honesty-need-design-2026-10-08.md)（`ba0ac686`）· D306 枢纽日路由后下一档**体验**硬债 · 对齐加固债 #3 再刷动机侧「费用可见」缺口  
> **性质：** 硬设计 **待批 A**；本窗 **只 docs**；批准前禁改 YAML/Java。  
> **硬约束：** TrMenu 点选 · NI · 同服 Multiverse · **六槽不做** · **禁 Pack6 / 新工坊玩法包 / 新材料轨** · **禁改 `refineCost` / `qualityCost` / `enhanceCost` / `upgradeCost` 数值（= 偷换 forge R）** · **禁抬体力 / 掉率 / event_rate / ALTS** · **禁永久统一减伤/增伤 / 新永久乘区** · **禁天赋续跑（HOLD）** · **禁灰印续跑（HOLD）** · **禁守招邻域再调** · **禁事件 R/W · 调律 R · 工坊 R（价/节奏样本窗）· 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R 夹带** · **禁重开 D306 主交付** · **禁纯 lore / 菜单空白却假装已写费用** · 玩家面不写「请执行 /corerpg p1 …」。  
> **Backlog 指针：** `B-workshop-menu-honesty`  
> **邻域边界：** D299 近档 `held_next_*` / `blade_next_*`（**保留，本稿补费用同屏**）· **≠ forge R**（价/节奏样本窗，仍等人感）· D306 日路由（**不重开**）· 灰印/天赋 HOLD · 强化/升阶/精工/成色/互换/分解确认流权威仍在 `EmberForgeService` 聊天预览

---

## 0. 证据（tip + ember_p1_forge + D299 + ForgeService / UpgradeRules 实扫）

| # | 证据 | 出处 | 对本稿含义 |
|---|------|------|------------|
| E1 | tip 选题 G：走廊/深渊/团本/预警/挂机层/枢纽日路由（D300–D306）M 波已收；样本门禁 R 窗仍等人。现网工坊页多处仍写「**消耗以聊天为准**」——D299 只补了近档/相对穿着，**费用不跟确认键同屏** | tip `ba0ac686` §1–§2 | **本债本体**；零改价 = 非样本门禁；与 forge **R** 硬切 |
| E2 | `ember_p1_forge` 确认格 **五处**明文「先点上面的预览，消耗以聊天为准」：强化 `e`、升阶 `u`、精工 `r`、成色 `q`、互换 `s` | `plugins/TrMenu/menus/ember_p1_forge.yml` L46 / L65 / L89 / L112 / L145 | **确认键零费用镜像**；玩家必须先预览再读聊天才知扣什么 |
| E3 | 精工/成色**预览**格已有 D299：`%corerpg_p1_held_next_c%` / `held_next_q%`（手持件当前→下一档 + 费用摘要），但仍附「§8费用以聊天预览为准」；强化/升阶/互换**预览格无**同类费用 PAPI | 同文件 L79–80 / L102–103 · STATUS D299 W1b | D299 **覆盖近档文案**，**未**覆盖「确认键同屏费用」与强化/升阶/互换费用嵌入 |
| E4 | 装备页 D299 W1a：`blade_next_q/c` · `charm_next_q/c`（穿着件成色·精工→下一档费用）；结算 W1c 相对穿着对照——**皆不回答「点确认这一下要扣什么、背包够不够」** | STATUS D299 · `EmberGearNextHint` | 近档可读 ≠ 本次操作闸同屏；本稿补后者 |
| E5 | `EmberGearNextHint.costShort` / `qualityLine` / `craftLine` 已能把 `EmberUpgradeRules.qualityCost` / `refineCost` 压成「胚16 骨16 币1600」形；PAPI 经 `EmberRunPapi` `held_next_*` / `blade_next_*` 暴露 | `EmberGearNextHint.java` · `EmberRunPapi.java` | **真源算法已有**；菜单侧可镜像，无需改价表 |
| E6 | `EmberForgeService`：预览路径 `enhance` / `refine` / `upgrade` / `swap` / `dismantle` 均 `sendMessage` 打出 `Cost.label()`（碎片×N · 核心×N · 胚料×N · 骨尘×N · 余烬币×N）+ 缺料 `lacking` + D96 材料来源短句 + `ConfirmTokens` 聊天确认钮；确认才 `payDurable` | `EmberForgeService.java` preview / simple / lacking / sources | **权威扣费流在聊天**；菜单空白是展示债，不是规则债 |
| E7 | 价表真源（本稿 **零改**）：`enhanceCost` 按档碎片/核心/币；`upgradeCost` T1→T2=60/12/6/0/1500 · T2→T3=60/15/6/0/1800；`refineCost` 0→1=胚3骨5币300 · 1→2=6/10/600 · 2→3=12/20/1200；`qualityCost` 0→1=8/8/800 · 1→2=16/16/1600（成色 2→3 工坊无） | `EmberUpgradeRules.java` | 钉「费用真源仍是 UpgradeRules / 聊天预览算法，菜单只镜像」 |
| E8 | 互换 `swap`：**免费**（`SwapPlan` 无 Cost）；分解：预览关菜单 + 聊天 `[确认分解]` 一键 token（B2.137）；确认格 `d` 已写「确认在聊天栏」——**分解不伪称菜单内扣费** | `ember_p1_forge.yml` S/s/D/d · ForgeService dismantle | 互换应同屏写「免费」；分解保持聊天确认权威，可补「得胚×T」预览短签，**不**把销毁确认搬回菜单硬点 |
| E9 | 旧轨菜单仍在：`ember_forge`（T1→T2/T2→T3 静态配方 lore 已写消耗数字）· `ember_enhance`（「按表消耗碎片/骨尘/核心」**无具体档费用**）· `ember_socket` · `ember_disassemble`（重铸石消耗已人话化 B2.20）——注释写明「旧物品仍走旧强化」；**P1 主入口是 `ember_p1_forge`** | 各 yml 文首 · tip E 扫描要求 | 本稿主交付钉 **P1 工坊**；旧轨仅旁注「不重开旧强化经济」，可选同形轻补但不抢主窗 |
| E10 | tip / D299：forge **R** =「看得见但养不起」时**单档轻调价** + p1sim；样本门禁未解除前不开。菜单诚实 = **零改价镜像** | tip §1 表 C/G · DESIGN D299 §2.2 | **≠ forge R**；误动价表须升格另批 |
| E11 | 灰印 T0/T0b **HOLD**；天赋换机制 **HOLD**；守招 D301 等人；事件 R/W / 调律 R / 走廊 W2 / 深渊 R / 周本 R / Boss 预警 R / 挂机 R 后置；**D306 刚上线禁重开** | tip §5 · backlog | **本窗不抢** |
| E12 | UX：工坊经枢纽「工坊」/装备页 `menu: ember_p1_forge`；点选 TrMenu；材料 NI（胚料/骨尘/碎片/核心）；玩家不靠手打 `confirm`（D95/B2.176） | `ember_hub` / `ember_p1_gear` / `ember_p1_forge` | 入口保持菜单点选；PAPI 只展示 |

**为何「消耗以聊天为准」仍尖：** D299 让精工/成色**预览旁**能看见近档费用摘要，但五处**确认键**仍把真相推到聊天；强化/升阶/互换连预览格都无费用/「免费」同屏。玩家路径仍是「点预览 → 低头读聊天 → 再回菜单点确认」，与 D306「同屏诚实」形不一致。

**为何 ≠ forge R：** forge R 改的是 `refineCost`/`qualityCost`/enhance 档价与节奏（养不起→调价），须人感/样本 + 经济门禁。本窗只把**已有** `Cost.label()` / `costShort` **嵌进菜单 lore**，数值一字不动。

**为何不抢灰印/天赋 HOLD、不重开 D306：** tip 七选一已否 A–F；灰印/天赋 HOLD；D306 刚收 §1B；本债是工坊**菜单可读性**，不是日路由、换机制或调价。

**一句话问题：** 近档进度已可见；工坊确认前仍要「先预览再读聊天」——要在**不改工坊价表、不改掉落、不抬体力**前提下，让费用与材料闸**菜单同屏诚实**（≠ forge R）。

---

## 1. 玩家感知目标（≤3）

1. **确认前菜单写清消耗：** 强化 / 升阶 / 精工 / 成色（及互换「免费」）在预览格与确认格 lore 一眼看到本次操作要花的币/胚/骨/碎片/核心（或免费），不再只靠聊天。  
2. **材料不够诚实灰显或短句：** 不够时菜单侧有可读原因（或缺料半行），与 `EmberForgeService.lacking` 口径一致；不假装点确认就能扣。  
3. **预览与确认同屏一致：** 菜单镜像与聊天预览算法同源（UpgradeRules）；确认键不再写「消耗以聊天为准」当唯一答案（聊天仍可作权威备份）。

---

## 2. 方案表

### 2.1 方案 M（**荐** · PAPI/短签嵌费用 · 材料不足诚实 · **零改价**）

> **原则：** 费用真源仍是 `EmberUpgradeRules` + `EmberForgeService` 预览算法；菜单**只镜像**。抽 D299 近档形（`held_next_*` / `costShort`）+ D306 同屏诚实形。禁止菜单写一套、聊天扣另一套。

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 费用同屏口径表 + 确认/预览 lore（必做）** | 定玩家面口径表（设计钉死、施工照表）：操作种 → 展示键 → lore 行。意向——**精工/成色**：确认格改嵌 `%held_next_c/q%` 或更短「本次：胚N 骨N 币N」（与预览同源）；去掉「消耗以聊天为准」作唯一句，可留「与预览一致」半行。**强化**：新增只读 PAPI（例 `held_enhance_cost` / `held_enhance_rate`）嵌预览+确认：`+N→+N+1 · 碎片×a 核心×b 币×c · 成功率 p%（保底 n/m）`。**升阶**：`held_upgrade_cost`：`T1→T2 · 碎片×60 …`（闸未开时诚实「需首通 Q04/Q07」）。**互换**：确认/预览写「免费 · 交换强化等级+失败计数」。**分解**：确认格保持「在聊天栏确认」；预览可补「得胚×T」（只读），**不**把销毁硬点回菜单 | 一切 Cost 数值 · 成功率表 · 保底次数 |
| **W1b · 材料不足诚实提示（必做）** | 菜单侧：缺料时确认格灰显或 lore 半行「缺少：…」（口径对齐 `lacking` 人话）；可选 Open/update 刷新。材料够则显示完整费用行。来源短句（D96）可缩为确认旁半行或仍留给预览聊天——**不**新开材料轨 | 扣费时机仍在 confirm；不改 NI 掉落 |
| **W1c · 钉 D299 近档与真源镜像（必做）** | 保留装备页 `blade/charm_next_*` 与工坊 `held_next_*`；本窗**扩展**费用同屏，不删近档。施工验收：菜单费用行 == `Cost.label()`/`costShort` 同源；聊天预览可保留作备份，**禁止**两套数 | D299 近档主语义 · UpgradeRules 数值 |
| **W1d · 旧轨旁注（可选同批）** | `ember_enhance` 若仍被旧物入口点到：可加「P1 请走主菜单工坊」互指；**不**重做旧 `corerpg enhance` 经济。`ember_forge` 静态配方已写数字则不动 | 旧强化掉级规则 · forge.yml 升阶 |

**期望：** 打开 P1 工坊，不点预览也能在确认格看见「这次扣什么 / 够不够 / 互换免费」；点预览仍与菜单一致——可感来自**镜像 + 缺料诚实**，不是改价。

**荐批：** **批 M**（W1a+W1b+W1c 必做；W1d 同批或可砍）。

### 2.2 方案 R（轻交互偏置 · 不够时确认灰锁 / 预览刷新密度 · **禁改价表**）

| 项 | 内容 |
|----|------|
| **做什么** | 可选轻旋钮（缺省=现行）：材料不足时确认键灰锁（点击 tell「材料不足」不发 confirm）；预览后自动 `update` 刷新费用 lore 密度；缺料半行高亮 |
| **玩家感知** | 「不够点不了确认 / 费用刷新更勤」——与 M 口径一致 |
| **回滚** | 键缺省=现行可点确认（仍由服务端拒扣） |
| **相对 M** | 表达力略强；可能动 TrMenu 条件图标或薄 jar 闸展示；**仍禁**改 refineCost/qualityCost/enhance/upgrade 数值 |
| **门禁** | 若 R **仅**灰锁/刷新/展示（零改价）→ **不跑 p1sim**；若误动价表 → **升格 forge R** 另批 + 经济对照 |
| **本稿建议** | **不荐纯 R 首批**；若总控要灰锁，勾 **批 M+R**（先 M 镜像，R 作续窗或同批轻闸） |

### 2.3 方案 W（**否决默认** · 调低工坊价 / 新材料轨 / Pack6 新工坊玩法）

| 项 | 内容 |
|----|------|
| **提议（不采纳）** | 顺手调低 refine/quality/enhance 价、新「工坊石」材料轨、Pack6 新工坊玩法模式，或菜单假写费用与真源脱节 |
| **否决理由** | ① tip 明文：改价 = **偷换 forge R**，样本门禁未开；② 新材料轨 / Pack6 硬禁与经济过敏；③ D299 已证痛点在**看得见**，首因不是价；④ 假费用比「以聊天为准」更糟 |
| **结论** | **默认否决**；批注**不**提供「批 W」勾选 |

---

## 3. 不动清单与门禁

### 3.1 不动（硬表）

| 项 | 锚点 | 本稿 |
|----|------|------|
| `refineCost` / `qualityCost` / `enhanceCost` / `upgradeCost` 数值 | `EmberUpgradeRules` | **不动**（M/R 展示偏置皆禁改价；改价=forge R） |
| `QUALITY_WEIGHTS` / `CRAFT_WEIGHTS` / 深渊成色表 | `EmberRunRules` 等 | **不动** |
| 体力日回/上限/单局 · 掉落 · `event_rate` · ALTS | tip | **不动** |
| 六槽 / Pack6 / 新工坊玩法 / 新材料轨 | tip · ARCH | **不做** |
| 天赋续跑 / 灰印续跑 / 守招邻域再调 | HOLD · D301 | **不做** |
| 事件 R/W · 调律 R · **工坊 R** · 走廊 W2 · 深渊 R · 周本 R · Boss 预警 R · 挂机 R | tip | **不夹带** |
| D306 主交付（日路由决策板） | STATUS D306 | **不重开** |
| D299 近档 / 结算相对穿着 | STATUS D299 | **保留**；本稿只扩费用同屏 |
| 分解聊天一键确认权威（B2.137） | ForgeService dismantle | **保留**；不改销毁安全模型 |
| 假费用与真源脱节 / 纯 lore | tip | **驳回** |

### 3.2 门禁

| 若动到… | 门禁 |
|---------|------|
| **仅 M · W1a/b/c/d**（口径表 + lore/PAPI 镜像 + 缺料提示 + 钉 D299） | 静态：菜单费用行与 `Cost.label()`/`costShort`/enhance 表一致；冒烟：手持各档强化/精工/成色/升阶/互换/无手持；缺料见诚实短句；确认与预览同数；分解仍走聊天确认；**不要求 p1sim**（零经济压；**类比 D299/D306 菜单 M**） |
| **R · 仅灰锁 / 刷新密度（不改价）** | 冒烟对照；**默认不要求 p1sim** |
| **R · 动 refine/quality/enhance/upgrade 价或权重** | **禁止本窗**；须升格 **forge R** + 经济/p1sim 对照另批 |
| **调低价 / 新材料 / Pack6 / 假费用** | **禁止**；不设门禁通道 |

**本窗门禁结论：** 荐 **M** → 口径表静态核对 + 工坊冒烟为主，**写明不走 p1sim**。若批 **M+R** 且 R 仅展示/灰锁，仍可不跑 p1sim；仅当误动价表才启用 forge R 门禁（本窗默认不做）。

---

## 4. 切窗建议（批 A 后）

1. 文首改 **已批 A**（只 docs）→ 总控另派施工号（建议 **D307+**；本荐 M **无**价旋钮，通常不跑 p1sim）。  
2. **首窗：** M · W1a（费用同屏口径 + 确认/预览 lore）+ W1b（缺料诚实）+ W1c（钉 D299/真源镜像）；W1d 可选。  
3. **R：** 默认后置；仅 **批 M+R** 时开确认灰锁/刷新密度，仍禁改价。  
4. **禁同塞：** 改价表、新材料轨、Pack6、六槽、抬体力掉落、天赋、灰印、守招再调、事件 R/W、调律 R、工坊 R、走廊 W2、深渊 R、周本 R、Boss 预警 R、挂机 R、重开 D306、假费用、纯 lore。

---

## 5. 禁止项（自检清单）

- [ ] 六槽  
- [ ] Pack6 / 新工坊玩法包 / 新材料轨  
- [ ] 改 `refineCost` / `qualityCost` / `enhanceCost` / `upgradeCost`（偷换 forge R）  
- [ ] 抬体力 / 掉率 / `event_rate` / ALTS  
- [ ] 永久统一减伤/增伤 / 新永久乘区  
- [ ] 菜单费用与 UpgradeRules / 聊天预览脱节（假费用）  
- [ ] 纯 lore 薄窗 / 只改文案不嵌真源  
- [ ] 天赋续跑 / 灰印续跑 / 守招邻域再调  
- [ ] 事件 R/W / 调律 R / **工坊 R** / 走廊 W2 / 深渊 R / 周本 R / Boss 预警 R / 挂机 R 夹带  
- [ ] 重开 D306 / 拆掉 D299 近档  
- [ ] 把分解销毁确认硬搬回菜单（破坏 B2.137 token 安全）  
- [ ] 未批施工 YAML/Java  
- [ ] 零经济压却瞎跑 p1sim / 未改价却宣称经济验收

---

## 6. 邻域边界

| 邻域 | 边界 |
|------|------|
| **D299 再刷短反馈** | **保留**近档与结算对照；本稿补「本次操作费用/闸同屏」 |
| **forge R（价/节奏）** | **后置**；本窗明文切开；误动价表升格另批 |
| **D306 枢纽日路由** | **不重开**；不夹带枢纽决策板 |
| **旧 ember_enhance / forge / socket / disassemble** | 旁注；主交付 P1 `ember_p1_forge` |
| **天赋 HOLD / 灰印 HOLD** | 不夹带 |
| **守招 D301** | 已上线等人；不碰 |
| **加固债 #3** | 再刷动机侧「费用可见」；不抬掉率/新材料 |

---

## 7. 批注勾选（总控）

- [ ] **批 M**（W1a 费用同屏口径+确认/预览 lore + W1b 缺料诚实 + W1c 钉 D299/真源镜像必做；W1d 可选；R 后置）· **策划荐**  
- [ ] **批 R**（仅确认灰锁/刷新密度；禁改价）· **不荐首批**  
- [ ] **批 M+R**（M 首窗 + R 轻灰锁/刷新同批或预留续窗）  
- [ ] **驳回改派**（理由：________）

**策划荐勾：** **批 M**。

**说明：** 方案 W 为否决默认，**不提供「批 W」勾选**。

---

## 7.5 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-08 | 策划 · 初稿 STATUS **待批 A** · 荐方案 M（W1a+W1b+W1c；W1d 可选）；R 后置可勾 M+R；W 否决 |

---

## 8. 参考

- tip · [`STATUS-ember-next-hard-debt-workshop-menu-honesty-need-design-2026-10-08.md`](../status/STATUS-ember-next-hard-debt-workshop-menu-honesty-need-design-2026-10-08.md)（`ba0ac686`）  
- D299 · [`DESIGN-ember-refarm-short-feedback-2026-10-07.md`](DESIGN-ember-refarm-short-feedback-2026-10-07.md) · [`STATUS-ember-refarm-short-feedback-d299.md`](../status/STATUS-ember-refarm-short-feedback-d299.md)  
- D306 · [`DESIGN-ember-hub-daily-routing-2026-10-08.md`](DESIGN-ember-hub-daily-routing-2026-10-08.md)  
- 真源 · `plugins/TrMenu/menus/ember_p1_forge.yml` · `EmberForgeService` · `EmberUpgradeRules` · `EmberGearNextHint` · `EmberRunPapi`  
- 旧轨旁注 · `ember_forge.yml` · `ember_enhance.yml` · `ember_socket.yml` · `ember_disassemble.yml`  
- backlog · `B-workshop-menu-honesty`

---

*待批 A；批准前禁施工 YAML/Java。*
