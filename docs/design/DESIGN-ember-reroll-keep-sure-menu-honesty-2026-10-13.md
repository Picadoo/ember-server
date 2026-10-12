# DESIGN · 洗练「更优档直留」菜单诚实（洗练 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** **CLOSED live** · TIP-BU · items `f59b9beae` · TrMenu reload 77 · jar 未改 `1.65.801-d13207` · 2026-10-13 00:56 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-reroll-keep-sure-menu-honesty-2026-10-13.md`](RESEARCH-ember-reroll-keep-sure-menu-honesty-2026-10-13.md)  
> **对接 live：** TIP-BT CLOSED live items `683011255` · `EmberRerollKeepSure` · `EmberGrowthService`「更优档直留 · 自动保留新」· `ember_p1_reroll.yml` N/K/O · power-paths SUR1  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功 / 互换免费确定再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零 forgeroll 权重 densify · 零伤乘 · 零抬表 · **零改 pity/pool/shard/币** · 零抬 RATE/upgradeCost · 零抬成色/精工 cost · 零抬 swap 收费 · 零改解锁数学 · 零 ConvertCap densify · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹/副手/强化率/升阶保留/形缓/转化/access_*/成色精工确定成功/互换免费确定再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

洗练说明与保留新格写出 **更优档直留 · 自动保留新**；与聊天同口径，不改 pity。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 聊天 / ActionBar | 已写「更优档直留 · 自动保留新」+「洗练 · 直留新档」 | **不动数值** |
| `EmberRerollKeepSure` | 更优/强抽不差 → 直留 | **不动** |
| 洗练 Icon `N` | 用料说明 · Shift · 点下方 — **无直留** | **用料 · 更优档直留·自动保留新 · 点下方/Shift锁**（≤3） |
| 洗练 Icon `K` | 仅 pending — **无「更优已直留」** | **pending · 更优已直留·本格自选**（≤3） |
| forge E/Q/R/U/S / 互换 | 已齐 / 冻再叠 | **本窗不叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 pity/pool）

| 键 | 规则 |
|----|------|
| 直留半行 | 固定 **`更优档直留 · 自动保留新`**（对齐聊天） |
| 待选半行 | 固定 **`更优已直留 · 本格自选`**（说明较差/同档仍手选） |
| 禁 | 改 `autoKeepNew` · 抬 pity · 扩池/权重 · 改 shard/币 · Affix densify |

### 2.2 PAPI

**本窗零新 PAPI**；继续 `%corerpg_p1_reroll_pending%` 等既有键。

### 2.3 TrMenu lore 定稿（≤3）

| 格 | lore 定稿 |
|----|-----------|
| **N** 说明 | `§7用重复件或碎片洗词条` / `§a更优档直留 · 自动保留新` / `§e➥ §f点下方按钮 · Shift锁词` |
| **K** 保留新 | `%corerpg_p1_reroll_pending%` / `§7更优已直留 · 本格自选较差/同档` |

- **O / a / b / c / d：本窗不改**（O 已有「不选=旧」；预览格离 forge 半行链）。  
- 每格 lore **≤3**；禁教学墙；禁把 pity 长句塞回第四行。  
- Shift 说明并入 N 第三行，腾出第二行给直留。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `ember_p1_reroll.yml` `N`/`K` | lore → §2.3 |
| `EmberRerollKeepSure` / `EmberGrowthService` 洗练结果 | **禁改** |
| forge E/Q/R/U/S / 互换 / soft / 开孔 / Affix / SF4 / access_* | **本窗不改** |

### 2.5 验收核（物品）

| 核 | 期望 |
|----|------|
| 说明格可见「更优档直留」与「自动保留新」 | 可见 |
| 保留新格可见「更优已直留」与 pending | 可见 |
| 每格 lore ≤3；无第四行 | 可核 |
| `git show` 无 pity/pool/weights/shard/币改 / Affix densify / soft·开孔·Anvil·SF4 / forge EQRUS·互换再叠 / 伤乘 / skill_mult / CoreRpg 业务 jar | PASS |

### 2.6 玩家口径（固定）

- 打开洗练页一眼看见 **更优档直留 · 自动保留新**。  
- 更优（或 pity 强抽不差）时系统已直留；待选时保留新格提示本格自选较差/同档。  
- 本窗不改 pity、不改池、不改花费。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**；**不派插件**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名·套装·宝石·天赋·烙纹·副手·强化率·升阶保留·形缓·转化·access_* / 成色精工确定成功 / 互换免费确定再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功 / 互换免费确定再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | hub/raid/abyss「通关才开」扫尾 | **仍冻**（access_* 再叠） |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选**（升阶邻链 · prefer 再离） |
| 10 | forgeroll 普通格成色权重半行 | **另 tip · 可选**（赌线 · 禁 ForgeChoice densify） |
| 11 | Affix densify / ConvertCap densify / 抬表 / forgeroll 权重 densify | **仍冻** |
| 12 | 分解菜单「确定」字面再压 | **另 tip · 可选**（弱） |
| 13 | 洗练 a/b/c/d 预览格挂 pity 一眼 | **另 tip · 可选**（本窗只 N+K · prefer 再离预览 densify） |

## STATUS: OPEN · TIP-BU · 荐批 A·M · 2026-10-13 00:53 Asia/Shanghai
