# DESIGN · 升阶预览「确定·保留」菜单诚实（工坊 TrMenu · 薄窗 · 可施工）· 2026-10-13

> **状态：** CLOSED live items `64fe9f46e` · TIP-BO · TrMenu reload 77 · jar 未改 · 2026-10-13 00:19 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-upgrade-preview-keep-honesty-2026-10-13.md`](RESEARCH-ember-upgrade-preview-keep-honesty-2026-10-13.md)  
> **对接 live：** TIP-BN CLOSED live `1.65.800-d13143` · `EmberUpgradeRules.upgrade` Plan.note · `held_upgrade_cost` / `held_upgrade_lack` · `ember_p1_forge.yml` U/u · power-paths WS2 · BN 旁债 #9  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · **零抬 upgradeCost/C04/RATE/MAX_TRIES** · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹/副手/强化率展示再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

升阶预览/确认格补 **确定 · 保留强化/成色/精工** 与点预览/确认 cue；与聊天 Plan.note 同口径，不改价。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| Plan.note / 聊天 | 已写「全部保留」+ 确定成功 | **不动数值** |
| `held_upgrade_cost` | 已有 `T→T · 花费` | **不动** |
| 工坊 Icon `U`/`u` | 本次 + 缺料 · **无保留 · 无点预览 cue** | **本次 · 确定保留 · 点预览/确认**（≤3） |
| 成色/精工 / 强化率 | WS2 已齐 / BN 已齐 | **本窗不叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改 upgradeCost）

| 键 | 规则 |
|----|------|
| 保留半行 | 固定文案 **`确定 · 保留强化/成色/精工`**（对齐 Plan.note；绑定同保留，菜单短写） |
| 档位+花费 | 继续用 `%corerpg_p1_held_upgrade_cost%`（upgradeLine） |
| 缺料 | 继续 `%corerpg_p1_held_upgrade_lack%`；够料时空串 |
| 禁 | 改 C04 / upgradeCost / firstClear / soft MULT · 新发明价表 |

### 2.2 PAPI

**本窗零新 PAPI**；只用既有 `held_upgrade_cost` / `held_upgrade_lack`。

### 2.3 TrMenu lore 定稿（≤3）

| 格 | lore 定稿 |
|----|-----------|
| **U** 升阶·预览 | `§e本次：§f%corerpg_p1_held_upgrade_cost%` / `§a确定 · 保留强化/成色/精工` / `§e➥ §f点预览` |
| **u** 升阶·确认 | `§e本次：§f%corerpg_p1_held_upgrade_cost%` / `§a确定 · 保留强化/成色/精工` / `§e➥ §f点确认` |

- **缺料**：预览/确认主格本窗以保留半行为第二行；缺料仍可由 cost 行门禁短句 + 聊天预览红字承担。若物品岗合行需要，允许第二行写成 `§a确定 · 保留强化/成色/精工 §c%corerpg_p1_held_upgrade_lack%`（够料时 lack 空，仍 ≤3）。  
- 甲灰显子 icon：**本窗可不改**（仍本次+缺料）；勿第四行。  
- **禁**改强化 E/e / 成色 Q/q / 精工 R/r / 稳赌 / 旁路 / 配方速览本窗扩面。  
- 每格 lore **≤3**；禁教学墙；**禁**把印记「另一条路」长文塞进菜单。

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `ember_p1_forge.yml` `U`/`u` | lore → §2.3 |
| `EmberUpgradeRules.upgrade` / upgradeCost / C04 / soft | **禁改** |
| 强化率 / soft / 开孔 / Affix / SF4 / 签名·套装… | **本窗不改** |

### 2.5 验收核（物品）

| 核 | 期望 |
|----|------|
| 预览格可见「确定 · 保留强化/成色/精工」 | 可见 |
| 确认格同构；有 ➥ 点确认 | 可见 |
| 本次行仍显示 T→T · 花费（可升阶时） | 可见 |
| 每格 lore ≤3；无第四行 | 可核 |
| `git show` 无 upgradeCost/C04/RATE 改 / Affix densify / soft·开孔·Anvil·SF4 / 签名·套装·宝石·天赋·烙纹·副手·强化率再叠 / 伤乘 / skill_mult / CoreRpg 业务 jar | PASS |

### 2.6 玩家口径（固定）

- 点升阶预览/确认前一眼看见 **确定成功且保留强化/成色/精工**，以及本次档位与花费。  
- 本窗 **不**改升阶材料价、不改首通门禁、不改日首减负。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**；**不派插件**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名·套装·宝石·天赋·烙纹·副手·强化率再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | pin 预览气泡补档区间（pctList） | **另 tip · 可选**（烙纹链 · prefer 再离） |
| 9 | 成色/精工再压（已齐；勿叠） | **仍冻再叠** |
| 10 | 升阶菜单「印记另一条路」短指针 | **另 tip · 可选**（本窗只保留半行） |
| 11 | Affix densify / 抬 upgradeCost | **仍冻** |
| 12 | SF4 U1 技能页形缓展示诚实 | **另 tip · 可选**（离接线链） |

## STATUS: CLOSED live items `64fe9f46e` · TIP-BO · TrMenu reload 77 · jar 未改 · 2026-10-13 00:19 Asia/Shanghai
