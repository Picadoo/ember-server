# Ember Variety Pack 2：重复刷图花样扩包（D171，设计-only，2026-10-04）

> 状态：**已实现并上线 CoreRpg 1.65.6（D176，2026-10-04）**。Stage C p1sim 门禁仍 deferred（gear-stage0 锁）。
> 前置：D138（`variety:` 炽热 / 分裂 / 厚甲 + 限时清房）、D144（花样委托按「词缀精英击杀 / 房间事件达标」泛型计数）、D147（厚甲用词）、D164（破甲只认 shield/blazing/split）。
> 实现对齐：等 `COORD-asset-fix-1655` DONE，再找空闲 CoreRpg 窗落地；落地前用离线 sim 过门禁（见 §8）。
> 参考：PoE 稀有词缀（Regen / Temporal Chains 类减速圈）、Diablo 精英词缀（Teleporter/Molten 式预警圈 + 冲锋）、既有 `EmberRunDirector` 的 `charge` 技能与 D138 `promote/affixTick/splitAdds`。

## 0. 一段话裁决

在**已首通的普通版 Q01–Q07**（与 D138 同一闸门）上，把词缀池从 3 扩到 **6**、房间事件池从「只有限时清房」扩到 **3**，全部是**侧向花样**：改走位 / 集火 / 护驾，不抬永久乘区、不加新货币、不改结算表；花样委托继续按「击败词缀精英」「房间事件达标」泛型计数，新类型自动进同一日委托。反伤词缀否决。本窗只出文档。

## 1. 白话版（给服主 / 玩家）

- 重打已通的普通图时，发光精英除了炽热 / 分裂 / 厚甲，还可能是：**再生**（按脉冲回血，发光打断窗里猛打可打断）、**冲锋**（脚下亮一条直线，≥1.2 秒预警后冲过来，躲开条带）、**凝霜**（身边一圈减速，走出圈就没事）。
- 约一半局里某个房间会出可选事件：原来的**限时清房**还在；新增**砸余烬晶**（房内 3 块发光晶，清房前砸掉）和**护宝兔**（一只发光小兔必须活着到清房，死了就没有事件奖，门照开）。
- 奖励还是：打掉词缀精英 → 余烬碎片；事件成功 → 余烬核心碎片。花样委托「打 2 只词缀 / 做 1 次房事件」照旧，**新类型也算**。
- 首通、挑战、深渊、团本、连战、活动本：**没有**这套花样（闸门不变）。

## 2. 与现网 D138 的对应（先读代码再定稿）

| 现网 | Pack 2 |
|---|---|
| 闸门：NORMAL + 全员已首通该图 | **不变**（`EmberRunService` 进本滚 `variety` 的条件） |
| 种子：`subSeed(seed,"variety")`，重连不重抽 | **不变**；事件类型一并写进 roll 结果 |
| `affixes: [blazing, split, shield]` | 追加 `regen` / `charge` / `frost`（等权；实现时 `Variety.KNOWN` 扩表） |
| 只滚 `eventRoom`，逻辑写死限时清房 | 另滚 `eventKind ∈ {timed, crystal, escort}`；`eventDone` 仍是一个布尔 |
| 奖励 `affix_shard` / `event_core` | **数量与键不变**（现网 shards=2、core=1；实现窗若要微调须过 §8 门禁） |
| 花样委托 kind `affix` / `timed` | id **保留**（存档兼容）；`timed` 文案改为「房间事件达标」，凡 `eventDone` 都计 |
| 破甲 `dmg_affix_shield/blazing/split` | 新三缀**暂不**加专属键，只吃通用 `dmg_affix`；专属侧向键另开成长窗 |
| 化妆品 / 宠物 / 光环奖励 | **不做**（用户 10-04：化妆品后置） |

代码锚点（只读，本窗不改）：

- `plugins/CoreRpg/ember-v1-runs.yml` / `CoreRpg/.../ember-v1-runs.yml` → `variety:`
- `EmberRunMaps.Variety`（`KNOWN`、`roll`、`label`）
- `EmberRunDirector.promote` / `affixTick` / `splitAdds` / 房间开清里的限时事件 / 既有 `charge` 技能执行
- `EmberRunService.onAffixDone` / `onEventResult` / `varietyGrants` / 花样委托结算
- `EmberRunRules.VarietyBounty.label()`（现「限时清房达标」）

## 3. 词缀表（新 3 + 旧 3）

奖励列对所有词缀相同：击败 → 结算 `affix_shard`（现 2）；不发新币种。

