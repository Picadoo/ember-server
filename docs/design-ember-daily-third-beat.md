# 设计稿 · 日常第三拍试点（异于重叠刷怪 / 门槛怪）

> **未批准前不施工。** 玩法 YAML / MM 技能 / 地图零改；本稿仅策划。  
> tip：`81c77f7`（周本 boss_prep 批 B；Next: daily third-beat pilot design）  
> 债源：`docs/STATUS-ember-daily-rhythm-leverage-closeout-review.md` 软债#4「宏观骨架仍像亲戚」；backlog「日常第三拍试点」  
> **硬禁本轮再堆：** 房2 start 链式变体、Boss 前压扩霜晶/锈轨、周本/精英再对齐。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 日常 · 1～2 线「第三拍」试点（非重叠 / 非门槛） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 Asia/Shanghai |
| 关联 | 优先读七线 `monster.yml` + live DP action + Ember* Boss MM；对照挑刺收口 |
| 状态 | **待总控批 A 或 B · 未批准前零改** |
| 推荐 | **方案 A · 霜晶 Boss「霜暴读条」可躲**（仅 MythicMobs） |

### 硬约束

| 可动（若批 A） | 不可动（硬禁） |
|----------------|----------------|
| `EmberDailyFrostBrute` 技能表 + 新建 **1** 个 MM Skill 包（读条序列） | 七线 `monster.yml` 房1/房2/`boss_prep`/门坐标；链式 delay |
| Boss 战内提示文案（MM `message`） | **体力 / 通关箱 / 掉落池总量口径**；Boss HP / Display 去色名 |
| | **`$kill-any`**；跨组合并 `$kill`；大改 MCA（除非必要且坐标写清——本 A **不碰图**） |
| | 霜晶/锈轨加 `boss_prep`；他线再叠房2；周本/精英节奏对齐 |
| | CoreRpg `quest.yml` 主线挂钩当「支线」 |

---

## 1. 债源

| 来源 | 结论 |
|------|------|
| 节奏全杠杆收口挑刺 | 房2 **七线全重叠** + 五线 `boss_prep` 已齐；**无挡级**；软债#4：**宏观仍是铁栅骨架像亲戚**——要「爱玩」级需 **第三拍/叙事**，不挡里程碑 |
| tip `81c77f7` | 周本深室前压批 **B**；明确 Next = **日常第三拍试点设计**（非再堆 prep/链式） |
| 霜晶/锈轨无 prep | 设计刻意（前压扩线硬条）；终厅直 Boss——第三拍宜落 **Boss 战内** 或异于「门槛怪」的机制，**禁止**为对称硬加 prep |

**一句话债：** 杠杆够日刷不困，但拍子族仍是「清怪开门」亲戚；要试点 **一种不同族** 的可感机制，且必须现栈能落地。

---

## 2. 禁堆清单（本轮）

| # | 禁 | 理由 |
|---|----|------|
| 1 | 房2 `wave2a.start→delay wave2b` 再变体 / 再扩 delay | 七线已全有；再动=复印机加深 |
| 2 | 霜晶 / 锈轨加 `boss_prep` | 前压扩线硬条「不加」；本轮禁止用门槛怪冒充第三拍 |
| 3 | 周本 / 精英再对齐日常拍子 | 周本刚批 B；精英厅二刚批 B；纪律收口 |
| 4 | 断塔环廊再折腾 | 防坠刚做；测法债另挂，非本条 |
| 5 | `$kill-any` / 跨组合并 `$kill` | 全仓纪律 |
| 6 | 为第三拍抬 Boss HP / 改掉落期望 / 改体力 30 | 硬条：不改体力与掉落池总量口径 |

---

## 3. 现栈能力结论（能 / 不能）

调研时点：2026-09-28；来源 live `plugins/DungeonPlus/dungeon/EmberDaily*/`、`task/timeout.yml`、`actionscript`/`extension/kethers` 样例、`plugins/MythicMobs/Mobs/EmberDaily*.yml` + `Skills/EmberDungeonDiffSkills.yml` + `ExampleSkills.yml`、`plugins/CoreRpg/quest.yml`。

### 3.1 DungeonPlus（日常 live）

