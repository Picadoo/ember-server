# 状态 · 团本房间「剩 1–2 只远程怪打不到」诊断（2026-10-08 · 怪物岗 · 只诊断）

**日期：** 2026-10-08 07:00–07:30（上海时间）
**上游：** 总控派单「团本房间间歇卡住 · 只诊断」· 测试报告 [`STATUS-ember-dp-p1-maps-rerun-test-2026-10-08.md`](STATUS-ember-dp-p1-maps-rerun-test-2026-10-08.md)（`75d86c4f`）
**约束：** 未改任何数值 / 配置 / MM / 地图 / 脚本；未停服、未重启、未 `/mm reload`；未在仓库用 stash / reset / checkout。地图只读：把 dungeon-caches 的 region 复制到 `/tmp/diag` 后离线解析。

## 结论（TL;DR）

**主因（高置信）：是测试脚本的问题，不是地图问题，也不是 MM 怪逃出房间。** 4 次软卡**全部**发生在「队长 bot `P1RrA1008` 在该房开打后 3–8 s 内倒下 → 进入观战（spectator，镜头锁在队友 B 上）」之后。脚本 `p1-maps-rerun-smoke.js` 的移动逻辑是「`tp 队长 → 怪坐标`，再 `tp 其他 bot → 队长`」。队长观战时位置每 tick 被拉回 B 身上，所以 B、C 实际被 tp 到自己身边，**根本没有靠近怪**。bot 只有在怪距离 ≤ 4 格时才出手。近战怪会自己走过来送死；远程怪（vanilla SKELETON 弓 AI）会和目标保持约 7.5–15 格，永远进不了 4 格 → 房间挂住 120 s。

**次因（中置信，属于设计风险，对真人影响低）：** `EmberQ06Ranged` / `EmberQ07Ranged` 没写 `AIGoalSelectors`，用的是 1.12 原版骷髅弓 AI（`PathfinderGoalBowShoot`）：目标在约 7.5 格内就**边后退边横移**。另外 R01/R02 r2、R02 r1 的侧边有 7–11 格宽的开口，正好贴着两侧刷怪点。真人能追上，但只剩一个人时，追「后撤弓手 + 出界被拉回 home」会比较烦。这不会导致不可清。

**已排除：** 怪卡在墙里或高台上、飞走、跑出房间。DungeonPlus 房清判定也排除了，因为 DP 根本不管房清。

## 1. 卡住样本与「队长倒下」的对应关系（证据）

来源：`/tmp/p1rerun/raids*.json|out`、`chat-P1Rr{A,B,C}1008.log`、`window.log`（latest.log 05:56 之后的部分）。

| 时间 | 团本·房 | 变体 | 房间开打 | 队长 A 倒下 | 结果 |
|---|---|---|---|---|---|
| 06:23:57 | R01 r1 | A 近×6·远×1 | — | 活着 | 清 |
| 06:24:27 | R01 r3 | B 近×6·远×1 | — | 活着 | 清 |
| 06:25:33 | R02 r1 | A 近×6·远×1 | — | 活着（倒下的是 B，06:25:41） | 清 |
| 06:26:03 | R02 r3 | B 近×5·远×2 | — | 活着 | 清 |
| **06:27:42** | **R01 r2** | A 近×5·远×2 | 06:27:42 | **06:27:49**（锈轨矿工） | **卡 120 s（alive=2→1）** |
| **06:31:00** | **R02 r2** | A 近×5·远×2 | 06:31:00 | **06:31:08**（霜哨卒） | **卡 120 s（alive=1）** |
| 06:34:03 | R03 r1 | B 近×5·重×1（无远程） | 06:34:03 | 06:34:06 | 清（近战怪自己走过来）→ **下一房 r2 一直触发不了**（见下） |
| 06:39:53 | R01 r1 | A 近×6·远×1 | — | 活着 | 清 |
| **06:40:07** | **R01 r2** | A 近×5·远×2 | 06:40:07 | **06:40:10**（锈轨矿工） | **卡 120 s（alive=2）** |
| 06:44:25 / 06:44:40 | R01 r1 / r2 | A·远×1 / A·远×2 | — | 活着（06:45:19 才在首领厅倒下） | 清 / 清（同一房 r2，14.3 s） |
| **06:46:12** | **R02 r1** | A 近×6·远×1 | 06:46:12 | **06:46:19**（哨楼弩手） | **卡 120 s（alive=1）** |
| 06:49:12 | R03 r2 | A 近×5·远×2 | — | 活着 | 清 |

