# STATUS · ARCH S3-1（CoreRpg 1.65.56 / D230）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.55 / D229 / bv58（7060ca2）

## 为什么是这一刀

ARCH §5 S3：「`EmberRunService` 按分节拆为 Entry / Settlement / Abyss / Raid / **Rush** / Pledge / Recruit / Papi（行为不变）」。连战 / 残响 / 前哨是最独立的一块（D144 + D174 stage 2b），适合作为拆分第一步。

## 做了什么

1. 新建 `EmberRushService`：周认领计数、导演 stage 钩子、settle 发放、`/corerpg p1 rush` 菜单、文案辅助。
2. `EmberRunService` 保留薄委托（`onRushStart` / `onRushStage` / `rushWeek` / `rushSettle` / `cmdRush` / 静态文案），`EmberRunDirector` 与 PAPI 调用点不变。
3. `EmberRushService.applyGrants`：Bukkit-free 核（ledger 行 + claim 计数 + badge），供单测钉死发放。
4. `EmberRushServiceTest` ×7：主线连战首通 / 练习 / 幂等；前哨 T2+三图徽记；残响周 3 次封顶；文案；Economy SourceId。
5. `EmberCounters`：`p4_rush` / `p4_rush_claim` 归属改为 `EmberRushService`。

## 不变

- `balance_version` **58**；所有发放 / 消耗数量
- 技能；C15；echotune；p1sim；R2 swap；内容包

## 下一刀候选

1. S3 step 2：拆 `EmberPledgeService`（自选誓约）或 `EmberAbyssService`（深渊段）
2. S3 遭遇原语接口（预警形状 / 伤害包 / 破绽窗口）— 可与拆类并行
3. S14/S15 经 grant*（体力 / 退药）— S2 leftover

## 冒烟

FreshQ798+（见 `docs/tests/smoke-2026-10-06-d230-arch-s3-rush.md`）。
