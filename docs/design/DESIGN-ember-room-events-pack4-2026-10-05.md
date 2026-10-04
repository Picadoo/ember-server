# Ember Room Events Pack 4：房间事件扩包（D191，2026-10-05）

> 状态：**已实现并上线 CoreRpg 1.65.29** / balance_version 52（`COORD-routine-0745`，2026-10-05）。冒烟按政策合批（本包 = 新一批未测上线的第 1 个）。
> 前置：D138（限时清房 + 闸门）、D144（花样委托按「房间事件达标」泛型计数）、D171/D176（Pack 2：砸晶 / 护宝兔）、D179（Pack 3：占点 / 护灯 / 传火，1.65.22）。
> 结构完全照 D179：只扩 `variety.events`，复用开房提示 → tick → 清房判定 → `onEventResult` → 结算 `varietyGrants` 的同一条链。
> 门禁：`tools/p1sim/eventpack4.py` → `tools/p1sim/out-eventpack4-d191.md`（within range，见 §8）。

## R. 参考研究（先看成熟游戏，再定事件）

| 参考 | 原作里玩家在做什么 | 我们拿什么 | 我们**不**拿什么（及原因） |
|---|---|---|---|
| **Path of Exile · Breach（裂隙，3.0 联盟后进核心）** | 碰「手」开一个会扩张的裂隙圈，圈里刷裂隙怪；在圈内杀怪延长 / 撑大裂隙，停手它就收拢消失 | **`breach` 裂隙**：一个会收缩的圈，**圈内击杀**把它撑开；圈合上 = 事件失败。改变的是「在哪里打」：把怪拉进圈、别追出去 | **不刷额外怪、不掉裂隙碎片**——Breach 的奖励来自额外怪和碎片，属于加收益；本包只要「站位 + 击杀节奏」 |
| **Diablo III · Massacre（屠杀连击）** | 短时间内连续击杀，屏幕弹「Massacre ×N」并给经验加成 | **`chain` 连斩**：每两次击杀间隔 ≤ gap 秒、连杀 N 只。改变的是「怎么打」：聚怪、留 AOE、别一只只放风筝 | **不给经验 / 属性加成**（那是乘区）；只是一个达成条件，奖励仍是固定核心 1 |
| **传奇 / 国服 1.12 私服「连斩」系统** | 连续击杀计数，断档清零，连斩数越高提示越响（常配连斩奖励） | 同上：断档清零的计数 + 聊天连斩提示 | **不做**连斩阶梯奖励、连斩 buff（永久或临时属性都不做） |
| **Path of Exile · Sanctum（圣域，3.20）的 Resolve（决心）** | 每被打中一次扣决心，扣光就失败；整个玩法围绕「别挨打」 | **`unscathed` 无伤**：清房时全队被打中次数 ≤ 预算。改变的是「怎么打」：躲预警、拉开距离、先杀远程 | **不做**决心资源 / 圣域遗物 / 负面词条交换奖励（那是奖励操控） |
| **WoW 地下城成就「击败 X 且无人被 Y 命中」**（大量 5 人本 / 团本成就同模） | 可选挑战，不影响通关，只要求更干净的打法 | 「可选、失败不罚、门照开」的定位（与 D138/D171/D179 一致）；多人时预算按人数加 | 不做成就点 / 称号外观（化妆品后置） |
| Diablo III Cursed Chest / D4 Helltide 类波次事件 | 限时内打完额外刷出的波次 | — | **否决**：额外波次 = 额外击杀 / 掉落 / 经验，违反「只改玩法不改收益」 |
| PoE Ritual / Harbinger / Legion / Delirium | 祭坛重抽奖励、额外通货怪、冻结额外怪群、风险越高奖励倍率越高 | — | **否决**：全部是奖励操控或奖励倍率 |

结论：三个新事件都只改「这个房间怎么打」——在哪里打（裂隙）、打的节奏（连斩）、打得干不干净（无伤）。奖励键、数量、频率全不动。

## 0. 一段话裁决

在**已首通的普通版 Q01–Q07 重打**（与 D138/D171/D179 同一闸门；首通 / 挑战 / 深渊 / 团本 / 连战 / 活动本不出）上，事件池从 6（限时 / 砸晶 / 护宝兔 / 占点 / 护灯 / 传火）扩到 **9**，等权新增 `breach` 裂隙、`chain` 连斩、`unscathed` 无伤。`event_rate` 仍 0.5，成功仍只记 `event_core: 1`（余烬核心碎片），花样委托 kind `timed`（房间事件达标）照常计数。不加怪、不加掉落、不加乘区 / 永久属性 / 装备槽 / 货币 / 化妆品；视觉只用现有框架（GLOWING 名牌盔甲架 + 海晶灯 + `warnCircle` 粒子圈，与占点同款）。

