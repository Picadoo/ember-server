# 余烬 · 挂机庭「顺手精工」（庭内第三件可感 · 零抬产 · ≠sx20）

STATUS=**已批 A · 方案 M · D13307** · 总控 2026-10-13 21:46 Asia/Shanghai · tip APPROVED `5eb12dbff` · 物品 live `fc94b0561` · 2026-10-13 21:50 Asia/Shanghai · tip [`STATUS-ember-next-hard-debt-afk-courtyard-quick-refine-need-design-2026-10-13.md`](../status/STATUS-ember-next-hard-debt-afk-courtyard-quick-refine-need-design-2026-10-13.md) · RESEARCH [`RESEARCH-ember-afk-courtyard-quick-refine-2026-10-13.md`](RESEARCH-ember-afk-courtyard-quick-refine-2026-10-13.md) · backlog `B-afk-courtyard-quick-refine` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠改 UpgradeRules/afk.tiers ≠复开 D423 ≠教裸指令**

> **一句话玩家价值：** 挂机坐着料够了，在庭里点一下就能精工手持——第三件产→花不断在「去工坊」；**不**抬日产量、**不**新开商店。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控连开 · TIP-CA live `68544f966` | 再交薄 tip：顺手精工（挂机庭第三件）或其它乐趣；禁 sx20/抬表/诚实空转/L13/K3 |
| E2 | D13304 顺手强化+1 · D13306 顺手升阶 live | 前两件花料范式；本窗同形扩精工 |
| E3 | `ember_p1_forge.yml` R/r | `corerpg p1 refine craft` / `confirm`；护甲灰 |
| E4 | `held_next_c` / `held_refine_lack` / `held_refine_hope` / `recipe_gap_refine1` | 花费+缺料+进度+代表档仓差（**gap 为拼接串，非纯数字**） |
| E5 | D13304§6#1 / D13306§6#1 | 后置精工/成色明文 |
| E6 | Stage2 / 硬禁 / D13306 教训 | 观察未关 ≠ 关观察；禁假开旧日常；禁抬表；**禁 gap<=0** |

**一句话问题：** 庭内能强化/升阶，精工仍要离庭——第三件可感断点。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再叠 remain/仓差/半行 | **否** | 空转硬禁 |
| 菜单诚实 / sure·pity | **否** | 硬禁 |
| sx20 / 抬日表 / 新挂机店 | **否** | 硬禁 |
| 顺手成色本窗双开 | **后置** | 保持薄；成色另窗 |
| **庭内顺手精工（荐）** | **采纳** | E2–E5；命令已 live；对齐 D13304/D13306 模式 |

---

## 1. 玩家感知目标（≤3）

1. **庭内可精工：** 打开挂机庭，见「顺手精工」预览/确认，点两下走真精工。  
2. **护甲/不可用诚实：** 护甲灰态；缺料/不可精见 lack 红字或命令失败 tell，**不**假成功。  
3. **零抬产：** 不改日 2400 / 层表 / UpgradeRules；成交扣仓与工坊同口径。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 Open tell「记得去工坊精工」** | 口号 | **否决独批** — 仍无庭内动作 |
| **M · 庭内顺手精工格 + 真命令（荐）** | 新格 R/r 对齐工坊 refine craft→confirm；护甲灰；lore 挂 next_c/lack/hope/gap 串 · **零改产量/价** · **禁 gap<=0** | **荐** — 真第三件；薄可施工 |
| **L · 本窗顺手成色（或双开精工+成色）/ 抬表** | 双开或动表 | **否决独批** — 不薄 / 硬禁抬表；成色后置 |
| **W · sx20 / 诚实半行族 / jar 改价 / 静默精工 / gap<=0 假条件** | — | **否决** — 硬禁 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a 顺手精工格必做；W1b Open/去哪花半行同批；W1c 确认路径对齐工坊；见 §3）
- [x] 否决 A（只口号）/ L（本窗成色双开·抬表）/ W（sx20/诚实空转/改价/gap<=0）
- [x] 批注：总控批 A·M · D13307 · TIP-CB · 派余烬-物品 TrMenu-only · 成色后置


