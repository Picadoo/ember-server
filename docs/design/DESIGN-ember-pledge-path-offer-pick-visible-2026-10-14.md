# 余烬 · PledgePath.offerPick / applyAndReply「誓约路径选打」菜单化（选路径聊天钮去指令化 · ≠试炼路径选打同题 · ≠深渊/短征/团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬伤害/CD表/掉落/体力价/誓约徽记表 · ≠全族 *Path.offerPick 大扫 · ≠破绽/步态同窗 · ≠复开 D481 玩法本体）

| 日 | 事 |
|----|-----|
| 2026-10-14 12:22 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DJ CLOSED 后连开 · 拟 D13342 · TIP-DK |

STATUS=**待批 A** · 荐 M · L 不荐 · 拟 D13342 · TIP-DK · 2026-10-14 12:22 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-pledge-path-offer-pick-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-pledge-path-offer-pick-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-pledge-path-offer-pick-visible-2026-10-14.md`](RESEARCH-ember-pledge-path-offer-pick-visible-2026-10-14.md) · backlog `B-pledge-path-offer-pick-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬伤害/CD表/掉落/体力价/誓约徽记表 ≠试炼路径选打同题 ≠深渊路径选打同题 ≠短征路径选打同题 ≠团本路径选打同题 ≠硬本路径选打同题 ≠精选路径选打同题 ≠已齐 settle 下一环同题 ≠短征settle回看同题 ≠D673同题复读 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫 ≠全族 *Path.offerPick 大扫 ≠破绽/步态同窗 ≠复开 D481 玩法本体**

> **一句话玩家价值：** 周选/改誓约路径一点开「誓约路径」——限药/逆行/双挂/清空/回誓约页/进阶/冒险都能点；**不**抬伤害/CD表/掉落/体力价/誓约徽记表、**不**教手打指令、**不**复读已齐 settle/试炼·深渊·短征·团本·硬本·精选选打同题、**不**扩成全族路径大扫。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DJ CLOSED live `f63d095b2`（PASS `61e500086`）· 派 TIP-DK | 内容优先；禁已齐 settle/选打同题；签律/破绽/步态逐支；只钉一支 |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** `recipe_gap_gem`/`forge_gap_gem` → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH 已齐；sx20 禁 → **无薄窗新硬债** |
| E4 | settle CW–DD + 选打 TIP-DE–DJ | **已齐勿复读** |
| E5 | DESIGN-DJ §6⑦ | 明文后置签律/破绽/步态等 *Path.offerPick 逐支另窗 |
| E6 | `EmberPledgePath.offerPick` | ConfirmTokens → `/corerpg p1 pledgepath lean\|reverse\|both\|clear` |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ06` | 周要约 / Q06 后 → `offerPick` |
| E8 | `applyAndReply` | 下一步 ConfirmTokens：`/corerpg p1 pledge` / `modes` / `pledgepath` |
| E9 | `EmberCommand` pledgepath 无参 | 无参 → glance + `offerPick`；用法仍教 `lean\|reverse\|both\|clear`；nudge 改路径仍教手打 |
| E10 | TrMenu **有** `ember_p1_pledge`（规则开关）· **无** `ember_p1_pledge_path` | menus/ |
| E11 | `EmberCounterPath.offerPick` / `EmberStridePath.offerPick` 仍教裸指令 | **后置本窗**（破绽/步态；禁全族大扫） |
| E12 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E13 | 半行 REJECT `d00ed84bc` 保持 | backlog |
| E14 | 为何钉 Pledge 非 Counter/Stride | 列表首位；与 Trial 内容相邻；周要约+Q06 全链；缺专用选打薄页；证据最硬 |

**一句话问题：** 挂机/短征/各 settle/选打 DE–DJ 已齐后，**誓约路径选打**仍钉在 ConfirmTokens 教裸 `pledgepath`，周要约/无参/Q06 同入口，`applyAndReply` 下一步仍聊天钮——**没有**选打专用薄页（既有 `ember_p1_pledge` 是规则开关，不是 lean/reverse/both 选打）。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 已齐 settle 同题 / 试炼选打 TIP-DJ / 深渊·短征·团本·硬本·精选选打 | **否** | 派单禁；刚 CLOSED |
| 破绽/步态同窗 | **否** | 后置；禁全族大扫 |
| legacy evacuate 大扫 | **后置** | 须另窗证据 |
| 全族 *Path.offerPick 大扫 | **否** | 扩窗 |
| 只半行 | **否** | REJECT |
| 复开 D481 / 改 `ember_p1_pledge` 当选打主交付 | **否** | 规则开关≠路径选打；本窗新建选打页 |
| **PledgePath.offerPick / applyAndReply 选打菜单化（荐）** | **采纳** | E2–E10·E14；真可点；镜像 TIP-DE–DJ；≠ 试炼选打同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼选打：** 周要约/改路径/无参 pledgepath → 开「誓约路径」薄页，见当前限药/逆行/双挂。
2. **一点去向：** 限药 / 逆行 / 双挂 / 清空 / 誓约页 / 进阶 / 冒险 **全是点击**；**零**以 ConfirmTokens 教裸 `pledgepath` 为主 UI。
3. **诚实可读：** lore 解锁/Q06 灰态诚实；不抬伤害/CD表/掉落/体力价/誓约徽记表。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄选打页（荐）** | 真开菜单；清教指令主路径；**零改**伤害/CD表/掉落/体力价/誓约规则语义 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬伤害/CD表/掉落/体力价/誓约徽记表 / sx20 / 已齐同题 / 庭内 / 全族 Path / 破绽·步态同窗** | — | **否决** |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a 插件改 offerPick + applyAndReply + 无参 pledgepath + 周要约必做；W1b 薄选打页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [ ] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/全族大扫/破绽·步态同窗）
- [ ] 批注：

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_pledge_path`（OpenCommand `/ember_p1_pledge_path`） | 新页；**不**把 `ember_p1_pledge` 当选打主页；**不**复开已齐选打/settle 同题 |
| 触发 A | `EmberPledgePath.offerPick`（周要约/无参/重选） | 禁改誓约规则语义/伤害/CD |
| 触发 B | `applyAndReply` 成功/失败下一步 | 禁 ConfirmTokens 教裸 pledgepath/pledge/modes 为主 UI |
| 触发 C | 命令无参 / 周要约 / Q06 schedule | 改开薄页或菜单优先 |
| 选限药/逆行/双挂/清空 | 菜单 actions `pledgepath lean\|reverse\|both\|clear` | **禁**主 UI 仍以 ConfirmTokens 唯一交付 |
| 誓约页 | 开既有 `ember_p1_pledge` | 禁改 pledge Layout 主交 |
| 进阶模式 | 开既有 `ember_p1_modes` | 禁改进阶 Layout 主交 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 工坊（可选） | 开 `ember_p1_forge` | 禁改 forge 价 |
| glance/nudge/用法 | 去「手打 /corerpg p1 pledgepath …」主教 | 可改「点誓约路径页」 |
| 已齐 settle / Short·Raid·Featured·Challenge·Abyss·Trial Path / Cast / Aid / variety / 庭内 | **零改** | 本债≠同题复读 |
| 破绽/步态等 *Path.offerPick | **零改本窗** | 另窗 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | offerPick / 无参 pledgepath / 周要约 / Q06 / applyAndReply 下一步→开薄页；清 ConfirmTokens 教指令主路径；可选 PAPI；单测 | 不改伤害/CD表/掉落/体力价/誓约规则语义 |
| **W1b · 薄选打页（必做 · TrMenu）** | `ember_p1_pledge_path`：限药/逆行/双挂/清空/誓约页/进阶/冒险/可选工坊；lore 挂路径 | 不改 trial_path / 庭内 / 破绽·步态 / 其他 Path / pledge Layout 主交 |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

