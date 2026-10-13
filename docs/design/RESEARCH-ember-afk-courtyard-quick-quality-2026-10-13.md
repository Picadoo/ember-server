# RESEARCH · 挂机庭「顺手成色」（庭内第四件可感 · 零抬产）· 2026-10-13

> **状态：** **勾毕 · CLOSED live** · 批 A·M · D13308 · tip `7f05e5cd1` · PASS `928545e6a` · 2026-10-13 21:57 Asia/Shanghai  
> **起因：** 总控【连开】TIP-CB live `fc94b0561`。挂机庭强化/升阶/精工三件齐。下一薄 tip：荐顺手成色 或 其它乐趣系统（日短本轮换可见/事件窗小乐趣）；禁 sx20/抬表/诚实空转/L13/K3。  
> **上游：** TIP-CB CLOSED live menu `fc94b0561`（D13307 顺手精工）· TIP-CA `68544f966`（升阶）· TIP-BY `e214b0399`（强化）· D13304/D13306/D13307§6#1 明文后置「庭内顺手成色」  
> **硬禁：** **零抬** `daily_kills`/`afk.tiers` · **零** sx20 · **零** L13/K3/Pack6/天赋/灰印/R · **零** 伤乘/skill_mult · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实空转 / observe 空转 · **零** 假开 `gate_daily` · **零** 改 UpgradeRules 价表 · **禁止**用拼接 gap 串做数值条件

---

## 0. 一句话

庭内已能强化+升阶+精工，成色仍要跳工坊——挂机第四件「可点花料」断在成色这一格；**择成色**同形扩，零抬产。

---

## 1. 选题扫描（TIP-CB 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **挂机庭「顺手成色」· 对齐工坊 Q/q** | **采纳** | 开窗点名荐成色；D13304–D13307 后置；命令/键已 live；与强化/升阶/精工同形；**无** gap 拼接串陷阱（比精工更薄） |
| 日短本轮换可见 | **后置** | 合法乐趣；但庭内第四件债更直、同模式已验 |
| 事件窗小乐趣 | **后置** | 可感；本窗优先结清庭内花料断点 |
| 再叠 AFK remain/仓差/半行 | **否** | 展示已厚；禁空转 |
| 菜单诚实 / sure·pity·soft | **否** | 硬禁 |
| sx20 / 抬日表 / 新挂机店 | **否** | 硬禁 |
| 观察 exhausted 空声明 | **否** | 有可施工真债 |

**择优结论：** 成色债清——工坊 Q/q 已 live；`held_next_q` / `held_quality_lack` / `held_quality_hope` 齐；护甲灰同形；布局空位可落 Q/q；**无** `recipe_gap_quality` 拼接串需防；零抬产 TrMenu-only。故 **写死荐 M = 顺手成色**，不改交短征/事件窗。

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `ember_p1_afk` 已有 E/e + U/u + R/r | `ember_p1_afk.yml` · `e214b0399`/`68544f966`/`fc94b0561` | 三件花料 live；布局空格可扩 Q/q |
| E2 | 工坊成色：`corerpg p1 refine quality` / `… confirm`；Q/q 仅护甲灰 | `ember_p1_forge.yml` | **真命令已 live**；庭内可复用 |
| E3 | `%corerpg_p1_held_next_q%` / `%corerpg_p1_held_quality_lack%` / `%corerpg_p1_held_quality_hope%` | forge lore + Open/I 已挂 hope | lore 可挂花费/缺料/进度；缺料时 lack 非空 |
| E4 | **无** `recipe_gap_quality*` 仓差键（现网只挂 enhance1/upgrade_t2/refine1） | afk I / recipes | 本窗 **不**发明 gap；不碰 `<=0` 假条件；缺料靠 lack + 命令失败 |
| E5 | 战况 I / Open 已挂 quality_hope；缺可点成色 | `ember_p1_afk.yml` | 展示齐；缺可点成色 |
| E6 | D13304§6#1 · D13306§6#1 · D13307§6#1 / W1d 明文后置成色 | DESIGN | 合法接力 |
| E7 | TIP-CB live；开窗点名荐顺手成色；禁诚实半行/sx20/抬表 | `fc94b0561` · 总控连开 | 选题边界 |
| E8 | 布局 `'#US4 VRE#'` / `'#ueI FrH#'` 中部空位可插 Q/q | afk Layout | 薄可施工：`'#US4QVRE#'` / `'#ueIqFrH#'` |

**一句话缺口：** 强化/升阶/精工能在庭里点，成色仍逼跳工坊——产→花第四厘米还断着。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu（主）：** `ember_p1_afk` 新格 Q/q「顺手成色」  
   - 对齐工坊：默认可点 → `corerpg p1 refine quality` / `refine quality confirm`  
   - 护甲：`held_is_armor == 1` → 灰「不可用」+ tell（与工坊同）  
   - lore：held_next_q · held_quality_lack · held_quality_hope ·「点预览·再确认」  
   - **不**挂假 gap；**不**发明 `recipe_gap_quality`  
2. **Open / 去哪花半行：** 「成色也可本页点」指针（同批薄）  
3. **零 jar；** 缺料靠 lack 红字 + 命令失败 tell  
4. **施工岗：** 批后 **余烬-物品（TrMenu）**；不派插件  

---

## 4. 明确不做

- 抬 `daily_kills` / `afk.tiers` / 离线% / 层收益 · 改 UpgradeRules · 发明庭内第二价轨  
- 本窗双开短征轮换/事件窗主交付 · sx20 · 菜单诚实半行族 · observe 空转  
- Pack6 / 天赋 / 灰印 / R / L13 / K3 / 伤乘 / skill_mult / Affix densify  
- 复开 D423 · 假开 `gate_daily` · 教手打指令 · stage 脏 runtime  
- 发明 `recipe_gap_quality` 或用拼接串做数值条件  

## STATUS: **勾毕 · CLOSED live** · 批 A·M · D13308 · tip `7f05e5cd1` · PASS `928545e6a` · 2026-10-13 21:57 Asia/Shanghai
