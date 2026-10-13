# 余烬 · 冒险「今日三冲」聚合（今日推+冲一下+本周短征 · 零抬日帽/目标数/事件率 · ≠sx20）

STATUS=**待批 A** · 荐方案 **M** · 拟 **D13313** · TIP-CH · docs-only · 2026-10-13 23:54 Asia/Shanghai · tip [`STATUS-ember-next-hard-debt-adventure-today-triple-chase-need-design-2026-10-13.md`](../status/STATUS-ember-next-hard-debt-adventure-today-triple-chase-need-design-2026-10-13.md) · RESEARCH [`RESEARCH-ember-adventure-today-triple-chase-visible-2026-10-13.md`](RESEARCH-ember-adventure-today-triple-chase-visible-2026-10-13.md) · backlog `B-adventure-today-triple-chase-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠抬 weekly_goals.targets/徽/短征日帽/event_rate/NEED ≠新地图 ≠重开 calamity ≠Pack6 ≠再叠挂机庭花料 ≠复开 D423 ≠教裸指令**

> **一句话玩家价值：** 打开冒险就能一眼看见今日推 / 冲一下 / 本周短征，一点进当前最该冲的那条——三冲从分格变成首页决策；**不**抬日帽、**不**抬周目标数、**不**抬事件率。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CG CLOSED · 连开 | 拟 D13313 · TIP-CH；合法接力三冲聚合；禁 sx20/抬表/诚实空转/L13/K3/Pack6/灾厄/第五件花料 |
| E2 | D13312§6① | 明文后置「冒险今日三冲聚合」 |
| E3 | D13309/10/12 三冲 live | short-today / event-chase / short-goal 与展示键齐 |
| E4 | 冒险 H 仅进选页；E=冲一下；无周目标格 | 首页缺聚合 |
| E5 | Layout `'#WsEb u n'` 空位 | 可落 P |
| E6 | 挂机庭四件 / Cast 三房 / 第五件硬禁 | 勿改题叠花料/Cast |

**一句话问题：** 三冲分格已齐，冒险首页仍无「一眼三行 + 一点智路由」——挂时长断在首页决策。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再叠 Open 诚实半行 | **否** | 空转硬禁 |
| 抬 short:5 / 日帽 / event_rate | **否** | 硬禁 |
| 挂机庭第五件 / sx20 / Cast 再叠 | **否** | 硬禁 / 已齐 |
| 改题其它乐趣 | **否** | 证据表 E1–E6；合法接力更可感 |
| **冒险今日三冲聚合（荐）** | **采纳** | E1–E5；薄 jar+菜单；可感 |

---

## 1. 玩家感知目标（≤3）

1. **一眼三冲：** 冒险页见「今日三冲」，lore 同时挂今日推 / 冲一下 / 本周短征状态。  
2. **一点智路由：** 点格进当前推荐冲口（优先今日推有奖 → 本周短征未满 → 房间事件）；门禁/体力同该本。  
3. **零抬经济：** 不改日帽、short:5、徽、event_rate/NEED、AFK 表；不撤 H/E/F。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 Open 半行复读三冲名** | 口号 | **否决独批** — 仍无冲口 |
| **M · today_triple 键 + 冒险 P 格智路由进本（荐）** | jar `today_triple*` + 菜单 P 真 enter + lore 三行 + Open 半行 · **零改**日帽/目标数/事件率 | **荐** — 债清；薄可施工 |
| **L · 改交挂机庭第五件 / Cast 打磨 / 抬表** | 换题或动表 | **否决独批** — 硬禁或非本窗 |
| **W · 诚实空转 / 假写固定 sx01 / 无键硬编码 / 撤 H·E** | — | **否决** — 假写或不薄 |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a today_triple 必做；W1b 冒险 P 格必做；W1c Open 半行同批；见 §3）
- [ ] 或勾 **方案 L**（本窗改题）· **本窗不荐 L**
- [ ] 否决 A（只口号）/ W（假写/空转/撤分格）
- [ ] 批注：

---

## 3. 可落地（方案 M）

### 3.1 智路由钉死（只读 · 零经济）

| 项 | 口径 | 纪律 |
|----|------|------|
| 优先级 | ① `short-today`：今日推目标已解锁且今日仍有有奖次数；② `short-goal`：`goal_short_done≠1` 且 `goal_short_chase_ok=1`；③ `event-chase`：`event_chase_ok=1`；④ 空 | **禁**指未解锁；**禁**第二奖励轨 |
| 键 | `%corerpg_p1_today_triple%`（解析后的 enter 别名或 mapId）/ `_name` / `_kind`（today|week|event|empty）/ `_ok`（非空→1） | 只读；禁改 targets/rate |
| 进本 | `corerpg p1 enter today-triple`（内部转到 short-today / short-goal / event-chase） | **同**现网体力/解锁/日帽 |
| 展示三行 | **复用** `sx_today_name`+day_line · `event_chase_name`+soft_hope · `goal_short`+chase_name | 禁另造经济文案 |
| 奖励 | **不变** | 禁抬徽/short:5/日帽/event_core |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · today_triple PAPI + enter（必做 · 插件）** | `EmberRunPapi` 增 today_triple/_name/_kind/_ok；`EmberRunService` 增 `today-triple` 解析；单测：优先今日推有奖、次回周目标、再事件、全空、边界 | 不改 addGoal / SoftPity / targets / event_rate |
| **W1b · 冒险 P 格（必做 · TrMenu）** | Layout `'#WsEb u n'`→`'#WsEbPu n'`（或等价空位，保留 W/s/E/b/u/n）；名 `§e今日三冲`；lore 三行 + 推荐名 +「左键：冲推荐」；`_ok=0` / 体力不足灰态；actions：`enter today-triple` | **不撤** H 短征 / E 冲一下 / 短征选页 D·F |
| **W1c · Open 半行（必做同批）** | Open +「今日三冲见本页 P」；可保留冲一下 E 指针 | 不盖 mapid_board / drop_q_hope |
| **W1d · 分格强化（本窗不做）** | — | 不改 H/E/F 主语义为独交付 |
| **W1e · 经济/地图（默认不做）** | — | 禁动日帽/S 表/MM/地图 |

### 3.3 玩家路径

| 步 | 路径 | 说明 |
|----|------|------|
| 看 | 开冒险 | 见今日三冲三行 + 推荐 |
| 进 | 点「今日三冲」 | 智路由真 enter；门禁同该本 |
| 仍可 | 点 H / E / 进选页 D·F | 分格保留；聚合=加枢纽 |
| 空 | 三冲皆不可用 | 格灰「暂无推荐」；不教指令 |

**文案钉死示例（施工可微调色码，语义不得漂）：**

```
§e今日三冲
§7今日推：§f%corerpg_p1_sx_today_name% §7· %corerpg_p1_sx_today_day_line%
§7冲一下：§f%corerpg_p1_event_chase_name%
§7本周短征：§f%corerpg_p1_goal_short% §8· %corerpg_p1_goal_short_chase_name%
§e推荐：§f%corerpg_p1_today_triple_name%
§e➥ §f左键：冲推荐
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13309 今日推 | **保留**选页 D；本窗=冒险聚合入口 |
| D13310 冲一下 | **保留** E；P 不替代 E |
| D13312 本周短征可冲 | **保留**选页 F；P 不替代 F |
| D13304–08 / Cast 三房 | **不叠** |
| fest / calamity | **本窗不做** |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 冒险页见 P 格；lore 含三冲行 + 推荐名；点格进本成功（门禁同推荐本） |
| V2 | 智路由优先序：今日推有奖 → 周目标未满可冲 → 事件可冲；全空/体力不足灰态诚实 |
| V3 | H / E / 短征选页 D·F 与施工前一致（不撤分格） |
| V4 | `git diff`：无改 `weekly_goals.targets` / 徽表 / `daily_kills` / `afk.tiers` / `event_rate` / NEED / UpgradeRules；无新增 sx20 |
| V5 | 玩家面无裸指令；Open 半行可见 P 指针 |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · 只半行无进本  
- 抬 `short:5` / 日帽 / event_rate / NEED · 把 short 改必做 GOALS  
- 再叠挂机庭花料第五件 · 复开 D423 · stage 脏 runtime · 教裸指令 · 撤 H/E/F 当分格  

---

## 6. 后置（明文 · 本窗不做）

1. R1 远程轻 Cast / GuardCast 粒子二次打磨  
2. fest 继任 / 灾厄枢纽可见（须总控另批）  
3. sx20（仍禁，除非总控另开窗）  
4. 挂机庭资源环新乐趣（非第五件花料复读；须另证真债）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-13 | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CG 后连开 · 选题冒险今日三冲聚合 · 拟 D13313 · ≠sx20 ≠抬表 ≠第五件花料 ≠复开 D423 · **未改题**（证据表见 RESEARCH） |

*拟 D13313 · 冒险今日三冲聚合 · 待批 A · 荐方案 M · ≠关观察 ≠抬日表 ≠sx20。*
