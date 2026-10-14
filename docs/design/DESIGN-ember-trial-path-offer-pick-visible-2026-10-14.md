# 余烬 · TrialPath.offerPick / applyAndReply「试炼路径选打」菜单化（选路径聊天钮去指令化 · ≠深渊路径选打同题 · ≠短征/团本/硬本/精选路径选打同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬伤害/CD表/掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠签律/破绽/步态同窗）

| 日 | 事 |
|----|-----|
| 2026-10-14 12:20 Asia/Shanghai | **CLOSED live** · feat `845a99920` · tip `dbbc499b0` · PASS `61e500086` · jar `1.65.834-d13341.local` · 批 sha `dbbc499b0` · menu tip `9dabb653e` · 证据 `/workspace/tmp/tip-dj-trial-path/` · W1a–b 齐 · 试炼路径选打菜单化同题勿再交 · 下一窗：内容优先（挂机若新硬债 / 其他菜单去指令残留如签律/破绽/步态等 *Path.offerPick 另窗逐支 / legacy evacuate 另窗；试炼路径选打本支已齐） |
| 2026-10-14 12:10 Asia/Shanghai | 总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/applyAndReply/无参 trialpath/周要约→开 ember_p1_trial_path · 清 ConfirmTokens 教 trialpath 主 UI · 零改伤害/CD表/掉落/体力价 · ≠ abyss_path / 已齐选打同题 · 签律/破绽/步态后置 · 2026-10-14 12:10 Asia/Shanghai |
| 2026-10-14 12:05 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DI CLOSED 后连开 · 拟 D13341 · TIP-DJ |

STATUS=**CLOSED live** · feat `845a99920` · tip `dbbc499b0` · PASS `61e500086` · jar `1.65.834-d13341.local` · 批 sha `dbbc499b0` · D13341 · TIP-DJ · 2026-10-14 12:20 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-trial-path-offer-pick-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-trial-path-offer-pick-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-trial-path-offer-pick-visible-2026-10-14.md`](RESEARCH-ember-trial-path-offer-pick-visible-2026-10-14.md) · backlog `B-trial-path-offer-pick-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬伤害/CD表/掉落/体力价 ≠深渊路径选打同题 ≠短征路径选打同题 ≠团本路径选打同题 ≠硬本路径选打同题 ≠精选路径选打同题 ≠已齐 settle 下一环同题 ≠短征settle回看同题 ≠D673同题复读 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫 ≠全族 *Path.offerPick 大扫 ≠签律/破绽/步态同窗**

