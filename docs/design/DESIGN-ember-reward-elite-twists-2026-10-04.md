# Ember 奖励精英变招 Pack 1（D182，设计-only，2026-10-04）

> 状态：**已实现 CoreRpg 1.65.20 / balance_version 43**（`COORD-routine-0047` / branch `d182-elite`；未 push / 未 deploy，等 signin DEPLOY LOCK RELEASE）。
> 前置：Extra.ELITE 现网（10% 权重、金皮木桩、`ELITE_SHARD=10` + `ELITE_CORE=1`）；形状库同 D140/D173（`circle`/`cone`/`line`/`charge`，`light`，warn≥1.2s，kb≤1）。
> 研究：`docs/design/RESEARCH-ember-reward-elite-2026-10-04.md`（D3 Champion/Rare、PoE、WoW M+、本服词缀/首领边界）。
> 边界：词缀精英扩包 = D138/D176/D181；房间事件扩包 = D179；首领半血 = D173。**本包只动 Extra.ELITE 奖励精英。**
> p1sim：`tools/p1sim` Extra.ELITE 仍是无招木桩（`mob(..., 'elite')`，无 blaze/技能表）；本包**不扩** p1sim 大模型。§7 通关率门禁（有招 vs 无招 Δ≤2pp）留到 p1sim 有 elite-extra 招式模型后再扫；经济 Δ≈0（未改 shard/core）。

## 0. 一段话裁决

主线局抽到 **额外事件「奖励精英」**（约 10%）时，刷出的 `EmberQ0xElite` 不再是纯木桩：每张图给它 **1 条固定、有预警的轻招**（主题贴合该图），改走位、不抬永久乘区、**不改** 10 碎片 + 1 核心。首通局也可以遇到（Extra 闸门本来如此）。不做小怪包、不加随机第二词缀、不碰宝藏怪/宝箱。本窗只出文档。

## 1. 白话版（给服主 / 玩家）

- 进本有时会刷一只金盔甲「××奖励精英」。以前它只会普攻；现在它会多放一招亮圈 / 扇 / 条带 / 冲撞，躲开再打。
- 每一张主线图的招不一样（灰烬推线、焦骨踏圈、残誓扇、潮闸冲、断塔点名圈、霜息锥、炉锁劈），打多了能认图。
- 杀掉仍是结算 **余烬碎片 +10、余烬核心 +1**（和词缀精英那 2 碎片不是一回事）。
- 词缀精英（身上挂「炽热 / 投弹」那种）照旧；两只偶尔同局出现时，招式节奏错开，不会故意叠爆。

## 2. 为什么是 Pack 1（与词缀 / 首领包的差别）

| | 词缀精英 Pack 3（D181） | 首领 Pack 2（D173） | **奖励精英 Pack 1（D182）** |
|---|---|---|---|
| 对象 | 提升后的普通怪 | Q01–Q07 Boss | Extra.ELITE 专用 MM |
| 变化 | 随机池 +2 | 半血转阶段 | **每图 1 条固定轻招** |
| 闸门 | 已首通普通版 | 有首领就有 | Extra 权重（含首通） |
| 奖励 | affix_shard | 通关表 | **不变**（10+1） |
| 主题 | 通用词缀名 | 首领名招 | **地图身份** |

## 3. 现网基线（只读，实现前再核对）

| 项 | 值 |
|---|---|
| 权重 | NONE 70 / TREASURE 15 / **ELITE 10** / CHEST 5 |
| MM | `EmberQ01Elite`…`EmberQ07Elite`（无 Skills） |
| HP/atk | 以 `ember-v1-runs.yml` `roles.elite` 为准（Q01 120/3 … Q07 442/12） |
| 奖励 | `extra_elite_shard` 10 + `extra_elite_core` 1 |
| 测试钩 | `/corerpg p1 runs extra elite`（下一局一次） |

## 4. 每图固定招（示意；落地前以 sim / 实机扫）

伤害为 **绝对伤害示意**（对齐该图普通轻怪量级，`light: true` 走挑战覆盖时用 `boss.light` 同类比例，或单独 `elite.light` 键——实现窗二选一，默认复用 mob 轻伤表）。全部 `warn ≥ 1.2`，`kb ≤ 1`（Q04 建议 kb=0）。

| 图 | 精英显示名（现网） | 新招 | 形状 | 参数示意 | 玩家要做什么 |
|---|---|---|---|---|---|
| Q01 | 灰烬奖励精英 | **门廊推** | `line` light | every 14，warn 1.2，length 5，width 2，dmg ≈ atk×1.0 | 正前亮带，侧移 |
| Q02 | 焦骨奖励精英 | **焦焰踏** | `circle` light，ahead 0 | every 14，warn 1.2，radius 2.5，dmg ≈ atk×1.1 | 脚下圈，走开 |
| Q03 | 残誓奖励精英 | **誓印扫** | `cone` light | every 14，warn 1.2，angle 90，range 4，dmg ≈ atk×1.0 | 正前扇，走到扇外 |
| Q04 | 潮闸奖励精英 | **闸冲** | `charge` light | every 15，warn 1.3，length 6，width 3，dmg ≈ atk×1.0；**kb=0** | 直线冲撞条带，离开（复用 B2.165 墙距裁剪） |
| Q05 | 断塔奖励精英 | **落尘** | `circle` `target:player` light | every 15，warn 1.2，radius 2.0，dmg ≈ atk×1.1 | 脚附近亮圈，提前走 |
| Q06 | 霜封奖励精英 | **霜息** | `cone` light | every 15，warn 1.2，angle 100，range 4，dmg ≈ atk×1.0 | 正前扇，侧移 |
| Q07 | 炉锁奖励精英 | **矿渣劈** | `line` light | every 15，warn 1.3，length 6，width 2.5，dmg ≈ atk×1.1 | 正前条带，侧移 |

