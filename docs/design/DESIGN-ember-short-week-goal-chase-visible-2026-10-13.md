# 余烬 · 短征选页「本周短征可冲」（周目标导航可感 · 零抬目标数/日帽 · ≠sx20）

STATUS=**待批 A · 荐方案 M · 拟 D13312** · 2026-10-13 23:24 Asia/Shanghai · tip [`STATUS-ember-next-hard-debt-short-week-goal-chase-need-design-2026-10-13.md`](../status/STATUS-ember-next-hard-debt-short-week-goal-chase-need-design-2026-10-13.md) · RESEARCH [`RESEARCH-ember-short-week-goal-chase-visible-2026-10-13.md`](RESEARCH-ember-short-week-goal-chase-visible-2026-10-13.md) · backlog `B-short-week-goal-chase-visible` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠抬 weekly_goals.targets/徽/短征日帽 ≠新地图 ≠重开 calamity ≠Pack6 ≠再叠挂机庭花料 ≠复开 D423 ≠教裸指令**

> **一句话玩家价值：** 打开短征选本就能看见本周短征周目标进度，一点进「还能计有奖」的本——周目标从赛季页挪到选本动作；**不**抬目标数、**不**抬日帽。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CF CLOSED · 连开 | 挂机庭/短征/冒险可感；禁 sx20/抬表/诚实空转/L13/K3/Pack6/灾厄/第五件花料 |
| E2 | D401 `goal_short` + short:5 live | 进度有、选页无冲口 |
| E3 | 短征 Open/I 只指赛季；赛季 6→短征 | 反向入口齐，正向冲口缺 |
| E4 | Layout `'#PRSJD  #'` D 后空位 | 可落 F（对齐今日推落 D） |
| E5 | short-today / event-chase 范式 | 薄别名可复用 |
| E6 | Cast 三房 / 今日推 / 冲一下 / 挂机庭四件已齐 | 勿再叠 |
| E7 | D13311§6 后置①「周目标选页可冲」 | 合法接力 |

**一句话问题：** 可选周目标·短征已计数，选本页仍无「一点可冲」——周目标挂时长断在导航。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再叠 Open/I 诚实半行 | **否** | 空转硬禁 |
| 抬 short:5 / 徽 / 日帽 | **否** | 硬禁 |
| 冒险今日三冲本窗双开 | **后置** | 分格已齐；聚合下窗 |
| 挂机庭第五件 / sx20 / Cast 再叠 | **否** | 硬禁 / 已齐 |
| **本周短征可冲（荐）** | **采纳** | E2–E7；薄 jar+菜单；可感 |

---

## 1. 玩家感知目标（≤3）

1. **一眼进度：** 短征选页见「本周短征可冲」与 `%goal_short%`。  
2. **一点进本：** 点格进冲图标（优先今日仍有有奖次数的已解锁短征）；门禁/体力同该本。  
3. **零抬经济：** 不改 short:5、徽、日帽、S40–S58、体力。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 Open/I 半行复读 goal_short** | 口号 | **否决独批** — 仍无冲口 |
| **M · chase 键 + 选页 F 格进本（荐）** | jar `goal_short_chase*` + 菜单 F 真 enter + Open 半行 · **零改**目标数/日帽 | **荐** — 债清；薄可施工 |
| **L · 本窗改交冒险三冲聚合 / 抬 short:5** | 换题或动表 | **否决独批** — 三冲后置；抬表硬禁 |
| **W · 诚实空转 / 假写固定 sx01 不验解锁 / 无键硬编码** | — | **否决** — 假写或不薄 |

### 批 A 勾选（总控填）

- [ ] **方案 M**（W1a chase 必做；W1b 选页 F 必做；W1c Open 半行同批；见 §3）
- [ ] 或勾 **方案 L**（本窗改交冒险三冲；周目标可冲后置）· **本窗不荐 L**
- [ ] 否决 A（只口号）/ W（假写/空转）
- [ ] 批注：____________________

---

## 3. 可落地（方案 M）

### 3.1 冲图钉死（只读 · 零经济）

