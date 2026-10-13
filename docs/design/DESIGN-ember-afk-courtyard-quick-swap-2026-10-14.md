# 余烬 · 挂机庭「顺手互换」（产→换轨可点 · ≠第五件花料 · ≠半行空转 · ≠抬日表）

STATUS=**已批 A · 方案 M · D13317** · 批 sha `ac5ba6436` · 总控 2026-10-14 01:05 Asia/Shanghai · TIP-CL · 布局建议 `#P1W2w3D#` · 已派物品 · **勿勾 CLOSED**（等 live CLOSED）· docs→施工
> tip [`STATUS-ember-next-hard-debt-afk-courtyard-quick-swap-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-afk-courtyard-quick-swap-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-afk-courtyard-quick-swap-2026-10-14.md`](RESEARCH-ember-afk-courtyard-quick-swap-2026-10-14.md) · backlog `B-afk-courtyard-quick-swap` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠第五件花料 ≠推荐花复读 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠Cast粒子主交付 ≠半行独批**

> **一句话玩家价值：** 挂机庭里手持刃，一点就能预览/确认强化轨道互换——产→换轨不断跳工坊；**不**新开第五花料、**不**抬日产。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CK CLOSED `b8eaf7696` · 连开 TIP-CL | 须真可点/可进机制；禁半行独批 |
| E2 | D13316§6③ | 明文后置「庭内互换 S 顺手」 |
| E3 | `ember_p1_afk` | E/U/R/Q/P/D/S live；**无** W/w；**无** `p1 swap` |
| E4 | `ember_p1_forge.yml` S/s | `corerpg p1 swap` / `swap confirm` + `%held_swap_cost%` + 甲灰 live |
| E5 | 短征 Cast 波已齐 | R1 近战/远程 · R2 Elite · Boss；再开易触粒子打磨硬禁 |
| E6 | 硬禁 | 第五花料/推荐花复读/半行空转/抬表/sx20/P兼确认 |
| E7 | **为何不是半行** | 主交付=**新格真 command**（swap + confirm）；Open 半行不可独批（见 §2 A 否决） |

**一句话问题：** 庭内能花能拆不能换轨——资源环互换侧断链。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再交选页可躲半行 | **否** | REJECT 保持 |
| Cast 粒子打磨 / 再叠 Cast | **否** | 派单禁；已齐 |
| P 格兼确认 / 第五花料 / 推荐花复读 | **否** | 硬禁 |
| 只 Open「记得去工坊换轨」 | **否** | 半行空转 |
| **庭内顺手互换真格（荐）** | **采纳** | E2–E5；真动作；≠花料 |

---

## 1. 玩家感知目标（≤3）

1. **庭内可换轨：** 打开挂机庭见「顺手互换」，点预览→点确认，与工坊同成交。  
2. **预览诚实：** lore 挂 `%held_swap_cost%`（免费确定口径）；甲灰不可假成功。  
3. **零抬产：** 不改 Swap 价/UpgradeRules/afk.tiers/daily_kills；与工坊同命令同确认。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 Open「记得去工坊换轨」半行** | 口号 | **否决独批** — 无新动作=诚实空转 |
| **M · 庭内 W/w 顺手互换 + 真 swap（荐）** | 新格 `command: corerpg p1 swap` + `swap confirm`；lore 挂 cost；甲灰对齐 forge；Open 半行仅同批辅 · **零改**价/产量 | **荐** — 真可点；对位四花+拆模式；≠第五花料 |
| **L · 本窗改交粒子/推荐花复读/P兼确认/短征再叠 Cast** | 换题 | **否决独批** — 弱或硬禁 |
| **W · 静默换无确认 / 抬价 / sx20 / 半行独批** | — | **否决** — 硬禁或假平面 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a W/w 格必做；W1b Open/去哪花半行同批辅；W1c 甲灰对齐工坊；见 §3）
- [ ] 或勾 **方案 L**（改交粒子/推荐花复读/再叠 Cast）· **本窗不批 L**
- [x] 否决 A（只半行）/ W（静默换/抬表/sx20）
- [x] 批注：总控批 A·M · D13317 · TIP-CL · TrMenu W/w→p1 swap/confirm · 甲灰对齐 forge · 零改价/产 · 不撤四花+P+D · 半行仅辅 · 布局建议 `#P1W2w3D#`

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 预览展示 | `%corerpg_p1_held_swap_cost%` | **零新 PAPI**；吃 live 免费确定口径 |
| 命令 | `corerpg p1 swap` → 菜单 **w** `swap confirm` | **与工坊同**；禁发明庭内第二换轨 / 静默 confirm |
| 甲 | `held_is_armor==1` → 灰+tell（与 forge 同） | **不**假成功；甲走 D 拆 |
| 刃/可换件 | 亮态预览→确认 | 禁暗示庭内能强化甲 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 顺手互换格（必做 · TrMenu）** | `ember_p1_afk` 空位落 **W** 名 `§b顺手互换` · lore≤3：cost 行 +「免费确定·预览直办」+「点预览·再确认」· `command: corerpg p1 swap`；**w** 名 `§b顺手互换 · 确认` · `command: corerpg p1 swap confirm`（布局钉死：**总控建议** `'#P1W2w3D#'`（9 格 W+w 同排）；原示例 `'#P1 2 3D#'`→`'#P1W2 3D#'` 已升为含 w 同排） | 不撤 E/U/R/Q/P/D/S；不改层传送/战况 |
| **W1b · Open / 去哪花半行（必做同批 · 辅）** | Open +半行「本页也可顺手换轨」；S 去哪花 +半行同指针 | **不可**无 W1a 独交半行 |
| **W1c · 甲灰诚实（必做）** | 对齐 forge：甲→灰玻璃 + tell「护甲随护符…甲·K0 只能分解」 | 不改 Swap 价 |
| **W1d · jar（默认不做）** | TrMenu-only；键已 live | 禁借号抬产/改价 |
| **W1e · 经济/花料（不做）** | — | 禁第五花料/改价/抬日表 |

