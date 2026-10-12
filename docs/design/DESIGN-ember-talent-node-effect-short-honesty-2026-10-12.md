# DESIGN · 天赋节点效果短诚实（TrMenu 悬停 · 薄窗 · 可施工）· 2026-10-12

> **状态：** CLOSED live items `4c2c38485` · TIP-BK · TrMenu reload 77 · jar 未改 · 2026-10-12 23:52 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-talent-node-effect-short-honesty-2026-10-12.md`](RESEARCH-ember-talent-node-effect-short-honesty-2026-10-12.md)  
> **对接 live：** TIP-BJ CLOSED live `1.65.799-d13079` · `talent.yml` · `ember_talent.yml` · B2.15 attr-copy · D12200 lore compress  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 / 调律 live bad 进持件 / 签名 lore 再叠 / 套装 blurb 再叠 / 宝石短效果再叠 / soft·开孔再叠 **仍冻**  
> **硬禁：** 零功率表 · 零抬 talent.yml stats/cost/requires · 零 skill_mult · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 Affix/伤乘/sx20 · 零效果长墙 · 零 lore>3

---

## 0. 一句话

菜单已有节点悬停用 **`talent.yml` SoT** 写回 **短效果**（合进消耗行，lore≤3）；不改天赋数值、不扩节点墙。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `talent.yml` stats | live SoT | **不动数值** |
| B2.15 二层中文效果 | 曾 SHIP | **压回菜单** |
| D12200 压缩 | 删掉效果半行 | **合行恢复 · 守 ≤3** |
| 根三 + 烬斩 | 无效果字 | **补短效果** |
| 一层中段节点 | 不在 TrMenu | **本窗不扩** |
| soft / 开孔 / 宝石短效果 | 已齐 / 刚 CLOSED | **零再叠** |

---

## 2. 方案 M（定稿荐）

### 2.1 键→中文（沿用 B2.15 · 仅展示）

| 旧键 | 新人话 |
|------|--------|
| `phys_damage` | `物攻` |
| `crit_damage_pct` | `暴伤` |
| `crit_chance_pct` | `暴击率` |
| `move_speed_pct` | `移速` |
| `attack_speed_pct` | `攻速` |
| `on_hit_slow_pct` | `击中减速` |
| `damage_taken_pct` | `受伤` |
| `max_health` | `生命` |
| `phys_defense` | `物防` |
| `life_steal_pct` | `吸血`（本窗菜单无该节点则不用） |
| `skill_id` 节点 | `解锁主动技{名}` |

数值/符号 **照 talent.yml 原样**（如 `+1` · `+0.03` · `-0.01`）。

### 2.2 本窗 13 格定稿（仅 `ember_talent.yml`）

| 图标 | nodeId | 消耗行定稿（第 1 lore） | 其它行 |
|------|--------|------------------------|--------|
| 1 | blaze_root | `§7根 · 消耗 2 · 物攻 +1` | 点击 |
| 2 | ash_root | `§7根 · 消耗 2 · 攻速 +0.02` | 点击 |
| 3 | warden_root | `§7根 · 消耗 2 · 物防 +1` | 点击 |
| 4 | blaze_slash | `§7主动 · 消耗 4 · 解锁烬斩` | 前置燃眼+饮烬 · 点击 |
| a | blaze_ember2 | `§7二层 · 消耗 3 · 物攻 +1` | 先点满燎原 · 点击 |
| b | blaze_heat2 | `§7二层 · 消耗 3 · 暴伤 +0.03` | 先点满燎原 · 点击 |
| c | blaze_second | `§7二层 · 消耗 4 · 暴击率 +0.005 · 物攻 +1` | 先点满燎原 · 点击 |
| d | ash_veil2 | `§7二层 · 消耗 3 · 移速 +0.01` | 先点满烟幕 · 点击 |
| e | ash_dust2 | `§7二层 · 消耗 3 · 攻速 +0.01` | 先点满烟幕 · 点击 |
| f | ash_shroud | `§7二层 · 消耗 4 · 击中减速 +0.03 · 攻速 +0.01` | 先点满烟幕 · 点击 |
| g | warden_bulwark2 | `§7二层 · 消耗 3 · 受伤 -0.01` | 先点满永护 · 点击 |
| h | warden_aegis2 | `§7二层 · 消耗 3 · 生命 +2` | 先点满永护 · 点击 |
| i | warden_vow2 | `§7二层 · 消耗 4 · 物防 +1 · 受伤 -0.01` | 先点满永护 · 点击 |

- **每格 lore ≤3 行**（根可 2 行；有前置者 3 行）。  
- **禁止**改 `talent.yml` / unlock `command:` 实参 / cost / requires / stats。  
- **禁止**把一层中段（燃眼/饮烬/…）塞进本菜单。  
- V/U/Z/5/B/j 等非解锁格：**不动**（或仅保持 ≤3，禁借号改规则）。

### 2.3 调用方接线

| 点 | 动作 |
|----|------|
| `ember_talent.yml` | 上表 13 格消耗行改写；前置/点击行保留 |
| `talent.yml` | **本窗不改** |
| CoreRpg jar | **不派插件** |
| 验收 `rg` | 13 格定稿短效果均可见；无 `phys_damage` 等裸键；无 lore 行数>3 的解锁格 |

### 2.4 玩家口径（固定）

- 点天赋前悬停：一眼知道这格 **加什么、加多少**（或解锁哪招）。  
- 本窗 **不**提高天赋数值、不改点数、不改洗点价。  
- 一层中段节点仍走 `/corerpg talent` 或既有命令流（本窗不扩菜单墙）。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后：**余烬-物品**（只 `ember_talent.yml`）；**不派插件**  
- L13 施工仍 HOLD；Anvil / soft / 开孔再叠 / SF4 U2U3 / 灰印 / 调律 live bad / 签名 lore / 套装 blurb / 宝石短效果再叠仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件 | **仍冻** |
| 6 | 签名 lore / 套装 blurb / 宝石短效果再叠 | **仍冻** |
| 7 | soft·开孔·GemHole 再叠 | **仍冻** |
| 8 | 烙纹定向幅度菜单诚实 | **→ TIP-BL OPEN**（非 Affix densify · 只展示） |
| 9 | 一层中段节点进 TrMenu | **另 tip · 非本窗 densify** |

## STATUS: CLOSED live items `4c2c38485` · TIP-BK · TrMenu reload 77 · jar 未改 · 2026-10-12 23:52 Asia/Shanghai
