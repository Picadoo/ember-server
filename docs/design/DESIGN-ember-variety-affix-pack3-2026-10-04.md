# Ember Variety Affix Pack 3：词缀精英再扩包（D181，设计-only，2026-10-04）

> 状态：**设计定稿，未实现**。本窗不改 CoreRpg 源码 / yml / jar / 菜单，不 bump `balance_version`，不跑 p1sim（`COORD-afk-p1` 占用 `tools/p1sim`；`COORD-mainline-unlocks` / `COORD-signin-online` 占用 CoreRpg）。
> 前置：D138（词缀精英闸门）、D171/D176 Pack 2（池 3→6：再生/冲锋/凝霜）、D144（花样委托泛型「词缀精英击杀」计数）、D164（破甲只认 shield/blazing/split —— 本包新缀**仍不**加专属键）。
> 房间事件扩包另见 D179（`DESIGN-ember-room-events-pack3-2026-10-04.md`，hold/beacon/relay）；本包**只动词缀池**，不改 `events:`。
> 实现对齐：等 afk / mainline / signin 释放 CoreRpg + p1sim 后，空闲窗落地；落地前用离线 sim 过 §8 门禁。
> 参考（2026-10-04 检索）：Diablo 3 精英词缀（Mortar 点名圈、Molten 亡爆圈、Waller/Vortex/Teleporter/Reflect 作否决对照）；Path of Exile 稀有怪单缀/三缀 + 光环类（本包不做光环叠乘）；WoW 大秘境近年「少叠压力、预警可读」方向。国服 1.12 RPG 常见「精英点名砸地 / 死后爆圈」可读原型。

## 0. 一段话裁决

在**已首通的普通版 Q01–Q07**（与 D138/D176 同一闸门）上，把词缀池从 **6 扩到 8**：追加 **`mortar` 投弹**（周期在玩家附近落预警圈）与 **`molten` 亡爆**（精英死亡后尸体处延迟预警圈）。全部侧向花样：改走位，不抬永久乘区、不加新货币、不改 `affix_shard` 数量。花样委托继续泛型计数。否决反伤 / 拉人 / 造墙 / 瞬移 / 吸血。本窗只出文档。

## 1. 白话版（给服主 / 玩家）

- 重打已通的普通图时，发光词缀精英除了炽热 / 分裂 / 厚甲 / 再生 / 冲锋 / 凝霜，还可能是：
  - **投弹**：隔几秒在你脚下附近亮一个圈，≥1.2 秒后砸一下——提前走开。
  - **亡爆**：精英一死，尸体位置会再亮一个圈再炸——杀掉后立刻退开两步，别站在尸体上捡东西发呆。
- 奖励照旧：打掉 → 余烬碎片（现网 `affix_shard: 2`）。房间事件池本包不动（仍限时/砸晶/护宝兔；D179 的占点/护灯/传火另窗）。
- 首通、挑战、深渊、团本、连战、活动本：**没有**（闸门不变）。

## 2. 为什么是 Pack 3（与 Pack 2 / 参考游戏的差别）

| | Pack 2（D171/D176） | Pack 3（D181） |
|---|---|---|
| 主题 | 打断回血 / 条带冲锋 / 减速圈 | **点名落圈** + **死后余压** |
| 池 | 3→6 | 6→8（+2，不再一次 +3，避免刷图花样稀释过快） |
| 事件池 | +crystal/escort | **不动**（事件扩包 = D179） |
| 新形状 | 复用 charge / Slow / 读条 | 复用 **circle + target:player / onDeath**（导演已有 `onDeath`、circle 预警） |
| 参考 | PoE Regen / Temporal；D3 冲锋感 | D3 **Mortar** / **Molten**；明确对照否决 Waller/Vortex/Teleporter/Reflect |

研究摘要（机制取舍，不是抄数值）：

