# Ember 奖励精英变招 Pack 2（D185，2026-10-05）

> 状态：**实现窗** CoreRpg 1.65.24 / balance_version 47（`COORD-routine-0309`）。
> 前置：D182 Pack 1 已上线（`elite_twists` 每图 1 招，bv43 / 1.65.20）。
> 研究：同 Pack 1 — `docs/design/RESEARCH-ember-reward-elite-2026-10-04.md`。
> 边界：只动 Extra.ELITE 第二招；**不改** 10+1、权重、词缀池、房间事件、首领半血。

## 0. 一段话裁决

主线抽到「奖励精英」时，每图在 Pack 1 固定轻招之外再挂 **1 条互补形状的第二轻招**（`alt:`）。两招交替施放（先 Pack1，再 Pack2，循环），改走位、不抬永久乘区、奖励与权重不变。形状仍限 circle / cone / line / charge + light / follow 已有能力；kb≤1，Q04 第二招亦 kb=0。

## 1. 每图第二招（Pack 2）

| 图 | Pack 1 | Pack 2 move | 形状 | 参数示意 | 玩家要做什么 |
|---|---|---|---|---|---|
| Q01 | 门廊推 line | **灰烬扇** ashfan | cone | every 16，warn 1.2，angle 80，range 3.5，dmg ≈ atk×1.0 | 正前扇，走到扇外 |
| Q02 | 焦焰踏 circle | **焦线** sear | line | every 16，warn 1.2，length 5，width 2，dmg ≈ atk×1.0 | 正前亮带，侧移 |
| Q03 | 誓印扫 cone | **誓踏** oathstomp | circle ahead 0 | every 16，warn 1.2，radius 2.2，dmg ≈ atk×1.05 | 脚下圈，走开 |
| Q04 | 闸冲 charge | **潮扇** tidefan | cone | every 16，warn 1.3，angle 90，range 3.5，dmg ≈ atk×1.0；**kb=0** | 正前扇，扇外（无击退） |
| Q05 | 落尘 circle target:player | **碎带** rubble | line | every 16，warn 1.2，length 5，width 2，dmg ≈ atk×1.0 | 正前亮带，侧移 |
| Q06 | 霜息 cone | **霜环踏** frostring | circle ahead 0 | every 16，warn 1.2，radius 2.2，dmg ≈ atk×1.05 | 脚下圈，走开 |
| Q07 | 矿渣劈 line | **炉扇** forgefan | cone | every 16，warn 1.3，angle 90，range 4，dmg ≈ atk×1.0 | 正前扇，扇外 |

配置：在既有 `q0x:` 行下嵌套 `alt: {move: …}`（向后兼容无 alt 的单招）。

## 2. 明确否决

| 想法 | 原因 |
|---|---|
| 同时放两招 / 缩短 every 叠压 | 预警叠读；本包只交替 |
| 提高 ELITE 权重或 10+1 | 经济窗；零奖励变化 |
| 新形状 / 新货币 / 永久乘区 | 形状库已够；禁止膨胀 |
| Jailer / Reflect / Teleport | 同 Pack 1 否决表 |
| 替换 Pack 1 招 | Pack 1 已上线；本包是「再加一条」 |

## 3. 漏洞审查（exploit）

| 风险 | 处理 |
|---|---|
| 双招结算 / 双倍碎片 | 奖励仍走 Extra.ELITE 一次 `extra_elite_shard/core`；招式不发奖 |
| 周 cap / 体力绕过 | 不改 stamina、extra 权重、settlement |
| Q04 击退摔死 | Pack 2 潮扇 kb=0；无 charge |
| 与词缀精英预警叠 | Pack 2 every≥16；open_delay 3s 不变 |
| 重连 / 清房 | 同 D182：预警随 Tracked 卸掉 |
| 双 claim | 无新 claim 计数器；无新菜单领取 |

## 4. 门禁

| 检查 | 目标 |
|---|---|
| 单测 | 七图各解析到 primary + alt；Q04 alt kb=0；light/warn≥1.2 |
| p1sim §7 | 仍 deferred（Extra.ELITE 无招式模型，同 D182） |
| 冒烟 | POLICY 01:53 攒批；本包不单测冒烟 |
| 经济 | shard/core / 权重不变 → Δ≈0 |

## 5. 实现清单

1. `ember-v1-runs.yml`：七图 `alt:` + `balance_version` 46→47  
2. `EmberRunMaps.EliteTwists.Twist.alt` 解析 + 七个中文名  
3. `EmberRunDirector` Tracked.twistAlt + twistTick 交替  
4. `EmberRunService.onExtraSpawned` 文案带两招名  
5. 单测 `_D185`；bump CoreRpg 1.65.24  
6. **不改** ELITE_SHARD / ELITE_CORE / Extra 权重 / MythicMobs Skills

## 6. 登记

- 源表：**D185**（CoreRpg 1.65.24 / bv47）  
- COORD：`COORD-routine-0309`  
- 设计：`docs/design/DESIGN-ember-reward-elite-twists-pack2-2026-10-05.md`
