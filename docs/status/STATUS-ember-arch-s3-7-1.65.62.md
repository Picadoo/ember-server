# STATUS · ARCH S3-7（CoreRpg 1.65.62 / D236）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.61 / D235 / bv58（3b5370c）

## 为什么做遭遇原语（第一刀）

ARCH §5 S3：「`EmberRunDirector` 抽出原语接口——预警形状、伤害包、定身/眩晕窗口（`wall_stun / whiff_stun / break_hp`）、房间目标、词缀行为——每个原语一个类 + 单测」。

内容包暂停中（POLICY 12:19）。本刀只做**接口 + 第一适配器**，不交新内容、不改数值：

- D188/D192/D193 破绽判定与眩晕时长已在 Director 里，适合先抽成 Bukkit-free 适配器，单测钉死现有行为。
- D106 复活点 why 字符串散落在 Director / RunService，适合收成 `RevivePoint` 枚举（行为仍由 `EmberRaidService.reviveFallen` 执行）。
- 房间目标 / 首领招式完整适配器过大 → 本刀只留接口桩，文档写明下一刀。

## 做了什么

1. 新建包 `town.sunshine.corerpg.p1.encounter`：
   - `EmberEncounter`：包入口 / 切片说明。
   - `CounterplayKind`：`WALL` / `WHIFF` / `BREAK`（yml 键）。
   - `EmberCounterplay`（**第一适配器**）：`armWhiff` / `armBreakNeed` / `armWallCrash`、`whiffs` / `broken`、`crashGrid` / `clearRunGrid`、`stunMs` / `stunPotionTicks` / `applyStunBounds`、warn 行后缀、常量 `WALL_STUN_MAX` 等（与 Skill 解析上限一致，bv58）。
   - `RevivePoint`：`ROOM_OPEN` / `BOSS_SPAWN` / `HALF_HP` / `ADDS_PHASE` / `LAST_PHASE`（why 原文不变）。
   - `BossMove` / `RoomObjective`：接口桩（未接线）。
2. `EmberRunDirector`：破绽判定 / 撞墙网格 / 眩晕时长 / warn 后缀改走 `EmberCounterplay`；半血 / 援兵 / 末段复活改走 `RevivePoint`；`whiffs`/`broken`/`crashGrid`/`clearRunGrid` 保留薄委托（`EmberRunShapeTest` 仍绿）。
3. `EmberRunService`：房间开打 / 首领现身 → `RevivePoint`；`onBossPhase` / `reviveFallen` 增加枚举重载。
4. 单测：`EmberCounterplayTest` ×11、`RevivePointTest` ×3（+14；全量 460/0）。
5. 版本 **1.65.62**；`balance_version` **58** 不变。

## 未做（下一刀）

- **Session/Settlement**：Entry 余部（会话创建 / 体力+层费预留 / DP 派发 / verifyEntry）+ 结算分节 → `EmberSessionService`。
- `BossMove` / `RoomObjective` 适配器（把 Director 招式循环 / 房间事件分支迁到原语）。
- 词缀行为原语；p1sim 按同一原语建模；伤害轨迹回放比对工具（ARCH §5 S3 验证项）。

## 不变

- `balance_version` **58**；所有发放 / 消耗数量；撞墙 / 落空 / 破招秒数与门槛；复活 50% 生命 / 末段 20% 额外复活；技能；C15；echotune；p1sim；R2 swap；内容包。

## 冒烟

FreshQ819+（见 `docs/tests/smoke-2026-10-06-d236-arch-s3-encounter.md`）：Q01 通关回归 + 若可则已知破绽日志（wall/whiff）。
