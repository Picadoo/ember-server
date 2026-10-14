# 余烬 · 挑战本结算「下一环」菜单化（settle 聊天钮去指令化 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠深渊同窗大扫）

| 日 | 事 |
|----|-----|
| 2026-10-14 09:28 Asia/Shanghai | **CLOSED live** · feat `3d218ee83` · tip `d0f10a344` · PASS `020a8f2a7` · jar `1.65.825-d13332.local` · 批 sha `d0f10a344` · 证据 `/workspace/tmp/tip-da-challenge-settle/` · W1a–b 齐 · 挑战结算下一环菜单化同题勿再交 · 下一窗：深渊 settle 去指令化 §6 |
| 2026-10-14 09:02 Asia/Shanghai | 总控批 A·M · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · ChallengePath.afterSettle→开 ember_p1_challenge_settle · 清 ConfirmTokens 教裸指令主 UI · 零改挑战掉落/体力价/成色 · 深渊后置 · |
| 2026-10-14 09:00 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-CZ CLOSED 后连开 · 拟 D13332 · TIP-DA |

STATUS=**CLOSED live** · feat `3d218ee83` · tip `d0f10a344` · PASS `020a8f2a7` · jar `1.65.825-d13332.local` · 批 sha `d0f10a344` · D13332 · TIP-DA · 2026-10-14 09:28 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-challenge-settle-next-loop-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-challenge-settle-next-loop-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-challenge-settle-next-loop-visible-2026-10-14.md`](RESEARCH-ember-challenge-settle-next-loop-visible-2026-10-14.md) · backlog `B-challenge-settle-next-loop-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬挑战掉落/体力价/成色表 ≠团本settle下一环同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠深渊同窗大扫**

