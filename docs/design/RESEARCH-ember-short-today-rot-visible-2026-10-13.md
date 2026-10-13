# RESEARCH · 短征「今日推」轮换可见（日短本选本可感 · 零抬日帽）· 2026-10-13

> **状态：** **CLOSED live** · 批 A·M · D13309 · menu `5611a4337` · PASS `8c4b8ce2d` · 2026-10-13 22:16 Asia/Shanghai  
> **起因：** 总控【TIP-CC CLOSED】挂机庭强化/升阶/精工/成色四件齐 · tip `7f05e5cd1` · PASS `928545e6a`。下一波交薄 tip：荐短征轮换 或 事件窗（此前策划后置，现可开）；或其它挂机庭/日征乐趣可感。硬禁 sx20 / 抬 AFK 日表 / L13 / K3 live / 菜单诚实空转 / Pack6 / talent ash。  
> **上游：** TIP-CC CLOSED · D13304–D13308 挂机庭四件齐 · D13305 短征 R2 EliteCast · 多窗 §6 明文后置「今日推短征轮换 / 日短本轮换可见」  
> **硬禁：** **零抬** `daily_kills`/`afk.tiers` · **零** sx20 · **零** L13/K3/Pack6/天赋/灰印/R · **零** 伤乘/skill_mult · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实空转 / observe 空转 · **零** 假开 `gate_daily` · **零** 改短征日帽/S40–S58/体力

---

## 0. 一句话

十九本短征选页是平面墙——没有「今天推哪本」；挂时长缺一条日更选本理由。债清：日帽/首通/进本命令已齐，只缺轮换键+可见格。

---

## 1. 选题扫描（TIP-CC 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **短征「今日推」轮换可见** | **采纳** | 总控点名荐；多窗后置兑现；十九本墙决策空；日帽/进本/名称已 live；薄 jar+TrMenu |
| 事件窗小乐趣 | **后置** | 合法；但 fest 窗/房间事件 soft_hope 已有面；本窗缺「清晰薄债」不如今日推债清 |
| 再叠挂机庭第五件/诚实半行 | **否** | 四件齐；空转硬禁 |
| sx20 / 抬日表 / 抬短征日帽 | **否** | 硬禁 |
| 观察 exhausted 空声明 | **否** | 有可施工真债 |

**择优结论：** 「今日推」比事件窗更债清——无新经济、无新地图；只需历日→sx 映射键 + 选页一格；可感挂时长（每天换一本动机）。故 **写死荐 M = 短征今日推轮换可见**，事件窗后置。

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `ember_p1_short` 十九本并列；Open/I 有日帽合计/首通，**无**「今日推哪本」 | `ember_p1_short.yml` | 选本墙；缺日更焦点 |
| E2 | `%corerpg_p1_sx0N_day_line%` / `sx_day_left_sum` / `sx0N_fc` / `enter sx0N` 已 live | PAPI + 菜单 | 可挂真日线；进本命令现成 |
| E3 | **无** `sx_today*` / 短征日轮换键（对比主线 `%corerpg_p1_featured%` 周轮换） | `EmberRunPapi` | 需薄 jar 或假写——假写否决 |
| E4 | 布局 `'#I H B Z#'` + 末行 `'#PRSJ   #'` 有空位可落 D | 菜单 Layout | 薄可施工；Z=返回冒险保留 |
| E5 | 多窗 DESIGN§6 后置「今日推短征轮换 / 日短本轮换可见」 | D13304–08 / D13305 | 合法接力 |
| E6 | TIP-CC CLOSED；总控点名荐短征轮换或事件窗 | `7f05e5cd1` · 派单 | 选题边界 |
| E7 | 房间事件 `event_soft_hope` / fest `fest_window` 已有可见面 | adventure Open / fest | 事件窗债不如今日推清晰 |
| E8 | 短征链停 sx19；禁 sx20；日帽×3 分本 | backlog / runs.yml | 轮换**不**开新本、**不**抬帽 |

**一句话缺口：** 知道「还有几次有奖」，不知道「今天更该打哪本」——日更选本乐趣断在导航。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **薄 jar（主键）：** `%corerpg_p1_sx_today%` = `sx01`…`sx19`（上海 `LocalDate.toEpochDay() mod 19`）；`_name` 中文名；`_day_line` 复用该本 day_line（只读）  
2. **进本：** 菜单 `corerpg p1 enter %corerpg_p1_sx_today%`（若 TrMenu 不扩 PAPI，则薄别名 `corerpg p1 enter short-today`）· **同**现网门禁/体力/日帽  
3. **TrMenu：** `ember_p1_short` 空位 **D**「今日推」· lore 挂 name+day_line+「0 点换下一本」· 灰态同本：未解锁/体力不足  
4. **Open / 冒险半行（同批薄）：** 「今日推：…」指针  
5. **施工岗：** 批后 **余烬-插件（薄 PAPI+可选 enter 别名）** + **余烬-物品（TrMenu）**；不派怪物/地图

---

## 4. 明确不做

- 抬短征日帽 / S40–S58 / 体力 / 掉落 · 抬 `daily_kills`/`afk.tiers`  
- sx20 · 本窗双开事件窗主交付 · 菜单诚实半行族 · observe 空转  
- 假写固定「今日=sx01」不按历日轮换 · 改主线 featured 周规则  
- Pack6 / 天赋 / 灰印 / R / L13 / K3 · 复开 D423 · 假开 `gate_daily` · stage 脏 runtime  

## STATUS: CLOSED live · 批 A·M · D13309 · menu `5611a4337` · PASS `8c4b8ce2d` · 2026-10-13 22:16 Asia/Shanghai
