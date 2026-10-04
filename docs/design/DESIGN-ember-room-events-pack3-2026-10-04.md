# Ember Room Events Pack 3：房间事件扩包（D179，设计-only，2026-10-04）

> 状态：**已实现 CoreRpg 1.65.22** / balance_version 45（`COORD-routine-0141`，2026-10-05）。Stage C p1sim deferred（同 D181/D176）。
> 前置：D138（限时清房）、D144（花样委托按「房间事件达标」泛型计数）、D171（设计 Pack 2）、D176（Pack 2 上线：词缀 6 + 事件 timed/crystal/escort）。
> 实现对齐：等 `COORD-mainline-unlocks`（ml-wt）与 `COORD-afk-p1`（p1sim）释放后，找空闲 CoreRpg 窗落地；落地前用离线 sim 过门禁（见 §8）。
> 源表：`DESIGN-ember-v1.0-P1-source-table.md` 的 D179 行 + §13.xx **本窗不改**（afk-wt 正改该文件）；下一 routine 在 afk 释放后补写。
> 参考（研究笔记，非自制设定）：见 §R。

## R. 研究笔记（短）

| 参考 | 玩家行为 | 对本包的启发 |
|---|---|---|
| Path of Exile Blight / 占道 | 站在路径上挡波，离开进度停 | **占点**：站位税，不是 DPS 乘区 |
| Diablo 诅咒箱 / 神龛事件房 | 可选侧目标，改优先级与走位 | 事件失败不罚、门照开（与 D138/D171 一致） |
| WoW 地下城侧目标（占点 / 护灯 / 点灯序） | 控场、拉仇恨、按序交互 | **护灯**（固定易碎物）≠ 护送活物；**传火**（有序点亮）≠ 乱序砸晶 |
| 国服 1.12 RPG 常见「护送 / 破坏 / 占点」 | 护驾、拆物、站圈 | 复用现有 spawn/block/GLOWING 形状，不新开 Mythic 技能类型 |
| PoE Legion / Betrayal（轻量） | 时限内优先目标 | 软时限 + 顺序，**禁止**永久 buff / 反伤 / 伤害乘区 |

## 0. 一段话裁决

在**已首通的普通版 Q01–Q07**（与 D138/D171/D176 同一闸门）上，**只扩房间事件**：词缀池保持 6（炽热/分裂/厚甲/再生/冲锋/凝霜），**本包不加词缀**；事件池从 3（限时 / 砸晶 / 护宝兔）扩到 **6**，新增 `hold` 占点、`beacon` 护灯、`relay` 传火。全部是**侧向花样**：改站位 / 仇恨优先级 / 交互顺序，不抬永久乘区、不加新货币、不改 `event_core` 数量、不发化妆品。`event_rate` 仍约 0.5；花样委托继续按 `eventDone` 泛型计数。否决破笼（砸块≈砸晶）、抽水（站位≈占点）、反伤、分享伤害圈、永久 buff。本窗只出文档。

## 1. 白话版（给服主 / 玩家）

- 重打已通的普通图时，约一半局里某个房间仍会出可选事件。原来的三种还在：**限时清房**、**砸余烬晶**、**护宝兔**。
- 新增三种：
  - **占点**：房中心亮一圈，有人站在圈里才涨进度；进度满再清完 → 有核心奖。多人可分摊站圈。
  - **护灯**：房里竖一盏发光灯柱，怪会去砸灯；灯灭了就没事件奖，门照开。玩家要拉怪、挡刀，护到清房。
  - **传火**：房内 2～3 个按序发光标记，必须按 1→2→3 点亮（踩上去或右键），软时限内点完再清完。
- 奖励不变：事件成功 → 结算时仍是 **余烬核心碎片**（现 `event_core: 1`）。花样委托「做 1 次房事件」照旧，**新类型也算**。
- 首通、挑战、深渊、团本、连战、活动本：**没有**这套花样（闸门不变）。

## 2. 与现网 D176 的对应（先读代码再定稿）

| 现网（D176） | Pack 3（D179） |
|---|---|
| 闸门：NORMAL + 全员已首通该图 | **不变** |
| 种子：`subSeed(seed,"variety")`，重连不重抽 | **不变**；新 `eventKind` 一并写进 roll |
| `affixes: [blazing, split, shield, regen, charge, frost]` | **不变**（本包不加词缀） |
| `events: [timed, crystal, escort]` | 追加 `hold` / `beacon` / `relay`（等权；目标池 6） |
| `event_rate: 0.5`，`event_core: 1` | **不变**（改数须过 §8，且本设计默认不改） |
| 花样委托 kind `timed` =「房间事件达标」 | **不变**；凡 `eventDone` 都计 |
| 化妆品 / 宠物 / 光环奖励 | **不做** |