> **一句话玩家价值：** 挑战本结算后一点开「下一环」——再挑战同图、回挑战选图、换硬本路径、回冒险、去工坊、图装追都能点；**不**抬掉落/体力价、**不**教手打指令、**不**复读短征/团本同题。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CZ CLOSED `dceae5440` · 连开 TIP-DA | 内容优先；禁已齐同题；§6 明确候选挑战/深渊 |
| E2 | D13331§6① | 明文后置挑战 settle；短征+团本已齐 |
| E3 | EmberChallengePath.afterSettle ConfirmTokens | `/corerpg p1 enter … challenge` / `forge` / `mapfarm` |
| E4 | SettleService 挑战首通 D671 | 工坊仍 `/corerpg p1 forge` |
| E5 | 无 `ember_p1_challenge_settle`；`ember_p1_challenge` 仅选图进本 | TrMenu |
| E6 | short_settle / raid_settle 范式可镜像 | TIP-CW / TIP-CZ |
| E7 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E8 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 短征+团本结算下一环已可点，但挑战结算下一环仍钉在聊天裸指令轨。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 短征settle下一环·回看 / 团本settle下一环 / variety再拧 | **否** | 派单禁；已 CLOSED |
| soft-reward / 路径追 / 日差 / 今日推复读 | **否** | 已齐勿再交 |
| Cast/Aid/todayRot 再拧 | **否** | 硬禁 |
| 庭内旁路 / 宝石 | **否** | 饱和 / gap 未亮 |
| 深渊同窗大扫 | **后置** | §6②；本窗只钉挑战一支 |
| 只半行 | **否** | REJECT |
| **挑战本结算下一环菜单化（荐）** | **采纳** | E2–E6；真可点；≠短征/团本同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼下一环：** 挑战结算后自动出现/可开「挑战下一环」薄页，见本图名与硬本路径标签。  
2. **一点去向：** 再挑战同图 / 挑战选图页 / 换硬本路径 / 冒险 / 工坊 / 图装追 **全是点击**；**零**教手打 `/corerpg p1 enter … challenge` 主 UI。  
3. **灰态诚实：** 体力不足·再挑战灰；未选硬本路径时路径格引导选路径而非刷 chat 为主交付。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/结算半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄 settle 页（荐）** | 真开菜单；清裸指令主路径；**零改**掉落/体力价 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬掉落/体力价 / sx20 / 短征·团本同题 / 庭内 / 深渊大扫** | — | **否决** |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a 插件改 ChallengePath.afterSettle 下一环入口必做；W1b 薄 settle 页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/短征·团本同题/庭内/深渊大扫）
- [x] 批注：总控批 A·M · D13332 · TIP-DA · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · ChallengePath.afterSettle→开 ember_p1_challenge_settle · 清 ConfirmTokens 教裸指令主 UI · 零改挑战掉落/体力价/成色 · 深渊后置 · 2026-10-14 09:02 Asia/Shanghai

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_challenge_settle`（OpenCommand 建议 `/ember_p1_challenge_settle`） | 新页；**不**复开短征/团本 settle 同题 |
| 再挑战 | 真 `corerpg enter <map> challenge` 或 `corerpg p1 enter <map> challenge`（菜单 actions；玩家不手打） | 禁改体力价/掉落/成色 |
| 挑战选图 | 开 `ember_p1_challenge` | 禁改七图 Layout 主交 |
| 换硬本路径 | 菜单点 `corerpg p1 challengepath prefer\|easy\|follow\|clear`（actions）或薄格 | 禁改路径语义 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 工坊 | 开 `ember_p1_forge` | 禁改 forge 价/配方 |
| 图装追 | 菜单 actions：`corerpg p1 mapfarm` / `mapfarm resume`（或冒险页已有追入口） | 禁发明新追猎经济 |
| settle 入口 | `EmberChallengePath.afterSettle`：优先 `open` 薄页 **或** ConfirmTokens 目标改为 `/ember_p1_challenge_settle`；挑战首通 D671 工坊钮同步改菜单优先 | **禁**目标仍以 `/corerpg p1 enter … challenge` / `/corerpg p1 forge` 为唯一主 UI |
| 文案 | glance/下一步若保留辅钮，须菜单优先；禁教手打为主路径 | 禁保留教指令主路径 |
| 短征/团本/深渊/Cast/Aid/variety | **零改** | 本债≠同题/大扫 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | ChallengePath.afterSettle 下一环→开薄页；记住 settle map；清裸指令主路径；可选 `chal_settle_*` PAPI；单测；同批对齐 D671 工坊钮→`/ember_p1_forge` 或进薄页 | 不改掉落/体力价/成色 |
| **W1b · 薄 settle 页（必做 · TrMenu）** | `ember_p1_challenge_settle`：再挑战 / 挑战选图 / 硬本路径 / 冒险 / 工坊 / 图装追；lore 挂图名+路径 label；灰态诚实 | 不改 `ember_p1_challenge` 选图主交 / 短征·团本薄页 / 庭内 |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

```
§c挑战下一环
§7本图：§fQ03 · 锈廊
§7硬本路径：§e偏挑战
§8点选 · 再挑战 / 选图 / 换路径 / 冒险 / 花料
```

### 3.4 经济幅度

| 项 | 钉死 | 禁 |
|----|------|-----|
| 挑战掉落 / 体力价 / 成色表 / 日帽 | **零改** | 除非总控另批（本 tip **不**明示抬） |
| 奖励 | **零** 新 NI 结算奖 | 禁借下一环发经济 |
| 短征/团本/variety/soft-reward | **不碰** | 同题勿再交 |

### 3.5 为何不是半行

主交付 = **薄 challenge settle 页真开菜单去向** + **插件改 ChallengePath.afterSettle 下一环入口** + 清裸指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（施工号用 · 本号只定规格）

| # | 场景 | 期望 |
|---|------|------|
| V1 | 挑战非首通且偏挑战路径 | 下一环薄页可开；再挑战可进同图挑战；选图/路径/冒险/工坊/图装追真开 |
| V2 | 体力不足 | 再挑战灰/诚实；仍可换路径/冒险/工坊 |
| V3 | 结算文案 | 聊天 **不以**「手打 /corerpg p1 enter … challenge」为主交付；主路径为菜单 |
| V4 | 零改抽测 | 挑战掉落/体力价/成色/Cast/Aid/variety/短征·团本薄页不变；零 sx20；零半行独档；零短征·团本同题施工；零深渊大扫 |

---

## 5. 禁区

- 半行 lore-only 独批（REJECT `d00ed84bc`）
- 短征 settle下一环·回看 / 团本 settle下一环 / variety再拧 / soft-reward / 路径追 / 日差同题复读
- 抬挑战掉落 / 体力价 / 成色表 / 日帽 / S表 / 伤表
- sx20 · Pack6 · 开R/K3 · 关观察 · densify · 灾厄/fest重开
- 庭内旁路再拧 · 教裸指令主 UI · stage 脏 runtime
- 深渊 settle 同窗大扫（须另窗）

---

## 6. 后置（明文 · 本窗不做）

1. 深渊 settle / evacuate 去指令化（含 `abysstarget` 教指令清扫；`/ember_p1_abyss` 已有钮但仍有裸指令旁路）  
2. 庭内顺手宝石（须庭面先亮 gem gap）  
3. variety 完整经济 / W1e todayRot 偏置  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  
6. 挑战成色/掉落/体力价抬表（须总控另批更大经济）  
7. 全服其余 settle 聊天钮（图身份首通 D673 / 精选路径 D794 等）逐支另窗  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 09:28 Asia/Shanghai | 总控 · **勾毕 CLOSED live** · feat `3d218ee83` · tip `d0f10a344` · PASS `020a8f2a7` · jar `1.65.825-d13332.local` · 批 sha `d0f10a344` · 证据 `/workspace/tmp/tip-da-challenge-settle/` · 半行 REJECT `d00ed84bc` **保持否决** · 挑战结算下一环菜单化同题勿再交 · 下一窗：深渊 settle 去指令化 §6 |
| 2026-10-14 09:02 Asia/Shanghai | 总控 · **批 A·M** · 可施工 W1a插件+W1b物品 · L不批 · 勿勾 CLOSED · ChallengePath.afterSettle→开 ember_p1_challenge_settle · 清 ConfirmTokens 教裸指令主 UI · 零改挑战掉落/体力价/成色 · 深渊后置 · 半行 REJECT `d00ed84bc` 保持 |
| 2026-10-14 09:00 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CZ CLOSED 后连开 · 选题挑战本结算下一环菜单化 · 拟 D13332 · TIP-DA · ≠半行 ≠团本settle下一环同题 ≠短征settle同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（§6①；ChallengePath ConfirmTokens；无薄页；见 RESEARCH） |

*D13332 **CLOSED live** · `3d218ee83` / `d0f10a344` · PASS `020a8f2a7` · 挑战本结算下一环菜单化 · TIP-DA。**已批 A · 方案 M** · ChallengePath.afterSettle→开 ember_p1_challenge_settle · 清 ConfirmTokens 教裸指令主 UI · 零改挑战掉落/体力价/成色 · 深渊后置 · ≠半行 ≠短征·团本同题复读 ≠sx20。*
