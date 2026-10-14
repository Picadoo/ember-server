# 余烬 · RaidPath.offerPick / applyAndReply「团本路径选打」菜单化（选路径聊天钮去指令化 · ≠硬本路径选打同题 · ≠精选路径选打同题 · ≠团本settle下一环同题 · ≠已齐 settle 下一环同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬掉落/体力价/周帽 · ≠全族 *Path.offerPick 大扫 · ≠FeaturedPath/ChallengePath同窗）

| 日 | 事 |
|----|-----|
| 2026-10-14 11:15 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DF CLOSED 后连开 · 拟 D13338 · TIP-DG |

STATUS=**待批 A** · 荐方案 **M** · L不批 · 拟 D13338 · TIP-DG · 2026-10-14 11:15 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-raid-path-offer-pick-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-raid-path-offer-pick-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-raid-path-offer-pick-visible-2026-10-14.md`](RESEARCH-ember-raid-path-offer-pick-visible-2026-10-14.md) · backlog `B-raid-path-offer-pick-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬掉落/体力价/周帽 ≠硬本路径选打同题 ≠精选路径选打同题 ≠团本settle下一环同题 ≠已齐 settle 下一环同题 ≠短征settle回看同题 ≠D673同题复读 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫 ≠全族 *Path.offerPick 大扫 ≠FeaturedPath/ChallengePath同窗**

