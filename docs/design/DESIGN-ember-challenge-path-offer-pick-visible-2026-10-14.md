# 余烬 · ChallengePath.offerPick / applyAndReply「硬本路径选打」菜单化（选路径聊天钮去指令化 · ≠精选路径选打同题 · ≠挑战settle下一环同题 · ≠精选/图身份/深渊/团本/短征 settle 下一环同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬掉落/体力价 · ≠全族 *Path.offerPick 大扫 · ≠RaidPath同窗）

| 日 | 事 |
|----|-----|
| 2026-10-14 10:57 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DE CLOSED 后连开 · 拟 D13337 · TIP-DF |

STATUS=**待批 A** · 荐方案 M · 拟 D13337 · TIP-DF · L不荐 · docs-only · 勿勾 CLOSED · 零改掉落/体力价 · 2026-10-14 10:57 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-challenge-path-offer-pick-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-challenge-path-offer-pick-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-challenge-path-offer-pick-visible-2026-10-14.md`](RESEARCH-ember-challenge-path-offer-pick-visible-2026-10-14.md) · backlog `B-challenge-path-offer-pick-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬掉落/体力价 ≠精选路径选打同题 ≠挑战settle下一环同题 ≠精选settle下一环同题 ≠图身份settle下一环同题 ≠深渊settle下一环同题 ≠团本settle下一环同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠D673同题复读 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫 ≠全族 *Path.offerPick 大扫 ≠RaidPath同窗大扫**

