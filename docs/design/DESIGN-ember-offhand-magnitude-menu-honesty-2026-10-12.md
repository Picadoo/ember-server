# DESIGN · 副手「微量」守势数字诚实（TrMenu 购/炼入口 · 薄窗 · 可施工）· 2026-10-12

> **状态：** CLOSED live items `91300ad65` · TIP-BM · TrMenu reload 77 · jar 未改 · 2026-10-13 00:03 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-offhand-magnitude-menu-honesty-2026-10-12.md`](RESEARCH-ember-offhand-magnitude-menu-honesty-2026-10-12.md)  
> **对接 live：** TIP-BL CLOSED live items `2f0507813` · NI offhand/parts · `ember_life` / `ember_set` / `ember_part` / `ember_forge` · D289/D294/D309  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore 再叠 / 套装 blurb 再叠 / 宝石短效果再叠 / 天赋悬停再叠 / 烙纹档幅度再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名/套装/宝石/天赋/烙纹幅度展示再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 skill_mult/sx20 · 零效果长墙 · 零 lore>3 · 零副手并轨方案 B

---

## 0. 一句话

购/炼入口用 **现网 NI SoT** 写回 **物防 +2 / 生命 +8 / 物防 +1**；不改数值表。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| NI 持件 lore | +2 / +8 / +1 已写 | **不动数值** |
| life O/W | 仅价·周次（D12199 误删诚实行） | **价 + 数字 + 不进 P1** |
| set O | 「微量守势」无数字 | **点出 +2/+8** |
| part A / forge P | 材料-only / 「微量物防」 | **+1 + 不进 P1** |
| soft / Affix / SF4 | 已齐 / 冻 | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT 短数字（只读 NI · 禁改 flat）

数字必须与 NI 持件行一致（`物理防御: +2` / `生命力: +8` / `物理防御: +1`）；**禁手写漂移 · 禁抬**。

| 菜单格 | id | lore 定稿（≤3） |
|--------|----|-----------------|
| `ember_life` **O** | ward | `§780 余烬币 · 每周 1 次` / `§7副手 · 物防 +2 · 不进 P1 B/H` / `§e➥ §f购买` |
| `ember_life` **W** | vita | `§780 余烬币 · 每周 1 次` / `§7副手 · 生命 +8 · 不进 P1 B/H` / `§e➥ §f购买` |
| `ember_set` **O** | 入口 | `§7副手微量守势 · 永不进 P1 B/H` / `§8守腕物防+2 / 生坠生命+8 · 灰粮可购` / `§e➥ §f打开补给·生活` |
| `ember_part` **A** | ash_brace | `§7物防 +1 · 不进 P1 B/H` / `§e消耗：碎×12 + 铁锭×2` / `§8薄于守腕` |
| `ember_forge` **P** | 入口 | `§7材料炼部件 · 灰箍：碎×12+铁锭×2` / `§8副手物防 +1 · 不计主线战力` / `§e➥ §f打开炼部件` |

可选：`ember_part` **I** 说明格若仍 ≤3，可把末行换成 `§8物防 +1 · 不进主线战力`（勿第四行）。

- **禁止**改 NI 属性数字 / StatService / life.yml coin·weekly·give / part 配方量。  
- **禁止** soft / 开孔 / Anvil densify / SF4 / 签名·套装·宝石·天赋·烙纹幅度再叠。  
- 每格 lore **≤3**；合行优先；禁教学墙。  
- 保留「不进 P1 B/H / 不计主线战力」口径（D289/D294/D309）。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `ember_life.yml` Icons `O`/`W` | lore → 上表 |
| `ember_set.yml` Icon `O` | lore → 上表 |
| `ember_part.yml` Icon `A`（±`I`） | lore → 上表 |
| `ember_forge.yml` Icon `P` | lore → 上表 |
| NI offhand/parts | **默认只读**（数字已在） |
| life.yml / part.yml 配方 / Stat | **禁改** |
| soft / CostSoft / Affix values | **本窗不改** |

### 2.3 验收核（物品岗）

| 核 | 期望 |
|----|------|
| life O 含 `物防 +2` 与 `不进 P1` | 可见 |
| life W 含 `生命 +8` 与 `不进 P1` | 可见 |
| set O 含 `+2` 与 `+8` | 可见 |
| part A 含 `物防 +1` 与 `不进 P1` | 可见 |
| forge P 含 `物防 +1` | 可见 |
| 每格 lore ≤3；无第四行教学 | 可核 |
| `git show` 无 NI 属性数字改 / life coin 改 / soft·开孔·Anvil·SF4·签名·套装·宝石·天赋·烙纹幅度再叠 / Affix densify / 伤乘 / skill_mult / CoreRpg 业务 jar | PASS |

### 2.4 玩家口径（固定）

- 买守腕/生坠、炼灰箍前一眼看见 **加多少物防/生命**。  
- 仍是微量守势、**永不进 P1 B/H**；本窗 **不**改数值、不改价、不改周次。  
- 仍守 lore≤3；不写效果长墙。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**（只列上表 TrMenu）；**不派插件**  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度再叠 | **仍冻** |
| 7 | soft·开孔·GemHole 再叠 | **仍冻** |
| 8 | pin 预览气泡补档区间（pctList） | **另 tip · 可选**（烙纹链 · prefer 再离） |
| 8b | 强化预览成功率菜单诚实（WS2） | **TIP-BN OPEN** |
| 9 | Affix densify / 抬 values | **仍冻** |
| 10 | 副手并轨方案 B | **仍冻（D309）** |

## STATUS: CLOSED live items `91300ad65` · TIP-BM · TrMenu reload 77 · jar 未改 · 2026-10-13 00:03 Asia/Shanghai
