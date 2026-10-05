# Smoke · D237 / CoreRpg 1.65.63 — ARCH S3-8 EmberSessionService extract

**何时：** 2026-10-06 ~06:33–06:34 Asia/Shanghai · **脚本：** `tools/p1map/d237-arch-s3-session-smoke.sh` · **Bot：** FreshQ821（Q01 进本 + 三房 + 结算）

## 结果

**PASS 15 / FAIL 0 / SOFT 0** · MySQL×2 · SEVERE 0 · jar `9b7211cc5af1d35b…` · play PID 2274533

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.63 enable · MySQL×2 · E1 SoT bv58 · 无 FAIL-CLOSED | — | PASS |
| Q01 进本行「正在创建实例……（已预留体力 30）」 | FreshQ821 | PASS |
| Q01 r1 / r2 / r3 清房 | FreshQ821 | PASS |
| Q01 settle · session bound · 无 grant refuse | FreshQ821 | PASS |
| SEVERE（本次启动后） | — | 0 |

## 说明

- runId / D138 花样强制 / 开场行 / 层费文案由 `EmberSessionServiceTest` ×6 钉死；冒烟覆盖命令路径 + Q01 回归（会话创建 → 预留 → DP → verifyEntry → 结算仍在 RunService）。
- 无资产路径变更 → 不做 persist-roundtrip。
