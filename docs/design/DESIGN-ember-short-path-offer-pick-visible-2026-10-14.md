# 余烬 · ShortPath.offerPick / applyAndReply「短征路径选打」菜单化（选路径聊天钮去指令化 · ≠路径追同题 · ≠短征settle下一环同题 · ≠短征settle回看同题 · ≠团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬日表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠AbyssPath/TrialPath同窗）

| 日 | 事 |
|----|-----|
| 2026-10-14 11:32 Asia/Shanghai | 总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/无参 shortpath/周要约→开 ember_p1_short_path · 清 ConfirmTokens 教 shortpath/enter 主 UI · 零改日表/掉落/体力价/resolve · ≠路径追同题/short_settle · Abyss/Trial 后置 · 2026-10-14 11:32 Asia/Shanghai |
| 2026-10-14 11:30 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DG CLOSED 后连开 · 拟 D13339 · TIP-DH |

STATUS=**已批 A · 方案 M** · APPROVED A·M · D13339 · TIP-DH · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · 零改日表/掉落/体力价/resolve · 2026-10-14 11:32 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-short-path-offer-pick-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-short-path-offer-pick-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-path-offer-pick-visible-2026-10-14.md`](RESEARCH-ember-short-path-offer-pick-visible-2026-10-14.md) · backlog `B-short-path-offer-pick-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬日表/掉落/体力价 ≠路径追同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠团本路径选打同题 ≠硬本路径选打同题 ≠精选路径选打同题 ≠已齐 settle 下一环同题 ≠D673同题复读 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫 ≠全族 *Path.offerPick 大扫 ≠AbyssPath/TrialPath同窗**

