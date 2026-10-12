# Stage0 · 变强途径花样（非难度 densify）· 2026-10-12

> **状态：** 已批 A · 方案 M（总控 · 2026-10-12）· Batch1 tip 已开  
> **触发：** Karen t147u（2026-10-12）—「多弄变强的方式、变强的途径，变着花样来，而不是增加难度」；t146u 明确词缀池滚到七十对玩家几乎无用。  
> **硬停：** **STOP Affix Pack / 词缀池扩容 / 难度 densify**（Pack4–36 及后续一律冻结，除非另批「显眼/教学/图感」且 **零扩池**）。  
> **硬禁（不变）：** 无永久伤乘 · 无 skill_mult 抬 · 无 gem flat · 无 AFK 表抬 · 无 K3 · 无 sx20 · 无 clear-gate / drop-rate hack · 无 hope celebration / PAPI / sig-dry twin · 无 sticky combat-path twin · 无 ConfirmTokens densify · 不重开 legacy socket · 不发明新主循环。

---

## 0. 一句话

玩家要的是「今天又能变强一点、路还不一样」；不是「怪又多俩词缀」。本窗只做 **展示与选择感**：希望一眼可见、工坊诚实选择、图装身份可追、已有保底/必做 **浮出水面**——**不抬表、不扩池、不加难度。**

---

## 1. 问题陈述（为何停词缀）

| 现象 | 玩家感受 | 设计债 |
|------|----------|--------|
| 词缀池 12→70+ | 辨识度不升，配置与记忆成本涨 | 同构变体换名 = 难度噪音 |
| soft densify（今日首次+材料/环境伤减半…） | 补丁噪音，非变强路径 | 掩盖真正的赋能闭环 |
| 希望表盘散落多页 | 一眼看不见「离必成还有几步」 | 已有 `EmberHopeMeter` / remainLine，缺 **单点汇总** |
| 图装 methodLine 已有 | 仍弱「这图掉什么 → 我下一件」 | EM1-A SHIP；EM1-B 曾 BLOCKED（缺业主方法表） |
| sure-do / pity 已大量 SHIP | 玩家不知道，或被 lore 墙淹没 | **表面化 > 再叠 twin** |

**裁定：** 难度扩容线关闭。下一施工轴 = **Power Paths Variety**（变强途径花样）。

---

## 2. 参考摘录（短 · 对位余烬）

| 参考 | 机制本质 | 余烬对位 | 本窗可否落地 |
|------|----------|----------|--------------|
| **Diablo 3 pity timer** | 长时间无传说 → 隐式抬率；目标是 **下限安全感**，不是常态掉率 | 强化/洗练/成色/签名/掉落成色 pity 已存在 | **可**把 n/N·下次必成 **汇成一眼表盘**；**否**再抬 RATE |
| **Diablo 3 Greater Rift** | 单一可见进度条 + 明确奖励口（宝石升级） | 希望表盘 / 图鉴身份 / 宝石通关保底 | **可**学「一条进度 = 一种变强」；**否**再造 GR 难度梯 |
| **PoE craft agency** | 玩家要的是 **选择影响结果**（稳线 vs 赌线），不是纯 slot | K1 诚实入口 + K2 forgeroll floor 已 SHIP；K3 BANNED | **可**加深 commit-one-slot / 预览→确认手感；**否**确定性神器 / 抬极品权 |
| **PoE map / league identity** | 本图自带获取路径（非全局乘区） | `EmberMapIdentity.methodLine` / lootFamily / mapAccessUnlocked | **可**每 Q 一行「农场签名」可见；**否**clear-gate、新掉率乘 |
| **1.12 服常见 RPG** | TrMenu 实物入口 + 短 lore；成长靠强化/洗练/套装追 | 余烬星实物入口；lore≤3 已压 | **可**菜单静音 + 表盘半行；**否**字墙教学 |

结论：业界「少绝望」靠 **可见下限 + 有意义选择 + 图本身份**；不是靠 **内容包扩难度表**。

---

## 3. 四条可施工轴（薄 · 可验收）

### 3.1 Hope dial — 一眼进度

**目标：** 打开枢纽 / 装备 / 工坊任一入口，**≤3 行非空** 看到「手持件下一必成 / 本图身份差 / 宝石保底」——不再散落十几个 remainLine twin。