| 项 | 口径 | 纪律 |
|----|------|------|
| 冲图标 | 已解锁短征 `sx01…sx19` 中：优先 `day_left>0`（今日仍可有奖）；同档内稳定序；可选若 `sx_today` 满足则优先之 | **禁**指未解锁；**禁**第二奖励轨 |
| 键 | `%corerpg_p1_goal_short_chase%` / `_name` / `_day_line`；`%corerpg_p1_goal_short_done%`（进度≥target→1）；复用 `%corerpg_p1_goal_short%` 展示行 | done **只读**；禁改 targets |
| 进本 | `corerpg p1 enter short-goal`（或 `goal-short-chase` / chase 键） | **同**现网体力/解锁/日帽 |
| 周目标计数 | **仍** D401：有奖通关 `addGoal("short",1)`；第 4+ 日奖/失败不计 | **禁**另开计数轨 |
| 奖励 | **不变** | 禁抬徽 / short:5 / 日帽 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · chase PAPI + enter 别名（必做 · 插件）** | `EmberRunPapi`（或并列）增 chase/name/day_line/done；`EmberRunService` 增 `short-goal` 解析；单测：有 day_left 优先、全满回落可进练习本、未解锁跳过、done 边界 | 不改 addGoal / targets / SoftPity |
| **W1b · 选页 F 格（必做 · TrMenu）** | Layout `'#PRSJD  #'`→`'#PRSJDF #'`（或等价空位）；名 `§e本周短征可冲`；lore：`goal_short` + chase_name + day_line +「有奖通关计入 · 可选 · 不算全部完成」；done=1 或无 chase / 体力不足灰态；actions：`enter short-goal` | 不改十九本格 / D 今日推 / B 去哪花 / Z 返回 |
| **W1c · Open 半行（必做同批）** | Open +「本周短征可冲见本页 F」；可保留一句 goal_short | 不盖今日推 / D409 满态工坊 / 仓差 |
| **W1d · 赛季 lore 诚实（可选）** | 赛季格 6 lore「五本 sx01–sx05」→「任一短征有奖通关计入」（对齐现网钩） | **非主交付**；禁改 targets |
| **W1e · 冒险三冲聚合（本窗不做）** | — | 后置 |

### 3.3 玩家路径

| 步 | 路径 | 说明 |
|----|------|------|
| 看 | 开短征选本 | 见本周进度 + 冲图名 |
| 进 | 点「本周短征可冲」 | 真 enter；门禁同该本 |
| 计 | 有奖通关 | D401 钩 +1；展示 goal_short 更新 |
| 满 | 进度达 5 | 格灰「本周可选已满」；仍可点下方十九本 / 今日推 |
| 自选 | 仍可点各本 / 今日推 | 本格=周目标导航，不锁其它 |

**文案钉死示例（施工可微调色码，语义不得漂）：**

```
§e本周短征可冲
§f%corerpg_p1_goal_short%
§7冲图：§f%corerpg_p1_goal_short_chase_name% §7· %corerpg_p1_goal_short_chase_day_line%
§8有奖通关计入 · 可选 · 不算全部完成
§e➥ §f左键：进本
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D401 goal_short | **保留**计数与徽；本窗只加冲口 |
| D13309 今日推 | **不抢**；今日推=日轮换，本格=周目标 |
| D13310 冲一下 | **不抢**；事件窗在冒险页 |
| D13304–08 / Cast 三房 | **不叠** |
| fest / calamity | **本窗不做** |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 短征选页见 F 格；lore 含 goal_short + 冲图名；点格进本成功（门禁同该本） |
| V2 | 冲图标优先 day_left>0 已解锁短征；无解锁/体力不足/done 灰态诚实 |
| V3 | 有奖通关后 goal_short 进度 +1（未满时）；第 4+ 日奖不计（沿 D401） |
| V4 | `git diff`：无改 `weekly_goals.targets` / 徽表 / `daily_kills` / `afk.tiers` / UpgradeRules；无新增 sx20 |
| V5 | 今日推 D / 十九本 / 满态去工坊链路与施工前一致；玩家面无裸指令 |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · 只半行无进本  
- 抬 `short:5` / 把 short 改必做 GOALS · 抬日帽/S 表  
- 再叠挂机庭花料第五件 · 复开 D423 · stage 脏 runtime · 教裸指令  

---

## 6. 后置（明文 · 本窗不做）

1. 冒险「今日三冲」聚合格（今日推 + 冲一下 + 本周短征）  
2. R1 远程轻 Cast / GuardCast 粒子二次打磨  
3. fest 继任 / 灾厄枢纽可见（须总控另批）  
4. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-13 | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CF 后连开 · 选题短征选页本周短征可冲 · 拟 D13312 · ≠sx20 ≠抬目标数 ≠第五件花料 ≠复开 D423 |

*拟 D13312 · 短征选页本周短征可冲 · 待批 A·方案 M · ≠关观察 ≠抬日表 ≠sx20。*