## 1. 白话版（给服主 / 玩家）

- 重打已通的普通图时，约一半局里某个房间会出一个可选事件。原来六种都还在，新增三种：
  - **裂隙**：房间中间出一个紫色圈，会慢慢缩小；在圈里杀怪能把它撑大一点。清完房间时圈还没合上就算成功。
  - **连斩**：连续杀 4 只怪（房间怪少于 4 只就是全部），每两只之间不超过 4 秒。断了就从头数。
  - **无伤**：清完这个房间时，全队被怪打中不超过 4 次（每多 1 个队友 +2 次）。
- 成功 → 结算时 **余烬核心碎片 +1**（和其他房间事件一样）；失败没有惩罚，门照开。
- 花样委托「1 次房间事件达标」——新事件也算。

## 2. 与现网 D179 的对应

| 现网（D179 / 1.65.22 起） | Pack 4（D191） |
|---|---|
| 闸门：NORMAL + 全员已首通该图 | **不变** |
| 种子：`subSeed(seed,"variety")`，`roll` 返回 4 槽，重连 / 重进不重抽 | **不变**；新 kind 进同一个 roll |
| `affixes` 10 种 | **不变** |
| `events: [timed, crystal, escort, hold, beacon, relay]` | 追加 `breach, chain, unscathed`（等权，池 9） |
| `event_rate: 0.5`，`event_core: 1` | **不变** |
| 结算源键 `var_event_*` | 新增 `var_event_breach / chain / unscathed`（同一 MAT_CORE、同数量，只是账单前缀「裂隙 / 连斩 / 无伤」） |
| 花样委托 kind `timed` | **不变**；凡 `eventDone` 都计 |

## 3. 词缀表

**本包不改。**

## 4. 房间事件表（旧 6 + 新 3）

| id | 玩家名 | 机制 | 成功条件 | 失败 | 实现（复用） |
|---|---|---|---|---|---|
| `breach` | 裂隙 | 房锚点（第 1 个刷怪点，同 D179 `eventAnchorPt`）放海晶灯 + GLOWING 名牌「[裂隙] 半径 x.x」；紫色 `warnCircle`（SPELL_WITCH，与首领召唤点同款）每 tick 画当前半径；半径每秒缩 `shrink`；**本房怪**死在圈内（水平距离）→ 半径 + `grow`（≤ `max`） | 清房时半径 > `min` | 缩到 ≤ `min` → 聊天一行「裂隙合上了」，标记清除，门照开 | D179 hold 的放置 / 清理 / 画圈；击杀钩子在 `EmberRunDirector.onDeath` |
| `chain` | 连斩 | 开房告知需要 N = min(`need`, 本房实际刷怪数) 只；每次本房怪死亡：距上次 ≤ `gap` 秒则 +1，否则重置为 1；连斩 ≥2 时聊天「连斩 ×k / N」，达成时一行 | 清房时最佳连斩 ≥ N | 清房时未达成（无倒计时） | 纯计数；无方块 / 盔甲架 |
| `unscathed` | 无伤 | 开房按在场人数定预算 = `hits` + `per_member` ×(人数−1)；房间进行中，**任何真落地**的本局怪物命中（普攻、投射物、技能 / 词缀预警、精英变招——都以怪或其投射物为 damager）+1；到一半和用满时各提示一行 | 清房时命中数 ≤ 预算 | 超出 → 立刻一行「无伤失败」，门照开 | `EmberRunService.onRunHitTaken`（MONITOR、ignoreCancelled、finalDamage > 0；被间隔护栏 / 眩晕 / 施法者近战取消的命中**不算**） |

yml（`ember-v1-runs.yml` → `variety:`，balance_version 52）：

```yaml
  events: [timed, crystal, escort, hold, beacon, relay, breach, chain, unscathed]
  breach: {radius: 5.0, min: 1.5, max: 7.0, shrink: 0.15, grow: 0.8}   # 不杀怪约 23 秒合上；每次圈内击杀 +5.3 秒
  chain: {need: 4, gap: 4.0}
  unscathed: {hits: 4, per_member: 2}
```

钳位（`EmberRunMaps.Variety`）：breach min 0.5–4、max ≥ min+1 且 ≤10、radius ∈ [min+0.5, max]、shrink 0.01–1（**永远会合上**，不能配成永不失败）、grow 0–3；chain need 2–8、gap 1–10；unscathed hits 0–20、per_member 0–10。未知 kind（如 `ritual` / `harbinger`）在解析时丢弃。

