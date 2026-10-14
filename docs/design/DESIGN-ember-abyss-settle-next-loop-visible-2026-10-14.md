# 余烬 · 深渊结算「下一环」菜单化（settle/evacuate 去指令化 · ≠挑战settle下一环同题 · ≠团本settle下一环同题 · ≠短征settle下一环·回看同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬掉落/体力价/层表）

| 日 | 事 |
|----|-----|
| 2026-10-14 09:33 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-DA CLOSED 后连开 · 拟 D13333 · TIP-DB |

STATUS=**待批 A** · 荐 **M** · L 不荐 · 拟 D13333 · TIP-DB · docs-only · 2026-10-14 09:33 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-abyss-settle-next-loop-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-abyss-settle-next-loop-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-abyss-settle-next-loop-visible-2026-10-14.md`](RESEARCH-ember-abyss-settle-next-loop-visible-2026-10-14.md) · backlog `B-abyss-settle-next-loop-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬深渊掉落/体力价/层表/日帽 ≠挑战settle下一环同题 ≠团本settle下一环同题 ≠短征settle下一环同题 ≠短征settle回看同题 ≠variety再拧 ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠路径追同题 ≠日差同题 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠legacy abyss evacuate 大扫**

> **一句话玩家价值：** 深渊通关后一点开「下一环」——再深渊、回选层、回冒险、去工坊、设层目标都能点；**不**抬掉落/体力价/层表、**不**教手打指令、**不**复读短征/团本/挑战同题。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-DA CLOSED `8e12b6985` · 连开 TIP-DB | 内容优先；禁已齐同题；选题钉死深渊 settle/evacuate 去指令化 |
| E2 | D13332 DESIGN §6① | 明文后置「深渊 settle / evacuate 去指令化（含 abysstarget 教指令清扫；`/ember_p1_abyss` 已有钮但仍有裸指令旁路）」；短征+团本+挑战 settle 已齐 |
| E3 | EmberSettleService 深渊 newBest ConfirmTokens | `[深渊页]/ember_p1_abyss` · `[冒险页]` · `[工坊]/corerpg p1 forge`；另教 `/corerpg p1 abysstarget` |
| E4 | EmberAbyssTarget.afterBest / cmd | ConfirmTokens→`/corerpg p1 abysstarget` / `abyss next`；文案教手打 |
| E5 | ember_p1_abyss.yml Open | `tell`+`command: corerpg p1 abysstarget` 教指令旁路 |
| E6 | TrMenu **无** `ember_p1_abyss_settle`；短征/团本/挑战 settle 范式可镜像 | TIP-CW / CZ / DA |
| E7 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E8 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 短征+团本+挑战结算下一环已可点，但深渊结算下一环仍钉在聊天裸指令轨（工坊/层目标），且选层页 Open 仍教 `abysstarget`。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 短征/团本/挑战 settle 同题 | **否** | 派单禁；已 CLOSED |
| soft-reward / 路径追 / 日差 / variety再拧 | **否** | 已齐勿再交 |
| Cast/Aid/todayRot 再拧 | **否** | 硬禁 |
| 庭内旁路 / 宝石 | **否** | 饱和 / gap 未亮 |
| legacy `/corerpg abyss evacuate` 大扫 | **否 · 本窗** | LegacyGate；本窗钉 P1 一支 |
| 只半行 | **否** | REJECT |
| **深渊结算下一环菜单化（荐）** | **采纳** | E2–E6；真可点；≠短征/团本/挑战同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼下一环：** 深渊结算（新纪录）后自动出现/可开「深渊下一环」薄页，见本层号与深渊路径标签。
2. **一点去向：** 再深渊 / 回选层 / 冒险 / 工坊 / 设层目标（可选） **全是点击**；**零**教手打 `/corerpg p1 abysstarget` / `/corerpg p1 forge` 主 UI。
3. **灰态诚实：** 体力不足·再深渊灰；层未开/层费不足诚实；未设目标时目标格引导菜单点设而非刷 chat 为主交付。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/结算半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄 settle 页（荐）** | 真开菜单；清裸指令主路径；**零改**掉落/体力价/层表 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬掉落/体力价/层表 / sx20 / 短征·团本·挑战同题 / 庭内** | — | **否决** |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a 插件改深渊 settle 下一环入口必做；W1b 薄 settle 页必做；W1c 清裸指令主路径同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗不批 L**
- [ ] 否决 A（只半行）/ W（抬表/sx20/短征·团本·挑战同题/庭内）
- [ ] 批注：

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_abyss_settle`（OpenCommand 建议 `/ember_p1_abyss_settle`） | 新页；**不**复开短征/团本/挑战 settle 同题 |
| 再深渊 | 真 `corerpg p1 abyss next` 或可开层（菜单 actions；玩家不手打） | 禁改体力价/层费/掉落/层表 |
| 回选层 | 开 `ember_p1_abyss` | 禁改十层 Layout 主交；**清**该页 Open 教 `abysstarget` 旁路 |
| 设层目标 | 菜单 actions：`corerpg p1 abysstarget <n\|next\|clear>`（薄格） | 禁教手打为主 UI；禁改目标语义 |
| 深渊路径（可选） | 菜单点 `abysspath push\|farm\|clear`（actions）或薄格 | 禁改路径语义/层费 |
| 冒险 | 开 `ember_p1_adventure` | 禁改冒险 Layout 主交 |
| 工坊 | 开 `ember_p1_forge` | 禁改 forge 价/配方 |
| settle 入口 | `EmberSettleService` 深渊 `newBest`：优先 `open` 薄页 **或** ConfirmTokens 目标改为 `/ember_p1_abyss_settle`；工坊钮同步改菜单优先 | **禁**目标仍以 `/corerpg p1 forge` / 教 `abysstarget` 为唯一主 UI |
| 文案 | glance/下一步若保留辅钮，须菜单优先；禁教手打为主路径 | 禁保留教指令主路径 |
| 短征/团本/挑战/Cast/Aid/variety | **零改** | 本债≠同题复读 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | 深渊 settle 下一环→开薄页；记住 settle 层；清裸指令主路径；可选 `abyss_settle_*` PAPI；单测；同批对齐工坊钮→`/ember_p1_forge` 或进薄页；`EmberAbyssTarget.afterBest` 主路径改菜单优先 | 不改掉落/体力价/层表 |
| **W1b · 薄 settle 页（必做 · TrMenu）** | `ember_p1_abyss_settle`：再深渊 / 回选层 / 冒险 / 工坊 / 层目标（可选）/ 路径（可选）；lore 挂层号+路径；灰态诚实；清 `ember_p1_abyss` Open 教指令 | 不改十层选层主交 / 短征·团本·挑战薄页 / 庭内 |
| **W1c · 辅指针（同批可选）** | 若需 ActionBar/半行辅指 → **不可独批** | 禁半行独档 |

### 3.3 文案示例（短 · 可感）

```
§5深渊下一环
§7本层：§f第 3 层 · 新纪录
§7深渊路径：§e冲层
§8点选 · 再深渊 / 选层 / 冒险 / 花料
```

### 3.4 经济幅度

| 项 | 钉死 | 禁 |
|----|------|-----|
| 深渊掉落 / 体力价 / 层费 / 层表 / 日帽 | **零改** | 除非总控另批（本 tip **不**明示抬） |
| 奖励 | **零** 新 NI 结算奖 | 禁借下一环发经济 |
| 短征/团本/挑战/variety/soft-reward | **不碰** | 同题勿再交 |

### 3.5 为何不是半行

主交付 = **薄 abyss settle 页真开菜单去向** + **插件改深渊 settle 下一环入口** + 清裸指令主路径（含 abysstarget）。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（施工号用 · 本号只定规格）

| # | 场景 | 期望 |
|---|------|------|
| V1 | 深渊 newBest 结算 | 下一环薄页可开；再深渊/选层/冒险/工坊真开；层目标可点设（若做） |
| V2 | 体力不足 / 层未开 | 再深渊灰/诚实；仍可选层/冒险/工坊 |
| V3 | 结算与选层页文案 | 聊天 **不以**「手打 /corerpg p1 abysstarget」为主交付；`ember_p1_abyss` Open **不**再 command 教 abysstarget；主路径为菜单 |
| V4 | 零改抽测 | 掉落/体力价/层表/Cast/Aid/variety/短征·团本·挑战薄页不变；零 sx20；零半行独档；零短征·团本·挑战同题施工 |

---

## 5. 禁区

- 半行 lore-only 独批（REJECT `d00ed84bc`）
- 短征 settle下一环·回看 / 团本 settle下一环 / 挑战 settle下一环 / variety再拧 / soft-reward / 路径追 / 日差同题复读
- 抬深渊掉落 / 体力价 / 层费 / 层表 / 日帽 / S表 / 伤表
- sx20 · Pack6 · 开R/K3 · 关观察 · densify · 灾厄/fest重开
- 庭内旁路再拧 · 教裸指令主 UI · stage 脏 runtime
- legacy deep-abyss `/corerpg abyss evacuate` 同窗大扫（须另窗；本窗钉 P1）

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手宝石（须庭面先亮 gem gap）
2. variety 完整经济 / W1e todayRot 偏置
3. fest 继任 / 灾厄枢纽可见（须总控另批）
4. sx20（仍禁，除非总控另开窗）
5. 深渊掉落/体力价/层表抬表（须总控另批更大经济）
6. 全服其余 settle 聊天钮（图身份首通 D673 / 精选路径 D794 等）逐支另窗
7. legacy AbyssSettleService evacuate / 深井撤离菜单大扫（须另窗证据）

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 09:33 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-DA CLOSED 后连开 · 选题深渊结算下一环菜单化 · 拟 D13333 · TIP-DB · ≠半行 ≠短征/团本/挑战settle同题 ≠variety再拧 ≠庭内旁路 · **改题有证据**（D13332§6①；Settle ConfirmTokens；abysstarget 教指令；无薄页；见 RESEARCH） |

*D13333 **待批 A** · 荐 **M** · L 不荐 · 深渊结算下一环菜单化 · TIP-DB · ancestor TIP-DA CLOSED `8e12b6985` · 半行 REJECT `d00ed84bc` 保持 · 2026-10-14 09:33 Asia/Shanghai*