代码锚点（只读，本窗不改）：

- `plugins/CoreRpg/ember-v1-runs.yml` → `variety:`（现 `events: [timed, crystal, escort]`）
- `EmberRunMaps.Variety`（`EVENTS`、`eventLabel`、`eventLimit`、`roll`）
- `EmberRunDirector`：房开始 / 房清 / `placeCrystals` / `pollCrystals` / `spawnEscort` / `removeEscort` / GLOWING / 既有 circle·cone·line·charge 形状
- `EmberRunService.onEventResult` / `varietyGrants` / 强制钩子 `runs variety …` / `onCrystalBreak`
- `EmberRunRules.VarietyBounty`（kind `timed` 泛型）

## 3. 词缀表

**本包不改。** 仍为 D176 六缀；否决再加 `reflect` 等。实现清单勿扩 `KNOWN`。

## 4. 房间事件表（旧 3 + 新 3）

闸门与 D138 相同；`event_rate` 仍约 0.5。成功 → 结算 `event_core`（现 1）；失败不罚、门照常开（可选事件）。

| id | 玩家名 | 机制（玩家要做什么） | 成功条件 | 失败 | 复用 / 新钩子 |
|---|---|---|---|---|---|
| `timed` | 限时清房 | 现网：进房开表，secs 内清完 | 清房用时 ≤ secs | 超时无核心 | **无**（已有） |
| `crystal` | 砸余烬晶 | 现网：砸 3 块发光晶再清完 | 碎晶 ≥ count 且可清；软时限 | 超时未砸够 | **无**（已有；含 poll 防创造瞬挖漏计） |
| `escort` | 护宝兔 | 现网：护发光小兔到清房 | 清房时兔仍存活 | 兔死 → 失败 | **无**（已有；不走宝藏币） |
| `hold` | 占点 | 房 trigger 内放**一圈**占点区（粒子圈 + 脚下可见标记，半径可配）；**圈内有 ≥1 名在场玩家**时累计秒数（多人**不**加速，只分摊站位压力）；进度 HUD/聊天每 25% 一行；怪可把人打出圈 | `held_secs ≥ need` **且**房间已可清；软时限 `secs`（超时仍可清房但事件失败） | 超时进度未满 / 退房未满 | **复用** blazing 式 circle 判定 + tick 累计；**不**需要新 Mythic；标记用盔甲架/海晶灯柱均可，清房/失败/卸载还原 |
| `beacon` | 护灯 | 进房在刷怪点附近竖 **1** 个易碎灯柱（建议 SEA_LANTERN 或带 GLOWING 的盔甲架 + 名牌「余烬灯」）；灯有独立 HP（相对该图 melee HP × `hp`）；附近怪每 `tick` 若无玩家在灯 `aggro_r` 内，则优先「咬灯」扣血（对灯伪伤害，不走玩家受伤公式）；玩家靠近可吸走仇恨 | 清房时灯仍存活（HP>0） | 灯碎 → 事件失败（聊天一行）；门仍开 | **复用** escort 的「存活到清房」判定骨架 + crystal 的放置/清理；**不是**第二只护宝兔：灯**不移动**、不进 `mobs` 清房计数、不掉宝藏币；怪咬灯用 director tick 伪伤，避免新 Mythic 技能 |
| `relay` | 传火 | 进房放置 `count`（2～3）个**有序**标记（GLOWING 盔甲架或不同颜色羊毛/灯，名牌「传火 1/2/3」）；玩家须按序踩入半径或右键交互点亮；跳序无效（一行提示「先点亮上一处」）；全部点亮后才算事件进度满 | 顺序点亮全部 **且**房间已可清；软时限 `secs` | 超时未点完 | **复用** crystal 坐标集 + 玩家进圈检测（与 hold 同源）；标记清房/失败/卸载一律清除；**禁止**乱序等同 crystal |

事件开房提示（一行）：

- hold：`§b占点 §7· 站进发光圈攒满进度再清完 → 结算时 §f余烬核心碎片 +N §7（可选）`
- beacon：`§b护灯 §7· 清房前别让发光灯柱被砸碎 → 结算时 §f余烬核心碎片 +N §7（可选）`
- relay：`§b传火 §7· 按 1→2→3 点亮标记再清完 → 结算时 §f余烬核心碎片 +N §7（可选）`

建议实现默认值（可 yml 调；落地前用 sim 扫）：