| 已有资产 | 缺口 | 薄切片（零改数值） |
|----------|------|-------------------|
| `EmberHopeMeter`（强化/洗练/成色/精工/签名…） | hub 希望表盘曾被 lore 压删；多页重复 | **HD1** 单面板「希望一眼」：手持 1 条 + 焦点图 1 条 + 宝石 1 条；其它页只留指针半行 |
| drop_q / event_soft / gem clear pity | 玩家不知存在 | **HD2** 成功/失败后 ActionBar 复读「n/N · 下次必成」（禁新 PAPI hope twin） |
| 装备 tip / applyLore | tip 字墙或空 | **HD3** tip 只挂手持希望一行（≤1），细节进工坊预览 |

**禁：** 改 pity 数学、抬 MAX_TRIES、新 celebration 特效墙、再发明第二套 HopeMeter。

### 3.2 Workshop choice feel（K1/K2 诚实 · commit 环）

**目标：** 默认路径 = **材料 → 确定 +1 档**；随机锻是旁路惊喜。选择感来自「稳 / 赌 / 锁洗 / 转化」分流，**不抬表**。

| 已有 | 缺口 | 薄切片 |
|------|------|--------|
| K1 诚实入口、K2 floor、K2b 菜单 | 锻后「下一确认槽」偶发丢；多口仍像同级赌 | **WS1** forge 成功后 **commit-one-slot** 强制指到下一稳线按钮（已有 `EmberForgeCommitNext` 族 → 补齐漏口，禁 K3 自动跳页） |
| EnhancePityChase / Quality / Refine 预览 | 预览与结果页文案仍偏长 | **WS2** 预览三态压成：花费 · 成功率或 n/N · 「点确认」；零改 cost |
| forgeroll floor | 「加料买下限」可发现性依赖菜单 | **WS3** 工坊主页一格「稳锻 / 赌锻」并排半行；禁抬极品权、禁 K3 |

**禁：** K3 · 抬 forgeroll 极品 · 抬 enhance/quality/refine 表 · ConfirmTokens densify。

### 3.3 Map gear identity — 每 Q 可刷签名（无 clear-gate）

**目标：** Q01–Q07 各有 **可农场的变强身份**（套件/烙印/形状绑定已有解锁口），进图就知道「这图养什么」；**首进或穿戴即可见**，通关不是唯一门闩。

| 已有 | 缺口 | 薄切片 |
|------|------|--------|
| lootFamily / `methodLine` / mapid / MapResonance（几何加宽零伤乘） | 冒险菜单仍弱「图→掉什么」；EM1-B 方法表未业主签字 | **MG1** 冒险页每 Q **一行身份**：`族·缺件·下一稳线`（复用 methodLine；≤1 行） |
| mapAccessUnlocked / endgameUnlocked（进图解锁） | 玩家仍以为要「清关才开」 | **MG2** 入口文案统一「进过即开 / 清过也开」；**零改**解锁条件 |
| 图装命中给骨尘等 soft | 易滑向 densify | **MG3** **冻结**图装 soft gift densify；只做身份可见 + 套装追焦点对齐 |

**禁：** clear-gate · drop-rate · 新永久伤乘 · 新房间事件难度包 · 词缀池扩。

### 3.4 Visible pity / sure-do — 表面化，不叠 twin

**目标：** 已 SHIP 的保底与「必做」动作让玩家 **摸得到**；停止同构 sure-do / pity 再开一族。

| 已 SHIP（勿 densify） | 表面化做法 |
|----------------------|------------|
| EnhancePityChase · ConvertSure · RerollKeepSure · UpgradeSure · BrandSure · RerollPitySure · forge quality/craft/enhance preview-do | **SUR1** 工坊/洗练结果页一枚「已自动保留/下次必成」徽章半行 |
| AttuneBreak · ImprintBreak · MarksBreak · AwakenBreak · GemHoleSeat | **SUR2** 首次触发时一条短 AB，之后静默（禁每日 celebratetwin） |
| SigStampPity · DropQualityPity · DropCraftPity · ChargeMissRest 等 | **SUR3** 并入 Hope dial（§3.1），**STOP** 再开平行 remainLine 族 |

