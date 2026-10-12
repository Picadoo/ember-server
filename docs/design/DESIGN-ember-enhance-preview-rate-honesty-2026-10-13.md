# DESIGN · 强化预览成功率菜单诚实（工坊 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** APPROVED · TIP-BN · 已派插件+物品 · 2026-10-13 00:08 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-enhance-preview-rate-honesty-2026-10-13.md`](RESEARCH-ember-enhance-preview-rate-honesty-2026-10-13.md)  
> **对接 live：** TIP-BM CLOSED live items `91300ad65` · `EmberUpgradeRules.RATE` · `EmberForgeService.enhance` 聊天预览 · `ember_p1_forge.yml` E/e · `EmberRunPapi` held_enhance_* · power-paths WS2  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量数字再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · **零抬 RATE/MAX_TRIES/cost** · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹/副手幅度展示再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

强化预览/确认格用 **现网 RATE** 写出下一档成功率；与聊天预览同数，不改表。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `RATE[]` / 聊天预览 | 成功率已算、已说 | **不动数值** |
| 工坊 Icon `E`/`e` | 花费 + 保底 · **无率** | **花费 · 成功率 · 保底**（≤3 行） |
| PAPI | cost / pity / lack · **无 rate** | **新 `held_enhance_rate`** |
| soft / Affix / SF4 / 副手数字 | 已齐 / 冻 | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 RATE）

| 键 | 规则 |
|----|------|
| 目标档 `t = enhance+1` | `ratePct = round(RATE[t]*100)` |
| 本次必成 | `EmberUpgradeRules` 保底将强制成功 → 展示 **`必成`**（与聊天「本次必成」同口径） |
| 不可强 / 甲 / 空手 | 短空位（同构 pity empty：`手持—` / `甲不可强` / 错误短句） |
| 禁 | 手写漂移表 · 抬 RATE · 抬 MAX_TRIES · 改 cost |

### 2.2 PAPI（插件）

| id | 返回例 | 备注 |
|----|--------|------|
| `corerpg_p1_held_enhance_rate` | `70%` / `必成` / `§8手持—` | 新键；登记 `EmberRunPapi` + 单测；**零改**既有 pity/cost |

可选紧凑（若物品岗合行需要）：同一实现可被 lore 拼 `成功率：%rate% · %pity%`。

### 2.3 TrMenu lore 定稿（≤3）

| 格 | lore 定稿 |
|----|-----------|
| **E** 强化·预览 | `§e花费：§f%corerpg_p1_held_enhance_cost%` / `§e成功率：§f%corerpg_p1_held_enhance_rate% §8· %corerpg_p1_held_enhance_pity%` / `§e➥ §f点预览` |
| **e** 强化·确认 | `§e花费：§f%corerpg_p1_held_enhance_cost%` / `§e成功率：§f%corerpg_p1_held_enhance_rate% §8· %corerpg_p1_held_enhance_pity%` / `§e➥ §f点确认` |

- 甲灰显子 icon：**本窗可不加率**（仍花费+缺料）；勿第四行。  
- **禁**改升阶/精工/成色/稳赌/旁路/配方速览本窗扩面。  
- **禁**改 afk 页 pity 主块（仍用既有 `held_enhance_pity`）。  
- 每格 lore **≤3**；禁教学墙。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `EmberRunPapi` | 登记 `held_enhance_rate`；读手持 → `RATE` / 必成判定 |
| 单测 `EmberRunPapiTest`（或邻测） | 可强件回 `N%`；必成回 `必成`；空/甲空位 |
| `ember_p1_forge.yml` `E`/`e` | lore → §2.3 |
| `EmberUpgradeRules.RATE` / cost / pity / CostSoft | **禁改** |
| soft / 开孔 / Affix / SF4 / 副手·烙纹·签名… | **本窗不改** |

### 2.5 验收核（插件+物品）

| 核 | 期望 |
|----|------|
| 预览格可见 `成功率` 且与聊天预览同档 % | 可见 |
| 保底将成时可见 `必成`（或等价） | 可见 |
| 确认格同构有率 | 可见 |
| 每格 lore ≤3；无第四行 | 可核 |
| `git show` 无 RATE/MAX_TRIES/cost 改 / Affix densify / soft·开孔·Anvil·SF4 / 签名·套装·宝石·天赋·烙纹·副手幅度再叠 / 伤乘 / skill_mult | PASS |

### 2.6 玩家口径（固定）

- 点强化预览/确认前一眼看见 **下一档成功率**（或本次必成）与保底进度。  
- 本窗 **不**改成功率表、不改材料价、不改保底次数。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-插件** + **余烬-物品**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名·套装·宝石·天赋·烙纹·副手幅度再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量数字再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | pin 预览气泡补档区间（pctList） | **另 tip · 可选**（烙纹链 · prefer 再离） |
| 9 | 成色/精工/升阶预览再压 WS2 同构 | **另 tip · 可选**（本窗只强化随机档） |
| 10 | Affix densify / 抬 RATE | **仍冻** |
| 11 | SF4 U1 技能页形缓展示诚实 | **另 tip · 可选**（离接线链） |

## STATUS: APPROVED · TIP-BN · 已派插件+物品 · 2026-10-13 00:08 Asia/Shanghai
