# STATUS · ARCH S3-2（CoreRpg 1.65.57 / D231）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.56 / D230 / bv58（585ab03）

## 为什么选 Abyss 而不是 Pledge

ARCH §5 S3：「`EmberRunService` 按分节拆为 Entry / Settlement / **Abyss** / Raid / Rush / Pledge / Recruit / Papi（行为不变）」。

- **Abyss**：`feeMarks` + C08 层费 spend/refund + `p2_abyss_best` 层纪录 + `/corerpg p1 abyss` 菜单，形成与 Rush `applyGrants` 同形的封闭计数器/账本缝；共享 `enter(...)` 只留薄调用点。
- **Pledge**：自选誓约与周规则/修饰符共用 `s.modifier` 与 settle 主路径，边界更缠。本刀不做 Pledge；下一刀可拆。

## 做了什么

1. 新建 `EmberAbyssService`：开放/最高层、层费印记算术、C08 spend/refund、层纪录 grant、`/corerpg p1 abyss` 菜单、`abyssLine`。
2. `EmberRunService` 薄委托（`abyssOpen` / `abyssBest` / `abyssMaxStart` / `tryEnterAbyss` / `feeMarks` / `feeFor` / `cmdAbyss` / `abyssLine`）；`enter` 内层费走 `applyFeeSpend`；`releaseFee` 走 `applyFeeRefund`；settle 层纪录走 `applyFloorGrant`。
3. Bukkit-free：`feeMarks` / `applyFloorGrant` / `applyFeeSpend` / `applyFeeRefund`。
4. `EmberAbyssServiceTest` ×6：feeMarks 金样；coin/mark 层费；refund；floor grant；yml 层费表；计数器归属。
5. `EmberCounters`：`p2_abyss_best` 归属 → `EmberAbyssService`；`EmberEconomyTest` addCoin 白名单加 `EmberAbyssService`（C08 退费）。

## 不变

- `balance_version` **58**；所有发放 / 消耗数量
- 技能；C15；echotune；p1sim；R2 swap；内容包；Pledge 行为

## 下一刀候选

1. S3 step 3：拆 `EmberPledgeService`（或 Raid / Recruit / Papi）
2. S3 遭遇原语接口（可并行）
3. S14/S15 经 grant*（体力 / 退药）— S2 leftover

## 冒烟

FreshQ800+（见 `docs/tests/smoke-2026-10-06-d231-arch-s3-abyss.md`）。
