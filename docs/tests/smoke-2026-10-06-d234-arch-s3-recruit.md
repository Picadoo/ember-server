# Smoke · D234 / CoreRpg 1.65.60 — ARCH S3-5 EmberRecruitService extract

**何时：** 2026-10-06 ~05:50–05:53 Asia/Shanghai · **脚本：** `tools/p1map/d234-arch-s3-recruit-smoke.sh` · **Bot：** FreshQ810（发帖队长）+ FreshQ811（收帖 / join）+ FreshQ812（Q01 结算）

## 结果

**PASS 22 / FAIL 0 / SOFT 1** · MySQL×2 · SEVERE 0 · jar `9a2547c4105864d72803281af0bc6a4a…` · play PID 2239873

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.60 enable · MySQL×2 · E1 SoT bv58 · 无 FAIL-CLOSED | — | PASS |
| `/corerpg p1 recruit list` 空板「现在没有团本在招人」 | FreshQ810 | PASS |
| `/corerpg p1 recruit r01` 发帖「已向 1 位已首通 Q07…」+ 10 分钟挂板说明 | FreshQ810 | PASS |
| 收帖侧可见招募喊话 / [申请入队] | FreshQ811 | PASS |
| `/corerpg p1 recruit list`「团本招募板」+「R01 · FreshQ810 · 1/3 · 刚刚（你的招募）」 | FreshQ810 | PASS |
| `papi parse … %corerpg_p1_recruits%` | console | SOFT（console 侧 `Failed to find player`，同 D233 PAPI；板文案已由 list 覆盖） |
| `/corerpg p1 recruit join FreshQ810` → DP「入队请求已发送」 | FreshQ811 | PASS |
| 60 秒 CD「招募 60 秒内只能发一次（还剩 N 秒）」 | FreshQ810 | PASS |
| Q01 进本 + 三房 + 结算 · 无 grant refuse | FreshQ812 | PASS |
| SEVERE | — | 0 |

## 说明

- TTL / CD / 板文案 / PAPI label / 喊话与发帖文案由 `EmberRecruitServiceTest` 钉死；冒烟覆盖命令路径（空板 → 发帖 → 板列表 → join → CD）+ Q01 回归。
- 无资产路径变更 → 不做 persist-roundtrip。
