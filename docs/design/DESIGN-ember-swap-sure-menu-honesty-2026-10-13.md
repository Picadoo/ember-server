# DESIGN · 强化轨道互换「免费·确定」菜单诚实（工坊 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** APPROVED · TIP-BT · 批 A·M · 已派余烬-物品 · 不派插件 · 2026-10-13 00:50 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-swap-sure-menu-honesty-2026-10-13.md`](RESEARCH-ember-swap-sure-menu-honesty-2026-10-13.md)  
> **对接 live：** TIP-BS CLOSED live items `eba75771b` · `EmberForgeService.swapSlots`「免费，确定」+「预览直办」· `held_swap_cost` · `ember_p1_forge.yml` S/s · power-paths WS2  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · **零抬成色/精工 cost · 零抬 RATE/upgradeCost · 零改 swap 收费** · 零改解锁数学 · 零 ConvertCap densify · 零改 WEEKLY_CAP/MARK_TICKS · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹/副手/强化率/升阶保留/形缓/转化/access_*/成色精工确定成功再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

互换预览·确认格写出 **免费确定 · 预览直办**；与聊天同口径，不改价（本就免费）。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 聊天 / ActionBar | 已写「免费，确定」+「预览直办」 | **不动数值** |
| `held_swap_cost` | 已有「免费 · 交换强化等级+失败计数」 | **不动** |
| 工坊 Icon `S`/`s` | 花费口径 · 动作说明 — **无免费确定/预览直办** | **花费 · 免费确定·预览直办 · 点预览/确认**（≤3） |
| 成色/精工确定成功 / 强化率 / 升阶保留 | 已齐 / 冻再叠 | **本窗不叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 swap 数学/收费）

| 键 | 规则 |
|----|------|
| 免费确定半行 | 固定 **`免费确定 · 预览直办`**（对齐聊天；预览即结果） |
| 花费 | 继续 `%corerpg_p1_held_swap_cost%` |
| 禁 | 改 Cost.NONE · 收费化 · soft MULT · 新发明价表 · 改 swapSlots 数学 |

### 2.2 PAPI

**本窗零新 PAPI**；只用既有 `held_swap_cost`。

### 2.3 TrMenu lore 定稿（≤3）

| 格 | lore 定稿 |
|----|-----------|
| **S** 互换·预览 | `§e%corerpg_p1_held_swap_cost%` / `§a免费确定 §8· §f预览直办` / `§e➥ §f点预览` |
| **s** 互换·确认 | `§e%corerpg_p1_held_swap_cost%` / `§a免费确定 §8· §f预览直办` / `§e➥ §f点确认` |

- 甲灰显子 icon：**本窗可不改**（仍护甲不可用三行）；勿第四行。  
- **禁**改强化 E/e / 升阶 U/u / 成色 Q/q / 精工 R/r / 稳赌 / 旁路 / 配方速览 / access 菜单本窗扩面。  
- 每格 lore **≤3**；禁教学墙；禁把「主手与副手…」长说明塞回第四行（「免费确定·预览直办」已够；细节仍由聊天预览承担）。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `ember_p1_forge.yml` `S`/`s` | lore → §2.3 |
| `EmberForgeService.swapSlots` / `EmberGearNextHint.swapLine` | **禁改** |
| 成色/精工确定成功 / 升阶保留 / 强化率 / soft / 开孔 / Affix / SF4 / access_* | **本窗不改** |

### 2.5 验收核（物品）

| 核 | 期望 |
|----|------|
| 互换预览/确认可见「免费确定」与「预览直办」 | 可见 |
| 花费行（免费口径）仍可见 | 可见 |
| 每格 lore ≤3；无第四行 | 可核 |
| `git show` 无 swap 收费化 / cost/hope/RATE/upgradeCost 改 / Affix densify / soft·开孔·Anvil·SF4 / access_* / 成色精工确定成功·升阶保留·强化率·转化·形缓再叠 / 伤乘 / skill_mult / CoreRpg 业务 jar | PASS |

### 2.6 玩家口径（固定）

- 点互换预览或确认前一眼看见 **免费确定 · 预览直办** 与免费花费口径。  
- 本窗 **不**改互换为收费、不改交换内容（强化等级+失败计数）、不改日首加赠。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**；**不派插件**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名·套装·宝石·天赋·烙纹·副手·强化率·升阶保留·形缓·转化·access_* / 成色精工确定成功再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | hub/raid/abyss「通关才开」扫尾 | **仍冻**（access_* 再叠） |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选**（升阶邻链 · prefer 再离） |
| 10 | forgeroll 普通格成色权重半行 | **另 tip · 可选**（赌线 · prefer 再离） |
| 11 | Affix densify / ConvertCap densify / 抬表 | **仍冻** |
| 12 | 分解菜单「确定」字面再压 | **另 tip · 可选**（弱 · 已有 yield 预览） |

## STATUS: APPROVED · TIP-BT · 批 A·M · 已派余烬-物品 · 不派插件 · 2026-10-13 00:50 Asia/Shanghai
