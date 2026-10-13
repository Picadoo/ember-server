# RESEARCH · 挂机庭「顺手精工」（庭内第三件可感 · 零抬产）· 2026-10-13

> **状态：** 待批 A · 荐方案 M · TIP-CB / 拟 **D13307** · docs-only · 2026-10-13 21:45 Asia/Shanghai  
> **起因：** 总控【连开】TIP-CA live `68544f966`。再交一薄 tip：顺手精工（挂机庭第三件）或其它乐趣系统；禁 sx20/抬表/诚实空转/L13/K3。  
> **上游：** TIP-CA CLOSED live menu `68544f966`（D13306 顺手升阶）· TIP-BY live `e214b0399`（D13304 顺手强化+1）· D13304§6#1 / D13306§6#1 后置「庭内顺手精工」  
> **硬禁：** **零抬** `daily_kills`/`afk.tiers` · **零** sx20 · **零** L13/K3/Pack6/天赋/灰印/R · **零** 伤乘/skill_mult · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实空转 / observe 空转 · **零** 假开 `gate_daily` · **零** 改 UpgradeRules 价表 · **禁止**用拼接 gap 串做 `<=0` 条件（D13306 教训）

---

## 0. 一句话

庭内已能强化+升阶，精工仍要跳工坊——挂机第三件「可点花料」断在精工这一格；**择精工**（非成色）同形扩，零抬产。

---

## 1. 选题扫描（TIP-CA 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **挂机庭「顺手精工」· 对齐工坊 R/r** | **采纳** | 开窗点名挂机庭第三件/顺手精工；D13304/D13306 后置；命令/键已 live；与强化/升阶同形 |
| 挂机庭「顺手成色」 | **后置** | 同形合法；本窗只钉精工一条保持薄 |
| 再叠 AFK remain/仓差/半行 | **否** | 展示已厚；禁空转 |
| 菜单诚实 / sure·pity·soft | **否** | 硬禁 |
| sx20 / 抬日表 / 新挂机店 | **否** | 硬禁 |
| 短征今日推轮换 | **后置** | 非庭内多一事 |
| 观察 exhausted 空声明 | **否** | 有可施工真债 |

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `ember_p1_afk` 已有 E/e 顺手强化 + U/u 顺手升阶 | `ember_p1_afk.yml` · `e214b0399` / `68544f966` | 两件花料 live；第三件空位可扩 |
| E2 | 工坊精工：`corerpg p1 refine craft` / `… confirm`；R/r 仅护甲灰（`held_is_armor==1`） | `ember_p1_forge.yml` | **真命令已 live**；庭内可复用 |
| E3 | `%corerpg_p1_held_next_c%` / `%corerpg_p1_held_refine_lack%` / `%corerpg_p1_held_refine_hope%` | `EmberRunPapi` | lore 可挂花费/缺料/进度；缺料时 lack 非空 |
| E4 | `%corerpg_p1_recipe_gap_refine1%` = **拼接半行**「胚差·骨差」（**非**纯数字） | `recipeGapRefine1` | **不能**照搬 enhance1 的 `<=0` 条件（D13306 同教训）；亮/灰仓够同形需 jar——本窗默认不做 |
| E5 | 战况 I 已挂精工0→1 gap + held_refine_hope | `ember_p1_afk.yml` D415/D612 | 展示齐；缺可点精工 |
| E6 | D13304 W1d / §6#1 · D13306§6#1 明文后置精工/成色 | DESIGN | 合法接力 |
| E7 | TIP-CA live；开窗点名顺手精工；禁诚实半行/sx20/抬表 | `68544f966` · 总控连开 | 选题边界 |
| E8 | 成色：`refine quality`/`confirm` · held_next_q / quality_hope | forge + Papi | 可后置；本窗不双开 |

**一句话缺口：** 强化/升阶能在庭里点，精工仍逼跳工坊——产→花第三厘米还断着。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu（主）：** `ember_p1_afk` 新格 R/r「顺手精工」  
   - 对齐工坊：默认可点 → `corerpg p1 refine craft` / `refine craft confirm`  
   - 护甲：`held_is_armor == 1` → 灰「不可用」+ tell（与工坊同）  
   - lore：held_next_c · held_refine_lack · held_refine_hope · recipe_gap_refine1 展示行 ·「点预览·再确认」  
   - **禁止** `recipe_gap_refine1 <= 0` 当亮/灰条件  
2. **Open / 去哪花半行：** 「精工也可本页点」指针（同批薄）  
3. **零 jar；** 不做 `recipe_gap_refine_ok` 数字键（后置）；缺料靠 lack 红字 + 命令失败 tell  
4. **施工岗：** 批后 **余烬-物品（TrMenu）**；不派插件  

---

## 4. 明确不做

- 抬 `daily_kills` / `afk.tiers` / 离线% / 层收益 · 改 UpgradeRules · 发明庭内第二价轨  
- 本窗双开成色 · sx20 · 菜单诚实半行族 · observe 空转  
- Pack6 / 天赋 / 灰印 / R / L13 / K3 / 伤乘 / skill_mult / Affix densify  
- 复开 D423 · 假开 `gate_daily` · 教手打指令 · stage 脏 runtime  
- 用拼接 gap 串做数值条件（D13306 教训）

## STATUS: 待批 A · 荐 M · 拟 D13307 · 2026-10-13 21:45 Asia/Shanghai