| 能力 | 结论 | 证据 |
|------|------|------|
| `$message` / `$teleport` / `$monstergroup{delay=}` / `$operation-block` / `$command` / `$kill` / `$end` | **能**（现网七线全用） | 各 `monster.yml` / `option.yml` |
| 整本超时 → `end-type=FAILURE` | **能**（过重） | `EmberDaily/task/timeout.yml` 720s；**霜晶/锈轨 `task/` 空目录**（无独立 timeout 文件） |
| Kether `title` / `actionbar` | **样例能**；日常 live **未用** | `extension/kethers/example.yml`；非验收必需 |
| **中段限时窗**（只丢旁支奖励、不卡门、不整本失败） | **不能**（现栈无此 API） | 无 mid-objective / 旁支 fail 态；`$end` FAILURE=整本；timeout 仅整本计时 |
| **可失败支线 objective**（失败不卡门） | **不能**靠 DP  alone | 组条件只有 `$kill` 开门链；旁支出怪若不进 `$kill` 则无「失败」语义，只是可忽略怪 |
| 环境方块危害（岩浆/火） | **`$operation-block` 能摆方块**；日常本 `break/place=false` + 需坐标与回滚 | 改动面大，易踩冒险模式/回图债 |

### 3.2 MythicMobs（日常 Boss）

| 能力 | 结论 | 证据 |
|------|------|------|
| 半径伤害 / 减速光环 `~onTimer` | **能**（无读条） | 霜核：`potion SLOW r6` + `damage 0.4 r5` `~onTimer:45`；潮闸/锈轨同族 |
| **读条可躲**：`message` → 粒子 → `delay` → 半径伤害 | **能** | `ExampleSkills.yml` `SmashAttack`：message + teleport + **delay 10** + `damage @PlayersInRadius`；MM 包内 `delay` 已在栈 |
| SoftHit「可走位」 | **弱** | `EmberDailySoftHit`=目标减速+粒子，**非**地面读条区 |
| 团本/灾厄点名砸地 | **有模板但偏重** | `EmberRaidSmash` / `EmberCalamityShred`；日常应薄抄 delay+半径，勿搬团本数值 |

### 3.3 CoreRpg 任务

| 能力 | 结论 | 证据 |
|------|------|------|
| 主线章节（进日常通关一步） | **能** | `quest.yml` ch 提示「日常本」 |
| **日常本内可失败支线**（失败丢奖励不卡门） | **不能** | 无地牢内旁支 quest hook；不可空许愿绑 QuestService |

### 3.4 机制落地性总表（对照派工清单）

| 候选机制族 | 现栈？ | 本稿裁决 |
|------------|--------|----------|
| Boss 读条可躲（MM message+delay+半径伤） | **能** | **试点主推** |
| 限时冲刺窗（失败只丢奖励不卡门） | **不能**像样 | **否**（见 §7） |
| 可失败支线（CoreRpg 任务） | **不能** | **否** |
| 暖点站位环境危害 | **不能**（`dungeon-area: []`；无 region） | **否**；近身冻圈可读条化替代 |
| 假退路真代价（方块/TP 陷阱） | 理论 `$operation-block`/`$teleport` 能 | **本轮否**（要坐标+回滚+易大改 MCA） |
| 环境危害靠 MM 半径 | **能**（已有无读条光环） | 并入读条试点，不另开房段 |

---

## 4. 候选线（结构差异）

| 线 | 房2 | Boss 前 | 异拍空间 | 备注 |
|----|-----|---------|----------|------|
| **霜晶** `EmberDailyFrost` | start 链 delay2 | **无 prep（刻意）** | **终厅直 Boss** → 战内机制最干净 | 冻主题贴合读条霜暴；**推荐试点** |
| **锈轨** `EmberDailyRail` | 真侧袭 delay1 | **无 prep（刻意）** | 同上 | 工业砸地可读条，作 **PASS 后复制窗**（本轮不双开） |
| 潮蚀 / 庭院 | 链+prep | 有门槛 | 可 Boss 战内 | 已有 prep，第三拍仍可战内；本轮优先无 prep 线避免「又像门槛」 |
| 断塔 | 链+prep+防坠 | 有 | — | **勿再折腾环廊** |
| 焦骨 / 地窖 | 链+prep | 有 | — | 非本轮优先 |

**择线结论：** 本轮 **只试点霜晶 1 条**；锈轨同构 MM 读条列为扩线候选，不写进 A 施工必做。

---

## 5. 方案 A · 试点施工稿（霜晶 · Boss 霜暴读条）

