# 余烬 · FlexPath.offerPick / applyAndReply「轻技路径选打」菜单化（选路径聊天钮去指令化 · ≠步态路径选打同题 · ≠破绽/誓约/试炼/深渊/短征/团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠身法/闪冲同窗 · ≠复开 D494 玩法本体）

| 日 | 事 |
|----|-----|
| 2026-10-14 13:22 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DM CLOSED 后连开 · 拟 D13345 · TIP-DN |

STATUS=**待批 A** · 荐 M · D13345 · TIP-DN · 2026-10-14 13:22 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-flex-path-offer-pick-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-flex-path-offer-pick-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-flex-path-offer-pick-visible-2026-10-14.md`](RESEARCH-ember-flex-path-offer-pick-visible-2026-10-14.md) · backlog `B-flex-path-offer-pick-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬伤害/CD表/掉落/体力价 ≠步态路径选打同题 ≠破绽路径选打同题 ≠誓约路径选打同题 ≠试炼路径选打同题 ≠深渊路径选打同题 ≠短征路径选打同题 ≠团本路径选打同题 ≠硬本路径选打同题 ≠精选路径选打同题 ≠已齐 settle 下一环同题 ≠短征settle回看同题 ≠D673同题复读 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫 ≠全族 *Path.offerPick 大扫 ≠身法/闪冲同窗 ≠复开 D494 玩法本体**

> **一句话玩家价值：** 周选/改轻技路径一点开「轻技路径」——装配踏步/卸下轻技/清空/技能组/冒险都能点；**不**抬伤害/CD表/掉落/体力价、**不**教手打指令、**不**复读已齐 settle/步态·破绽·誓约·试炼·深渊·短征·团本·硬本·精选选打同题、**不**扩成全族路径大扫。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DM CLOSED live `584e17b53`（PASS `1e7d58049`）· 派 TIP-DN | 内容优先；禁已齐 settle/选打同题；轻技/身法/闪冲逐支；只钉一支 |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** `recipe_gap_gem`/`forge_gap_gem` → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH 已齐；sx20 禁 → **无薄窗新硬债** |
| E4 | settle CW–DD + 选打 TIP-DE–DM | **已齐勿复读** |
| E5 | DESIGN-DM §6⑦ | 明文后置轻技 FlexPath / 身法 StepPath / 闪冲 SidestepPath 等 *Path.offerPick 逐支另窗 |
| E6 | `EmberFlexPath.offerPick` L78–85 | ConfirmTokens → `/corerpg p1 flexpath on\|off\|clear` |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ01` | 周要约 / Q01 后 → `offerPick` |
| E8 | `applyAndReply` L151–160 | 下一步 ConfirmTokens：`steppath` / `flexpath on` / `flexpath` / `trmenu open ember_skill_kit` |
| E9 | `EmberCommand` flexpath 无参 L3615–3618 | 无参 → glance + `offerPick`；用法仍教 `on\|off\|clear`；glance 未选仍教手打 `/corerpg p1 flexpath` |
| E10 | TrMenu **无** `ember_p1_flex_path`（亦无 `ember_p1_step_path` / `ember_p1_sidestep_path`） | menus/ |
| E11 | StepPath / SidestepPath 仍 ConfirmTokens；本仓 **无** EmberPacePath | **后置本窗**（§6；禁全族大扫） |
| E12 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E13 | 半行 REJECT `d00ed84bc` 保持 | backlog |
| E14 | 为何钉 Flex（最硬） | Q01 最早解锁；ConfirmTokens 全链；`applyAndReply` 交叉教 `steppath`；缺专用选打薄页；DESIGN-DM §6⑦ 首位；Step/Sidestep 后置 |

**一句话问题：** 挂机/短征/各 settle/选打 DE–DM 已齐后，**轻技路径选打**仍钉在 ConfirmTokens 教裸 `flexpath`，周要约/无参/Q01 同入口，`applyAndReply` 下一步仍聊天钮开身法/换一条——**没有**选打专用薄页。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 已齐 settle 同题 / 步态选打 TIP-DM / 破绽·誓约·试炼·深渊·短征·团本·硬本·精选选打 | **否** | 派单禁；刚 CLOSED |
| 身法/闪冲同窗 | **否** | 后置；禁全族大扫 |
| PacePath | **后置** | 本仓无 EmberPacePath |
| legacy evacuate 大扫 | **后置** | 须另窗证据 |
| 全族 *Path.offerPick 大扫 | **否** | 扩窗 |
| 只半行 | **否** | REJECT |
| 复开 D494 / 抬伤害/CD | **否** | 玩法本体零改；本窗只菜单化选打 |
| **FlexPath.offerPick / applyAndReply 选打菜单化（荐）** | **采纳** | E2–E10·E14；真可点；镜像 TIP-DE–DM；≠ 步态选打同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼选打：** 周要约/改路径/无参 flexpath → 开「轻技路径」薄页，见当前装配踏步/卸下轻技。
2. **一点去向：** 装配踏步 / 卸下轻技 / 清空 / 技能组 / 冒险 **全是点击**；**零**以 ConfirmTokens 教裸 `flexpath` 为主 UI。
3. **诚实可读：** lore 解锁/Q01 灰态诚实；不抬伤害/CD表/掉落/体力价。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄选打页（荐）** | 真开菜单；清教指令主路径；**零改**伤害/CD表/掉落/体力价/轻技规则语义 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬伤害/CD表/掉落/体力价 / sx20 / 已齐同题 / 庭内 / 全族 Path / 身法·闪冲同窗** | — | **否决** |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a 插件改 offerPick + applyAndReply + 无参 flexpath + 周要约必做；W1b 薄选打页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [ ] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/全族大扫/身法·闪冲同窗）
- [ ] 批注：

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_flex_path`（OpenCommand `/ember_p1_flex_path`） | 新页；**不**复开已齐选打/settle 同题 |
| 触发 A | `EmberFlexPath.offerPick`（周要约/无参/重选） | 禁改轻技规则语义/伤害/CD |
| 触发 B | `applyAndReply` 成功/失败下一步 | 禁 ConfirmTokens 教裸 flexpath/steppath 为主 UI |
| 触发 C | 命令无参 / 周要约 / Q01 schedule | 改开薄页或菜单优先 |
| 选装配踏步/卸下轻技/清空 | 菜单 actions `flexpath on\|off\|clear` | **禁**主 UI 仍以 ConfirmTokens 唯一交付 |
| 技能组 | 开 `ember_skill_kit` | 禁改技能数值 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 工坊（可选） | 开 `ember_p1_forge` | 禁改 forge 价 |
| 身法方向（可选辅） | 若已有 `ember_p1_step_path` 可开；否则后置 Step 窗 | **禁**本窗新建 Step 薄页主交 |
| glance/用法 | 去「手打 /corerpg p1 flexpath …」主教 | 可改「点轻技路径页」 |
| 已齐 settle / Short·Raid·Featured·Challenge·Abyss·Trial·Pledge·Counter·Stride Path / Cast / Aid / variety / 庭内 | **零改** | 本债≠同题复读 |
| 身法 StepPath / 闪冲 SidestepPath 等 *Path.offerPick | **零改本窗** | 另窗 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | offerPick / 无参 flexpath / 周要约 / Q01 / applyAndReply 下一步→开薄页；清 ConfirmTokens 教指令主路径；可选 PAPI；单测 | 不改伤害/CD表/掉落/体力价/轻技规则语义 |
| **W1b · 薄选打页（必做 · TrMenu）** | `ember_p1_flex_path`：装配踏步/卸下轻技/清空/技能组/冒险/可选工坊；lore 挂路径；`from`/`back` | 不改 stride_path / 庭内 / 身法·闪冲 / 其他 Path |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

