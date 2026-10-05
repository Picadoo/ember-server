# Smoke · D232 / CoreRpg 1.65.58 — ARCH S3-3 EmberPledgeService extract

**何时：** 2026-10-06 ~05:27–05:30 Asia/Shanghai · **脚本：** `tools/p1map/d232-arch-s3-pledge-smoke.sh` · **Bot：** FreshQ804（誓约 list/toggle/off + pledge 进本）+ FreshQ805（Q01 结算）

## 结果

**PASS 18/0** · MySQL×2 · SEVERE 0 · jar `9de29f8306be1b7e…` · play PID 2217552

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.58 enable | — | PASS |
| CoreRpg + CoreGacha MySQL | — | PASS |
| ember-v1-economy.yml SoT bv58 | — | PASS |
| `/corerpg p1 pledge list` | FreshQ804 | PASS |
| pledge toggle lean + off | FreshQ804 | PASS |
| pledge path applied (enter q01) | FreshQ804 | PASS |
| Q01 enter + 三房 + settle | FreshQ805 | PASS |
| 无 grant refuse | FreshQ805 | PASS |
| SEVERE | — | 0 |

## 说明

- 计数器 / encode / S09 grant 由 `EmberPledgeServiceTest` 钉死；冒烟覆盖菜单/toggle 委托链、誓约进本（当日非精选挡住周规则，pledge 生效）、Q01 回归。
- 无资产路径变更 → 不做 persist-roundtrip。
