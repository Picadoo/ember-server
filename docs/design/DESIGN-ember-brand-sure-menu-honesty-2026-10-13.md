# DESIGN · 烙纹合成/定向「预览直办」菜单诚实（烙纹 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** **OPEN** · TIP-BV · 荐批 A·M · 2026-10-13 00:58 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-brand-sure-menu-honesty-2026-10-13.md`](RESEARCH-ember-brand-sure-menu-honesty-2026-10-13.md)  
> **对接 live：** TIP-BU CLOSED live items `f59b9beae` · `EmberBrandSure` · `EmberGrowthService`「碎片齐·预览直办 / 料齐·预览直办 / 合成后自动定向」· `ember_p1_brand.yml` N/C/1–5 · power-paths SUR1 · TIP-BL 档幅度已齐  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功 / 互换免费确定 / 洗练直留再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零 forgeroll 权重 densify · 零伤乘 · 零抬表 · **零改 craftShards/pin 价/MAX_CHARGES** · 零抬 RATE/upgradeCost · 零抬成色/精工 cost · 零抬 swap 收费 · 零改解锁数学 · 零 ConvertCap densify · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹档幅度/副手/强化率/升阶保留/形缓/转化/access_*/成色精工确定成功/互换免费确定/洗练直留再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

烙纹说明 / 合成 / 类型格写出 **料齐预览直办** 与 **路径齐合成后自动定向**；与聊天同口径，不改价。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 聊天 / ActionBar | 已写「碎片齐·预览直办」「料齐·预览直办」「路径直办·合成后自动定向」 | **不动数值** |
| `EmberBrandSure` | craftReady / pinReady / maybeAutoPinAfterCraft | **不动** |
| 烙纹 Icon `N` | 仓差 · 定向还差 · 档随机 — **无直办** | **仓差合行 · 料齐预览直办·路径齐自动定向 · 档随机**（≤3） |
| 烙纹 Icon `C` | 合成比 · 聊天栏确认 · 还差 — **无直办** | **合成比 · 碎片齐·预览直办 · 还差**（≤3） |
| 类型 `1`–`5` | 档幅度（BL）· 点击预览定向 — **无料齐直办** | **档幅度 · 料齐·预览直办 · 点预览**（≤3） |
| forge / 洗练 / 互换 sure | 已齐 / 冻再叠 | **本窗不叠** |
| 烙纹档幅度 values | BL 已齐 | **本窗不改 values** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改价/次数/Affix）

| 键 | 规则 |
|----|------|
| 合成半行 | 固定 **`碎片齐 · 预览直办（不改合成比）`**（对齐聊天） |
| 定向半行 | 固定 **`料齐 · 预览直办（不改价/次数）`** |
| 路径半行 | 固定 **`路径齐 · 合成后自动定向`**（说明 latch；不改 latch 数学） |
| 禁 | 改 craftShards · pin 价 · MAX_CHARGES · Affix values/weights/pool · Affix densify |

### 2.2 PAPI

**本窗零新 PAPI**；继续 `%corerpg_p1_forge_gap_brand%` / `%corerpg_p1_forge_gap_pin%` / `%corerpg_p1_brand_path%` 等既有键。

### 2.3 TrMenu lore 定稿（≤3）

| 格 | lore 定稿 |
|----|-----------|
| **N** 说明 | `§c仓差碎 %corerpg_p1_forge_gap_brand% · 定向还差 %corerpg_p1_forge_gap_pin%` / `§a料齐预览直办 · 路径齐合成后自动定向` / `§7档位仍随机 · 成色上限截断` |
| **C** 合成 | `§740 余烬碎片 → 1 余烬烙纹` / `§a碎片齐 · 预览直办（不改合成比）` / `§c还差：§f碎差 %corerpg_p1_forge_gap_brand%（需×40）` |
| **1**–**5** 类型 | **保留 BL 档幅度行** / `§a料齐 · 预览直办（不改价/次数）` / `§e➥ §f点击预览定向` |

- 每格 lore **≤3**；禁教学墙；禁把 Affix 长句/权重表塞回第四行。  
- Open tell 可选对齐：`§8料齐则预览直办 · 路径已选则合成后自动定向（不改价）`（物品岗可一并改；不改命令）。  
- **零改** BrandSure Java / Affix / craftShards / pin 价。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `ember_p1_brand.yml` `N`/`C`/`1`–`5`（+可选 Open tell） | lore → §2.3 |
| `EmberBrandSure` / `EmberGrowthService` brandCraft/Pin | **禁改** |
| Affix values / forge EQRUS / 洗练 / 互换 / soft / 开孔 / SF4 / access_* | **本窗不改** |

### 2.5 验收核（物品）

| 核 | 期望 |
|----|------|
| 说明格可见「料齐预览直办」与「自动定向」 | 可见 |
| 合成格可见「碎片齐 · 预览直办」 | 可见 |
| 类型格可见「料齐 · 预览直办」且档幅度仍在 | 可见 |
| 每格 lore ≤3；无第四行 | 可核 |
| `git show` 无 Affix values/weights/pool 改 / craftShards·pin 价改 / soft·开孔·Anvil·SF4 / forge EQRUS·洗练·互换再叠 / 伤乘 / skill_mult / CoreRpg 业务 jar | PASS |

### 2.6 玩家口径（固定）

- 打开烙纹页一眼看见 **料齐预览直办** 与 **路径齐合成后自动定向**。  
- 碎片齐点合成 → 直办；烙纹+币够点类型 → 直办；已选路径且料够 → 合成后自动定向一次。  
- 本窗不改价、不改次数、不改 Affix 档。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**；**不派插件**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名·套装·宝石·天赋·烙纹档幅度·副手·强化率·升阶保留·形缓·转化·access_* / 成色精工确定成功 / 互换免费确定 / 洗练直留再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹档幅度 / 副手 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功 / 互换免费确定 / 洗练直留再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | hub/raid/abyss「通关才开」扫尾 | **仍冻**（access_* 再叠） |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选**（升阶邻链 · prefer 再离） |
| 10 | forgeroll 普通格成色权重半行 | **另 tip · 可选**（赌线 · 禁 ForgeChoice densify） |
| 11 | Affix densify / ConvertCap densify / 抬表 / forgeroll 权重 densify | **仍冻** |
| 12 | 分解菜单「确定」字面再压 | **另 tip · 可选**（弱） |
| 13 | 烙纹 BrandSure 再叠 / Open tell densify | **本窗后冻再叠** |
| 14 | SigStampPity 菜单 n/N 一眼 | **另 tip · 可选**（签名邻 · prefer 离 brand 后再评） |

## STATUS: OPEN · TIP-BV · 荐批 A·M · 2026-10-13 00:58 Asia/Shanghai
