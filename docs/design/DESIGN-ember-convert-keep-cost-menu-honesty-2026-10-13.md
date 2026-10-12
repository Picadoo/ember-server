# DESIGN · 每周转化保留/代价菜单诚实（转化 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** APPROVED · TIP-BQ · 批 A·M · 已派余烬-物品 · 不派插件 · 2026-10-13 00:29 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-convert-keep-cost-menu-honesty-2026-10-13.md`](RESEARCH-ember-convert-keep-cost-menu-honesty-2026-10-13.md)  
> **对接 live：** TIP-BP CLOSED live items `ae8ad1c70` · jar 未改 `1.65.800-d13143` · `EmberConvertRules.plan` · `EmberGrowthService.convertHeld` 聊天预览 · `ember_p1_convert.yml` · forge Icon `V` · power-paths · TIP-AW CapSoft live（**禁 densify**）  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A 再叠 / soft·开孔再叠 / ConvertCap densify **仍冻**  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · **零改 WEEKLY_CAP / blanks / coins / QUALITY_CAP / plan** · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零改 MARK_TICKS/skill_mult · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹/副手/强化率/升阶保留/形缓展示再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · **零 ConvertCap densify** · 零 sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

转化页目标族格与说明写出 **保留 · 代价**；与聊天 `convertHeld` 预览同口径，不改周帽、不改价。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `EmberConvertRules.plan` / 聊天预览 | 已写保留/代价/花费 | **不动数值** |
| 转化页 Icon `1`/`2`/`3` | 仅族名 · **无 lore** | **保留 · 代价 · 点选 cue**（≤3） |
| 说明 `N` / Open tell / forge `V` | 偏代价或空 | **补保留半句（薄）** |
| CapSoft / CostSoft | TIP-AW/AT 已齐 | **本窗禁 densify** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 convert 数学）

| 键 | 规则 |
|----|------|
| 保留半行 | **`保留精工/词条/签名/孔 · uid 不变`**（对齐 plan + 聊天；锻造次数/保底含于「词条/保底」语义，菜单短写） |
| 代价半行 | **`强化归零 · 成色截卓越`** |
| 花费 | 仍聊天预览 + `%corerpg_p1_forge_gap_convert%`；**禁**本窗新价 PAPI |
| 禁 | 改 WEEKLY_CAP / blanks / coins / QUALITY_CAP / plan / CapSoft / CostSoft MULT |

### 2.2 PAPI

**本窗零新 PAPI**。

### 2.3 TrMenu lore 定稿（≤3）

| 格 | lore 定稿 |
|----|-----------|
| **1** 转到·焚烬 | `§7同阶同部位改族` / `§a保留精工/词条/签名/孔 · §c强化归零 · 成色截卓越` / `§e➥ §f点选 · 聊天预览` |
| **2** 转到·烬爆 | 同构 |
| **3** 转到·承烬 | 同构 |
| **N** 说明 | `§7每周 1 次 · 手持刃/护符` / `§a保留精工/词条/签名/孔 · uid 不变` / `§c还差：§f%corerpg_p1_forge_gap_convert%` |
| **V** 工坊入口（可选同窗） | `§7跨族改底 · 每周 1 次` / `§a保留精工/词条 · §c强化归零 · 成色截卓越` / `§e➥ §f打开转化页` |

- Open tell：建议改为 `§8手持刃/护符 · 每周 1 次 · 保留精工/词条/签名/孔 · 强化归零 · 成色截卓越`（仍 ≤1 行 tell；**非**第四 lore）。  
- 每格 lore **≤3**；禁教学墙；禁把 CapSoft「容错」再叠进目标格（AW 已齐）。  
- **禁**改强化 E/e / 升阶 U/u / 成色/精工 / 形缓 skill_kit A 本窗。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `ember_p1_convert.yml` `1`/`2`/`3`/`N` + Open tell | lore/tell → §2.3 |
| `ember_p1_forge.yml` `V` | 可选同窗 lore → §2.3 |
| `EmberConvertRules` / CapSoft / CostSoft / convertHeld | **禁改** |
| 形缓 / 升阶保留 / 强化率 / soft / 开孔 / Affix / SF4 | **本窗不改** |

### 2.5 验收核（物品）

| 核 | 期望 |
|----|------|
| 三目标格可见保留 + 代价 | 可见 |
| 说明格可见保留 + 还差 | 可见 |
| forge `V`（若改）可见保留半句 | 可见 |
| 每格 lore ≤3；无第四行 | 可核 |
| `git show` 无 WEEKLY_CAP/价/plan/CapSoft 改 / Affix densify / soft·开孔·Anvil·SF4 / 形缓·升阶保留·强化率·签名·套装·宝石·天赋·烙纹·副手再叠 / 伤乘 / skill_mult / CoreRpg 业务 jar | PASS |

### 2.6 玩家口径（固定）

- 点转化目标族前一眼看见 **保留什么、失去什么**（强化归零 · 成色截卓越）。  
- 本窗 **不**改每周次数、不改胚币价、不改成色截档、不改 CapSoft。  
- 点选后仍走既有聊天预览/确认（或 ConvertSure 直办）；菜单只提前对齐。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**；**不派插件**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 形缓·升阶保留·强化率·签名·套装·宝石·天赋·烙纹·副手再叠 / ConvertCap densify 仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手 / 强化率 / 升阶保留 / 形缓 Icon A 再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | 形状格 D/E/F 形缓半行 | **另 tip · 可选**（形缓链 · prefer 再离） |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选**（升阶邻链 · prefer 再离） |
| 10 | playstyle 入口半行点名形缓 | **另 tip · 可选** |
| 11 | Affix densify / ConvertCap densify / 抬 WEEKLY_CAP | **仍冻** |
| 12 | 成色/精工「确定成功」字面再压 | **另 tip · 可选**（hope 已齐 · 弱） |

## STATUS: APPROVED · TIP-BQ · 批 A·M · 已派余烬-物品 · 不派插件 · 2026-10-13 00:29 Asia/Shanghai