| 来源 | 机制 | 本包采用？ | 原因 |
|---|---|---|---|
| D3 Mortar | 周期向玩家落点砸圈 | **是 → mortar** | 预警圈 + 走开，和主线招式形状库一致 |
| D3 Molten | 死后留熔岩 / 爆圈 | **是 → molten**（延迟预警圈，非整片 DoT 地毯） | 死后余压；可读；不留持久方块 |
| D3 Waller | 造墙挡路 | **否** | MC 方块墙易卡图 / 软锁门线 |
| D3 Vortex | 拉人贴脸 | **否** | Q04 落差图 + kb 纪律（§10.I / kb≤1）风险 |
| D3 Teleporter | 瞬移 | **否** | 易出 leash / 穿墙；刷图恶心 |
| D3 Reflect | 反伤 | **否** | Pack 2 已否决；公式对账差 |
| D3 Vampiric | 吸血 | **否** | 与 regen 重叠且难打断，变相抬 TTK |
| PoE 光环叠乘 | 稀有光环改全队强度 | **否** | 禁止永久/局内乘区膨胀；本包零 atk/HP 乘区（mortar 伤害用现网 atk 倍率） |
| WoW M+ | 少叠、可读 | **采纳原则** | 每局仍只 1 只词缀精英（D138）；不叠第二只 |

## 3. 现网基线（只读）

| 项 | 现网（1.65.10 / D176） |
|---|---|
| 闸门 | NORMAL + 全员已首通该图 |
| `affixes` | `[blazing, split, shield, regen, charge, frost]` |
| `affix_shard` | 2 |
| `events` | `[timed, crystal, escort]`（本包不改） |
| 破甲 | 只认 shield/blazing/split；新缀吃通用 `dmg_affix` |
| 代码 | `EmberRunMaps.Variety.KNOWN`；`EmberRunDirector.promote` / `affixTick` / `onDeath`；`EmberRunService.onAffixDone` |

## 4. 新词缀表

奖励列与旧缀相同：击败 → 结算 `affix_shard`（现 2）；不发新币种。本体 **不**额外放大 HP/atk（亡爆是死后圈，投弹用 `dmg×自身 atk`）。

| id | 玩家名 | 机制（玩家要做什么） | 预警 / 可读 | HP / 伤害预算 | 否决备注 |
|---|---|---|---|---|---|
| `mortar` | 投弹 | 每 `every` 秒以**最近在场玩家**脚附近（可 `ahead` 微调）放 1 个 `circle`；预警后结算 `dmg`×自身 atk | warn ≥1.2s；脚下火焰轮廓 + 名牌「投弹」；聊天一行 | 本体 HP 不放大；伤害倍率对齐炽热（1.0×atk）；**禁止**额外真实伤害 / 击飞出图 / kb>1 | 与首领「落石」不同：精英版半径更小、every 更长，避免房内战报叠爆 |
| `molten` | 亡爆 | 精英 **死亡瞬间**在尸体位置挂 1 个延迟 `circle`（`delay` 后 warn，再结算）；**活着时无周期技** | 死后立刻聊天一行 + 延迟轮廓；warn ≥1.2s | 本体 HP 不放大；亡爆 dmg 建议 1.2×atk（略高于炽热单圈，因一生只一次）；分身（split）**不**再触发亡爆 | 不是整片 DoT 地毯；不落方块；卸载/退房取消未结算圈 |

建议实现默认值（可 yml 调；落地前用 sim 扫）：

```yaml
mortar: {every: 5.0, warn: 1.2, radius: 2.0, ahead: 0.0, dmg: 1.0}
molten: {delay: 0.4, warn: 1.3, radius: 2.5, dmg: 1.2}   # delay=死后到亮圈；warn=亮圈到结算
```

聊天一行示例（语气对齐 D166/D170/D171）：

- 投弹：`§6词缀精英「投弹」§7出现：脚下附近会亮圈，走开再打 · 击败 → 结算时 §f余烬碎片 +N`
- 亡爆：`§6词缀精英「亡爆」§7出现：杀掉后尸体要炸，立刻退开 · 击败 → …`
- 亡爆触发：`§c亡爆 §7· 尸体要炸，退后！`

## 5. 明确否决

| 想法 | 原因 |
|---|---|
| `reflect` / 反伤 | Pack 2 已否决；手感与公式对账差 |
| `vortex` / 拉人 | Q04 落差 + 统一 kb 纪律 |
| `waller` / 临时方块墙 | 卡门、卡刷点、残留方块债 |
| `teleport` / 瞬移 | leash / 穿墙；刷图恶心 |
| `vampiric` / 吸血 | 与 regen 重叠；难打断 → 变相抬 TTK |
| 一局两只词缀精英 | D138 一局一只；叠压力违反「少叠可读」 |
| 新缀加专属破甲键 | 成长窗另开；本包只吃通用 `dmg_affix` |
| 亡爆落持久熔岩方块 / 粒子奖励 | 方块残留债；化妆品后置 |
| 改 `affix_shard` / 结算表 | 奖励通胀；须另开经济窗 + p2econ |
| 挑战 / 深渊 / 首通带花样 | 闸门不变 |

