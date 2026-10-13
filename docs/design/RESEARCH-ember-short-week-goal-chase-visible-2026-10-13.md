# RESEARCH · 短征选页「本周短征可冲」（周目标导航可感 · 零抬目标数/日帽）· 2026-10-13

> **状态：** 待批 A · 荐方案 M · TIP-CG / 拟 **D13312** · docs-only · 2026-10-13 23:24 Asia/Shanghai  
> **起因：** 总控【TIP-CF CLOSED】短征 R1 GuardCast live · tip `1c90911ef` · PASS `9322cf4bc`（sx01/07/13/19 · 0.32<0.52<0.85）。下一波交薄 tip：挂机庭/短征/冒险可感；禁 sx20/抬表/诚实空转/L13/K3/Pack6/灾厄重开/**第五件花料**。  
> **上游：** TIP-CF CLOSED · D13311 R1 GuardCast · D13305 EliteCast · BossCast 齐 · D13309 今日推 · D13310 冲一下 · D13304–08 挂机庭四件齐 · D401 `goal_short` live · fest/灾厄仍后置  
> **硬禁：** **零** sx20 · **零** L13 施工 · **零** K3 · **零** Pack6/天赋灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/伤乘/`skill_mult` · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实/observe 空转 · **零** 假开 `gate_daily` · **零** 重开 calamity join · **零** 新地图硬开 · **零** 再叠挂机庭花料第五件 · **零抬** `weekly_goals.targets.short` / 徽奖励

---

## 0. 一句话

D401 已有「可选周目标·短征」进度键，但**短征选页只有 Open tell 指赛季**——没有「一眼进度 + 一点进可计本」；今日推/房间冲已齐，**周目标动作断在选本页**。

---

## 1. 选题扫描（TIP-CF 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **短征选页「本周短征可冲」** | **采纳** | D13311§6 明文后置①；goal_short live 无冲口；Layout 末行空位可落 F；对齐今日推/冲一下范式；薄 jar+TrMenu |
| 冒险「今日三冲」聚合格 | **后置备选** | 今日推+冲一下已分格；聚合偏导航综述，可下窗 |
| R1 远程轻 Cast / 粒子打磨 | **后置** | 防刷屏；Cast 三房已齐，非债清主链 |
| 挂机庭第五件花料 | **否** | 四件齐；总控硬禁 |
| 新 fest / 重开灾厄 | **否** | 硬禁/政策后置 |
| 菜单诚实 / sure·pity·soft | **否** | 硬禁 |
| sx20 / 抬日表 / 抬周目标数 | **否** | 硬禁 |

**择优结论：** 房内花样（Cast）已齐；下一可感挂时长债在 **选本页周目标动作**——把已 live 的 `goal_short` 从「赛季页可看」变成选页一点可冲。故 **写死荐 M = 短征选页本周短征可冲**。

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `%corerpg_p1_goal_short%` live；`weekly_goals.targets.short:5`；有奖通关 `addGoal("short",1)` | D401 · `ember-v1-runs.yml` · `EmberShortService` | 进度与计数已齐；**禁**抬 5 / 徽 |
| E2 | 短征 Open 仅 tell「赛季页可看」；I 半行同；**无**可点周目标冲格 | `ember_p1_short.yml` | 见得到，冲不到 |
| E3 | 赛季格 6 → `menu: ember_p1_short`（反向入口）；选页无对称冲口 | `ember_p1_season.yml` | 债在选页动作，非再写赛季 |
| E4 | Layout 末行 `'#PRSJD  #'` 在 D 后仍有空位可落 **F** | 菜单 | 薄可施工（对齐 D13309 落 D） |
| E5 | `enter short-today` / `event-chase` 范式 live | `EmberRunService` · D13309/10 | 可再薄别名 `short-goal` |
| E6 | 今日推 D / 冲一下 E / Guard·Elite·Boss Cast / 挂机庭四件已齐 | D13304–11 | 勿再叠花料/Cast/空转 |
| E7 | 赛季 lore 仍写「sx01–sx05」；现网任一短征有奖通关均可计 short | season.yml vs ShortService | 可选顺手改文案诚实（非主交付抬表） |

**一句话缺口：** 知道本周短征周目标，仍要自己翻十九本猜哪本还有日奖可计——周目标乐趣断在一键。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **薄 jar：** `%corerpg_p1_goal_short_chase%` = 冲图标（已解锁短征中优先「今日仍有有奖次数」者，稳定序 sx01…sx19；可选优先对齐 `sx_today` 若其 day_left>0）；`_name` / `_day_line`；`goal_short_done`（进度≥5→1）只读  
2. **进本：** `corerpg p1 enter short-goal`（或 `goal-short-chase`）· **同**现网解锁/体力30/日帽；**不**第二奖励轨；周目标计数仍走 D401 有奖钩  
3. **TrMenu：** 短征空位 **F**「本周短征可冲」· lore 挂 `goal_short` + chase_name + day_line +「有奖通关计入 · 可选 · 不算全部完成」· 未完成亮态可进 · 已满/无解锁/体力不足灰态诚实  
4. **Open 半行（同批薄）：** 「本周短征可冲见本页 F」；可保留一句 goal_short；不盖今日推/仓差/满态工坊  
5. **施工岗：** 批后 **余烬-插件（薄 chase PAPI + enter 别名）** + **余烬-物品（TrMenu）**；不派怪物/地图

---

## 4. 明确不做

- 抬 `weekly_goals.targets.short` / 徽 / bonus · 把 short 塞进必做 GOALS  
- sx20 · 新地图 · 抬 AFK 日表/tiers · 抬短征日帽/S40–S58  
- 只加 Open/I 半行无进本（诚实空转）· 菜单 sure/pity 半行再叠  
- 挂机庭花料第五件 · Pack6 / 天赋 / 灰印 / R / L13 / K3 · 重开 calamity · 新 fest · 复开 D423 · 教裸指令 · stage 脏 runtime  
- 本窗双开冒险「今日三冲」聚合（后置）· R1 远程再挂 Cast  

## STATUS: 待批 A · 荐方案 M · 拟 D13312 · TIP-CG · 2026-10-13 23:24 Asia/Shanghai