| id | 玩家名 | 机制（玩家要做什么） | 预警 / 可读 | HP / 伤害预算 | 否决备注 |
|---|---|---|---|---|---|
| `blazing` | 炽热 | 脚下火圈，躲开 | warn 1.0s，粒子火 | dmg×自身 atk（现网） | 已有 |
| `split` | 分裂 | 死后出分身，门等分身 | 名牌 + 粒子 | 分身 hp×0.5×2 | 已有 |
| `shield` | 厚甲 | 站桩硬砍 | 名牌发光 | HP×1.6 | 已有；**不要**再造同名「厚甲」变体 |
| `regen` | 再生 | 每 `every` 秒试图回复 `heal`×最大生命；进入「再生」读条窗（粒子 + 名牌闪）时长 `interrupt_window`，窗内累计伤害 ≥ `interrupt_hp`×最大生命 → **打断本次回复**；未打断则回血 | 读条窗 ≥1.2s；聊天一行 | 本体 HP **不**额外放大（≈厚甲以下的时间税，靠回血）；不提高 atk | — |
| `charge` | 冲锋 | 朝最近玩家方向亮**条带**，预警后沿条冲刺，条内吃 `dmg`×自身 atk | warn ≥1.2s（复用 director `charge` 形状）；条带宽可见 | 本体 HP 不放大；伤害倍率对齐炽热火圈（1.0×atk），**禁止**额外真实伤害 / 击飞出图 | 与周规则「卫士潮」转化怪「跑得快」不同：这是预警条带，不是移速 buff |
| `frost` | 凝霜 | 精英为圆心的减速光环；圈内 Slow（amplifier 可配）；**出圈立即解除**（清边） | 持续粒子圈 + 进圈一行提示；无 AoE 爆发伤害 | 本体 HP 不放大；**零**额外伤害（只减速）；不叠虚弱 / 破防 | — |
| ~~`reflect`~~ | ~~反伤~~ | — | — | — | **否决**：刷图局反伤惩罚手感极差，且与统一伤害公式对账麻烦 |

建议实现默认值（可 yml 调；落地前用 sim 扫）：

```yaml
regen:  {every: 4.0, heal: 0.10, interrupt_window: 1.5, interrupt_hp: 0.05}
charge: {every: 5.5, warn: 1.2, length: 7, width: 2.5, dmg: 1.0}
frost:  {radius: 3.0, amplifier: 1, tick: 0.5}   # Slow II；半径按房型可再收
```

聊天一行示例（语气对齐 D166/D170）：

- 再生：`§6词缀精英「再生」§7出现：发光读条时猛打可打断回血 · 击败 → 结算时 §f余烬碎片 +N`
- 冲锋：`§6词缀精英「冲锋」§7出现：看见脚下亮带就躲开 · 击败 → …`
- 凝霜：`§6词缀精英「凝霜」§7出现：别站在它身边的霜圈里（出圈即解除）· 击败 → …`

## 4. 房间事件表

闸门与 D138 相同；`event_rate` 仍约 0.5。成功 → 结算 `event_core`（现 1）；失败不罚、门照常开（可选事件）。

| id | 玩家名 | 机制 | 成功条件 | 失败 | 新钩子 |
|---|---|---|---|---|---|
| `timed` | 限时清房 | 现网：进房开表，`event_secs` 内清完 | 清房用时 ≤ secs | 超时无核心 | **无**（已有） |
| `crystal` | 砸余烬晶 | 进房时在刷怪点附近临时放下 `count` 块发光晶（建议 SEA_LANTERN 或带 GLOWING 的盔甲架标记块）；须在清房前砸碎 | 碎晶数 ≥ count **且**房间已可清（怪死完）；有软时限 `secs`（超时仍可清房但事件失败） | 超时未砸够 / 退房未砸够 | **需要**：房开始放置 + 记录坐标；`BlockBreakEvent`（仅本 run 世界 / 坐标集）；房清 / 失败 / 卸载时 **一律还原为 AIR**（防残留刷物） |
| `escort` | 护宝兔 | 进房额外刷 1 只 `treasure` 角色（或专用 Mythic `EmberVarietyRabbit`，低威胁、发光、拴在房 trigger 内）；玩家边清边护 | 清房时兔子仍存活 | 兔子死亡 → 事件失败（聊天一行）；门仍开 | **需要**：房开始 `spawn` treasure/elite 旁路一只；死亡监听里若是事件兔 → `onEventResult(false)`；**不**走 §9.3 宝藏怪额外币（避免和主线 Extra 叠奖） |

事件开房提示（一行）：