**STOP densify 族（沿用既有名单，本窗加粗）：**  
ChargeMiss / DropCraft / DashStep / LockNear / ParryMiss / ChargeRest / MarkNear / SigStamp / Codex / MapResonance / Bag / sure-do 全家 / TalentPlaystyle / access / Path / PostureGeom / **Affix Pack** / 今日首次材料礼 / 环境伤减半海。

---

## 4. 方案对照

| 方案 | 内容 | 风险 | 推荐 |
|------|------|------|------|
| **M（推荐）** | HD1 + MG1 + SUR1 三薄批（见 §5）；零 jar 数值；展示/入口为主 | 低 | **是** |
| A | 先写 EM1-B 七图方法表再码 | 需业主签字；本窗可并行文档 | 可作 Batch2 输入 |
| B | 继续 Affix Pack / soft densify | **违背 Karen** | **否决** |
| C | 抬强化率/宝石 flat/skill_mult「假变强」 | 硬禁 | **否决** |

---

## 5. 三薄施工批（tip → 批 A·M → ship）

> 每批：**零改** pity/RATE/cost/掉率/伤乘；可热更 TrMenu + 纯展示 Java；p1sim **不要求**（无战斗/经济表变更）；试玩窗口禁止热更炸服。

### Batch 1 · `power-hope-glance`（HD1 + SUR1）

- **tip 名建议：** `TIP-ember-power-hope-glance-…`  
- **做：** 枢纽/装备/工坊挂 **同一** `EmberHopeMeter.glanceHeld(player)` 三行上限：手持强化或洗练 n/N · 成色或精工旁路 · 宝石或签名其一（按手持上下文选）。结果页 sure-do 半行「已自动保留」。  
- **不做：** 新 PAPI、新 pity 计数器、celebration。  
- **验收：** 三页文案一致；overs lore≤3；数值单测不动。

### Batch 2 · `power-map-identity-line`（MG1 + MG2）

- **tip 名建议：** `TIP-ember-power-map-id-line-…`  
- **做：** `ember_p1_adventure` 每 Q 槽 lore 第 1 行 = `methodLine` 压缩；Unlock 文案「进过即开」。必要时补 Q01–Q07 业主方法表一页（文档）但本批码只接线已有 methodLine。  
- **不做：** 新掉落池、新怪、clear-gate、词缀。  
- **验收：** 进 Q01–Q03 各见不同身份半行；解锁语义与码一致。

### Batch 3 · `power-forge-commit-choice`（WS1 + WS2）

- **tip 名建议：** `TIP-ember-power-forge-commit-…`  
- **做：** 锻造成功后 commit 下一稳线槽；预览三态短句；主页稳/赌并排（WS3 可并入若仍薄）。  
- **不做：** K3、抬表、forgeroll 极品权。  
- **验收：** 强化失败→仍见保底剩余；成功→下一确认可点；无新经济叶子。

**顺序理由：** 先让「已有变强」被看见（Batch1）→ 再让「去哪刷」清楚（Batch2）→ 再让「怎么稳着变强」有选择感（Batch3）。全部服务 Karen「途径花样」，零难度扩容。

---

## 6. 非目标 / 冻结清单

- Affix Pack 37+、房间事件难度权、Boss 半血破招扩包（手感包另议且须对照，本窗不排）  
- soft densify：今日首次胚碎骨、环境伤减半、进图短抗海  
- 希望 celebration / 新 hope PAPI / sig-dry twin  
- Talent 系数、六槽 HOLD、Stage2 unsafe 经济扫  
- 化妆品 C15、旧日常假开、legacy socket  

---

## 7. 与既有文档关系

| 文档 | 关系 |
|------|------|
| `DESIGN-ember-empowerment-scan-next-stage0` | 本窗接棒；饱和族仍勿 twin |
| `DESIGN-ember-em1-map-empowerment` | EM1-A 复用；EM1-B 方法表可作 Batch2 文档输入 |
| `DESIGN-ember-forge-choice-depth` | K1/K2 已 SHIP；本窗只补 commit/预览表面，不重开 K3 |
| `DESIGN-ember-*-sure-*` / pity Stage0 族 | **表面化 only**；STOP densify |
| Affix Pack 4–36 DESIGN/STATUS | **归档冻结**；不再 tip→ship 扩池 |