## 6. 漏洞 / 实现注意

| 风险 | 处理 |
|---|---|
| mortar 与 blazing 同房预警叠 | every 偏长（≥5s）；sim 查「同时预警」；必要时再拉长 |
| mortar 点名飞天 / 创造 | 落点用玩家脚下方块 Y（onGround 优先）；无效则精英脚下 |
| Q04 落差：圈边缘击退摔死 | kb=0；只伤害不位移（与 D171 charge 条带纪律一致） |
| molten + split：分身也亡爆 | **仅原始词缀精英**触发；`splitAdds` 标记不分发 molten |
| molten 与击杀同帧捡物 | delay≥0.4 + warn≥1.2 → 总窗口 ≥1.6s，够走开 |
| 退房 / 崩溃残留圈 | 圈绑 run session；卸载 `clear` 与 crystal 还原同路径取消 |
| 强制钩子冒烟 | `/corerpg p1 runs variety mortar` / `molten`（实现窗扩） |
| 委托双计 | 仍一次 `onAffixDone`；亡爆是表现不是第二次击杀 |

## 7. 配置草图（键 only）

```yaml
variety:
  affixes: [blazing, split, shield, regen, charge, frost, mortar, molten]
  mortar: {every: 5.0, warn: 1.2, radius: 2.0, ahead: 0.0, dmg: 1.0}
  molten: {delay: 0.4, warn: 1.3, radius: 2.5, dmg: 1.2}
  # events / rates / shard 键一律不动
```

## 8. 平衡门禁（实现窗，非本窗）

本窗 **不**跑 p1sim。实现前 / 合并前须：

1. 静态 42 格（7 图 × 普通重打参考装 × 躲避 0.3/0.5/0.7，种子配对）：每格通关率 Δ **目标 ≤ ±3pp**，硬顶 5pp；若超 → 只许拉长 mortar.every / 降 radius·dmg / 降 molten.dmg，**不许**加薪或加 HP。
2. **不**强制跑 `p2econ`（`affix_shard` 数量未变）；若实现窗误改奖励键，再补 p2econ ±0.5 周。
3. 体感抽检：Q04 / Q07 重打各 1 局强制 mortar、1 局强制 molten（含 split 局确认分身无亡爆）。

## 9. 验收（实现后）

| 项 | 标准 |
|---|---|
| 池 | `KNOWN` 含 8；配置写 `reflect`/`vortex` 仍忽略 |
| 闸门 | 首通 / 挑战 / 深渊 / 团本 roll 空 |
| mortar | 强制钩子：预警可见、躲开不伤、踩中掉血、kb=0 |
| molten | 击杀后延迟圈；split 分身死亡无圈；退房无残留 |
| 委托 | 新缀击杀计入「词缀精英」日委托 |
| 文案 | 冒险页 / 图录词缀一行扩到 8 名；破甲说明仍写「厚甲/炽热/分裂」 |
| 资产 | 不改资产路径；无需 loss/dup（若误动结算钩子则补跑） |

## 10. 分期

| 阶段 | 内容 | 前置 |
|---|---|---|
| Stage A（yml+Java） | KNOWN +2；mortar tick；molten onDeath；单测 + 强制冒烟 | CoreRpg 空闲；JDK8 构建；afk/mainline/signin 锁释放 |
| Stage B | 冒险页 / 图录一行 | Stage A |
| Stage C | p1sim 42 格门禁报告；必要时调参 | p1sim 锁释放 |

## 11. 本窗明确不做

| 不做 | 原因 |
|---|---|
| CoreRpg / runs yml / 部署 / 机器人 | 车道隔离 |
| 改房间事件池 | D179 专窗 |
| 新 Mythic 技能类型 | 复用 circle / onDeath |
| 化妆品 / 光环 / 宠物 | 用户后置 |
| 本窗改 `tools/p1sim` | afk 占用中 |
| 实现 Boss Moves Pack 2（D173） | 需 CoreRpg+p1sim；不抢 |

## 12. 与邻单关系

- D176 = Pack 2 已上线（6 缀 + 3 事件）。
- D179 = Room Events Pack 3 设计已出（hold/beacon/relay），实现仍 deferred。
- D173 = Boss Moves Pack 2 设计已出，实现仍 deferred。
- D181 = 本单（词缀 6→8）。
- D180 = 签到 / 在线时长（`COORD-signin-online`，并行，不碰撞本设计）。

