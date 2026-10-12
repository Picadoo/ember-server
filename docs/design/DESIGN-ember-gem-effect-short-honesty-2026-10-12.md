# DESIGN · 宝石效果短诚实（合成/镶嵌/石物 lore · 薄窗 · 可施工）· 2026-10-12

> **状态：** CLOSED live `1.65.799-d13079` · TIP-BJ · feat `72cba2db4` · items `bc1e63407` · 2026-10-12 23:46 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-gem-effect-short-honesty-2026-10-12.md`](RESEARCH-ember-gem-effect-short-honesty-2026-10-12.md)  
> **对接 live：** TIP-BI CLOSED live `1.65.798-d13063` · `EmberGems` · `EmberGemSeatLive` · `ember_p1_gem.yml` · `ember-enhance-gems.yml` · `ember_socket.yml`  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore 再叠 / 套装 blurb 再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零功率表 · 零抬 gem flat/CAP · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad · 零套装 blurb 再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 Affix/伤乘/skill_mult/sx20 · 零效果长墙

---

## 0. 一句话

合成/镶嵌/石物用 **`EmberGems` 常量** 写 **≤1 行短效果**（锋利含刃 T2+）；不抬宝石数字。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `EmberGems` 常量 | live SoT | **不动数值** |
| 座椅 `effectLine` | 无幅度 · 仅镶后 cue | **同 SoT 短句 · 含幅度** |
| NI 石物 lore | 「微量××」 | **短效果 + 锋利 T2+** |
| 合成/镶入菜单 | 族名/材料 only | **加半行短效果** |
| 持件 `socketLoreLine` | 孔名 | **本窗可不动**（禁扩字墙） |
| soft / 开孔 / pity | 已齐 | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 短句（只读常量 · 禁改数值）

```
// EmberGems.shortEffect(code) 或 EmberGemSeatLive.effectLine 升级为同口径
// 幅度字符串必须由 SHARP_ATK / STEADY_DEF / DRAIN_LS / GALE_MS / SHARP_MIN_TIER 格式化，禁手写漂移

CODE_SHARP  → "锋利 · 攻击+" + fmt(SHARP_ATK) + "（刃 T" + SHARP_MIN_TIER + "+ 才计）"
CODE_STEADY → "稳固 · 防御+" + fmt(STEADY_DEF)
CODE_DRAIN  → "汲取 · 吸血+" + pct(DRAIN_LS)   // 0.01 → 1%
CODE_GALE   → "疾风 · 移速+" + pct(GALE_MS)    // 0.02 → 2%
其它        → null

// 可选页首半行（菜单 Open tell 一条即可，非每格重复）：
// "四石合计封顶：攻/防各 +0.2 · 吸血 5% · 移速 8%（不改表）"
```

- **禁止**改 `SHARP_ATK` / `STEADY_DEF` / `DRAIN_LS` / `GALE_MS` / `CAP_*` / `SHARP_MIN_TIER`。  
- **禁止** soft / 开孔门槛 / pity / DELTA 再叠。  
- 座椅 one-shot cue **复用**同一 `shortEffect`（玩家镶后听到的与菜单一致）。  
- 命名保持石物「锋利/稳固/汲取/疾风」；不借机改 path 尖「锋刃/稳御/汲魂」大清洗。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberGems` 或 `EmberGemSeatLive` | `shortEffect` / 升级 `effectLine` |
| 单测 | 四码非空；sharp 含 `T2`/`T`+MIN；无「微量」；幅度跟常量 |
| `ember-enhance-gems.yml` | 四石 lore 第 1 行 → 定稿短句；第 2 行可留「刃/护符孔可用」 |
| `ember_p1_gem.yml` | 合成四格 + 镶入格各加 **一行** 短效果（≤1）；禁堆第二段教学 |
| `ember_socket.yml` | 「微量××」→ 同短句（若入口仍在） |
| 可选 PAPI | `gem_fx_sharp` 等只读短句；**非** remain glance twin；无则菜单抄 DESIGN 定稿亦可 |
| `socketLoreLine` / 持件孔名 | **默认不动** |
| 数值常量 / soft / 开孔 / pity | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| shortEffect(SHARP) | 含攻击幅度字 + `T2`（或 MIN_TIER）；**无**「微量」 |
| shortEffect(STEADY/DRAIN/GALE) | 含防御/吸血%/移速% 类字；非空 |
| shortEffect(0) | null |
| `git show` | 无 SHARP_ATK 等赋值改；无 soft/开孔/DELTA/pity/Anvil/SF4/签名/套装 SetRules；无 Affix/伤乘/skill_mult |

### 2.4 玩家口径（固定）

- 看石物/合成/镶入：一眼知道这颗石 **加什么、加多少**；锋利写明 **刃 T2+ 才计入攻击**。  
- 本窗 **不**提高宝石加成、不改开孔门槛、不改保底。  
- 孔位上仍可只显示石名；要细看去合成/镶嵌页短行。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-插件**（shortEffect + seat cue 对齐 + 单测）+ **余烬-物品**（NI 四石 + gem 菜单 ± socket）  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名 lore / 套装 blurb 再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb 再叠 | **仍冻** |
| 7 | soft·开孔·GemHole 再叠 | **仍冻** |
| 8 | 持件孔位逐孔效果长墙 | **另 tip · 禁本窗扩** |

## STATUS: CLOSED live `1.65.799-d13079` · TIP-BJ · feat `72cba2db4` · items `bc1e63407` · 2026-10-12 23:46 Asia/Shanghai