---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 护甲灰 | `%corerpg_p1_held_is_armor% == 1`（TrMenu `check papi`） | **与工坊 R/r 同** |
| 花费展示 | `%corerpg_p1_held_next_c%` | 禁假写固定花费 |
| 缺料/进度 | `%corerpg_p1_held_refine_lack%`（够则空）+ `%corerpg_p1_held_refine_hope%` + `%corerpg_p1_recipe_gap_refine1%` 半行 | gap **是拼接串**；**禁止**用 `gap_refine1 <= 0` 当条件（非数字 · D13306 教训） |
| 命令 | `corerpg p1 refine craft` → `corerpg p1 refine craft confirm` | **与工坊同命令**；禁第二价轨 |
| 仓够亮/灰数字条件 | **本窗不做**（需 jar 新键） | 缺料诚实靠 lack + 命令失败；后置可选 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 顺手精工格（必做 · TrMenu）** | `ember_p1_afk` 空位新格（**荐 R/r**，避开 F/H/I/S/E/e/U/u/层格/B；布局示例：`#US4 V E#`→`#US4 VRE#` / `#ueI F H#`→`#ueI FrH#`，施工择空槽钉死）：名 `§6顺手精工` · lore 挂 next_c + lack + hope + gap_refine1 +「材料进仓库直扣」+「点一次预览·再确认」· actions 对齐工坊 refine craft→confirm；**护甲**灰「§8顺手精工 · 不可用」+ tell | 不改 E/e 顺手强化；不改 U/u 顺手升阶；不改未满/满额 F；不改层传送；不改 afk fight |
| **W1b · Open / 去哪花半行（必做同批）** | Open +半行「精工也可本页点」；「去哪花」lore +半行「本页也可顺手精工」（保留强化/升阶半行） | 不重开 D375 主交付；不重写 vault 主块 |
| **W1c · 确认与失败诚实（必做）** | 预览/确认两步与工坊同；缺料/手持不可精 → 既有 refine 失败 tell，**不**假成功 | 不改 UpgradeRules；不静默扣料 |
| **W1d · 顺手成色（本窗不做）** | — | 后置；本窗只钉精工一条 |
| **W1e · jar（默认不做）** | 不做 `recipe_gap_refine_ok` 数字键；TrMenu-only | 禁借号抬表/改价 |

### 3.3 玩家路径

| 步 | 路径 | 说明 |
|----|------|------|
| 挂 | 挂机庭打怪 · 料进仓 | 现网；**不改**产量 |
| 看 | 战况已见精工0→1 gap / hope | D415/D612 保留 |
| **花（本债）** | 点「顺手精工」→ 预览 → 确认 | W1a/W1c |
| 不可用 | 护甲灰 / 命令失败 tell | 与工坊同 |
| 深花 | 仍可走「去哪花」进工坊全功能 | D375 保留 |
| 强化/升阶 | E/e · U/u 保留 | D13304/D13306 不抢 |

**文案钉死示例（施工可微调色码，语义不得漂）：**

```
§6顺手精工
§7花费：§f%corerpg_p1_held_next_c%
§c%corerpg_p1_held_refine_lack%
§e进度：§f%corerpg_p1_held_refine_hope%
§7仓差：§f%corerpg_p1_recipe_gap_refine1%
§7仓扣仓库 · 与工坊同
§e➥ §f点预览 · 再确认
```

护甲态：

```
§8顺手精工 · 不可用
§8护甲随护符成长
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13304 顺手强化 E/e | **保留**；本债=精工，不改强化格 |
| D13306 顺手升阶 U/u | **保留**；本债=精工，不改升阶格 |
| D375 去哪花 | **保留**；本债=庭内精工 |
| D404/D415 仓差展示 | **保留**；不复述为主交付 |
| 工坊 refine craft | **同命令**；禁第二价轨 |
| 成色顺手 | **后置** |
| sx20 / D423 | **不做 / 不复开** |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 挂机庭见顺手精工预览/确认格；点开 lore 含真花费键 |
| V2 | 确认后扣仓与工坊同口径；精工成功或既有失败 tell |
| V3 | 护甲见灰态；**无**假成功 |
| V4 | `git show` / 配置 diff：**无**改 `daily_kills`/`afk.tiers`/UpgradeRules 价 |
| V5 | 玩家面无裸指令教学；TrMenu 点击完成 |
| V6 | E/e 顺手强化 / U/u 顺手升阶 / 满额 F / 层格 / 自动战斗与施工前一致 |
| V7 | **无** `recipe_gap_refine1 <= 0` 条件 |

---

## 5. 禁区

- 抬挂机产量/日顶/离线% · 改精工价 · 庭内静默自动精工 · 新挂机商店  
- sx20 · Pack6 · 天赋 · 灰印 · R · L13 · K3 · 伤乘 · skill_mult · Affix densify  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · 假开旧日常  
- 用 `recipe_gap_refine1 <= 0` 假条件（键非数字 · D13306 教训）  
- 复开 D423 · 复述 D375–D420 展示债为主交付 · stage 脏 runtime  

---

## 6. 后置（明文 · 本窗不做）

1. 庭内顺手成色（同形扩）  
2. 薄 jar：`recipe_gap_refine_ok`（0/1）以做仓够亮/灰同形强化  
3. 今日推短征轮换  
4. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-13 | 策划 · 初稿 STATUS=待批 A · 荐 M · TIP-CA 后连开 · 选题挂机庭顺手精工 · 拟 D13307 · ≠sx20 ≠抬日表 ≠复开 D423 · 禁 gap<=0 |
| 2026-10-13 | 总控批 A·M · tip `5eb12dbff` · 物品 live `fc94b0561` · STATUS=**已批 A · 方案 M** · **勾毕** · 成色后置 |

*D13307 · 挂机庭顺手精工 · **已批 A·方案 M** · CLOSED live menu `fc94b0561` · tip `5eb12dbff` · 物品施工 · ≠关观察 ≠抬日表 ≠sx20。*