---

## 8. 验收（Stage0 本文）

- [x] Karen 口径写入硬停（变强途径 ≠ 难度 densify）  
- [x] 四轴可施工、禁区明确  
- [x] 参考 Diablo / PoE / 1.12 RPG 短对位  
- [x] 三薄批 tip→批 可直接派工  
- [x] 总控批 A · 方案 M  
- [x] Batch1 tip 开工：`TIP-ember-power-hope-glance-d12240-d12247-2026-10-12.md`

---

## 9. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-12  
- Batch1：TIP-A 希望一眼（优先单轨，非三轨并行墙）
- Batch2：TIP-C CLOSED（d12255）
- Batch3：TIP-B 已开 `TIP-ember-power-forge-commit-d12256-d12263-2026-10-12.md`  
- 本文件 **docs only**；本回合 **不改 jar**、不 push（除非 tip 管线另需）。

## 进度
- **TIP-O CLOSED PASS** · sx01–04 叠饰
戳 · 2026-10-12 15:44 CST

- Batch1 TIP-A / Batch2 TIP-C / Batch3 TIP-B：**CLOSED live**（至 1.65.760-d12263）
- Affix Pack：**FROZEN**
- TIP-D：**CLOSED live**（1.65.761-d12271）
- TIP-E：**CLOSED live**（1.65.762-d12279）
- TIP-F：**CLOSED live**（1.65.763-d12287）
- TIP-G：**CLOSED live**（1.65.764-d12295）
- TIP-H：**CLOSED live**（trmenu `82512b4d`）
- TIP-I：**CLOSED live**（1.65.765-d12311）
- **TIP-J CLOSED 停窗** · `TIP-ember-power-paths-stop-window-d12312-d12319` · Batch1–3+A～I 收束；默认不再开 power-glance
- **TIP-K CLOSED live** 1.65.766-d12335（技能手感包 cue）
- **TIP-L CLOSED live** 1.65.767-d12351（Q07 charm + 下一环追烙）
- **TIP-M CLOSED 停窗观察** · hub 挤度纪要；确认挤后再开 F1；无码
- **TIP-N CLOSED live** 1.65.768-d12375（warn_bar_hold + pre_half_cue）
- **TIP-O CLOSED PASS** live `b566bfe4`（sx01–04 叠饰 @d12376）
- **TIP-P CLOSED live** 1.65.769-d12407 · `TIP-ember-set-awaken-feel-thin-d12392-d12407-2026-10-12.md` · 套装觉醒升档白话

## 进度戳
- **TIP-AI CLOSED** · L13 stage1 离线门禁 · `STAGE1_L13_OFFLINE=FAIL` · HOLD 施工

- **TIP-AI OPEN** · `TIP-ember-blade-sig-l13-stage1-offline-thin-d12632-d12647-2026-10-12.md` · DESIGN `DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md` · RESULT PENDING · 刃 L13 stage1 离线门禁（钉 **R1+R2** · docs+tools · 零改 L13 mods · 零业务 jar · PASS≠开工）· R5 升级轨 · TIP-AD exclude 保留 · 深化 densify / STAMP / skill_mult 仍冻

- **TIP-AH CLOSED live** `1.65.774-d12631.local` · feat `5e1b43c96` · chore `57efb60b8` · attune 预览六槽对齐 · **L13 stage1 → TIP-AI OPEN**

- **TIP-AH OPEN** · `TIP-ember-attune-preview-sixslot-align-thin-d12616-d12631-2026-10-12.md` · RESEARCH `RESEARCH-ember-attune-preview-sixslot-align-2026-10-12.md` · DESIGN `DESIGN-ember-attune-preview-sixslot-align-2026-10-12.md` · attune 预览六槽对齐（可施工薄 · 正交刃轨 · 零功率表）· 批后派插件 · **L13 stage1 HOLD 下一 tip** · 深化 densify 仍冻 · 策划 docs-only · **（历史 OPEN 行；已 CLOSED live）**

- **TIP-AG CLOSED** `7d61761e4` · L13 蓄力身份 DESIGN stage0 APPROVED · HOLD 施工 · R1–R5 · **L13 stage1 → TIP-AI OPEN**

