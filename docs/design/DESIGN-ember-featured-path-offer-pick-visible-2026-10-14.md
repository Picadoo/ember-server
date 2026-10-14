# 余烬 · FeaturedPath.offerPick / applyAndReply「精选路径选打」菜单化（选路径聊天钮去指令化 · ≠精选结算下一环同题 · ≠图身份/深渊/挑战/团本/短征 settle 下一环同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬掉落/体力价/周帽 · ≠全族 *Path.offerPick 大扫）

| 日 | 事 |
|----|-----|
| 2026-10-14 10:41 Asia/Shanghai | 总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/applyAndReply/无参 featurepath→开 ember_p1_featured_path · 清 ConfirmTokens 教 featurepath/enter 主 UI · 零改掉落/体力价/周帽 · ≠ featured_settle 同题 · |
| 2026-10-14 10:40 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DD CLOSED 后连开 · 拟 D13336 · TIP-DE |

STATUS=**已批 A · 方案 M** · APPROVED A·M · D13336 · TIP-DE · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改掉落/体力价/周帽 · 2026-10-14 10:41 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-featured-path-offer-pick-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-featured-path-offer-pick-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-featured-path-offer-pick-visible-2026-10-14.md`](RESEARCH-ember-featured-path-offer-pick-visible-2026-10-14.md) · backlog `B-featured-path-offer-pick-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬掉落/体力价/周帽 ≠精选settle下一环同题 ≠图身份settle下一环同题 ≠深渊settle下一环同题 ≠挑战settle下一环同题 ≠团本settle下一环同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠D673同题复读 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫 ≠全族 *Path.offerPick 大扫**