- **4/4 卡住 ⇔ 队长在该房开打 ≤ 8 s 内倒下；队长活着的 9 个含远程房间全部清掉（平均 11–16 s）。** 「同房其他轮可清」也就有了解释：R01 r2 在 06:44 队长活着时 14.3 s 就清了。
- 团本倒下后要等「下一个房间开打 / 首领转阶段」才复活（D106）。房间不清就不会复活，队长整段都在观战，形成死锁。
- **反证（R03 06:34）：** 队长观战时，脚本执行了 `tp P1RrA1008 34.5 72 64.5`（R03 r2 第一个刷怪点，在 r2 trigger `[28,71,50..52,76,76]` 内），然后 `tp B/C → 队长`。之后约 90 s，`next=r2 active=-` 一直没变，r2 始终没触发。说明 B、C **没有被传到队长被 tp 的位置**（`participantsHere()` 只认活着、非观战的成员）。这证明队长观战时，「跟队长 tp」这条链是断的。
- 机制（代码推断）：`EmberRaidService.watchTeammate` 用 `setSpectatorTarget(队友)` 把倒下的人锁到队友身上。1.12 的 `EntityPlayer` 每 tick 会把观战者的位置拉回到镜头目标，所以 `tp 队长 xyz` 最多生效 1 tick，`tp B 队长` 实际等于 tp 到 B 自己身边。
- 卡住时怪确实活着、而且是 1 血：`runs weaken` 每 2 s 回报 `n=1/2`（`n` = 导演表里未死亡的怪数）。日志里没有 `anomaly`、`was suffocating`、`fell below`、`stuck in block`。06:27 和 06:39 那两轮，B、C 最后都是被 `矿道弓手` 射死的（06:29:58、06:42:00），说明弓手一直在射程内对着 bot 射击，只是 bot 不去近身。
- 卡住期间队长的 tp 目标坐标（`raids2.out`）一直在房内地板 y=64 上移动，例如 R01 r2 是 x 40–49、z 53–61，最后集中在 `46–48, 64, 60.7` 附近，没有一次在高处或房外。06:44 的选择器定位 `-7.5,64,25.7` / `41.8,64,63.4` / `-9.3,64,102.6`，脚下和头顶都是 air。

## 2. 房清判定与拉回机制（CoreRpg，不是 DungeonPlus）

- `plugins/DungeonPlus/dungeon/EmberQ0R1|R2/`：`monster.yml` 里没有 DP 怪组（注释写明由 CoreRpg 导演生成）；`task/timeout.yml` 只有 1800 s 维护超时；`option.yml` 不刷怪、不判房清。**DP 不参与房清。**
- 房清：`EmberRunDirector.tick()` → `aliveIn(activeRoom) == 0` 时调用 `roomCleared`。`aliveIn` 统计 `roomId` 等于当前房、未死亡、并且不是护宝兔的被追踪怪。**没有阈值、没有超时、没有养残保护。**
- 拉回（每 5 tick 检查一次）：怪的 `leash` = 本房 `trigger` 盒。
  - 位置超出 trigger 盒外扩 2 格，或高度 `y > home.y + 4.5`，或低于 `fallCatchY` → 立即 tp 回自己的刷怪点（home）。
  - 头部连续 6 次在不透光方块里 → `anomaly stuck in block` 并移除。
  - 低于 `y0 − 4` → `anomaly fell below the room` 并移除。
  - **所以怪不可能停留在房外、高台（> 4.5 格）或墙里。**
- 注意测试脚本读的 `alive=` 是 `mobs.size()`（全部被追踪的怪，包括 extra 精英等），不是本房存活数。例如 R03 06:49 r2 清掉后仍显示 `alive=1`，那是额外精英。判断房间是否清掉应看 `active=` 字段。这一点不是卡住原因，但容易误读。

## 3. 涉事怪与房间几何

### MythicMobs（`plugins/MythicMobs/Mobs/EmberP1Main.yml`）

| 怪 | Type | 关键 Options | AI / 技能 |
|---|---|---|---|
| `EmberQ07Ranged` 矿道弓手（R01 远程） | SKELETON（不会飞） | MovementSpeed 0.25 · FollowRange 24 · Despawn false · PreventRandomEquipment · BOW | **没有 AIGoalSelectors / AITargetSelectors / Skills** → 原版骷髅弓 AI |
| `EmberQ06Ranged` 哨楼弩手（R02 远程） | SKELETON | 同上（Health 91，Damage 10） | 同上 |
| 对照：`EmberQ07Melee` / `EmberQ06Melee` | ZOMBIE | 0.23 · FollowRange 24 | 原版僵尸 AI（会贴脸） |