```yaml
hold:   {radius: 2.5, need: 12.0, secs: 40}     # 满进度约 12s 站圈；软时限 40s
beacon: {hp: 0.45, aggro_r: 4.0, tick: 1.0, bite: 0.08}  # 每秒无玩家护卫则扣灯最大生命 8%
relay:  {count: 3, radius: 1.6, secs: 40}
```

## 5. 配置草图（键 only，`variety:` 下）

```yaml
variety:
  affix_rate: 1.0
  affixes: [blazing, split, shield, regen, charge, frost]   # Pack 3 不改
  affix_shard: 2
  # …既有 blazing/split/shield/regen/charge/frost 块不变…
  event_rate: 0.5
  events: [timed, crystal, escort, hold, beacon, relay]     # 新：等权抽一种
  event_secs: 30
  event_core: 1                                             # 数量不变
  timed:   {secs: 30}
  crystal: {count: 3, secs: 35}
  escort:  {hp: 0.35}
  hold:    {radius: 2.5, need: 12.0, secs: 40}
  beacon:  {hp: 0.45, aggro_r: 4.0, tick: 1.0, bite: 0.08}
  relay:   {count: 3, radius: 1.6, secs: 40}
```

`ember-v1.yml` `bounty.variety:` **键不变**（kind `timed` 仍表「房间事件达标」）。

测试钩子扩（实现窗）：`/corerpg p1 runs variety hold[:rN]|beacon[:rN]|relay[:rN]|…`

## 6. Java 触点清单（**本窗不写代码**）

| 文件 | 改什么 |
|---|---|
| `EmberRunMaps.Variety` | `EVENTS` +hold/beacon/relay；解析三块参数；`eventLabel` / `eventLimit`；`roll` 仍返回 4 槽 |
| `EmberRunSession` | 无新持久字段（`eventKind` 已有）；局内进度由 Director 持有 |
| `EmberRunDirector` | 房开始：hold 放圈 / beacon 放灯 / relay 放有序标记；tick：hold 累计、beacon 咬灯、relay 进圈点亮；房清：按 kind 判定；卸载清理方块/盔甲架；beacon 碎 → `eventFailed`（对齐 escort 死） |
| `EmberRunService` | `onEventResult` 文案按 kind；强制 variety 钩子；结算来源前缀加「占点 / 护灯 / 传火」 |
| `EmberRunRules.VarietyBounty` | 注释写明含 hold/beacon/relay；label 仍「房间事件达标」 |
| 单测 | 见 §9 |
| Mythic | **不需要**新技能类型；beacon/relay/hold 全部走既有 spawn/block/GLOWING |

**明确不做（本包）**：新词缀、套装键、永久乘区、新材料/货币、化妆品奖励、挑战/深渊污染、改 `balance_version`（本设计提交）、改 p1sim（另窗）、改源表（afk-wt 占用中）。

## 7. 漏洞审查

| 场景 | 风险 | 设计约束 |
|---|---|---|
| 事件中途重连 | 重抽 / 双开表 | roll 仍绑 `seed`；`eventKind`/`eventRoom` 在 session；进度可重置为 0（可接受）或写 session——实现选「重连进度清零、种类不变」，禁止重 roll |
| 退房再进 / 换号同实例 | 多领 | 结算只走 `onRunSettle` 一次；`eventDone` 每局一次性 |
| 双索赔 | 同键重复加 | `varietyGrants` 按布尔一次；勿在 `onEventResult` 当场发物 |
| 挑战 / 深渊 / 团本 / 连战 / 首通 | 花样串模式 | **不放宽闸门**；单测断言这些模式 variety 空 |
| 占点 AFK 挂圈 | 站圈刷委托 | 日 cap + 体力；每局最多 1 次事件；可接受 |
| 占点创造飞天站圈 | 空中蹭进度 | 判定须 `onGround` 或脚下方块非 AIR（实现二选一，推荐 onGround） |
| 护灯变第二宝藏怪 | 叠 Extra 币 / 清房计数 | beacon **不**进 `mobs` 清房列表、不走 `Extra.TREASURE`；独立 track 或纯方块+盔甲架 |
| 护灯残留 / 传火标记残留 | 世界污染 | 清房 / 失败 / 卸载 / 实例回收一律清除；禁止掉落物 |
| 传火跳序 / 一次踩亮全部 | 顺序被绕过 | 严格 `nextIndex`；未到序的标记交互无效 |
| 创造瞬挖灯柱漏计 | 同 crystal | beacon 若用方块：复用 `pollCrystals` 式轮询；盔甲架则听死亡/移除 |
| 每周/每日委托刷 | 多开事件种类刷币 | 委托仍日 cap；种类增多只改体验不改次数 |
| 与词缀精英同房 | 过难 | 事件与词缀可同房（现网已允许）；门禁用 42 格通关率兜住；必要时降 `beacon.bite` / `hold.need` |
| 多人占点加速 | 进度翻倍破坏软时限 | **明确**：多人只分摊站位，**不**提高 `need` 填充速率 |

