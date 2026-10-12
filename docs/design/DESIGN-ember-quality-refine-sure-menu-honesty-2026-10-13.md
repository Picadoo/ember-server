# DESIGN · 成色/精工「确定成功」菜单诚实（工坊 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** **OPEN** · TIP-BS · 荐批 A·M · 2026-10-13 00:43 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-quality-refine-sure-menu-honesty-2026-10-13.md`](RESEARCH-ember-quality-refine-sure-menu-honesty-2026-10-13.md)  
> **对接 live：** TIP-BR CLOSED live `1.65.801-d13207` · `EmberForgeService.simple` 成色/精工「确定成功，预览即结果」· `held_next_q`/`held_next_c`/`held_quality_hope`/`held_refine_hope` · `ember_p1_forge.yml` Q/q/R/r · power-paths WS2 · BR 旁债 #8  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* 再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · **零抬成色/精工 cost · 零抬 RATE/upgradeCost** · 零改解锁数学 · 零 ConvertCap densify · 零改 WEEKLY_CAP/MARK_TICKS · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹/副手/强化率/升阶保留/形缓/转化/access_* 再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

成色/精工预览·确认格第二行写出 **确定成功**（合希望进度）；与聊天同口径，不改价。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 聊天 / ActionBar | 已写「确定成功，预览即结果」 | **不动数值** |
| `held_next_q` / `held_next_c` | 已有花费行 | **不动** |
| hope PAPI | 已有进度 | **保留可见** |
| 工坊 Icon `Q`/`q`/`R`/`r` | 花费 · 进度 · 点预览 — **无确定成功** | **花费 · 确定成功·进度 · 点预览/确认**（≤3） |
| 升阶保留 / 强化率 / MG2 access | 已齐 / 冻再叠 | **本窗不叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 cost/hope）

| 键 | 规则 |
|----|------|
| 确定成功半行 | 固定 **`确定成功`** 前缀（对齐聊天；预览即结果） |
| 希望进度 | 继续 `%corerpg_p1_held_quality_hope%` / `%corerpg_p1_held_refine_hope%` |
| 花费 | 继续 `%corerpg_p1_held_next_q%` / `%corerpg_p1_held_next_c%` |
| 禁 | 改 C05/C06 · quality/refine cost · hope MAX · RATE · soft MULT · 新发明价表 |

### 2.2 PAPI

**本窗零新 PAPI**；只用既有 next_q/c · quality/refine_hope · lack。

### 2.3 TrMenu lore 定稿（≤3）

| 格 | lore 定稿 |
|----|-----------|
| **Q** 成色·预览 | `§e花费：§f%corerpg_p1_held_next_q%` / `§a确定成功 §8· §e进度：§f%corerpg_p1_held_quality_hope%` / `§e➥ §f点预览` |
| **q** 成色·确认 | `§e花费：§f%corerpg_p1_held_next_q%` / `§a确定成功 §8· §e进度：§f%corerpg_p1_held_quality_hope%` / `§e➥ §f点确认` |
| **R** 精工·预览 | `§e花费：§f%corerpg_p1_held_next_c%` / `§a确定成功 §8· §e进度：§f%corerpg_p1_held_refine_hope%` / `§e➥ §f点预览` |
| **r** 精工·确认 | `§e花费：§f%corerpg_p1_held_next_c%` / `§a确定成功 §8· §e进度：§f%corerpg_p1_held_refine_hope%` / `§e➥ §f点确认` |

- 甲灰显子 icon：**本窗可不改**（仍手持件+缺料）；勿第四行。  
- **禁**改强化 E/e / 升阶 U/u / 稳赌 / 旁路 / 配方速览 / access 菜单本窗扩面。  
- 每格 lore **≤3**；禁教学墙；禁把「预览直办」长文塞第四行（「确定成功」已够）。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `ember_p1_forge.yml` `Q`/`q`/`R`/`r` | lore → §2.3 |
| `EmberForgeService` / UpgradeRules cost / hope | **禁改** |
| 升阶保留 / 强化率 / soft / 开孔 / Affix / SF4 / access_* | **本窗不改** |

### 2.5 验收核（物品）

| 核 | 期望 |
|----|------|
| 成色预览/确认可见「确定成功」 | 可见 |
| 精工预览/确认同构 | 可见 |
| 花费行与希望进度仍可见 | 可见 |
| 每格 lore ≤3；无第四行 | 可核 |
| `git show` 无 cost/hope/RATE/upgradeCost 改 / Affix densify / soft·开孔·Anvil·SF4 / access_* / 升阶保留·强化率·转化·形缓再叠 / 伤乘 / skill_mult / CoreRpg 业务 jar | PASS |

### 2.6 玩家口径（固定）

- 点成色/精工预览或确认前一眼看见 **确定成功**（预览即结果）与花费、希望进度。  
- 本窗 **不**改成色/精工材料价、不改希望保底次数、不改日首减负。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**；**不派插件**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名·套装·宝石·天赋·烙纹·副手·强化率·升阶保留·形缓·转化·access_* 再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* 再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | hub/raid/abyss「通关才开」扫尾 | **仍冻**（access_* 再叠） |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选**（升阶邻链 · prefer 再离） |
| 10 | forgeroll 普通格成色权重半行 | **另 tip · 可选**（赌线 · prefer 再离） |
| 11 | Affix densify / ConvertCap densify / 抬表 | **仍冻** |

## STATUS: OPEN · TIP-BS · 荐批 A·M · 2026-10-13 00:43 Asia/Shanghai
