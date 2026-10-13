# 余烬 · 短征结算「下一环」菜单化（settle 聊天钮去指令化 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠庭内旁路 · ≠sx20 · ≠抬表）
| 2026-10-14 04:36 Asia/Shanghai | 初稿 STATUS=**待批 A** · 荐 M · TIP-CV CLOSED 后连开 · 拟 D13328 · TIP-CW |

STATUS=**待批 A** · 荐 **M** · 拟 D13328 · TIP-CW · 2026-10-14 04:36 Asia/Shanghai · **仅 docs**
> tip [`STATUS-ember-next-hard-debt-short-settle-next-loop-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-short-settle-next-loop-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-settle-next-loop-visible-2026-10-14.md`](RESEARCH-ember-short-settle-next-loop-visible-2026-10-14.md) · backlog `B-short-settle-next-loop-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠todayRot/W1e独批 ≠抬 event_core ≠新 variety kind ≠完整 variety 经济 ≠第五件花料 ≠推荐花复读 ≠洗练/烙纹/转化/随机锻再拧 ≠庭内旁路再拧 ≠今日推复读 ≠本周短征可冲复读 ≠日差同题 ≠路径追同题 ≠D406 首通同屏复读 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠灾厄/fest重开 ≠Pack6 ≠densify ≠改 ShortPath 算法权重/日帽**

> **一句话玩家价值：** 短征通关后一点开「下一环」——再进本图、回选页、改路径追、去工坊都能点；**不**再交路径追同题、**不**抬日帽、**不**教手打指令。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CV CLOSED `f3defd0bb` · 连开 TIP-CW | 强烈偏向短征日更/乐趣；庭内默认勿再交；**路径追同题勿再交**；禁半行/Cast·Aid·variety再拧 |
| E2 | D13327§6⑥ | 明文后置 settle 去指令化 |
| E3 | ShortService settle ConfirmTokens | `/corerpg p1 enter` / `forge` / `shortpath` |
| E4 | 裸指令文案 + spendChase forge | ShortService L160 · ShortRules L228 |
| E5 | 路径追菜单已齐；结算未接 | D13327 · ShortService |
| E6 | 无 settle 薄页；todayRot settleCue chat | TrMenu · TodayRotFeel |
| E7 | **为何不是半行** | 主交付=薄页+改入口；见 §3.5 |
| E8 | 半行 REJECT `d00ed84bc` 保持 | backlog |

**一句话问题：** 路径追已可点，但通关结算下一环仍钉在聊天裸指令轨。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 路径追同题 / 改 resolve | **否** | 派单禁；已 CLOSED |
| 日差同题 / 软目标发奖 | **否** | 派单禁；经济另批 |
| 今日推 / 周目标 / 三冲 / D406 复读 | **否** | 已齐 |
| Cast/Aid/variety/todayRot 再拧 | **否** | 硬禁 |
| 庭内旁路 / 宝石 | **否** | 饱和 / gap 未亮 |
| 只半行 | **否** | REJECT |
| **结算下一环菜单化（荐）** | **采纳** | E2–E6；真可点；≠路径追同题 |

---

## 1. 玩家感知目标（≤3）

1. **一眼下一环：** 有奖通关后自动出现/可开「短征下一环」薄页，见再进剩余次数、当前路径标签。  
2. **一点去向：** 再进本图 / 回选页 / 路径追 / 工坊 **全是点击**；**零**教手打 `/corerpg p1 shortpath` / `forge`。  
3. **灰态诚实：** 日奖满·再进灰；体力不足灰；路径未选时路径格引导开路径页而非刷 chat。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 Open/结算半行口号** | 口号 | **否决独批** — 仍空转 |
| **M · 插件改入口 + 薄 settle 页（荐）** | 真开菜单；清裸指令文案；**零改**日帽/S/算法 | **荐** — 真可点乐趣 |
| **L · 仅把一条聊天钮改 `/ember_p1_*`；无薄页聚合** | 减面 | **默认不批 L** |
| **W · 抬日帽/S/targets / sx20 / 路径追同题 / 日差同题 / 庭内 / 改 resolve** | — | **否决** |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a 插件改 settle 下一环入口必做；W1b 薄 settle 页必做；W1c 清裸指令文案同批；见 §3）
- [ ] 或勾 **方案 L**（仅改单钮命令；薄页后置）· **本窗默认不批 L**
- [ ] 否决 A（只半行）/ W（抬表/sx20/路径追同题/日差同题/庭内）
- [ ] 批注：

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 薄页 id | `ember_p1_short_settle`（TrMenu OpenCommand 建议 `/ember_p1_short_settle`） | 新页；**不**复开路径追同题施工 |
| 再进 | 真 `corerpg p1 enter <currentMap>`（菜单 actions；玩家不手打） | 禁改日帽/体力价 |
| 回选页 | 开 `ember_p1_short` | 禁改十九本格 |
| 路径追 | 开 **已有** `ember_p1_short_path`（D13327） | **禁**再交路径追同题 / 改 resolve |
| 工坊 | 开 `ember_p1_forge`（或现网 forge 菜单 OpenCommand） | 禁改 forge 价/配方 |
| settle 入口 | 有奖 settle 后：优先 `open` 薄页 **或** ConfirmTokens 目标改为 `/ember_p1_short_settle`（一点开薄页） | **禁**目标仍为 `/corerpg p1 shortpath` / 教裸打 |
| 文案 | 删除/改写「可改 /corerpg p1 shortpath day」类；`spendChaseButtonCmd`→菜单开 | 禁保留教指令主路径 |
| todayRot settleCue | 同批：按钮改菜单再进或保持 `enter short-today`（别名可点，不教手打）；**禁**新半行独档 | ≠todayRot/W1e 独批再拧 |
| Cast/Aid/variety/日差/路径算法 | **零改** | 本债≠打磨/同题 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 插件冲钉（必做）** | settle 有奖下一环→开薄页/菜单命令；清裸指令文案；`spendChaseButtonCmd` 改菜单；单测 | 不改日帽/S/targets/resolve/Cast/Aid/variety |
| **W1b · 薄 settle 页（必做 · TrMenu）** | `ember_p1_short_settle`：再进 / 回选页 / 路径追 / 工坊；lore 挂日奖剩余+路径 label；灰态诚实 | 不改选页 Layout / 路径薄页内部 / 庭内 |
| **W1c · todayRot settleCue 对齐（同批）** | 结算今日推钮不教手打；可进薄页或 enter 别名 | **不可** todayRot 独批打磨；**不可**半行独档 |

### 3.3 文案示例（短 · 可感）

```
§e短征下一环
§7本日本图还可领：§a2§7/3
§7路径：§e有奖追
§8点选 · 再进 / 改追 / 花料
```

再进格：

```
§a再进本图
§7本日有奖剩余 §a2
§8点进 · 真进本
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| 路径追薄页 `0` | 选页主 UI 已齐；**本债=结算接菜单**，≠再交路径追同题 |
| 今日推 D | 单本轮换；settleCue 仅对齐去指令，≠再拧 todayRot cue |
| 本周短征 F | 周计数；本债≠抬 targets |
| 日差 L | **本窗不做同题** |
| 庭内旁路 | **不交** |
| variety 结算回看 | 后置；本窗不做 |

