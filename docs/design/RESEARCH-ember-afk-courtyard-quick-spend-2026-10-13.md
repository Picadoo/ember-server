# RESEARCH · 挂机庭「顺手花料」（庭内多一事 · 产→花可点）· 2026-10-13

> **状态：** 待批 A · 荐方案 M · TIP-BY / 拟 **D13304** · docs-only · 2026-10-13 21:26 Asia/Shanghai  
> **起因：** 总控开窗 · TIP-BX CLOSED live `1.65.804-d13303` tip `be7a461ec` — 下一选题必须「可感内容/挂时长」；**禁止**再开菜单诚实/observe 空转；硬禁 sx20·L13·K3·Pack6/天赋灰印/R·抬 AFK 日表/伤乘/skill_mult·Affix densify·sure/pity/soft 诚实半行族再叠。优先：**挂机庭多一事可做 / 资源去向可见** 或短本玩法花样（**非 sx20**）。  
> **上游：** TIP-BX CLOSED · D375 去哪花→工坊 · D381 材料哪来→挂机 · D404/D415 仓差 · D405/D413 remain · D408 满额短征追 · D409 有奖花材追 · D411/D420 next_farm · D612 挂机在养手持 · handoff `7fc55366`（**本单不复开 D423**）  
> **硬禁：** **零抬** `daily_kills`/`afk.tiers` · **零** sx20 · **零** L13/K3/Pack6/天赋/灰印/R · **零** 伤乘/skill_mult · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实空转 / observe 空转 · **零** 假开 `gate_daily` · **零** 改 UpgradeRules 价表

---

## 0. 一句话

挂机庭已能看见「料进仓 / 还差多少 / 去哪花」，但**庭内仍不能顺手把料花掉**——产→花断在「跳工坊」一层；要补**一点就能强化手持**的可感动作（零抬产量）。

---

## 1. 选题扫描（TIP-BX 后 · 可感内容真债）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **挂机庭「顺手花料」· 仓够庭内强化+1** | **采纳** | 对位开窗「多一事可做/资源去向」；去哪花已齐但庭内无动作；`corerpg p1 enhance` 已 live；条件可复用 `recipe_gap_enhance1` |
| 再叠 AFK remain/仓差/next_farm/层名片半行 | **否** | D305/D375/D383/D404–D415/D411 已厚；再叠=展示空转，开窗明文禁 |
| 菜单诚实 / sure·pity·soft 半行族 | **否** | TIP-BX 刚关；开窗硬禁 |
| sx20 新本硬开 | **否** | 硬禁；短征链 sx19 已 PASS 暂停 |
| 短征 R2 精英 Cast 扩线 | **后置备选** | 真玩法花样；工期大于庭内一点强化；本窗优先挂机多一事 |
| 今日推短征轮换 | **后置** | 可感但偏选本导航；不解决庭内「没事可点」 |
| 抬日表 / 新挂机地图包 / 假平面 | **否** | 硬禁 |
| 观察停窗 only / exhausted 空声明 | **否** | 有可施工真债 |

---

## 2. 证据（现网只读 · 2026-10-13）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 挂机页有「战利品去哪花」→`ember_p1_forge`；Open/战况挂 vault·recipe_gap·remain·next_farm·held hope | `ember_p1_afk.yml` | **资源去向可见已齐**；缺**庭内花费动作** |
| E2 | 工坊强化：`command: corerpg p1 enhance` / `… confirm` | `ember_p1_forge.yml` | **真花费命令已 live**；可复用，禁发明第二套价 |
| E3 | `%corerpg_p1_recipe_gap_enhance1%` = 仓碎相对强化+1 代表档缺口（0=够） | `EmberRunPapi.recipeGapEnhance1` | **可作 TrMenu 条件**（够料亮 / 不够灰） |
| E4 | `%corerpg_p1_held_enhance_cost%` 手持本次花费 | forge 菜单 | 顺手格 lore 可挂真花费；成交仍走 enhance 门禁 |
| E5 | D375/D381 只做双向跳转；D404+ 只展示 gap | DESIGN/STATUS 链 | **互指≠多一事**；本债=可点花费 |
| E6 | TIP-BX CLOSED；开窗禁诚实半行族 / sx20 / 抬表 | tip `be7a461ec` · 总控派单 | 选题边界钉死 |
| E7 | 短征 sx14–sx19 已上线；视觉 O→X 闭合；R2 Elite **无** Cast（仅 SoftHit） | MM EmberSx* | 短本花样后置合法；本窗不抢 |
| E8 | handoff：D423 曾待批；现网 DESIGN 已 PASS 上线——**本单仍不勾/复开 D423** | handoff `7fc55366` · 派单 | 以本开窗为准；勿动旧号 |

**一句话缺口：** 挂机坐着只会看进度；料够了仍要离开庭进工坊——「多一事」断在最后一厘米。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu（主）：** `ember_p1_afk` 新格「顺手花料 / 顺手强化+1」  
   - `recipe_gap_enhance1 <= 0`：亮态 → `corerpg p1 enhance`（预览）+ 确认路径对齐工坊（confirm）  
   - `> 0`：灰/琥珀态 → lore 挂还差 + 点击仍可进 `ember_p1_forge`（或 forge_recipes）  
2. **Open / 去哪花半行：** 「仓够时可在本页顺手花」指针（可选同批）  
3. **零 jar 优先；** 若 confirm 双步在庭内不稳，批后薄插件仅允许「庭内 enhance 单确认」——**禁改价/产量**  
4. **施工岗：** 批后 **余烬-物品（TrMenu）**；插件仅当 TrMenu 不够时另派  

---

## 4. 明确不做

- 抬 `daily_kills` / `afk.tiers` / 离线% / 层收益 · 改 UpgradeRules · 发明庭内第二商店  
- sx20 · 短征 R2 Cast 本窗硬铺 · 菜单诚实半行族 · observe 空转  
- Pack6 / 天赋 / 灰印 / R / L13 / K3 / 伤乘 / skill_mult / Affix densify  
- 复开 D423 · 假开 `gate_daily` · 教手打指令  

## STATUS: 待批 A · 荐 M · 拟 D13304 · 2026-10-13 21:26 Asia/Shanghai