开房提示（一行，同旧事件格式）：

- 裂隙：`§b裂隙 §7· 紫圈会慢慢收拢，在圈里杀怪能把它撑开；清房时圈没合上 → 结算时 §f余烬核心碎片 +1 §7（可选）`
- 连斩：`§b连斩 §7· 连续击杀 N 只怪，每两次击杀间隔不超过 4 秒 → 结算时 §f余烬核心碎片 +1 §7（可选）`
- 无伤：`§b无伤 §7· 清完这个房间时全队被怪打中不超过 M 次 → 结算时 §f余烬核心碎片 +1 §7（可选）`

测试钩子（已有泛型，无需改代码）：`/corerpg p1 runs variety breach[:rN]|chain[:rN]|unscathed[:rN]`（`EmberRunMaps.Variety.EVENTS.contains`）。

## 5. Java 触点（实现）

| 文件 | 改动 |
|---|---|
| `EmberRunMaps.Variety` | `EVENTS` 9 项；`breach*/chain*/unscathed*` 解析 + 钳位；`eventLabel` 裂隙 / 连斩 / 无伤；`eventLimit` 三者 0（无倒计时，不发「还剩 10 秒」） |
| `EmberRunDirector` | 开房：`placeBreach` / `chainReset(spawned)` / `unscathedReset(party)`；tick：`tickBreach`（缩圈、画圈、合上即失败）；`onDeath` → `noteEventKill`（裂隙圈内击杀撑开 / 连斩计数）；`noteHitTaken`（无伤计数）；清房按 kind 判定；清房 / `finish` 一律 `clearBreach`；纯函数 `breachStep/Grow/Collapsed/Inside`、`chainNext/NeedFor`、`unscathedBudget/Ok` 供单测 |
| `EmberRunService` | `onRunHitTaken`（MONITOR 计落地命中）；`matSource` 三个新前缀 |
| `EmberRunRules.varietyGrants` | 源键 `var_event_breach/chain/unscathed`（同 MAT_CORE、同 `eventCore`）；`VarietyBounty` 注释 |
| TrMenu `ember_p1_codex` / `ember_p1_adventure`；`ember-v1.yml` 注释 | 事件列表 6 → 9 + 图鉴一行三事件说明 |
| Mythic | **不需要**任何新技能 / 新怪 |

## 6. 明确不做

新怪 / 额外波次、额外掉落 / 经验、连斩 buff、伤害或减伤乘区、新货币 / 新材料、新装备槽、化妆品（新粒子 / 光环 / 称号）、改 `event_core` / `event_rate`、挑战 / 深渊 / 团本 / 连战 / 首通放开闸门。

## 7. 漏洞审查

| 场景 | 风险 | 处理 |
|---|---|---|
| **双领 / 重复结算** | 一局两次核心 | 每局最多 1 个事件房（roll 只给一个 `eventRoom`）；`onEventResult` 只置 `s.eventDone`（已 done 直接 return），核心只在 `onRunSettle` → `varietyGrants` 按布尔发一次；单测 `pack4PaysExistingCoreOnceOnly_D191` |
| **复制（dup）** | 事件物被拾取 / 盔甲架掉落 | 裂隙标记是不可破坏的盔甲架 + 海晶灯，清房 / 失败 / `finish` 全清；不发实体物品，只记结算材料；不碰资产路径（仓库存取 / 拆解 / 撤销 / 快照 / 扭蛋 / 补发全部未改） |
| **周 / 日上限绕过** | 多刷事件冲委托 | 花样委托仍按日 cap、仍是同一个 kind `timed`；池子变大只改体验不改次数；`event_rate` 不变 → 每局期望事件数不变 |
| **重进 / 放弃重来** | 放弃刷一个「简单事件」再进 | 事件种类绑定 `subSeed(seed,"variety")`，同一局重连不重抽（单测 `assertArrayEquals` 同种子同结果）；放弃 = 本局无结算（只有开战前离开才退体力），重开是新局新种子；只有完成整局（击杀首领）才结算；新事件成功率 ≤ 旧事件参考带（§8），挑事件没有收益 |
| **无伤：重连清零** | 挨打后掉线重进把计数清零 | 计数在 Director（每局一个，跟随副本世界），玩家重连不新建 Director；只有服务器重启会丢失（此时整局按既有逻辑处理） |
| **无伤：躲出房间让队友打** | 不挨打白拿 | 预算是**全队合计**；房间必须清完才判定；远程风筝本身就是该事件想鼓励的打法 |
| **无伤：伤害被取消也算 / 不算** | 计数被刷 | 只在 MONITOR + ignoreCancelled + finalDamage > 0 计；被间隔护栏、首领眩晕、施法者近战取消的不算 |
| **裂隙：飞行 / 卡点** | 不站圈也蹭进度 | 判定看**怪死的位置**（水平距离），与玩家站哪无关；不存在「站圈累计」 |
| **裂隙 / 连斩：分裂词缀小怪凑数** | 精英分裂出的小怪让连斩变容易 | 分裂小怪本就是该房怪（D138），同房出现属正常；奖励固定 1 核心，最多变「更容易成功」，期望收益上限 = 旧池 |
| **AFK 挂机** | 站着不动拿事件 | 三个新事件都要求**清房**（击杀）；AFK 不能清房；挂机庭（`ember_afk`）不是主线副本，不出事件 |
| **残留** | 世界污染 | `clearBreach` 在清房、失败、`finish`（含放弃 / 团灭 / 卸载）都调用；连斩 / 无伤无实体 |
| **与词缀精英同房** | 过难 | 现网已允许事件 + 词缀同房；无伤最难，失败只少 1 核心，不罚、门照开 |