> **一句话玩家价值：** 周选/改硬本路径一点开「硬本路径」——偏挑战/偏普通/随令/清空/回冒险都能点；**不**抬掉落/体力价、**不**教手打指令、**不**复读已齐 settle/精选选打同题、**不**扩成全族路径大扫。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DE CLOSED（PASS `687033a51`）· 连开 TIP-DF | 内容优先；禁已齐 settle/精选选打同题；①②无新债则钉一条 Path.offerPick |
| E2 | 实扫挂机庭 | 旁路全弧 CLOSED；afk **零** gem gap → **无新硬债** |
| E3 | 实扫短征乐趣 | Cast/Aid/todayRot/variety/日差/路径追/settle/soft-reward/回看已齐；sx20 禁 → **无薄窗新硬债** |
| E4 | settle CW–DD + 精选选打 TIP-DE | **已齐勿复读** |
| E5 | DESIGN-DE §6⑦ | 明文后置其他 *Path.offerPick 逐支另窗 |
| E6 | `EmberChallengePath.offerPick` L103–111 | ConfirmTokens → `SETTLE_NEXT_LOOP_CMD`=`/ember_p1_challenge_settle`（借结算面当选打） |
| E7 | `applyAndReply` L177–181 | ConfirmTokens「下一步」→ 同 settle 薄页 |
| E8 | `EmberCommand` challengepath 无参 L2705–2711；冒险 Open adventure.yml L48 | 无参/Open → `offerPick`；用法 L2728 仍教手打 prefer|easy|follow|clear；nudge L2721 仍教 `/corerpg p1 challengepath` |
| E9 | TrMenu **无** `ember_p1_challenge_path`；仅有 `ember_p1_challenge_settle`（结算面混装路径格，≠选打专用面） | menus/ |
| E10 | RaidPath.offerPick 同构残留 | **后置本窗**（逐支另窗；禁全族大扫） |
| E11 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E12 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 挂机/短征/各 settle/精选选打已齐后，**硬本路径选打**仍钉在 ConfirmTokens（借 `challenge_settle` 当选打主 UI），冒险 Open 仍触发聊天钮——**没有**选打专用薄页。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 挂机庭加厚 / 宝石 | **否** | 近饱和；gem gap 未亮 |
| 短征乐趣薄窗 / sx20 | **否** | 已齐 / 硬禁 |
| 已齐 settle 同题 / 精选选打 TIP-DE | **否** | 派单禁；刚 CLOSED |
| legacy evacuate 大扫 | **后置** | 须另窗证据 |
| RaidPath.offerPick | **后置本窗** | 同构；逐支另窗；本窗只钉 ChallengePath |
| 全族 *Path.offerPick 大扫 | **否** | 扩窗 |
| 只半行 | **否** | REJECT |
| **ChallengePath.offerPick / applyAndReply 选打菜单化（荐）** | **采纳** | E2–E10；真可点；镜像 TIP-DE；≠ settle 同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼选打：** 周要约/改路径/冒险点硬本路径 → 开「硬本路径」薄页，见当前路径。
2. **一点去向：** 偏挑战 / 偏普通 / 随令 / 清空 / 冒险 **全是点击**；**零**以 ConfirmTokens 借 settle 页或教裸 `challengepath` 为主 UI。
3. **诚实可读：** lore 解锁/Q07 灰态诚实；不抬掉落/体力价。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄选打页（荐）** | 真开菜单；清借 settle/教指令主路径；**零改**掉落/体力价 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬掉落/体力价 / sx20 / 已齐同题 / 庭内 / 全族 Path / RaidPath同窗** | — | **否决** |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a 插件改 offerPick + applyAndReply + 无参 challengepath 必做；W1b 薄选打页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [ ] 否决 A（只半行）/ W（抬表/sx20/已齐同题/庭内/全族大扫/Raid同窗）
- [ ] 批注：____

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_challenge_path`（OpenCommand `/ember_p1_challenge_path`） | 新页；**不**复开 `ember_p1_challenge_settle` 同题 |
| 触发 A | `EmberChallengePath.offerPick`（周要约/无参/重选） | 禁改硬本语义/resolve |
| 触发 B | `applyAndReply` 成功/失败下一步 | 禁借 settle ConfirmTokens 为主 UI |
| 触发 C | 冒险 Open `corerpg p1 challengepath` / 命令无参 | 改开薄页或菜单优先 |
| 选偏挑战/偏普通/随令/清空 | 菜单 actions `challengepath prefer|easy|follow|clear` | **禁**主 UI 仍以 ConfirmTokens/借 settle 唯一交付 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 挑战选图（可选） | 开 `ember_p1_challenge` | 禁改挑战选图数值 |
| 工坊（可选） | 开 `ember_p1_forge` | 禁改 forge 价 |
| glance/nudge/用法 | 去「手打 /corerpg p1 challengepath …」主教 | 可改「点硬本路径页」 |
| 已齐 settle（含 challenge_settle）/ FeaturedPath / Cast / Aid / variety / 庭内 | **零改** | 本债≠同题复读 |
| RaidPath / 其他 *Path.offerPick | **零改本窗** | 另窗 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | offerPick / 无参 challengepath / applyAndReply 下一步→开薄页；清 ConfirmTokens 借 settle/教指令主路径；可选 PAPI；单测 | 不改掉落/体力价/resolve |
| **W1b · 薄选打页（必做 · TrMenu）** | `ember_p1_challenge_path`：偏挑战/偏普通/随令/清空/冒险/可选选图·工坊；lore 挂路径 | 不改 challenge_settle / 庭内 / RaidPath / FeaturedPath |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

- 页标题：`余烬 · 硬本路径`
- lore：`当前：偏挑战/偏普通/随令/未选`
- 灰态：`需首通 Q07`

### 3.4 经济

**零** 改掉落 / 体力价 / 硬本语义 / resolve 算法。

### 3.5 为何不是半行

主交付 = **薄 challenge_path 页真开菜单选打** + **插件改 offerPick / applyAndReply / 无参入口** + 清 ConfirmTokens 借 settle/教指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（抽测）

| # | 项 |
|---|-----|
| V1 | offerPick / 无参 challengepath / 冒险 Open → 开 `ember_p1_challenge_path`（非 ConfirmTokens 借 settle / 教 challengepath 为主 UI） |
| V2 | 薄页真开：偏挑战/偏普通/随令/清空/冒险 |
| V3 | applyAndReply 后下一步菜单优先；PAPI lore 诚实；Q07 灰态 |
| V4 | 零抬掉落/体力价；零改已齐 settle 薄页（含 `ember_p1_challenge_settle`） |

---

## 5. 硬禁（本窗）

- 半行独批 · 已齐 settle 同题复读 · 精选路径选打同题复读（TIP-DE）
- 抬掉落/日帽/体力价 · sx20 · densify · Pack6 · 开 K3 · 关观察
- variety 完整经济 · Cast·Aid 再拧 · 庭内旁路 · 教裸指令主 UI
- legacy evacuate 大扫 · 全族 *Path.offerPick 大扫 · RaidPath 同窗大扫

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 工坊升阶下一环 / 图装下一环菜单化（另窗证据）
6. legacy AbyssSettleService evacuate 大扫（须另窗证据）
7. **RaidPath.offerPick / applyAndReply 团本路径选打菜单化**（同构残留；逐支另窗）
8. 试炼/签律/步态等其他 *Path.offerPick 逐支另窗

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 10:57 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DE CLOSED 后连开 · 选题 ChallengePath.offerPick / applyAndReply 硬本路径选打菜单化 · 拟 D13337 · TIP-DF · ≠半行 ≠精选选打同题 ≠挑战settle同题 ≠已齐 settle 同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（挂机近饱和·gem gap未亮；短征薄债已齐；settle CW–DD+TIP-DE选打已齐；DESIGN-DE§6⑦；offerPick ConfirmTokens→challenge_settle；无参/冒险 Open；无 `ember_p1_challenge_path`；RaidPath后置；见 RESEARCH） |

*D13337 **待批 A** · 荐方案 M · ChallengePath.offerPick / applyAndReply 硬本路径选打菜单化 · TIP-DF。L不荐 · docs-only · 勿勾 CLOSED · 半行 REJECT `d00ed84bc` 保持 · ≠ challenge_settle 同题 · ≠ featured_path 选打同题 · ≠全族Path大扫 · 2026-10-14 10:57 Asia/Shanghai*
