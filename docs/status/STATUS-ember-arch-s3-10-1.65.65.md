# STATUS · ARCH S3-10（CoreRpg 1.65.65 / D239）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.64 / D238 / bv58（f9f50c2）

## 为什么做 BossMove / RoomObjective 适配器

ARCH §5 S3：「`EmberRunDirector` 抽出原语接口……每个原语一个类 + 单测」。D236 留下 `BossMove` / `RoomObjective` 接口桩；本刀把 Director 里**已可单测的招式调度与房间事件助手**迁到适配器，FX / 走 tick 仍在 Director。

## 做了什么

1. `EmberBossMove`（实现 `BossMove`）：Skill 数据视图 + `dueSkill` / `gatedNext` / `nextDue` / `hasBelowPressure` / `belowOf` / `gated()` / `has(CounterplayKind)`。
2. `EmberRoomObjective`（实现 `RoomObjective`）：`familyOf` 标签 + `markComplete`；D179 `holdCounts` / `relayAdvance` / beacon 标志；D191 `breach*` / `chain*` / `unscathed*`。
3. `EmberRunDirector`：上述静态方法改为一行委托（既有 `EmberRunRulesTest` / shape 测试仍绿）。
4. 包入口 `EmberEncounter` 切片改为 S3-10 / D239。
5. 单测：`EmberBossMoveTest` + `EmberRoomObjectiveTest`；`balance_version` **58** 不变。
6. 版本 **1.65.65**。

## 未做（下一刀）

- **Papi** 分节（`EmberRunService` 余部分节）。
- 词缀行为原语；p1sim 按同一原语建模；伤害轨迹回放比对（ARCH §5 S3 验证项）。
- Director 施法 FX / `inShape` 再迁（本刀只迁调度与房间助手）。

## 不变

- `balance_version` **58**；所有发放 / 消耗数量；撞墙 / 落空 / 破招秒数与门槛；裂隙 / 连斩 / 无伤参数；技能；C15；echotune；p1sim；R2 swap；内容包。

## 冒烟

FreshQ823+（见 `docs/tests/smoke-2026-10-06-d239-arch-s3-bossmove-room.md`）：Q01 通关回归（进本 + 三房 + 结算）。