- **TIP-AG OPEN** · `TIP-ember-blade-sig-l13-design-stage0-thin-d12608-d12615-2026-10-12.md` · DESIGN `DESIGN-ember-blade-sig-l13-charge-identity-stage0-2026-10-12.md` · 刃 L13 蓄力身份 DESIGN stage0（docs-only · HOLD 施工 · 零改 mods · 零 jar 直至门禁+显式开工）· L11 HOLD 另轨 · TIP-AD exclude 保留 · 深化 densify 仍冻 · 无码 · **（历史 OPEN 行；已 CLOSED）**

- **TIP-AF CLOSED** docs-only `f6e16ea46` · 刃 L11/L13 另轨研究 · exclude+观察 HOLD · 荐下一 L13 DESIGN stage0 → TIP-AG

- **TIP-AF OPEN** · `TIP-ember-blade-sig-l11-l13-research-thin-d12600-d12607-2026-10-12.md` · RESEARCH `RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md` · 刃 L11/L13 另轨研究 docs-only（清 TIP-AD HOLD · 禁改 mods / 抬表 / skill_mult）· 深化 densify 仍冻 · 无码 · **（历史 OPEN 行；已 CLOSED）**

- **TIP-AE CLOSED** live `1.65.773-d12599`（`f3a1073cb`/`abe84cb22`）· 烙印扩甲/defense fits · 甲 stampable 仍空 · L11/L13 未动 · 刃债交 TIP-AF

- **TIP-AE OPEN** · `TIP-ember-armor-signature-min-slice-build-thin-d12584-d12599-2026-10-12.md` · build DESIGN `DESIGN-ember-armor-signature-min-slice-build-2026-10-12.md` · **开工最小切片**（烙印扩甲 / defense-side fits · 不扩掉落 · 不改 L11/L13 · 不抬 STAMP）· 对接 stage0+TIP-AD · 批后派插件 · 深化 densify 仍冻 · 刃 L11/L13 HOLD 另轨 · **（历史 OPEN 行；已 CLOSED live）**
- **TIP-AD CLOSED PASS patched** `abc3875d0` / RESULT head `b4c43551e` · S3 评测集砍刃 L11/L13 · judge-s3 PASS · STAGE1_OFFLINE=PASS(patched) · **显式开工令已发** → TIP-AE

- **TIP-AD OPEN** · `TIP-ember-armor-signature-gate-s3-eval-patch-thin-d12576-d12583-2026-10-12.md` · 补丁 DESIGN `DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md` · S3 评测集砍刃 L11/L13（docs-only · `judge-s3` PASS 候选 · 零业务 jar · PASS≠开工）· 深化 densify 仍冻 · **（历史 OPEN 行；已 CLOSED）**
- **TIP-AC CLOSED FAIL** `469cc1a46` · `TIP-ember-armor-signature-gate-stage1-offline-thin-d12560-d12575-2026-10-12.md` · RESULT `STAGE1_OFFLINE=FAIL`（S3-T/P 刃 L11/L13；S1/S2/S3-R/E/X/S4 PASS）· HOLD 施工 · 零业务 jar
- **TIP-AB CLOSED** `a5b264fce` · 防具签名+叠合门禁 DESIGN stage0 APPROVED · HOLD 施工至 S1–S4+显式开工 · TIP-AD 补丁勘误 S3 评测集

- **TIP-AA CLOSED** docs-only `79247dab2` · 主线解锁 2c 研究 · 真债=防具签名+叠合 · HOLD 至独立 DESIGN stage0
- **TIP-AB CLOSED** `a5b264fce` · `TIP-ember-armor-signature-gate-design-stage0-thin-d12552-d12559-2026-10-12.md` · DESIGN stage0 APPROVED HOLD


- **TIP-Z CLOSED** docs-only `923e031f1` · 主线内容轴观察停窗 · densify 仍冻 · P0 试玩 / P1 解锁研究 / P2 烬斩门闩

