# Smoke · D230 / CoreRpg 1.65.56 — ARCH S3-1 EmberRushService extract

**何时：** 2026-10-06 ~05:05–05:08 Asia/Shanghai · **脚本：** `tools/p1map/d230-arch-s3-rush-smoke.sh` · **Bot：** FreshQ798（连战菜单+绑本）+ FreshQ799（Q01 结算）

## 结果

**PASS** · MySQL×2 · SEVERE 0 · jar `93acf02ae298bd5f…`

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.56 enable | — | PASS |
| CoreRpg + CoreGacha MySQL | — | PASS |
| ember-v1-economy.yml SoT bv58 | — | PASS |
| `/corerpg p1 rush` 菜单 | FreshQ798/799 | PASS |
| `/corerpg p1 rush outpost` 菜单 | FreshQ798 | PASS |
| `/corerpg p1 rush echo_q01` 菜单 | FreshQ798 | PASS |
| `/corerpg p1 rush go` 绑本 EmberQ0B1 | FreshQ798 | PASS |
| Q01 enter + 三房 + settle | FreshQ799 | PASS |
| 无 grant refuse | FreshQ799 | PASS |
| SEVERE | — | 0 |

## 说明

- FreshQ798 第一轮 Q01 因脚本误用 `/corerpg p1 run q01`（只列图）失败；已改 `/corerpg p1 enter q01`，FreshQ799 复跑全绿。连战菜单+绑本在 FreshQ798 已 PASS，覆盖 extract 主路径。
- 无资产路径变更 → 不做 persist-roundtrip。