聊天一行（语气对齐 D166/D170）：

- 刷出：`§e额外事件：奖励精英「门廊推」§7· 会放正前亮带，侧移再打 · 击败 → §f碎片 +10 §7+ §f核心 +1`
- 放招前可省略（预警轮廓已够）；若要一行：`§6门廊推 §7· 正前亮带`

配置草图（实现窗；本窗不改 yml）：

```yaml
elite_twists:
  enabled: false   # 落地时改 true；或随 balance_version 开关
  q01: {move: shove,  shape: line,   every: 14, warn: 1.2, length: 5, width: 2,   dmg: 1.0}
  q02: {move: stomp,  shape: circle, every: 14, warn: 1.2, radius: 2.5,          dmg: 1.1}
  q03: {move: sweep,  shape: cone,   every: 14, warn: 1.2, angle: 90, range: 4,  dmg: 1.0}
  q04: {move: barge,  shape: charge, every: 15, warn: 1.3, length: 6, width: 3,  dmg: 1.0, kb: 0}
  q05: {move: dust,   shape: circle, every: 15, warn: 1.2, radius: 2.0, target: player, dmg: 1.1}
  q06: {move: breath, shape: cone,   every: 15, warn: 1.2, angle: 100, range: 4, dmg: 1.0}
  q07: {move: slag,   shape: line,   every: 15, warn: 1.3, length: 6, width: 2.5, dmg: 1.1}
```

## 5. 明确否决

| 想法 | 原因 |
|---|---|
| 给奖励精英再挂随机词缀（炽热/投弹…） | 与 D138 池重叠；同局双随机难读 |
| Champion 式 3～5 只小怪包 | 门线 / 密度 / 宝藏币纪律；本服 Extra 是单事件 |
| Jailer / Vortex / Waller / Reflect / Teleport / 吸血 | 与 D171/D181 否决表一致 |
| 提高 ELITE 权重或 10+1 材料 | 经济窗 + p2econ；本包零奖励变化 |
| 改宝藏怪 / 额外宝箱 | 另包；宝藏怪 atk=0 是「打沙包换币」 |
| 新粒子 / 金光光环奖励 | 化妆品后置（用户 10-04） |
| 局内 atk/HP 乘区（「精英狂暴」） | 禁止永久/局内乘区膨胀；只加招式 |
| 挑战/深渊关掉奖励精英招 | Extra 本就全模式；招是 light，应用同一套 |

## 6. 漏洞 / 实现注意

| 风险 | 处理 |
|---|---|
| 与词缀精英同房预警叠 | 奖励精英 every ≥14s；词缀 mortar every≥5s 但不同对象；sim 查同房同时预警；必要时奖励精英开场延迟 3s 再进 CD |
| Q04 落差 + charge 击退摔死 | kb=0；墙距裁剪；过短条带跳过施放 |
| 首通局新手被吓 | Q01–Q03 every 偏长、dmg 偏低；聊天一行写清「侧移再打」；不改首通掉落 |
| 重连 / 清房 | 与首领招同一套房间清理；未放完的预警卸掉 |
| 测试钩 | 保持 `extra elite`；可加 `elite_twist clear\|q01..` 强制招名（仅 CORERPG 测试） |
| 图录 | 七图「奖励精英」条目补招名 / 范围 / 躲法（文案窗可并 D166 语气） |

## 7. 门禁（实现窗，本窗不跑）

| 检查 | 目标 |
|---|---|
| 通关率 | 有招 vs 无招，主线普通参考装 × 躲避 0.3/0.5/0.7，抽到 ELITE 的种子子集；Δ ≤2pp，硬顶 3pp |
| 经济 | **禁止**改 shard/core；p2econ 应 Δ≈0（仅 TTK 噪声） |
| 超标只许 | 放慢 every / 降 dmg / 加 warn —— **不许**加薪或加材料 |
| 冒烟 | `runs extra elite` → FreshQ 进 Q01/Q04/Q07 各一眼：预警可见、击败仍 +10/+1、botd 全退 |

## 8. 实现清单（另窗）

1. `ember-v1-runs.yml`：`elite_twists:`（默认 false → 开）  
2. `EmberRunMaps` 解析 + `EmberRunDirector` 对 Extra.ELITE Tracked 挂轻招  
3. 单测：七图各解析到 1 招；kb=0 on Q04；禁用开关时行为与现网一致  
4. 图录 / 聊天文案  
5. sim 门禁 + 短冒烟；bump CoreRpg 小版本 + `balance_version`（因 runs 招式表）  
6. **不改** `ELITE_SHARD` / `ELITE_CORE` / Extra 权重 / MythicMobs Skills（招走导演）

## 9. 登记

- 源表：**D182**（CoreRpg 1.65.20 / bv43，待 RELEASE）  
- P2 草稿：§5zl  
- COORD：设计窗 `COORD-routine-1942`；实现窗 `COORD-routine-0047`（worktree，不抢 DEPLOY LOCK）