### 5.1 一句话

霜晶终厅 **霜核蛮兵** 将「无预警环伤」改为 **可读条、可走出半径的霜暴**：提示 → 蓄力粒子 → 短 delay → 半径伤+减速；站圈吃伤害，拉开免伤。  
**不动** 房1/房2/门/`boss_prep`（仍无）/体力/掉落/DP `monster.yml`。

### 5.2 线名 + 机制

| 项 | 口径 |
|----|------|
| 线 | **余烬窟·霜晶裂隙**（`EmberDailyFrost`） |
| 机制族 | **Boss 读条可躲**（第三拍；≠重叠刷怪 ≠门槛怪） |
| 插件能力 | **MythicMobs** Skill 包 + Boss `Skills` 挂载；**不依赖** DP 新 action / CoreRpg quest |
| 玩家体感 | 霜厅开打后周期性出现「霜核蓄力」提示；约 **1.0～1.5s**（20～30 tick）后对 Boss 周围 **r≈5** 结算；学会侧步/拉开 |

### 5.3 施工边界（批准后专岗）

| 文件 | 改动 |
|------|------|
| `plugins/MythicMobs/Skills/` 新建或并入 `EmberDungeonDiffSkills.yml` | 新 Skill 例名 **`EmberFrostNovaCast`**（名称可微调，须唯一） |
| `plugins/MythicMobs/Mobs/EmberDailyFrost.yml` · `EmberDailyFrostBrute` | **替换** 现网两条无预警 `~onTimer:45` 光环，改为挂读条 Skill；保留 SoftHit / 死亡掉落命令 **零改** |

**建议 Skill 序列（施工稿 · 数值可测岗微拧，勿抬 Boss HP）：**

```yaml
# 草案（未批准不落盘）
EmberFrostNovaCast:
  Cooldown: 8
  Skills:
  - message{m="&b【霜厅】&f霜核蓄力——&7拉开！"} @PlayersInRadius{r=24}
  - effect:particles{p=snowballpoof;amount=40;hSpread=4;ySpread=1;speed=0} @Self
  - effect:sound{s=minecart.base;volume=0.6;pitch=0.7} @Self
  - delay 25
  - damage{amount=1.2} @PlayersInRadius{r=5}
  - potion{type=SLOW;duration=40;level=0} @PlayersInRadius{r=5}
  - effect:particles{p=crit;amount=30;hSpread=5;ySpread=0.8;speed=0} @Self
```

Boss 挂载草案：

```yaml
# EmberDailyFrostBrute Skills 中：删 onTimer:45 的 potion/damage 两行
# 增：
- skill{s=EmberFrostNovaCast} @self ~onTimer:80
```

| 参数 | 建议初值 | 说明 |
|------|----------|------|
| 读条 `delay` | **25** tick（≈1.25s） | 短于团本；日常可反应 |
| 结算半径 | **5** | 对齐现网无预警伤 r5；拉开即免 |
| 单次伤害 | **≈1.2**（可 1.0～1.5） | 高于旧 0.4/2.25s 瞬时，但可躲；**禁止**为「逼喝药」抬到团本级 |
| `~onTimer` | **80**（≈4s） | 比旧 45 疏；给走位窗 |
| Cooldown | **8** | 防与 onTimer 叠炸 |

**难度口径：** 旧光环=站桩持续掉血；新机制= **会躲则更轻松、贪刀则更疼**。期望通关时长与掉落 **不变**；不改 `option.yml` reward 列表。

### 5.4 明确不施工

- DP `EmberDailyFrost/monster.yml` / `option.yml` / 门 AIR  
- 加 `boss_prep`、改 wave 数量/刷点  
- 锈轨本轮双开（扩线另批）  
- MCA / 暖点方块 / 假岔陷阱  

### 5.5 方案 B（本轮仍不动）

若总控认为日常 Boss 读条会抬投诉或测力不足：**维持霜核无预警环伤**；第三拍顺延至下一窗口（见 §7 能力债）。  
**理由可写进批注：** 铁栅骨架软债非挡级；现杠杆已够日刷。

---

## 6. A / B 对比与推荐

