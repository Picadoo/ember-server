# DESIGN · 烙纹定向幅度菜单诚实（TrMenu 类型格 · 薄窗 · 可施工）· 2026-10-12

> **状态：** CLOSED live items `2f0507813` · TIP-BL · TrMenu reload · jar 未改 · 2026-10-12 23:57 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-brand-pin-magnitude-menu-honesty-2026-10-12.md`](RESEARCH-ember-brand-pin-magnitude-menu-honesty-2026-10-12.md)  
> **对接 live：** TIP-BK CLOSED live items `4c2c38485` · `ember-v1-growth.yml` affix · `ember_p1_brand.yml` · `EmberAffix` / `signedPct`  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore 再叠 / 套装 blurb 再叠 / 宝石短效果再叠 / 天赋悬停再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify（禁改 values/weights/pool/pity/rollable）· 零伤乘 · 零抬表 · 零抬 talent/gem flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋展示再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3

---

## 0. 一句话

烙纹页五类型格用 **现网 affix `values[]`** 写 **档位幅度半行**；不改词缀表。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `ember-v1-growth.yml` affix values | live SoT 四档 | **不动数值** |
| `EmberAffix.Def.text` / `signedPct` | 持件/结算已用 | **菜单对齐同口径** |
| `ember_p1_brand.yml` 五格 | 定性-only | **定性 + 档区间** |
| pin 预览聊天气泡 | 类型名 · 无区间 | **本窗可不动**（另 tip） |
| soft / CostSoft / Hope | 已齐 | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 短幅度（只读 values · 禁改表）

幅度字必须可由 `EmberGrowth.signedPct` 从 `values[]` 复算；**禁手写漂移**。定稿（与 RESEARCH §2 表一致）：

| 格 | id | lore 第 1 行（定稿） | lore 第 2 行 |
|----|----|----------------------|--------------|
| `1` | `b_affix` | `§7对词缀精英 · 档 +4%～+16%（仅主线重打）` | `§e➥ §f点击预览定向` |
| `2` | `b_set` | `§7套装事件伤害 · 档 +0.25%～+1%` | `§e➥ §f点击预览定向` |
| `3` | `c_tele` | `§7受预警招伤害 · 档 −0.5%～−2%` | `§e➥ §f点击预览定向` |
| `4` | `c_share` | `§7烬核自己份 · 档 −1%～−4%` | `§e➥ §f点击预览定向` |
| `5` | `c_affix` | `§7受词缀精英伤害 · 档 −4%～−16%（仅主线重打）` | `§e➥ §f点击预览定向` |

说明格 `N` 可加半行（仍 ≤3）：

- `§7档位仍随机 · 成色上限截断 · 本窗不改表`

- **禁止**改 `values` / `tier_weights` / `pity` / `rollable` / pool / `EmberBrandRules` 价次数。  
- **禁止** soft / 开孔 / Anvil / SF4 / 天赋·宝石·签名·套装展示再叠。  
- 每格 lore **≤3**（本窗目标 2；N 说明 ≤3）。  
- 合行优先：幅度合进定性行，禁再叠教学墙。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `ember_p1_brand.yml` Icons `1`–`5` | lore 换上表定稿（保留 name / material / command） |
| Icons `N` | 可选加「档位仍随机…」半行 |
| Open tell | **默认不动**（已有路径/合成指引） |
| `ember-v1-growth.yml` affix | **只读 · 禁改** |
| pin 预览气泡 / `pctList` | **本窗不派插件**；另 tip |
| soft / CostSoft / pin 价 | **本窗不改** |

### 2.3 验收核（物品岗）

| 核 | 期望 |
|----|------|
| 五格均含 `档` + 区间字 | 可见 |
| `b_affix`/`c_affix` 含「仅主线重打」 | 可见 |
| `b_set` 含 `+0.25%` 与 `+1%`（或 `～` 两端） | 可见 |
| 减伤三格用 `−`/`-` 幅度，非「微量」 | 可见 |
| 每格 lore ≤3；无第四行教学 | 可核 |
| `git show` 无 growth affix values/weights 改；无 soft/开孔/Anvil/SF4/签名/套装/宝石/天赋再叠；无 Affix densify / 伤乘 / skill_mult；无 CoreRpg 业务 jar | PASS |

### 2.4 玩家口径（固定）

- 打开烙纹定向页，点类型前一眼看见 **这纹大概加/减多少**（四档区间）。  
- 档位仍按成色随机截断；本窗 **不**改词缀表、不改花费、不改次数。  
- 仍守 lore≤3；不写效果长墙。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**（只 `ember_p1_brand.yml`）；**不派插件**  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停再叠 | **仍冻** |
| 7 | soft·开孔·GemHole 再叠 | **仍冻** |
| 8 | pin 预览气泡补档区间（pctList） | **另 tip · 可选** |
| 9 | 副手「微量」守势数字诚实 | **→ TIP-BM OPEN** |
| 10 | Affix densify / 抬 values | **仍冻** |

## STATUS: CLOSED live items `2f0507813` · TIP-BL · TrMenu reload · jar 未改 · 2026-10-12 23:57 Asia/Shanghai