## 8. 平衡门禁（实现窗，非本窗）

本窗 **不**跑 p1sim（afk-wt / gear 相关占用中）。实现前 / 合并前须：

1. `p1sim` 42 个首通 / 挑战格：有花样 vs `--no-variety` 通关率 **±3pp**（用户 10-04：「within range」即可）。
2. **不**强制跑 `p2econ`（奖励键与数量未变）；若实现窗误改 `event_core` / 委托币，再补 p2econ ±0.5 周。
3. 若超标：先降 `beacon.bite` / 升 `hold.need` 放宽、或新事件权重 0.7（旧 1.0），**禁止**用加奖励补偿难度。
4. 奖励数默认保持 `event_core: 1`；若经济偏快，只允许减不允许加。

## 9. 验收（实现后）

**单测（建议名）：**

- `varietyPack3RollsNewEventsOnly_D179` — 种子稳定；EVENTS 含 6；KNOWN 仍 6；首通 / 挑战 / 深渊 / 团本 roll 空。
- `varietyBountyCountsHoldBeaconRelay_D179` — 三新事件成功 → 推进 kind=`timed` 委托。
- `holdRequiresGroundPresence_D179` — 圈外 / 离地不累计。
- `beaconDoesNotPayTreasureOrBlockClear_D179` — 无宝藏币；灯不挡清房计数。
- `relayEnforcesOrderAndCleanup_D179` — 跳序无效；失败/卸载无残留。
- `pack3RejectsCageDrainAsConfig_D179`（可选）— 配置写 `cage`/`drain` 被忽略。

**冒烟（政策：几分钟，禁长测）：** FreshQ 号；`runs variety hold` / `beacon` / `relay` 各一局 Q01 普通重打，确认聊天一行、结算核心、委托计数、无残留方块/盔甲架。不做通关率扫图。

**资产：** 本设计不改资产路径；实现窗若动结算钩子，顺带跑 loss/dup 脚本。

## 10. 分期落地

| 阶段 | 内容 | 依赖 |
|---|---|---|
| **本窗 D179** | 本文；源表行留给下一 routine | 无代码；**不**改 source-table（afk-wt） |
| Stage A（yml+Java） | EVENTS +3；hold/beacon/relay 逻辑；限时/砸晶/护宝兔回归 | mainline + afk/p1sim 锁释放；空闲 CoreRpg 窗；JDK8 构建 |
| Stage B | 冒险页 / 图鉴三行说明 | Stage A |
| Stage C | p1sim 42 格 ±3pp 门禁报告；必要时调参 | p1sim 锁释放后 |

## 11. 否决清单（本包明确不做）

| 想法 | 原因 |
|---|---|
| `cage` 破笼（砸笼放友方 / 杀笼内精英） | 砸块 ≈ `crystal`；杀笼内精英 ≈ 额外精英优先级，易叠奖或变弱克隆 |
| `drain` 抽水/灭火（踩渠道 / 开关） | 站位税与 `hold` 重叠；开关次数易变成无脑连点 |
| 新词缀 / `reflect` 反伤 | 本包范围 = 房间事件 only；反伤已在 D171 否决 |
| 分享伤害圈（强制组队） | 单人局惩罚；用户硬约束 |
| 永久 buff / 新货币 / 化妆品奖励 | 用户 10-04：无永久威力、化妆品后置 |
| 新 Mythic 技能类型 | 优先复用 circle/GLOWING/block/treasure 旁路 |
| 改 `event_core` 数量或新奖励键 | 经济门禁；默认不动 |
| 本窗改 source-table / CoreRpg / p1sim / 部署 | 车道隔离 |

## 12. 决策编号

- **D179** = Room Events Pack 3 设计定稿（本文）。
- D176 = Variety Pack 2 已上线（词缀+3 / 事件+2）。
- D173 = Boss Moves Pack 2 设计已出、实现仍 deferred（需 CoreRpg+p1sim；本窗不抢）。
- 源表 D179 行 + §13.xx：由下一 routine 在 `afk-wt` 释放 `DESIGN-ember-v1.0-P1-source-table.md` 后追加。