- crystal：`§b砸余烬晶 §7· 砸掉本房 3 块发光晶再清完 → 结算时 §f余烬核心碎片 +N §7（可选）`
- escort：`§b护宝兔 §7· 清房前别让发光小兔倒下 → 结算时 §f余烬核心碎片 +N §7（可选）`

## 5. 配置草图（键 only，`variety:` 下）

```yaml
variety:
  affix_rate: 1.0
  affixes: [blazing, split, shield, regen, charge, frost]
  affix_shard: 2
  blazing: {every: 3.0, radius: 1.5, warn: 1.0, dmg: 1.0}
  split:   {count: 2, hp: 0.5}
  shield:  {hp: 1.6}
  regen:   {every: 4.0, heal: 0.10, interrupt_window: 1.5, interrupt_hp: 0.05}
  charge:  {every: 5.5, warn: 1.2, length: 7, width: 2.5, dmg: 1.0}
  frost:   {radius: 3.0, amplifier: 1, tick: 0.5}
  event_rate: 0.5
  events: [timed, crystal, escort]          # 新：等权抽一种
  event_secs: 30                             # timed 默认；兼作未单独配置时的回退
  event_core: 1
  timed:   {secs: 30}                        # 可省略，回退 event_secs
  crystal: {count: 3, secs: 35}
  escort:  {hp: 0.35}                        # 相对该图 melee HP；无独立倒计时
```

`ember-v1.yml` `bounty.variety:` **键不变**：

```yaml
bounty:
  variety:
    - {kind: affix, count: 2, coin: 20}
    - {kind: timed, count: 1, coin: 20}   # 实现时 label →「房间事件达标」
```

测试钩子扩（实现窗）：`/corerpg p1 runs variety regen|charge|frost|crystal[:rN]|escort[:rN]|…`

## 6. Java 触点清单（**本窗不写代码**）

| 文件 | 改什么 |
|---|---|
| `EmberRunMaps.Variety` | `KNOWN` 加 3 id；解析 regen/charge/frost 与 `events`/`crystal`/`escort`；`label()`；`roll()` 返回 `{affixRoom, affixId, eventRoom, eventKind}`（第 4 槽，旧调用兼容填 `timed`） |
| `EmberRunSession` | 字段 `eventKind`（默认 `timed`） |
| `EmberRunDirector` | `promote` 分支 regen HP/名牌；`affixTick`：regen 读条+打断计数、charge 复用 `charge` skill、frost 光环上 Slow；房开始：crystal 放块 / escort 刷兔；房清：按 kind 判定；卸载清理晶块；兔死亡短路事件失败 |
| `EmberRunService` | `onEventResult` 文案按 kind；强制 variety 钩子；结算来源前缀「词缀精英 / 限时清房 / 砸余烬晶 / 护宝兔」 |
| `EmberRunRules.VarietyBounty` | `label()`：`timed` →「房间事件达标」；注释写明含 crystal/escort |
| `EmberGrowth` / CN 图录 | 词缀图鉴条目 +3；**不加**新 `dmg_affix_*` 键 |
| 单测 | 见 §9 |
| Mythic（若 escort 不用现成 treasure） | 可选极简 `EmberVarietyRabbit`；优先复用已有 treasure 角色皮肤以免新包 |

**明确不做（本包）**：套装键、永久乘区、新材料、宝石、化妆品奖励、挑战/深渊污染、改 `balance_version`（本设计提交）、改 p1sim（另窗、等 p1sim 锁释放）。

## 7. 漏洞审查

