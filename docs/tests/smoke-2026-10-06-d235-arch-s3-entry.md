# Smoke · D235 / CoreRpg 1.65.61 — ARCH S3-6 EmberEntryService extract

**何时：** 2026-10-06 ~05:58–06:03 Asia/Shanghai · **脚本：** `tools/p1map/d235-arch-s3-entry-smoke.sh` · **Bot：** FreshQ816（新角色门槛 + Q01 结算）+ FreshQ818（D96）+ FreshQ817（D104，全首通）

## 结果

**PASS 30 / FAIL 0 / SOFT 0** · MySQL×2 · SEVERE 0 · jar `0158fd07c0b434e3…` · play PID 2247175

| 项 | Bot | 结果 |
|---|---|---|
| CoreRpg 1.65.61 enable · MySQL×2 · E1 SoT bv58 · 无 FAIL-CLOSED | — | PASS |
| `enter q02`「未解锁（需先首通 Q01 灰烬庭院）」 | FreshQ816 | PASS |
| `enter q01 challenge`「未开放挑战版（需本人首通 Q07）」 | FreshQ816 | PASS |
| `enter rush`「未开放余烬连战（需本人首通 Q07）」（经 `EmberRushService.entryProblem`） | FreshQ816 | PASS |
| `enter rush challenge`「余烬连战没有挑战 / 深渊版本」+ 挑战门槛行 | FreshQ816 | PASS |
| `enter r01` 单人「人数 3～5，当前 1」+「未开放团本（需本人首通 Q07）」 | FreshQ816 | PASS |
| `abyss 1`「未开放深渊（需本人首通 Q07）」（经 `EmberAbyssService.entryProblems`） | FreshQ816 | PASS |
| 体力 10 → `enter q01`「体力不足（需 30，当前 10）」 | FreshQ816 | PASS |
| D96：仅首通 Q01 → `enter q02`「Q02 首通推荐：T1 刃 + T1 护符…」+ [仍然进入] | FreshQ818 | PASS |
| D104：全首通 → `enter q01 challenge`「挑战版按 T3 装备来调…」+ [仍然进入] | FreshQ817 | PASS |
| `runs modifier lean` → Q01 普通版 log「modifier forced lean (admin test, normal run)」 | FreshQ816 | PASS |
| Q01 进本行「正在创建实例……（已预留体力 30）」+ 三房 + 结算 · 无 grant refuse | FreshQ816 | PASS |
| SEVERE（本次启动后） | — | 0 |

## 说明

- run 1（FreshQ813–814）PASS 26 / FAIL 2：已全首通的 Q02 无 D96 提醒、直接进本（正确），随后在副本里测挑战被副本命令白名单拦 → 脚本顺序问题；改为 D96 用仅首通 Q01 的新角色、D104 先测，run 2 全过。
- 门槛文案 / 人数体力谓词 / 周规则选择由 `EmberEntryServiceTest` 钉死；冒烟覆盖命令路径 + Q01 回归。
- 无资产路径变更 → 不做 persist-roundtrip。
