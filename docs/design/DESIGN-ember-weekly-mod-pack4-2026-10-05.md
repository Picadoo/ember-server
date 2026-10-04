# Ember Weekly Modifier Pack 4：精选图周规则收尾（D187，2026-10-05）

> 状态：**已上线 CoreRpg 1.65.26 / D187**（2026-10-05，`COORD-routine-0418`）。双写 `ember-v1-runs.yml` +2（hexplate/blades），`balance_version` 48→49；轮换索引斜移（`EmberRunMaps.modifierIndex`）；单测 14 规则 / 98 对；Stage C `p2econ --mods --weeks 24` **within range**；冒烟 **DEFERRED batch**（POLICY 01:53）。
> 前置：D80 / D152 / D154 / D155 / D158 / D178 Pack 2 / D186 Pack 3。
> 参考：同 Pack 2/3 — Diablo 大秘境式每周突变、PoE 地图词缀、WoW 大秘境词缀轮换。机制**只落在**现有 `rotation.modifiers` 字段（`remap` / `converted` / `text` / `normal`）；唯一新 Java 是轮换索引（见 §4）。

## 0. 一段话裁决

在 **P2-8 精选图周规则**（现网 12 条）上再加 **2 条侧向 remap+converted**，用完 D186 留下的最后两个方向 `heavy→caster` / `caster→melee`。全部 **challenge-only**，不改奖励、不加乘区、不加新货币；缺源 / 缺目标角色的图自动 noop。至此 4 个战斗角色之间的 12 个 remap 方向**全部用完**，周规则扩包到此为止，后续花样走别的系统（词缀精英 / 房间事件 / 首领招式）。

池从 12 变 14 时有一个坑：14 和 7 张图有公因数 7，旧式「ISO 周 mod 14」会让每张图永远只碰到 2 条规则（例如 Q01 只见 w 与 w+7 两条）。所以本包同时改了轮换索引，见 §4。

## 1. 白话版（给服主 / 玩家）

- 精选图的挑战局里，每周仍只有一条规则；规则池从 12 扩到 **14** 条。轮换方式微调：任意连续 98 周里，7 张图 × 14 条规则的 98 种组合各出现一次，相邻两周不会是同一条规则。
- 新两条只改「谁刷出来、转化怪手感」，**奖励表一字不动**：
  - **咒甲**：有重甲的图，重甲换成披甲术者（更肉、读条更慢；直线预警，躲开就不疼）。
  - **断咒**：有术者的图，术者丢下法杖变成冲锋近战（更快、单下略轻）。
- Q01 / Q02 没有术者 → 两条都 noop（和 casters / bolters / shell / hexers 一样）。
- 普通版首通、未首通重打、别的挑战图、深渊、团本、连战：**新两条都不进**。

## 2. 现网基线与占用表（只读，Pack 3 后）

| 方向 | 规则 | 包 |
|---|---|---|
| ranged→caster | casters 术者换防 | P2-8 |
| heavy→melee | disarm 卸甲 | D152 |
| ranged→heavy | guards 卫士潮 | D154 |
| melee→heavy | wall 铁卫 | D155 |
| caster→ranged | bolters 弓潮 | Pack 2 |
| caster→heavy | shell 龟甲 | Pack 2 |
| ranged→melee | press 压阵 | Pack 2 |
| melee→ranged | skirmish 散兵 | Pack 3 |
| melee→caster | hexers 咒潮 | Pack 3 |
| heavy→ranged | ballista 重弩 | Pack 3 |
| **heavy→caster** | **hexplate 咒甲** | **Pack 4** |
| **caster→melee** | **blades 断咒** | **Pack 4** |

非 remap 规则：lean 限药（normal）、reverse 逆行（normal）。单测断言每个 remap 方向只被一条规则使用。

## 3. 提案表（本包 2 条）

| id | 玩家名 | 机制 | 字段 | 闸门 | 玩家要做什么 | noop |
|---|---|---|---|---|---|---|
| `hexplate` | 咒甲 | 重甲→术者；更肉、读条慢 | `remap: {heavy: caster}`，`converted: {hp: 1.25, interval: 1.2}` | challenge-only | 少硬砍重甲，多读直线预警，侧移后输出 | 图无 heavy 或无 caster（Q01/Q02） |
| `blades` | 断咒 | 术者→近战；更快、单下略轻 | `remap: {caster: melee}`，`converted: {speed: 1.2, atk: 0.9}` | challenge-only | 少躲直线，多处理贴脸；退到门口一次吃一两只 | 图无 caster（Q01/Q02） |

数值取向与旧条对齐：

| 新条 | 易混旧条 | 差别 |
|---|---|---|
| 咒甲 heavy→caster | 龟甲 caster→heavy、卸甲 heavy→melee | 反向于龟甲；扭矩是「肉术者」（hp↑、出手慢），不是卸甲的脆快近战 |
| 断咒 caster→melee | 压阵 ranged→melee、弓潮 caster→ranged | 源是术者而非弓手；扭矩是移速 ×1.2 + 单下 ×0.9，比压阵（×1.3 / hp×0.85）温和 |