> **一句话玩家价值：** 周选/改短征路径一点开已有「路径追」——首通追/有奖追/后段追/清空/进下一本都能点；**不**抬日表/掉落/体力价、**不**教手打指令、**不**复读已齐路径追页/settle/选打同题、**不**扩成全族路径大扫。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DG CLOSED（PASS `7ec45e0a1`）· 派 TIP-DH | 内容优先；禁已齐 settle/选打/路径追同题；默认钉 ShortPath.offerPick |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** gem gap → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追**页**/settle/soft/回看已齐；sx20 禁；**offerPick 入口仍硬债** |
| E4 | settle CW–DD + 精选·硬本·团本选打 TIP-DE–DG | **已齐勿复读** |
| E5 | DESIGN-DG §6⑦；派单内容优先短征 | 其他 *Path 逐支后置；默认钉本支 |
| E6 | `EmberShortPath.offerPick` L184–192 | ConfirmTokens → `/corerpg p1 shortpath fc\|day\|late\|clear` |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ01` | 周要约 → `offerPick` |
| E8 | `EmberCommand` shortpath 无参 L3283–3292；用法 L3313 | 无参 → `offerPick`；仍教手打 |
| E9 | `applyAndReply` L263–272；`SETTLE_PATH_CMD=/ember_p1_short_path` | 「换一条」已菜单；「进本」仍裸 enter ConfirmTokens |
| E10 | TrMenu **有** `ember_p1_short_path`；选页 Open 仍 `corerpg p1 shortpath` | TIP-CV 薄页已在；Open 旁路未清 |
| E11 | AbyssPath / TrialPath 仍教裸指令；无对应 path 薄页 | **后置本窗** |
| E12 | **为何不是半行** | 主交付=开已有薄页+改入口；见 §3.5 |
| E13 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 路径追薄页与 settle「换一条」已齐，但 **offerPick / 周要约 / 无参 shortpath / 选页 Open** 仍钉 ConfirmTokens 教裸 `shortpath`——选打入口未开已有薄页。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣已齐支 / sx20 | **否** | 已齐 / 硬禁 |
| 已齐 settle / 选打 DE–DG / 路径追 TIP-CV | **否** | 派单禁；刚 CLOSED / ≠同题 |
| AbyssPath / TrialPath 同窗 | **否** | 后置；禁全族大扫；内容优先短征 |
| legacy evacuate 大扫 | **后置** | 须另窗证据 |
| 只半行 | **否** | REJECT |
| **ShortPath.offerPick 选打菜单化（荐）** | **采纳** | E2–E10；真可点；镜像 TIP-DE/DF/DG；≠路径追同题；≠ settle 同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼选打：** 周要约/改路径/无参 shortpath → 开已有「路径追」薄页，见当前偏好。
2. **一点去向：** 首通追 / 有奖追 / 后段追 / 清空 / 进下一本 **全是点击**；**零**以 ConfirmTokens 教裸 `shortpath`/`enter` 为主 UI。
3. **诚实可读：** lore 解锁/Q01 灰态诚实；不抬日表/掉落/体力价。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 既有薄页接线/补洞（荐）** | 真开菜单；清教指令主路径；**零改**日表/掉落/体力价 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_short_path`；无清旁路** | 减面 | **默认不批 L** |
| **W · 抬日表/掉落/体力价 / sx20 / 已齐同题 / 庭内 / 全族 Path / Abyss·Trial同窗** | — | **否决** |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a 插件改 offerPick + 无参 shortpath + 周要约必做；W1b 既有薄页接线/补洞必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；旁路后置）· **本窗不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/全族大扫/Abyss·Trial同窗）
- [x] 批注：总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/无参 shortpath/周要约→开 ember_p1_short_path · 清 ConfirmTokens 教 shortpath/enter 主 UI · 零改日表/掉落/体力价/resolve · ≠路径追同题/short_settle · Abyss/Trial 后置 · 2026-10-14 11:32 Asia/Shanghai

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_short_path`（OpenCommand `/ember_p1_short_path`） | **已有**（TIP-CV）；本窗接线/补洞；**不**复开路径追同题新建 |
| 触发 A | `EmberShortPath.offerPick`（周要约/无参/重选） | 禁改短征语义/resolve |
| 触发 B | `applyAndReply` 下一步 | 禁裸 enter ConfirmTokens 为主 UI；「换一条」已菜单可保持 |
| 触发 C | 命令无参 / 选页 Open `corerpg p1 shortpath` | 改开薄页；清 Open 旁路 |
| 选 FC/DAY/LATE/清空 | 菜单 actions `shortpath fc\|day\|late\|clear`（薄页已有） | **禁**主 UI 仍以 ConfirmTokens 唯一交付 |
| 进下一本 | 薄页既有 `enter short-path`（或等价） | 禁教裸 enter 为主 |
| 短征选页 / 冒险 / 工坊（可选） | 开 `ember_p1_short` / `ember_p1_adventure` / `ember_p1_forge` | 禁改 Layout 主交 |
| glance/nudge/用法 | 去「手打 /corerpg p1 shortpath …」主教 | 可改「点路径追页」 |
| 已齐 settle（含 short_settle）/ 路径追页语义 / RaidPath/FeaturedPath/ChallengePath / Cast / Aid / variety / 庭内 | **零改** | 本债≠同题复读 |
| 其他 *Path.offerPick（含 Abyss/Trial） | **零改本窗** | 另窗 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | offerPick / 无参 shortpath / 周要约→开薄页；清 ConfirmTokens 教 shortpath/enter 主路径；可选 PAPI；单测 | 不改日表/掉落/体力价/resolve |
| **W1b · 薄选打页接线/补洞（必做 · TrMenu）** | 既有 `ember_p1_short_path` 确认真开；清选页 Open 旁路；lore/灰态若缺则补 | 不改 short_settle / 庭内 / 其他 Path；**非必新建** |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

- 页标题：`短征 · 路径追`（既有）
- lore：`当前：首通追/有奖追/后段追/未选`
- 灰态：`需首通 Q01`

### 3.4 经济

**零** 改日表 / 掉落 / 体力价 / ShortPath resolve 算法。

### 3.5 为何不是半行

主交付 = **开已有 short_path 薄页真菜单选打** + **插件改 offerPick / 无参 / 周要约入口** + 清 ConfirmTokens 教指令主路径 + 清选页 Open 旁路。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（抽测）

| # | 项 |
|---|-----|
| V1 | offerPick / 无参 shortpath / 周要约 / 选页 Open → 开 `ember_p1_short_path`（非 ConfirmTokens 教 shortpath 为主 UI） |
| V2 | 薄页真开：首通追/有奖追/后段追/清空/进下一本 |
| V3 | applyAndReply 后下一步菜单优先；PAPI lore 诚实；Q01 灰态 |
| V4 | 零抬日表/掉落/体力价；零改已齐 settle 薄页（含 `ember_p1_short_settle`）；≠路径追同题复读 |

---

## 5. 硬禁（本窗）

- 半行独批 · 已齐 settle 同题复读 · 路径追同题复读（TIP-CV） · 短征settle下一环同题复读（TIP-CW） · 短征settle回看同题复读（TIP-CY） · 团本/硬本/精选路径选打同题复读（TIP-DG/DF/DE）
- 抬日表/掉落/体力价 · sx20 · densify · Pack6 · 开 K3 · 关观察
- variety 完整经济 · Cast·Aid 再拧 · 庭内旁路 · 教裸指令主 UI
- legacy evacuate 大扫 · 全族 *Path.offerPick 大扫 · AbyssPath/TrialPath 同窗

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 工坊升阶下一环 / 图装下一环菜单化（另窗证据）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）
7. **AbyssPath.offerPick / TrialPath.offerPick** 及其他试炼/签律/步态等 *Path.offerPick 逐支另窗（禁全族大扫）

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 11:32 Asia/Shanghai | 总控 · **批 A·M** · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/无参 shortpath/周要约→开 ember_p1_short_path · 清 ConfirmTokens 教 shortpath/enter 主 UI · 零改日表/掉落/体力价/resolve · ≠路径追同题/short_settle · Abyss/Trial 后置 · 半行 REJECT `d00ed84bc` 保持 |
| 2026-10-14 11:30 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DG CLOSED 后连开 · 选题 ShortPath.offerPick / applyAndReply 短征路径选打菜单化 · 拟 D13339 · TIP-DH · ≠半行 ≠路径追同题 ≠短征settle同题 ≠团本/硬本/精选选打同题 ≠已齐 settle 同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征已齐支勿复读但 offerPick 入口仍教指令；薄页已在·换一条已菜单；Abyss/Trial 后置；见 RESEARCH） |

*D13339 **已批 A · 方案 M** · APPROVED A·M · ShortPath.offerPick / applyAndReply 短征路径选打菜单化 · TIP-DH。**可施工 W1a插件+W1b物品** · L不批 · 勿勾 CLOSED · offerPick/无参 shortpath/周要约→开 ember_p1_short_path · 清 ConfirmTokens 教 shortpath/enter 主 UI · 零改日表/掉落/体力价/resolve · ≠路径追同题/short_settle · Abyss/Trial 后置 · ≠半行 ≠已齐 settle/选打同题复读 ≠sx20 · 2026-10-14 11:32 Asia/Shanghai*