### 3.3 玩家路径

| 步 | 路径 | 说明 |
|----|------|------|
| 挂 | 庭内打怪产料 / 换手持刃 | 现网 |
| **换（本债）** | 点 W → 预览 · 点 w → 确认成交 | W1a/W1c |
| 花/拆 | 仍点 E/U/R/Q/P/D | 四花+推荐花+拆保留 |
| 深 | 「去哪花」进工坊全功能 | D375 保留 |

**文案钉死示例（施工可微调色码，语义不得漂）：**

```
§b顺手互换
§e%corerpg_p1_held_swap_cost%
§a免费确定 §8· §f预览直办
§e➥ §f点预览·再确认
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13304–08/15/16 四花+推荐花+拆 | **保留**；本债=换轨侧；**≠**第五花料 |
| forge S/s | **同命令同 PAPI**；不改价 |
| 半行 REJECT D13316 | **保持否决**；本窗真 command |
| P 兼确认 / fest / calamity / sx20 | **本窗不做** |

### 3.5 为何不是半行（验收必读）

| 半行空转（禁） | 本窗 M（过） |
|----------------|--------------|
| 只改 lore/Open 口号 | **新 Icon W/w + 真 `command: corerpg p1 swap` / `swap confirm`** |
| 玩家无新点击结果 | 点击→预览→确认→轨道互换成交 |
| 对位 sure/pity/observe | 对位四花「顺手*」真花费/动作格 |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 庭内出现 W「顺手互换」；左键触发 `p1 swap` 预览（与工坊同） |
| V2 | lore 含 `%held_swap_cost%`；w 触发 `swap confirm` 成交（抽测） |
| V3 | 甲点 W/w → 灰/tell，不假成功 |
| V4 | E/U/R/Q/P/D/S 仍在；无第五花料命令；无静默换 |
| V5 | `git diff`：无改 Swap 价/afk.tiers/daily_kills/伤表、无 sx20、无 P兼确认、无半行独档 |
| V6 | 玩家面无裸指令（仅菜单） |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · **只半行无 W 格**  
- **第五件花料** · **推荐花复读** · **P 格兼确认** · 静默换 · 改 Swap 价 · 撤 E/U/R/Q/P/D  
- Cast 粒子纯打磨当主交付 · 复开 D423 · stage 脏 runtime · 教裸指令  

---

## 6. 后置（明文 · 本窗不做）

1. P 格兼确认（须另批）  
2. Guard/Elite/Archer/Boss Cast 粒子二次打磨  
3. 庭内顺手洗炼/reroll（若另证真债）  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 01:05 Asia/Shanghai | 总控批 A·M · 派物品 · 批 sha `ac5ba6436` · 布局建议 `#P1W2w3D#` |
| 2026-10-14 01:06 Asia/Shanghai | 策划补记 · STATUS/RESEARCH/tip/backlog 标**已批 A·方案 M** · 旁注批 sha `ac5ba6436` · 布局 `#P1W2w3D#` · **勿勾 CLOSED** |
| 2026-10-14 01:02 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CK CLOSED 后连开 · 选题挂机庭顺手互换 · 拟 D13317 · TIP-CL · ≠sx20 ≠抬表 ≠第五件花料 ≠半行空转 ≠推荐花复读 · **改题有证据**（真 command · 见 §3.5；短征 Cast 已齐故不主交粒子） |

*D13317 **已批 A · 方案 M** · 批 sha `ac5ba6436` · 布局 `#P1W2w3D#` · 挂机庭顺手互换 · 已派物品 · **勿勾 CLOSED** · TIP-CL · ≠半行空转 ≠第五花料 ≠推荐花复读。*
