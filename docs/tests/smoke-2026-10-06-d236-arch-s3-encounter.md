# Smoke · D236 / CoreRpg 1.65.62 — ARCH S3-7 encounter primitives

**何时：** 2026-10-06 ~06:23–06:28 Asia/Shanghai · **脚本：** `tools/p1map/d236-arch-s3-encounter-smoke.sh` · **Bot：** FreshQ820（Q01 三房 + 首领结算）

## 结果

**PASS 13 / FAIL 0 / SOFT 1** · MySQL×2 · SEVERE 0 · jar `7db6e84917e2ce4b…` · play PID 2264301

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.62 enable · MySQL×2 · E1 SoT bv58 · 无 FAIL-CLOSED | — | PASS |
| Q01 进本行「正在创建实例……（已预留体力 30）」 | FreshQ820 | PASS |
| Q01 r1 / r2 / r3 清房 | FreshQ820 | PASS |
| Q01 settle · 无 grant refuse | FreshQ820 | PASS |
| 破绽日志（whiff/wall/break） | FreshQ820 | SOFT（进本路线未触发；单测钉死判定） |
| SEVERE（本次启动后） | — | 0 |

## 说明

- 破绽 / 复活点 why / 眩晕公式由 `EmberCounterplayTest` ×11 + `RevivePointTest` ×3 + 既有 `EmberRunShapeTest` 钉死；冒烟覆盖命令路径 + Q01 回归。
- run 1（FreshQ819）脚本缺房间 tp → room 1 FAIL；已按 D235 补 tp，run 2 全过。
- 无资产路径变更 → 不做 persist-roundtrip。
