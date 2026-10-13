# 余烬 · 短征「今日推」本局内可感（短本日更花样续 · ≠Cast/Aid复读 · ≠半行 · ≠庭内镜像 · ≠sx20 · ≠抬表）

STATUS=**待批 A** · 荐 **方案 M** · 拟 D13320 · TIP-CO · docs-only · 2026-10-14 01:58 Asia/Shanghai
> tip [`STATUS-ember-next-hard-debt-short-today-inrun-feel-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-short-today-inrun-feel-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-today-inrun-feel-readable-2026-10-14.md`](RESEARCH-ember-short-today-inrun-feel-readable-2026-10-14.md) · backlog `B-short-today-inrun-feel-readable` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠EliteAid/BossAid再拧 ≠第五件花料 ≠推荐花复读 ≠P格兼确认 ≠庭内再叠顺手 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠灾厄重开 ≠Pack6**

> **一句话玩家价值：** 进「今日推」短征本时，开局/开房/结算会明确告诉你「这是今日推」——日更轮换从菜单走进房内；**不**开 sx20、**不**抬日表、**不**再叠 Cast/Aid、**不**叠庭内顺手。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CN CLOSED `931274777` · 连开 TIP-CO | 首选短征日更花样；须真可点/房内可感；禁半行/Cast·Aid打磨主交/庭内再叠；Cast+Aid 已齐勿复读 |
| E2 | D13309 今日推菜单 live · `sx_today*` | 菜单齐；门内缺口 |
| E3 | `EmberMapEnterFeel` 短征一律「短征」 | 进本无今日分支 |
| E4 | Session/短征 settle 无 todayRot | 局内/结算无今日反馈 |
| E5 | Cast+BossAid+EliteAid 全线 live | 禁 Aid/Cast 再拧 |
| E6 | 挂机庭/导航链已齐 | 勿回资源环 |
| E7 | 半行 REJECT `d00ed84bc` 保持否决 | backlog |
| E8 | **为何不是半行** | 主交付=真 session 分支；见 §3.5 |

**一句话问题：** 今日推停在选页——进本后与随手短征无异，日更花样断在门内。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再交选页半行 / 诚实口号 | **否** | REJECT 保持 |
| Cast 粒子打磨 / Aid 再拧 | **否** | 硬禁；刚 CLOSED |
| 庭内再叠顺手 | **否** | 派单禁；无新硬债 |
| 只 Open「进本有标记」半行 | **否** | 半行空转 |
| 导航三冲/周目标复读 | **否** | 已 CLOSED |
| **今日推本局内可感（荐）** | **采纳** | E2–E4；真 session；补日更门内；≠Aid/Cast/庭内 |

---

## 1. 玩家感知目标（≤3）

