# DESIGN · 分解日首得胚+1（YieldBoost）菜单/预览诚实（工坊 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** APPROVED · TIP-BX · 批 A·M · 已派余烬-插件 + 余烬-物品 · 2026-10-13 01:15 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-dismantle-yield-boost-menu-honesty-2026-10-13.md`](RESEARCH-ember-dismantle-yield-boost-menu-honesty-2026-10-13.md)  
> **对接 live：** TIP-BW CLOSED live `1.65.802-d13287` · `EmberDismantleYieldBoost` · `EmberForgeService.dismantle` · `EmberGearNextHint.dismantleYieldLine` · `EmberRunPapi.held_dismantle_yield` · `ember_p1_forge.yml` D/d  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad / 签名盖章 pity n/N 再叠 / 签名效果 lore / 套装 blurb / 宝石 / 天赋 / 烙纹档幅度 / 副手 / 强化率 / 升阶保留 / 形缓 / 转化 / access_* / 成色·精工确定成功 / 互换 / 洗练直留 / 烙纹直办 / soft·开孔再叠 / UndoWindow densify / **抬 BONUS · 改 dismantleYield 表** **仍冻**  
> **硬禁：** 零改 BONUS/C_USED · 零改 dismantleYield 表 · 零 Affix densify · 零伤乘 · 零抬表 · 零抬 STAMP · 零扩 stampable · 零 SigStamp densify · 零签名盖章 pity / 效果 lore 再叠 · 零 sure·keep·直办半行再叠 · 零 soft·开孔再叠 · 零 Anvil densify · 零 SF4 U2U3 · 零灰印/聚火 · 零 UndoWindow densify · 零 ConvertCap densify · 零 forgeroll 权重 densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

菜单 `%held_dismantle_yield%` 与聊天预览同吃 **YieldBoost peek**：日首待用写清得胚+1；不改增产常数。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| YieldBoost | BONUS=1 日首非甲 · 成交 consume | **不动数学** |
| 聊天预览 | `!go` 已 peek boost 后数字 | **保留** |
| 菜单 PAPI | 只报表额得胚 | **对齐 boost peek + 待用尾** |
| Icon d | 「确认在聊天栏」无得胚 | **得胚行 + 聊天确认短句** |
| sure/pity 半行族 / 盖章 n/N | 已齐或刚 CLOSED | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 BONUS）

| 键 | 规则 |
|----|------|
| 表额 | `EmberUpgradeRules.dismantleYield(d)` · **禁改** |
| peek | `EmberDismantleYieldBoost.apply(pd, base, false)` · **禁**在菜单路径 consume |
| ready 句 | 展示得胚 = peek 结果；尾缀明示「今日首拆+1待用」（或等价 ≤1 短尾） |
| 已用句 | 得胚 = 表额 · 无待用尾 |
| 甲 | `SixPapi.heldArmorLine` · **本窗不改** |
| 禁 | 抬 BONUS · 改 C_USED 周期 · 甲也加日首 · 假写固定「必+1」当已用 |

### 2.2 插件

| 点 | 动作 |
|----|------|
| `EmberGearNextHint.dismantleYieldLine` | 增 `PlayerData` 重载（或改签名）：不可拆→check 短句；甲勿走此路径；非甲 ready→`得胚×(base+BONUS) · 今日首拆+1待用`；否则 `得胚×base` |
| `EmberRunPapi.heldForgeLine("dismantle")` | 传入已取 softPd；甲仍 SixPapi |
| 单测 | ready→含「+1」或「首拆」且数字=base+1；consume 后→表额；peek 不 latch C_USED；不可拆短句回归 |
| `EmberDismantleYieldBoost` / BONUS | **禁改** |

零新 PAPI 键（修既有 `held_dismantle_yield`）。

### 2.3 TrMenu lore / Open 定稿（≤3）

| 点 | 定稿 |
|----|------|
| **Icon D（预览）** | lore：`§e预览：§f%corerpg_p1_held_dismantle_yield%` / `§7只限普通掉落件；甲→白板胚零头` /（第三行可留空或 `§e➥ §f点预览·聊天确认`）· **≤3**；吃对齐后 PAPI |
| **Icon d（确认指引）** | lore：`§e%corerpg_p1_held_dismantle_yield%` / `§7点上面预览后，聊天栏 §c[确认分解]` / `§8首通/起步件拆不了`（≤3；用得胚行替换「无数字」空话） |
| **Open tell（可选同批）** | +半行 `§8分解预览可见今日首拆+1待用（不改表）`；禁刷屏墙 |
| **E/Q/R/U/S/brand/reroll/sig** | **本窗不改** |

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| GearNextHint + RunPapi | §2.2 |
| `ember_p1_forge.yml` D/d（+可选 Open） | §2.3 |
| ForgeService.dismantle 聊天预览 | **本窗可不改**（已 peek）；若预览静默 boost 数字可另 tip 加「今日首拆」聊天尾——**非本窗必做** |
| YieldBoost / dismantleYield 表 / Affix / soft·开孔 / STAMP | **禁改** |
| sure/keep/直办半行 · 签名盖章 pity | **本窗不改** |

### 2.5 验收核（插件+物品）

| 核 | 期望 |
|----|------|
| 日首未拆 · 持可拆非甲 · Icon D/d 见得胚×(表额+1) 与待用尾 | 可见 |
| 日首已拆 · 见得胚×表额 · 无待用尾 | 可核 |
| 与聊天 `!go` peek 数字同口径 | 可核 |
| lore≤3；甲零头句未改 | 可核 |
| `git show` 无 BONUS/dismantleYield 表 / Affix / soft·开孔·Anvil·SF4 / sure 半行再叠 / 伤乘 / skill_mult / STAMP | PASS |

### 2.6 玩家口径（固定）

- 打开工坊看分解：今日还没拆过普通掉落件时，预览写清 **多 1 胚 · 今日首拆待用**。  
- 今日已用过增产：只见表额得胚。  
- 本窗 **不**改分解得胚表、不改日首+1 常数。  
- 仍守 lore≤3；确认仍在聊天栏点（不把销毁确认搬回菜单硬点）。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-插件** + **余烬-物品**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名盖章 pity·效果 lore·套装·宝石·天赋·烙纹·副手·强化率·升阶·形缓·转化·access_* / sure·keep·直办半行再叠 / **抬 BONUS · 改 dismantleYield** 仍冻

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名盖章 pity n/N / 效果 lore / 套装 blurb / 宝石 / 天赋 / 烙纹档幅度 / 副手 / 强化率 / 升阶保留 / 形缓 / 转化 / access_* / sure·keep·直办半行再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | hub/raid/abyss「通关才开」扫尾 | **仍冻**（access_* 再叠） |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选** |
| 10 | forgeroll 普通格成色权重半行 | **另 tip · 可选**（禁 ForgeChoice densify） |
| 11 | Affix densify / ConvertCap densify / 抬表 / forgeroll 权重 densify / 抬 STAMP / 扩 stampable / **抬 BONUS** | **仍冻** |
| 12 | 分解聊天预览补「今日首拆」字面（数字已对齐时可发） | **另 tip · 可选** |
| 13 | UndoWindowSoft 菜单剩余分钟一眼 | **另 tip · 可选**（禁 densify 窗长） |
| 14 | AFK 层差 dwell cue 再叠 | **另 tip · 可选**（非抬日表 · 链已厚 prefer 观察） |
| 15 | 本窗 YieldBoost 菜单诚实再叠 / celebration | **本窗后冻再叠** |

## STATUS: OPEN · TIP-BX · 荐批 A·M · 2026-10-13 01:14 Asia/Shanghai