- 页标题：`余烬 · 轻技路径`
- lore：`当前：装配踏步/卸下轻技/未选 · 真改潜行+Q 是否放身法`
- 灰态：`需本人首进/首通 Q01`

### 3.4 经济

**零** 改伤害 / CD 表 / 掉落 / 体力价 / 轻技规则语义（D494 on/off 装配潜行+Q）。

### 3.5 为何不是半行

主交付 = **薄 flex_path 页真开菜单选打** + **插件改 offerPick / applyAndReply / 无参 / 周要约入口** + 清 ConfirmTokens 教指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（抽测）

| # | 项 |
|---|-----|
| V1 | offerPick / 无参 flexpath / 周要约 → 开 `ember_p1_flex_path`（非 ConfirmTokens 教 flexpath 为主 UI） |
| V2 | 薄页真开：装配踏步/卸下轻技/清空/技能组/冒险 |
| V3 | applyAndReply 后下一步菜单优先；PAPI lore 诚实；Q01 灰态 |
| V4 | 零抬伤害/CD表/掉落/体力价；零改已齐选打/settle 薄页（含 `ember_p1_stride_path`） |

---

## 5. 硬禁（本窗）

- 半行独批 · 已齐 settle 同题复读 · 步态路径选打同题复读（TIP-DM） · 破绽/誓约/试炼/深渊/短征/团本/硬本/精选路径选打同题复读（TIP-DL–DE）
- 抬伤害/CD表/掉落/体力价 · sx20 · densify · Pack6 · 开 K3 · 关观察
- variety 完整经济 · Cast·Aid 再拧 · 庭内旁路 · 教裸指令主 UI
- legacy evacuate 大扫 · 全族 *Path.offerPick 大扫 · 身法/闪冲同窗 · 复开 D494 玩法本体

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 工坊升阶下一环 / 图装下一环菜单化（另窗证据）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）
7. **身法 StepPath.offerPick / 闪冲 SidestepPath.offerPick** 等其他 *Path.offerPick 逐支另窗（禁全族大扫）
8. PacePath 等剩支（本仓无 EmberPacePath）后置

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 13:22 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DM CLOSED 后连开 · 选题 FlexPath.offerPick / applyAndReply 轻技路径选打菜单化 · 拟 D13345 · TIP-DN · ≠半行 ≠步态选打同题 ≠破绽/誓约/试炼/深渊/短征/团本/硬本/精选选打同题 ≠已齐 settle 同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征薄债+选打已齐；settle CW–DD+DE–DM选打已齐；DESIGN-DM§6⑦；offerPick ConfirmTokens→flexpath；applyAndReply交叉教steppath；无参/周要约/Q01；无 flex_path；身法/闪冲后置；见 RESEARCH） |

*D13345 **待批 A** · 荐 M · FlexPath.offerPick / applyAndReply 轻技路径选打菜单化 · TIP-DN。offerPick/applyAndReply/无参 flexpath/周要约→开 ember_p1_flex_path · 清 ConfirmTokens 教 flexpath 主 UI · 零改伤害/CD表/掉落/体力价/轻技规则语义 · ≠ stride_path / 已齐选打同题 · 身法/闪冲后置 · ≠半行 ≠已齐 settle/选打同题复读 ≠sx20。*