- 两只远程怪都没有 teleport、leap、throw、knockback、`~onTimer` 之类拉开距离的技能。团本里的 atk / interval / range 12 由 CoreRpg 伤害管道接管。
- 1.12 原版 `PathfinderGoalBowShoot` 的行为：
  - 目标在 15 格内且看得见 → 停止寻路，原地横移射箭；
  - 目标近于 0.25×15² ≈ **7.5 格** → **后退横移**；
  - 远于约 13 格 → 不再后退。
  - 结果就是一直和近战目标保持约 7.5–13 格。原地不动、只靠「≤ 4 格才攻击」的 bot 永远打不到它。
- MM 4.11（v1_12_R1）jar 里可用的 AI goal：`clear` / `arrowattack`（= 原版 `PathfinderGoalArrowAttack`，站桩射击，不后撤）/ `bowshoot`（MM 自带的 BowShoot，也会横移）/ `meleeattack` / `movetowardstarget` / `movetowardsrestriction` / `randomstroll` 等，另有 `FleeConditionalGoal`。

### 地图（v2 缓存 `dungeon_EmberQ0R1_B2BED675` / `dungeon_EmberQ0R2_33F9EFF5`，离线解析 y=63–66）

| 房 | trigger（leash） | 刷怪点 | 地形 |
|---|---|---|---|
| R01 r2 侧置货仓 | `[28,63,52 .. 52,68,76]` | 8 点全部在 y=64（地板 y=63） | 约 25×25 平地。内墙 x≈26–27 / 53–54。**z 61–67 东西两侧开口约 7 格宽**：西边通长廊，东边通事件区 x 58–74。侧点 `(31,64,64)`、`(49,64,64)` 正对开口。零星有 1 格高的箱子（`^`），能跳上去，也低于 4.5 格拉回线。没有高台、没有悬空、没有水或岩浆。 |
| R02 r2 军械仓 | 同坐标 `[28,63,52 .. 52,68,76]` | 同上 y=64 | 同构。**东侧 z 59–69 有约 11 格开口**通事件区；北墙只在 x 37–43 留进门通道，西、南两面是墙。 |
| R02 r1 霜旗外院 | `[-18,63,20 .. 18,68,44]` | y=64；侧点 `(±15,64,32)` | 约 37×25 平地。**东侧 z 27–37 有开口**通往 r2 的走廊，侧点 `(15,64,32)` 正对开口。 |

- 开口都在 leash 范围内：后撤的弓手走出「trigger 外扩 2 格」（例如 x > 54 或 x < 26）的下一次检查（≤ 0.25 s）就会被 tp 回 home。所以**最坏情况是「弓手退进开口 → 被拉回刷怪点」**，不会消失，也不会卡在打不到的地方。
- 复跑报告里 R01/R03 的窒息、anomaly 都是 0，和上面一致。

## 4. 原因排序

1. **（主因 · 高）测试脚本：队长观战后「跟队长 tp」失效 + 远程怪保持距离 + bot 只打 ≤ 4 格。** 证据是第 1 节的 4/4 相关性和 R03 反证。属于测试工具问题，**不能判为地图或怪物缺陷**。
2. **（次因 · 中，影响真人的概率低）原版骷髅弓 AI 后撤 + 开口贴着侧点。** 只剩 1 人时，追弓手体验差。leash 的「出界瞬移回 home」会让弓手在开口和刷怪点之间来回闪，看起来像「打不到」，但能清掉。
3. **（已排除）** 怪在墙里或高台上 / 会飞 / 跑出房间（leash + 地形 + 选择器坐标都否定）；DP 房清（DP 不判房清）；怪被回血（weaken 的计数稳定，没有回血源）。

## 5. 最小修法选项（只写方案，未实施；按推荐顺序）

