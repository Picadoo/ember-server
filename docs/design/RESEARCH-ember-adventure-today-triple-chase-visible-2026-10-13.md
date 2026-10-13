# RESEARCH · 冒险「今日三冲」聚合（今日推+冲一下+本周短征 · 零抬日帽/目标数/事件率）· 2026-10-13

> **状态：** **勾毕 · CLOSED live** · 批 A·M · D13313 · PASS `ce9cd56bb` · jar `1.65.810-d13313.local` · TIP-CH CLOSED live `69cc1580c` · 策划勾毕 2026-10-14 00:12 Asia/Shanghai  
> **起因：** 总控【连开·下一档内容真债】TIP-CG CLOSED live `eb0dbeeae` · PASS `20907456a` · jar `1.65.809-d13312b.local`。请出下一窗 tip+DESIGN（拟 D13313 · TIP-CH），STATUS=待批 A · 荐 M · docs-only。优先可感：挂机庭资源环 / 短征日更 / 乐趣系统；合法接力可看 D13312§6①「冒险今日三冲聚合」。硬禁：sx20·抬日帽/AFK表/short:5·L13·K3·Pack6·天赋灰印·诚实/observe空转·灾厄重开·挂机庭第五件花料·复开D423·教裸指令。  
> **上游：** TIP-CG CLOSED · D13312 本周短征可冲 live · D13309 今日推 · D13310 冲一下 · D13304–08 挂机庭四件齐 · Cast 三房齐 · fest/灾厄仍后置  
> **硬禁：** **零** sx20 · **零** L13 施工 · **零** K3 · **零** Pack6/天赋灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/伤乘/`skill_mult` · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实/observe 空转 · **零** 假开 `gate_daily` · **零** 重开 calamity join · **零** 新地图硬开 · **零** 再叠挂机庭花料第五件 · **零抬** `weekly_goals.targets.short` / 徽 / `event_rate` / NEED · **零** 复开 D423 · **零** 教裸指令

---

## 0. 一句话

今日推 / 冲一下 / 本周短征 **三件冲口已分格 live**，但冒险首页仍要「短征 H 进选页」或「只摸 E」——缺一个**一眼三冲 + 一点智路由**的聚合动作；挂时长断在首页决策。

---

## 1. 选题扫描（TIP-CG 后 · 可感内容真债）

| 候选 | 裁决 | 理由（证据） |
|------|------|--------------|
| **冒险「今日三冲」聚合** | **采纳** | D13312§6① 明文后置；三冲键/enter 已齐；冒险 Layout 第三行空位可落 P；H 仅 lore 今日推无直进；本周短征只在选页 F |
| 挂机庭资源环再开 / 第五件花料 | **否** | 四件齐；总控硬禁第五件 |
| R1 远程 Cast / 粒子打磨 | **后置** | Cast 三房已齐；非债清主链 |
| 新 fest / 重开灾厄 | **否** | 硬禁/政策后置 |
| 菜单诚实 / sure·pity·soft | **否** | 空转硬禁 |
| sx20 / 抬日表 / 抬 short:5 / 抬 event_rate | **否** | 硬禁 |

### 1.1 改题？**否** — 证据表支持合法接力

| # | 证据 | 出处 | 为何仍荐三冲（非改题） |
|---|------|------|------------------------|
| E1 | D13312§6① 明文「冒险今日三冲聚合」后置 | DESIGN D13312 | 总控点名合法接力 |
| E2 | `sx_today*` + `enter short-today` live；冒险 H 仅 lore+进选页 | D13309 · adventure.yml | 首页无直进今日推 |
| E3 | `event_chase*` + E 格 live | D13310 | 三冲之一已在冒险，但另两件不在同视域动作 |
| E4 | `goal_short_chase*` + 选页 F live；冒险无周目标冲口 | D13312 | 周目标仍藏在选页 |
| E5 | Layout `'#WsEb u n'` 在 b/u 间有空位可落 **P** | adventure.yml | 薄可施工 |
| E6 | 挂机庭四件 / Cast 三房 / 第五件花料硬禁 | TIP-CG 派单 | 无更优可感真债可改题 |

**择优结论：** 不改题。写死荐 M = 冒险页「今日三冲」聚合（一眼三行 + 一点智路由进本）。

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 三 enter 别名已齐：`short-today` / `event-chase` / `short-goal` | `EmberRunService` | 聚合可复用，禁第二奖励轨 |
| E2 | 三套展示键已齐：`sx_today_name`/`sx_today_day_line` · `event_chase_name`/`event_soft_hope` · `goal_short`/`goal_short_chase_name` | `EmberRunPapi` | lore 可零新文案体系 |
| E3 | 冒险 H=进短征选页（非 enter）；E=冲一下；无本周短征格 | `ember_p1_adventure.yml` | 首页缺聚合冲口 |
| E4 | 第三行 `'#WsEb u n'` 空位可落 P（保留 W/s/E/b/u/n） | 菜单 Layout | 薄 TrMenu |
| E5 | 总控硬禁第五件花料 / sx20 / 抬表 / 诚实空转 | 派单 | 边界钉死 |

**一句话缺口：** 知道有今日推、冲一下、本周短征，仍要自己在冒险↔选页间跳——三冲乐趣断在首页一键。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **薄 jar（荐）：** `%corerpg_p1_today_triple%` = 智路由目标键（优先 `short-today` 若今日推仍有有奖；否则 `short-goal` 若周目标未满且 chase_ok；否则 `event-chase` 若 chase_ok；否则空）；`_name` / `_kind`（today|week|event|empty）/ `_ok`（0/1）  
2. **进本：** `corerpg p1 enter today-triple`（解析到上列三者之一）· **同**现网解锁/体力/日帽；**不**第二奖励轨  
3. **TrMenu：** 冒险空位 **P**「今日三冲」· lore 三行复用现键（今日推 / 冲一下 / 本周短征）+「左键：冲当前推荐」· `_ok=0` / 体力不足灰态诚实；**不撤** H/E/F  
4. **Open 半行（同批薄）：** 「今日三冲见本页 P」；不盖 mapid_board / 冲一下 E 指针  
5. **施工岗：** 批后 **余烬-插件（薄 today_triple* + enter）** + **余烬-物品（TrMenu）**；不派怪物/地图

---

## 4. 明确不做

- 抬 `weekly_goals.targets.short` / 徽 / 短征日帽 / `event_rate` / NEED / AFK 日表  
- sx20 · 新地图 · 只加 Open 半行无进本（诚实空转）· 菜单 sure/pity 半行再叠  
- 挂机庭花料第五件 · Pack6 / 天赋 / 灰印 / R / L13 / K3 · 重开 calamity · 新 fest · 复开 D423 · 教裸指令 · stage 脏 runtime  
- 撤换现有 H/E/短征 F 为独一家（聚合=加枢纽，不删分格）· 再叠 Cast / 粒子刷屏  

## STATUS: CLOSED live · 批 A·M · D13313 · PASS `ce9cd56bb` · TIP-CH · 2026-10-14 00:12 Asia/Shanghai
