# DESIGN · 主线/短征解锁「进过即开」菜单诚实（MG2 · 薄窗 · 可施工）· 2026-10-13

> **状态：** OPEN · TIP-BR · 荐批 A·M · 2026-10-13 00:33 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-map-enter-unlock-cue-honesty-2026-10-13.md`](RESEARCH-ember-map-enter-unlock-cue-honesty-2026-10-13.md)  
> **对接 live：** TIP-BQ CLOSED live items `a5c3f49d7` · `EmberSkillKit.gemPathUnlocked` / `endgameUnlocked` / `mapAccessUnlocked` · `EmberRunService.challengeOpen` · `ember_p1_short.yml` / `ember_p1_adventure.yml` / `ember_p1_challenge.yml` · power-paths MG2  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · **零改解锁数学** · 零改 WEEKLY_CAP/MARK_TICKS/RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 ConvertCap densify · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹/副手/强化率/升阶保留/形缓/转化保留再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零并轨方案 B

---

## 0. 一句话

灰锁条件与文案跟上「进过即开 · 清过也开」；服务端门闩不动。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| SoT 解锁 | Q01/Q07 **进过∨首通** 已 live | **不动数学** |
| 短征/冒险灰锁条件 | `q01done` / `q01_fc` / `q07done`（仅首通/领奖） | **`access_q01` / `access_q07`** |
| 菜单文案 | 「首通」「先通」「通关后解锁」 | 「进过或首通」「进过即开」 |
| 挑战队员口径 | 「各自首通 Q07」 | 「各自进过或首通 Q07」 |
| soft / Affix / 转化保留… | 已齐 / 冻 | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 锁（只读 · 禁改解锁数学）

| 键 | 规则 |
|----|------|
| `access_q01` | `EmberSkillKit.gemPathUnlocked(d, runs)` → `"1"` / `"0"` |
| `access_q07` | `EmberSkillKit.endgameUnlocked(d, runs)` → `"1"` / `"0"` |
| 禁 | 改 C_GEM_ENTER / C_ATTUNE_ENTER / firstCleared / mapAccessUnlocked 语义 · 抬日帽/体力/掉率 |

### 2.2 PAPI（插件）

| id | 返回 | 备注 |
|----|------|------|
| `corerpg_p1_access_q01` | `1` / `0` | 新键；GATE 段；单测 enter-only / clear-only / 皆无 |
| `corerpg_p1_access_q07` | `1` / `0` | 新键；同构 endgame |

既有 `q01done` / `q07done` **保留**（仍服务「首通进度」展示，勿删）；本窗只让**灰锁门**改绑 access_*。

### 2.3 TrMenu 定稿（物品）

#### A. `ember_p1_short.yml`

| 处 | 改 |
|----|-----|
| sx01–13 灰锁 condition | `%corerpg_p1_q01done% < 1` → `%corerpg_p1_access_q01% < 1` |
| sx14–19（及同构）灰锁 | `%corerpg_p1_q01_fc% != 已领取` → `%corerpg_p1_access_q01% < 1`（与前段统一） |
| 灰锁 name 尾 | `（需本人 Q01 首通）` → `（需本人进过或首通 Q01）` |
| 灰锁 lore | `先通主线 Q01…` / `通关后本格解锁` → `§c未解锁：进过或首通主线 Q01` + `§7进过即开 · 清过也开`（≤3；可保留日线半行） |
| 冒险页短征入口灰锁（若本文件无则见 adventure） | 同绑 `access_q01` |

#### B. `ember_p1_adventure.yml`

| 处 | 改 |
|----|-----|
| Icon `H` 短征灰锁 | `q01done` → `access_q01`；文案「先通」→「进过或首通」 |
| 后图灰锁（`X`/`A`/`R`/… `q07done`） | → `access_q07`；「首通 Q07 …后开放」→「进过或首通 Q07 后开放」 |
| 头注释「Q07 普通首通后」 | 勘误「进过或首通 Q07 后」（注释可改） |
| Open tell「不锁通关」 | **可留**；可选补半句「短征：进过 Q01 即开」（禁第四 tell 墙；若已满则不加） |

#### C. `ember_p1_challenge.yml`

| 处 | 改 |
|----|-----|
| 七格 lore「队员需各自首通 Q07」 | → `队员需各自进过或首通 Q07` |
| 文件头注释「Q07 普通首通后」 | 勘误「进过或首通」 |

### 2.4 调用方接线

| 点 | 动作 |
|----|------|
| `EmberRunPapi.gate`（或邻段） | 登记 `access_q01` / `access_q07` |
| 单测 | enter-only → 1；皆无 → 0；clear-only → 1 |
| TrMenu 三文件 | §2.3 |
| `EmberSkillKit` 解锁方法体 | **禁改** |
| soft / 开孔 / Affix / SF4 / 转化·形缓·升阶·幅度族 | **本窗不改** |

### 2.5 验收核（插件+物品）

| 核 | 期望 |
|----|------|
| 仅进过 Q01（未首通）→ 短征灰锁消失、可点进本 | 与 SoT 一致 |
| 未进未通 Q01 → 仍灰 · 文案含「进过或首通」 | 可见 |
| 仅进过 Q07 → 冒险后图/挑战入口按 SoT 开放 | 一致 |
| 挑战七格无「各自首通」谎称 | 可核 |
| lore≤3；无第四行教学墙 | 可核 |
| `git show` 无 C_*_ENTER/firstCleared 语义改 / Affix/伤乘/soft·开孔·Anvil·SF4 / 转化·形缓·升阶·幅度再叠 / skill_mult | PASS |

### 2.6 玩家口径（固定）

- 短征/挑战/终局入口：**进过对应主线本即开，清过也开**。  
- 本窗 **不**改体力、日帽、掉率、解锁数学。  
- 「首通奖励」仍要通关才领；**进本资格 ≠ 首通奖**。  
- 仍守 lore≤3。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-插件** + **余烬-物品**；策划 docs-only  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 转化·形缓·升阶·签名·套装·宝石·天赋·烙纹·副手·强化率再叠 / ConvertCap densify 仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留再叠 | **仍冻** |
| 7 | soft·开孔·GemHole / enhance+5 再叠 | **仍冻** |
| 8 | 成色/精工「确定成功」再压 | **另 tip · 可选**（弱） |
| 9 | 升阶「印记另一条路」短指针 | **另 tip · 可选**（升阶邻链 · prefer 再离） |
| 10 | 形状格 D/E/F 形缓半行 | **另 tip · 可选**（形缓链 · prefer 再离） |
| 11 | Affix densify / ConvertCap densify / 抬表 | **仍冻** |
| 12 | raid/深渊页其它「通关才开」字面扫尾 | **另 tip · 可选**（本窗优先 short+adventure+challenge） |

## STATUS: OPEN · TIP-BR · 荐批 A·M · 2026-10-13 00:33 Asia/Shanghai