> **一句话玩家价值：** 周选/改试炼路径一点开「试炼路径」——稳慎/猛开/加压/清空/回冒险都能点；**不**抬伤害/CD表/掉落/体力价、**不**教手打指令、**不**复读已齐 settle/深渊·短征·团本·硬本·精选选打同题、**不**扩成全族路径大扫。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DI CLOSED（PASS `22aed762e`）· 派 TIP-DJ | 内容优先；禁已齐 settle/深渊选打同题；默认钉 TrialPath.offerPick |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** gem gap → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH 已齐；sx20 禁 → **无薄窗新硬债** |
| E4 | settle CW–DD + 选打 TIP-DE–DI | **已齐勿复读** |
| E5 | DESIGN-DI §6⑦ | 明文后置 TrialPath.offerPick / 签律·破绽·步态等逐支另窗 |
| E6 | `EmberTrialPath.offerPick` | ConfirmTokens → `/corerpg p1 trialpath cautious\|aggressive\|pressure\|clear` |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ06` | 周要约 → `offerPick` |
| E8 | `applyAndReply` | 下一步 ConfirmTokens：进本 / `pledgepath` / `counterpath` / `trialpath` |
| E9 | `EmberCommand` trialpath 无参 | 无参 → glance + `offerPick`；用法仍教 `cautious\|aggressive\|pressure\|clear`；nudge 改路径仍教手打 |
| E10 | TrMenu **无** `ember_p1_trial_path`（亦无其他 trial 选打页） | menus/ |
| E11 | 签律/破绽/步态等 *Path.offerPick 仍教裸指令 | **后置本窗**（禁全族大扫） |
| E12 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E13 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 挂机/短征/各 settle/选打 DE–DI 已齐后，**试炼路径选打**仍钉在 ConfirmTokens 教裸 `trialpath`，周要约/无参同入口，`applyAndReply` 下一步仍聊天钮——**没有**选打专用薄页。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 已齐 settle 同题 / 深渊选打 TIP-DI / 短征·团本·硬本·精选选打 | **否** | 派单禁；刚 CLOSED |
| 签律/破绽/步态同窗 | **否** | 后置；禁全族大扫 |
| legacy evacuate 大扫 | **后置** | 须另窗证据 |
| 全族 *Path.offerPick 大扫 | **否** | 扩窗 |
| 只半行 | **否** | REJECT |
| **TrialPath.offerPick / applyAndReply 选打菜单化（荐）** | **采纳** | E2–E10；真可点；镜像 TIP-DE–DI；≠ 深渊选打同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼选打：** 周要约/改路径/无参 trialpath → 开「试炼路径」薄页，见当前稳慎/猛开/加压。
2. **一点去向：** 稳慎 / 猛开 / 加压 / 清空 / 冒险 **全是点击**；**零**以 ConfirmTokens 教裸 `trialpath` 为主 UI。
3. **诚实可读：** lore 解锁/Q06 灰态诚实；不抬伤害/CD表/掉落/体力价。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄选打页（荐）** | 真开菜单；清教指令主路径；**零改**伤害/CD表/掉落/体力价 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬伤害/CD表/掉落/体力价 / sx20 / 已齐同题 / 庭内 / 全族 Path / 签律·破绽同窗** | — | **否决** |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a 插件改 offerPick + applyAndReply + 无参 trialpath + 周要约必做；W1b 薄选打页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/全族大扫/签律·破绽同窗）
- [x] 批注：总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/applyAndReply/无参 trialpath/周要约→开 ember_p1_trial_path · 清 ConfirmTokens 教 trialpath 主 UI · 零改伤害/CD表/掉落/体力价 · ≠ abyss_path / 已齐选打同题 · 签律/破绽/步态后置 · 2026-10-14 12:10 Asia/Shanghai

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_trial_path`（OpenCommand `/ember_p1_trial_path`） | 新页；**不**复开已齐选打/settle 同题 |
| 触发 A | `EmberTrialPath.offerPick`（周要约/无参/重选） | 禁改试炼语义/伤害/CD |
| 触发 B | `applyAndReply` 成功/失败下一步 | 禁 ConfirmTokens 教裸 trialpath/pledgepath/counterpath 为主 UI |
| 触发 C | 命令无参 / 周要约 | 改开薄页或菜单优先 |
| 选稳慎/猛开/加压/清空 | 菜单 actions `trialpath cautious\|aggressive\|pressure\|clear` | **禁**主 UI 仍以 ConfirmTokens 唯一交付 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 工坊（可选） | 开 `ember_p1_forge` | 禁改 forge 价 |
| 进本（可选） | 按当前试炼 `enterCmd` | 禁改本表/体力价 |
| glance/nudge/用法 | 去「手打 /corerpg p1 trialpath …」主教 | 可改「点试炼路径页」 |
| 已齐 settle / Short·Raid·Featured·Challenge·Abyss Path / Cast / Aid / variety / 庭内 | **零改** | 本债≠同题复读 |
| 签律/破绽/步态等 *Path.offerPick | **零改本窗** | 另窗 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | offerPick / 无参 trialpath / 周要约 / applyAndReply 下一步→开薄页；清 ConfirmTokens 教指令主路径；可选 PAPI；单测 | 不改伤害/CD表/掉落/体力价/试炼写入语义 |
| **W1b · 薄选打页（必做 · TrMenu）** | `ember_p1_trial_path`：稳慎/猛开/加压/清空/冒险/可选工坊·进本；lore 挂路径 | 不改 abyss_path / 庭内 / 签律·破绽·步态 / 其他 Path |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

- 页标题：`余烬 · 试炼路径`
- lore：`当前：稳慎/猛开/加压/未选 · 限药+落空 / 逆行+撞墙 / 双挂+破招`
- 灰态：`需首通 Q06（自选誓约解锁）`