## 8. 平衡门禁（离线 sim，已跑）

`tools/p1sim/p1sim.py`：`Fight` 记录落地命中数（`nhit`）与每只怪死亡时间（`_dt`），`run_map` 对事件房按 kind 判定（`event_ok`：旧 6 种保持原来的「event_secs 内清房」模型；裂隙 = 缩圈 + 圈内击杀（近战系 90%、其他 50% 在圈内）；连斩 = 死亡时间序列最长 ≤gap 连段；无伤 = 房内落地命中 ≤ hits）。kind 用独立随机流抽，不动战斗流——**通关率按构造不变**（旧池输出与 D189 表逐币一致，证明插桩未移动基线）。

结果（`out-eventpack4-d191.md`）：

- Part A（Q01–Q07 × 书参考 / T3+6 × 躲避 0.3/0.5/0.7，1500 局 × 3 种子）：参考带 97%，裂隙 97%，连斩 97%，**无伤 83%**（书参考装 + 躲避 0.3 仅 31–58%）。新事件均不高于参考带（上限 +10 pp）→ 每次出事件的期望核心 0.969 → 0.952（更难，不更肥）。
- Part B（p2econ 120 人 × 12 周，躲避 0.5，新 9 种池 vs 旧 6 种池）：所有列一致，W12 币中位 57740/57745 → 57740/57735。
- 裁决：**within range**。sim 清房快、无走位，所以裂隙 / 连斩在 sim 里偏易；真人更难只会让收益更低，不需要补偿。真人数据回来后若某个事件成功率远低于其他（如无伤 < 30%），只允许放宽参数（`hits` +1、`gap` +1），**不允许加奖励**。

## 9. 验收

**单测（+6，合计 291/0）：** `varietyPack4RollsBreachChainUnscathed_D191`（参数、标签、无倒计时、种子稳定、roll 能出全部 9 种、session 往返）、`pack4PaysExistingCoreOnceOnly_D191`（源键、MAT_CORE×1、首通 / 失败不发、委托 label）、`breachShrinksGrowsAndCollapses_D191`、`chainNeedsKillsWithinGap_D191`、`unscathedBudgetScalesPerMember_D191`、`pack4ConfigClampsAndUnknownKindsDropped_D191`（ritual / harbinger 被丢弃、钳位）。另：两处 EVENTS 计数 6→9、balance_version 52。

**冒烟（政策：合批，本包不单独跑）：** 下一批合批冒烟时 FreshQ 号 `runs variety breach` / `chain` / `unscathed` 各一局 Q01 普通重打：开房一行、裂隙紫圈与名牌半径递减 / 圈内击杀回弹、连斩 ×k 提示、无伤计数提示、结算「裂隙 / 连斩 / 无伤 余烬核心碎片 +1」、委托计数、无残留海晶灯 / 盔甲架。

**资产：** 不改资产路径 → 不需要 `persist-roundtrip.sh`。

## 10. 决策编号

- **D191** = Room Events Pack 4（本文），CoreRpg 1.65.29 / bv52。
- D179 = Pack 3（占点 / 护灯 / 传火，1.65.22）；D189 = Affix Pack 4（1.65.28）；D190 = L11b 重扫（设计-only）。