| # | 改哪 | 做法 | 风险 / 代价 |
|---|---|---|---|
| **1（推荐先做）** | `mineflayer-tests/p1-maps-rerun-smoke.js`（测试岗；不动游戏） | ① 选「执行者」时跳过观战 / 死亡的 bot（`bot.game.gameMode === 'spectator'` 或聊天收到「你已倒下」），用第一个活着的 bot 当队长。② 不再 `tp 其他人 → 队长`，改为每个活着的 bot 直接 `tp` 到目标怪坐标。③ 远程怪 > 4 格时可以把怪拉过来，例如 `execute <活bot> ~ ~ ~ tp @e[type=skeleton,r=24,c=1] <活bot>`（仍由 bot 击杀）。④ 房清判断只看 `active=`，不看 `alive=`。 | 零线上风险。修完重跑 R01/R02 各 3–5 轮验证。预计软卡归零。 |
| 2 | 真人复现（测试岗） | 真人进 R01 r2 / R02 r1–r2 各打一局，故意只留 1 人追弓手。 | 无。用来确认次因在真人场景下是否算问题。 |
| 3（若真人确认弓手难追） | MM `Mobs/EmberP1Main.yml` 里的 `EmberQ06Ranged` / `EmberQ07Ranged` | 加 `AIGoalSelectors: [clear, arrowattack, randomstroll]` + `AITargetSelectors: [clear, hurtby, players]`，把会后撤的弓 AI 换成站桩射击。数值不动。 | 同一个 MM ID 也被 Q06 / Q07 单人本复用（`ember-v1-runs.yml` 第 639、716 行），**手感会变（更好打）**，需要设计签。1.12 骷髅换装（`setSlot`）时会调用 `setCombatTask` 重新挂上 BowShoot，可能把自定义 AI 冲掉，**要在 Q07 单人本实测 AI 是否真的生效**。需要 `/mm reload`。 |
| 4（偏系统，CoreRpg 岗） | `CoreRpg/.../EmberRunDirector.tick()` 怪物维护段 | 「远程残局破局」：当本房剩下的全是 `ranged`，且距离上一次击杀 ≥ 45 s（或房间开打 ≥ 90 s）→ 给剩余的怪 `setGlowing(true)`，并把它们 tp 回 home（或 tp 到离最近活着的成员 3 格内的地面点）。每 15 s 最多一次，写进日志。可以加一个 `ember-v1-runs.yml` 开关（例如 `raid_ranged_unstick_s: 45`，0 = 关）。 | 要改代码并重编 CoreRpg（不是 MM 岗）。tp 目标点必须是已经核验过的刷怪点，避免进墙。不影响伤害和数值，也不碰 D191 事件的计数。 |
| 5（不推荐） | MM `~onTimer` + `teleport @NearestPlayer`（加 distance 条件） | 远离玩家超过 N 格时，弓手瞬移到玩家身边。 | 和 CoreRpg 的 leash / home 互相抢位置，弓手会贴脸瞬移，观感差。MM 4.11 的条件写法在 1.12 上还要验证。导演已经有 home 拉回，不建议再加一层。 |
| 6（不推荐） | CoreRpg 房清改成「剩 ≤ 1 即清」/ 超时强清 | — | 会破坏 D191 连斩 / 无伤 / 裂隙、护宝兔、treasure 和「击杀须由玩家完成」的口径，也会让刷残怪成为可利用的漏洞。如果一定要兜底，建议用 #4（发光 + 拉回）代替自动判清。 |
| 可选 | 地图：R01/R02 r2 的 z 61–67 侧开口、R02 r1 的东开口 | 刷怪点 `(31,64,64)` / `(49,64,64)` / `(15,64,32)` 往房内挪 3–4 格（改 `ember-v1-runs.yml` 的 `points`）；或者在开口加一道开房后落下、清房后打开的栏（类似 door）。 | 改 points 是配置改动，会影响 A/B 布局。加栏要改地图和导演。收益小，等真人复现后再说。 |

## 6. 附：本单读过的文件 / 命令（只读）

- `docs/status/STATUS-ember-dp-p1-maps-rerun-test-2026-10-08.md`、`mineflayer-tests/p1-maps-rerun-smoke.js`（`fight()` 的 tp / 攻击逻辑）
- `/tmp/p1rerun/raids{,2,3,4,5}.{json,out}`、`chat-P1Rr{A,B,C}1008.log`、`window.log`
- `CoreRpg/src/main/java/town/sunshine/corerpg/p1/EmberRunDirector.java`（tick、aliveIn、leash、spawn）、`EmberRunService.java`（`runs weaken`）、`EmberRaidService.java`（leashFallen / watchTeammate / setSpectatorTarget）、`EmberRunMaps.Box`
- `plugins/CoreRpg/ember-v1-runs.yml`（raids r01/r02/r03 的 rooms / trigger / points / mobs）
- `plugins/DungeonPlus/dungeon/EmberQ0R1/{option,monster,obstacle}.yml`、`task/timeout.yml`
- `plugins/MythicMobs/Mobs/EmberP1Main.yml`（Q06/Q07 Ranged / Melee）、`plugins/MythicMobs.jar`（v1_12_R1 AI goal 列表）
- region 离线解析：`dungeon-caches/dungeon_EmberQ0R1_B2BED675`、`dungeon_EmberQ0R2_33F9EFF5`（先复制到 `/tmp/diag` 再读）
