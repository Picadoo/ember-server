# RESEARCH · 挂机庭「顺手升阶」（庭内第二件可感 · 零抬产）· 2026-10-13

> **状态：** **已批 A · 方案 M · D13306** · TIP-CA CLOSED live menu `68544f966` · tip `328d0db1e` · 2026-10-13 21:45 Asia/Shanghai  
> **起因：** 总控【连开】TIP-BZ live `d39c32bb6`。再交一薄 tip：挂机庭第二件可感（荐顺手升阶/精工其一，仍零抬产）或其它乐趣系统；禁 sx20/诚实空转/抬表/L13/K3。  
> **上游：** TIP-BZ CLOSED live MM `d39c32bb6`（D13305 R2 EliteCast）· TIP-BY live menu `e214b0399`（D13304 顺手强化+1）· D13304§6#1 / D13305§6#1 后置「庭内顺手升阶/精工」  
> **硬禁：** **零抬** `daily_kills`/`afk.tiers` · **零** sx20 · **零** L13/K3/Pack6/天赋/灰印/R · **零** 伤乘/skill_mult · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实空转 / observe 空转 · **零** 假开 `gate_daily` · **零** 改 UpgradeRules 价表

---

## 0. 一句话

庭内已能顺手强化+1，升阶/精工仍要跳工坊——挂机第二件「可点花料」断在升阶这一格；**择升阶**（非精工）同形扩，零抬产。

---

## 1. 选题扫描（TIP-BZ 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **挂机庭「顺手升阶」· 对齐工坊 U/u** | **采纳** | 开窗点名挂机庭第二件；D13304§6#1 / D13305§6#1 后置；命令/键已 live；与顺手强化同形 |
| 挂机庭「顺手精工」 | **后置** | 同形合法；精工带 hope/进度感，本窗只钉一条保持薄 |
| 再叠 AFK remain/仓差/半行 | **否** | 展示已厚；禁空转 |
| 菜单诚实 / sure·pity·soft | **否** | 硬禁 |
| sx20 / 抬日表 / 新挂机店 | **否** | 硬禁 |
| 短征今日推轮换 | **后置** | 非庭内多一事 |
| 观察 exhausted 空声明 | **否** | 有可施工真债 |

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `ember_p1_afk` 已有 E/e 顺手强化+1（gap_enhance1 亮/灰） | `ember_p1_afk.yml` · tip `e214b0399` | 第一件花料 live；第二件空位可扩 |
| E2 | 工坊升阶：`corerpg p1 upgrade` / `… confirm`；U/u 仅护甲灰（`held_is_armor==1`） | `ember_p1_forge.yml` | **真命令已 live**；庭内可复用 |
| E3 | `%corerpg_p1_held_upgrade_cost%` / `%corerpg_p1_held_upgrade_lack%` | `EmberRunPapi` | lore 可挂真花费/缺料；缺料时 lack 非空 |
| E4 | `%corerpg_p1_recipe_gap_upgrade_t2%` = **拼接半行**「碎差·核差·胚差」（**非**纯数字） | `recipeGapUpgradeT2` | **不能**照搬 enhance1 的 `<=0` 条件；亮/灰仓够同形需 jar——本窗默认不做 |
| E5 | 战况 I 已挂升阶T2 / 精工0→1 gap 行 | `ember_p1_afk.yml` D415 | 展示齐；缺可点升阶 |
| E6 | D13304 W1d / §6#1 明文后置升阶/精工；D13305§6#1 再后置 | DESIGN | 合法接力 |
| E7 | TIP-BZ live；开窗禁诚实半行/sx20/抬表 | `d39c32bb6` · 总控连开 | 选题边界 |
| E8 | 精工：`refine craft`/`confirm` · hope 键 · gap_refine1 亦拼接串 | forge + Papi | 可后置；本窗不双开 |

**一句话缺口：** 强化能在庭里点，升阶仍逼跳工坊——产→花第二厘米还断着。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu（主）：** `ember_p1_afk` 新格 U/u「顺手升阶」  
   - 对齐工坊：默认可点 → `corerpg p1 upgrade` / `upgrade confirm`  
   - 护甲：`held_is_armor == 1` → 灰「不可用」+ tell（与工坊同）  
   - lore：held_upgrade_cost · held_upgrade_lack · recipe_gap_upgrade_t2 展示行 ·「点预览·再确认」  
2. **Open / 去哪花半行：** 「升阶也可本页点」指针（同批薄）  
3. **零 jar；** 不做 `recipe_gap_upgrade_ok` 数字键（后置）；缺料靠 lack 红字 + 命令失败 tell（与工坊同诚实）  
4. **施工岗：** 批后 **余烬-物品（TrMenu）**；不派插件  

---

## 4. 明确不做

- 抬 `daily_kills` / `afk.tiers` / 离线% / 层收益 · 改 UpgradeRules · 发明庭内第二价轨  
- 本窗双开精工/成色 · sx20 · 菜单诚实半行族 · observe 空转  
- Pack6 / 天赋 / 灰印 / R / L13 / K3 / 伤乘 / skill_mult / Affix densify  
- 复开 D423 · 假开 `gate_daily` · 教手打指令 · stage 脏 runtime  

## STATUS: **已批 A · 方案 M · D13306** · CLOSED live menu `68544f966` · tip `328d0db1e` · 2026-10-13 21:45 Asia/Shanghai