1. **进本即知：** 进今日推本见 title/AB/tell「今日推·本」——与非今日本不同。  
2. **房内持续：** 开房有轻量「今日推 · 第N房」提示（可节流；不刷屏）。  
3. **结算收口：** 有奖通关后见「今日推完成」+ 可点「再进今日推」；**不**抬日帽/掉落。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改选页/Open「进本有今日标记」半行** | 口号 | **否决独批** — 无局内动作=诚实空转 |
| **M · 进本 todayRot + 分房 AB + 结算完成（荐）** | session 分支 + enter cue + 开房提示 + 结算 tell/按钮；**零改**日帽/S表/Cast/Aid | **荐** — 真房内日更可感；≠半行；≠Aid/Cast 复读 |
| **L · 仅 W1a 进本 cue；无分房/结算** | 减面 | 可批但弱于全日更弧；**本窗荐全弧** |
| **W · 抬日帽/掉落/event_core / sx20 / 半行独批 / Cast·Aid打磨 / 庭内再叠** | — | **否决** — 硬禁 |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a 进本 todayRot 必做；W1b 分房提示必做；W1c 结算完成必做；W1d 选页半行可选辅；见 §3）
- [ ] 或勾 **方案 L**（仅进本 cue；分房/结算后置）· **本窗默认不批 L**
- [ ] 否决 A（只半行）/ W（抬表/sx20/打磨主交付/Aid再拧/庭内）
- [ ] 批注：…

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 判定 | `m.shortExpedition && m.key.equalsIgnoreCase(EmberShortRules.todayKey())` | 禁误标非今日本；禁改 todayKey 算法 |
| session | 布尔 `todayRot`（或等价字段）进本写入、结算可读 | 禁持久账本抬奖励 |
| 进本 cue | title + ActionBar + tell「今日推·本」（可叠 `EmberMapEnterFeel` 短征行） | 禁改体力扣费 |
| 分房 | 开门/进房 AB「今日推 · 第N房」；同房节流 ≥15s | 禁每 tick 刷屏 |
| 结算 | 有奖 settle 追加「今日推完成」；chat `[再进今日推]` → `enter short-today` | **零改** S 表数额；满态诚实 |
| Cast / Aid | **零改** | 本债≠打磨/再拧 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 进本 todayRot（必做 · 插件）** | enter commit 写 todayRot + title/AB/tell | 不改日帽/体力/MM |
| **W1b · 分房提示（必做 · 插件）** | 开门/进房 AB；节流 | 不改房间逻辑/刷怪 |
| **W1c · 结算完成（必做 · 插件）** | 有奖 tell + 再进按钮 | 不改 S40–S58 数额 |
| **W1d · 选页半行（可选辅）** | D lore「进本有今日标记」 | **不可独批**；半行 REJECT 保持 |

### 3.3 文案示例（短 · 可感）

```
§6【今日推】§f入场 · §7本局有今日标记
§6今日推 · 第2房
§a今日推完成 §7· 日更本已通
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13309 今日推菜单 | **保留**；本债=**门内可感**，≠菜单复读 |
| D13313 三冲 | **保留**；本债≠再聚合导航 |
| D13318/19 Aid | **保留不动**；≠添头复读 |
| 挂机庭环 | **不抢**；本窗短征日更 |
| 半行 REJECT | **保持否决** |

### 3.5 为何不是半行（验收必读）

| 半行空转（禁） | 本窗 M（过） |
|----------------|--------------|
| 只改 lore/Open 口号 | **session 分支 + 进本/开房/结算真反馈** |
| 玩家无新局内结果 | 今日本 vs 非今日本 **体感不同** |
| 对位 sure/pity/observe | 对位 D13309 菜单→门内兑现 |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 进 `todayKey` 本：见今日推 title/AB/tell；进非今日本：无该标记 |
| V2 | todayRot 局内开第 2 房见分房 AB（或等价）；非 todayRot 无 |
| V3 | todayRot 有奖结算见「今日推完成」+ 再进按钮可用；满态诚实 |
| V4 | `git diff`：无改日帽/`afk.tiers`/伤乘/S 表数额、无 sx20、无半行独档、无 Cast/Aid yml、无庭内菜单 |
| V5 | 玩家面无裸指令（按钮走既有 `enter short-today`） |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · **只半行无局内分支**  
- **Cast 粒子纯打磨当主交付** · **EliteAid/BossAid 再拧** · 第二 Cast  
- **第五件花料** · **推荐花复读** · **P 格兼确认** · 庭内再叠顺手  
- 抬 event_rate/event_core · 新 variety kind  
- 复开 D423 · stage 脏 runtime · 教裸指令  

---

## 6. 后置（明文 · 本窗不做）

1. 选页/Open「今日推有标记」半行独批（须另证且过半行硬禁另议）  
2. 短征轻量 variety 接入（零抬 event_core 另窗）  
3. Cast 粒子二次打磨 / Aid 再拧  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 01:58 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CN CLOSED 后连开 · 选题短征今日推本局内可感 · 拟 D13320 · TIP-CO · ≠半行 ≠Cast/Aid复读 ≠庭内再叠 · **改题有证据**（D13309 菜单齐、进本无 today 分支；D13319§6 不可主交；见 RESEARCH） |

*D13320 **待批 A** · 荐方案 M · 短征今日推本局内可感 · TIP-CO。≠半行 ≠Cast/Aid复读 ≠sx20。*