### 3.5 为何不是半行

主交付 = **薄 settle 页真开菜单去向** + **插件改 settle 下一环入口** + 清裸指令文案。任何半行仅同批辅指针；**不可**半行独档过批（REJECT `d00ed84bc` 保持）。

---

## 4. 验收（施工号用 · 本号只定规格）

| # | 场景 | 期望 |
|---|------|------|
| V1 | 有奖通关且日奖未满 | 下一环薄页可开；再进可进本图；路径/工坊开已有菜单 |
| V2 | 日奖已满 | 再进灰/诚实；仍可回选页/路径/工坊 |
| V3 | 结算文案 | 聊天 **无**「可改 /corerpg p1 shortpath …」教手打；路径钮不刷 chat-only shortpath |
| V4 | 零改抽测 | 日帽/S/targets/Cast/Aid/variety/日差 L/路径追 resolve 不变；零 sx20；零半行独档；零路径追同题施工 |

---

## 5. 禁区

- 半行独批 · 路径追同题 · 日差同题 · 今日推/周目标/三冲/D406 复读主交  
- Cast 粒子主交付 · Aid/variety再拧 · todayRot/W1e独批  
- 庭内旁路再拧 · 宝石（须庭面 gap）· densify  
- sx20 · 抬日帽/S/targets · Pack6 · 开R/K3 · 关观察 · 灾厄/fest重开 · 教裸指令 · stage脏runtime  
- 改 ShortPath resolve 排序/权重当「优化」主交付  

---

## 6. 后置（明文 · 本窗不做）

1. 日差软目标发奖 / 写入 `weekly_goals`（须总控另批经济）  
2. 庭内顺手宝石（须庭面先亮 gem gap）  
3. 短征花样结算回看 / variety 经济发核心 / W1e  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  
6. 全服 settle（团本/挑战/深渊）聊天钮去指令化大扫（可另窗；本窗只钉短征有奖 settle）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 04:36 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CV CLOSED 后连开 · 选题短征结算下一环菜单化 · 拟 D13328 · TIP-CW · ≠半行 ≠路径追同题 ≠日差同题 ≠今日推/周目标复读 ≠Cast/Aid/variety再拧 ≠庭内旁路 · **改题有证据**（D13327§6⑥；ShortService settle 裸指令轨；路径追菜单齐未接结算；见 RESEARCH） |

*D13328 **待批 A** · 荐 M · 短征结算下一环菜单化 · TIP-CW。≠半行 ≠路径追同题 ≠日差同题 ≠今日推/周目标复读 ≠Cast/Aid/variety再拧 ≠庭内旁路 · ≠sx20 · ≠改 resolve 算法。**仅 docs**。*
