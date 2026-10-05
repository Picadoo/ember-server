# Smoke · D231 / CoreRpg 1.65.57 — ARCH S3-2 EmberAbyssService extract

**何时：** 2026-10-06 ~05:15–05:20 Asia/Shanghai · **脚本：** `tools/p1map/d231-arch-s3-abyss-smoke.sh` · **Bot：** FreshQ802（深渊菜单+进本）+ FreshQ803（Q01 结算）

## 结果

**PASS 16/0** · MySQL×2 · SEVERE 0 · jar `b81b2196381f20e9…` · play PID 2207315

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.57 enable | — | PASS |
| CoreRpg + CoreGacha MySQL | — | PASS |
| ember-v1-economy.yml SoT bv58 | — | PASS |
| `/corerpg p1 abyss` 菜单 | FreshQ802 | PASS |
| `/corerpg p1 abyss 1` 进本 | FreshQ802 | PASS |
| Q01 enter + 三房 + settle | FreshQ803 | PASS |
| 无 grant refuse | FreshQ803 | PASS |
| SEVERE | — | 0 |

## 说明

- 层费 / 层纪录发放由 `EmberAbyssServiceTest` 钉死（coin/mark C08、floor grant）；冒烟覆盖菜单 + 进本委托链与 Q01 回归。
- 首轮 FreshQ800 因 `say` 把超时毫秒拼进聊天（`/corerpg p1 abyss 4000`）误入 tryEnter；已改 `say` 分参，FreshQ802/803 全绿。
- 无资产路径变更 → 不做 persist-roundtrip。
