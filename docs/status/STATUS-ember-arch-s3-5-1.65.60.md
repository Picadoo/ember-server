# STATUS · ARCH S3-5（CoreRpg 1.65.60 / D234）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.59 / D233 / bv58（69cd7e7）

## 为什么拆 Recruit（最低风险）

ARCH §5 S3：「`EmberRunService` 按分节拆为 Entry / Settlement / Abyss / Raid / Rush / Pledge / Recruit / Papi（行为不变）」。

- **零共享状态**：招募板只读 `maps` / `progressFlag` / DP 队伍桥；与 Raid 周计数器、D106 倒下流程、settle 账本全部脱钩（D233 STATUS 已标为下一刀候选）。
- **封闭缝**：`cmdRecruit` / `liveRecruits` / `recruitsLabel` / `showRecruits` / `onJoinRecruits`（板）/ `onRequestRefused` 形成一个社交功能岛（D104 / E-review #5）；TTL 10 分钟、发帖 CD 60 秒、板容量 5、PAPI 最多 2 条。
- **EventHandler 原位**：`onJoinRecruits` 仍注册在 `EmberRunService`（D116 `season.apply` 必须留在原 handler），板闪给 `EmberRecruitService.onJoinShow`；`onRequestRefused` 一行转发。

## 做了什么

1. 新建 `EmberRecruitService`：
   - Bukkit-free：`cooldownOk` / `cooldownRemainSec` / `stillLive` / `boardText` / `labelText` / `callBody` / `postedOk` / `postedEmpty` / `usageHelp` / `emptyBoardMsg`。
   - live：`cmd`（post / list / join）、`live` / `textOf` / `label` / `show` / `onJoinShow` / `onRequestRefused`；状态 `recruits` + `recruitAt` 随迁。
2. `EmberRunService` 薄委托：`recruit.cmd` / `recruitsLabel` / `showRecruits`；两个 `@EventHandler` 原位转发；`RECRUIT_TTL` 别名 → `EmberRecruitService.TTL_MS`。−90 行量级。
3. `EmberRecruitServiceTest` ×6：常量 + 打包 r01–r03；CD 公式与原文 `60-(now-last)/1000` 一致；`stillLive` 谓词；板文案（刚刚 / N 分钟前 / 可开本）；PAPI 空 / 1 / 2 / 溢出「等 N 个」；喊话 / 发帖成功与空站 / 用法 / 空板；r01 `party_hint` 透传。
4. 版本 **1.65.60**；`balance_version` **58** 不变。

## 不变

- `balance_version` **58**；所有发放 / 消耗数量；团本 50 体力 / 3 次/周；技能；C15；echotune；p1sim；R2 swap；内容包；Raid / Rush / Abyss / Pledge 已抽服务

## 下一刀候选

1. **Entry**：`enter` 的 party / 体力 / 问题行 / 周规则选择 — Rush / Abyss / Pledge / Raid 都只剩一行调用，主路径可单独成服务（风险高于 Recruit）。
2. **S3 遭遇原语接口**（可并行）：房间 / 首领招式 / 破绽 / 复活点作为数据驱动原语（D106 `onBossPhase` 已在 Raid 服务）。
3. **Papi / Settlement** 其余分节；S14/S15 经 grant*（体力 / 退药）— S2 leftover。

## 冒烟

FreshQ810–812（见 `docs/tests/smoke-2026-10-06-d234-arch-s3-recruit.md`）：PASS 22 / FAIL 0 / SOFT 1。