> **一句话玩家价值：** 周选/改精选打法一点开「精选路径」——挑战/普通/清空/进本周精选/回冒险都能点；**不**抬掉落/体力价/周帽、**不**教手打指令、**不**复读已齐 settle 同题、**不**扩成全族路径大扫。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DD CLOSED `36ecc1b43` · 连开 TIP-DE | 内容优先；禁已齐 settle 同题；选题挂机>短征>其余；①②无新债则扫菜单去指令残留 |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** gem gap → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追/settle/soft-reward/回看已齐；sx20 禁 → **无薄窗新硬债** |
| E4 | D673/D794 settle | TIP-DC/TIP-DD **已齐勿复读**；短征/团本/挑战/深渊 settle 亦齐 |
| E5 | DESIGN-DD §6⑤ | 明文后置「FeaturedPath.offerPick / glance 全链教指令大扫（本窗只钉 settle；另窗）」 |
| E6 | `EmberFeaturedPath.offerPick` L99–107 | ConfirmTokens → `/corerpg p1 featurepath challenge\|normal\|clear` |
| E7 | `EmberFeaturedPath.applyAndReply` L171–179 | ConfirmTokens「下一步」→ 裸 `enterCmd` / runs / featurepath |
| E8 | `EmberCommand` featurepath 无参 L3787–3790；冒险 Open L50 | 无参/Open → `offerPick` 聊天选；**无** `ember_p1_featured_path` |
| E9 | glance L73；enterCue L189 | 文案仍教 `/corerpg p1 featurepath` |
| E10 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E11 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 挂机/短征/各 settle 下一环已齐后，**精选路径选打**仍钉在 ConfirmTokens（featurepath/enter），冒险 Open 仍触发聊天三钮——**没有**选打薄页。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 已齐 settle 同题（含精选 settle TIP-DD） | **否** | 派单禁；刚 CLOSED |
| legacy evacuate 大扫 | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 有债但非 DESIGN-DD 明文后置；forge/mapfarm 已有菜单主路径 |
| 全族 *Path.offerPick 大扫 | **否** | 扩窗；本窗只钉 FeaturedPath |
| 只半行 | **否** | REJECT |
| **FeaturedPath.offerPick / applyAndReply 选打菜单化（荐）** | **采纳** | E2–E9；真可点；≠ settle 同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼选打：** 周要约/改路径/冒险点精选路径 → 开「精选路径」薄页，见本周精选图与当前路径。
2. **一点去向：** 精选·挑战 / 精选·普通 / 清空 / 进本周精选 / 冒险 **全是点击**；**零**以 ConfirmTokens 教裸 `featurepath`/`enter` 为主 UI。
3. **诚实可读：** lore 解锁/Q07 挑战灰态诚实；不抬周帽/掉落。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄选打页（荐）** | 真开菜单；清裸指令主路径；**零改**掉落/体力价/周帽 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬掉落/体力价/周帽 / sx20 / 已齐 settle 同题 / 庭内 / 全族 Path 大扫** | — | **否决** |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a 插件改 offerPick + applyAndReply + 无参 featurepath 必做；W1b 薄选打页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/全族大扫）
- [x] 批注：总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/applyAndReply/无参 featurepath→开 ember_p1_featured_path · 清 ConfirmTokens 教 featurepath/enter 主 UI · 零改掉落/体力价/周帽 · ≠ featured_settle 同题 · 2026-10-14 10:41 Asia/Shanghai

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_featured_path`（OpenCommand `/ember_p1_featured_path`） | 新页；**不**复开 `ember_p1_featured_settle` 同题 |
| 触发 A | `EmberFeaturedPath.offerPick`（周要约/无参/重选） | 禁改精选语义/周规则 |
| 触发 B | `applyAndReply` 成功/失败下一步 | 禁裸 enter ConfirmTokens 为主 UI |
| 触发 C | 冒险 Open `corerpg p1 featurepath` / 命令无参 | 改开薄页或菜单优先 |
| 选挑战/普通/清空 | 菜单 actions `featurepath challenge\|normal\|clear` | **禁**主 UI 仍以 ConfirmTokens 三钮唯一交付 |
| 进本周精选 | 菜单 actions 调既有 `enter`（challenge/普通） | 禁教手打 enter 为主 UI |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 工坊（可选） | 开 `ember_p1_forge` | 禁改 forge 价 |
| glance/enterCue | 去「手打 /corerpg p1 featurepath」主教 | 可改「点精选路径页」 |
| 已齐 settle / Cast / Aid / variety / 庭内 | **零改** | 本债≠同题复读 |
| 其他 *Path.offerPick | **零改本窗** | 另窗 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | offerPick / 无参 featurepath / applyAndReply 下一步→开薄页；清 ConfirmTokens 教指令主路径；可选 `featured_path_*` PAPI；单测 | 不改掉落/体力价/周帽 |
| **W1b · 薄选打页（必做 · TrMenu）** | `ember_p1_featured_path`：挑战/普通/清空/进本/冒险/可选工坊；lore 挂图名+路径 | 不改 featured_settle / 庭内 / 其他 Path |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

- 页标题：`余烬 · 精选路径`
- lore：`本周精选：{图名}` · `当前：挑战/普通/未选`
- 灰态：`需首通 Q01` · `挑战需首通 Q07`

### 3.4 经济

**零** 改掉落 / 体力价 / 周帽 / 精选语义 / 印记帽。

### 3.5 为何不是半行

主交付 = **薄 featured_path 页真开菜单选打** + **插件改 offerPick / applyAndReply / 无参入口** + 清 ConfirmTokens 教指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（抽测）

| # | 项 |
|---|-----|
| V1 | offerPick / 无参 featurepath / 冒险 Open → 开 `ember_p1_featured_path`（非 ConfirmTokens 教 featurepath 三钮为主 UI） |
| V2 | 薄页真开：挑战/普通/清空/进本周精选/冒险 |
| V3 | applyAndReply 后下一步菜单优先；PAPI lore 诚实；Q01/Q07 灰态 |
| V4 | 零抬掉落/体力价/周帽；零改已齐 settle 薄页（含 featured_settle） |

---

## 5. 硬禁（本窗）

- 半行独批 · 已齐 settle 同题复读（含 TIP-DD 精选 settle）
- 抬掉落/日帽/体力价/周帽 · sx20 · densify · Pack6 · 开 K3 · 关观察
- variety 完整经济 · Cast·Aid 再拧 · 庭内旁路 · 教裸指令主 UI
- legacy evacuate 大扫 · 全族 *Path.offerPick 大扫

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 工坊升阶下一环 / 图装下一环菜单化（另窗证据）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）
7. 试炼/签律/步态等其他 *Path.offerPick 逐支另窗

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 10:41 Asia/Shanghai | 总控 · **批 A·M** · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/applyAndReply/无参 featurepath→开 ember_p1_featured_path · 清 ConfirmTokens 教 featurepath/enter 主 UI · 零改掉落/体力价/周帽 · ≠ featured_settle 同题 · 半行 REJECT `d00ed84bc` 保持 |
| 2026-10-14 10:40 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DD CLOSED 后连开 · 选题 FeaturedPath.offerPick / applyAndReply 精选路径选打菜单化 · 拟 D13336 · TIP-DE · ≠半行 ≠精选settle同题 ≠已齐 settle 同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征薄债已齐；D673/D794 settle已齐；DESIGN-DD§6⑤；offerPick ConfirmTokens；applyAndReply 裸 enter；冒险 Open featurepath；无薄页；见 RESEARCH） |

*D13336 **已批 A · 方案 M** · APPROVED A·M · FeaturedPath.offerPick / applyAndReply 精选路径选打菜单化 · TIP-DE。**可施工 W1a插件+W1b物品** · L不批 · 勿勾 CLOSED · offerPick/applyAndReply/无参 featurepath→开 ember_p1_featured_path · 清 ConfirmTokens 教 featurepath/enter 主 UI · 零改掉落/体力价/周帽 · ≠ featured_settle 同题 · ≠半行 ≠已齐 settle 同题复读 ≠sx20 · 2026-10-14 10:41 Asia/Shanghai*