| | **A. 霜晶霜暴读条** | **B. 本轮不动** |
|--|---------------------|-----------------|
| 还债 | 直接回应「第三拍 / 宏观亲戚」 | 不还软债#4 |
| 改动面 | **仅 MM** 1 Skill + Boss 挂载 | 零 |
| 可测性 | 高：提示可见、圈内受伤、圈外免伤 | — |
| 风险 | 读条过短难躲 / 过长变站桩；粒子 1.12 名须测岗核对 | 挑刺续刺铁栅 |
| 禁堆对齐 | 不碰房2/prep/他线 | 同 |

**推荐：A。**  
一句：用现栈已验证的 MM `delay` 读条包，在 **无 prep 的霜晶终厅** 落地可躲霜暴，零动 DP 铁栅与掉落体力口径。

---

## 7. 明确否掉的空许愿（勿施工）

| 机制 | 否因 |
|------|------|
| **限时冲刺窗（失败只丢奖励、不卡门）** | DP 无旁支 objective；`task/timeout` 只会 **整本 FAILURE**；霜晶/锈轨甚至无独立 timeout 文件。做「像样」限时窗需新能力（旁支计时器或 CoreRpg 本内 flag） |
| **可失败支线绑 CoreRpg 任务** | `quest.yml` 仅主线章；日常实例内无挂钩 |
| **暖点 / 安全区站位** | `dungeon-area: []`；无 WorldGuard 式区域伤害证据；不能空许愿 |
| **假退路真代价** | 需陷阱坐标 + `$operation-block`/`$teleport` + 回图；易触发大改 MCA；改动面超试点 |
| **锈轨工业限时阀整本失败** | 过重且与日票/体力体感冲突；非「只丢奖励」 |
| **为对称给霜晶/锈轨加 prep** | 硬禁堆 |

**下一窗口若要真·限时旁支，需要：** DP 或 CoreRpg 提供「本内旁支计时 + 成功/失败旗（失败不 `$end`、可选扣/不加额外 NI）」；或 MM 可销毁旁支怪 + **从既有掉落池挪** 的成功奖励（须总控明示改池口径）。

---

## 8. 验收 ≤5（批准 A 后测岗）

1. **提示先于伤害：** 霜暴结算前聊天/可见提示出现；`delay` 内站在 Boss 旁 **尚未** 吃到该次读条伤（允许普攻/SoftHit）。  
2. **圈内吃伤：** 读条结束时位于 r≈5 内 → 受该次 `damage`（可用日志/血量观察）。  
3. **圈外免伤：** 提示后立刻拉开出半径 → **该次** 读条伤不中（普攻除外）。  
4. **铁栅零回归：** 冻台左右链、房2 重叠、门1/门2 AIR、无 `boss_prep`、通关箱与体力扣 30 **与基线一致**（DP YAML 本轮应 **零 diff**）。  
5. **禁项：** 无 `$kill-any`；无 MCA 大改；掉落命令行与通关 `dungeon-reward-script` **零改**。

---

## 9. 禁项（施工复核）

- 未批准落盘任何玩法 YAML / MM  
- 禁 kill-any；禁改体力/掉落池总量口径  
- 禁房2 链式再调；禁霜晶/锈轨 prep；禁断塔环廊再动  
- 禁借机改周本/精英  
- 禁 git push（文档 commit 除外由本岗处理）

---

## 10. 施工岗（批准 A 后）

| 岗 | 职责 |
|----|------|
| **怪物（MythicMobs）** | 落 `EmberFrostNovaCast`；改 `EmberDailyFrostBrute` Skills；`/mm reload`；核对 1.12 粒子名（`snowballpoof` 若无效改 `cloud`/`crit`） |
| **插件（DungeonPlus）** | **本 A 无改**；回归时只读确认 `monster.yml` 零 diff |
| **地图** | **本 A 无改** |
| **测岗** | 霜晶进本打 Boss：验收 1～5；记 tip 与是否需拧 delay/伤害 |
| **策划** | 批注 A/B；PASS 后决定是否开锈轨同构扩线窗 |

---

## 11. 回报摘要（给总控）

| 项 | 内容 |
|----|------|
| 路径 | `docs/design-ember-daily-third-beat.md` |
| 推荐 | **A** · 霜晶 Boss「霜暴读条」可躲 · **MythicMobs**（message + delay + 半径伤） |
| 否掉 | 限时旁支窗、CoreRpg 本内失败支线、暖点站位、假退路陷阱、霜晶/锈轨 prep |
| 状态 | **未批准前不施工** |