- **TIP-Y CLOSED** docs-only `a838781ed` · 烬斩手感研究 · 门闩已立 · 无码
- **TIP-Z CLOSED** · `TIP-ember-mainline-content-observe-thin-d12536-d12543-2026-10-12.md` · RESEARCH `RESEARCH-ember-mainline-content-observe-2026-10-12.md` · 主线内容轴观察停窗（TIP-L 闭环后无新薄断环 · 解锁 2c 须 DESIGN）· 无码 · 深化 densify 仍冻 · hash `923e031f1`
- **TIP-X CLOSED** `77a768df1` · sx01–19 视觉线闭合
- **TIP-Y CLOSED** · 烬斩手感研究 docs-only · 门闩已立 · 无码

- **TIP-Y 待批 A·M** · tip D12528–D12535 · 烬斩手感研究 docs-only（禁 skill_mult · 不重开 TIP-K cue）· 无码
- **TIP-Y OPEN** · `TIP-ember-skill-feel-research-thin-d12528-d12535-2026-10-12.md` · RESEARCH `RESEARCH-ember-skill-feel-post-k-next-2026-10-12.md` · TIP-X CLOSED PASS `77a768df1`；短征视觉线闭合；换轴手感研究
- **TIP-X CLOSED PASS** live `77a768df1` · tip D12512–D12527 · sx17–sx19 收尾 · O→X 闭合

- **TIP-X 批 A·M** · tip D12512–D12527 · 交施工 sx17–sx19 收尾

- **TIP-X 待批 A·M** · tip D12512–D12527 · 荐 sx17–sx19 叠饰收尾（换轴续 · 非变强 densify · 闭合 O→U→V→W→X）

- **TIP-W CLOSED PASS** live `fb4debc2` · tip D12496–D12511 · sx13–sx16 `@d12496`

- **TIP-V CLOSED PASS** live `bae53046` · tip D12480–D12495 · sx09–sx12 `@d12480`

- **TIP-U CLOSED PASS** live `a3590b41` · tip D12464–D12479 · sx05–sx08 `@d12464`

- **TIP-T CLOSED 观察停窗** · D12456–D12463 · 无码 · 深化 densify 冻

- **TIP-S 批 A·M** · tip D12440–D12455 · 交插件

- **TIP-R 批 A·M** · tip D12424–D12439 · 交插件

- **TIP-Q 批 A·M** · tip D12408–D12423 · 交插件

- **TIP-P CLOSED live**（2026-10-12 16:51）· 1.65.769-d12407
- **TIP-Q CLOSED PASS live `1.65.770-d12423`** · `TIP-ember-drop-craft-pity-settle-thin-d12408-d12423-2026-10-12.md` · 精工软保底结算进度（对齐成色 · 非 glance）
- **TIP-R CLOSED PASS live `1.65.771-d12439`** · `TIP-ember-map-res-wear-cue-thin-d12424-d12439-2026-10-12.md` · 局内换装共鸣升沿（图装身份 · 非 glance）
- **TIP-S CLOSED PASS live `1.65.772-d12455`** · `TIP-ember-forge-choice-stable-gamble-thin-d12440-d12455-2026-10-12.md` · 稳/旁路「选的是」补齐（工坊选择感 · 非 glance）
- **TIP-T CLOSED（观察停窗 · 无码 · `510740e0`）** · `TIP-ember-power-paths-deepen-observe-d12456-d12463-2026-10-12.md` · 深化轴观察停窗（P～S 收束 · 无码）
- **TIP-U CLOSED PASS** live `a3590b41` · `TIP-ember-short-visual-sx05-sx08-thin-d12464-d12479-2026-10-12.md` · 短征视觉续 sx05–sx08 `@d12464`
- **TIP-V CLOSED PASS** live `bae53046` · `TIP-ember-short-visual-sx09-sx12-thin-d12480-d12495-2026-10-12.md` · 短征视觉续 sx09–sx12 `@d12480`
- **TIP-W CLOSED PASS** live `fb4debc2` · `TIP-ember-short-visual-sx13-sx16-thin-d12496-d12511-2026-10-12.md` · 短征视觉续 sx13–sx16 `@d12496`
- **TIP-X CLOSED PASS** live `77a768df1` · `TIP-ember-short-visual-sx17-sx19-thin-d12512-d12527-2026-10-12.md` · 短征视觉收尾 sx17–sx19 · O→U→V→W→X **闭合**