- 页标题：`余烬 · 誓约路径`
- lore：`当前：限药/逆行/双挂/未选 · 每人最多 3 瓶回复药 / 第一间↔第三间互换 / 两条都挂`
- 灰态：`需首通 Q06（自选誓约解锁）`

### 3.4 经济

**零** 改伤害 / CD 表 / 掉落 / 体力价 / 誓约规则语义（D481 lean/reverse pool 与徽记逻辑仍按既有）。

### 3.5 为何不是半行

主交付 = **薄 pledge_path 页真开菜单选打** + **插件改 offerPick / applyAndReply / 无参 / 周要约入口** + 清 ConfirmTokens 教指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（抽测）

| # | 项 |
|---|-----|
| V1 | offerPick / 无参 pledgepath / 周要约 → 开 `ember_p1_pledge_path`（非 ConfirmTokens 教 pledgepath 为主 UI） |
| V2 | 薄页真开：限药/逆行/双挂/清空/誓约页/进阶/冒险 |
| V3 | applyAndReply 后下一步菜单优先；PAPI lore 诚实；Q06 灰态 |
| V4 | 零抬伤害/CD表/掉落/体力价/誓约徽记表；零改已齐选打/settle 薄页（含 `ember_p1_trial_path`） |

---

## 5. 硬禁（本窗）

- 半行独批 · 已齐 settle 同题复读 · 试炼路径选打同题复读（TIP-DJ） · 深渊/短征/团本/硬本/精选路径选打同题复读（TIP-DI–DE）
- 抬伤害/CD表/掉落/体力价/誓约徽记表 · sx20 · densify · Pack6 · 开 K3 · 关观察
- variety 完整经济 · Cast·Aid 再拧 · 庭内旁路 · 教裸指令主 UI
- legacy evacuate 大扫 · 全族 *Path.offerPick 大扫 · 破绽/步态同窗 · 复开 D481 玩法本体

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 工坊升阶下一环 / 图装下一环菜单化（另窗证据）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）
7. **破绽 CounterPath.offerPick / 步态 StridePath.offerPick** 等其他 *Path.offerPick 逐支另窗（禁全族大扫）
8. 身法 StepPath / 闪冲 SidestepPath / 轻技 FlexPath 等（若仍教裸指令）逐支另窗

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 12:22 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DJ CLOSED 后连开 · 选题 PledgePath.offerPick / applyAndReply 誓约路径选打菜单化 · 拟 D13342 · TIP-DK · ≠半行 ≠试炼选打同题 ≠深渊/短征/团本/硬本/精选选打同题 ≠已齐 settle 同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征薄债+选打已齐；settle CW–DD+DE–DJ选打已齐；DESIGN-DJ§6⑦；offerPick ConfirmTokens→pledgepath；无参/周要约/Q06；有 pledge 无 pledge_path；破绽/步态后置；见 RESEARCH） |

*D13342 **待批 A** · 荐 M · PledgePath.offerPick / applyAndReply 誓约路径选打菜单化 · TIP-DK。offerPick/applyAndReply/无参 pledgepath/周要约→开 ember_p1_pledge_path · 清 ConfirmTokens 教 pledgepath 主 UI · 零改伤害/CD表/掉落/体力价/誓约规则语义 · ≠ trial_path / 已齐选打同题 · 破绽/步态后置 · ≠半行 ≠已齐 settle/选打同题复读 ≠sx20。*