| 场景 | 风险 | 设计约束 |
|---|---|---|
| 事件中途重连 | 重抽事件 / 双开表 | roll 仍绑进本 `seed`；`eventKind`/`eventRoom`/`affix*` 写在 session，重连恢复同局，不重 roll |
| 退房再进 / 换号进同一实例 | 多领 | 结算只走 `onRunSettle` 一次；`affixDone`/`eventDone` 每局一次性；材料走现有 grant 键 |
| 多索赔结算 | 同键重复加 | `varietyGrants` 已按布尔一次；实现勿在 `onAffixDone` 当场发物（保持「待结算」） |
| 挑战 / 深渊 / 团本 / 连战 / 首通 | 花样串模式 | **不放宽闸门**；单测继续断言这些模式 `variety` 全空 |
| 晶体残留 / 世界污染 | 晶块留在实例外被捡 | 仅写在 run 世界；清房 / 失败 / 卸载 / 实例回收路径都必须 `setType(AIR)`；禁止掉落物 |
| 护宝兔变宝藏怪刷币 | 叠 §9.3 Extra 币 | escort 兔 **不**走 `Extra.TREASURE` 结算；独立 track 标记 `varietyEscort` |
| 再生挂机刷碎片 | AFK 等回满再杀 | 每局仍只有 1 只词缀、结算一次；回血只拖时间不加成奖励。可接受 |
| 晶体 AFK 机器人 | 自动挖块 | 与现网限时清房相同：委托日 cap + 体力；不另加反作弊。挖块须玩家在线且在实例内 |
| 冲锋撞出界 / 卡墙 | 软锁 | 复用 B2.165 charge 长度裁剪；冲锋精英 **不**位移出 trigger 盒（只播条带伤害，或位移夹在 leash 内——实现二选一，推荐「只播条带、主体不真冲」以降风险） |
| 凝霜永久 Slow | 出房仍慢 | 出半径或房清 / 精英死立即 `removePotionEffect(SLOW)`；仅本 run 施加 |
| 与周规则转化怪叠乘 | 过难 | 词缀加在「房内最硬一只」上；转化怪的 convHp/atk 仍先算。门禁用 42 格通关率兜住 |
| 破甲天赋对新缀无效 | 体感「第二排骗钱」 | 文档 / 图鉴写明破甲只点名厚甲 / 炽热 / 分裂；新缀吃通用猎缀。专属侧向键另窗 |

## 8. 平衡门禁（实现窗，非本窗）

本窗 **不**跑 p1sim（`COORD-gear-stage0` 占锁）。实现前 / 合并前须：

1. `p1sim` 42 个首通 / 挑战格：有花样 vs `--no-variety`（或旧三缀基线）通关率 **±3pp**（用户 10-04：「within range」即可）。
2. `p2econ` 两件极品最快路线：**±0.5 周**。
3. 若超标：先降 `regen.heal` / `charge.dmg` / `frost.radius`，或把新缀 `affix_rate` 内权重调低（例如新缀权重 0.7、旧缀 1.0），**禁止**用加奖励补偿难度。
4. 奖励数默认保持 shards=2 / core=1；若经济偏快，只允许减不允许加。

## 9. 验收（实现后）

**单测（建议名）：**

- `varietyPack2RollsNewAffixesAndEventsOffFirstClears_D171` — 种子稳定；KNOWN 含 6；events 含 3；首通 / 挑战 / 深渊 / 团本 roll 空。
- `varietyBountyCountsAnyEventKind_D171` — crystal/escort 成功 → 与 timed 一样推进 kind=`timed` 委托。
- `crystalBlocksCleanupOnFailAndUnload_D171` — 失败 / 卸载无残留。
- `escortRabbitDoesNotPayTreasureCoin_D171` — 无 `extra_treasure_coin`。
- `reflectAffixRejected_D171` — 配置写 `reflect` 被忽略（不进 KNOWN）。

**冒烟（政策：几分钟，禁长测）：** FreshQ109+；`runs variety regen` / `charge` / `frost` / `crystal:r1` / `escort:r2` 各一局 Q01 普通重打，确认聊天一行、结算碎片/核心、花样委托计数、无残留晶块。不做通关率扫图。

**资产：** 若实现窗动结算 / 事件奖励路径，顺带跑现有 loss/dup 脚本；本设计本身不改资产路径。

## 10. 分期落地

| 阶段 | 内容 | 依赖 |
|---|---|---|
| **本窗 D171** | 本文 + 源表 §13.86 + P2 指针 | 无代码 |
| Stage A（yml+Java） | 6 词缀 + 事件池；限时仍可用；crystal/escort | `asset-fix-1655` DONE；空闲 CoreRpg 窗；JDK8 构建 |
| Stage B | 图鉴 / 冒险页说明三行；委托文案「房间事件达标」 | Stage A |
| Stage C | p1sim 词缀表扩 + 门禁报告；必要时调参 | `COORD-gear-stage0` 释放后 |

## 11. 明确不做

- 不 bump `balance_version`（本提交）。
- 不加永久威力槽 / 乘区 / 宝石 / 新货币。
- 不做反伤；不做宠物 / 光环 / 粒子奖励。
- 不改装备结构 Java（仍 HOLD）；不碰 `tools/p1sim/**` 本窗。
- 不关闭 / 抢占 `COORD-asset-fix-1655` / `COORD-gear-stage0`。

## 12. 决策编号

- **D171** = Variety Pack 2 设计定稿（本文）。
- D168 = gear8 已取消，不占用。
- 资产修复窗若曾预标「Decision D171」，实现提交应改用当时源表下一个空号（预计 D172+），避免撞号。
