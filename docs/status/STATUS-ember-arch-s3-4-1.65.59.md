# STATUS · ARCH S3-4（CoreRpg 1.65.59 / D233）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.58 / D232 / bv58（3ff17d2）

## 为什么拆 Raid（Recruit 不并入）

ARCH §5 S3：「`EmberRunService` 按分节拆为 Entry / Settlement / Abyss / Raid / Rush / Pledge / Recruit / Papi（行为不变）」。

- **Raid 可拆**：`p2_raid_<capKey>` 周计数器只在 3 处读写（进本问题行、settle `raid_mark` 新行、菜单 / PAPI / 失败行标签）；D106 倒下流程（观战 / 复活 / 牵引 / 拦 `/dp leave` / `/corerpg p1 watch`）只依赖 session + director，没有反向调 enter/settle 内部。`enter` 里的 party / 体力 / 「已在另一局」检查本来就是所有本共用的，留在 `EmberRunService`，只把团本那一行问题换成 `raid.entryProblem(...)`。
- **Recruit 不缠**：招募板只读 `maps` / `progressFlag` / DP 队伍桥，和 Raid 计数器、倒下流程零共享状态；它是独立的社交功能（D104 / E-review #5），单独一刀更干净 → 下一刀候选。
- 所以本刀**没有**退而求其次：Raid 本身就是更清楚的那一缝。

## 做了什么

1. 新建 `EmberRaidService`：
   - 计数器 / 文本（Bukkit-free）：`capKey`、`counterKey`、`weekCount`、`capReached`、`entryProblemText`、`labelText`、`failNoBurnText`、`startText`。
   - settle（Bukkit-free）：`settleGrants`（`raid_item` = `EmberRunRules.raidItem` 原式 + `raid_mark` 1 枚 run tier，S12）、`applyClearCount`（新记 `raid_mark` 行才 +1 `p2_raid_<capKey>@周`）。
   - live：`week` / `label` / `entryProblem` / `tellFailNoBurn` / `onStart` / `menuButtons` / `papi`。
   - D106：`isRaid` / `livingIn` / `nearestLiving` / `nextReviveText` / `watchTeammate` / `watchLater` / `onFall` / `reviveFallen` / `leashFallen` / `onSpectateTeleport` / `onFallenLeave` / `cmdWatch`（`leashTold` / `leaveAsked` 状态随迁）。
2. `EmberRunService` 薄委托：`raidWeek` / `capKey` / `raidLabel` / `isRaid` / `reviveFallen` / `leashFallen` / `onBossPhase` / `cmdWatch`；两个 `@EventHandler`（`onSpectateTeleport` / `onFallenLeave`）仍在原类注册、一行转发（Listener 注册不变）；新增 package `director(world)` 给 Raid 服务查实例。−174 行。
3. `EmberCounters`：`p2_raid_` 归属 → `EmberRaidService`。
4. `EmberRaidServiceTest` ×6：打包 yml r01–r03 共用 `cap_group: raid`（3/周、3～5 人、50 体力、T3、`raid_item.quality_floor` 1、各自 loot 族）；`applyClearCount` 只认新 `raid_mark` 行 + 按 cap_group 共计 + 跨周归零 + 无 cap_group 自用计数；进本问题行 / 标签 / 失败行原文；settle 两条 grant 形状且与 `EmberRunRules.raidItem` 同种子逐字节相同、目标族优先；计数器归属 + 时钟回拨保护。
5. `raid_mark` grant 仍写字面量（`EmberEconomyTest` D228 账本键扫描依赖）。

## 不变

- `balance_version` **58**；所有发放 / 消耗数量（团本 50 体力、3 次/周、`raid_item` 保底精良、`raid_mark` 1 枚、委托 ×2、复活 50% 生命 / 20% 末段复活）
- 结算顺序：`cosmetics.onRaidClear` / `season.onRaid` / 委托权重 2 仍在 `settleFor` 原位（共享结算流程，未搬以免改调用顺序）
- 技能；C15；echotune；p1sim；R2 swap；内容包；招募板

## 下一刀候选

1. **S3-5 Recruit**：招募板 `cmdRecruit` / `liveRecruits` / `recruitsLabel` / `showRecruits` / `onJoinRecruits` → `EmberRecruitService`（零共享状态，最小风险）。
2. **S3 遭遇原语接口**（可并行）：房间 / 首领招式 / 破绽 / 复活点作为数据驱动原语（D106 复活点钩子 `onBossPhase` 已经集中在 Raid 服务，是天然接入点）。
3. **Entry**：`enter` 的 party / 体力 / 问题行 / 周规则选择 — 现在 Rush / Abyss / Pledge / Raid 都只剩一行调用，Entry 可以单独成服务了，但它是主路径，放在 Recruit 之后。
4. S14/S15 经 grant*（体力 / 退药）— S2 leftover。

## 冒烟

FreshQ806–809（见 `docs/tests/smoke-2026-10-06-d233-arch-s3-raid.md`）：PASS 32 / FAIL 0 / SOFT 1。