## 4. 轮换索引修正（唯一 Java 变化）

- 现网：精选图 = w mod 7，规则 = w mod n。n=12 与 7 互质 → 84 周走遍 84 对（D186 单测）。
- n=14：gcd(14, 7)=7 → 规则 = w mod 14 时，图 k 只会遇到规则 k 与 k+7。等于 12 条规则对每张图永远不出现——违背「每周换花样」，也让「缺角色 noop」集中在固定图上。
- 修正：`EmberRunMaps.modifierIndex(w, n, k)`
  - gcd(n, k)=1：仍 `w mod n`（现网 12 条时行为完全不变；只因 n 改为 14 而换周序）。
  - 否则：`(w mod k + w div k) mod n`。证明：令 p = w mod 98，a = p mod 7（图），b = p div 7（0..13），规则 = (a + b) mod 14；固定 a 时 b→规则是双射，任意 98 个连续 w 覆盖全部 p → 覆盖全部 98 对；同一 7 周块内 a 递增、b 不变 → 相邻周规则必不同。
- 单测：多个起点的 98 周窗口各 98 对；400 周内相邻不重复；互质池 `modifierIndex(5,12,7)=5`、负周 floorMod。
- 模拟器：`tools/p1sim/p1sim.py` 新增 `mod_index()` 同式；`p2econ.py` 在非互质时用与精选图同一周号取规则。

部署当周的规则会因池大小变化而换（D178 / D186 同样如此）；规则不影响奖励，可接受。

## 5. 明确否决

| 想法 | 原因 |
|---|---|
| 再加第 3 条（非 remap） | 12 个方向已用完；`drought` 与 lean 叠床架屋，`fog` 1.12 无钩子，`swarm` 无 count 字段 |
| 只加 1 条凑 13（互质免改 Java） | 留一个方向永远不用；本包一次收尾，改一个小索引更干净 |
| 新两条设 `normal: true` | 战斗 remap 污染首通；一律 challenge-only |
| treasure 映射 | 宝藏怪 atk 0；漏洞邻域 |
| 新货币 / 奖励改动 / 乘区 | 超出侧向花样，违反「不加永久强度」 |
| 化妆品周规则 | 用户 10-04：化妆品后置 |

## 6. 漏洞审查

| 场景 | 风险 | 约束 |
|---|---|---|
| 周 cap / 精选挑战 cap 绕过 | 靠换规则多领奖 | 规则**不改**结算；周 cap 计数器 `rotationWeekKey` 未动 |
| 部署当周规则变化 | 同一周内两种规则 | 仅影响刷怪角色，奖励相同；无可套利项 |
| 斜移索引 vs 周键 | 周键与规则错位 | 周键仍用 `weekIndex`；规则只是展示 + 刷怪，无持久状态 |
| `normal: true` 误开 | 首通被 remap | 单测 `normal==false` |
| 缺角色图 noop | 规则空转 | 既有行为；Q01/Q02 每 14 周约 6 周空转，与 Pack 2/3 一致 |
| 咒甲让 Q03–Q07 变易 | 重甲变术者，hp 较原重甲低 | hp×1.25 补偿；Stage C 差值 ≤ ±1 点 |
| 断咒贴脸过痛 | 通关率下沉 | atk×0.9；超标只调 converted |
| 与 variety / 誓约叠 | 誓约只取 `normal: true` 规则；variety 只在普通重打 | 新条 challenge-only，模式不重叠 |

## 7. 平衡门禁

| 检查 | 结果 |
|---|---|
| `tools/p1sim/p2econ.py --mods --weeks 98` | **within range**（`tools/p1sim/out-p2econ-mods-d187.md`；14 条规则各 360–420 玩家-周，通关率差全部 +0 点；经济列与无规则同量级。注：`--weeks 24` 在斜移索引下新 5 条规则 0 样本，故改跑 98 周 = 一个完整 7×14 周期）|
| 经济列 | 规则不改奖励 → 不变 |
| 超标处理 | 只调该条 `converted`；禁止加掉落 / 永久乘区 / 放宽 `normal` |

冒烟：**DEFERRED batch**（用户 01:53「测试攒一堆后再测」）。本窗只做单测 + 开服检查（Enabling 1.65.26 / 双 MySQL / SEVERE 0）。

## 8. 实现清单

1. 双写 `ember-v1-runs.yml`：追加 §3 两条 + 注释；`balance_version` 48→49 + 注释。
2. `EmberRunMaps.modifierFor` → `modifierIndex(w, n, maps.size())`。
3. `pom.xml` + `plugin.yml` → 1.65.26。
4. 单测：池 12→14、98 对、斜移窗口、相邻不重复、remap 方向唯一、Pack 4 remap / converted / `normal==false`。
5. sim：`p1sim.mod_index` + `p2econ` 取规则。
6. 源表 **D187**；RELEASE / STATUS。

## 9. 登记

- 源表：**D187**（CoreRpg 1.65.26 / bv49）
- COORD：`COORD-routine-0418`（routine-0418 中断；routine-0517 收尾部署）
- 设计：本文件