### 3.4 经济

**零** 改伤害 / CD 表 / 掉落 / 体力价 / 试炼语义（誓约+破绽写入规则仍按既有 D496）。

### 3.5 为何不是半行

主交付 = **薄 trial_path 页真开菜单选打** + **插件改 offerPick / applyAndReply / 无参 / 周要约入口** + 清 ConfirmTokens 教指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（抽测）

| # | 项 |
|---|-----|
| V1 | offerPick / 无参 trialpath / 周要约 → 开 `ember_p1_trial_path`（非 ConfirmTokens 教 trialpath 为主 UI） |
| V2 | 薄页真开：稳慎/猛开/加压/清空/冒险 |
| V3 | applyAndReply 后下一步菜单优先；PAPI lore 诚实；Q06 灰态 |
| V4 | 零抬伤害/CD表/掉落/体力价；零改已齐选打/settle 薄页（含 `ember_p1_abyss_path`） |

---

## 5. 硬禁（本窗）

- 半行独批 · 已齐 settle 同题复读 · 深渊路径选打同题复读（TIP-DI） · 短征/团本/硬本/精选路径选打同题复读（TIP-DH/DG/DF/DE）
- 抬伤害/CD表/掉落/体力价 · sx20 · densify · Pack6 · 开 K3 · 关观察
- variety 完整经济 · Cast·Aid 再拧 · 庭内旁路 · 教裸指令主 UI
- legacy evacuate 大扫 · 全族 *Path.offerPick 大扫 · 签律/破绽/步态同窗

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 工坊升阶下一环 / 图装下一环菜单化（另窗证据）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）
7. **签律/破绽/步态等其他 *Path.offerPick** 逐支另窗（禁全族大扫）

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 12:20 Asia/Shanghai | 总控 · **勾毕 CLOSED live** · feat `845a99920` · tip `dbbc499b0` · PASS `61e500086` · jar `1.65.834-d13341.local` · 批 sha `dbbc499b0` · menu tip `9dabb653e` · 证据 `/workspace/tmp/tip-dj-trial-path/` · 半行 REJECT `d00ed84bc` **保持否决** · 试炼路径选打菜单化同题勿再交 · 下一窗：内容优先（挂机若新硬债 / 其他菜单去指令残留如签律/破绽/步态等 *Path.offerPick 另窗逐支 / legacy evacuate 另窗；试炼路径选打本支已齐） |
| 2026-10-14 12:10 Asia/Shanghai | 总控 · **批 A·M** · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · offerPick/applyAndReply/无参 trialpath/周要约→开 ember_p1_trial_path · 清 ConfirmTokens 教 trialpath 主 UI · 零改伤害/CD表/掉落/体力价 · ≠ abyss_path / 已齐选打同题 · 签律/破绽/步态后置 · 半行 REJECT `d00ed84bc` 保持 |
| 2026-10-14 12:05 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DI CLOSED 后连开 · 选题 TrialPath.offerPick / applyAndReply 试炼路径选打菜单化 · 拟 D13341 · TIP-DJ · ≠半行 ≠深渊选打同题 ≠短征/团本/硬本/精选选打同题 ≠已齐 settle 同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征薄债+选打已齐；settle CW–DD+DE–DI选打已齐；DESIGN-DI§6⑦；offerPick ConfirmTokens→trialpath；无参/周要约；无 `ember_p1_trial_path`；签律/破绽/步态后置；见 RESEARCH） |

*D13341 **CLOSED live** · `845a99920` / `dbbc499b0` · PASS `61e500086` · TrialPath.offerPick / applyAndReply 试炼路径选打菜单化 · TIP-DJ。**已批 A · 方案 M** · offerPick/applyAndReply/无参 trialpath/周要约→开 ember_p1_trial_path · 清 ConfirmTokens 教 trialpath 主 UI · 零改伤害/CD表/掉落/体力价 · ≠ abyss_path / 已齐选打同题 · 签律/破绽/步态后置 · ≠半行 ≠已齐 settle/选打同题复读 ≠sx20。*