> **一句话玩家价值：** 周选/改团本路径一点开「团本路径」——R01/R02/R03/清空/回冒险都能点；**不**抬掉落/体力价/周帽、**不**教手打指令、**不**复读已齐 settle/硬本·精选选打同题、**不**扩成全族路径大扫。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DF CLOSED（PASS `6d98cb62a`）· 派 TIP-DG | 内容优先；禁已齐 settle/精选·硬本选打同题；默认钉 RaidPath.offerPick |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** gem gap → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追/settle/soft-reward/回看已齐；sx20 禁 → **无薄窗新硬债** |
| E4 | settle CW–DD + 精选选打 TIP-DE + 硬本选打 TIP-DF | **已齐勿复读** |
| E5 | DESIGN-DF §6⑦ | 明文后置 RaidPath.offerPick / applyAndReply 团本路径选打菜单化 |
| E6 | `EmberRaidPath.offerPick` L99–107 | ConfirmTokens → `SETTLE_NEXT_LOOP_CMD`=`/ember_p1_raid_settle`（借结算面当选打） |
| E7 | `applyAndReply` L170–175 | ConfirmTokens「下一步」→ 同 settle 薄页 |
| E8 | `EmberCommand` raidpath 无参 L3851–3856；冒险 Open adventure.yml L49 | 无参/Open → `offerPick`；用法 L3873 仍教手打 r01\|r02\|r03\|clear；nudge L3866 仍教 `/corerpg p1 raidpath` |
| E9 | TrMenu **有** `ember_p1_raid_settle` / `ember_raid*`；**无** `ember_p1_raid_path` | menus/ |
| E10 | FeaturedPath / ChallengePath 选打已齐 | **零改本窗**；≠同题复读 |
| E11 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E12 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 挂机/短征/各 settle/精选·硬本选打已齐后，**团本路径选打**仍钉在 ConfirmTokens（借 `raid_settle` 当选打主 UI），冒险 Open 仍触发聊天钮——**没有**选打专用薄页。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 已齐 settle 同题 / 精选选打 TIP-DE / 硬本选打 TIP-DF | **否** | 派单禁；刚 CLOSED |
| legacy evacuate 大扫 | **后置** | 须另窗证据 |
| FeaturedPath / ChallengePath 同窗 | **否** | 已齐；禁复读 |
| 全族 *Path.offerPick 大扫 | **否** | 扩窗 |
| 只半行 | **否** | REJECT |
| **RaidPath.offerPick / applyAndReply 选打菜单化（荐）** | **采纳** | E2–E10；真可点；镜像 TIP-DE/DF；≠ settle 同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼选打：** 周要约/改路径/冒险点团本路径 → 开「团本路径」薄页，见当前路径。
2. **一点去向：** R01 / R02 / R03 / 清空 / 冒险 **全是点击**；**零**以 ConfirmTokens 借 settle 页或教裸 `raidpath` 为主 UI。
3. **诚实可读：** lore 解锁/Q07 灰态诚实；不抬掉落/体力价/周帽。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄选打页（荐）** | 真开菜单；清借 settle/教指令主路径；**零改**掉落/体力价/周帽 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬掉落/体力价/周帽 / sx20 / 已齐同题 / 庭内 / 全族 Path / Featured·Challenge同窗** | — | **否决** |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a 插件改 offerPick + applyAndReply + 无参 raidpath 必做；W1b 薄选打页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [ ] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/全族大扫/Featured·Challenge同窗）
- [ ] 批注：

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_raid_path`（OpenCommand `/ember_p1_raid_path`） | 新页；**不**复开 `ember_p1_raid_settle` 同题 |
| 触发 A | `EmberRaidPath.offerPick`（周要约/无参/重选） | 禁改团本语义/resolve |
| 触发 B | `applyAndReply` 成功/失败下一步 | 禁借 settle ConfirmTokens 为主 UI |
| 触发 C | 冒险 Open `corerpg p1 raidpath` / 命令无参 | 改开薄页或菜单优先 |
| 选 R01/R02/R03/清空 | 菜单 actions `raidpath r01\|r02\|r03\|clear` | **禁**主 UI 仍以 ConfirmTokens/借 settle 唯一交付 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 团本大厅（可选） | 开 `ember_raid` | 禁改大厅数值 |
| 工坊（可选） | 开 `ember_p1_forge` | 禁改 forge 价 |
| glance/nudge/用法 | 去「手打 /corerpg p1 raidpath …」主教 | 可改「点团本路径页」 |
| 已齐 settle（含 raid_settle）/ FeaturedPath / ChallengePath / Cast / Aid / variety / 庭内 | **零改** | 本债≠同题复读 |
| 其他 *Path.offerPick | **零改本窗** | 另窗 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | offerPick / 无参 raidpath / applyAndReply 下一步→开薄页；清 ConfirmTokens 借 settle/教指令主路径；可选 PAPI；单测 | 不改掉落/体力价/周帽/resolve |
| **W1b · 薄选打页（必做 · TrMenu）** | `ember_p1_raid_path`：R01/R02/R03/清空/冒险/可选大厅·工坊；lore 挂路径 | 不改 raid_settle / 庭内 / FeaturedPath / ChallengePath |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

- 页标题：`余烬 · 团本路径`
- lore：`当前：R01 锈轨/R02 霜封/R03 断塔/未选`
- 灰态：`需首通 Q07`

### 3.4 经济

**零** 改掉落 / 体力价 / 周帽 / 团本语义 / resolve 算法。

### 3.5 为何不是半行

主交付 = **薄 raid_path 页真开菜单选打** + **插件改 offerPick / applyAndReply / 无参入口** + 清 ConfirmTokens 借 settle/教指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（抽测）

| # | 项 |
|---|-----|
| V1 | offerPick / 无参 raidpath / 冒险 Open → 开 `ember_p1_raid_path`（非 ConfirmTokens 借 settle / 教 raidpath 为主 UI） |
| V2 | 薄页真开：R01/R02/R03/清空/冒险 |
| V3 | applyAndReply 后下一步菜单优先；PAPI lore 诚实；Q07 灰态 |
| V4 | 零抬掉落/体力价/周帽；零改已齐 settle 薄页（含 `ember_p1_raid_settle`） |

---

## 5. 硬禁（本窗）

- 半行独批 · 已齐 settle 同题复读 · 硬本路径选打同题复读（TIP-DF） · 精选路径选打同题复读（TIP-DE） · 团本settle下一环同题复读（TIP-CZ）
- 抬掉落/日帽/体力价/周帽 · sx20 · densify · Pack6 · 开 K3 · 关观察
- variety 完整经济 · Cast·Aid 再拧 · 庭内旁路 · 教裸指令主 UI
- legacy evacuate 大扫 · 全族 *Path.offerPick 大扫 · FeaturedPath/ChallengePath 同窗

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 工坊升阶下一环 / 图装下一环菜单化（另窗证据）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）
7. 试炼/签律/步态等其他 *Path.offerPick 逐支另窗（禁全族大扫）

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 11:15 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DF CLOSED 后连开 · 选题 RaidPath.offerPick / applyAndReply 团本路径选打菜单化 · 拟 D13338 · TIP-DG · ≠半行 ≠硬本选打同题 ≠精选选打同题 ≠团本settle同题 ≠已齐 settle 同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征薄债已齐；settle CW–DD+DE+DF选打已齐；DESIGN-DF§6⑦；offerPick ConfirmTokens→raid_settle；无参/冒险 Open；无 `ember_p1_raid_path`；见 RESEARCH） |

*D13338 **待批 A** · RaidPath.offerPick / applyAndReply 团本路径选打菜单化 · TIP-DG。**荐方案 M** · offerPick/applyAndReply/无参 raidpath→开 ember_p1_raid_path · 清 ConfirmTokens 借 settle/教 raidpath 主 UI · 零改掉落/体力价/周帽 · ≠ raid_settle / featured_path / challenge_path 同题 · ≠半行 ≠已齐 settle 同题复读 ≠sx20。*
