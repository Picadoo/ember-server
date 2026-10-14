# 余烬 · 团本结算「下一环」菜单化（settle 聊天钮去指令化 · ≠短征settle下一环同题 · ≠短征settle回看同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬周帽）

| 日 | 事 |
|----|-----|
| 2026-10-14 08:40 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-CY CLOSED 后连开 · 拟 D13331 · TIP-CZ |

STATUS=**待批 A** · 荐 **M** · 拟 D13331 · TIP-CZ · docs-only · 2026-10-14 08:40 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-raid-settle-next-loop-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-raid-settle-next-loop-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-raid-settle-next-loop-visible-2026-10-14.md`](RESEARCH-ember-raid-settle-next-loop-visible-2026-10-14.md) · backlog `B-raid-settle-next-loop-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬 weeklyCap/loot ≠短征settle下一环同题 ≠短征settle回看同题 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠挑战/深渊同窗大扫**

> **一句话玩家价值：** 团本周清后一点开「下一环」——再进路径团本、选/换路径、回冒险、去工坊、招募都能点；**不**抬周帽、**不**教手打指令、**不**复读短征同题。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CY CLOSED `bdf88e272` · 连开 TIP-CZ | 内容优先；禁短征同题复读；庭内默认勿交；§6 仍缓可施工 |
| E2 | D13330/29/28§6⑥ | 明文后置全服 settle 去指令化；短征支已齐 |
| E3 | EmberRaidLoop ConfirmTokens | `/corerpg p1 raidpath` / `recruit` / `enter` |
| E4 | EmberRaidPath glance/offerPick | 文案/钮教 `/corerpg p1 raidpath` |
| E5 | 无 `ember_p1_raid_settle`；`ember_raid` 仅大厅开本 | TrMenu |
| E6 | 短征 `ember_p1_short_settle` 范式可镜像 | TIP-CW |
| E7 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E8 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 短征结算下一环已可点，但团本周清下一环仍钉在聊天裸指令轨。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 短征settle下一环 / settle回看 / variety再拧 | **否** | 派单禁；已 CLOSED |
| soft-reward / 路径追 / 日差 / 今日推复读 | **否** | 已齐勿再交 |
| Cast/Aid/todayRot 再拧 | **否** | 硬禁 |
| 庭内旁路 / 宝石 | **否** | 饱和 / gap 未亮 |
| 挑战/深渊同窗大扫 | **后置** | §6；本窗只钉团本 |
| 只半行 | **否** | REJECT |
| **团本结算下一环菜单化（荐）** | **采纳** | E2–E6；真可点；≠短征同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼下一环：** 团本周清后自动出现/可开「团本下一环」薄页，见本周额度与路径标签。  
2. **一点去向：** 再进路径团本 / 选·换路径 / 冒险 / 工坊 / 招募 **全是点击**；**零**教手打 `/corerpg p1 raidpath` 主 UI。  
3. **灰态诚实：** 周额度满·再进灰；未选路径时路径格引导选路径而非刷 chat 为主交付。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/结算半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄 settle 页（荐）** | 真开菜单；清裸指令主路径；**零改**周帽/loot | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬周帽/loot / sx20 / 短征同题 / 庭内 / 挑战深渊大扫** | — | **否决** |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a 插件改 RaidLoop 下一环入口必做；W1b 薄 settle 页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗默认不批 L**
- [ ] 否决 A（只半行）/ W（抬表/sx20/短征同题/庭内/挑战深渊大扫）
- [ ] 批注：________________________________

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_raid_settle`（OpenCommand 建议 `/ember_p1_raid_settle`） | 新页；**不**复开短征 settle 同题 |
| 再进 | 真 `corerpg p1 enter <pathMap>`（菜单 actions；玩家不手打） | 禁改 weeklyCap/体力价 |
| 选/换路径 | 菜单点 `corerpg p1 raidpath …`（actions）或薄格切 R01/R02/R03/clear | 禁改路径语义/周帽 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 工坊 | 开 `ember_p1_forge` | 禁改 forge 价/配方 |
| 招募 | 菜单 actions：`corerpg p1 recruit <mapKey>`（有路径时） | 禁发明新招募经济 |
| settle 入口 | `EmberRaidLoop.afterWeeklyClear` 后：优先 `open` 薄页 **或** ConfirmTokens 目标改为 `/ember_p1_raid_settle` | **禁**目标仍以 `/corerpg p1 raidpath` 为唯一主 UI |
| 文案 | glance/下一步若保留辅钮，须菜单优先；禁教手打为主路径 | 禁保留教指令主路径 |
| 挑战/深渊/Cast/Aid/variety/短征 | **零改** | 本债≠同题/大扫 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | RaidLoop 下一环→开薄页；额度满/未满两态；清裸指令主路径；可选 `raid_settle_*` PAPI；单测 | 不改 weeklyCap/loot/体力价 |
| **W1b · 薄 settle 页（必做 · TrMenu）** | `ember_p1_raid_settle`：再进 / 路径 / 冒险 / 工坊 / 招募；lore 挂周次+路径 label；灰态诚实 | 不改 `ember_raid` 大厅主交 / 短征薄页 / 庭内 |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

```
§9团本下一环
§7本周额度：§e2§7/§f3
§7路径：§eR01 锈轨
§8点选 · 再进 / 换路径 / 冒险 / 花料
```

### 3.4 经济幅度

| 项 | 钉死 | 禁 |
|----|------|-----|
| 周帽 / loot / 体力价 | **零改** | 除非总控另批（本 tip **不**明示抬） |
| 奖励 | **零** 新 NI 结算奖 | 禁借下一环发经济 |
| 短征/variety/soft-reward | **不碰** | 同题勿再交 |

### 3.5 为何不是半行

主交付 = **薄 raid settle 页真开菜单去向** + **插件改 RaidLoop 下一环入口** + 清裸指令主路径。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（施工号用 · 本号只定规格）

| # | 场景 | 期望 |
|---|------|------|
| V1 | 周清且额度未满 | 下一环薄页可开；再进可进路径团本；路径/冒险/工坊/招募真开 |
| V2 | 周额度已满 | 再进灰/诚实；仍可换路径/冒险/工坊 |
| V3 | 结算文案 | 聊天 **不以**「手打 /corerpg p1 raidpath」为主交付；主路径为菜单 |
| V4 | 零改抽测 | weeklyCap/loot/体力价/Cast/Aid/variety/短征薄页不变；零 sx20；零半行独档；零短征同题施工 |

---

## 5. 禁区

- 半行 lore-only 独批（REJECT `d00ed84bc`）
- 短征 settle下一环 / settle回看 / variety再拧 / soft-reward / 路径追 / 日差同题复读
- 抬 weeklyCap / loot / 体力价 / S表 / 伤表
- sx20 · Pack6 · 开R/K3 · 关观察 · densify · 灾厄/fest重开
- 庭内旁路再拧 · 教裸指令主 UI · stage 脏 runtime
- 挑战/深渊 settle 同窗大扫（须另窗）

---

## 6. 后置（明文 · 本窗不做）

1. 挑战本 settle 下一环菜单化（`EmberChallengePath.afterSettle` ConfirmTokens）  
2. 深渊 settle / evacuate 去指令化大扫  
3. 庭内顺手宝石（须庭面先亮 gem gap）  
4. variety 完整经济 / W1e todayRot 偏置  
5. fest 继任 / 灾厄枢纽可见（须总控另批）  
6. sx20（仍禁，除非总控另开窗）  
7. 写入 `weekly_goals` / 抬团本周帽（须总控另批更大经济）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 08:40 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CY CLOSED 后连开 · 选题团本结算下一环菜单化 · 拟 D13331 · TIP-CZ · ≠半行 ≠短征settle下一环同题 ≠短征settle回看同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（§6⑥；RaidLoop ConfirmTokens；无薄页；见 RESEARCH） |

*D13331 **待批 A** · 荐 M · 团本结算下一环菜单化 · TIP-CZ。**仅 docs** · 批准前禁改 YAML/Java · ≠半行 ≠短征同题复读 ≠sx20 ≠抬 weeklyCap。*
