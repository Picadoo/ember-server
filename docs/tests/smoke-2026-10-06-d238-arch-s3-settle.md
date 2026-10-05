# Smoke · D238 / CoreRpg 1.65.64 — ARCH S3-9 EmberSettleService extract

**何时：** 2026-10-06 ~06:40–06:42 Asia/Shanghai · **脚本：** `tools/p1map/d238-arch-s3-settle-smoke.sh` · **Bot：** FreshQ822（Q01 进本 + 三房 + 结算）

## 结果

**PASS 15 / FAIL 0 / SOFT 1** · MySQL×2 · SEVERE 0 · jar `8541e3f4a6de5a59…` · play PID 2283561

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.64 enable · MySQL×2 · E1 SoT bv58 · 无 FAIL-CLOSED | — | PASS |
| Q01 进本行「正在创建实例……（已预留体力 30）」 | FreshQ822 | PASS |
| Q01 r1 / r2 / r3 清房 | FreshQ822 | PASS |
| Q01 settle · session bound · 无 grant refuse | FreshQ822 | PASS |
| failrefund PAPI（可选） | FreshQ822 | SOFT（bot 已 quit 后解析） |
| SEVERE（本次启动后） | — | 0 |

## 说明

- fail-refund / grant 顺序与数额由 `EmberSettleServiceTest` ×7 钉死；冒烟覆盖命令路径 + Q01 结算回归（Session → Settle 拆分后）。
- 无资产路径变更 → 不做 persist-roundtrip。
