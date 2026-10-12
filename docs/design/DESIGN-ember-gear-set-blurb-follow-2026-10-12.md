# DESIGN · 装备页套装效果行跟随成套族诚实（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-BI · 已派余烬-插件+余烬-物品 · 2026-10-12 23:34 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-gear-set-blurb-follow-2026-10-12.md`](RESEARCH-ember-gear-set-blurb-follow-2026-10-12.md)  
> **对接 live：** TIP-BH CLOSED live `1.65.797-d13047` · TIP-P 觉醒白话 · D325 四件套 · `ember_p1_gear.yml` · `EmberItemData.familyBlurb` · `EmberLoadout.setLabel` · `EmberRunPapi` loadout  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore 再叠 **仍冻**  
> **硬禁：** 零功率表 · 零改 SetRules 数组 · 零改 Formula / `sustain_hp_mult` · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 Affix/伤乘/skill_mult/sx20

---

## 0. 一句话

装备页套装格效果行跟随 **当前成套族**（`familyBlurb`），并修正炽愈 ×1.12 谎称；不改套装战斗数字。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 标题 `%awaken%` | 动态「焚烬 觉醒II」 | **不动** |
| 效果行 | **写死炽愈** | **跟随 activeSet** |
| `familyBlurb(sustain)` | 写 ×1.12 | **对齐 D293（无 ×1.12）** |
| 四件套行 | `%armor_set%` 诚实 | **保留** |
| SetRules / Formula | live | **零改数值** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 文案（只改正字 · 禁改系数）

```
// EmberItemData.familyBlurb — 修正 sustain 谎称；scorch/burst 句式保留
familyBlurb("scorch")  // 既有：每 3 次普攻点燃…（不改）
familyBlurb("burst")   // 既有：每 5 次普攻炸开…（不改）
familyBlurb("sustain") // 旧：生命 ×1.12，每 5 次普攻回… 
                       // 新：生命 ×1.00，每 5 次普攻回最大生命 2.5%（觉醒后最多 4%，6 秒一次），最稳
                       // （与现网 gear 静态行 / D293 sustain_hp_mult=1.00 对齐；禁改 yml 数值）

// EmberRunPapi loadout 新键（薄 · 非 remain glance twin）
"set_blurb":
  l = loadout
  if l.activeSet is none/empty → "§7刃+护符同族启用套装效果"
  else → familyBlurb(l.activeSet)   // 可加族色前缀；禁贴 SetRules 数字墙
```

- **禁止**改 `BURN_COEF` / `BURST_*` / `SUSTAIN_PCT` / `sustain_hp_mult` / every / ICD。  
- **禁止**签名 applyLore 再叠；**禁止**调律 live bad 进持件。  
- `set_run` / TIP-P awkDelta **本窗不改语义**（正交）。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberItemData.familyBlurb` | sustain 句对齐 ×1.00 |
| `EmberRunPapi` loadout | 增 `set_blurb`（路由进既有 LOADOUT 段） |
| `ember_p1_gear.yml` `S` lore | 效果行 → `%corerpg_p1_set_blurb%`；保留 `%armor_set%` |
| help / set / FamilyPath | 自然吃修正后 familyBlurb（聊天同口径） |
| SetRules / Formula / ember-v1.yml 数值 | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| familyBlurb(sustain) | **无**「1.12」；含回血 / 最稳类字；可含 ×1.00 |
| familyBlurb(scorch/burst) | 非空；与改前同主题 |
| PAPI set_blurb · activeSet=scorch | 含「焚烬」或点燃类字；**无**「炽愈」主句 |
| PAPI set_blurb · activeSet=none | 含「同族启用」类提示 |
| `git show` | 无 SetRules 数组 / Formula / sustain_hp_mult / DEFS.mods / soft / Anvil / SF4 / 签名 appendHeld 再改；无甲闸/SetChase densify |

### 2.4 玩家口径（固定）

- 打开装备页套装格：标题是哪套，效果行就说 **那套** 干什么。  
- 炽愈不再谎称生命 ×1.12。  
- 四件套受伤 −3% 行照旧；本窗不改伤害表。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-插件**（familyBlurb + set_blurb + 单测）+ **余烬-物品**（gear.yml）  
- L13 施工仍 HOLD；Anvil / soft / SF4 U2U3 / 灰印 / 调律 live bad / 签名 lore 再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore 再叠 | **仍冻** |
| 7 | 宝石镶嵌效果持件/菜单细述 | 另 tip · 非本窗 |

## STATUS: APPROVED · TIP-BI · 已派余烬-插件+余烬-物品 · 2026-10-12 23:34 Asia/Shanghai
